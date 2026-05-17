# Capability: Audit Outcome Review

This is a lightweight capability contract for future refinement. It records the governed boundary for searching, explaining, summarizing, exporting, retaining, and learning from audit/work/outcome traces without inventing final retention rules, export schemas, or evaluator metrics.

## Capability definition

- capability-id: `audit-outcome-review`
- capability number: `CAP-10`
- class: trace/audit, read/evidence, scheduled
- purpose: make foundation, agent, workflow, policy, decision, tool, data-access, integration, and outcome history reviewable within scoped authority and redaction rules.
- business outcome: auditors and accountable operators can reconstruct what happened, why, under whose authority, with which evidence/policies/tools, and what outcome followed, while sensitive data remains protected.

## In-scope outcomes

- Search and review scoped admin audit events, work traces, decision traces, policy traces, data-access traces, tool traces, integration traces, and outcome links.
- Generate role-safe summaries, investigation timelines, exception digests, outcome reviews, and improvement candidates.
- Support retention, export, or redaction workflows where policy and approval allow.
- Link outcomes back to recommendations, decisions, automation, policies, and human interventions.
- Make denials, support access, policy activations, and consequential AI/tool activity visible to authorized reviewers.

## Out-of-scope outcomes

- Unlimited raw log access, cross-tenant export, secret exposure, or support access without scoped authorization.
- Final SIEM/export formats, legal hold rules, retention schedules, or evaluator score formulas.
- Automatic policy or behavior changes from audit summaries or outcome findings.

## Authority and contract

- actors/callers: auditor, dealer owner, operations supervisor, data steward, SaaS support with active support access, audit summary agent, outcome evaluator, retention/export timers.
- AuthContext/scope: authenticated account or trusted scheduled identity, selected tenant/customer or support-access context, audit/outcome visibility grant, retention/export permission, and tenant/customer filters.
- inputs: audit/outcome query, filters, trace ids, capability ids, time range, export/retention/redaction request, reason, correlation id, and idempotency key for side-effecting requests.
- outputs: redacted trace result, summary/digest, timeline, export status, retention/redaction decision status, safe denial shape, redaction markers, and trace links.
- side effects: audit query trace, summary/evaluation record, scheduled digest, export request, retention/redaction workflow request, improvement proposal link, and audit/work trace.
- idempotency: duplicate export/retention/digest requests for the same scope/filter/version return existing job/result where safe.
- policy/approval: exports, retention changes, redaction, deletion/anonymization, support-access review, and improvement activation require policy and human approval where applicable.
- exposure surfaces: audit/outcome UI, HTTP APIs, audit/outcome views, scheduled digests/retention/export jobs, scoped summary/evaluation tools, and trace consumers.

## Required future detail

- Trace schemas, retention/export/redaction policies, and legal hold boundaries.
- Outcome metric definitions and evaluator rubrics.
- Summary-agent data access and redaction constraints.
- Concrete tests for scoped search, denial redaction, support access, export approval, scheduled digest idempotency, audit of audit access, and outcome-link integrity.

## Linked layers

- operating model: `../15-operating-model/audit-trace-and-outcomes.md`, `../15-operating-model/outcomes-and-learning-loops.md`, `../15-operating-model/decisions-exceptions-and-evidence.md`
- behavior: `../20-behavior/flows/02-lifecycle-and-exception-flows.md`
- auth/security: `../40-auth-security/data-protection.md`, `../40-auth-security/foundation-onboarding-admin-boundaries.md`
- observability: `../50-observability/audit-trace-and-outcomes.md`
- UI: `../55-ui/ui-surfaces.md`
- tests: future test refresh under `../30-tests/`
