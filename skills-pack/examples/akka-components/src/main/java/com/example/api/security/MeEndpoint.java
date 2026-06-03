package com.example.api.security;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import com.example.domain.security.LocalAccount;
import com.example.domain.security.RoleAssignment;
import com.example.security.AuthorizationService;
import java.util.Comparator;
import java.util.List;

/** JWT-protected account bootstrap endpoint for authenticated browser users. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/me")
public class MeEndpoint extends AbstractHttpEndpoint {

  private final AuthorizationService authorization;

  public MeEndpoint(ComponentClient componentClient) {
    this.authorization = new AuthorizationService(componentClient);
  }

  @Get
  public HttpResponse me() {
    var auth = authorization.requireAuthenticated(requestContext());
    return HttpResponses.ok(MeResponse.from(auth.actor()));
  }

  public record MeResponse(
      String userId,
      String email,
      String displayName,
      String status,
      List<String> roles,
      List<ScopeResponse> scopes,
      List<String> capabilities) {

    static MeResponse from(LocalAccount.State account) {
      var roles =
          account.roles().stream()
              .map(role -> role.role().name())
              .distinct()
              .sorted()
              .toList();
      var scopes =
          account.roles().stream()
              .sorted(
                  Comparator.comparing((RoleAssignment role) -> role.role().name())
                      .thenComparing(role -> role.tenantId() == null ? "" : role.tenantId())
                      .thenComparing(role -> role.customerId() == null ? "" : role.customerId()))
              .map(ScopeResponse::from)
              .toList();
      return new MeResponse(
          account.userId(),
          account.email(),
          account.profile().displayName(),
          account.status().name(),
          roles,
          scopes,
          capabilities(account));
    }

    private static List<String> capabilities(LocalAccount.State account) {
      if (!account.isActive()) {
        return List.of();
      }
      if (account.isAppAdmin()) {
        return List.of("ADMIN_USERS", "ADMIN_TENANTS", "VIEW_AUDIT", "SUPERVISE_OPERATIONS");
      }
      return account.roles().stream()
          .map(
              role ->
                  switch (role.role()) {
                    case DEALER_OWNER -> "SUPERVISE_OPERATIONS";
                    case OPERATIONS_SUPERVISOR -> "REVIEW_DECISIONS";
                    case POLICY_OWNER -> "MANAGE_POLICY_PROPOSALS";
                    case AUDITOR -> "VIEW_AUDIT";
                    case CUSTOMER_ADMIN -> "ADMIN_CUSTOMER_USERS";
                    case USER -> "USE_APP";
                    case APP_ADMIN -> "ADMIN_USERS";
                  })
          .distinct()
          .sorted()
          .toList();
    }
  }

  public record ScopeResponse(String role, String tenantId, String customerId) {
    static ScopeResponse from(RoleAssignment assignment) {
      return new ScopeResponse(assignment.role().name(), assignment.tenantId(), assignment.customerId());
    }
  }
}
