---
name: app-description-surface-modeling
description: Model structured workstream surfaces in app descriptions, including typed payloads, reusable functional-agent placement, capability-backed actions, rendering states, auth, traces, and tests.
---

# App Description Surface Modeling

Use this skill to update authoritative app-description surface contracts for agent workstream apps. A surface is a typed, backend-backed user interaction unit, not a static mockup.

## Required reading

- `../docs/structured-surface-contracts.md`
- `../docs/workstream-ui-reference-architecture.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/web-ui-style-guide.md` when visual guidance is in scope
- current `app-description/**` workstream, capability, UI, auth/security, tests, and traceability files

## Surface contract fields

For each surface, define:

- stable id, type, version, owning workstream/functional agent, and purpose
- actor roles/scopes and selected `AuthContext` requirements
- payload schema summary with frontend-safe fields only
- loading, empty, ready, submitting, success, validation-error, forbidden, conflict, stale/reconnect, partial-data, and failure states as applicable
- visible and hidden/denied actions
- browser action/tool id, governed backend capability/tool id, idempotency/correlation behavior, and approval gate for each consequential action
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

## Output contract

Update or propose updates to the app-description with:

- new/changed surface contracts
- affected capability/security/test/observability links
- traceability map changes
- assumptions and open questions
- generation impact: localized UI/API change, backend capability change, or broader workstream redesign

If a surface requires data, authority, or behavior not yet described, queue or ask the blocking question instead of inventing it.
