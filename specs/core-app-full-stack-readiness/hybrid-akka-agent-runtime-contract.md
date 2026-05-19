# Hybrid Akka Agent Runtime Contract

## Purpose

Define the implementation handoff between tenant-governed Agent Admin records and static Java Akka `Agent` classes for generated secure AI-first SaaS applications.

This is a specification artifact. It does not change runtime code. Future code tasks should use it as the contract for `AgentRuntimeResolver`, Java Agent wrappers, tools, endpoint/workflow invocation surfaces, traces, and tests.

## Scope

Included:

- Resolver inputs/outputs for runtime, test, replay, and evaluation modes.
- Production lookup sources for active `AgentDefinition`, prompt, skill manifest, tool boundary, and model policy.
- Static Java Agent binding and component-client invocation handoff.
- Deterministic prompt assembly with compact manifest only.
- `readSkill(skillId)` authorization and `SkillLoadTrace` emission.
- Backend-enforced `ToolPermissionBoundary` checks for local, component, MCP, data, and side-effecting tools.
- Safe model selection through governed `ModelConfigRef` / `ModelPolicy` before Java Agent invocation.
- Trace emission for allowed, denied, approval-required, replay, evaluation, and error paths.
- Safe denial shapes and redaction rules.
- Runtime and governed-agent tests.

Excluded and deferred:

- Concrete durable entity/view implementation for Agent Admin artifacts; specified by `agent-admin-component-api-slice.md`.
- Durable trace storage/search/redaction internals; specified by the Audit/Trace core module.
- Governance/Policy decision-card workflow internals for approval-required side effects; this contract only defines runtime handoff points.
- Concrete React components and workstream UI rendering.

## Core invariant

Static Java Agent code is an execution adapter, not the source of tenant behavior or authority.

Governed records decide:

- whether an agent may run;
- which prompt version is active;
- which skills can be advertised in the compact manifest;
- which skills can be loaded by `readSkill(skillId)`;
- which tools/data/side effects are available;
- which model reference and fallback policy are allowed;
- which traces and audit facts must be emitted.

Java Agent classes provide:

- the SDK `Agent` component binding;
- the single command handler shape;
- structured request/response mapping;
- optional SDK memory/session behavior when explicitly allowed;
- SDK model invocation through an already-resolved model/config context;
- registered tool bindings that call backend enforcers before protected work.

Prompt text, skill text, tool descriptions, compact manifests, and model output never grant authorization, data access, approval authority, or tool permissions.

## Runtime sequence

```text
caller surface
→ resolve caller or trusted runtime actor
→ resolve selected AuthContext and requested capability
→ authorize the invocation capability
→ create or propagate correlationId/workTraceId
→ AgentRuntimeResolver.resolve(request)
→ load active governed records and model policy
→ deny before model invocation if any prerequisite fails
→ assemble effective prompt with compact manifest only
→ emit PromptAssemblyTrace for allowed or denied assembly
→ select static Java Agent binding from runtimeClassRef
→ invoke Java Agent through componentClient.forAgent().inSession(...)
→ enforce ToolPermissionBoundary for every tool request
→ authorize readSkill(skillId) through manifest + boundary + skill version
→ emit SkillLoadTrace, ToolInvocationTrace, AgentWorkTrace, and AdminAuditEvent facts
→ return browser/service-safe success, denial, approval-required, or error result
```

## Invocation surfaces

All surfaces call the same resolver contract and must not assemble prompts inline.

| Surface | Required handoff |
|---|---|
| HTTP/browser API | Authenticate WorkOS/AuthKit JWT, resolve selected tenant/customer `AuthContext`, check route capability, create correlation id, call resolver, return redacted DTO. |
| Test console | Require `agent.runtime.test`, mode `test`, allow permitted draft overrides, force no production side effects unless explicitly approved. |
| Workflow step | Store tenant/customer, capability id, agent id, approval state, idempotency key, and trace ids in workflow state; reauthorize after pauses. |
| Timed action | Carry scheduled authority basis and policy ref; deny platform-wide authority unless explicitly granted and traced. |
| Consumer | Carry event provenance and tenant/customer scope; deny if event cannot establish runtime authority. |
| MCP/service boundary | Validate service identity/JWT/ACL, propagate scope and correlation ids, and enforce the same capability contract. |
| Replay/evaluation | Use pinned historical refs or authorized draft refs, label mode, disable side effects by default, and emit replay/evaluation traces. |

## Resolver request

