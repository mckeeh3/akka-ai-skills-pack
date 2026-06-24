package ai.first.application.coreapp.agentadmin;

import ai.first.application.foundation.agent.AgentBehaviorRepository;
import ai.first.application.foundation.agent.AgentRuntimeTraceSink;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.agent.AgentReferenceManifest;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import ai.first.domain.foundation.agent.AgentSkillManifest;
import ai.first.domain.foundation.agent.PromptDocument;
import ai.first.domain.foundation.agent.PromptVersion;
import ai.first.domain.foundation.agent.ReferenceDocument;
import ai.first.domain.foundation.agent.ReferenceVersion;
import ai.first.domain.foundation.agent.SkillDocument;
import ai.first.domain.foundation.agent.SkillVersion;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.ScopeType;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SaaS-owner Agent Admin service boundary for AI-assisted prompt, skill, and reference-doc administration.
 *
 * <p>This slice defines the browser/API-facing contract and enforces SaaS-admin-only access. The durable
 * historical version store and full editing-agent lifecycle are intentionally implemented by later slices; this
 * service exposes the typed boundary those slices will fill without exposing stale tenant-scoped governance flows.
 */
public final class AgentAdminDocAdministrationService {
  public static final String LIST_AGENTS_CAPABILITY = AgentAdminService.LIST_DEFINITIONS;
  public static final String READ_AGENT_CAPABILITY = AgentAdminService.GET_DEFINITION;
  public static final String READ_PROMPT_CAPABILITY = AgentAdminService.GET_PROMPT_VERSION;
  public static final String READ_SKILL_CAPABILITY = AgentAdminService.GET_SKILL_VERSION;
  public static final String READ_REFERENCE_CAPABILITY = AgentAdminService.GET_REFERENCE_VERSION;
  public static final String UPDATE_AGENT_PROFILE_CAPABILITY = "saas_owner.admin.manage";
  public static final String DRAFT_EDIT_CAPABILITY = AgentRuntimeCapabilityIds.DRAFT_BEHAVIOR_CHANGE;
  public static final String CANCEL_EDIT_CAPABILITY = AgentRuntimeCapabilityIds.CANCEL_BEHAVIOR_CHANGE;
  public static final String SAVE_EDIT_CAPABILITY = AgentRuntimeCapabilityIds.ACTIVATE_BEHAVIOR_CHANGE;
  public static final String RESTORE_VERSION_CAPABILITY = AgentRuntimeCapabilityIds.ROLLBACK_BEHAVIOR_CHANGE;
  public static final String MANAGE_SKILL_CAPABILITY = "saas_owner.admin.manage";
  public static final String MANAGE_REFERENCE_CAPABILITY = "saas_owner.admin.manage";

  private final AgentBehaviorRepository repository;
  private final AuthContextResolver authContextResolver;
  private final Clock clock;
  private final AgentAdminDocEditingRuntime editingRuntime;
  private final AgentRuntimeTraceSink traceSink;
  private final Map<String, EditSessionRecord> sessions = new ConcurrentHashMap<>();

  public AgentAdminDocAdministrationService(AgentBehaviorRepository repository, AuthContextResolver authContextResolver, Clock clock) {
    this(repository, authContextResolver, clock, new FailClosedAgentAdminDocEditingRuntime(), new NoopAgentRuntimeTraceSink());
  }

  public AgentAdminDocAdministrationService(
      AgentBehaviorRepository repository,
      AuthContextResolver authContextResolver,
      Clock clock,
      AgentAdminDocEditingRuntime editingRuntime,
      AgentRuntimeTraceSink traceSink) {
    this.repository = Objects.requireNonNull(repository);
    this.authContextResolver = Objects.requireNonNull(authContextResolver);
    this.clock = Objects.requireNonNull(clock);
    this.editingRuntime = Objects.requireNonNull(editingRuntime);
    this.traceSink = Objects.requireNonNull(traceSink);
  }

  public AgentListResponse listAgents(AuthContextResolver.ResolvedMe actor, AgentListRequest request, String correlationId) {
    requireSaasAdmin(actor, LIST_AGENTS_CAPABILITY, "agent_admin.doc_agents.list.v1", correlationId);
    var filter = request == null ? new AgentListRequest(null, null) : request;
    var rows = repository.agentDefinitions(platformScopeId()).stream()
        .sorted(Comparator.comparing(AgentDefinition::displayName))
        .map(this::agentListRow)
        .toList();
    var filtered = rows.stream()
        .filter(row -> contains(row.agentName(), filter.nameContains()) || contains(row.shortPurpose(), filter.nameContains()))
        .filter(row -> filter.workstreamOrDomain() == null || filter.workstreamOrDomain().isBlank() || contains(row.workstreamDomain(), filter.workstreamOrDomain()))
        .toList();
    return new AgentListResponse(filtered, rows.size(), filtered.size(), List.of(traceId("agent-list", platformScopeId(), correlationId)));
  }

  public AgentProfileUpdateResult updateAgentProfile(AuthContextResolver.ResolvedMe actor, AgentProfileUpdateRequest request, String correlationId) {
    requireSaasAdmin(actor, UPDATE_AGENT_PROFILE_CAPABILITY, "agent_admin.doc_agent_profile.update.v1", correlationId);
    requireNonBlank(request == null ? null : request.agentDefinitionId(), "agentDefinitionId-required");
    var existing = agent(request.agentDefinitionId());
    var displayName = firstNonBlank(request.agentName(), existing.displayName()).trim();
    var purpose = firstNonBlank(request.purpose(), existing.description()).trim();
    if (displayName.isBlank()) throw new AuthorizationException(400, "agent-name-required");
    if (purpose.isBlank()) throw new AuthorizationException(400, "agent-purpose-required");
    var updated = new AgentDefinition(
        existing.tenantId(),
        existing.agentDefinitionId(),
        displayName,
        purpose,
        existing.placement(),
        existing.functionalAreaId(),
        existing.authorityLevel(),
        existing.status(),
        existing.promptDocumentId(),
        existing.activePromptVersion(),
        existing.skillManifestId(),
        existing.activeSkillManifestVersion(),
        existing.referenceManifestId(),
        existing.activeReferenceManifestVersion(),
        existing.toolBoundaryId(),
        existing.activeToolBoundaryVersion(),
        existing.modelConfigRefId(),
        existing.modelPolicyRefId(),
        existing.runtimeClassRef(),
        existing.traceRequirements(),
        existing.seedProvenance(),
        existing.createdAt(),
        Instant.now(clock));
    repository.saveAgentDefinition(updated);
    return new AgentProfileUpdateResult(agentListRow(updated), List.of(traceId("agent-profile-update", updated.agentDefinitionId(), correlationId)));
  }

  public AgentDocDetail agentDetail(AuthContextResolver.ResolvedMe actor, String agentDefinitionId, String correlationId) {
    requireSaasAdmin(actor, READ_AGENT_CAPABILITY, "agent_admin.doc_agent.detail.v1", correlationId);
    var agent = agent(agentDefinitionId);
    var prompt = prompt(agent);
    var skills = skillSummaries(agent);
    var references = referenceSummaries(agent);
    return new AgentDocDetail(
        agent.agentDefinitionId(),
        agent.displayName(),
        agent.description(),
        agent.functionalAreaId(),
        lastEditTime(agent, prompt, skills.stream().map(SkillDocSummary::updatedAt).toList(), references.stream().map(ReferenceDocSummary::updatedAt).toList()),
        docSummary(AgentDocKind.PROMPT, prompt.promptDocumentId(), prompt.title(), prompt.changeSummary(), prompt.activeVersion(), prompt.updatedAt()),
        skills,
        references,
        List.of(traceId("agent-detail", agent.agentDefinitionId(), correlationId), traceId("runtime-doc-reads", agent.agentDefinitionId(), correlationId)));
  }

