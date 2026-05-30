package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import {{JAVA_BASE_PACKAGE}}.application.security.AuthContextResolver;
import {{JAVA_BASE_PACKAGE}}.application.security.BootstrapAdminSeeder;
import {{JAVA_BASE_PACKAGE}}.application.security.LocalDemoAccessReviewTaskRepository;
import {{JAVA_BASE_PACKAGE}}.application.security.LocalDemoIdentityRepository;
import {{JAVA_BASE_PACKAGE}}.application.security.StarterSecurityComponents;
import {{JAVA_BASE_PACKAGE}}.application.security.UserAdminAccessReviewService;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace;
import {{JAVA_BASE_PACKAGE}}.domain.security.AccessReviewTask;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class UserAdminAccessReviewWorkerTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-24T12:00:00Z"), ZoneOffset.UTC);

  @Test
  void completesAccessReviewWithModelBackedGovernedRuntimeAndNoDirectMutation() {
    var runtime = runtimeService(request -> new ModelProviderClient.ModelProviderResponse(
        "## Access review recommendation\n\nNo stale admin memberships found. no direct mutation; human must accept or reject.",
        request.providerAlias(),
        "test-model",
        "provider-response-1",
        "stop",
        "test provider produced model-backed access review"));
    var worker = new UserAdminAccessReviewWorker(runtime, new AgentRuntimeToolResolver(StarterSecurityComponents.agentBehaviorRepository(), runtime), request -> new ModelProviderClient.ModelProviderResponse(
        "## Access review recommendation\n\nNo stale admin memberships found. no direct mutation; human must accept or reject.",
        request.providerAlias(),
        "test-model",
        "provider-response-1",
        "stop",
        "test provider produced model-backed access review"));
    var actor = starterTenantAdmin("corr-worker-actor");
    var accessReviews = new UserAdminAccessReviewService(new LocalDemoAccessReviewTaskRepository(), StarterSecurityComponents.userAdminService(), clock);
    var beforeRoles = StarterSecurityComponents.identityRepository().findMembership(actor.selectedContext().membershipId()).orElseThrow().roles();
    var task = accessReviews.start(actor, "idem-worker-success", "corr-worker-start");

    var workerResult = worker.execute(actor, task, "corr-worker-success");
    var completed = accessReviews.recordWorkerResult(actor, task.taskId(), workerResult, "corr-worker-success");

    assertEquals(AccessReviewTask.Status.COMPLETED, completed.status());
    assertEquals(100, completed.progressPercent());
    assertTrue(completed.evidenceRefs().contains("userAdminEvidence.read"));
    assertTrue(completed.evidenceRefs().stream().anyMatch(ref -> ref.contains("readSkill:ua.access-review-triage.v1")));
    assertTrue(completed.recommendationRefs().stream().anyMatch(ref -> ref.contains("model-backed-recommendation")));
    assertTrue(completed.summary().contains("Model-backed access-review investigation completed"));
    assertEquals(beforeRoles, StarterSecurityComponents.identityRepository().findMembership(actor.selectedContext().membershipId()).orElseThrow().roles(), "Worker output must not directly mutate access state.");
    assertTrue(runtime.traces().stream().anyMatch(trace -> trace.traceType().equals("SKILL_LOAD") && trace.targetId().equals("ua.access-review-triage.v1") && trace.decision() == AgentRuntimeTrace.Decision.ALLOWED));
    assertTrue(runtime.traces().stream().anyMatch(trace -> trace.traceType().equals("REFERENCE_LOAD") && trace.targetId().equals("ua.access-review-policy.v1") && trace.decision() == AgentRuntimeTrace.Decision.ALLOWED));
    assertTrue(runtime.traces().stream().anyMatch(trace -> trace.traceType().equals("AgentWorkTrace") && trace.decision() == AgentRuntimeTrace.Decision.ALLOWED));
    assertFalse(completed.toString().toLowerCase().contains("api_key"));
  }

  @Test
  void providerFailureFailsClosedAsBlockedAccessReviewTaskWithTraceLinks() {
    var failingProvider = (ModelProviderClient) request -> {
      throw new ModelProviderClient.ModelProviderException("model-provider-config-missing", "Model provider configuration is missing required backend variable OPENAI_API_KEY.");
    };
    var runtime = runtimeService(failingProvider);
    var worker = new UserAdminAccessReviewWorker(runtime, new AgentRuntimeToolResolver(StarterSecurityComponents.agentBehaviorRepository(), runtime), failingProvider);
    var actor = starterTenantAdmin("corr-worker-blocked-actor");
    var accessReviews = new UserAdminAccessReviewService(new LocalDemoAccessReviewTaskRepository(), StarterSecurityComponents.userAdminService(), clock);
    var beforeRoles = StarterSecurityComponents.identityRepository().findMembership(actor.selectedContext().membershipId()).orElseThrow().roles();
    var task = accessReviews.start(actor, "idem-worker-blocked", "corr-worker-blocked-start");

    var workerResult = worker.execute(actor, task, "corr-worker-blocked");
    var blocked = accessReviews.recordWorkerResult(actor, task.taskId(), workerResult, "corr-worker-blocked");

    assertEquals(AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, blocked.status());
    assertEquals("model-provider-config-missing", blocked.blockerCode());
    assertTrue(blocked.summary().contains("failed closed"));
    assertTrue(blocked.traceIds().stream().anyMatch(trace -> trace != null && !trace.isBlank()));
    assertEquals(beforeRoles, StarterSecurityComponents.identityRepository().findMembership(actor.selectedContext().membershipId()).orElseThrow().roles());
    assertFalse(blocked.recommendationRefs().stream().anyMatch(ref -> ref.contains("model-backed-recommendation")));
  }

  private AgentRuntimeService runtimeService(ModelProviderClient provider) {
    return new AgentRuntimeService(
        StarterSecurityComponents.agentBehaviorRepository(),
        StarterSecurityComponents.authContextResolver(),
        clock,
        provider);
  }

  private AuthContextResolver.ResolvedMe starterTenantAdmin(String correlationId) {
    var identityRepository = new LocalDemoIdentityRepository();
    StarterSecurityComponents.bindTestIdentityRepository(identityRepository);
    BootstrapAdminSeeder.seedConfiguredAdmins(identityRepository, "admin@example.test:TENANT_ADMIN:tenant-starter");
    seedTenantMember(identityRepository);
    StarterSecurityComponents.agentBehaviorSeedLoader().importStarterDefaults("tenant-starter", "test-bootstrap", correlationId + "-seed");
    return StarterSecurityComponents.authContextResolver().resolveMe(
        new WorkosIdentity("workos-admin", "admin@example.test", "Admin"),
        "membership-admin@example.test",
        correlationId);
  }

  private void seedTenantMember(LocalDemoIdentityRepository identityRepository) {
    identityRepository.saveAccount(new {{JAVA_BASE_PACKAGE}}.domain.security.Account("member@example.test", null, "member@example.test", "member@example.test", {{JAVA_BASE_PACKAGE}}.domain.security.AccountStatus.ACTIVE, "UNLINKED"));
    identityRepository.saveProfile(new {{JAVA_BASE_PACKAGE}}.domain.security.UserProfile("member@example.test", "member@example.test", "member", null, null, null));
    identityRepository.saveSettings(new {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings("member@example.test", {{JAVA_BASE_PACKAGE}}.domain.security.UserSettings.UiMode.LIGHT));
    identityRepository.saveMembership(new {{JAVA_BASE_PACKAGE}}.domain.security.Membership("membership-member@example.test", "member@example.test", {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType.TENANT, "tenant-starter", null, java.util.List.of({{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole.TENANT_EMPLOYEE), {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus.ACTIVE, false, null));
  }
}
