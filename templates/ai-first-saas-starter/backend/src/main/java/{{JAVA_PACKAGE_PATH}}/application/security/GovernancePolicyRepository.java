package {{JAVA_BASE_PACKAGE}}.application.security;

import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicyProposal;
import java.util.List;
import java.util.Optional;

/** Tenant-scoped deterministic storage boundary for Governance/Policy proposal lifecycle records. */
public interface GovernancePolicyRepository {
  Optional<GovernancePolicyProposal> findProposal(String tenantId, String customerId, String proposalId);

  Optional<GovernancePolicyProposal> findByIdempotencyKey(String tenantId, String customerId, String accountId, String idempotencyKey);

  GovernancePolicyProposal saveProposal(GovernancePolicyProposal proposal);

  List<GovernancePolicyProposal> listProposals(String tenantId, String customerId);
}
