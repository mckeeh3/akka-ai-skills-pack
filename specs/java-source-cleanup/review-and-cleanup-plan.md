# Java Source Review and Cleanup Plan

## Scope

Review source-controlled Java reference assets only:

- `src/main/java` and `src/test/java` — executable reference examples for the skills pack.
- `templates/ai-first-saas-starter/backend/src` — generated starter source templates.
- `examples/poc-user-auth-onboarding/src` — removed legacy portable security/onboarding proof-of-concept; historical references may remain in migration specs only.

Exclude generated or packaged output:

- `target/`
- `dist/`

## Baseline validation

Observed in this repository:

- `mvn -q -DskipTests compile` succeeds for the root reference project.
- Full root `mvn test` starts many Akka runtime-backed tests and can exceed a 120s harness timeout even when completed reports show passing tests.
- The root test tree contains many integration-style tests in the normal Maven test phase; this should be treated as a validation-structure issue, not necessarily a code failure.

## Priority findings

### P1 — Legacy POC security sample was superseded and removed

`examples/poc-user-auth-onboarding` duplicated older versions of user/admin/security reference code while the canonical direction moved to:

- `templates/ai-first-saas-starter/backend/src/**` for generated starter code.
- `src/main/java/com/example/domain/security/**` and related `api/security` / `application/security` files for richer executable security/reference examples.

Cleanup result:

1. Removed the PoC from normal examples.
2. Updated active app-description docs to point to the starter template and current root security reference packages.
3. Later removed the DCA app-description and supplies executable vertical when it no longer provided unique skills-pack value.

### P1 — Root `src/` is an overgrown all-in-one reference app

The root executable reference project now mixes many unrelated examples: carts, orders, workflows, agents, security, frontend, MCP, gRPC, SSE, WebSocket, runtime agent state, and governed agent foundation patterns.

Cleanup direction:

1. Keep root `src/` buildable as a compatibility gate.
2. Identify canonical minimal examples per skill family.
3. Move or archive showcase slices that no longer teach a focused pattern.
4. Avoid adding more unrelated examples to root `src/` unless they are required as executable fixtures.

### P1 — Java test phases are not separated by cost/risk

Root `src/test/java` includes many `*IntegrationTest.java` and `TestKitSupport` tests in the default Maven test phase. This makes `mvn test` expensive for routine harness runs.

Cleanup direction:

1. Define a fast default validation gate: compile + deterministic unit/entity/service tests.
2. Run Akka runtime integration tests in focused slice gates.
3. Run SSE/WebSocket/gRPC/long-polling tests separately with a larger timeout.
4. Consider Maven Surefire/Failsafe split or test tags after confirming existing CI expectations.

### P2 — Starter template services are large and should be decomposed before more features accrete

Large files such as the starter invitation/email/admin services are useful but risk becoming generated-app sludge.

Cleanup direction:

1. Keep generated starter behavior stable.
2. Extract token hashing, authorization checks, delivery/outbox behavior, and DTO grouping only when doing a deliberate starter refactor.
3. Add rendered-template compile smoke validation if not already present in packaging checks.

## Immediate cleanup applied

- Removed `examples/poc-user-auth-onboarding/**` as a legacy PoC superseded by the starter template and current root security/admin reference code.
- Removed unused `src/main/java/com/example/domain/security/BootstrapAdmin.java`.
- Added `docs/java-validation-guide.md`.
- Added `specs/java-source-cleanup/src-main-java-inventory.md`.
- Added `specs/java-source-cleanup/java-code-review-and-cleanup-plan.md` with self-contained future task briefs.
- Removed DCA/supplies app-description, Java source, tests, and static resources in the follow-up cleanup pass.

## Recommended next tasks

1. Repair active docs/spec references to the removed PoC; leave only explicit historical provenance references.
2. Add rendered-starter template compile/test validation.
3. Decide ownership of the core security/admin root reference slice.
4. Decide whether the frontend reference endpoint pair is canonical or redundant.
5. Introduce Maven profiles/tags only after confirming whether current CI expects all `*IntegrationTest` tests under `mvn test`.
