# Behavior: Governance/Policy

## Current-state behavior

Manage policy proposals, simulations, decisions, activation, rollback, approval gates, thresholds, behavior-change governance, and outcome notes. The workstream starts from a role-specific dashboard, accepts contextual composer requests, returns structured surfaces, and maps consequential actions to governed backend tools.

## Agent behavior

`governance-policy-agent` may explain, summarize, draft, recommend, and prepare proposals only within authorized capabilities. It cannot grant permissions through prompt text, bypass approval gates, or act outside its tool boundary. Model-backed turns use governed runtime configuration or fail closed.

## Lifecycle and command behavior

Policy proposals move through explicit lifecycle states defined by `../../capabilities/governance-policy-lifecycle.md`. Drafting/submission creates inert proposals; simulations and impact-analysis tasks produce advisory evidence only; human decisions approve, reject, or request changes; activation and rollback are separate approval-gated command modes that require current proposal state, prerequisite evidence, rollback metadata, backend authority, idempotency, and trace creation.

The durable impact-analysis path is an advisory autonomous-agent task path. Starting, reading, cancelling, accepting, rejecting, or requesting changes to impact-analysis results updates task/result disposition and evidence links only. It never approves, activates, rolls back, weakens security, expands authority, or fabricates model-backed analysis.

## Edge cases

Repeated commands with the same idempotency key return the existing result and must not duplicate proposals, tasks, activations, rollbacks, outcome notes, notifications, or traces. Stale proposal versions return conflict/stale states with recovery guidance. Provider/security misconfiguration returns actionable denial/failure feedback and a blocked-provider-or-runtime task state where applicable. Unsupported business-domain requests are routed to extension guidance rather than silently added.
