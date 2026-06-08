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

A `ToolPermissionBoundary` is tenant-scoped policy for one agent/profile/version context. It should answer:

- which tools are allowed, denied, read-only, side-effecting, or approval-required
- which capability ids and governed-tool ids authorize each use
- which tenant/customer/data scopes are allowed
- which arguments must be redacted, validated, or denied
- whether tool use is allowed in this runtime mode, workstream, surface action, and selected `AuthContext`
- which trace facts must be recorded

Tool boundaries are backend enforcement, not prompt advice. Hiding a button, omitting a tool from prompt text, or asking the model to comply is not sufficient.

## Enforcement points

Apply checks before registering or invoking tools through:

- local `@FunctionTool` methods and external tool classes
- `effects().tools(ComponentClass.class)` Akka component tools
- remote MCP tools
- `readSkill(skillId)` and `readReferenceDoc(referenceId)` governed loader tools
- evidence/data access helpers
- side-effecting tools such as email, membership changes, policy activation, exports, or external integrations

Denied or approval-required tool use must return a safe structured denial/system-message result, emit trace/audit facts, and avoid leaking hidden tool names, secrets, cross-tenant identifiers, or privileged evidence.

## Implementation checklist

- Model boundary state with tenant id, agent/profile/version refs, lifecycle/status, allowed tool specs, denied specs, scope constraints, risk/approval flags, and audit metadata.
- Resolve effective boundary during agent runtime setup before tool registration.
- Validate selected `AuthContext`, membership/capability, workstream/surface context, governed document status, and tenant/customer/data scope.
- Fail closed when boundary, profile, capability state, provider config, or governed docs are missing.
- Record allow/deny/approval-required decisions in `AgentWorkTrace` / tool-load trace with correlation ids and redacted args.
- Make authority expansion a proposal/approval flow, never a silent runtime escalation.

## Tests

Cover:

- allowed tool invocation with trace facts
- denied unassigned, inactive, cross-tenant/customer, wrong capability, wrong workstream, and side-effect-without-approval cases
- loader-tool denials for inactive/unapproved skill/reference docs
- missing boundary/profile/config fail-closed behavior
- redaction of arguments, secrets, and privileged evidence
- agent runtime cannot bypass checks by direct service/provider/tool calls

Use `TestModelProvider` or isolated test doubles only in tests. Normal runtime must use the governed Akka agent/tool path.
