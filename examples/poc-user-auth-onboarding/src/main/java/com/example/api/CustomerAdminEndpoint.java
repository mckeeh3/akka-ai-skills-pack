package com.example.api;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Delete;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.AbstractHttpEndpoint;
import com.example.application.CustomerEntity;
import com.example.domain.Customer;
import com.example.security.AuthorizationService;

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

  public record UpsertCustomerRequest(String name, boolean active) {}

  @Get("/{customerId}")
  public Customer getCustomer(String tenantId, String customerId) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireCustomerAdmin(auth, tenantId, customerId);
    return componentClient.forKeyValueEntity(customerId).method(CustomerEntity::get).invoke();
  }

  @Put("/{customerId}")
  public Customer upsertCustomer(String tenantId, String customerId, UpsertCustomerRequest request) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireTenantAdmin(auth, tenantId);
    var customer = componentClient
      .forKeyValueEntity(customerId)
      .method(CustomerEntity::upsert)
      .invoke(new CustomerEntity.UpsertCustomer(tenantId, request.name(), request.active()));
    authorization.audit(auth.actorUserId(), auth.effectiveUserId(), "CUSTOMER_UPSERTED", "Customer", customerId, tenantId, customerId, requestContext(), null);
    return customer;
  }

  @Delete("/{customerId}")
  public String deleteCustomer(String tenantId, String customerId) {
    var auth = authorization.requireAuthenticated(requestContext());
    authorization.requireTenantAdmin(auth, tenantId);
    componentClient.forKeyValueEntity(customerId).method(CustomerEntity::deleteCustomer).invoke();
    authorization.audit(auth.actorUserId(), auth.effectiveUserId(), "CUSTOMER_DELETED", "Customer", customerId, tenantId, customerId, requestContext(), null);
    return "deleted";
  }
}
