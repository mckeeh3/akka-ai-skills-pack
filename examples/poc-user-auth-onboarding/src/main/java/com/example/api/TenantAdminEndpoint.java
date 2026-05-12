package com.example.api;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Delete;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import com.example.application.TenantEntity;
import com.example.domain.Tenant;
import com.example.security.AuthorizationService;

@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/tenants")
public class TenantAdminEndpoint extends AbstractHttpEndpoint {
  private final ComponentClient componentClient;
  private final AuthorizationService authorization;

  public TenantAdminEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
    this.authorization = new AuthorizationService(componentClient);
  }

  public record UpsertTenantRequest(String name, boolean active) {}

  @Get("/{tenantId}")
  public Tenant getTenant(String tenantId) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireTenantAdmin(auth, tenantId);
    return componentClient.forKeyValueEntity(tenantId).method(TenantEntity::get).invoke();
  }

  @Put("/{tenantId}")
  public Tenant upsertTenant(String tenantId, UpsertTenantRequest request) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireAppAdmin(auth);
    var tenant = componentClient
      .forKeyValueEntity(tenantId)
      .method(TenantEntity::upsert)
      .invoke(new TenantEntity.UpsertTenant(request.name(), request.active()));
    authorization.audit(auth.actorUserId(), auth.effectiveUserId(), "TENANT_UPSERTED", "Tenant", tenantId, tenantId, null, requestContext(), null);
    return tenant;
  }

  @Delete("/{tenantId}")
  public String deleteTenant(String tenantId) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireAppAdmin(auth);
    componentClient.forKeyValueEntity(tenantId).method(TenantEntity::deleteTenant).invoke();
    authorization.audit(auth.actorUserId(), auth.effectiveUserId(), "TENANT_DELETED", "Tenant", tenantId, tenantId, null, requestContext(), null);
    return "deleted";
  }
}
