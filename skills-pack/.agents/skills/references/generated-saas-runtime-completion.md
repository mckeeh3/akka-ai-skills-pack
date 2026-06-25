# Generated SaaS runtime completion doctrine

A generated-app or SaaS Foundation App feature is complete only when the real local runtime path works at the stated scope. A task, slice, workstream, or verification note must not use the unqualified word `complete` for user-visible behavior unless it also names the achieved readiness level and records the runtime evidence that justifies it.

For model-backed workstream behavior, normal message submission must invoke a concrete Akka `Agent` component through the governed runtime path:

- active `AgentDefinition` and model/provider configuration;
- governed prompt, skill, reference, and manifest resolution;
- authorized `readSkill(skillId)` and `readReferenceDoc(referenceId)` loader tools where assigned;
- `ToolPermissionBoundary` enforcement;
- resolved runtime tools registered with `effects().tools(runtimeTools)`;
- provider invocation from the Akka Agent path;
- durable prompt/skill/reference/model/tool/data/policy trace facts and user-visible safe failure surfaces.

Missing provider or security configuration should fail closed with actionable errors and traces.

Do not count deterministic/demo/mock/simulated/model-less normal runtime behavior as implemented for workstream agents, auth, durability, protected capabilities, authorization denials, provider calls, audit/work traces, or work traces. Fixtures, mocks, deterministic fakes, and test doubles are allowed only in tests, local-only harness checks, or explicitly named test adapters; they must not be wired as the default user-facing runtime path.

## Readiness vocabulary

Use these labels to prevent contract/rendering work from being mistaken for runtime completion:

- `described` — app-description/spec intent exists, but no implementation proof.
- `surface-ready` — structured surface contract or rendering exists, but backend/API/runtime behavior is not fully proven.
- `backend-ready` — backend service/component behavior is tested, but the browser/API path is not proven.
- `frontend-rendered` — frontend components render, but production action round trips are not proven.
- `api-smoked` — the protected API/workstream endpoint path was exercised locally with role/context expectations.
- `browser-smoked` — the local browser/DOM/manual flow was exercised against the intended API/client path.
- `manual-ready` — enough manual/browser/API evidence exists for human manual testing at the stated scope.
- `runtime-ready` — the intended local Akka/API/UI path works at the stated scope, including required auth, tenant scope, side effects, audit/work traces, tests, and provider behavior or explicit fail-closed evidence.

`surface-ready`, `backend-ready`, `frontend-rendered`, unit tests, contract tests, typecheck, and build are not synonyms for `runtime-ready`.

## Required runtime evidence

Feature-bearing done notes, verification docs, and task briefs should record:

- the current-intent workstream/capability node or selected task that the evidence closes;
- the canonical path tested: worker -> execution harness -> actor adapter -> governed tool -> capability -> browser surface/action or non-UI trigger -> API/endpoint/client -> Akka component/service/substrate -> view/trace/audit outcome;
- the actor role, selected `AuthContext`, tenant/customer/organization scope, and at least one denial/forbidden or hidden/not-found case when authorization is in scope;
- success, validation/error, stale/conflict/idempotency, and provider-unconfigured fail-closed behavior where relevant;
- commands and/or manual smoke steps with pass/fail result;
- trace/correlation/audit evidence and browser-safe error/status copy;
- external provider status: configured and smoked, or missing and fail-closed with actionable setup instructions.

If this evidence cannot be produced for a user-visible runtime feature, the task should remain `blocked` or be explicitly marked below `runtime-ready`; do not compensate by adding more fixture tests.
