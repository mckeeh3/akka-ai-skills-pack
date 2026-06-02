# My Account Personal Attention Digest AutonomousAgent Handoff

## Status

The AI-first SaaS starter now includes an implemented bounded **My Account Personal Attention Digest** `AutonomousAgent` vertical. It is a personal, signed-in-user digest of backend-authorized attention evidence, not a general notification platform and not an email, push, scheduled, or enterprise digest system.

Implemented starter-template paths include:

- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/MyAccountPersonalAttentionDigestAutonomousAgent.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/ComponentClientMyAccountPersonalAttentionDigestAutonomousAgentRuntime.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/FailClosedMyAccountPersonalAttentionDigestAutonomousAgentRuntime.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MyAccountPersonalAttentionDigestService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/security/MyAccountPersonalAttentionDigestTask.java`
- `templates/ai-first-saas-starter/frontend/src/workstream-my-account-vertical.contract.test.mjs`

## Runtime boundary

The worker uses the concrete Akka `AutonomousAgent` task lifecycle for normal model-backed success. The digest runtime starts and observes a durable task, projects backend-owned task state, emits `workflow.my_account.personal_attention_digest.*` and shared `worker.task.*` v3 events, and derives My Account progress/result/blocked surfaces from backend projections.

Provider or runtime absence fails closed. Missing provider/model configuration, `ComponentClient`, governed profile, tool grants, or authorized evidence access must produce blocked provider/runtime state with safe recovery copy, v3 events, and attention. It must never produce deterministic, canned, simulated, fake, fixture-only, or model-less normal success.

## Evidence and redaction

The digest reads only authorized personal attention evidence for the selected `AuthContext`. Hidden workstreams and hidden attention items must not leak by name, count, severity, category, trace id, omitted-source wording, or inferred existence. Digest aggregate counts are counts of authorized included evidence only, and source attention remains authoritative.

Human accept/reject actions record disposition for the advisory digest task only. They do not acknowledge, dismiss, resolve, expire, or mutate source attention items and do not perform protected workstream actions.

## Surfaces and future notification boundary

Implemented surfaces are My Account personal attention digest progress/result/blocked surfaces. They are backend-derived and capability-backed. They are intended to help the signed-in user decide where to look next in authorized attention.

Future notification platform work remains separate. Scheduled digests, enterprise notification preferences, email/push delivery, fan-out routing, cross-account subscriptions, and notification delivery analytics must be modeled as new governed capabilities and queues rather than hidden inside this personal digest worker.

## Validation evidence

Validation is recorded in `specs/my-account-personal-attention-digest-autonomous-agent/validation/01-personal-attention-digest-validation.md`.

Provider-skip fullstack validation passed with:

- scaffolded backend Maven tests: `Tests run: 227, Failures: 0, Errors: 0, Skipped: 1`;
- digest backend tests included: `MyAccountPersonalAttentionDigestServiceTest` and `MyAccountPersonalAttentionDigestAutonomousAgentTest`;
- frontend tests: `132` passing tests;
- frontend typecheck and Vite build passed;
- optional real-provider smoke skipped only when `OPENAI_API_KEY` was intentionally unset.

A separate ambient real-provider smoke failed with a sanitized assertion failure and remains a local real-provider smoke blocker. That blocker is not treated as fake success or as a model-less substitute for normal runtime behavior.