  public DocumentVersionDetail readDocumentVersion(AuthContextResolver.ResolvedMe actor, DocumentVersionRequest request, String correlationId) {
    requireDocumentCapability(actor, request == null ? null : request.kind(), "agent_admin.doc_version.read.v1", correlationId);
    return resolveDocumentVersion(request).toVersionDetail(correlationId);
  }

  public VersionHistoryResponse versionHistory(AuthContextResolver.ResolvedMe actor, DocumentVersionRequest request, String correlationId) {
    requireDocumentCapability(actor, request == null ? null : request.kind(), "agent_admin.doc_version.history.v1", correlationId);
    if (request == null) throw new AuthorizationException(400, "document-request-required");
    var current = resolveDocument(new DocumentVersionRequest(request.agentDefinitionId(), request.kind(), request.documentId(), null));
    var rows = versionSnapshots(current).stream()
        .map(version -> new VersionHistoryRow(version.version(), version.version() == current.currentVersion(), version.updatedAt(), version.title()))
        .toList();
    return new VersionHistoryResponse(current.agentDefinitionId(), current.kind(), current.documentId(), rows, List.of(traceId("version-history", current.documentId(), correlationId)));
  }

  public AdjacentDiffResponse adjacentDiff(AuthContextResolver.ResolvedMe actor, DocumentVersionRequest request, String correlationId) {
    requireDocumentCapability(actor, request == null ? null : request.kind(), "agent_admin.doc_version.diff.v1", correlationId);
    var selected = resolveDocumentVersion(request);
    if (selected.version() <= 1) {
      return new AdjacentDiffResponse(selected.agentDefinitionId(), selected.kind(), selected.documentId(), null, selected.version(), DiffStatus.NO_PRIOR_VERSION, "", List.of(traceId("version-diff", selected.documentId(), correlationId)));
    }
    var prior = resolveDocumentVersion(new DocumentVersionRequest(selected.agentDefinitionId(), selected.kind(), selected.documentId(), selected.version() - 1));
    return new AdjacentDiffResponse(selected.agentDefinitionId(), selected.kind(), selected.documentId(), prior.version(), selected.version(), DiffStatus.READY, simpleUnifiedDiff(prior.contentBody(), selected.contentBody()), List.of(traceId("version-diff", selected.documentId(), correlationId)));
  }

  public EditSessionRecord startEditSession(AuthContextResolver.ResolvedMe actor, StartEditSessionRequest request, String correlationId) {
    requireSaasAdmin(actor, DRAFT_EDIT_CAPABILITY, "agent_admin.doc_edit_session.start.v1", correlationId);
    var base = resolveDocument(new DocumentVersionRequest(request.agentDefinitionId(), request.kind(), request.documentId(), null));
    var session = new EditSessionRecord(
        "agent-doc-edit-session-" + UUID.randomUUID(),
        base.agentDefinitionId(),
        base.kind(),
        base.documentId(),
        base.currentVersion(),
        actor.account().accountId(),
        EditSessionStatus.DRAFTING,
        List.of(new EditInstruction(Instant.now(clock), actor.account().accountId(), safe(request.initialInstructions()))),
        null,
        null,
        null,
        List.of(),
        List.of(traceId("edit-session-start", base.documentId(), correlationId)),
        Instant.now(clock),
        null);
    sessions.put(session.sessionId(), session);
    return session;
  }

  public EditSessionRecord draftEditSessionWithAgent(AuthContextResolver.ResolvedMe actor, AgentDraftEditSessionRequest request, String correlationId) {
    requireSaasAdmin(actor, DRAFT_EDIT_CAPABILITY, "agent_admin.doc_edit_session.agent_draft.v1", correlationId);
    requireNonBlank(request == null ? null : request.instructions(), "edit-instructions-required");
    var base = resolveDocument(new DocumentVersionRequest(request.agentDefinitionId(), request.kind(), request.documentId(), null));
    var session = new EditSessionRecord(
        "agent-doc-edit-session-" + UUID.randomUUID(),
        base.agentDefinitionId(),
        base.kind(),
        base.documentId(),
        base.currentVersion(),
        actor.account().accountId(),
        EditSessionStatus.DRAFTING,
        List.of(new EditInstruction(Instant.now(clock), actor.account().accountId(), safe(request.instructions()))),
        null,
        null,
        null,
        List.of(),
        List.of(traceId("edit-session-start", base.documentId(), correlationId)),
        Instant.now(clock),
        null);
    var proposed = invokeEditingAgent(actor, session, base, null, correlationId);
    sessions.put(proposed.sessionId(), proposed);
    return proposed;
  }

  public EditSessionRecord reviseEditSessionWithAgent(AuthContextResolver.ResolvedMe actor, AgentReviseEditSessionRequest request, String correlationId) {
    requireSaasAdmin(actor, DRAFT_EDIT_CAPABILITY, "agent_admin.doc_edit_session.agent_revise.v1", correlationId);
    requireNonBlank(request == null ? null : request.instructions(), "edit-instructions-required");
    var existing = session(request.sessionId());
    ensureSessionActor(actor, existing);
    if (existing.status() == EditSessionStatus.CANCELLED || existing.status() == EditSessionStatus.SAVED) {
      throw new AuthorizationException(409, "edit-session-closed");
    }
    var instructions = new ArrayList<>(existing.instructions());
    instructions.add(new EditInstruction(Instant.now(clock), actor.account().accountId(), safe(request.instructions())));
    var current = new EditSessionRecord(
        existing.sessionId(),
        existing.agentDefinitionId(),
        existing.kind(),
        existing.documentId(),
        existing.baseVersion(),
        existing.actorAccountId(),
        EditSessionStatus.DRAFTING,
        List.copyOf(instructions),
        existing.proposedContent(),
        existing.changeSummary(),
        existing.clarifyingQuestion(),
        existing.warnings(),
        appendTrace(existing.traceLinks(), traceId("edit-session-revise-request", existing.sessionId(), correlationId)),
        existing.startedAt(),
        null);
    var base = resolveDocument(new DocumentVersionRequest(current.agentDefinitionId(), current.kind(), current.documentId(), null));
    if (base.currentVersion() != current.baseVersion()) throw new AuthorizationException(409, "edit-session-base-version-stale");
    var proposed = invokeEditingAgent(actor, current, base, current.proposedContent(), correlationId);
    sessions.put(proposed.sessionId(), proposed);
    return proposed;
  }

  public EditSessionRecord reviseEditSession(AuthContextResolver.ResolvedMe actor, ReviseEditSessionRequest request, String correlationId) {
    requireSaasAdmin(actor, DRAFT_EDIT_CAPABILITY, "agent_admin.doc_edit_session.revise.v1", correlationId);
    var existing = session(request.sessionId());
    ensureSessionActor(actor, existing);
    var instructions = new ArrayList<>(existing.instructions());
    instructions.add(new EditInstruction(Instant.now(clock), actor.account().accountId(), safe(request.instructions())));
    var revised = new EditSessionRecord(
        existing.sessionId(),
        existing.agentDefinitionId(),
        existing.kind(),
        existing.documentId(),
        existing.baseVersion(),
        existing.actorAccountId(),
        EditSessionStatus.PROPOSAL_READY,
        List.copyOf(instructions),
        safe(request.proposedContent()),
        safe(request.changeSummary()),
        null,
        List.copyOf(request.warnings() == null ? List.of() : request.warnings()),
        appendTrace(existing.traceLinks(), traceId("edit-session-revise", existing.sessionId(), correlationId)),
        existing.startedAt(),
        null);
    sessions.put(revised.sessionId(), revised);
    return revised;
  }

