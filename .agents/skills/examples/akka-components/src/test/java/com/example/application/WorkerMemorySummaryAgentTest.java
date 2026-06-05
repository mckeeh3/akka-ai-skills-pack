package com.example.application;

import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import org.junit.jupiter.api.Test;

class WorkerMemorySummaryAgentTest extends TestKitSupport {

  private final TestModelProvider summaryModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(WorkerMemorySummaryAgent.class, summaryModel);
  }

  @Test
  void agentCanBeCalledWithFilteredReadOnlyMemoryConfiguration() {
    summaryModel.fixedResponse("Worker-only summary generated.");

    var answer =
        componentClient
            .forAgent()
            .inSession("worker-memory-session")
            .method(WorkerMemorySummaryAgent::summarize)
            .invoke("Summarize the worker discussion so far.");

    assertTrue(answer.contains("Worker-only summary"));
  }
}
