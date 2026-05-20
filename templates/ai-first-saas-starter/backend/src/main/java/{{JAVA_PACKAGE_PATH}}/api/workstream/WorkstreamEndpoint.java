package {{JAVA_BASE_PACKAGE}}.api.workstream;

import akka.NotUsed;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import akka.stream.javadsl.Source;
import static akka.javasdk.http.HttpException.forbidden;
import static akka.javasdk.http.HttpException.notFound;
import static akka.javasdk.http.HttpException.unauthorized;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthorizationException;
import {{JAVA_BASE_PACKAGE}}.application.security.StarterSecurityComponents;
import {{JAVA_BASE_PACKAGE}}.application.security.WorkstreamService.CapabilityActionRequest;
import {{JAVA_BASE_PACKAGE}}.domain.security.WorkosIdentity;

/** Workstream shell API endpoints for User Admin structured surfaces and capability actions. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/workstream")
public class WorkstreamEndpoint extends AbstractHttpEndpoint {

  @Get("/bootstrap")
  public HttpResponse bootstrap() {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(StarterSecurityComponents.workstreamService().bootstrap(identity, selectedContextId, correlationId)));
  }

  @Get("/functional-agents")
  public HttpResponse functionalAgents() {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(StarterSecurityComponents.workstreamService().functionalAgents(identity, selectedContextId, correlationId)));
  }

  @Get("/items")
  public HttpResponse items() {
    var functionalAgentId = requestContext().queryParams().getString("functionalAgentId").orElse(null);
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(StarterSecurityComponents.workstreamService().items(identity, selectedContextId, functionalAgentId, correlationId)));
  }

  @Get("/surfaces/{surfaceId}")
  public HttpResponse surface(String surfaceId) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(StarterSecurityComponents.workstreamService().surface(identity, selectedContextId, surfaceId, correlationId)));
  }

  @Post("/actions")
  public HttpResponse action(CapabilityActionRequest request) {
    return authorized((identity, selectedContextId, correlationId) -> HttpResponses.ok(StarterSecurityComponents.workstreamService().runAction(identity, selectedContextId, request)));
  }

  @Get("/events")
  public HttpResponse events() {
    var functionalAgentId = requestContext().queryParams().getString("functionalAgentId").orElse(null);
    var lastEventId = requestContext().lastSeenSseEventId()
        .or(() -> requestContext().queryParams().getString("lastEventId"))
        .orElse(null);
    return authorized((identity, selectedContextId, correlationId) -> {
      Source<{{JAVA_BASE_PACKAGE}}.application.security.WorkstreamService.WorkstreamEvent, NotUsed> source =
          Source.from(StarterSecurityComponents.workstreamService().events(identity, selectedContextId, functionalAgentId, lastEventId, correlationId));
      return HttpResponses.serverSentEvents(source, event -> event.eventId(), event -> event.eventType());
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
