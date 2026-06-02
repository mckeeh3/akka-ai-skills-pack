package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicyProposal;
import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicySimulationResult;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory repository used by the starter and tests until an Akka entity-backed policy store is introduced. */
public final class LocalDemoGovernancePolicyRepository implements GovernancePolicyRepository {
  private final Map<String, GovernancePolicyProposal> proposals = new ConcurrentHashMap<>();
  private final Map<String, GovernancePolicySimulationResult> simulations = new ConcurrentHashMap<>();
  private final Map<String, String> idempotencyIndex = new ConcurrentHashMap<>();
  private final Map<String, String> simulationIdempotencyIndex = new ConcurrentHashMap<>();

  @Override
  public Optional<GovernancePolicyProposal> findProposal(String tenantId, String customerId, String proposalId) {
    return Optional.ofNullable(proposals.get(scopeKey(tenantId, customerId, proposalId)));
  }

  @Override
  public Optional<GovernancePolicyProposal> findByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) return Optional.empty();
    var proposalId = idempotencyIndex.get(idempotencyKey(tenantId, customerId, accountId, idempotencyKey));
    return proposalId == null ? Optional.empty() : findProposal(tenantId, customerId, proposalId);
  }

  @Override
  public GovernancePolicyProposal saveProposal(GovernancePolicyProposal proposal) {
    proposals.put(scopeKey(proposal.tenantId(), proposal.customerId(), proposal.proposalId()), proposal);
    if (proposal.idempotencyKey() != null && !proposal.idempotencyKey().isBlank()) {
      idempotencyIndex.put(idempotencyKey(proposal.tenantId(), proposal.customerId(), proposal.createdByAccountId(), proposal.idempotencyKey()), proposal.proposalId());
    }
    return proposal;
  }

  @Override
  public List<GovernancePolicyProposal> listProposals(String tenantId, String customerId) {
    return proposals.values().stream()
        .filter(proposal -> tenantId.equals(proposal.tenantId()))
        .filter(proposal -> customerId == null || customerId.equals(proposal.customerId()))
        .sorted(Comparator.comparing(GovernancePolicyProposal::updatedAt).reversed())
        .toList();
  }

  @Override
  public Optional<GovernancePolicySimulationResult> findSimulation(String tenantId, String customerId, String simulationId) {
    return Optional.ofNullable(simulations.get(scopeKey(tenantId, customerId, simulationId)));
  }

  @Override
  public Optional<GovernancePolicySimulationResult> findSimulationByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) return Optional.empty();
    var simulationId = simulationIdempotencyIndex.get(idempotencyKey(tenantId, customerId, accountId, idempotencyKey));
    return simulationId == null ? Optional.empty() : findSimulation(tenantId, customerId, simulationId);
  }

  @Override
  public GovernancePolicySimulationResult saveSimulation(GovernancePolicySimulationResult simulation) {
    simulations.put(scopeKey(simulation.tenantId(), simulation.customerId(), simulation.simulationId()), simulation);
    if (simulation.idempotencyKey() != null && !simulation.idempotencyKey().isBlank()) {
      simulationIdempotencyIndex.put(idempotencyKey(simulation.tenantId(), simulation.customerId(), simulation.requestedByAccountId(), simulation.idempotencyKey()), simulation.simulationId());
    }
    return simulation;
  }

  @Override
  public List<GovernancePolicySimulationResult> listSimulations(String tenantId, String customerId, String proposalId) {
    return simulations.values().stream()
        .filter(simulation -> tenantId.equals(simulation.tenantId()))
        .filter(simulation -> customerId == null || customerId.equals(simulation.customerId()))
        .filter(simulation -> proposalId == null || proposalId.equals(simulation.proposalId()))
        .sorted(Comparator.comparing(GovernancePolicySimulationResult::createdAt).reversed())
        .toList();
  }

  private static String scopeKey(String tenantId, String customerId, String proposalId) {
    return tenantId + ":" + (customerId == null ? "tenant" : customerId) + ":" + proposalId;
  }

  private static String idempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) {
    return tenantId + ":" + (customerId == null ? "tenant" : customerId) + ":" + accountId + ":" + idempotencyKey;
  }
}
