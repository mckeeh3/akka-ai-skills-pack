package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.TeamLeadershipReviewTasks;
import com.example.application.TeamReviewLeadAutonomousAgent;
import java.util.UUID;

/** HTTP endpoint example that starts an Autonomous Agent TeamLeadership task. */
@HttpEndpoint("/autonomous/team-leadership")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class AutonomousTeamLeadershipEndpoint {

  public record TeamReviewRequest(String subject) {}

  public record TeamReviewTaskResponse(
      String taskId, String agentInstanceId, String agentComponentId) {}

  private final ComponentClient componentClient;

  public AutonomousTeamLeadershipEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post
  public HttpResponse start(TeamReviewRequest request) {
    if (request == null || request.subject() == null || request.subject().isBlank()) {
      return HttpResponses.badRequest("subject must not be blank");
    }

    var agentInstanceId = UUID.randomUUID().toString();
    var taskId =
        componentClient
            .forAutonomousAgent(TeamReviewLeadAutonomousAgent.class, agentInstanceId)
            .runSingleTask(
                TeamLeadershipReviewTasks.TEAM_REVIEW.instructions(
                    "Lead a shared-backlog team review for: " + request.subject()));

    return HttpResponses.ok(
        new TeamReviewTaskResponse(
            taskId, agentInstanceId, "team-review-lead-autonomous-agent"));
  }
}
