package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.ReviewModerationTasks;
import com.example.application.ReviewModeratorAutonomousAgent;
import java.util.UUID;

/** HTTP endpoint example that starts an Autonomous Agent moderation task. */
@HttpEndpoint("/autonomous/review-moderation")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class AutonomousReviewModerationEndpoint {

  public record ReviewRequest(String subject) {}

  public record ReviewTaskResponse(
      String taskId, String agentInstanceId, String agentComponentId) {}

  private final ComponentClient componentClient;

  public AutonomousReviewModerationEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post
  public HttpResponse start(ReviewRequest request) {
    if (request == null || request.subject() == null || request.subject().isBlank()) {
      return HttpResponses.badRequest("subject must not be blank");
    }

    var agentInstanceId = UUID.randomUUID().toString();
    var taskId =
        componentClient
            .forAutonomousAgent(ReviewModeratorAutonomousAgent.class, agentInstanceId)
            .runSingleTask(
                ReviewModerationTasks.REVIEW.instructions(
                    "Moderate a concise technical and policy review for: " + request.subject()));

    return HttpResponses.ok(
        new ReviewTaskResponse(
            taskId, agentInstanceId, "review-moderator-autonomous-agent"));
  }
}
