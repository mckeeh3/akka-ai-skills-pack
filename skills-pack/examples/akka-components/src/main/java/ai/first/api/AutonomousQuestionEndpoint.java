package ai.first.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.agent.task.TaskNotification;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import ai.first.application.AnswerQuestionAutonomousAgent;
import ai.first.application.AnswerQuestionTasks;
import java.util.UUID;

/** HTTP endpoint example that starts a single Autonomous Agent task. */
@HttpEndpoint("/autonomous/questions")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class AutonomousQuestionEndpoint {

  public record AskQuestion(String question) {}

  public record QuestionTaskResponse(String taskId, String agentInstanceId, String agentComponentId) {}

  public record TaskNotificationEvent(String taskId, String taskName, String status, String detail) {}

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

  /**
   * Streams terminal task notifications for progress UIs.
   *
   * <p>Reference-only authorization note: generated apps must protect this route with tenant-scoped
   * task lookup, backend authorization, and notification exposure audit. Notifications are not the
   * source of truth; callers must query the task snapshot/result before making business decisions.
   */
  @Get("/tasks/{taskId}/notifications")
  public HttpResponse taskNotifications(String taskId) {
    var source = componentClient.forTask(taskId).notificationStream().map(AutonomousQuestionEndpoint::toApi);
    return HttpResponses.serverSentEvents(source, event -> event.taskId(), __ -> "task-notification");
  }

  private static TaskNotificationEvent toApi(TaskNotification notification) {
    return switch (notification) {
      case TaskNotification.Completed completed ->
          new TaskNotificationEvent(
              completed.taskId(), completed.taskName(), "COMPLETED", completed.result());
      case TaskNotification.Failed failed ->
          new TaskNotificationEvent(failed.taskId(), failed.taskName(), "FAILED", failed.reason());
      case TaskNotification.Cancelled cancelled ->
          new TaskNotificationEvent(
              cancelled.taskId(), cancelled.taskName(), "CANCELLED", cancelled.reason());
      case TaskNotification.ResultRejected rejected ->
          new TaskNotificationEvent(
              rejected.taskId(),
              rejected.taskName(),
              "RESULT_REJECTED",
              rejected.ruleClassName() + ": " + rejected.reason());
    };
  }
}
