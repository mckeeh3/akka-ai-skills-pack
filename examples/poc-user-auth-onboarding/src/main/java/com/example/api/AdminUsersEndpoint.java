package com.example.api;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Delete;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import com.example.application.UserAccountEntity;
import com.example.domain.RoleAssignment;
import com.example.domain.UserAccount;
import com.example.domain.UserProfile;
import com.example.security.AuthorizationService;
import java.util.List;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/admin/users")
public class AdminUsersEndpoint extends AbstractHttpEndpoint {
  private final ComponentClient componentClient;
  private final AuthorizationService authorization;

  public AdminUsersEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
    this.authorization = new AuthorizationService(componentClient);
  }

  public record InviteUserRequest(String userId, String email, UserProfile profile, List<RoleAssignment> roles) {}
  public record ActivateUserRequest(String workosUserId) {}

  @Get("/{userId}")
  public UserAccount getUser(String userId) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireAppAdmin(auth);
    return authorization.getUser(userId);
  }

  @Post
  public UserAccount invite(InviteUserRequest request) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireAppAdmin(auth);
    var user = componentClient
      .forKeyValueEntity(request.userId())
      .method(UserAccountEntity::invite)
      .invoke(new UserAccountEntity.InviteUser(request.email(), request.profile(), request.roles()));
    authorization.audit(auth.actorUserId(), auth.effectiveUserId(), "USER_INVITED", "UserAccount", request.userId(), null, null, requestContext(), null);
    return user;
  }

  @Post("/{userId}/activate")
  public UserAccount activate(String userId, ActivateUserRequest request) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireAppAdmin(auth);
    var user = componentClient
      .forKeyValueEntity(userId)
      .method(UserAccountEntity::activate)
      .invoke(new UserAccountEntity.ActivateUser(request.workosUserId()));
    authorization.audit(auth.actorUserId(), auth.effectiveUserId(), "USER_ACTIVATED", "UserAccount", userId, null, null, requestContext(), null);
    return user;
  }

  @Put("/{userId}/roles")
  public UserAccount updateRoles(String userId, List<RoleAssignment> roles) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireAppAdmin(auth);
    var user = componentClient
      .forKeyValueEntity(userId)
      .method(UserAccountEntity::updateRoles)
      .invoke(roles);
    authorization.audit(auth.actorUserId(), auth.effectiveUserId(), "USER_ROLES_UPDATED", "UserAccount", userId, null, null, requestContext(), null);
    return user;
  }

  @Post("/{userId}/disable")
  public UserAccount disable(String userId) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireAppAdmin(auth);
    var user = componentClient
      .forKeyValueEntity(userId)
      .method(UserAccountEntity::disable)
      .invoke();
    authorization.audit(auth.actorUserId(), auth.effectiveUserId(), "USER_DISABLED", "UserAccount", userId, null, null, requestContext(), null);
    return user;
  }

  @Delete("/{userId}")
  public String deleteUser(String userId) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireAppAdmin(auth);
    componentClient.forKeyValueEntity(userId).method(UserAccountEntity::deleteAccount).invoke();
    authorization.audit(auth.actorUserId(), auth.effectiveUserId(), "USER_DELETED", "UserAccount", userId, null, null, requestContext(), null);
    return "deleted";
  }
}
