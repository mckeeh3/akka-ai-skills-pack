# Workstream: Audit/Trace

## Purpose

Give tenant admins a searchable activity log that answers **"who did what?"** across human worker requests/responses, agent worker requests/responses, and tool calls.

V1 is intentionally narrow: search, view detail/full payloads, and configure tenant retention. Export, investigation notes, suspicious-activity acknowledgement, and agent-generated audit summaries are future candidates and are not part of the v1 build scope.

## Functional agent

The workstream owns `audit-trace-agent` as its exactly-one user-facing functional-agent binding for workstream navigation and explanation copy only. In v1, the agent does not receive trace-search or trace-detail governed-tool authority and cannot reveal audit payloads through chat. Tenant-admin trace access is through protected browser surface actions backed by backend authorization.

## Capability binding

Primary capability: `../../capabilities/audit-and-trace-investigation.md`.

## V1 user and scope

- Primary user: `tenant-admin`.
- Selected scope: tenant/Organization selected through backend-owned `AuthContext`.
- Optional object scope: customer/account when a trace is linked to one.
- System-level/no-customer traces are allowed.

## V1 surfaces

- `surface-audit-trace-activity-log`: searchable activity log.
- `surface-audit-trace-detail`: detail view for an authorized trace.
- `surface-audit-trace-retention-settings`: retention configuration panel.

## Readiness posture

This node captures current intent only. Runtime readiness still requires local Akka/API/UI validation of tenant-admin authorization, trace emission, search/detail/retention behavior, denials, immutable storage, retention expiry, and frontend sensitive-payload handling.
