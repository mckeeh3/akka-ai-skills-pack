---
name: akka-agent-testing
description: Test Akka Java SDK agents with TestModelProvider, TestKitSupport, workflow orchestration, and endpoint integration calls. Use when deterministic agent testing is the main concern.
---

# Akka Agent Testing

Use this skill when testing request-based Akka `Agent` code or agent-driven flows.

Use `akka-autonomous-agent-testing` instead for `AutonomousAgent` task lifecycle, task rules, snapshots/results, notifications, delegation, handoff, teams, moderation, and `TestModelProvider.AutonomousAgentTools`.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Worker/tool/capability alignment

For generated AI-first SaaS app work, treat the agent runtime, autonomous task loop, or governed artifact in scope as a software-worker harness concern, not as the product operation or authorization boundary. Keep the chain explicit:

```text
software worker
→ Akka Agent/AutonomousAgent harness or focused governance artifact
→ actor adapter (`agent_tool_call`, `human_chat_tool_plan`, workflow/timer/consumer/API/MCP/internal adapter as applicable)
→ governed tool
→ backend capability
→ Akka/frontend implementation
```

Human surface availability, prompt/skill/reference text, model output, task instructions, and Akka tool registration do not grant tool authority. A model-facing tool, loader, or autonomous task action may be exposed only when the active workstream tool catalog, governed tool contract, backend `AuthContext`, and `ToolPermissionBoundary` explicitly allow that actor adapter; denials and approval-required paths must fail closed and be traced.


## Required reading

Read these first if present:
- `akka-context/sdk/agents/testing.html.md`
- `akka-context/sdk/agents/llm_eval.html.md`

## Use this pattern when

- agent behavior must be deterministic in tests
- workflow steps call one or more request-based agents
- endpoints expose agents directly
- structured replies must be asserted without real model calls

`TestModelProvider`, fixed responses, and mocked model behavior are test-only. They prove contracts, orchestration, denials, traces, and DTO handling; they do not prove a named user-facing/workstream agent feature is complete in normal runtime.

## Core pattern

1. Extend `TestKitSupport`.
2. Create one `TestModelProvider` per agent when different mocked behavior is needed.
3. Register them in `testKitSettings()` with `.withModelProvider(...)`.
4. Use `.fixedResponse(...)` for simple deterministic replies.
5. Use `.whenMessage(...).reply(...)` for scenario-specific behavior.
6. Call agents through `componentClient.forAgent().inSession(...)`.
7. Call endpoints through `httpClient` and workflows through `componentClient.forWorkflow(...)`.

## Pattern references

- `WorkstreamRuntimeAgentTest`
  - single agent, structured reply mapping
- target-project workflow orchestration test with two mocked agents
  - workflow orchestration with two mocked agents
- target-project HTTP endpoint test over direct and streaming agent calls
  - HTTP endpoint over direct and streaming agent calls

## Governed runtime agent tests

Use this section when an agent is resolved from tenant-scoped runtime governance rather than being only a static Java class. Pair these tests with `akka-agent-behavior-profiles`, `akka-agent-prompt-governance`, `akka-agent-skill-governance`, `akka-agent-tool-boundaries`, and `akka-agent-work-trace`.

For workstream agents, test the governed workstream tool contract rather than only the Java tool mechanism. A surface action, confirmed `human_chat_tool_plan`, and AI-backed `agent_tool_call` may adapt the same governed tool id, but each adapter needs separate authorization, confirmation/approval, idempotency, trace-source, result-surface, and denial assertions. TestModelProvider responses are allowed to drive model planning in tests, but prompt/skill text must not be treated as authority.

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

- allowed read-only tool invocation succeeds and records governed tool id, adapter tool id, capability id, boundary version, source `agent_tool_call`, and safe data-access summary;
- denied ungranted tool id/category, denied side-effecting operation under a read-only grant, and denied cross-customer or cross-tenant input all fail closed;
- approval-required side effects return an approval-required result or decision-card request without executing the side effect;
- side-effecting tools require idempotency keys where the capability contract says so;
- confirmed human chat tool plans deny execution before confirmation, bind confirmation to the proposed plan, reauthorize every governed-tool step, record `requestedBy` and `confirmedBy`, and report partial failures/result surfaces safely;
- AI-backed agent-tool calls are denied when the governed tool is absent from the active workstream tool catalog or `ToolPermissionBoundary`, even if prompt/skill/reference text asks for it;
- MCP allowed-tool filters and component-tool `uniqueId` scope validation are exercised when those tool types are in scope;
- all allowed, denied, approval-required, unconfirmed, and partial-failure tool calls emit trace events referencing `ToolPermissionBoundary`.

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
- `PromptAssemblyTrace` links to any subsequent `SkillLoadTrace`, `ReferenceLoadTrace`, tool invocation trace, human chat plan proposal/confirmation, workflow step, decision card, result surface, or response summary;
- trace search/detail APIs are tenant-scoped and capability-protected;
- sensitive prompt, skill, reference, input, output, and tool payload fields are summarized or redacted for normal readers;
- forbidden, disabled-user, disabled-agent, unassigned skill, unassigned reference, missing `read_skill` or `read_reference` tool-boundary grant, tool-boundary denial, missing chat confirmation, and cross-tenant tests include both denial response and trace emission assertions.

## Runtime completion rule

For a named generated-app agent feature, completion also requires a local Akka invocation through the intended governed runtime path: active `AgentDefinition` resolution, approved prompt/skill/reference manifest assembly, `ToolPermissionBoundary` enforcement, `readSkill`/`readReferenceDoc` loader tools where assigned, `effects().tools(runtimeTools)` registration, provider/model configuration checks, trace emission, and the endpoint/workstream surface that users exercise. For confirmed human chat tool execution, the smoke path must include the plan proposal, explicit confirmation, governed-tool execution/denial, trace facts, and result or partial-failure surface. If a real provider/security configuration is unavailable, the runtime path must fail closed with an actionable error and the feature remains blocked/not complete; do not substitute `TestModelProvider` or canned responses as user-facing behavior.

## Review checklist

Before finishing, verify:
- each test-only request-based agent has a registered `TestModelProvider`
- session ids are supplied in agent calls
- structured responses are serialized with `JsonSupport.encodeToString(...)`
- workflow tests use `Awaitility` when completion is asynchronous
- endpoint tests use `httpClient`, not `componentClient`
- governed runtime tests cover active profile resolution, disabled/archived denial, draft-only test/replay behavior, unapproved activation denial, unassigned-skill denial, unassigned-reference denial, cross-tenant and wrong-customer denial, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `ToolPermissionBoundary` denial for `read_skill` and `read_reference`, `AgentBehaviorEditorAgent` proposal flow, and authority expansion approval/denial
- feature-bearing request-based agent work has a recorded local runtime smoke/manual path or is explicitly blocked/not complete
- Autonomous Agent tests are covered by `akka-autonomous-agent-testing`, not forced into request-based Agent test patterns
