# Repository Guidance: Secure AI-first SMB SaaS Core App

## Repository role

This repository has a two-fold purpose:

1. Maintain the source harness skills, referenced docs, templates, tools, and code examples for building secure AI-first SMB SaaS applications on Akka.
2. Provide the canonical runnable **secure AI-first SMB SaaS core app** that users clone or fork, run locally, and extend with business-specific domains, workstreams, surfaces, agents, and Akka components.

Treat root source, frontend, app-description, specs, docs, and tools as app-facing assets unless a task explicitly targets the isolated skills pack.

The installable Akka AI skills pack source lives under [`skills-pack/`](skills-pack/). For pack maintenance, read `skills-pack/AGENTS.md` and work inside `skills-pack/**` unless the task explicitly requires a root compatibility wrapper or root documentation link.

## Default interpretation

- Root app work: improve the runnable secure AI-first SMB SaaS core application, its Akka Java SDK backend, React/Vite frontend, app-description, specs, validation, docs, and domain-specific extension seams.
- Skills-pack work: improve installable `.agents` assets under `skills-pack/`, including skills, docs, code examples, templates, package manifests, installers, and release tooling used by harness agents to maintain the core app and generate downstream business-specific SaaS extensions.
- Do not mix focused skills-pack reference examples into root `src/**`.
- Do not add core app runtime code under `skills-pack/**`.

## Runtime completion doctrine

A generated-app or core-app feature is complete only when the real local runtime path works at the stated scope. Validate through the intended Akka/API/UI path rather than through duplicated templates, deterministic demos, or fixture-only checks.

Do not count deterministic/demo/mock/simulated/model-less normal runtime behavior as implemented for workstream agents, auth, durability, provider calls, protected capabilities, authorization denials, audit traces, or work traces. Fixtures, mocks, and test doubles belong in tests or explicitly named fixture modes only.

Model-backed workstream agents must invoke a concrete Akka `Agent` component through the governed runtime path with active configuration, governed loader tools, tool permission boundaries, registered runtime tools, and durable traces. Missing provider or security configuration should fail closed with actionable errors.

## Root app layout

Canonical app paths:

```text
pom.xml
src/main/java/ai/first/**
src/test/java/ai/first/**
src/main/resources/**
frontend/**
app-description/**
specs/**
docs/**
tools/**
```

The supported default Java package is `ai.first`. Keep downstream domain work additive and merge-friendly by using extension zones such as:

```text
src/main/java/ai/first/domain/business/<domain>/
src/main/java/ai/first/application/business/<domain>/
src/main/java/ai/first/api/business/<domain>/
src/test/java/ai/first/business/<domain>/
frontend/src/extensions/<domain>/
app-description/extensions/<domain>/
specs/extensions/<domain>/
docs/extensions/<domain>/
```

Within each Java layer, reusable platform/security/identity/managed-agent runtime code belongs under `foundation`, built-in five-core-workstream code belongs under `coreapp`, and user-owned product code belongs under `business.<domain>`.

Users of this repository should extend the root app rather than generate or maintain a separate parallel application. Business-specific features should land in the root app workspace as additive domains, workstreams, surfaces, agents, Akka components, app-description extensions, specs, frontend extensions, docs, and tests. When domain behavior needs core integration, prefer a small stable hook or registry in core code plus Java domain implementation under the `business.<domain>` package path.

## Required checks

Choose the smallest checks that prove the selected task. Common root app checks are:

```bash
mvn test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

For skills-pack work, use the checks required by `skills-pack/AGENTS.md`, the selected task brief, or the touched pack tooling.

## Working rules

- Read the selected pending-task entry and task brief before editing.
- Execute only one queued task per fresh harness session when using `specs/**/pending-tasks.md`.
- Mark the selected task `in-progress` before implementation edits and `done` only after checks pass.
- Commit the task changes and queue update together.
- Preserve tenant/customer scoping, backend authorization, audit/work traces, provider fail-closed behavior, and frontend secret boundaries in root app changes.
- Use `domain-specific` or the user's actual domain name for app-specific follow-up work; do not use historical placeholder domain names as generic labels.

## Skills-pack pointer

Use these files only when the task targets skills-pack maintenance:

```text
skills-pack/AGENTS.md
skills-pack/README.md
skills-pack/skills/README.md
skills-pack/docs/**
skills-pack/examples/**
skills-pack/tools/**
```
