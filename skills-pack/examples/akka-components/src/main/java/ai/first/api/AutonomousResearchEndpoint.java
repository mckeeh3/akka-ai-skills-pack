package ai.first.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import ai.first.application.ResearchCoordinationTasks;
import ai.first.application.ResearchCoordinatorAutonomousAgent;
import java.util.UUID;

/** HTTP endpoint example that starts an Autonomous Agent delegation task. */
@HttpEndpoint("/autonomous/research")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class AutonomousResearchEndpoint {

  public record ResearchRequest(String topic) {}

  public record ResearchTaskResponse(
      String taskId, String agentInstanceId, String agentComponentId) {}

  private final ComponentClient componentClient;

  public AutonomousResearchEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post
  public HttpResponse start(ResearchRequest request) {
    if (request == null || request.topic() == null || request.topic().isBlank()) {
      return HttpResponses.badRequest("topic must not be blank");
    }

    var agentInstanceId = UUID.randomUUID().toString();
    var taskId =
        componentClient
            .forAutonomousAgent(ResearchCoordinatorAutonomousAgent.class, agentInstanceId)
            .runSingleTask(
                ResearchCoordinationTasks.BRIEF.instructions(
                    "Create a concise research brief about: " + request.topic()));

    return HttpResponses.ok(
        new ResearchTaskResponse(
            taskId, agentInstanceId, "research-coordinator-autonomous-agent"));
  }
}
