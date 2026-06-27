# Agent Admin Behavior Profile Realization

## Purpose

Compile the updated Agent Admin app-description into the runnable root SaaS core app. The current code still reflects an older direct document-editing/governance-console implementation. This mini-project realigns Agent Admin around SaaS-admin-only behavior-profile governance: proposal-first behavior changes, review/activation separation, versioned prompts/skills/references/profile assignments, runtime loading, trace visibility, and current frontend surfaces.

## Current-intent provenance

Primary app-description sources:

- `app-description/domains/core-starter/workstreams/agent-admin/workstream.md`
- `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
- `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tools/governed-tools.md`
- `app-description/domains/core-starter/workstreams/agent-admin/traces/work-traces.md`
- `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/api-contracts.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/frontend-routes.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/source-alignment.md`
- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`

## Discussed implementation drift

The 2026-06-27 compile scout and source-alignment update found these material gaps:

1. Direct Save/restore/create/delete paths mutate active behavior instead of creating non-active proposals followed by protected activation.
2. Proposal review/approve/reject/activate/request-changes surfaces and backend records are missing or partial.
3. Tenant-scoped behavior-profile clone/versioning for prompt, model config reference, skill assignment, and generated tool assignment is missing or partial.
4. Skill/reference create/delete are direct active mutations instead of proposal/deprecate-default lifecycle flows.
5. Generated agent name/purpose and whole-agent lifecycle UI/actions are stale; generated agent identity and code-generated tool implementation are static.
6. Agent list/detail filters and summaries are not yet rich enough for placement, steward, authority level, safe model alias, resolved profile scope, allowed generated tools, manifest summaries, profile history, proposal entries, and trace links.
7. Runtime traces do not yet cover the full intended profile resolution, prompt assembly, model-policy, readSkill/readReferenceDoc, generated-tool assignment, tool-boundary decision, and AgentWorkTrace metadata surface.
8. Frontend and tests still encode some stale direct-save/permanent-delete/whole-agent lifecycle assumptions.

## Done state

The mini-project is complete when Agent Admin reaches `api-smoked/frontend-rendered` for the implemented current scope and records remaining provider/manual residuals explicitly. Completion requires:

- SaaS Owner/Admin-only Agent Admin access and browser-safe non-admin denials.
- Catalog/detail surfaces that inspect generated agents and behavior profiles without whole-agent creation/deletion or generated tool code editing.
- Draft/proposal lifecycle for prompt, skill, and reference doc changes where Save Draft is non-active and activation is separate.
- Low-risk activation path with audit/work traces, stale-version checks, and active runtime reads updating only after activation.
- Medium/high-risk or authority-expanding direct activation denied or routed to review/decision-card style recovery, with active behavior unchanged.
- Restore creates a proposal, not a direct active restore.
- Skill/reference create/deprecate/remove semantics are proposal-first and avoid hidden loader access.
- Behavior-profile version seams for tenant-scoped model config reference, skill assignment, and generated tool assignment changes.
- Runtime loader reads active profile/docs only, enforces assigned-skill/reference and generated-tool boundaries, and emits read/work trace evidence.
- Frontend surfaces/contracts render the current proposal/review/profile/assignment/trace inventory and no longer assert stale governance-console behavior.
- Full relevant backend/frontend checks pass, followed by a terminal verification task that either closes this mini-project or appends bounded follow-up tasks plus a new terminal verification task.

## Non-goals

- Do not add provider-secret administration to Agent Admin.
- Do not create or delete generated agents.
- Do not create/edit/delete generated tool code or backend authorization implementation from Agent Admin.
- Do not claim provider-backed model success without configured real provider runtime. Missing provider/model configuration must fail closed.
- Do not implement unrelated User Admin, Governance/Policy, Audit/Trace, or business-domain features except for shared trace/surface compatibility required by Agent Admin.

## Runtime path to prove

SaaS Owner/Admin browser/API path:

```text
WorkOS/AuthKit-authenticated SaaS Owner/Admin
  -> /api/workstream/bootstrap and /api/workstream/surfaces/actions
  -> WorkstreamService Agent Admin surface_action adapter
  -> Agent Admin governed tool ids/capabilities
  -> AgentAdminDocAdministrationService / behavior profile repositories / runtime loader
  -> Akka components or deterministic services
  -> durable versions/proposals/traces
  -> frontend workstream surfaces
```

Model-backed editing-agent path must use a concrete Akka `Agent` when provider/test-model configuration is present and must fail closed with browser-safe system-message/proposal recovery when provider configuration is absent.
