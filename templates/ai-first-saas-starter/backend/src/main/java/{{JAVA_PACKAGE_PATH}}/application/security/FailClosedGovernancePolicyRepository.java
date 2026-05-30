package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicyProposal;
import java.util.List;
import java.util.Optional;

/** Fail-closed governance policy port until an Akka-backed policy repository is bound. */
public final class FailClosedGovernancePolicyRepository implements GovernancePolicyRepository {
  private IllegalStateException unavailable() {
    return FailClosedFoundationRuntime.unavailable("GovernancePolicyRepository");
  }

  @Override public Optional<GovernancePolicyProposal> findProposal(String tenantId, String customerId, String proposalId) { throw unavailable(); }
  @Override public Optional<GovernancePolicyProposal> findByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey) { throw unavailable(); }
  @Override public GovernancePolicyProposal saveProposal(GovernancePolicyProposal proposal) { throw unavailable(); }
  @Override public List<GovernancePolicyProposal> listProposals(String tenantId, String customerId) { throw unavailable(); }
}
