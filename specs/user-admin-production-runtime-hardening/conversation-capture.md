# Conversation Capture: User Admin Production Runtime Hardening

## Source discussion

After User Admin surface conformance cleanup and browser/workstream smoke automation were completed, the user asked to proceed with a new mini-project covering three production-readiness areas:

1. Provider-backed invitation delivery hardening.
2. Production identity exception recovery workflows.
3. Model-backed access-review automation.

## Decisions

- Treat this as root app realization work.
- Create one coordinated User Admin production runtime hardening mini-project because the three areas share User Admin surfaces, authorization, audit/work traces, provider fail-closed behavior, and runtime completion doctrine.
- Split implementation into bounded task groups so each fresh harness session handles only one slice.
- Preserve current completed User Admin surface conformance and browser smoke behavior.
- Do not require real Resend, WorkOS, or model provider credentials for default tests; real-provider smoke tests should be skipped/documented unless credentials are present.
- Do not count deterministic/model-less behavior as production access-review automation. Missing model/provider config must return actionable fail-closed states.

## Runtime completion constraints

- Invitation delivery must validate through the application outbox/provider path, not a fixture-only email simulation.
- Identity exception recovery must use durable state/workflow and deterministic authorization, not just a read-only placeholder panel.
- Access review automation must invoke a concrete governed Akka Agent path when configured, including model config, tool permissions, traces, and fail-closed behavior.
- All browser-visible payloads must preserve tenant/customer scope, selected `AuthContext`, redaction, audit/work traces, and frontend secret boundaries.

## Prior completed foundations

- `specs/archive/user-admin-surface-conformance-cleanup/**` completed User Admin structured-surface conformance.
- `specs/user-admin-browser-workstream-smoke/**` completed User Admin hosted UI/workstream smoke coverage.
- Broader checks previously passed when run with `ADMIN_USERS` unset for Maven.
