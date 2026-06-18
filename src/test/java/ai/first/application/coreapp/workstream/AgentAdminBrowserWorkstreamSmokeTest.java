package ai.first.application.coreapp.workstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import ai.first.application.coreapp.workstream.WorkstreamService.CapabilityActionRequest;
import ai.first.application.coreapp.workstream.WorkstreamService.CapabilityActionResult;
import ai.first.application.coreapp.workstream.WorkstreamService.SurfaceEnvelope;
import ai.first.application.coreapp.workstream.WorkstreamService.WorkstreamBootstrapResponse;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;
import ai.first.application.foundation.agent.AkkaAgentBehaviorRepository;
import ai.first.application.foundation.identity.AkkaIdentityRepository;
import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
import ai.first.domain.foundation.identity.Account;
import ai.first.domain.foundation.identity.AccountStatus;
import ai.first.domain.foundation.identity.Customer;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.Membership;
import ai.first.domain.foundation.identity.MembershipStatus;
import ai.first.domain.foundation.identity.ScopeType;
import ai.first.domain.foundation.identity.Tenant;
import ai.first.domain.foundation.identity.UserProfile;
import ai.first.domain.foundation.identity.UserSettings;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Scriptable Akka-hosted UI/API smoke for the Agent Admin command center. */
class AgentAdminBrowserWorkstreamSmokeTest extends TestKitSupport {
  private static final String TENANT_ID = "tenant-starter";
  private static final String CUSTOMER_ID = "customer-agent-smoke";
  private static final String ADMIN_CONTEXT_ID = "membership-agent-admin";
  private static final String MEMBER_CONTEXT_ID = "membership-agent-member";
  private static final String CUSTOMER_CONTEXT_ID = "membership-agent-customer";