  public EditSessionRecord cancelEditSession(AuthContextResolver.ResolvedMe actor, EditSessionCommandRequest request, String correlationId) {
    requireSaasAdmin(actor, CANCEL_EDIT_CAPABILITY, "agent_admin.doc_edit_session.cancel.v1", correlationId);
    var existing = session(request.sessionId());
    ensureSessionActor(actor, existing);
    var cancelled = transitionSession(existing, EditSessionStatus.CANCELLED, correlationId);
    recordEditTrace("EDIT_SESSION_CANCEL", AgentRuntimeTrace.Decision.DENIED, cancelled, correlationId, "edit session cancelled by actor; proposed output discarded", checksum(cancelled.proposedContent()));
    sessions.put(cancelled.sessionId(), cancelled);
    return cancelled;
  }

  public SaveEditSessionResult saveEditSession(AuthContextResolver.ResolvedMe actor, EditSessionCommandRequest request, String correlationId) {
    requireSaasAdmin(actor, SAVE_EDIT_CAPABILITY, "agent_admin.doc_edit_session.save.v1", correlationId);
    var existing = session(request.sessionId());
    ensureSessionActor(actor, existing);
    if (existing.status() == EditSessionStatus.CANCELLED) throw new AuthorizationException(409, "edit-session-cancelled");
    if (existing.status() != EditSessionStatus.PROPOSAL_READY || existing.proposedContent() == null || existing.proposedContent().isBlank()) throw new AuthorizationException(409, "edit-session-proposal-required");
    var current = resolveDocument(new DocumentVersionRequest(existing.agentDefinitionId(), existing.kind(), existing.documentId(), null));
    if (current.currentVersion() != existing.baseVersion()) throw new AuthorizationException(409, "edit-session-base-version-stale");
    var nextVersion = current.currentVersion() + 1;
    saveCurrentSnapshot(current, existing.proposedContent(), actor.account().accountId(), firstNonBlank(existing.changeSummary(), "Saved from Agent Admin edit session " + existing.sessionId()), editSessionTranscript(existing));
    var saved = transitionSession(existing, EditSessionStatus.SAVED, correlationId);
    recordEditTrace("EDIT_SESSION_SAVE", AgentRuntimeTrace.Decision.ALLOWED, saved, correlationId, "edit session saved as document version " + nextVersion, checksum(existing.proposedContent()));
    sessions.put(saved.sessionId(), saved);
    return new SaveEditSessionResult(saved, nextVersion, List.of(traceId("edit-session-save", existing.sessionId(), correlationId)));
  }

  public RestoreVersionResult restoreVersion(AuthContextResolver.ResolvedMe actor, RestoreVersionRequest request, String correlationId) {
    requireSaasAdmin(actor, RESTORE_VERSION_CAPABILITY, "agent_admin.doc_version.restore.v1", correlationId);
    var current = resolveDocument(new DocumentVersionRequest(request.agentDefinitionId(), request.kind(), request.documentId(), null));
    if (request.version() < 1 || request.version() >= current.currentVersion()) throw new AuthorizationException(404, "historical-document-version-required");
    if (!versionExists(current, request.version())) throw new AuthorizationException(404, "document-version-not-found");
    restoreSnapshot(current, request.version(), actor.account().accountId());
    return new RestoreVersionResult(current.agentDefinitionId(), current.kind(), current.documentId(), request.version(), current.currentVersion() + 1, "Restored from version " + request.version(), List.of(traceId("version-restore", current.documentId(), correlationId)));
  }

  public SkillLifecycleResult createSkill(AuthContextResolver.ResolvedMe actor, CreateSkillRequest request, String correlationId) {
    requireSaasAdmin(actor, MANAGE_SKILL_CAPABILITY, "agent_admin.skill.create.v1", correlationId);
    requireNonBlank(request == null ? null : request.agentDefinitionId(), "agentDefinitionId-required");
    requireNonBlank(request.stableSkillId(), "stableSkillId-required");
    requireNonBlank(request.name(), "skill-name-required");
    requireNonBlank(request.contentBody(), "skill-content-required");
    var agent = agent(request.agentDefinitionId());
    var now = Instant.now(clock);
    var documentId = firstNonBlank(request.skillDocumentId(), "skill-" + safeId(request.stableSkillId()));
    if (repository.skillDocument(platformScopeId(), documentId).isPresent()) throw new AuthorizationException(409, "skill-document-already-exists");
    var skill = repository.saveSkillDocument(new SkillDocument(platformScopeId(), documentId, request.stableSkillId(), request.name(), firstNonBlank(request.purpose(), request.name()), request.whenToUse(), List.of("agent-admin-created"), AgentLifecycleStatus.ACTIVE, 1, request.contentBody(), checksum(request.contentBody()), null, now, now));
    var manifest = repository.skillManifest(platformScopeId(), agent.skillManifestId()).orElseThrow(() -> new AuthorizationException(404, "skill-manifest-not-found-or-forbidden"));
    var entries = new ArrayList<>(manifest.entries());
    entries.add(new AgentSkillManifest.Entry(skill.stableSkillId(), skill.skillDocumentId(), skill.activeVersion(), skill.title(), skill.purpose(), skill.whenToUse()));
    repository.saveSkillManifest(new AgentSkillManifest(manifest.tenantId(), manifest.manifestId(), manifest.agentDefinitionId(), manifest.status(), manifest.manifestVersion() + 1, entries, checksum(entries.toString()), manifest.seedProvenance(), manifest.createdAt(), now));
    return new SkillLifecycleResult(agent.agentDefinitionId(), skill.skillDocumentId(), 1, List.of(traceId("skill-create", skill.skillDocumentId(), correlationId)));
  }

  public ReferenceLifecycleResult createReferenceDoc(AuthContextResolver.ResolvedMe actor, CreateReferenceDocRequest request, String correlationId) {
    requireSaasAdmin(actor, MANAGE_REFERENCE_CAPABILITY, "agent_admin.reference.create.v1", correlationId);
    requireNonBlank(request == null ? null : request.agentDefinitionId(), "agentDefinitionId-required");
    requireNonBlank(request.skillDocumentId(), "skillDocumentId-required");
    requireNonBlank(request.stableReferenceId(), "stableReferenceId-required");
    requireNonBlank(request.name(), "reference-name-required");
    requireNonBlank(request.contentBody(), "reference-content-required");
    var agent = agent(request.agentDefinitionId());
    skill(agent, request.skillDocumentId());
    var now = Instant.now(clock);
    var documentId = firstNonBlank(request.referenceDocumentId(), "ref-" + safeId(request.stableReferenceId()));
    if (repository.referenceDocument(platformScopeId(), documentId).isPresent()) throw new AuthorizationException(409, "reference-document-already-exists");
    var tags = List.of("agent-admin-created", skillReferenceTag(request.skillDocumentId()));
    var reference = repository.saveReferenceDocument(new ReferenceDocument(platformScopeId(), documentId, request.stableReferenceId(), request.name(), firstNonBlank(request.description(), request.name()), request.whenToConsult(), request.referenceType() == null ? ReferenceDocument.ReferenceType.OTHER : request.referenceType(), "internal", tags, AgentLifecycleStatus.ACTIVE, 1, request.contentBody(), checksum(request.contentBody()), null, now, now));
    var manifest = repository.referenceManifest(platformScopeId(), agent.referenceManifestId()).orElseThrow(() -> new AuthorizationException(404, "reference-manifest-not-found-or-forbidden"));
    var entries = new ArrayList<>(manifest.entries());
    entries.add(new AgentReferenceManifest.Entry(reference.stableReferenceId(), reference.referenceDocumentId(), reference.activeVersion(), reference.title(), reference.summary(), reference.whenToConsult(), "consult", reference.accessLevel()));
    repository.saveReferenceManifest(new AgentReferenceManifest(manifest.tenantId(), manifest.manifestId(), manifest.agentDefinitionId(), manifest.workstreamExpertBundleId(), manifest.status(), manifest.manifestVersion() + 1, entries, checksum(entries.toString()), manifest.seedProvenance(), manifest.createdAt(), now));
    return new ReferenceLifecycleResult(agent.agentDefinitionId(), request.skillDocumentId(), reference.referenceDocumentId(), 1, List.of(traceId("reference-create", reference.referenceDocumentId(), correlationId)));
  }

