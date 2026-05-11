# Sprint 7 Build Backlog: AI-First Alignment Hardening

## Purpose

Turn the completed AI-first migration into a coherent, installable, and internally consistent skills pack.

## Delivery goal

Installed users and source-repo maintainers should get the same AI-first SaaS default architecture guidance, with no missing packaged docs, stale references, broken paths, or follow-on planning skills that lose AI-first operating-model constraints.

## Suggested harness task breakdown

### 1. Align installed pack packaging and installed guidance

- task ID: `TASK-07-001`
- output: updated `pack/AGENTS.md`, `pack/README.md`, `pack/manifest.yaml`, `install.sh`, and release/dist guidance as needed.
- scope: package AI-first docs/examples/skills; update installed-pack guidance to describe AI-first as the default target architecture; ensure manifest includes `ai-first-saas*` skills and accurate metadata.

### 2. Audit and fix skill/doc relative paths

- task ID: `TASK-07-002`
- output: corrected relative references in AI-first skills and any other touched skills; documented or scripted link/path audit if useful.
- scope: fix incorrect `../../../docs`, `../../../AGENTS.md`, and similar source-path references; verify installed rewrite behavior remains valid.

### 3. Align core flow docs with AI-first default

- task ID: `TASK-07-003`
- output: updated core docs such as `intent-driven-usage-flow.md`, `prd-to-akka-flow.md`, `module-sprint-planning.md`, `solution-plan-to-implementation-queue.md`, `pending-question-queue.md`, and `pending-task-queue.md`.
- scope: add concise AI-first interpretation and preservation rules without bloating docs.

### 4. Add AI-first checks to app-description lifecycle skills

- task ID: `TASK-07-004`
- output: updated `app-description-change-impact`, `app-description-readiness-assessment`, `app-description-readiness-summary`, and `app-generate-app` skills.
- scope: ensure readiness/change-impact/generation check `15-operating-model/`, authority, policies, decisions, traces, outcomes, and AI-first UI surfaces when in scope.

### 5. Preserve AI-first context in leaf planning and queue skills

- task ID: `TASK-07-005`
- output: updated slice/backlog/task/question follow-on skills.
- scope: ensure `akka-slice-spec-to-backlog`, `akka-backlog-item-to-task-brief`, `akka-pending-task-queue-maintenance`, `akka-pending-question-queue-maintenance`, and `akka-do-next-pending-question` preserve delegated authority, policy, audit, trace, UI-surface, and outcome constraints.

### 6. Reconcile DCA example and gap docs

- task ID: `TASK-07-006`
- output: updated DCA example README/layer docs and gap docs.
- scope: make gap lists reflect completed Sprint 6 work, reconcile expected `15-operating-model/` versus `50-observability/` audit/outcome placement, and remove stale “planned fill order” language.

### 7. Plan the first executable AI-first reference implementation slice

- task ID: `TASK-07-007`
- output: a future sprint/backlog or task plan for implementing one executable AI-first reference slice.
- scope: choose the smallest useful slice, likely DCA supplies decision-card or goal/plan/approval flow; identify required domain, ESE/workflow/view/agent/endpoint/UI/test outputs; do not implement code in this task.

## Implementation order

Run tasks in listed order. Packaging/path correctness should be fixed before deeper docs and follow-on planning tasks. The DCA reconciliation task should happen before planning the executable slice so the future implementation plan uses current example artifacts.

## Done criteria

- All seven Sprint 7 tasks are represented in `pending-tasks.md`.
- Each task is bounded enough for one fresh Pi session.
- The sprint closes alignment gaps identified during post-migration review or explicitly records remaining work.
