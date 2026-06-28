---
name: akka-runtime-feature-verification
description: Verify that generated SaaS or SaaS Foundation App features marked complete actually work through the intended local Akka/API/UI runtime path, with auth, denials, audit/work traces, provider fail-closed behavior, and runtime-validation/browser/API smoke evidence.
---

# Akka Runtime Feature Verification

Use this skill when a feature, slice, sprint, workstream, or pending-task group is claimed complete or nearly complete and needs a hard runtime-readiness gate before more feature work proceeds.

Verify the compiled path, not just the visible screen or component test:

```text
worker
→ execution harness
→ actor adapter
→ governed tool
→ capability
→ API/Akka implementation
→ trace/view/result surface
```

When runtime validation is part of the readiness claim, apply `../docs/runtime-validation.md` and `../docs/manual-test-reconciliation.md` so human/browser-agent/API/scripted findings become classified reconciliation outputs before more feature work proceeds.

This is a validator/reviewer and drift-repair skill. It may update specs/queue evidence and append remediation tasks, but it should not implement unrelated product code.

## Required reads

Read these first when present:

- `../README.md`
- `../docs/app-development-lifecycle.md`
- `../references/generated-saas-runtime-completion.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/app-worker-tool-model.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/runtime-validation.md` when runtime-validation scenarios, setup prerequisites, execution modes, or accumulated runs are in scope
- `../docs/manual-test-reconciliation.md` when runtime-validation/manual/browser runtime smoke evidence or tester findings are in scope
- `../docs/pending-task-queue.md`
- `../docs/structured-surface-contracts.md` when surfaces are in scope
- `../akka-web-ui-testing/SKILL.md` when browser UI is in scope
- `../akka-http-endpoint-testing/SKILL.md` when HTTP/API routes are in scope
- target project `AGENTS.md`
- target project `app-description/**` files for the affected workstream/capability/surface
- target project `specs/**` files, pending-task entries, task briefs, and verification notes for the claimed scope
- target project source/tests only for the affected runtime path

## Runtime readiness levels

Use these exact labels in verification output:

- `described` — app-description/spec intent exists, but no implementation proof.
- `surface-ready` — structured surface contract/rendering exists, but backend/API/runtime behavior may be incomplete.
- `backend-ready` — backend/service/component behavior is tested, but the user-facing API/UI path is not proven.
- `frontend-rendered` — browser components render, but action round trips through the real API/runtime path are not proven.
- `api-smoked` — protected API or workstream endpoint path was exercised locally with role/context/auth expectations.
- `browser-smoked` — visible local browser/DOM flow was exercised by a human or browser-capable agent against the intended API/client path.
- `manual-ready` — the feature has enough local runtime, browser/runtime-validation, denial, trace, and provider/fail-closed evidence for the stated validation scope.
- `runtime-ready` — the intended local Akka/API/UI path works at the stated scope with required auth, tenant scope, side effects, traces, tests, and provider behavior or explicit fail-closed evidence.

Do not use `complete` without naming which readiness level is complete.

## Verification gate

For each claimed feature or task group, build a small evidence matrix:

| Claim | Intent source | Runtime path | Evidence | Level | Gap |
|---|---|---|---|---|---|
| `<feature/action>` | `<app-description/spec/task>` | `<worker -> harness -> actor adapter -> governed tool -> capability -> API/endpoint -> Akka component/service -> trace/view/result surface>` | `<tests/commands/runtime-validation notes>` | `<level>` | `<none or gap>` |

A feature-bearing claim may be `runtime-ready` only when evidence covers, as applicable:

- responsible worker type, execution harness, actor adapter, governed tool id, capability id, and selected Akka implementation path from the compile contract;
- canonical user role, `AuthContext`, tenant/customer/organization context, and denial case;
- visible browser surface/action, confirmed `human_chat_tool_plan`, AI `agent_tool_call`, workflow/timer/consumer/API/MCP/internal trigger, or explicit non-UI trigger;
- protected API/workstream endpoint/client path used by the browser or adapter, not fixture-only data;
- Akka component/service/substrate path used by the governed tool/capability;
- success, validation error, forbidden/hidden/not-found, stale/conflict/idempotency, and provider-unconfigured behavior where relevant;
- audit/work trace, correlation id, actor-adapter trace source, and browser-safe trace/status copy;
- local commands and/or runtime-validation/browser/API smoke notes with pass/fail result;
- no frontend secret exposure and no provider secrets in browser payloads;
- explicit external-provider status: configured and smoked, or missing and fail-closed with actionable errors.

## Blocking rules

Keep or move the relevant task to `blocked` instead of `done`, or keep the verification scope below `runtime-ready`, when:

- only unit/service/contract/typecheck evidence exists for a user-visible feature;
- the worker/harness/adapter/governed-tool/capability chain is missing or contradicted by the implementation;
- the browser uses fixture/frontend-only data for a required production action;
- API route tests are missing for the path used by the browser or declared actor adapter;
- provider-backed behavior is counted as implemented through a mock/default bypass;
- auth/tenant isolation/authorization denial is not exercised;
- trace/audit evidence is not recorded or not browser-safe;
- required runtime-validation/browser/API smoke cannot be run and the feature is not explicitly non-runtime/internal-only.

## Tooling

When a pending-task queue exists, run the runtime-evidence validator when available:

```bash
python3 skills-pack/tools/validate-runtime-completion-evidence.py specs/pending-tasks.md
# or from an installed skills library:
python3 .agents/skills/tools/validate-runtime-completion-evidence.py specs/pending-tasks.md
```

Use the workstream-contract validator too when repairing runnable tasks:

```bash
bash skills-pack/tools/validate-pending-task-workstream-contract.sh specs/pending-tasks.md
```

## Output shape

```md
# Runtime Feature Verification

## Scope
- ...

## Compile contract checked
- worker(s): ...
- harness/actor adapter(s): ...
- governed tool(s): ...
- capability/capabilities: ...
- API/Akka path(s): ...
- trace/result surface(s): ...

## Evidence matrix
| Claim | Intent source | Runtime path | Evidence | Level | Gap |
|---|---|---|---|---|---|

## Result
- readiness level: ...
- runtime-ready: yes/no

## Required repairs
- app-description gaps: ...
- implementation gaps: ...
- test gaps: ...
- provider/config blockers: ...
- queue changes: ...

## Checks run
- `...` — passed/failed/not run with reason

## Next step
- ...
```

## Anti-patterns

Avoid:

- equating `surface-ready`, `backend-ready`, or frontend contract tests with `runtime-ready`;
- verifying only a screen, route, agent tool, or component method without the worker/adapter/governed-tool/capability chain;
- accepting fixture-only, mock-only, or frontend-only action paths as user-facing implementation;
- adding broad new features while runtime verification for the current feature group is failing;
- marking provider-backed features done because the provider is missing but a mock passed;
- hiding runtime-validation failures in notes without adding remediation tasks.
