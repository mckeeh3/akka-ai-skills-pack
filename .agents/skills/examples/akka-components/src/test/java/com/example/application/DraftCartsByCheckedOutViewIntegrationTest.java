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

class DraftCartsByCheckedOutViewIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withKeyValueEntityIncomingMessages(DraftCartEntity.class);
  }

  @Test
  void shouldQueryCheckedOutDraftCartsWithPagination() {
    IncomingMessages draftCartUpdates = testKit.getKeyValueEntityIncomingMessages(DraftCartEntity.class);

    draftCartUpdates.publish(
        new DraftCart.State(
            "draft-view-1",
            List.of(new DraftCart.LineItem("akka-tshirt", "Akka T-Shirt", 2)),
            true),
        "draft-view-1");
    draftCartUpdates.publish(
        new DraftCart.State(
            "draft-view-2",
            List.of(
                new DraftCart.LineItem("akka-socks", "Akka Socks", 1),
                new DraftCart.LineItem("akka-cap", "Akka Cap", 1)),
            true),
        "draft-view-2");
    draftCartUpdates.publish(
        new DraftCart.State(
            "draft-view-3",
            List.of(new DraftCart.LineItem("akka-mug", "Akka Mug", 1)),
            false),
        "draft-view-3");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var page =
                  componentClient
                      .forView()
                      .method(DraftCartsByCheckedOutView::getCartsPage)
                      .invoke(new DraftCartsByCheckedOutView.FindPage(true, 0, 1));

              assertEquals(1, page.carts().size());
              assertEquals("draft-view-1", page.carts().getFirst().cartId());
              assertTrue(page.carts().getFirst().checkedOut());
            });
  }

  @Test
  void updatedDraftCartAppearsInCheckedOutQuery() {
    IncomingMessages draftCartUpdates = testKit.getKeyValueEntityIncomingMessages(DraftCartEntity.class);

    draftCartUpdates.publish(
        new DraftCart.State(
            "draft-view-update",
            List.of(new DraftCart.LineItem("akka-mug", "Akka Mug", 1)),
            false),
        "draft-view-update");
    draftCartUpdates.publish(
        new DraftCart.State(
            "draft-view-update",
            List.of(new DraftCart.LineItem("akka-mug", "Akka Mug", 1)),
            true),
        "draft-view-update");

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var result =
                  componentClient
                      .forView()
                      .method(DraftCartsByCheckedOutView::getCarts)
                      .invoke(new DraftCartsByCheckedOutView.FindByCheckedOut(true));

              assertEquals(1, result.carts().size());
              assertEquals("draft-view-update", result.carts().getFirst().cartId());
              assertEquals(1, result.carts().getFirst().itemCount());
            });
  }
}
