package com.example.api;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.GrpcEndpoint;
import akka.javasdk.annotations.JWT;
import akka.javasdk.grpc.AbstractGrpcEndpoint;
import com.example.api.grpc.AuthenticatedCallerResponse;
import com.example.api.grpc.SecureGreetingGrpcEndpoint;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;

/** Focused gRPC endpoint example for JWT bearer-token validation and claim access. */
@GrpcEndpoint
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
public class SecureGreetingGrpcEndpointImpl extends AbstractGrpcEndpoint
    implements SecureGreetingGrpcEndpoint {

  @Override
  @JWT(
      validate = JWT.JwtMethodMode.BEARER_TOKEN,
      bearerTokenIssuers = {"test-issuer", "backup-issuer"},
      staticClaims = @JWT.StaticClaim(claim = "role", values = "reader"))
  public AuthenticatedCallerResponse me(Empty in) {
    var claims = requestContext().getJwtClaims();
    var issuer = claims.issuer().orElse("unknown");
    var subject = claims.subject().orElse("anonymous");
    var role = claims.getString("role").orElse("unknown");

    var builder =
        AuthenticatedCallerResponse.newBuilder()
            .setIssuer(issuer)
            .setSubject(subject)
            .setRole(role)
            .setGreeting("Hello " + subject + "!");

    claims.audience().ifPresent(audience -> builder.setAudience(StringValue.of(audience)));
    return builder.build();
  }
}
