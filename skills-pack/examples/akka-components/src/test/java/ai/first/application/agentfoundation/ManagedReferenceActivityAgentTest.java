package ai.first.application.agentfoundation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import akka.javasdk.JsonSupport;
import akka.javasdk.agent.ModelTimeoutException;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import ai.first.domain.agentfoundation.ReferenceAgentDefinition;
import ai.first.domain.agentfoundation.ReferenceAgentSkillManifest;
import ai.first.domain.agentfoundation.ReferenceAgentWorkTrace;
import ai.first.domain.agentfoundation.ReferencePromptVersion;
import ai.first.domain.agentfoundation.ReferenceResolvedAgentRuntime;
import ai.first.domain.agentfoundation.ReferenceSkillDocument;
import ai.first.domain.agentfoundation.ReferenceSkillVersion;
import ai.first.domain.agentfoundation.ReferenceToolPermissionBoundary;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ManagedReferenceActivityAgentTest extends TestKitSupport {

  private final TestModelProvider managedActivityModel = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig("akka.javasdk.agent.openai.api-key = n/a")
        .withModelProvider(ManagedReferenceActivityAgent.class, managedActivityModel);
  }

  @Test
  void assembledPromptInvocationSucceedsAndRecordsCorrelatedWorkTrace() {
    var traceSink = new ReferenceTraceSink();
    var runtime = resolvedRuntime(traceSink, "corr-managed-success");
    var tools = new ReferenceAgentSkillTools(runtime, authorizer(traceSink));
    var loadedSkill = tools.readSkill(ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID);
    managedActivityModel.fixedResponse(
        JsonSupport.encodeToString(
            new ManagedReferenceActivityAgent.ManagedActivitySuggestion(
                "Museum visit", "The governed prompt and loaded skill favor indoor options.", "indoor")));

    var result = invokeManaged(runtime, "Find an activity for a rainy afternoon", traceSink);

    assertEquals("Museum visit", result.name());
    assertTrue(loadedSkill.contains("Authority: guidance only"));
    assertEquals(1, traceSink.promptAssemblyTraces().size());
    assertEquals(1, traceSink.skillLoadTraces().size());
    assertEquals(1, traceSink.agentWorkTraces().size());
    var workTrace = traceSink.agentWorkTraces().getFirst();
    assertTrue(workTrace.allowed());
    assertEquals("corr-managed-success", workTrace.correlationId());
    assertEquals(runtime.assembledPromptChecksum(), workTrace.promptAssemblyTraceId());
    assertEquals(
        ReferenceAgentFoundationFixtures.activeAssignedSkillVersion().skillVersionId(),
        workTrace.lastSkillLoadTraceId());
    assertTrue(workTrace.summary().contains("Museum visit"));
  }

  @Test
  void initialAssembledPromptContainsManifestButNotFullSkillText() {
    var traceSink = new ReferenceTraceSink();
    var runtime = resolvedRuntime(traceSink, "corr-no-full-skill-text");

    assertTrue(runtime.assembledPrompt().contains("AgentSkillManifest compact"));
    assertTrue(runtime.assembledPrompt().contains(ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID));
    assertFalse(
        runtime
            .assembledPrompt()
            .contains(ReferenceAgentFoundationFixtures.activeAssignedSkillVersion().content()));
  }

  @Test
  void disabledAgentDenialDoesNotInvokeModel() {
    managedActivityModel
        .whenMessage(message -> true)
        .failWith(new ModelTimeoutException("model should not be invoked for disabled agent"));
    var traceSink = new ReferenceTraceSink();
    var runtime = resolver(traceSink).resolve(
        ReferenceAgentFoundationFixtures.authContext(),
        ReferenceAgentFoundationFixtures.DISABLED_AGENT_ID,
        "corr-disabled-no-model");

    var result = invokeManaged(runtime, "This request must be denied before the model", traceSink);

    assertEquals("Denied", result.name());
    assertEquals("agent is not active for runtime", result.reason());
    assertEquals("not_applicable", result.setting());
    assertEquals(1, traceSink.promptAssemblyTraces().size());
    assertEquals(1, traceSink.agentWorkTraces().size());
    assertFalse(traceSink.agentWorkTraces().getFirst().allowed());
    assertEquals("corr-disabled-no-model", traceSink.agentWorkTraces().getFirst().correlationId());
  }

  private ManagedReferenceActivityAgent.ManagedActivitySuggestion invokeManaged(
      ReferenceResolvedAgentRuntime runtime, String userMessage, ReferenceTraceSink traceSink) {
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
                    runtime.assembledPrompt(), userMessage, runtime.correlationId()));

    var lastSkillTrace = traceSink.skillLoadTraces().isEmpty() ? null : traceSink.skillLoadTraces().getLast();
    traceSink.recordAgentWork(
        new ReferenceAgentWorkTrace(
            runtime.authContext().tenantId(),
            runtime.agentDefinition().agentDefinitionId(),
            runtime.correlationId(),
            runtime.assembledPromptChecksum(),
            lastSkillTrace == null ? "" : lastSkillTrace.resolvedSkillVersionId(),
            runtime.authContext().mode(),
            true,
            "ManagedReferenceActivityAgent returned " + result.name()));
    return result;
  }

  private static ReferenceResolvedAgentRuntime resolvedRuntime(
      ReferenceTraceSink traceSink, String correlationId) {
    return resolver(traceSink)
        .resolve(
            ReferenceAgentFoundationFixtures.authContext(),
            ReferenceAgentFoundationFixtures.AGENT_ID,
            correlationId);
  }

  private static ReferenceAgentRuntimeResolver resolver(ReferenceTraceSink traceSink) {
    return new ReferenceAgentRuntimeResolver(
        agentDefinitions(),
        promptVersions(),
        manifests(),
        toolBoundaries(),
        new ReferencePromptAssembler(),
        traceSink);
  }

  private static ReferenceSkillReadAuthorizer authorizer(ReferenceTraceSink traceSink) {
    return new ReferenceSkillReadAuthorizer(skillDocuments(), skillVersions(), traceSink);
  }

  private static Map<String, ReferenceAgentDefinition> agentDefinitions() {
    return Map.of(
        ReferenceAgentFoundationFixtures.AGENT_ID,
        ReferenceAgentFoundationFixtures.activeAgent(),
        ReferenceAgentFoundationFixtures.DISABLED_AGENT_ID,
        ReferenceAgentFoundationFixtures.disabledAgent());
  }

  private static Map<String, ReferencePromptVersion> promptVersions() {
    return Map.of(
        ReferenceAgentFoundationFixtures.PROMPT_VERSION_ID,
        ReferenceAgentFoundationFixtures.activePromptVersion());
  }

  private static Map<String, ReferenceAgentSkillManifest> manifests() {
    return Map.of(
        ReferenceAgentFoundationFixtures.SKILL_MANIFEST_ID,
        ReferenceAgentFoundationFixtures.activeManifest());
  }

  private static Map<String, ReferenceToolPermissionBoundary> toolBoundaries() {
    return Map.of(
        ReferenceAgentFoundationFixtures.TOOL_BOUNDARY_ID,
        ReferenceAgentFoundationFixtures.activeToolBoundary());
  }

  private static Map<String, ReferenceSkillDocument> skillDocuments() {
    return Map.of(
        ReferenceAgentFoundationFixtures.ASSIGNED_SKILL_ID,
        ReferenceAgentFoundationFixtures.activeAssignedSkillDocument(),
        ReferenceAgentFoundationFixtures.UNASSIGNED_SKILL_ID,
        ReferenceAgentFoundationFixtures.unassignedSkillDocument());
  }

  private static Map<String, ReferenceSkillVersion> skillVersions() {
    return Map.of(
        "skill-version-active",
        ReferenceAgentFoundationFixtures.activeAssignedSkillVersion(),
        "skill-version-unassigned",
        ReferenceAgentFoundationFixtures.unassignedSkillVersion());
  }
}
