package ai.first.api.coreapp.admin;

import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.invitation.Invitation;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import static akka.javasdk.http.HttpException.forbidden;
import static akka.javasdk.http.HttpException.notFound;
import static akka.javasdk.http.HttpException.unauthorized;
import ai.first.application.foundation.audit.AdminAuditView;
import ai.first.application.foundation.audit.AdminAuditView.AdminAuditRow;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.coreapp.useradmin.SaasOwnerOrganizationAdminService;
import ai.first.application.coreapp.useradmin.UserAdminService.RoleChangePreview;
import ai.first.application.coreapp.myaccount.DigestExportService;
import ai.first.application.foundation.invitation.InvitationService;
import ai.first.application.foundation.invitation.InvitationView.InvitationRow;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.application.coreapp.useradmin.UserAdminService.UserDirectoryRow;
import ai.first.application.foundation.identity.WorkosIdentityResolver;
import ai.first.domain.coreapp.useradmin.AccessReviewTask;
import ai.first.domain.coreapp.myaccount.DigestExportRequest;
import ai.first.domain.foundation.identity.EnterpriseIdentityProviderStatus;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScimProvisioningRequest;
import ai.first.domain.foundation.identity.ScimProvisioningResult;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.SsoConfigurationValidation;
import ai.first.domain.foundation.identity.WorkosIdentity;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import ai.first.application.foundation.invitation.InvitationView;
import ai.first.application.coreapp.useradmin.UserAdminService;

