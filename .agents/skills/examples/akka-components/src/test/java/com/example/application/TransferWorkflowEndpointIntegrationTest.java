package com.example.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKitSupport;
import com.example.domain.Wallet;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class TransferWorkflowEndpointIntegrationTest extends TestKitSupport {

  record StartTransferRequest(String fromWalletId, String toWalletId, int amount) {}

  record TransferResponse(
      String transferId,
      String fromWalletId,
      String toWalletId,
      int amount,
      String status,
      String failureReason) {}

  @Test
  void startAndGetTransferViaEndpoint() {
    createWallet("wallet-endpoint-a", 100);
    createWallet("wallet-endpoint-b", 20);

    var startResponse =
        await(
            httpClient
                .POST("/transfers/transfer-http-1")
                .withRequestBody(new StartTransferRequest("wallet-endpoint-a", "wallet-endpoint-b", 30))
                .responseBodyAs(TransferResponse.class)
                .invokeAsync());

    assertTrue(startResponse.status().isSuccess());
    assertEquals("transfer-http-1", startResponse.body().transferId());
    assertEquals("STARTED", startResponse.body().status());

    Awaitility.await()
        .ignoreExceptions()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              var getResponse =
                  await(
                      httpClient
                          .GET("/transfers/transfer-http-1")
                          .responseBodyAs(TransferResponse.class)
                          .invokeAsync());

              assertTrue(getResponse.status().isSuccess());
              assertEquals("COMPLETED", getResponse.body().status());
              assertEquals("wallet-endpoint-a", getResponse.body().fromWalletId());
              assertEquals("wallet-endpoint-b", getResponse.body().toWalletId());
            });
  }

  @Test
  void validationErrorBecomesBadRequest() {
    var error =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                await(
                    httpClient
                        .POST("/transfers/transfer-http-2")
                        .withRequestBody(new StartTransferRequest("wallet-a", "wallet-b", 0))
                        .responseBodyAs(String.class)
                        .invokeAsync()));

    assertTrue(error.getMessage().contains("HTTP status 400 Bad Request"));
    assertTrue(error.getMessage().contains("transfer amount must be greater than zero"));
  }

  @Test
  void updatesEndpointStreamsWorkflowNotifications() throws Exception {
    createWallet("wallet-sse-a", 100);
    createWallet("wallet-sse-b", 20);

    var starter =
        new Thread(
            () -> {
              try {
                Thread.sleep(500);
              } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
              }

              await(
                  httpClient
                      .POST("/transfers/transfer-http-3")
                      .withRequestBody(new StartTransferRequest("wallet-sse-a", "wallet-sse-b", 25))
                      .responseBodyAs(TransferResponse.class)
                      .invokeAsync());
            });
    starter.start();

    var events =
        testKit
            .getSelfSseRouteTester()
            .receiveFirstN("/transfers/transfer-http-3/updates", 2, Duration.ofSeconds(10));

    starter.join();

    assertEquals(2, events.size());

    var statuses =
        List.of(
            JsonSupport.getObjectMapper().readTree(events.get(0).getData()).get("status").asText(),
            JsonSupport.getObjectMapper().readTree(events.get(1).getData()).get("status").asText());
    var steps =
        List.of(
            JsonSupport.getObjectMapper().readTree(events.get(0).getData()).get("step").asText(),
            JsonSupport.getObjectMapper().readTree(events.get(1).getData()).get("step").asText());

    assertEquals(List.of("WITHDRAW_SUCCEEDED", "COMPLETED"), statuses);
    assertEquals(List.of("withdraw", "deposit"), steps);
  }

  private void createWallet(String walletId, int initialBalance) {
    componentClient
        .forEventSourcedEntity(walletId)
        .method(WalletEntity::create)
        .invoke(new Wallet.Command.Create(initialBalance));
  }
}
