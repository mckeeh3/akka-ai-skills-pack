---
name: app-generate-app
description: Realize the current app description as generated code, tests, and runnable outputs while preserving description primacy, making regeneration scope explicit, and reporting what was generated, executed, and left uncertain.
---

# App Generate App

Use this skill when the authoritative app-description/spec artifacts are ready to become runnable application changes. In this repository and downstream forks, realization means extending the root SaaS Foundation App workspace, not creating a parallel duplicate app from installed `.agents` assets.

## Lifecycle classification

- Phase: build-compile.
- Kind: realizer.
- Family: app-description.
- Living-graph contract: realize a bounded slice of the app-description current-intent graph and preserve worker, execution-harness, actor-adapter, governed-tool, capability, security, trace, test, and realization provenance in the generated repository changes.
- Compile contract: follow `../docs/app-description-to-code-compile-contract.md` before editing, during implementation routing, and when reporting validation or reconciliation; do not treat code, UI, endpoints, or agent tools as product authority outside the graph contract.

## Required reading

- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-description-skill-output-contracts.md`
- `../docs/app-description-source-alignment.md`
- target `AGENTS.md`
- current `app-description/**`, especially `app.md`, global definitions, domain capabilities/data-state, workstream access/behavior/surface/agent/tool/policy/trace/test bindings, and workstream realization files
- relevant `specs/**`, backlog, pending-task, and task brief files
- `../docs/generated-saas-canonical-doctrine.md`
- `../docs/minimum-ai-first-saas-app.md`
- `../docs/full-core-foundation-readiness.md`
- `../docs/requirements-to-workstream-development-process.md`
- `../docs/workstream-surface-intent-routing.md` when realizing composer-enabled workstreams or create/edit/task surfaces
- `../docs/retired-content-boundaries.md`
- focused Akka, web UI, auth/security, agent, workflow/entity/view/endpoint/test skills for the selected slice

## Generation contract

Before editing, state:

- source current-intent graph nodes/spec files and exact slice being realized
- target runtime paths: Java packages, frontend folders, app-description/spec extensions, docs, tests, and source-alignment entries
- governed tool ids, capability ids, and actor adapters/exposure channels being realized, including surface action/browser-tool, confirmed human chat tool-plan, AI agent-tool, API/workflow/timer/consumer/MCP/internal paths where in scope
- what is in scope, explicitly deferred, blocked, or assumed
- validation evidence required before claiming completion

Use the fixed Java base package `ai.first`. Keep extension work additive and merge-friendly under `foundation`, `coreapp`, and `business.<domain>` partitions as directed by the repository guidance.

## Runtime completion doctrine

A generated feature is complete only when the real local runtime path works at the stated scope. Do not count deterministic/demo/mock/simulated/model-less normal runtime behavior as implemented for auth, durability, provider calls, protected capabilities, authorization denials, audit/work traces, or workstream agents. Report the achieved readiness level (`described`, `surface-ready`, `backend-ready`, `frontend-rendered`, `api-smoked`, `browser-smoked`, `manual-ready`, or `runtime-ready`) and do not call user-visible behavior complete when only lower-level evidence exists.

Model-backed workstream behavior must invoke a concrete Akka `Agent` through the governed runtime path with active configuration, governed loader tools, tool permission boundaries, registered runtime tools, and durable traces. Missing provider or security configuration must fail closed with actionable browser-safe errors and trace references.

## Implementation routing

Route from the selected current-intent graph slice to the smallest focused skill set:

- app/security foundation: `core-saas-foundation`, `akka-workos-user-auth`, `akka-basic-user-admin`, `akka-saas-invitation-onboarding`, `akka-resend-email-service`
- backend capability/component work: `capability-first-backend`, `akka-solution-decomposition`, then entity/workflow/view/consumer/timer/endpoint skills
- model-backed workstream behavior: `akka-agents` and focused governance/tool/trace/testing skills
- composer-to-surface routing and prefill-only workstream behavior: `agent-workstream-apps`, `app-description-surface-modeling`, `akka-web-ui-apps`, and `capability-first-backend`
- browser UI: `akka-web-ui-apps` and focused UI companions
- planning queue updates: pending task/question/change-request skills

## What to update

Depending on scope, update all applicable artifacts together:

- Java domain/application/api code and resources
- deterministic surface intent routing/catalog code for composer-enabled workstreams when in scope, preserving no-direct-command behavior before user submit
- confirmed human chat tool-plan review/confirmation/execution/result paths when explicitly modeled, preserving shared governed tool ids, per-tool transaction/idempotency behavior, backend authorization, traces, and partial-failure surfaces
- frontend source under `frontend/**` and rebuilt static resources when required
- app-description extensions, `realization/source-alignment.md`, or readiness state when implementation discoveries change the authoritative model
- specs/task brief/pending queue status
- docs/run notes only when useful to future maintainers
- tests proving backend, UI, security, traces, and negative paths in the selected scope

## Reporting

Final handoff must include:

- generated/changed files grouped by backend, frontend, description/spec/docs/tests, including any `realization/source-alignment.md` updates
- runtime path exercised and commands run with exit codes
- runtime evidence for feature-bearing changes: readiness level, browser/surface/API/Akka path, role/AuthContext/tenant setup, governed tool ids and actor adapters exercised, deterministic route/prefill/no-mutation evidence when applicable, human chat confirmation/partial-failure evidence when in scope, agent-tool boundary evidence when in scope, denial/provider fail-closed coverage, trace/audit evidence, and manual/browser/API smoke result
- what passed, failed, or could not be run
- description/spec assumptions or drift discovered
- incomplete/blocked items with recommended next task

Do not mark a pending task done until required checks pass or the queue explicitly records why the task is blocked/incomplete.
