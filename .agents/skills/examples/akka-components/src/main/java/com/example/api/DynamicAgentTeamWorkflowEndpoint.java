package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.CommandException;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.DynamicAgentTeamWorkflow;

/** HTTP endpoint example for starting and querying a dynamically planned multi-agent workflow. */
@HttpEndpoint("/dynamic-agent-team")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class DynamicAgentTeamWorkflowEndpoint {

  public record StartRequest(String query) {}

  public record WorkflowResponse(String workflowId, String answer) {}

  private final ComponentClient componentClient;

  public DynamicAgentTeamWorkflowEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/{workflowId}")
  public HttpResponse start(String workflowId, StartRequest request) {
    if (request == null || request.query() == null || request.query().isBlank()) {
      return HttpResponses.badRequest("query must not be blank");
    }

    try {
      componentClient.forWorkflow(workflowId).method(DynamicAgentTeamWorkflow::start).invoke(request.query());
      return HttpResponses.created(new WorkflowResponse(workflowId, ""));
    } catch (CommandException error) {
      return HttpResponses.badRequest(error.getMessage());
    }
  }

  @Get("/{workflowId}")
  public HttpResponse get(String workflowId) {
    try {
      var answer =
          componentClient.forWorkflow(workflowId).method(DynamicAgentTeamWorkflow::getAnswer).invoke();
      return HttpResponses.ok(new WorkflowResponse(workflowId, answer));
    } catch (CommandException error) {
      return HttpResponses.notFound(error.getMessage());
    }
  }
}
