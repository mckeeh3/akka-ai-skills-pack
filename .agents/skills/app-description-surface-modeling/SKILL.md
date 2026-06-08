---
name: app-description-surface-modeling
description: Model structured workstream surfaces in app descriptions, including typed payloads, reusable functional-agent placement, capability-backed actions, rendering states, auth, traces, and tests.
---

# App Description Surface Modeling

Use this skill to update authoritative app-description surface definitions and workstream surface bindings for agent workstream apps. A surface is a typed, backend-backed user interaction unit, not a static mockup.

## Required reading

- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-description-skill-output-contracts.md`
- `../docs/structured-surface-contracts.md`
- `../docs/workstream-contract.md`
- `../docs/workstream-attention-contracts.md`
- `../docs/workstream-ui-reference-architecture.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/web-ui-style-guide.md` when visual guidance is in scope
- current `app-description/global/surfaces/**`, `global/tools/**`, `global/traces/**`, domain capability files, and workstream surface/access/tool/trace/test/realization bindings

## Surface contract fields

For each surface, define:

- whether this is a reusable global surface pattern or a workstream-specific surface binding
- stable id, type, version, owning workstream definition, exactly one owning functional agent, reusable functional agents/workstreams if any, and purpose
- actor roles/scopes and selected `AuthContext` requirements
- payload schema summary with frontend-safe fields only
- loading, empty, ready, submitting, success, validation-error, forbidden, conflict, stale/reconnect, partial-data, and failure states as applicable
- visible and hidden/denied actions
- stable `actionId`, browser action/tool id, governed backend capability/tool id, idempotency/correlation behavior, result surface, and approval gate for each consequential action
- target/result surface or typed `system_message`
- trace/audit/work-trace links and redaction rules
- accessibility/responsive expectations
- acceptance, regression, security, negative, idempotency, and observability tests

## Modeling rules

- Model dashboard attention and surface graph edges before conventional route/page details.
- Every consequential read/query/mutation/action maps to a backend capability and is authorized server-side.
- Denials, approval-required results, validation failures, stale/reconnect, no-op, and background-work states are explicit structured outcomes.
- Do not expose secrets, raw provider data, hidden roles, cross-tenant/customer identifiers, or privileged evidence in browser payloads.
- Do not describe fixture/static/mock surfaces as normal generated-app runtime.
- If a surface needs missing capability, governed-tool, action identity, authority, or result-surface semantics, ask or queue a blocking question instead of inventing stable implementation ids. Template examples may propose candidate ids only when clearly marked provisional.
- Process/template baselines may list deferred typed surfaces and first-slice fallbacks; app-level implementation cleanup must replace consequential deferred surfaces before claiming capability readiness.

## Output contract

Update or propose updates to the app-description with:

- new/changed surface contracts
- affected capability/security/test/observability links
- traceability map or graph-link changes
- assumptions and open questions
- generation impact: localized UI/API change, backend capability change, broader workstream redesign, or separate app-level surface implementation cleanup

If a surface requires data, authority, or behavior not yet described, queue or ask the blocking question instead of inventing it.
