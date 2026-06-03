package ai.first.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventingTestKit.IncomingMessages;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import ai.first.domain.agentfoundation.AgentLifecycleStatus;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class AgentDefinitionViewIntegrationTest extends TestKitSupport {

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT.withEventSourcedEntityIncomingMessages(AgentDefinitionEntity.class);
  }

  @Test
  void projectsAgentAdminCatalogDetailAndRuntimeLookupRows() {
    IncomingMessages events = testKit.getEventSourcedEntityIncomingMessages(AgentDefinitionEntity.class);
    var definition = AgentDefinitionEntityTest.definition("tenant-catalog", "agent-user-admin", AgentLifecycleStatus.ACTIVE);

    events.publish(
        new AgentDefinitionEntity.Event.DefinitionSaved(definition),
        AgentDefinitionEntity.entityId(definition.tenantId(), definition.agentDefinitionId()));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var detail = componentClient
                  .forView()
                  .method(AgentDefinitionView::getDetail)
                  .invoke(new AgentDefinitionView.AgentDefinitionDetailQuery("tenant-catalog", "agent-user-admin"));
              var runtime = componentClient
                  .forView()
                  .method(AgentDefinitionView::activeRuntimeLookup)
                  .invoke(AgentDefinitionView.AgentRuntimeLookupQuery.active("tenant-catalog", "agent-user-admin"));
              var catalog = componentClient
                  .forView()
                  .method(AgentDefinitionView::agentCatalog)
                  .invoke(new AgentDefinitionView.AgentCatalogQuery("tenant-catalog"));
              var placement = componentClient
                  .forView()
                  .method(AgentDefinitionView::byWorkstreamPlacement)
                  .invoke(new AgentDefinitionView.WorkstreamPlacementQuery("tenant-catalog", "user-admin"));

              assertEquals("tenant-catalog", detail.tenantId());
              assertEquals("agent-user-admin", detail.agentDefinitionId());
              assertEquals("ACTIVE", detail.lifecycleStatus());
              assertTrue(detail.functionalAgent());
              assertEquals("user-admin", runtime.functionalAreaId());
              assertEquals(1, catalog.agents().size());
              assertEquals(1, placement.agents().size());
              assertEquals("agent-user-admin", placement.agents().getFirst().agentDefinitionId());
            });
  }

  @Test
  void lifecycleFilterSeparatesDisabledAgentsFromActiveRuntimeLookup() {
    IncomingMessages events = testKit.getEventSourcedEntityIncomingMessages(AgentDefinitionEntity.class);
    var definition = AgentDefinitionEntityTest.definition("tenant-disabled", "agent-disabled", AgentLifecycleStatus.DISABLED);

    events.publish(
        new AgentDefinitionEntity.Event.DefinitionSaved(definition),
        AgentDefinitionEntity.entityId(definition.tenantId(), definition.agentDefinitionId()));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var disabled = componentClient
                  .forView()
                  .method(AgentDefinitionView::byLifecycle)
                  .invoke(new AgentDefinitionView.AgentLifecycleQuery("tenant-disabled", "DISABLED"));
              var catalog = componentClient
                  .forView()
                  .method(AgentDefinitionView::agentCatalog)
                  .invoke(new AgentDefinitionView.AgentCatalogQuery("tenant-disabled"));

              assertEquals(1, disabled.agents().size());
              assertEquals("DISABLED", disabled.agents().getFirst().lifecycleStatus());
              assertTrue(catalog.agents().stream().anyMatch(row -> row.agentDefinitionId().equals("agent-disabled")));
            });
  }

  @Test
  void tenantScopedQueriesDoNotReturnOtherTenantRows() {
    IncomingMessages events = testKit.getEventSourcedEntityIncomingMessages(AgentDefinitionEntity.class);
    var tenantOne = AgentDefinitionEntityTest.definition("tenant-1", "agent-user-admin", AgentLifecycleStatus.ACTIVE);
    var tenantTwo = AgentDefinitionEntityTest.definition("tenant-2", "agent-user-admin", AgentLifecycleStatus.ACTIVE);

    events.publish(new AgentDefinitionEntity.Event.DefinitionSaved(tenantOne), AgentDefinitionEntity.entityId("tenant-1", "agent-user-admin"));
    events.publish(new AgentDefinitionEntity.Event.DefinitionSaved(tenantTwo), AgentDefinitionEntity.entityId("tenant-2", "agent-user-admin"));

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var tenantOneCatalog = componentClient
                  .forView()
                  .method(AgentDefinitionView::agentCatalog)
                  .invoke(new AgentDefinitionView.AgentCatalogQuery("tenant-1"));
              var tenantTwoCatalog = componentClient
                  .forView()
                  .method(AgentDefinitionView::agentCatalog)
                  .invoke(new AgentDefinitionView.AgentCatalogQuery("tenant-2"));

              assertEquals(1, tenantOneCatalog.agents().size());
              assertEquals("tenant-1", tenantOneCatalog.agents().getFirst().tenantId());
              assertEquals(1, tenantTwoCatalog.agents().size());
              assertEquals("tenant-2", tenantTwoCatalog.agents().getFirst().tenantId());
            });
  }
}
