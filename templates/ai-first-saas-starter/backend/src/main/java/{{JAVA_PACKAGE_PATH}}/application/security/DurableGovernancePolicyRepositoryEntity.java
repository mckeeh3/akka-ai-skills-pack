package {{JAVA_BASE_PACKAGE}}.application.security;

import akka.javasdk.annotations.Component;
import akka.javasdk.keyvalueentity.KeyValueEntity;
import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicyProposal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Akka durable Governance/Policy proposal repository for starter proposal, decision, activation, and rollback state. */
@Component(id = "starter-governance-policy-repository")
public class DurableGovernancePolicyRepositoryEntity extends KeyValueEntity<DurableGovernancePolicyRepositoryEntity.State> {
  public static final String ENTITY_ID = "starter-governance-policy-repository";

  @Override
  public State emptyState() {
    return State.empty();
  }

  public ReadOnlyEffect<Optional<GovernancePolicyProposal>> findProposal(ProposalQuery query) {
    return effects().reply(Optional.ofNullable(currentState().proposals().get(scopeKey(query.tenantId(), query.customerId(), query.proposalId()))));
  }

  public ReadOnlyEffect<Optional<GovernancePolicyProposal>> findByIdempotencyKey(IdempotencyQuery query) {
    if (query.idempotencyKey() == null || query.idempotencyKey().isBlank()) return effects().reply(Optional.empty());
    var proposalId = currentState().idempotencyIndex().get(idempotencyKey(query.tenantId(), query.customerId(), query.accountId(), query.idempotencyKey()));
    var proposal = proposalId == null ? null : currentState().proposals().get(scopeKey(query.tenantId(), query.customerId(), proposalId));
    return effects().reply(Optional.ofNullable(proposal));
  }

  public Effect<GovernancePolicyProposal> saveProposal(GovernancePolicyProposal proposal) {
    return effects().updateState(currentState().saveProposal(proposal)).thenReply(() -> proposal);
  }

  public ReadOnlyEffect<List<GovernancePolicyProposal>> listProposals(ListQuery query) {
    return effects().reply(currentState().proposals().values().stream()
        .filter(proposal -> query.tenantId().equals(proposal.tenantId()))
        .filter(proposal -> query.customerId() == null || query.customerId().equals(proposal.customerId()))
        .sorted(Comparator.comparing(GovernancePolicyProposal::updatedAt).reversed())
        .toList());
  }

  private static String scopeKey(String tenantId, String customerId, String proposalId) {
    return tenantId + ":" + (customerId == null ? "tenant" : customerId) + ":" + proposalId;
  }

  private static String idempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) {
    return tenantId + ":" + (customerId == null ? "tenant" : customerId) + ":" + accountId + ":" + idempotencyKey;
  }

  public record ProposalQuery(String tenantId, String customerId, String proposalId) {}
  public record IdempotencyQuery(String tenantId, String customerId, String accountId, String idempotencyKey) {}
  public record ListQuery(String tenantId, String customerId) {}

  public record State(Map<String, GovernancePolicyProposal> proposals, Map<String, String> idempotencyIndex) {
    static State empty() {
      return new State(Map.of(), Map.of());
    }

    State saveProposal(GovernancePolicyProposal proposal) {
      var nextProposals = new HashMap<>(proposals);
      var nextIdempotency = new HashMap<>(idempotencyIndex);
      nextProposals.put(scopeKey(proposal.tenantId(), proposal.customerId(), proposal.proposalId()), proposal);
      if (proposal.idempotencyKey() != null && !proposal.idempotencyKey().isBlank()) {
        nextIdempotency.put(idempotencyKey(proposal.tenantId(), proposal.customerId(), proposal.createdByAccountId(), proposal.idempotencyKey()), proposal.proposalId());
      }
      return new State(Map.copyOf(nextProposals), Map.copyOf(nextIdempotency));
    }
  }
}
