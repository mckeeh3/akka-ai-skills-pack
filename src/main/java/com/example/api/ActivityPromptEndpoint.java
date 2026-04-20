package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.agent.PromptTemplate;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.Get;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Put;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.TemplateBackedActivityAgent;

/** HTTP endpoint example for managing PromptTemplate values used by an agent. */
@HttpEndpoint("/agent-prompts")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class ActivityPromptEndpoint {

  public record PromptRequest(String prompt) {}

  public record PromptResponse(String prompt) {}

  private final ComponentClient componentClient;

  public ActivityPromptEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Put("/activity")
  public HttpResponse updateActivityPrompt(PromptRequest request) {
    if (request == null || request.prompt() == null || request.prompt().isBlank()) {
      return HttpResponses.badRequest("prompt must not be blank");
    }

    componentClient
        .forEventSourcedEntity(TemplateBackedActivityAgent.PROMPT_TEMPLATE_ID)
        .method(PromptTemplate::update)
        .invoke(request.prompt());

    return HttpResponses.ok(new PromptResponse(request.prompt()));
  }

  @Get("/activity")
  public HttpResponse getActivityPrompt() {
    var prompt =
        componentClient
            .forEventSourcedEntity(TemplateBackedActivityAgent.PROMPT_TEMPLATE_ID)
            .method(PromptTemplate::getOptional)
            .invoke();

    return prompt
        .<HttpResponse>map(value -> HttpResponses.ok(new PromptResponse(value)))
        .orElseGet(() -> HttpResponses.notFound("prompt not initialized"));
  }
}
