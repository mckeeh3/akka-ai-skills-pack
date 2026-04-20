package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.annotations.Component;

/** Focused agent example that loads its system prompt from the built-in PromptTemplate entity. */
@Component(id = "template-backed-activity-agent")
public class TemplateBackedActivityAgent extends Agent {

  public static final String PROMPT_TEMPLATE_ID = "activity-agent-prompt";

  public Effect<String> suggest(String message) {
    return effects()
        .systemMessageFromTemplate(PROMPT_TEMPLATE_ID)
        .userMessage(message)
        .thenReply();
  }
}
