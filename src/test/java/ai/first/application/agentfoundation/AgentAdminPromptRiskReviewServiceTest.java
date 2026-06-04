package ai.first.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.LocalDemoIdentityRepository;
import ai.first.domain.agentfoundation.PromptRiskReviewTask;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.FoundationRole;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;

class AgentAdminPromptRiskReviewServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-25T10:15:30Z"), ZoneOffset.UTC);
  private LocalDemoIdentityRepository identityRepository;
  private AuthContextResolver resolver;
  private AuthContextResolver.ResolvedMe tenantAdmin;
  private AgentAdminPromptRiskReviewService service;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    resolver = new AuthContextResolver(identityRepository);
    service = new AgentAdminPromptRiskReviewService(new LocalDemoPromptRiskReviewTaskRepository(), resolver, clock);
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    seed("admin@example.test", "membership-admin", FoundationRole.TENANT_ADMIN);
    seed("member@example.test", "membership-member", FoundationRole.TENANT_EMPLOYEE);
    tenantAdmin = resolver.resolveMe(new WorkosIdentity("workos-admin", "admin@example.test", "Tenant Admin"), null, "corr-admin");
  }

  @Test
  void startCreatesDurableProviderBlockedPromptRiskTaskAndReplaysIdempotently() {
    var task = service.start(tenantAdmin, command("idem-prompt-risk"), "corr-prompt-risk");

    assertEquals(PromptRiskReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, task.status());
    assertEquals("blocked_provider_or_runtime", task.blockerCode());
    assertTrue(task.evidenceRefs().contains("agentAdminEvidence.read"));
    assertTrue(task.evidenceRefs().stream().anyMatch(ref -> ref.contains("readSkill")));
    assertTrue(task.summary().contains("fails closed"));
    assertTrue(task.summary().contains("model-less prompt-risk findings"));
    assertTrue(task.traceIds().get(0).contains("trace-agent-admin-prompt-risk-start"));

    var replay = service.start(tenantAdmin, command("idem-prompt-risk"), "corr-prompt-risk-replay");
    assertEquals(task.taskId(), replay.taskId());
  }

  @Test
  void componentClientBackedRuntimeStartIsQueuedAndIdempotentUntilProjectionCompletes() {
    var repository = new LocalDemoPromptRiskReviewTaskRepository();
    var runtime = new RecordingPromptRiskAutonomousAgentRuntime();
    var promptRisk = new AgentAdminPromptRiskReviewService(repository, resolver, clock, runtime);

    var task = promptRisk.start(tenantAdmin, command("idem-autonomous-prompt-risk"), "corr-autonomous-start");

    assertEquals(PromptRiskReviewTask.Status.QUEUED, task.status());
    assertEquals("akka-task-" + task.taskId(), task.autonomousAgentTaskId());
    assertTrue(task.traceIds().stream().anyMatch(trace -> trace.startsWith("autonomous_task:")));
    assertEquals(1, runtime.startCount);

    var replay = promptRisk.start(tenantAdmin, command("idem-autonomous-prompt-risk"), "corr-autonomous-replay");
    assertEquals(task.taskId(), replay.taskId());
    assertEquals(1, runtime.startCount, "idempotent replay must not start a second Akka AutonomousAgent task");

    runtime.nextProjection = new PromptRiskAutonomousAgentRuntime.Projection(
        PromptRiskReviewTask.Status.COMPLETED_REVIEW_REQUIRED,
        100,
        "Akka AutonomousAgent completed model-backed advisory prompt-risk review; human Agent Admin review required.",
        null,
        null,
        List.of("agentAdminEvidence.read", "readSkill:agent-admin-prompt-risk-review"),
        List.of("prompt_risk_finding:finding-1:prompt instruction hierarchy conflict"),
        List.of("autonomous_task:" + task.autonomousAgentTaskId(), "trace-model-backed-prompt-risk-result"));

    var completed = promptRisk.read(tenantAdmin, task.taskId(), "corr-autonomous-read");

    assertEquals(PromptRiskReviewTask.Status.COMPLETED_REVIEW_REQUIRED, completed.status());
    assertTrue(completed.summary().contains("model-backed advisory prompt-risk"));
    assertTrue(completed.findingRefs().stream().anyMatch(ref -> ref.contains("prompt_risk_finding")));
    assertFalse(completed.summary().toLowerCase().contains("api_key"));
  }

  @Test
  void cancelAcceptRejectAreAdvisoryOnlyAndRequireCompletedResult() {
    var task = service.start(tenantAdmin, command("idem-decision"), "corr-decision-start");

    var accept = assertThrows(AuthorizationException.class, () -> service.acceptResult(tenantAdmin, task.taskId(), "looks good", "corr-accept"));
    assertEquals("prompt-risk-result-not-completed", accept.reasonCode());
    var reject = assertThrows(AuthorizationException.class, () -> service.rejectResult(tenantAdmin, task.taskId(), "not enough evidence", "corr-reject"));
    assertEquals("prompt-risk-result-not-completed", reject.reasonCode());

    var cancelled = service.cancel(tenantAdmin, task.taskId(), "not needed", "corr-cancel");
    assertEquals(PromptRiskReviewTask.Status.CANCELLED, cancelled.status());
    assertTrue(cancelled.summary().contains("not needed"));
  }

  @Test
  void memberWithoutAgentAdminAuthorityCannotStartOrReadPromptRiskReview() {
    var deniedActor = resolver.resolveMe(new WorkosIdentity("workos-member", "member@example.test", "Member"), "membership-member", "corr-member");

    var deniedStart = assertThrows(AuthorizationException.class, () -> service.start(deniedActor, command("idem-denied"), "corr-denied"));
    assertTrue(deniedStart.reasonCode().contains("missing-capability:agent_admin.prompt_risk_review.start"));

    var task = service.start(tenantAdmin, command("idem-admin"), "corr-admin-start");
    var deniedRead = assertThrows(AuthorizationException.class, () -> service.read(deniedActor, task.taskId(), "corr-denied-read"));
    assertTrue(deniedRead.reasonCode().contains("missing-capability:agent_admin.prompt_risk_review.read"));
  }

  private AgentAdminPromptRiskReviewService.StartPromptRiskReviewCommand command(String idempotencyKey) {
    return new AgentAdminPromptRiskReviewService.StartPromptRiskReviewCommand(
        AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        "proposal-1",
        List.of(new PromptRiskReviewTask.BehaviorArtifactDelta(PromptRiskReviewTask.ArtifactKind.PROMPT_DOCUMENT, "agent-admin-system", 1, 2, "tighten governance prompt", "diff:proposal-1", "before", "after")),
        List.of("proposal-evidence:proposal-1"),
        idempotencyKey);
  }

  private static final class RecordingPromptRiskAutonomousAgentRuntime implements PromptRiskAutonomousAgentRuntime {
    private int startCount;
    private Projection nextProjection = Projection.unchanged();

    @Override
    public StartOutcome start(AuthContextResolver.ResolvedMe actor, PromptRiskReviewTask starterTask, String correlationId) {
      startCount++;
      return StartOutcome.queued(
          "akka-task-" + starterTask.taskId(),
          "Akka AutonomousAgent task accepted; no deterministic/model-less prompt-risk success is returned by start.",
          List.of("autonomous_task:akka-task-" + starterTask.taskId(), "trace-test-autonomous-prompt-risk-start"));
    }

    @Override
    public Projection project(PromptRiskReviewTask starterTask, String correlationId) {
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
