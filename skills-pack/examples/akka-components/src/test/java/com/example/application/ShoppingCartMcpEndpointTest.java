package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import com.example.api.ShoppingCartMcpEndpoint;
import com.example.domain.ShoppingCart;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ShoppingCartMcpEndpointTest extends TestKitSupport {

  @Test
  void cartToolAndResourceExposeStructuredCartSummary() throws Exception {
    var cartId = "cart-mcp-1";

    componentClient
        .forEventSourcedEntity(cartId)
        .method(ShoppingCartEntity::addItem)
        .invoke(new ShoppingCart.Command.AddItem("akka-shirt", "Akka Shirt", 2));

    componentClient
        .forEventSourcedEntity(cartId)
        .method(ShoppingCartEntity::addItem)
        .invoke(new ShoppingCart.Command.AddItem("akka-mug", "Akka Mug", 1));

    var endpoint = new ShoppingCartMcpEndpoint(componentClient);

    var toolJson = endpoint.getCartSummary(cartId);
    var toolSummary =
        JsonSupport.getObjectMapper().readValue(toolJson, ShoppingCartMcpEndpoint.CartSummary.class);

    assertEquals(cartId, toolSummary.cartId());
    assertEquals(2, toolSummary.itemCount());
    assertEquals(3, toolSummary.totalQuantity());
    assertFalse(toolSummary.checkedOut());
    assertEquals(2, toolSummary.items().size());

    var resourceSummary = endpoint.cartSummaryResource(cartId);
    assertEquals(cartId, resourceSummary.cartId());
    assertEquals(2, resourceSummary.itemCount());
    assertEquals(3, resourceSummary.totalQuantity());
  }

  @Test
  void manualSchemaToolPromptAndStaticResourceStayReadableForAgents() throws Exception {
    var cartId = "cart-mcp-2";

    componentClient
        .forEventSourcedEntity(cartId)
        .method(ShoppingCartEntity::addItem)
        .invoke(new ShoppingCart.Command.AddItem("akka-socks", "Akka Socks", 1));

    var endpoint = new ShoppingCartMcpEndpoint(componentClient);

    var actionJson =
        endpoint.suggestNextAction(
            new ShoppingCartMcpEndpoint.SuggestNextActionInput(
                cartId, "I want to finish my order", Optional.of("concise")));
    var suggestedAction =
        JsonSupport.getObjectMapper()
            .readValue(actionJson, ShoppingCartMcpEndpoint.SuggestedAction.class);

    assertEquals("offer-checkout", suggestedAction.action());
    assertEquals("concise", suggestedAction.tone());
    assertTrue(suggestedAction.rationale().contains("cart has items"));

    var prompt = endpoint.respondToCartQuestion(cartId, "Can I check out now?", "concise");
    assertTrue(prompt.contains("Use a concise tone."));
    assertTrue(prompt.contains("Akka Socks"));
    assertTrue(prompt.contains("Can I check out now?"));

    var guidelines = endpoint.checkoutGuidelines();
    assertTrue(guidelines.contains("Checkout guidelines"));
    assertTrue(guidelines.contains("cart is empty"));
  }
}
