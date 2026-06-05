package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.CommandException;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.ApprovalDeadlineWorkflow;
import com.example.domain.ApprovalDeadlineState;

/** HTTP endpoint for the workflow-triggered timer example. */
@HttpEndpoint("/approval-deadlines")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class ApprovalDeadlineWorkflowEndpoint {

  public record StartApprovalRequest(String documentId, String requestedBy, int timeoutSeconds) {}

  public record ApproveRequest(String approvedBy, String comment) {}

  public record ApprovalDeadlineResponse(
      String approvalId,
      String documentId,
      String requestedBy,
      int timeoutSeconds,
      String status,
      String approvedBy,
      String comment) {}

  public record StatusResponse(String status) {}

  private final ComponentClient componentClient;

  public ApprovalDeadlineWorkflowEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/{approvalId}")
  public HttpResponse start(String approvalId, StartApprovalRequest request) {
    try {
      var state =
          componentClient
              .forWorkflow(approvalId)
              .method(ApprovalDeadlineWorkflow::start)
              .invoke(
                  new ApprovalDeadlineWorkflow.StartApproval(
                      request.documentId(), request.requestedBy(), request.timeoutSeconds()));
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
              .method(ApprovalDeadlineWorkflow::approve)
              .invoke(new ApprovalDeadlineWorkflow.Approve(request.approvedBy(), request.comment()));
      return HttpResponses.ok(new StatusResponse(status));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Get("/{approvalId}")
  public HttpResponse get(String approvalId) {
    try {
      var state = componentClient.forWorkflow(approvalId).method(ApprovalDeadlineWorkflow::get).invoke();
      return HttpResponses.ok(toApi(approvalId, state));
    } catch (CommandException error) {
      return HttpResponses.notFound(error.getMessage());
    }
  }

  private static ApprovalDeadlineResponse toApi(String approvalId, ApprovalDeadlineState state) {
    return new ApprovalDeadlineResponse(
        approvalId,
        state.documentId(),
        state.requestedBy(),
        state.timeoutSeconds(),
        state.status().name(),
        state.approvedBy(),
        state.comment());
  }
}
