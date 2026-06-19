package ai.first.application.foundation.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.testkit.EventingTestKit.IncomingMessages;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import ai.first.domain.foundation.agent.AgentLifecycleStatus;
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
    var definition = AgentDefinitionEntityTest.definition("tenant-catalog", "user-admin-agent", AgentLifecycleStatus.ACTIVE);

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
                  .invoke(new AgentDefinitionView.AgentDefinitionDetailQuery("tenant-catalog", "user-admin-agent"));
              var runtime = componentClient
                  .forView()
                  .method(AgentDefinitionView::activeRuntimeLookup)
                  .invoke(AgentDefinitionView.AgentRuntimeLookupQuery.active("tenant-catalog", "user-admin-agent"));
              var catalog = componentClient
                  .forView()
                  .method(AgentDefinitionView::agentCatalog)
                  .invoke(new AgentDefinitionView.AgentCatalogQuery("tenant-catalog"));
              var placement = componentClient
                  .forView()
                  .method(AgentDefinitionView::byWorkstreamPlacement)
                  .invoke(new AgentDefinitionView.WorkstreamPlacementQuery("tenant-catalog", "user-admin"));

              assertEquals("tenant-catalog", detail.tenantId());
              assertEquals("user-admin-agent", detail.agentDefinitionId());
              assertEquals("ACTIVE", detail.lifecycleStatus());
              assertTrue(detail.functionalAgent());
              assertEquals("user-admin", runtime.functionalAreaId());
              assertEquals(1, catalog.agents().size());
              assertEquals(1, placement.agents().size());
              assertEquals("user-admin-agent", placement.agents().getFirst().agentDefinitionId());
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
    var tenantOne = AgentDefinitionEntityTest.definition("tenant-1", "user-admin-agent", AgentLifecycleStatus.ACTIVE);
    var tenantTwo = AgentDefinitionEntityTest.definition("tenant-2", "user-admin-agent", AgentLifecycleStatus.ACTIVE);

    events.publish(new AgentDefinitionEntity.Event.DefinitionSaved(tenantOne), AgentDefinitionEntity.entityId("tenant-1", "user-admin-agent"));
    events.publish(new AgentDefinitionEntity.Event.DefinitionSaved(tenantTwo), AgentDefinitionEntity.entityId("tenant-2", "user-admin-agent"));

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
