package ai.first.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.KeyValueEntityTestKit;
import ai.first.domain.security.AttentionCategory;
import ai.first.domain.security.AttentionItem;
import ai.first.domain.security.AttentionItemStatus;
import ai.first.domain.security.AttentionSeverity;
import ai.first.domain.security.AttentionSourceRef;
import ai.first.domain.security.AttentionSurfaceRef;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class DurableAttentionRepositoryEntityTest {
  private KeyValueEntityTestKit<AttentionRepositoryState, DurableAttentionRepositoryEntity> newTestKit() {
    return KeyValueEntityTestKit.of(DurableAttentionRepositoryEntity.ENTITY_ID, __ -> new DurableAttentionRepositoryEntity());
  }

  @Test
  void upsertsFindsListsAndSavesTenantScopedAttentionItems() {
    var testKit = newTestKit();
    var item = item("attention-1", "tenant-1");

    var upsert = testKit.method(DurableAttentionRepositoryEntity::upsert).invoke(item);

    assertTrue(upsert.stateWasUpdated());
    assertEquals(item, upsert.getReply());
    assertEquals(item, testKit.method(DurableAttentionRepositoryEntity::find)
        .invoke(new DurableAttentionRepositoryEntity.FindQuery("tenant-1", "attention-1"))
        .getReply().orElseThrow());
    assertEquals(List.of(item), testKit.method(DurableAttentionRepositoryEntity::listTenant)
        .invoke(new DurableAttentionRepositoryEntity.ListTenantQuery("tenant-1"))
        .getReply());
    assertTrue(testKit.method(DurableAttentionRepositoryEntity::listTenant)
        .invoke(new DurableAttentionRepositoryEntity.ListTenantQuery("tenant-2"))
        .getReply().isEmpty());

    var resolved = item.resolve(Instant.parse("2026-05-25T10:00:00Z"), "corr-resolve");
    var save = testKit.method(DurableAttentionRepositoryEntity::save).invoke(resolved);
    assertTrue(save.stateWasUpdated());
    assertEquals(AttentionItemStatus.RESOLVED, testKit.method(DurableAttentionRepositoryEntity::find)
        .invoke(new DurableAttentionRepositoryEntity.FindQuery("tenant-1", "attention-1"))
        .getReply().orElseThrow().status());
  }

  private AttentionItem item(String itemId, String tenantId) {
    var now = Instant.parse("2026-05-25T09:00:00Z");
    return new AttentionItem(
        itemId,
        tenantId,
        null,
        "agent-agent-admin",
        "Provider readiness blocked",
        "Provider configuration needs attention.",
        AttentionCategory.PROVIDER_READINESS,
        AttentionSeverity.BLOCKED,
        AttentionItemStatus.OPEN,
        AttentionItem.AssigneeKind.CAPABILITY,
        "agent_admin.list_definitions",
        "agent_admin.list_definitions",
        new AttentionSurfaceRef("agent-agent-admin", "surface-agent-admin-catalog", "dashboard", itemId, "open_attention_item", "agent_admin.list_definitions"),
        List.of(new AttentionSourceRef("audit_trace", "trace-provider", "Provider trace", "agent_admin.list_definitions", "trace-provider", "corr-provider")),
        null,
        now,
        now,
        now,
        null,
        null,
        null,
        null,
        "corr-provider");
  }
}
