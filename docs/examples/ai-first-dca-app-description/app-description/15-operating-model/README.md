# 15 Operating Model

Current files:
- `goals-and-objectives.md` — durable goals, lifecycle objectives, and outcome loops.
- `agent-roles-and-authority.md` — human roles, delegated work, retained authority, and fail-safe behavior.
- `agent-team-design.md` — bounded coordinator/specialist agent team and Akka substrate mapping.
- `policies-and-approval-gates.md` — versioned policy examples, thresholds, approval gates, and governed commits.
- `decisions-exceptions-and-evidence.md` — decision-card schema, exception types, evidence, and reviewer actions.

Related cross-layer files:
- `../50-observability/audit-trace-and-outcomes.md`
- `../55-ui/ui-surfaces.md`
- `../60-generation/implementation-slices.md`

Purpose: capture the AI-first operating model before Akka component choices: delegated work, retained human authority, policies, decisions, trace obligations, and outcome loops.

Placement note: this layer defines the business meaning of traces and outcomes; `50-observability/audit-trace-and-outcomes.md` owns the concrete audit, work-trace, decision-trace, metric, privacy, and access expectations for realization.
