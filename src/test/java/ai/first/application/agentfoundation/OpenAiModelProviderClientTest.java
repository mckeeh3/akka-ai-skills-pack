package ai.first.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.agentfoundation.ModelProviderClient.ModelProviderConfiguration;
import ai.first.application.agentfoundation.ModelProviderClient.ModelProviderException;
import ai.first.application.agentfoundation.ModelProviderClient.ModelProviderRequest;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OpenAiModelProviderClientTest {
  @Test
  void buildsOpenAiRequestFromBackendOnlyEnvironmentWithoutExposingSecretInSafeSummary() {
    var env = Map.of(
        "OPENAI_API_KEY", "sk-secret-never-return",
        "OPENAI_MODEL_ID", "gpt-4o-mini",
        "OPENAI_API_BASE_URL", "https://api.openai.test/v1",
        "OPENAI_REQUEST_TIMEOUT_SECONDS", "12");
    var client = new OpenAiModelProviderClient(env, request -> new OpenAiModelProviderClient.HttpTransportResponse(200, "{\"id\":\"resp-1\",\"choices\":[{\"message\":{\"content\":\"## real provider markdown\"},\"finish_reason\":\"stop\"}]}") );
    var result = client.invoke(request("corr-openai-shape"));

    assertEquals("## real provider markdown", result.markdown());
    assertEquals("openai-low-temperature", result.providerAlias());
    assertEquals("gpt-4o-mini", result.modelId());
    assertEquals("resp-1", result.providerRequestId());
    assertEquals("stop", result.finishReason());
    assertTrue(result.safeSummary().contains("model id=gpt-4o-mini"));
    assertFalse(result.safeSummary().contains("sk-secret-never-return"));

    var config = ModelProviderConfiguration.openAiFromEnvironment(env, "openai-low-temperature");
    var httpRequest = client.buildRequest(request("corr-header @ unsafe"), config);
    assertEquals("https://api.openai.test/v1/chat/completions", httpRequest.uri().toString());
    assertEquals(Duration.ofSeconds(12), httpRequest.timeout().orElseThrow());
    assertEquals("Bearer sk-secret-never-return", httpRequest.headers().firstValue("Authorization").orElseThrow());
    assertEquals("corr-header---unsafe", httpRequest.headers().firstValue("X-Correlation-Id").orElseThrow());
    assertFalse(config.safeSummary().contains("sk-secret-never-return"));
    assertTrue(config.safeSummary().contains("secret=[REDACTED]"));
  }

  @Test
  void missingProviderConfigFailsClosedWhenRuntimeModelInvocationIsRequested() {
    var client = new OpenAiModelProviderClient(Map.of("OPENAI_MODEL_ID", "gpt-4o-mini"), request -> new OpenAiModelProviderClient.HttpTransportResponse(200, "{}"));

    var failure = assertThrows(ModelProviderException.class, () -> client.invoke(request("corr-missing-provider")));

    assertEquals("model-provider-config-missing", failure.failure().safeCode());
    assertTrue(failure.failure().safeSummary().contains("OPENAI_API_KEY"));
    assertFalse(failure.failure().safeSummary().toLowerCase().contains("secret"));
  }

  @Test
  void mapsTimeoutHttpAndEmptyProviderResponsesToSafeErrors() {
    var timeoutClient = new OpenAiModelProviderClient(readyEnv(), request -> { throw new HttpTimeoutException("timed out with sk-secret"); });
    var httpClient = new OpenAiModelProviderClient(readyEnv(), request -> new OpenAiModelProviderClient.HttpTransportResponse(429, "{\"error\":\"rate limit sk-secret\"}"));
    var emptyClient = new OpenAiModelProviderClient(readyEnv(), request -> new OpenAiModelProviderClient.HttpTransportResponse(200, "{\"id\":\"resp-empty\",\"choices\":[{\"message\":{}}]}"));

    var timeout = assertThrows(ModelProviderException.class, () -> timeoutClient.invoke(request("corr-timeout")));
    var http = assertThrows(ModelProviderException.class, () -> httpClient.invoke(request("corr-http")));
    var empty = assertThrows(ModelProviderException.class, () -> emptyClient.invoke(request("corr-empty")));

    assertEquals("model-provider-timeout", timeout.failure().safeCode());
    assertEquals("model-provider-http-429", http.failure().safeCode());
    assertEquals("model-provider-empty-response", empty.failure().safeCode());
    assertFalse(timeout.failure().safeSummary().contains("sk-secret"));
    assertFalse(http.failure().safeSummary().contains("sk-secret"));
  }

  @Test
  void testOnlyFakeProviderCapturesRequestWithoutBeingRuntimeFallback() {
    var fake = new FakeModelProviderClient("## fake unit-test markdown");

    var response = fake.invoke(request("corr-fake"));

    assertEquals("## fake unit-test markdown", response.markdown());
    assertNotNull(fake.lastRequest);
    assertEquals("tenant-1", fake.lastRequest.tenantId());
    assertEquals(List.of("trace-prompt"), fake.lastRequest.promptTraceIds());
  }

  @Test
  void ioErrorsAreMappedToSafeFailures() {
    var client = new OpenAiModelProviderClient(readyEnv(), new OpenAiModelProviderClient.HttpTransport() {
      @Override
      public OpenAiModelProviderClient.HttpTransportResponse send(HttpRequest request) throws IOException {
        throw new IOException("network leaked sk-secret");
      }
    });

    var failure = assertThrows(ModelProviderException.class, () -> client.invoke(request("corr-io")));

    assertEquals("model-provider-io-error", failure.failure().safeCode());
    assertFalse(failure.failure().safeSummary().contains("sk-secret"));
  }

  private static Map<String, String> readyEnv() {
    return Map.of("OPENAI_API_KEY", "sk-secret", "OPENAI_MODEL_ID", "gpt-4o-mini");
  }

  private static ModelProviderRequest request(String correlationId) {
    return new ModelProviderRequest(
        "openai-low-temperature",
        "starter-default-model",
        "System prompt with compact manifest only.",
        "User asks for help.",
        "tenant-1",
        "agent-user-admin",
        correlationId,
        List.of("trace-prompt"));
  }
}
