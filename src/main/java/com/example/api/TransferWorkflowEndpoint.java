package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.CommandException;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.TransferWorkflow;
import com.example.domain.TransferState;

/**
 * HTTP endpoint example for starting, querying, and subscribing to a workflow.
 */
@HttpEndpoint("/transfers")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class TransferWorkflowEndpoint {

  public record StartTransferRequest(String fromWalletId, String toWalletId, int amount) {}

  public record TransferResponse(
      String transferId,
      String fromWalletId,
      String toWalletId,
      int amount,
      String status,
      String failureReason) {}

  public record TransferUpdateResponse(String step, String status, String message) {}

  private final ComponentClient componentClient;

  public TransferWorkflowEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/{transferId}")
  public HttpResponse start(String transferId, StartTransferRequest request) {
    try {
      var state =
          componentClient
              .forWorkflow(transferId)
              .method(TransferWorkflow::start)
              .invoke(
                  new TransferWorkflow.StartTransfer(
                      request.fromWalletId(), request.toWalletId(), request.amount()));
      return HttpResponses.created(toApi(transferId, state));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Get("/{transferId}")
  public HttpResponse get(String transferId) {
    try {
      var state = componentClient.forWorkflow(transferId).method(TransferWorkflow::get).invoke();
      return HttpResponses.ok(toApi(transferId, state));
    } catch (CommandException error) {
      return HttpResponses.notFound(error.getMessage());
    }
  }

  @Get("/{transferId}/updates")
  public HttpResponse updates(String transferId) {
    var source =
        componentClient
            .forWorkflow(transferId)
            .notificationStream(TransferWorkflow::updates)
            .source()
            .map(TransferWorkflowEndpoint::toApi);
    return HttpResponses.serverSentEvents(source);
  }

  private static TransferResponse toApi(String transferId, TransferState state) {
    return new TransferResponse(
        transferId,
        state.fromWalletId(),
        state.toWalletId(),
        state.amount(),
        state.status().name(),
        state.failureReason());
  }

  private static TransferUpdateResponse toApi(TransferWorkflow.TransferUpdate update) {
    return new TransferUpdateResponse(update.step(), update.status(), update.message());
  }
}
