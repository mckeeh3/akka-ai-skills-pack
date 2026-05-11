package com.example.application.supplies;

import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import org.junit.jupiter.api.Test;

class SupplyAutopilotUiEndpointIntegrationTest extends TestKitSupport {

  @Test
  void servesSuppliesCommandCenterShellWithAiFirstDecisionContext() {
    var page = httpClient.GET("/ui/supplies").responseBodyAs(String.class).invoke();

    assertTrue(page.status().isSuccess());
    assertTrue(page.body().contains("Supplies command center"));
    assertTrue(page.body().contains("GOAL-02"));
    assertTrue(page.body().contains("Evidence, risk, policy, and action"));
    assertTrue(page.body().contains("Approval, rejection, or suppression remains disabled until a decision card is loaded"));
    assertTrue(page.body().contains("data-api-base=\"/api/supplies\""));
    assertTrue(page.body().contains("href=\"#trace-panel\""));
    assertTrue(page.body().contains("aria-live=\"polite\""));
  }

  @Test
  void servesSuppliesStyleGuideAlignedCssAndBrowserClientAsset() {
    var css = httpClient.GET("/ui/supplies/app.css").responseBodyAs(String.class).invoke();
    assertTrue(css.status().isSuccess());
    assertTrue(css.body().contains("--color-primary: #2563eb"));
    assertTrue(css.body().contains("prefers-color-scheme: dark"));
    assertTrue(css.body().contains(":focus-visible"));
    assertTrue(css.body().contains("@media (max-width: 880px)"));

    var js = httpClient.GET("/ui/supplies/app.js").responseBodyAs(String.class).invoke();
    assertTrue(js.status().isSuccess());
    assertTrue(js.body().contains("fetch(`${apiBase}${path}`"));
    assertTrue(js.body().contains("/decisions/pending"));
    assertTrue(js.body().contains("/risks?status=WAITING_FOR_APPROVAL"));
    assertTrue(js.body().contains("Evidence, risk/confidence, policy, alternatives, trace, and outcome context are visible"));
    assertTrue(js.body().contains("Rationale must explain the evidence, policy, risk, or trace context."));
  }
}