```text
ResolveAgentRuntimeRequest
- tenantId
- selectedCustomerId optional
- accountId or trustedRuntimeActorId
- actorType: user | workflow | timer | consumer | service | evaluator
- agentDefinitionId
- requestedCapabilityId
- mode: runtime | test | replay | evaluation
- correlationId
- workTraceId optional; create if absent
- sessionId optional for Java Agent session
- invocationSurface: http | workflow | timer | consumer | mcp | test_console | replay | evaluation
- idempotencyKey optional for consequential side effects
- requestedPromptOverride optional; allowed only in test/replay/evaluation with capability
- requestedSkillVersionOverrides optional; allowed only in test/replay/evaluation with capability
- pinnedHistoricalRefs optional; required for exact replay
- inputRedactionClass and responseRedactionClass
```

## Resolver output

```text
ResolvedAgentRuntime
- decision: allowed | denied | approval_required
- safeDenialReason optional
- AuthContext snapshot
- agentDefinition id/version/status/authority/placement/runtimeClassRef
- promptDocumentId/promptVersionId/promptChecksum
- assembledSystemPrompt
- skillManifestId/manifestVersion/compactManifestText/manifestChecksum
- toolBoundaryId/boundaryVersion/toolGrantSnapshot
- modelConfigRefId/modelPolicyRefId/providerAlias/fallbackPolicy summary
- mode and invocationSurface
- correlationId/workTraceId/sessionId
- authorizationBasis summary
- traceRefs: promptAssemblyTraceId, agentWorkTraceId, optional denialTraceId
- redactionPolicy summary
```

The resolver fails closed. A denied result is a valid output and must include safe caller-facing text plus trace ids when trace persistence is available.

## Production lookup sources

Future code should prefer `AgentRuntimeLookupView` for fast reads and component clients for authoritative validation when freshness matters.

| Runtime input | Source | Required checks |
|---|---|---|
| `AgentDefinition` | `AgentRuntimeLookupView` or `AgentDefinitionEntity` component client | same tenant, active for runtime, not disabled/archived, caller may invoke requested capability, runtimeClassRef exists, authority level allows mode. |
| Prompt | `PromptVersionEntity` / prompt lookup view | same tenant/document, approved active for runtime; draft only in authorized non-runtime mode; checksum and secret-boundary validation pass. |
| Skill manifest | `AgentSkillManifestEntity` / manifest view | same tenant/agent, active approved for runtime; entries reference approved skills; compact manifest only. |
| Tool boundary | `ToolPermissionBoundaryEntity` / boundary view | same tenant/agent, active, deny-by-default, grants reference registry tool ids. |
| Model config | `ModelConfigRefEntity` / model catalog + `ModelPolicyEntity` | no secrets, active, allowed provider alias, allowed mode, agent, capability, and authority level; explicit fallback policy. |
| Trace sink | trace component/client, audit entity, or trace pipeline | accepts allowed and denied facts; redacts content according to policy. |

If a lookup view and authoritative component disagree, deny or retry through the authoritative component. Do not invoke the model with stale or partially resolved runtime state.

## Mode matrix

| Check | runtime | test | replay | evaluation |
|---|---|---|---|---|
| Active `AgentDefinition` required | Yes | Usually; draft allowed only with `agent.runtime.test` and explicit draft id | Historical/pinned or active; disabled allowed only for inspection replay | Active or pinned evaluation target |
| Approved active prompt required | Yes | Active or authorized draft override | Pinned historical or authorized draft | Active, candidate, or pinned according to evaluation plan |
| Approved active skills required | Yes | Active or authorized draft skill versions | Pinned historical or authorized draft | Active/candidate as plan allows |
| Side effects | Allowed only by boundary/policy | Disabled by default | Disabled | Disabled by default; explicit sandbox only |
| Tool boundary | Active boundary required | Active or test boundary required | Historical or active boundary required | Active/evaluation boundary required |
| Model policy | Active policy required | Test-allowed model required | Historical model ref or replay-safe substitute | Evaluation model policy required |
| Trace label | `runtime` | `test` | `replay` | `evaluation` |
| Browser/API response | normal redacted result | test banner + trace refs | replay banner + trace refs | evaluation summary + trace refs |

## Static Java Agent binding

`AgentDefinition.runtimeClassRef` maps governed runtime records to implementation-owned Java Agent components.

Required binding fields:

