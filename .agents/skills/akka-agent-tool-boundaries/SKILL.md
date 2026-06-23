---
name: akka-agent-tool-boundaries
description: Implement governed ToolPermissionBoundary enforcement for Akka agents across local function tools, Akka component tools, remote MCP tools, readSkill, readReferenceDoc, data access, side effects, approval-required expansion, runtime denials, and traces.
---

# Akka Agent Tool Boundaries

Use this skill when an Akka agent needs explicit runtime authorization for tools, data access, side effects, governed skill/reference loading, or approval-gated authority expansion.

## Required reading

- `../docs/governed-agent-substrate.md`
- `../docs/agent-runtime-invocation-pattern.md`
- `../docs/agent-workstream-application-architecture.md`
- `../akka-agent-behavior-profiles/SKILL.md`
- `../akka-agent-governed-documents/SKILL.md`
- `../akka-agent-skill-governance/SKILL.md` and/or `../akka-agent-reference-governance/SKILL.md` when loader tools are in scope
- `../akka-agent-work-trace/SKILL.md`
- focused tool skills: `akka-agent-tools`, `akka-agent-component-tools`, `akka-agent-mcp-tools`

## Boundary model

A `ToolPermissionBoundary` is tenant-scoped policy for one agent/profile/version context. It authorizes model-facing tool exposure for governed workstream tools; it is not the governed tool contract itself. It should answer:

- which governed tool ids and adapter tool ids are allowed, denied, read-only, side-effecting, or approval-required
- which capability ids and governed-tool ids authorize each use
- which tenant/customer/data scopes are allowed
- which arguments must be redacted, validated, or denied
- whether tool use is allowed in this runtime mode, workstream, actor adapter/source (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, workflow, timer, consumer, API, MCP), and selected `AuthContext`
- whether the invocation is human-requested, AI-backed, system-initiated, confirmed, approval-required, or autonomous within policy
- which trace facts must be recorded, including `requestedBy`, `confirmedBy`, confirmation id, governed tool id, adapter tool id, denial/approval basis, and result/partial-failure summary

Tool boundaries are backend enforcement, not prompt advice. Hiding a button, omitting a tool from prompt text, adding skill/reference instructions, or asking the model to comply is not sufficient. Prompt, skill, reference, manifest, or tool-description text cannot grant tool authority, tenant/customer scope, approval authority, or side-effect permission.

## Enforcement points

Apply checks before registering or invoking tools through:

- local `@FunctionTool` methods and external tool classes
- `effects().tools(ComponentClass.class)` Akka component tools
- remote MCP tools
- `readSkill(skillId)` and `readReferenceDoc(referenceId)` governed loader tools
- evidence/data access helpers
- side-effecting tools such as email, membership changes, policy activation, exports, or external integrations
- human-requested, model-planned tool execution after chat plan confirmation, when the workstream runtime uses the agent/tool boundary to decide which governed tools can execute

Denied, unconfirmed, approval-required, or partially failed tool use must return a safe structured denial/system-message/result-surface shape, emit trace/audit facts, and avoid leaking hidden tool names, secrets, cross-tenant identifiers, or privileged evidence. Multi-step confirmed chat plans execute as individually authorized tool invocations; failure of one step must report partial results without relying on model text to repair consistency.

## Implementation checklist

- Model boundary state with tenant id, agent/profile/version refs, lifecycle/status, allowed tool specs, denied specs, scope constraints, risk/approval flags, and audit metadata.
- Resolve effective boundary during agent runtime setup before tool registration.
- Validate selected `AuthContext`, membership/capability, workstream/surface context, governed document status, governed tool id, adapter/tool id, and tenant/customer/data scope.
- For human chat-mediated tool plans, validate that the plan was proposed within the selected workstream tool catalog and that explicit confirmation is bound to the exact plan before any consequential tool executes.
- For AI-backed `agent_tool_call` use, validate that the active managed agent profile exposes the governed tool to the model and that any human request context is recorded as `requestedBy` rather than treated as extra authority.
- Fail closed when boundary, profile, capability state, provider config, or governed docs are missing.
- Record allow/deny/approval-required/partial-failure decisions in `AgentWorkTrace` / tool-load trace with correlation ids, actor adapter/source, redacted args, `requestedBy`, `confirmedBy` when applicable, and result-surface refs.
- Make authority expansion a proposal/approval flow, never a silent runtime escalation.

## Tests

Cover:

- allowed tool invocation with trace facts for both AI-backed `agent_tool_call` and human-requested confirmed plan paths when those adapters are in scope
- denied unassigned, inactive, cross-tenant/customer, wrong capability, wrong governed tool id, wrong workstream, missing confirmation, and side-effect-without-approval cases
- loader-tool denials for inactive/unapproved skill/reference docs
- missing boundary/profile/config fail-closed behavior
- redaction of arguments, secrets, and privileged evidence
- prompt/skill/reference text cannot expand the workstream tool catalog or bypass plan confirmation
- agent runtime cannot bypass checks by direct service/provider/tool calls

Use `TestModelProvider` or isolated test doubles only in tests. Normal runtime must use the governed Akka agent/tool path.
