# Capability: Audit and trace investigation

## Purpose

Let authorized auditors, admins, supervisors, and policy owners search, inspect, summarize, redact, and export scoped audit/work trace evidence for protected actions, denials, data access, policy use, approvals, agent/tool/model activity, and outcomes.

## Actors and scope

- SaaS Owner Admins inspect app-owner/platform evidence and Organization administration traces visible from a SaaS Owner selected `AuthContext`.
- Organization/Tenant Admins inspect Organization-scoped evidence, including customer-boundary administration evidence inside that Organization.
- Customer Admins inspect only selected Customer-scoped evidence.
- Auditors and policy owners inspect scoped evidence where explicitly granted.
- Audit/Trace functional agent can summarize and explain trace timelines from authorized evidence.
- Internal audit summary workers may prepare redacted summaries and investigation notes.

## Governed tools and exposure

- `read-audit-trace-dashboard` (`browser-tool`, `agent-tool` read): scoped command-center counters, readiness, recent failures, trace gaps, and allowed investigation entry points.
- `search-audit-traces` (`browser-tool`, `agent-tool` read): scoped search and filter.
- `read-trace-detail` (`browser-tool`, `agent-tool` read): redaction-aware event details and linked workstream items.
- `read-trace-timeline` (`browser-tool`, `agent-tool` read): authorized correlation timeline with hidden categories omitted.
- `read-trace-failure-evidence` (`browser-tool`, `agent-tool` read): redacted denial/provider/tool/model/runtime blocker evidence and recovery guidance.
- `read-investigation-guide` (`browser-tool`, `agent-tool` read): advisory investigation next steps that cannot approve, retry, mutate, or expand authority.
- `request-redacted-export` (`browser-tool` approval when required): export bundle subject to policy.
- `draft-investigation-note` (`browser-tool`, `agent-tool` proposal): idempotent human-reviewed summary/note annotation.
- `start-audit-summary-task` (`browser-tool`, `agent-tool` prepare, `internal-tool` worker start): validates source scope, idempotency, provider/runtime readiness, and tool boundary before starting a real model-backed redacted advisory summary worker or returning a fail-closed blocker.
- `read-audit-summary-task` (`browser-tool`, `agent-tool` read): reads retained summary-task progress/status without trusting browser timers or cached rows.
- `review-audit-summary-task` (`browser-tool`, `agent-tool` read): opens a retained real model-backed advisory summary only when completed and authorized.
- `accept-audit-summary-task` and `reject-audit-summary-task` (`browser-tool` human decision): record review evidence only; they never mutate source traces, policies, exports, notes, or authorization state.

## Authorization and denials

Trace visibility is scoped, redacted, and permissioned. Unauthorized callers must not learn protected data or cross-tenant/customer identifiers. Export, sensitive evidence access, summary task start, summary review, accept/reject decisions, and investigation note append require explicit backend capability checks, idempotency/correlation handling where side-effecting, and durable audit/work traces.

## Outcomes

In scope: durable investigation evidence, correlation continuity, redacted timelines, configurable retention, export governance, trace links from workstream items, and diagnosable denial/provider failures. Default audit/work trace retention is one year. Retention is configurable at app/SaaS-owner, Organization, and Customer layers; Organization Admins may set Customer-level retention for Customers in their Organization when policy permits. Approved exports are available after approval, but bundle contents are limited to redacted evidence still retained and available at export generation/download time.

Out of scope: raw secret/provider payload exposure, unredacted exports in all cases, and audit access based only on frontend navigation.

## Linked graph nodes

- Workstream: `../workstreams/audit-trace/workstream.md`
- Tests: `../workstreams/audit-trace/tests/coverage.md`
- Traces: `../workstreams/audit-trace/traces/work-traces.md`
