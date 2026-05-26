package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventSourcedTestKit;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.PromptDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.ReferenceDocument;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.SkillDocument;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class GovernedDocumentEntityTest {
  private static final Instant NOW = Instant.parse("2026-05-20T00:00:00Z");

  @Test
  void promptDocumentActivationCreatesActiveVersionSnapshot() {
    var prompt = prompt("tenant-1", "prompt-user-admin", AgentLifecycleStatus.ACTIVE, 1, "Follow policy.");
    var testKit = EventSourcedTestKit.of(
        PromptDocumentEntity.entityId(prompt.tenantId(), prompt.promptDocumentId()),
        PromptDocumentEntity::new);

    var saved = testKit.method(PromptDocumentEntity::save).invoke(prompt);
    var active = testKit.method(PromptDocumentEntity::activeRuntimeLookup)
        .invoke(new PromptDocumentEntity.DocumentQuery("tenant-1", prompt.promptDocumentId()));
    var version = testKit.method(PromptDocumentEntity::version)
        .invoke(new PromptDocumentEntity.VersionQuery("tenant-1", prompt.promptDocumentId(), 1));

    assertEquals(prompt, saved.getReply());
    assertEquals(1, saved.getNextEventOfType(PromptDocumentEntity.Event.PromptDocumentSaved.class).activeVersion().version());
    assertEquals(prompt, active.getReply().orElseThrow());
    assertEquals(prompt.contentBody(), version.getReply().orElseThrow().contentBody());
    assertEquals(prompt.contentChecksum(), version.getReply().orElseThrow().contentChecksum());
  }

  @Test
  void skillDocumentPreservesVersionHistoryAcrossActivationUpdates() {
    var v1 = skill("tenant-1", "skill-access-review", AgentLifecycleStatus.ACTIVE, 1, "Use stale membership evidence.");
    var v2 = skill("tenant-1", "skill-access-review", AgentLifecycleStatus.ACTIVE, 2, "Use stale membership and role risk evidence.");
    var testKit = EventSourcedTestKit.of(
        SkillDocumentEntity.entityId(v1.tenantId(), v1.skillDocumentId()),
        SkillDocumentEntity::new);

    testKit.method(SkillDocumentEntity::save).invoke(v1);
    testKit.method(SkillDocumentEntity::save).invoke(v2);
    var oldVersion = testKit.method(SkillDocumentEntity::version)
        .invoke(new SkillDocumentEntity.VersionQuery("tenant-1", v1.skillDocumentId(), 1));
    var newVersion = testKit.method(SkillDocumentEntity::version)
        .invoke(new SkillDocumentEntity.VersionQuery("tenant-1", v1.skillDocumentId(), 2));

    assertEquals(1, oldVersion.getReply().orElseThrow().version());
    assertEquals(v1.contentBody(), oldVersion.getReply().orElseThrow().contentBody());
    assertEquals(2, newVersion.getReply().orElseThrow().version());
    assertEquals(v2.contentBody(), newVersion.getReply().orElseThrow().contentBody());
  }

  @Test
  void referenceDocumentDeniesCrossTenantLookupWithoutLeakingRecord() {
    var reference = reference("tenant-1", "ref-access-review", AgentLifecycleStatus.ACTIVE, 1, "Access review policy facts.");
    var testKit = EventSourcedTestKit.of(
        ReferenceDocumentEntity.entityId(reference.tenantId(), reference.referenceDocumentId()),
        ReferenceDocumentEntity::new);
    testKit.method(ReferenceDocumentEntity::save).invoke(reference);

    var wrongTenantDetail = testKit.method(ReferenceDocumentEntity::detail)
        .invoke(new ReferenceDocumentEntity.DocumentQuery("tenant-2", reference.referenceDocumentId()));
    var wrongTenantVersion = testKit.method(ReferenceDocumentEntity::version)
        .invoke(new ReferenceDocumentEntity.VersionQuery("tenant-2", reference.referenceDocumentId(), 1));

    assertTrue(wrongTenantDetail.getReply().isEmpty());
    assertTrue(wrongTenantVersion.getReply().isEmpty());
  }

  @Test
  void inactiveDocumentsRemainInspectableButUnavailableForRuntimeActiveLookup() {
    var skill = skill("tenant-1", "skill-access-review", AgentLifecycleStatus.DISABLED, 1, "Draft disabled guidance.");
    var testKit = EventSourcedTestKit.of(
        SkillDocumentEntity.entityId(skill.tenantId(), skill.skillDocumentId()),
        SkillDocumentEntity::new);
    testKit.method(SkillDocumentEntity::save).invoke(skill);

    var detail = testKit.method(SkillDocumentEntity::detail)
        .invoke(new SkillDocumentEntity.DocumentQuery("tenant-1", skill.skillDocumentId()));
    var active = testKit.method(SkillDocumentEntity::activeRuntimeLookup)
        .invoke(new SkillDocumentEntity.DocumentQuery("tenant-1", skill.skillDocumentId()));

    assertEquals(AgentLifecycleStatus.DISABLED, detail.getReply().orElseThrow().status());
    assertTrue(active.getReply().isEmpty());
  }

  @Test
  void seedImportIdempotencyDoesNotRewriteIdenticalDocumentSnapshot() {
    var prompt = prompt("tenant-1", "prompt-user-admin", AgentLifecycleStatus.ACTIVE, 1, "Follow policy.");
    var testKit = EventSourcedTestKit.of(
        PromptDocumentEntity.entityId(prompt.tenantId(), prompt.promptDocumentId()),
        PromptDocumentEntity::new);
    testKit.method(PromptDocumentEntity::save).invoke(prompt);

    var secondSave = testKit.method(PromptDocumentEntity::save).invoke(prompt);

    assertEquals(prompt, secondSave.getReply());
    assertFalse(secondSave.didPersistEvents());
  }

  @Test
  void secretLikeDocumentContentIsRejectedWithoutPersistingEvents() {
    var prompt = prompt("tenant-1", "prompt-bad", AgentLifecycleStatus.ACTIVE, 1, "api_key: sk-live-secret");
    var testKit = EventSourcedTestKit.of(
        PromptDocumentEntity.entityId(prompt.tenantId(), prompt.promptDocumentId()),
        PromptDocumentEntity::new);

    var result = testKit.method(PromptDocumentEntity::save).invoke(prompt);

    assertTrue(result.isError());
    assertEquals("prompt-content-contains-secret-like-value", result.getError());
    assertFalse(result.didPersistEvents());
  }

  private static PromptDocument prompt(String tenantId, String id, AgentLifecycleStatus status, int activeVersion, String content) {
    return new PromptDocument(
        tenantId,
        id,
        "agent-user-admin",
        "User Admin prompt",
        "system",
        status,
        activeVersion,
        content,
        "checksum-" + activeVersion,
        "change " + activeVersion,
        null,
        NOW,
        NOW);
  }

  private static SkillDocument skill(String tenantId, String id, AgentLifecycleStatus status, int activeVersion, String content) {
    return new SkillDocument(
        tenantId,
        id,
        "access-review",
        "Access Review",
        "Review tenant access risk.",
        "Use for access review requests.",
        List.of("foundation", "user-admin"),
        status,
        activeVersion,
        content,
        "checksum-" + activeVersion,
        null,
        NOW,
        NOW);
  }

  private static ReferenceDocument reference(String tenantId, String id, AgentLifecycleStatus status, int activeVersion, String content) {
    return new ReferenceDocument(
        tenantId,
        id,
        "access-review-policy",
        "Access Review Policy",
        "Tenant access review policy.",
        "Consult before recommending access changes.",
        ReferenceDocument.ReferenceType.POLICY,
        "internal",
        List.of("foundation", "user-admin"),
        status,
        activeVersion,
        content,
        "checksum-" + activeVersion,
        null,
        NOW,
        NOW);
  }
}
