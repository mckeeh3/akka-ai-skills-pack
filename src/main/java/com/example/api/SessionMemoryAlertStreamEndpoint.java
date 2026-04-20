package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import com.example.application.SessionMemoryAlertView;
import java.time.Instant;

/** SSE endpoint that streams session-memory threshold alerts for one agent component id. */
@HttpEndpoint("/agent-memory")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class SessionMemoryAlertStreamEndpoint extends AbstractHttpEndpoint {

  private final ComponentClient componentClient;

  public SessionMemoryAlertStreamEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Get("/alerts/{componentId}")
  public HttpResponse alerts(String componentId) {
    var source =
        componentClient
            .forView()
            .stream(SessionMemoryAlertView::continuousByComponent)
            .entriesSource(
                new SessionMemoryAlertView.FindByComponent(componentId),
                requestContext().lastSeenSseEventId().map(Instant::parse));

    return HttpResponses.serverSentEventsForView(source);
  }
}
