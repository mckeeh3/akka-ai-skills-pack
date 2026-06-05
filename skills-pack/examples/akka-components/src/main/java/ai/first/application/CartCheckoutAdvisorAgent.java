package ai.first.application;

import akka.javasdk.agent.Agent;
import akka.javasdk.annotations.AgentRole;
import akka.javasdk.annotations.Component;
import akka.javasdk.client.ComponentClient;

/** Agent example that uses a non-component tool facade backed by ComponentClient calls. */
@Component(
    id = "cart-checkout-advisor-agent",
    name = "Cart Checkout Advisor Agent",
    description =
        "Answers checkout readiness questions by calling a non-component tool facade that composes Akka component calls.")
@AgentRole("worker")
public class CartCheckoutAdvisorAgent extends Agent {

  private static final String SYSTEM_MESSAGE =
      """
      You answer shopping cart checkout-readiness questions.
      Use CartCheckoutAdvisorTools_adviseCheckout when you need current checkout evidence.
      The tool is the read-only cart.checkout-advice capability. It takes cartId, composes
      curated cart state with cart index evidence, and does not change cart state.
      Keep the final answer concise and grounded in the tool result.
      """
          .stripIndent();

  private final ComponentClient componentClient;

  public CartCheckoutAdvisorAgent(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public Effect<String> advise(String message) {
    return effects()
        .systemMessage(SYSTEM_MESSAGE)
        .tools(new CartCheckoutAdvisorTools(componentClient))
        .userMessage(message)
        .thenReply();
  }
}
