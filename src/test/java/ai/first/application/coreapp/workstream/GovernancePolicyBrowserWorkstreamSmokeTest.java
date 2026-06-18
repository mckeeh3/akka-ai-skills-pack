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
  void hostedShellAndProtectedWorkstreamApiExerciseGovernancePolicyProposalRuntimePath() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load before the proposal surface is exercised through browser API paths.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertTrue(shell.body().contains("/assets/"));
    assertBrowserSafe(shell.body());

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken("workos-governance-admin", "governance-admin@example.test", "Governance Admin"))
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-governance-proposal-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals(ADMIN_CONTEXT_ID, bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-governance-policy") && agent.availability().equals("visible")));
    assertBrowserSafe(bootstrap.body());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-governance-policy-proposal")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy proposal must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-governance-proposal-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-governance-policy-draft-proposal",
            "action-governance-policy-draft-proposal",
            "governance.policy.propose",
            "governance.policy.propose",
            Map.of("title", "Missing bearer proposal"),
            "idem-governance-proposal-missing-bearer",
            ADMIN_CONTEXT_ID,
            "surface-governance-policy-proposal",
            "corr-governance-proposal-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy proposal action path must reject missing bearer tokens.");

    var direct = getSurface("surface-governance-policy-proposal", "corr-governance-proposal-direct-new-draft");
    assertEquals("surface-governance-policy-proposal", direct.surfaceId());
    assertEquals("governance-diff", direct.surfaceType());
    assertEquals("governance.policy.proposal.v1", direct.data().get("surfaceContract"));
    assertEquals("empty/new-draft", direct.data().get("state"));
    assertEquals(true, direct.data().get("noDirectMutation"));
    assertEquals(true, direct.data().get("noFakeSuccess"));
    assertTrue(direct.toString().contains("proposalSummary"));
    assertTrue(direct.toString().contains("changeSet"));
    assertTrue(direct.toString().contains("draftFields"));
    assertTrue(direct.toString().contains("lifecycleGate"));
    assertTrue(direct.toString().contains("availableTransitions"));
    assertTrue(direct.toString().contains("blocked_provider_or_runtime"));
    assertTrue(direct.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-proposal")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-draft-proposal") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-proposal")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-submit-proposal") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-proposal")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-start-impact-analysis") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-impact-analysis-task")));
    assertBrowserSafe(direct);

    var missingIdempotency = runAction(new CapabilityActionRequest(
        "action-governance-policy-draft-proposal",
        "action-governance-policy-draft-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("title", "Missing idempotency proposal", "rationale", "verify validation state"),
        null,
        ADMIN_CONTEXT_ID,
        direct.surfaceId(),
        "corr-governance-proposal-missing-idempotency"));
    assertEquals("denied", missingIdempotency.status());
    assertEquals("surface-governance-policy-system-message", missingIdempotency.resultSurface().surfaceId());
    assertEquals(true, missingIdempotency.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, missingIdempotency.resultSurface().data().get("noDirectMutation"));
    assertTrue(missingIdempotency.resultSurface().toString().contains("idempotency-key-required"));
    assertBrowserSafe(missingIdempotency.resultSurface());

    var draft = runAction(new CapabilityActionRequest(
        "action-governance-policy-draft-proposal",
        "action-governance-policy-draft-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of(
            "title", "Proposal smoke authority boundary",
            "rationale", "exercise proposal governance-diff runtime path",
            "proposedContent", "Require backend authorization, human approval, simulation evidence, rollback metadata, and trace evidence before authority changes."),
        "idem-governance-proposal-draft",
        ADMIN_CONTEXT_ID,
        direct.surfaceId(),
        "corr-governance-proposal-draft"));
    assertEquals("accepted", draft.status());
    assertEquals("surface-governance-policy-proposal", draft.resultSurface().surfaceId());
    assertEquals("governance.policy.proposal.v1", draft.resultSurface().data().get("surfaceContract"));
    assertEquals("draft", draft.resultSurface().data().get("state"));
    assertEquals(true, draft.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, draft.resultSurface().data().get("noFakeSuccess"));
    assertTrue(draft.resultSurface().toString().contains("No authority changes before approval."));
    assertTrue(draft.resultSurface().toString().contains("policy-decision"));
    assertTrue(draft.resultSurface().toString().contains("admin-audit"));
    assertTrue(draft.resultSurface().toString().contains("workstream-log"));
    assertTrue(draft.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-proposal-draft")));
    assertBrowserSafe(draft.resultSurface());

    var duplicateDraft = runAction(new CapabilityActionRequest(
        "action-governance-policy-draft-proposal",
        "action-governance-policy-draft-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("title", "Duplicate ignored", "proposedContent", "This replay must not create another proposal."),
        "idem-governance-proposal-draft",
        ADMIN_CONTEXT_ID,
        direct.surfaceId(),
        "corr-governance-proposal-draft-replay"));
    assertEquals("no-op", duplicateDraft.status());
    assertEquals(draft.resultSurface().data().get("proposalId"), duplicateDraft.resultSurface().data().get("proposalId"));
    assertBrowserSafe(duplicateDraft.resultSurface());

    var proposalId = String.valueOf(draft.resultSurface().data().get("proposalId"));
    var unknownSubmit = runAction(new CapabilityActionRequest(
        "action-governance-policy-submit-proposal",
        "action-governance-policy-submit-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("proposalId", "proposal-not-visible"),
        "idem-governance-proposal-submit-missing",
        ADMIN_CONTEXT_ID,
        draft.resultSurface().surfaceId(),
        "corr-governance-proposal-submit-missing"));
    assertEquals("validation-error", unknownSubmit.status());
    assertEquals("surface-governance-policy-validation-error", unknownSubmit.resultSurface().surfaceId());
    assertEquals("proposalId", unknownSubmit.resultSurface().data().get("field"));
    assertBrowserSafe(unknownSubmit.resultSurface());

    var submitted = runAction(new CapabilityActionRequest(
        "action-governance-policy-submit-proposal",
        "action-governance-policy-submit-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("proposalId", proposalId, "freshnessToken", "browser-hint-only"),
        "idem-governance-proposal-submit",
        ADMIN_CONTEXT_ID,
        draft.resultSurface().surfaceId(),
        "corr-governance-proposal-submit"));
    assertEquals("accepted", submitted.status());
    assertEquals("surface-governance-policy-proposal", submitted.resultSurface().surfaceId());
    assertEquals("in_review", submitted.resultSurface().data().get("state"));
    assertEquals(true, submitted.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, submitted.resultSurface().data().get("noFakeSuccess"));
    assertTrue(submitted.resultSurface().toString().contains("human approval"));
    assertTrue(submitted.resultSurface().toString().contains("simulation evidence"));
    assertTrue(submitted.resultSurface().toString().contains("rollback metadata"));
    assertTrue(submitted.resultSurface().toString().contains("action-governance-policy-decide"));
    assertTrue(submitted.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-proposal-submit")));
    assertBrowserSafe(submitted.resultSurface());

    var duplicateSubmit = runAction(new CapabilityActionRequest(
        "action-governance-policy-submit-proposal",
        "action-governance-policy-submit-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("proposalId", proposalId),
        "idem-governance-proposal-submit",
        ADMIN_CONTEXT_ID,
        submitted.resultSurface().surfaceId(),
        "corr-governance-proposal-submit-replay"));
    assertEquals("no-op", duplicateSubmit.status());
    assertEquals("in_review", duplicateSubmit.resultSurface().data().get("state"));
    assertEquals(proposalId, duplicateSubmit.resultSurface().data().get("proposalId"));
    assertBrowserSafe(duplicateSubmit.resultSurface());

    var impact = runAction(new CapabilityActionRequest(
        "action-governance-policy-start-impact-analysis",
        "action-governance-policy-start-impact-analysis",
        "governance.policy.impact_analysis.start",
        "governance.policy.impact_analysis.start",
        Map.of("proposalId", proposalId, "scope", "proposal-smoke", "reason", "verify proposal surface provider/runtime fail-closed path"),
        "idem-governance-proposal-impact",
        ADMIN_CONTEXT_ID,
        submitted.resultSurface().surfaceId(),
        "corr-governance-proposal-impact"));
    assertEquals("blocked_provider_or_runtime", impact.status());
    assertEquals("surface-governance-policy-impact-analysis-task", impact.resultSurface().surfaceId());
    assertEquals("workflow-status", impact.resultSurface().surfaceType());
    assertEquals("blocked_provider_or_runtime", impact.resultSurface().data().get("status"));
    assertEquals(true, impact.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, impact.resultSurface().data().get("activationBlockedUntilHumanDecision"));
    assertTrue(impact.resultSurface().toString().contains("AutonomousAgent"));
    assertFalse(impact.resultSurface().toString().contains("impact_ready"));
    assertBrowserSafe(impact.resultSurface());

    var memberDenied = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-draft-proposal",
        "action-governance-policy-draft-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("title", "Member denied proposal"),
        "idem-governance-proposal-member-denied",
        MEMBER_CONTEXT_ID,
        direct.surfaceId(),
        "corr-governance-proposal-member-denied"),
        "workos-governance-member",
        "governance-member@example.test",
        "Governance Member",
        MEMBER_CONTEXT_ID);
    assertEquals("denied", memberDenied.status());
    assertEquals("surface-governance-policy-system-message", memberDenied.resultSurface().surfaceId());
    assertEquals(true, memberDenied.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, memberDenied.resultSurface().data().get("noDirectMutation"));
    assertTrue(memberDenied.resultSurface().toString().contains("CAPABILITY_FORBIDDEN"));
    assertBrowserSafe(memberDenied.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-governance-policy-proposal",
        "corr-governance-proposal-member-direct-denied",
        "workos-governance-member",
        "governance-member@example.test",
        "Governance Member",
        MEMBER_CONTEXT_ID), "Regular tenant members must not read the Governance/Policy proposal surface.");

    var crossTenant = runAction(new CapabilityActionRequest(
        "action-governance-policy-simulate",
        "action-governance-policy-simulate",
        "governance.policy.simulate",
        "governance.policy.simulate",
        Map.of("proposalId", proposalId, "tenantId", "tenant-other"),
        null,
        ADMIN_CONTEXT_ID,
        submitted.resultSurface().surfaceId(),
        "corr-governance-proposal-cross-tenant-denied"));
    assertEquals("denied", crossTenant.status());
    assertEquals("surface-governance-policy-system-message", crossTenant.resultSurface().surfaceId());
    assertEquals("governance.policy.system_message.v1", crossTenant.resultSurface().data().get("surfaceContract"));
    assertTrue(crossTenant.resultSurface().toString().contains("GOVERNANCE_POLICY_TENANT_FORBIDDEN"));
    assertBrowserSafe(crossTenant.resultSurface());
  }

  @Test
  @SuppressWarnings("unchecked")
  void hostedShellAndProtectedWorkstreamApiExerciseGovernancePolicySimulationRuntimePath() throws Exception {
    var simulationTenantId = "tenant-governance-simulation-" + System.nanoTime();
    var simulationCustomerId = "customer-governance-simulation-" + System.nanoTime();
    var simulationAdminContextId = "membership-governance-simulation-admin-" + System.nanoTime();
    var simulationMemberContextId = "membership-governance-simulation-member-" + System.nanoTime();
    var simulationAdminSubject = "workos-governance-simulation-admin-" + System.nanoTime();
    var simulationMemberSubject = "workos-governance-simulation-member-" + System.nanoTime();
    var simulationAdminEmail = simulationAdminSubject + "@example.test";
    var simulationMemberEmail = simulationMemberSubject + "@example.test";
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant(simulationTenantId, "Governance Simulation Smoke Tenant", true));
    repository.saveCustomer(new Customer(simulationTenantId, simulationCustomerId, "Governance Simulation Smoke Customer", true));
    seedIdentity(repository, simulationAdminEmail, "Governance Simulation Admin", simulationAdminContextId, List.of(FoundationRole.TENANT_ADMIN), simulationTenantId, simulationCustomerId);
    seedIdentity(repository, simulationMemberEmail, "Governance Simulation Member", simulationMemberContextId, List.of(FoundationRole.TENANT_EMPLOYEE), simulationTenantId, simulationCustomerId);

    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load before the simulation surface is exercised through browser API paths.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertTrue(shell.body().contains("/assets/"));
    assertBrowserSafe(shell.body());

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken(simulationAdminSubject, simulationAdminEmail, "Governance Simulation Admin"))
        .addHeader("X-Selected-Context-Id", simulationAdminContextId)
        .addHeader("X-Correlation-Id", "corr-governance-simulation-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals(simulationAdminContextId, bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-governance-policy") && agent.availability().equals("visible")));
    assertBrowserSafe(bootstrap.body());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-governance-policy-simulation")
        .addHeader("X-Selected-Context-Id", simulationAdminContextId)
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy simulation surface must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", simulationAdminContextId)
        .addHeader("X-Correlation-Id", "corr-governance-simulation-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-governance-policy-simulate",
            "action-governance-policy-simulate",
            "governance.policy.simulate",
            "governance.policy.simulate",
            Map.of("proposalId", "proposal-missing-bearer"),
            "idem-governance-simulation-missing-bearer",
            simulationAdminContextId,
            "surface-governance-policy-simulation",
            "corr-governance-simulation-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy simulation action path must reject missing bearer tokens.");

    var direct = getSurfaceAs("surface-governance-policy-simulation", "corr-governance-simulation-direct-empty", simulationAdminSubject, simulationAdminEmail, "Governance Simulation Admin", simulationAdminContextId);
    assertEquals("surface-governance-policy-simulation", direct.surfaceId());
    assertEquals("governance-diff", direct.surfaceType());
    assertEquals("governance.policy.simulation.v1", direct.data().get("surfaceContract"));
    assertEquals("empty/not-run", direct.data().get("state"));
    assertEquals(true, direct.data().get("noDirectMutation"));
    assertEquals(true, direct.data().get("noFakeSuccess"));
    assertTrue(direct.toString().contains("simulationSummaryPayload"));
    assertTrue(direct.toString().contains("expectedAccessChanges"));
    assertTrue(direct.toString().contains("activationGate"));
    assertTrue(direct.toString().contains("blocked_provider_or_runtime"));
    assertTrue(direct.traceIds().stream().anyMatch(traceId -> traceId.contains("simulation")));
    assertTrue(direct.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-simulate") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-simulation")));
    assertBrowserSafe(direct);

    var draft = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-draft-proposal",
        "action-governance-policy-draft-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of(
            "title", "Simulation smoke authority boundary",
            "rationale", "exercise simulation governance-diff runtime path",
            "proposedContent", "Require simulation evidence, human approval, rollback metadata, tenant isolation, redaction, and trace evidence before authority changes."),
        "idem-governance-simulation-draft",
        simulationAdminContextId,
        "surface-governance-policy-proposal",
        "corr-governance-simulation-draft"),
        simulationAdminSubject, simulationAdminEmail, "Governance Simulation Admin", simulationAdminContextId);
    assertEquals("accepted", draft.status());
    assertEquals("surface-governance-policy-proposal", draft.resultSurface().surfaceId());
    assertBrowserSafe(draft.resultSurface());

    var proposalId = String.valueOf(draft.resultSurface().data().get("proposalId"));
    var submitted = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-submit-proposal",
        "action-governance-policy-submit-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("proposalId", proposalId),
        "idem-governance-simulation-submit",
        simulationAdminContextId,
        draft.resultSurface().surfaceId(),
        "corr-governance-simulation-submit"),
        simulationAdminSubject, simulationAdminEmail, "Governance Simulation Admin", simulationAdminContextId);
    assertEquals("accepted", submitted.status());
    assertEquals("in_review", submitted.resultSurface().data().get("state"));
    assertBrowserSafe(submitted.resultSurface());

    var simulation = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-simulate",
        "action-governance-policy-simulate",
        "governance.policy.simulate",
        "governance.policy.simulate",
        Map.of("proposalId", proposalId, "scenario", "browser smoke expected access, denial, activation gate, and redaction review", "tenantId", simulationTenantId, "customerId", simulationCustomerId),
        "idem-governance-simulation-run",
        simulationAdminContextId,
        submitted.resultSurface().surfaceId(),
        "corr-governance-simulation-run"),
        simulationAdminSubject, simulationAdminEmail, "Governance Simulation Admin", simulationAdminContextId);
    assertEquals("accepted", simulation.status());
    assertEquals("surface-governance-policy-simulation", simulation.resultSurface().surfaceId());
    assertEquals("governance.policy.simulation.v1", simulation.resultSurface().data().get("surfaceContract"));
    assertEquals("ready", simulation.resultSurface().data().get("state"));
    assertEquals("completed_review_required", simulation.resultSurface().data().get("simulationStatus"));
    assertEquals(true, simulation.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, simulation.resultSurface().data().get("noFakeSuccess"));
    assertTrue(simulation.resultSurface().toString().contains("advisory deterministic simulation evidence record"));
    assertTrue(simulation.resultSurface().toString().contains("expectedAccessChanges"));
    assertTrue(simulation.resultSurface().toString().contains("model cannot self-approve"));
    assertTrue(simulation.resultSurface().toString().contains("prompt text cannot grant tool access"));
    assertTrue(simulation.resultSurface().toString().contains("blocked_provider_or_runtime"));
    assertTrue(simulation.resultSurface().toString().contains("rawProviderModelData=omitted"));
    assertTrue(simulation.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-simulation")));
    assertTrue(simulation.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-simulate") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-simulation")));
    assertTrue(simulation.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-decide") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-decision")));
    assertTrue(simulation.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-start-impact-analysis") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-impact-analysis-task")));
    var expectedAccessChanges = (List<Map<String, Object>>) simulation.resultSurface().data().get("expectedAccessChanges");
    assertTrue(expectedAccessChanges.stream().anyMatch(row -> "allow".equals(row.get("expectedOutcome"))));
    assertTrue(expectedAccessChanges.stream().anyMatch(row -> "deny".equals(row.get("expectedOutcome"))));
    assertBrowserSafe(simulation.resultSurface());

    var duplicateSimulation = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-simulate",
        "action-governance-policy-simulate",
        "governance.policy.simulate",
        "governance.policy.simulate",
        Map.of("proposalId", proposalId, "scenario", "duplicate request must not create second evidence record"),
        "idem-governance-simulation-run",
        simulationAdminContextId,
        simulation.resultSurface().surfaceId(),
        "corr-governance-simulation-run-replay"),
        simulationAdminSubject, simulationAdminEmail, "Governance Simulation Admin", simulationAdminContextId);
    assertEquals("no-op", duplicateSimulation.status());
    assertEquals(simulation.resultSurface().data().get("simulationId"), duplicateSimulation.resultSurface().data().get("simulationId"));
    assertTrue(duplicateSimulation.message().contains("no authority changed"));
    assertBrowserSafe(duplicateSimulation.resultSurface());

    var impact = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-start-impact-analysis",
        "action-governance-policy-start-impact-analysis",
        "governance.policy.impact_analysis.start",
        "governance.policy.impact_analysis.start",
        Map.of("proposalId", proposalId, "scope", "simulation-smoke", "reason", "verify simulation surface provider/runtime fail-closed path"),
        "idem-governance-simulation-impact",
        simulationAdminContextId,
        simulation.resultSurface().surfaceId(),
        "corr-governance-simulation-impact"),
        simulationAdminSubject, simulationAdminEmail, "Governance Simulation Admin", simulationAdminContextId);
    assertEquals("blocked_provider_or_runtime", impact.status());
    assertEquals("surface-governance-policy-impact-analysis-task", impact.resultSurface().surfaceId());
    assertEquals("blocked_provider_or_runtime", impact.resultSurface().data().get("status"));
    assertEquals(true, impact.resultSurface().data().get("activationBlockedUntilHumanDecision"));
    assertTrue(impact.resultSurface().toString().contains("AutonomousAgent"));
    assertFalse(impact.resultSurface().toString().contains("impact_ready"));
    assertBrowserSafe(impact.resultSurface());

    var memberDenied = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-simulate",
        "action-governance-policy-simulate",
        "governance.policy.simulate",
        "governance.policy.simulate",
        Map.of("proposalId", proposalId),
        "idem-governance-simulation-member-denied",
        simulationMemberContextId,
        "surface-governance-policy-simulation",
        "corr-governance-simulation-member-denied"),
        simulationMemberSubject,
        simulationMemberEmail,
        "Governance Simulation Member",
        simulationMemberContextId);
    assertEquals("denied", memberDenied.status());
    assertEquals("surface-governance-policy-system-message", memberDenied.resultSurface().surfaceId());
    assertEquals(true, memberDenied.resultSurface().data().get("noDirectMutation"));
    assertTrue(memberDenied.resultSurface().toString().contains("CAPABILITY_FORBIDDEN"));
    assertBrowserSafe(memberDenied.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-governance-policy-simulation",
        "corr-governance-simulation-member-direct-denied",
        simulationMemberSubject,
        simulationMemberEmail,
        "Governance Simulation Member",
        simulationMemberContextId), "Regular tenant members must not read the Governance/Policy simulation surface.");

    var crossTenant = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-simulate",
        "action-governance-policy-simulate",
        "governance.policy.simulate",
        "governance.policy.simulate",
        Map.of("proposalId", proposalId, "tenantId", "tenant-other"),
        "idem-governance-simulation-cross-tenant-denied",
        simulationAdminContextId,
        simulation.resultSurface().surfaceId(),
        "corr-governance-simulation-cross-tenant-denied"),
        simulationAdminSubject, simulationAdminEmail, "Governance Simulation Admin", simulationAdminContextId);
    assertEquals("denied", crossTenant.status());
    assertEquals("surface-governance-policy-system-message", crossTenant.resultSurface().surfaceId());
    assertEquals("governance.policy.system_message.v1", crossTenant.resultSurface().data().get("surfaceContract"));
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

    var proposalPreview = getSurface("surface-governance-policy-proposal", "corr-governance-dashboard-proposal-preview");
    assertEquals("surface-governance-policy-proposal", proposalPreview.surfaceId());
    assertEquals("governance-diff", proposalPreview.surfaceType());
    assertEquals("governance.policy.proposal.v1", proposalPreview.data().get("surfaceContract"));
    assertEquals("empty/new-draft", proposalPreview.data().get("state"));
    assertEquals(true, proposalPreview.data().get("noDirectMutation"));
    assertEquals(true, proposalPreview.data().get("noFakeSuccess"));
    assertTrue(proposalPreview.toString().contains("proposalSummary"));
    assertTrue(proposalPreview.toString().contains("draftFields"));
    assertTrue(proposalPreview.toString().contains("lifecycleGate"));
    assertTrue(proposalPreview.toString().contains("availableTransitions"));
    assertTrue(proposalPreview.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-draft-proposal") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-proposal")));
    assertBrowserSafe(proposalPreview);

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
    assertEquals(true, draft.resultSurface().data().get("noFakeSuccess"));
    assertTrue(draft.resultSurface().toString().contains("proposalSummary"));
    assertTrue(draft.resultSurface().toString().contains("changeSet"));
    assertTrue(draft.resultSurface().toString().contains("availableTransitions"));
    assertTrue(draft.resultSurface().toString().contains("blocked_provider_or_runtime"));
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
    var submitted = runAction(new CapabilityActionRequest(
        "action-governance-policy-submit-proposal",
        "action-governance-policy-submit-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("proposalId", proposalId),
        "idem-governance-dashboard-submit",
        ADMIN_CONTEXT_ID,
        draft.resultSurface().surfaceId(),
        "corr-governance-dashboard-submit"));
    assertEquals("accepted", submitted.status());
    assertEquals("surface-governance-policy-proposal", submitted.resultSurface().surfaceId());
    assertEquals("in_review", submitted.resultSurface().data().get("state"));
    assertTrue(submitted.resultSurface().toString().contains("policy-decision"));
    assertTrue(submitted.resultSurface().toString().contains("admin-audit"));
    assertBrowserSafe(submitted.resultSurface());

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

  @Test
  @SuppressWarnings("unchecked")
  void protectedWorkstreamApiExercisesGovernancePolicyImpactAnalysisTaskRuntimePath() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load before the impact-analysis task is exercised through browser API paths.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertBrowserSafe(shell.body());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-governance-policy-impact-analysis-task")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy impact-analysis task surface path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-governance-impact-task-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-governance-policy-start-impact-analysis",
            "action-governance-policy-start-impact-analysis",
            "governance.policy.impact_analysis.start",
            "governance.policy.impact_analysis.start",
            Map.of("proposalId", "proposal-missing"),
            "idem-governance-impact-task-missing-bearer",
            ADMIN_CONTEXT_ID,
            "surface-governance-policy-impact-analysis-task",
            "corr-governance-impact-task-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy impact-analysis task action path must reject missing bearer tokens.");

    var directReadiness = getSurface("surface-governance-policy-impact-analysis-task", "corr-governance-impact-task-direct-readiness");
    assertEquals("surface-governance-policy-impact-analysis-task", directReadiness.surfaceId());
    assertEquals("workflow-status", directReadiness.surfaceType());
    assertEquals("governance.policy.impact_analysis.task.v1", directReadiness.data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", directReadiness.data().get("status"));
    assertEquals("provider_runtime_blocked_fail_closed", directReadiness.data().get("readinessDecision"));
    assertEquals(true, directReadiness.data().get("noFakeSuccess"));
    assertEquals(true, directReadiness.data().get("noDirectMutation"));
    assertTrue(directReadiness.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-start-impact-analysis") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-impact-analysis-task")));
    assertBrowserSafe(directReadiness);

    var draft = runAction(new CapabilityActionRequest(
        "action-governance-policy-draft-proposal",
        "action-governance-policy-draft-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("title", "Impact task smoke proposal", "rationale", "exercise durable impact-analysis task runtime path", "proposedContent", "Keep impact analysis advisory, traceable, and fail-closed when provider/runtime is unavailable."),
        "idem-governance-impact-task-draft",
        ADMIN_CONTEXT_ID,
        "surface-governance-policy-proposal",
        "corr-governance-impact-task-draft"));
    assertEquals("accepted", draft.status());
    assertBrowserSafe(draft.resultSurface());
    var proposalId = String.valueOf(draft.resultSurface().data().get("proposalId"));

    var missingTask = runAction(new CapabilityActionRequest(
        "action-governance-policy-read-impact-analysis",
        "action-governance-policy-read-impact-analysis",
        "governance.policy.impact_analysis.read",
        "governance.policy.impact_analysis.read",
        Map.of("taskId", "governance-impact-missing"),
        null,
        ADMIN_CONTEXT_ID,
        directReadiness.surfaceId(),
        "corr-governance-impact-task-missing-read"));
    assertEquals("denied", missingTask.status());
    assertEquals("surface-governance-policy-system-message", missingTask.resultSurface().surfaceId());
    assertEquals(true, missingTask.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, missingTask.resultSurface().data().get("noDirectMutation"));
    assertBrowserSafe(missingTask.resultSurface());

    var started = runAction(new CapabilityActionRequest(
        "action-governance-policy-start-impact-analysis",
        "action-governance-policy-start-impact-analysis",
        "governance.policy.impact_analysis.start",
        "governance.policy.impact_analysis.start",
        Map.of("proposalId", proposalId, "scope", "task-smoke", "reason", "verify task start/read/cancel provider fail-closed path", "evidenceRefs", List.of("evidence:task-smoke")),
        "idem-governance-impact-task-start",
        ADMIN_CONTEXT_ID,
        directReadiness.surfaceId(),
        "corr-governance-impact-task-start"));
    assertEquals("blocked_provider_or_runtime", started.status());
    assertEquals("surface-governance-policy-impact-analysis-task", started.resultSurface().surfaceId());
    assertEquals("workflow-status", started.resultSurface().surfaceType());
    assertEquals("governance.policy.impact_analysis.task.v1", started.resultSurface().data().get("surfaceContract"));
    assertEquals("blocked_provider_or_runtime", started.resultSurface().data().get("status"));
    assertEquals(true, started.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, started.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, started.resultSurface().data().get("activationBlockedUntilHumanDecision"));
    assertTrue(started.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-impact")));
    assertTrue(started.resultSurface().toString().contains("AutonomousAgent"));
    assertTrue(started.resultSurface().toString().contains("provider/runtime"));
    assertTrue(started.resultSurface().toString().contains("raw prompts"));
    assertTrue(started.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-read-impact-analysis") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-impact-analysis-task")));
    assertTrue(started.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-cancel-impact-analysis") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-impact-analysis-task")));
    var impactTaskId = String.valueOf(started.resultSurface().data().get("impactTaskId"));
    assertNotNull(impactTaskId);
    assertBrowserSafe(started.resultSurface());

    var replay = runAction(new CapabilityActionRequest(
        "action-governance-policy-start-impact-analysis",
        "action-governance-policy-start-impact-analysis",
        "governance.policy.impact_analysis.start",
        "governance.policy.impact_analysis.start",
        Map.of("proposalId", proposalId, "scope", "task-smoke-replay", "reason", "idempotent replay must not duplicate impact tasks"),
        "idem-governance-impact-task-start",
        ADMIN_CONTEXT_ID,
        directReadiness.surfaceId(),
        "corr-governance-impact-task-start-replay"));
    assertEquals("blocked_provider_or_runtime", replay.status());
    assertEquals(impactTaskId, replay.resultSurface().data().get("impactTaskId"));
    assertBrowserSafe(replay.resultSurface());

    var read = runAction(new CapabilityActionRequest(
        "action-governance-policy-read-impact-analysis",
        "action-governance-policy-read-impact-analysis",
        "governance.policy.impact_analysis.read",
        "governance.policy.impact_analysis.read",
        Map.of("impactTaskId", impactTaskId),
        null,
        ADMIN_CONTEXT_ID,
        started.resultSurface().surfaceId(),
        "corr-governance-impact-task-read"));
    assertEquals("accepted", read.status());
    assertEquals(impactTaskId, read.resultSurface().data().get("impactTaskId"));
    assertEquals("blocked_provider_or_runtime", read.resultSurface().data().get("status"));
    assertTrue(read.resultSurface().toString().contains("traceLinks"));
    assertBrowserSafe(read.resultSurface());

    var cancel = runAction(new CapabilityActionRequest(
        "action-governance-policy-cancel-impact-analysis",
        "action-governance-policy-cancel-impact-analysis",
        "governance.policy.impact_analysis.cancel",
        "governance.policy.impact_analysis.cancel",
        Map.of("impactTaskId", impactTaskId, "reason", "task smoke cancellation verifies advisory-only lifecycle"),
        "idem-governance-impact-task-cancel",
        ADMIN_CONTEXT_ID,
        started.resultSurface().surfaceId(),
        "corr-governance-impact-task-cancel"));
    assertEquals("accepted", cancel.status());
    assertEquals("cancelled", cancel.resultSurface().data().get("status"));
    assertEquals(true, cancel.resultSurface().data().get("noDirectMutation"));
    assertTrue(cancel.message().contains("policy proposal unchanged"));
    assertTrue(cancel.resultSurface().toString().contains("cancelled"));
    assertBrowserSafe(cancel.resultSurface());

    var repeatCancel = runAction(new CapabilityActionRequest(
        "action-governance-policy-cancel-impact-analysis",
        "action-governance-policy-cancel-impact-analysis",
        "governance.policy.impact_analysis.cancel",
        "governance.policy.impact_analysis.cancel",
        Map.of("impactTaskId", impactTaskId, "reason", "repeat cancel must remain idempotent"),
        "idem-governance-impact-task-cancel-repeat",
        ADMIN_CONTEXT_ID,
        cancel.resultSurface().surfaceId(),
        "corr-governance-impact-task-cancel-repeat"));
    assertEquals("accepted", repeatCancel.status());
    assertEquals("cancelled", repeatCancel.resultSurface().data().get("status"));
    assertBrowserSafe(repeatCancel.resultSurface());

    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant("tenant-governance-other", "Governance Other Tenant", true));
    repository.saveCustomer(new Customer("tenant-governance-other", "customer-governance-other", "Governance Other Customer", true));
    seedIdentity(repository, "governance-other-admin@example.test", "Governance Other Admin", "membership-governance-other-admin", List.of(FoundationRole.TENANT_ADMIN), "tenant-governance-other", "customer-governance-other");

    var crossTenantDenied = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-read-impact-analysis",
        "action-governance-policy-read-impact-analysis",
        "governance.policy.impact_analysis.read",
        "governance.policy.impact_analysis.read",
        Map.of("impactTaskId", impactTaskId),
        null,
        "membership-governance-other-admin",
        started.resultSurface().surfaceId(),
        "corr-governance-impact-task-cross-tenant-denied"),
        "workos-governance-other-admin",
        "governance-other-admin@example.test",
        "Governance Other Admin",
        "membership-governance-other-admin");
    assertEquals("denied", crossTenantDenied.status());
    assertEquals("surface-governance-policy-system-message", crossTenantDenied.resultSurface().surfaceId());
    assertTrue(crossTenantDenied.resultSurface().toString().contains("not-found") || crossTenantDenied.resultSurface().toString().contains("forbidden"));
    assertEquals(true, crossTenantDenied.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, crossTenantDenied.resultSurface().data().get("noDirectMutation"));
    assertBrowserSafe(crossTenantDenied.resultSurface());

    var memberDenied = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-read-impact-analysis",
        "action-governance-policy-read-impact-analysis",
        "governance.policy.impact_analysis.read",
        "governance.policy.impact_analysis.read",
        Map.of("impactTaskId", impactTaskId),
        null,
        MEMBER_CONTEXT_ID,
        started.resultSurface().surfaceId(),
        "corr-governance-impact-task-member-denied"),
        "workos-governance-member",
        "governance-member@example.test",
        "Governance Member",
        MEMBER_CONTEXT_ID);
    assertEquals("denied", memberDenied.status());
    assertEquals("surface-governance-policy-system-message", memberDenied.resultSurface().surfaceId());
    assertTrue(memberDenied.resultSurface().toString().contains("CAPABILITY_FORBIDDEN"));
    assertEquals(true, memberDenied.resultSurface().data().get("noFakeSuccess"));
    assertEquals(true, memberDenied.resultSurface().data().get("noDirectMutation"));
    assertBrowserSafe(memberDenied.resultSurface());
  }

  @Test
  void protectedWorkstreamApiExercisesGovernancePolicyDecisionRuntimePath() throws Exception {
    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load before the decision surface is exercised through browser API paths.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertBrowserSafe(shell.body());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-governance-policy-decision")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy decision surface path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", ADMIN_CONTEXT_ID)
        .addHeader("X-Correlation-Id", "corr-governance-decision-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-governance-policy-decide",
            "action-governance-policy-decide",
            "governance.proposals.review",
            "governance.policy.approve",
            Map.of("proposalId", "proposal-missing", "decision", "approve"),
            "idem-governance-decision-missing-bearer",
            ADMIN_CONTEXT_ID,
            "surface-governance-policy-decision",
            "corr-governance-decision-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy decision action path must reject missing bearer tokens.");

    var draft = runAction(new CapabilityActionRequest(
        "action-governance-policy-draft-proposal",
        "action-governance-policy-draft-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("title", "Decision smoke proposal", "rationale", "exercise decision-card runtime path", "proposedContent", "Require human approval, advisory simulation, rollback metadata, tenant isolation, and audit traces before authority changes."),
        "idem-governance-decision-draft",
        ADMIN_CONTEXT_ID,
        "surface-governance-policy-proposal",
        "corr-governance-decision-draft"));
    assertEquals("accepted", draft.status());
    var proposalId = String.valueOf(draft.resultSurface().data().get("proposalId"));
    assertBrowserSafe(draft.resultSurface());

    var submitted = runAction(new CapabilityActionRequest(
        "action-governance-policy-submit-proposal",
        "action-governance-policy-submit-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("proposalId", proposalId),
        "idem-governance-decision-submit",
        ADMIN_CONTEXT_ID,
        draft.resultSurface().surfaceId(),
        "corr-governance-decision-submit"));
    assertEquals("accepted", submitted.status());
    assertEquals("in_review", submitted.resultSurface().data().get("state"));
    assertBrowserSafe(submitted.resultSurface());

    var simulation = runAction(new CapabilityActionRequest(
        "action-governance-policy-simulate",
        "action-governance-policy-simulate",
        "governance.policy.simulate",
        "governance.policy.simulate",
        Map.of("proposalId", proposalId, "scenario", "decision smoke simulation evidence"),
        "idem-governance-decision-simulation",
        ADMIN_CONTEXT_ID,
        submitted.resultSurface().surfaceId(),
        "corr-governance-decision-simulation"));
    assertEquals("accepted", simulation.status());
    assertEquals("surface-governance-policy-simulation", simulation.resultSurface().surfaceId());
    assertBrowserSafe(simulation.resultSurface());

    var directDecision = getSurface("surface-governance-policy-decision", "corr-governance-decision-direct");
    assertEquals("surface-governance-policy-decision", directDecision.surfaceId());
    assertEquals("decision-card", directDecision.surfaceType());
    assertEquals("governance.policy.decision.v1", directDecision.data().get("surfaceContract"));
    assertNotNull(directDecision.data().get("proposalId"));
    assertNotNull(directDecision.data().get("status"));
    assertEquals(true, directDecision.data().get("noDirectMutation"));
    assertEquals(true, directDecision.data().get("noFakeSuccess"));
    assertTrue(directDecision.toString().contains("decisionSummary"));
    assertTrue(directDecision.toString().contains("riskAndImpact"));
    assertTrue(directDecision.toString().contains("decisionEvidence"));
    assertTrue(directDecision.toString().contains("blocked_provider_or_runtime"), "Decision evidence must keep advisory provider/runtime readiness fail-closed instead of fabricating impact analysis.");
    assertTrue(directDecision.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-decide") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-decision")));
    assertTrue(directDecision.actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-activate") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-decision")));
    assertBrowserSafe(directDecision);

    var missingIdempotency = runAction(new CapabilityActionRequest(
        "action-governance-policy-decide",
        "action-governance-policy-decide",
        "governance.proposals.review",
        "governance.policy.approve",
        Map.of("proposalId", proposalId, "decision", "approve", "rationale", "missing idempotency should not mutate"),
        null,
        ADMIN_CONTEXT_ID,
        directDecision.surfaceId(),
        "corr-governance-decision-missing-idempotency"));
    assertEquals("denied", missingIdempotency.status());
    assertEquals("surface-governance-policy-system-message", missingIdempotency.resultSurface().surfaceId());
    assertEquals(true, missingIdempotency.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, missingIdempotency.resultSurface().data().get("noFakeSuccess"));
    assertTrue(missingIdempotency.resultSurface().toString().contains("idempotency"));
    assertBrowserSafe(missingIdempotency.resultSurface());

    var missingCapability = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-decide",
        "action-governance-policy-decide",
        "governance.proposals.review",
        "governance.policy.approve",
        Map.of("proposalId", proposalId, "decision", "approve", "rationale", "member context must be denied"),
        "idem-governance-decision-member-denied",
        MEMBER_CONTEXT_ID,
        directDecision.surfaceId(),
        "corr-governance-decision-member-denied"),
        "workos-governance-member",
        "governance-member@example.test",
        "Governance Member",
        MEMBER_CONTEXT_ID);
    assertEquals("denied", missingCapability.status());
    assertEquals("surface-governance-policy-system-message", missingCapability.resultSurface().surfaceId());
    assertTrue(missingCapability.resultSurface().toString().contains("CAPABILITY_FORBIDDEN"));
    assertEquals(true, missingCapability.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, missingCapability.resultSurface().data().get("noFakeSuccess"));
    assertBrowserSafe(missingCapability.resultSurface());

    var crossTenantDecision = runAction(new CapabilityActionRequest(
        "action-governance-policy-decide",
        "action-governance-policy-decide",
        "governance.proposals.review",
        "governance.policy.approve",
        Map.of("tenantId", "tenant-other", "proposalId", proposalId, "decision", "approve", "rationale", "cross tenant decision must be denied"),
        "idem-governance-decision-cross-tenant",
        ADMIN_CONTEXT_ID,
        directDecision.surfaceId(),
        "corr-governance-decision-cross-tenant"));
    assertEquals("denied", crossTenantDecision.status());
    assertEquals("surface-governance-policy-system-message", crossTenantDecision.resultSurface().surfaceId());
    assertTrue(crossTenantDecision.resultSurface().toString().contains("GOVERNANCE_POLICY_TENANT_FORBIDDEN"));
    assertTrue(crossTenantDecision.resultSurface().toString().contains("protected data omitted"));
    assertEquals(true, crossTenantDecision.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, crossTenantDecision.resultSurface().data().get("noFakeSuccess"));
    assertBrowserSafe(crossTenantDecision.resultSurface());

    var decision = runAction(new CapabilityActionRequest(
        "action-governance-policy-decide",
        "action-governance-policy-decide",
        "governance.proposals.review",
        "governance.policy.approve",
        Map.of("proposalId", proposalId, "decision", "approve", "rationale", "decision smoke human approval"),
        "idem-governance-decision-approve",
        ADMIN_CONTEXT_ID,
        directDecision.surfaceId(),
        "corr-governance-decision-approve"));
    assertEquals("accepted", decision.status());
    assertEquals("surface-governance-policy-decision", decision.resultSurface().surfaceId());
    assertEquals("approved", decision.resultSurface().data().get("status"));
    assertTrue(decision.resultSurface().toString().contains("disabledActions"));
    assertTrue(decision.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-decision")));
    assertBrowserSafe(decision.resultSurface());

    var decisionReplay = runAction(new CapabilityActionRequest(
        "action-governance-policy-decide",
        "action-governance-policy-decide",
        "governance.proposals.review",
        "governance.policy.approve",
        Map.of("proposalId", proposalId, "decision", "reject", "rationale", "replay must not alter approved state"),
        "idem-governance-decision-approve",
        ADMIN_CONTEXT_ID,
        directDecision.surfaceId(),
        "corr-governance-decision-approve-replay"));
    assertEquals("no-op", decisionReplay.status());
    assertEquals("approved", decisionReplay.resultSurface().data().get("status"), "Decision replay must preserve the already-approved state instead of committing a second lifecycle transition.");
    assertBrowserSafe(decisionReplay.resultSurface());

    var activationBlocked = runAction(new CapabilityActionRequest(
        "action-governance-policy-activate",
        "action-governance-policy-activate",
        "governance.proposals.activate",
        "governance.policy.activate",
        Map.of("proposalId", proposalId),
        "idem-governance-decision-activate-blocked",
        ADMIN_CONTEXT_ID,
        decision.resultSurface().surfaceId(),
        "corr-governance-decision-activate-blocked"));
    assertEquals("approval-required", activationBlocked.status());
    assertEquals("surface-governance-policy-activation-blocked", activationBlocked.resultSurface().surfaceId());
    assertEquals(true, activationBlocked.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, activationBlocked.resultSurface().data().get("noFakeSuccess"));
    assertTrue(activationBlocked.resultSurface().toString().contains("sideEffect=none"));
    assertBrowserSafe(activationBlocked.resultSurface());

    var activation = runAction(new CapabilityActionRequest(
        "action-governance-policy-activate",
        "action-governance-policy-activate",
        "governance.proposals.activate",
        "governance.policy.activate",
        Map.of("proposalId", proposalId, "rollbackReference", "decision smoke rollback metadata"),
        "idem-governance-decision-activate",
        ADMIN_CONTEXT_ID,
        decision.resultSurface().surfaceId(),
        "corr-governance-decision-activate"));
    assertEquals("accepted", activation.status());
    assertEquals("surface-governance-policy-decision", activation.resultSurface().surfaceId());
    assertEquals("activated", activation.resultSurface().data().get("status"));
    assertTrue(activation.resultSurface().toString().contains("activated-with-rollback-metadata"));
    assertBrowserSafe(activation.resultSurface());

    var rollback = runAction(new CapabilityActionRequest(
        "action-governance-policy-rollback",
        "action-governance-policy-rollback",
        "governance.proposals.activate",
        "governance.policy.rollback",
        Map.of("proposalId", proposalId),
        "idem-governance-decision-rollback",
        ADMIN_CONTEXT_ID,
        activation.resultSurface().surfaceId(),
        "corr-governance-decision-rollback"));
    assertEquals("accepted", rollback.status());
    assertEquals("surface-governance-policy-decision", rollback.resultSurface().surfaceId());
    assertEquals("rolled_back", rollback.resultSurface().data().get("status"));
    assertTrue(rollback.resultSurface().toString().contains("policy-decision"));
    assertBrowserSafe(rollback.resultSurface());
  }

  @Test
  @SuppressWarnings("unchecked")
  void protectedWorkstreamApiExercisesGovernancePolicyOutcomeRuntimePath() throws Exception {
    var outcomeTenantId = "tenant-governance-outcome-" + System.nanoTime();
    var outcomeCustomerId = "customer-governance-outcome-" + System.nanoTime();
    var outcomeAdminContextId = "membership-governance-outcome-admin-" + System.nanoTime();
    var outcomeMemberContextId = "membership-governance-outcome-member-" + System.nanoTime();
    var outcomeAdminSubject = "workos-governance-outcome-admin-" + System.nanoTime();
    var outcomeMemberSubject = "workos-governance-outcome-member-" + System.nanoTime();
    var outcomeAdminEmail = outcomeAdminSubject + "@example.test";
    var outcomeMemberEmail = outcomeMemberSubject + "@example.test";
    var repository = new AkkaIdentityRepository(componentClient);
    repository.saveTenant(new Tenant(outcomeTenantId, "Governance Outcome Smoke Tenant", true));
    repository.saveCustomer(new Customer(outcomeTenantId, outcomeCustomerId, "Governance Outcome Smoke Customer", true));
    seedIdentity(repository, outcomeAdminEmail, "Governance Outcome Admin", outcomeAdminContextId, List.of(FoundationRole.TENANT_ADMIN), outcomeTenantId, outcomeCustomerId);
    seedIdentity(repository, outcomeMemberEmail, "Governance Outcome Member", outcomeMemberContextId, List.of(FoundationRole.TENANT_EMPLOYEE), outcomeTenantId, outcomeCustomerId);

    var shell = httpClient.GET("/ui").responseBodyAs(String.class).invoke();
    assertTrue(shell.status().isSuccess(), "Hosted /ui shell must load before the outcome surface is exercised through browser API paths.");
    assertTrue(shell.body().contains("<div id=\"root\"></div>"));
    assertTrue(shell.body().contains("/assets/"));
    assertBrowserSafe(shell.body());

    var bootstrap = httpClient
        .GET("/api/workstream/bootstrap")
        .addHeader("Authorization", "Bearer " + bearerToken(outcomeAdminSubject, outcomeAdminEmail, "Governance Outcome Admin"))
        .addHeader("X-Selected-Context-Id", outcomeAdminContextId)
        .addHeader("X-Correlation-Id", "corr-governance-outcome-bootstrap")
        .responseBodyAs(WorkstreamBootstrapResponse.class)
        .invoke();
    assertTrue(bootstrap.status().isSuccess());
    assertEquals(outcomeAdminContextId, bootstrap.body().me().selectedAuthContext().selectedContextId());
    assertTrue(bootstrap.body().functionalAgents().stream().anyMatch(agent -> agent.functionalAgentId().equals("agent-governance-policy") && agent.availability().equals("visible")));
    assertBrowserSafe(bootstrap.body());

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .GET("/api/workstream/surfaces/surface-governance-policy-outcome")
        .addHeader("X-Selected-Context-Id", outcomeAdminContextId)
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy outcome surface path must reject missing bearer tokens.");

    assertThrows(IllegalArgumentException.class, () -> httpClient
        .POST("/api/workstream/actions")
        .addHeader("X-Selected-Context-Id", outcomeAdminContextId)
        .addHeader("X-Correlation-Id", "corr-governance-outcome-missing-bearer-action")
        .withRequestBody(new CapabilityActionRequest(
            "action-governance-policy-outcome-note",
            "action-governance-policy-outcome-note",
            "governance.outcomes.record",
            "governance.outcomes.record",
            Map.of("proposalId", "proposal-missing", "note", "missing bearer must be rejected"),
            "idem-governance-outcome-missing-bearer",
            outcomeAdminContextId,
            "surface-governance-policy-outcome",
            "corr-governance-outcome-missing-bearer-action"))
        .responseBodyAs(String.class)
        .invoke(), "Protected Governance/Policy outcome action path must reject missing bearer tokens.");

    var directEmpty = getSurfaceAs("surface-governance-policy-outcome", "corr-governance-outcome-direct-empty", outcomeAdminSubject, outcomeAdminEmail, "Governance Outcome Admin", outcomeAdminContextId);
    assertEquals("surface-governance-policy-outcome", directEmpty.surfaceId());
    assertEquals("outcome-panel", directEmpty.surfaceType());
    assertEquals("governance.policy.outcome.v1", directEmpty.data().get("surfaceContract"));
    assertEquals(true, directEmpty.data().get("noDirectMutation"));
    assertEquals(true, directEmpty.data().get("noFakeSuccess"));
    assertTrue(directEmpty.toString().contains("missing_visible_proposal") || directEmpty.toString().contains("outcomeSummary"));
    assertTrue(directEmpty.toString().contains("blocked_provider_or_runtime"));
    assertTrue(directEmpty.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-outcome")));
    assertBrowserSafe(directEmpty);

    var draft = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-draft-proposal",
        "action-governance-policy-draft-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of(
            "title", "Outcome smoke policy proposal",
            "rationale", "exercise outcome-panel runtime path",
            "proposedContent", "Record outcome evidence without approving, activating, rolling back, mutating authority, or exposing secrets."),
        "idem-governance-outcome-draft",
        outcomeAdminContextId,
        "surface-governance-policy-proposal",
        "corr-governance-outcome-draft"),
        outcomeAdminSubject, outcomeAdminEmail, "Governance Outcome Admin", outcomeAdminContextId);
    assertEquals("accepted", draft.status());
    var proposalId = String.valueOf(draft.resultSurface().data().get("proposalId"));
    assertBrowserSafe(draft.resultSurface());

    var submitted = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-submit-proposal",
        "action-governance-policy-submit-proposal",
        "governance.policy.propose",
        "governance.policy.propose",
        Map.of("proposalId", proposalId),
        "idem-governance-outcome-submit",
        outcomeAdminContextId,
        draft.resultSurface().surfaceId(),
        "corr-governance-outcome-submit"),
        outcomeAdminSubject, outcomeAdminEmail, "Governance Outcome Admin", outcomeAdminContextId);
    assertEquals("accepted", submitted.status());
    assertEquals("in_review", submitted.resultSurface().data().get("state"));
    assertBrowserSafe(submitted.resultSurface());

    var openedOutcome = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-read",
        "action-governance-policy-read",
        "governance.policy.read",
        "governance.policy.read",
        Map.of("proposalId", proposalId, "targetSurfaceId", "surface-governance-policy-outcome", "tenantId", outcomeTenantId, "customerId", outcomeCustomerId),
        null,
        outcomeAdminContextId,
        "surface-governance-policy-outcome",
        "corr-governance-outcome-open"),
        outcomeAdminSubject, outcomeAdminEmail, "Governance Outcome Admin", outcomeAdminContextId);
    assertEquals("accepted", openedOutcome.status());
    assertEquals("surface-governance-policy-outcome", openedOutcome.resultSurface().surfaceId());
    assertEquals("outcome-panel", openedOutcome.resultSurface().surfaceType());
    assertEquals("governance.policy.outcome.v1", openedOutcome.resultSurface().data().get("surfaceContract"));
    assertEquals(proposalId, openedOutcome.resultSurface().data().get("proposalId"));
    assertEquals(true, openedOutcome.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, openedOutcome.resultSurface().data().get("noFakeSuccess"));
    assertTrue(openedOutcome.resultSurface().toString().contains("outcomeSummary"));
    assertTrue(openedOutcome.resultSurface().toString().contains("metrics"));
    assertTrue(openedOutcome.resultSurface().toString().contains("recommendations"));
    assertTrue(openedOutcome.resultSurface().toString().contains("evidenceRefs"));
    assertTrue(openedOutcome.resultSurface().toString().contains("noteForm"));
    assertTrue(openedOutcome.resultSurface().toString().contains("blocked_provider_or_runtime"));
    assertTrue(openedOutcome.resultSurface().toString().contains("raw ids, provider output, prompts, tool payloads, and secrets are role-gated"));
    assertTrue(openedOutcome.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-outcome")));
    assertTrue(openedOutcome.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-outcome-note") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-outcome")));
    assertTrue(openedOutcome.resultSurface().actions().stream().anyMatch(action -> action.actionId().equals("action-governance-policy-start-impact-analysis") && action.resultSurface().updateSurfaceId().equals("surface-governance-policy-impact-analysis-task")));
    var metrics = (List<Map<String, Object>>) openedOutcome.resultSurface().data().get("metrics");
    assertTrue(metrics.stream().anyMatch(metric -> "provider-runtime-readiness".equals(metric.get("metricId")) && String.valueOf(metric.get("summary")).contains("blocked_provider_or_runtime")));
    assertBrowserSafe(openedOutcome.resultSurface());

    var missingIdempotency = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-outcome-note",
        "action-governance-policy-outcome-note",
        "governance.outcomes.record",
        "governance.outcomes.record",
        Map.of("proposalId", proposalId, "note", "missing idempotency should not append"),
        null,
        outcomeAdminContextId,
        openedOutcome.resultSurface().surfaceId(),
        "corr-governance-outcome-missing-idempotency"),
        outcomeAdminSubject, outcomeAdminEmail, "Governance Outcome Admin", outcomeAdminContextId);
    assertEquals("denied", missingIdempotency.status());
    assertEquals("surface-governance-policy-system-message", missingIdempotency.resultSurface().surfaceId());
    assertTrue(missingIdempotency.resultSurface().toString().contains("idempotency"));
    assertEquals(true, missingIdempotency.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, missingIdempotency.resultSurface().data().get("noFakeSuccess"));
    assertBrowserSafe(missingIdempotency.resultSurface());

    var noted = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-outcome-note",
        "action-governance-policy-outcome-note",
        "governance.outcomes.record",
        "governance.outcomes.record",
        Map.of("proposalId", proposalId, "note", "Human reviewer observed outcome evidence and no authority changed.", "tenantId", outcomeTenantId),
        "idem-governance-outcome-note",
        outcomeAdminContextId,
        openedOutcome.resultSurface().surfaceId(),
        "corr-governance-outcome-note"),
        outcomeAdminSubject, outcomeAdminEmail, "Governance Outcome Admin", outcomeAdminContextId);
    assertEquals("accepted", noted.status());
    assertEquals("surface-governance-policy-outcome", noted.resultSurface().surfaceId());
    assertEquals("governance.policy.outcome.v1", noted.resultSurface().data().get("surfaceContract"));
    assertTrue(noted.message().contains("no direct authority change"));
    assertTrue(noted.resultSurface().toString().contains("Human reviewer observed outcome evidence"));
    assertTrue(noted.resultSurface().toString().contains("admin-audit"));
    assertTrue(noted.resultSurface().toString().contains("policy-decision"));
    assertTrue(noted.traceIds().stream().anyMatch(traceId -> traceId.contains("trace-governance-policy-outcome-note")));
    assertBrowserSafe(noted.resultSurface());

    var impact = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-start-impact-analysis",
        "action-governance-policy-start-impact-analysis",
        "governance.policy.impact_analysis.start",
        "governance.policy.impact_analysis.start",
        Map.of("proposalId", proposalId, "scope", "outcome-smoke", "reason", "verify outcome panel provider/runtime fail-closed path"),
        "idem-governance-outcome-impact",
        outcomeAdminContextId,
        noted.resultSurface().surfaceId(),
        "corr-governance-outcome-impact"),
        outcomeAdminSubject, outcomeAdminEmail, "Governance Outcome Admin", outcomeAdminContextId);
    assertEquals("blocked_provider_or_runtime", impact.status());
    assertEquals("surface-governance-policy-impact-analysis-task", impact.resultSurface().surfaceId());
    assertEquals("blocked_provider_or_runtime", impact.resultSurface().data().get("status"));
    assertTrue(impact.resultSurface().toString().contains("AutonomousAgent"));
    assertFalse(impact.resultSurface().toString().contains("impact_ready"));
    assertBrowserSafe(impact.resultSurface());

    var memberDenied = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-outcome-note",
        "action-governance-policy-outcome-note",
        "governance.outcomes.record",
        "governance.outcomes.record",
        Map.of("proposalId", proposalId, "note", "member context must be denied"),
        "idem-governance-outcome-member-denied",
        outcomeMemberContextId,
        openedOutcome.resultSurface().surfaceId(),
        "corr-governance-outcome-member-denied"),
        outcomeMemberSubject,
        outcomeMemberEmail,
        "Governance Outcome Member",
        outcomeMemberContextId);
    assertEquals("denied", memberDenied.status());
    assertEquals("surface-governance-policy-system-message", memberDenied.resultSurface().surfaceId());
    assertTrue(memberDenied.resultSurface().toString().contains("CAPABILITY_FORBIDDEN"));
    assertEquals(true, memberDenied.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, memberDenied.resultSurface().data().get("noFakeSuccess"));
    assertBrowserSafe(memberDenied.resultSurface());

    assertThrows(RuntimeException.class, () -> getSurfaceAs(
        "surface-governance-policy-outcome",
        "corr-governance-outcome-member-direct-denied",
        outcomeMemberSubject,
        outcomeMemberEmail,
        "Governance Outcome Member",
        outcomeMemberContextId), "Regular tenant members must not read the Governance/Policy outcome surface.");

    var crossTenant = runActionAs(new CapabilityActionRequest(
        "action-governance-policy-outcome-note",
        "action-governance-policy-outcome-note",
        "governance.outcomes.record",
        "governance.outcomes.record",
        Map.of("proposalId", proposalId, "tenantId", "tenant-other", "note", "cross tenant outcome must be denied"),
        "idem-governance-outcome-cross-tenant",
        outcomeAdminContextId,
        noted.resultSurface().surfaceId(),
        "corr-governance-outcome-cross-tenant"),
        outcomeAdminSubject, outcomeAdminEmail, "Governance Outcome Admin", outcomeAdminContextId);
    assertEquals("denied", crossTenant.status());
    assertEquals("surface-governance-policy-system-message", crossTenant.resultSurface().surfaceId());
    assertTrue(crossTenant.resultSurface().toString().contains("GOVERNANCE_POLICY_TENANT_FORBIDDEN"));
    assertEquals(true, crossTenant.resultSurface().data().get("noDirectMutation"));
    assertEquals(true, crossTenant.resultSurface().data().get("noFakeSuccess"));
    assertBrowserSafe(crossTenant.resultSurface());
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
    try {
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
    } catch (RuntimeException e) {
      throw new RuntimeException("Workstream action failed for actionId=" + request.actionId() + ", correlationId=" + request.correlationId(), e);
    }
  }

  private void seedIdentity(AkkaIdentityRepository repository, String email, String displayName, String membershipId, List<FoundationRole> roles) {
    seedIdentity(repository, email, displayName, membershipId, roles, TENANT_ID, CUSTOMER_ID);
  }

  private void seedIdentity(AkkaIdentityRepository repository, String email, String displayName, String membershipId, List<FoundationRole> roles, String tenantId, String customerId) {
    repository.saveAccount(new Account(email, null, email, email, AccountStatus.ACTIVE, "UNLINKED"));
    repository.saveProfile(new UserProfile(email, email, displayName, null, null, null));
    repository.saveSettings(new UserSettings(email, UserSettings.ThemeId.AURORA_LIGHT));
    repository.saveMembership(new Membership(membershipId, email, ScopeType.TENANT, tenantId, null, roles, MembershipStatus.ACTIVE, false, null));
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
