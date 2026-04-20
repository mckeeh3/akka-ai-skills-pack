package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.Principal;
import akka.javasdk.testkit.TestKitSupport;
import com.example.api.grpc.SecureGreetingGrpcEndpointClient;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SecureGreetingGrpcEndpointIntegrationTest extends TestKitSupport {

  @Test
  void bearerTokenClaimsAreAvailableThroughGrpcRequestContext() throws Exception {
    var client =
        getGrpcEndpointClient(SecureGreetingGrpcEndpointClient.class, Principal.INTERNET)
            .addRequestHeader(
                "Authorization",
                "Bearer "
                    + bearerTokenWith(
                        Map.of(
                            "iss", "test-issuer",
                            "sub", "alice",
                            "role", "reader",
                            "aud", "team-a")));

    var response = client.me(Empty.getDefaultInstance());

    assertEquals("test-issuer", response.getIssuer());
    assertEquals("alice", response.getSubject());
    assertEquals("reader", response.getRole());
    assertEquals("Hello alice!", response.getGreeting());
    assertTrue(response.hasAudience());
    assertEquals("team-a", response.getAudience().getValue());
  }

  @Test
  void missingBearerTokenIsRejected() {
    var client = getGrpcEndpointClient(SecureGreetingGrpcEndpointClient.class, Principal.INTERNET);

    var error =
        assertThrows(StatusRuntimeException.class, () -> client.me(Empty.getDefaultInstance()));

    assertTrue(
        Set.of(Status.Code.UNAUTHENTICATED, Status.Code.PERMISSION_DENIED)
            .contains(error.getStatus().getCode()));
  }

  @Test
  void invalidStaticClaimIsRejected() throws Exception {
    var client =
        getGrpcEndpointClient(SecureGreetingGrpcEndpointClient.class, Principal.INTERNET)
            .addRequestHeader(
                "Authorization",
                "Bearer "
                    + bearerTokenWith(
                        Map.of("iss", "test-issuer", "sub", "bob", "role", "writer")));

    var error =
        assertThrows(StatusRuntimeException.class, () -> client.me(Empty.getDefaultInstance()));

    assertTrue(
        Set.of(Status.Code.UNAUTHENTICATED, Status.Code.PERMISSION_DENIED)
            .contains(error.getStatus().getCode()));
  }

  @Test
  void secondAllowedIssuerIsAccepted() throws Exception {
    var client =
        getGrpcEndpointClient(SecureGreetingGrpcEndpointClient.class, Principal.INTERNET)
            .addRequestHeader(
                "Authorization",
                "Bearer "
                    + bearerTokenWith(
                        Map.of("iss", "backup-issuer", "sub", "carol", "role", "reader")));

    var response = client.me(Empty.getDefaultInstance());

    assertEquals("backup-issuer", response.getIssuer());
    assertEquals("carol", response.getSubject());
    assertFalse(response.hasAudience());
  }

  private String bearerTokenWith(Map<String, String> claims) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(claims));
    return header + "." + payload;
  }
}
