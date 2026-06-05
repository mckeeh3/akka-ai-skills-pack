package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.testkit.KeyValueEntityTestKit;
import com.example.domain.ExpiringDraftCartSession;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ExpiringDraftCartSessionEntityTest {

  @Test
  void addItemUpdatesStateAndSetsTtl() {
    var testKit = KeyValueEntityTestKit.of("session-ttl-1", ExpiringDraftCartSessionEntity::new);

    var result = testKit.method(ExpiringDraftCartSessionEntity::addItem).invoke("sku-1");

    assertEquals(Done.getInstance(), result.getReply());
    assertTrue(result.stateWasUpdated());
    assertEquals(Optional.of(Duration.ofDays(30)), result.getExpireAfter());
    assertEquals(
        new ExpiringDraftCartSession.State("session-ttl-1", List.of("sku-1")),
        testKit.getState());
  }

  @Test
  void addItemWithBlankProductIdReturnsErrorWithoutUpdatingState() {
    var testKit = KeyValueEntityTestKit.of("session-ttl-2", ExpiringDraftCartSessionEntity::new);

    var result = testKit.method(ExpiringDraftCartSessionEntity::addItem).invoke(" ");

    assertTrue(result.isError());
    assertEquals("productId must not be blank.", result.getError());
    assertFalse(result.stateWasUpdated());
    assertEquals(Optional.empty(), result.getExpireAfter());
  }
}
