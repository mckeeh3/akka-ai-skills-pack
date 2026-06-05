package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.HandoffTriageTasks;
import com.example.application.SupportTriageAutonomousAgent;
import java.util.UUID;

/** HTTP endpoint example that starts an Autonomous Agent handoff triage task. */
@HttpEndpoint("/autonomous/support-triage")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class AutonomousSupportTriageEndpoint {

  public record SupportRequest(String description) {}

  public record SupportTaskResponse(String taskId, String agentInstanceId, String agentComponentId) {}

  private final ComponentClient componentClient;

  public AutonomousSupportTriageEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post
  public HttpResponse start(SupportRequest request) {
    if (request == null || request.description() == null || request.description().isBlank()) {
      return HttpResponses.badRequest("description must not be blank");
    }

    var agentInstanceId = UUID.randomUUID().toString();
    var taskId =
        componentClient
            .forAutonomousAgent(SupportTriageAutonomousAgent.class, agentInstanceId)
            .runSingleTask(
                HandoffTriageTasks.RESOLVE.instructions(
                    "Triage and resolve this support request: " + request.description()));

    return HttpResponses.ok(
        new SupportTaskResponse(
            taskId, agentInstanceId, "support-triage-autonomous-agent"));
  }
}
