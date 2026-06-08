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
      throw forbidden(error.reasonCode());
    }
  }
}
