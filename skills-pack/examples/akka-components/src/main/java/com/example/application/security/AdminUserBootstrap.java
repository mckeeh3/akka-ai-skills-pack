package com.example.application.security;

import akka.javasdk.ServiceSetup;
import akka.javasdk.annotations.Setup;
import akka.javasdk.client.ComponentClient;
import com.example.domain.security.AdminAuditEntry;
import com.example.domain.security.LocalAccount;
import com.example.domain.security.RoleAssignment;
import com.example.domain.security.SecurityRole;
import com.example.domain.security.UserProfile;
import com.example.security.AuthorizationService;
import com.example.security.RequiredEnvironment;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Idempotent backend-only bootstrap for initial reference administrators. */
@Setup
public class AdminUserBootstrap implements ServiceSetup {

  private static final Logger logger = LoggerFactory.getLogger(AdminUserBootstrap.class);

  private final ComponentClient componentClient;
  private final AuthorizationService authorization;

  public AdminUserBootstrap(ComponentClient componentClient) {
    this.componentClient = componentClient;
    this.authorization = new AuthorizationService(componentClient);
  }

  @Override
  public void onStartup() {
    if (!isTestRuntime()) {
      RequiredEnvironment.validateOrThrow(System.getenv());
    }
    bootstrapFrom(System.getenv("ADMIN_USERS"), Instant.now());
  }

  /** Visible for integration tests and local startup adapters. */
  public List<BootstrapResult> bootstrapFrom(String adminUsers, Instant at) {
    var specs = parseAdminUsers(adminUsers);
    var results = new ArrayList<BootstrapResult>();
    if (specs.isEmpty()) {
      logger.info("ADMIN_USERS is not set; skipping admin user bootstrap");
      return List.of();
    }
    for (var spec : specs) {
      try {
        var existing = account(spec.userId());
        componentClient
            .forKeyValueEntity(spec.userId())
            .method(LocalAccountEntity::invite)
            .invoke(
                new LocalAccount.Command.Invite(
                    spec.email(), new UserProfile(spec.displayName(), spec.email()), List.of(spec.role()), at));
        var changed = !existing.exists();
        AdminAuditEntry audit = null;
        if (changed) {
          audit =
              authorization.audit(
                  AdminAuditEntry.AdminAuditAction.BOOTSTRAP_ADMIN,
                  "system",
                  spec.userId(),
                  spec.role().tenantId(),
                  spec.role().customerId(),
                  Map.of("email", spec.email(), "role", spec.role().role().name()));
        }
        results.add(new BootstrapResult(spec.userId(), changed, audit == null ? null : audit.auditId()));
      } catch (RuntimeException ex) {
        logger.error("Failed to bootstrap admin user [{}]", spec.email(), ex);
        throw ex;
      }
    }
    return List.copyOf(results);
  }

  public static List<BootstrapAdminSpec> parseAdminUsers(String adminUsers) {
    if (adminUsers == null || adminUsers.isBlank()) {
      return List.of();
    }
    var specs = new ArrayList<BootstrapAdminSpec>();
    for (var rawEntry : adminUsers.split(",")) {
      var entry = rawEntry.trim();
      if (entry.isBlank()) {
        continue;
      }
      var parts = entry.split(":", -1);
      if (parts.length != 3) {
        throw new IllegalArgumentException("ADMIN_USERS entries must be email:ROLE:scope");
      }
      var email = normalizeEmail(parts[0]);
      var role = parseRole(parts[1]);
      var assignment = assignment(role, parts[2].trim());
      specs.add(new BootstrapAdminSpec(email, email, defaultDisplayName(email), assignment));
    }
    return List.copyOf(specs);
  }

  private LocalAccount.State account(String userId) {
    return componentClient.forKeyValueEntity(userId).method(LocalAccountEntity::get).invoke();
  }

  private static RoleAssignment assignment(SecurityRole role, String scope) {
    if (role == SecurityRole.APP_ADMIN) {
      if (!"ALL".equalsIgnoreCase(scope)) {
        throw new IllegalArgumentException("APP_ADMIN scope must be ALL");
      }
      return new RoleAssignment(role, null, null);
    }
    if (scope == null || scope.isBlank() || "ALL".equalsIgnoreCase(scope)) {
      throw new IllegalArgumentException(role + " scope must include a tenant id");
    }
    if (role == SecurityRole.CUSTOMER_ADMIN || role == SecurityRole.USER) {
      var parts = scope.split("/", -1);
      if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
        throw new IllegalArgumentException(role + " scope must be tenant-id/customer-id");
      }
      return new RoleAssignment(role, parts[0], parts[1]);
    }
    return new RoleAssignment(role, scope, null);
  }

  private static SecurityRole parseRole(String rawRole) {
    var normalized = rawRole == null ? "" : rawRole.trim().toUpperCase(Locale.ROOT);
    if (normalized.equals("ADMIN")) {
      return SecurityRole.APP_ADMIN;
    }
    return SecurityRole.valueOf(normalized);
  }

  private static String normalizeEmail(String email) {
    var normalized = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    if (normalized.isBlank() || !normalized.contains("@")) {
      throw new IllegalArgumentException("Invalid email in ADMIN_USERS entry");
    }
    return normalized;
  }

  private static String defaultDisplayName(String email) {
    return email.substring(0, email.indexOf('@'));
  }

  private static boolean isTestRuntime() {
    return System.getProperty("surefire.test.class.path") != null;
  }

  public record BootstrapAdminSpec(String userId, String email, String displayName, RoleAssignment role) {}

  public record BootstrapResult(String userId, boolean created, String auditId) {}
}
