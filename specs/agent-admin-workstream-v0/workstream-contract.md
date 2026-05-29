# Agent Admin Workstream v0 Contract

## Purpose

Define the v0 contract for the `Agent Admin Agent` workstream in the secure AI-first SaaS starter/reference runtime. This contract inherits the shared five-core v0 contract and narrows it to governed managed-agent administration: AgentDefinitions, prompts, skills, references, manifests, model refs, tool boundaries, default seeds, behavior-change controls, and runtime visibility.

## Scope boundary

Agent Admin v0 is a focused vertical, not full-core SaaS behavior governance. It must be useful enough for an authorized administrator to inspect and govern the managed-agent runtime while preserving production-like runtime standards.

In scope:

- role-authorized Agent Admin workstream entry and request/response turns;
- governed managed-agent configuration records and active/default seed visibility;
- prompt, skill, reference, manifest, model-ref, and ToolPermissionBoundary lifecycle actions;
- safe proposal/review/activation flow for behavior-changing updates;
- trace and audit links for prompt assembly, skill/reference loading, tool-boundary decisions, model/provider blocked states, and behavior changes;
- deterministic resolver, validator, authorizer, seed, projection, redaction, and lifecycle services;
- one explicitly justified AutonomousAgent-backed internal/background capability for behavior evaluation or improvement review when implemented in the runtime task.

Out of scope for v0:

- implementing unrelated workstreams except through already-existing dependencies;
- full policy/governance expansion beyond Agent Admin behavior-change controls;
- tenant self-service marketplace or broad model-provider administration;
- deterministic or mock/model-less normal runtime responses for model-backed workstream turns.

## Functional agent responsibility

`Agent Admin Agent` is the user-facing functional/context-area agent for managed-agent administration. It answers bounded administrator requests, explains current behavior configuration, drafts safe change proposals, and navigates the administrator to structured surfaces.

Normal user-facing turns are request/response Akka `Agent` turns. A turn is complete only when the runtime path resolves the selected `AuthContext`, active governed managed-agent `AgentDefinition`, prompt/skill/reference manifests, ToolPermissionBoundary, model configuration, and trace records before invoking the concrete Akka Agent component. Direct provider calls, static canned responses, or deterministic substitutes do not satisfy this contract.

## Actors and authority

| Actor/caller | Authority intent | Guardrails |
|---|---|---|
| Tenant owner / administrator | Inspect definitions, prompts, skills, references, manifests, model refs, tool boundaries, seeds, traces, and draft/review/activate permitted changes. | Must have selected tenant/customer `AuthContext`, active membership, and Agent Admin capability grants. |
| Behavior reviewer / approver | Approve or reject behavior-changing proposals when the change affects prompt, manifest, model, reference, skill, or tool authority. | Approval must be explicit, audited, idempotent, and must not expand authority beyond backend permissions. |
| Agent Admin Agent | Explains configuration, reads assigned evidence, drafts proposals, and invokes only authorized tools. | ToolPermissionBoundary is backend-enforced; loaded skills/references are behavior guidance, not authorization. |
| Deterministic internal services | Resolve active config, validate manifests, enforce lifecycle, seed defaults, redact outputs, project status, and emit traces. | Not AI agents; cannot bypass AuthContext or audit. |
| Optional AutonomousAgent-backed reviewer | Runs durable background evaluation/review tasks where progress, result, notification, cancellation, or retry semantics are needed. | Task start/read/result/cancel must be governed capabilities with trace emission and provider fail-closed behavior. |

## Structured surfaces and actions

Agent Admin v0 should expose structured surfaces before page-specific mechanics:

| Surface | Purpose | Primary actions | Capability mapping |
|---|---|---|---|
| Agent catalog | Show managed-agent definitions, active/default status, authority tier, model ref, and health. | `list_agents`, `view_agent_definition`, `open_trace` | `agent_admin.list_definitions`, `agent_admin.get_definition`, trace read capability from Audit/Trace when available. |
| Agent definition detail | Show active AgentDefinition, lifecycle state, owner/steward, model ref, prompt/skill/reference manifests, and tool-boundary summary. | `view_prompt`, `view_manifest`, `view_tool_boundary`, `draft_change`, `open_trace` | `agent_admin.get_definition`, `agent_admin.get_prompt_version`, `agent_admin.get_manifest`, `agent_admin.get_tool_boundary`, `agent_admin.draft_behavior_change`. |
| Prompt/skill/reference detail | Show approved versions, status, provenance, assigned manifests, redacted content preview, and usage traces. | `compare_versions`, `draft_change`, `request_review`, `open_trace` | document read and proposal capabilities. |
| Tool boundary detail | Show allowed/denied tool ids, read-only vs side-effecting classification, approval requirements, and denial traces. | `simulate_tool_boundary`, `draft_boundary_change`, `open_denial_trace` | `agent_admin.get_tool_boundary`, `agent_admin.simulate_tool_boundary`, `agent_admin.draft_behavior_change`. |
| Behavior change proposal | Show proposed diff, risk/impact, authority change, evidence, reviewer decision status, and trace links. | `submit_for_review`, `approve_change`, `reject_change`, `activate_change`, `cancel_change` | proposal/review/activation capabilities. |
| Seed/default material | Show implementation-developed seed records and tenant override status. | `list_seed_material`, `reseed_missing_default`, `open_trace` | seed visibility/reseed capabilities. |
| Evaluation/review task | Show optional AutonomousAgent-backed task progress, findings, blocked/provider state, result, and notifications. | `start_review_task`, `read_task`, `cancel_task`, `open_trace` | autonomous task capabilities when implemented. |
| Safe blocked/error system message | Explain forbidden, validation, provider missing, tool denied, or config missing states. | `open_trace`, `retry_when_configured`, `request_access` | denial/trace/readiness capabilities. |

