# Sprint 01: My Account Full-Core Map and First Implementation Group

## Objective

Define and queue the My Account SMB full-core implementation path, then implement the first bounded source-edit group once source boundaries are known.

## Source context

The other core workstreams now produce meaningful signals. This sprint turns My Account into the signed-in user's trusted control center for context, authority, attention, trace refs, self-service settings, and guided next steps.

## Ordered work areas

1. Define My Account vertical slice contracts and implementation map.
2. Implement `/api/me`, selected context, authority summary, and lower-left launcher foundations.
3. Implement profile/settings surfaces and deterministic update lifecycle.
4. Implement personal attention aggregation, trace refs, and safe sibling workstream navigation.
5. Implement MyAccountAgent request/response guidance.
6. Decide personal digest worker readiness only if deterministic foundations and task semantics justify it.
7. Validate runtime/API/UI behavior and verify mini-project readiness.

## Acceptance criteria

- Implementation tasks are bounded by actual source boundaries.
- My Account surfaces are typed, trace-linked, authority-scoped, redacted, and visually polished.
- Deterministic services own `/api/me`, context, validation, attention filtering, navigation authorization, and trace redaction.
- Model-backed guidance/worker behavior uses governed Akka runtime and provider fail-closed semantics.
- Targeted and broad starter validation pass or blockers are queued.
