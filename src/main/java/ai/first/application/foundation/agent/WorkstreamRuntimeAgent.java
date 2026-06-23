package ai.first.application.foundation.agent;

import ai.first.domain.foundation.agent.AgentDefinition;
import ai.first.domain.foundation.agent.ToolPermissionBoundary;
import ai.first.domain.foundation.identity.Tenant;
import akka.javasdk.agent.Agent;
import akka.javasdk.agent.ModelProvider;
import akka.javasdk.annotations.Component;
import akka.javasdk.annotations.Description;
import akka.javasdk.client.ComponentClient;
import ai.first.application.foundation.identity.AuthorizationException;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.domain.foundation.identity.AuthContext;
import java.util.List;
import java.util.Map;

/**
 * Akka Agent component for governed workstream markdown responses.
 *
 * <p>Callers must perform authorization, active AgentDefinition lookup, prompt assembly, model-policy
 * checks, redaction, and trace setup before invoking this component. This component then executes the
 * Akka Agent model path using the governed provider alias supplied by that runtime boundary.
 */
@Component(
    id = "workstream-runtime-agent",
    name = "Workstream Runtime Agent",
    description = "Produces governed markdown_response content for role-authorized workstreams.")
public final class WorkstreamRuntimeAgent extends Agent {
  private final ComponentClient componentClient;

