package com.example.domain;

import java.util.List;
import java.util.stream.Stream;

/**
 * Focused domain model for the key value TTL example.
 *
 * <p>This example is intentionally small so it can be used in documentation and by AI coding
 * assistants when generating key value entities that use {@code expireAfter(...)}.
 */
public final class ExpiringDraftCartSession {

  private ExpiringDraftCartSession() {}

  public record State(String sessionId, List<String> productIds) {

    public static State empty(String sessionId) {
      return new State(sessionId, List.of());
    }

    public State addItem(String productId) {
      return new State(sessionId, Stream.concat(productIds.stream(), Stream.of(productId)).toList());
    }
  }
}
