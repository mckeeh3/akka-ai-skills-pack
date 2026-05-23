---
name: akka-agent-testing
description: Test Akka Java SDK agents with TestModelProvider, TestKitSupport, workflow orchestration, and endpoint integration calls. Use when deterministic agent testing is the main concern.
---

# Akka Agent Testing

Use this skill when testing agent code or agent-driven flows.


## Generated SaaS input contract

For generated full-stack AI-first SaaS agent work, implement only after the task, app-description, spec, or backlog supplies or explicitly defers:
- placement as a user-facing functional agent or a bounded internal agent, including owning workstream and structured surface placement when user-facing;
- capability id/class for each model request, tool call, output, workflow step, endpoint, or evaluation result;
- caller `AuthContext`, tenant/customer scope, roles/capabilities, assigned skill/reference manifests, allowed data/tools, and backend authorization boundary;
- input/output DTOs, redaction, side effects, idempotency, policy/approval/escalation, audit/work trace fields, correlation ids, and required tests.

If these are absent for generated SaaS implementation, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing from prompt, memory, streaming, guardrail, or test mechanics.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/testing.html.md`
- `akka-context/sdk/agents/llm_eval.html.md`
- `../../src/test/java/com/example/application/ActivityAgentTest.java`
- `../../src/test/java/com/example/application/AgentTeamWorkflowIntegrationTest.java`
- `../../src/test/java/com/example/application/ActivityAgentEndpointIntegrationTest.java`

## Use this pattern when

- agent behavior must be deterministic in tests
- workflow steps call one or more agents
- endpoints expose agents directly
- structured replies must be asserted without real model calls

## Core pattern

1. Extend `TestKitSupport`.
2. Create one `TestModelProvider` per agent when different mocked behavior is needed.
3. Register them in `testKitSettings()` with `.withModelProvider(...)`.
4. Use `.fixedResponse(...)` for simple deterministic replies.
5. Use `.whenMessage(...).reply(...)` for scenario-specific behavior.
6. Call agents through `componentClient.forAgent().inSession(...)`.
7. Call endpoints through `httpClient` and workflows through `componentClient.forWorkflow(...)`.

## Repository examples

- `ActivityAgentTest`
  - single agent, structured reply mapping
- `AgentTeamWorkflowIntegrationTest`
  - workflow orchestration with two mocked agents
- `ActivityAgentEndpointIntegrationTest`
  - HTTP endpoint over direct and streaming agent calls

## Governed runtime agent tests

Use this section when an agent is resolved from tenant-scoped runtime governance rather than being only a static Java class. Pair these tests with `akka-agent-behavior-profiles`, `akka-agent-prompt-governance`, `akka-agent-skill-governance`, `akka-agent-tool-boundaries`, and `akka-agent-work-trace`.

### Runtime resolution and lifecycle

Test the `AgentRuntimeResolver` or equivalent boundary before model invocation:

- active `AgentDefinition` resolution succeeds only for the caller's authorized tenant/customer context;
- cross-tenant `AgentDefinition`, prompt, skill, manifest, tool boundary, trace, and replay access is denied;
- disabled agent and archived agent runtime calls fail closed for HTTP, workflow, timer, consumer, tool, and test-console entry points;
- draft agents are denied for production runtime and allowed only in explicitly authorized test/replay mode;
- lifecycle denials emit audit/work trace events with safe reasons.

### Prompt governance tests

Cover prompt document/version semantics:

- runtime mode uses only active approved `PromptVersion` content;
- draft prompt and unapproved prompt versions are available only in authorized test/replay mode and are visibly labeled as non-production;
- unapproved prompt activation is denied and audited;
- prompt assembly is deterministic for the same `AgentDefinition`, prompt version, compact skill manifest, tool boundary, mode, and AuthContext;
- every test/runtime/replay assembly emits `PromptAssemblyTrace` with agent id, prompt document/version, model config ref, manifest ref, tool-boundary ref, caller, mode, correlation id, and checksum;
- prompt text that attempts authority expansion cannot grant role, data, tool, tenant, or approval authority by itself.

### Skill governance and `readSkill` tests

Cover manifest and skill-loading behavior:

- compact `AgentSkillManifest` appears in the assembled prompt, but full skill text is absent until `readSkill(skillId)` is called;
- assigned active skill load succeeds and emits `SkillLoadTrace` with manifest version, skill version, mode, caller, and authorization decision;
- unassigned skill, inactive skill, wrong-version skill, oversized skill, unauthorized mode, and cross-tenant skill reads are denied with safe model-visible messages;
- disabled/archived agents cannot call `readSkill` except for explicitly authorized inspection/replay;
- `readSkill` is allowed only when both the active `AgentSkillManifest` and active `ToolPermissionBoundary` grant the skill tool.

### Reference governance and `readReferenceDoc` tests

Cover manifest and reference-loading behavior:

- compact `AgentReferenceManifest` entries appear in the assembled expertise manifest alongside skill entries, but full reference text is absent until `readReferenceDoc(referenceId)` is called;
- assigned active reference load succeeds and emits `ReferenceLoadTrace` or `DocumentLoadTrace(documentKind=reference)` with manifest version, reference version, requested use, redaction decision, mode, caller, and authorization decision;
- unassigned reference, inactive/deprecated/unapproved reference, wrong-version reference, unauthorized use mode, oversized/token-limit reference, redaction/access-level denial, and cross-tenant or wrong-customer reference reads are denied with safe model-visible messages;
- disabled/archived agents cannot call `readReferenceDoc` except for explicitly authorized inspection/replay;
- `readReferenceDoc` is allowed only when both the active `AgentReferenceManifest` and active `ToolPermissionBoundary` grant the reference loader, such as `read_reference`;
- reference text that claims an agent can access data, grant roles, use tools, approve changes, bypass tenant/customer scope, or execute side effects cannot expand authority beyond backend capability contracts and tool boundaries.

### Tool-boundary tests

Cover backend-enforced `ToolPermissionBoundary` semantics:

- allowed read-only tool invocation succeeds and records tool id, capability id, boundary version, and safe data-access summary;
- denied ungranted tool id/category, denied side-effecting operation under a read-only grant, and denied cross-customer or cross-tenant input all fail closed;
- approval-required side effects return an approval-required result or decision-card request without executing the side effect;
- side-effecting tools require idempotency keys where the capability contract says so;
- MCP allowed-tool filters and component-tool `uniqueId` scope validation are exercised when those tool types are in scope;
- all allowed, denied, and approval-required tool calls emit trace events referencing `ToolPermissionBoundary`.

### Behavior-editing and authority-expansion tests

Cover `AgentBehaviorEditorAgent` or equivalent editing-agent flows:

- change request produces a structured proposal with affected documents, proposed diff, rationale, risk classification, tests/replay notes, and target draft version or boundary proposal;
- prompt, skill, manifest, tool-boundary, model, or authority proposals create draft artifacts rather than mutating active runtime state directly;
- reviewer approval activates only the approved draft/version/boundary and emits audit/work traces;
- rejection leaves active runtime behavior unchanged;
- unauthorized authority expansion is denied or routed to approval, including new tool grants, broader data scope, higher autonomy, tenant/customer scope changes, or approval bypass attempts.

### Trace and isolation tests

For consequential governed-agent behavior, assert trace facts rather than logs only:

- allowed and denied runtime calls create `AgentWorkTrace`/audit events with correlation ids;
- `PromptAssemblyTrace` links to any subsequent `SkillLoadTrace`, `ReferenceLoadTrace`, tool invocation trace, workflow step, decision card, or response summary;
- trace search/detail APIs are tenant-scoped and capability-protected;
- sensitive prompt, skill, reference, input, output, and tool payload fields are summarized or redacted for normal readers;
- forbidden, disabled-user, disabled-agent, unassigned skill, unassigned reference, missing `read_skill` or `read_reference` tool-boundary grant, tool-boundary denial, and cross-tenant tests include both denial response and trace emission assertions.

## Review checklist

Before finishing, verify:
- each tested agent has a registered `TestModelProvider`
- session ids are supplied in agent calls
- structured responses are serialized with `JsonSupport.encodeToString(...)`
- workflow tests use `Awaitility` when completion is asynchronous
- endpoint tests use `httpClient`, not `componentClient`
- governed runtime tests cover active profile resolution, disabled/archived denial, draft-only test/replay behavior, unapproved activation denial, unassigned-skill denial, unassigned-reference denial, cross-tenant and wrong-customer denial, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `ToolPermissionBoundary` denial for `read_skill` and `read_reference`, `AgentBehaviorEditorAgent` proposal flow, and authority expansion approval/denial
