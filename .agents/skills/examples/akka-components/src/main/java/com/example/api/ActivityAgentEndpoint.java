package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.ActivityAgent;
import com.example.application.StreamingActivityAgent;

/** HTTP endpoint example for direct and streaming agent calls. */
@HttpEndpoint("/agents")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class ActivityAgentEndpoint {

  public record AskRequest(String sessionId, String question) {}

  public record ActivitySuggestionResponse(String name, String reason, String setting) {}

  public record AskResponse(String sessionId, ActivitySuggestionResponse suggestion) {}

  private final ComponentClient componentClient;

  public ActivityAgentEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/activity/ask")
  public HttpResponse ask(AskRequest request) {
    var validationError = validate(request);
    if (validationError != null) {
      return HttpResponses.badRequest(validationError);
    }

    var suggestion =
        componentClient
            .forAgent()
            .inSession(request.sessionId())
            .method(ActivityAgent::suggest)
            .invoke(request.question());

    return HttpResponses.ok(
        new AskResponse(
            request.sessionId(),
            new ActivitySuggestionResponse(
                suggestion.name(), suggestion.reason(), suggestion.setting())));
  }

  @Post("/activity/stream")
  public HttpResponse stream(AskRequest request) {
    var validationError = validate(request);
    if (validationError != null) {
      return HttpResponses.badRequest(validationError);
    }

    var tokenSource =
        componentClient
            .forAgent()
            .inSession(request.sessionId())
            .tokenStream(StreamingActivityAgent::suggest)
            .source(request.question());

    return HttpResponses.streamText(tokenSource);
  }

  private static String validate(AskRequest request) {
    if (request == null) {
      return "request must not be null";
    } else if (request.sessionId() == null || request.sessionId().isBlank()) {
      return "sessionId must not be blank";
    } else if (request.question() == null || request.question().isBlank()) {
      return "question must not be blank";
    }
    return null;
  }
}
