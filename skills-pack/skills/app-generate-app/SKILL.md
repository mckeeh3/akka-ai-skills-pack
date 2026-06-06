---
name: app-generate-app
description: Realize the current app description as generated code, tests, and runnable outputs while preserving description primacy, making regeneration scope explicit, and reporting what was generated, executed, and left uncertain.
---

# App Generate App

Use this skill when the authoritative app-description/spec artifacts are ready to become runnable application changes. In this repository and downstream forks, realization means extending the root SaaS Foundation App workspace, not creating a parallel duplicate app from installed `.agents` assets.

## Required reading

- target `AGENTS.md`
- current `app-description/**`, especially readiness/generation policy files
- relevant `specs/**`, backlog, pending-task, and task brief files
- `../docs/generated-saas-canonical-doctrine.md`
- `../docs/minimum-ai-first-saas-app.md`
- `../docs/full-core-foundation-readiness.md`
- `../docs/requirements-to-workstream-development-process.md`
- `../docs/retired-content-boundaries.md`
- focused Akka, web UI, auth/security, agent, workflow/entity/view/endpoint/test skills for the selected slice

## Generation contract

Before editing, state:

- source description/spec files and exact slice being realized
- target runtime paths: Java packages, frontend folders, app-description/spec extensions, docs, tests
- what is in scope, explicitly deferred, blocked, or assumed
- validation evidence required before claiming completion

Use the fixed Java base package `ai.first`. Keep extension work additive and merge-friendly under `foundation`, `coreapp`, and `business.<domain>` partitions as directed by the repository guidance.

## Runtime completion doctrine

A generated feature is complete only when the real local runtime path works at the stated scope. Do not count deterministic/demo/mock/simulated/model-less normal runtime behavior as implemented for auth, durability, provider calls, protected capabilities, authorization denials, audit/work traces, or workstream agents.

Model-backed workstream behavior must invoke a concrete Akka `Agent` through the governed runtime path with active configuration, governed loader tools, tool permission boundaries, registered runtime tools, and durable traces. Missing provider or security configuration must fail closed with actionable browser-safe errors and trace references.

## Implementation routing

Route from the selected description slice to the smallest focused skill set:

- app/security foundation: `core-saas-foundation`, `akka-workos-user-auth`, `akka-basic-user-admin`, `akka-saas-invitation-onboarding`, `akka-resend-email-service`
- backend capability/component work: `capability-first-backend`, `akka-solution-decomposition`, then entity/workflow/view/consumer/timer/endpoint skills
- model-backed workstream behavior: `akka-agents` and focused governance/tool/trace/testing skills
- browser UI: `akka-web-ui-apps` and focused UI companions
- planning queue updates: pending task/question/change-request skills

## What to update

Depending on scope, update all applicable artifacts together:

- Java domain/application/api code and resources
- frontend source under `frontend/**` and rebuilt static resources when required
- app-description extensions or readiness state when implementation discoveries change the authoritative model
- specs/task brief/pending queue status
- docs/run notes only when useful to future maintainers
- tests proving backend, UI, security, traces, and negative paths in the selected scope

## Reporting

Final handoff must include:

- generated/changed files grouped by backend, frontend, description/spec/docs/tests
- runtime path exercised and commands run with exit codes
- what passed, failed, or could not be run
- description/spec assumptions or drift discovered
- incomplete/blocked items with recommended next task

Do not mark a pending task done until required checks pass or the queue explicitly records why the task is blocked/incomplete.
