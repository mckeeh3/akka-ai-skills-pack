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
import java.util.Map;

/** JWT-protected customer administration APIs nested under a tenant. */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@JWT(validate = JWT.JwtMethodMode.BEARER_TOKEN)
@HttpEndpoint("/api/tenants/{tenantId}/customers")
public class CustomerAdminEndpoint extends AbstractHttpEndpoint {

  private final ComponentClient componentClient;
  private final AuthorizationService authorization;

  public CustomerAdminEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
    this.authorization = new AuthorizationService(componentClient);
  }

  public record UpsertCustomerRequest(String customerId, String name) {}

  public record CustomerActionResponse(CustomerResponse customer, String auditId) {}

  public record CustomerResponse(String tenantId, String customerId, String name, boolean active) {
    static CustomerResponse from(String tenantId, TenantDirectory.Customer customer) {
      return new CustomerResponse(tenantId, customer.customerId(), customer.name(), customer.active());
    }
  }

  @Get("/{customerId}")
  public HttpResponse getCustomer(String tenantId, String customerId) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireCustomerAccess(auth, tenantId, customerId);
    var customer = tenant(tenantId).findCustomer(customerId);
    if (customer.isEmpty()) {
      return HttpResponses.notFound("Customer not found");
    }
    return HttpResponses.ok(CustomerResponse.from(tenantId, customer.get()));
  }

  @Post
  public HttpResponse upsertCustomer(String tenantId, UpsertCustomerRequest request) {
    if (request == null || request.customerId() == null || request.customerId().isBlank()) {
      return HttpResponses.badRequest("customerId must not be blank");
    }
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireTenantAccess(auth, tenantId);
    componentClient
        .forKeyValueEntity(tenantId)
        .method(TenantDirectoryEntity::upsertCustomer)
        .invoke(new TenantDirectory.Command.UpsertCustomer(request.customerId(), request.name(), Instant.now()));
    var audit =
        authorization.audit(
            AdminAuditEntry.AdminAuditAction.UPSERT_CUSTOMER,
            auth.actorUserId(),
            null,
            tenantId,
            request.customerId(),
            Map.of("customerName", request.name() == null ? "" : request.name()));
    var customer = tenant(tenantId).findCustomer(request.customerId()).orElseThrow();
    return HttpResponses.ok(new CustomerActionResponse(CustomerResponse.from(tenantId, customer), audit.auditId()));
  }

  private TenantDirectory.State tenant(String tenantId) {
    return componentClient.forKeyValueEntity(tenantId).method(TenantDirectoryEntity::get).invoke();
  }
}
