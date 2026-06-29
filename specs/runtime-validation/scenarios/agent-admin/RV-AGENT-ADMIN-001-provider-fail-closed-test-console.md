---
id: RV-AGENT-ADMIN-001
title: Agent Admin test console fails closed when provider configuration is missing
workstream: agent-admin
surface: agent-admin-test-console
persona: saas-admin
environment: local-dev
dataSetup:
  - base-organization
authMode: workos-test-users
executionMode: human-manual
executionStatus: authored-not-run
readinessClaim: not-run
---

# Purpose

Validate that the Agent Admin managed-agent test console and loader/tool-boundary paths fail closed when model provider configuration is absent, without exposing secrets or bypassing governed tool permissions.

# Prerequisites

- Start the app using `environments/local-dev.md` with model provider credentials intentionally absent or recorded as withheld.
- Prepare `data-setups/base-organization.md`.
- Log in as `personas/saas-admin.md`.
- Record the managed-agent definition or catalog entry selected for the test console.

# Runtime path

`SaaS admin -> Agent Admin catalog/detail/test-console surface -> surface_action or protected workstream API -> managed-agent governance/test-console governed tool -> AgentRuntimeService/loader/tool-permission boundary/model-provider client -> provider-missing fail-closed result surface and AgentRuntimeTrace evidence`

# Surface, adapter, and governed-tool contract

- Surface graph node: Agent Admin catalog/detail/test-console.
- Action edge: run test prompt or load managed-agent behavior for inspection.
- Actor adapter/source: browser `surface_action`; chat-plan proposals for test-console actions require explicit confirmation when available.
- Governed tool scope: managed-agent test/loader tools for SaaS admin only; no provider secrets exposed to frontend.
- Tool-boundary behavior: disallowed loader/tool access must return a denial trace, not a best-effort provider call.

# Setup

The base setup creates the SaaS admin identity. The run must record the model provider state as missing/withheld for this scenario. Seed data may identify an existing starter managed-agent but must not inject a passing provider response.

# Human UI validation script

1. Open the local frontend URL and log in as `saas.admin@example.com`.
2. Navigate to Agent Admin and open a managed-agent catalog/detail entry.
3. Open the test console or equivalent test action.
4. Submit a harmless test prompt with provider credentials absent.
5. Record the fail-closed result message and browser/network status.
6. Attempt or inspect a loader/tool-boundary action that should be denied for missing permission or missing provider configuration.
7. Confirm no provider secret values appear in the browser, network payloads, logs captured for evidence, or result surfaces.

# Expected results

- The test console does not execute a model-backed normal path without configured provider credentials.
- The user receives an actionable fail-closed message suitable for an administrator.
- Loader/tool-boundary denials are explicit, scoped, and traceable.
- No browser-visible payload contains provider keys, backend secrets, or hidden prompts beyond approved browser-safe display.
- Repeating the same failed test is idempotent and creates bounded diagnostic traces rather than changing managed-agent state.
- Agent runtime traces capture provider-missing and denied loader/tool attempts.

# Evidence to capture

- Selected agent id/name and test prompt summary.
- Provider configuration state recorded as missing/withheld.
- Screenshots or DOM observations of fail-closed and denial messages.
- Network/API statuses and sanitized payload snippets.
- AgentRuntimeTrace, audit, or work trace ids for provider-missing and tool-boundary events.
- Browser secret-boundary observation.

# Failure classification hints

- `provider/config blocker` if the scenario cannot place the app in provider-missing mode or needs provider credentials unexpectedly.
- `implementation gap` for model execution without provider config, missing tool-boundary enforcement, or secret exposure.
- `UX/state gap` for vague provider-missing copy or missing recovery instructions.
- `test gap` for missing trace visibility.
- `auth/setup gap` for missing SaaS admin mapping.
