# Java code review and cleanup plan

## Objective

Keep the skills pack's Java code directly useful as executable reference material for Akka Java SDK skills. Remove stale source assets, document validation, and break future cleanup into small self-contained harness sessions.

## Current decisions

- The legacy `examples/poc-user-auth-onboarding/**` PoC is removed.
- The DCA/supplies domain-specific vertical is removed.
- The starter template under `templates/ai-first-saas-starter/**` is the canonical generated-app starter source.
- Root `src/main/java` remains an executable reference fixture set, not an end-user application.
- Deletions from root `src/main/java` require skill-family inventory and focused validation because many classes are discovered by Akka annotations or exercised by route tests without direct imports.

## Completed cleanup in this pass

- Removed `examples/poc-user-auth-onboarding/**`.
- Removed unused `src/main/java/com/example/domain/security/BootstrapAdmin.java`.
- Added `docs/java-validation-guide.md`.
- Added `specs/java-source-cleanup/src-main-java-inventory.md`.
- Removed DCA/supplies app-description, Java source, tests, and static resources in the follow-up cleanup pass.

## Validation expectation

Use `docs/java-validation-guide.md` for commands. Minimum gate after Java cleanup:

```bash
mvn -q -DskipTests compile
```

Use focused tests for the changed slice. Full `mvn -q test` is a long confidence gate, not the default first command.

## Self-contained future tasks

Each task below is intended to be run in a fresh harness session. Start each task by reading:

- `AGENTS.md`
- `skills/README.md`
- `docs/java-validation-guide.md`
- `specs/java-source-cleanup/src-main-java-inventory.md`
- this file

### Task JAVA-CLEANUP-001 — Repair references to removed legacy PoC

**Goal:** Remove or reword broken references to `examples/poc-user-auth-onboarding/**` now that the PoC has been deleted.

**Scope:**

- `docs/**`
- `specs/**`
- `.agents/docs/**` only if installed-pack mirror updates are in scope for the session

**Do not edit:**

- `dist/**`
- `target/**`

**Steps:**

1. Run `grep -R "poc-user-auth-onboarding" -n docs specs .agents --exclude-dir=target --exclude-dir=dist`.
2. For active docs, replace PoC guidance with references to `templates/ai-first-saas-starter/**` and current root security/admin reference packages.
3. For historical migration specs, either leave as historical provenance or add a short note that the PoC was removed after migration.
4. Run a final grep and report any intentional historical references left behind.

**Validation:**

```bash
grep -R "poc-user-auth-onboarding" -n docs specs .agents --exclude-dir=target --exclude-dir=dist || true
```

### Task JAVA-CLEANUP-002 — Rendered starter template Java validation

**Goal:** Add or document a repeatable check that renders `templates/ai-first-saas-starter/backend` and compiles/tests the rendered backend.

**Scope:**

- `templates/ai-first-saas-starter/backend/**`
- existing scaffold/render scripts under `bin/`, `scripts/`, or `.agents/bin/` if present
- `docs/java-validation-guide.md`

**Steps:**

1. Find the existing scaffold/render script for the starter template.
2. Render the backend into a temporary directory with a package such as `ai.first.validation`.
3. Run `mvn -q -DskipTests test-compile` and, if reasonable, `mvn -q test` in the rendered backend.
4. If no script exists, create the smallest non-invasive validation script under an appropriate repo script directory.
5. Update `docs/java-validation-guide.md` with the exact command.

**Validation:** rendered starter backend compiles and tests pass or documented blockers are explicit.

### Task JAVA-CLEANUP-003 — Decide core security/admin reference ownership

**Goal:** Decide whether root security/admin code remains a canonical executable reference, moves to a named example slice, or is superseded by the starter template.

**Scope:**

- `src/main/java/com/example/api/security/**`
- `src/main/java/com/example/application/security/**`
- `src/main/java/com/example/domain/security/**`
- `src/main/java/com/example/security/**`
- matching tests under `src/test/java/com/example/application/security/**` and `src/test/java/com/example/security/**`
- relevant skills/docs: `core-saas-foundation`, `akka-workos-user-auth`, `akka-basic-user-admin`, `akka-saas-invitation-onboarding`, `akka-resend-email-service`

**Output:** a short decision record under `specs/java-source-cleanup/` with one of:

1. keep and document as canonical richer reference;
2. move to a named example/source asset;
3. deprecate after starter coverage reaches parity.

**Validation:** run security/admin focused tests if code changes are made.

### Task JAVA-CLEANUP-004 — Frontend reference endpoint decision

**Goal:** Decide whether `FrontendReferenceApiEndpoint` and `FrontendReferenceUiEndpoint` are canonical web UI fixtures or redundant.

**Scope:**

- `src/main/java/com/example/api/FrontendReferenceApiEndpoint.java`
- `src/main/java/com/example/api/FrontendReferenceUiEndpoint.java`
- matching static resources under `src/main/resources/`
- `src/test/java/com/example/application/FrontendReferenceWebUiIntegrationTest.java`
- web UI skills/docs

**If keeping:** add explicit docs/skill references naming this as the lightweight frontend-reference fixture.

**If removing:** remove endpoints, resources, and test together, then run focused endpoint tests plus compile.

### Task JAVA-CLEANUP-005 — Test phase/profile cleanup proposal

**Goal:** Reduce harness friction by separating fast deterministic tests from Akka runtime integration tests without losing coverage.

**Scope:**

- root `pom.xml`
- `src/test/java/**`
- `docs/java-validation-guide.md`

**Steps:**

1. Count and classify tests by unit/entity/service/integration/stream/gRPC/MCP/agent.
2. Propose either Maven profiles/tags or a documented command-only strategy.
3. Do not rename many tests or change CI behavior without explicit approval.

**Output:** proposal under `specs/java-source-cleanup/` and any small doc update.

### Task JAVA-CLEANUP-006 — Remove fixed sleeps from stream/realtime tests

**Goal:** Replace brittle `Thread.sleep(...)` patterns in stream/realtime tests with deterministic Awaitility or route/client readiness checks.

**Scope:**

- `src/test/java/**/*Stream*Test.java`
- `src/test/java/**/*WebSocket*Test.java`
- endpoint integration tests using SSE/background threads

**Known starting grep:**

```bash
grep -R "Thread.sleep" -n src/test/java
```

**Validation:** run the affected stream/realtime tests with a generous timeout.

## Stop rules for future sessions

Stop and ask before:

- deleting an entire skill-family fixture;
- changing public starter template behavior;
- altering Maven test phase semantics that CI may rely on;
- removing docs/specs that are historical provenance rather than active guidance.
