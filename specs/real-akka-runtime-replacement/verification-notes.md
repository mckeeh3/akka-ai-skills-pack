# Verification Notes: Real Akka Runtime Replacement

Date: 2026-05-30

## Verdict

Complete for the mini-project scope. No bounded follow-up task is required for the real Akka runtime replacement queue.

The stricter decision has been verified at the starter scope: normal generated-app runtime paths for the claimed workstream/foundation features are Akka component-backed, while fixture/local substitute behavior is confined to tests or explicit blocked/fail-closed provider/runtime surfaces.

## Scope comparison

Compared against:

- `specs/real-akka-runtime-replacement/README.md` done state;
- `conversation-capture.md` strict user decision;
- Sprint 01 objective and acceptance criteria;
- backlog item 9 terminal verification;
- task done notes for `TASK-RUNTIME-01-001` through `TASK-RUNTIME-01-008`;
- `non-akka-runtime-seam-map.md` replacement inventory.

## Done-state assessment

- Backend substitute adapters: no `LocalDemo*`, `Substitute*`, `FailClosed*Repository`, or `FailClosed*Sink` implementation remains under `templates/ai-first-saas-starter/backend/src/main/java`.
- Backend normal runtime binding: completed task notes and rendered backend validation show identity, workstream log, audit trace, governance policy, access-review, invitation, governed agent behavior, and agent trace paths are bound through Akka components in normal runtime.
- Frontend fixtures: fixture clients/data remain under `src/__tests__/fixtures/**`; production runtime imports do not expose `fixtureWorkstream`, `FixtureWorkstream*`, `FixtureApiClient`, or `FixtureRealtimeClient` when test directories are excluded.
- Guidance/readiness: completed doctrine task updated repo guidance to require real Akka component-backed normal runtime and to allow substitutes only in tests.
- Remaining provider/runtime blocked surfaces in `WorkstreamService.java` are explicit fail-closed/not-ready surfaces for future durable worker candidates or missing provider/runtime configuration; they do not return model-less successful worker output.

## Checks run

- `tools/validate-ai-first-saas-starter-fullstack.sh`
  - Result: failed only at optional real-provider smoke because the ambient `OPENAI_API_KEY` caused `RealModelProviderSmokeTest` to attempt a live OpenAI call that ended with TLS/remote-provider failure.
  - Classification: environment/provider-dependent validation failure, not evidence of a local substitute runtime path. The preceding rendered backend suite, Akka runtime component discovery, frontend tests, typecheck, build, and secret scan all completed before the optional smoke.
- `env -u OPENAI_API_KEY tools/validate-ai-first-saas-starter-fullstack.sh`
  - Result: passed.
  - Evidence: rendered backend compiled 124 main source files; Akka annotation processor discovered 4 HTTP endpoints, 1 Agent, 8 Views, 6 Key Value Entities, 1 service setup, and 8 Event Sourced Entities; backend tests reported 177 tests with 0 failures/errors and 1 provider-smoke skip; frontend tests/typecheck/build passed; static secret scan passed.
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run && npm run typecheck && npm run build`
  - Result: passed; 121 frontend tests passed.
- `cd frontend && npm test -- --run && npm run typecheck && npm run build`
  - Result: passed; 118 frontend tests passed.
- `find templates/ai-first-saas-starter/backend/src/main/java -type f | rg -i "LocalDemo|Substitute|FailClosed.*Repository|FailClosed.*Sink"`
  - Result: no matches.
- Production substitute scan excluding test assets:
  - `rg -n "new LocalDemo|new Substitute|AI_FIRST_SAAS_LOCAL_DEMO|local/demo repositories|FixtureWorkstream|fixtureWorkstream" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/frontend/src templates/ai-first-saas-starter/README.md --glob '!**/__tests__/**' --glob '!**/*.test.mjs' --glob '!**/node_modules/**' --glob '!**/target/**'`
  - Result: no matches.
- Required broad substitute scan:
  - Result: remaining matches are test-only fixture files under `frontend/src/__tests__/fixtures/**` plus intentional blocked/fail-closed wording in `WorkstreamService.java` that says no model-less successful output/canned guidance is returned.
- `git diff --check`
  - Result: passed.

## Follow-up decision

No new queue task is appended. The live provider smoke should be rerun by an operator with known-good provider credentials/network when validating real OpenAI connectivity, but that is an external configuration check rather than an unimplemented Akka runtime replacement task.
