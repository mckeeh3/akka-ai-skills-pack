package ai.first.application.security;

import ai.first.domain.security.AccessReviewTask;
import java.util.List;

/** Model-backed worker seam for durable User Admin access-review tasks; no access mutations are exposed here. */
public interface AccessReviewWorker {
  WorkerResult execute(AuthContextResolver.ResolvedMe actor, AccessReviewTask task, String correlationId);

  record WorkerResult(
      AccessReviewTask.Status status,
      int progressPercent,
      String summary,
      String blockerCode,
      List<String> evidenceRefs,
      List<String> recommendationRefs,
      List<String> traceIds) {
    public WorkerResult {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
      recommendationRefs = List.copyOf(recommendationRefs == null ? List.of() : recommendationRefs);
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }

    public static WorkerResult blocked(String summary, String blockerCode, List<String> traceIds) {
      return new WorkerResult(AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, 0, summary, blockerCode, List.of("userAdminEvidence.read", "readSkill:user-admin-access-review", "readReferenceDoc:user-admin-access-review"), List.of(), traceIds);
    }
  }
}
