package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.annotations.Component;

/** Focused agent example for token streaming. */
@Component(id = "streaming-activity-agent")
public class StreamingActivityAgent extends Agent {

  private static final String SYSTEM_MESSAGE =
      """
      You are an activity recommendation agent.
      Stream a concise answer with one practical suggestion and a short explanation.
      """
          .stripIndent();

  public StreamEffect suggest(String message) {
    return streamEffects().systemMessage(SYSTEM_MESSAGE).userMessage(message).thenReply();
  }
}
