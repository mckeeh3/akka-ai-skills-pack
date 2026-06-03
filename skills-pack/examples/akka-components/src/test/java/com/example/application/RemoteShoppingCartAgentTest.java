package com.example.application;

import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import akka.javasdk.testkit.TestModelProvider.AiResponse;
import akka.javasdk.testkit.TestModelProvider.ToolInvocationRequest;
import com.example.domain.ShoppingCart;
import org.junit.jupiter.api.Test;

class RemoteShoppingCartAgentTest extends TestKitSupport {

  private final TestModelProvider remoteCartModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(RemoteShoppingCartAgent.class, remoteCartModel);
  }

  @Test
  void agentCanCallRemoteMcpTool() {
    componentClient
        .forEventSourcedEntity("remote-cart-1")
        .method(ShoppingCartEntity::addItem)
        .invoke(new ShoppingCart.Command.AddItem("sku-2", "Coffee", 1));

    remoteCartModel
        .whenMessage(message -> message.contains("remote-cart-1"))
        .reply(new ToolInvocationRequest("getCartSummary", "{\"cartId\":\"remote-cart-1\"}"));
    remoteCartModel
        .whenToolResult(result -> result.name().equals("getCartSummary"))
        .thenReply(
            result ->
                new AiResponse(
                    result.content().contains("\"Coffee\"")
                        ? "Remote cart remote-cart-1 contains Coffee and is ready for checkout help."
                        : "Remote cart details unavailable."));

    var answer =
        componentClient
            .forAgent()
            .inSession("remote-cart-session")
            .method(RemoteShoppingCartAgent::answerQuestion)
            .invoke("Summarize cart remote-cart-1 before checkout.");

    assertTrue(answer.contains("Coffee"));
    assertTrue(answer.contains("checkout"));
  }
}
