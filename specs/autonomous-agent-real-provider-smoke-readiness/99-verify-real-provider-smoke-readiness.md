# Verification: Real-provider smoke readiness

## Task

`TASK-AARPS-99-001` verified the mini-project done state after diagnosis, fix, and docs updates.

## Assessment

Real-provider smoke readiness is **complete** for this mini-project.

- The recorded My Account Personal Attention Digest ambient smoke failure was diagnosed as a brittle `RealModelProviderSmokeTest` setup issue, not a digest runtime bug.
- The smoke fix captures `StarterSecurityComponents.agentRuntimeService()` after `workstreamService(...)` binds the Akka runtime, so global trace assertions read the runtime actually used by workstream submission.
- Provider-skip behavior remains explicit and safe for CI when `OPENAI_API_KEY` is absent.
- Configured real-provider smoke passes locally through the rendered starter.
- Fail-closed/no-fake-success guardrails remain documented and present in source/test scans.

## Verification commands

### Provider-skip validation

Command:

```bash
env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh --keep --base-package ai.first --maven-group-id ai.first
```

Result:

```text
[starter-real-model-smoke] Akka Agent smoke skipped: OPENAI_API_KEY is not set or is blank.
[starter-real-model-smoke] To enable real provider validation: export OPENAI_API_KEY, optionally OPENAI_MODEL_ID/OPENAI_API_BASE_URL/OPENAI_REQUEST_TIMEOUT_SECONDS, then rerun this command.
```

### Configured real-provider smoke

`OPENAI_API_KEY` was configured in the harness environment.

Command:

```bash
tools/smoke-ai-first-saas-starter-real-model.sh --keep --base-package ai.first --maven-group-id ai.first
```

Result:

```text
[starter-real-model-smoke] Scaffolding starter into /tmp/ai-first-saas-starter-real-model.89SRkI
[starter-real-model-smoke] Running real provider Akka Agent smoke through backend workstream message submission
[starter-real-model-smoke] Real provider Akka Agent smoke passed without provider-secret leaks in smoke logs, frontend env, or static assets
[starter-real-model-smoke] Kept smoke target: /tmp/ai-first-saas-starter-real-model.89SRkI
```

### Guardrail scans

Command:

```bash
rg -n "blocked_provider_or_runtime|fail closed|fails closed|no fake success|noFakeSuccess|model-less|fake" \
  templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/{security,agentfoundation} \
  templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/{security,agentfoundation} | wc -l
```

Result: `206` matching guardrail references.

Command:

```bash
rg -n "OPENAI_API_KEY|provider-skip|real-provider|real provider|fail-closed|fake|model-less|smoke-ai-first-saas-starter-real-model|RealModelProviderSmokeTest|provider configuration" \
  specs/autonomous-agent-real-provider-smoke-readiness/README.md \
  templates/ai-first-saas-starter/README.md \
  specs/my-account-personal-attention-digest-autonomous-agent/my-account-personal-attention-digest-handoff.md \
  specs/full-core-smb-polish-release-readiness/release-handoff.md
```

Result: docs/handoffs contain provider configuration, provider-skip validation, configured real-provider smoke, fail-closed behavior, troubleshooting, and no fake/model-less success guidance.

## Follow-up tasks

None. The mini-project has no remaining runnable tasks.
