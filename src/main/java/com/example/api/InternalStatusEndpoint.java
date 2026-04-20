package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;

/**
 * Focused HTTP endpoint example for internal-only ACLs and method-level overrides.
 */
@HttpEndpoint("/internal-status")
@Acl(allow = @Acl.Matcher(service = "*"))
public class InternalStatusEndpoint extends AbstractHttpEndpoint {

  public record CallerResponse(String origin, String localService, boolean internet) {}

  public record StatusResponse(String status) {}

  @Get("/whoami")
  public HttpResponse whoAmI() {
    var principals = requestContext().getPrincipals();
    var origin =
        principals.isSelf()
            ? "self"
            : principals.isBackoffice()
                ? "backoffice"
                : principals.isInternet() ? "internet" : "service";

    return HttpResponses.ok(
        new CallerResponse(origin, principals.getLocalService().orElse(""), principals.isInternet()));
  }

  @Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
  @Get("/public-ping")
  public HttpResponse publicPing() {
    return HttpResponses.ok(new StatusResponse("ok"));
  }
}
