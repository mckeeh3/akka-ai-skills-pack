package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.PromptTemplate;
import akka.javasdk.testkit.TestKitSupport;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class PromptTemplateHistoryViewIntegrationTest extends TestKitSupport {

  @Test
  void viewTracksCurrentPromptAndUpdateCount() {
    componentClient
        .forEventSourcedEntity("prompt-history-1")
        .method(PromptTemplate::update)
        .invoke("First prompt version");
    componentClient
        .forEventSourcedEntity("prompt-history-1")
        .method(PromptTemplate::update)
        .invoke("Second prompt version");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(PromptTemplateHistoryView::getByDeleted)
                      .invoke(new PromptTemplateHistoryView.FindByDeleted(false));

              var row =
                  result.items().stream()
                      .filter(item -> item.templateId().equals("prompt-history-1"))
                      .findFirst()
                      .orElseThrow();

              assertEquals("Second prompt version", row.currentPrompt());
              assertEquals(2, row.updateCount());
              assertTrue(!row.deleted());
            });
  }

  @Test
  void viewTracksDeletedTemplatesAsTombstones() {
    componentClient
        .forEventSourcedEntity("prompt-history-deleted-1")
        .method(PromptTemplate::update)
        .invoke("Prompt before delete");
    componentClient
        .forEventSourcedEntity("prompt-history-deleted-1")
        .method(PromptTemplate::delete)
        .invoke();

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(PromptTemplateHistoryView::getByDeleted)
                      .invoke(new PromptTemplateHistoryView.FindByDeleted(true));

              var row =
                  result.items().stream()
                      .filter(item -> item.templateId().equals("prompt-history-deleted-1"))
                      .findFirst()
                      .orElseThrow();

              assertEquals("Prompt before delete", row.currentPrompt());
              assertEquals(1, row.updateCount());
              assertTrue(row.deleted());
            });
  }
}