```text
RuntimeClassRef
- componentId
- javaClassName or adapterId
- commandName
- requestSchemaVersion
- responseSchemaVersion
- supportsStreaming: true/false
- allowedModes
- requiredModelProviderCapability optional
- registeredToolSetId optional
```

Rules:

1. Runtime class refs are implementation-owned safe identifiers, not user-provided class names.
2. Tenant admins may select only allowed runtime class refs exposed by the app.
3. The resolver denies missing, disabled, unsupported-mode, or incompatible schema refs.
4. The Java Agent command receives assembled prompt/profile context from the resolver or from a runtime adapter; it does not resolve governed artifacts itself.
5. Static agent prompts are minimal adapter prompts only. Tenant behavior comes from governed prompt versions.
6. Agent classes remain stateless and keep one public command handler, per `akka-agent-component` guidance.

## Component-client invocation handoff

Future implementation may use an adapter service similar to:

```text
ManagedAgentInvoker.invoke(resolvedRuntime, userInput)
→ choose runtimeClassRef
→ build AgentRequestEnvelope
→ componentClient.forAgent().inSession(sessionId).method(...).invoke(envelope)
→ wrap result in AgentRuntimeResult
→ emit completion/error AgentWorkTrace
```

`AgentRequestEnvelope` should include:

- user/task input after validation and redaction;
- assembled system prompt or prompt template variable values;
- compact manifest text;
- mode label;
- AuthContext reference, not secret-bearing credentials;
- correlation/work trace ids;
- tool-boundary snapshot id/version;
- model config ref id/provider alias, not provider secret;
- response schema/version and redaction policy.

Do not pass raw provider credentials, WorkOS tokens, user JWTs, hidden platform secrets, cross-tenant ids, or full unrequested skill text to the model.

## Prompt assembly contract

`PromptAssembler` deterministically layers:

1. implementation-owned non-secret platform guardrails;
2. active governed prompt version content;
3. compact skill manifest only;
4. tool-boundary summary suitable for model guidance;
5. mode label and no-side-effect/test/replay banner when applicable;
6. task-specific user input after redaction and schema validation.

Assembly output must be stable for the same refs/checksums/mode/AuthContext. `PromptAssemblyTrace` records refs, checksums, mode, caller/capability summary, allowed/denied decision, and redaction classification.

Full skill text is never included during initial prompt assembly. It is available only through `readSkill(skillId)`.

## `readSkill(skillId)` contract

`readSkill(skillId)` is a governed tool backed by application state.

Allowed path:

```text
model requests skill id
→ tool wrapper maps to stable read_skill tool id
→ ToolBoundaryEnforcer verifies READ_SKILL grant for mode and agent
→ SkillReadAuthorizer verifies active invocation, tenant, agent, manifest, skill id, skill document, skill version, mode, size, and secret validation
→ SkillLoadTrace emitted
→ approved content returned with checksum and authority note
```

Required denials:

- unknown or unassigned skill id;
- inactive/unapproved skill in runtime mode;
- cross-tenant document/version;
- disabled/archived agent outside authorized inspection/replay;
- missing `READ_SKILL` grant in active tool boundary;
- unsupported mode;
- oversized or secret-like content;
- missing caller capability for test/replay override.

Denied reads return a safe model-visible denial and emit `SkillLoadTrace` without leaking whether a cross-tenant skill exists.

## Tool-boundary enforcement contract

Every local function tool, Akka component tool, MCP tool, data lookup, email/external side-effect tool, and `readSkill` tool is checked by `ToolBoundaryEnforcer` before protected work.

Minimum tool-call input to enforcer:

```text
ToolInvocationRequest
- tenantId/customerId
- agentDefinitionId
- boundaryId/boundaryVersion
- toolId
- toolCategory
- capabilityId
- operation: read | propose | request_approval | execute
- mode
- AuthContext/caller capability summary
- inputSummary and redaction class
- idempotencyKey when side-effecting
- correlationId/workTraceId
```

Decision results:

- `allowed`: execute binding and trace access/side effect;
- `denied`: do not execute; return safe denial and trace reason;
- `approval_required`: do not execute; create or reference decision-card/proposal request when available; trace policy ref.

Rules:

1. Tool ids come from the backend registry, never from model-supplied names, URLs, resource paths, component ids, or MCP server names.
2. Component tools validate `uniqueId` tenant/customer/aggregate scope before execution.
3. MCP tools use an allowlist/filter that matches active registry grants and remote endpoint ACLs.
4. Side effects require idempotency and trace policy.
5. Security, billing, role/membership, external email/message, export, delete, cross-customer, and irreversible operations default to `approval_required` unless a documented policy grants bounded autonomy.
6. Approval-required responses do not perform the side effect.

