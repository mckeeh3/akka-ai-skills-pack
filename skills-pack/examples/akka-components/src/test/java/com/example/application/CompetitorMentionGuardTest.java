package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.agent.GuardrailContext;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

class CompetitorMentionGuardTest {

  @Test
  void guardBlocksConfiguredPhrase() {
    var guard = new CompetitorMentionGuard(context("blocked-phrase = \"CompetitorCo\""));

    var result = guard.evaluate("This answer mentions CompetitorCo by name.");

    assertFalse(result.passed());
    assertTrue(result.explanation().contains("CompetitorCo"));
  }

  @Test
  void guardAllowsCleanText() {
    var guard = new CompetitorMentionGuard(context("blocked-phrase = \"CompetitorCo\""));

    var result = guard.evaluate("This answer stays focused on our own service.");

    assertTrue(result.passed());
    assertEquals("", result.explanation());
  }

  private static GuardrailContext context(String config) {
    return new GuardrailContext() {
      @Override
      public String name() {
        return "competitor mention guard";
      }

      @Override
      public com.typesafe.config.Config config() {
        return ConfigFactory.parseString(config);
      }
    };
  }
}