  public SkillLifecycleResult deleteSkill(AuthContextResolver.ResolvedMe actor, DeleteSkillRequest request, String correlationId) {
    requireSaasAdmin(actor, MANAGE_SKILL_CAPABILITY, "agent_admin.skill.delete.v1", correlationId);
    requireNonBlank(request == null ? null : request.agentDefinitionId(), "agentDefinitionId-required");
    var agent = agent(request.agentDefinitionId());
    var skill = skill(agent, request.skillDocumentId());
    if (request.confirmation() == null || !request.confirmation().contains(skill.title())) throw new AuthorizationException(400, "skill-delete-confirmation-required");
    var now = Instant.now(clock);
    var referencesToDelete = referencesForSkill(agent, skill.skillDocumentId());
    for (var reference : referencesToDelete) {
      repository.deleteReferenceDocument(platformScopeId(), reference.referenceDocumentId(), actor.account().accountId(), now);
    }
    removeReferencesFromManifest(agent, referencesToDelete.stream().map(ReferenceDocument::referenceDocumentId).toList(), now);
    repository.deleteSkillDocument(platformScopeId(), skill.skillDocumentId(), actor.account().accountId(), now);
    removeSkillFromManifest(agent, skill.skillDocumentId(), now);
    return new SkillLifecycleResult(agent.agentDefinitionId(), skill.skillDocumentId(), 0, List.of(traceId("skill-delete", skill.skillDocumentId(), correlationId)));
  }

  public ReferenceLifecycleResult deleteReferenceDoc(AuthContextResolver.ResolvedMe actor, DeleteReferenceDocRequest request, String correlationId) {
    requireSaasAdmin(actor, MANAGE_REFERENCE_CAPABILITY, "agent_admin.reference.delete.v1", correlationId);
    requireNonBlank(request == null ? null : request.agentDefinitionId(), "agentDefinitionId-required");
    var agent = agent(request.agentDefinitionId());
    var reference = reference(agent, request.referenceDocumentId());
    if (request.confirmation() == null || !request.confirmation().contains(reference.title())) throw new AuthorizationException(400, "reference-delete-confirmation-required");
    var now = Instant.now(clock);
    repository.deleteReferenceDocument(platformScopeId(), reference.referenceDocumentId(), actor.account().accountId(), now);
    removeReferencesFromManifest(agent, List.of(reference.referenceDocumentId()), now);
    return new ReferenceLifecycleResult(agent.agentDefinitionId(), null, reference.referenceDocumentId(), 0, List.of(traceId("reference-delete", reference.referenceDocumentId(), correlationId)));
  }

  public RuntimeDocReadTraceResponse runtimeDocReadTraces(AuthContextResolver.ResolvedMe actor, RuntimeDocReadTraceQuery query, String correlationId) {
    requireSaasAdmin(actor, READ_AGENT_CAPABILITY, "agent_admin.runtime_doc_reads.trace_rows.v1", correlationId);
    var safeQuery = query == null ? new RuntimeDocReadTraceQuery(null, null, null, null) : query;
    var rows = traceSink.traces().stream()
        .filter(this::isRuntimeDocReadTrace)
        .filter(trace -> isBlank(safeQuery.agentDefinitionId()) || safeQuery.agentDefinitionId().equals(trace.agentDefinitionId()))
        .filter(trace -> isBlank(safeQuery.documentIdOrStableId()) || safeQuery.documentIdOrStableId().equals(trace.targetId()) || documentName(trace).contains(safeQuery.documentIdOrStableId()))
        .filter(trace -> safeQuery.occurredAtFrom() == null || !trace.occurredAt().isBefore(safeQuery.occurredAtFrom()))
        .filter(trace -> safeQuery.occurredAtTo() == null || !trace.occurredAt().isAfter(safeQuery.occurredAtTo()))
        .sorted(Comparator.comparing(AgentRuntimeTrace::occurredAt).reversed())
        .map(this::runtimeDocReadTraceRow)
        .toList();
    return new RuntimeDocReadTraceResponse(rows, List.of(traceId("runtime-doc-read-trace-rows", firstNonBlank(safeQuery.agentDefinitionId(), "all"), correlationId)));
  }

  private boolean isRuntimeDocReadTrace(AgentRuntimeTrace trace) {
    return trace != null && ("SKILL_LOAD".equals(trace.traceType()) || "REFERENCE_LOAD".equals(trace.traceType()));
  }

  private RuntimeDocReadTraceRow runtimeDocReadTraceRow(AgentRuntimeTrace trace) {
    var agent = repository.agentDefinition(platformScopeId(), trace.agentDefinitionId()).orElse(null);
    var agentName = agent == null ? trace.agentDefinitionId() : agent.displayName();
    var docType = "SKILL_LOAD".equals(trace.traceType()) ? "skill" : "reference";
    var docName = documentName(trace);
    return new RuntimeDocReadTraceRow(
        trace.traceId(),
        trace.occurredAt(),
        trace.agentDefinitionId(),
        agentName,
        docType,
        trace.targetId(),
        docName,
        docType + ": " + docName,
        trace.correlationId(),
        trace.actorId(),
        userCustomerContext(trace),
        trace.decision().name(),
        trace.safeSummary());
  }

  private String documentName(AgentRuntimeTrace trace) {
    if (trace == null) return "";
    if ("SKILL_LOAD".equals(trace.traceType())) {
      return repository.skillDocuments(platformScopeId()).stream()
          .filter(skill -> trace.targetId().equals(skill.stableSkillId()) || trace.targetId().equals(skill.skillDocumentId()))
          .map(SkillDocument::title)
          .findFirst()
          .orElse(trace.targetId());
    }
    if ("REFERENCE_LOAD".equals(trace.traceType())) {
      return repository.referenceDocuments(platformScopeId()).stream()
          .filter(reference -> trace.targetId().equals(reference.stableReferenceId()) || trace.targetId().equals(reference.referenceDocumentId()))
          .map(ReferenceDocument::title)
          .findFirst()
          .orElse(trace.targetId());
    }
    return trace.targetId();
  }

  private String userCustomerContext(AgentRuntimeTrace trace) {
    var summary = trace.safeSummary() == null ? "" : trace.safeSummary();
    var marker = "userContext=";
    var index = summary.indexOf(marker);
    if (index >= 0) {
      var end = summary.indexOf(";", index);
      return end >= 0 ? summary.substring(index + marker.length(), end) : summary.substring(index + marker.length());
    }
    return "accountId=" + safe(trace.actorId());
  }

