# Sprint 5: Substrate Implementation Skill Reframing

## Sprint goal

Reframe existing Akka implementation skills as the substrate used to realize AI-first SaaS applications while preserving their focused component-generation value.

## Dependencies

- Sprint 1 doctrine complete.
- Sprint 2 AI-first routing skill family available.
- Sprint 4 planning refactor identifies how implementation tasks route into component skills.

## Scope

Patch existing Stage 3 implementation skills with concise AI-first context:

- agents as bounded operational workers
- workflows as execution plans, approvals, exception routing, and compensation
- event sourced entities as audit-grade state for goals, policies, decisions, traces, and outcomes
- key value entities as simpler current-state stores
- views as supervision queues, decision queues, digest material, audit views, and outcome dashboards
- consumers as trace fanout, activity classification, metrics, and projection support
- timed actions as reminders, SLA checks, scheduled digests, replay jobs, and expiry
- endpoints and web UI as supervision/governance/control surfaces

## Primary skill families likely affected

- `akka-agents` and focused agent skills
- `akka-workflows`
- `akka-event-sourced-entities`
- `akka-key-value-entities`
- `akka-views`
- `akka-consumers`
- `akka-timed-actions`
- `akka-http-endpoints`
- `akka-web-ui-apps`
- `akka-grpc-endpoints`
- `akka-mcp-endpoints`

## Acceptance behavior

- Component skills remain low-token, focused, and implementation-ready.
- AI-first notes point back to the AI-first routing family rather than duplicating broad doctrine.
- Tests and examples are identified where AI-first-specific additions are needed.

## Done criteria

- Existing skill families are not rewritten wholesale.
- Every affected family has clear guidance for when it is realizing an AI-first substrate object.
- Any missing example/test gaps are listed for future work instead of silently ignored.
