# Personal Attention Digest Validation

## Task

`TASK-MAPAD-04-001` validated the rendered `templates/ai-first-saas-starter` scaffold after the My Account Personal Attention Digest AutonomousAgent runtime, event, attention, and surface wiring tasks.

## Commands and evidence

### Initial fullstack validation

Command:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh --keep
```

Result: failed during scaffolded backend test compilation because `MyAccountPersonalAttentionDigestServiceTest` asserted `event.surfaceId()`, but `WorkstreamEventEnvelope` exposes the v3 surface accessor as `targetSurfaceId()`.

Fix applied in the starter template test:

- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/MyAccountPersonalAttentionDigestServiceTest.java`
- changed the digest result-surface event assertion from `surfaceId()` to `targetSurfaceId()`.

### Fullstack validation with ambient provider key

Command:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh --keep
```

Result: scaffolded backend Maven tests, frontend tests, frontend typecheck, frontend build, static-resource checks, and secret scan completed before the optional real provider smoke. The optional smoke used the ambient `OPENAI_API_KEY` and failed in `RealModelProviderSmokeTest.workstreamMessageSubmissionUsesRealProviderAndEmitsTraceShape` with a sanitized assertion failure. This is recorded as a local real-provider smoke blocker, not as a deterministic/model-less success substitute.

Kept scaffold target: `/tmp/ai-first-saas-starter-fullstack.CLQkA4`.

### Fullstack validation in provider-skip mode

Command:

```bash
env -u OPENAI_API_KEY tools/validate-ai-first-saas-starter-fullstack.sh --keep
```

Result: passed.

Evidence:

- scaffold render: 431 files rendered into `/tmp/ai-first-saas-starter-fullstack.bh4DGW`
- backend Maven tests: `BUILD SUCCESS`; `Tests run: 227, Failures: 0, Errors: 0, Skipped: 1`
- digest backend tests included: `MyAccountPersonalAttentionDigestServiceTest` (`6` tests) and `MyAccountPersonalAttentionDigestAutonomousAgentTest` (`2` tests)
- frontend tests: `132` tests, `132` pass
- frontend typecheck: `tsc --noEmit` passed
- frontend build: Vite built Akka static resources under `src/main/resources/static-resources`
- static resource scan: no backend secret marker found
- optional real provider smoke: skipped because `OPENAI_API_KEY` was intentionally unset for provider-skip validation

Kept scaffold target: `/tmp/ai-first-saas-starter-fullstack.bh4DGW`.

## Manual/local smoke notes

- Rendered scaffold compiles the concrete Akka component set with `5 autonomous-agent` components, including the My Account personal attention digest AutonomousAgent.
- The digest-specific backend service and AutonomousAgent tests run in the rendered scaffold and pass after the test accessor fix.
- The frontend My Account and attention contract tests pass in the rendered scaffold, and the static-resource build succeeds.
- No frontend-only digest success was used as validation evidence.
- No deterministic/model-less normal-runtime digest success was claimed. The validation evidence is scaffolded tests plus provider-skip fullstack validation; the ambient real-provider smoke failure remains a local provider smoke blocker for real-provider validation.
