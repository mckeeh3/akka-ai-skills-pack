package ai.first.application.foundation.agent;

import ai.first.application.foundation.agent.ModelProviderClient.ModelProviderConfiguration;
import ai.first.application.foundation.agent.ModelProviderClient.ModelProviderException;
import ai.first.application.foundation.agent.ModelProviderClient.ModelProviderRequest;
import ai.first.application.foundation.agent.ModelProviderClient.ModelProviderResponse;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Real OpenAI chat-completions adapter for local production-like model-backed workstream execution. */
public final class OpenAiModelProviderClient implements ModelProviderClient {
  private final Map<String, String> environment;
  private final HttpTransport transport;

  public OpenAiModelProviderClient() {
    this(System.getenv(), new JavaNetHttpTransport());
  }

  public OpenAiModelProviderClient(Map<String, String> environment, HttpTransport transport) {
    this.environment = Map.copyOf(environment == null ? Map.of() : environment);
    this.transport = Objects.requireNonNull(transport, "transport");
  }

  @Override
  public ModelProviderResponse invoke(ModelProviderRequest request) {
    Objects.requireNonNull(request, "request");
    var config = ModelProviderConfiguration.openAiFromEnvironment(environment, request.providerAlias());
    config.requireReadyForInvocation();
    var httpRequest = buildRequest(request, config);
    try {
      var response = transport.send(httpRequest);
      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        var markdown = extractJsonString(response.body(), "content").orElseThrow(() -> new ModelProviderException("model-provider-empty-response", "Model provider returned no assistant content."));
        return new ModelProviderResponse(markdown, config.providerAlias(), config.modelId(), extractJsonString(response.body(), "id").orElse("openai-response"), extractJsonString(response.body(), "finish_reason").orElse("unknown"), "OpenAI model invocation completed for " + request.functionalAgentId() + "; " + config.safeSummary());
      }
      throw new ModelProviderException("model-provider-http-" + response.statusCode(), "Model provider request failed with HTTP " + response.statusCode() + ".");
    } catch (HttpTimeoutException e) {
      throw new ModelProviderException("model-provider-timeout", "Model provider request timed out.");
    } catch (IOException e) {
      throw new ModelProviderException("model-provider-io-error", "Model provider request failed with an I/O error.");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ModelProviderException("model-provider-interrupted", "Model provider request was interrupted.");
    }
  }

  public HttpRequest buildRequest(ModelProviderRequest request, ModelProviderConfiguration config) {
    config.requireReadyForInvocation();
    var endpoint = config.apiBaseUri().resolve(config.apiBaseUri().getPath().endsWith("/") ? "chat/completions" : config.apiBaseUri().getPath() + "/chat/completions");
    var body = "{"
        + jsonField("model", config.modelId()) + ","
        + "\"temperature\":0.2,"
        + "\"messages\":["
        + "{\"role\":\"system\",\"content\":" + jsonString(request.systemPrompt()) + "},"
        + "{\"role\":\"user\",\"content\":" + jsonString(request.userPrompt()) + "}"
        + "]} ";
    return HttpRequest.newBuilder(endpoint)
        .timeout(config.timeout())
        .header("Authorization", "Bearer " + config.apiKey())
        .header("Content-Type", "application/json")
        .header("X-Correlation-Id", safeHeader(request.correlationId()))
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();
  }

  public interface HttpTransport {
    HttpTransportResponse send(HttpRequest request) throws IOException, InterruptedException;
  }

  public record HttpTransportResponse(int statusCode, String body) {}

  private static final class JavaNetHttpTransport implements HttpTransport {
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public HttpTransportResponse send(HttpRequest request) throws IOException, InterruptedException {
      var response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return new HttpTransportResponse(response.statusCode(), response.body());
    }
  }

  private static String jsonField(String name, String value) {
    return jsonString(name) + ":" + jsonString(value);
  }

  private static String jsonString(String value) {
    return "\"" + jsonEscape(value) + "\"";
  }

  private static String jsonEscape(String value) {
    return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
  }

  private static String safeHeader(String value) {
    if (value == null || value.isBlank()) return "model-provider-request";
    return value.replaceAll("[^A-Za-z0-9._:-]", "-");
  }

  private static Optional<String> extractJsonString(String json, String fieldName) {
    if (json == null) return Optional.empty();
    var marker = "\"" + fieldName + "\"";
    var field = json.indexOf(marker);
    if (field < 0) return Optional.empty();
    var colon = json.indexOf(':', field + marker.length());
    if (colon < 0) return Optional.empty();
    var start = json.indexOf('"', colon + 1);
    if (start < 0) return Optional.empty();
    var end = start + 1;
    var escaped = false;
    while (end < json.length()) {
      var ch = json.charAt(end);
      if (ch == '"' && !escaped) break;
      escaped = ch == '\\' && !escaped;
      if (ch != '\\') escaped = false;
      end++;
    }
    if (end >= json.length()) return Optional.empty();
    return Optional.of(json.substring(start + 1, end).replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\"));
  }
}
