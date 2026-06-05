package com.example.api;

import akka.http.javadsl.model.HttpHeader;
import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;

/**
 * Focused HTTP endpoint example for request-header access through request context.
 */
@HttpEndpoint("/request-headers")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class RequestHeadersEndpoint extends AbstractHttpEndpoint {

  public record HeaderSummaryResponse(
      String requestId,
      String tenant,
      boolean internetPrincipal,
      int headerCount) {}

  @Get("/echo")
  public HttpResponse echo() {
    var requestId =
        requestContext().requestHeader("X-Request-Id").map(HttpHeader::value).orElse("");
    if (requestId.isBlank()) {
      return HttpResponses.badRequest("X-Request-Id header is required");
    }

    var tenant = requestContext().requestHeader("X-Tenant").map(HttpHeader::value).orElse("public");
    var principals = requestContext().getPrincipals();

    return HttpResponses.ok(
        new HeaderSummaryResponse(
            requestId,
            tenant,
            principals.isInternet(),
            requestContext().allRequestHeaders().size()));
  }
}