## Model resolution contract

`ModelConfigRef` and `ModelPolicy` are resolved before Java Agent invocation.

Checks:

- model config is active and same tenant/platform-scope allowed;
- provider alias is allowed and not denied by policy;
- mode is allowed;
- agentDefinitionId, requestedCapabilityId, and authority level are allowed;
- fallback policy is explicit (`noFallback` is valid);
- provider secrets are absent from governed state, prompts, skills, traces, browser DTOs, and model-visible context;
- model-use trace obligation is recorded.

If primary model selection fails, fallback may be used only when policy explicitly permits it. Fallback choice is traced. If no fallback is permitted, deny before model invocation.

## Trace and audit obligations

Emit durable facts or enqueue them for durable persistence for both allowed and denied paths.

| Trace | When | Minimum fields |
|---|---|---|
| `PromptAssemblyTrace` | resolver allow/deny, test/replay/evaluation assembly | tenant, agent, prompt refs, manifest ref, boundary ref, model ref, mode, caller/capability summary, correlation/workTrace ids, checksum, decision, denial reason. |
| `SkillLoadTrace` | every `readSkill` allowed/denied | tenant, agent, manifest ref, skill id/document/version when allowed, mode, decision, denial reason, correlation/workTrace ids. |
| `ToolInvocationTrace` | every tool allowed/denied/approval-required | tenant/customer, agent, boundary ref, tool id/category/capability, operation, mode, decision, policy/approval refs, safe input/result summaries, idempotency key. |
| `AgentWorkTrace` | invocation start/end/error and result summary | tenant/customer, agent, surface, mode, capability, actor, correlation/workTrace ids, model ref/provider alias, result status, trace refs. |
| `AdminAuditEvent` | consequential admin/runtime denials and changes | actor, AuthContext, artifact refs, command/query/tool, allowed/denied, reason, timestamp. |

Normal readers receive summaries and trace refs. Raw prompt, skill, input, output, or tool payload access requires explicit sensitive-trace capability and redaction policy.

## Safe denial and redaction contract

Safe denials:

- use generic unavailable wording for cross-tenant or existence-sensitive records;
- identify actionable local issues when safe (for example, `agent is disabled`, `missing invoke capability`, `approval required`);
- include correlation/trace id when available;
- never include provider secrets, JWTs, API keys, raw hidden prompts, unauthorized skill text, cross-tenant artifact ids, or internal stack traces.

External/browser shape:

```text
AgentRuntimeDeniedDto
- status: denied | approval_required | validation_error | unavailable
- message
- correlationId
- traceRef optional
- approvalRequestRef optional
- retryable: true/false
```

Model-visible tool denial shape:

```text
ToolResultDenied
- decision: denied | approval_required
- safeReason
- instruction: continue without this tool, ask for approval, or escalate according to the active workflow
```

## Error handling

- Resolver prerequisite failures return denied results, not thrown model errors.
- Java Agent/model provider failures use explicit fallback handling from the Agent class only after governed model policy permits fallback.
- Tool execution errors are traced with safe summaries and do not expose stack traces to the model or browser.
- Partial trace persistence failure should not grant runtime authority. For consequential runtime calls, deny or degrade according to trace policy; for non-consequential test runs, record a visible warning.

## Reference-code alignment

`ReferenceAgentRuntimeResolver` already demonstrates fail-closed resolution for:

- invoke capability;
- same-tenant `AgentDefinition`;
- active agent status;
- active prompt version;
- active skill manifest;
- active tool boundary;
- compact prompt assembly;
- `PromptAssemblyTrace` for allowed and denied resolution.

Production code must extend that reference with:

- component/view lookups instead of constructor-provided maps;
- explicit resolver request/output records and mode matrix;
- safe `ModelConfigRef` / `ModelPolicy` resolution;
- `runtimeClassRef` validation and Java Agent component invocation;
- generalized `ToolBoundaryEnforcer` for all tools, not only `readSkill`;
- `SkillReadAuthorizer` using active manifest plus boundary;
- `AgentWorkTrace` and tool invocation traces;
- side-effect approval-required behavior;
- replay/evaluation/test-console mode constraints;
- safe denial DTOs and redaction policy.

## Implementation handoff order

