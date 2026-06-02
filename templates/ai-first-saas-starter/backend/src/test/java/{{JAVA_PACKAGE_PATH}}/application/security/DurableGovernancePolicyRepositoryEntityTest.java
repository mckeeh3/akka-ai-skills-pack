package {{JAVA_BASE_PACKAGE}}.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.KeyValueEntityTestKit;
import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicyProposal;
import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicySimulationResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class DurableGovernancePolicyRepositoryEntityTest {

  private KeyValueEntityTestKit<DurableGovernancePolicyRepositoryEntity.State, DurableGovernancePolicyRepositoryEntity> newTestKit() {
    return KeyValueEntityTestKit.of(
        DurableGovernancePolicyRepositoryEntity.ENTITY_ID,
        __ -> new DurableGovernancePolicyRepositoryEntity());
  }

  @Test
  void persistsProposalLifecycleAndIdempotencyThroughAkkaState() {
    var testKit = newTestKit();
    var draft = proposal("proposal-1", "tenant-1", null, "admin-1", "idem-1", GovernancePolicyProposal.Status.DRAFT);
    var submitted = draft.submitted("corr-submit", Instant.parse("2026-05-30T00:01:00Z"));
    var approved = submitted.approved("approved by human", "corr-decision", Instant.parse("2026-05-30T00:02:00Z"));
    var activated = approved.activated("rollback-v1", "corr-activate", Instant.parse("2026-05-30T00:03:00Z"));

    assertTrue(testKit.method(DurableGovernancePolicyRepositoryEntity::saveProposal).invoke(draft).stateWasUpdated());
    assertTrue(testKit.method(DurableGovernancePolicyRepositoryEntity::saveProposal).invoke(submitted).stateWasUpdated());
    assertTrue(testKit.method(DurableGovernancePolicyRepositoryEntity::saveProposal).invoke(approved).stateWasUpdated());
    assertTrue(testKit.method(DurableGovernancePolicyRepositoryEntity::saveProposal).invoke(activated).stateWasUpdated());

    assertEquals(activated, testKit.method(DurableGovernancePolicyRepositoryEntity::findProposal)
        .invoke(new DurableGovernancePolicyRepositoryEntity.ProposalQuery("tenant-1", null, "proposal-1")).getReply().orElseThrow());
    assertEquals(activated, testKit.method(DurableGovernancePolicyRepositoryEntity::findByIdempotencyKey)
        .invoke(new DurableGovernancePolicyRepositoryEntity.IdempotencyQuery("tenant-1", null, "admin-1", "idem-1")).getReply().orElseThrow());
    assertEquals(List.of(activated), testKit.method(DurableGovernancePolicyRepositoryEntity::listProposals)
        .invoke(new DurableGovernancePolicyRepositoryEntity.ListQuery("tenant-1", null)).getReply());
  }

  @Test
  void persistsSimulationEvidenceAndIdempotencyThroughAkkaState() {
    var testKit = newTestKit();
    var simulation = simulation("sim-1", "tenant-1", null, "proposal-1", "admin-1", "idem-sim-1");
    var otherProposal = simulation("sim-2", "tenant-1", null, "proposal-2", "admin-1", "idem-sim-2");

    assertTrue(testKit.method(DurableGovernancePolicyRepositoryEntity::saveSimulation).invoke(simulation).stateWasUpdated());
    testKit.method(DurableGovernancePolicyRepositoryEntity::saveSimulation).invoke(otherProposal);

    assertEquals(simulation, testKit.method(DurableGovernancePolicyRepositoryEntity::findSimulation)
        .invoke(new DurableGovernancePolicyRepositoryEntity.SimulationQuery("tenant-1", null, "sim-1")).getReply().orElseThrow());
    assertEquals(simulation, testKit.method(DurableGovernancePolicyRepositoryEntity::findSimulationByIdempotencyKey)
        .invoke(new DurableGovernancePolicyRepositoryEntity.IdempotencyQuery("tenant-1", null, "admin-1", "idem-sim-1")).getReply().orElseThrow());
    assertEquals(List.of(simulation), testKit.method(DurableGovernancePolicyRepositoryEntity::listSimulations)
        .invoke(new DurableGovernancePolicyRepositoryEntity.SimulationListQuery("tenant-1", null, "proposal-1")).getReply());
  }

  @Test
  void tenantAndCustomerScopedQueriesDoNotLeakRows() {
    var testKit = newTestKit();
    var tenantRow = proposal("proposal-tenant", "tenant-1", null, "admin-1", "idem-tenant", GovernancePolicyProposal.Status.DRAFT);
    var customerRow = proposal("proposal-customer", "tenant-1", "customer-1", "admin-1", "idem-customer", GovernancePolicyProposal.Status.DRAFT);
    var otherTenant = proposal("proposal-other", "tenant-2", null, "admin-2", "idem-other", GovernancePolicyProposal.Status.DRAFT);
    testKit.method(DurableGovernancePolicyRepositoryEntity::saveProposal).invoke(tenantRow);
    testKit.method(DurableGovernancePolicyRepositoryEntity::saveProposal).invoke(customerRow);
    testKit.method(DurableGovernancePolicyRepositoryEntity::saveProposal).invoke(otherTenant);

    assertEquals(2, testKit.method(DurableGovernancePolicyRepositoryEntity::listProposals)
        .invoke(new DurableGovernancePolicyRepositoryEntity.ListQuery("tenant-1", null)).getReply().size());
    assertEquals(List.of(customerRow), testKit.method(DurableGovernancePolicyRepositoryEntity::listProposals)
        .invoke(new DurableGovernancePolicyRepositoryEntity.ListQuery("tenant-1", "customer-1")).getReply());
    assertTrue(testKit.method(DurableGovernancePolicyRepositoryEntity::findProposal)
        .invoke(new DurableGovernancePolicyRepositoryEntity.ProposalQuery("tenant-1", null, "proposal-other")).getReply().isEmpty());
  }

  private static GovernancePolicySimulationResult simulation(String simulationId, String tenantId, String customerId, String proposalId, String accountId, String idempotencyKey) {
    return new GovernancePolicySimulationResult(
        simulationId,
        tenantId,
        customerId,
        proposalId,
        accountId,
        GovernancePolicySimulationResult.Status.COMPLETED_REVIEW_REQUIRED,
        "scenario without secrets",
        List.of("authorized read"),
        List.of("activation denied before approval"),
        List.of("human review required"),
        List.of("medium risk"),
        List.of("proposal:" + proposalId),
        List.of(GovernancePolicyService.APPROVE_CAPABILITY, GovernancePolicyService.ACTIVATE_CAPABILITY),
        idempotencyKey,
        "corr-sim",
        Instant.parse("2026-05-30T00:04:00Z"));
  }

  private static GovernancePolicyProposal proposal(String proposalId, String tenantId, String customerId, String accountId, String idempotencyKey, GovernancePolicyProposal.Status status) {
    return new GovernancePolicyProposal(
        proposalId,
        tenantId,
        customerId,
        accountId,
        status,
        "policy-human-approval",
        "Policy proposal",
        "rationale",
        "proposed content without secrets",
        "medium",
        List.of(GovernancePolicyService.APPROVE_CAPABILITY, GovernancePolicyService.ACTIVATE_CAPABILITY),
        List.of("governance-policy-workstream"),
        GovernancePolicyService.APPROVE_CAPABILITY,
        "rollback metadata required",
        idempotencyKey,
        "corr-create",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        Instant.parse("2026-05-30T00:00:00Z"),
        Instant.parse("2026-05-30T00:00:00Z"));
  }
}
