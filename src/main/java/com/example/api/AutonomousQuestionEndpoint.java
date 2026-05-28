package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.AnswerQuestionAutonomousAgent;
import com.example.application.AnswerQuestionTasks;
import java.util.UUID;

/** HTTP endpoint example that starts a single Autonomous Agent task. */
@HttpEndpoint("/autonomous/questions")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class AutonomousQuestionEndpoint {

  public record AskQuestion(String question) {}

  public record QuestionTaskResponse(String taskId, String agentInstanceId, String agentComponentId) {}

  private final ComponentClient componentClient;

  public AutonomousQuestionEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post
  public HttpResponse ask(AskQuestion request) {
    if (request == null || request.question() == null || request.question().isBlank()) {
      return HttpResponses.badRequest("question must not be blank");
    }

    var agentInstanceId = UUID.randomUUID().toString();
    var taskId =
        componentClient
            .forAutonomousAgent(AnswerQuestionAutonomousAgent.class, agentInstanceId)
            .runSingleTask(AnswerQuestionTasks.ANSWER.instructions(request.question()));

    return HttpResponses.ok(
        new QuestionTaskResponse(taskId, agentInstanceId, "answer-question-autonomous-agent"));
  }
}
