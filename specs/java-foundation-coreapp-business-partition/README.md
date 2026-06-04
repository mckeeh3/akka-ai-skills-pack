# Java Foundation/Coreapp/Business Package Partition

## Purpose

Refactor the root core app Java packages so users can clearly distinguish:

1. `foundation` — common/base/platform code used by all app areas;
2. `coreapp` — the built-in AI-first SaaS core app workstreams and operational app behavior;
3. `business` — user-owned business-specific domains such as CRM, ERP, procurement, billing, or other domain-specific SaaS extensions.

The refactor must preserve the standard Akka Java project layering:

```text
ai.first.api.*
ai.first.application.*
ai.first.domain.*
```

The partition happens inside those layers rather than replacing them.

## Background and trigger

After the core-app-first repository refactor, the root app is now the canonical runnable Akka Java + frontend core app and the skills pack is isolated under `skills-pack/`. The next structural issue is Java package clarity.

Current packages such as `ai.first.application.security`, `ai.first.application.agentfoundation`, `ai.first.domain.security`, and `ai.first.api.workstream` do not clearly separate common foundation code from the first built-in app domain: the five core app workstreams. They also leave future business-specific code without an obvious outside-in location.

The agreed naming set is:

```text
foundation
coreapp
business
```

This is intended to be understandable to users who fork the project to build their own business-specific SaaS app. Users should see the provided base and core app as stable upstream code, while their CRM/ERP/domain-specific work goes under `business.<area>` packages.

## Scope

In scope:

- Inventory current root Java packages/classes and classify them as `foundation`, `coreapp`, or future `business` seam.
- Define target package maps inside `api`, `application`, and `domain`.
- Move foundation/common/base-layer Java code under:
  - `ai.first.api.foundation.*`
  - `ai.first.application.foundation.*`
  - `ai.first.domain.foundation.*`
- Move the five built-in core app workstream and operational app code under:
  - `ai.first.api.coreapp.*`
  - `ai.first.application.coreapp.*`
  - `ai.first.domain.coreapp.*`
- Add empty or documented business extension zones only where useful:
  - `ai.first.api.business.<business-area>.*`
  - `ai.first.application.business.<business-area>.*`
  - `ai.first.domain.business.<business-area>.*`
- Update imports, tests, resources, docs, app-description implementation maps, and skills-pack guidance references affected by the Java package move.
- Add package-boundary documentation and lightweight checks/search proof.

## Non-goals

- Do not add real CRM, ERP, procurement, or other business-specific product features in this refactor.
- Do not abandon the standard Akka `api` / `application` / `domain` layering.
- Do not create a separate Maven module unless a later explicit decision requires it.
- Do not move frontend business-extension structure except for docs or tiny placeholder guidance required by Java package boundary docs.
- Do not broaden runtime scope or claim new features; preserve existing behavior.

## Affected repository areas

Likely affected paths include:

- `src/main/java/ai/first/**`
- `src/test/java/ai/first/**`
- `pom.xml` only if package-boundary checks are added there
- `src/main/resources/**` if class names/packages appear in config or seed metadata
- `app-description/**` and `specs/**` implementation maps or package references
- `docs/**`, `README.md`, and `AGENTS.md`
- `skills-pack/skills/README.md`, skills-pack docs, or examples only where package guidance references the core app layout

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run its required checks or block with a precise reason, and make one focused commit before being marked `done`.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills-pack/skills/README.md`
3. `skills-pack/docs/pending-task-queue.md`
4. this mini-project `README.md`
5. `conversation-capture.md`
6. selected sprint, backlog, pending-task entry, and task brief
7. the smallest relevant source files listed by the task

## Sprint sequence

1. Package design and inventory.
2. Foundation package migration.
3. Core app package migration.
4. Business extension seams and boundary checks.
5. Documentation, skills-pack reference updates, and terminal verification.

## Done state

This mini-project is complete when:

- root Java source still follows standard Akka `api` / `application` / `domain` layering;
- common/base/platform code is clearly under `*.foundation.*` packages;
- built-in five-core-app workstream and operational app code is clearly under `*.coreapp.*` packages;
- user-owned future business-specific code has an explicit `*.business.<area>.*` package convention;
- imports/tests/resources/docs are updated and no stale root `ai.first.application.security`, `ai.first.application.agentfoundation`, `ai.first.domain.security`, `ai.first.domain.agentfoundation`, `ai.first.api.security`, `ai.first.api.admin`, or `ai.first.api.workstream` package assumptions remain outside historical/provenance text;
- package-boundary docs or checks state that `foundation` must not depend on `coreapp` or `business`, and `business` should use foundation/core public contracts rather than modifying core internals;
- `mvn test` and relevant frontend/docs/skills-pack checks pass for the refactored scope;
- terminal verification records no material unqueued gaps.

## Open concerns and recommendations

- The exact class-by-class split between `foundation` and `coreapp` should be inventoried before moving files. Some services currently named under `security` may be foundation primitives, while others may serve specific core app workstreams.
- Keep package moves as mechanical as possible and avoid behavior changes in migration tasks.
- Consider adding lightweight architecture tests after the move to prevent future `foundation -> coreapp/business` dependencies.
