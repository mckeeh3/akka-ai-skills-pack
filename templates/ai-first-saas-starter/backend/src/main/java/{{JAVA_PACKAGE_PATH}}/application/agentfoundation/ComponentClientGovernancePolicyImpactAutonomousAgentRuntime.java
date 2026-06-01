package {{JAVA_BASE_PACKAGE}}.application.agentfoundation;

import akka.javasdk.agent.task.TaskStatus;
import akka.javasdk.client.ComponentClient;
import {{JAVA_BASE_PACKAGE}}.application.security.AuthContextResolver;
import {{JAVA_BASE_PACKAGE}}.domain.security.GovernancePolicyImpactTask;
import java.util.List;
import java.util.Objects;

/** ComponentClient-backed bridge from governed Governance/Policy capabilities to Akka AutonomousAgent impact tasks. */
public final class ComponentClientGovernancePolicyImpactAutonomousAgentRuntime implements GovernancePolicyImpactAutonomousAgentRuntime {
  private final ComponentClient componentClient;
  private final AgentRuntimeService runtimeService;
  private final AgentRuntimeToolResolver toolResolver;

  public ComponentClientGovernancePolicyImpactAutonomousAgentRuntime(ComponentClient componentClient, AgentRuntimeService runtimeService, AgentRuntimeToolResolver toolResolver) {
    this.componentClient = Objects.requireNonNull(componentClient);
    this.runtimeService = Objects.requireNonNull(runtimeService);
    this.toolResolver = Objects.requireNonNull(toolResolver);
  }

  @Override
  public StartOutcome start(AuthContextResolver.ResolvedMe actor, GovernancePolicyImpactTask starterTask, String evidenceRequest, String correlationId) {
    var traceIds = new java.util.ArrayList<String>();
    try {
      var tools = toolResolver.resolve(new AgentRuntimeToolResolver.ResolveRuntimeToolsRequest(
          starterTask.tenantId(),
          AgentBehaviorSeedLoader.GOVERNANCE_POLICY_AGENT_ID,
          actor.selectedContext(),
          "autonomous_agent_task",
          {{JAVA_BASE_PACKAGE}}.application.security.GovernancePolicyImpactService.START_CAPABILITY,
          correlationId));
      requireGranted(tools, ToolRegistry.GOVERNANCE_POLICY_EVIDENCE_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_SKILL_TOOL_ID);
      requireGranted(tools, ToolRegistry.READ_REFERENCE_DOC_TOOL_ID);

      var preparation = runtimeService.prepareWorkstreamAgentInvocation(new AgentRuntimeService.RuntimeInvocationRequest(
          starterTask.tenantId(),
          AgentBehaviorSeedLoader.GOVERNANCE_POLICY_AGENT_ID,
          actor.selectedContext(),
          correlationId,
          "Prepare governed context for Governance/Policy impact AutonomousAgent task " + starterTask.impactTaskId() + "; advisory only, no direct policy mutation."));
      traceIds.addAll(preparation.traceIds());
      if (preparation.decision() != {{JAVA_BASE_PACKAGE}}.domain.agentfoundation.AgentRuntimeTrace.Decision.ALLOWED) {
        return StartOutcome.blocked(
            "Governance/Policy impact AutonomousAgent start failed closed during governed prompt/model preparation: " + preparation.safeErrorSummary(),
            "blocked_provider_or_runtime",
            traceIds);
      }

      var request = new GovernancePolicyImpactTasks.GovernancePolicyImpactRequest(
          starterTask.impactTaskId(),
          starterTask.proposalId(),
          starterTask.targetPolicyId(),
          starterTask.tenantId(),
          starterTask.customerId(),
          starterTask.startedByAccountId(),
          correlationId,
          {{JAVA_BASE_PACKAGE}}.application.security.GovernancePolicyImpactService.START_CAPABILITY,
          evidenceRequest,
          "modelProviderAlias=" + preparation.governedRequest().modelProviderAlias()
              + "; modelConfigRefId=" + preparation.modelConfigRefId()
              + "; promptTraceIds=" + String.join(",", preparation.traceIds())
              + "; advisory governance policy impact only; no direct approval, activation, rollback, or policy mutation allowed",
          starterTask.affectedCapabilityIds(),
          starterTask.affectedArtifactRefs(),
          starterTask.evidenceRefs());
      var autonomousAgentTaskId = componentClient
          .forAutonomousAgent(GovernancePolicyImpactAutonomousAgent.class, agentInstanceId(starterTask))
          .runSingleTask(GovernancePolicyImpactTasks.impactInstructions(request));
      traceIds.add("autonomous_task:" + autonomousAgentTaskId);
      return StartOutcome.queued(
          autonomousAgentTaskId,
          "Governance/Policy impact AutonomousAgent task accepted by Akka runtime; result remains backend-projected and human-review gated.",
          traceIds);
    } catch (RuntimeException failure) {
      return StartOutcome.blocked(
          "Governance/Policy impact AutonomousAgent start failed closed before successful model-backed task execution: " + safe(failure.getMessage()),
          "blocked_provider_or_runtime",
          traceIds);
    }
  }

