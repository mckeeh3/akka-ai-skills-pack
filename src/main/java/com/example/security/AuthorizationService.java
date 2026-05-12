package com.example.security;

import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.RequestContext;
import com.example.application.security.AdminAuditEntryEntity;
import com.example.application.security.LocalAccountEntity;
import com.example.domain.security.AdminAuditEntry;
import com.example.domain.security.LocalAccount;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static akka.javasdk.http.HttpException.forbidden;
import static akka.javasdk.http.HttpException.unauthorized;

/**
 * Central backend authorization helper for DCA seed APIs.
 *
 * <p>WorkOS/JWT authenticates the browser user. Local Akka account state is the authority for
 * status, roles, and tenant/customer scopes.
 */
public class AuthorizationService {

  private final ComponentClient componentClient;

  public AuthorizationService(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public AuthContext requireAuthenticated(RequestContext requestContext) {
    var identity = WorkosClaimExtractor.from(requestContext.getJwtClaims());
    var account = getAccount(identity.subject());
    var userId = identity.subject();

    if (!account.exists()) {
      if (!identity.hasEmail()) {
        throw forbidden("Local account invite is required");
      }
      userId = identity.email();
      account = getAccount(userId);
    }

    if (!account.exists()) {
      throw forbidden("Local account invite is required");
    }
    if (account.status().name().equals("DISABLED")) {
      throw forbidden("Local account is disabled");
    }

    if (!account.isActive()) {
      linkAndActivate(userId, identity);
      account = getAccount(userId);
      audit(
          AdminAuditEntry.AdminAuditAction.LINK_ACCOUNT,
          userId,
          userId,
          null,
          null,
          Map.of("workosUserId", identity.subject()));
    } else if (account.workosUserId() != null && !account.workosUserId().equals(identity.subject())) {
      throw forbidden("Local account is already linked to a different WorkOS identity");
    }

    if (!account.isActive()) {
      throw forbidden("Local account is not active");
    }
    return new AuthContext(userId, identity.subject(), account);
  }

  public void requireAppAdmin(AuthContext auth) {
    requireActive(auth);
    if (!auth.actor().isAppAdmin()) {
      throw forbidden("APP_ADMIN role is required");
    }
  }

  public void requireTenantAccess(AuthContext auth, String tenantId) {
    requireActive(auth);
    if (tenantId == null || tenantId.isBlank() || !auth.actor().canAccessTenant(tenantId)) {
      throw forbidden("Tenant access is required");
    }
  }

  public void requireCustomerAccess(AuthContext auth, String tenantId, String customerId) {
    requireActive(auth);
    if (tenantId == null
        || tenantId.isBlank()
        || customerId == null
        || customerId.isBlank()
        || !auth.actor().canAccessCustomer(tenantId, customerId)) {
      throw forbidden("Customer access is required");
    }
  }

  public LocalAccount.State getAccount(String userId) {
    if (userId == null || userId.isBlank()) {
      throw unauthorized("Local user id is required");
    }
    return componentClient.forKeyValueEntity(userId).method(LocalAccountEntity::get).invoke();
  }

  private void linkAndActivate(String userId, WorkosUserIdentity identity) {
    try {
      componentClient
          .forKeyValueEntity(userId)
          .method(LocalAccountEntity::linkAndActivate)
          .invoke(new LocalAccount.Command.LinkAndActivate(identity.subject(), Instant.now()));
    } catch (RuntimeException ex) {
      throw forbidden(ex.getMessage());
    }
  }

  private void requireActive(AuthContext auth) {
    if (auth == null || !auth.isActive()) {
      throw forbidden("Active local account is required");
    }
  }

  public void audit(
      AdminAuditEntry.AdminAuditAction action,
      String actorUserId,
      String targetUserId,
      String tenantId,
      String customerId,
      Map<String, String> details) {
    var auditId = UUID.randomUUID().toString();
    var entry =
        new AdminAuditEntry(
            auditId,
            action,
            actorUserId,
            targetUserId,
            tenantId,
            customerId,
            Instant.now(),
            details == null ? Map.of() : details);
    componentClient.forKeyValueEntity(auditId).method(AdminAuditEntryEntity::create).invoke(entry);
  }
}
