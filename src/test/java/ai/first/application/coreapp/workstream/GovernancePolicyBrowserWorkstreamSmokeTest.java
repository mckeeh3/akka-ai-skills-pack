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
import ai.first.application.foundation.identity.AkkaIdentityRepository;
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

/** Akka-hosted browser/API smoke coverage for the Governance/Policy dashboard runtime path. */
class GovernancePolicyBrowserWorkstreamSmokeTest extends TestKitSupport {
  private static final String TENANT_ID = "tenant-governance-smoke";
  private static final String CUSTOMER_ID = "customer-governance-smoke";
  private static final String ADMIN_CONTEXT_ID = "membership-governance-admin";
  private static final String MEMBER_CONTEXT_ID = "membership-governance-member";

  @BeforeEach
  void seedGovernancePolicySmokeActors() {
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant(TENANT_ID, "Governance Smoke Tenant", true));
    repository.saveCustomer(new Customer(TENANT_ID, CUSTOMER_ID, "Governance Smoke Customer", true));
    seedIdentity(repository, "governance-admin@example.test", "Governance Admin", ADMIN_CONTEXT_ID, List.of(FoundationRole.TENANT_ADMIN));
    seedIdentity(repository, "governance-member@example.test", "Governance Member", MEMBER_CONTEXT_ID, List.of(FoundationRole.TENANT_EMPLOYEE));
  }

  @Test
  @SuppressWarnings("unchecked")
  void hostedShellAndProtectedWorkstreamApiExerciseGovernancePolicyInventoryRuntimePath() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load before the inventory surface is exercised through browser API paths.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertTrue(shell.body().contains("/assets/"));
    assertBrowserSafe(shell.body());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-governance-policy-inventory")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy inventory must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-governance-inventory-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-governance-policy-list",
            "action-governance-policy-list",
            "governance.policy.read",
            "governance.policy.read",
            Map.of("search", "human approval"),
            null,
            ADMIN_CONTEXT_ID,
            "surface-governance-policy-inventory",
            "corr-governance-inventory-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy inventory action path must reject missing bearer tokens.");

    var inventory = getSurface("surface-governance-policy-inventory", "corr-governance-inventory-direct");
    assertEquals("surface-governance-policy-inventory", inventory.surfaceId());
    assertEquals("list-search", inventory.surfaceType());
    assertEquals("governance.policy.inventory.v1", inventory.data().get("surfaceContract"));
    assertEquals("corr-governance-inventory-direct", inventory.correlationId());
    assertTrue(inventory.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-inventory")));
    assertEquals(true, inventory.data().get("noFakeSuccess"));
    assertEquals(true, inventory.data().get("noDirectMutation"));
    assertTrue(inventory.toString().contains("backend-resolved selected AuthContext"));
    assertTrue(inventory.toString().contains("ToolPermissionBoundary"));
    assertTrue(inventory.toString().contains("blocked_provider_or_runtime"));
    assertTrue(inventory.toString().contains("rawDatabaseCursors=omitted"));
    assertTrue(inventory.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-list") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-inventory")));
    assertTrue(inventory.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-read") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-detail")));
    assertTrue(inventory.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-start-impact-analysis") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-impact-analysis-task")));
    var rows = (List<Map<String, Object>>) inventory.data().get("rows");
    assertFalse(rows.isEmpty(), "Inventory must return backend-owned visible policy/proposal rows for the selected tenant admin AuthContext.");
    assertTrue(rows.stream().anyMatch(row -> "policy-human-approval".equals(row.get("policyId")) && "action-governance-policy-read".equals(row.get("openActionId"))));
    assertBrowserSafe(inventory);

    var detail = runAction(new CapabilityActionRequest(
        "action-governance-policy-read",
        "action-governance-policy-read",
        "governance.policy.read",
        "governance.policy.read",
        Map.of("policyId", "policy-human-approval", "tenantId", TENANT_ID),
        null,
        ADMIN_CONTEXT_ID,
        inventory.surfaceId(),
        "corr-governance-inventory-read-row"));
    assertEquals("accepted", detail.status());
    assertEquals("surface-governance-policy-detail", detail.resultSurface().surfaceId());
    assertEquals("detail-edit", detail.resultSurface().surfaceType());
    assertTrue(detail.resultSurface().toString().contains("backend AuthContext"));
    assertTrue(detail.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-detail")));
    assertBrowserSafe(detail.resultSurface());

    var draft = runAction(new CapabilityActionRequest(
        "action-governance-policy-draft-proposal",
        "action-governance-policy-draft-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("title", "Inventory smoke proposal", "rationale", "exercise inventory list-search action graph", "proposedContent", "Keep Governance/Policy inventory scoped, traced, and browser safe."),
        "idem-governance-inventory-draft",
        ADMIN_CONTEXT_ID,
        inventory.surfaceId(),
        "corr-governance-inventory-draft"));
    assertEquals("accepted", draft.status());
    assertEquals("surface-governance-policy-proposal", draft.resultSurface().surfaceId());
    var proposalId = String.valueOf(draft.resultSurface().data().get("proposalId"));
    assertBrowserSafe(draft.resultSurface());

    var filtered = runAction(new CapabilityActionRequest(
        "action-governance-policy-list",
        "action-governance-policy-list",
        "governance.policy.read",
        "governance.policy.read",
        Map.of("search", "Inventory smoke", "lifecycle", "draft", "tenantId", TENANT_ID, "customerId", CUSTOMER_ID),
        null,
        ADMIN_CONTEXT_ID,
        inventory.surfaceId(),
        "corr-governance-inventory-filtered"));
    assertEquals("accepted", filtered.status());
    assertEquals("surface-governance-policy-inventory", filtered.resultSurface().surfaceId());
    assertEquals("governance.policy.inventory.v1", filtered.resultSurface().data().get("surfaceContract"));
    assertTrue(filtered.resultSurface().toString().contains("selectedFiltersSummary"));
    assertTrue(filtered.resultSurface().toString().contains(proposalId));
    assertTrue(filtered.resultSurface().toString().contains("No authorized rows match") || filtered.resultSurface().toString().contains("Inventory smoke proposal"));
    assertBrowserSafe(filtered.resultSurface());

    var impact = runAction(new CapabilityActionRequest(
        "action-governance-policy-start-impact-analysis",
        "action-governance-policy-start-impact-analysis",
        "governance.policy.impact_analysis.start",
        "governance.policy.impact_analysis.start",
        Map.of("proposalId", proposalId, "scope", "inventory-smoke", "reason", "verify inventory row provider/runtime fail-closed path"),
        "idem-governance-inventory-impact",
        ADMIN_CONTEXT_ID,
        filtered.resultSurface().surfaceId(),
        "corr-governance-inventory-impact"));
    assertEquals("blocked_provider_or_runtime", impact.status());
    assertEquals("surface-governance-policy-impact-analysis-task", impact.resultSurface().surfaceId());
    assertEquals("blocked_provider_or_runtime", impact.resultSurface().data().get("status"));
    assertTrue(impact.resultSurface().toString().contains("AutonomousAgent"));
    assertFalse(impact.resultSurface().toString().contains("impact_ready"));
    assertBrowserSafe(impact.resultSurface());

    var memberDenied = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-list",
        "action-governance-policy-list",
        "governance.policy.read",
        "governance.policy.read",
        Map.of("search", "human approval"),
        null,
        MEMBER_CONTEXT_ID,
        "surface-governance-policy-inventory",
        "corr-governance-inventory-member-denied"),
        "workos-governance-member",
        "governance-member@example.test",
        "Governance Member",
        MEMBER_CONTEXT_ID);
    assertEquals("denied", memberDenied.status());
    assertEquals("surface-governance-policy-system-message", memberDenied.resultSurface().surfaceId());
    assertEquals(true, memberDenied.resultSurface().data().get("noFakeSuccess"));
    assertTrue(memberDenied.resultSurface().toString().contains("CAPABILITY_FORBIDDEN"));
    assertBrowserSafe(memberDenied.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-governance-policy-inventory",
        "corr-governance-inventory-member-direct-denied",
        "workos-governance-member",
        "governance-member@example.test",
        "Governance Member",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read the Governance/Policy inventory.");

    var crossTenant = runAction(new CapabilityActionRequest(
        "action-governance-policy-list",
        "action-governance-policy-list",
        "governance.policy.read",
        "governance.policy.read",
        Map.of("tenantId", "tenant-other", "search", "Inventory smoke"),
        null,
        ADMIN_CONTEXT_ID,
        inventory.surfaceId(),
        "corr-governance-inventory-cross-tenant-denied"));
    assertEquals("denied", crossTenant.status());
    assertEquals("surface-governance-policy-system-message", crossTenant.resultSurface().surfaceId());
    assertTrue(crossTenant.resultSurface().toString().contains("GOVERNANCE_POLICY_TENANT_FORBIDDEN"));
    assertBrowserSafe(crossTenant.resultSurface());
  }

  @Test
  @SuppressWarnings("unchecked")
  void hostedShellAndProtectedWorkstreamApiExerciseGovernancePolicyDashboardRuntimePath() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load from Akka static resources.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertTrue(shell.body().contains("/assets/"));
    assertBrowserSafe(shell.body());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-governance-policy-dashboard")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy dashboard must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-governance-dashboard-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-governance-policy-dashboard",
            "action-governance-policy-dashboard",
            "governance.policy.read",
            "governance.policy.read",
            null,
            null,
            ADMIN_CONTEXT_ID,
            "surface-governance-policy-dashboard",
            "corr-governance-dashboard-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy dashboard action path must reject missing bearer tokens.");

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-governance-admin", "governance-admin@example.test", "Governance Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-governance-dashboard-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals(ADMIN_CONTEXT_ID, bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-governance-policy") && agent.availability().equals("visible")));
    assertBrowserSafe(bootstrap.body());

    var dashboard = getSurface("surface-governance-policy-dashboard", "corr-governance-dashboard-direct");
    assertEquals("surface-governance-policy-dashboard", dashboard.surfaceId());
    assertEquals("dashboard", dashboard.surfaceType());
    assertEquals("governance.policy.dashboard.v1", dashboard.data().get("surfaceContract"));
    assertEquals("corr-governance-dashboard-direct", dashboard.correlationId());
    assertTrue(dashboard.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-dashboard")));
    assertEquals(true, dashboard.data().get("noFakeSuccess"));
    assertEquals(true, dashboard.data().get("noDirectMutation"));
    assertTrue(dashboard.toString().contains("ready_with_fail_closed_advisory_workers"));
    assertTrue(dashboard.toString().contains("things that need my attention") || dashboard.toString().contains("attentionQueues"));
    assertTrue(dashboard.toString().contains("policy-impact-analysis"));
    assertTrue(dashboard.toString().contains("blocked_provider_or_runtime"));
    assertTrue(dashboard.toString().contains("omittedFieldKeys"));
    assertTrue(dashboard.toString().contains("governance.policy.read"));
    assertTrue(dashboard.toString().contains("governance.policy.impact_analysis.start"));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-dashboard") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-dashboard")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-list") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-inventory")));
    assertTrue(dashboard.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-start-impact-analysis") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-impact-analysis-task")));
    assertBrowserSafe(dashboard);

    var refreshed = runAction(new CapabilityActionRequest(
        "action-governance-policy-dashboard",
        "action-governance-policy-dashboard",
        "governance.policy.read",
        "governance.policy.read",
        null,
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-governance-dashboard-refresh"));
    assertEquals("accepted", refreshed.status());
    assertEquals("surface-governance-policy-dashboard", refreshed.resultSurface().surfaceId());
    assertEquals("governance.policy.dashboard.v1", refreshed.resultSurface().data().get("surfaceContract"));
    assertTrue(refreshed.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-dashboard")));
    assertBrowserSafe(refreshed.resultSurface());

    var inventory = runAction(new CapabilityActionRequest(
        "action-governance-policy-list",
        "action-governance-policy-list",
        "governance.policy.read",
        "governance.policy.read",
        Map.of("filter", "submitted"),
        null,
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-governance-dashboard-list"));
    assertEquals("accepted", inventory.status());
    assertEquals("surface-governance-policy-inventory", inventory.resultSurface().surfaceId());
    assertEquals("governance.policy.inventory.v1", inventory.resultSurface().data().get("surfaceContract"));
    assertTrue(inventory.resultSurface().toString().contains("ToolPermissionBoundary"));
    assertBrowserSafe(inventory.resultSurface());

    var draft = runAction(new CapabilityActionRequest(
        "action-governance-policy-draft-proposal",
        "action-governance-policy-draft-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("title", "Governance dashboard smoke proposal", "rationale", "exercise dashboard action graph", "proposedContent", "Preserve backend authorization and trace gates."),
        "idem-governance-dashboard-draft",
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-governance-dashboard-draft"));
    assertEquals("accepted", draft.status());
    assertEquals("surface-governance-policy-proposal", draft.resultSurface().surfaceId());
    assertEquals("governance.policy.proposal.v1", draft.resultSurface().data().get("surfaceContract"));
    assertEquals(true, draft.resultSurface().data().get("noDirectMutation"));
    assertBrowserSafe(draft.resultSurface());

    var duplicateDraft = runAction(new CapabilityActionRequest(
        "action-governance-policy-draft-proposal",
        "action-governance-policy-draft-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("title", "Ignored duplicate dashboard proposal"),
        "idem-governance-dashboard-draft",
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-governance-dashboard-draft-replay"));
    assertEquals("no-op", duplicateDraft.status());
    assertEquals(draft.resultSurface().data().get("proposalId"), duplicateDraft.resultSurface().data().get("proposalId"));
    assertBrowserSafe(duplicateDraft.resultSurface());

    var proposalId = String.valueOf(draft.resultSurface().data().get("proposalId"));
    var impact = runAction(new CapabilityActionRequest(
        "action-governance-policy-start-impact-analysis",
        "action-governance-policy-start-impact-analysis",
        "governance.policy.impact_analysis.start",
        "governance.policy.impact_analysis.start",
        Map.of("proposalId", proposalId, "scope", "dashboard-smoke", "reason", "verify provider/runtime fail-closed dashboard task path"),
        "idem-governance-dashboard-impact",
        ADMIN_CONTEXT_ID,
        dashboard.surfaceId(),
        "corr-governance-dashboard-impact"));
    assertEquals("blocked_provider_or_runtime", impact.status());
    assertEquals("surface-governance-policy-impact-analysis-task", impact.resultSurface().surfaceId());
    assertEquals("workflow-status", impact.resultSurface().surfaceType());
    assertEquals("governance.policy.impact_analysis.task.v1", impact.resultSurface().data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", impact.resultSurface().data().get("status"));
    assertEquals(true, impact.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, impact.resultSurface().data().get("activationBlockedUntilHumanDecision"));
    assertTrue(impact.message().contains("blocked_provider_or_runtime") || impact.resultSurface().toString().contains("blocked_provider_or_runtime"));
    assertTrue(impact.resultSurface().toString().contains("AutonomousAgent"));
    assertTrue(impact.resultSurface().toString().contains("governance.policy.impact_analysis.read"));
    assertBrowserSafe(impact.resultSurface());

    var memberDenied = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-list",
        "action-governance-policy-list",
        "governance.policy.read",
        "governance.policy.read",
        null,
        null,
        MEMBER_CONTEXT_ID,
        "surface-governance-policy-dashboard",
        "corr-governance-dashboard-member-denied"),
        "workos-governance-member",
        "governance-member@example.test",
        "Governance Member",
        MEMBER_CONTEXT_ID);
    assertEquals("denied", memberDenied.status());
    assertEquals("surface-governance-policy-system-message", memberDenied.resultSurface().surfaceId());
    assertEquals("system-message", memberDenied.resultSurface().surfaceType());
    assertEquals("governance.policy.system_message.v1", memberDenied.resultSurface().data().get("surfaceContract"));
    assertEquals(true, memberDenied.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, memberDenied.resultSurface().data().get("noDirectMutation"));
    assertTrue(memberDenied.resultSurface().toString().contains("CAPABILITY_FORBIDDEN"));
    assertBrowserSafe(memberDenied.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-governance-policy-dashboard",
        "corr-governance-dashboard-member-direct-denied",
        "workos-governance-member",
        "governance-member@example.test",
        "Governance Member",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read the Governance/Policy dashboard.");

    var crossTenant = runAction(new CapabilityActionRequest(
        "action-governance-policy-simulate",
        "action-governance-policy-simulate",
        "governance.policy.simulate",
        "governance.policy.simulate",
        Map.of("tenantId", "tenant-other", "proposalId", proposalId),
        null,
        ADMIN_CONTEXT_ID,
        draft.resultSurface().surfaceId(),
        "corr-governance-dashboard-cross-tenant-denied"));
    assertEquals("denied", crossTenant.status());
    assertEquals("surface-governance-policy-system-message", crossTenant.resultSurface().surfaceId());
    assertEquals("governance.policy.system_message.v1", crossTenant.resultSurface().data().get("surfaceContract"));
    assertTrue(crossTenant.resultSurface().toString().contains("GOVERNANCE_POLICY_TENANT_FORBIDDEN"));
    assertBrowserSafe(crossTenant.resultSurface());

    var finalDashboard = getSurface("surface-governance-policy-dashboard", "corr-governance-dashboard-after-actions");
    var attentionQueues = (List<Map<String, Object>>) finalDashboard.data().get("attentionQueues");
    assertTrue(attentionQueues.stream().anyMatch(queue -> "policy-impact-analysis".equals(queue.get("queueId")) && "blocked_provider_or_runtime".equals(queue.get("severity"))));
    assertTrue(finalDashboard.toString().contains(proposalId));
    assertBrowserSafe(finalDashboard);
  }

  private SurfaceEnvelope getSurface(String surfaceId, String correlationId) throws Exception {
    return getSurfaceAs(surfaceId, correlationId, "workos-governance-admin", "governance-admin@example.test", "Governance Admin", ADMIN_CONTEXT_ID);
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
    return runActionAs(request, "workos-governance-admin", "governance-admin@example.test", "Governance Admin", ADMIN_CONTEXT_ID);
  }

  private CapabilityActionResult runActionAs(CapabilityActionRequest request, String subject, String email, String name, String selectedContextId) throws Exception {
    var response = httpClient
        .POST("/api/workstream/actions")
        .addHeader("Authorization", "Bearer " + bearerToken(subject, email, name))
        .addHeader("X-Selected-Context-Id", selectedContextId)
        .addHeader("X-Correlation-Id", request.correlationId())
        .withRequestBody(request)
        .responseBodyAs(CapabilityActionResult.class)
        .invoke();
    assertTrue(response.status().isSuccess());
    assertNotNull(response.body().traceIds());
    return response.body();
  }

  private void seedIdentity(AkkaIdentityRepository repository, String email, String displayName, String membershipId, List<FoundationRole> roles) {
    repository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "UNLINKED"));
    repository.saveProfile(new UserProfile(email, email, displayName, null, null, null));
    repository.saveSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.saveMembership(new Membership(membershipId, email, ScopeType.TENANT, TENANT_ID, null, roles, MembershipStatus.ACTIVE, false, null));
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
    assertFalse(text.contains("sk_live_"));
    assertFalse(text.contains("sk_test_"));
    assertFalse(text.contains("Bearer "));
    assertFalse(text.contains("rawJwt=ey"));
    assertFalse(text.contains("rawProviderCredential=sk_"));
    assertFalse(text.contains("hiddenPromptText=secret"));
    assertFalse(text.contains("rawToolPayload={"));
    assertFalse(text.contains("providerSecret=secret"));
    assertFalse(text.contains("api_key="));
    assertFalse(text.contains("test-fake-provider"));
    assertFalse(text.contains("test-fake-model"));
  }
}
