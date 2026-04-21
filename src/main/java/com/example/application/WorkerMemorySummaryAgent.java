package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.agent.MemoryFilter;
import akka.javasdk.agent.MemoryProvider;
import akka.javasdk.annotations.Component;

/** Agent example that reads only selected session-memory messages from worker agents. */
@Component(
    id = "worker-memory-summary-agent",
    name = "Worker Memory Summary Agent",
    description =
        "Summarizes a conversation using only worker-agent messages from shared session memory.")
public class WorkerMemorySummaryAgent extends Agent {

  private static final String SYSTEM_MESSAGE =
      """
      You summarize the conversation using only the worker-agent context available in memory.
      Ignore any missing details rather than inventing them.
      """
          .stripIndent();

  public Effect<String> summarize(String message) {
    return effects()
        .memory(
            MemoryProvider.limitedWindow()
                .readOnly(
                    MemoryFilter.includeFromAgentRole("worker")
                        .excludeFromAgentId("debug-agent")))
        .systemMessage(SYSTEM_MESSAGE)
        .userMessage(message)
        .thenReply();
  }
}
