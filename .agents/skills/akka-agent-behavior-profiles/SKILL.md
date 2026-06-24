---
name: akka-agent-behavior-profiles
description: Design and implement tenant-scoped durable AgentDefinition and behavior profile state before prompt, skill, model, tool, or runtime execution details. Use when tasks mention agent admin UI, agent lifecycle, owner/steward, authority level, model config references, tool permission boundaries, or runtime behavior profiles.
---

# Akka Agent Behavior Profiles

Use this skill when the task is mainly about durable agent identity, lifecycle, authority, workstream placement, and runtime profile composition for an AI-first SaaS app.

This is not the skill for writing the Java `Agent` class itself. Use it before `akka-agent-component` when agents are managed runtime actors rather than only static code classes. For generated full-stack SaaS apps, classify each managed agent as either a user-facing functional/context-area agent or a bounded internal agent before designing profile fields and UI. Managed agents must use the governed runtime path: `AgentDefinition` → active prompt version → per-agent `AgentSkillManifest` compact list → `ToolPermissionBoundary` → Java Agent invocation with Akka-registered tools including `readSkill(skillId)`.

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
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/governed-agent-substrate.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/agent-coverage-matrix.md`
- `../docs/agent-runtime-invocation-pattern.md`
- `../akka-agent-model-governance/SKILL.md` when model config policy, provider aliases, fallbacks, or provider secret boundaries are in scope
- `../core-saas-foundation/SKILL.md`
- `../ai-first-saas-audit-trace/SKILL.md`
- `../akka-agents/SKILL.md`
- `../akka-event-sourced-entities/SKILL.md`
- `../akka-key-value-entities/SKILL.md`
- `../akka-views/SKILL.md`
- `../akka-http-endpoints/SKILL.md`

## Use when the request mentions

- `AgentDefinition`, agent profile, behavior profile, or runtime profile
- functional agent, context-area agent, internal agent, workstream placement, or left-rail agent catalog
- agent admin UI, catalog, list/detail, enable/disable, or archive
- agent owner, steward, maintainer, reviewer, or responsible team
- lifecycle states: draft, active, disabled, archived
- authority level: advisory, draft-only, approval-required, or autonomous within limits
- model configuration reference, `ModelConfigRef`, fallback model policy, provider secret boundary, or model policy
- allowed tools, denied tools, tool categories, or per-agent tool permission boundaries
- policy, approval, audit, trace, or tenant/customer scope for agent execution

## Core model

A durable behavior profile composes the inputs needed to decide whether an agent may run and what it may see or do:

```text
AgentDefinition
- tenantId
- agentDefinitionId
- displayName / description
- agentPlacement: functional_context_area | internal_worker
- functionalAreaId optional for user-facing workstream agents
- lifecycleStatus: draft | active | disabled | archived
- ownerAccountId / stewardRole
- authorityLevel
- promptDocumentId / activePromptVersion reference
- skillManifestId / activeSkillManifestVersion reference
- modelConfigRef
- workstreamToolCatalogRefs / governedToolIds exposed to this agent
- toolPermissionBoundary
- policyRefs / approvalBoundaryRefs
- traceRequirements
- createdBy / updatedBy / timestamps
```

Optional current snapshot:

```text
AgentRuntimeProfileSnapshot
- agentDefinitionId
- definitionVersion
- prompt/version refs
- skill manifest/version refs
- model/tool/policy refs
- checksum
- activeFrom
```

## Akka component mapping

Prefer this shape unless the app has a simpler explicitly accepted governance model:

- `AgentDefinitionEntity` as an Event Sourced Entity for lifecycle, ownership, authority, and reference changes.
- Optional Key Value Entity snapshot for immutable or activation-pinned runtime profile versions.
- Views for tenant-scoped agent list, detail, lifecycle filters, and admin search.
- HTTP endpoints and web UI surfaces for authorized administration.
- Consumers for audit/work-trace emission or snapshot projection when lifecycle/reference events change.

Route to:
- `akka-event-sourced-entities` for `AgentDefinitionEntity` lifecycle state/events/commands
- `akka-key-value-entities` for immutable runtime profile snapshots when version pinning is required
- `akka-views` for agent catalog, detail, and admin query models
- `akka-http-endpoints` and web UI skills for admin APIs and UI
- `ai-first-saas-audit-trace` for lifecycle, authority, model, prompt, skill, and tool-boundary audit events
- `core-saas-foundation` for tenant/customer scope, backend authorization, memberships, roles, and capabilities

## Command and event checklist

Typical commands:
- create draft agent definition
- update name/description/steward
- set prompt reference
- set skill manifest reference
- set model config reference
- set tool permission boundary
- set authority level / approval boundary
- activate draft or approved definition
- disable active agent
- archive disabled agent

Typical events:
- `AgentDefinitionCreated`
- `AgentDefinitionMetadataUpdated`
- `AgentDefinitionPromptReferenceChanged`
- `AgentDefinitionSkillManifestChanged`
- `AgentDefinitionModelConfigChanged`
- `AgentDefinitionToolBoundaryChanged`
- `AgentDefinitionAuthorityChanged`
- `AgentDefinitionActivated`
- `AgentDefinitionDisabled`
- `AgentDefinitionArchived`

## Rules

1. Agent definitions are tenant-scoped. Include `tenantId` in commands, state, events, views, and endpoints.
2. Classify each agent definition as `functional_context_area` or `internal_worker`. Functional/context-area agents may appear as role-authorized workstream verticals with structured surfaces; internal agents are invoked behind workflows, tools, consumers, timers, or services and should not be exposed as primary navigation unless product intent promotes them.
3. Backend authorization is authoritative. Do not trust frontend-visible role labels or prompt instructions.
4. Tool permissions are denied by default. Add explicit allow rules by governed tool id, adapter tool id/category, actor adapter/source, and scope.
5. A functional workstream agent profile may support human-backed surface actions, confirmed `human_chat_tool_plan` execution, and AI-backed `agent_tool_call` exposure, but those are separate adapters over governed tools. Human availability does not automatically grant model tool availability, and prompt/skill/reference text cannot expand the active tool catalog.
6. Model config references must not contain provider secrets or frontend-exposed credentials; use `akka-agent-model-governance` when defining `ModelConfigRef`, model policy, fallback model policy, provider-secret boundaries, model-use traces, or tenant/agent/task model selection.
7. Disabled or archived agents cannot be used by runtime flows, workflows, tools, scheduled jobs, or test consoles except for authorized inspection/replay.
8. Draft agents cannot perform consequential production actions.
9. Authority changes that expand autonomy or tool access require an explicit approval rule unless the product has a documented single-admin simplification.
10. Lifecycle, placement, authority, prompt reference, skill manifest, model, workstream tool catalog, and tool-boundary changes emit audit events.
11. Prompts and skills are not security boundaries. Mechanical authorization checks still gate data and tool access.
12. Tools are exposure surfaces for named capabilities. Do not grant a tool because a prompt/skill asks for it; require the governed tool contract and active `ToolPermissionBoundary` to allow it.
13. Runtime flows must resolve the active behavior profile before invoking the Java `Agent` class.
14. The resolved profile's `AgentSkillManifest` is per agent; User Admin and Agent Admin must not silently share a generic global skill list.
15. Use `../docs/agent-runtime-invocation-pattern.md` for the concrete `AgentRuntimeResolver` handoff: AuthContext validation, active AgentDefinition lookup, prompt assembly from the active prompt plus compact assigned-skill names/descriptions/hints, ToolPermissionBoundary, Java Agent invocation with Akka-registered tools including `readSkill(skillId)`, readSkill authorization, and PromptAssemblyTrace/SkillLoadTrace/AgentWorkTrace emission must happen before or around model invocation.

## Admin API and UI surfaces

Provide protected admin surfaces for:
- list/filter agents by lifecycle status, steward, and functional/internal placement
- create/edit draft definitions
- inspect active runtime profile references
- for functional/context-area agents, inspect linked workstream shell placement, default surfaces, callable capabilities, and trace links
- for internal agents, inspect invoking workflows/tools/timers/consumers/services, service authority basis, and trace links
- activate, disable, and archive with reason capture
- review authority/tool/model changes before activation
- show audit/work-trace links for profile changes and executions

Pair these with tenant-isolation, forbidden-access, disabled-agent, archived-agent, and audit tests.

## Implementation order

1. Confirm tenant/customer scope and required admin capabilities with `core-saas-foundation`.
2. Classify each agent as functional/context-area or internal and record the workstream placement or backend invocation basis.
3. Model `AgentDefinition` state, lifecycle commands, validation, and events.
4. Add views for admin list/detail and runtime lookup.
5. Add protected endpoints and UI surfaces.
6. Add audit/work-trace emission for lifecycle, placement, and authority changes.
7. Integrate runtime lookup so workflows/endpoints refuse disabled, archived, unauthorized, or out-of-scope agents.
8. Add an `AgentRuntimeResolver`-style application helper or service boundary that coordinates AuthContext, AgentDefinition, prompt versions, compact AgentSkillManifest, ToolPermissionBoundary, readSkill authorization, and trace ids according to `../docs/agent-runtime-invocation-pattern.md`.
9. Only then route to prompt, skill, tools, memory, orchestration, or component implementation skills.

## Review checklist

Before finishing, verify:
- every command/query is tenant-scoped and authorization-protected
- functional/context-area agents are distinguishable from internal agents in state, views, UI, and tests
- lifecycle transitions are explicit and tested
- disabled/archived agents cannot run
- tool permissions default to deny
- authority expansion has approval/audit semantics
- model config references are secret-free and governed by explicit model policy/fallback rules when runtime selection is tenant-, agent-, or task-specific
- prompt/skill/model/tool references are versioned or intentionally simple
- admin views and endpoints filter by authorized tenant/customer context
- audit events are emitted for lifecycle, authority, and behavior-profile reference changes
