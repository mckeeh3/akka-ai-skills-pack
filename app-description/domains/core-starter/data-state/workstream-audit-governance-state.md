# Data State: Workstream, audit, and governance state

## Responsibility

Durable workstream logs, stream items, attention items, notifications, audit/admin events, agent work traces, policy proposals, simulations, decisions, activation history, rollback history, and outcome notes for the five core workstreams.

## Lifecycle and invariants

- Workstream requests and structured results include correlation ids and trace links.
- Attention counts are backend-owned and scoped to authorized users and selected context.
- Audit/trace evidence is redacted and permissioned before browser or agent exposure.
- Policy proposals and decisions preserve evidence, risk, confidence, impact, alternatives, reviewer, and outcome linkage.
- Export/digest requests are governed and never bypass data visibility rules.

## Retention and traces

Trace records must answer who or what acted, under which tenant/customer context and authority, which policy/tool/data/model was used, what was denied or approved, and what outcome evidence followed.
