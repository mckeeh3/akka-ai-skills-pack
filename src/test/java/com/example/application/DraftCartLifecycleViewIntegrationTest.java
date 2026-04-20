package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventingTestKit.IncomingMessages;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.DraftCart;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class DraftCartLifecycleViewIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withKeyValueEntityIncomingMessages(DraftCartEntity.class);
  }

  @Test
  void deleteHandlerCanPerformLogicalDelete() {
    IncomingMessages draftCartUpdates = testKit.getKeyValueEntityIncomingMessages(DraftCartEntity.class);

    draftCartUpdates.publish(
        new DraftCart.State(
            "draft-lifecycle-1",
            List.of(new DraftCart.LineItem("sku-1", "Akka T-Shirt", 2)),
            false),
        "draft-lifecycle-1");
    draftCartUpdates.publishDelete("draft-lifecycle-1");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(DraftCartLifecycleView::getByDeleted)
                      .invoke(new DraftCartLifecycleView.FindByDeleted(true));

              assertEquals(1, result.items().size());
              assertEquals("draft-lifecycle-1", result.items().getFirst().cartId());
              assertTrue(result.items().getFirst().deleted());
            });
  }
}
