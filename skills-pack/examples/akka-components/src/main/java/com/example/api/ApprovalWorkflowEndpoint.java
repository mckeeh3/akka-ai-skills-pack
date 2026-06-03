package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.CommandException;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.ApprovalWorkflow;
import com.example.domain.ApprovalState;

/** HTTP endpoint example for a paused approval workflow. */
@HttpEndpoint("/approvals")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class ApprovalWorkflowEndpoint {

  public record StartApprovalRequest(String documentId, String requestedBy) {}

  public record ApproveRequest(String approvedBy, String comment) {}

  public record ApprovalResponse(
      String approvalId,
      String documentId,
      String requestedBy,
      String status,
      String approvedBy,
      String comment) {}

  public record StatusResponse(String status) {}

  public record ApprovalUpdateResponse(String step, String status, String message) {}

  private final ComponentClient componentClient;

  public ApprovalWorkflowEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/{approvalId}")
  public HttpResponse start(String approvalId, StartApprovalRequest request) {
    try {
      var state =
          componentClient
              .forWorkflow(approvalId)
              .method(ApprovalWorkflow::start)
              .invoke(new ApprovalWorkflow.StartApproval(request.documentId(), request.requestedBy()));
      return HttpResponses.created(toApi(approvalId, state));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Post("/{approvalId}/approve")
  public HttpResponse approve(String approvalId, ApproveRequest request) {
    try {
      var status =
          componentClient
              .forWorkflow(approvalId)
              .method(ApprovalWorkflow::approve)
              .invoke(new ApprovalWorkflow.Approve(request.approvedBy(), request.comment()));
      return HttpResponses.ok(new StatusResponse(status));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Get("/{approvalId}")
  public HttpResponse get(String approvalId) {
    try {
      var state = componentClient.forWorkflow(approvalId).method(ApprovalWorkflow::get).invoke();
      return HttpResponses.ok(toApi(approvalId, state));
    } catch (CommandException error) {
      return HttpResponses.notFound(error.getMessage());
    }
  }

  @Get("/{approvalId}/updates")
  public HttpResponse updates(String approvalId) {
    var source =
        componentClient
            .forWorkflow(approvalId)
            .notificationStream(ApprovalWorkflow::updates)
            .source()
            .map(ApprovalWorkflowEndpoint::toApi);
    return HttpResponses.serverSentEvents(source);
  }

  private static ApprovalResponse toApi(String approvalId, ApprovalState state) {
    return new ApprovalResponse(
        approvalId,
        state.documentId(),
        state.requestedBy(),
        state.status().name(),
        state.approvedBy(),
        state.comment());
  }

  private static ApprovalUpdateResponse toApi(ApprovalWorkflow.ApprovalUpdate update) {
    return new ApprovalUpdateResponse(update.step(), update.status(), update.message());
  }
}
