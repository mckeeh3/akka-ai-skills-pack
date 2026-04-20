package com.example.api;

import akka.javasdk.JsonSupport;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.mcp.McpEndpoint;
import akka.javasdk.annotations.mcp.McpPrompt;
import akka.javasdk.annotations.mcp.McpResource;
import akka.javasdk.annotations.mcp.McpTool;
import akka.javasdk.client.ComponentClient;
import com.example.application.ShoppingCartEntity;
import com.example.domain.ShoppingCart;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * MCP endpoint example that exposes shopping-cart tools, resources, and prompts for LLM clients.
 */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
@McpEndpoint(
    path = "/mcp/shopping",
    serverName = "shopping-cart-assistant",
    serverVersion = "1.0.0",
    instructions =
        "Use tools to inspect cart state. Read the checkout guidelines resource before suggesting checkout steps.")
public class ShoppingCartMcpEndpoint {

  public record CartItem(String productId, String name, int quantity) {}

  public record CartSummary(
      String cartId,
      int itemCount,
      int totalQuantity,
      boolean checkedOut,
      List<CartItem> items) {}

  public record SuggestNextActionInput(
      String cartId,
      String customerGoal,
      Optional<String> preferredTone) {}

  public record SuggestedAction(String action, String rationale, String tone) {}

  private final ComponentClient componentClient;

  public ShoppingCartMcpEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @McpTool(
      description =
          "Return a compact JSON summary of a shopping cart so an LLM can answer questions about items, quantities, and checkout readiness.")
  public String getCartSummary(@Description("The shopping cart id to inspect") String cartId) {
    return JsonSupport.encodeToString(toSummary(loadCart(cartId)));
  }

  @McpTool(
      name = "suggest-next-action",
      description =
          "Given a cart id and the customer's goal, return the next assistant action to take as compact JSON.",
      inputSchema =
          """
          {
            "type": "object",
            "properties": {
              "input": {
                "type": "object",
                "properties": {
                  "cartId": {
                    "type": "string",
                    "description": "Shopping cart id to inspect"
                  },
                  "customerGoal": {
                    "type": "string",
                    "description": "What the customer is trying to do next"
                  },
                  "preferredTone": {
                    "type": "string",
                    "description": "Optional assistant tone such as concise or friendly"
                  }
                },
                "required": ["cartId", "customerGoal"]
              }
            },
            "required": ["input"]
          }
          """)
  public String suggestNextAction(SuggestNextActionInput input) {
    var cart = loadCart(input.cartId());

    String action;
    String rationale;
    if (cart.checkedOut()) {
      action = "confirm-order-status";
      rationale = "The cart is already checked out, so the assistant should confirm completion and offer post-purchase help.";
    } else if (cart.items().isEmpty()) {
      action = "recommend-products";
      rationale = "The cart is empty, so the assistant should help the customer add an item before discussing checkout.";
    } else {
      action = "offer-checkout";
      rationale =
          "The cart has items and is not checked out, so the assistant should answer the goal and guide the customer toward checkout.";
    }

    return JsonSupport.encodeToString(
        new SuggestedAction(action, rationale, input.preferredTone().orElse("friendly")));
  }

  @McpResource(
      uri = "file:///shopping/policies/checkout-guidelines.md",
      name = "Checkout guidelines",
      description = "Guidelines the assistant should follow when discussing shopping cart checkout.",
      mimeType = "text/markdown")
  public String checkoutGuidelines() {
    return readClasspathResource("/mcp/checkout-guidelines.md");
  }

  @McpResource(
      uriTemplate = "cart://summary/{cartId}",
      name = "Cart summary",
      description = "Structured JSON summary for a specific shopping cart id.",
      mimeType = "application/json")
  public CartSummary cartSummaryResource(String cartId) {
    return toSummary(loadCart(cartId));
  }

  @McpPrompt(description = "Prompt template for responding to a customer's shopping cart question")
  public String respondToCartQuestion(
      @Description("The cart id being discussed") String cartId,
      @Description("The customer's current question") String customerQuestion,
      @Description("The assistant tone such as concise or friendly") String tone) {
    var cart = loadCart(cartId);
    var itemLines =
        cart.items().isEmpty()
            ? "- the cart is empty"
            : cart.items().stream()
                .map(item -> "- " + item.name() + " (" + item.productId() + ") x " + item.quantity())
                .reduce((left, right) -> left + "\n" + right)
                .orElse("- the cart is empty");

    return """
        You are helping a shopper with cart %s.
        Use a %s tone.
        Cart checked out: %s
        Cart items:
        %s

        Customer question:
        %s

        Answer with the next useful step. If checkout is possible, say so explicitly.
        """
        .formatted(
            cartId,
            tone,
            cart.checkedOut(),
            itemLines,
            customerQuestion);
  }

  private ShoppingCart.State loadCart(String cartId) {
    return componentClient
        .forEventSourcedEntity(cartId)
        .method(ShoppingCartEntity::getCart)
        .invoke();
  }

  private static CartSummary toSummary(ShoppingCart.State cart) {
    return new CartSummary(
        cart.cartId(),
        cart.items().size(),
        cart.items().stream().mapToInt(ShoppingCart.LineItem::quantity).sum(),
        cart.checkedOut(),
        cart.items().stream()
            .map(item -> new CartItem(item.productId(), item.name(), item.quantity()))
            .toList());
  }

  private String readClasspathResource(String resourcePath) {
    try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        throw new IllegalStateException("Missing classpath resource: " + resourcePath);
      }
      return new String(inputStream.readAllBytes());
    } catch (IOException error) {
      throw new IllegalStateException("Failed to read classpath resource: " + resourcePath, error);
    }
  }
}
