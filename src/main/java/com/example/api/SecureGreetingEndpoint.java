package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.JWT;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.http.AbstractHttpEndpoint;
import akka.javasdk.http.HttpResponses;

/**
 * Focused HTTP endpoint example for JWT bearer token validation and claim access.
 */
@HttpEndpoint("/secure-greetings")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
public class SecureGreetingEndpoint extends AbstractHttpEndpoint {

  public record AuthenticatedGreetingResponse(
      String issuer,
      String subject,
      String role,
      String message) {}

  @JWT(
      validate = JWT.JwtMethodMode.BEARER_TOKEN,
      bearerTokenIssuers = "test-issuer",
      staticClaims = @JWT.StaticClaim(claim = "role", values = "reader"))
  @Get("/me")
  public HttpResponse me() {
    var claims = requestContext().getJwtClaims();
    var issuer = claims.issuer().orElse("unknown");
    var subject = claims.subject().orElse("anonymous");
    var role = claims.getString("role").orElse("unknown");

    return HttpResponses.ok(
        new AuthenticatedGreetingResponse(
            issuer,
            subject,
            role,
            "Hello " + subject + "!"));
  }
}