1. Implement resolver request/output records and safe denial DTOs.
2. Implement runtime lookup clients for `AgentRuntimeLookupView` and authoritative component fallback.
3. Implement `ModelConfigRef` / `ModelPolicy` resolver and provider-secret boundary tests.
4. Implement deterministic `PromptAssembler` with compact manifest and trace checksums.
5. Implement `ToolBoundaryEnforcer` over registry-backed stable tool ids.
6. Implement `SkillReadAuthorizer` and `readSkill(skillId)` tool wrapper.
7. Implement `ManagedAgentInvoker` that maps `runtimeClassRef` to Java Agent component-client calls.
8. Wire HTTP/test-console/workflow/timer/consumer/MCP invocation surfaces to the resolver.
9. Emit `PromptAssemblyTrace`, `SkillLoadTrace`, `ToolInvocationTrace`, `AgentWorkTrace`, and `AdminAuditEvent` facts.
10. Add governed runtime tests and endpoint/workflow integration tests.

## Required tests for future code tasks

### Resolver and profile tests

- Active runtime resolution succeeds with authorized same-tenant `AuthContext`.
- Missing invoke capability is denied before model invocation.
- Cross-tenant agent/prompt/manifest/boundary/model refs are denied without existence leakage.
- Disabled, archived, and draft agents are denied in runtime mode.
- Draft prompt or skill overrides are allowed only in authorized test/replay/evaluation mode.
- Missing prompt, manifest, boundary, model ref, or runtimeClassRef denies activation/runtime.
- Lookup-view staleness falls back to authoritative validation or denies safely.

### Prompt and model tests

- Prompt assembly is deterministic for identical refs/mode/AuthContext.
- Assembled runtime prompt contains compact manifest but not full skill text.
- `PromptAssemblyTrace` is emitted for allowed and denied assembly.
- Model policy denies disabled provider alias, forbidden mode, forbidden agent/capability, forbidden authority level, and implicit fallback.
- Provider secrets are absent from prompts, traces, DTOs, and model-visible context.

### `readSkill` and skill-load tests

- Assigned active skill load succeeds and emits `SkillLoadTrace`.
- Unassigned, inactive, wrong-version, oversized, unauthorized-mode, disabled-agent, and cross-tenant reads are denied safely.
- `readSkill` requires both active manifest assignment and active `ToolPermissionBoundary` `READ_SKILL` grant.
- Denied cross-tenant skill reads do not reveal existence.

### Tool-boundary tests

- Allowed read-only local/data/component/MCP tool calls execute and trace.
- Ungranted tool id/category and free-form model-supplied tool names are denied.
- Component-tool `uniqueId` tenant/customer scope is enforced.
- MCP allowed-tool filtering matches active boundary grants.
- Side-effecting tool under read-only grant is denied.
- Approval-required side effect returns approval-required result and does not execute.
- Side-effecting duplicate idempotency keys are handled according to capability contract.

### Invocation-surface tests

- HTTP endpoint authenticates, authorizes, calls resolver, and returns redacted success/denial DTOs.
- Test console labels test mode, blocks production side effects, permits only authorized draft overrides, and emits traces.
- Workflow step reauthorizes after pause before invoking the agent.
- Timer/consumer invocations require provenance and scoped authority basis.
- Replay/evaluation invocations are mode-labeled, side-effect safe by default, and trace pinned refs.

### Trace, audit, and redaction tests

- Allowed and denied invocations emit `AgentWorkTrace` or audit facts with correlation ids.
- Tool, skill, and prompt traces link through the same workTraceId.
- Normal trace/search DTOs redact prompt, skill, input, output, and tool payloads.
- Forbidden, disabled-user, disabled-agent, missing-scope, unassigned-skill, tool-denial, model-denial, and cross-tenant cases assert both denial response and trace emission.

## Done handoff for future code generation

A future implementation task can proceed when it preserves these decisions:

- Governed state is resolved and authorized before Java Agent invocation.
- Runtime code uses compact manifest in prompts and `readSkill(skillId)` for full skill text.
- Active `ToolPermissionBoundary` enforces every tool/data/side-effect request mechanically.
- Safe `ModelConfigRef` / `ModelPolicy` determines provider alias and fallback without exposing secrets.
- Static Java Agent classes are adapter components selected by safe `runtimeClassRef` values.
- Runtime, test, replay, and evaluation modes have explicit different permissions and trace labels.
- Denials fail closed, return safe shapes, and emit trace/audit facts.
