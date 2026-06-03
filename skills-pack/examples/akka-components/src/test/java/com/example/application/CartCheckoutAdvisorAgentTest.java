package com.example.application;

import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import akka.javasdk.testkit.TestModelProvider.AiResponse;
import akka.javasdk.testkit.TestModelProvider.ToolInvocationRequest;
import com.example.domain.ShoppingCart;
import org.junit.jupiter.api.Test;

class CartCheckoutAdvisorAgentTest extends TestKitSupport {

  private final TestModelProvider advisorModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(CartCheckoutAdvisorAgent.class, advisorModel);
  }

  @Test
  void agentCanUseNonComponentToolFacadeBackedByComponentClient() {
    componentClient
        .forEventSourcedEntity("cart-advice-1")
        .method(ShoppingCartEntity::addItem)
        .invoke(new ShoppingCart.Command.AddItem("sku-1", "Tea", 2));

    advisorModel
        .whenMessage(message -> message.contains("cart-advice-1"))
        .reply(
            new ToolInvocationRequest(
                "CartCheckoutAdvisorTools_adviseCheckout", "{\"cartId\":\"cart-advice-1\"}"));
    advisorModel
        .whenToolResult(result -> result.name().equals("CartCheckoutAdvisorTools_adviseCheckout"))
        .thenReply(
            result ->
                new AiResponse(
                    result.content().contains("ready_for_checkout")
                            && result.content().contains("\"totalQuantity\":2")
                            && result.content().contains("cart.checkout-advice")
                        ? "Cart cart-advice-1 is ready for checkout with 2 items."
                        : "Checkout advice unavailable."));

    var answer =
        componentClient
            .forAgent()
            .inSession("cart-advice-session")
            .method(CartCheckoutAdvisorAgent::advise)
            .invoke("Is cart cart-advice-1 ready for checkout?");

    assertTrue(answer.contains("ready for checkout"));
    assertTrue(answer.contains("2 items"));
  }
}
