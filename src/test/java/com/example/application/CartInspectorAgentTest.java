package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import akka.javasdk.testkit.TestModelProvider.AiResponse;
import akka.javasdk.testkit.TestModelProvider.ToolInvocationRequest;
import com.example.domain.ShoppingCart;
import org.junit.jupiter.api.Test;

class CartInspectorAgentTest extends TestKitSupport {

  private final TestModelProvider inspectorModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(CartInspectorAgent.class, inspectorModel);
  }

  @Test
  void agentCanCallShoppingCartEntityAsComponentTool() {
    componentClient
        .forEventSourcedEntity("cart-tool-1")
        .method(ShoppingCartEntity::addItem)
        .invoke(new ShoppingCart.Command.AddItem("sku-1", "Tea", 2));

    inspectorModel
        .whenMessage(message -> message.contains("cart-tool-1"))
        .reply(
            new ToolInvocationRequest(
                "ShoppingCartEntity_inspectCartSummary", "{\"uniqueId\":\"cart-tool-1\"}"));
    inspectorModel
        .whenToolResult(result -> result.name().equals("ShoppingCartEntity_inspectCartSummary"))
        .thenReply(
            result ->
                new AiResponse(
                    result.content().contains("\"checkedOut\":false")
                            && result.content().contains("\"totalQuantity\":2")
                            && result.content().contains("\"Tea\"")
                        ? "Cart cart-tool-1 has 2 units of Tea and is not checked out."
                        : "Cart details unavailable."));

    var answer =
        componentClient
            .forAgent()
            .inSession("cart-tool-session")
            .method(CartInspectorAgent::answerQuestion)
            .invoke("What is currently in cart cart-tool-1?");

    assertTrue(answer.contains("Tea"));
    assertTrue(answer.contains("not checked out"));

    var summary =
        componentClient
            .forEventSourcedEntity("cart-tool-1")
            .method(ShoppingCartEntity::inspectCartSummary)
            .invoke();
    assertEquals(1, summary.items().size());
    assertEquals(2, summary.totalQuantity());
    assertEquals("Tea", summary.items().get(0).name());
  }
}
