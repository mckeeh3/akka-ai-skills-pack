package ai.first.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.KeyValueEntityTestKit;
import ai.first.domain.security.AccessReviewTask;
import ai.first.domain.foundation.identity.ScopeType;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class DurableAccessReviewTaskRepositoryEntityTest {

  private KeyValueEntityTestKit<DurableAccessReviewTaskRepositoryEntity.State, DurableAccessReviewTaskRepositoryEntity> newTestKit() {
    return KeyValueEntityTestKit.of(
        DurableAccessReviewTaskRepositoryEntity.ENTITY_ID,
        __ -> new DurableAccessReviewTaskRepositoryEntity());
  }

  @Test
  void persistsAccessReviewTaskLifecycleAndIdempotencyThroughAkkaState() {
    var testKit = newTestKit();
    var task = task("access-review-1", "tenant-1", "admin-1", "idem-1", AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME);
    var completed = task.withWorkerUpdate(
        AccessReviewTask.Status.COMPLETED,
        100,
        "model-backed advisory result completed",
        null,
        List.of("userAdminEvidence.read"),
        List.of("recommendation:review dormant admin"),
        List.of("trace-worker-complete"),
        Instant.parse("2026-05-30T00:10:00Z"));
    var accepted = completed.withDecision(
        AccessReviewTask.Status.ACCEPTED,
        "accepted",
        "human accepted recommendation evidence; no direct mutation",
        List.of("trace-accepted"),
        Instant.parse("2026-05-30T00:11:00Z"));

    assertTrue(testKit.method(DurableAccessReviewTaskRepositoryEntity::save).invoke(task).stateWasUpdated());
    assertTrue(testKit.method(DurableAccessReviewTaskRepositoryEntity::save).invoke(completed).stateWasUpdated());
    assertTrue(testKit.method(DurableAccessReviewTaskRepositoryEntity::save).invoke(accepted).stateWasUpdated());

    assertEquals(accepted, testKit.method(DurableAccessReviewTaskRepositoryEntity::find).invoke("access-review-1").getReply().orElseThrow());
    assertEquals(accepted, testKit.method(DurableAccessReviewTaskRepositoryEntity::findByIdempotencyKey)
        .invoke(new DurableAccessReviewTaskRepositoryEntity.IdempotencyQuery("tenant-1", "admin-1", "idem-1")).getReply().orElseThrow());
  }

  @Test
  void idempotencyLookupIsTenantAndActorScoped() {
    var testKit = newTestKit();
    var task = task("access-review-1", "tenant-1", "admin-1", "idem-1", AccessReviewTask.Status.BLOCKED_PROVIDER_OR_RUNTIME);
    testKit.method(DurableAccessReviewTaskRepositoryEntity::save).invoke(task);

    assertTrue(testKit.method(DurableAccessReviewTaskRepositoryEntity::findByIdempotencyKey)
        .invoke(new DurableAccessReviewTaskRepositoryEntity.IdempotencyQuery("tenant-2", "admin-1", "idem-1")).getReply().isEmpty());
    assertTrue(testKit.method(DurableAccessReviewTaskRepositoryEntity::findByIdempotencyKey)
        .invoke(new DurableAccessReviewTaskRepositoryEntity.IdempotencyQuery("tenant-1", "other-admin", "idem-1")).getReply().isEmpty());
  }

  private static AccessReviewTask task(String taskId, String tenantId, String accountId, String idempotencyKey, AccessReviewTask.Status status) {
    return new AccessReviewTask(
        taskId,
        null,
        tenantId,
        null,
        ScopeType.TENANT,
        accountId,
        "membership-" + accountId,
        idempotencyKey,
        status,
        0,
        "Access-review task record created; provider/runtime blocked fail closed.",
        "blocked_provider_or_runtime",
        null,
        null,
        List.of("userAdminEvidence.read"),
        List.of(),
        List.of("trace-start"),
        Instant.parse("2026-05-30T00:00:00Z"),
        Instant.parse("2026-05-30T00:00:00Z"));
  }
}
