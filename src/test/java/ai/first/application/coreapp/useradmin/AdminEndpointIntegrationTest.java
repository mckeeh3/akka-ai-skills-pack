package ai.first.application.coreapp.useradmin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import ai.first.api.coreapp.admin.AdminEndpoint.AccessReviewApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.AccessReviewApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.AccountActionApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.AccountActionApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.AdminAuditEventsResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.AdminUsersResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.ChangeMembershipStatusApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.ChangeRolesApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.CreateInvitationApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.CustomerActionApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.CustomerAdminListPayload;
import ai.first.api.coreapp.admin.AdminEndpoint.CustomerDetailPayload;
import ai.first.api.coreapp.admin.AdminEndpoint.CustomerLifecycleApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.CustomerListPayload;
import ai.first.api.coreapp.admin.AdminEndpoint.InvitationActionApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.IdentityRelinkApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.OrganizationActionApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.OrganizationCreateApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.OrganizationDetailPayload;
import ai.first.api.coreapp.admin.AdminEndpoint.OrganizationLifecycleApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.OrganizationListPayload;
import ai.first.api.coreapp.admin.AdminEndpoint.OrganizationRenameApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.IdentityRelinkApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.InvitationApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.InvitationsApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.MembershipActionApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.RoleChangePreviewApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.SupportAccessApiRequest;
import ai.first.api.coreapp.admin.AdminEndpoint.SupportAccessApiResponse;
import ai.first.api.coreapp.admin.AdminEndpoint.UserAdminDashboardPayload;
import ai.first.api.coreapp.admin.AdminEndpoint.UserAdminUserAccountPayload;
import ai.first.application.foundation.identity.AkkaIdentityRepository;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.Customer;
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
import ai.first.api.coreapp.admin.AdminEndpoint;

class AdminEndpointIntegrationTest extends TestKitSupport {

