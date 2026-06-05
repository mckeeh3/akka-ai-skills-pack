package ai.first.api.security;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;
import ai.first.application.security.TenantDirectoryEntity;
import ai.first.domain.security.AdminAuditEntry;
import ai.first.domain.security.TenantDirectory;
import ai.first.security.AuthorizationService;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** JWT-protected tenant administration APIs. */
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

  public record UpsertTenantRequest(String tenantId, String name) {}

  public record TenantsResponse(List<TenantResponse> tenants) {}

  public record TenantActionResponse(TenantResponse tenant, String auditId) {}

  public record TenantResponse(String tenantId, String name, boolean active, List<CustomerResponse> customers) {
    static TenantResponse from(TenantDirectory.State state) {
      return new TenantResponse(
          state.tenantId(), state.name(), state.active(), state.customers().stream().map(CustomerResponse::from).toList());
    }
  }

  public record CustomerResponse(String customerId, String name, boolean active) {
    static CustomerResponse from(TenantDirectory.Customer customer) {
      return new CustomerResponse(customer.customerId(), customer.name(), customer.active());
    }
  }

  @Get
  public HttpResponse listTenants() {
    var auth = authorization.requireAuthenticated(requestContext());
    var ids = requestContext().queryParams().getString("tenantIds").orElse("");
    if (ids.isBlank()) {
      return HttpResponses.ok(new TenantsResponse(List.of()));
    }
    var tenants =
        Arrays.stream(ids.split(","))
            .map(String::trim)
            .filter(id -> !id.isBlank())
            .filter(id -> auth.actor().canAccessTenant(id))
            .map(this::tenant)
            .filter(TenantDirectory.State::exists)
            .map(TenantResponse::from)
            .toList();
    return HttpResponses.ok(new TenantsResponse(tenants));
  }

  @Get("/{tenantId}")
  public HttpResponse getTenant(String tenantId) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireTenantAccess(auth, tenantId);
    var tenant = tenant(tenantId);
    if (!tenant.exists()) {
      return HttpResponses.notFound("Tenant not found");
    }
    return HttpResponses.ok(TenantResponse.from(tenant));
  }

  @Post
  public HttpResponse upsertTenant(UpsertTenantRequest request) {
    if (request == null || request.tenantId() == null || request.tenantId().isBlank()) {
      return HttpResponses.badRequest("tenantId must not be blank");
    }
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireAppAdmin(auth);
    componentClient
        .forKeyValueEntity(request.tenantId())
        .method(TenantDirectoryEntity::upsertTenant)
        .invoke(new TenantDirectory.Command.UpsertTenant(request.name(), Instant.now()));
    var audit =
        authorization.audit(
            AdminAuditEntry.AdminAuditAction.UPSERT_TENANT,
            auth.actorUserId(),
            null,
            request.tenantId(),
            null,
            Map.of("tenantName", request.name() == null ? "" : request.name()));
    return HttpResponses.ok(new TenantActionResponse(TenantResponse.from(tenant(request.tenantId())), audit.auditId()));
  }

  private TenantDirectory.State tenant(String tenantId) {
    return componentClient.forKeyValueEntity(tenantId).method(TenantDirectoryEntity::get).invoke();
  }
}
