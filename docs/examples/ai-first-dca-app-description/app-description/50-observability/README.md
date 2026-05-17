# 50 Observability

This layer defines generation-facing observability requirements for the DCA vertical reference. It covers foundation security events plus DCA work, decision, policy, tool, data-access, integration, and outcome traces.

Current files:

- `logs-and-audit.md` — structured log context, durable audit events, foundation security audit, DCA capability audit, audit access, support access, and redaction rules.
- `traces-and-correlation.md` — correlation identifiers, foundation and supplies end-to-end trace paths, durable trace contracts, derived views, and failure diagnosability.
- `metrics.md` — foundation metrics, DCA operational metrics, outcome categories, measurement requirements, and privacy/aggregation constraints.
- `health-and-alerts.md` — health signals, alert-worthy conditions, diagnosis expectations, recovery, and escalation rules.
- `audit-trace-and-outcomes.md` — business audit facts, trace event types, foundation and supplies trace fields, outcome metrics, feedback-to-learning loop, privacy/access rules, tests, and Akka substrate mapping.

Purpose: define what evidence must be emitted, projected, secured, measured, alerted, and tested before future realization slices generate code.

Placement note: `15-operating-model/` states why traces and outcomes matter for delegated work and retained human authority; this layer states what the app must record and expose operationally.

Generation-readiness note: this layer is detailed enough for future slice planning, but executable realization still needs concrete provider payloads, metric thresholds, retention periods, and integration adapters before runnable code generation.
