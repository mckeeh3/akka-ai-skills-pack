package ai.first.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKitSupport;
import ai.first.domain.agentfoundation.PromptRiskReviewTask;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import ai.first.application.foundation.agent.AgentBehaviorSeedLoader;

class DurablePromptRiskReviewTaskRepositoryEntityTest extends TestKitSupport {
  @Test
  void storesPromptRiskTaskAndIdempotencyIndexDurablyThroughComponentClient() {
    var repository = new AkkaPromptRiskReviewTaskRepository(componentClient);
    var task = new PromptRiskReviewTask(
        "prompt-risk-1",
        "akka-task-1",
        "tenant-1",
        null,
        AgentBehaviorSeedLoader.AGENT_ADMIN_AGENT_ID,
        "proposal-1",
        "admin-1",
        "membership-admin",
        "idem-1",
        PromptRiskReviewTask.Status.QUEUED,
        5,
        "Akka AutonomousAgent prompt-risk task queued; no fake success.",
        null,
        null,
        null,
        List.of(new PromptRiskReviewTask.BehaviorArtifactDelta(PromptRiskReviewTask.ArtifactKind.PROMPT_DOCUMENT, "agent-admin-system", 1, 2, "safe summary", "diff:1", "before", "after")),
        List.of("agentAdminEvidence.read"),
        List.of(),
        List.of("autonomous_task:akka-task-1"),
        Instant.parse("2026-05-25T10:15:30Z"),
        Instant.parse("2026-05-25T10:15:30Z"));

    repository.save(task);

    assertEquals(task.taskId(), repository.find("prompt-risk-1").orElseThrow().taskId());
    assertEquals(task.taskId(), repository.findByIdempotencyKey("tenant-1", "admin-1", "idem-1").orElseThrow().taskId());
    assertTrue(repository.findByIdempotencyKey("tenant-1", "admin-1", "missing").isEmpty());
  }
}
