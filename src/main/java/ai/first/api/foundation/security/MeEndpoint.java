package ai.first.api.foundation.security;

import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.identity.Membership;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import static akka.javasdk.http.HttpException.forbidden;
import static akka.javasdk.http.HttpException.unauthorized;
import ai.first.application.foundation.identity.AuthorizationException;
import java.util.List;
import java.util.Map;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.application.foundation.identity.WorkosIdentityResolver;

/** JWT-protected browser bootstrap endpoint for the selected local AuthContext. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/me")
public class MeEndpoint extends AbstractHttpEndpoint {

  @Get
  public HttpResponse me() {
    try {
      var identity = WorkosIdentityResolver.fromClaims(requestContext().getJwtClaims());
      var selectedMembershipId = requestContext().requestHeader("X-Selected-Context-Id")
          .or(() -> requestContext().requestHeader("X-Selected-Membership-Id"))
          .map(header -> header.value())
          .orElse(null);
      var correlationId = requestContext().requestHeader("X-Correlation-Id").map(header -> header.value()).orElse("api-me");
      return HttpResponses.ok(StarterSecurityComponents.meService().me(identity, selectedMembershipId, correlationId));
    } catch (AuthorizationException error) {
      if (error.httpStatus() == 401) {
        throw unauthorized(error.reasonCode());
      }
      if ("account-disabled".equals(error.reasonCode()) || "no-active-membership".equals(error.reasonCode())) {
        return HttpResponses.ok(noAccessRecovery(error.reasonCode()));
      }
      throw forbidden(error.reasonCode());
    }
  }

  private Map<String, Object> noAccessRecovery(String reasonCode) {
    return Map.of(
        "status", "no_access_recovery",
        "reasonCode", reasonCode,
        "message", "Your signed-in account was recognized, but no active application context is available. My Account can only show safe profile/session recovery guidance until an administrator restores access.",
        "accountContext", Map.of("selectedContextId", "not_available", "redactionLevel", "browser-safe"),
        "visibleFunctionalAgents", List.of(),
        "availableActions", List.of("sign_out", "contact_admin"),
        "redaction", "No tenant, customer, membership, workstream, capability, provider, or hidden context details are enumerated.",
        "traceRefs", List.of("trace-me-no-access-recovery"));
  }
}