  private EditSessionRecord invokeEditingAgent(AuthContextResolver.ResolvedMe actor, EditSessionRecord session, DocumentSnapshot base, String priorProposal, String correlationId) {
    var agent = agent(session.agentDefinitionId());
    var result = editingRuntime.proposeEdit(new AgentAdminDocEditingRuntime.EditProposalRequest(
        platformScopeId(),
        actor.selectedContext(),
        actor.account().accountId(),
        agent.agentDefinitionId(),
        agent.displayName(),
        session.kind(),
        session.documentId(),
        session.baseVersion(),
        base.contentBody(),
        sameAgentEditingContext(agent, session.documentId()),
        session.instructions().stream().map(EditInstruction::instructions).toList(),
        priorProposal,
        correlationId,
        session.traceLinks()));
    if (result.decision() != AgentRuntimeTrace.Decision.ALLOWED || result.proposal() == null) {
      recordEditTrace("EDIT_AGENT_INVOCATION", AgentRuntimeTrace.Decision.DENIED, session, correlationId, firstNonBlank(result.safeErrorSummary(), "editing agent unavailable"), null);
      throw new AuthorizationException(503, firstNonBlank(result.safeErrorCode(), "AGENT_ADMIN_DOC_EDITING_RUNTIME_UNAVAILABLE"));
    }
    var proposal = result.proposal();
    var status = normalizeProposalStatus(proposal.status(), proposal.proposedMarkdown());
    var warnings = proposal.warnings().stream().map(AgentAdminDocAdministrationService::safe).toList();
    var traceLinks = new ArrayList<>(result.traceIds());
    traceLinks.add(traceId("edit-session-agent-proposal", session.sessionId(), correlationId));
    recordEditTrace("EDIT_AGENT_INVOCATION", AgentRuntimeTrace.Decision.ALLOWED, session, correlationId, "editing agent returned status=" + status + "; " + safe(proposal.changeSummary()), checksum(proposal.proposedMarkdown()));
    return new EditSessionRecord(
        session.sessionId(),
        session.agentDefinitionId(),
        session.kind(),
        session.documentId(),
        session.baseVersion(),
        session.actorAccountId(),
        status,
        session.instructions(),
        status == EditSessionStatus.PROPOSAL_READY ? safe(proposal.proposedMarkdown()) : session.proposedContent(),
        safe(proposal.changeSummary()),
        safe(proposal.clarifyingQuestion()),
        warnings,
        List.copyOf(traceLinks),
        session.startedAt(),
        null);
  }

  private EditSessionStatus normalizeProposalStatus(String status, String proposedMarkdown) {
    var normalized = status == null ? "" : status.toLowerCase(Locale.ROOT);
    if ("clarification_requested".equals(normalized)) return EditSessionStatus.CLARIFICATION_REQUESTED;
    if ("refused".equals(normalized)) return EditSessionStatus.REFUSED;
    if (!isBlank(proposedMarkdown)) return EditSessionStatus.PROPOSAL_READY;
    return EditSessionStatus.CLARIFICATION_REQUESTED;
  }

  private String sameAgentEditingContext(AgentDefinition agent, String targetDocumentId) {
    var parts = new ArrayList<String>();
    repository.promptDocument(platformScopeId(), agent.promptDocumentId())
        .ifPresent(prompt -> parts.add("## Agent prompt: " + prompt.title() + " (" + prompt.promptDocumentId() + ")\n" + redactIfTarget(prompt.promptDocumentId(), targetDocumentId, prompt.contentBody())));
    var skillManifest = repository.skillManifest(platformScopeId(), agent.skillManifestId()).orElse(null);
    if (skillManifest != null) {
      for (var entry : skillManifest.entries()) {
        repository.skillDocument(platformScopeId(), entry.skillDocumentId())
            .ifPresent(skill -> parts.add("## Skill: " + skill.title() + " (" + skill.skillDocumentId() + ")\nPurpose: " + skill.purpose() + "\nWhen to use: " + skill.whenToUse() + "\n" + redactIfTarget(skill.skillDocumentId(), targetDocumentId, skill.contentBody())));
      }
    }
    var referenceManifest = repository.referenceManifest(platformScopeId(), agent.referenceManifestId()).orElse(null);
    if (referenceManifest != null) {
      for (var entry : referenceManifest.entries()) {
        repository.referenceDocument(platformScopeId(), entry.referenceDocumentId())
            .ifPresent(reference -> parts.add("## Reference: " + reference.title() + " (" + reference.referenceDocumentId() + ")\nSummary: " + reference.summary() + "\nWhen to consult: " + reference.whenToConsult() + "\n" + redactIfTarget(reference.referenceDocumentId(), targetDocumentId, reference.contentBody())));
      }
    }
    return String.join("\n\n", parts);
  }

  private static String redactIfTarget(String documentId, String targetDocumentId, String contentBody) {
    return Objects.equals(documentId, targetDocumentId) ? "<target document content supplied separately>" : contentBody;
  }

  private void recordEditTrace(String traceType, AgentRuntimeTrace.Decision decision, EditSessionRecord session, String correlationId, String safeSummary, String checksum) {
    traceSink.record(new AgentRuntimeTrace(
        traceId(traceType.toLowerCase(Locale.ROOT), session.sessionId(), correlationId),
        Instant.now(clock),
        platformScopeId(),
        session.agentDefinitionId(),
        correlationId,
        session.sessionId(),
        traceType,
        decision,
        session.actorAccountId(),
        capabilityForTrace(traceType),
        session.documentId(),
        safe(safeSummary),
        checksum));
  }

  private static String capabilityForTrace(String traceType) {
    if ("EDIT_SESSION_SAVE".equals(traceType)) return SAVE_EDIT_CAPABILITY;
    if ("EDIT_SESSION_CANCEL".equals(traceType)) return CANCEL_EDIT_CAPABILITY;
    return DRAFT_EDIT_CAPABILITY;
  }

  private void requireDocumentCapability(AuthContextResolver.ResolvedMe actor, AgentDocKind kind, String surfaceContract, String correlationId) {
    var capability = switch (kind == null ? AgentDocKind.PROMPT : kind) {
      case PROMPT -> READ_PROMPT_CAPABILITY;
      case SKILL -> READ_SKILL_CAPABILITY;
      case REFERENCE -> READ_REFERENCE_CAPABILITY;
    };
    requireSaasAdmin(actor, capability, surfaceContract, correlationId);
  }

  private void requireSaasAdmin(AuthContextResolver.ResolvedMe actor, String capabilityId, String surfaceContract, String correlationId) {
    var context = actor.selectedContext();
    if (context.scopeType() != ScopeType.SAAS_OWNER || !context.roles().contains(FoundationRole.SAAS_OWNER_ADMIN)) {
      authContextResolver.appendDeniedTrace(actor, capabilityId, "agent-admin-requires-saas-owner-admin", correlationId);
      throw new AuthorizationException(403, "agent-admin-requires-saas-owner-admin");
    }
    authContextResolver.requireCapability(context, capabilityId);
    authContextResolver.appendProtectedReadTrace(actor, capabilityId, surfaceContract, correlationId);
  }

  private AgentListRow agentListRow(AgentDefinition agent) {
    return new AgentListRow(agent.agentDefinitionId(), agent.displayName(), agent.description(), agent.functionalAreaId(), agent.updatedAt());
  }

  private AgentDefinition agent(String agentDefinitionId) {
    requireNonBlank(agentDefinitionId, "agentDefinitionId-required");
    return repository.agentDefinition(platformScopeId(), agentDefinitionId)
        .orElseThrow(() -> new AuthorizationException(404, "agent-not-found-or-forbidden"));
  }

  private PromptDocument prompt(AgentDefinition agent) {
    return repository.promptDocument(platformScopeId(), agent.promptDocumentId())
        .orElseThrow(() -> new AuthorizationException(404, "prompt-not-found-or-forbidden"));
  }

  private List<SkillDocSummary> skillSummaries(AgentDefinition agent) {
    var manifest = repository.skillManifest(platformScopeId(), agent.skillManifestId())
        .orElse(new AgentSkillManifest(platformScopeId(), agent.skillManifestId(), agent.agentDefinitionId(), agent.status(), 0, List.of(), "", agent.seedProvenance(), agent.createdAt(), agent.updatedAt()));
    return manifest.entries().stream().map(entry -> {
      var skill = repository.skillDocument(platformScopeId(), entry.skillDocumentId()).orElse(null);
      return new SkillDocSummary(
          entry.stableSkillId(),
          entry.skillDocumentId(),
          skill == null ? entry.title() : skill.title(),
          skill == null ? entry.purpose() : skill.purpose(),
          skill == null ? entry.pinnedVersion() : skill.activeVersion(),
          skill == null ? null : skill.updatedAt(),
          referenceSummaries(agent));
    }).toList();
  }

