package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.annotations.Component;

/** Agent example that uses an Akka component class as a function tool. */
@Component(
    id = "cart-inspector-agent",
    name = "Cart Inspector Agent",
    description =
        "Answers shopping cart questions by calling the shopping cart entity as a component tool.")
public class CartInspectorAgent extends Agent {

  private static final String SYSTEM_MESSAGE =
      """
      You answer shopping cart questions.
      Use the ShoppingCartEntity_getCart tool whenever you need current cart state.
      The tool requires uniqueId, which is the shopping cart id.
      Keep the final answer concise and grounded in the tool result.
      """
          .stripIndent();

  public Effect<String> answerQuestion(String message) {
    return effects()
        .systemMessage(SYSTEM_MESSAGE)
        .tools(ShoppingCartEntity.class)
        .userMessage(message)
        .thenReply();
  }
}
