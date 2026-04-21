package com.example.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.agent.RemoteMcpTools;
import akka.javasdk.annotations.Component;
import java.time.Duration;

/** Agent example that uses tools from a remote MCP endpoint. */
@Component(
    id = "remote-shopping-cart-agent",
    name = "Remote Shopping Cart Agent",
    description =
        "Answers shopping cart questions by calling MCP tools hosted by a remote shopping-cart MCP endpoint.")
public class RemoteShoppingCartAgent extends Agent {

  static final String SHOPPING_MCP_SERVER = "http://empty-service/mcp";

  private static final String SYSTEM_MESSAGE =
      """
      You help with shopping cart questions.
      Use the remote MCP tool getCartSummary whenever you need current cart contents or checkout state.
      Keep the final answer short and based only on the tool result and the user question.
      """
          .stripIndent();

  public Effect<String> answerQuestion(String message) {
    return effects()
        .systemMessage(SYSTEM_MESSAGE)
        .mcpTools(
            RemoteMcpTools.fromServer(SHOPPING_MCP_SERVER)
                .withAllowedToolNames("getCartSummary")
                .withTimeout(Duration.ofSeconds(10)))
        .userMessage(message)
        .thenReply();
  }
}
