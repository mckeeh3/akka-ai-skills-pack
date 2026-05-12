package com.example.api;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Delete;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import com.example.application.UserAccountEntity;
import com.example.domain.UserAccount;
import com.example.domain.UserProfile;
import com.example.security.AuthorizationService;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/me")
public class MeEndpoint extends AbstractHttpEndpoint {
  private final ComponentClient componentClient;
  private final AuthorizationService authorization;

  public MeEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
    this.authorization = new AuthorizationService(componentClient);
  }

  @Get
  public UserAccount me() {
    return authorization.requireAuthenticated(requestContext()).effectiveUser();
  }

  @Put("/profile")
  public UserAccount updateProfile(UserProfile profile) {
    var auth = authorization.requireAuthenticated(requestContext());
    var updated = componentClient
      .forKeyValueEntity(auth.effectiveUserId())
      .method(UserAccountEntity::updateProfile)
      .invoke(profile);
    authorization.audit(auth.actorUserId(), auth.effectiveUserId(), "PROFILE_UPDATED", "UserAccount", auth.effectiveUserId(), null, null, requestContext(), null);
    return updated;
  }

  @Delete
  public String deleteMyAccount() {
    var auth = authorization.requireAuthenticated(requestContext());
    componentClient
      .forKeyValueEntity(auth.effectiveUserId())
      .method(UserAccountEntity::deleteAccount)
      .invoke();
    authorization.audit(auth.actorUserId(), auth.effectiveUserId(), "USER_SELF_DELETED", "UserAccount", auth.effectiveUserId(), null, null, requestContext(), null);
    return "deleted";
  }
}
