package com.example.security;

import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.RequestContext;
import com.example.application.AdminAuditEntryEntity;
import com.example.application.UserAccountEntity;
import com.example.domain.AdminAuditEntry;
import com.example.domain.UserAccount;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static akka.javasdk.http.HttpException.forbidden;
import static akka.javasdk.http.HttpException.unauthorized;

public class AuthorizationService {
  public static final String IMPERSONATE_HEADER = "X-Impersonate-User-Id";

  private final ComponentClient componentClient;
  private final WorkosUserLookup workosUserLookup;

  public AuthorizationService(ComponentClient componentClient) {
    this.componentClient = componentClient;
    this.workosUserLookup = new WorkosUserLookup();
  }

  public AuthContext requireAuthenticated(RequestContext requestContext) {
    var claims = requestContext.getJwtClaims();
    var workosUserId = claims.subject().orElseThrow(() -> unauthorized("JWT subject is required"));

    var actor = getUser(workosUserId);
    var actorUserId = workosUserId;

    if (!actor.exists()) {
      var email = claims.getString("email").map(String::toLowerCase).orElse("");
      if (email.isBlank()) {
        email = workosUserLookup.userById(workosUserId).email().toLowerCase();
      }
      if (!email.isBlank()) {
        var invitedByEmail = getUser(email);
        if (invitedByEmail.exists()) {
          actorUserId = email;
          actor = invitedByEmail.isActive()
            ? invitedByEmail
            : componentClient
              .forKeyValueEntity(email)
              .method(UserAccountEntity::activate)
              .invoke(new UserAccountEntity.ActivateUser(workosUserId));
        }
      }
    }

    if (!actor.exists() || !actor.isActive()) {
      throw forbidden("User is not active");
    }

    var impersonatedUserId = requestContext.requestHeader(IMPERSONATE_HEADER).map(header -> header.value()).orElse(null);
    if (impersonatedUserId == null || impersonatedUserId.isBlank() || impersonatedUserId.equals(actorUserId)) {
      return new AuthContext(actorUserId, actorUserId, workosUserId, actor, actor, false);
    }

    if (!actor.isAppAdmin()) {
      throw forbidden("Only APP_ADMIN users may impersonate");
    }
    var effective = getUser(impersonatedUserId);
    if (!effective.exists() || !effective.isActive()) {
      throw forbidden("Impersonated user is not active");
    }
    audit(actorUserId, impersonatedUserId, "IMPERSONATION_USED", "UserAccount", impersonatedUserId, null, null, requestContext, Map.of());
    return new AuthContext(actorUserId, impersonatedUserId, workosUserId, actor, effective, true);
  }

  public void requireAppAdmin(AuthContext auth) {
    if (!auth.actor().isAppAdmin()) {
      throw forbidden("APP_ADMIN role is required");
    }
  }

  public void requireTenantAdmin(AuthContext auth, String tenantId) {
    if (!auth.actor().canAdminTenant(tenantId)) {
      throw forbidden("Tenant admin access is required");
    }
  }

  public void requireCustomerAdmin(AuthContext auth, String tenantId, String customerId) {
    if (!auth.actor().canAdminCustomer(tenantId, customerId)) {
      throw forbidden("Customer admin access is required");
    }
  }

  public UserAccount getUser(String userId) {
    return componentClient.forKeyValueEntity(userId).method(UserAccountEntity::get).invoke();
  }

  public void audit(
    String actorUserId,
    String effectiveUserId,
    String action,
    String targetType,
    String targetId,
    String tenantId,
    String customerId,
    RequestContext requestContext,
    Map<String, String> metadata
  ) {
    var auditId = UUID.randomUUID().toString();
    var userAgent = requestContext.requestHeader("User-Agent").map(header -> header.value()).orElse("");
    var entry = new AdminAuditEntry(
      auditId,
      actorUserId,
      effectiveUserId,
      action,
      targetType,
      targetId,
      tenantId,
      customerId,
      Instant.now(),
      "",
      userAgent,
      metadata == null ? Map.of() : metadata
    );
    componentClient.forKeyValueEntity(auditId).method(AdminAuditEntryEntity::create).invoke(entry);
  }
}
