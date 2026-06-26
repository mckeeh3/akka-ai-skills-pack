# Workstream: Audit/Trace

## Purpose

Give tenant admins a searchable activity log that answers **"who did what?"** across human worker requests/responses, agent worker requests/responses, and tool calls.

The tenant-admin activity-log scope is intentionally narrow: search, view detail/full payloads, and configure tenant retention. Export, investigation notes, suspicious-activity acknowledgement, and agent-generated audit summaries are future candidates and are not part of this build scope.

## Worker roster

Audit/Trace binds explicit workers under `workers/` so implementation tasks preserve the current skills-pack chain:

```text
worker → execution harness → actor adapter → governed tool → capability → Akka/API/frontend realization
```

- `workers/tenant-admin-human.md` — authorized tenant-admin human worker using structured browser surfaces for search, detail/tool-call detail, and retention settings.
- `workers/audit-trace-functional-agent-worker.md` — the user-facing workstream assistant / functional-agent worker behind `audit-trace-agent`; it provides navigation and safe explanation only and has no trace-evidence retrieval or mutation authority in this scope.
- `workers/audit-trace-system-worker.md` — deterministic backend/API/projection/retention participants that record traces, execute authorized read/update operations, enforce retention expiry, and emit evidence.

## Functional agent

The workstream owns `audit-trace-agent` as its exactly-one user-facing functional-agent binding and product-facing workstream assistant for navigation and explanation copy only. In the tenant-admin activity-log scope, the agent does not receive trace-search, trace-detail, tool-call-detail, payload-read, retention-mutation, export, note, summary, or `human_chat_tool_plan` authority and cannot reveal audit payloads through chat. Tenant-admin trace access is through protected browser surface actions backed by backend authorization.

## Capability binding

Primary capability: `../../capabilities/audit-and-trace-investigation.md`.

## Tenant-admin activity-log scope user and scope

- Primary user: `tenant-admin`.
- Selected scope: tenant/Organization selected through backend-owned `AuthContext`.
- Optional object scope: customer/account when a trace is linked to one.
- System-level/no-customer traces are allowed.

## Tenant-admin activity-log scope surfaces

- `surface-audit-trace-activity-log`: searchable activity log.
- `surface-audit-trace-detail`: detail view for an authorized trace.
- `surface-audit-trace-retention-settings`: retention configuration panel.

## Readiness posture

This node captures current intent only and is compile-ready for focused build/alignment tasks, not runtime-ready. Runtime readiness still requires local Akka/API/UI validation of tenant-admin authorization, trace emission, search/detail/retention behavior, denials, immutable storage, retention expiry, and frontend sensitive-payload handling. Because this review added current skills-pack worker bindings and adapter clarity, mapped implementation should remain `stale-description-changed` until a source-alignment review or compile updates evidence.
