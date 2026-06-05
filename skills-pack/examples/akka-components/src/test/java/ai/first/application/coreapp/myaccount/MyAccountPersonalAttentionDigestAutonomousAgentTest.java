package ai.first.application.coreapp.myaccount;

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

class MyAccountPersonalAttentionDigestAutonomousAgentTest extends TestKitSupport {
  private final TestModelProvider model = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(MyAccountPersonalAttentionDigestAutonomousAgent.class, model);
  }

  @Test
  void completesTypedPersonalAttentionDigestTaskThroughAutonomousAgentTestModelProvider() {
    model.fixedResponse(new TestModelProvider.AiResponse(completeTask(result())));

    var taskId = componentClient
        .forAutonomousAgent(MyAccountPersonalAttentionDigestAutonomousAgent.class, "my-account-digest-test-agent")
        .runSingleTask(MyAccountPersonalAttentionDigestTasks.personalAttentionDigestInstructions(request("digest-1")));

    Awaitility.await().ignoreExceptions().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      var snapshot = componentClient.forTask(taskId).get(MyAccountPersonalAttentionDigestTasks.PERSONAL_ATTENTION_DIGEST);
      var result = snapshot.result().orElseThrow();
      assertEquals("digest-1", result.digestTaskId());
      assertEquals("tenant-1", result.tenantId());
      assertTrue(result.summary().contains("AutonomousAgent"));
      assertTrue(result.safety().contains("advisory"));
      assertFalse(result.toString().toLowerCase().contains("api_key"));
      assertFalse(result.toString().toLowerCase().contains("secret="));
    });
  }

  @Test
  void failTaskMapsToFailedSnapshotWithoutModelLessSuccess() {
    model.fixedResponse(new TestModelProvider.AiResponse(failTask("provider/model/myAccountEvidence.read unavailable; fail closed before personal attention digest findings")));

    var taskId = componentClient
        .forAutonomousAgent(MyAccountPersonalAttentionDigestAutonomousAgent.class, "my-account-digest-fail-test-agent")
        .runSingleTask(MyAccountPersonalAttentionDigestTasks.PERSONAL_ATTENTION_DIGEST.instructions("Run personal attention digest; fail closed if provider/model/evidence is unavailable."));

    Awaitility.await().ignoreExceptions().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      var snapshot = componentClient.forTask(taskId).get(MyAccountPersonalAttentionDigestTasks.PERSONAL_ATTENTION_DIGEST);
      assertEquals(akka.javasdk.agent.task.TaskStatus.FAILED, snapshot.status());
      assertTrue(snapshot.failureReason().orElse("").contains("fail closed"));
      assertTrue(snapshot.result().isEmpty(), "failed AutonomousAgent tasks must not expose fake or model-less personal attention digest success results");
    });
  }

  private static MyAccountPersonalAttentionDigestTasks.PersonalAttentionDigestRequest request(String digestTaskId) {
    return new MyAccountPersonalAttentionDigestTasks.PersonalAttentionDigestRequest(
        digestTaskId,
        "tenant-1",
        null,
        "account-1",
        "membership-1",
        List.of(new MyAccountPersonalAttentionDigestTasks.PersonalAttentionEvidenceItem(
            "attention_item:attention-1",
            "attention-1",
            "agent-audit-trace",
            "Audit/Trace needs review",
            "Provider readiness evidence is available for authorized review.",
            "open",
            "warning",
            "provider_readiness",
            "audit.trace.read",
            "surface-audit-trace-dashboard",
            "full",
            List.of("trace-attention-1"))),
        List.of("attention_item:attention-1", "readSkill:my-account-personal-attention-digest"),
        List.of("my_account.personal_attention_digest.read", "audit.trace.read"),
        "idem-digest-test",
        "corr-my-account-digest-test",
        "test governed runtime context; advisory only, redacted, no source attention mutation");
  }

  private static MyAccountPersonalAttentionDigestResult result() {
    var section = new MyAccountPersonalAttentionDigestResult.PersonalAttentionDigestSection(
        "section-audit-trace",
        "agent-audit-trace",
        "Audit/Trace review",
        "Provider readiness attention is available for authorized review.",
        "warning",
        1,
        List.of("attention_item:attention-1"));
    var recommendation = new MyAccountPersonalAttentionDigestResult.PersonalAttentionDigestRecommendation(
        "recommendation-open-audit-trace",
        "Review Audit/Trace evidence",
        "This visible item is warning-level and cites authorized evidence only.",
        "agent-audit-trace",
        "surface-audit-trace-dashboard",
        "audit.trace.read",
        List.of("attention_item:attention-1"));
    return new MyAccountPersonalAttentionDigestResult(
        "digest-1",
        "tenant-1",
        null,
        "account-1",
        "membership-1",
        "Model-backed AutonomousAgent personal attention digest completed; one authorized item needs review.",
        1,
        MyAccountPersonalAttentionDigestResult.DigestUrgency.WARNING,
        List.of(section),
        List.of(recommendation),
        List.of("attention_item:attention-1"),
        List.of("trace-attention-1", "trace-model-backed-my-account-digest"),
        "This digest is advisory only; source attention remains authoritative and lifecycle changes require separate governed capabilities.");
  }
}
