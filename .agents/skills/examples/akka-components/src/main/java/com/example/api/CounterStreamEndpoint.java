package com.example.api;

import akka.NotUsed;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import akka.stream.javadsl.Source;

/**
 * Focused HTTP endpoint example for server-sent events.
 *
 * <p>This endpoint demonstrates deterministic SSE payloads, explicit event ids, and resumable
 * streams using {@link #requestContext()}.
 */
@HttpEndpoint("/counter-stream")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class CounterStreamEndpoint extends AbstractHttpEndpoint {

  public record CounterEvent(int value, String label) {}

  @Get("/numbers")
  public HttpResponse numbers() {
    var lastSeen = requestContext().lastSeenSseEventId().map(Integer::parseInt).orElse(0);
    var start = lastSeen + 1;

    Source<CounterEvent, NotUsed> source =
        Source.range(start, start + 2).map(value -> new CounterEvent(value, "tick-" + value));

    return HttpResponses.serverSentEvents(
        source,
        event -> Integer.toString(event.value()),
        __ -> "counter");
  }
}
