# Backlog: Real Akka Runtime Replacement

## Goal

Make the starter's normal generated runtime use real Akka components for all workstream/foundation state and behavior that the starter claims as implemented. In-memory/default/mock/fixture substitutes are permitted only in tests.

## Suggested harness task breakdown

1. Strict source map and task refinement.
2. Identity/account/membership Akka runtime replacement.
3. Workstream log and audit trace Akka runtime replacement.
4. Access-review and governance policy Akka runtime replacement.
5. Agent behavior, invitation, and runtime trace binding hardening.
6. Remove or test-quarantine backend local-demo/fail-closed adapters and constructors.
7. Frontend fixture runtime quarantine.
8. Docs/skills/readiness wording cleanup.
9. Terminal verification.

## Implementation notes

- Existing Akka classes such as `AkkaInvitationRepository`, `AkkaAgentBehaviorRepository`, `AkkaAgentRuntimeTraceSink`, and `AkkaWorkstreamLogRepository` should become normal runtime bindings rather than optional seams.
- Stores without real Akka implementations need new Akka components. Choose Key Value Entity for current-state starter scope only when event history is not part of the feature claim; choose Event Sourced Entity for audit-grade lifecycle records.
- Service constructors in `backend/src/main/java` must not silently instantiate `LocalDemo*` or fail-closed repositories for in-scope features.
- Test-only replacements should live under `backend/src/test/java` or clearly test-only frontend files.
- Frontend production entrypoints must use `HttpWorkstreamApiClient` and real realtime clients only.

## Dependencies

- Existing runtime durability remediation docs and code are inputs, but their local/demo compromise is superseded.
- Validation should use scaffolded/rendered starter commands because template placeholders make direct backend Maven execution invalid before rendering.

## Required checks by the end of the sprint

- `git diff --check`
- rendered starter backend tests covering changed repositories/components
- `tools/validate-ai-first-saas-starter-fullstack.sh`
- template frontend `npm test`, `npm run typecheck`, and `npm run build`
- root frontend checks if root mirror changes
- scans proving no normal-runtime substitute seams remain in production source paths

## Acceptance criteria

- Every queue task can run in one fresh session.
- Replacement tasks name exact source areas, expected Akka substrates, tests, and scans.
- Verification can decide whether the stricter Akka-component-backed runtime bar is actually met.
