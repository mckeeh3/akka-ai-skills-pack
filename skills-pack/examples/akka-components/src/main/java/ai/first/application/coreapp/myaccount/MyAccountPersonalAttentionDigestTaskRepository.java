package ai.first.application.coreapp.myaccount;

import ai.first.domain.foundation.identity.Account;
import ai.first.domain.coreapp.myaccount.MyAccountPersonalAttentionDigestTask;
import java.util.Optional;

/** Repository for durable My Account personal attention digest task projections. */
public interface MyAccountPersonalAttentionDigestTaskRepository {
  Optional<MyAccountPersonalAttentionDigestTask> find(String digestTaskId);

  Optional<MyAccountPersonalAttentionDigestTask> findByIdempotencyKey(String tenantId, String accountId, String idempotencyKey);

  MyAccountPersonalAttentionDigestTask save(MyAccountPersonalAttentionDigestTask task);
}
