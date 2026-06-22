package ai.first.application.coreapp.workstream;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Default deterministic surface intent router for high-confidence, side-effect-free surface opens. */
final class DefaultSurfaceIntentRouter implements SurfaceIntentRouter {
  private static final String MY_ACCOUNT_AGENT_ID = "my-account-agent";

  static SurfaceIntentRouter create() {
    return new DefaultSurfaceIntentRouter();
  }

  @Override
  public Optional<Result> route(Request request) {
    var normalized = normalize(request.prompt());
    if (MY_ACCOUNT_AGENT_ID.equals(request.functionalAgentId()) && isMyAccountDashboardOpen(normalized)) {
      var traceId = "trace-surface-intent-my-account-dashboard-" + stableSuffix(request.correlationId());
      return Optional.of(new Result(
          request.functionalAgentId(),
          "surface-my-account-dashboard",
          request.prompt(),
          "open my account dashboard",
          Map.of(),
          "high",
          "surface_open",
          true,
          List.of(traceId),
          Map.of(
              "routerContract", "surface_intent_route.v1",
              "routeId", "route-my-account-dashboard-open-v1",
              "sideEffect", "none",
              "selectedContextId", request.selectedAuthContext().membershipId())));
    }
    return Optional.empty();
  }

  private static boolean isMyAccountDashboardOpen(String normalized) {
    return List.of(
        "open my account",
        "open my account dashboard",
        "show my account",
        "show my account dashboard",
        "my account dashboard").contains(normalized);
  }

  private static String normalize(String prompt) {
    return prompt == null ? "" : prompt.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
  }

  private static String stableSuffix(String value) {
    return Integer.toUnsignedString((value == null ? "surface-intent-route" : value).hashCode(), 36);
  }
}