  @BeforeEach
  void seedDefaultAdminEndpointActors() {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant("tenant-starter", "Starter Tenant", true));
    seedIdentity(repository, "admin@example.test", "Admin", ScopeType.TENANT, "tenant-starter", null, FoundationRole.TENANT_ADMIN);
    seedIdentity(repository, "member@example.test", "Member", ScopeType.TENANT, "tenant-starter", null, FoundationRole.TENANT_EMPLOYEE);
  }

  @Test
  void adminCanSearchUsersThroughConcreteProtectedApiAndAuditIsVisible() throws Exception {
    var users = httpClient
        .GET("/api/admin/users?query=admin")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-users")
        .responseBodyAs(AdminUsersResponse.class)
        .invoke();

    assertTrue(users.status().isSuccess());
    assertEquals("corr-admin-users", users.body().correlationId());
    assertTrue(users.body().users().stream().anyMatch(user -> user.accountId().equals("admin@example.test")));
    assertTrue(users.body().users().stream().allMatch(user -> user.tenantId().equals("tenant-starter")));

    var audit = httpClient
        .GET("/api/admin/audit-events?limit=100")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-audit")
        .responseBodyAs(AdminAuditEventsResponse.class)
        .invoke();

    assertTrue(audit.status().isSuccess());
    assertTrue(audit.body().events().stream().anyMatch(event -> event.actionType().equals("USER_DIRECTORY_SEARCH") && event.correlationId().equals("corr-admin-users")));
    assertTrue(audit.body().events().stream().allMatch(event -> event.tenantId() == null || event.tenantId().equals("tenant-starter")));
  }

  @Test
  void saasOwnerOrganizationAdminApiSupportsSafeLifecycleAndDenials() throws Exception {
    seedOrganizationAdminActors();

    var list = httpClient
        .GET("/api/admin/organizations?query=starter")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "Owner"))
        .addHeader("X-Correlation-Id", "corr-org-list")
        .responseBodyAs(OrganizationListPayload.class)
        .invoke();
    assertTrue(list.status().isSuccess());
    assertEquals("corr-org-list", list.body().correlationId());
    assertTrue(list.body().organizations().stream().anyMatch(organization -> organization.organizationName().equals("Starter Tenant")));
    assertTrue(list.body().safeBoundaryNotice().contains("does not grant tenant/customer application-data access"));
    assertTrue(list.body().redactions().contains("provider-secrets-redacted"));
    assertTrue(!list.body().toString().contains("providerSecret"));

    var detail = httpClient
        .GET("/api/admin/organizations/tenant-starter")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "Owner"))
        .addHeader("X-Correlation-Id", "corr-org-read")
        .responseBodyAs(OrganizationDetailPayload.class)
        .invoke();
    assertTrue(detail.status().isSuccess());
    assertEquals("tenant-starter", detail.body().organization().organizationId());
    assertTrue(detail.body().visibleActions().contains("suspend"));

    var created = httpClient
        .POST("/api/admin/organizations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "Owner"))
        .addHeader("X-Correlation-Id", "corr-org-create")
        .withRequestBody(new OrganizationCreateApiRequest("Endpoint Organization", "idem-org-endpoint-create", "new customer boundary"))
        .responseBodyAs(OrganizationActionApiResponse.class)
        .invoke();
    assertTrue(created.status().isSuccess());
    assertEquals("accepted", created.body().status());
    assertEquals("active", created.body().organization().organization().status());
    assertTrue(created.body().traceRefs().stream().anyMatch(trace -> trace.contains("trace-organization-create")));
    var organizationId = created.body().organization().organization().organizationId();

    var replay = httpClient
        .POST("/api/admin/organizations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "Owner"))
        .withRequestBody(new OrganizationCreateApiRequest("Endpoint Organization", "idem-org-endpoint-create", "replay"))
        .responseBodyAs(OrganizationActionApiResponse.class)
        .invoke();
    assertEquals("no-op", replay.body().status());
    assertEquals(organizationId, replay.body().organization().organization().organizationId());

    var renamed = httpClient
        .POST("/api/admin/organizations/" + organizationId + "/rename")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "Owner"))
        .addHeader("X-Correlation-Id", "corr-org-rename")
        .withRequestBody(new OrganizationRenameApiRequest("Endpoint Organization Renamed", "idem-org-endpoint-rename", "display name correction"))
        .responseBodyAs(OrganizationActionApiResponse.class)
        .invoke();
    assertEquals("accepted", renamed.body().status());
    assertEquals("Endpoint Organization Renamed", renamed.body().organization().organization().organizationName());

    var suspended = httpClient
        .POST("/api/admin/organizations/" + organizationId + "/suspend")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "Owner"))
        .addHeader("X-Correlation-Id", "corr-org-suspend")
        .withRequestBody(new OrganizationLifecycleApiRequest("contract ended", "idem-org-endpoint-suspend"))
        .responseBodyAs(OrganizationActionApiResponse.class)
        .invoke();
    assertEquals("accepted", suspended.body().status());
    assertEquals("suspended", suspended.body().organization().organization().status());
    assertTrue(suspended.body().organization().safeBoundaryNotice().contains("does not grant tenant/customer application-data access"));

    var reactivated = httpClient
        .POST("/api/admin/organizations/" + organizationId + "/reactivate")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "Owner"))
        .addHeader("X-Correlation-Id", "corr-org-reactivate")
        .withRequestBody(new OrganizationLifecycleApiRequest("contract restored", "idem-org-endpoint-reactivate"))
        .responseBodyAs(OrganizationActionApiResponse.class)
        .invoke();
    assertEquals("accepted", reactivated.body().status());
    assertEquals("active", reactivated.body().organization().organization().status());

    var missingIdempotency = assertThrows(
        IllegalArgumentException.class,
        () -> httpClient
            .POST("/api/admin/organizations/" + organizationId + "/rename")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "Owner"))
            .withRequestBody(new OrganizationRenameApiRequest("No Key", null, "missing key"))
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(missingIdempotency.getMessage().contains("400"));

    var tenantAdminForbidden = assertThrows(
        RuntimeException.class,
        () -> httpClient
            .GET("/api/admin/organizations")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
            .addHeader("X-Correlation-Id", "corr-org-tenant-denied")
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(tenantAdminForbidden.getMessage().contains("403"));

    var customerAdminForbidden = assertThrows(
        RuntimeException.class,
        () -> httpClient
            .GET("/api/admin/organizations")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-customer-admin", "customer-admin@example.test", "Customer Admin"))
            .addHeader("X-Correlation-Id", "corr-org-customer-denied")
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(customerAdminForbidden.getMessage().contains("403"));

    var hiddenTarget = assertThrows(
        RuntimeException.class,
        () -> httpClient
            .GET("/api/admin/organizations/missing-organization")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-owner", "owner@example.test", "Owner"))
            .addHeader("X-Correlation-Id", "corr-org-hidden")
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(hiddenTarget.getMessage().contains("404"));
  }

  @Test
  void customerAdminApisDefaultToCustomerAdminAndDenyTenantOrOwnerRoles() throws Exception {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveCustomer(new Customer("tenant-starter", "customer-role-safe", "Role Safe Customer", true));
    seedIdentity(repository, "existing-customer-role-admin@example.test", "Existing Customer Admin", ScopeType.CUSTOMER, "tenant-starter", "customer-role-safe", FoundationRole.CUSTOMER_ADMIN);

    var defaulted = httpClient
        .POST("/api/admin/customers/customer-role-safe/admins/invitations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-admin-default-role")
        .withRequestBody(new CreateInvitationApiRequest("default-customer-admin@example.test", "Default Customer Admin", null, "idem-customer-admin-default-role"))
        .responseBodyAs(InvitationApiResponse.class)
        .invoke();
    assertTrue(defaulted.status().isSuccess());

    var adminsAfterDefault = httpClient
        .GET("/api/admin/customers/customer-role-safe/admins")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-admin-list-default")
        .responseBodyAs(CustomerAdminListPayload.class)
        .invoke();
    assertTrue(adminsAfterDefault.status().isSuccess());
    assertTrue(adminsAfterDefault.body().invitations().stream()
        .anyMatch(invitation -> "default-customer-admin@example.test".equals(invitation.email())
            && invitation.roles().equals(List.of("CUSTOMER_ADMIN"))
            && "customer-role-safe".equals(invitation.customerId())));

    var tenantRoleInviteDenied = assertThrows(
        RuntimeException.class,
        () -> httpClient
            .POST("/api/admin/customers/customer-role-safe/admins/invitations")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
            .addHeader("X-Correlation-Id", "corr-customer-admin-tenant-role-denied")
            .withRequestBody(new CreateInvitationApiRequest("tenant-role-customer-admin@example.test", "Bad Role", List.of("TENANT_ADMIN"), "idem-customer-admin-tenant-role"))
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(tenantRoleInviteDenied.getMessage().contains("403"));

    var ownerRoleInviteDenied = assertThrows(
        RuntimeException.class,
        () -> httpClient
            .POST("/api/admin/customers/customer-role-safe/admins/invitations")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
            .addHeader("X-Correlation-Id", "corr-customer-admin-owner-role-denied")
            .withRequestBody(new CreateInvitationApiRequest("owner-role-customer-admin@example.test", "Bad Role", List.of("SAAS_OWNER_ADMIN"), "idem-customer-admin-owner-role"))
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(ownerRoleInviteDenied.getMessage().contains("403"));

    var tenantRoleChangeDenied = assertThrows(
        RuntimeException.class,
        () -> httpClient
            .PUT("/api/admin/customers/customer-role-safe/admins/existing-customer-role-admin@example.test/roles")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
            .addHeader("X-Correlation-Id", "corr-customer-admin-role-tenant-denied")
            .withRequestBody(new ChangeRolesApiRequest(List.of("TENANT_ADMIN"), "escalation attempt", "idem-customer-admin-role-tenant"))
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(tenantRoleChangeDenied.getMessage().contains("403"));

    var ownerRoleChangeDenied = assertThrows(
        RuntimeException.class,
        () -> httpClient
            .PUT("/api/admin/customers/customer-role-safe/admins/existing-customer-role-admin@example.test/roles")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
            .addHeader("X-Correlation-Id", "corr-customer-admin-role-owner-denied")
            .withRequestBody(new ChangeRolesApiRequest(List.of("SAAS_OWNER_ADMIN"), "escalation attempt", "idem-customer-admin-role-owner"))
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(ownerRoleChangeDenied.getMessage().contains("403"));

    var adminsAfterDenials = httpClient
        .GET("/api/admin/customers/customer-role-safe/admins")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-admin-list-denials")
        .responseBodyAs(CustomerAdminListPayload.class)
        .invoke();
    assertTrue(adminsAfterDenials.status().isSuccess());
    assertTrue(adminsAfterDenials.body().admins().stream()
        .anyMatch(admin -> "existing-customer-role-admin@example.test".equals(admin.accountId())
            && admin.roles().equals(List.of("CUSTOMER_ADMIN"))));
    assertTrue(adminsAfterDenials.body().invitations().stream()
        .noneMatch(invitation -> List.of("tenant-role-customer-admin@example.test", "owner-role-customer-admin@example.test").contains(invitation.email())));
  }

  @Test
  void suspendedCustomerFailsClosedForCustomerAdminApiOperationsButCanReactivate() throws Exception {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveCustomer(new Customer("tenant-starter", "customer-suspended-admins", "Suspended Admin Customer", true));
    seedIdentity(repository, "existing-suspended-customer-admin@example.test", "Existing Suspended Customer Admin", ScopeType.CUSTOMER, "tenant-starter", "customer-suspended-admins", FoundationRole.CUSTOMER_ADMIN);

    var suspended = httpClient
        .POST("/api/admin/customers/customer-suspended-admins/suspend")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-suspend-before-admins")
        .withRequestBody(new CustomerLifecycleApiRequest("pause customer admin maintenance", "idem-customer-suspend-before-admins"))
        .responseBodyAs(CustomerActionApiResponse.class)
        .invoke();
    assertTrue(suspended.status().isSuccess());
    assertEquals("suspended", suspended.body().customer().customer().status());

    var detail = httpClient
        .GET("/api/admin/customers/customer-suspended-admins")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-suspended-detail")
        .responseBodyAs(CustomerDetailPayload.class)
        .invoke();
    assertTrue(detail.status().isSuccess());
    assertTrue(detail.body().visibleActions().contains("reactivate"));

    var listDenied = assertThrows(RuntimeException.class, () -> httpClient
        .GET("/api/admin/customers/customer-suspended-admins/admins")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-admin-list-suspended-denied")
        .responseBodyAs(String.class)
        .invoke());
    assertTrue(listDenied.getMessage().contains("403"));

    var inviteDenied = assertThrows(RuntimeException.class, () -> httpClient
        .POST("/api/admin/customers/customer-suspended-admins/admins/invitations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-admin-invite-suspended-denied")
        .withRequestBody(new CreateInvitationApiRequest("suspended-customer-admin@example.test", "Suspended Customer Admin", null, "idem-suspended-customer-admin-invite"))
        .responseBodyAs(String.class)
        .invoke());
    assertTrue(inviteDenied.getMessage().contains("403"));

    var roleDenied = assertThrows(RuntimeException.class, () -> httpClient
        .PUT("/api/admin/customers/customer-suspended-admins/admins/existing-suspended-customer-admin@example.test/roles")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-admin-role-suspended-denied")
        .withRequestBody(new ChangeRolesApiRequest(List.of("CUSTOMER_ADMIN"), "suspended customer", "idem-customer-admin-role-suspended"))
        .responseBodyAs(String.class)
        .invoke());
    assertTrue(roleDenied.getMessage().contains("403"));

    var statusDenied = assertThrows(RuntimeException.class, () -> httpClient
        .POST("/api/admin/customers/customer-suspended-admins/admins/existing-suspended-customer-admin@example.test/suspend")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-admin-status-suspended-denied")
        .withRequestBody(new AccountActionApiRequest("suspended customer", "idem-customer-admin-status-suspended"))
        .responseBodyAs(String.class)
        .invoke());
    assertTrue(statusDenied.getMessage().contains("403"));

    var reactivated = httpClient
        .POST("/api/admin/customers/customer-suspended-admins/reactivate")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-reactivate-before-admins")
        .withRequestBody(new CustomerLifecycleApiRequest("resume customer admin maintenance", "idem-customer-reactivate-before-admins"))
        .responseBodyAs(CustomerActionApiResponse.class)
        .invoke();
    assertTrue(reactivated.status().isSuccess());
    assertEquals("active", reactivated.body().customer().customer().status());

    var listAfterReactivate = httpClient
        .GET("/api/admin/customers/customer-suspended-admins/admins")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-admin-list-reactivated")
        .responseBodyAs(CustomerAdminListPayload.class)
        .invoke();
    assertTrue(listAfterReactivate.status().isSuccess());
    assertTrue(listAfterReactivate.body().admins().stream().anyMatch(admin -> "existing-suspended-customer-admin@example.test".equals(admin.accountId())));
  }

  @Test
  void customerListApiHonorsQueryAndStatusFiltersWithoutHiddenCountLeakage() throws Exception {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveCustomer(new Customer("tenant-starter", "customer-acme-active", "Acme Active Customer", true));
    repository.saveCustomer(new Customer("tenant-starter", "customer-acme-suspended", "Acme Suspended Customer", false));
    repository.saveCustomer(new Customer("tenant-starter", "customer-beta-active", "Beta Active Customer", true));
    repository.saveCustomer(new Customer("tenant-other", "customer-hidden-active", "Hidden Active Customer", true));

    var queryFiltered = httpClient
        .GET("/api/admin/customers?query=acme")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-query-filter")
        .responseBodyAs(CustomerListPayload.class)
        .invoke();
    assertTrue(queryFiltered.status().isSuccess());
    assertEquals("corr-customer-query-filter", queryFiltered.body().correlationId());
    assertEquals(2, queryFiltered.body().customers().size());
    assertTrue(queryFiltered.body().customers().stream().allMatch(customer -> customer.customerName().contains("Acme")));
    assertTrue(queryFiltered.body().customers().stream().noneMatch(customer -> customer.customerId().contains("hidden")));
    assertTrue(!queryFiltered.body().toString().contains("customer-hidden-active"));

    var activeFiltered = httpClient
        .GET("/api/admin/customers?status=active")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-status-active-filter")
        .responseBodyAs(CustomerListPayload.class)
        .invoke();
    assertTrue(activeFiltered.status().isSuccess());
    assertTrue(activeFiltered.body().customers().stream().allMatch(customer -> customer.status().equals("active")));
    assertTrue(activeFiltered.body().customers().stream().anyMatch(customer -> customer.customerId().equals("customer-acme-active")));
    assertTrue(activeFiltered.body().customers().stream().noneMatch(customer -> customer.customerId().equals("customer-acme-suspended")));

    var suspendedFiltered = httpClient
        .GET("/api/admin/customers?status=suspended")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-status-suspended-filter")
        .responseBodyAs(CustomerListPayload.class)
        .invoke();
    assertTrue(suspendedFiltered.status().isSuccess());
    assertTrue(suspendedFiltered.body().customers().stream().allMatch(customer -> customer.status().equals("suspended")));
    assertEquals(List.of("customer-acme-suspended"), suspendedFiltered.body().customers().stream().map(customer -> customer.customerId()).toList());

    var emptyFiltered = httpClient
        .GET("/api/admin/customers?query=no-match&status=active")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-customer-empty-filter")
        .responseBodyAs(CustomerListPayload.class)
        .invoke();
    assertTrue(emptyFiltered.status().isSuccess());
    assertTrue(emptyFiltered.body().customers().isEmpty());
    assertTrue(!emptyFiltered.body().toString().contains("hiddenCount"));
    assertTrue(!emptyFiltered.body().toString().contains("customer-hidden-active"));
  }

  @Test
  void userAdminDashboardAndAccountPayloadsExposeInvitationLifecycleWithoutRawTokens() throws Exception {
    var created = httpClient
        .POST("/api/admin/invitations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-dashboard-invite")
        .withRequestBody(new CreateInvitationApiRequest("dashboard-invite@example.test", "Dashboard Invite", List.of("TENANT_EMPLOYEE"), "idem-dashboard-invite"))
        .responseBodyAs(InvitationApiResponse.class)
        .invoke();
    assertTrue(created.status().isSuccess());

    var dashboard = httpClient
        .GET("/api/admin/users/dashboard")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-dashboard")
        .responseBodyAs(UserAdminDashboardPayload.class)
        .invoke();
    assertTrue(dashboard.status().isSuccess());
    assertEquals("corr-admin-dashboard", dashboard.body().correlationId());
    assertEquals("tenant", dashboard.body().selectedScope().scopeType());
    assertTrue(dashboard.body().counts().visibleUsers() >= 1);
    assertTrue(dashboard.body().counts().pendingInvitations() >= 1);
    assertTrue(dashboard.body().invitationQueue().stream().anyMatch(invitation -> invitation.invitationId().equals(created.body().invitationId())));
    assertTrue(dashboard.body().visibleActions().contains("action-invite-user"));
    assertTrue(dashboard.body().traceIds().contains("trace-user-admin-dashboard"));
    assertTrue(dashboard.body().toString().contains("dashboard-invite@example.test"));
    assertTrue(!dashboard.body().toString().contains("invite-token-"));
    assertTrue(!dashboard.body().toString().contains("tokenHash"));

    var account = httpClient
        .GET("/api/admin/users/dashboard-invite@example.test")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-account")
        .responseBodyAs(UserAdminUserAccountPayload.class)
        .invoke();
    assertTrue(account.status().isSuccess());
    assertEquals("dashboard-invite@example.test", account.body().account().accountId());
    assertTrue(account.body().invitationHistory().stream().anyMatch(invitation -> invitation.invitationId().equals(created.body().invitationId())));
    assertTrue(account.body().redactions().contains("raw-token-redacted"));
    assertTrue(account.body().visibleActions().contains("action-useradmin-preview-role-change"));
    assertTrue(!account.body().toString().contains("invite-token-"));
  }

  @Test
  void adminInvitationApiRequiresIdempotencyAndReplaysToSameInvitation() throws Exception {
    var missingKey = assertThrows(
        IllegalArgumentException.class,
        () -> httpClient
            .POST("/api/admin/invitations")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
            .withRequestBody(new CreateInvitationApiRequest("api-invite@example.test", "API Invite", List.of("TENANT_EMPLOYEE"), null))
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(missingKey.getMessage().contains("400"));

    var request = new CreateInvitationApiRequest("api-invite@example.test", "API Invite", List.of("TENANT_EMPLOYEE"), "idem-api-invite");
    var first = httpClient
        .POST("/api/admin/invitations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-invite")
        .withRequestBody(request)
        .responseBodyAs(InvitationApiResponse.class)
        .invoke();
    var replay = httpClient
        .POST("/api/admin/invitations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-invite-replay")
        .withRequestBody(request)
        .responseBodyAs(InvitationApiResponse.class)
        .invoke();

    assertTrue(first.status().isSuccess());
    assertTrue(replay.status().isSuccess());
    assertEquals(first.body().invitationId(), replay.body().invitationId());
    assertTrue(List.of("pending_delivery", "sent").contains(first.body().status()));
    assertNotNull(first.body().deliveryStatus());

    var listed = httpClient
        .GET("/api/admin/invitations")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .responseBodyAs(InvitationsApiResponse.class)
        .invoke();
    assertTrue(listed.body().invitations().stream().anyMatch(invitation -> invitation.invitationId().equals(first.body().invitationId())));

    var resent = httpClient
        .POST("/api/admin/invitations/" + first.body().invitationId() + "/resend")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .withRequestBody(new InvitationActionApiRequest("repair delivery", "idem-api-resend"))
        .responseBodyAs(InvitationApiResponse.class)
        .invoke();
    assertEquals(1, resent.body().resendCount());

    var revoked = httpClient
        .POST("/api/admin/invitations/" + first.body().invitationId() + "/revoke")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .withRequestBody(new InvitationActionApiRequest("wrong recipient", null))
        .responseBodyAs(InvitationApiResponse.class)
        .invoke();
    assertEquals("revoked", revoked.body().status());
  }

  @Test
  void adminCanUseConcreteMembershipRoleAndStatusApiActions() throws Exception {
    var roleChange = httpClient
        .POST("/api/admin/memberships/membership-member@example.test/roles")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-role-change")
        .withRequestBody(new ChangeRolesApiRequest(List.of("TENANT_EMPLOYEE"), "least privilege replay", "idem-api-role"))
        .responseBodyAs(MembershipActionApiResponse.class)
        .invoke();
    assertTrue(roleChange.status().isSuccess());
    assertEquals("no-op", roleChange.body().status());
    assertEquals("membership-member@example.test", roleChange.body().membershipId());
    assertTrue(roleChange.body().traceId().contains("trace-useradmin-change-member-roles"));

    var disabled = httpClient
        .POST("/api/admin/memberships/membership-member@example.test/status")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-member-disable")
        .withRequestBody(new ChangeMembershipStatusApiRequest("SUSPENDED", "offboarding", "idem-api-disable"))
        .responseBodyAs(MembershipActionApiResponse.class)
        .invoke();
    assertTrue(disabled.status().isSuccess());
    assertEquals("accepted", disabled.body().status());
    assertEquals("suspended", disabled.body().membershipStatus());
  }

  @Test
  void adminCanPreviewRolesManageSupportAccessIdentityRelinkAndAccessReviewThroughProtectedApis() throws Exception {
    var preview = httpClient
        .POST("/api/admin/memberships/membership-member@example.test/roles/preview")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-admin-role-preview")
        .withRequestBody(new ChangeRolesApiRequest(List.of("TENANT_EMPLOYEE"), "least privilege preview", null))
        .responseBodyAs(RoleChangePreviewApiResponse.class)
        .invoke();
    assertTrue(preview.status().isSuccess());
    assertTrue(preview.body().allowed());
    assertTrue(preview.body().traceId().contains("trace-useradmin-preview-role-change"));

    var support = httpClient
        .POST("/api/admin/support-access/membership-member@example.test/grant")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-support-grant")
        .withRequestBody(new SupportAccessApiRequest("break glass support", "2026-06-03T12:00:00Z", "idem-support-grant"))
        .responseBodyAs(SupportAccessApiResponse.class)
        .invoke();
    assertTrue(support.status().isSuccess());
    assertTrue(support.body().supportAccess());
    assertEquals("membership-member@example.test", support.body().membershipId());

    var relink = httpClient
        .POST("/api/admin/users/member@example.test/identity-relink/request")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-identity-relink")
        .withRequestBody(new IdentityRelinkApiRequest("provider subject mismatch", null, "idem-relink-request"))
        .responseBodyAs(IdentityRelinkApiResponse.class)
        .invoke();
    assertTrue(relink.status().isSuccess());
    assertEquals("approval-required", relink.body().status());

    var accessReview = httpClient
        .POST("/api/admin/access-review")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-access-review-api")
        .withRequestBody(new AccessReviewApiRequest("quarterly user admin review", "idem-access-review-api"))
        .responseBodyAs(AccessReviewApiResponse.class)
        .invoke();
    assertTrue(accessReview.status().isSuccess());
    assertTrue(List.of("queued", "running", "blocked_provider_or_runtime").contains(accessReview.body().status()));
    assertTrue(accessReview.body().traceIds().stream().anyMatch(trace -> trace.contains("trace-useradmin-access-review")));

    var readAccessReview = httpClient
        .GET("/api/admin/access-review/" + accessReview.body().taskId())
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-access-review-read")
        .responseBodyAs(AccessReviewApiResponse.class)
        .invoke();
    assertTrue(readAccessReview.status().isSuccess());
    assertEquals(accessReview.body().taskId(), readAccessReview.body().taskId());
  }

  @Test
  void adminAccountLifecycleApiIsBackendAuthorizedAndIdempotent() throws Exception {
    var disabled = httpClient
        .POST("/api/admin/users/member@example.test/disable")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-account-disable")
        .withRequestBody(new AccountActionApiRequest("offboarding", "idem-account-disable"))
        .responseBodyAs(AccountActionApiResponse.class)
        .invoke();
    assertTrue(disabled.status().isSuccess());
    assertEquals("disabled", disabled.body().accountStatus());

    var reactivated = httpClient
        .POST("/api/admin/users/member@example.test/reactivate")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .addHeader("X-Correlation-Id", "corr-account-reactivate")
        .withRequestBody(new AccountActionApiRequest("returned", "idem-account-reactivate"))
        .responseBodyAs(AccountActionApiResponse.class)
        .invoke();
    assertTrue(reactivated.status().isSuccess());
    assertEquals("active", reactivated.body().accountStatus());
  }

  @Test
  void protectedAdminApisDenyMissingForbiddenAndCrossContextAccess() throws Exception {
    var missingAuth = assertThrows(
        IllegalArgumentException.class,
        () -> httpClient.GET("/api/admin/users").responseBodyAs(String.class).invoke());
    assertTrue(missingAuth.getMessage().contains("400") || missingAuth.getMessage().contains("401") || missingAuth.getMessage().contains("403"));

    var employeeForbidden = assertThrows(
        RuntimeException.class,
        () -> httpClient
            .GET("/api/admin/users")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-member", "member@example.test", "Member"))
            .addHeader("X-Correlation-Id", "corr-employee-denied")
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(employeeForbidden.getMessage().contains("403"));

    var crossContextDenied = assertThrows(
        RuntimeException.class,
        () -> httpClient
            .GET("/api/admin/users")
            .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
            .addHeader("X-Selected-Context-Id", "membership-member@example.test")
            .addHeader("X-Correlation-Id", "corr-cross-context-denied")
            .responseBodyAs(String.class)
            .invoke());
    assertTrue(crossContextDenied.getMessage().contains("403"));

    var audit = httpClient
        .GET("/api/admin/audit-events?limit=100")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Admin"))
        .responseBodyAs(AdminAuditEventsResponse.class)
        .invoke();
    assertTrue(audit.body().events().stream().anyMatch(event -> event.correlationId().equals("corr-employee-denied") && event.result().equals("denied")));
  }

  private void seedOrganizationAdminActors() {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant("tenant-starter", "Starter Tenant", true));
    repository.saveCustomer(new Customer("tenant-starter", "customer-admin-target", "Customer Admin Target", true));
    seedIdentity(repository, "owner@example.test", "Owner", ScopeType.SAAS_OWNER, null, null, FoundationRole.SAAS_OWNER_ADMIN);
    seedIdentity(repository, "customer-admin@example.test", "Customer Admin", ScopeType.CUSTOMER, "tenant-starter", "customer-admin-target", FoundationRole.CUSTOMER_ADMIN);
  }

  private void seedIdentity(AkkaIdentityRepository repository, String email, String displayName, ScopeType scopeType, String tenantId, String customerId, FoundationRole role) {
    repository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "UNLINKED"));
    repository.saveProfile(new UserProfile(email, email, displayName, null, null, null));
    repository.saveSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.saveMembership(new Membership("membership-" + email, email, scopeType, tenantId, customerId, List.of(role), MembershipStatus.ACTIVE, false, null));
  }

  private String bearerToken(String subject, String email, String name) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(Map.of("sub", subject, "email", email, "name", name)));
    return header + "." + payload;
  }
}
