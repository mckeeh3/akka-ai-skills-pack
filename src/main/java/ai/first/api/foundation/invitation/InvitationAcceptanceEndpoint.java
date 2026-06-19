package ai.first.api.foundation.invitation;

import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.application.foundation.identity.WorkosIdentityResolver;
import ai.first.application.foundation.invitation.InvitationService.AcceptInvitationRequest;
import ai.first.application.foundation.invitation.InvitationService.InvitationAcceptanceBootstrapRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;

import static akka.javasdk.http.HttpException.forbidden;
import static akka.javasdk.http.HttpException.unauthorized;

/** Standard invitee onboarding endpoints for signed invitation acceptance. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint("/api/invitations/acceptance")
public class InvitationAcceptanceEndpoint extends AbstractHttpEndpoint {

  @Post("/bootstrap")
  public HttpResponse bootstrap(InvitationAcceptanceBootstrapRequest request) {
    return HttpResponses.ok(StarterSecurityComponents.invitationService().bootstrapForBrowser(request, correlationId()));
  }

  @Post("/complete")
  @JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
  public HttpResponse complete(AcceptInvitationRequest request) {
    try {
      var identity = WorkosIdentityResolver.fromClaims(requestContext().getJwtClaims());
      return HttpResponses.ok(StarterSecurityComponents.invitationService().acceptForBrowser(identity, request, correlationId()));
    } catch (AuthorizationException error) {
      if (error.httpStatus() == 401) throw unauthorized(error.reasonCode());
      throw forbidden(error.reasonCode());
    }
  }

  private String correlationId() {
    return requestContext().requestHeader("X-Correlation-Id").map(header -> header.value()).orElse("invitation-acceptance");
  }
}
