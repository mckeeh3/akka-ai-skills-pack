package {{JAVA_BASE_PACKAGE}}.api.admin;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import static akka.javasdk.http.HttpException.forbidden;
import static akka.javasdk.http.HttpException.notFound;
import static akka.javasdk.http.HttpException.unauthorized;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthorizationException;
import {{JAVA_BASE_PACKAGE}}.application.security.InvitationService;
import {{JAVA_BASE_PACKAGE}}.application.security.StarterSecurityComponents;
import {{JAVA_BASE_PACKAGE}}.application.security.UserAdminService.UserDirectoryRow;
import {{JAVA_BASE_PACKAGE}}.domain.security.AdminAuditEvent;
import {{JAVA_BASE_PACKAGE}}.domain.security.FoundationRole;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Concrete protected User Admin and audit APIs backing starter workstream surfaces. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/admin")
public class AdminEndpoint extends AbstractHttpEndpoint {

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

  @Post("/invitations")
  public HttpResponse createInvitation(CreateInvitationApiRequest request) {
    var idempotencyKey = request == null ? null : request.idempotencyKey();
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      idempotencyKey = requestContext().requestHeader("X-Idempotency-Key").map(header -> header.value()).orElse(null);
    }
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      return HttpResponses.badRequest("X-Idempotency-Key or idempotencyKey is required");
    }
    var stableIdempotencyKey = idempotencyKey;
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
      return HttpResponses.ok(new InvitationApiResponse(invite.invitationId(), invite.normalizedEmail(), invite.status().name().toLowerCase(), invite.deliveryStatus().name().toLowerCase(), correlationId));
    });
  }

  @Get("/audit-events")
  public HttpResponse auditEvents() {
    var limit = requestContext().queryParams().getInteger("limit").orElse(50);
    return authorized((identity, selectedContextId, correlationId) -> {
      var actor = StarterSecurityComponents.authContextResolver().resolveMe(identity, selectedContextId, correlationId);
      var events = StarterSecurityComponents.userAdminService().auditEvents(actor, Math.max(1, Math.min(limit, 100)), correlationId).stream()
          .map(AdminAuditEventResponse::from)
          .toList();
      return HttpResponses.ok(new AdminAuditEventsResponse(events, correlationId));
    });
  }

  private HttpResponse authorized(AuthorizedCall call) {
    try {
      var claims = requestContext().getJwtClaims();
      var identity = new WorkosIdentity(claims.subject().orElse(null), claims.getString("email").orElse(null), claims.getString("name").orElse(null));
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
  public record InvitationApiResponse(String invitationId, String email, String status, String deliveryStatus, String correlationId) {}
  public record AdminAuditEventsResponse(List<AdminAuditEventResponse> events, String correlationId) {}
  public record AdminAuditEventResponse(String eventId, String occurredAt, String correlationId, String actorAccountId, String actionType, String result, String reasonCode, String tenantId, String customerId, String targetAccountId, String targetMembershipId) {
    static AdminAuditEventResponse from(AdminAuditEvent event) {
      return new AdminAuditEventResponse(event.auditEventId(), event.timestamp().toString(), event.correlationId(), event.actorAccountId(), event.actionType(), event.result().name().toLowerCase(), event.reasonCode(), event.tenantId(), event.customerId(), event.targetAccountId(), event.targetMembershipId());
    }
  }
}