  private List<ReferenceDocSummary> referenceSummaries(AgentDefinition agent) {
    var manifest = repository.referenceManifest(platformScopeId(), agent.referenceManifestId())
        .orElse(new AgentReferenceManifest(platformScopeId(), agent.referenceManifestId(), agent.agentDefinitionId(), null, agent.status(), 0, List.of(), "", agent.seedProvenance(), agent.createdAt(), agent.updatedAt()));
    return manifest.entries().stream().map(entry -> {
      var reference = repository.referenceDocument(platformScopeId(), entry.referenceDocumentId()).orElse(null);
      return new ReferenceDocSummary(
          entry.stableReferenceId(),
          entry.referenceDocumentId(),
          reference == null ? entry.title() : reference.title(),
          reference == null ? entry.summary() : reference.summary(),
          reference == null ? entry.pinnedVersion() : reference.activeVersion(),
          reference == null ? null : reference.updatedAt());
    }).toList();
  }

  private DocumentSnapshot resolveDocument(DocumentVersionRequest request) {
    if (request == null) throw new AuthorizationException(400, "document-request-required");
    var agent = agent(request.agentDefinitionId());
    var kind = request.kind() == null ? AgentDocKind.PROMPT : request.kind();
    return switch (kind) {
      case PROMPT -> {
        var prompt = prompt(agent);
        if (request.documentId() != null && !request.documentId().isBlank() && !request.documentId().equals(prompt.promptDocumentId())) {
          throw new AuthorizationException(404, "document-not-found-or-forbidden");
        }
        yield new DocumentSnapshot(agent.agentDefinitionId(), kind, prompt.promptDocumentId(), prompt.title(), prompt.changeSummary(), prompt.activeVersion(), prompt.activeVersion(), prompt.contentBody(), prompt.contentChecksum(), prompt.updatedAt(), "system", prompt.changeSummary());
      }
      case SKILL -> {
        var skill = skill(agent, request.documentId());
        yield new DocumentSnapshot(agent.agentDefinitionId(), kind, skill.skillDocumentId(), skill.title(), skill.purpose(), skill.activeVersion(), skill.activeVersion(), skill.contentBody(), skill.contentChecksum(), skill.updatedAt(), "system", skill.purpose());
      }
      case REFERENCE -> {
        var reference = reference(agent, request.documentId());
        yield new DocumentSnapshot(agent.agentDefinitionId(), kind, reference.referenceDocumentId(), reference.title(), reference.summary(), reference.activeVersion(), reference.activeVersion(), reference.contentBody(), reference.contentChecksum(), reference.updatedAt(), "system", reference.summary());
      }
    };
  }

