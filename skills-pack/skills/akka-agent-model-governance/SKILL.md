---
name: akka-agent-model-governance
description: Design and implement governed model configuration for managed Akka agents, including ModelConfigRef, provider secret boundaries, tenant/agent/task selection, fallback model policy, audit/work traces, and tests.
---

# Akka Agent Model Governance

Use this skill when agent behavior profiles include runtime model selection or model configuration references, especially in secure AI-first SaaS apps where tenants, agents, tasks, or modes may use different approved model aliases.

This skill governs model configuration. It does not replace `akka-agent-component` for Java `Agent` class structure or `akka-agent-behavior-profiles` for durable `AgentDefinition` lifecycle.

Use this skill only for model-selection authority, policy, fallback, secret-boundary, and model-use trace concerns. Use `akka-agent-behavior-profiles` for which agent references a model config, `app-description-functional-agent-modeling` / `docs/workstream-expertise-model.md` for the per-workstream expert bundle contract, `akka-agent-tool-boundaries` for tool/data/side-effect authority, and prompt/skill governance skills for model-visible behavior guidance. Prompt text, skill text, or evaluator recommendations cannot select an unapproved provider or bypass model policy.

## Required reading

Read these first if present:
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/agent-runtime-invocation-pattern.md`
- `../docs/agent-coverage-matrix.md`
- `../akka-agent-behavior-profiles/SKILL.md`
- `../akka-agent-component/SKILL.md`
- `../akka-agent-work-trace/SKILL.md`
- `../examples/akka-components/src/main/java/com/example/application/ConfiguredModelActivityAgent.java`
- `../examples/akka-components/src/test/java/com/example/application/ConfiguredModelActivityAgentTest.java`

## Use when the request mentions

- `ModelConfigRef`, model alias, model profile, or model selection
- tenant-, agent-, task-, capability-, mode-, or workflow-specific model policy
- provider secret boundary, model provider credentials, or frontend secret exposure
- fallback model policy, denied provider, disabled model, or provider outage behavior
- model usage audit, model config change audit, or model references in `AgentWorkTrace`
- `ModelProvider.fromConfig(...)` examples that need governance alignment

## Core model

Keep provider configuration and governed model references separate:

```text
ModelConfigRef
- tenantId or platformScope
- modelConfigRefId
- displayName
- providerAlias: configured provider/model alias, not a secret
- allowedAgentDefinitionIds optional
- allowedCapabilityIds optional
- allowedModes: runtime | test | replay | evaluation
- allowedAuthorityLevels optional
- fallbackPolicyRef optional
- status: draft | active | disabled | archived
- createdBy / updatedBy / timestamps
```

```text
ModelPolicy
- policyId
- tenantId or platformScope
- allowedProviderAliases
- deniedProviderAliases
- maxCostTier / maxLatencyTier optional
- dataResidency / retention / safety constraints optional
- fallbackOrder: ordered `ModelConfigRef` ids or explicit `noFallback`
- requiresApprovalForChanges: boolean
- traceLevel: summary | detailedReference
```

Provider secrets, API keys, base URLs with embedded credentials, and service-account credentials belong in deployment/runtime configuration or secret management, never in `ModelConfigRef`, `AgentDefinition`, prompt/skill content, browser state, trace payloads, or app-description examples.

## Runtime selection rules

Before model invocation, the runtime resolver must verify:

0. The workstream expert bundle names either an explicit `ModelConfigRef`/`ModelPolicy` pair or an explicit inherited governed default model binding; no implicit provider fallback or prompt-selected model is allowed.
1. `AgentDefinition.modelConfigRef` exists or resolves through the governed default binding, and belongs to the tenant/platform scope allowed for the selected `AuthContext`.
2. The referenced model config is active for the invocation mode.
3. The selected agent, capability, authority level, and task type are allowed by `ModelPolicy`.
4. Prompt/skill text cannot override model policy or request an unapproved provider.
5. Fallback model use follows an explicit `fallbackPolicyRef`; no implicit provider fallback.
6. Provider secrets are resolved only by backend runtime configuration and are never returned to the model, browser, trace views, or agent tools.
7. `AgentWorkTrace` records the `ModelConfigRef`, policy decision, fallback decision when used, and safe provider/model alias summary.

For a static Java example of configured model aliases, use `ConfiguredModelActivityAgent`, which demonstrates `ModelProvider.fromConfig("openai-low-temperature")`. In managed agents, prefer resolving the approved `ModelConfigRef` before invoking the Java `Agent` or passing a known-safe alias into the invocation wrapper.

## Akka component mapping

Use Akka state only when model choices are tenant-managed or behavior-governed:

- Event Sourced Entity for model configuration lifecycle when changes affect production behavior or audit.
- Key Value Entity for a small current-state model registry only when version history is intentionally not needed and audit is emitted elsewhere.
- Views for model catalog, allowed-by-agent, disabled models, and pending changes.
- Consumers for audit/work-trace normalization of model config changes and runtime model-use events.
- HTTP endpoints and web UI for authorized admin model catalog and policy review.

Do not make provider secrets editable through generic agent governance UI. Admin UI may reference provider aliases and health/status, but secret values remain deployment-controlled.

## Capability and authority rules

- Model changes are behavior changes. Treat activation, fallback expansion, provider changes, and higher-capability model access as governed operations.
- Expanding a model policy to allow more expensive, less constrained, external, or higher-risk models should require approval or a documented safe admin simplification.
- Tenant admins may manage tenant model policy only if the SaaS product grants that capability; SaaS Owner provider administration remains separate from tenant data access.
- Test/replay/evaluation model overrides are mode-labeled, permission-checked, side-effect safe by default, and traced.
- A disabled model config must deny runtime invocation before model call and emit a denial trace.

## Tests to plan

Plan tests for:

- active `ModelConfigRef` resolution for an active `AgentDefinition`;
- disabled, archived, cross-tenant, or unauthorized model config denial before model invocation;
- forbidden provider alias denial by `ModelPolicy`;
- fallback model policy success and `noFallback` denial behavior;
- provider secret boundary: no API key/secret in frontend API responses, traces, prompts, skills, or model-visible context;
- model config change audit and runtime `AgentWorkTrace` model reference;
- prompt/skill attempts to request unauthorized model/provider do not change backend selection;
- deterministic static model-alias tests with `TestModelProvider`, as shown by `ConfiguredModelActivityAgentTest`.

## Review checklist

Before finishing, verify:

- every LLM-backed workstream expert bundle has an explicit `ModelConfigRef`/`ModelPolicy` or explicit inherited governed default model binding;
- `AgentDefinition` stores a `ModelConfigRef`, not provider secrets;
- model policy is checked before Java Agent invocation;
- fallback behavior is explicit and traceable;
- browser/admin APIs expose only safe aliases and policy metadata;
- traces include safe model references and deny/fallback decisions without secrets;
- tests cover denied provider/secret exposure paths as well as success paths.
