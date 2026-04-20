package com.example.application;

import akka.javasdk.agent.GuardrailContext;
import akka.javasdk.agent.TextGuardrail;

/** Focused custom guardrail example configured from application.conf. */
public class CompetitorMentionGuard implements TextGuardrail {

  private final String blockedPhrase;

  public CompetitorMentionGuard(GuardrailContext context) {
    this.blockedPhrase = context.config().getString("blocked-phrase");
  }

  @Override
  public Result evaluate(String text) {
    if (text != null && text.contains(blockedPhrase)) {
      return new Result(false, "Response mentioned blocked phrase '" + blockedPhrase + "'.");
    }
    return Result.OK;
  }
}
