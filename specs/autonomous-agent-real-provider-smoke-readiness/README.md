# AutonomousAgent Real-Provider Smoke Readiness

## Purpose

Resolve and document real-provider smoke readiness for the completed AutonomousAgent worker set.

Provider-skip validation is strong across the starter, but the My Account Personal Attention Digest handoff records a local real-provider smoke blocker: an ambient real-provider smoke failed with a sanitized assertion failure. This mini-project should diagnose that failure, make real-provider smoke checks actionable and non-brittle, and preserve fail-closed behavior when provider configuration is absent.

## Source context

Builds on:

- `specs/my-account-personal-attention-digest-autonomous-agent/`
- `specs/autonomous-agent-fullstack-regression-readiness/`
- completed AutonomousAgent worker handoffs and validation artifacts
- starter provider/model config, test provider, fail-closed runtime, and fullstack validation scripts

## Scope

- Diagnose the My Account digest real-provider smoke failure.
- Improve real-provider smoke assertions so failures are actionable without weakening runtime guarantees.
- Verify provider-skip validation still passes.
- Add or update docs for safe real-provider smoke execution.
- Confirm fail-closed behavior remains correct when provider config is absent.

## Non-goals

- Do not implement a new AutonomousAgent worker.
- Do not require provider secrets for ordinary provider-skip validation.
- Do not bypass governed runtime, model policy, tool boundaries, evidence authorization, or fail-closed behavior.
- Do not turn a failed real-provider smoke into fake/model-less success.

## Done state

Complete when:

- the real-provider smoke blocker is diagnosed and fixed or documented as an external provider/config blocker with actionable guidance;
- provider-skip fullstack validation still passes;
- real-provider smoke can be run safely when configured;
- absent provider config still fails closed;
- docs/handoff explain commands, required secrets, expected outcomes, and troubleshooting.