  @BeforeEach
  void seedAgentAdminSmokeActors() {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant(TENANT_ID, "Starter Tenant", true));
    repository.saveCustomer(new Customer(TENANT_ID, CUSTOMER_ID, "Customer Agent Smoke", true));
    seedIdentity(repository, "admin@example.test", "Tenant Admin", ADMIN_CONTEXT_ID, ScopeType.TENANT, null, List.of(FoundationRole.TENANT_ADMIN, FoundationRole.AUDITOR));
    seedIdentity(repository, "member@example.test", "Member User", MEMBER_CONTEXT_ID, ScopeType.TENANT, null, List.of(FoundationRole.TENANT_EMPLOYEE));
    seedIdentity(repository, "customer@example.test", "Customer Admin", CUSTOMER_CONTEXT_ID, ScopeType.CUSTOMER, CUSTOMER_ID, List.of(FoundationRole.CUSTOMER_ADMIN));
  }

  @Test
  void hostedShellAndProtectedWorkstreamApiExerciseAgentAdminDashboardRuntimePath() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load from Akka static resources.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertTrue(shell.body().contains("/assets/"));
    assertBrowserSafe(shell.body());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-admin-dashboard")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin dashboard must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-agent-admin-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-agent-admin-refresh-dashboard",
            "action-agent-admin-refresh-dashboard",
            "agent_admin.list_definitions",
            "agent_admin.list_definitions",
            null,
            null,
            ADMIN_CONTEXT_ID,
            "surface-agent-admin-dashboard",
            "corr-agent-admin-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin dashboard action path must reject missing bearer tokens.");

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-agent-admin-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals(ADMIN_CONTEXT_ID, bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-admin-agent") && agent.availability().equals("visible")));
    assertBrowserSafe(bootstrap.body());

    var dashboard = getSurface("surface-agent-admin-dashboard", "corr-agent-admin-dashboard");
    assertEquals("surface-agent-admin-dashboard", dashboard.surfaceId());
    assertEquals("dashboard", dashboard.surfaceType());
    assertEquals("agent_admin.dashboard.v1", dashboard.data().get("surfaceContract"));
    assertEquals("corr-agent-admin-dashboard", dashboard.correlationId());
    assertTrue(dashboard.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-surface-agent-admin-dashboard")));
    assertTrue(dashboard.toString().contains("surfaceSummary"));
    assertTrue(dashboard.toString().contains("scopeSummary"));
    assertTrue(dashboard.toString().contains("attentionSections"));
    assertTrue(dashboard.toString().contains("authorizedActions"));
    assertTrue(dashboard.toString().contains("providerModelStatus"));
    assertTrue(dashboard.toString().contains("blocked_provider_or_runtime"));
    assertTrue(dashboard.toString().contains("noFakeSuccess=true"));
    assertTrue(dashboard.toString().contains("trace-agent-admin-dashboard"));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-open-catalog") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-catalog")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-open-prompt-risk-review") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-prompt-risk-review")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-open-tool-boundary") && action.resultSurface().updateSurfaceId().equals("surface-agent-tool-boundary-diff")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-open-model-refs") && action.resultSurface().updateSurfaceId().equals("surface-agent-model-refs")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-open-trace") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-trace")));
    assertBrowserSafe(dashboard);

    var dashboardToolBoundary = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-tool-boundary",
        "action-agent-admin-open-tool-boundary",
        "agent_admin.get_tool_boundary",
        "agent_admin.get_tool_boundary",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-dashboard-tool-boundary"));
    assertEquals("accepted", dashboardToolBoundary.status());
    assertEquals("surface-agent-tool-boundary-diff", dashboardToolBoundary.resultSurface().surfaceId());
    assertEquals("agent_admin.tool_boundary_diff.v1", dashboardToolBoundary.resultSurface().data().get("surfaceContract"));
    assertTrue(dashboardToolBoundary.resultSurface().toString().contains("ToolPermissionBoundary"));
    assertBrowserSafe(dashboardToolBoundary.resultSurface());

    var refreshed = runAction(new CapabilityActionRequest(
        "action-agent-admin-refresh-dashboard",
        "action-agent-admin-refresh-dashboard",
        "agent_admin.list_definitions",
        "agent_admin.list_definitions",
        null,
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-refresh"));
    assertEquals("accepted", refreshed.status());
    assertEquals("surface-agent-admin-dashboard", refreshed.resultSurface().surfaceId());
    assertEquals("corr-agent-admin-refresh", refreshed.correlationId());
    assertBrowserSafe(refreshed.resultSurface());

    var catalog = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-catalog",
        "action-agent-admin-open-catalog",
        "agent_admin.list_definitions",
        "agent_admin.list_definitions",
        null,
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-catalog"));
    assertEquals("accepted", catalog.status());
    assertEquals("surface-agent-admin-catalog", catalog.resultSurface().surfaceId());
    assertEquals("list-search", catalog.resultSurface().surfaceType());
    assertEquals("agent_admin.catalog.v1", catalog.resultSurface().data().get("surfaceContract"));
    assertTrue(catalog.resultSurface().toString().contains("action-open-agent-detail"));
    assertTrue(catalog.resultSurface().toString().contains("trace-agent-admin-catalog"));
    assertBrowserSafe(catalog.resultSurface());

    var modelRefs = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-model-refs",
        "action-agent-admin-open-model-refs",
        "agent_admin.get_model_ref",
        "agent_admin.get_model_ref",
        null,
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-model-refs"));
    assertEquals("accepted", modelRefs.status());
    assertEquals("surface-agent-model-refs", modelRefs.resultSurface().surfaceId());
    assertEquals("governance-diff", modelRefs.resultSurface().surfaceType());
    assertEquals("agent_admin.model_refs.v1", modelRefs.resultSurface().data().get("surfaceContract"));
    assertTrue(modelRefs.resultSurface().toString().contains("modelReferenceSummary"));
    assertTrue(modelRefs.resultSurface().toString().contains("redactedModelReferenceDiff"));
    assertTrue(modelRefs.resultSurface().toString().contains("action-agent-model-refs-submit-review"));
    assertTrue(modelRefs.resultSurface().toString().contains("providerCredential=[REDACTED]"));
    assertTrue(modelRefs.resultSurface().toString().contains("secretVisibility=redacted"));
    assertTrue(modelRefs.resultSurface().toString().contains("traceLinks"));
    assertBrowserSafe(modelRefs.resultSurface());

    var promptRisk = runAction(new CapabilityActionRequest(
        "action-agentadmin-start-prompt-risk-review",
        "action-agentadmin-start-prompt-risk-review",
        "agent_admin.prompt_risk_review.start",
        "agent_admin.prompt_risk_review.start",
        Map.of("agentDefinitionId", "agent-admin-agent", "proposalId", "proposal-agent-admin-prompt-001"),
        "idem-agent-admin-prompt-risk-smoke",
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-prompt-risk"));
    assertEquals("blocked_provider_or_runtime", promptRisk.status());
    assertEquals("surface-agent-admin-prompt-risk-review", promptRisk.resultSurface().surfaceId());
    assertEquals("agent_admin.prompt_risk_review_task.v1", promptRisk.resultSurface().data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", promptRisk.resultSurface().data().get("status"));
    assertTrue(promptRisk.resultSurface().toString().contains("activationBlockedUntilHumanDecision=true"));
    assertTrue(promptRisk.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-admin-prompt-risk")));
    assertBrowserSafe(promptRisk.resultSurface());

    var trace = runAction(new CapabilityActionRequest(
        "action-agent-admin-open-trace",
        "action-agent-admin-open-trace",
        "audit.trace.read",
        "audit.trace.read",
        Map.of("traceId", "trace-agent-admin-dashboard"),
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-agent-admin-trace"));
    assertEquals("accepted", trace.status());
    assertEquals("surface-agent-admin-trace", trace.resultSurface().surfaceId());
    assertEquals("audit-timeline", trace.resultSurface().surfaceType());
    assertTrue(trace.resultSurface().toString().contains("events"));
    assertTrue(trace.resultSurface().toString().contains("PromptAssemblyTrace"));
    assertTrue(trace.resultSurface().toString().contains("AgentWorkTrace"));
    assertBrowserSafe(trace.resultSurface());
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedWorkstreamApiExercisesAgentAdminCatalogRuntimePath() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-admin-catalog")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin catalog must reject missing bearer tokens.");
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-admin-detail")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin detail must reject missing bearer tokens.");
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-prompt-governance")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin prompt-governance surface must reject missing bearer tokens.");
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-skill-manifest-diff")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin skill-manifest surface must reject missing bearer tokens.");
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-tool-boundary-diff")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin tool-boundary diff surface must reject missing bearer tokens.");
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-model-refs")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin model-reference surface must reject missing bearer tokens.");

    var seedDefaults = runAction(new CapabilityActionRequest(
        "action-import-agent-seed-defaults",
        "action-import-agent-seed-defaults",
        "agent_admin.reseed_missing_defaults",
        "agent_admin.reseed_missing_defaults",
        Map.of("reason", "catalog-runtime-smoke"),
        "idem-agent-admin-catalog-seed",
        ADMIN_CONTEXT_ID,
        "surface-agent-admin-dashboard",
        "corr-agent-admin-catalog-seed"));
    assertTrue(seedDefaults.status().equals("accepted") || seedDefaults.status().equals("no-op"));
    assertEquals("surface-agent-seed-import-confirmation", seedDefaults.resultSurface().surfaceId());
    assertBrowserSafe(seedDefaults.resultSurface());

    var catalog = getCatalogWithRows("corr-agent-admin-catalog-direct");
    assertEquals("surface-agent-admin-catalog", catalog.surfaceId());
    assertEquals("list-search", catalog.surfaceType());
    assertEquals("agent_admin.catalog.v1", catalog.data().get("surfaceContract"));
    assertTrue(catalog.correlationId().startsWith("corr-agent-admin-catalog-direct"));
    assertTrue(catalog.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-surface-agent-admin-catalog")));
    assertTrue(catalog.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-refresh-catalog")));
    assertTrue(catalog.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-search-catalog")));
    assertTrue(catalog.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-reset-catalog-filters")));
    assertTrue(catalog.actions().stream().anyMatch(action -> action.actionId().equals("action-open-agent-detail") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-detail")));
    assertTrue(catalog.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-admin-catalog-open-trace") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-trace")));
    assertFalse(catalog.actions().stream().anyMatch(action -> action.actionId().contains("activate") || action.actionId().contains("deactivate") || action.actionId().contains("rollback")), "Catalog must not expose inline lifecycle mutation actions.");

    var catalogSummary = (Map<String, Object>) catalog.data().get("catalogSummary");
    assertEquals("surface-agent-admin-catalog", catalogSummary.get("surfaceId"));
    assertEquals("agent_admin.catalog.v1", catalogSummary.get("contract"));
    assertTrue(((Number) catalogSummary.get("resultCount")).intValue() > 0);
    assertTrue(String.valueOf(catalogSummary.get("providerReadinessCounts")).contains("blocked_provider_or_runtime") || catalog.toString().contains("provider-fail-closed"));
    var scopeSummary = (Map<String, Object>) catalog.data().get("scopeSummary");
    assertEquals(ADMIN_CONTEXT_ID, scopeSummary.get("selectedAuthContextId"));
    assertEquals("tenant", scopeSummary.get("scopeType"));
    assertEquals(Boolean.TRUE, scopeSummary.get("governanceAuthorized"));
    var filters = (Map<String, Object>) catalog.data().get("filters");
    assertEquals(Boolean.TRUE, filters.get("backendAuthoritative"));
    assertEquals("", filters.get("searchText"));
    var rows = (List<Map<String, Object>>) catalog.data().get("rows");
    assertFalse(rows.isEmpty());
    assertTrue(rows.stream().anyMatch(row -> AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID.equals(row.get("id")) && "action-open-agent-detail".equals(row.get("openActionId")) && "surface-agent-admin-detail".equals(row.get("targetSurfaceId"))));
    assertTrue(catalog.toString().contains("safeRedactionSummary"));
    assertTrue(catalog.toString().contains("providerCredentials=omitted"));
    assertTrue(catalog.toString().contains("rawTraceEvidence=role-gated"));
    assertTrue(catalog.toString().contains("secretVisibility=redacted"));
    assertTrue(catalog.toString().contains("trace-agent-admin-catalog"));
    assertBrowserSafe(catalog);

    var search = runAction(new CapabilityActionRequest(
        "action-agent-admin-search-catalog",
        "action-agent-admin-search-catalog",
        "agent_admin.list_definitions",
        "agent_admin.list_definitions",
        Map.of("query", "Agent Admin"),
        null,
        ADMIN_CONTEXT_ID,
        catalog.surfaceId(),
        "corr-agent-admin-catalog-search"));
    assertEquals("accepted", search.status());
    assertEquals("surface-agent-admin-catalog", search.resultSurface().surfaceId());
    assertEquals("Agent Admin", ((Map<String, Object>) search.resultSurface().data().get("filters")).get("searchText"));
    assertTrue(search.resultSurface().toString().contains("Agent Admin Agent"));
    assertFalse(search.resultSurface().toString().contains("tenant:tenant-2"));
    assertBrowserSafe(search.resultSurface());

    var noMatches = runAction(new CapabilityActionRequest(
        "action-agent-admin-search-catalog",
        "action-agent-admin-search-catalog",
        "agent_admin.list_definitions",
        "agent_admin.list_definitions",
        Map.of("query", "no matching governed agent"),
        null,
        ADMIN_CONTEXT_ID,
        catalog.surfaceId(),
        "corr-agent-admin-catalog-empty"));
    assertEquals("accepted", noMatches.status());
    assertEquals("empty-no-filter-matches", ((Map<String, Object>) noMatches.resultSurface().data().get("emptyState")).get("state"));
    assertTrue(((List<Map<String, Object>>) noMatches.resultSurface().data().get("rows")).isEmpty());
    assertBrowserSafe(noMatches.resultSurface());

    var reset = runAction(new CapabilityActionRequest(
        "action-agent-admin-reset-catalog-filters",
        "action-agent-admin-reset-catalog-filters",
        "agent_admin.list_definitions",
        "agent_admin.list_definitions",
        Map.of("query", "Agent Admin"),
        null,
        ADMIN_CONTEXT_ID,
        catalog.surfaceId(),
        "corr-agent-admin-catalog-reset"));
    assertEquals("no-op", reset.status());
    assertEquals("", ((Map<String, Object>) reset.resultSurface().data().get("filters")).get("searchText"));
    assertFalse(((List<Map<String, Object>>) reset.resultSurface().data().get("rows")).isEmpty());
    assertBrowserSafe(reset.resultSurface());

    var detail = runAction(new CapabilityActionRequest(
        "action-open-agent-detail",
        "action-open-agent-detail",
        "agent_admin.get_definition",
        "agent_admin.get_definition",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        catalog.surfaceId(),
        "corr-agent-admin-catalog-open-row"));
    assertEquals("accepted", detail.status());
    assertEquals("surface-agent-admin-detail", detail.resultSurface().surfaceId());
    assertEquals("show-inspection", detail.resultSurface().surfaceType());
    assertEquals("agent_admin.detail.v1", detail.resultSurface().data().get("surfaceContract"));
    assertEquals(AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, detail.resultSurface().data().get("recordId"));
    assertTrue(detail.resultSurface().toString().contains("detailSummary"));
    assertTrue(detail.resultSurface().toString().contains("scopeSummary"));
    assertTrue(detail.resultSurface().toString().contains("readinessNarrative"));
    assertTrue(detail.resultSurface().toString().contains("behaviorArtifactCards"));
    assertTrue(detail.resultSurface().toString().contains("taskEntryPoints"));
    assertTrue(detail.resultSurface().toString().contains("safeRedactionSummary"));
    assertTrue(detail.resultSurface().toString().contains("action-agent-detail-open-prompt-governance"));
    assertTrue(detail.resultSurface().toString().contains("action-agent-detail-open-skill-manifest"));
    assertTrue(detail.resultSurface().toString().contains("action-agent-detail-open-tool-boundary"));
    assertTrue(detail.resultSurface().toString().contains("action-agent-detail-open-model-refs"));
    assertTrue(detail.resultSurface().toString().contains("action-agent-detail-run-test"));
    assertTrue(detail.resultSurface().toString().contains("action-agent-detail-open-prompt-risk-review"));
    assertTrue(detail.resultSurface().toString().contains("action-agent-detail-open-activation"));
    assertTrue(detail.resultSurface().toString().contains("action-agent-detail-open-deactivation"));
    assertTrue(detail.resultSurface().toString().contains("action-agent-detail-open-rollback"));
    assertTrue(detail.resultSurface().toString().contains("action-agent-detail-open-trace"));
    assertTrue(detail.resultSurface().toString().contains("action-agent-detail-back-to-catalog"));
    assertTrue(detail.resultSurface().toString().contains("providerCredentials=omitted"));
    assertTrue(detail.resultSurface().toString().contains("trace-agent-admin-definition"));
    assertTrue(detail.resultSurface().toString().contains("noDirectMutation=true"));
    assertBrowserSafe(detail.resultSurface());

    var directDetail = getSurface("surface-agent-admin-detail", "corr-agent-admin-detail-direct");
    assertEquals("surface-agent-admin-detail", directDetail.surfaceId());
    assertEquals("show-inspection", directDetail.surfaceType());
    assertEquals("agent_admin.detail.v1", directDetail.data().get("surfaceContract"));
    assertTrue(directDetail.toString().contains("authorizedActions"));
    assertBrowserSafe(directDetail);

    var refreshedDetail = runAction(new CapabilityActionRequest(
        "action-agent-detail-refresh",
        "action-agent-detail-refresh",
        "agent_admin.get_definition",
        "agent_admin.get_definition",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-agent-admin-detail-refresh"));
    assertEquals("no-op", refreshedDetail.status());
    assertEquals("surface-agent-admin-detail", refreshedDetail.resultSurface().surfaceId());
    assertBrowserSafe(refreshedDetail.resultSurface());

    var detailModelRefs = runAction(new CapabilityActionRequest(
        "action-agent-detail-open-model-refs",
        "action-agent-detail-open-model-refs",
        "agent_admin.get_model_ref",
        "agent_admin.get_model_ref",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        detail.resultSurface().surfaceId(),
        "corr-agent-admin-detail-model-refs"));
    assertEquals("accepted", detailModelRefs.status());
    assertEquals("surface-agent-model-refs", detailModelRefs.resultSurface().surfaceId());
    assertEquals("agent_admin.model_refs.v1", detailModelRefs.resultSurface().data().get("surfaceContract"));
    assertTrue(detailModelRefs.resultSurface().toString().contains("providerCredential=[REDACTED]"));
    assertBrowserSafe(detailModelRefs.resultSurface());

    var promptGovernance = assertDetailActionRoutes("action-agent-detail-open-prompt-governance", "agent_admin.get_prompt_version", "surface-agent-prompt-governance", "accepted", "corr-agent-admin-detail-prompt-governance");
    assertEquals("agent_admin.prompt_governance.v1", promptGovernance.resultSurface().data().get("surfaceContract"));
    assertTrue(promptGovernance.resultSurface().toString().contains("governanceSummary"));
    assertTrue(promptGovernance.resultSurface().toString().contains("redactedPromptDiff"));
    assertTrue(promptGovernance.resultSurface().toString().contains("reviewState"));
    assertTrue(promptGovernance.resultSurface().toString().contains("action-agent-prompt-governance-refresh"));
    assertTrue(promptGovernance.resultSurface().toString().contains("action-agent-prompt-governance-simulate"));
    assertTrue(promptGovernance.resultSurface().toString().contains("action-agent-prompt-governance-submit-review"));
    assertTrue(promptGovernance.resultSurface().toString().contains("action-agent-prompt-governance-approve"));
    assertTrue(promptGovernance.resultSurface().toString().contains("action-agent-prompt-governance-reject"));
    assertTrue(promptGovernance.resultSurface().toString().contains("action-agent-prompt-governance-open-risk-review"));
    assertTrue(promptGovernance.resultSurface().toString().contains("action-agent-prompt-governance-open-trace"));
    assertTrue(promptGovernance.resultSurface().toString().contains("action-agent-prompt-governance-back-to-detail"));

    var directPromptGovernance = getSurface("surface-agent-prompt-governance", "corr-agent-admin-prompt-governance-direct");
    assertEquals("surface-agent-prompt-governance", directPromptGovernance.surfaceId());
    assertEquals("governance-diff", directPromptGovernance.surfaceType());
    assertEquals("agent_admin.prompt_governance.v1", directPromptGovernance.data().get("surfaceContract"));
    assertEquals("corr-agent-admin-prompt-governance-direct", directPromptGovernance.correlationId());
    assertTrue(directPromptGovernance.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-surface-agent-prompt-governance")));
    var promptGovernanceSummary = (Map<String, Object>) directPromptGovernance.data().get("governanceSummary");
    assertEquals("agent_admin.prompt_governance.v1", promptGovernanceSummary.get("contract"));
    assertEquals("blocked_provider_or_runtime", promptGovernanceSummary.get("providerModelReadinessCategory"));
    assertEquals(Boolean.TRUE, promptGovernanceSummary.get("noDirectActivation"));
    var promptScopeSummary = (Map<String, Object>) directPromptGovernance.data().get("scopeSummary");
    assertEquals(ADMIN_CONTEXT_ID, promptScopeSummary.get("selectedContextId"));
    assertEquals(Boolean.TRUE, promptScopeSummary.get("governanceAuthorized"));
    assertEquals("visible", promptScopeSummary.get("visibilityDecision"));
    assertTrue(directPromptGovernance.toString().contains("safeRedactionSummary"));
    assertTrue(directPromptGovernance.toString().contains("rawPromptText=omitted"));
    assertTrue(directPromptGovernance.toString().contains("blocked_provider_or_runtime"));
    assertTrue(directPromptGovernance.toString().contains("noDirectActivation=true"));
    assertBrowserSafe(directPromptGovernance);

    var promptRefresh = runAction(new CapabilityActionRequest(
        "action-agent-prompt-governance-refresh",
        "action-agent-prompt-governance-refresh",
        "agent_admin.get_prompt_version",
        "agent_admin.get_prompt_version",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        promptGovernance.resultSurface().surfaceId(),
        "corr-agent-admin-prompt-governance-refresh"));
    assertEquals("no-op", promptRefresh.status());
    assertEquals("surface-agent-prompt-governance", promptRefresh.resultSurface().surfaceId());
    assertBrowserSafe(promptRefresh.resultSurface());

    var promptSimulation = runAction(new CapabilityActionRequest(
        "action-agent-prompt-governance-simulate",
        "action-agent-prompt-governance-simulate",
        "agent_admin.draft_behavior_change",
        "agent_admin.draft_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-prompt-governance-simulate",
        ADMIN_CONTEXT_ID,
        promptGovernance.resultSurface().surfaceId(),
        "corr-agent-admin-prompt-governance-simulate"));
    assertEquals("accepted", promptSimulation.status());
    assertEquals("surface-agent-test-console", promptSimulation.resultSurface().surfaceId());
    assertTrue(promptSimulation.resultSurface().toString().contains("noProductionSideEffects=true"));
    assertBrowserSafe(promptSimulation.resultSurface());

    var promptSubmit = runAction(new CapabilityActionRequest(
        "action-agent-prompt-governance-submit-review",
        "action-agent-prompt-governance-submit-review",
        "agent_admin.submit_behavior_change_for_review",
        "agent_admin.submit_behavior_change_for_review",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-prompt-governance-submit",
        ADMIN_CONTEXT_ID,
        promptGovernance.resultSurface().surfaceId(),
        "corr-agent-admin-prompt-governance-submit"));
    assertEquals("approval-required", promptSubmit.status());
    assertEquals("surface-agent-behavior-proposal", promptSubmit.resultSurface().surfaceId());
    assertTrue(promptSubmit.message().contains("active prompt behavior remains unchanged"));
    assertBrowserSafe(promptSubmit.resultSurface());

    var promptApprove = runAction(new CapabilityActionRequest(
        "action-agent-prompt-governance-approve",
        "action-agent-prompt-governance-approve",
        "agent_admin.approve_behavior_change",
        "agent_admin.approve_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-prompt-governance-approve",
        ADMIN_CONTEXT_ID,
        promptGovernance.resultSurface().surfaceId(),
        "corr-agent-admin-prompt-governance-approve"));
    assertEquals("approval-required", promptApprove.status());
    assertEquals("surface-agent-behavior-proposal", promptApprove.resultSurface().surfaceId());
    assertTrue(promptApprove.message().contains("activation remains blocked until a separate confirmation surface"));
    assertBrowserSafe(promptApprove.resultSurface());

    var promptRejectMissingReason = runAction(new CapabilityActionRequest(
        "action-agent-prompt-governance-reject",
        "action-agent-prompt-governance-reject",
        "agent_admin.reject_behavior_change",
        "agent_admin.reject_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-prompt-governance-reject-missing-reason",
        ADMIN_CONTEXT_ID,
        promptGovernance.resultSurface().surfaceId(),
        "corr-agent-admin-prompt-governance-reject-missing-reason"));
    assertEquals("validation-error", promptRejectMissingReason.status());
    assertEquals("surface-agent-prompt-governance", promptRejectMissingReason.resultSurface().surfaceId());
    assertTrue(promptRejectMissingReason.message().contains("requires a human-readable reason"));
    assertBrowserSafe(promptRejectMissingReason.resultSurface());

    var promptReject = runAction(new CapabilityActionRequest(
        "action-agent-prompt-governance-reject",
        "action-agent-prompt-governance-reject",
        "agent_admin.reject_behavior_change",
        "agent_admin.reject_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "reason", "Keep current prompt until human review evidence is complete."),
        "idem-agent-prompt-governance-reject",
        ADMIN_CONTEXT_ID,
        promptGovernance.resultSurface().surfaceId(),
        "corr-agent-admin-prompt-governance-reject"));
    assertEquals("accepted", promptReject.status());
    assertEquals("surface-agent-behavior-proposal", promptReject.resultSurface().surfaceId());
    assertTrue(promptReject.message().contains("active behavior unchanged"));
    assertBrowserSafe(promptReject.resultSurface());

    var promptRiskFromGovernance = runAction(new CapabilityActionRequest(
        "action-agent-prompt-governance-open-risk-review",
        "action-agent-prompt-governance-open-risk-review",
        "agent_admin.prompt_risk_review.read",
        "agent_admin.prompt_risk_review.read",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        promptGovernance.resultSurface().surfaceId(),
        "corr-agent-admin-prompt-governance-risk"));
    assertEquals("accepted", promptRiskFromGovernance.status());
    assertEquals("surface-agent-admin-prompt-risk-review", promptRiskFromGovernance.resultSurface().surfaceId());
    assertTrue(promptRiskFromGovernance.resultSurface().toString().contains("blocked_provider_or_runtime"));
    assertBrowserSafe(promptRiskFromGovernance.resultSurface());

    var promptTrace = runAction(new CapabilityActionRequest(
        "action-agent-prompt-governance-open-trace",
        "action-agent-prompt-governance-open-trace",
        "audit.trace.read",
        "audit.trace.read",
        Map.of("traceId", "trace-agent-admin-prompt-governance"),
        null,
        ADMIN_CONTEXT_ID,
        promptGovernance.resultSurface().surfaceId(),
        "corr-agent-admin-prompt-governance-trace"));
    assertEquals("accepted", promptTrace.status());
    assertEquals("surface-agent-admin-trace", promptTrace.resultSurface().surfaceId());
    assertTrue(promptTrace.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-prompt-governance-open-trace")));
    assertBrowserSafe(promptTrace.resultSurface());

    var promptBackToDetail = runAction(new CapabilityActionRequest(
        "action-agent-prompt-governance-back-to-detail",
        "action-agent-prompt-governance-back-to-detail",
        "agent_admin.get_definition",
        "agent_admin.get_definition",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        promptGovernance.resultSurface().surfaceId(),
        "corr-agent-admin-prompt-governance-detail-return"));
    assertEquals("accepted", promptBackToDetail.status());
    assertEquals("surface-agent-admin-detail", promptBackToDetail.resultSurface().surfaceId());
    assertBrowserSafe(promptBackToDetail.resultSurface());

    var skillManifest = assertDetailActionRoutes("action-agent-detail-open-skill-manifest", "agent_admin.get_manifest", "surface-agent-skill-manifest-diff", "accepted", "corr-agent-admin-detail-skill-manifest");
    assertEquals("agent_admin.skill_manifest_diff.v1", skillManifest.resultSurface().data().get("surfaceContract"));
    assertTrue(skillManifest.resultSurface().toString().contains("manifestDiffSummary"));
    assertTrue(skillManifest.resultSurface().toString().contains("redactedManifestDiff"));
    assertTrue(skillManifest.resultSurface().toString().contains("reviewState"));
    assertTrue(skillManifest.resultSurface().toString().contains("action-agent-skill-manifest-refresh"));
    assertTrue(skillManifest.resultSurface().toString().contains("action-agent-skill-manifest-simulate"));
    assertTrue(skillManifest.resultSurface().toString().contains("action-agent-skill-manifest-submit-review"));
    assertTrue(skillManifest.resultSurface().toString().contains("action-agent-skill-manifest-approve"));
    assertTrue(skillManifest.resultSurface().toString().contains("action-agent-skill-manifest-reject"));
    assertTrue(skillManifest.resultSurface().toString().contains("action-agent-skill-manifest-open-tool-boundary"));
    assertTrue(skillManifest.resultSurface().toString().contains("action-agent-skill-manifest-open-model-refs"));
    assertTrue(skillManifest.resultSurface().toString().contains("action-agent-skill-manifest-open-trace"));
    assertTrue(skillManifest.resultSurface().toString().contains("action-agent-skill-manifest-back-to-detail"));

    var directSkillManifest = getSurface("surface-agent-skill-manifest-diff", "corr-agent-admin-skill-manifest-direct");
    assertEquals("surface-agent-skill-manifest-diff", directSkillManifest.surfaceId());
    assertEquals("governance-diff", directSkillManifest.surfaceType());
    assertEquals("agent_admin.skill_manifest_diff.v1", directSkillManifest.data().get("surfaceContract"));
    assertEquals("corr-agent-admin-skill-manifest-direct", directSkillManifest.correlationId());
    assertTrue(directSkillManifest.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-surface-agent-skill-manifest-diff")));
    var manifestSummary = (Map<String, Object>) directSkillManifest.data().get("manifestDiffSummary");
    assertEquals("agent_admin.skill_manifest_diff.v1", manifestSummary.get("contract"));
    assertEquals("blocked_provider_or_runtime", manifestSummary.get("providerRuntimeReadinessCategory"));
    assertEquals(Boolean.TRUE, manifestSummary.get("noDirectActivation"));
    var manifestScope = (Map<String, Object>) directSkillManifest.data().get("scopeSummary");
    assertEquals(ADMIN_CONTEXT_ID, manifestScope.get("selectedContextId"));
    assertEquals(Boolean.TRUE, manifestScope.get("governanceAuthorized"));
    assertEquals("visible", manifestScope.get("visibilityDecision"));
    assertTrue(directSkillManifest.toString().contains("safeRedactionSummary"));
    assertTrue(directSkillManifest.toString().contains("rawSkillReferenceBodies=omitted"));
    assertTrue(directSkillManifest.toString().contains("providerCredentials=omitted"));
    assertTrue(directSkillManifest.toString().contains("blocked_provider_or_runtime"));
    assertTrue(directSkillManifest.toString().contains("noDirectActivation=true"));
    assertBrowserSafe(directSkillManifest);

    var skillRefresh = runAction(new CapabilityActionRequest(
        "action-agent-skill-manifest-refresh",
        "action-agent-skill-manifest-refresh",
        "agent_admin.get_manifest",
        "agent_admin.get_manifest",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        skillManifest.resultSurface().surfaceId(),
        "corr-agent-admin-skill-manifest-refresh"));
    assertEquals("no-op", skillRefresh.status());
    assertEquals("surface-agent-skill-manifest-diff", skillRefresh.resultSurface().surfaceId());
    assertBrowserSafe(skillRefresh.resultSurface());

    var skillSimulation = runAction(new CapabilityActionRequest(
        "action-agent-skill-manifest-simulate",
        "action-agent-skill-manifest-simulate",
        "agent_admin.draft_behavior_change",
        "agent_admin.draft_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-skill-manifest-simulate",
        ADMIN_CONTEXT_ID,
        skillManifest.resultSurface().surfaceId(),
        "corr-agent-admin-skill-manifest-simulate"));
    assertEquals("accepted", skillSimulation.status());
    assertEquals("surface-agent-test-console", skillSimulation.resultSurface().surfaceId());
    assertTrue(skillSimulation.resultSurface().toString().contains("noProductionSideEffects=true"));
    assertBrowserSafe(skillSimulation.resultSurface());

    var skillSubmit = runAction(new CapabilityActionRequest(
        "action-agent-skill-manifest-submit-review",
        "action-agent-skill-manifest-submit-review",
        "agent_admin.submit_behavior_change_for_review",
        "agent_admin.submit_behavior_change_for_review",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-skill-manifest-submit",
        ADMIN_CONTEXT_ID,
        skillManifest.resultSurface().surfaceId(),
        "corr-agent-admin-skill-manifest-submit"));
    assertEquals("approval-required", skillSubmit.status());
    assertEquals("surface-agent-behavior-proposal", skillSubmit.resultSurface().surfaceId());
    assertTrue(skillSubmit.message().contains("active manifest and reference behavior remain unchanged"));
    assertBrowserSafe(skillSubmit.resultSurface());

    var skillApprove = runAction(new CapabilityActionRequest(
        "action-agent-skill-manifest-approve",
        "action-agent-skill-manifest-approve",
        "agent_admin.approve_behavior_change",
        "agent_admin.approve_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-skill-manifest-approve",
        ADMIN_CONTEXT_ID,
        skillManifest.resultSurface().surfaceId(),
        "corr-agent-admin-skill-manifest-approve"));
    assertEquals("approval-required", skillApprove.status());
    assertEquals("surface-agent-behavior-proposal", skillApprove.resultSurface().surfaceId());
    assertTrue(skillApprove.message().contains("activation remains blocked until a separate confirmation surface"));
    assertBrowserSafe(skillApprove.resultSurface());

    var skillRejectMissingReason = runAction(new CapabilityActionRequest(
        "action-agent-skill-manifest-reject",
        "action-agent-skill-manifest-reject",
        "agent_admin.reject_behavior_change",
        "agent_admin.reject_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-skill-manifest-reject-missing-reason",
        ADMIN_CONTEXT_ID,
        skillManifest.resultSurface().surfaceId(),
        "corr-agent-admin-skill-manifest-reject-missing-reason"));
    assertEquals("validation-error", skillRejectMissingReason.status());
    assertEquals("surface-agent-skill-manifest-diff", skillRejectMissingReason.resultSurface().surfaceId());
    assertTrue(skillRejectMissingReason.message().contains("requires a human-readable reason"));
    assertBrowserSafe(skillRejectMissingReason.resultSurface());

    var skillReject = runAction(new CapabilityActionRequest(
        "action-agent-skill-manifest-reject",
        "action-agent-skill-manifest-reject",
        "agent_admin.reject_behavior_change",
        "agent_admin.reject_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "reason", "Keep manifest stable until reference evidence is complete."),
        "idem-agent-skill-manifest-reject",
        ADMIN_CONTEXT_ID,
        skillManifest.resultSurface().surfaceId(),
        "corr-agent-admin-skill-manifest-reject"));
    assertEquals("accepted", skillReject.status());
    assertEquals("surface-agent-behavior-proposal", skillReject.resultSurface().surfaceId());
    assertTrue(skillReject.message().contains("active manifest and reference behavior unchanged"));
    assertBrowserSafe(skillReject.resultSurface());

    var skillToolBoundary = runAction(new CapabilityActionRequest(
        "action-agent-skill-manifest-open-tool-boundary",
        "action-agent-skill-manifest-open-tool-boundary",
        "agent_admin.get_tool_boundary",
        "agent_admin.get_tool_boundary",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        skillManifest.resultSurface().surfaceId(),
        "corr-agent-admin-skill-manifest-tool-boundary"));
    assertEquals("accepted", skillToolBoundary.status());
    assertEquals("surface-agent-tool-boundary-diff", skillToolBoundary.resultSurface().surfaceId());
    assertEquals("agent_admin.tool_boundary_diff.v1", skillToolBoundary.resultSurface().data().get("surfaceContract"));
    assertTrue(skillToolBoundary.resultSurface().toString().contains("toolBoundarySummary"));
    assertTrue(skillToolBoundary.resultSurface().toString().contains("redactedToolBoundaryDiff"));
    assertTrue(skillToolBoundary.resultSurface().toString().contains("ToolPermissionBoundary"));
    assertTrue(skillToolBoundary.resultSurface().toString().contains("action-agent-tool-boundary-simulate"));
    assertTrue(skillToolBoundary.resultSurface().toString().contains("action-agent-tool-boundary-submit-review"));
    assertTrue(skillToolBoundary.resultSurface().toString().contains("action-agent-tool-boundary-open-model-refs"));
    assertBrowserSafe(skillToolBoundary.resultSurface());

    var directToolBoundary = getSurface("surface-agent-tool-boundary-diff", "corr-agent-admin-tool-boundary-direct");
    assertEquals("surface-agent-tool-boundary-diff", directToolBoundary.surfaceId());
    assertEquals("governance-diff", directToolBoundary.surfaceType());
    assertEquals("agent_admin.tool_boundary_diff.v1", directToolBoundary.data().get("surfaceContract"));
    assertEquals("corr-agent-admin-tool-boundary-direct", directToolBoundary.correlationId());
    assertTrue(directToolBoundary.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-surface-agent-tool-boundary-diff")));
    var toolBoundarySummary = (Map<String, Object>) directToolBoundary.data().get("toolBoundarySummary");
    assertEquals("agent_admin.tool_boundary_diff.v1", toolBoundarySummary.get("contract"));
    assertEquals("blocked_provider_or_runtime", toolBoundarySummary.get("providerRuntimeReadinessCategory"));
    assertEquals(Boolean.TRUE, toolBoundarySummary.get("noDirectActivation"));
    var toolBoundaryScope = (Map<String, Object>) directToolBoundary.data().get("scopeSummary");
    assertEquals(ADMIN_CONTEXT_ID, toolBoundaryScope.get("selectedContextId"));
    assertEquals(Boolean.TRUE, toolBoundaryScope.get("governanceAuthorized"));
    assertEquals("visible", toolBoundaryScope.get("visibilityDecision"));
    assertTrue(directToolBoundary.toString().contains("rawToolInputsOutputs=omitted"));
    assertTrue(directToolBoundary.toString().contains("providerCredentials=omitted"));
    assertTrue(directToolBoundary.toString().contains("tool-boundary-denied"));
    assertTrue(directToolBoundary.toString().contains("noDirectActivation=true"));
    assertTrue(directToolBoundary.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-tool-boundary-refresh")));
    assertTrue(directToolBoundary.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-tool-boundary-simulate") && action.resultSurface().updateSurfaceId().equals("surface-agent-test-console")));
    assertTrue(directToolBoundary.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-tool-boundary-submit-review") && action.resultSurface().updateSurfaceId().equals("surface-agent-behavior-proposal")));
    assertTrue(directToolBoundary.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-tool-boundary-approve") && action.resultSurface().updateSurfaceId().equals("surface-agent-behavior-proposal")));
    assertTrue(directToolBoundary.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-tool-boundary-reject") && action.resultSurface().updateSurfaceId().equals("surface-agent-behavior-proposal")));
    assertTrue(directToolBoundary.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-tool-boundary-open-model-refs") && action.resultSurface().updateSurfaceId().equals("surface-agent-model-refs")));
    assertTrue(directToolBoundary.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-tool-boundary-open-trace") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-trace")));
    assertTrue(directToolBoundary.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-tool-boundary-back-to-detail") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-detail")));
    assertBrowserSafe(directToolBoundary);

    var toolBoundaryRefresh = runAction(new CapabilityActionRequest(
        "action-agent-tool-boundary-refresh",
        "action-agent-tool-boundary-refresh",
        "agent_admin.get_tool_boundary",
        "agent_admin.get_tool_boundary",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        directToolBoundary.surfaceId(),
        "corr-agent-admin-tool-boundary-refresh"));
    assertEquals("no-op", toolBoundaryRefresh.status());
    assertEquals("surface-agent-tool-boundary-diff", toolBoundaryRefresh.resultSurface().surfaceId());
    assertBrowserSafe(toolBoundaryRefresh.resultSurface());

    var toolBoundarySimulation = runAction(new CapabilityActionRequest(
        "action-agent-tool-boundary-simulate",
        "action-agent-tool-boundary-simulate",
        "agent_admin.simulate_tool_boundary",
        "agent_admin.simulate_tool_boundary",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-tool-boundary-simulate",
        ADMIN_CONTEXT_ID,
        directToolBoundary.surfaceId(),
        "corr-agent-admin-tool-boundary-simulate"));
    assertEquals("accepted", toolBoundarySimulation.status());
    assertEquals("surface-agent-test-console", toolBoundarySimulation.resultSurface().surfaceId());
    assertTrue(toolBoundarySimulation.resultSurface().toString().contains("noProductionSideEffects=true"));
    assertBrowserSafe(toolBoundarySimulation.resultSurface());

    var toolBoundarySubmit = runAction(new CapabilityActionRequest(
        "action-agent-tool-boundary-submit-review",
        "action-agent-tool-boundary-submit-review",
        "agent_admin.submit_behavior_change_for_review",
        "agent_admin.submit_behavior_change_for_review",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-tool-boundary-submit",
        ADMIN_CONTEXT_ID,
        directToolBoundary.surfaceId(),
        "corr-agent-admin-tool-boundary-submit"));
    assertTrue(toolBoundarySubmit.status().equals("approval-required") || toolBoundarySubmit.status().equals("denied"));
    assertEquals("surface-agent-behavior-proposal", toolBoundarySubmit.resultSurface().surfaceId());
    assertTrue(toolBoundarySubmit.message().contains("active ToolPermissionBoundary grants remain unchanged"));
    assertBrowserSafe(toolBoundarySubmit.resultSurface());

    var toolBoundaryRepeatedSubmit = runAction(new CapabilityActionRequest(
        "action-agent-tool-boundary-submit-review",
        "action-agent-tool-boundary-submit-review",
        "agent_admin.submit_behavior_change_for_review",
        "agent_admin.submit_behavior_change_for_review",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-tool-boundary-submit",
        ADMIN_CONTEXT_ID,
        directToolBoundary.surfaceId(),
        "corr-agent-admin-tool-boundary-submit-repeat"));
    assertTrue(toolBoundaryRepeatedSubmit.status().equals("approval-required") || toolBoundaryRepeatedSubmit.status().equals("denied"));
    assertEquals("surface-agent-behavior-proposal", toolBoundaryRepeatedSubmit.resultSurface().surfaceId());
    assertTrue(toolBoundaryRepeatedSubmit.message().contains("active ToolPermissionBoundary grants remain unchanged"));
    assertBrowserSafe(toolBoundaryRepeatedSubmit.resultSurface());

    var toolBoundaryApprove = runAction(new CapabilityActionRequest(
        "action-agent-tool-boundary-approve",
        "action-agent-tool-boundary-approve",
        "agent_admin.approve_behavior_change",
        "agent_admin.approve_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-tool-boundary-approve",
        ADMIN_CONTEXT_ID,
        directToolBoundary.surfaceId(),
        "corr-agent-admin-tool-boundary-approve"));
    assertTrue(toolBoundaryApprove.status().equals("approval-required") || toolBoundaryApprove.status().equals("denied"));
    assertEquals("surface-agent-behavior-proposal", toolBoundaryApprove.resultSurface().surfaceId());
    assertTrue(toolBoundaryApprove.message().contains("activation remains blocked until a separate confirmation surface"));
    assertBrowserSafe(toolBoundaryApprove.resultSurface());

    var toolBoundaryRejectMissingReason = runAction(new CapabilityActionRequest(
        "action-agent-tool-boundary-reject",
        "action-agent-tool-boundary-reject",
        "agent_admin.reject_behavior_change",
        "agent_admin.reject_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-tool-boundary-reject-missing-reason",
        ADMIN_CONTEXT_ID,
        directToolBoundary.surfaceId(),
        "corr-agent-admin-tool-boundary-reject-missing-reason"));
    assertEquals("validation-error", toolBoundaryRejectMissingReason.status());
    assertEquals("surface-agent-tool-boundary-diff", toolBoundaryRejectMissingReason.resultSurface().surfaceId());
    assertTrue(toolBoundaryRejectMissingReason.message().contains("requires a human-readable reason"));
    assertBrowserSafe(toolBoundaryRejectMissingReason.resultSurface());

    var toolBoundaryReject = runAction(new CapabilityActionRequest(
        "action-agent-tool-boundary-reject",
        "action-agent-tool-boundary-reject",
        "agent_admin.reject_behavior_change",
        "agent_admin.reject_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "reason", "Keep side-effecting grants disabled until provider and policy evidence is complete."),
        "idem-agent-tool-boundary-reject",
        ADMIN_CONTEXT_ID,
        directToolBoundary.surfaceId(),
        "corr-agent-admin-tool-boundary-reject"));
    assertTrue(toolBoundaryReject.status().equals("accepted") || toolBoundaryReject.status().equals("denied"));
    assertEquals("surface-agent-behavior-proposal", toolBoundaryReject.resultSurface().surfaceId());
    assertTrue(toolBoundaryReject.message().contains("active ToolPermissionBoundary grants and tenant scope remain unchanged"));
    assertBrowserSafe(toolBoundaryReject.resultSurface());

    var toolBoundaryModelRefs = runAction(new CapabilityActionRequest(
        "action-agent-tool-boundary-open-model-refs",
        "action-agent-tool-boundary-open-model-refs",
        "agent_admin.get_model_ref",
        "agent_admin.get_model_ref",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        directToolBoundary.surfaceId(),
        "corr-agent-admin-tool-boundary-model-refs"));
    assertEquals("accepted", toolBoundaryModelRefs.status());
    assertEquals("surface-agent-model-refs", toolBoundaryModelRefs.resultSurface().surfaceId());
    assertBrowserSafe(toolBoundaryModelRefs.resultSurface());

    var toolBoundaryTrace = runAction(new CapabilityActionRequest(
        "action-agent-tool-boundary-open-trace",
        "action-agent-tool-boundary-open-trace",
        "audit.trace.read",
        "audit.trace.read",
        Map.of("traceId", "trace-agent-admin-tool-boundary"),
        null,
        ADMIN_CONTEXT_ID,
        directToolBoundary.surfaceId(),
        "corr-agent-admin-tool-boundary-trace"));
    assertEquals("accepted", toolBoundaryTrace.status());
    assertEquals("surface-agent-admin-trace", toolBoundaryTrace.resultSurface().surfaceId());
    assertTrue(toolBoundaryTrace.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-tool-boundary-open-trace")));
    assertBrowserSafe(toolBoundaryTrace.resultSurface());

    var toolBoundaryBackToDetail = runAction(new CapabilityActionRequest(
        "action-agent-tool-boundary-back-to-detail",
        "action-agent-tool-boundary-back-to-detail",
        "agent_admin.get_definition",
        "agent_admin.get_definition",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        directToolBoundary.surfaceId(),
        "corr-agent-admin-tool-boundary-detail-return"));
    assertEquals("accepted", toolBoundaryBackToDetail.status());
    assertEquals("surface-agent-admin-detail", toolBoundaryBackToDetail.resultSurface().surfaceId());
    assertBrowserSafe(toolBoundaryBackToDetail.resultSurface());

    var skillModelRefs = runAction(new CapabilityActionRequest(
        "action-agent-skill-manifest-open-model-refs",
        "action-agent-skill-manifest-open-model-refs",
        "agent_admin.get_model_ref",
        "agent_admin.get_model_ref",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        skillManifest.resultSurface().surfaceId(),
        "corr-agent-admin-skill-manifest-model-refs"));
    assertEquals("accepted", skillModelRefs.status());
    assertEquals("surface-agent-model-refs", skillModelRefs.resultSurface().surfaceId());
    assertBrowserSafe(skillModelRefs.resultSurface());

    var directModelRefs = getSurface("surface-agent-model-refs", "corr-agent-admin-model-refs-direct");
    assertEquals("surface-agent-model-refs", directModelRefs.surfaceId());
    assertEquals("governance-diff", directModelRefs.surfaceType());
    assertEquals("agent_admin.model_refs.v1", directModelRefs.data().get("surfaceContract"));
    assertEquals("corr-agent-admin-model-refs-direct", directModelRefs.correlationId());
    assertTrue(directModelRefs.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-surface-agent-model-refs")));
    var modelReferenceSummary = (Map<String, Object>) directModelRefs.data().get("modelReferenceSummary");
    assertEquals("agent_admin.model_refs.v1", modelReferenceSummary.get("contract"));
    var modelProviderCategory = String.valueOf(modelReferenceSummary.get("providerReadinessCategory"));
    assertTrue(modelProviderCategory.equals("ready") || modelProviderCategory.equals("blocked_provider_or_runtime"));
    assertEquals(Boolean.TRUE, modelReferenceSummary.get("approvalRequired"));
    assertEquals(Boolean.TRUE, modelReferenceSummary.get("noDirectActivation"));
    var modelScope = (Map<String, Object>) directModelRefs.data().get("scopeSummary");
    assertEquals(ADMIN_CONTEXT_ID, modelScope.get("selectedAuthContextId"));
    assertEquals(Boolean.TRUE, modelScope.get("governanceAuthorized"));
    var providerReadiness = (Map<String, Object>) directModelRefs.data().get("providerReadiness");
    assertEquals(modelProviderCategory, providerReadiness.get("category"));
    assertEquals(Boolean.valueOf(!"ready".equals(modelProviderCategory)), providerReadiness.get("noFakeSuccess"));
    var reviewState = (Map<String, Object>) directModelRefs.data().get("reviewState");
    assertEquals(Boolean.TRUE, reviewState.get("approvalRequired"));
    assertEquals(Boolean.TRUE, reviewState.get("rejectionReasonRequired"));
    assertEquals(Boolean.TRUE, reviewState.get("noDirectMutation"));
    assertTrue(directModelRefs.toString().contains("redactedModelReferenceDiff"));
    assertTrue(directModelRefs.toString().contains("provider credentials omitted"));
    assertTrue(directModelRefs.toString().contains("rawProviderModelResponses=omitted"));
    assertTrue(directModelRefs.toString().contains("noFakeSuccess="));
    assertTrue(directModelRefs.toString().contains("noDirectActivation=true"));
    assertTrue(directModelRefs.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-model-refs-refresh") && action.resultSurface().updateSurfaceId().equals("surface-agent-model-refs")));
    assertTrue(directModelRefs.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-model-refs-run-test") && action.resultSurface().updateSurfaceId().equals("surface-agent-test-console")));
    assertTrue(directModelRefs.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-model-refs-submit-review") && action.resultSurface().updateSurfaceId().equals("surface-agent-behavior-proposal")));
    assertTrue(directModelRefs.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-model-refs-approve") && action.resultSurface().updateSurfaceId().equals("surface-agent-behavior-proposal")));
    assertTrue(directModelRefs.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-model-refs-reject") && action.resultSurface().updateSurfaceId().equals("surface-agent-behavior-proposal")));
    assertTrue(directModelRefs.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-model-refs-open-prompt-governance") && action.resultSurface().updateSurfaceId().equals("surface-agent-prompt-governance")));
    assertTrue(directModelRefs.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-model-refs-open-tool-boundary") && action.resultSurface().updateSurfaceId().equals("surface-agent-tool-boundary-diff")));
    assertTrue(directModelRefs.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-model-refs-open-trace") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-trace")));
    assertTrue(directModelRefs.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-model-refs-back-to-detail") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-detail")));
    assertBrowserSafe(directModelRefs);

    var modelRefsRefresh = runAction(new CapabilityActionRequest(
        "action-agent-model-refs-refresh",
        "action-agent-model-refs-refresh",
        "agent_admin.get_model_ref",
        "agent_admin.get_model_ref",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        directModelRefs.surfaceId(),
        "corr-agent-admin-model-refs-refresh"));
    assertEquals("no-op", modelRefsRefresh.status());
    assertEquals("surface-agent-model-refs", modelRefsRefresh.resultSurface().surfaceId());
    assertBrowserSafe(modelRefsRefresh.resultSurface());

    var modelRefsRunTest = runAction(new CapabilityActionRequest(
        "action-agent-model-refs-run-test",
        "action-agent-model-refs-run-test",
        "agent_admin.draft_behavior_change",
        "agent_admin.draft_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-model-refs-run-test",
        ADMIN_CONTEXT_ID,
        directModelRefs.surfaceId(),
        "corr-agent-admin-model-refs-run-test"));
    assertEquals("blocked_provider_or_runtime", modelRefsRunTest.status());
    assertEquals("surface-agent-test-console", modelRefsRunTest.resultSurface().surfaceId());
    assertTrue(modelRefsRunTest.message().contains("no fixture/model-less success was counted"));
    assertTrue(modelRefsRunTest.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-model-refs-provider-fail-closed")));
    assertTrue(modelRefsRunTest.resultSurface().toString().contains("agent_admin.test_console.v1"));
    assertTrue(modelRefsRunTest.resultSurface().toString().contains("noProductionSideEffects=true"));
    assertTrue(modelRefsRunTest.resultSurface().toString().contains("noFakeSuccess=true"));
    assertBrowserSafe(modelRefsRunTest.resultSurface());

    var modelRefsSubmit = runAction(new CapabilityActionRequest(
        "action-agent-model-refs-submit-review",
        "action-agent-model-refs-submit-review",
        "agent_admin.submit_behavior_change_for_review",
        "agent_admin.submit_behavior_change_for_review",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-model-refs-submit",
        ADMIN_CONTEXT_ID,
        directModelRefs.surfaceId(),
        "corr-agent-admin-model-refs-submit"));
    assertTrue(modelRefsSubmit.status().equals("approval-required") || modelRefsSubmit.status().equals("denied"));
    assertEquals("surface-agent-behavior-proposal", modelRefsSubmit.resultSurface().surfaceId());
    assertTrue(modelRefsSubmit.message().contains("active model/provider routing remains unchanged"));
    assertBrowserSafe(modelRefsSubmit.resultSurface());

    var modelRefsRepeatedSubmit = runAction(new CapabilityActionRequest(
        "action-agent-model-refs-submit-review",
        "action-agent-model-refs-submit-review",
        "agent_admin.submit_behavior_change_for_review",
        "agent_admin.submit_behavior_change_for_review",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-model-refs-submit",
        ADMIN_CONTEXT_ID,
        directModelRefs.surfaceId(),
        "corr-agent-admin-model-refs-submit-repeat"));
    assertTrue(modelRefsRepeatedSubmit.status().equals("approval-required") || modelRefsRepeatedSubmit.status().equals("denied"));
    assertEquals("surface-agent-behavior-proposal", modelRefsRepeatedSubmit.resultSurface().surfaceId());
    assertTrue(modelRefsRepeatedSubmit.message().contains("active model/provider routing remains unchanged"));
    assertBrowserSafe(modelRefsRepeatedSubmit.resultSurface());

    var modelRefsApprove = runAction(new CapabilityActionRequest(
        "action-agent-model-refs-approve",
        "action-agent-model-refs-approve",
        "agent_admin.approve_behavior_change",
        "agent_admin.approve_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-model-refs-approve",
        ADMIN_CONTEXT_ID,
        directModelRefs.surfaceId(),
        "corr-agent-admin-model-refs-approve"));
    assertTrue(modelRefsApprove.status().equals("approval-required") || modelRefsApprove.status().equals("denied"));
    assertEquals("surface-agent-behavior-proposal", modelRefsApprove.resultSurface().surfaceId());
    assertTrue(modelRefsApprove.message().contains("activation remains blocked until a separate confirmation surface"));
    assertBrowserSafe(modelRefsApprove.resultSurface());

    var modelRefsRejectMissingReason = runAction(new CapabilityActionRequest(
        "action-agent-model-refs-reject",
        "action-agent-model-refs-reject",
        "agent_admin.reject_behavior_change",
        "agent_admin.reject_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-model-refs-reject-missing-reason",
        ADMIN_CONTEXT_ID,
        directModelRefs.surfaceId(),
        "corr-agent-admin-model-refs-reject-missing-reason"));
    assertEquals("validation-error", modelRefsRejectMissingReason.status());
    assertEquals("surface-agent-model-refs", modelRefsRejectMissingReason.resultSurface().surfaceId());
    assertTrue(modelRefsRejectMissingReason.message().contains("requires a human-readable reason"));
    assertBrowserSafe(modelRefsRejectMissingReason.resultSurface());

    var modelRefsReject = runAction(new CapabilityActionRequest(
        "action-agent-model-refs-reject",
        "action-agent-model-refs-reject",
        "agent_admin.reject_behavior_change",
        "agent_admin.reject_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "reason", "Keep provider routing stable until real provider readiness evidence is complete."),
        "idem-agent-model-refs-reject",
        ADMIN_CONTEXT_ID,
        directModelRefs.surfaceId(),
        "corr-agent-admin-model-refs-reject"));
    assertTrue(modelRefsReject.status().equals("accepted") || modelRefsReject.status().equals("denied"));
    assertEquals("surface-agent-behavior-proposal", modelRefsReject.resultSurface().surfaceId());
    assertTrue(modelRefsReject.message().contains("active model/provider routing remains unchanged"));
    assertBrowserSafe(modelRefsReject.resultSurface());

    var modelRefsPromptGovernance = runAction(new CapabilityActionRequest(
        "action-agent-model-refs-open-prompt-governance",
        "action-agent-model-refs-open-prompt-governance",
        "agent_admin.get_prompt_version",
        "agent_admin.get_prompt_version",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        directModelRefs.surfaceId(),
        "corr-agent-admin-model-refs-prompt-governance"));
    assertEquals("accepted", modelRefsPromptGovernance.status());
    assertEquals("surface-agent-prompt-governance", modelRefsPromptGovernance.resultSurface().surfaceId());
    assertBrowserSafe(modelRefsPromptGovernance.resultSurface());

    var modelRefsToolBoundary = runAction(new CapabilityActionRequest(
        "action-agent-model-refs-open-tool-boundary",
        "action-agent-model-refs-open-tool-boundary",
        "agent_admin.get_tool_boundary",
        "agent_admin.get_tool_boundary",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        directModelRefs.surfaceId(),
        "corr-agent-admin-model-refs-tool-boundary"));
    assertEquals("accepted", modelRefsToolBoundary.status());
    assertEquals("surface-agent-tool-boundary-diff", modelRefsToolBoundary.resultSurface().surfaceId());
    assertBrowserSafe(modelRefsToolBoundary.resultSurface());

    var modelRefsTrace = runAction(new CapabilityActionRequest(
        "action-agent-model-refs-open-trace",
        "action-agent-model-refs-open-trace",
        "audit.trace.read",
        "audit.trace.read",
        Map.of("traceId", "trace-agent-admin-model-refs"),
        null,
        ADMIN_CONTEXT_ID,
        directModelRefs.surfaceId(),
        "corr-agent-admin-model-refs-trace"));
    assertEquals("accepted", modelRefsTrace.status());
    assertEquals("surface-agent-admin-trace", modelRefsTrace.resultSurface().surfaceId());
    assertTrue(modelRefsTrace.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-model-refs-open-trace")));
    assertBrowserSafe(modelRefsTrace.resultSurface());

    var modelRefsBackToDetail = runAction(new CapabilityActionRequest(
        "action-agent-model-refs-back-to-detail",
        "action-agent-model-refs-back-to-detail",
        "agent_admin.get_definition",
        "agent_admin.get_definition",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        directModelRefs.surfaceId(),
        "corr-agent-admin-model-refs-detail-return"));
    assertEquals("accepted", modelRefsBackToDetail.status());
    assertEquals("surface-agent-admin-detail", modelRefsBackToDetail.resultSurface().surfaceId());
    assertBrowserSafe(modelRefsBackToDetail.resultSurface());

    var skillTrace = runAction(new CapabilityActionRequest(
        "action-agent-skill-manifest-open-trace",
        "action-agent-skill-manifest-open-trace",
        "audit.trace.read",
        "audit.trace.read",
        Map.of("traceId", "trace-agent-admin-skill-manifest"),
        null,
        ADMIN_CONTEXT_ID,
        skillManifest.resultSurface().surfaceId(),
        "corr-agent-admin-skill-manifest-trace"));
    assertEquals("accepted", skillTrace.status());
    assertEquals("surface-agent-admin-trace", skillTrace.resultSurface().surfaceId());
    assertTrue(skillTrace.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-skill-manifest-open-trace")));
    assertTrue(skillTrace.resultSurface().toString().contains("SkillLoadTrace"));
    assertBrowserSafe(skillTrace.resultSurface());

    var skillBackToDetail = runAction(new CapabilityActionRequest(
        "action-agent-skill-manifest-back-to-detail",
        "action-agent-skill-manifest-back-to-detail",
        "agent_admin.get_definition",
        "agent_admin.get_definition",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        skillManifest.resultSurface().surfaceId(),
        "corr-agent-admin-skill-manifest-detail-return"));
    assertEquals("accepted", skillBackToDetail.status());
    assertEquals("surface-agent-admin-detail", skillBackToDetail.resultSurface().surfaceId());
    assertBrowserSafe(skillBackToDetail.resultSurface());

    assertDetailActionRoutes("action-agent-detail-open-tool-boundary", "agent_admin.get_tool_boundary", "surface-agent-tool-boundary-diff", "accepted", "corr-agent-admin-detail-tool-boundary");
    var noSideEffectTest = assertDetailActionRoutes("action-agent-detail-run-test", "agent_admin.draft_behavior_change", "surface-agent-test-console", "accepted", "corr-agent-admin-detail-run-test");
    assertTrue(noSideEffectTest.resultSurface().toString().contains("noProductionSideEffects=true"));
    assertTrue(noSideEffectTest.resultSurface().toString().contains("agent_admin.test_console.v1"));
    assertTrue(noSideEffectTest.resultSurface().toString().contains("blocked_provider_or_runtime"));
    assertTrue(noSideEffectTest.resultSurface().toString().contains("PromptAssemblyTrace"));
    assertTrue(noSideEffectTest.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-agent-test-console-run") && action.resultSurface().updateSurfaceId().equals("surface-agent-test-console")));
    assertTrue(noSideEffectTest.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-agent-test-console-open-trace") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-trace")));
    assertTrue(noSideEffectTest.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-agent-test-console-back-to-detail") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-detail")));
    var promptRiskStatus = assertDetailActionRoutes("action-agent-detail-open-prompt-risk-review", "agent_admin.prompt_risk_review.read", "surface-agent-admin-prompt-risk-review", "accepted", "corr-agent-admin-detail-prompt-risk");
    assertTrue(promptRiskStatus.resultSurface().toString().contains("blocked_provider_or_runtime"));
    assertTrue(promptRiskStatus.resultSurface().toString().contains("activationBlockedUntilHumanDecision=true"));
    assertDetailActionRoutes("action-agent-detail-open-activation", "agent.definitions.manage", "surface-agent-activation-confirmation", "approval-required", "corr-agent-admin-detail-activation");
    assertDetailActionRoutes("action-agent-detail-open-deactivation", "agent.definitions.manage", "surface-agent-deactivation-confirmation", "approval-required", "corr-agent-admin-detail-deactivation");
    assertDetailActionRoutes("action-agent-detail-open-rollback", "agent_admin.rollback_behavior_change", "surface-agent-rollback-confirmation", "approval-required", "corr-agent-admin-detail-rollback");
    assertDetailActionRoutes("action-agent-detail-back-to-catalog", "agent_admin.list_definitions", "surface-agent-admin-catalog", "accepted", "corr-agent-admin-detail-back-to-catalog");

    var hiddenRow = runAction(new CapabilityActionRequest(
        "action-open-agent-detail",
        "action-open-agent-detail",
        "agent_admin.get_definition",
        "agent_admin.get_definition",
        Map.of("agentDefinitionId", "hidden-or-stale-agent-row"),
        null,
        ADMIN_CONTEXT_ID,
        catalog.surfaceId(),
        "corr-agent-admin-detail-hidden-row"));
    assertEquals("accepted", hiddenRow.status());
    assertEquals("surface-agent-admin-detail", hiddenRow.resultSurface().surfaceId());
    assertEquals("not_found_or_redacted", ((Map<String, Object>) hiddenRow.resultSurface().data().get("scopeSummary")).get("visibilityDecision"));
    assertTrue(hiddenRow.resultSurface().toString().contains("empty-hidden-or-stale-selection"));
    assertFalse(hiddenRow.resultSurface().toString().contains("hidden-or-stale-agent-row"));
    assertBrowserSafe(hiddenRow.resultSurface());

    var trace = runAction(new CapabilityActionRequest(
        "action-agent-admin-catalog-open-trace",
        "action-agent-admin-catalog-open-trace",
        "audit.trace.read",
        "audit.trace.read",
        Map.of("traceId", "trace-agent-admin-catalog"),
        null,
        ADMIN_CONTEXT_ID,
        catalog.surfaceId(),
        "corr-agent-admin-catalog-trace"));
    assertEquals("accepted", trace.status());
    assertEquals("surface-agent-admin-trace", trace.resultSurface().surfaceId());
    assertTrue(trace.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-admin-catalog-open-trace")));
    assertBrowserSafe(trace.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-admin-catalog",
        "corr-agent-admin-catalog-member-denied",
        "workos-member",
        "member@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Agent Admin catalog rows or counts.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-admin-detail",
        "corr-agent-admin-detail-member-denied",
        "workos-member",
        "member@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Agent Admin detail payloads.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-prompt-governance",
        "corr-agent-admin-prompt-governance-member-denied",
        "workos-member",
        "member@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Agent Admin prompt-governance payloads or prompt metadata.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-skill-manifest-diff",
        "corr-agent-admin-skill-manifest-member-denied",
        "workos-member",
        "member@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Agent Admin skill-manifest diff payloads or compact manifest metadata.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-tool-boundary-diff",
        "corr-agent-admin-tool-boundary-member-denied",
        "workos-member",
        "member@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Agent Admin tool-boundary payloads, grant metadata, or denial counts.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-model-refs",
        "corr-agent-admin-model-refs-member-denied",
        "workos-member",
        "member@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Agent Admin model-reference payloads, provider aliases, or readiness details.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-admin-catalog",
        "corr-agent-admin-catalog-customer-denied",
        "workos-customer",
        "customer@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped contexts must not expose Agent Admin catalog rows or tenant governance counts.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-admin-detail",
        "corr-agent-admin-detail-customer-denied",
        "workos-customer",
        "customer@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped contexts must not expose Agent Admin detail rows or tenant governance state.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-prompt-governance",
        "corr-agent-admin-prompt-governance-customer-denied",
        "workos-customer",
        "customer@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped contexts must not expose Agent Admin prompt-governance rows or prompt metadata.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-skill-manifest-diff",
        "corr-agent-admin-skill-manifest-customer-denied",
        "workos-customer",
        "customer@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped contexts must not expose Agent Admin skill-manifest diff rows, manifest ids, or reference metadata.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-tool-boundary-diff",
        "corr-agent-admin-tool-boundary-customer-denied",
        "workos-customer",
        "customer@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped contexts must not expose Agent Admin tool-boundary rows, grants, hidden ids, or tenant governance state.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-model-refs",
        "corr-agent-admin-model-refs-customer-denied",
        "workos-customer",
        "customer@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped contexts must not expose Agent Admin model-reference rows, provider aliases, hidden ids, or tenant governance state.");
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedWorkstreamApiExercisesAgentActivationConfirmationRuntimeTestingPath() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-activation-confirmation")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin activation confirmation must reject missing bearer tokens.");
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-agent-activation-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-agent-activation-confirm",
            "action-agent-activation-confirm",
            "agent_admin.activate_behavior_change",
            "agent_admin.activate_behavior_change",
            Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "acknowledgement", "ACTIVATE"),
            "idem-agent-activation-missing-bearer",
            ADMIN_CONTEXT_ID,
            "surface-agent-activation-confirmation",
            "corr-agent-activation-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin activation confirmation action path must reject missing bearer tokens.");

    var directActivation = getSurface("surface-agent-activation-confirmation", "corr-agent-activation-direct");
    assertEquals("surface-agent-activation-confirmation", directActivation.surfaceId());
    assertEquals("lifecycle-confirmation", directActivation.surfaceType());
    assertEquals("agent_admin.activation_confirmation.v1", directActivation.data().get("surfaceContract"));
    assertEquals("corr-agent-activation-direct", directActivation.correlationId());
    assertEquals(Boolean.TRUE, directActivation.data().get("noDirectMutation"));
    assertEquals(Boolean.TRUE, directActivation.data().get("noFakeSuccess"));
    assertFalse(directActivation.traceIds().isEmpty());
    assertTrue(directActivation.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-surface-agent-activation-confirmation")));
    assertTrue(directActivation.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-activation-refresh") && action.resultSurface().updateSurfaceId().equals("surface-agent-activation-confirmation")));
    assertTrue(directActivation.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-activation-confirm") && action.resultSurface().updateSurfaceId().equals("surface-agent-behavior-proposal")));
    assertTrue(directActivation.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-activation-cancel") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-detail")));
    assertTrue(directActivation.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-activation-open-proposal") && action.resultSurface().updateSurfaceId().equals("surface-agent-behavior-proposal")));
    assertTrue(directActivation.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-activation-open-trace") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-trace")));

    var activationSummary = (Map<String, Object>) directActivation.data().get("activationSummary");
    assertEquals("surface-agent-activation-confirmation", activationSummary.get("surfaceId"));
    assertEquals("agent_admin.activation_confirmation.v1", activationSummary.get("contract"));
    assertEquals("provider-fail-closed", activationSummary.get("readinessState"));
    assertEquals("blocked_until_approved_version", activationSummary.get("approvedVersionLabel"));
    var scopeSummary = (Map<String, Object>) directActivation.data().get("scopeSummary");
    assertEquals(ADMIN_CONTEXT_ID, scopeSummary.get("selectedAuthContextId"));
    assertEquals("tenant", scopeSummary.get("scopeType"));
    assertEquals(Boolean.TRUE, scopeSummary.get("governanceAuthorized"));
    assertEquals("visible", scopeSummary.get("visibilityDecision"));
    var approvalSummary = (Map<String, Object>) directActivation.data().get("approvalSummary");
    assertEquals("missing-approved-candidate", approvalSummary.get("humanApprovalStatus"));
    assertEquals("blocked_provider_or_runtime", approvalSummary.get("providerRuntimeReadiness"));
    assertEquals(Boolean.TRUE, approvalSummary.get("noFakeSuccess"));
    var confirmationState = (Map<String, Object>) directActivation.data().get("confirmationState");
    assertEquals(Boolean.TRUE, confirmationState.get("acknowledgementRequired"));
    assertEquals("ACTIVATE", confirmationState.get("requiredAcknowledgementText"));
    assertEquals("blocked_provider_or_runtime", confirmationState.get("providerFailClosedState"));
    assertTrue(((List<String>) confirmationState.get("disabledActions")).contains("confirm"));
    assertTrue(directActivation.toString().contains("rawPromptText=omitted"));
    assertTrue(directActivation.toString().contains("rawSkillReferenceBodies=omitted"));
    assertTrue(directActivation.toString().contains("providerCredentials=omitted"));
    assertTrue(directActivation.toString().contains("bearerTokens=omitted"));
    assertBrowserSafe(directActivation);

    var activationFromDetail = assertDetailActionRoutes(
        "action-agent-detail-open-activation",
        "agent.definitions.manage",
        "surface-agent-activation-confirmation",
        "approval-required",
        "corr-agent-activation-from-detail");
    assertTrue(activationFromDetail.resultSurface().toString().contains("agent_admin.activation_confirmation.v1"));
    assertTrue(activationFromDetail.resultSurface().toString().contains("noDirectMutation=true"));
    assertTrue(activationFromDetail.resultSurface().toString().contains("noFakeSuccess=true"));

    var refresh = runAction(new CapabilityActionRequest(
        "action-agent-activation-refresh",
        "action-agent-activation-refresh",
        "agent.definitions.manage",
        "agent.definitions.manage",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        directActivation.surfaceId(),
        "corr-agent-activation-refresh"));
    assertEquals("no-op", refresh.status());
    assertEquals("surface-agent-activation-confirmation", refresh.resultSurface().surfaceId());
    assertEquals("corr-agent-activation-refresh", refresh.correlationId());
    assertTrue(refresh.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-activation-refresh")));
    assertTrue(refresh.message().contains("no lifecycle mutation occurred"));
    assertBrowserSafe(refresh.resultSurface());

    var missingAck = runAction(new CapabilityActionRequest(
        "action-agent-activation-confirm",
        "action-agent-activation-confirm",
        "agent_admin.activate_behavior_change",
        "agent_admin.activate_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-activation-missing-ack",
        ADMIN_CONTEXT_ID,
        directActivation.surfaceId(),
        "corr-agent-activation-missing-ack"));
    assertEquals("validation-error", missingAck.status());
    assertEquals("surface-agent-activation-confirmation", missingAck.resultSurface().surfaceId());
    assertTrue(missingAck.message().contains("requires the explicit ACTIVATE acknowledgement"));
    assertTrue(missingAck.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-activation-acknowledgement-required")));
    assertBrowserSafe(missingAck.resultSurface());

    var providerBlocked = runAction(new CapabilityActionRequest(
        "action-agent-activation-confirm",
        "action-agent-activation-confirm",
        "agent_admin.activate_behavior_change",
        "agent_admin.activate_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "acknowledgement", "ACTIVATE"),
        "idem-agent-activation-confirm",
        ADMIN_CONTEXT_ID,
        directActivation.surfaceId(),
        "corr-agent-activation-confirm"));
    assertEquals("approval-required", providerBlocked.status());
    assertEquals("surface-agent-activation-confirmation", providerBlocked.resultSurface().surfaceId());
    assertTrue(providerBlocked.message().contains("failed closed with no lifecycle mutation"));
    assertTrue(providerBlocked.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-activation-provider-fail-closed")));
    assertTrue(providerBlocked.resultSurface().toString().contains("provider-fail-closed"));
    assertTrue(providerBlocked.resultSurface().toString().contains("noFakeSuccess=true"));
    assertTrue(providerBlocked.resultSurface().toString().contains("noDirectMutation=true"));
    assertBrowserSafe(providerBlocked.resultSurface());

    var repeatedProviderBlocked = runAction(new CapabilityActionRequest(
        "action-agent-activation-confirm",
        "action-agent-activation-confirm",
        "agent_admin.activate_behavior_change",
        "agent_admin.activate_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "acknowledgement", "ACTIVATE"),
        "idem-agent-activation-confirm",
        ADMIN_CONTEXT_ID,
        directActivation.surfaceId(),
        "corr-agent-activation-confirm-repeat"));
    assertEquals("approval-required", repeatedProviderBlocked.status());
    assertEquals("surface-agent-activation-confirmation", repeatedProviderBlocked.resultSurface().surfaceId());
    assertTrue(repeatedProviderBlocked.message().contains("failed closed with no lifecycle mutation"));
    assertBrowserSafe(repeatedProviderBlocked.resultSurface());

    var proposal = runAction(new CapabilityActionRequest(
        "action-agent-activation-open-proposal",
        "action-agent-activation-open-proposal",
        "agent.definitions.manage",
        "agent.definitions.manage",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "proposalId", "proposal-agent-admin-activation-required"),
        null,
        ADMIN_CONTEXT_ID,
        directActivation.surfaceId(),
        "corr-agent-activation-proposal"));
    assertEquals("accepted", proposal.status());
    assertEquals("surface-agent-behavior-proposal", proposal.resultSurface().surfaceId());
    assertBrowserSafe(proposal.resultSurface());

    var trace = runAction(new CapabilityActionRequest(
        "action-agent-activation-open-trace",
        "action-agent-activation-open-trace",
        "audit.trace.read",
        "audit.trace.read",
        Map.of("traceId", "trace-agent-definition-activate-confirmation"),
        null,
        ADMIN_CONTEXT_ID,
        directActivation.surfaceId(),
        "corr-agent-activation-trace"));
    assertEquals("accepted", trace.status());
    assertEquals("surface-agent-admin-trace", trace.resultSurface().surfaceId());
    assertTrue(trace.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-activation-open-trace")));
    assertBrowserSafe(trace.resultSurface());

    var cancel = runAction(new CapabilityActionRequest(
        "action-agent-activation-cancel",
        "action-agent-activation-cancel",
        "agent.definitions.manage",
        "agent.definitions.manage",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-activation-cancel",
        ADMIN_CONTEXT_ID,
        directActivation.surfaceId(),
        "corr-agent-activation-cancel"));
    assertEquals("accepted", cancel.status());
    assertEquals("surface-agent-admin-detail", cancel.resultSurface().surfaceId());
    assertTrue(cancel.message().contains("lifecycle state was not changed"));
    assertTrue(cancel.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-activation-cancel")));
    assertBrowserSafe(cancel.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-activation-confirmation",
        "corr-agent-activation-member-denied",
        "workos-member",
        "member@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Agent Admin activation confirmation or lifecycle blockers.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-activation-confirmation",
        "corr-agent-activation-customer-denied",
        "workos-customer",
        "customer@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped contexts must not expose Agent Admin activation confirmation, proposal state, or tenant governance blockers.");
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedWorkstreamApiExercisesAgentDeactivationConfirmationRuntimeTestingPath() throws Exception {
    var agentRepository = new AkkaAgentBehaviorRepository(componentClient);
    try {
      assertThrows(IllegalArgumentException.class, () -> httpClient
          .GET("/api/workstream/surfaces/surface-agent-deactivation-confirmation")
          .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
          .responseBodyAs(String.class)
          .invoke(), "Protected Agent Admin deactivation confirmation must reject missing bearer tokens.");
      assertThrows(IllegalArgumentException.class, () -> httpClient
          .POST("/api/workstream/actions")
          .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
          .addHeader("X-Correlation-Id", "corr-agent-deactivation-missing-bearer-action")
          .withRequestBody(new CapabilityActionRequest(
              "action-agent-deactivation-confirm",
              "action-agent-deactivation-confirm",
              "agent.definitions.manage",
              "agent.definitions.manage",
              Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "acknowledgement", "DEACTIVATE", "reason", "Retire unsafe behavior"),
              "idem-agent-deactivation-missing-bearer",
              ADMIN_CONTEXT_ID,
              "surface-agent-deactivation-confirmation",
              "corr-agent-deactivation-missing-bearer-action"))
          .responseBodyAs(String.class)
          .invoke(), "Protected Agent Admin deactivation confirmation action path must reject missing bearer tokens.");

      var directDeactivation = getSurface("surface-agent-deactivation-confirmation", "corr-agent-deactivation-direct");
      assertEquals("surface-agent-deactivation-confirmation", directDeactivation.surfaceId());
      assertEquals("lifecycle-confirmation", directDeactivation.surfaceType());
      assertEquals("agent_admin.deactivation_confirmation.v1", directDeactivation.data().get("surfaceContract"));
      assertEquals("corr-agent-deactivation-direct", directDeactivation.correlationId());
      assertEquals(Boolean.TRUE, directDeactivation.data().get("noDirectMutation"));
      assertEquals(Boolean.TRUE, directDeactivation.data().get("noFakeSuccess"));
      assertFalse(directDeactivation.traceIds().isEmpty());
      assertTrue(directDeactivation.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-surface-agent-deactivation-confirmation")));
      assertTrue(directDeactivation.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-deactivation-refresh") && action.resultSurface().updateSurfaceId().equals("surface-agent-deactivation-confirmation")));
      assertTrue(directDeactivation.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-deactivation-confirm") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-detail")));
      assertTrue(directDeactivation.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-deactivation-cancel") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-detail")));
      assertTrue(directDeactivation.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-deactivation-open-proposal") && action.resultSurface().updateSurfaceId().equals("surface-agent-behavior-proposal")));
      assertTrue(directDeactivation.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-deactivation-open-trace") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-trace")));

      var deactivationSummary = (Map<String, Object>) directDeactivation.data().get("deactivationSummary");
      assertEquals("surface-agent-deactivation-confirmation", deactivationSummary.get("surfaceId"));
      assertEquals("agent_admin.deactivation_confirmation.v1", deactivationSummary.get("contract"));
      assertEquals("deactivated", deactivationSummary.get("proposedLifecycleState"));
      assertEquals("current_active_version", deactivationSummary.get("approvedVersionLabel"));
      var scopeSummary = (Map<String, Object>) directDeactivation.data().get("scopeSummary");
      assertEquals(ADMIN_CONTEXT_ID, scopeSummary.get("selectedAuthContextId"));
      assertEquals("tenant", scopeSummary.get("scopeType"));
      assertEquals(Boolean.TRUE, scopeSummary.get("governanceAuthorized"));
      assertEquals("visible", scopeSummary.get("visibilityDecision"));
      var policyAndApprovalSummary = (Map<String, Object>) directDeactivation.data().get("policyAndApprovalSummary");
      assertEquals("deactivation-allowed-with-confirmation", policyAndApprovalSummary.get("humanApprovalStatus"));
      assertEquals("not_applicable_no_provider_success_claim", policyAndApprovalSummary.get("providerRuntimeReadiness"));
      assertEquals(Boolean.TRUE, policyAndApprovalSummary.get("noFakeSuccess"));
      var confirmationState = (Map<String, Object>) directDeactivation.data().get("confirmationState");
      assertEquals(Boolean.TRUE, confirmationState.get("acknowledgementRequired"));
      assertEquals(Boolean.TRUE, confirmationState.get("reasonRequired"));
      assertEquals("DEACTIVATE", confirmationState.get("requiredAcknowledgementText"));
      assertEquals("not_applicable", confirmationState.get("providerFailClosedState"));
      assertTrue(((List<String>) confirmationState.get("disabledActions")).isEmpty());
      assertTrue(directDeactivation.toString().contains("rawPromptText=omitted"));
      assertTrue(directDeactivation.toString().contains("rawSkillReferenceBodies=omitted"));
      assertTrue(directDeactivation.toString().contains("providerCredentials=omitted"));
      assertTrue(directDeactivation.toString().contains("bearerTokens=omitted"));
      assertBrowserSafe(directDeactivation);

      var deactivationFromDetail = assertDetailActionRoutes(
          "action-agent-detail-open-deactivation",
          "agent.definitions.manage",
          "surface-agent-deactivation-confirmation",
          "approval-required",
          "corr-agent-deactivation-from-detail");
      assertTrue(deactivationFromDetail.resultSurface().toString().contains("agent_admin.deactivation_confirmation.v1"));
      assertTrue(deactivationFromDetail.resultSurface().toString().contains("noDirectMutation=true"));
      assertTrue(deactivationFromDetail.resultSurface().toString().contains("noFakeSuccess=true"));

      var refresh = runAction(new CapabilityActionRequest(
          "action-agent-deactivation-refresh",
          "action-agent-deactivation-refresh",
          "agent.definitions.manage",
          "agent.definitions.manage",
          Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
          null,
          ADMIN_CONTEXT_ID,
          directDeactivation.surfaceId(),
          "corr-agent-deactivation-refresh"));
      assertEquals("no-op", refresh.status());
      assertEquals("surface-agent-deactivation-confirmation", refresh.resultSurface().surfaceId());
      assertEquals("corr-agent-deactivation-refresh", refresh.correlationId());
      assertTrue(refresh.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-deactivation-refresh")));
      assertTrue(refresh.message().contains("no lifecycle mutation occurred"));
      assertBrowserSafe(refresh.resultSurface());

      var missingAck = runAction(new CapabilityActionRequest(
          "action-agent-deactivation-confirm",
          "action-agent-deactivation-confirm",
          "agent.definitions.manage",
          "agent.definitions.manage",
          Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "reason", "Retire unsafe behavior"),
          "idem-agent-deactivation-missing-ack",
          ADMIN_CONTEXT_ID,
          directDeactivation.surfaceId(),
          "corr-agent-deactivation-missing-ack"));
      assertEquals("validation-error", missingAck.status());
      assertEquals("surface-agent-deactivation-confirmation", missingAck.resultSurface().surfaceId());
      assertTrue(missingAck.message().contains("requires the explicit DEACTIVATE acknowledgement"));
      assertTrue(missingAck.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-deactivation-acknowledgement-required")));
      assertBrowserSafe(missingAck.resultSurface());

      var missingReason = runAction(new CapabilityActionRequest(
          "action-agent-deactivation-confirm",
          "action-agent-deactivation-confirm",
          "agent.definitions.manage",
          "agent.definitions.manage",
          Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "acknowledgement", "DEACTIVATE"),
          "idem-agent-deactivation-missing-reason",
          ADMIN_CONTEXT_ID,
          directDeactivation.surfaceId(),
          "corr-agent-deactivation-missing-reason"));
      assertEquals("validation-error", missingReason.status());
      assertEquals("surface-agent-deactivation-confirmation", missingReason.resultSurface().surfaceId());
      assertTrue(missingReason.message().contains("requires a browser-safe admin reason"));
      assertTrue(missingReason.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-deactivation-reason-required")));
      assertBrowserSafe(missingReason.resultSurface());

      var cancel = runAction(new CapabilityActionRequest(
          "action-agent-deactivation-cancel",
          "action-agent-deactivation-cancel",
          "agent.definitions.manage",
          "agent.definitions.manage",
          Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
          "idem-agent-deactivation-cancel",
          ADMIN_CONTEXT_ID,
          directDeactivation.surfaceId(),
          "corr-agent-deactivation-cancel"));
      assertEquals("accepted", cancel.status());
      assertEquals("surface-agent-admin-detail", cancel.resultSurface().surfaceId());
      assertTrue(cancel.message().contains("lifecycle state was not changed"));
      assertTrue(cancel.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-deactivation-cancel")));
      assertBrowserSafe(cancel.resultSurface());

      var proposal = runAction(new CapabilityActionRequest(
          "action-agent-deactivation-open-proposal",
          "action-agent-deactivation-open-proposal",
          "agent.definitions.manage",
          "agent.definitions.manage",
          Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "proposalId", "proposal-agent-admin-deactivation-required"),
          null,
          ADMIN_CONTEXT_ID,
          directDeactivation.surfaceId(),
          "corr-agent-deactivation-proposal"));
      assertEquals("accepted", proposal.status());
      assertEquals("surface-agent-behavior-proposal", proposal.resultSurface().surfaceId());
      assertBrowserSafe(proposal.resultSurface());

      var trace = runAction(new CapabilityActionRequest(
          "action-agent-deactivation-open-trace",
          "action-agent-deactivation-open-trace",
          "audit.trace.read",
          "audit.trace.read",
          Map.of("traceId", "trace-agent-definition-deactivate-confirmation"),
          null,
          ADMIN_CONTEXT_ID,
          directDeactivation.surfaceId(),
          "corr-agent-deactivation-trace"));
      assertEquals("accepted", trace.status());
      assertEquals("surface-agent-admin-trace", trace.resultSurface().surfaceId());
      assertTrue(trace.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-deactivation-open-trace")));
      assertBrowserSafe(trace.resultSurface());

      var confirmed = runAction(new CapabilityActionRequest(
          "action-agent-deactivation-confirm",
          "action-agent-deactivation-confirm",
          "agent.definitions.manage",
          "agent.definitions.manage",
          Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "acknowledgement", "DEACTIVATE", "reason", "Retire unsafe behavior"),
          "idem-agent-deactivation-confirm",
          ADMIN_CONTEXT_ID,
          directDeactivation.surfaceId(),
          "corr-agent-deactivation-confirm"));
      assertEquals("accepted", confirmed.status());
      assertEquals("surface-agent-admin-detail", confirmed.resultSurface().surfaceId());
      assertTrue(confirmed.message().contains("no prompt, skill, reference, provider, or tenant override artifacts were deleted"));
      assertTrue(confirmed.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-deactivation-confirm")));
      assertEquals(AgentLifecycleStatus.DISABLED, agentRepository.agentDefinition(TENANT_ID, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID).orElseThrow().status());
      assertBrowserSafe(confirmed.resultSurface());

      var repeated = runAction(new CapabilityActionRequest(
          "action-agent-deactivation-confirm",
          "action-agent-deactivation-confirm",
          "agent.definitions.manage",
          "agent.definitions.manage",
          Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "acknowledgement", "DEACTIVATE", "reason", "Retire unsafe behavior"),
          "idem-agent-deactivation-confirm",
          ADMIN_CONTEXT_ID,
          directDeactivation.surfaceId(),
          "corr-agent-deactivation-confirm-repeat"));
      assertEquals("no-op", repeated.status());
      assertEquals("surface-agent-admin-detail", repeated.resultSurface().surfaceId());
      assertTrue(repeated.message().contains("already deactivated"));
      assertBrowserSafe(repeated.resultSurface());

      var alreadyDeactivated = getSurface("surface-agent-deactivation-confirmation", "corr-agent-deactivation-already-disabled");
      assertEquals("surface-agent-deactivation-confirmation", alreadyDeactivated.surfaceId());
      assertEquals("disabled", alreadyDeactivated.data().get("currentStatus"));
      var alreadyPolicy = (Map<String, Object>) alreadyDeactivated.data().get("policyAndApprovalSummary");
      assertEquals("already-deactivated/no-op", alreadyPolicy.get("humanApprovalStatus"));
      var alreadyState = (Map<String, Object>) alreadyDeactivated.data().get("confirmationState");
      assertTrue(((List<String>) alreadyState.get("disabledActions")).contains("confirm"));
      assertTrue(alreadyDeactivated.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-deactivation-confirm") && action.disabled() != null));
      assertBrowserSafe(alreadyDeactivated);

      assertThrows(RuntimeException.class, () -> getSurfaceAs(
          "surface-agent-deactivation-confirmation",
          "corr-agent-deactivation-member-denied",
          "workos-member",
          "member@example.test",
          "Member User",
          MEMBER_CONTEXT_ID), "Regular tenant members must not read Agent Admin deactivation confirmation or lifecycle state.");
      assertThrows(RuntimeException.class, () -> getSurfaceAs(
          "surface-agent-deactivation-confirmation",
          "corr-agent-deactivation-customer-denied",
          "workos-customer",
          "customer@example.test",
          "Customer Admin",
          CUSTOMER_CONTEXT_ID), "Customer-scoped contexts must not expose Agent Admin deactivation confirmation, hidden agent ids, or tenant governance state.");
    } finally {
      agentRepository.agentDefinition(TENANT_ID, AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID)
          .filter(agent -> agent.status() == AgentLifecycleStatus.DISABLED)
          .ifPresent(agent -> agentRepository.saveAgentDefinition(agentWithStatus(agent, AgentLifecycleStatus.ACTIVE)));
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedWorkstreamApiExercisesAgentAdminTestConsoleRuntimeTestingPath() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-agent-test-console")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin test console must reject missing bearer tokens.");
    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-agent-test-console-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-agent-test-console-run",
            "action-agent-test-console-run",
            "agent_admin.draft_behavior_change",
            "agent_admin.draft_behavior_change",
            Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "testCategory", "detail-readiness"),
            "idem-agent-test-console-missing-bearer",
            ADMIN_CONTEXT_ID,
            "surface-agent-test-console",
            "corr-agent-test-console-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Agent Admin test-console action path must reject missing bearer tokens.");

    var testConsole = getSurface("surface-agent-test-console", "corr-agent-test-console-direct");
    assertEquals("surface-agent-test-console", testConsole.surfaceId());
    assertEquals("workflow-status", testConsole.surfaceType());
    assertEquals("agent_admin.test_console.v1", testConsole.data().get("surfaceContract"));
    assertEquals("corr-agent-test-console-direct", testConsole.correlationId());
    assertEquals(Boolean.TRUE, testConsole.data().get("noProductionSideEffects"));
    assertEquals(Boolean.TRUE, testConsole.data().get("noDirectMutation"));
    assertEquals(Boolean.TRUE, testConsole.data().get("noFakeSuccess"));
    assertFalse(testConsole.traceIds().isEmpty());
    assertTrue(String.valueOf(testConsole.data().get("traceIds")).contains("trace-agent-work") || testConsole.toString().contains("PromptAssemblyTrace"));
    assertTrue(testConsole.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-test-console-refresh") && action.resultSurface().updateSurfaceId().equals("surface-agent-test-console")));
    assertTrue(testConsole.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-test-console-run") && action.resultSurface().updateSurfaceId().equals("surface-agent-test-console")));
    assertTrue(testConsole.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-test-console-read-result") && action.resultSurface().updateSurfaceId().equals("surface-agent-test-console")));
    assertTrue(testConsole.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-test-console-open-proposal") && action.resultSurface().updateSurfaceId().equals("surface-agent-behavior-proposal")));
    assertTrue(testConsole.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-test-console-open-trace") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-trace")));
    assertTrue(testConsole.actions().stream().anyMatch(action -> action.actionId().equals("action-agent-test-console-back-to-detail") && action.resultSurface().updateSurfaceId().equals("surface-agent-admin-detail")));

    var testSummary = (Map<String, Object>) testConsole.data().get("testSummary");
    assertEquals("surface-agent-test-console", testSummary.get("surfaceId"));
    assertEquals("detail-readiness", testSummary.get("requestedTestCategory"));
    assertEquals("blocked-provider-or-runtime", testSummary.get("taskStatus"));
    assertTrue(String.valueOf(testSummary.get("noDirectMutationNotice")).contains("never activates behavior"));
    var scopeSummary = (Map<String, Object>) testConsole.data().get("scopeSummary");
    assertEquals(ADMIN_CONTEXT_ID, scopeSummary.get("selectedAuthContextId"));
    assertEquals("tenant", scopeSummary.get("scopeType"));
    assertEquals(Boolean.TRUE, scopeSummary.get("governanceAuthorized"));
    var runtimeReadiness = (Map<String, Object>) testConsole.data().get("runtimeReadiness");
    assertEquals("blocked_provider_or_runtime", runtimeReadiness.get("providerModelRuntimeConfigurationCategory"));
    assertEquals(Boolean.TRUE, runtimeReadiness.get("noFakeSuccess"));
    assertEquals(Boolean.FALSE, runtimeReadiness.get("fixtureModelLessResultsAccepted"));
    var safeResultSummary = (Map<String, Object>) testConsole.data().get("safeResultSummary");
    assertEquals("blocked-provider-or-runtime", safeResultSummary.get("advisoryResultOutcome"));
    assertEquals(Boolean.TRUE, safeResultSummary.get("humanReviewRequired"));
    assertEquals(Boolean.FALSE, safeResultSummary.get("proposalCanBeDraftedOrUpdated"));
    var checks = (List<Map<String, Object>>) testConsole.data().get("loaderAndBoundaryChecks");
    assertTrue(checks.stream().anyMatch(check -> check.get("checkId").equals("prompt-assembly") && check.get("decision").equals("allowed")));
    assertTrue(checks.stream().anyMatch(check -> check.get("checkId").equals("assigned-skill-load") && check.get("decision").equals("allowed")));
    assertTrue(checks.stream().anyMatch(check -> check.get("checkId").equals("unassigned-skill-denial") && check.get("decision").equals("denied") && check.get("targetSurfaceId").equals("surface-agent-tool-boundary-diff")));
    assertTrue(checks.stream().anyMatch(check -> check.get("checkId").equals("provider-model-runtime") && check.get("decision").equals("blocked_provider_or_runtime") && check.get("targetSurfaceId").equals("surface-agent-model-refs")));
    var proposalRoute = (Map<String, Object>) testConsole.data().get("proposalRoute");
    assertEquals("surface-agent-behavior-proposal", proposalRoute.get("targetSurfaceId"));
    assertEquals("disabled", proposalRoute.get("proposalUpdateStatus"));
    assertEquals("blocked_provider_or_runtime", proposalRoute.get("disabledReason"));
    assertTrue(testConsole.toString().contains("PromptAssemblyTrace"));
    assertTrue(testConsole.toString().contains("SkillLoadTrace"));
    assertTrue(testConsole.toString().contains("ToolPermissionBoundary"));
    assertTrue(testConsole.toString().contains("providerCredentials=omitted"));
    assertTrue(testConsole.toString().contains("rawProviderModelResponses=omitted"));
    assertTrue(testConsole.toString().contains("bearerTokens=omitted"));
    assertBrowserSafe(testConsole);

    var refresh = runAction(new CapabilityActionRequest(
        "action-agent-test-console-refresh",
        "action-agent-test-console-refresh",
        "agent_admin.draft_behavior_change",
        "agent_admin.draft_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        testConsole.surfaceId(),
        "corr-agent-test-console-refresh"));
    assertEquals("no-op", refresh.status());
    assertEquals("surface-agent-test-console", refresh.resultSurface().surfaceId());
    assertEquals(Boolean.TRUE, refresh.resultSurface().data().get("noProductionSideEffects"));
    assertBrowserSafe(refresh.resultSurface());

    var run = runAction(new CapabilityActionRequest(
        "action-agent-test-console-run",
        "action-agent-test-console-run",
        "agent_admin.draft_behavior_change",
        "agent_admin.draft_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "testCategory", "detail-readiness"),
        "idem-agent-test-console-run",
        ADMIN_CONTEXT_ID,
        testConsole.surfaceId(),
        "corr-agent-test-console-run"));
    assertEquals("blocked_provider_or_runtime", run.status());
    assertEquals("surface-agent-test-console", run.resultSurface().surfaceId());
    assertTrue(run.message().contains("no fixture/model-less success was counted"));
    assertFalse(run.traceIds().isEmpty());
    assertEquals(Boolean.TRUE, run.resultSurface().data().get("noFakeSuccess"));
    assertEquals(Boolean.TRUE, run.resultSurface().data().get("noProductionSideEffects"));
    assertBrowserSafe(run.resultSurface());

    var repeatedRun = runAction(new CapabilityActionRequest(
        "action-agent-test-console-run",
        "action-agent-test-console-run",
        "agent_admin.draft_behavior_change",
        "agent_admin.draft_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID, "testCategory", "detail-readiness"),
        "idem-agent-test-console-run",
        ADMIN_CONTEXT_ID,
        testConsole.surfaceId(),
        "corr-agent-test-console-run-repeat"));
    assertEquals("blocked_provider_or_runtime", repeatedRun.status());
    assertEquals("surface-agent-test-console", repeatedRun.resultSurface().surfaceId());
    assertEquals(Boolean.TRUE, repeatedRun.resultSurface().data().get("noProductionSideEffects"));
    assertBrowserSafe(repeatedRun.resultSurface());

    var readResult = runAction(new CapabilityActionRequest(
        "action-agent-test-console-read-result",
        "action-agent-test-console-read-result",
        "agent_admin.draft_behavior_change",
        "agent_admin.draft_behavior_change",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        testConsole.surfaceId(),
        "corr-agent-test-console-read-result"));
    assertEquals("no-op", readResult.status());
    assertEquals("surface-agent-test-console", readResult.resultSurface().surfaceId());
    assertTrue(readResult.message().contains("without rerunning provider/tool work"));
    assertBrowserSafe(readResult.resultSurface());

    var proposal = runAction(new CapabilityActionRequest(
        "action-agent-test-console-open-proposal",
        "action-agent-test-console-open-proposal",
        "agent_admin.submit_behavior_change_for_review",
        "agent_admin.submit_behavior_change_for_review",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-agent-test-console-proposal",
        ADMIN_CONTEXT_ID,
        testConsole.surfaceId(),
        "corr-agent-test-console-proposal"));
    assertEquals("denied", proposal.status());
    assertEquals("surface-agent-test-console", proposal.resultSurface().surfaceId());
    assertTrue(proposal.message().contains("cannot activate artifacts directly"));
    assertBrowserSafe(proposal.resultSurface());

    var trace = runAction(new CapabilityActionRequest(
        "action-agent-test-console-open-trace",
        "action-agent-test-console-open-trace",
        "audit.trace.read",
        "audit.trace.read",
        Map.of("traceId", "trace-agent-test-console"),
        null,
        ADMIN_CONTEXT_ID,
        testConsole.surfaceId(),
        "corr-agent-test-console-trace"));
    assertEquals("accepted", trace.status());
    assertEquals("surface-agent-admin-trace", trace.resultSurface().surfaceId());
    assertTrue(trace.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-agent-test-console-open-trace")));
    assertTrue(trace.resultSurface().toString().contains("AgentWorkTrace"));
    assertBrowserSafe(trace.resultSurface());

    var backToDetail = runAction(new CapabilityActionRequest(
        "action-agent-test-console-back-to-detail",
        "action-agent-test-console-back-to-detail",
        "agent_admin.get_definition",
        "agent_admin.get_definition",
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        null,
        ADMIN_CONTEXT_ID,
        testConsole.surfaceId(),
        "corr-agent-test-console-detail"));
    assertEquals("accepted", backToDetail.status());
    assertEquals("surface-agent-admin-detail", backToDetail.resultSurface().surfaceId());
    assertBrowserSafe(backToDetail.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-test-console",
        "corr-agent-test-console-member-denied",
        "workos-member",
        "member@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Agent Admin test-console runtime evidence.");
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-test-console",
        "corr-agent-test-console-customer-denied",
        "workos-customer",
        "customer@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped contexts must not expose Agent Admin test-console runtime evidence or hidden managed-agent metadata.");
  }

  @Test
  void protectedAgentAdminDashboardDeniesUnauthorizedAndCustomerScopedContextsSafely() {
    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-admin-dashboard",
        "corr-agent-admin-member-denied",
        "workos-member",
        "member@example.test",
        "Member User",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read Agent Admin dashboard state.");

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-agent-admin-dashboard",
        "corr-agent-admin-customer-denied",
        "workos-customer",
        "customer@example.test",
        "Customer Admin",
        CUSTOMER_CONTEXT_ID), "Customer-scoped contexts must not imply Agent Admin authority or expose tenant governance counts.");
  }

  private SurfaceEnvelope getSurface(String surfaceId, String correlationId) throws Exception {
    return getSurfaceAs(surfaceId, correlationId, "workos-admin", "admin@example.test", "Tenant Admin", ADMIN_CONTEXT_ID);
  }

  private AgentDefinition agentWithStatus(AgentDefinition agent, AgentLifecycleStatus status) {
    return new AgentDefinition(
        agent.tenantId(),
        agent.agentDefinitionId(),
        agent.displayName(),
        agent.description(),
        agent.placement(),
        agent.functionalAreaId(),
        agent.authorityLevel(),
        status,
        agent.promptDocumentId(),
        agent.activePromptVersion(),
        agent.skillManifestId(),
        agent.activeSkillManifestVersion(),
        agent.referenceManifestId(),
        agent.activeReferenceManifestVersion(),
        agent.toolBoundaryId(),
        agent.activeToolBoundaryVersion(),
        agent.modelConfigRefId(),
        agent.modelPolicyRefId(),
        agent.runtimeClassRef(),
        agent.traceRequirements(),
        agent.seedProvenance(),
        agent.createdAt(),
        agent.updatedAt());
  }

  private CapabilityActionResult assertDetailActionRoutes(String actionId, String capabilityId, String expectedSurfaceId, String expectedStatus, String correlationId) throws Exception {
    var result = runAction(new CapabilityActionRequest(
        actionId,
        actionId,
        capabilityId,
        capabilityId,
        Map.of("agentDefinitionId", AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID),
        "idem-" + correlationId,
        ADMIN_CONTEXT_ID,
        "surface-agent-admin-detail",
        correlationId));
    assertEquals(expectedStatus, result.status());
    assertEquals(expectedSurfaceId, result.resultSurface().surfaceId());
    assertEquals(correlationId, result.correlationId());
    assertFalse(result.traceIds().isEmpty());
    assertBrowserSafe(result.resultSurface());
    return result;
  }

  @SuppressWarnings("unchecked")
  private SurfaceEnvelope getCatalogWithRows(String correlationId) throws Exception {
    SurfaceEnvelope latest = null;
    for (int attempt = 0; attempt < 10; attempt++) {
      latest = getSurface("surface-agent-admin-catalog", correlationId + "-attempt-" + attempt);
      var catalogSummary = (Map<String, Object>) latest.data().get("catalogSummary");
      if (((Number) catalogSummary.get("resultCount")).intValue() > 0) return latest;
      Thread.sleep(100);
    }
    return latest == null ? getSurface("surface-agent-admin-catalog", correlationId) : latest;
  }

  private SurfaceEnvelope getSurfaceAs(String surfaceId, String correlationId, String subject, String email, String name, String selectedContextId) throws Exception {
    var response = httpClient
        .GET("/api/workstream/surfaces/" + surfaceId)
        .addHeader("Authorization", "Bearer " + bearerToken(subject, email, name))
        .addHeader("X-Selected-Context-Id", selectedContextId)
        .addHeader("X-Correlation-Id", correlationId)
        .responseBodyAs(SurfaceEnvelope.class)
        .invoke();
    assertTrue(response.status().isSuccess());
    return response.body();
  }

  private CapabilityActionResult runAction(CapabilityActionRequest request) throws Exception {
    try {
      var response = httpClient
          .POST("/api/workstream/actions")
          .addHeader("Authorization", "Bearer " + bearerToken("workos-admin", "admin@example.test", "Tenant Admin"))
          .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
          .addHeader("X-Correlation-Id", request.correlationId())
          .withRequestBody(request)
          .responseBodyAs(CapabilityActionResult.class)
          .invoke();
      assertTrue(response.status().isSuccess());
      return response.body();
    } catch (RuntimeException failed) {
      throw new RuntimeException("Action " + request.actionId() + " failed: " + failed.getMessage(), failed);
    }
  }

  private void seedIdentity(AkkaIdentityRepository repository, String email, String displayName, String membershipId, ScopeType scopeType, String customerId, List<FoundationRole> roles) {
    repository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "UNLINKED"));
    repository.saveProfile(new UserProfile(email, email, displayName, null, null, null));
    repository.saveSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.saveMembership(new Membership(membershipId, email, scopeType, TENANT_ID, customerId, roles, MembershipStatus.ACTIVE, false, null));
  }

  private String bearerToken(String subject, String email, String name) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(Map.of("sub", subject, "email", email, "name", name)));
    return header + "." + payload;
  }

  private static void assertBrowserSafe(Object payload) {
    var text = String.valueOf(payload);
    assertFalse(text.contains("invite-token"));
    assertFalse(text.contains("tokenHash"));
    assertFalse(text.contains("providerSecret"));
    assertFalse(text.contains("sk_live_"));
    assertFalse(text.contains("sk_test_"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
  }
}
