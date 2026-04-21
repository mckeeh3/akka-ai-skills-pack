package com.example.api;

import akka.javasdk.JsonSupport;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.mcp.McpEndpoint;
import akka.javasdk.annotations.mcp.McpTool;
import akka.javasdk.client.ComponentClient;
import com.example.application.ShoppingCartEntity;
import com.example.domain.ShoppingCart;
import java.util.List;

/** Default-path MCP endpoint kept intentionally small for remote agent tool examples. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
@McpEndpoint(
    path = "/mcp",
    serverName = "shopping-cart-tools",
    serverVersion = "1.0.0",
    instructions = "Use these tools to inspect current shopping cart state.")
public class ShoppingCartToolsMcpEndpoint {

  public record CartItem(String productId, String name, int quantity) {}

  public record CartSummary(
      String cartId,
      int itemCount,
      int totalQuantity,
      boolean checkedOut,
      List<CartItem> items) {}

  private final ComponentClient componentClient;

  public ShoppingCartToolsMcpEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @McpTool(
      description =
          "Return a compact JSON summary of a shopping cart so a remote agent can answer questions about items, quantities, and checkout readiness.")
  public String getCartSummary(@Description("The shopping cart id to inspect") String cartId) {
    return JsonSupport.encodeToString(toSummary(loadCart(cartId)));
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
}
