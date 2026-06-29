---
id: RV-AUDIT-001
title: Audit/Trace search shows scoped evidence, denial, and redaction behavior
workstream: audit-trace
surface: audit-trace-search-detail-timeline
persona: support-operator
environment: local-dev
dataSetup:
  - base-organization
authMode: workos-test-users
executionMode: human-manual
executionStatus: authored-not-run
readinessClaim: not-run
---

# Purpose

Validate that Audit/Trace search and detail surfaces expose only scoped trace evidence, enforce support/tenant authorization, preserve redaction boundaries, and record trace-read/denial evidence.

# Prerequisites

- Start the app using `environments/local-dev.md`.
- Prepare `data-setups/base-organization.md` with at least one auditable prior action or explicitly record `seed-data gap` if none exists.
- Log in as `personas/support-operator.md` with a scoped support-access grant, or record `auth/setup gap` if the grant cannot be prepared.
- Identify an in-scope trace id or searchable event and an out-of-scope tenant/trace target for denial testing.

# Runtime path

`support operator -> Audit/Trace search/detail/timeline surface -> surface_action or protected workstream API -> audit-and-trace-investigation governed capability -> audit/work/agent trace views and repositories -> redacted result surfaces plus trace-read and denied-read evidence`

# Surface, adapter, and governed-tool contract

- Surface graph node: Audit/Trace search/detail/timeline/correlation.
- Action edge: search traces, open trace detail, inspect timeline/correlation, attempt denied read.
- Actor adapter/source: browser `surface_action` or protected API query; read-only chat plans, if offered, must remain bounded to inspection and confirmation where required.
- Governed tool scope: audit/work trace investigation tools limited by tenant/support-access scope.
- Read/idempotency behavior: repeated reads are idempotent and may emit bounded trace-read evidence without mutating domain state.
- Redaction behavior: browser-safe redaction applies to sensitive fields in search results, details, screenshots, and exported evidence.

# Setup

The base setup should produce or reference prior auditable activity such as a login, `/api/me` read, invitation attempt, provider fail-closed event, or policy decision. Setup may grant support access, but the scenario validates that the granted scope is enforced during reads.

# Human UI validation script

1. Open the local frontend URL and log in as `support.operator@example.com`.
2. Navigate to Audit/Trace search.
3. Search for an in-scope trace or event from the base organization.
4. Open trace detail/timeline and record visible redacted fields and correlation links.
5. Attempt to search or open an out-of-scope trace/tenant/event.
6. Record the denied, hidden, or no-enumeration result.
7. Repeat the in-scope read and confirm idempotent read behavior.
8. If an unprivileged member can reach the route, attempt the same read as `member@example.com` and record denial.

# Expected results

- In-scope trace search returns only permitted tenant/support-scope evidence.
- Detail/timeline views show browser-safe redacted trace data and correlation links where available.
- Out-of-scope reads are forbidden, hidden, or no-enumeration safe.
- Unprivileged member access is denied or hidden.
- Repeated reads do not mutate domain state and keep bounded operational trace evidence.
- Audit/work traces record support read, denied read, actor source, scope, and redaction behavior.

# Evidence to capture

- Support grant id/scope/expiry if available.
- Search query, result count, trace ids, and redacted detail observations.
- Network/API statuses for in-scope, out-of-scope, repeated, and member-denied reads.
- Trace-read and denied-read audit/work trace ids.
- Screenshots or DOM observations with sensitive values redacted before committing any run record.

# Failure classification hints

- `seed-data gap` if no auditable trace data exists to search.
- `auth/setup gap` for missing support-access grant or support persona mapping.
- `implementation gap` for cross-tenant leakage, missing denial enforcement, or missing redaction.
- `UX/state gap` for unclear no-results, denied, or redacted-state copy.
- `test gap` for missing trace-read or denial evidence.
