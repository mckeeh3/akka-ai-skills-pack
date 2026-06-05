package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.agent.ModelProvider;
import akka.javasdk.annotations.Component;

/** Focused example showing model selection with ModelProvider.fromConfig(...). */
@Component(id = "configured-model-activity-agent")
public class ConfiguredModelActivityAgent extends Agent {

  static final String MODEL_CONFIG = "openai-low-temperature";

  private static final String SYSTEM_MESSAGE =
      "Respond with one concise activity suggestion and one short reason.";

  public Effect<String> suggest(String message) {
    return effects()
        .model(ModelProvider.fromConfig(MODEL_CONFIG))
        .systemMessage(SYSTEM_MESSAGE)
        .userMessage(message)
        .thenReply();
  }
}
