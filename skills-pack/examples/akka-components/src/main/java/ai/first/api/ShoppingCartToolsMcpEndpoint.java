package ai.first.api;

import akka.javasdk.JsonSupport;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.mcp.McpEndpoint;
import akka.javasdk.annotations.mcp.McpTool;
import akka.javasdk.client.ComponentClient;
import ai.first.application.ShoppingCartEntity;
import ai.first.domain.ShoppingCart;
import java.util.List;

/**
 * Default-path MCP endpoint kept intentionally small for remote agent tool examples.
 *
 * <p>Capability exposed: {@code cart.summary.inspect}. It is read-only evidence for a remote
 * assistant and returns a curated summary instead of raw entity state.
 */
@Acl(allow = @Acl.Matcher(service = "shopping-assistant-service"))
@McpEndpoint(
    path = "/mcp",
    serverName = "shopping-cart-tools",
    serverVersion = "1.0.0",
    instructions =
        "Use these tools only for the read-only cart.summary.inspect capability. Return curated cart evidence; do not treat this MCP endpoint as authority to mutate carts.")
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
          "Capability cart.summary.inspect: return a compact, read-only JSON summary of a shopping cart so an authorized remote agent can answer questions about items, quantities, and checkout readiness. This tool does not expose raw cart state and does not mutate the cart.")
  public String getCartSummary(
      @Description("The shopping cart id to inspect within the caller's allowed scope") String cartId) {
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