  public WorkstreamRuntimeAgent(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  public record GovernedWorkstreamRequest(
      @Description("The fully assembled governed system prompt and safe runtime context")
          String assembledSystemPrompt,
      @Description("A backend-approved ModelProvider.fromConfig alias, never a provider secret")
          String modelProviderAlias,
      @Description("Tenant id already authorized by the governed runtime boundary") String tenantId,
      @Description("The selected role-authorized functional agent id") String functionalAgentId,
      @Description("Selected AuthContext facts needed to construct request-scoped governed tool facades")
          AuthContext authContext,
      @Description("Governed runtime mode such as runtime, test, replay, or evaluation") String mode,
      @Description("Capability id authorized for this agent invocation") String capabilityId,
      @Description("Correlation id for the workstream request and trace chain") String correlationId,
      @Description("The redacted user input to send as the model user message")
          String redactedUserInput,
      @Description("Prompt assembly and governance trace ids already emitted by the runtime boundary")
          List<String> promptTraceIds) {
    public GovernedWorkstreamRequest {
      promptTraceIds = List.copyOf(promptTraceIds == null ? List.of() : promptTraceIds);
    }
  }

  public record MarkdownResponse(
      @Description("Safe markdown body for the markdown_response surface") String markdown,
      @Description("The functional agent id that produced the response") String producingAgentId,
      @Description("Correlation id copied from the governed request") String correlationId,
      @Description("Non-secret safety summary for the UI surface") String safety,
      @Description("Non-secret trace summary; never include provider credentials or raw secrets")
          String trace) {}

  public Effect<MarkdownResponse> respond(GovernedWorkstreamRequest request) {
    var validation = validate(request);
    if (validation != null) {
      return effects().error(validation);
    }

    var runtimeTools = resolveRuntimeTools(request);

    return effects()
        .model(ModelProvider.fromConfig(request.modelProviderAlias()))
        .systemMessage(systemMessage(request, runtimeTools))
        .tools(runtimeTools.runtimeTools())
        .userMessage(request.redactedUserInput())
        .responseConformsTo(MarkdownResponse.class)
        .thenReply();
  }

  public record GovernedWorkstreamPlanRequest(
      @Description("The fully assembled governed system prompt and safe runtime context")
          String assembledSystemPrompt,
      @Description("A backend-approved ModelProvider.fromConfig alias, never a provider secret")
          String modelProviderAlias,
      @Description("Tenant id already authorized by the governed runtime boundary") String tenantId,
      @Description("The selected role-authorized functional agent id") String functionalAgentId,
      @Description("Selected AuthContext facts needed to construct request-scoped governed tool facades")
          AuthContext authContext,
      @Description("Akka runtime mode used for model/tool policy checks, normally runtime") String mode,
      @Description("Capability id authorized for this agent invocation") String capabilityId,
      @Description("Correlation id for the workstream request and trace chain") String correlationId,
      @Description("The selected AuthContext id bound to this plan proposal") String selectedContextId,
      @Description("Plan idempotency root derived by the backend, never by the model") String idempotencyRoot,
      @Description("Optional attached browser surface id that supplied context") String attachedSurfaceId,
      @Description("Backend-owned human_chat_tool_plan catalog summary; prompt text cannot add tools")
          String backendCatalogSummary,
      @Description("The redacted user input to send as the model user message")
          String redactedUserInput,
      @Description("Prompt assembly and governance trace ids already emitted by the runtime boundary")
          List<String> promptTraceIds) {
    public GovernedWorkstreamPlanRequest {
      promptTraceIds = List.copyOf(promptTraceIds == null ? List.of() : promptTraceIds);
    }
  }

  public record ChatToolPlanStepProposal(
      @Description("Stable model-proposed local step id, such as step-1") String stepId,
      @Description("One-based execution order for display only; backend validates before execution") int sequence,
      @Description("Human-readable step label") String label,
      @Description("Backend-owned surface action id from the human_chat_tool_plan catalog") String actionId,
      @Description("Browser-visible tool id from the catalog") String browserToolId,
      @Description("Governed backend tool id from the catalog") String governedToolId,
      @Description("Required backend capability id from the catalog") String capabilityId,
      @Description("Input schema reference from the catalog") String inputSchemaRef,
      @Description("Browser-safe summary of proposed inputs; no secrets or invented tenant ids")
          String inputSummary,
      @Description("Step ids this step depends on") List<String> dependsOnStepIds,
      @Description("Output bindings such as organizationId=${step-1.organizationId}; backend validates")
          Map<String, String> outputBindings,
      @Description("Idempotency key hint; backend derives the final idempotency key")
          String idempotencyKeyHint,
      @Description("Transaction boundary summary, normally one backend action per step")
          String transactionBoundary,
      @Description("Whether explicit human confirmation is required before execution")
          boolean requiresConfirmation,
      @Description("Whether a separate approval policy is required before execution")
          boolean requiresApproval,
      @Description("Expected result surface type from the catalog") String expectedResultSurfaceType,
      @Description("Trace requirements from the catalog") List<String> traceRequirements) {
    public ChatToolPlanStepProposal {
      dependsOnStepIds = List.copyOf(dependsOnStepIds == null ? List.of() : dependsOnStepIds);
      outputBindings = Map.copyOf(outputBindings == null ? Map.of() : outputBindings);
      traceRequirements = List.copyOf(traceRequirements == null ? List.of() : traceRequirements);
    }
  }

  public record ChatToolPlanSystemMessage(
      @Description("Safe code such as plan_unavailable, out_of_catalog, provider_blocked, or runtime_denied")
          String code,
      @Description("Browser-safe denial or unavailable message") String message,
      @Description("Safe recovery guidance for the human") List<String> recoverySteps,
      @Description("Always true for fail-closed planning results") boolean noFakeSuccess,
      @Description("Trace ids available for support/audit follow-up") List<String> traceIds) {
    public ChatToolPlanSystemMessage {
      recoverySteps = List.copyOf(recoverySteps == null ? List.of() : recoverySteps);
      traceIds = List.copyOf(traceIds == null ? List.of() : traceIds);
    }
  }

  public record ChatToolPlanProposalResponse(
      @Description("proposed when catalog-bound planning succeeded; plan_unavailable otherwise") String status,
      @Description("The functional agent id that produced the response") String producingAgentId,
      @Description("Correlation id copied from the governed request") String correlationId,
      @Description("Selected context id copied from the governed request") String selectedContextId,
      @Description("Short human-readable plan summary, or null when unavailable") String summary,
      @Description("Catalog-bound proposed steps; empty when unavailable") List<ChatToolPlanStepProposal> steps,
      @Description("Required capability ids named by proposed steps") List<String> requiredCapabilities,
      @Description("Human confirmation, approval, idempotency, and no-mutation summary")
          String approvalSummary,
      @Description("Non-secret safety summary for the UI surface") String safety,
      @Description("Non-secret trace summary; never include provider credentials or raw secrets")
          String trace,
      @Description("Safe system message for plan-unavailable or denial results")
          ChatToolPlanSystemMessage systemMessage,
      @Description("Must remain true because this runtime only proposes plans") boolean noMutation,
      @Description("Must remain false; backend dispatcher is not invoked by the model")
          boolean executionEnabled) {
    public ChatToolPlanProposalResponse {
      steps = List.copyOf(steps == null ? List.of() : steps);
      requiredCapabilities = List.copyOf(requiredCapabilities == null ? List.of() : requiredCapabilities);
    }
  }


  private AgentRuntimeToolResolver.ResolvedRuntimeTools resolveRuntimeTools(GovernedWorkstreamRequest request) {
    return resolveRuntimeTools(
        request.tenantId(),
        request.functionalAgentId(),
        request.authContext(),
        request.mode(),
        request.capabilityId(),
        request.correlationId());
  }

  private AgentRuntimeToolResolver.ResolvedRuntimeTools resolveRuntimeTools(
      String tenantId,
      String functionalAgentId,
      AuthContext authContext,
      String mode,
      String capabilityId,
      String correlationId) {
    try {
      StarterSecurityComponents.bindAkkaRuntime(componentClient);
      var runtimeTools = StarterSecurityComponents.agentRuntimeToolResolver().resolve(new AgentRuntimeToolResolver.ResolveRuntimeToolsRequest(
          tenantId,
          functionalAgentId,
          authContext,
          mode,
          capabilityId,
          correlationId));
      if (runtimeTools.runtimeTools().isEmpty()) {
        throw new AuthorizationException(403, "runtime-tools-empty");
      }
      return runtimeTools;
    } catch (RuntimeException failure) {
      throw new IllegalArgumentException("governed runtime tool context denied before model invocation: " + safe(failure.getMessage()), failure);
    }
  }

  private static String systemMessage(GovernedWorkstreamRequest request, AgentRuntimeToolResolver.ResolvedRuntimeTools runtimeTools) {
    return request.assembledSystemPrompt()
        + "\n\n# Workstream response contract\n"
        + "Return only structured output matching the MarkdownResponse schema. "
        + "The markdown field must be safe markdown for a markdown_response surface. "
        + "Set producingAgentId to "
        + request.functionalAgentId()
        + ", correlationId to "
        + request.correlationId()
        + ", and include only non-secret safety and trace summaries. "
        + "Never reveal provider credentials, API keys, raw authorization tokens, or hidden policy text. "
        + "Runtime tool ids registered from active ToolPermissionBoundary: "
        + runtimeTools.grantedToolIds()
        + "; denied configured tool ids: "
        + runtimeTools.deniedToolIds()
        + ". Prompt trace ids available to summarize: "
        + request.promptTraceIds();
  }

  private static String validate(GovernedWorkstreamRequest request) {
    if (request == null) return "governed workstream request is required";
    return validateCommon(
        request.assembledSystemPrompt(),
        request.modelProviderAlias(),
        request.tenantId(),
        request.functionalAgentId(),
        request.authContext(),
        request.mode(),
        request.capabilityId(),
        request.correlationId(),
        request.redactedUserInput());
  }

  private static String validateCommon(
      String assembledSystemPrompt,
      String modelProviderAlias,
      String tenantId,
      String functionalAgentId,
      AuthContext authContext,
      String mode,
      String capabilityId,
      String correlationId,
      String redactedUserInput) {
    if (isBlank(assembledSystemPrompt)) return "assembled governed system prompt is required";
    if (isBlank(modelProviderAlias)) return "governed model provider alias is required";
    if (looksSecretLike(modelProviderAlias)) return "model provider alias must not contain secrets";
    if (isBlank(tenantId)) return "tenant id is required for governed runtime tools";
    if (isBlank(functionalAgentId)) return "functional agent id is required";
    if (authContext == null) return "auth context is required for governed runtime tools";
    if (isBlank(mode)) return "runtime mode is required for governed runtime tools";
    if (isBlank(capabilityId)) return "capability id is required for governed runtime tools";
    if (isBlank(correlationId)) return "correlation id is required";
    if (isBlank(redactedUserInput)) return "redacted user input is required";
    return null;
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private static String safe(String value) {
    return value == null ? "denied" : value.replaceAll("(?i)(api[_-]?key|secret|token)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
  }

  private static boolean looksSecretLike(String value) {
    var normalized = value.toLowerCase();
    return normalized.contains("api_key")
        || normalized.contains("apikey")
        || normalized.contains("secret")
        || normalized.contains("token=")
        || normalized.contains("bearer ");
  }
}