  @Override
  public Projection project(GovernancePolicyImpactTask starterTask, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return Projection.unchanged();
    try {
      var snapshot = componentClient.forTask(starterTask.autonomousAgentTaskId()).get(GovernancePolicyImpactTasks.IMPACT_ANALYSIS);
      if (snapshot.status() == TaskStatus.COMPLETED) {
        var result = snapshot.result().orElse(null);
        if (result == null) return Projection.unchanged();
        var findingRefs = result.impactFindings().stream()
            .map(finding -> "governance_policy_impact_finding:" + safe(finding.findingId()) + ":" + safe(finding.category()))
            .toList();
        return new Projection(
            GovernancePolicyImpactTask.Status.COMPLETED_REVIEW_REQUIRED,
            100,
            safe(result.summary()),
            null,
            result,
            result.sourceRefs(),
            findingRefs,
            withAutonomousTaskTrace(result.traceRefs(), starterTask.autonomousAgentTaskId()));
      }
      if (snapshot.status() == TaskStatus.FAILED) {
        return new Projection(
            GovernancePolicyImpactTask.Status.FAILED,
            starterTask.progressPercent(),
            "Governance/Policy impact AutonomousAgent task failed closed: " + snapshot.failureReason().orElse("no safe failure reason supplied"),
            "autonomous_agent_task_failed",
            null,
            starterTask.evidenceRefs(),
            List.of(),
            List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
      }
      if (snapshot.status() == TaskStatus.CANCELLED) {
        return new Projection(
            GovernancePolicyImpactTask.Status.CANCELLED,
            starterTask.progressPercent(),
            "Governance/Policy impact AutonomousAgent task was cancelled before completion; policy proposal unchanged.",
            null,
            null,
            starterTask.evidenceRefs(),
            List.of(),
            List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
      }
      var running = snapshot.status() == TaskStatus.IN_PROGRESS || snapshot.status() == TaskStatus.RESULT_REJECTED
          ? GovernancePolicyImpactTask.Status.RUNNING
          : GovernancePolicyImpactTask.Status.QUEUED;
      return new Projection(
          running,
          running == GovernancePolicyImpactTask.Status.RUNNING ? 50 : Math.max(5, starterTask.progressPercent()),
          "Governance/Policy impact AutonomousAgent task is " + snapshot.status().name().toLowerCase(java.util.Locale.ROOT) + "; backend task snapshot remains source of truth.",
          null,
          null,
          starterTask.evidenceRefs(),
          starterTask.findingRefs(),
          List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
    } catch (RuntimeException failure) {
      return new Projection(
          GovernancePolicyImpactTask.Status.BLOCKED_PROVIDER_OR_RUNTIME,
          starterTask.progressPercent(),
          "Governance/Policy impact AutonomousAgent task query failed closed: " + safe(failure.getMessage()),
          "autonomous_agent_task_query_failed",
          null,
          starterTask.evidenceRefs(),
          List.of(),
          List.of("autonomous_task:" + starterTask.autonomousAgentTaskId()));
    }
  }

  @Override
  public void cancel(GovernancePolicyImpactTask starterTask, String reason, String correlationId) {
    if (starterTask.autonomousAgentTaskId() == null || starterTask.autonomousAgentTaskId().isBlank()) return;
    try {
      componentClient.forTask(starterTask.autonomousAgentTaskId()).fail("Cancelled by authorized Governance/Policy reviewer: " + safe(reason));
    } catch (RuntimeException ignored) {
      // Starter projection remains authoritative for cancel; failed task termination must not resurrect impact work.
    }
  }

  private static void requireGranted(AgentRuntimeToolResolver.ResolvedRuntimeTools tools, String toolId) {
    if (!tools.grantedToolIds().contains(toolId)) throw new IllegalStateException("required governed tool not granted for Governance/Policy impact AutonomousAgent task: " + toolId);
  }

  private static String agentInstanceId(GovernancePolicyImpactTask task) {
    return "governance-policy-impact:" + task.tenantId() + ":" + (task.customerId() == null ? "none" : task.customerId()) + ":" + task.impactTaskId();
  }

  private static List<String> withAutonomousTaskTrace(List<String> traces, String autonomousAgentTaskId) {
    var copy = new java.util.ArrayList<String>();
    if (traces != null) copy.addAll(traces);
    copy.add("autonomous_task:" + autonomousAgentTaskId);
    return copy;
  }

  private static String safe(String value) {
    var safe = value == null ? "" : value.replaceAll("(?i)(api[_-]?key|secret|token|jwt)\\s*[:=]\\s*\\S+", "$1=[REDACTED]");
    return safe.length() <= 240 ? safe : safe.substring(0, 240);
  }
}
