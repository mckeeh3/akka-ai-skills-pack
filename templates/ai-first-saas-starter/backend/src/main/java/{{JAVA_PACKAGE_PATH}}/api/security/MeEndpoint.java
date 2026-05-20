package {{JAVA_BASE_PACKAGE}}.api.security;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import static akka.javasdk.http.HttpException.forbidden;
import static akka.javasdk.http.HttpException.unauthorized;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthorizationException;
import {{JAVA_BASE_PACKAGE}}.application.security.StarterSecurityComponents;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;

/** JWT-protected browser bootstrap endpoint for the selected local AuthContext. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/me")
public class MeEndpoint extends AbstractHttpEndpoint {

  @Get
  public HttpResponse me() {
    try {
      var claims = requestContext().getJwtClaims();
      var identity =
          new WorkosIdentity(
              claims.subject().orElse(null),
              claims.getString("email").orElse(null),
              claims.getString("name").orElse(null));
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
