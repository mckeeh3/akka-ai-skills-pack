package ai.first.application.coreapp.myaccount;

import ai.first.domain.foundation.identity.Account;
import ai.first.application.foundation.identity.AuthContextResolver;
import ai.first.domain.coreapp.myaccount.MyAccountPersonalAttentionDigestTask;
import java.util.List;

/** Governed runtime adapter for starting/querying My Account Personal Attention Digest Akka AutonomousAgent tasks. */
public interface MyAccountPersonalAttentionDigestAutonomousAgentRuntime {
  StartOutcome start(AuthContextResolver.ResolvedMe actor, MyAccountPersonalAttentionDigestTask starterTask, String correlationId);

  Projection project(MyAccountPersonalAttentionDigestTask starterTask, String correlationId);

  default void cancel(MyAccountPersonalAttentionDigestTask starterTask, String reason, String correlationId) {}

  record StartOutcome(String autonomousAgentTaskId, MyAccountPersonalAttentionDigestTask.Status status, int progressPercent, String summary, String blockerCode, List<String> traceIds) {
    public StartOutcome {
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }

    public static StartOutcome queued(String autonomousAgentTaskId, String summary, List<String> traceIds) {
      return new StartOutcome(autonomousAgentTaskId, MyAccountPersonalAttentionDigestTask.Status.QUEUED, 5, summary, null, traceIds);
    }

    public static StartOutcome blocked(String summary, String blockerCode, List<String> traceIds) {
      return new StartOutcome(null, MyAccountPersonalAttentionDigestTask.Status.BLOCKED_PROVIDER_OR_RUNTIME, 0, summary, blockerCode, traceIds);
    }
  }

  record Projection(MyAccountPersonalAttentionDigestTask.Status status, int progressPercent, String summary, String blockerCode, MyAccountPersonalAttentionDigestResult result, int authorizedAttentionCount, List<String> evidenceRefs, List<String> sectionRefs, List<String> traceIds) {
    public Projection {
      evidenceRefs = List.copyOf(evidenceRefs == null ? List.of() : evidenceRefs);
      sectionRefs = List.copyOf(sectionRefs == null ? List.of() : sectionRefs);
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }

    public static Projection unchanged() {
      return new Projection(null, 0, null, null, null, 0, List.of(), List.of(), List.of());
    }

    public boolean changed() {
      return status != null;
    }
  }
}
