package ai.first.application.security;

import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.agentfoundation.GovernancePolicyImpactAutonomousAgentRuntime;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.security.GovernancePolicyImpactTask;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GovernancePolicyImpactServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-26T10:15:30Z"), ZoneOffset.UTC);
  private LocalDemoIdentityRepository identityRepository;
  private LocalDemoGovernancePolicyRepository governancePolicyRepository;
  private AuthContextResolver resolver;
  private AuthContextResolver.ResolvedMe tenantAdmin;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    governancePolicyRepository = new LocalDemoGovernancePolicyRepository();
    resolver = new AuthContextResolver(identityRepository);
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    seed("admin@example.test", "membership-admin", FoundationRole.TENANT_ADMIN);
    seed("member@example.test", "membership-member", FoundationRole.TENANT_EMPLOYEE);
    tenantAdmin = resolver.resolveMe(new WorkosIdentity("workos-admin", "admin@example.test", "Tenant Admin"), "membership-admin", "corr-admin");
  }

  @Test
  void startCreatesProviderBlockedImpactTaskWithoutFakePolicyImpactSuccessAndReplaysIdempotently() {
    var proposalId = submittedProposalId("idem-impact-proposal");
    var service = new GovernancePolicyImpactService(new LocalDemoGovernancePolicyImpactTaskRepository(), governancePolicyRepository, resolver, clock);

    var task = service.start(tenantAdmin, command(proposalId, "idem-impact"), "corr-impact-start");

    assertEquals(GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, task.status());
    assertEquals("blocked_provider_or_runtime", task.blockerCode());
    assertTrue(task.evidenceRefs().contains("governancePolicyEvidence.read"));
    assertTrue(task.evidenceRefs().stream().anyMatch(ref -> ref.contains("readSkill")));
    assertTrue(task.summary().contains("fails closed"));
    assertTrue(task.summary().contains("model-less policy impact findings"));
    assertFalse(task.summary().contains("api_key="));
    assertTrue(task.traceIds().stream().anyMatch(trace -> trace.contains("trace-governance-policy-impact-start")));

    var replay = service.start(tenantAdmin, command(proposalId, "idem-impact"), "corr-impact-replay");
    assertEquals(task.impactTaskId(), replay.impactTaskId());
  }

  @Test
  void componentClientBackedRuntimeStartProjectsCompletedReviewRequiredWithoutDirectPolicyMutation() {
    var proposalId = submittedProposalId("idem-impact-runtime-proposal");
    var repository = new LocalDemoGovernancePolicyImpactTaskRepository();
    var runtime = new RecordingGovernancePolicyImpactRuntime();
    var service = new GovernancePolicyImpactService(repository, governancePolicyRepository, resolver, clock, runtime);

    var task = service.start(tenantAdmin, command(proposalId, "idem-impact-runtime"), "corr-impact-runtime-start");

    assertEquals(GovernancePolicyImpactTask.Status.QUEUED, task.status());
    assertEquals("akka-task-" + task.impactTaskId(), task.autonomousAgentTaskId());
    assertEquals(1, runtime.startCount);

    runtime.nextProjection = new GovernancePolicyImpactAutonomousAgentRuntime.Projection(
        GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED,
        100,
        "Akka AutonomousAgent completed model-backed governance policy impact analysis; human approval still required before activation.",
        null,
        null,
        List.of("governancePolicyEvidence.read", "readSkill:governance-policy-impact-analysis"),
        List.of("governance_policy_impact_finding:finding-1:approval-gate"),
        List.of("autonomous_task:" + task.autonomousAgentTaskId(), "trace-model-backed-governance-impact-result"));

    var completed = service.read(tenantAdmin, task.impactTaskId(), "corr-impact-runtime-read");

    assertEquals(GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED, completed.status());
    assertTrue(completed.summary().contains("model-backed governance policy impact"));
    assertTrue(completed.findingRefs().stream().anyMatch(ref -> ref.contains("governance_policy_impact_finding")));
    assertFalse(completed.summary().toLowerCase().contains("secret="));

    var taskSurface = service.taskSurface(tenantAdmin, completed.impactTaskId(), "corr-impact-task-surface");
    assertEquals("surface-governance-policy-impact-analysis-task", taskSurface.surfaceId());
    assertEquals("workflow-status", taskSurface.surfaceType());
    assertEquals("governance.policy.impact_analysis.task.v1", taskSurface.data().get("surfaceContract"));
    assertEquals(true, taskSurface.data().get("noDirectMutation"));

    var resultSurface = service.resultSurface(tenantAdmin, completed.impactTaskId(), "corr-impact-result-surface");
    assertEquals("surface-governance-policy-impact-analysis-result", resultSurface.surfaceId());
    assertEquals("decision", resultSurface.surfaceType());
    assertEquals("governance.policy.impact_analysis.result.v1", resultSurface.data().get("surfaceContract"));
    assertTrue(resultSurface.data().get("requiredHumanDecisions").toString().contains("separately activate"));
    assertTrue(resultSurface.data().get("disabledActions").toString().contains("not executed by this worker"));
    assertFalse(resultSurface.toString().contains("api_key="));

    var accepted = service.acceptResult(tenantAdmin, completed.impactTaskId(), "accepted as advisory evidence", "corr-impact-accept");
    assertEquals(GovernancePolicyImpactTask.Status.ACCEPTED, accepted.status());
    assertTrue(accepted.decisionReason().contains("advisory"));
  }

  @Test
  void cancelRejectAndRequestChangesAreAdvisoryOnlyAndRequireCompletedResult() {
    var proposalId = submittedProposalId("idem-impact-decision-proposal");
    var service = new GovernancePolicyImpactService(new LocalDemoGovernancePolicyImpactTaskRepository(), governancePolicyRepository, resolver, clock);
    var task = service.start(tenantAdmin, command(proposalId, "idem-impact-decision"), "corr-impact-decision-start");

    var accept = assertThrows(AuthorizationException.class, () -> service.acceptResult(tenantAdmin, task.impactTaskId(), "looks good", "corr-impact-accept"));
    assertEquals("governance-impact-result-not-completed", accept.reasonCode());
    var reject = assertThrows(AuthorizationException.class, () -> service.rejectResult(tenantAdmin, task.impactTaskId(), "not enough evidence", "corr-impact-reject"));
    assertEquals("governance-impact-result-not-completed", reject.reasonCode());
    var changes = assertThrows(AuthorizationException.class, () -> service.requestChanges(tenantAdmin, task.impactTaskId(), "add approval-gate evidence", "corr-impact-changes"));
    assertEquals("governance-impact-result-not-completed", changes.reasonCode());

    var cancelled = service.cancel(tenantAdmin, task.impactTaskId(), "not needed", "corr-impact-cancel");
    assertEquals(GovernancePolicyImpactTask.Status.CANCELLED, cancelled.status());
    assertTrue(cancelled.summary().contains("not needed"));
  }

  @Test
  void memberAndCrossTenantInputsAreDeniedBeforeImpactEvidenceLeakage() {
    var proposalId = submittedProposalId("idem-impact-denial-proposal");
    var service = new GovernancePolicyImpactService(new LocalDemoGovernancePolicyImpactTaskRepository(), governancePolicyRepository, resolver, clock);
    var deniedActor = resolver.resolveMe(new WorkosIdentity("workos-member", "member@example.test", "Member"), "membership-member", "corr-member");

    var deniedStart = assertThrows(AuthorizationException.class, () -> service.start(deniedActor, command(proposalId, "idem-impact-denied"), "corr-impact-denied"));
    assertTrue(deniedStart.reasonCode().contains("missing-capability:governance.policy.impact_analysis.start"));

    var task = service.start(tenantAdmin, command(proposalId, "idem-impact-admin"), "corr-impact-admin");
    var deniedRead = assertThrows(AuthorizationException.class, () -> service.read(deniedActor, task.impactTaskId(), "corr-impact-denied-read"));
    assertTrue(deniedRead.reasonCode().contains("missing-capability:governance.policy.impact_analysis.read"));
  }

  private String submittedProposalId(String idempotencyKey) {
    var governance = new GovernancePolicyService(governancePolicyRepository, resolver, clock);
    var draft = governance.draftProposal(tenantAdmin, Map.of("rationale", "tighten approval copy", "proposedContent", "change ToolPermissionBoundary approval gate"), idempotencyKey, idempotencyKey + "-draft");
    var proposalId = draft.surface().data().get("proposalId").toString();
    governance.submitProposal(tenantAdmin, Map.of("proposalId", proposalId), idempotencyKey + "-submit", idempotencyKey + "-submit-corr");
    return proposalId;
  }

  private GovernancePolicyImpactService.StartGovernancePolicyImpactCommand command(String proposalId, String idempotencyKey) {
    return new GovernancePolicyImpactService.StartGovernancePolicyImpactCommand(
        proposalId,
        "policy-human-approval",
        "proposalId=" + proposalId + " focus=approval gates and ToolPermissionBoundary redaction",
        List.of("governance.policy.approve", "governance.policy.activate"),
        List.of("ToolPermissionBoundary", "AgentDefinition"),
        List.of("proposal-evidence:" + proposalId),
        idempotencyKey);
  }

  private static final class RecordingGovernancePolicyImpactRuntime implements GovernancePolicyImpactAutonomousAgentRuntime {
    private int startCount;
    private Projection nextProjection = Projection.unchanged();

    @Override
    public StartOutcome start(AuthContextResolver.ResolvedMe actor, GovernancePolicyImpactTask starterTask, String evidenceRequest, String correlationId) {
      startCount++;
      return StartOutcome.queued(
          "akka-task-" + starterTask.impactTaskId(),
          "Akka AutonomousAgent task accepted; no deterministic/model-less governance policy impact success is returned by start.",
          List.of("autonomous_task:akka-task-" + starterTask.impactTaskId(), "trace-test-autonomous-governance-impact-start"));
    }

    @Override
    public Projection project(GovernancePolicyImpactTask starterTask, String correlationId) {
      return nextProjection;
    }
  }

  private void seed(String email, String membershipId, FoundationRole role) {
    identityRepository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, "tenant-1", null, List.of(role), MembershipStatus.ACTIVE, false, null));
  }
}
