package ai.first.api.workstream;

import ai.first.domain.foundation.identity.Membership;
import akka.NotUsed;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import akka.stream.javadsl.Source;
import static akka.javasdk.http.HttpException.forbidden;
import static akka.javasdk.http.HttpException.notFound;
import static akka.javasdk.http.HttpException.unauthorized;
import ai.first.application.foundation.workstream.AkkaWorkstreamLogRepository;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.invitation.InvitationService.AcceptInvitationRequest;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.application.foundation.identity.WorkosIdentityResolver;
import ai.first.application.security.WorkstreamService;
import ai.first.application.security.WorkstreamService.CapabilityActionRequest;
import ai.first.application.security.WorkstreamService.WorkstreamMessageRequest;
import ai.first.application.security.WorkstreamService.WorkstreamShellRequest;
import ai.first.domain.foundation.identity.WorkosIdentity;
import ai.first.application.foundation.invitation.InvitationService;

/** Workstream shell API endpoints for User Admin structured surfaces and capability actions. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/workstream")
public class WorkstreamEndpoint extends AbstractHttpEndpoint {
  private final WorkstreamService workstreamService;

  public WorkstreamEndpoint(ComponentClient componentClient) {
    this.workstreamService = StarterSecurityComponents.workstreamService(componentClient, new AkkaWorkstreamLogRepository(componentClient));
  }

  @Get("/bootstrap")
  public HttpResponse bootstrap() {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.bootstrap(identity, selectedContextId, correlationId)));
  }

  @Get("/functional-agents")
  public HttpResponse functionalAgents() {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.functionalAgents(identity, selectedContextId, correlationId)));
  }

  @Get("/items")
  public HttpResponse items() {
    var functionalAgentId = requestContext().queryParams().getString("functionalAgentId").orElse(null);
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.items(identity, selectedContextId, functionalAgentId, correlationId)));
  }

  @Get("/surfaces/{surfaceId}")
  public HttpResponse surface(String surfaceId) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.surface(identity, selectedContextId, surfaceId, correlationId)));
  }

  @Post("/actions")
  public HttpResponse action(CapabilityActionRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.runAction(identity, selectedContextId, request)));
  }

  @Post("/shell-requests")
  public HttpResponse shellRequest(WorkstreamShellRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.runShellRequest(identity, selectedContextId, request)));
  }

  @Post("/messages")
  public HttpResponse message(WorkstreamMessageRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(workstreamService.submitMessage(identity, selectedContextId, request, correlationId)));
  }

  @Post("/invitations/accept")
  public HttpResponse acceptInvitation(AcceptInvitationRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(StarterSecurityComponents.invitationService().acceptForBrowser(identity, request, correlationId)));
  }

  @Get("/events")
  public HttpResponse events() {
    var functionalAgentId = requestContext().queryParams().getString("functionalAgentId").orElse(null);
    var lastEventId = requestContext().lastSeenSseEventId()
        .or(() -> requestContext().queryParams().getString("lastEventId"))
        .orElse(null);
    return authorized((identity, selectedContextId, correlationId) -> {
      Source<ai.first.application.security.WorkstreamService.WorkstreamEvent, NotUsed> source =
          Source.from(workstreamService.events(identity, selectedContextId, functionalAgentId, lastEventId, correlationId));
      return HttpResponses.serverSentEvents(source, event -> event.eventId(), event -> event.eventType());
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
      var correlationId = requestContext().requestHeader("X-Correlation-Id").map(header -> header.value()).orElse("api-workstream");
      return call.invoke(identity, selectedContextId, correlationId);
    } catch (AuthorizationException error) {
      if (error.httpStatus() == 401) throw unauthorized(error.reasonCode());
      if (error.httpStatus() == 404) throw notFound();
      throw forbidden(error.reasonCode());
    }
  }

  private interface AuthorizedCall {
    HttpResponse invoke(WorkosIdentity identity, String selectedContextId, String correlationId);
  }
}
