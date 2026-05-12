# Operational Tests: Observability and Audit

- every command has a correlation id in logs and traces
- authorization denial records safe diagnostic context without leaking sensitive data
- goal execution emits trace events for plan generation, task start/end, decision-card creation, and completion
- policy invocation records policy id/version/clause where applicable
- timed invitation expiry produces an audit event
- health endpoint indicates backend readiness for local development
