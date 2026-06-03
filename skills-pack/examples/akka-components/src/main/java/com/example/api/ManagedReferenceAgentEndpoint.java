package com.example.api;

import akka.http.javadsl.model.HttpResponse;
import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;
import akka.javasdk.http.HttpResponses;
import com.example.application.agentfoundation.ManagedReferenceActivityAgent;
import com.example.application.agentfoundation.ReferenceAgentFoundationSeed;
import com.example.application.agentfoundation.ReferenceAgentSkillTools;
import com.example.application.agentfoundation.ReferenceTraceSink;
import com.example.domain.agentfoundation.ReferenceAgentWorkTrace;
import com.example.domain.agentfoundation.ReferenceResolvedAgentRuntime;
import java.util.Set;

/** Narrow managed-agent test-console reference endpoint for governed runtime invocation. */
@HttpEndpoint("/agentfoundation/managed-reference-agent")
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.ALL))
public class ManagedReferenceAgentEndpoint {

  public record InvokeRequest(
      String tenantId,
      String accountId,
      Set<String> capabilityIds,
      String agentDefinitionId,
      String correlationId,
      String message,
      String loadSkillId) {}

  public record InvokeResponse(
      boolean allowed,
      String denialReason,
      String correlationId,
      String agentDefinitionId,
      ActivitySuggestionResponse suggestion,
      int promptAssemblyTraceCount,
      int skillLoadTraceCount,
      int agentWorkTraceCount,
      String loadedSkillId,
      boolean skillLoadAllowed) {}

  public record ActivitySuggestionResponse(String name, String reason, String setting) {}

  private final ComponentClient componentClient;

  public ManagedReferenceAgentEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/invoke")
  public HttpResponse invoke(InvokeRequest request) {
    var validationError = validate(request);
    if (validationError != null) {
      return HttpResponses.badRequest(validationError);
    }

    var traceSink = new ReferenceTraceSink();
    var authContext =
        ReferenceAgentFoundationSeed.authContext(
            request.tenantId(), request.accountId(), request.capabilityIds(), "runtime");
    var runtime =
        ReferenceAgentFoundationSeed.resolver(traceSink)
            .resolve(authContext, request.agentDefinitionId(), request.correlationId());

    var loadedSkillId = "";
    var skillLoadAllowed = false;
    if (runtime.allowed() && request.loadSkillId() != null && !request.loadSkillId().isBlank()) {
      var tools =
          new ReferenceAgentSkillTools(
              runtime, ReferenceAgentFoundationSeed.skillReadAuthorizer(traceSink));
      var loadedSkill = tools.readSkill(request.loadSkillId());
      loadedSkillId = request.loadSkillId();
      skillLoadAllowed = !loadedSkill.contains("Skill unavailable");
    }

    var suggestion = invokeManaged(runtime, request.message(), traceSink);
    return HttpResponses.ok(
        new InvokeResponse(
            runtime.allowed(),
            runtime.denialReason(),
            request.correlationId(),
            request.agentDefinitionId(),
            new ActivitySuggestionResponse(suggestion.name(), suggestion.reason(), suggestion.setting()),
            traceSink.promptAssemblyTraces().size(),
            traceSink.skillLoadTraces().size(),
            traceSink.agentWorkTraces().size(),
            loadedSkillId,
            skillLoadAllowed));
  }

  private ManagedReferenceActivityAgent.ManagedActivitySuggestion invokeManaged(
      ReferenceResolvedAgentRuntime runtime, String message, ReferenceTraceSink traceSink) {
    if (!runtime.allowed()) {
      traceSink.recordAgentWork(
          new ReferenceAgentWorkTrace(
              runtime.authContext().tenantId(),
              runtime.agentDefinition() == null ? "" : runtime.agentDefinition().agentDefinitionId(),
              runtime.correlationId(),
              runtime.assembledPromptChecksum(),
              "",
              runtime.authContext().mode(),
              false,
              runtime.denialReason()));
      return new ManagedReferenceActivityAgent.ManagedActivitySuggestion(
          "Denied", runtime.denialReason(), "not_applicable");
    }

    var result =
        componentClient
            .forAgent()
            .inSession(runtime.correlationId())
            .method(ManagedReferenceActivityAgent::suggest)
            .invoke(
                new ManagedReferenceActivityAgent.ManagedActivityRequest(
                    runtime.assembledPrompt(), message, runtime.correlationId()));
    var lastSkillTrace =
        traceSink.skillLoadTraces().isEmpty() ? null : traceSink.skillLoadTraces().getLast();
    traceSink.recordAgentWork(
        new ReferenceAgentWorkTrace(
            runtime.authContext().tenantId(),
            runtime.agentDefinition().agentDefinitionId(),
            runtime.correlationId(),
            runtime.assembledPromptChecksum(),
            lastSkillTrace == null ? "" : lastSkillTrace.resolvedSkillVersionId(),
            runtime.authContext().mode(),
            true,
            "ManagedReferenceActivityAgent endpoint returned " + result.name()));
    return result;
  }

  private static String validate(InvokeRequest request) {
    if (request == null) {
      return "request must not be null";
    } else if (blank(request.tenantId())) {
      return "tenantId must not be blank";
    } else if (blank(request.accountId())) {
      return "accountId must not be blank";
    } else if (blank(request.agentDefinitionId())) {
      return "agentDefinitionId must not be blank";
    } else if (blank(request.correlationId())) {
      return "correlationId must not be blank";
    } else if (blank(request.message())) {
      return "message must not be blank";
    }
    return null;
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }
}
