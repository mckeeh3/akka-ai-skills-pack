package com.example.api.security;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import com.example.application.security.LocalAccountEntity;
import com.example.domain.security.AdminAuditEntry;
import com.example.domain.security.LocalAccount;
import com.example.domain.security.RoleAssignment;
import com.example.domain.security.UserProfile;
import com.example.security.AuthorizationService;
import com.example.security.InvitationEmailSender;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** JWT-protected local account administration APIs. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/admin/users")
public class AdminUsersEndpoint extends AbstractHttpEndpoint {

  private final ComponentClient componentClient;
  private final AuthorizationService authorization;
  private final InvitationEmailSender invitationEmailSender;

  public AdminUsersEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
    this.authorization = new AuthorizationService(componentClient);
    this.invitationEmailSender = new InvitationEmailSender();
  }

  public record InviteUserRequest(String email, String displayName, List<RoleAssignment> roles) {}

  public record ReplaceRolesRequest(List<RoleAssignment> roles) {}

  public record UsersResponse(List<UserResponse> users) {}

  public record UserActionResponse(UserResponse user, String auditId) {}

  public record UserResponse(
      String userId,
      String workosUserId,
      String email,
      String displayName,
      String status,
      List<RoleAssignment> roles) {
    static UserResponse from(LocalAccount.State state) {
      return new UserResponse(
          state.userId(),
          state.workosUserId(),
          state.email(),
          state.profile().displayName(),
          state.status().name(),
          state.roles());
    }
  }

  @Get
  public HttpResponse listUsers() {
    var auth = authorization.requireAuthenticated(requestContext());
    var ids = requestContext().queryParams().getString("userIds").orElse("");
    if (ids.isBlank()) {
      return HttpResponses.ok(new UsersResponse(List.of()));
    }
    var tenantId = requestContext().queryParams().getString("tenantId").orElse("");
    var customerId = requestContext().queryParams().getString("customerId").orElse("");
    var users =
        Arrays.stream(ids.split(","))
            .map(AdminUsersEndpoint::normalizeEmail)
            .filter(id -> !id.isBlank())
            .map(authorization::getAccount)
            .filter(LocalAccount.State::exists)
            .filter(account -> authorization.canViewAccount(auth, account))
            .filter(account -> tenantId.isBlank() || account.roles().stream().anyMatch(role -> tenantId.equals(role.tenantId())))
            .filter(account -> customerId.isBlank() || account.roles().stream().anyMatch(role -> customerId.equals(role.customerId())))
            .map(UserResponse::from)
            .toList();
    return HttpResponses.ok(new UsersResponse(users));
  }

  @Get("/{userId}")
  public HttpResponse getUser(String userId) {
    var auth = authorization.requireAuthenticated(requestContext());
    var account = authorization.getAccount(normalizeEmail(userId));
    if (!account.exists() || !authorization.canViewAccount(auth, account)) {
      return HttpResponses.notFound("User not found");
    }
    return HttpResponses.ok(UserResponse.from(account));
  }

  @Post("/invite")
  public HttpResponse invite(InviteUserRequest request) {
    if (request == null || normalizeEmail(request.email()).isBlank()) {
      return HttpResponses.badRequest("email must not be blank");
    }
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireCanManageAccountRoles(auth, request.roles());
    var userId = normalizeEmail(request.email());
    var displayName =
        request.displayName() == null || request.displayName().isBlank()
            ? userId.substring(0, userId.indexOf('@'))
            : request.displayName().trim();

    componentClient
        .forKeyValueEntity(userId)
        .method(LocalAccountEntity::invite)
        .invoke(
            new LocalAccount.Command.Invite(
                userId, new UserProfile(displayName, userId), request.roles(), Instant.now()));
    var delivery = invitationEmailSender.sendInvitation(userId, displayName);
    var audit =
        authorization.audit(
            AdminAuditEntry.AdminAuditAction.INVITE_USER,
            auth.actorUserId(),
            userId,
            firstTenantId(request.roles()),
            firstCustomerId(request.roles()),
            Map.of(
                "email",
                userId,
                "inviteEmailStatus",
                delivery.status(),
                "inviteEmailReason",
                delivery.reason()));
    return HttpResponses.ok(new UserActionResponse(UserResponse.from(authorization.getAccount(userId)), audit.auditId()));
  }

  @Post("/{userId}/roles")
  public HttpResponse replaceRoles(String userId, ReplaceRolesRequest request) {
    if (request == null) {
      return HttpResponses.badRequest("roles request is required");
    }
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireCanManageAccountRoles(auth, request.roles());
    var normalizedUserId = normalizeEmail(userId);
    componentClient
        .forKeyValueEntity(normalizedUserId)
        .method(LocalAccountEntity::replaceRoles)
        .invoke(new LocalAccount.Command.ReplaceRoles(request.roles(), Instant.now()));
    var audit =
        authorization.audit(
            AdminAuditEntry.AdminAuditAction.REPLACE_ROLES,
            auth.actorUserId(),
            normalizedUserId,
            firstTenantId(request.roles()),
            firstCustomerId(request.roles()),
            Map.of("roleCount", Integer.toString(request.roles() == null ? 0 : request.roles().size())));
    return HttpResponses.ok(new UserActionResponse(UserResponse.from(authorization.getAccount(normalizedUserId)), audit.auditId()));
  }

  @Post("/{userId}/disable")
  public HttpResponse disable(String userId) {
    var auth = authorization.requireAuthenticated(requestContext());
    var normalizedUserId = normalizeEmail(userId);
    var target = authorization.getAccount(normalizedUserId);
    if (!target.exists() || !authorization.canViewAccount(auth, target)) {
      return HttpResponses.notFound("User not found");
    }
    authorization.requireCanManageAccountRoles(auth, target.roles());
    componentClient
        .forKeyValueEntity(normalizedUserId)
        .method(LocalAccountEntity::disable)
        .invoke(new LocalAccount.Command.Disable(Instant.now()));
    var audit =
        authorization.audit(
            AdminAuditEntry.AdminAuditAction.DISABLE_USER,
            auth.actorUserId(),
            normalizedUserId,
            firstTenantId(target.roles()),
            firstCustomerId(target.roles()),
            Map.of());
    return HttpResponses.ok(new UserActionResponse(UserResponse.from(authorization.getAccount(normalizedUserId)), audit.auditId()));
  }

  @Post("/{userId}/activate")
  public HttpResponse activate(String userId) {
    var auth = authorization.requireAuthenticated(requestContext());
    var normalizedUserId = normalizeEmail(userId);
    var target = authorization.getAccount(normalizedUserId);
    if (!target.exists() || !authorization.canViewAccount(auth, target)) {
      return HttpResponses.notFound("User not found");
    }
    authorization.requireCanManageAccountRoles(auth, target.roles());
    componentClient
        .forKeyValueEntity(normalizedUserId)
        .method(LocalAccountEntity::reactivate)
        .invoke(new LocalAccount.Command.Reactivate(Instant.now()));
    var audit =
        authorization.audit(
            AdminAuditEntry.AdminAuditAction.REACTIVATE_USER,
            auth.actorUserId(),
            normalizedUserId,
            firstTenantId(target.roles()),
            firstCustomerId(target.roles()),
            Map.of());
    return HttpResponses.ok(new UserActionResponse(UserResponse.from(authorization.getAccount(normalizedUserId)), audit.auditId()));
  }

  private static String firstTenantId(List<RoleAssignment> roles) {
    return roles == null
        ? null
        : roles.stream().map(RoleAssignment::tenantId).filter(value -> value != null && !value.isBlank()).findFirst().orElse(null);
  }

  private static String firstCustomerId(List<RoleAssignment> roles) {
    return roles == null
        ? null
        : roles.stream().map(RoleAssignment::customerId).filter(value -> value != null && !value.isBlank()).findFirst().orElse(null);
  }

  private static String normalizeEmail(String value) {
    return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
  }
}
