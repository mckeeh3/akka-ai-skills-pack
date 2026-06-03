package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.SessionMemoryByComponentView;
import java.util.List;

/** HTTP endpoint for querying session-memory analytics by the last component that wrote memory. */
@HttpEndpoint("/agent-memory")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class SessionMemoryViewEndpoint {

  public record SessionMemoryItem(
      String sessionId, String lastComponentId, int messageCount, long historySizeInBytes) {}

  public record SessionMemoryResponse(List<SessionMemoryItem> sessions) {}

  private final ComponentClient componentClient;

  public SessionMemoryViewEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Get("/components/{componentId}")
  public HttpResponse getByComponent(String componentId) {
    var result =
        componentClient
            .forView()
            .method(SessionMemoryByComponentView::getByComponent)
            .invoke(new SessionMemoryByComponentView.FindByComponent(componentId));

    var sessions =
        result.sessions().stream()
            .map(
                row ->
                    new SessionMemoryItem(
                        row.sessionId(),
                        row.lastComponentId(),
                        row.messageCount(),
                        row.historySizeInBytes()))
            .toList();

    return HttpResponses.ok(new SessionMemoryResponse(sessions));
  }
}
