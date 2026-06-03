package com.example.application;

import akka.javasdk.annotations.Description;
import akka.javasdk.annotations.FunctionTool;
import akka.javasdk.client.ComponentClient;
import java.util.ArrayList;
import java.util.List;

/**
 * Non-component function-tool facade that composes Akka component calls plus deterministic logic.
 *
 * <p>This tool is intentionally not an Akka component method exposed directly through
 * {@code effects().tools(ComponentClass.class)}. It hides the underlying component layout from the
 * model, calls multiple components through {@link ComponentClient}, applies capability-specific
 * processing, and returns a curated agent-safe result.
 */
public class CartCheckoutAdvisorTools {

  private final ComponentClient componentClient;

  public CartCheckoutAdvisorTools(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @FunctionTool(
      description =
          "Read-only capability cart.checkout-advice. Assess whether the given cartId is ready "
              + "for checkout by reading curated cart state and related cart index evidence. "
              + "This tool does not change cart state.")
  public CartCheckoutAdvice adviseCheckout(
      @Description("Shopping cart id to assess for checkout readiness.") String cartId) {
    var summary =
        componentClient
            .forEventSourcedEntity(cartId)
            .method(ShoppingCartEntity::inspectCartSummary)
            .invoke();

    var openCarts =
        componentClient
            .forView()
            .method(ShoppingCartsByCheckedOutView::getCarts)
            .invoke(new ShoppingCartsByCheckedOutView.FindByCheckedOut(false));

    var reasons = new ArrayList<String>();
    String recommendation;

    if (summary.checkedOut()) {
      recommendation = "already_checked_out";
      reasons.add("Cart has already been checked out.");
    } else if (summary.totalQuantity() == 0) {
      recommendation = "add_items_before_checkout";
      reasons.add("Cart has no items.");
    } else {
      recommendation = "ready_for_checkout";
      reasons.add("Cart has at least one item and has not been checked out.");
    }

    reasons.add("Open-cart index currently contains " + openCarts.carts().size() + " carts.");

    return new CartCheckoutAdvice(
        "cart.checkout-advice",
        cartId,
        summary.totalQuantity(),
        summary.checkedOut(),
        openCarts.carts().size(),
        recommendation,
        List.copyOf(reasons));
  }

  public record CartCheckoutAdvice(
      String capabilityId,
      String cartId,
      int totalQuantity,
      boolean checkedOut,
      int openCartCount,
      String recommendation,
      List<String> reasons) {}
}
