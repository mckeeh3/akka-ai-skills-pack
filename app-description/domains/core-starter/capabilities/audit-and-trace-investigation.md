# Capability: Audit and trace investigation

## Purpose

Let authorized auditors, admins, supervisors, and policy owners search, inspect, summarize, redact, and export scoped audit/work trace evidence for protected actions, denials, data access, policy use, approvals, agent/tool/model activity, and outcomes.

## Actors and scope

- Auditor and authorized admins inspect evidence within selected tenant/customer scope.
- Audit/Trace functional agent can summarize and explain trace timelines from authorized evidence.
- Internal audit summary workers may prepare redacted summaries and investigation notes.

## Governed tools and exposure

- `search-audit-traces` (`browser-tool`, `agent-tool` read): scoped search and filter.
- `read-trace-detail` (`browser-tool`, `agent-tool` read): timeline, correlation, redaction-aware details, and linked workstream items.
- `request-redacted-export` (`browser-tool` approval when required): export bundle subject to policy.
- `draft-investigation-note` (`agent-tool` proposal): human-reviewed summary/note.

## Authorization and denials

Trace visibility is scoped, redacted, and permissioned. Unauthorized callers must not learn protected data or cross-tenant/customer identifiers. Export and sensitive evidence access require explicit policy gates.

## Outcomes

In scope: durable investigation evidence, correlation continuity, redacted timelines, export governance, trace links from workstream items, and diagnosable denial/provider failures.

Out of scope: raw secret/provider payload exposure, unredacted exports by default, and audit access based only on frontend navigation.

## Linked graph nodes

- Workstream: `../workstreams/audit-trace/workstream.md`
- Tests: `../workstreams/audit-trace/tests/coverage.md`
- Traces: `../workstreams/audit-trace/traces/work-traces.md`
