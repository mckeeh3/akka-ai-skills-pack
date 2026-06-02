# Validation: Fix and run provider smoke readiness

## Task

`TASK-AARPS-02-001` applied the bounded fix from the diagnosis and reran provider smoke readiness checks.

## Code change

Updated `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/RealModelProviderSmokeTest.java` so the smoke captures `StarterSecurityComponents.agentRuntimeService()` after `StarterSecurityComponents.workstreamService(componentClient, ...)` binds the Akka runtime.

This keeps the global `PROMPT_ASSEMBLY`, `MODEL_INVOCATION`, and `AgentWorkTrace` assertions pointed at the runtime service actually used by workstream submission.

## Guardrails preserved

The fix does not weaken or remove response-level assertions for:

- `markdown_response` surface type;
- owner functional agent id;
- `ready` response status;
- no placeholder body for successful model text;
- non-blank markdown;
- no provider-secret leaks in `/api/me`, markdown, response DTOs, persisted items, persisted surfaces, or runtime traces;
- response trace shape with prompt/model/work trace ids.

The provider-skip behavior remains explicit and does not claim fake real-provider success when `OPENAI_API_KEY` is absent.

## Validation evidence

### Provider-skip script validation

Command:

```bash
env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh --keep --base-package ai.first --maven-group-id ai.first
```

Result:

```text
[starter-real-model-smoke] Akka Agent smoke skipped: OPENAI_API_KEY is not set or is blank.
[starter-real-model-smoke] To enable real provider validation: export OPENAI_API_KEY, optionally OPENAI_MODEL_ID/OPENAI_API_BASE_URL/OPENAI_REQUEST_TIMEOUT_SECONDS, then rerun this command.
```

### Real-provider smoke validation

`OPENAI_API_KEY` was configured in the harness environment.

Command:

```bash
tools/smoke-ai-first-saas-starter-real-model.sh --keep --base-package ai.first --maven-group-id ai.first
```

Result:

```text
[starter-real-model-smoke] Scaffolding starter into /tmp/ai-first-saas-starter-real-model.bViLXT
[starter-real-model-smoke] Running real provider Akka Agent smoke through backend workstream message submission
[starter-real-model-smoke] Real provider Akka Agent smoke passed without provider-secret leaks in smoke logs, frontend env, or static assets
[starter-real-model-smoke] Kept smoke target: /tmp/ai-first-saas-starter-real-model.bViLXT
```

### Provider-skip full test validation in generated starter

Command:

```bash
cd /tmp/ai-first-saas-starter-real-model.bViLXT && env -u OPENAI_API_KEY mvn test
```

Result:

```text
Tests run: 227, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

`RealModelProviderSmokeTest` was the single skipped test under absent provider config.

### Focused guardrail scans

Command:

```bash
rg -n "blocked_provider_or_runtime|fail closed|fails closed|no fake success|noFakeSuccess|model-less|fake" \
  templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/{security,agentfoundation} \
  templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/{security,agentfoundation} | wc -l
```

Result: `206` matching guardrail references.

Command:

```bash
rg -n "realModelProviderSmoke|OPENAI_API_KEY|markdown_response|traceIds\(\)\.size|Provider secret leaked|agentRuntimeService\.traces\(\)" \
  templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/RealModelProviderSmokeTest.java
```

Result: confirmed the real-provider smoke still gates on `realModelProviderSmoke` and `OPENAI_API_KEY`, asserts `markdown_response`, verifies trace id shape, checks provider-secret leakage, and retains runtime trace assertions.

## Readiness state

Real-provider smoke readiness is now passing locally with configured provider credentials. Absent provider config still skips safely without reporting model-backed success.
