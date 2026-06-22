package ai.first.application.coreapp.agentadmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.InMemoryTestAgentBehaviorRepository;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.application.foundation.identity.InMemoryTestIdentityRepository;
import ai.first.application.foundation.workstream.WorkstreamEventPublisher;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Clock;
import java.util.List;
import org.junit.jupiter.api.Test;

class AgentAdminServicePlatformScopeTest {
  @Test
  void saasOwnerReadsPlatformScopedAgentCatalogWithoutTenantContext() {
    var identityRepository = new InMemoryTestIdentityRepository();
    seedOwner(identityRepository);
    var resolver = new AuthContextResolver(identityRepository);
    var agentRepository = new InMemoryTestAgentBehaviorRepository();
    new AgentBehaviorSeedLoader(agentRepository, Clock.systemUTC()).importStarterDefaults(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID, "owner@example.test", "corr-platform-seed");
    var service = new AgentAdminService(agentRepository, resolver);
    var owner = resolver.resolveMe(new WorkosIdentity("workos-owner", "owner@example.test", "SaaS Owner"), "membership-owner", "corr-owner");

    var catalog = service.catalog(owner, "corr-owner-catalog");

    assertEquals("agent_admin.catalog.v1", catalog.get("surfaceContract"));
    assertFalse(((List<?>) catalog.get("rows")).isEmpty());
    assertTrue(catalog.toString().contains("scopeType=saas_owner"));
    assertTrue(catalog.toString().contains(WorkstreamEventPublisher.PLATFORM_SCOPE_TENANT_ID));
  }

  private static void seedOwner(InMemoryTestIdentityRepository repository) {
    repository.saveAccount(new Account("owner@example.test", null, "owner@example.test", "owner@example.test", AccountStatus.ACTIVE, "LINKED"));
    repository.putProfile(new UserProfile("owner@example.test", "owner@example.test", "SaaS Owner", "SaaS", "Owner", null));
    repository.putSettings(new UserSettings("owner@example.test", UserSettings.ThemeId.AURORA_LIGHT));
    repository.putMembership(new Membership("membership-owner", "owner@example.test", ScopeType.SAAS_OWNER, null, null, List.of(FoundationRole.SAAS_OWNER_ADMIN), MembershipStatus.ACTIVE, false, null));
  }
}
