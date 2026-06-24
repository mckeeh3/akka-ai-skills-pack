---
name: akka-agent-skill-governance
description: Implement governed runtime skills for Akka agents with SkillDocument, SkillVersion, per-agent AgentSkillManifest, compact manifest prompt context, readSkill(skillId) tool authorization, SkillLoadTrace, versioning, diff/history UI, and safe test consoles.
---

# Akka Agent Skill Governance

Use this skill when model-loadable agent guidance must be tenant-scoped, versioned, reviewed, activated, authorized per agent, loaded through a governed tool, and traced. For small deploy-time packaged resources, prefer `akka-agent-harness-skills`.

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

- `../docs/governed-agent-substrate.md`
- `../docs/agent-runtime-invocation-pattern.md`
- `../akka-agent-governed-documents/SKILL.md`
- `../akka-agent-behavior-profiles/SKILL.md`
- `../akka-agent-tool-boundaries/SKILL.md`
- `../akka-agent-work-trace/SKILL.md`

## Core records

Implement or use:

- `SkillDocument`: tenant id, skill id, name, purpose, owner/steward, lifecycle status, current active version, audit metadata
- `SkillVersion`: immutable content, semantic summary, change reason, review status, risk classification, checksum, author/reviewer, created/approved/activated timestamps
- `AgentSkillManifest`: per-agent/profile allowed skill ids, active version refs or selection policy, compact name/description/when-to-use hints, ordering/grouping, and authority constraints
- `SkillLoadTrace`: requested skill id, resolved version, allow/deny reason, checksum, redaction/token facts, actor/AuthContext, correlation id

Lifecycle should include draft, in-review, approved, active, deprecated/archived as appropriate. Activation must be explicit and auditable.

## Runtime loading

Agents should receive only compact manifest context in the prompt. Full skill text is loaded through an authorized `readSkill(skillId)` function/tool when needed. Compact entries and loaded skill bodies may teach the model how to use surface actions, confirmed `human_chat_tool_plan` protocols, or AI-backed `agent_tool_call` tools, but they cannot grant governed tool access or bypass confirmation, approval, AuthContext, or `ToolPermissionBoundary` checks.

`readSkill` must:

- validate tenant, agent/profile, manifest assignment, active/approved version, capability/tool boundary, selected `AuthContext`, and runtime purpose
- return only authorized active content plus safe metadata
- deny unassigned, inactive, deprecated, cross-tenant, wrong-agent, wrong-purpose, or over-limit requests
- emit `SkillLoadTrace` for both allowed and denied loads
- fail closed when governance state or boundaries are unavailable

## UI/admin expectations

When a governance UI is in scope, provide:

- skill catalog/search/detail
- version history and diff view
- draft/edit/review/approve/activate/deprecate actions with authority checks
- per-agent manifest assignment and preview of compact manifest context
- safe test console that uses governed runtime assembly and records traces
- denial/recovery surfaces for missing approval, missing boundary, or unauthorized expansion

## Tests

Cover:

- create draft, review, approve, activate, deprecate/archive flows
- immutable versions and checksum/diff behavior
- manifest assignment/removal and compact prompt context
- allowed `readSkill` load with trace
- denied load for unassigned, inactive/unapproved, cross-tenant, wrong agent/profile, wrong purpose, missing boundary, and token/redaction limits
- safe test console cannot bypass governance
- skill text that claims a new tool, broader tenant/customer scope, approval authority, or unconfirmed chat execution is denied by backend tool-boundary/capability checks and traced
- runtime fail-closed behavior when active skill/config is missing

Do not implement normal runtime by copying every skill into every prompt. Manifest-first context plus governed on-demand loading is the intended pattern.
