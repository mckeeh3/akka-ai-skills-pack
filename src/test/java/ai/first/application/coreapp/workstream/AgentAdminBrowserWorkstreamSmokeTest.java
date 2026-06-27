package ai.first.application.coreapp.workstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import ai.first.application.coreapp.workstream.WorkstreamService.CapabilityActionRequest;
import ai.first.application.coreapp.workstream.WorkstreamService.CapabilityActionResult;
import ai.first.application.coreapp.workstream.WorkstreamService.SurfaceEnvelope;
import ai.first.application.coreapp.workstream.WorkstreamService.WorkstreamBootstrapResponse;
import ai.first.application.foundation.identity.AkkaIdentityRepository;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Scriptable Akka-hosted UI/API smoke for the current SaaS-admin-only Agent Admin doc editor. */
class AgentAdminBrowserWorkstreamSmokeTest extends TestKitSupport {
  private static final String TENANT_ID = "tenant-agent-admin-smoke";
  private static final String OWNER_CONTEXT_ID = "membership-agent-admin-owner";
  private static final String TENANT_CONTEXT_ID = "membership-agent-admin-tenant";

  @BeforeEach
  void seedAgentAdminSmokeActors() {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant(TENANT_ID, "Agent Admin Smoke Tenant", true));
    seedIdentity(repository, "owner@example.test", "SaaS Owner", OWNER_CONTEXT_ID, ScopeType.SAAS_OWNER, null, List.of(FoundationRole.SAAS_OWNER_ADMIN));
    seedIdentity(repository, "tenant-admin@example.test", "Tenant Admin", TENANT_CONTEXT_ID, ScopeType.TENANT, TENANT_ID, List.of(FoundationRole.TENANT_ADMIN));
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedWorkstreamApiWiresCurrentAgentAdminDocEditingActions() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-admin-dashboard")
        .addHeader("X-Selected-Context-Id", OWNER_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Agent Admin surfaces must reject missing bearer tokens.");

    assertThrows(RuntimeException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-admin-dashboard")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-tenant", "tenant-admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", TENANT_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-agent-admin-tenant-denied")
        .responseBodyAs(String.class)
        .invoke(), "Tenant admins must not read Agent Admin doc-editing surfaces.");

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "SaaS Owner"))
        .addHeader("X-Selected-Context-Id", OWNER_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-agent-admin-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-admin-agent") && agent.availability().equals("visible")));

    var blank = getSurface("surface-agent-admin-blank", "corr-agent-admin-blank");
    assertEquals("surface-agent-admin-blank", blank.surfaceId());
    assertEquals("agent_admin.blank.v1", blank.data().get("surfaceContract"));
    assertTrue(blank.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-show-dashboard")));
    assertTrue(blank.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-show-agents")));

    var dashboard = getSurface("surface-agent-admin-dashboard", "corr-agent-admin-dashboard");
    assertEquals("surface-agent-admin-dashboard", dashboard.surfaceId());
    assertEquals("agent_admin.dashboard.v1", dashboard.data().get("surfaceContract"));
    assertTrue(dashboard.toString().contains("thingsYouCanDo"));
    assertFalse(dashboard.toString().contains("prompt-risk"));
    assertFalse(dashboard.toString().contains("tool-boundary"));
    assertFalse(dashboard.toString().contains("model-ref"));
    assertFalse(dashboard.toString().contains("seed-material"));
    assertFalse(dashboard.actions().stream().anyMatch(action -> action.resultSurface().updateSurfaceId().contains("prompt-risk") || action.resultSurface().updateSurfaceId().contains("tool-boundary") || action.resultSurface().updateSurfaceId().contains("model-refs") || action.resultSurface().updateSurfaceId().contains("seed-material")));

    var list = runAction(new CapabilityActionRequest(
        "action-agent-admin-show-agents",
        "action-agent-admin-show-agents",
        "list-agent-doc-agents",
        "agent_admin.list_definitions",
        Map.of("nameContains", "User Admin"),
        null,
        OWNER_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-list"));
    assertEquals("accepted", list.status());
    assertEquals("surface-agent-admin-agent-list", list.resultSurface().surfaceId());
    var rows = (List<Map<String, Object>>) list.resultSurface().data().get("rows");
    assertFalse(rows.isEmpty());
    var agentId = String.valueOf(rows.get(0).get("agentDefinitionId"));

    var detail = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-agent-detail",
        "action-agent-admin-open-agent-detail",
        "read-agent-doc-agent",
        "agent_admin.get_definition",
        Map.of("agentDefinitionId", agentId),
        null,
        OWNER_CONTEXT_ID,
        list.resultSurface().surfaceId(),
        "corr-agent-admin-detail"));
    assertEquals("surface-agent-admin-agent-detail", detail.resultSurface().surfaceId());
    var detailText = String.valueOf(detail.resultSurface().data());
    assertTrue(detailText.contains("generatedIdentityReadOnly=true"));
    assertTrue(detailText.contains("surface-agent-admin-agent-profile-history"));
    assertTrue(detailText.contains("surface-agent-admin-skill-assignment"));
    assertTrue(detailText.contains("surface-agent-admin-tool-assignment"));
    assertTrue(detailText.contains("surface-agent-admin-model-config-ref"));
    assertFalse(detailText.contains("action-agent-admin-save-agent-profile"));
    assertFalse(detailText.contains("action-agent-detail-open-activation"));
    assertFalse(detailText.contains("action-agent-detail-open-deactivation"));
    assertFalse(detailText.contains("action-agent-detail-open-rollback"));
    var profile = (Map<String, Object>) detail.resultSurface().data().get("profile");
    var prompt = (Map<String, Object>) detail.resultSurface().data().get("prompt");
    var promptDocumentId = String.valueOf(prompt.get("documentId"));

    var profileHistory = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-profile-history",
        "action-agent-admin-open-profile-history",
        "read-agent-behavior-profile-history",
        "agent_admin.get_definition",
        Map.of("agentDefinitionId", agentId),
        null,
        OWNER_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-agent-admin-profile-history"));
    assertEquals("surface-agent-admin-agent-profile-history", profileHistory.resultSurface().surfaceId());

    var skillAssignment = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-skill-assignment",
        "action-agent-admin-open-skill-assignment",
        "assign-agent-skills",
        "saas_owner.admin.manage",
        Map.of("agentDefinitionId", agentId),
        null,
        OWNER_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-agent-admin-skill-assignment"));
    assertEquals("surface-agent-admin-skill-assignment", skillAssignment.resultSurface().surfaceId());

    var toolAssignment = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-tool-assignment",
        "action-agent-admin-open-tool-assignment",
        "assign-agent-generated-tools",
        "saas_owner.admin.manage",
        Map.of("agentDefinitionId", agentId),
        null,
        OWNER_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-agent-admin-tool-assignment"));
    assertEquals("surface-agent-admin-tool-assignment", toolAssignment.resultSurface().surfaceId());
    assertTrue(String.valueOf(toolAssignment.resultSurface().data()).contains("noGeneratedToolCodeMutation=true"));

    var modelRef = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-model-config-ref",
        "action-agent-admin-open-model-config-ref",
        "update-agent-model-config-ref",
        "saas_owner.admin.manage",
        Map.of("agentDefinitionId", agentId),
        null,
        OWNER_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-agent-admin-model-ref"));
    assertEquals("surface-agent-admin-model-config-ref", modelRef.resultSurface().surfaceId());
    assertTrue(String.valueOf(modelRef.resultSurface().data()).contains("providerSecretsExposed=false"));

    var modelNoop = runAction(new CapabilityActionRequest(
        "action-agent-admin-update-model-config-ref",
        "action-agent-admin-update-model-config-ref",
        "update-agent-model-config-ref",
        "saas_owner.admin.manage",
        Map.of("agentDefinitionId", agentId, "expectedProfileVersion", Integer.parseInt(String.valueOf(profile.get("profileVersion"))), "modelConfigRefId", String.valueOf(profile.get("modelConfigRefId"))),
        "idem-agent-admin-model-ref-noop",
        OWNER_CONTEXT_ID,
        modelRef.resultSurface().surfaceId(),
        "corr-agent-admin-model-ref-noop"));
    assertEquals("no-op", modelNoop.status());

    assertThrows(RuntimeException.class, () -> runAction(new CapabilityActionRequest(
        "action-agent-admin-save-agent-profile",
        "action-agent-admin-save-agent-profile",
        "update-agent-name-purpose",
        "saas_owner.admin.manage",
        Map.of("agentDefinitionId", agentId, "agentName", "Stale name", "purpose", "Stale purpose"),
        "idem-agent-admin-stale-profile",
        OWNER_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-agent-admin-stale-profile")), "Stale whole-agent profile mutation action must not be exposed or executable as a current product action.");

    var promptDoc = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-prompt-doc",
        "action-agent-admin-open-prompt-doc",
        "read-agent-prompt-doc",
        "agent_admin.get_prompt_version",
        Map.of("agentDefinitionId", agentId, "kind", "PROMPT", "documentId", promptDocumentId),
        null,
        OWNER_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-agent-admin-prompt"));
    assertEquals("surface-agent-admin-prompt-doc", promptDoc.resultSurface().surfaceId());
    assertEquals("agent_admin.prompt_doc.v1", promptDoc.resultSurface().data().get("surfaceContract"));
    assertTrue(String.valueOf(promptDoc.resultSurface().data()).contains("contentBody"));

    var edit = runAction(new CapabilityActionRequest(
        "action-agent-doc-edit-start",
        "action-agent-doc-edit-start",
        "draft-agent-doc-edit",
        "agent_admin.draft_behavior_change",
        Map.of("agentDefinitionId", agentId, "kind", "PROMPT", "documentId", promptDocumentId, "instructions", "Make the prompt more concise."),
        null,
        OWNER_CONTEXT_ID,
        promptDoc.resultSurface().surfaceId(),
        "corr-agent-admin-edit-start"));
    assertEquals("surface-agent-admin-edit-session", edit.resultSurface().surfaceId());
    var session = (Map<String, Object>) edit.resultSurface().data().get("session");
    var sessionId = String.valueOf(session.get("sessionId"));
    var currentDoc = (Map<String, Object>) promptDoc.resultSurface().data().get("doc");
    var proposed = String.valueOf(currentDoc.get("contentBody")) + "\n\nConcise behavior note.";

    var revised = runAction(new CapabilityActionRequest(
        "action-agent-doc-edit-revise",
        "action-agent-doc-edit-revise",
        "revise-agent-doc-edit",
        "agent_admin.draft_behavior_change",
        Map.of("sessionId", sessionId, "instructions", "Keep Markdown headings.", "proposedContent", proposed, "changeSummary", "Added concise behavior note", "warnings", List.of("Advisory only")),
        null,
        OWNER_CONTEXT_ID,
        edit.resultSurface().surfaceId(),
        "corr-agent-admin-edit-revise"));
    assertTrue(String.valueOf(revised.resultSurface().data()).contains("proposal_ready"));

    var saved = runAction(new CapabilityActionRequest(
        "action-agent-doc-edit-save",
        "action-agent-doc-edit-save",
        "save-agent-doc-edit",
        "agent_admin.draft_behavior_change",
        Map.of("sessionId", sessionId),
        null,
        OWNER_CONTEXT_ID,
        revised.resultSurface().surfaceId(),
        "corr-agent-admin-edit-save"));
    assertEquals("accepted", saved.status());
    assertEquals("surface-agent-admin-proposal-review", saved.resultSurface().surfaceId());
    assertTrue(String.valueOf(saved.resultSurface().data()).contains("proposalId"));
    assertTrue(String.valueOf(saved.resultSurface().data()).contains("currentActiveVersion"));
    var proposal = (Map<String, Object>) saved.resultSurface().data().get("proposal");
    var proposalId = String.valueOf(proposal.get("proposalId"));

    var approved = runAction(new CapabilityActionRequest(
        "action-agent-doc-proposal-approve",
        "action-agent-doc-proposal-approve",
        "approve-agent-doc-proposal",
        "agent_admin.approve_behavior_change",
        Map.of("proposalId", proposalId, "rationale", "Low-risk smoke approval"),
        null,
        OWNER_CONTEXT_ID,
        saved.resultSurface().surfaceId(),
        "corr-agent-admin-proposal-approve"));
    assertEquals("accepted", approved.status());
    assertEquals("approved", ((Map<String, Object>) approved.resultSurface().data().get("proposal")).get("status"));

    var activated = runAction(new CapabilityActionRequest(
        "action-agent-doc-proposal-activate",
        "action-agent-doc-proposal-activate",
        "activate-agent-doc-version",
        "agent_admin.activate_behavior_change",
        Map.of("proposalId", proposalId, "confirmation", "ACTIVATE"),
        null,
        OWNER_CONTEXT_ID,
        approved.resultSurface().surfaceId(),
        "corr-agent-admin-proposal-activate"));
    assertEquals("accepted", activated.status());
    assertTrue(String.valueOf(activated.resultSurface().data()).contains("newCurrentVersion"));

    var staleActivation = runAction(new CapabilityActionRequest(
        "action-agent-doc-proposal-activate",
        "action-agent-doc-proposal-activate",
        "activate-agent-doc-version",
        "agent_admin.activate_behavior_change",
        Map.of("proposalId", proposalId, "confirmation", "ACTIVATE"),
        null,
        OWNER_CONTEXT_ID,
        activated.resultSurface().surfaceId(),
        "corr-agent-admin-proposal-stale-activate"));
    assertEquals("stale-conflict", staleActivation.status());
    assertEquals("surface-agent-admin-system-message", staleActivation.resultSurface().surfaceId());

    var history = runAction(new CapabilityActionRequest(
        "action-agent-doc-version-history",
        "action-agent-doc-version-history",
        "read-agent-doc-version-history",
        "agent_admin.get_prompt_version",
        Map.of("agentDefinitionId", agentId, "kind", "PROMPT", "documentId", promptDocumentId, "version", 1),
        null,
        OWNER_CONTEXT_ID,
        saved.resultSurface().surfaceId(),
        "corr-agent-admin-history"));
    assertEquals("surface-agent-admin-version-history", history.resultSurface().surfaceId());

    var diff = runAction(new CapabilityActionRequest(
        "action-agent-doc-version-diff",
        "action-agent-doc-version-diff",
        "read-agent-doc-version-diff",
        "agent_admin.get_prompt_version",
        Map.of("agentDefinitionId", agentId, "kind", "PROMPT", "documentId", promptDocumentId, "version", 1),
        null,
        OWNER_CONTEXT_ID,
        history.resultSurface().surfaceId(),
        "corr-agent-admin-diff"));
    assertEquals("surface-agent-admin-version-diff", diff.resultSurface().surfaceId());
    assertTrue(String.valueOf(diff.resultSurface().data()).contains("version"));

    var traces = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-runtime-traces",
        "action-agent-admin-open-runtime-traces",
        "read-agent-doc-runtime-traces",
        "agent_admin.get_definition",
        Map.of("agentDefinitionId", agentId),
        null,
        OWNER_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-agent-admin-runtime-traces"));
    assertEquals("surface-agent-admin-runtime-traces", traces.resultSurface().surfaceId());
    assertFalse(String.valueOf(traces.resultSurface().data()).contains("# Endpoint Smoke Reference\nDetails."));

    assertBrowserSafe(traces.resultSurface());
  }

  private SurfaceEnvelope getSurface(String surfaceId, String correlationId) throws Exception {
    var response = httpClient
        .GET("/api/workstream/surfaces/" + surfaceId)
        .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "SaaS Owner"))
        .addHeader("X-Selected-Context-Id", OWNER_CONTEXT_ID)
        .addHeader("X-Correlation-Id", correlationId)
        .responseBodyAs(SurfaceEnvelope.class)
        .invoke();
    assertTrue(response.status().isSuccess());
    return response.body();
  }

  private CapabilityActionResult runAction(CapabilityActionRequest request) throws Exception {
    try {
      var response = httpClient
          .POST("/api/workstream/actions")
          .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "SaaS Owner"))
          .addHeader("X-Selected-Context-Id", OWNER_CONTEXT_ID)
          .addHeader("X-Correlation-Id", request.correlationId())
          .withRequestBody(request)
          .responseBodyAs(CapabilityActionResult.class)
          .invoke();
      assertTrue(response.status().isSuccess());
      return response.body();
    } catch (RuntimeException failed) {
      throw new RuntimeException("Action " + request.actionId() + " failed: " + failed.getMessage(), failed);
    }
  }

  private void seedIdentity(AkkaIdentityRepository repository, String email, String displayName, String membershipId, ScopeType scopeType, String tenantId, List<FoundationRole> roles) {
    repository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "UNLINKED"));
    repository.saveProfile(new UserProfile(email, email, displayName, null, null, null));
    repository.saveSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.saveMembership(new Membership(membershipId, email, scopeType, tenantId, null, roles, MembershipStatus.ACTIVE, false, null));
  }

  private String bearerToken(String subject, String email, String name) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(Map.of("sub", subject, "email", email, "name", name)));
    return header + "." + payload;
  }

  private static void assertBrowserSafe(Object payload) {
    var text = String.valueOf(payload);
    assertFalse(text.contains("invite-token"));
    assertFalse(text.contains("tokenHash"));
    assertFalse(text.contains("providerSecret"));
    assertFalse(text.contains("sk_live_"));
    assertFalse(text.contains("sk_test_"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
  }
}
