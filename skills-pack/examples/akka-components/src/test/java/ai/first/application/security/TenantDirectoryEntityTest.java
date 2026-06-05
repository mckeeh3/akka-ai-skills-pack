package ai.first.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.testkit.KeyValueEntityTestKit;
import ai.first.domain.security.TenantDirectory;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TenantDirectoryEntityTest {

  private static final Instant T1 = Instant.parse("2026-01-01T00:00:00Z");
  private static final Instant T2 = Instant.parse("2026-01-02T00:00:00Z");

  private KeyValueEntityTestKit<TenantDirectory.State, TenantDirectoryEntity> newTestKit(String tenantId) {
    return KeyValueEntityTestKit.of(tenantId, TenantDirectoryEntity::new);
  }

  @Test
  void upsertTenantCreatesCurrentState() {
    var testKit = newTestKit("tenant-1");

    var result =
        testKit
            .method(TenantDirectoryEntity::upsertTenant)
            .invoke(new TenantDirectory.Command.UpsertTenant("Dealer One", T1));

    assertEquals(Done.getInstance(), result.getReply());
    assertTrue(result.stateWasUpdated());
    assertEquals("Dealer One", testKit.getState().name());
    assertTrue(testKit.getState().active());
  }

  @Test
  void repeatedTenantUpsertIsIdempotent() {
    var testKit = newTestKit("tenant-1");
    var command = new TenantDirectory.Command.UpsertTenant("Dealer One", T1);

    testKit.method(TenantDirectoryEntity::upsertTenant).invoke(command);
    var result = testKit.method(TenantDirectoryEntity::upsertTenant).invoke(command);

    assertEquals(Done.getInstance(), result.getReply());
    assertFalse(result.stateWasUpdated());
  }

  @Test
  void upsertCustomerRequiresExistingTenantAndIsIdempotent() {
    var testKit = newTestKit("tenant-1");

    var missingTenantResult =
        testKit
            .method(TenantDirectoryEntity::upsertCustomer)
            .invoke(new TenantDirectory.Command.UpsertCustomer("customer-1", "Fleet A", T1));

    assertTrue(missingTenantResult.isError());
    assertEquals("Tenant does not exist: tenant-1", missingTenantResult.getError());

    testKit
        .method(TenantDirectoryEntity::upsertTenant)
        .invoke(new TenantDirectory.Command.UpsertTenant("Dealer One", T1));
    var createCustomerResult =
        testKit
            .method(TenantDirectoryEntity::upsertCustomer)
            .invoke(new TenantDirectory.Command.UpsertCustomer("customer-1", "Fleet A", T2));
    var repeatedCustomerResult =
        testKit
            .method(TenantDirectoryEntity::upsertCustomer)
            .invoke(new TenantDirectory.Command.UpsertCustomer("customer-1", "Fleet A", T2));

    assertTrue(createCustomerResult.stateWasUpdated());
    assertFalse(repeatedCustomerResult.stateWasUpdated());
    assertEquals("Fleet A", testKit.getState().findCustomer("customer-1").orElseThrow().name());
  }
}
