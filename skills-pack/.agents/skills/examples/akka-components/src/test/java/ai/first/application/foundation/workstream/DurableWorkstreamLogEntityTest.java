package ai.first.application.foundation.workstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.KeyValueEntityTestKit;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import ai.first.application.coreapp.workstream.WorkstreamService;

class DurableWorkstreamLogEntityTest {

  private KeyValueEntityTestKit<WorkstreamLogState, DurableWorkstreamLogEntity> newTestKit() {
    return KeyValueEntityTestKit.of(
        DurableWorkstreamLogEntity.ENTITY_ID,
        __ -> new DurableWorkstreamLogEntity());
  }

  @Test
  void appendsReadsAndLooksUpSurfaceByTenantContextAndFunctionalAgent() {
    var testKit = newTestKit();
    var entry = entry("tenant-1", "membership-1", "agent-user-admin", "idem-1", "corr-1");

    var append = testKit.method(DurableWorkstreamLogEntity::appendMessage).invoke(entry);

    assertTrue(append.stateWasUpdated());
    assertEquals(entry, append.getReply());
    var items = testKit.method(DurableWorkstreamLogEntity::items)
        .invoke(new DurableWorkstreamLogEntity.ItemsQuery("tenant-1", "membership-1", "agent-user-admin"))
        .getReply();
    assertEquals(List.of(entry.userItem(), entry.agentItem()), items);
    var surface = testKit.method(DurableWorkstreamLogEntity::surface)
        .invoke(new DurableWorkstreamLogEntity.SurfaceQuery("tenant-1", "membership-1", entry.surface().surfaceId()))
        .getReply();
    assertEquals(entry.surface(), surface.orElseThrow());
  }

  @Test
  void duplicateIdempotencyReturnsExistingEntryWithoutUpdatingState() {
    var testKit = newTestKit();
    var first = entry("tenant-1", "membership-1", "agent-user-admin", "idem-dup", "corr-first");
    var duplicate = entry("tenant-1", "membership-1", "agent-user-admin", "idem-dup", "corr-second");
    testKit.method(DurableWorkstreamLogEntity::appendMessage).invoke(first);

    var appendDuplicate = testKit.method(DurableWorkstreamLogEntity::appendMessage).invoke(duplicate);

    assertFalse(appendDuplicate.stateWasUpdated());
    assertEquals(first, appendDuplicate.getReply());
    var byKey = testKit.method(DurableWorkstreamLogEntity::findByIdempotencyKey)
        .invoke(new DurableWorkstreamLogEntity.IdempotencyQuery("tenant-1", "membership-1", "agent-user-admin", "idem-dup"))
        .getReply();
    assertEquals(first, byKey.orElseThrow());
  }

  @Test
  void tenantAndContextIsolationHideItemsAndSurfaces() {
    var testKit = newTestKit();
    var entry = entry("tenant-1", "membership-1", "agent-user-admin", "idem-isolated", "corr-isolated");
    testKit.method(DurableWorkstreamLogEntity::appendMessage).invoke(entry);

    assertTrue(testKit.method(DurableWorkstreamLogEntity::items)
        .invoke(new DurableWorkstreamLogEntity.ItemsQuery("tenant-2", "membership-1", "agent-user-admin"))
        .getReply().isEmpty());
    assertTrue(testKit.method(DurableWorkstreamLogEntity::items)
        .invoke(new DurableWorkstreamLogEntity.ItemsQuery("tenant-1", "membership-2", "agent-user-admin"))
        .getReply().isEmpty());
    assertTrue(testKit.method(DurableWorkstreamLogEntity::surface)
        .invoke(new DurableWorkstreamLogEntity.SurfaceQuery("tenant-2", "membership-1", entry.surface().surfaceId()))
        .getReply().isEmpty());
  }

  @Test
  void appendsDeniedSystemEntry() {
    var testKit = newTestKit();
    var denied = new WorkstreamService.WorkstreamItem("item-denied", "agent-user-admin", "system_message", Instant.parse("2026-05-24T10:15:30Z").toString(), "corr-denied", List.of("trace-denied"), null, "Message not submitted", "Backend authorization denied this workstream message: FUNCTIONAL_AGENT_FORBIDDEN.", "blocked");

    var append = testKit.method(DurableWorkstreamLogEntity::appendSystemEntry)
        .invoke(new DurableWorkstreamLogEntity.SystemEntryCommand("tenant-1", "membership-1", denied, null));

    assertTrue(append.stateWasUpdated());
    assertEquals(denied, append.getReply());
    var items = testKit.method(DurableWorkstreamLogEntity::items)
        .invoke(new DurableWorkstreamLogEntity.ItemsQuery("tenant-1", "membership-1", "agent-user-admin"))
        .getReply();
    assertEquals(List.of(denied), items);
  }

  private static WorkstreamLogRepository.WorkstreamMessageLogEntry entry(String tenantId, String selectedContextId, String agentId, String idempotencyKey, String correlationId) {
    var user = new WorkstreamService.WorkstreamItem("item-user-" + correlationId, agentId, "user-request", Instant.parse("2026-05-24T10:15:30Z").toString(), correlationId, List.of("trace-user"), null, null, "Prompt", "ready");
    var agent = new WorkstreamService.WorkstreamItem("item-agent-" + correlationId, agentId, "markdown_response", Instant.parse("2026-05-24T10:15:31Z").toString(), correlationId, List.of("trace-agent"), "surface-" + correlationId, "Agent response", "Model-backed response produced.", "ready");
    var surface = new WorkstreamService.SurfaceEnvelope("surface-" + correlationId, "markdown_response", "v1", "Response", agentId, List.of("agent-audit-trace"), Map.of("tenantId", tenantId, "selectedContextId", selectedContextId, "customerId", "customer-1"), correlationId, List.of("trace-agent"), Instant.parse("2026-05-24T10:15:31Z").toString(), null, Map.of("profile", "tenant-admin"), Map.of("markdown", "## Provider-backed response", "workstreamEntryId", agent.itemId()), List.of(), List.of());
    return new WorkstreamLogRepository.WorkstreamMessageLogEntry(tenantId, selectedContextId, agentId, idempotencyKey, correlationId, user, agent, surface);
  }
}
