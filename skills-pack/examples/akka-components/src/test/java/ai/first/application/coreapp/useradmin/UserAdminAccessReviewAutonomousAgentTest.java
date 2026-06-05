package ai.first.application.coreapp.useradmin;

import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.completeTask;
import static akka.javasdk.testkit.TestModelProvider.AutonomousAgentTools.failTask;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class UserAdminAccessReviewAutonomousAgentTest extends TestKitSupport {
  private final TestModelProvider model = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(UserAdminAccessReviewAutonomousAgent.class, model);
  }

  @Test
  void completesTypedAccessReviewTaskThroughAutonomousAgentTestModelProvider() {
    model.fixedResponse(new TestModelProvider.AiResponse(completeTask(new AccessReviewAutonomousAgentResult(
        "access-review-1",
        "tenant-1",
        null,
        "Model-backed AutonomousAgent access-review advisory result completed; User Admin human review is required.",
        List.of(new AccessReviewAutonomousAgentResult.AccessReviewFinding("finding-1", "LOW", "No stale admin membership found.", List.of("userAdminEvidence.read"))),
        List.of(new AccessReviewAutonomousAgentResult.AccessReviewRecommendation("rec-1", "Review dormant admin evidence before taking action.", "User Admin accept or reject", List.of("userAdminEvidence.read"))),
        List.of("userAdminEvidence.read", "readSkill:ua.access-review-triage.v1"),
        List.of("trace-prompt", "trace-model"),
        "User Admin human review is required before any access change."))));

    var taskId = componentClient
        .forAutonomousAgent(UserAdminAccessReviewAutonomousAgent.class, "ua-access-review-test-agent")
        .runSingleTask(UserAdminAccessReviewTasks.accessReviewInstructions(new UserAdminAccessReviewTasks.AccessReviewAutonomousAgentRequest(
            "access-review-1", "tenant-1", null, "TENANT", "admin-1", "corr-aa-test", "user_admin.access_review.start", "test governed runtime context; no direct mutation", List.of("userAdminEvidence.read"))));

    Awaitility.await().ignoreExceptions().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      var snapshot = componentClient.forTask(taskId).get(UserAdminAccessReviewTasks.ACCESS_REVIEW);
      var result = snapshot.result().orElseThrow();
      assertEquals("access-review-1", result.taskId());
      assertEquals("tenant-1", result.tenantId());
      assertTrue(result.summary().contains("AutonomousAgent"));
      assertTrue(result.safety().contains("human review"));
      assertFalse(result.toString().toLowerCase().contains("api_key"));
    });
  }

  @Test
  void failTaskMapsToFailedSnapshotWithoutModelLessSuccess() {
    model.fixedResponse(new TestModelProvider.AiResponse(failTask("provider/model/tool-boundary unavailable; fail closed before recommendations")));

    var taskId = componentClient
        .forAutonomousAgent(UserAdminAccessReviewAutonomousAgent.class, "ua-access-review-fail-test-agent")
        .runSingleTask(UserAdminAccessReviewTasks.ACCESS_REVIEW.instructions("Run access review for access-review-fail; fail closed if provider/model/tool-boundary is unavailable."));

    Awaitility.await().ignoreExceptions().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      var snapshot = componentClient.forTask(taskId).get(UserAdminAccessReviewTasks.ACCESS_REVIEW);
      assertEquals(akka.javasdk.agent.task.TaskStatus.FAILED, snapshot.status());
      assertTrue(snapshot.failureReason().orElse("").contains("fail closed"));
      assertTrue(snapshot.result().isEmpty(), "failed AutonomousAgent tasks must not expose model-less success results");
    });
  }
}
