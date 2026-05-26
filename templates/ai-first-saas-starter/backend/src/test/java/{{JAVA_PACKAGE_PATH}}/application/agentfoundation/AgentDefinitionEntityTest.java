package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventSourcedTestKit;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentDefinition;
import {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentLifecycleStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class AgentDefinitionEntityTest {

  private EventSourcedTestKit<AgentDefinitionEntity.State, AgentDefinitionEntity.Event, AgentDefinitionEntity> newTestKit(
      String tenantId, String agentDefinitionId) {
    return EventSourcedTestKit.of(
        AgentDefinitionEntity.entityId(tenantId, agentDefinitionId),
        AgentDefinitionEntity::new);
  }

  @Test
  void savesActiveTenantScopedAgentDefinitionForRuntimeLookup() {
    var definition = definition("tenant-1", "agent-user-admin", AgentLifecycleStatus.ACTIVE);
    var testKit = newTestKit(definition.tenantId(), definition.agentDefinitionId());

    var saveResult = testKit.method(AgentDefinitionEntity::save).invoke(definition);
    var lookupResult = testKit.method(AgentDefinitionEntity::activeRuntimeLookup)
        .invoke(new AgentDefinitionEntity.AgentDefinitionQuery("tenant-1", "agent-user-admin"));

    assertEquals(definition, saveResult.getReply());
    assertEquals(
        new AgentDefinitionEntity.Event.DefinitionSaved(definition),
        saveResult.getNextEventOfType(AgentDefinitionEntity.Event.DefinitionSaved.class));
    assertEquals(definition, lookupResult.getReply().orElseThrow());
  }

  @Test
  void disabledAgentDefinitionRemainsInspectableButDeniedForRuntimeLookup() {
    var definition = definition("tenant-1", "agent-user-admin", AgentLifecycleStatus.ACTIVE);
    var testKit = newTestKit(definition.tenantId(), definition.agentDefinitionId());
    testKit.method(AgentDefinitionEntity::save).invoke(definition);

    var disabled = testKit.method(AgentDefinitionEntity::disable)
        .invoke(new AgentDefinitionEntity.LifecycleCommand("tenant-1", "maintenance", "admin-1"));
    var runtimeLookup = testKit.method(AgentDefinitionEntity::activeRuntimeLookup)
        .invoke(new AgentDefinitionEntity.AgentDefinitionQuery("tenant-1", "agent-user-admin"));
    var detail = testKit.method(AgentDefinitionEntity::detail)
        .invoke(new AgentDefinitionEntity.AgentDefinitionQuery("tenant-1", "agent-user-admin"));

    assertEquals(AgentLifecycleStatus.DISABLED, disabled.getReply().status());
    assertEquals(
        AgentLifecycleStatus.DISABLED,
        disabled.getNextEventOfType(AgentDefinitionEntity.Event.DefinitionDisabled.class).definition().status());
    assertTrue(runtimeLookup.getReply().isEmpty());
    assertEquals(AgentLifecycleStatus.DISABLED, detail.getReply().orElseThrow().status());
  }

  @Test
  void archivedAgentDefinitionRemainsInspectableButDeniedForRuntimeLookup() {
    var definition = definition("tenant-1", "agent-user-admin", AgentLifecycleStatus.DISABLED);
    var testKit = newTestKit(definition.tenantId(), definition.agentDefinitionId());
    testKit.method(AgentDefinitionEntity::save).invoke(definition);

    var archived = testKit.method(AgentDefinitionEntity::archive)
        .invoke(new AgentDefinitionEntity.LifecycleCommand("tenant-1", "retired", "admin-1"));
    var runtimeLookup = testKit.method(AgentDefinitionEntity::activeRuntimeLookup)
        .invoke(new AgentDefinitionEntity.AgentDefinitionQuery("tenant-1", "agent-user-admin"));

    assertEquals(AgentLifecycleStatus.ARCHIVED, archived.getReply().status());
    assertEquals(
        AgentLifecycleStatus.ARCHIVED,
        archived.getNextEventOfType(AgentDefinitionEntity.Event.DefinitionArchived.class).definition().status());
    assertTrue(runtimeLookup.getReply().isEmpty());
  }

  @Test
  void crossTenantLookupDoesNotRevealAgentDefinition() {
    var definition = definition("tenant-1", "agent-user-admin", AgentLifecycleStatus.ACTIVE);
    var testKit = newTestKit(definition.tenantId(), definition.agentDefinitionId());
    testKit.method(AgentDefinitionEntity::save).invoke(definition);

    var wrongTenant = testKit.method(AgentDefinitionEntity::detail)
        .invoke(new AgentDefinitionEntity.AgentDefinitionQuery("tenant-2", "agent-user-admin"));
    var wrongTenantRuntime = testKit.method(AgentDefinitionEntity::activeRuntimeLookup)
        .invoke(new AgentDefinitionEntity.AgentDefinitionQuery("tenant-2", "agent-user-admin"));

    assertTrue(wrongTenant.getReply().isEmpty());
    assertTrue(wrongTenantRuntime.getReply().isEmpty());
  }

  @Test
  void invalidAgentDefinitionIsRejectedWithoutPersistingEvents() {
    var testKit = newTestKit("tenant-1", "agent-user-admin");
    var invalid = definition("tenant-1", "agent|bad", AgentLifecycleStatus.ACTIVE);

    var result = testKit.method(AgentDefinitionEntity::save).invoke(invalid);

    assertTrue(result.isError());
    assertEquals("agent-definition-id-contains-reserved-character", result.getError());
    assertFalse(result.didPersistEvents());
  }

  static AgentDefinition definition(String tenantId, String agentDefinitionId, AgentLifecycleStatus status) {
    return new AgentDefinition(
        tenantId,
        agentDefinitionId,
        "User Admin Agent",
        "Agent catalog profile for User Admin workstream.",
        AgentDefinition.Placement.FUNCTIONAL_CONTEXT_AREA,
        "user-admin",
        AgentDefinition.AuthorityLevel.APPROVAL_REQUIRED,
        status,
        "prompt-user-admin",
        1,
        "manifest-user-admin-skills",
        1,
        "manifest-user-admin-references",
        1,
        "boundary-user-admin",
        1,
        "starter-default-model",
        "starter-default-policy",
        "workstream-runtime-agent",
        List.of("PROMPT_ASSEMBLY", "AGENT_WORK"),
        null,
        Instant.parse("2026-05-20T00:00:00Z"),
        Instant.parse("2026-05-20T00:00:00Z"));
  }
}
