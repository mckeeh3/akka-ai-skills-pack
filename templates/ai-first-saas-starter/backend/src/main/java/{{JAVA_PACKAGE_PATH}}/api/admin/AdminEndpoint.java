package {{JAVA_BASE_PACKAGE}}.api.admin;

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
import {{JAVA_BASE_PACKAGE}}.application.security.AdminAuditView;
import {{JAVA_BASE_PACKAGE}}.application.security.AdminAuditView.AdminAuditRow;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthorizationException;
import {{JAVA_BASE_PACKAGE}}.application.security.DigestExportService;
import {{JAVA_BASE_PACKAGE}}.application.security.InvitationService;
import {{JAVA_BASE_PACKAGE}}.application.security.InvitationView.InvitationRow;
import {{JAVA_BASE_PACKAGE}}.application.security.StarterSecurityComponents;
import {{JAVA_BASE_PACKAGE}}.application.security.UserAdminService.UserDirectoryRow;
import {{JAVA_BASE_PACKAGE}}.application.security.WorkosIdentityResolver;
import {{JAVA_BASE_PACKAGE}}.domain.security.DigestExportRequest;
import {{JAVA_BASE_PACKAGE}}.domain.security.EnterpriseIdentityProviderStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.MembershipStatus;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScimProvisioningRequest;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScimProvisioningResult;
import {{JAVA_BASE_PACKAGE}}.domain.security.ScopeType;
import {{JAVA_BASE_PACKAGE}}.domain.security.SsoConfigurationValidation;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Concrete protected User Admin and audit APIs backing starter workstream surfaces. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/admin")
public class AdminEndpoint extends AbstractHttpEndpoint {

  public AdminEndpoint(ComponentClient componentClient) {
    StarterSecurityComponents.bindAkkaRuntime(componentClient);
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

  public record AdminUsersResponse(List<AdminUserResponse> users, String correlationId) {}
  public record AdminUserResponse(String accountId, String displayName, String membershipId, List<String> roles, String status, String scopeType, String tenantId, String customerId) {
    static AdminUserResponse from(UserDirectoryRow row) {
      return new AdminUserResponse(row.accountId(), row.displayName(), row.membershipId(), row.roles().stream().map(Enum::name).toList(), row.status().name().toLowerCase(), row.scopeType().name().toLowerCase(), row.tenantId(), row.customerId());
    }
  }
  public record CreateInvitationApiRequest(String email, String displayName, List<String> roles, String idempotencyKey) {}
  public record InvitationActionApiRequest(String reason, String idempotencyKey) {}
  public record ChangeRolesApiRequest(List<String> roles, String reason, String idempotencyKey) {}
  public record ChangeMembershipStatusApiRequest(String status, String reason, String idempotencyKey) {}
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
  public record DigestExportApiResponses(List<DigestExportApiResponse> requests, String correlationId) {}
  public record DigestExportApiResponse(String requestId, String requestType, String status, String redactionProfile, String exportFormat, boolean sensitiveApprovalRequired, String scheduledFor, String evidenceScope, String resultUri, String safeSummary, List<String> traceIds, String updatedAt) {
    static DigestExportApiResponse from(DigestExportRequest request) {
      return new DigestExportApiResponse(request.requestId(), request.requestType().name().toLowerCase(), request.status().name().toLowerCase(), request.redactionProfile().name().toLowerCase(), request.exportFormat().name().toLowerCase(), request.sensitiveApprovalRequired(), request.scheduledFor() == null ? null : request.scheduledFor().toString(), request.evidenceScope(), request.resultUri(), request.safeSummary(), request.traceIds(), request.updatedAt().toString());
    }
  }
  public record InvitationsApiResponse(List<InvitationApiResponse> invitations, String correlationId) {}
  public record InvitationHistoryApiResponses(List<InvitationHistoryApiResponse> history, String correlationId) {}
  public record InvitationHistoryApiResponse(String factId, String invitationId, String eventType, String email, String status, String deliveryStatus, int deliveryAttempts, int resendCount, String actorAccountId, String result, String reasonCode, String deliveryAttemptId, String occurredAt, String correlationId) {
    static InvitationHistoryApiResponse from({{JAVA_BASE_PACKAGE}}.application.security.InvitationView.InvitationHistoryRow row) {
      return new InvitationHistoryApiResponse(row.factId(), row.invitationId(), row.eventType(), row.targetEmail(), row.invitationStatus().name().toLowerCase(), row.deliveryStatus().name().toLowerCase(), row.deliveryAttempts(), row.resendCount(), row.actorAccountId(), row.result(), row.reasonCode(), row.deliveryAttemptId(), row.occurredAt().toString(), row.correlationId());
    }
  }
  public record InvitationApiResponse(String invitationId, String email, String status, String deliveryStatus, int deliveryAttempts, int resendCount, boolean canResend, boolean canRevoke, String expiresAt, String correlationId) {
    static InvitationApiResponse from(InvitationRow invite) {
      return new InvitationApiResponse(invite.invitationId(), invite.targetEmail(), invite.status().name().toLowerCase(), invite.deliveryStatus().name().toLowerCase(), invite.deliveryAttempts(), invite.resendCount(), invite.canResend(), invite.canRevoke(), invite.expiresAt().toString(), null);
    }

    static InvitationApiResponse from({{JAVA_BASE_PACKAGE}}.domain.security.Invitation invite, String correlationId) {
      return new InvitationApiResponse(invite.invitationId(), invite.normalizedEmail(), invite.status().name().toLowerCase(), invite.deliveryStatus().name().toLowerCase(), invite.deliveryAttempts(), invite.resendCount(), invite.resendable(), !invite.terminal(), invite.expiresAt().toString(), correlationId);
    }
  }
  public record MembershipActionApiResponse(String status, String message, String membershipId, String accountId, List<String> roles, String membershipStatus, String traceId, String correlationId) {}
  public record AdminAuditEventsResponse(List<AdminAuditEventResponse> events, String correlationId) {}
  public record AdminAuditEventResponse(String eventId, String occurredAt, String correlationId, String actorAccountId, String actionType, String result, String reasonCode, String tenantId, String customerId, String targetAccountId, String targetMembershipId, String evidenceSummary, String dataClassification, String redactionSummary) {
    static AdminAuditEventResponse from(AdminAuditRow row) {
      return new AdminAuditEventResponse(row.auditEventId(), row.occurredAt().toString(), row.correlationId(), row.actorAccountId(), row.actionType(), row.result().name().toLowerCase(), row.reasonCode(), row.tenantId(), row.customerId(), row.targetAccountId(), row.targetMembershipId(), row.evidenceSummary(), row.dataClassification(), row.redactionSummary());
    }
  }
}
