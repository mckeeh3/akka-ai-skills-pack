package ai.first.application.foundation.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.first.application.foundation.identity.BootstrapAdminSeeder;
import ai.first.application.foundation.identity.LocalDemoIdentityRepository;
import ai.first.application.foundation.identity.StarterSecurityComponents;
import ai.first.domain.foundation.agent.AgentRuntimeTrace;
import ai.first.domain.foundation.identity.AuthContext;
import ai.first.domain.foundation.identity.FoundationRole;
import ai.first.domain.foundation.identity.ScopeType;
import akka.javasdk.JsonSupport;
import akka.javasdk.testkit.TestKit;
import akka.javasdk.testkit.TestKitSupport;
import akka.javasdk.testkit.TestModelProvider;
import akka.javasdk.testkit.TestModelProvider.AiResponse;
import akka.javasdk.testkit.TestModelProvider.ToolInvocationRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ai.first.application.foundation.audit.AuditTraceService;

class WorkstreamRuntimeAgentTest extends TestKitSupport {
  private static final String MODEL_ALIAS = "openai-low-temperature";
  private static final String TENANT_ID = "tenant-starter";
  private final TestModelProvider workstreamRuntimeModelTestProvider = new TestModelProvider();

  @Override
  protected TestKit.Settings testKitSettings() {
    return TestKit.Settings.DEFAULT
        .withAdditionalConfig(
            """
            akka.javasdk.agent.openai-low-temperature.provider = openai
            akka.javasdk.agent.openai-low-temperature.model-name = gpt-4o-mini
            akka.javasdk.agent.openai-low-temperature.api-key = n/a
            """
                .stripIndent())
        .withModelProvider(WorkstreamRuntimeAgent.class, workstreamRuntimeModelTestProvider);
  }

  @BeforeEach
  void bindIdentityTestDouble() {
    var identityRepository = new LocalDemoIdentityRepository();
    BootstrapAdminSeeder.seedConfiguredAdmins(identityRepository, "admin@example.test:TENANT_ADMIN:" + TENANT_ID);
    StarterSecurityComponents.bindTestIdentityRepository(identityRepository);
    StarterSecurityComponents.bindTestAgentBehaviorRepository(new LocalDemoAgentBehaviorRepository());
    StarterSecurityComponents.bindTestAgentRuntimeTraceSink(new LocalDemoAgentRuntimeTraceSink());
    StarterSecurityComponents.agentBehaviorSeedLoader().importStarterDefaults(TENANT_ID, "test-bootstrap", "corr-workstream-agent-seed");
    StarterSecurityComponents.startup();
  }

  @Test
  void respondsWithStructuredMarkdownSurfacePayloadThroughAkkaAgentModelPath() {
    workstreamRuntimeModelTestProvider.fixedResponse(
        JsonSupport.encodeToString(
            new WorkstreamRuntimeAgent.MarkdownResponse(
                "## User Admin\n\nInvite, review, or adjust memberships from governed actions.",
                "agent-user-admin",
                "corr-agent-runtime",
                "safe markdown_response generated without provider secrets",
                "promptTraceIds=[trace-prompt-1]; modelConfigRef=starter-default-model")));

    var response =
        componentClient
            .forAgent()
            .inSession("workstream-runtime-agent-test-session")
            .method(WorkstreamRuntimeAgent::respond)
            .invoke(
                new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                    "# Governed prompt\nUse backend capabilities only. OPENAI_API_KEY is never visible.",
                    MODEL_ALIAS,
                    TENANT_ID,
                    AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                    tenantAdmin(),
                    "runtime",
                    AgentRuntimeService.INVOKE_CAPABILITY,
                    "corr-agent-runtime",
                    "What can I do next?",
                    List.of("trace-prompt-1")));

