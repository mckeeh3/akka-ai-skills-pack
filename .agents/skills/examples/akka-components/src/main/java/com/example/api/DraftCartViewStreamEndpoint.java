package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import com.example.application.DraftCartsByCheckedOutView;
import java.time.Instant;

/**
 * HTTP endpoint example that streams view query results and updates as SSE.
 */
@HttpEndpoint("/view-streams")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class DraftCartViewStreamEndpoint extends AbstractHttpEndpoint {

  private final ComponentClient componentClient;

  public DraftCartViewStreamEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Get("/draft-carts/checked-out/{checkedOut}")
  public HttpResponse checkedOutDraftCarts(boolean checkedOut) {
    var source =
        componentClient
            .forView()
            .stream(DraftCartsByCheckedOutView::continuousCarts)
            .entriesSource(
                new DraftCartsByCheckedOutView.FindByCheckedOut(checkedOut),
                requestContext().lastSeenSseEventId().map(Instant::parse));

    return HttpResponses.serverSentEventsForView(source);
  }
}
