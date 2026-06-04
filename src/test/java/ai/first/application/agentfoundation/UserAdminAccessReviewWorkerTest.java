package ai.first.application.agentfoundation;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.security.AuthContextResolver;
import ai.first.application.security.BootstrapAdminSeeder;
import ai.first.application.security.LocalDemoAccessReviewTaskRepository;
import ai.first.application.security.LocalDemoIdentityRepository;
import ai.first.application.security.StarterSecurityComponents;
import ai.first.application.security.UserAdminAccessReviewService;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import ai.first.domain.security.AccessReviewTask;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class UserAdminAccessReviewWorkerTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-24T12:00:00Z"), ZoneOffset.UTC);

  @Test
  void completesAccessReviewWithModelBackedGovernedRuntimeAndNoDirectMutation() {
    StarterSecurityComponents.bindTestAgentBehaviorRepository(new LocalDemoAgentBehaviorRepository());
    var actor = starterTenantAdmin("corr-worker-actor");
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
    StarterSecurityComponents.bindTestAgentBehaviorRepository(new LocalDemoAgentBehaviorRepository());
    var actor = starterTenantAdmin("corr-worker-blocked-actor");
    var runtime = runtimeService(failingProvider);
    var worker = new UserAdminAccessReviewWorker(runtime, new AgentRuntimeToolResolver(StarterSecurityComponents.agentBehaviorRepository(), runtime), failingProvider);
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
        provider,
        new LocalDemoAgentRuntimeTraceSink());
  }

  private AuthContextResolver.ResolvedMe starterTenantAdmin(String correlationId) {
    var identityRepository = new LocalDemoIdentityRepository();
    StarterSecurityComponents.bindTestIdentityRepository(identityRepository);
    StarterSecurityComponents.bindTestInvitationRepository(new ai.first.application.security.LocalDemoInvitationRepository());
    BootstrapAdminSeeder.seedConfiguredAdmins(identityRepository, "admin@example.test:TENANT_ADMIN:tenant-starter");
    seedTenantMember(identityRepository);
    StarterSecurityComponents.agentBehaviorSeedLoader().importStarterDefaults("tenant-starter", "test-bootstrap", correlationId + "-seed");
    return StarterSecurityComponents.authContextResolver().resolveMe(
        new WorkosIdentity("workos-admin", "admin@example.test", "Admin"),
        "membership-admin@example.test",
        correlationId);
  }

  private void seedTenantMember(LocalDemoIdentityRepository identityRepository) {
    identityRepository.saveAccount(new ai.first.domain.foundation.identity.Account("member@example.test", null, "member@example.test", "member@example.test", ai.first.domain.foundation.identity.AccountStatus.ACTIVE, "UNLINKED"));
    identityRepository.saveProfile(new ai.first.domain.foundation.identity.UserProfile("member@example.test", "member@example.test", "member", null, null, null));
    identityRepository.saveSettings(new ai.first.domain.foundation.identity.UserSettings("member@example.test", ai.first.domain.foundation.identity.UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.saveMembership(new ai.first.domain.foundation.identity.Membership("membership-member@example.test", "member@example.test", ai.first.domain.foundation.identity.ScopeType.TENANT, "tenant-starter", null, java.util.List.of(ai.first.domain.foundation.identity.FoundationRole.TENANT_EMPLOYEE), ai.first.domain.foundation.identity.MembershipStatus.ACTIVE, false, null));
  }
}
