package com.example.application;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.domain.AdminAuditEntry;
import com.example.domain.Role;
import com.example.domain.RoleAssignment;
import com.example.domain.UserProfile;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

import akka.javasdk.ServiceSetup;
import akka.javasdk.annotations.Setup;
import akka.javasdk.client.ComponentClient;

@Setup
public class AdminUserBootstrap implements ServiceSetup {
  private static final Logger logger = LoggerFactory.getLogger(AdminUserBootstrap.class);

  private final ComponentClient componentClient;

  public AdminUserBootstrap(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Override
  public void onStartup() {
    var adminUsers = getenv("ADMIN_USERS");
    if (adminUsers.isBlank()) {
      logger.info("ADMIN_USERS is not set; skipping admin user bootstrap");
      return;
    }

    for (var rawEntry : adminUsers.split(",")) {
      var entry = rawEntry.trim();
      if (entry.isBlank())
        continue;

      try {
        bootstrapAdminUser(parseEntry(entry));
      } catch (Exception e) {
        logger.error("Failed to bootstrap admin user entry [{}]", entry, e);
      }
    }
  }

  private void bootstrapAdminUser(AdminUserSpec spec) throws ResendException {
    var existing = componentClient
        .forKeyValueEntity(spec.userId())
        .method(UserAccountEntity::get)
        .invoke();

    if (existing.exists()) {
      logger.info("Admin bootstrap user [{}] already exists; skipping invite", spec.email());
      return;
    }

    var created = componentClient
        .forKeyValueEntity(spec.userId())
        .method(UserAccountEntity::invite)
        .invoke(new UserAccountEntity.InviteUser(spec.email(), defaultProfile(spec.email()),
            List.of(spec.roleAssignment())));

    createAuditEntry("system", created.userId(), "BOOTSTRAP_ADMIN_USER_CREATED", "UserAccount", created.userId(),
        spec.roleAssignment().tenantId(), spec.roleAssignment().customerId(),
        Map.of("email", spec.email(), "role", spec.role().name()));
    sendInviteEmail(spec.email());
    logger.info("Bootstrapped admin invite for [{}] with role [{}]", spec.email(), spec.role());
  }

  private AdminUserSpec parseEntry(String entry) {
    var parts = entry.split(":", -1);
    if (parts.length != 3) {
      throw new IllegalArgumentException("ADMIN_USERS entries must be email:ROLE:scope");
    }

    var email = normalizeEmail(parts[0]);
    var role = parseRole(parts[1]);
    var scope = parts[2].trim();

    String tenantId = null;
    String customerId = null;

    if (role == Role.APP_ADMIN) {
      if (!scope.equalsIgnoreCase("ALL")) {
        throw new IllegalArgumentException("APP_ADMIN/ADMIN scope must be ALL");
      }
    } else if (role == Role.TENANT_ADMIN) {
      if (scope.isBlank() || scope.equalsIgnoreCase("ALL")) {
        throw new IllegalArgumentException("TENANT_ADMIN scope must be a tenant id");
      }
      tenantId = scope;
    } else if (role == Role.CUSTOMER_ADMIN) {
      var scopeParts = scope.split("/", -1);
      if (scopeParts.length != 2 || scopeParts[0].isBlank() || scopeParts[1].isBlank()) {
        throw new IllegalArgumentException("CUSTOMER_ADMIN scope must be tenant-id/customer-id");
      }
      tenantId = scopeParts[0];
      customerId = scopeParts[1];
    } else {
      throw new IllegalArgumentException("ADMIN_USERS supports ADMIN, APP_ADMIN, TENANT_ADMIN, and CUSTOMER_ADMIN");
    }

    return new AdminUserSpec(email, email, role, new RoleAssignment(role, tenantId, customerId));
  }

  private Role parseRole(String rawRole) {
    var normalized = rawRole.trim().toUpperCase(Locale.ROOT);
    if (normalized.equals("ADMIN"))
      return Role.APP_ADMIN;
    return Role.valueOf(normalized);
  }

  private void sendInviteEmail(String email) throws ResendException {
    var apiKey = getenv("RESEND_API_KEY");
    if (apiKey.isBlank()) {
      logger.warn("RESEND_API_KEY is not set; created invite user [{}] but did not send email", email);
      return;
    }

    var appBaseUrl = getenv("APP_BASE_URL");
    if (appBaseUrl.isBlank())
      appBaseUrl = "http://localhost:9000";

    var from = getenv("INVITE_EMAIL_FROM");
    if (from.isBlank())
      from = "Akka Secure App <onboarding@resend.dev>";

    var resend = new Resend(apiKey);
    var params = CreateEmailOptions.builder()
        .from(from)
        .to(email)
        .subject(inviteSubject())
        .html(inviteHtml(appBaseUrl))
        .build();

    var response = resend.emails().send(params);
    logger.info("Sent bootstrap invite to [{}] via Resend email id [{}]", email, response.getId());
  }

  private String inviteSubject() {
    var subject = getenv("INVITE_EMAIL_SUBJECT");
    return subject.isBlank() ? "Account access information" : subject;
  }

  private String inviteHtml(String appBaseUrl) {
    return """
        <p>Hello,</p>
        <p>An account has been prepared for you in our application.</p>
        <p>Please use the link below to access the application and complete sign-in.</p>
        <p><a href=\"%s\">Access your account</a></p>
        <p>If you were not expecting this message, you can ignore this email.</p>
        """.formatted(appBaseUrl);
  }

  private UserProfile defaultProfile(String email) {
    var localPart = email.substring(0, email.indexOf('@'));
    return new UserProfile("", "", localPart, "", "", "en-US", "UTC");
  }

  private void createAuditEntry(String actorUserId, String effectiveUserId, String action, String targetType,
      String targetId, String tenantId, String customerId, Map<String, String> metadata) {
    var auditId = UUID.randomUUID().toString();
    var entry = new AdminAuditEntry(auditId, actorUserId, effectiveUserId, action, targetType, targetId, tenantId,
        customerId, Instant.now(), "", "service-startup", metadata);
    componentClient.forKeyValueEntity(auditId).method(AdminAuditEntryEntity::create).invoke(entry);
  }

  private String normalizeEmail(String email) {
    var normalized = email.trim().toLowerCase(Locale.ROOT);
    if (normalized.isBlank() || !normalized.contains("@")) {
      throw new IllegalArgumentException("Invalid email in ADMIN_USERS entry");
    }
    return normalized;
  }

  private String getenv(String name) {
    var value = System.getenv(name);
    return value == null ? "" : value.trim();
  }

  private record AdminUserSpec(String email, String userId, Role role, RoleAssignment roleAssignment) {
  }
}
