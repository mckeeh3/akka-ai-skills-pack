package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.PromptTemplate;
import akka.javasdk.testkit.TestKitSupport;
import com.example.api.PromptTemplateHistoryEndpoint;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class PromptTemplateHistoryEndpointIntegrationTest extends TestKitSupport {

  @Test
  void endpointReturnsPromptTemplateHistoryRows() {
    componentClient
        .forEventSourcedEntity("prompt-endpoint-1")
        .method(PromptTemplate::update)
        .invoke("Prompt version one");
    componentClient
        .forEventSourcedEntity("prompt-endpoint-1")
        .method(PromptTemplate::update)
        .invoke("Prompt version two");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var response =
                  httpClient
                      .GET("/agent-prompts/history/false")
                      .responseBodyAs(PromptTemplateHistoryEndpoint.PromptTemplateHistoryResponse.class)
                      .invoke();

              assertTrue(response.status().isSuccess());
              var row =
                  response.body().items().stream()
                      .filter(item -> item.templateId().equals("prompt-endpoint-1"))
                      .findFirst()
                      .orElseThrow();

              assertEquals("Prompt version two", row.currentPrompt());
              assertEquals(2, row.updateCount());
              assertTrue(!row.deleted());
            });
  }
}
