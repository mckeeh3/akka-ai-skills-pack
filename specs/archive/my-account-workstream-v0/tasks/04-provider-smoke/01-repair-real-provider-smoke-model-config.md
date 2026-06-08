# Task: Repair real-provider smoke model compatibility

## Objective

Make the starter's optional real-provider smoke pass when `OPENAI_API_KEY` is present with the documented/default `OPENAI_MODEL_ID`, without weakening fail-closed behavior when provider configuration is absent or invalid.

## Trigger

`TASK-MYACCT-99-001` ran `tools/validate-ai-first-saas-starter-fullstack.sh`. Backend tests, frontend tests, frontend typecheck, frontend build, static secret scan, and provider-missing skip behavior passed, but the optional real-provider smoke failed because the rendered test/provider configuration sent `temperature = 0.1` to an OpenAI model that rejected non-default temperature.

## Required inherited reads

- `specs/my-account-workstream-v0/workstream-contract.md`
- `specs/my-account-workstream-v0/capability-inventory.md`
- `templates/ai-first-saas-starter/README.md`

## In scope

- Update the starter template and rendered smoke/test configuration so real-provider model-backed workstream submission remains compatible with supported/default OpenAI models.
- Preserve the concrete Akka `WorkstreamRuntimeAgent` path, governed runtime tool registration, trace assertions, provider-secret redaction, and missing-provider fail-closed behavior.
- Update README or env guidance if configuration semantics change.
- Add or adjust targeted tests for the provider configuration boundary.

## Out of scope

- Do not bypass the Akka `Agent` component with a direct provider/service call.
- Do not introduce deterministic/model-less normal runtime fallback.
- Do not expose provider secrets to frontend, responses, logs, or trace payloads.
- Do not implement unrelated workstreams beyond shared provider-smoke compatibility required for the five-core runtime.

## Required checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `git diff --check`

## Done criteria

- With `OPENAI_API_KEY` present, the optional real-provider smoke either passes against the documented/default model configuration or fails closed only for genuine provider/auth/network failures with actionable, sanitized output.
- With provider configuration absent, the smoke still skips loudly and normal runtime behavior remains fail-closed.
- My Account and other five-core request/response workstream turns still invoke the concrete Akka Agent through the governed runtime path in tests.
