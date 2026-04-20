package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.Principal;
import akka.javasdk.testkit.TestKitSupport;
import com.example.api.grpc.InternalStatusGrpcEndpointClient;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InternalStatusGrpcEndpointIntegrationTest extends TestKitSupport {

  @Test
  void serviceCallerCanInspectPrincipalsThroughRequestContext() {
    var client =
        getGrpcEndpointClient(
            InternalStatusGrpcEndpointClient.class, Principal.localService("caller-service"));

    var response = client.whoAmI(Empty.getDefaultInstance());

    assertEquals("service", response.getOrigin());
    assertEquals("caller-service", response.getLocalService());
    assertFalse(response.getInternet());
    assertTrue(response.getMetadataEntryCount() >= 0);
  }

  @Test
  void internetCallerIsDeniedByServiceOnlyAcl() {
    var client = getGrpcEndpointClient(InternalStatusGrpcEndpointClient.class, Principal.INTERNET);

    var error =
        assertThrows(StatusRuntimeException.class, () -> client.whoAmI(Empty.getDefaultInstance()));

    assertTrue(
        Set.of(Status.Code.PERMISSION_DENIED, Status.Code.UNAUTHENTICATED)
            .contains(error.getStatus().getCode()));
  }
}
