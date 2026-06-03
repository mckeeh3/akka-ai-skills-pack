package com.example.api;

import akka.NotUsed;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.WebSocket;
import akka.stream.javadsl.Flow;

/**
 * Focused HTTP endpoint example for WebSocket text message handling.
 */
@HttpEndpoint
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class PingWebSocketEndpoint {

  @WebSocket("/websockets/ping")
  public Flow<String, String, NotUsed> ping() {
    return Flow.of(String.class).map(message -> message.equals("ping") ? "pong" : "echo:" + message);
  }
}
