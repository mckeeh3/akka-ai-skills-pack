package ai.first.application.security;

import ai.first.domain.security.MyAccountPersonalAttentionDigestTask;
import java.util.Optional;

/** Repository for durable My Account personal attention digest task projections. */
public interface MyAccountPersonalAttentionDigestTaskRepository {
  Optional<MyAccountPersonalAttentionDigestTask> find(String digestTaskId);

  Optional<MyAccountPersonalAttentionDigestTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey);

  MyAccountPersonalAttentionDigestTask save(MyAccountPersonalAttentionDigestTask task);
}
