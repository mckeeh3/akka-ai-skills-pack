package ai.first.application.coreapp.myaccount;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.coreapp.myaccount.MyAccountPersonalAttentionDigestTask;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Unit-test/local adapter for My Account personal attention digest task projections. */
public final class InMemoryTestMyAccountPersonalAttentionDigestTaskRepository implements MyAccountPersonalAttentionDigestTaskRepository {
  private final Map<String, MyAccountPersonalAttentionDigestTask> tasks = new LinkedHashMap<>();

  @Override
  public synchronized Optional<MyAccountPersonalAttentionDigestTask> find(String digestTaskId) {
    return Optional.ofNullable(tasks.get(digestTaskId));
  }

  @Override
  public synchronized Optional<MyAccountPersonalAttentionDigestTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey) {
    return tasks.values().stream()
        .filter(task -> task.tenantId().equals(tenantId))
        .filter(task -> task.startedByAccountId().equals(accountId))
        .filter(task -> task.idempotencyKey().equals(idempotencyKey))
        .findFirst();
  }

  @Override
  public synchronized MyAccountPersonalAttentionDigestTask save(MyAccountPersonalAttentionDigestTask task) {
    tasks.put(task.digestTaskId(), task);
    return task;
  }
}
