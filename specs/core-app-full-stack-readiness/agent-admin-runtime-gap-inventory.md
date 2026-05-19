# Agent Admin and Hybrid Runtime Gap Inventory

## Purpose

Inventory the current governed-agent reference coverage against the full-core Agent Admin and hybrid Akka agent runtime target. This is a planning artifact only; no production code is changed in this task.

## Inputs reviewed

- `docs/agent-coverage-matrix.md`
- `docs/agent-runtime-invocation-pattern.md`
- `skills/akka-agent-behavior-profiles/SKILL.md`
- `skills/akka-agent-prompt-governance/SKILL.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- `skills/akka-agent-model-governance/SKILL.md`
- `src/main/java/com/example/domain/agentfoundation/**`
- `src/main/java/com/example/application/agentfoundation/**`
- supporting reference API/UI/test files found during inventory

## Current executable/reference coverage

| Area | Existing reference coverage | Status |
|---|---|---|
| `AgentDefinition` | `ReferenceAgentDefinition`, fixture seed records, `ReferenceAgentRuntimeResolver`, resolver tests for active/disabled/cross-tenant behavior. | Partial production shape: runtime lookup semantics exist, but durable component/API contracts are not specified. |
| Prompt governance | `ReferencePromptDocument`, `ReferencePromptVersion`, deterministic `ReferencePromptAssembler`, `ReferencePromptAssemblyTrace`, seed import, behavior-edit prompt proposals, resolver tests. | Partial production shape: active prompt assembly is executable; full PromptDocument lifecycle, diff/history/review APIs and views are not implemented/speced here. |
| Skill governance | `ReferenceSkillDocument`, `ReferenceSkillVersion`, `ReferenceAgentSkillManifest`, `ReferenceSkillReadAuthorizer`, `ReferenceAgentSkillTools`, `ReferenceSkillLoadTrace`, seed import, readSkill tests. | Good minimal runtime reference; missing production manifest/document lifecycle contracts and admin APIs/UI. |
| Tool boundaries | `ReferenceToolPermissionBoundary`, resolver boundary lookup, readSkill tool grant enforcement, behavior-edit boundary proposal tests. | Partial: deny-by-default `readSkill` boundary exists; no registry/catalog shape or side-effecting/component/MCP tool boundary reference. |
| Model config | `ReferenceAgentDefinition.modelConfigRef` stores a safe string. Skills document the required `ModelConfigRef` governance. | Gap: no executable governed `ModelConfigRef`, policy resolver, fallback policy, or secret-boundary tests beyond static configured model examples elsewhere. |
| Seed import | `ReferenceAgentBehaviorSeedManifest`, `ReferenceAgentBehaviorSeedLoader`, classpath seed resources, provenance, idempotency, checksum, upgrade/customization preservation tests. | Strong reference for seed mechanics; needs production component/API handoff and bootstrap carrier decision. |
| Behavior editing | `ReferenceAgentBehaviorEditor`, `ReferenceAgentBehaviorEditorAgent`, `ReferenceBehaviorEditReviewService`, proposal/decision/trace records, tests for prompt/skill/manifest/tool-boundary/agent-definition proposals and authority expansion. | Strong proposal semantics; missing durable workflow/entity contracts for proposal lifecycle and activation handoff. |
| Test console/runtime API | `ManagedReferenceAgentEndpoint` provides a narrow `/agentfoundation/managed-reference-agent/invoke` test-console-like endpoint and integration tests. | Useful reference only; not a full Agent Admin API surface and currently `@Acl(ALL)` with request-body auth fixture semantics. |
| Runtime resolver | `ReferenceAgentRuntimeResolver` resolves AuthContext capability, active AgentDefinition, active prompt, active manifest, active tool boundary, compact prompt assembly, and prompt assembly trace before Java Agent invocation. | Good hybrid runtime reference; missing model policy enforcement, durable lookups, general tool enforcement, mode-specific draft/replay rules, and production component clients. |
| Traces | In-memory `ReferenceTraceSink` records prompt assembly, skill load, agent work, behavior edit, and improvement traces. | Good trace fact examples; missing durable trace storage/search/redaction/API/UI contracts for Agent Admin and Audit/Trace. |
| Frontend | Workstream fixtures include `Agent Admin` rail item with denied state. Seed app-description has Agent Admin UI references. | Gap: no concrete Agent Admin workstream components/fixtures for definitions, prompts, skills, manifests, tool boundaries, model refs, test console, proposals, or traces. |
| Tests | Unit/integration tests cover active resolution, disabled denial, cross-tenant prompt/manifest denial, readSkill allowed/denied, seed import, behavior proposals, review decisions, endpoint denial/success, no hidden prompt/skill text in endpoint response. | Strong reference tests; missing production API/view/component tests, tenant isolation across durable state, model config, broad tool boundaries, frontend UI tests. |

## Ordered gaps for follow-up tasks

### G1. Production-shaped Agent Admin component contracts are missing

Need an implementation-ready slice that defines Akka components, commands, events, views, APIs, tests, and UI contracts for:

- `AgentDefinitionEntity` lifecycle: create draft, update metadata, set prompt/skill/model/tool refs, set authority, activate, disable, archive.
- `PromptDocumentEntity` and immutable `PromptVersion` snapshots.
- `SkillDocumentEntity` and immutable `SkillVersion` snapshots.
- `AgentSkillManifest` state/versioning and assignment semantics.
- `ToolPermissionBoundary` state/versioning and approval-required changes.
- safe `ModelConfigRef` and model policy state.

Follow-up: `TASK-CORE-03-002` should produce `agent-admin-component-api-slice.md`.

### G2. Agent Admin HTTP/API contract is not defined

Current `ManagedReferenceAgentEndpoint` is a narrow managed-agent invocation fixture. It does not provide production Agent Admin APIs for:

- agent catalog/list/detail/filter;
- agent create/edit/activate/disable/archive;
- prompt create/edit/submit/review/activate/rollback/test;
- skill catalog/edit/review/activate/history;
- manifest assignment and compact manifest preview;
- tool registry and ToolPermissionBoundary draft/review/activate;
- safe model config read/select;
- behavior-edit proposal review;
- trace lookups for prompt/skill/tool/work facts.

The API contract must use selected `AuthContext`, backend capability checks, tenant/customer scope, redacted DTOs, idempotency keys where appropriate, and audit/trace emission.

### G3. Runtime resolver needs a production handoff contract

The reference resolver proves the sequence but not production integration. The hardened runtime contract must define:

- component-client or view lookup sources for active AgentDefinition, prompt version, manifest, boundary, model config, and policy;
- mode rules for `runtime`, `test`, `replay`, and `evaluation`;
- draft/unapproved prompt and skill use only in authorized non-runtime modes;
- model policy and fallback resolution before Java Agent invocation;
- boundary enforcement for every local, component, MCP, data, and `readSkill` tool call;
- correlation/work trace id creation and propagation;
- safe denial shapes and redaction rules for endpoints/tools;
- where Java static `Agent` code ends and governed tenant behavior begins.

Follow-up: `TASK-CORE-03-003` should produce `hybrid-akka-agent-runtime-contract.md`.

### G4. Governed `ModelConfigRef` remains the largest executable gap

Current main-source agentfoundation code stores `modelConfigRef` only as a string. The required full-core target needs:

- `ModelConfigRef` state with tenant/platform scope, provider alias, allowed modes, allowed agent/capability refs, status, fallback policy ref, and no secrets;
- `ModelPolicy` checks for allowed/denied providers, authority level, task/capability scope, and fallback behavior;
- runtime denial before model invocation for disabled/cross-tenant/unauthorized model refs;
- tests proving provider secrets are absent from browser DTOs, prompts, skills, traces, and model-visible context.

### G5. ToolPermissionBoundary is limited to `readSkill`

Existing code validates `readSkill` grants, but full core needs a broader tool registry and runtime boundary model:

- stable tool ids and capability ids for local function, component, MCP, data lookup, `read_skill`, and side-effecting tools;
- read-only vs side-effecting classification;
- customer/tenant scope and data classification limits;
- approval-required responses for high-impact operations;
- idempotency and trace fields for side effects;
- component-tool unique-id scope rules and MCP allowed-tool filtering.

### G6. Prompt and skill lifecycle coverage is runtime-only, not admin-lifecycle complete

The current reference demonstrates active runtime versions and behavior-edit proposed diffs. Missing production lifecycle details include:

- draft/in-review/approved/active/deprecated/archive transitions;
- immutable version creation from document lifecycle events;
- diff/history/review queue views;
- rollback/deprecate semantics;
- secret-like content validation and token limits before activation;
- activation readiness checks tying AgentDefinition, prompt, skill manifest, model ref, and tool boundary together.

### G7. Seed import needs a production carrier and audit path

The seed loader demonstrates the right import behavior in memory. Production handoff must decide and specify whether first-install/tenant-bootstrap seed import runs as:

- a bootstrap action;
- an internal endpoint restricted by ACL;
- a workflow;
- a timed/consumer-driven bootstrap process.

It must also specify persistence through governed components, idempotency keys, provenance fields, audit events, and upgrade proposal behavior without overwriting tenant customizations.

### G8. Behavior editing proposals need durable review/activation wiring

Current proposal/review helpers intentionally do not mutate active records. Full core still needs:

- durable proposal records and decision cards;
- workflow or entity commands for review, request-changes, approve, reject, escalate;
- activation handoff from approved proposal to PromptDocument/SkillDocument/Manifest/ToolBoundary/AgentDefinition commands;
- authority-expansion policy checks and audit traces;
- UI/API surfaces for proposed diffs and decision-card review.

### G9. Trace records are not durable/searchable/redacted

The in-memory trace sink is sufficient for reference tests but not full core. Need contracts for:

- durable `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`, tool invocation trace, and admin audit events;
- search/list/timeline views by tenant, customer, agent, correlation id, actor, artifact, and decision;
- redaction policy and sensitive-field access capability;
- trace links from Agent Admin surfaces to Audit/Trace surfaces;
- denial traces for unauthorized prompt/skill/tool/model/runtime attempts.

### G10. Agent Admin frontend is only represented as a rail fixture

Current frontend fixture proves the functional-agent rail can show a denied Agent Admin item. Missing UI references include:

- agent catalog and detail;
- lifecycle cards and activation readiness;
- prompt editor, diff/history/review, activation, rollback, and test console;
- skill catalog/editor/history and manifest management;
- tool registry/boundary editor with proposed diff and denial history;
- model reference display without secrets;
- behavior-edit proposal review and trace-linked result surfaces;
- loading, empty, forbidden, validation, approval-required, stale/reconnect, accessibility, and responsive states.

Follow-up UI work is blocked until `TASK-CORE-03-002` defines realistic API contracts.

## Recommended implementation order

1. Specify the Agent Admin component/API slice (`TASK-CORE-03-002`) with durable state boundaries, protected API DTOs, views, UI contract, and tests.
2. Harden the hybrid runtime resolver contract (`TASK-CORE-03-003`) so static Java Agents invoke only after governed active records, model policy, and tool boundaries are resolved.
3. Fold the resulting API contracts into workstream UI contract alignment (`TASK-CORE-04-001`).
4. Add Agent Admin workstream references (`TASK-CORE-04-002`).
5. Carry trace storage/search/redaction obligations into Audit/Trace core module (`TASK-CORE-05-001`).
6. Carry proposal/decision-card activation obligations into Governance/Policy core module (`TASK-CORE-05-002`).
7. Capture all Agent Admin/runtime acceptance and security tests in the full-core acceptance matrix (`TASK-CORE-06-001`).

## Non-goals for this inventory task

- No production code rewrite.
- No new Akka components.
- No frontend implementation.
- No changes to existing executable reference tests.