  private SkillDocument skill(AgentDefinition agent, String documentId) {
    var manifest = repository.skillManifest(platformScopeId(), agent.skillManifestId())
        .orElseThrow(() -> new AuthorizationException(404, "skill-manifest-not-found-or-forbidden"));
    var entry = manifest.entries().stream()
        .filter(candidate -> documentId == null || documentId.isBlank() || candidate.skillDocumentId().equals(documentId) || candidate.stableSkillId().equals(documentId))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "skill-not-found-or-forbidden"));
    return repository.skillDocument(platformScopeId(), entry.skillDocumentId())
        .orElseThrow(() -> new AuthorizationException(404, "skill-not-found-or-forbidden"));
  }

  private ReferenceDocument reference(AgentDefinition agent, String documentId) {
    var manifest = repository.referenceManifest(platformScopeId(), agent.referenceManifestId())
        .orElseThrow(() -> new AuthorizationException(404, "reference-manifest-not-found-or-forbidden"));
    var entry = manifest.entries().stream()
        .filter(candidate -> documentId == null || documentId.isBlank() || candidate.referenceDocumentId().equals(documentId) || candidate.stableReferenceId().equals(documentId))
        .findFirst()
        .orElseThrow(() -> new AuthorizationException(404, "reference-not-found-or-forbidden"));
    return repository.referenceDocument(platformScopeId(), entry.referenceDocumentId())
        .orElseThrow(() -> new AuthorizationException(404, "reference-not-found-or-forbidden"));
  }

  private DocumentSnapshot resolveDocumentVersion(DocumentVersionRequest request) {
    var current = resolveDocument(request);
    var selectedVersion = request.version() == null ? current.currentVersion() : request.version();
    if (selectedVersion < 1 || selectedVersion > current.currentVersion()) throw new AuthorizationException(404, "document-version-not-found");
    return switch (current.kind()) {
      case PROMPT -> repository.promptVersion(platformScopeId(), current.documentId(), selectedVersion)
          .map(version -> new DocumentSnapshot(current.agentDefinitionId(), AgentDocKind.PROMPT, version.promptDocumentId(), version.title(), version.changeSummary(), current.currentVersion(), version.version(), version.contentBody(), version.contentChecksum(), version.createdAt(), version.actorAccountId(), version.editSessionTranscriptSummary()))
          .orElseGet(() -> selectedVersion == current.currentVersion() ? current : missingDocumentVersion());
      case SKILL -> repository.skillVersion(platformScopeId(), current.documentId(), selectedVersion)
          .map(version -> new DocumentSnapshot(current.agentDefinitionId(), AgentDocKind.SKILL, version.skillDocumentId(), version.title(), version.purpose(), current.currentVersion(), version.version(), version.contentBody(), version.contentChecksum(), version.createdAt(), version.actorAccountId(), version.editSessionTranscriptSummary()))
          .orElseGet(() -> selectedVersion == current.currentVersion() ? current : missingDocumentVersion());
      case REFERENCE -> repository.referenceVersion(platformScopeId(), current.documentId(), selectedVersion)
          .map(version -> new DocumentSnapshot(current.agentDefinitionId(), AgentDocKind.REFERENCE, version.referenceDocumentId(), version.title(), version.summary(), current.currentVersion(), version.version(), version.contentBody(), version.contentChecksum(), version.createdAt(), version.actorAccountId(), version.editSessionTranscriptSummary()))
          .orElseGet(() -> selectedVersion == current.currentVersion() ? current : missingDocumentVersion());
    };
  }

  private static DocumentSnapshot missingDocumentVersion() {
    throw new AuthorizationException(404, "document-version-not-found");
  }

  private List<DocumentSnapshot> versionSnapshots(DocumentSnapshot current) {
    return switch (current.kind()) {
      case PROMPT -> repository.promptVersions(platformScopeId(), current.documentId()).stream()
          .map(version -> new DocumentSnapshot(current.agentDefinitionId(), AgentDocKind.PROMPT, version.promptDocumentId(), version.title(), version.changeSummary(), current.currentVersion(), version.version(), version.contentBody(), version.contentChecksum(), version.createdAt(), version.actorAccountId(), version.editSessionTranscriptSummary()))
          .toList();
      case SKILL -> repository.skillVersions(platformScopeId(), current.documentId()).stream()
          .map(version -> new DocumentSnapshot(current.agentDefinitionId(), AgentDocKind.SKILL, version.skillDocumentId(), version.title(), version.purpose(), current.currentVersion(), version.version(), version.contentBody(), version.contentChecksum(), version.createdAt(), version.actorAccountId(), version.editSessionTranscriptSummary()))
          .toList();
      case REFERENCE -> repository.referenceVersions(platformScopeId(), current.documentId()).stream()
          .map(version -> new DocumentSnapshot(current.agentDefinitionId(), AgentDocKind.REFERENCE, version.referenceDocumentId(), version.title(), version.summary(), current.currentVersion(), version.version(), version.contentBody(), version.contentChecksum(), version.createdAt(), version.actorAccountId(), version.editSessionTranscriptSummary()))
          .toList();
    };
  }

  private boolean versionExists(DocumentSnapshot current, int version) {
    return switch (current.kind()) {
      case PROMPT -> repository.promptVersion(platformScopeId(), current.documentId(), version).isPresent();
      case SKILL -> repository.skillVersion(platformScopeId(), current.documentId(), version).isPresent();
      case REFERENCE -> repository.referenceVersion(platformScopeId(), current.documentId(), version).isPresent();
    };
  }

  private void saveCurrentSnapshot(DocumentSnapshot current, String contentBody, String actorAccountId, String changeSummary, String editSessionTranscriptSummary) {
    var command = new AgentBehaviorRepository.DocumentVersionSave(platformScopeId(), current.documentId(), current.currentVersion(), contentBody, actorAccountId, changeSummary, editSessionTranscriptSummary, Instant.now(clock));
    try {
      switch (current.kind()) {
        case PROMPT -> repository.savePromptDocumentVersion(command);
        case SKILL -> repository.saveSkillDocumentVersion(command);
        case REFERENCE -> repository.saveReferenceDocumentVersion(command);
      }
    } catch (IllegalStateException failure) {
      if ("stale-current-version".equals(failure.getMessage())) throw new AuthorizationException(409, "edit-session-base-version-stale");
      throw failure;
    }
  }

  private void restoreSnapshot(DocumentSnapshot current, int version, String actorAccountId) {
    var command = new AgentBehaviorRepository.DocumentVersionRestore(platformScopeId(), current.documentId(), version, actorAccountId, Instant.now(clock));
    try {
      switch (current.kind()) {
        case PROMPT -> repository.restorePromptDocumentVersion(command);
        case SKILL -> repository.restoreSkillDocumentVersion(command);
        case REFERENCE -> repository.restoreReferenceDocumentVersion(command);
      }
    } catch (IllegalStateException failure) {
      throw new AuthorizationException(404, failure.getMessage());
    }
  }

  private EditSessionRecord session(String sessionId) {
    requireNonBlank(sessionId, "editSessionId-required");
    var session = sessions.get(sessionId);
    if (session == null) throw new AuthorizationException(404, "edit-session-not-found-or-forbidden");
    return session;
  }

  private void ensureSessionActor(AuthContextResolver.ResolvedMe actor, EditSessionRecord session) {
    if (!actor.account().accountId().equals(session.actorAccountId())) throw new AuthorizationException(403, "edit-session-actor-mismatch");
  }

  private EditSessionRecord transitionSession(EditSessionRecord existing, EditSessionStatus status, String correlationId) {
    return new EditSessionRecord(
        existing.sessionId(),
        existing.agentDefinitionId(),
        existing.kind(),
        existing.documentId(),
        existing.baseVersion(),
        existing.actorAccountId(),
        status,
        existing.instructions(),
        existing.proposedContent(),
        existing.changeSummary(),
        existing.clarifyingQuestion(),
        existing.warnings(),
        appendTrace(existing.traceLinks(), traceId("edit-session-" + status.name().toLowerCase(Locale.ROOT), existing.sessionId(), correlationId)),
        existing.startedAt(),
        Instant.now(clock));
  }

  private DocumentSummary docSummary(AgentDocKind kind, String documentId, String title, String description, int currentVersion, Instant updatedAt) {
    return new DocumentSummary(kind, documentId, title, description, currentVersion, updatedAt);
  }

  private List<ReferenceDocument> referencesForSkill(AgentDefinition agent, String skillDocumentId) {
    var tag = skillReferenceTag(skillDocumentId);
    return referenceSummaries(agent).stream()
        .map(summary -> repository.referenceDocument(platformScopeId(), summary.documentId()).orElse(null))
        .filter(Objects::nonNull)
        .filter(reference -> reference.tags().contains(tag))
        .toList();
  }

  private void removeSkillFromManifest(AgentDefinition agent, String skillDocumentId, Instant now) {
    var manifest = repository.skillManifest(platformScopeId(), agent.skillManifestId()).orElseThrow();
    var entries = manifest.entries().stream()
        .filter(entry -> !entry.skillDocumentId().equals(skillDocumentId))
        .toList();
    repository.saveSkillManifest(new AgentSkillManifest(manifest.tenantId(), manifest.manifestId(), manifest.agentDefinitionId(), manifest.status(), manifest.manifestVersion() + 1, entries, checksum(entries.toString()), manifest.seedProvenance(), manifest.createdAt(), now));
  }

  private void removeReferencesFromManifest(AgentDefinition agent, List<String> referenceDocumentIds, Instant now) {
    if (referenceDocumentIds.isEmpty()) return;
    var manifest = repository.referenceManifest(platformScopeId(), agent.referenceManifestId()).orElseThrow();
    var ids = java.util.Set.copyOf(referenceDocumentIds);
    var entries = manifest.entries().stream()
        .filter(entry -> !ids.contains(entry.referenceDocumentId()))
        .toList();
    repository.saveReferenceManifest(new AgentReferenceManifest(manifest.tenantId(), manifest.manifestId(), manifest.agentDefinitionId(), manifest.workstreamExpertBundleId(), manifest.status(), manifest.manifestVersion() + 1, entries, checksum(entries.toString()), manifest.seedProvenance(), manifest.createdAt(), now));
  }

  private static String editSessionTranscript(EditSessionRecord session) {
    var instructionTranscript = session.instructions().stream()
        .map(instruction -> instruction.actorAccountId() + ": " + instruction.instructions())
        .collect(java.util.stream.Collectors.joining("\n"));
    return instructionTranscript
        + "\n\nchangeSummary: " + safe(session.changeSummary())
        + "\nwarnings: " + session.warnings()
        + "\nproposedContentChecksum: " + checksum(session.proposedContent());
  }

  private static String simpleUnifiedDiff(String prior, String selected) {
    if (Objects.equals(prior, selected)) return "";
    return "--- version N-1\n+++ version N\n-" + String.valueOf(prior).replace("\n", "\n-") + "\n+" + String.valueOf(selected).replace("\n", "\n+");
  }

  private static String skillReferenceTag(String skillDocumentId) {
    return "skill-document:" + skillDocumentId;
  }

  private static String safeId(String value) {
    return String.valueOf(value).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
  }

  private Instant lastEditTime(AgentDefinition agent, PromptDocument prompt, List<Instant> skillTimes, List<Instant> referenceTimes) {
    var times = new ArrayList<Instant>();
    if (agent.updatedAt() != null) times.add(agent.updatedAt());
    if (prompt.updatedAt() != null) times.add(prompt.updatedAt());
    skillTimes.stream().filter(Objects::nonNull).forEach(times::add);
    referenceTimes.stream().filter(Objects::nonNull).forEach(times::add);
    return times.stream().max(Comparator.naturalOrder()).orElse(null);
  }

  private static boolean contains(String haystack, String needle) {
    if (needle == null || needle.isBlank()) return true;
    return String.valueOf(haystack).toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
  }

  private static String firstNonBlank(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private static String safe(String value) {
    return value == null ? null : value.replaceAll("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
  }

  private static void requireNonBlank(String value, String reason) {
    if (value == null || value.isBlank()) throw new AuthorizationException(400, reason);
  }

  private static List<String> appendTrace(List<String> existing, String traceId) {
    var traces = new ArrayList<>(existing == null ? List.of() : existing);
    traces.add(traceId);
    return List.copyOf(traces);
  }

  private static String platformScopeId() {
    return WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID;
  }

  private static String traceId(String kind, String id, String correlationId) {
    return "trace-agent-admin-doc-" + kind + "-" + Math.abs((String.valueOf(id) + ":" + String.valueOf(correlationId)).hashCode());
  }

  private static String checksum(String content) {
    try {
      var digest = MessageDigest.getInstance("SHA-256").digest(String.valueOf(content).getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException failure) {
      throw new IllegalStateException(failure);
    }
  }

  private record DocumentSnapshot(String agentDefinitionId, AgentDocKind kind, String documentId, String title, String description, int currentVersion, int version, String contentBody, String checksum, Instant updatedAt, String actorAccountId, String editSessionTranscriptSummary) {
    DocumentVersionDetail toVersionDetail(String correlationId) {
      var current = version == currentVersion;
      return new DocumentVersionDetail(agentDefinitionId, kind, documentId, version, current, current, title, description, contentBody, checksum, updatedAt, actorAccountId, editSessionTranscriptSummary, List.of(traceId("version-read", documentId, correlationId)));
    }
  }

  private static final class AgentRuntimeCapabilityIds {
    private static final String DRAFT_BEHAVIOR_CHANGE = "agent_admin.draft_behavior_change";
    private static final String CANCEL_BEHAVIOR_CHANGE = "agent_admin.cancel_behavior_change";
    private static final String ACTIVATE_BEHAVIOR_CHANGE = "agent_admin.activate_behavior_change";
    private static final String ROLLBACK_BEHAVIOR_CHANGE = "agent_admin.rollback_behavior_change";
  }

  public enum AgentDocKind { PROMPT, SKILL, REFERENCE }

  public enum EditSessionStatus { DRAFTING, CLARIFICATION_REQUESTED, PROPOSAL_READY, REFUSED, SAVED, CANCELLED }

  public enum DiffStatus { READY, NO_PRIOR_VERSION, PRIOR_VERSION_NOT_AVAILABLE_IN_CURRENT_SLICE }

  public record AgentListRequest(String nameContains, String workstreamOrDomain) {}

  public record AgentListRow(String agentDefinitionId, String agentName, String shortPurpose, String workstreamDomain, Instant lastEditTime) {}

  public record AgentListResponse(List<AgentListRow> rows, int totalCount, int filteredCount, List<String> traceLinks) {
    public AgentListResponse {
      rows = List.copyOf(rows == null ? List.of() : rows);
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record AgentProfileUpdateRequest(String agentDefinitionId, String agentName, String purpose) {}

  public record AgentProfileUpdateResult(AgentListRow row, List<String> traceLinks) {
    public AgentProfileUpdateResult {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record AgentDocDetail(String agentDefinitionId, String agentName, String purpose, String workstreamDomain, Instant lastEditTime, DocumentSummary prompt, List<SkillDocSummary> skills, List<ReferenceDocSummary> referenceDocs, List<String> traceLinks) {
    public AgentDocDetail {
      skills = List.copyOf(skills == null ? List.of() : skills);
      referenceDocs = List.copyOf(referenceDocs == null ? List.of() : referenceDocs);
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record DocumentSummary(AgentDocKind kind, String documentId, String title, String description, int currentVersion, Instant updatedAt) {}

  public record SkillDocSummary(String stableSkillId, String documentId, String name, String purpose, int currentVersion, Instant updatedAt, List<ReferenceDocSummary> referenceDocs) {
    public SkillDocSummary {
      referenceDocs = List.copyOf(referenceDocs == null ? List.of() : referenceDocs);
    }
  }

  public record ReferenceDocSummary(String stableReferenceId, String documentId, String name, String description, int currentVersion, Instant updatedAt) {}

  public record DocumentVersionRequest(String agentDefinitionId, AgentDocKind kind, String documentId, Integer version) {}

  public record DocumentVersionDetail(String agentDefinitionId, AgentDocKind kind, String documentId, int version, boolean currentVersion, boolean editable, String title, String description, String contentBody, String contentChecksum, Instant createdAt, String actorAccountId, String editSessionTranscriptSummary, List<String> traceLinks) {
    public DocumentVersionDetail {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record VersionHistoryRow(int version, boolean currentVersion, Instant createdAt, String label) {}

  public record VersionHistoryResponse(String agentDefinitionId, AgentDocKind kind, String documentId, List<VersionHistoryRow> rows, List<String> traceLinks) {
    public VersionHistoryResponse {
      rows = List.copyOf(rows == null ? List.of() : rows);
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record AdjacentDiffResponse(String agentDefinitionId, AgentDocKind kind, String documentId, Integer priorVersion, int selectedVersion, DiffStatus status, String unifiedDiff, List<String> traceLinks) {
    public AdjacentDiffResponse {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record StartEditSessionRequest(String agentDefinitionId, AgentDocKind kind, String documentId, String initialInstructions) {}

  public record ReviseEditSessionRequest(String sessionId, String instructions, String proposedContent, String changeSummary, List<String> warnings) {
    public ReviseEditSessionRequest {
      warnings = List.copyOf(warnings == null ? List.of() : warnings);
    }
  }

  public record EditSessionCommandRequest(String sessionId) {}

  public record AgentDraftEditSessionRequest(String agentDefinitionId, AgentDocKind kind, String documentId, String instructions) {}

  public record AgentReviseEditSessionRequest(String sessionId, String instructions) {}

  public record EditInstruction(Instant at, String actorAccountId, String instructions) {}

  public record EditSessionRecord(String sessionId, String agentDefinitionId, AgentDocKind kind, String documentId, int baseVersion, String actorAccountId, EditSessionStatus status, List<EditInstruction> instructions, String proposedContent, String changeSummary, String clarifyingQuestion, List<String> warnings, List<String> traceLinks, Instant startedAt, Instant endedAt) {
    public EditSessionRecord {
      instructions = List.copyOf(instructions == null ? List.of() : instructions);
      warnings = List.copyOf(warnings == null ? List.of() : warnings);
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record SaveEditSessionResult(EditSessionRecord session, int savedVersion, List<String> traceLinks) {
    public SaveEditSessionResult {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record RestoreVersionRequest(String agentDefinitionId, AgentDocKind kind, String documentId, int version) {}

  public record RestoreVersionResult(String agentDefinitionId, AgentDocKind kind, String documentId, int restoredFromVersion, int newCurrentVersion, String summary, List<String> traceLinks) {
    public RestoreVersionResult {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record CreateSkillRequest(String agentDefinitionId, String skillDocumentId, String stableSkillId, String name, String purpose, String whenToUse, String contentBody, String editSessionTranscriptSummary) {}
  public record DeleteSkillRequest(String agentDefinitionId, String skillDocumentId, String confirmation) {}
  public record SkillLifecycleResult(String agentDefinitionId, String skillDocumentId, int currentVersion, List<String> traceLinks) {
    public SkillLifecycleResult {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record CreateReferenceDocRequest(String agentDefinitionId, String skillDocumentId, String referenceDocumentId, String stableReferenceId, String name, String description, String whenToConsult, ReferenceDocument.ReferenceType referenceType, String contentBody, String editSessionTranscriptSummary) {}
  public record DeleteReferenceDocRequest(String agentDefinitionId, String referenceDocumentId, String confirmation) {}
  public record ReferenceLifecycleResult(String agentDefinitionId, String skillDocumentId, String referenceDocumentId, int currentVersion, List<String> traceLinks) {
    public ReferenceLifecycleResult {
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record RuntimeDocReadTraceQuery(String agentDefinitionId, String documentIdOrStableId, Instant occurredAtFrom, Instant occurredAtTo) {}

  public record RuntimeDocReadTraceResponse(List<RuntimeDocReadTraceRow> rows, List<String> traceLinks) {
    public RuntimeDocReadTraceResponse {
      rows = List.copyOf(rows == null ? List.of() : rows);
      traceLinks = List.copyOf(traceLinks == null ? List.of() : traceLinks);
    }
  }

  public record RuntimeDocReadTraceRow(
      String traceId,
      Instant occurredAt,
      String agentDefinitionId,
      String agentName,
      String documentType,
      String documentIdOrStableId,
      String documentName,
      String documentRead,
      String requestSessionId,
      String actorAccountId,
      String userCustomerContext,
      String decision,
      String safeSummary) {}

  private static final class NoopAgentRuntimeTraceSink implements AgentRuntimeTraceSink {
    @Override
    public AgentRuntimeTrace record(AgentRuntimeTrace trace) {
      return trace;
    }

    @Override
    public List<AgentRuntimeTrace> traces() {
      return List.of();
    }
  }
}
