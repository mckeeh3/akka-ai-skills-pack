package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import com.example.application.SessionMemoryCompactionAuditView;
import java.time.Instant;

/** SSE endpoint that streams session-memory compaction audit updates for one session. */
@HttpEndpoint("/agent-memory")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class SessionMemoryCompactionStreamEndpoint extends AbstractHttpEndpoint {

  private final ComponentClient componentClient;

  public SessionMemoryCompactionStreamEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Get("/compactions/{sessionId}")
  public HttpResponse compactions(String sessionId) {
    var source =
        componentClient
            .forView()
            .stream(SessionMemoryCompactionAuditView::continuousBySessionId)
            .entriesSource(
                new SessionMemoryCompactionAuditView.FindBySessionId(sessionId),
                requestContext().lastSeenSseEventId().map(Instant::parse));

    return HttpResponses.serverSentEventsForView(source);
  }
}