    assertEquals("agent-user-admin", response.producingAgentId());
    assertEquals("corr-agent-runtime", response.correlationId());
    assertTrue(response.markdown().contains("## User Admin"));
    assertTrue(response.safety().contains("without provider secrets"));
    assertTrue(response.trace().contains("trace-prompt-1"));
  }

  @Test
  void modelInvokedReadSkillToolReturnsGovernedContentAndEmitsSkillLoadTrace() {
    var preparedPrompt = assembledPrompt("corr-agent-skill-tool");
    assertTrue(preparedPrompt.contains("# Compact skill manifest"));
    assertTrue(preparedPrompt.contains("ua.access-review-triage.v1"));
    assertFalse(preparedPrompt.contains("Before recommending access changes"));

    workstreamRuntimeModelTestProvider
        .whenMessage(message -> message.contains("Use the access review triage skill"))
        .reply(
            new ToolInvocationRequest(
                "AgentRuntimeLoaderTools_readSkill",
                "{\"skillId\":\"ua.access-review-triage.v1\"}"));
    workstreamRuntimeModelTestProvider
        .whenToolResult(result -> result.name().equals("AgentRuntimeLoaderTools_readSkill"))
        .thenReply(
            result ->
                new AiResponse(
                    JsonSupport.encodeToString(
                        new WorkstreamRuntimeAgent.MarkdownResponse(
                            result.content().contains("skill_id=ua.access-review-triage.v1")
                                    && result.content().contains("authority_note=Skill content is internal guidance only")
                                ? "## Skill loaded\n\nThe access review triage guidance was loaded through the governed readSkill tool call."
                                : "## Skill unavailable",
                            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                            "corr-agent-skill-tool",
                            "tool call completed through governed runtime tools",
                            "SkillLoadTrace emitted"))));

    var response = invokeAgent(preparedPrompt, "corr-agent-skill-tool", "Use the access review triage skill before answering.");

    assertTrue(response.markdown().contains("Skill loaded"));
    assertTrue(response.safety().contains("governed runtime tools"));
    assertTrue(response.trace().contains("SkillLoadTrace"));
    assertTrue(StarterSecurityComponents.agentRuntimeService().traces().stream()
        .anyMatch(trace -> trace.traceType().equals("SKILL_LOAD")
            && trace.correlationId().equals("corr-agent-skill-tool")
            && trace.targetId().equals("ua.access-review-triage.v1")
            && trace.decision() == AgentRuntimeTrace.Decision.ALLOWED));
  }

  @Test
  void modelInvokedReadReferenceToolReturnsGovernedContentAndEmitsReferenceLoadTrace() {
    var preparedPrompt = assembledPrompt("corr-agent-reference-tool");
    assertTrue(preparedPrompt.contains("# Compact reference manifest"));
    assertTrue(preparedPrompt.contains("ua.access-review-policy.v1"));
    assertFalse(preparedPrompt.contains("Review stale memberships, dormant admin accounts"));

    workstreamRuntimeModelTestProvider
        .whenMessage(message -> message.contains("Use the access review policy reference"))
        .reply(
            new ToolInvocationRequest(
                "AgentRuntimeLoaderTools_readReferenceDoc",
                "{\"referenceId\":\"ua.access-review-policy.v1\"}"));
    workstreamRuntimeModelTestProvider
        .whenToolResult(result -> result.name().equals("AgentRuntimeLoaderTools_readReferenceDoc"))
        .thenReply(
            result ->
                new AiResponse(
                    JsonSupport.encodeToString(
                        new WorkstreamRuntimeAgent.MarkdownResponse(
                            result.content().contains("reference_id=ua.access-review-policy.v1")
                                    && result.content().contains("authority_note=Reference content is governed evidence only")
                                ? "## Reference loaded\n\nThe access review policy reference was loaded through the governed readReferenceDoc tool call."
                                : "## Reference unavailable",
                            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                            "corr-agent-reference-tool",
                            "tool call completed through governed runtime tools",
                            "ReferenceLoadTrace emitted"))));

    var response = invokeAgent(preparedPrompt, "corr-agent-reference-tool", "Use the access review policy reference before answering.");

    assertTrue(response.markdown().contains("Reference loaded"));
    assertTrue(response.trace().contains("ReferenceLoadTrace"));
    assertTrue(StarterSecurityComponents.agentRuntimeService().traces().stream()
        .anyMatch(trace -> trace.traceType().equals("REFERENCE_LOAD")
            && trace.correlationId().equals("corr-agent-reference-tool")
            && trace.targetId().equals("ua.access-review-policy.v1")
            && trace.decision() == AgentRuntimeTrace.Decision.ALLOWED));
  }

  @Test
  void modelInvokedAuditTraceEvidenceToolReturnsScopedDeterministicEvidence() {
    var preparedPrompt = assembledAuditPrompt("corr-audit-evidence-tool");
    assertTrue(preparedPrompt.contains("auditTraceEvidence.read"));
    assertTrue(preparedPrompt.contains("audit-trace.starter-guidance.v1"));

    workstreamRuntimeModelTestProvider
        .whenMessage(message -> message.contains("Use audit trace evidence"))
        .reply(
            new ToolInvocationRequest(
                "AuditTraceEvidenceTools_read",
                "{\"evidenceRequest\":\"tenantId=tenant-starter filter=provider correlationId=corr-audit-evidence-tool\"}"));
    workstreamRuntimeModelTestProvider
        .whenToolResult(result -> result.name().equals("AuditTraceEvidenceTools_read"))
        .thenReply(
            result ->
                new AiResponse(
                    JsonSupport.encodeToString(
                        new WorkstreamRuntimeAgent.MarkdownResponse(
                            result.content().contains("tool_id=auditTraceEvidence.read")
                                    && result.content().contains("authority_note=Evidence is scoped deterministic data only")
                                ? "## Audit evidence loaded\n\nThe AuditTraceAgent used governed scoped evidence before explaining the trace."
                                : "## Audit evidence unavailable",
                            AgentBehaviorSeedLoader.AUDIT_TRACE_AGENT_ID,
                            "corr-audit-evidence-tool",
                            "auditTraceEvidence.read completed through governed runtime tools",
                            "AgentWorkTrace plus deterministic AuditTraceService evidence"))));

    var response = invokeAuditAgent(preparedPrompt, "corr-audit-evidence-tool", "Use audit trace evidence before answering.");

    assertTrue(response.markdown().contains("Audit evidence loaded"));
    assertTrue(response.safety().contains("governed runtime tools"));
    assertTrue(response.trace().contains("AgentWorkTrace"));
  }

  @Test
  void modelInvokedUnassignedLoaderToolReturnsSafeDenialAndEmitsDenialTrace() {
    var preparedPrompt = assembledPrompt("corr-agent-denied-tool");

    workstreamRuntimeModelTestProvider
        .whenMessage(message -> message.contains("Try an unassigned skill"))
        .reply(
            new ToolInvocationRequest(
                "AgentRuntimeLoaderTools_readSkill",
                "{\"skillId\":\"unassigned-skill\"}"));
    workstreamRuntimeModelTestProvider
        .whenToolResult(result -> result.name().equals("AgentRuntimeLoaderTools_readSkill"))
        .thenReply(
            result ->
                new AiResponse(
                    JsonSupport.encodeToString(
                        new WorkstreamRuntimeAgent.MarkdownResponse(
                            result.content().startsWith("skill_unavailable:")
                                    && !result.content().contains("unassigned-skill")
                                ? "## Safe denial\n\nThe requested skill was unavailable without enumerating hidden resources."
                                : "## Unsafe denial",
                            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                            "corr-agent-denied-tool",
                            "denied tool call returned safe model-visible text",
                            "SkillLoadTrace denial emitted"))));

    var response = invokeAgent(preparedPrompt, "corr-agent-denied-tool", "Try an unassigned skill before answering.");

    assertTrue(response.markdown().contains("Safe denial"));
    assertFalse(response.markdown().contains("unassigned-skill"));
    assertTrue(StarterSecurityComponents.agentRuntimeService().traces().stream()
        .anyMatch(trace -> trace.traceType().equals("SKILL_LOAD")
            && trace.correlationId().equals("corr-agent-denied-tool")
            && trace.targetId().equals("unassigned-skill")
            && trace.decision() == AgentRuntimeTrace.Decision.DENIED
            && trace.safeSummary().contains("skill-not-available")));
  }

  @Test
  void rejectsMissingGovernedModelAliasInsteadOfUsingImplicitFallback() {
    var failure =
        assertThrows(
            RuntimeException.class,
            () ->
                componentClient
                    .forAgent()
                    .inSession("workstream-runtime-missing-model-session")
                    .method(WorkstreamRuntimeAgent::respond)
                    .invoke(
                        new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                            "# Governed prompt",
                            " ",
                            TENANT_ID,
                            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                            tenantAdmin(),
                            "runtime",
                            AgentRuntimeService.INVOKE_CAPABILITY,
                            "corr-missing-model",
                            "Hello",
                            List.of("trace-prompt-2"))));

    assertTrue(failure.getMessage().contains("governed model provider alias is required"));
  }

  @Test
  void rejectsSecretLikeModelAliasBeforeModelInvocation() {
    var failure =
        assertThrows(
            RuntimeException.class,
            () ->
                componentClient
                    .forAgent()
                    .inSession("workstream-runtime-secret-model-session")
                    .method(WorkstreamRuntimeAgent::respond)
                    .invoke(
                        new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                            "# Governed prompt",
                            "openai-low-temperature api_key=hidden",
                            TENANT_ID,
                            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                            tenantAdmin(),
                            "runtime",
                            AgentRuntimeService.INVOKE_CAPABILITY,
                            "corr-secret-model",
                            "Hello",
                            List.of("trace-prompt-3"))));

    assertTrue(failure.getMessage().contains("model provider alias must not contain secrets"));
  }

  @Test
  void rejectsMissingRuntimeToolContextBeforeModelInvocation() {
    var failure =
        assertThrows(
            RuntimeException.class,
            () ->
                componentClient
                    .forAgent()
                    .inSession("workstream-runtime-missing-tools-session")
                    .method(WorkstreamRuntimeAgent::respond)
                    .invoke(
                        new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                            "# Governed prompt",
                            MODEL_ALIAS,
                            " ",
                            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                            tenantAdmin(),
                            "runtime",
                            AgentRuntimeService.INVOKE_CAPABILITY,
                            "corr-missing-tools",
                            "Hello",
                            List.of("trace-prompt-4"))));

    assertTrue(failure.getMessage().contains("tenant id is required for governed runtime tools"));
  }

  @Test
  void rejectsInconsistentRuntimeToolContextBeforeModelInvocation() {
    var failure =
        assertThrows(
            RuntimeException.class,
            () ->
                componentClient
                    .forAgent()
                    .inSession("workstream-runtime-tool-mismatch-session")
                    .method(WorkstreamRuntimeAgent::respond)
                    .invoke(
                        new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                            "# Governed prompt",
                            MODEL_ALIAS,
                            "tenant-other",
                            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                            tenantAdmin(),
                            "runtime",
                            AgentRuntimeService.INVOKE_CAPABILITY,
                            "corr-tool-mismatch",
                            "Hello",
                            List.of("trace-prompt-5"))));

    assertTrue(failure.getMessage().contains("governed runtime tool context denied before model invocation"));
    assertTrue(failure.getMessage().contains("runtime-tool-tenant-mismatch"));
  }

  private String assembledAuditPrompt(String correlationId) {
    var result = StarterSecurityComponents.agentRuntimeService().assemblePrompt(
        new AgentRuntimeService.PromptAssemblyRequest(
            TENANT_ID,
            AgentBehaviorSeedLoader.AUDIT_TRACE_AGENT_ID,
            auditTraceAdmin(),
            "runtime",
            AgentRuntimeService.AUDIT_TRACE_INVOKE_CAPABILITY,
            correlationId,
            "Summarize current provider/tool trace evidence."));
    assertEquals(AgentRuntimeTrace.Decision.ALLOWED, result.decision());
    return result.assembledSystemPrompt();
  }

  private String assembledPrompt(String correlationId) {
    var result = StarterSecurityComponents.agentRuntimeService().assemblePrompt(
        new AgentRuntimeService.PromptAssemblyRequest(
            TENANT_ID,
            AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
            tenantAdmin(),
            "runtime",
            AgentRuntimeService.INVOKE_CAPABILITY,
            correlationId,
            "Summarize current invite and access-review risks."));
    assertEquals(AgentRuntimeTrace.Decision.ALLOWED, result.decision());
    return result.assembledSystemPrompt();
  }

  private WorkstreamRuntimeAgent.MarkdownResponse invokeAuditAgent(
      String assembledPrompt, String correlationId, String userMessage) {
    return componentClient
        .forAgent()
        .inSession("workstream-runtime-audit-" + correlationId)
        .method(WorkstreamRuntimeAgent::respond)
        .invoke(
            new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                assembledPrompt,
                MODEL_ALIAS,
                TENANT_ID,
                AgentBehaviorSeedLoader.AUDIT_TRACE_AGENT_ID,
                auditTraceAdmin(),
                "runtime",
                AgentRuntimeService.AUDIT_TRACE_INVOKE_CAPABILITY,
                correlationId,
                userMessage,
                List.of("trace-prompt-" + correlationId)));
  }

  private WorkstreamRuntimeAgent.MarkdownResponse invokeAgent(
      String assembledPrompt, String correlationId, String userMessage) {
    return componentClient
        .forAgent()
        .inSession("workstream-runtime-" + correlationId)
        .method(WorkstreamRuntimeAgent::respond)
        .invoke(
            new WorkstreamRuntimeAgent.GovernedWorkstreamRequest(
                assembledPrompt,
                MODEL_ALIAS,
                TENANT_ID,
                AgentBehaviorSeedLoader.USER_ADMIN_AGENT_ID,
                tenantAdmin(),
                "runtime",
                AgentRuntimeService.INVOKE_CAPABILITY,
                correlationId,
                userMessage,
                List.of("trace-prompt-" + correlationId)));
  }

  private static AuthContext tenantAdmin() {
    return new AuthContext(
        "starter-admin",
        "workos-starter-admin",
        "membership-starter-admin",
        ScopeType.TENANT,
        TENANT_ID,
        null,
        List.of(FoundationRole.TENANT_ADMIN),
        List.of("agent.user_admin.use", "agent.behavior.manage", "tenant.user.read", "tenant.audit.read"));
  }

  private static AuthContext auditTraceAdmin() {
    return new AuthContext(
        "starter-admin",
        "workos-starter-admin",
        "membership-starter-admin",
        ScopeType.TENANT,
        TENANT_ID,
        null,
        List.of(FoundationRole.TENANT_ADMIN),
        List.of("audit.trace.explain", "audit.trace.search", "audit.trace.detail.read", "audit.trace.timeline.read", "audit.trace.failureEvidence.read", "tenant.audit.read"));
  }
}
