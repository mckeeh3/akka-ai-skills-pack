package ai.first.application.coreapp.workstream;

import ai.first.domain.foundation.identity.AuthContext;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Side-effect-free contract for deterministic workstream prompt routing to structured surfaces.
 *
 * <p>Implementations may inspect only the submitted prompt, functional agent id, selected
 * AuthContext, and correlation data. They must not mutate domain state, submit capability actions,
 * call model providers, or expand authority. WorkstreamService remains responsible for resolving the
 * selected AuthContext and loading the target surface through the normal backend-authorized path.
 */
@FunctionalInterface
public interface SurfaceIntentRouter {
  Optional<Result> route(Request request);

  static SurfaceIntentRouter noop() {
    return request -> Optional.empty();
  }

  record Request(String functionalAgentId, String prompt, AuthContext selectedAuthContext, String correlationId) {
    public Request {
      if (functionalAgentId == null || functionalAgentId.isBlank()) throw new IllegalArgumentException("functionalAgentId is required");
      if (prompt == null || prompt.isBlank()) throw new IllegalArgumentException("prompt is required");
      selectedAuthContext = Objects.requireNonNull(selectedAuthContext, "selectedAuthContext");
      correlationId = correlationId == null || correlationId.isBlank() ? "surface-intent-route" : correlationId;
    }
  }

  record Result(
      String functionalAgentId,
      String targetSurfaceId,
      String sourcePrompt,
      String canonicalPrompt,
      Map<String, Object> prefill,
      String confidence,
      String category,
      boolean noMutation,
      List<String> traceIds,
      Map<String, Object> metadata) {
    public Result {
      if (functionalAgentId == null || functionalAgentId.isBlank()) throw new IllegalArgumentException("functionalAgentId is required");
      if (targetSurfaceId == null || targetSurfaceId.isBlank()) throw new IllegalArgumentException("targetSurfaceId is required");
      if (sourcePrompt == null || sourcePrompt.isBlank()) throw new IllegalArgumentException("sourcePrompt is required");
      canonicalPrompt = canonicalPrompt == null || canonicalPrompt.isBlank() ? sourcePrompt.trim() : canonicalPrompt.trim();
      confidence = confidence == null || confidence.isBlank() ? "high" : confidence;
      category = category == null || category.isBlank() ? "surface_open" : category;
      if (!noMutation) throw new IllegalArgumentException("surface intent routes must carry noMutation=true");
      prefill = copyMap(prefill);
      traceIds = traceIds == null ? List.of() : List.copyOf(traceIds);
      metadata = copyMap(metadata);
    }
  }

  private static Map<String, Object> copyMap(Map<String, Object> values) {
    return values == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(values));
  }
}
