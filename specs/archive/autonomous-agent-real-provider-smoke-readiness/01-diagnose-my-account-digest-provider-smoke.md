# Diagnosis: My Account digest real-provider smoke blocker

## Task

`TASK-AARPS-01-001` diagnosed the recorded ambient real-provider smoke failure from the My Account Personal Attention Digest handoff.

## Classification

The reproduced failure is a **brittle real-provider smoke assertion/test setup issue**, not a My Account Personal Attention Digest runtime bug and not evidence of deterministic/model-less success.

The failing test is the starter's optional real-provider workstream smoke:

- `RealModelProviderSmokeTest.workstreamMessageSubmissionUsesRealProviderAndEmitsTraceShape`
- template source: `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/RealModelProviderSmokeTest.java`

The failure occurs after the per-workstream real-provider response assertions complete. The assertion failure is at the global in-process trace-sink check:

```java
assertTrue(agentRuntimeService.traces().stream().anyMatch(trace -> trace.traceType().equals("PROMPT_ASSEMBLY")));
```

## Root cause

`RealModelProviderSmokeTest` captures `agentRuntimeService` before creating the `WorkstreamService` used for the smoke:

```java
var agentRuntimeService = StarterSecurityComponents.agentRuntimeService();
var service = StarterSecurityComponents.workstreamService(componentClient, new LocalDemoWorkstreamLogRepository());
```

`StarterSecurityComponents.workstreamService(componentClient, ...)` calls `bindAkkaRuntime(componentClient)`, which replaces the static `AgentRuntimeService` with a newly constructed Akka-backed instance. The `WorkstreamService` uses the newly rebound runtime service, while the local `agentRuntimeService` variable still points at the previous instance and its previous in-process trace sink.

As a result, model-backed workstream invocations can complete and return response-level trace ids, while the final test assertion reads the wrong in-process trace list. This makes the smoke brittle. The assertion should use the post-binding runtime service or durable/response trace evidence instead of a stale pre-binding service reference.

## Evidence

### Real-provider reproduction with configured provider

Command:

```bash
tools/smoke-ai-first-saas-starter-real-model.sh --keep --base-package ai.first --maven-group-id ai.first
```

Result: failed in a fresh scaffold at `/tmp/ai-first-saas-starter-real-model.DVvDiV`.

Important output:

- real provider env was present, so the optional smoke ran instead of skipping;
- Akka runtime started with `1 agent` and `5 autonomous-agent` components;
- failure: `RealModelProviderSmokeTest.workstreamMessageSubmissionUsesRealProviderAndEmitsTraceShape(RealModelProviderSmokeTest.java:94)`;
- assertion: `expected: <true> but was: <false>`;
- line 94 is the first global `agentRuntimeService.traces()` assertion for `PROMPT_ASSEMBLY`.

A rerun against the prior kept scaffold `/tmp/ai-first-saas-starter-fullstack.CLQkA4` reproduced the same line-94 failure.

### Provider-skip and targeted digest context

Command:

```bash
env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh --target /tmp/ai-first-saas-starter-real-model.DVvDiV --base-package ai.first --maven-group-id ai.first
```

Result: passed by explicitly reporting the safe provider-skip state because `OPENAI_API_KEY` was unset.

Command:

```bash
cd /tmp/ai-first-saas-starter-real-model.DVvDiV && \
  env -u OPENAI_API_KEY mvn -Dtest=RealModelProviderSmokeTest,MyAccountPersonalAttentionDigestServiceTest,MyAccountPersonalAttentionDigestAutonomousAgentTest test
```

Result: `BUILD SUCCESS`; `Tests run: 9, Failures: 0, Errors: 0, Skipped: 1`.

This confirms the provider-skip path and My Account Personal Attention Digest targeted tests still pass without claiming fake real-provider success.

## Suggested fix boundary for TASK-AARPS-02-001

A bounded fix should make `RealModelProviderSmokeTest` assert against the runtime service that is actually used after Akka binding, for example by moving the local `agentRuntimeService` assignment after `StarterSecurityComponents.workstreamService(componentClient, ...)`, or by reading `StarterSecurityComponents.agentRuntimeService()` after service construction before the global trace assertions.

Keep these guardrails:

- do not weaken response-level checks for `markdown_response`, owner functional agent id, no placeholder body, no provider-secret leaks, and response trace shape;
- do not turn absent provider config into success;
- do not replace the real Akka Agent path with direct provider calls or deterministic/model-less output;
- keep provider-skip validation explicit and safe for CI.
