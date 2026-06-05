package com.example.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.Done;
import akka.javasdk.testkit.KeyValueEntityTestKit;
import com.example.domain.security.AccountStatus;
import com.example.domain.security.LocalAccount;
import com.example.domain.security.RoleAssignment;
import com.example.domain.security.SecurityRole;
import com.example.domain.security.UserProfile;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class LocalAccountEntityTest {

  private static final Instant T1 = Instant.parse("2026-01-01T00:00:00Z");
  private static final Instant T2 = Instant.parse("2026-01-02T00:00:00Z");
  private static final Instant T3 = Instant.parse("2026-01-03T00:00:00Z");

  private KeyValueEntityTestKit<LocalAccount.State, LocalAccountEntity> newTestKit(String userId) {
    return KeyValueEntityTestKit.of(userId, LocalAccountEntity::new);
  }

  @Test
  void inviteCreatesLocalAccountForBackendAuthorization() {
    var testKit = newTestKit("user-1");

    var result =
        testKit
            .method(LocalAccountEntity::invite)
            .invoke(
                new LocalAccount.Command.Invite(
                    "Jane@Example.COM",
                    new UserProfile("Jane Example", "jane@example.com"),
                    List.of(new RoleAssignment(SecurityRole.DEALER_OWNER, "tenant-1", null)),
                    T1));

    assertEquals(Done.getInstance(), result.getReply());
    assertTrue(result.stateWasUpdated());
    assertEquals(AccountStatus.INVITED, testKit.getState().status());
    assertEquals("jane@example.com", testKit.getState().email());
    assertTrue(testKit.getState().canAccessTenant("tenant-1") == false, "invited users are not active yet");
  }

  @Test
  void repeatedInviteWithSameEmailIsIdempotent() {
    var testKit = newTestKit("user-1");
    var command =
        new LocalAccount.Command.Invite(
            "jane@example.com",
            new UserProfile("Jane Example", "jane@example.com"),
            List.of(new RoleAssignment(SecurityRole.APP_ADMIN, null, null)),
            T1);

    testKit.method(LocalAccountEntity::invite).invoke(command);
    var result = testKit.method(LocalAccountEntity::invite).invoke(command);

    assertEquals(Done.getInstance(), result.getReply());
    assertFalse(result.stateWasUpdated());
  }

  @Test
  void linkAndActivateEnablesScopedChecks() {
    var testKit = newTestKit("user-1");
    testKit
        .method(LocalAccountEntity::invite)
        .invoke(
            new LocalAccount.Command.Invite(
                "ops@example.com",
                new UserProfile("Ops Lead", "ops@example.com"),
                List.of(new RoleAssignment(SecurityRole.OPERATIONS_SUPERVISOR, "tenant-1", null)),
                T1));

    var result =
        testKit
            .method(LocalAccountEntity::linkAndActivate)
            .invoke(new LocalAccount.Command.LinkAndActivate("workos-1", T2));

    assertEquals(Done.getInstance(), result.getReply());
    assertTrue(result.stateWasUpdated());
    assertEquals(AccountStatus.ACTIVE, testKit.getState().status());
    assertTrue(testKit.getState().canAccessTenant("tenant-1"));
    assertFalse(testKit.getState().canAccessTenant("tenant-2"));
  }

  @Test
  void disabledAccountCannotBeActivatedWithValidWorkosIdentity() {
    var testKit = newTestKit("user-1");
    testKit
        .method(LocalAccountEntity::invite)
        .invoke(
            new LocalAccount.Command.Invite(
                "jane@example.com",
                new UserProfile("Jane Example", "jane@example.com"),
                List.of(new RoleAssignment(SecurityRole.USER, "tenant-1", "customer-1")),
                T1));
    testKit.method(LocalAccountEntity::disable).invoke(new LocalAccount.Command.Disable(T2));

    var result =
        testKit
            .method(LocalAccountEntity::linkAndActivate)
            .invoke(new LocalAccount.Command.LinkAndActivate("workos-1", T3));

    assertTrue(result.isError());
    assertEquals("User is disabled: user-1", result.getError());
    assertEquals(AccountStatus.DISABLED, testKit.getState().status());
  }

  @Test
  void replacingSameRolesIsANoOp() {
    var testKit = newTestKit("user-1");
    var roles = List.of(new RoleAssignment(SecurityRole.CUSTOMER_ADMIN, "tenant-1", "customer-1"));
    testKit
        .method(LocalAccountEntity::invite)
        .invoke(
            new LocalAccount.Command.Invite(
                "admin@example.com", new UserProfile("Admin", "admin@example.com"), roles, T1));

    var result =
        testKit.method(LocalAccountEntity::replaceRoles).invoke(new LocalAccount.Command.ReplaceRoles(roles, T2));

    assertEquals(Done.getInstance(), result.getReply());
    assertFalse(result.stateWasUpdated());
  }
}