/** Concrete protected User Admin and audit APIs backing starter workstream surfaces. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/admin")
public class AdminEndpoint extends AbstractHttpEndpoint {

  public AdminEndpoint(ComponentClient componentClient) {
    StarterSecurityComponents.bindAkkaRuntime(componentClient);
  }

  @Get("/users/dashboard")
  public HttpResponse usersDashboard() {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var scopeType = actor.selectedContext().scopeType();
      var tenantId = actor.selectedContext().tenantId();
      var customerId = actor.selectedContext().customerId();
      var users = StarterSecurityComponents.userAdminService().searchUsers(actor, null, correlationId).stream()
          .map(AdminUserResponse::from)
          .toList();
      var invitations = StarterSecurityComponents.invitationView()
          .list(actor, scopeType, tenantId, customerId)
          .stream()
          .map(InvitationApiResponse::from)
          .toList();
      var audit = new AdminAuditView(StarterSecurityComponents.userAdminService())
          .list(actor, 10, correlationId)
          .stream()
          .map(AdminAuditEventResponse::from)
          .toList();
      var pendingInvitations = invitations.stream().filter(invitation -> !List.of("accepted", "revoked", "expired").contains(invitation.status())).count();
      var deliveryFailures = invitations.stream().filter(invitation -> "failed".equals(invitation.deliveryStatus())).count();
      return HttpResponses.ok(new UserAdminDashboardPayload(
          new SelectedAdminScope(scopeType.name().toLowerCase(), tenantId, customerId, actor.selectedContext().membershipId()),
          new UserAdminDashboardCounts(users.size(), pendingInvitations, deliveryFailures, audit.size()),
          invitations,
          users.stream().limit(10).toList(),
          audit,
          List.of("action-invite-user", "action-display-user-list", "action-useradmin-start-access-review", "action-read-support-access", "action-grant-support-access"),
          List.of("trace-user-admin-dashboard", correlationId),
          correlationId));
    });
  }

  @Get("/users")
  public HttpResponse users() {
    var query = requestContext().queryParams().getString("query").orElse(null);
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var rows = StarterSecurityComponents.userAdminService().searchUsers(actor, query, correlationId).stream()
          .map(AdminUserResponse::from)
          .toList();
      return HttpResponses.ok(new AdminUsersResponse(rows, correlationId));
    });
  }

  @Get("/organizations")
  public HttpResponse organizations() {
    var query = requestContext().queryParams().getString("query").orElse(null);
    var status = requestContext().queryParams().getString("status").orElse(null);
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      return HttpResponses.ok(OrganizationListPayload.from(StarterSecurityComponents.saasOwnerOrganizationAdminService().listOrganizations(actor, query, status, correlationId)));
    });
  }

  @Get("/organizations/{organizationId}")
  public HttpResponse organization(String organizationId) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      return HttpResponses.ok(OrganizationDetailPayload.from(StarterSecurityComponents.saasOwnerOrganizationAdminService().readOrganization(actor, organizationId, correlationId)));
    });
  }

  @Post("/organizations")
  public HttpResponse createOrganization(OrganizationCreateApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.saasOwnerOrganizationAdminService().createOrganization(actor, request == null ? null : request.organizationName(), stableIdempotencyKey, request == null ? null : request.reason(), correlationId);
      return HttpResponses.ok(OrganizationActionApiResponse.from(result));
    });
  }

  @Post("/organizations/{organizationId}/rename")
  public HttpResponse renameOrganization(String organizationId, OrganizationRenameApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.saasOwnerOrganizationAdminService().renameOrganization(actor, organizationId, request == null ? null : request.organizationName(), stableIdempotencyKey, request == null ? null : request.reason(), correlationId);
      return HttpResponses.ok(OrganizationActionApiResponse.from(result));
    });
  }

  @Post("/organizations/{organizationId}/suspend")
  public HttpResponse suspendOrganization(String organizationId, OrganizationLifecycleApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.saasOwnerOrganizationAdminService().suspendOrganization(actor, organizationId, request == null ? null : request.reason(), stableIdempotencyKey, correlationId);
      return HttpResponses.ok(OrganizationActionApiResponse.from(result));
    });
  }

  @Post("/organizations/{organizationId}/reactivate")
  public HttpResponse reactivateOrganization(String organizationId, OrganizationLifecycleApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.saasOwnerOrganizationAdminService().reactivateOrganization(actor, organizationId, request == null ? null : request.reason(), stableIdempotencyKey, correlationId);
      return HttpResponses.ok(OrganizationActionApiResponse.from(result));
    });
  }

  @Get("/saas-owner-admins")
  public HttpResponse saasOwnerAdmins() {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      if (actor.selectedContext().scopeType() != ScopeType.SAAS_OWNER || !actor.selectedContext().hasCapability("saas_owner.user.manage")) throw forbidden();
      var admins = StarterSecurityComponents.userAdminService().searchUsers(actor, null, correlationId).stream().map(AdminSubjectSummary::from).toList();
      return HttpResponses.ok(new SaasOwnerAdminListPayload(admins, List.of(), List.of("trace-saas-owner-admins"), correlationId, ADMIN_SUBJECT_REDACTIONS));
    });
  }

  @Get("/organizations/{organizationId}/admins")
  public HttpResponse organizationAdmins(String organizationId) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var organization = OrganizationSummaryApiResponse.from(StarterSecurityComponents.saasOwnerOrganizationAdminService().readOrganization(actor, organizationId, correlationId).organization());
      return HttpResponses.ok(new OrganizationAdminListPayload(organization, List.of(), List.of(), List.of("trace-organization-admins"), correlationId, ADMIN_SUBJECT_REDACTIONS));
    });
  }

  @Get("/customers")
  public HttpResponse customers() {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.tenantCustomerAdminService().listCustomers(actor, null, null, correlationId);
      return HttpResponses.ok(new CustomerListPayload(result.customers().stream().map(AdminEndpoint::fromCustomerSummary).toList(), result.safeBoundaryNotice(), result.traceRefs(), result.correlationId(), CUSTOMER_REDACTIONS));
    });
  }

  @Get("/customers/{customerId}")
  public HttpResponse customer(String customerId) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      return HttpResponses.ok(fromCustomerDetail(StarterSecurityComponents.tenantCustomerAdminService().readCustomer(actor, customerId, correlationId)));
    });
  }

  @Post("/customers")
  public HttpResponse createCustomer(CustomerCreateApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.tenantCustomerAdminService().createCustomer(actor, request == null ? null : request.customerName(), request == null ? null : request.idempotencyKey(), request == null ? null : request.reason(), correlationId);
      return HttpResponses.ok(new CustomerActionApiResponse(result.status(), result.message(), fromCustomerDetail(result.customer()), result.traceRefs(), result.correlationId()));
    });
  }

  @Post("/customers/{customerId}/rename")
  public HttpResponse renameCustomer(String customerId, CustomerRenameApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.tenantCustomerAdminService().renameCustomer(actor, customerId, request == null ? null : request.customerName(), request == null ? null : request.idempotencyKey(), request == null ? null : request.reason(), correlationId);
      return HttpResponses.ok(new CustomerActionApiResponse(result.status(), result.message(), fromCustomerDetail(result.customer()), result.traceRefs(), result.correlationId()));
    });
  }

  @Post("/customers/{customerId}/suspend")
  public HttpResponse suspendCustomer(String customerId, CustomerLifecycleApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.tenantCustomerAdminService().suspendCustomer(actor, customerId, request == null ? null : request.reason(), request == null ? null : request.idempotencyKey(), correlationId);
      return HttpResponses.ok(new CustomerActionApiResponse(result.status(), result.message(), fromCustomerDetail(result.customer()), result.traceRefs(), result.correlationId()));
    });
  }

  @Post("/customers/{customerId}/reactivate")
  public HttpResponse reactivateCustomer(String customerId, CustomerLifecycleApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.tenantCustomerAdminService().reactivateCustomer(actor, customerId, request == null ? null : request.reason(), request == null ? null : request.idempotencyKey(), correlationId);
      return HttpResponses.ok(new CustomerActionApiResponse(result.status(), result.message(), fromCustomerDetail(result.customer()), result.traceRefs(), result.correlationId()));
    });
  }

  @Get("/customers/{customerId}/admins")
  public HttpResponse customerAdmins(String customerId) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var customer = fromCustomerSummary(StarterSecurityComponents.tenantCustomerAdminService().readCustomer(actor, customerId, correlationId).customer());
      return HttpResponses.ok(new CustomerAdminListPayload(customer, List.of(), List.of(), List.of("trace-customer-admins"), correlationId, CUSTOMER_REDACTIONS));
    });
  }

  @Get("/users/{accountId}")
  public HttpResponse userAccount(String accountId) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var users = StarterSecurityComponents.userAdminService().searchUsers(actor, accountId, correlationId).stream()
          .filter(user -> user.accountId().equals(accountId))
          .map(AdminUserResponse::from)
          .toList();
      if (users.isEmpty()) throw notFound();
      var user = users.get(0);
      var invitations = StarterSecurityComponents.invitationView()
          .list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId())
          .stream()
          .filter(invitation -> invitation.targetEmail().equals(accountId))
          .map(InvitationApiResponse::from)
          .toList();
      var audit = new AdminAuditView(StarterSecurityComponents.userAdminService())
          .list(actor, 25, correlationId)
          .stream()
          .filter(event -> accountId.equals(event.targetAccountId()))
          .map(AdminAuditEventResponse::from)
          .toList();
      return HttpResponses.ok(new UserAdminUserAccountPayload(
          user,
          invitations,
          audit,
          List.of("action-useradmin-disable-member", "action-useradmin-reactivate-member", "action-useradmin-preview-role-change", "action-useradmin-change-member-roles", "action-disable-account", "action-reactivate-account", "action-request-identity-relink", "action-read-support-access", "action-grant-support-access", "action-revoke-support-access", "action-useradmin-read-access-review"),
          List.of("raw-token-redacted", "token-hash-redacted", "provider-secret-redacted"),
          List.of("trace-user-admin-detail", correlationId),
          correlationId));
    });
  }

  @Get("/invitations")
  public HttpResponse invitations() {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var invites = StarterSecurityComponents.invitationView()
          .list(actor, actor.selectedContext().scopeType(), actor.selectedContext().tenantId(), actor.selectedContext().customerId())
          .stream()
          .map(InvitationApiResponse::from)
          .toList();
      return HttpResponses.ok(new InvitationsApiResponse(invites, correlationId));
    });
  }

  @Get("/invitations/{invitationId}/history")
  public HttpResponse invitationHistory(String invitationId) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var history = StarterSecurityComponents.invitationView().history(actor, invitationId).stream()
          .map(InvitationHistoryApiResponse::from)
          .toList();
      return HttpResponses.ok(new InvitationHistoryApiResponses(history, correlationId));
    });
  }

  @Post("/users/invitations")
  public HttpResponse createUserInvitation(CreateInvitationApiRequest request) {
    return createInvitation(request);
  }

  @Post("/invitations")
  public HttpResponse createInvitation(CreateInvitationApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var invite = StarterSecurityComponents.invitationService().createInvitation(
          actor,
          new InvitationService.CreateInvitationRequest(
              stableIdempotencyKey,
              actor.selectedContext().scopeType(),
              actor.selectedContext().tenantId(),
              actor.selectedContext().customerId(),
              requireText(request == null ? null : request.email(), "email"),
              textOr(request == null ? null : request.displayName(), "Invited User"),
              rolesOrDefault(request == null ? null : request.roles()),
              Instant.now().plus(7, ChronoUnit.DAYS),
              "admin-api",
              correlationId));
      return HttpResponses.ok(InvitationApiResponse.from(invite, correlationId));
    });
  }

  @Post("/invitations/{invitationId}/resend")
  public HttpResponse resendInvitation(String invitationId, InvitationActionApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var invite = StarterSecurityComponents.invitationService().resend(actor, invitationId, stableIdempotencyKey, textOr(request == null ? null : request.reason(), "admin-api-resend"), correlationId);
      return HttpResponses.ok(InvitationApiResponse.from(invite, correlationId));
    });
  }

  @Post("/invitations/{invitationId}/revoke")
  public HttpResponse revokeInvitation(String invitationId, InvitationActionApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var invite = StarterSecurityComponents.invitationService().revoke(actor, invitationId, textOr(request == null ? null : request.reason(), "admin-api-revoke"), correlationId);
      return HttpResponses.ok(InvitationApiResponse.from(invite, correlationId));
    });
  }

  @Post("/memberships/{membershipId}/roles/preview")
  public HttpResponse previewMembershipRoleChange(String membershipId, ChangeRolesApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var preview = StarterSecurityComponents.userAdminService().previewRoleChange(actor, membershipId, rolesOrDefault(request == null ? null : request.roles()), textOr(request == null ? null : request.reason(), "admin-api-role-preview"), correlationId);
      return HttpResponses.ok(RoleChangePreviewApiResponse.from(preview, correlationId));
    });
  }

  @Post("/memberships/{membershipId}/roles")
  public HttpResponse changeMembershipRoles(String membershipId, ChangeRolesApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.userAdminService().changeMemberRoles(actor, membershipId, rolesOrDefault(request == null ? null : request.roles()), textOr(request == null ? null : request.reason(), "admin-api-role-change"), stableIdempotencyKey, correlationId);
      return HttpResponses.ok(new MembershipActionApiResponse(result.status(), result.message(), result.membership().membershipId(), result.membership().accountId(), result.membership().roles().stream().map(Enum::name).toList(), result.membership().status().name().toLowerCase(), result.traceId(), correlationId));
    });
  }

  @Post("/memberships/{membershipId}/status")
  public HttpResponse updateMembershipStatus(String membershipId, ChangeMembershipStatusApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var targetStatus = MembershipStatus.valueOf(requireText(request == null ? null : request.status(), "status").toUpperCase());
      var result = StarterSecurityComponents.userAdminService().updateMemberStatus(actor, membershipId, targetStatus, textOr(request == null ? null : request.reason(), "admin-api-status-change"), stableIdempotencyKey, correlationId);
      return HttpResponses.ok(new MembershipActionApiResponse(result.status(), result.message(), result.membership().membershipId(), result.membership().accountId(), result.membership().roles().stream().map(Enum::name).toList(), result.membership().status().name().toLowerCase(), result.traceId(), correlationId));
    });
  }

  @Post("/users/{accountId}/disable")
  public HttpResponse disableAccount(String accountId, AccountActionApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var account = StarterSecurityComponents.userAdminService().disableAccount(actor, accountId, textOr(request == null ? null : request.reason(), "admin-api-disable-account"), correlationId);
      return HttpResponses.ok(new AccountActionApiResponse("accepted", account.accountId(), account.status().name().toLowerCase(), List.of("trace-useradmin-account-disable-" + correlationId.hashCode()), correlationId));
    });
  }

  @Post("/users/{accountId}/reactivate")
  public HttpResponse reactivateAccount(String accountId, AccountActionApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var account = StarterSecurityComponents.userAdminService().reactivateAccount(actor, accountId, textOr(request == null ? null : request.reason(), "admin-api-reactivate-account"), correlationId);
      return HttpResponses.ok(new AccountActionApiResponse("accepted", account.accountId(), account.status().name().toLowerCase(), List.of("trace-useradmin-account-reactivate-" + correlationId.hashCode()), correlationId));
    });
  }

  @Post("/users/{accountId}/identity-relink/request")
  public HttpResponse requestIdentityRelink(String accountId, IdentityRelinkApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.userAdminService().requestIdentityRelink(actor, accountId, textOr(request == null ? null : request.reason(), "admin-api-identity-relink"), stableIdempotencyKey, correlationId);
      return HttpResponses.ok(IdentityRelinkApiResponse.from(result, correlationId));
    });
  }

  @Get("/users/{accountId}/identity-relink")
  public HttpResponse readIdentityRelink(String accountId) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      return HttpResponses.ok(IdentityRelinkApiResponse.from(StarterSecurityComponents.userAdminService().readIdentityRelink(actor, accountId, correlationId), correlationId));
    });
  }

  @Post("/users/{accountId}/identity-relink/approve")
  public HttpResponse approveIdentityRelink(String accountId, IdentityRelinkApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.userAdminService().approveIdentityRelink(actor, accountId, textOr(request == null ? null : request.reason(), "admin-api-identity-approve"), request == null ? null : request.approvalRef(), stableIdempotencyKey, correlationId);
      return HttpResponses.ok(IdentityRelinkApiResponse.from(result, correlationId));
    });
  }

  @Post("/users/{accountId}/identity-relink/deny")
  public HttpResponse denyIdentityRelink(String accountId, IdentityRelinkApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.userAdminService().denyIdentityRelink(actor, accountId, textOr(request == null ? null : request.reason(), "admin-api-identity-deny"), stableIdempotencyKey, correlationId);
      return HttpResponses.ok(IdentityRelinkApiResponse.from(result, correlationId));
    });
  }

  @Post("/users/{accountId}/identity-relink/complete")
  public HttpResponse completeIdentityRelink(String accountId, IdentityRelinkApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.userAdminService().completeIdentityRelink(actor, accountId, request == null ? null : request.approvalRef(), stableIdempotencyKey, correlationId);
      return HttpResponses.ok(IdentityRelinkApiResponse.from(result, correlationId));
    });
  }

  @Post("/support-access/{membershipId}/grant")
  public HttpResponse grantSupportAccess(String membershipId, SupportAccessApiRequest request) {
    return updateSupportAccess(membershipId, true, request);
  }

  @Post("/support-access/{membershipId}/revoke")
  public HttpResponse revokeSupportAccess(String membershipId, SupportAccessApiRequest request) {
    return updateSupportAccess(membershipId, false, request);
  }

  @Post("/support-access/{membershipId}/extend")
  public HttpResponse extendSupportAccess(String membershipId, SupportAccessApiRequest request) {
    return updateSupportAccess(membershipId, true, request);
  }

  private HttpResponse updateSupportAccess(String membershipId, boolean enabled, SupportAccessApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var expiresAt = request == null || request.expiresAt() == null || request.expiresAt().isBlank() ? null : Instant.parse(request.expiresAt());
      var membership = StarterSecurityComponents.userAdminService().updateSupportAccess(actor, membershipId, enabled, expiresAt, textOr(request == null ? null : request.reason(), enabled ? "admin-api-support-access-grant" : "admin-api-support-access-revoke"), stableIdempotencyKey, correlationId);
      return HttpResponses.ok(new SupportAccessApiResponse("accepted", membership.membershipId(), membership.accountId(), membership.supportAccess(), membership.expiresAt() == null ? null : membership.expiresAt().toString(), List.of("trace-useradmin-support-access-" + correlationId.hashCode()), correlationId));
    });
  }

  @Post("/access-review")
  public HttpResponse startAccessReview(AccessReviewApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      return HttpResponses.ok(AccessReviewApiResponse.from(StarterSecurityComponents.userAdminAccessReviewService().start(actor, stableIdempotencyKey, correlationId), correlationId));
    });
  }

  @Get("/access-review/{taskId}")
  public HttpResponse readAccessReview(String taskId) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      return HttpResponses.ok(AccessReviewApiResponse.from(StarterSecurityComponents.userAdminAccessReviewService().read(actor, taskId, correlationId), correlationId));
    });
  }

  @Post("/access-review/{taskId}/cancel")
  public HttpResponse cancelAccessReview(String taskId, AccessReviewApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      return HttpResponses.ok(AccessReviewApiResponse.from(StarterSecurityComponents.userAdminAccessReviewService().cancel(actor, taskId, textOr(request == null ? null : request.reason(), "admin-api-access-review-cancel"), correlationId), correlationId));
    });
  }

  @Post("/access-review/{taskId}/accept")
  public HttpResponse acceptAccessReviewResult(String taskId, AccessReviewApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      return HttpResponses.ok(AccessReviewApiResponse.from(StarterSecurityComponents.userAdminAccessReviewService().acceptResult(actor, taskId, textOr(request == null ? null : request.reason(), "admin-api-access-review-accept"), correlationId), correlationId));
    });
  }

  @Post("/access-review/{taskId}/reject")
  public HttpResponse rejectAccessReviewResult(String taskId, AccessReviewApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      return HttpResponses.ok(AccessReviewApiResponse.from(StarterSecurityComponents.userAdminAccessReviewService().rejectResult(actor, taskId, textOr(request == null ? null : request.reason(), "admin-api-access-review-reject"), correlationId), correlationId));
    });
  }

  @Get("/enterprise-identity/status")
  public HttpResponse enterpriseIdentityStatus() {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      return HttpResponses.ok(EnterpriseIdentityStatusApiResponse.from(StarterSecurityComponents.enterpriseIdentityAdminService().status(actor, correlationId), correlationId));
    });
  }

  @Post("/enterprise-identity/scim/validate")
  public HttpResponse validateScimOperation(ScimProvisioningApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.enterpriseIdentityAdminService().validateScimOperation(actor, new ScimProvisioningRequest(
          request == null ? null : request.operation(),
          request == null ? null : request.externalId(),
          request == null ? null : request.email(),
          request == null ? null : request.displayName(),
          request == null || request.scopeType() == null ? null : ScopeType.valueOf(request.scopeType().toUpperCase()),
          request == null ? null : request.tenantId(),
          request == null ? null : request.customerId(),
          rolesOrDefault(request == null ? null : request.roles()),
          request == null ? null : request.reason(),
          stableIdempotencyKey), correlationId);
      return HttpResponses.ok(ScimProvisioningApiResponse.from(result, correlationId));
    });
  }

  @Post("/enterprise-identity/sso/validate")
  public HttpResponse validateSsoConfiguration(SsoValidationApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.enterpriseIdentityAdminService().validateSsoConfiguration(actor, request == null ? null : request.domain(), request == null ? null : request.issuer(), request == null ? null : request.metadataUrl(), request != null && request.productionRequested(), correlationId);
      return HttpResponses.ok(SsoValidationApiResponse.from(result, correlationId));
    });
  }

  @Get("/digest-export/requests")
  public HttpResponse digestExportRequests() {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var rows = StarterSecurityComponents.digestExportService().list(actor, correlationId).stream().map(DigestExportApiResponse::from).toList();
      return HttpResponses.ok(new DigestExportApiResponses(rows, correlationId));
    });
  }

  @Post("/digest-export/manual-digests")
  public HttpResponse startManualDigest(DigestCommandApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.digestExportService().startManualDigest(actor, new DigestExportService.DigestCommand(stableIdempotencyKey, null, request == null ? null : request.redactionProfile(), request == null ? null : request.evidenceScope()), correlationId);
      return HttpResponses.ok(DigestExportApiResponse.from(result));
    });
  }

  @Post("/digest-export/scheduled-digests")
  public HttpResponse scheduleDigest(DigestCommandApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.digestExportService().scheduleDigest(actor, new DigestExportService.DigestCommand(stableIdempotencyKey, request == null || request.scheduledFor() == null ? null : Instant.parse(request.scheduledFor()), request == null ? null : request.redactionProfile(), request == null ? null : request.evidenceScope()), correlationId);
      return HttpResponses.ok(DigestExportApiResponse.from(result));
    });
  }

  @Post("/digest-export/exports")
  public HttpResponse requestExport(ExportCommandApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.digestExportService().requestExport(actor, new DigestExportService.ExportCommand(stableIdempotencyKey, request == null ? null : request.redactionProfile(), request == null ? null : request.exportFormat(), request != null && request.sensitiveApprovalRequired(), request == null ? null : request.evidenceScope()), correlationId);
      return HttpResponses.ok(DigestExportApiResponse.from(result));
    });
  }

  @Post("/digest-export/exports/{requestId}/approve")
  public HttpResponse approveExport(String requestId, ExportApprovalApiRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.digestExportService().approveExport(actor, requestId, request == null ? null : request.reason(), correlationId);
      return HttpResponses.ok(DigestExportApiResponse.from(result));
    });
  }

  @Post("/digest-export/legal-holds")
  public HttpResponse requestLegalHold(LegalHoldApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.digestExportService().requestLegalHold(actor, new DigestExportService.LegalHoldCommand(stableIdempotencyKey, request == null || request.retentionUntil() == null ? null : Instant.parse(request.retentionUntil()), request == null ? null : request.evidenceScope(), request == null ? null : request.reason()), correlationId);
      return HttpResponses.ok(DigestExportApiResponse.from(result));
    });
  }

  @Post("/digest-export/ediscovery-exports")
  public HttpResponse requestEdiscoveryExport(EnterpriseExportApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.digestExportService().requestEdiscoveryExport(actor, new DigestExportService.EnterpriseExportCommand(stableIdempotencyKey, request == null ? null : request.redactionProfile(), request == null ? null : request.exportFormat(), request == null ? null : request.evidenceScope(), request == null ? null : request.reason()), correlationId);
      return HttpResponses.ok(DigestExportApiResponse.from(result));
    });
  }

  @Post("/digest-export/siem-exports")
  public HttpResponse requestSiemExport(SiemExportApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.digestExportService().requestSiemExport(actor, new DigestExportService.SiemExportCommand(stableIdempotencyKey, request == null ? null : request.evidenceScope(), request != null && request.productionDeliveryRequested()), correlationId);
      return HttpResponses.ok(DigestExportApiResponse.from(result));
    });
  }

  @Post("/digest-export/compliance-reports")
  public HttpResponse requestComplianceReport(EnterpriseExportApiRequest request) {
    var stableIdempotencyKey = idempotencyKey(request == null ? null : request.idempotencyKey());
    if (stableIdempotencyKey == null) return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var result = StarterSecurityComponents.digestExportService().requestComplianceReport(actor, new DigestExportService.EnterpriseExportCommand(stableIdempotencyKey, request == null ? null : request.redactionProfile(), request == null ? null : request.exportFormat(), request == null ? null : request.evidenceScope(), request == null ? null : request.reason()), correlationId);
      return HttpResponses.ok(DigestExportApiResponse.from(result));
    });
  }

  @Get("/audit-events")
  public HttpResponse auditEvents() {
    var limit = requestContext().queryParams().getInteger("limit").orElse(50);
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var events = new AdminAuditView(StarterSecurityComponents.userAdminService())
          .list(actor, limit, correlationId)
          .stream()
          .map(AdminAuditEventResponse::from)
          .toList();
      return HttpResponses.ok(new AdminAuditEventsResponse(events, correlationId));
    });
  }

  private HttpResponse authorized(AuthorizedCall call) {
    try {
      var identity = WorkosIdentityResolver.fromClaims(requestContext().getJwtClaims());
      var selectedContextId = requestContext().requestHeader("X-Selected-Context-Id")
          .or(() -> requestContext().requestHeader("X-Selected-Membership-Id"))
          .map(header -> header.value())
          .or(() -> requestContext().queryParams().getString("selectedContextId"))
          .orElse(null);
      var correlationId = requestContext().requestHeader("X-Correlation-Id").map(header -> header.value()).orElse("api-admin");
      return call.invoke(identity, selectedContextId, correlationId);
    } catch (AuthorizationException error) {
      if (error.httpStatus() == 400) return HttpResponses.badRequest(error.reasonCode());
      if (error.httpStatus() == 401) throw unauthorized(error.reasonCode());
      if (error.httpStatus() == 404) throw notFound();
      throw forbidden(error.reasonCode());
    }
  }

  private static String requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new AuthorizationException(400, "validation:" + field);
    }
    return value;
  }

  private static String textOr(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private static CustomerSummaryApiResponse fromCustomerSummary(ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerSummary summary) {
    return new CustomerSummaryApiResponse(summary.customerId(), summary.customerName(), summary.status(), summary.traceRefs());
  }

  private static CustomerDetailPayload fromCustomerDetail(ai.first.application.coreapp.useradmin.TenantCustomerAdminService.CustomerDetail detail) {
    return new CustomerDetailPayload(fromCustomerSummary(detail.customer()), detail.safeBoundaryNotice(), detail.visibleActions(), List.of(), detail.traceRefs(), detail.correlationId(), CUSTOMER_REDACTIONS);
  }

  private String idempotencyKey(String bodyValue) {
    var key = bodyValue == null || bodyValue.isBlank()
        ? requestContext().requestHeader("X-Idempotency-Key").map(header -> header.value()).orElse(null)
        : bodyValue;
    return key == null || key.isBlank() ? null : key;
  }

  private static List<FoundationRole> rolesOrDefault(List<String> roles) {
    if (roles == null || roles.isEmpty()) {
      return List.of(FoundationRole.TENANT_EMPLOYEE);
    }
    return roles.stream().map(FoundationRole::valueOf).toList();
  }

  private interface AuthorizedCall {
    HttpResponse invoke(WorkosIdentity identity, String selectedContextId, String correlationId);
  }

  public record SelectedAdminScope(String scopeType, String tenantId, String customerId, String selectedContextId) {}
  public record UserAdminDashboardCounts(int visibleUsers, long pendingInvitations, long deliveryFailures, int recentAuditEvents) {}
  public record UserAdminDashboardPayload(SelectedAdminScope selectedScope, UserAdminDashboardCounts counts, List<InvitationApiResponse> invitationQueue, List<AdminUserResponse> recentUsers, List<AdminAuditEventResponse> recentAuditEvents, List<String> visibleActions, List<String> traceIds, String correlationId) {}
  public record UserAdminUserAccountPayload(AdminUserResponse account, List<InvitationApiResponse> invitationHistory, List<AdminAuditEventResponse> auditEvents, List<String> visibleActions, List<String> redactions, List<String> traceIds, String correlationId) {}
  public record AdminUsersResponse(List<AdminUserResponse> users, String correlationId) {}
  public record OrganizationSummaryApiResponse(String organizationId, String organizationName, String status, List<String> traceRefs) {
    static OrganizationSummaryApiResponse from(SaasOwnerOrganizationAdminService.OrganizationSummary organization) {
      return new OrganizationSummaryApiResponse(organization.organizationId(), organization.organizationName(), organization.status(), organization.traceRefs());
    }
  }
  public record OrganizationListPayload(List<OrganizationSummaryApiResponse> organizations, String safeBoundaryNotice, List<String> traceRefs, String correlationId, List<String> redactions) {
    static OrganizationListPayload from(SaasOwnerOrganizationAdminService.OrganizationListResult result) {
      return new OrganizationListPayload(result.organizations().stream().map(OrganizationSummaryApiResponse::from).toList(), result.safeBoundaryNotice(), List.of(result.traceId()), result.correlationId(), ORGANIZATION_REDACTIONS);
    }
  }
  public record OrganizationDetailPayload(OrganizationSummaryApiResponse organization, String safeBoundaryNotice, List<String> visibleActions, List<AdminAuditEventResponse> recentAuditEvents, List<String> traceRefs, String correlationId, List<String> redactions) {
    static OrganizationDetailPayload from(SaasOwnerOrganizationAdminService.OrganizationDetail detail) {
      return new OrganizationDetailPayload(OrganizationSummaryApiResponse.from(detail.organization()), detail.safeBoundaryNotice(), detail.visibleActions(), List.of(), detail.traceRefs(), detail.correlationId(), ORGANIZATION_REDACTIONS);
    }
  }
  public record OrganizationActionApiResponse(String status, String message, OrganizationDetailPayload organization, List<String> traceRefs, String correlationId) {
    static OrganizationActionApiResponse from(SaasOwnerOrganizationAdminService.OrganizationActionResult result) {
      return new OrganizationActionApiResponse(result.status(), result.message(), OrganizationDetailPayload.from(result.organization()), List.of(result.traceId()), result.correlationId());
    }
  }
  public record OrganizationCreateApiRequest(String organizationName, String idempotencyKey, String reason) {}
  public record OrganizationRenameApiRequest(String organizationName, String idempotencyKey, String reason) {}
  public record OrganizationLifecycleApiRequest(String reason, String idempotencyKey) {}
  private static final List<String> ORGANIZATION_REDACTIONS = List.of("tenant-app-data-redacted", "provider-secrets-redacted", "billing-authority-redacted", "support-access-internals-redacted", "hidden-counts-redacted");
  private static final List<String> ADMIN_SUBJECT_REDACTIONS = List.of("raw-provider-ids-redacted", "raw-invitation-token-redacted", "tenant-customer-data-redacted", "hidden-counts-redacted");
  private static final String CUSTOMER_BOUNDARY_NOTICE = "Customer administration is scoped to the selected Organization/Tenant; sibling-customer facts, tenant application data, provider secrets, and hidden counts are omitted.";
  private static final List<String> CUSTOMER_REDACTIONS = List.of("sibling-customers-redacted", "tenant-app-data-redacted", "provider-secrets-redacted", "hidden-counts-redacted");
  public record AdminSubjectSummary(String accountId, String invitationId, String displayName, String email, String scopeType, String tenantId, String customerId, List<String> roles, String status, String invitationStatus, String deliveryStatus, boolean lastAdminRisk, List<String> visibleActions, List<String> traceRefs) {
    static AdminSubjectSummary from(UserDirectoryRow row) {
      return new AdminSubjectSummary(row.accountId(), null, row.displayName(), row.accountId(), row.scopeType().name().toLowerCase(), row.tenantId(), row.customerId(), row.roles().stream().map(Enum::name).toList(), row.status().name().toLowerCase(), null, null, false, List.of("read", "role-preview", "lifecycle"), List.of("trace-admin-subject-" + row.membershipId()));
    }
  }
  public record SaasOwnerAdminListPayload(List<AdminSubjectSummary> admins, List<AdminSubjectSummary> invitations, List<String> traceRefs, String correlationId, List<String> redaction) {}
  public record OrganizationAdminListPayload(OrganizationSummaryApiResponse organization, List<AdminSubjectSummary> admins, List<AdminSubjectSummary> invitations, List<String> traceRefs, String correlationId, List<String> redaction) {}
  public record CustomerSummaryApiResponse(String customerId, String customerName, String status, List<String> traceRefs) {}
  public record CustomerListPayload(List<CustomerSummaryApiResponse> customers, String safeBoundaryNotice, List<String> traceRefs, String correlationId, List<String> redaction) {}
  public record CustomerDetailPayload(CustomerSummaryApiResponse customer, String safeBoundaryNotice, List<String> visibleActions, List<AdminAuditEventResponse> recentAuditEvents, List<String> traceRefs, String correlationId, List<String> redaction) {}
  public record CustomerActionApiResponse(String status, String message, CustomerDetailPayload customer, List<String> traceRefs, String correlationId) {}
  public record CustomerCreateApiRequest(String customerName, String idempotencyKey, String reason) {}
  public record CustomerRenameApiRequest(String customerName, String idempotencyKey, String reason) {}
  public record CustomerLifecycleApiRequest(String reason, String idempotencyKey) {}
  public record CustomerAdminListPayload(CustomerSummaryApiResponse customer, List<AdminSubjectSummary> admins, List<AdminSubjectSummary> invitations, List<String> traceRefs, String correlationId, List<String> redaction) {}
  public record AdminUserResponse(String accountId, String displayName, String membershipId, List<String> roles, String status, String scopeType, String tenantId, String customerId) {
    static AdminUserResponse from(UserDirectoryRow row) {
      return new AdminUserResponse(row.accountId(), row.displayName(), row.membershipId(), row.roles().stream().map(Enum::name).toList(), row.status().name().toLowerCase(), row.scopeType().name().toLowerCase(), row.tenantId(), row.customerId());
    }
  }
  public record CreateInvitationApiRequest(String email, String displayName, List<String> roles, String idempotencyKey) {}
  public record InvitationActionApiRequest(String reason, String idempotencyKey) {}
  public record ChangeRolesApiRequest(List<String> roles, String reason, String idempotencyKey) {}
  public record ChangeMembershipStatusApiRequest(String status, String reason, String idempotencyKey) {}
  public record AccountActionApiRequest(String reason, String idempotencyKey) {}
  public record SupportAccessApiRequest(String reason, String expiresAt, String idempotencyKey) {}
  public record IdentityRelinkApiRequest(String reason, String approvalRef, String idempotencyKey) {}
  public record AccessReviewApiRequest(String reason, String idempotencyKey) {}
  public record ScimProvisioningApiRequest(String operation, String externalId, String email, String displayName, String scopeType, String tenantId, String customerId, List<String> roles, String reason, String idempotencyKey) {}
  public record SsoValidationApiRequest(String domain, String issuer, String metadataUrl, boolean productionRequested) {}
  public record EnterpriseIdentityStatusApiResponse(String tenantId, String customerId, boolean workosAuthKitBoundaryPreserved, boolean scimFoundationEnabled, boolean productionScimConfigured, boolean productionSsoConfigured, String productionReadiness, List<String> providerLimits, List<String> requiredSecretNames, String traceId, String correlationId) {
    static EnterpriseIdentityStatusApiResponse from(EnterpriseIdentityProviderStatus status, String correlationId) {
      return new EnterpriseIdentityStatusApiResponse(status.tenantId(), status.customerId(), status.workosAuthKitBoundaryPreserved(), status.scimFoundationEnabled(), status.productionScimConfigured(), status.productionSsoConfigured(), status.productionReadiness(), status.providerLimits(), status.requiredSecretNames(), status.traceId(), correlationId);
    }
  }
  public record ScimProvisioningApiResponse(String status, String message, String operation, String externalId, String accountId, String scopeType, String tenantId, String customerId, List<String> roles, boolean wouldMutateLocalAuthorization, boolean productionProviderConfigured, String providerDeliveryState, String traceId, String correlationId) {
    static ScimProvisioningApiResponse from(ScimProvisioningResult result, String correlationId) {
      return new ScimProvisioningApiResponse(result.status(), result.message(), result.operation(), result.externalId(), result.accountId(), result.scopeType().name().toLowerCase(), result.tenantId(), result.customerId(), result.roles().stream().map(Enum::name).toList(), result.wouldMutateLocalAuthorization(), result.productionProviderConfigured(), result.providerDeliveryState(), result.traceId(), correlationId);
    }
  }
  public record SsoValidationApiResponse(String status, String message, String tenantId, String domain, String issuer, boolean productionRequested, boolean productionProviderConfigured, List<String> missingSecretNames, String traceId, String correlationId) {
    static SsoValidationApiResponse from(SsoConfigurationValidation result, String correlationId) {
      return new SsoValidationApiResponse(result.status(), result.message(), result.tenantId(), result.domain(), result.issuer(), result.productionRequested(), result.productionProviderConfigured(), result.missingSecretNames(), result.traceId(), correlationId);
    }
  }
  public record DigestCommandApiRequest(String idempotencyKey, String scheduledFor, String redactionProfile, String evidenceScope) {}
  public record ExportCommandApiRequest(String idempotencyKey, String redactionProfile, String exportFormat, boolean sensitiveApprovalRequired, String evidenceScope) {}
  public record ExportApprovalApiRequest(String reason) {}
  public record LegalHoldApiRequest(String idempotencyKey, String retentionUntil, String evidenceScope, String reason) {}
  public record EnterpriseExportApiRequest(String idempotencyKey, String redactionProfile, String exportFormat, String evidenceScope, String reason) {}
  public record SiemExportApiRequest(String idempotencyKey, String evidenceScope, boolean productionDeliveryRequested) {}
  public record DigestExportApiResponses(List<DigestExportApiResponse> requests, String correlationId) {}
  public record DigestExportApiResponse(String requestId, String requestType, String status, String redactionProfile, String exportFormat, boolean sensitiveApprovalRequired, String scheduledFor, String evidenceScope, String resultUri, String safeSummary, String blockerCode, List<String> traceIds, String updatedAt) {
    static DigestExportApiResponse from(DigestExportRequest request) {
      return new DigestExportApiResponse(request.requestId(), request.requestType().name().toLowerCase(), request.status().name().toLowerCase(), request.redactionProfile().name().toLowerCase(), request.exportFormat().name().toLowerCase(), request.sensitiveApprovalRequired(), request.scheduledFor() == null ? null : request.scheduledFor().toString(), request.evidenceScope(), request.resultUri(), request.safeSummary(), request.blockerCode(), request.traceIds(), request.updatedAt().toString());
    }
  }
  public record InvitationsApiResponse(List<InvitationApiResponse> invitations, String correlationId) {}
  public record InvitationHistoryApiResponses(List<InvitationHistoryApiResponse> history, String correlationId) {}
  public record InvitationHistoryApiResponse(String factId, String invitationId, String eventType, String email, String status, String deliveryStatus, int deliveryAttempts, int resendCount, String actorAccountId, String result, String reasonCode, String deliveryAttemptId, String occurredAt, String correlationId) {
    static InvitationHistoryApiResponse from(ai.first.application.foundation.invitation.InvitationView.InvitationHistoryRow row) {
      return new InvitationHistoryApiResponse(row.factId(), row.invitationId(), row.eventType(), row.targetEmail(), row.invitationStatus().name().toLowerCase(), row.deliveryStatus().name().toLowerCase(), row.deliveryAttempts(), row.resendCount(), row.actorAccountId(), row.result(), row.reasonCode(), row.deliveryAttemptId(), row.occurredAt().toString(), row.correlationId());
    }
  }
  public record InvitationApiResponse(String invitationId, String email, String status, String deliveryStatus, int deliveryAttempts, int resendCount, boolean canResend, boolean canRevoke, String expiresAt, String correlationId) {
    static InvitationApiResponse from(InvitationRow invite) {
      return new InvitationApiResponse(invite.invitationId(), invite.targetEmail(), invite.status().name().toLowerCase(), invite.deliveryStatus().name().toLowerCase(), invite.deliveryAttempts(), invite.resendCount(), invite.canResend(), invite.canRevoke(), invite.expiresAt().toString(), null);
    }

    static InvitationApiResponse from(ai.first.domain.foundation.invitation.Invitation invite, String correlationId) {
      return new InvitationApiResponse(invite.invitationId(), invite.normalizedEmail(), invite.status().name().toLowerCase(), invite.deliveryStatus().name().toLowerCase(), invite.deliveryAttempts(), invite.resendCount(), invite.resendable(), !invite.terminal(), invite.expiresAt().toString(), correlationId);
    }
  }
  public record MembershipActionApiResponse(String status, String message, String membershipId, String accountId, List<String> roles, String membershipStatus, String traceId, String correlationId) {}
  public record RoleChangePreviewApiResponse(boolean allowed, boolean noOp, String message, String traceId, List<String> capabilityDelta, List<String> affectedWorkstreams, List<String> policyHints, String lastAdminImpact, String correlationId) {
    static RoleChangePreviewApiResponse from(RoleChangePreview preview, String correlationId) {
      return new RoleChangePreviewApiResponse(preview.allowed(), preview.noOp(), preview.message(), preview.traceId(), preview.capabilityDelta(), preview.affectedWorkstreams(), preview.policyHints(), preview.lastAdminImpact(), correlationId);
    }
  }
  public record AccountActionApiResponse(String status, String accountId, String accountStatus, List<String> traceIds, String correlationId) {}
  public record SupportAccessApiResponse(String status, String membershipId, String accountId, boolean supportAccess, String expiresAt, List<String> traceIds, String correlationId) {}
  public record IdentityRelinkApiResponse(String status, String message, String accountId, String recoveryId, String lifecycleStatus, String traceId, List<String> evidenceRefs, List<String> redactions, String correlationId) {
    static IdentityRelinkApiResponse from(UserAdminService.IdentityRelinkResult result, String correlationId) {
      return new IdentityRelinkApiResponse(result.status(), result.message(), result.accountId(), result.recoveryId(), result.lifecycleStatus(), result.traceId(), result.evidenceRefs(), result.redactions(), correlationId);
    }
  }
  public record AccessReviewApiResponse(String taskId, String status, int progressPercent, String summary, String blockerCode, List<String> evidenceRefs, List<String> recommendationRefs, List<String> traceIds, String correlationId) {
    static AccessReviewApiResponse from(AccessReviewTask task, String correlationId) {
      return new AccessReviewApiResponse(task.taskId(), task.status().name().toLowerCase(), task.progressPercent(), task.summary(), task.blockerCode(), task.evidenceRefs(), task.recommendationRefs(), task.traceIds(), correlationId);
    }
  }
  public record AdminAuditEventsResponse(List<AdminAuditEventResponse> events, String correlationId) {}
  public record AdminAuditEventResponse(String eventId, String occurredAt, String correlationId, String actorAccountId, String actionType, String result, String reasonCode, String tenantId, String customerId, String targetAccountId, String targetMembershipId, String evidenceSummary, String dataClassification, String redactionSummary) {
    static AdminAuditEventResponse from(AdminAuditRow row) {
      return new AdminAuditEventResponse(row.auditEventId(), row.occurredAt().toString(), row.correlationId(), row.actorAccountId(), row.actionType(), row.result().name().toLowerCase(), row.reasonCode(), row.tenantId(), row.customerId(), row.targetAccountId(), row.targetMembershipId(), row.evidenceSummary(), row.dataClassification(), row.redactionSummary());
    }
  }
}
