package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.ApprovalPipelineAutonomousAgent;
import com.example.application.ApprovalPipelineTasks;
import java.util.UUID;

/** HTTP endpoint example for Autonomous Agent task dependencies and external approval. */
@HttpEndpoint("/autonomous/approval-pipeline")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class AutonomousApprovalPipelineEndpoint {

  public record StartApprovalPipeline(String topic) {}

  public record ApprovalPipelineResponse(
      String investigationTaskId,
      String approvalTaskId,
      String publishTaskId,
      String investigationAgentInstanceId,
      String publishAgentInstanceId,
      String agentComponentId) {}

  public record ApproveRequest(String approvedBy, String comment, String publishTaskId, String publishAgentId) {}

  public record RejectRequest(String rejectedBy, String reason) {}

  private final ComponentClient componentClient;

  public AutonomousApprovalPipelineEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post
  public HttpResponse start(StartApprovalPipeline request) {
    if (request == null || request.topic() == null || request.topic().isBlank()) {
      return HttpResponses.badRequest("topic must not be blank");
    }

    var investigationAgentId = UUID.randomUUID().toString();
    var investigationTaskId =
        componentClient
            .forAutonomousAgent(ApprovalPipelineAutonomousAgent.class, investigationAgentId)
            .runSingleTask(
                ApprovalPipelineTasks.INVESTIGATE.instructions(
                    "Investigate approval-gated request: " + request.topic()));

    var approvalTaskId = UUID.randomUUID().toString();
    componentClient
        .forTask(approvalTaskId)
        .create(
            ApprovalPipelineTasks.APPROVAL
                .instructions("Review investigation before approving: " + request.topic())
                .dependsOn(investigationTaskId));

    var publishAgentId = UUID.randomUUID().toString();
    var publishTaskId = UUID.randomUUID().toString();
    componentClient
        .forTask(publishTaskId)
        .create(
            ApprovalPipelineTasks.PUBLISH
                .instructions("Publish approved request after external approval: " + request.topic())
                .dependsOn(approvalTaskId));

    return HttpResponses.ok(
        new ApprovalPipelineResponse(
            investigationTaskId,
            approvalTaskId,
            publishTaskId,
            investigationAgentId,
            publishAgentId,
            "approval-pipeline-autonomous-agent"));
  }

  @Post("/approve/{approvalTaskId}")
  public HttpResponse approve(String approvalTaskId, ApproveRequest request) {
    if (request == null || request.approvedBy() == null || request.approvedBy().isBlank()) {
      return HttpResponses.badRequest("approvedBy must not be blank");
    }

    componentClient.forTask(approvalTaskId).assign(request.approvedBy());
    componentClient
        .forTask(approvalTaskId)
        .complete(
            ApprovalPipelineTasks.APPROVAL,
            new ApprovalPipelineTasks.ApprovalDecision(
                request.approvedBy(), "approved", request.comment()));

    if (request.publishTaskId() != null
        && !request.publishTaskId().isBlank()
        && request.publishAgentId() != null
        && !request.publishAgentId().isBlank()) {
      componentClient
          .forAutonomousAgent(ApprovalPipelineAutonomousAgent.class, request.publishAgentId())
          .assignTasks(request.publishTaskId());
    }

    return HttpResponses.ok("Approved");
  }

  @Post("/reject/{approvalTaskId}")
  public HttpResponse reject(String approvalTaskId, RejectRequest request) {
    if (request == null || request.rejectedBy() == null || request.rejectedBy().isBlank()) {
      return HttpResponses.badRequest("rejectedBy must not be blank");
    }
    if (request.reason() == null || request.reason().isBlank()) {
      return HttpResponses.badRequest("reason must not be blank");
    }

    componentClient.forTask(approvalTaskId).assign(request.rejectedBy());
    componentClient.forTask(approvalTaskId).fail(request.reason());

    return HttpResponses.ok("Rejected");
  }
}
