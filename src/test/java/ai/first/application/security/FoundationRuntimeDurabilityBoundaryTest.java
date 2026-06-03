package ai.first.application.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class FoundationRuntimeDurabilityBoundaryTest {
  @Test
  void failClosedFoundationPortsExplainRequiredDurableBinding() {
    var identityFailure = assertThrows(IllegalStateException.class, () -> new FailClosedIdentityRepository().findAccountByEmail("admin@example.test"));
    var workstreamFailure = assertThrows(IllegalStateException.class, () -> new FailClosedWorkstreamLogRepository().items("tenant-starter", "member-starter-admin", null));
    var accessReviewFailure = assertThrows(IllegalStateException.class, () -> new FailClosedAccessReviewTaskRepository().find("access-review-1"));
    var auditFailure = assertThrows(IllegalStateException.class, () -> new FailClosedAuditTraceRepository().eventsFor(null, "corr-1"));
    var governanceFailure = assertThrows(IllegalStateException.class, () -> new FailClosedGovernancePolicyRepository().listProposals("tenant-starter", null));

    for (var failure : new IllegalStateException[] {identityFailure, workstreamFailure, accessReviewFailure, auditFailure, governanceFailure}) {
      assertTrue(failure.getMessage().contains("Durable Akka foundation repository binding is required for normal runtime"));
      assertTrue(failure.getMessage().contains("Test doubles are allowed only from test source"));
    }
  }
}
