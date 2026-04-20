package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.Principal;
import akka.javasdk.testkit.TestKitSupport;
import com.example.api.grpc.PatternSecureGreetingGrpcEndpointClient;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PatternSecureGreetingGrpcEndpointIntegrationTest extends TestKitSupport {

  @Test
  void regexValidatedClaimsAllowMatchingToken() throws Exception {
    var client =
        getGrpcEndpointClient(PatternSecureGreetingGrpcEndpointClient.class, Principal.INTERNET)
            .addRequestHeader(
                "Authorization",
                "Bearer "
                    + bearerTokenWith(
                        Map.of(
                            "iss", "pattern-issuer",
                            "sub", "123e4567-e89b-12d3-a456-426614174000",
                            "role", "admin",
                            "name", "alice")));

    var response = client.validateCaller(Empty.getDefaultInstance());

    assertEquals("pattern-issuer", response.getIssuer());
    assertEquals("123e4567-e89b-12d3-a456-426614174000", response.getSubject());
    assertEquals("admin", response.getRole());
    assertEquals("alice", response.getName());
    assertEquals("Validated admin alice", response.getGreeting());
  }

  @Test
  void nonMatchingRolePatternIsRejected() throws Exception {
    var client =
        getGrpcEndpointClient(PatternSecureGreetingGrpcEndpointClient.class, Principal.INTERNET)
            .addRequestHeader(
                "Authorization",
                "Bearer "
                    + bearerTokenWith(
                        Map.of(
                            "iss", "pattern-issuer",
                            "sub", "123e4567-e89b-12d3-a456-426614174000",
                            "role", "reader",
                            "name", "alice")));

    var error =
        assertThrows(StatusRuntimeException.class, () -> client.validateCaller(Empty.getDefaultInstance()));

    assertTrue(
        Set.of(Status.Code.UNAUTHENTICATED, Status.Code.PERMISSION_DENIED)
            .contains(error.getStatus().getCode()));
  }

  @Test
  void nonMatchingUuidSubjectPatternIsRejected() throws Exception {
    var client =
        getGrpcEndpointClient(PatternSecureGreetingGrpcEndpointClient.class, Principal.INTERNET)
            .addRequestHeader(
                "Authorization",
                "Bearer "
                    + bearerTokenWith(
                        Map.of(
                            "iss", "pattern-issuer",
                            "sub", "not-a-uuid",
                            "role", "editor",
                            "name", "alice")));

    var error =
        assertThrows(StatusRuntimeException.class, () -> client.validateCaller(Empty.getDefaultInstance()));

    assertTrue(
        Set.of(Status.Code.UNAUTHENTICATED, Status.Code.PERMISSION_DENIED)
            .contains(error.getStatus().getCode()));
  }

  @Test
  void blankNamePatternIsRejected() throws Exception {
    var client =
        getGrpcEndpointClient(PatternSecureGreetingGrpcEndpointClient.class, Principal.INTERNET)
            .addRequestHeader(
                "Authorization",
                "Bearer "
                    + bearerTokenWith(
                        Map.of(
                            "iss", "pattern-issuer",
                            "sub", "123e4567-e89b-12d3-a456-426614174000",
                            "role", "editor",
                            "name", "")));

    var error =
        assertThrows(StatusRuntimeException.class, () -> client.validateCaller(Empty.getDefaultInstance()));

    assertTrue(
        Set.of(Status.Code.UNAUTHENTICATED, Status.Code.PERMISSION_DENIED)
            .contains(error.getStatus().getCode()));
  }

  private String bearerTokenWith(Map<String, String> claims) throws Exception {
    var header = Base64.getEncoder().encodeToString("{\"alg\":\"none\"}".getBytes());
    var payload = Base64.getEncoder().encodeToString(JsonSupport.getObjectMapper().writeValueAsBytes(claims));
    return header + "." + payload;
  }
}
