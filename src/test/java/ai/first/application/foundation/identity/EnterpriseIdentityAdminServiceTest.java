package ai.first.application.foundation.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScimProvisioningRequest;
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

class EnterpriseIdentityAdminServiceTest {
  private final Clock clock = Clock.fixed(Instant.parse("2026-05-20T10:15:30Z"), ZoneOffset.UTC);
  private InMemoryTestIdentityRepository identityRepository;
  private AuthContextResolver.ResolvedMe tenantAdmin;

  @BeforeEach
  void setUp() {
    identityRepository = new InMemoryTestIdentityRepository();
    identityRepository.putTenant(new Tenant("tenant-1", "Tenant One", true));
    seedAdmin("admin@example.com", "membership-admin", FoundationRole.TENANT_ADMIN);
    tenantAdmin = new AuthContextResolver(identityRepository).resolveMe(new WorkosIdentity("workos-admin@example.com", "admin@example.com", "Admin"), null, "corr-admin");
  }

  @Test
  void enterpriseIdentityStatusFailsClosedWithoutProviderSecretsAndAuditsRead() {
    var service = new EnterpriseIdentityAdminService(identityRepository, clock, Map.of());

    var status = service.status(tenantAdmin, "corr-iam-status");

    assertTrue(status.workosAuthKitBoundaryPreserved());
    assertTrue(status.scimFoundationEnabled());
    assertFalse(status.productionScimConfigured());
    assertEquals("fail-closed-until-workos-provider-secrets-are-configured", status.productionReadiness());
    assertTrue(status.requiredSecretNames().contains("WORKOS_API_KEY"));
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("IAM_STATUS_READ")));
  }

  @Test
  void scimStyleProvisioningValidationPreservesLocalAuthorizationAndProviderFailClosedSemantics() {
    var service = new EnterpriseIdentityAdminService(identityRepository, clock, Map.of());

    var result = service.validateScimOperation(tenantAdmin, new ScimProvisioningRequest(
        "create_user",
        "external-user-123456789",
        "new.user@example.com",
        "New User",
        ScopeType.TENANT,
        "tenant-1",
        null,
        List.of(FoundationRole.TENANT_EMPLOYEE),
        "directory sync",
        "scim-idem-1"), "corr-scim");

    assertEquals("validated-local-only-fail-closed", result.status());
    assertEquals("new.user@example.com", result.accountId());
    assertEquals("exte…6789", result.externalId());
    assertTrue(result.wouldMutateLocalAuthorization());
    assertFalse(result.productionProviderConfigured());
    assertEquals("fail-closed-provider-config-missing", result.providerDeliveryState());
    assertTrue(identityRepository.auditEvents().stream().anyMatch(event -> event.actionType().equals("SCIM_OPERATION_VALIDATE")));
  }

  @Test
  void scimValidationRejectsCrossTenantAndRoleEscalation() {
    var service = new EnterpriseIdentityAdminService(identityRepository, clock, Map.of());

    assertThrows(AuthorizationException.class, () -> service.validateScimOperation(tenantAdmin, new ScimProvisioningRequest(
        "create_user", "external", "other@example.com", "Other", ScopeType.TENANT, "tenant-2", null, List.of(FoundationRole.TENANT_EMPLOYEE), "cross", "scim-cross"), "corr-cross"));
    var roleError = assertThrows(AuthorizationException.class, () -> service.validateScimOperation(tenantAdmin, new ScimProvisioningRequest(
        "create_user", "external", "owner@example.com", "Owner", ScopeType.TENANT, "tenant-1", null, List.of(FoundationRole.SAAS_OWNER_ADMIN), "escalate", "scim-role"), "corr-role"));
    assertEquals("role-escalation-denied", roleError.reasonCode());
  }

  @Test
  void ssoValidationBlocksProductionActivationWithoutSecretsAndRejectsSecretBearingMetadataUrls() {
    var service = new EnterpriseIdentityAdminService(identityRepository, clock, Map.of());

    var validation = service.validateSsoConfiguration(tenantAdmin, "Example.COM", "https://idp.example.com", "https://idp.example.com/metadata.xml", true, "corr-sso");

    assertEquals("production-fail-closed", validation.status());
    assertEquals("example.com", validation.domain());
    assertTrue(validation.missingSecretNames().contains("WORKOS_CLIENT_ID"));
    assertFalse(validation.productionProviderConfigured());
    assertThrows(AuthorizationException.class, () -> service.validateSsoConfiguration(tenantAdmin, "example.com", "https://idp.example.com", "https://idp.example.com/metadata.xml?token=secret", false, "corr-sso-secret"));
  }

  private void seedAdmin(String email, String membershipId, FoundationRole role) {
    identityRepository.saveAccount(new Account(email, "workos-" + email, email, email, AccountStatus.ACTIVE, "LINKED"));
    identityRepository.putProfile(new UserProfile(email, email, email, null, null, null));
    identityRepository.putSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    identityRepository.putMembership(new Membership(membershipId, email, ScopeType.TENANT, "tenant-1", null, List.of(role), MembershipStatus.ACTIVE, false, null));
  }
}
