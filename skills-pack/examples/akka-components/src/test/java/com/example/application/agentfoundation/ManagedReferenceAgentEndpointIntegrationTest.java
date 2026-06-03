package com.example.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.agent.ModelTimeoutException;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import com.example.api.ManagedReferenceAgentEndpoint;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ManagedReferenceAgentEndpointIntegrationTest extends TestKitSupport {

  private final TestModelProvider managedActivityModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(ManagedReferenceActivityAgent.class, managedActivityModel);
  }

  @Test
  void endpointInvokesManagedAgentWithCorrelationAndNoHiddenPromptOrSkillText() {
    managedActivityModel.fixedResponse(
        JsonSupport.encodeToString(
            new ManagedReferenceActivityAgent.ManagedActivitySuggestion(
                "Indoor climbing", "The governed activity agent selected an indoor option.", "indoor")));

    var response =
        httpClient
            .POST("/agentfoundation/managed-reference-agent/invoke")
            .withRequestBody(
                new ManagedReferenceAgentEndpoint.InvokeRequest(
                    ReferenceAgentFoundationSeed.TENANT_ID,
                    "account-admin-1",
                    Set.of(ReferenceAgentRuntimeResolver.INVOKE_CAPABILITY),
                    ReferenceAgentFoundationSeed.AGENT_ID,
                    "corr-http-success",
                    "Need a rainy-day activity",
                    ReferenceAgentFoundationSeed.ASSIGNED_SKILL_ID))
            .responseBodyAs(ManagedReferenceAgentEndpoint.InvokeResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertTrue(response.body().allowed());
    assertEquals("corr-http-success", response.body().correlationId());
    assertEquals("Indoor climbing", response.body().suggestion().name());
    assertEquals(1, response.body().promptAssemblyTraceCount());
    assertEquals(1, response.body().skillLoadTraceCount());
    assertEquals(1, response.body().agentWorkTraceCount());
    assertTrue(response.body().skillLoadAllowed());
    var serializedResponse = JsonSupport.encodeToString(response.body());
    assertFalse(serializedResponse.contains("You are an activity guide"));
    assertFalse(serializedResponse.contains("Recommend indoor activities first"));
    assertFalse(serializedResponse.contains("api-key"));
  }

  @Test
  void disabledAgentReturnsSafeDenialAndDoesNotInvokeModel() {
    managedActivityModel
        .whenMessage(message -> true)
        .failWith(new ModelTimeoutException("model should not be invoked for disabled agent"));

    var response =
        httpClient
            .POST("/agentfoundation/managed-reference-agent/invoke")
            .withRequestBody(
                new ManagedReferenceAgentEndpoint.InvokeRequest(
                    ReferenceAgentFoundationSeed.TENANT_ID,
                    "account-admin-1",
                    Set.of(ReferenceAgentRuntimeResolver.INVOKE_CAPABILITY),
                    ReferenceAgentFoundationSeed.DISABLED_AGENT_ID,
                    "corr-http-disabled",
                    "This should be denied before model invocation",
                    ReferenceAgentFoundationSeed.ASSIGNED_SKILL_ID))
            .responseBodyAs(ManagedReferenceAgentEndpoint.InvokeResponse.class)
            .invoke();

    assertTrue(response.status().isSuccess());
    assertFalse(response.body().allowed());
    assertEquals("agent is not active for runtime", response.body().denialReason());
    assertEquals("Denied", response.body().suggestion().name());
    assertEquals("corr-http-disabled", response.body().correlationId());
    assertEquals(1, response.body().promptAssemblyTraceCount());
    assertEquals(0, response.body().skillLoadTraceCount());
    assertEquals(1, response.body().agentWorkTraceCount());
  }

  @Test
  void invalidRequestReturnsBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                httpClient
                    .POST("/agentfoundation/managed-reference-agent/invoke")
                    .withRequestBody(
                        new ManagedReferenceAgentEndpoint.InvokeRequest(
                            ReferenceAgentFoundationSeed.TENANT_ID,
                            "account-admin-1",
                            Set.of(ReferenceAgentRuntimeResolver.INVOKE_CAPABILITY),
                            ReferenceAgentFoundationSeed.AGENT_ID,
                            " ",
                            "Need an activity",
                            null))
                    .responseBodyAs(String.class)
                    .invoke());

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("correlationId must not be blank"));
  }
}
