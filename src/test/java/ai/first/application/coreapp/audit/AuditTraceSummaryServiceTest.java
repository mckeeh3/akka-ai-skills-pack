package ai.first.application.coreapp.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.coreapp.audit.AuditTraceSummaryAutonomousAgentRuntime;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.coreapp.audit.AuditTraceSummaryTask;
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
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.LocalDemoIdentityRepository;

class AuditTraceSummaryServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-25T10:15:30Z"), ZoneOffset.UTC);
  private LocalDemoIdentityRepository identityRepository;
  private AuthContextResolver resolver;
  private AuthContextResolver.ResolvedMe auditor;
  private AuditTraceSummaryService service;

  @BeforeEach
  void setUp() {
    identityRepository = new LocalDemoIdentityRepository();
    resolver = new AuthContextResolver(identityRepository);
    service = new AuditTraceSummaryService(new LocalDemoAuditTraceSummaryTaskRepository(), resolver, clock);
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    seed("auditor@example.test", "membership-auditor", FoundationRole.AUDITOR);
    seed("member@example.test", "membership-member", FoundationRole.TENANT_EMPLOYEE);
    auditor = resolver.resolveMe(new WorkosIdentity("workos-auditor", "auditor@example.test", "Auditor"), null, "corr-auditor");
  }

  @Test
  void startCreatesDurableProviderBlockedAuditSummaryTaskAndReplaysIdempotently() {
    var task = service.start(auditor, command("idem-audit-summary"), "corr-audit-summary");

    assertEquals(AuditTraceSummaryTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, task.status());
    assertEquals("blocked_provider_or_runtime", task.blockerCode());
    assertTrue(task.evidenceRefs().contains("auditTraceSummaryEvidence.read"));
    assertTrue(task.evidenceRefs().stream().anyMatch(ref -> ref.contains("readSkill")));
    assertTrue(task.summary().contains("fails closed"));
    assertTrue(task.summary().contains("model-less audit summary success"));
    assertFalse(task.summary().toLowerCase().contains("api_key"));

    var replay = service.start(auditor, command("idem-audit-summary"), "corr-audit-summary-replay");
    assertEquals(task.taskId(), replay.taskId());
  }

  @Test
  void componentClientBackedRuntimeStartIsQueuedAndIdempotentUntilProjectionCompletes() {
    var repository = new LocalDemoAuditTraceSummaryTaskRepository();
    var runtime = new RecordingAuditTraceSummaryAutonomousAgentRuntime();
    var summaries = new AuditTraceSummaryService(repository, resolver, clock, runtime);

    var task = summaries.start(auditor, command("idem-autonomous-audit-summary"), "corr-autonomous-start");

    assertEquals(AuditTraceSummaryTask.Status.QUEUED, task.status());
    assertEquals("akka-task-" + task.taskId(), task.autonomousAgentTaskId());
    assertTrue(task.traceIds().stream().anyMatch(trace -> trace.startsWith("autonomous_task:")));
    assertEquals(1, runtime.startCount);

    var replay = summaries.start(auditor, command("idem-autonomous-audit-summary"), "corr-autonomous-replay");
    assertEquals(task.taskId(), replay.taskId());
    assertEquals(1, runtime.startCount, "idempotent replay must not start a second Akka AutonomousAgent task");

    runtime.nextProjection = new AuditTraceSummaryAutonomousAgentRuntime.Projection(
        AuditTraceSummaryTask.Status.COMPLETED_REVIEW_REQUIRED,
        100,
        "Akka AutonomousAgent completed model-backed redacted audit summary; human Audit/Trace review required.",
        null,
        null,
        List.of("auditTraceSummaryEvidence.read", "readSkill:audit-trace-summary-review"),
        List.of("audit_trace_summary_finding:finding-1:provider_readiness"),
        List.of("autonomous_task:" + task.autonomousAgentTaskId(), "trace-model-backed-audit-summary-result"));

    var completed = summaries.read(auditor, task.taskId(), "corr-autonomous-read");

    assertEquals(AuditTraceSummaryTask.Status.COMPLETED_REVIEW_REQUIRED, completed.status());
    assertTrue(completed.summary().contains("model-backed redacted audit summary"));
    assertTrue(completed.findingRefs().stream().anyMatch(ref -> ref.contains("audit_trace_summary_finding")));
    assertFalse(completed.summary().toLowerCase().contains("api_key"));

    var review = summaries.review(auditor, task.taskId(), "corr-autonomous-review");
    assertEquals(AuditTraceSummaryTask.Status.COMPLETED_REVIEW_REQUIRED, review.status());

    var accepted = summaries.acceptResult(auditor, task.taskId(), "advisory summary retained as review evidence only", "corr-autonomous-accept");
    assertEquals(AuditTraceSummaryTask.Status.ACCEPTED, accepted.status());
    assertEquals("accepted", accepted.decision());
    assertTrue(accepted.decisionReason().contains("review evidence"));
    assertTrue(summaries.acceptResult(auditor, task.taskId(), "idempotent replay", "corr-autonomous-accept-replay").traceIds().containsAll(accepted.traceIds()));
  }

  @Test
  void cancelAcceptRejectAreAdvisoryOnlyAndRequireCompletedResult() {
    var task = service.start(auditor, command("idem-decision"), "corr-decision-start");

    var accept = assertThrows(AuthorizationException.class, () -> service.acceptResult(auditor, task.taskId(), "looks good", "corr-accept"));
    assertEquals("audit-summary-result-not-completed", accept.reasonCode());
    var reject = assertThrows(AuthorizationException.class, () -> service.rejectResult(auditor, task.taskId(), "not enough evidence", "corr-reject"));
    assertEquals("audit-summary-result-not-completed", reject.reasonCode());

    var cancelled = service.cancel(auditor, task.taskId(), "not needed", "corr-cancel");
    assertEquals(AuditTraceSummaryTask.Status.CANCELLED, cancelled.status());
    assertTrue(cancelled.summary().contains("not needed"));
  }

  @Test
  void memberWithoutAuditSummaryAuthorityCannotStartOrRead() {
    var deniedActor = resolver.resolveMe(new WorkosIdentity("workos-member", "member@example.test", "Member"), "membership-member", "corr-member");

    var deniedStart = assertThrows(AuthorizationException.class, () -> service.start(deniedActor, command("idem-denied"), "corr-denied"));
    assertTrue(deniedStart.reasonCode().contains("missing-capability:audit.trace.summary_task.start"));

    var task = service.start(auditor, command("idem-admin"), "corr-admin-start");
    var deniedRead = assertThrows(AuthorizationException.class, () -> service.read(deniedActor, task.taskId(), "corr-denied-read"));
    assertTrue(deniedRead.reasonCode().contains("missing-capability:audit.trace.summary_task.read"));
  }

  private AuditTraceSummaryService.StartAuditTraceSummaryCommand command(String idempotencyKey) {
    return new AuditTraceSummaryService.StartAuditTraceSummaryCommand(
        Instant.parse("2026-05-20T00:00:00Z"),
        Instant.parse("2026-05-25T00:00:00Z"),
        List.of("admin_audit", "provider_readiness", "agent_work"),
        idempotencyKey);
  }

  private static final class RecordingAuditTraceSummaryAutonomousAgentRuntime implements AuditTraceSummaryAutonomousAgentRuntime {
    private int startCount;
    private Projection nextProjection = Projection.unchanged();

    @Override
    public StartOutcome start(AuthContextResolver.ResolvedMe actor, AuditTraceSummaryTask starterTask, String correlationId) {
      startCount++;
      return StartOutcome.queued(
          "akka-task-" + starterTask.taskId(),
          "Akka AutonomousAgent task accepted; no deterministic/model-less audit summary success is returned by start.",
          List.of("autonomous_task:akka-task-" + starterTask.taskId(), "trace-test-autonomous-audit-summary-start"));
    }

    @Override
    public Projection project(AuditTraceSummaryTask starterTask, String correlationId) {
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
