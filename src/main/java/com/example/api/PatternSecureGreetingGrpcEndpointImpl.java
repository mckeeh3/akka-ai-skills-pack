package com.example.api;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.GrpcEndpoint;
import akka.javasdk.annotations.JWT;
import akka.javasdk.grpc.AbstractGrpcEndpoint;
import com.example.api.grpc.PatternSecureGreetingGrpcEndpoint;
import com.example.api.grpc.PatternValidatedCallerResponse;
import com.google.protobuf.Empty;

/** Focused gRPC endpoint example for regex-based JWT claim validation. */
@GrpcEndpoint
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
public class PatternSecureGreetingGrpcEndpointImpl extends AbstractGrpcEndpoint
    implements PatternSecureGreetingGrpcEndpoint {

  @Override
  @JWT(
      validate = JWT.JwtMethodMode.BEARER_TOKEN,
      bearerTokenIssuers = "pattern-issuer",
      staticClaims = {
        @JWT.StaticClaim(claim = "role", pattern = "^(admin|editor)$"),
        @JWT.StaticClaim(
            claim = "sub",
            pattern =
                "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"),
        @JWT.StaticClaim(claim = "name", pattern = "^\\S+$")
      })
  public PatternValidatedCallerResponse validateCaller(Empty in) {
    var claims = requestContext().getJwtClaims();
    var issuer = claims.issuer().orElse("unknown");
    var subject = claims.subject().orElse("unknown");
    var role = claims.getString("role").orElse("unknown");
    var name = claims.getString("name").orElse("unknown");

    return PatternValidatedCallerResponse.newBuilder()
        .setIssuer(issuer)
        .setSubject(subject)
        .setRole(role)
        .setName(name)
        .setGreeting("Validated " + role + " " + name)
        .build();
  }
}
