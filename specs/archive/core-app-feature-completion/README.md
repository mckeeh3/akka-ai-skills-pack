# Core App Feature Completion

## Purpose

Create a durable implementation queue for the starter/core app features that are explicitly not yet implemented or intentionally outside the current documented full-core SMB starter scope.

This mini-project concerns the `akka-ai-skills-pack` source repository, especially `templates/ai-first-saas-starter/`, starter app-description assets, validation tools, and release/readiness documentation. It is not an end-user Akka application project.

## Trigger

The user asked what core-app features are not yet implemented. The identified gaps were then accepted as follow-up implementation scope:

- richer event-sourced lifecycle history for invitations and governed agent artifacts;
- broader core projections/views;
- broader workstream/event coverage;
- notification delivery channels beyond in-app and existing email boundary;
- broader AutonomousAgent/task notification coverage;
- digest/export platform extensions;
- policy simulation;
- enterprise/admin extensions such as IAM/SCIM/SSO, SIEM/legal hold/e-discovery, compliance suites, marketplace prompts, and tenant-managed tool binding;
- non-blocking polish such as mobile/off-canvas QA, rendered asset scans, and bundle-size optimization.

## Scope

In scope:

- Implement bounded starter-template/runtime features under `templates/ai-first-saas-starter/`.
- Update starter app-description/source docs when implementation semantics change.
- Preserve the runtime completion doctrine: named runtime features are complete only when the rendered local Akka/API/UI path works at the stated scope.
- Add focused backend/frontend tests and fullstack scaffold validation for each implemented slice.
- Keep external provider-dependent production integrations fail-closed unless real provider configuration and validation are implemented.

Out of scope:

- Replacing the five user-facing request/response workstreams with AutonomousAgents.
- Treating deterministic, fixture, mock, canned, model-less, or frontend-only behavior as completed runtime behavior.
- Destructive starter regeneration or unrelated skills-pack cleanup.
- Implementing all enterprise suites in one task; enterprise work must be split into bounded, validated increments.

## Affected repository areas

- `templates/ai-first-saas-starter/backend/`
- `templates/ai-first-saas-starter/frontend/`
- `templates/ai-first-saas-starter/app-description/`
- `templates/ai-first-saas-starter/README.md`
- validation scripts under `tools/`
- related starter/full-core specs under `specs/`

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run its required checks or block with a precise reason, and make one focused commit before being marked `done`.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `docs/pending-task-queue.md`
4. this mini-project `README.md`
5. `conversation-capture.md`
6. selected sprint, backlog, pending-task entry, and task brief
7. the smallest relevant doctrine/skills/source files listed by that task

## Sprint sequence

1. Durable core history and projections.
2. Events, notifications, and delivery-channel platform.
3. Workers, digest/export, and governance simulation extensions.
4. Enterprise/admin extension foundations.
5. Polish and validation hardening.
6. Terminal verification and follow-up loop.

## Done state

This mini-project is complete when:

- every queued implementation/review task is done, deferred with explicit accepted scope impact, superseded by a later queue entry, or blocked only on recorded external/provider decisions;
- implemented features work through rendered local Akka runtime/API/UI paths at their stated scope;
- provider-dependent features either have real configured-provider validation or are explicitly narrower/fail-closed and not claimed as production delivery;
- starter README/app-description/readiness docs accurately distinguish implemented behavior from remaining deferrals;
- fullstack starter validation and focused checks pass for the completed scope;
- terminal verification records no material unqueued gaps in this mini-project scope.

## Open concerns

Some enterprise and external-channel features require product/provider choices. `pending-questions.md` records decisions that should block only the affected provider-specific tasks, not the provider-neutral platform work.
