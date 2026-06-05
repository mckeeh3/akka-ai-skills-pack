package ai.first.application.foundation.agent;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Backend-only model provider boundary. Provider secrets stay in runtime configuration and never cross API DTOs. */
public interface ModelProviderClient {
  ModelProviderResponse invoke(ModelProviderRequest request);

  record ModelProviderRequest(
      String providerAlias,
      String modelConfigRefId,
      String systemPrompt,
      String userPrompt,
      String tenantId,
      String functionalAgentId,
      String correlationId,
      List<String> promptTraceIds) {
    public ModelProviderRequest {
      promptTraceIds = List.copyOf(promptTraceIds == null ? List.of() : promptTraceIds);
    }
  }

  record ModelProviderResponse(
      String markdown,
      String providerAlias,
      String modelId,
      String providerRequestId,
      String finishReason,
      String safeSummary) {}

  record ModelProviderFailure(String safeCode, String safeSummary) {}

  final class ModelProviderException extends RuntimeException {
    private final ModelProviderFailure failure;

    public ModelProviderException(String safeCode, String safeSummary) {
      super(safeSummary);
      this.failure = new ModelProviderFailure(safeCode, safeSummary);
    }

    public ModelProviderFailure failure() {
      return failure;
    }
  }

  record ModelProviderConfiguration(
      String providerAlias,
      String modelId,
      URI apiBaseUri,
      Duration timeout,
      String apiKey) {
    private static final String DEFAULT_OPENAI_API_BASE_URL = "https://api.openai.com/v1";
    private static final String DEFAULT_OPENAI_MODEL_ID = "gpt-4o-mini";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    public static ModelProviderConfiguration openAiFromEnvironment(Map<String, String> environment, String providerAlias) {
      var env = environment == null ? Map.<String, String>of() : environment;
      var apiKey = trimToNull(env.get("OPENAI_API_KEY"));
      var modelId = Optional.ofNullable(trimToNull(env.get("OPENAI_MODEL_ID"))).orElse(DEFAULT_OPENAI_MODEL_ID);
      var baseUrl = Optional.ofNullable(trimToNull(env.get("OPENAI_API_BASE_URL"))).orElse(DEFAULT_OPENAI_API_BASE_URL);
      var timeout = parseTimeout(env.get("OPENAI_REQUEST_TIMEOUT_SECONDS"));
      return new ModelProviderConfiguration(
          Optional.ofNullable(trimToNull(providerAlias)).orElse("openai-low-temperature"),
          modelId,
          URI.create(baseUrl),
          timeout,
          apiKey);
    }

    public void requireReadyForInvocation() {
      if (apiKey == null || apiKey.isBlank()) {
        throw new ModelProviderException("model-provider-config-missing", "Model provider configuration is missing required backend variable OPENAI_API_KEY.");
      }
      if (modelId == null || modelId.isBlank()) {
        throw new ModelProviderException("model-provider-config-missing", "Model provider configuration is missing required backend variable OPENAI_MODEL_ID.");
      }
      Objects.requireNonNull(apiBaseUri, "apiBaseUri");
      Objects.requireNonNull(timeout, "timeout");
    }

    public String safeSummary() {
      return "providerAlias=" + providerAlias + "; model id=" + modelId + "; endpoint=" + safeEndpoint(apiBaseUri) + "; timeout=" + timeout.toSeconds() + "s; secret=[REDACTED]";
    }

    private static String safeEndpoint(URI uri) {
      if (uri == null) return "unconfigured";
      var port = uri.getPort() == -1 ? "" : ":" + uri.getPort();
      return uri.getScheme() + "://" + uri.getHost() + port + Optional.ofNullable(uri.getPath()).orElse("");
    }

    private static Duration parseTimeout(String rawValue) {
      var value = trimToNull(rawValue);
      if (value == null) return DEFAULT_TIMEOUT;
      try {
        var seconds = Long.parseLong(value);
        if (seconds < 1 || seconds > 120) return DEFAULT_TIMEOUT;
        return Duration.ofSeconds(seconds);
      } catch (NumberFormatException ignored) {
        return DEFAULT_TIMEOUT;
      }
    }
  }

  private static String trimToNull(String value) {
    if (value == null || value.isBlank()) return null;
    return value.trim();
  }
}