Frontend affordances must be driven by backend capability responses or `/api/me` browser-safe capability data. Hidden fields, prompt text, loaded skill text, or route names must not grant authority.

## Capability inventory summary

The detailed capability contracts live in `capability-inventory.md`. Required v0 capability groups:

1. Workstream request/response turn: `agent_admin.submit_turn`.
2. Managed-agent read/catalog: `agent_admin.list_definitions`, `agent_admin.get_definition`, document/manifests/tool-boundary/model-ref reads.
3. Behavior proposal and review: `agent_admin.draft_behavior_change`, `agent_admin.submit_behavior_change_for_review`, `agent_admin.approve_behavior_change`, `agent_admin.reject_behavior_change`, `agent_admin.activate_behavior_change`, `agent_admin.cancel_behavior_change`.
4. Tool-boundary simulation: `agent_admin.simulate_tool_boundary`.
5. Seed/default material: `agent_admin.list_seed_material`, `agent_admin.reseed_missing_defaults`.
6. Optional durable evaluation/review task: `agent_admin.start_behavior_review_task`, `agent_admin.get_behavior_review_task`, `agent_admin.cancel_behavior_review_task`.

## Agent-type selections

| Need | Selected substrate | Rationale |
|---|---|---|
| User-facing Agent Admin composer turn and markdown/structured explanation | request-based Akka `Agent` | Bounded request/response turn owned by the workstream shell. |
| Prompt/skill/reference/manifest/tool-boundary lifecycle state | Event Sourced Entity or equivalent audited stateful component in the starter baseline | Behavior-changing records need version history and audit-grade lifecycle. |
| Current active runtime resolution and catalog/read projections | deterministic service plus View/query surfaces | Mechanical lookup/projection; not model-driven. |
| Behavior change approval flow | Workflow or audited entity commands, depending on runtime shape | Explicit review/activation state and idempotent decisions. |
| Background evaluation/improvement review | Akka `AutonomousAgent`, only when durable task lifecycle is implemented | Requires task identity, progress snapshots, cancellation/failure, notifications, and result surfaces. |
| Validation, redaction, ToolPermissionBoundary enforcement, seed idempotency, trace normalization | deterministic non-AI services | Mechanical correctness; must not be mislabeled as AI behavior. |

## Request/response runtime contract

For every normal Agent Admin message turn:

1. resolve authenticated account and selected `AuthContext`;
2. check membership status and Agent Admin capability;
3. resolve the active governed managed-agent `AgentDefinition` for Agent Admin;
4. assemble approved prompt, compact AgentSkillManifest and AgentReferenceManifest entries, model ref, and allowed backend-owned tool registry ids;
5. enforce `ToolPermissionBoundary` before registering any runtime tools, including governed `readSkill(skillId)` and `readReferenceDoc(referenceId)`;
6. invoke the concrete Akka Agent component through the governed runtime path;
7. call the configured provider boundary for model-backed behavior;
8. emit PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, tool invocation/denial traces, model/provider traces, and AgentWorkTrace;
9. return a safe markdown_response or typed structured surface response, or a safe blocked/error surface when authorization/config/provider checks fail.

Missing or blank model/provider configuration must fail closed with an actionable surface and trace. It must not silently use deterministic fallback text.

## Authorization and AuthContext

All protected Agent Admin capabilities require:

- authenticated account;
- selected tenant/customer `AuthContext` where the target agent/config belongs;
- active membership and non-disabled account status;
- explicit role/scope/capability such as `agent_admin.read`, `agent_admin.propose`, `agent_admin.review`, `agent_admin.activate`, `agent_admin.seed`, or `agent_admin.evaluate`;
- tenant/customer filters on all reads/writes;
- backend checks in component commands, views, APIs, agent tools, workflow actions, timers, consumers, and AutonomousAgent task operations.

Support/SaaS-owner authority, if introduced later, must be separate from tenant/customer user authority and audited separately.

## Audit and trace contract

Agent Admin v0 must create or preserve durable records for:

- allowed and denied capability calls;
- definition/document/manifest/model/tool-boundary reads;
- behavior-change drafts, review decisions, activations, rejections, cancellations, and no-op/idempotent repeats;
- prompt assembly, skill/reference loads, model invocations, tool registrations, tool calls, and tool denials;
- seed/default material loads and reseed attempts;
- provider missing/blocked/failure states;
- AutonomousAgent task lifecycle events when the optional review task is implemented.

User-facing surfaces should include trace links or correlation ids wherever the workstream claims auditability.

## Validation path

Implementation tasks must prove the intended local path at the stated scope:

- backend tests for success, validation failures, forbidden access, tenant isolation, disabled/missing authority, idempotency/no-op behavior, audit/work trace emission, provider fail-closed behavior, and ToolPermissionBoundary denials;
- frontend tests/typecheck for surfaces, actions, blocked/forbidden/error states, sanitized markdown, trace links, and secret-boundary expectations;
- targeted fullstack or starter validation when runtime/API/UI behavior changes.

If real provider credentials are absent, validation may verify explicit skip behavior while preserving fail-closed runtime semantics, or record a bounded blocker/follow-up. Test doubles may appear only in tests or named test adapters, not as the normal user-facing runtime path.
