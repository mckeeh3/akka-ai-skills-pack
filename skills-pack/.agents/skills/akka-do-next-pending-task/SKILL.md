---
name: akka-do-next-pending-task
description: Select and execute the next runnable task from specs/pending-tasks.md, preferably in a fresh context, updating the queue status when complete or blocked.
---

# Akka Do Next Pending Task

Use this skill when the user asks to continue implementation from the project's pending task queue.

Typical user prompts:
- "do the next pending task"
- "continue with the next task"
- "run the next item in specs/pending-tasks.md"
- "execute TASK-003"
- "/do-next-pending-task"

This is a queue-execution skill, not a broad planning skill.
It should execute **one task only**.

## Goal

Reliably perform one bounded follow-on task from:

```text
specs/pending-tasks.md
```

The skill must:
- select the next runnable `pending` task unless the user named a specific task
- keep the task scope bounded
- prefer fresh-context execution
- load only the task's required reads and listed skills
- confirm the task is in the build/compile phase, respects the readiness target, and has the compile-contract inputs needed to proceed or block it before coding
- preserve current-intent graph provenance and any AI-first operating-model, governance, approval, audit, supervision UI, or outcome constraints named by the task without broadening scope
- preserve any workstream-expertise/reference-governance constraints named by the task: model binding, governed prompt/skill/reference docs, compact manifests, `readSkill`/`readReferenceDoc`, loader authorization, tool boundaries, load traces, expertise surfaces, default-content governance, and tests
- require or inherit the generated-SaaS vertical contract before coding: workstream/functional agent or internal/foundation scope, attention category, role-specific dashboard, human surface graph node/action edge, deterministic surface intent route/no-route context when applicable, governed-tool id and qualified exposure, capability id/API exposure, selected Akka substrate, internal workstream agent graph delegation/result mapping when applicable, autonomous task/result/notification mapping when applicable, auth, traces, and tests
- generate or update the requested outputs
- run the task's required checks and local/runtime validation path when the task implements app behavior
- record the achieved readiness level and runtime evidence for feature-bearing `done` tasks
- update the queue status before finishing
- commit the task changes only when the selected task is marked `done`
- report any blocking pending question or the next runnable pending task

## Required reading

Read these first if present:
- `../README.md`
- `../docs/app-development-lifecycle.md`
- `../docs/app-worker-tool-model.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/manual-test-reconciliation.md` when the selected task is manual-test remediation or runtime verification repair
- `../docs/ai-first-saas-application-architecture.md` when the selected task or its listed skills include AI-first SaaS concerns
- `../docs/workstream-expertise-model.md` when the selected task includes LLM-backed functional-agent expertise, reference governance, `readReferenceDoc`, model binding, manifests, loader authorization, tool boundaries, load traces, or expertise surfaces
- `../docs/pending-question-queue.md`
- `../docs/pending-task-queue.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/solution-plan-to-implementation-queue.md`
- `../docs/web-ui-style-guide.md` when selected task includes browser UI work
- the target project's implementation artifacts or legacy `specs/scaffold-report.md` if present, to detect existing-app extension mode and preserve fixed package/path decisions
- the target project's `specs/pending-questions.md` if it exists
- the target project's `specs/pending-tasks.md`

Then read only the selected task's `required reads` and the listed implementation skills. If the selected task lists `ai-first-saas` or an AI-first companion skill, load only those listed AI-first skills, not the whole family.

Do not reread the entire PRD unless the selected task explicitly lists it as a required read or the task is blocked without it. Prefer the AI-first interpretation already preserved in app-description, solution, sprint, slice, backlog, task brief, pending-question, or queue notes.

## Use this skill when

Use this skill when:
- `specs/pending-tasks.md` exists and the user asks to continue pending work
- a PRD/spec/backlog planning run has created queued implementation tasks
- the user names a specific task ID from the queue
- the next action should be one focused implementation run rather than new decomposition

Do **not** use this skill when:
- there is no pending-task queue yet and the user is still at the PRD/spec stage; use `akka-prd-to-specs-backlog` or `akka-solution-decomposition`
- the user asks to revise the planning structure rather than execute a task
- the user asks to implement a concrete component directly without relying on the queue; use the focused Stage 3 skills
- the task belongs to description-first app maintenance; use `app-descriptions` and companions

## Fresh-context rule

Each queue task is intended to be performed in a fresh harness context.

If this skill is invoked in a session that already contains substantial unrelated planning or coding context, and the harness cannot spawn an isolated fresh context, prefer a fresh handoff only when the contamination creates a concrete risk to scope or correctness. Otherwise, if the selected task is bounded, required reads are clear, and the user wants execution, proceed in the current session and execute exactly one queue item. When stopping is necessary, give the user this handoff prompt:

```text
Use the Akka skills pack to do the next pending task from specs/pending-tasks.md.
Execute only that one task, load only its required reads and listed skills, update its status when finished, and report the next runnable pending task.
```

If the user explicitly asks to proceed in the current session anyway, continue when the selected task is bounded and its required reads are clear.
Still execute only one queue item.

## Queue file contract

Use `../docs/intent-compiler-skill-contracts.md` and `../docs/intent-to-realization-flow.md` for the detailed queue-execution contract. Preserve generated-SaaS/SaaS Foundation App context when in scope, including invitation lifecycle, email delivery, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin/AdminRiskAgent/AccessReviewAgent, decision cards for risky admin, AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, readSkill, PromptAssemblyTrace, SkillLoadTrace, behavior editing, agent catalog, and agent detail coverage across the generated specs/backlog/task sequence.

## Blocking rules

Block instead of guessing when:
- required reads are missing
- the task has unsatisfied dependencies
- required architecture choices or blocking pending questions are unresolved
- AI-first authority boundaries, approval gates, policies, evidence/risk thresholds, trace obligations, UI style, or outcome metrics are required for implementation but absent
- the selected task lacks a lifecycle/readiness target, compile-contract provenance, required checks, or manual runtime scenario/non-runtime exemption needed to finish safely
- a generated full-stack SaaS task is component-only, CRUD-only, page-only, or dashboard-only and lacks a vertical contract naming or inheriting workstream, attention category, role-specific dashboard, human surface graph node/action edge, deterministic surface intent route/no-route context when applicable, governed-tool id/exposure, capability id, API/exposure channel, selected Akka substrate, internal workstream agent graph result handling when relevant, AuthContext, audit/work trace, and local validation path
- an LLM-backed functional-agent task lacks or fails to inherit the workstream expert bundle, approved model binding, governed prompt/skill/reference documents, compact manifests, authorized `readSkill`/`readReferenceDoc` loader behavior, ToolPermissionBoundary, load/work traces, expertise surfaces, default-content governance, or required tests
- an AutonomousAgent or autonomous task is in scope but start/query/result/lifecycle capabilities, progress/result surfaces, task notifications, failure/cancellation attention behavior, and lifecycle tests are missing
- the task conflicts with current code or specs
- a required external credential/service is unavailable for normal runtime; implement fail-closed configuration errors and test-only adapters where appropriate, but do not mark provider-backed user-facing behavior done through mocks, and do not use fail-closed internal persistence as a substitute for Akka component-backed state
- required local app-run, endpoint, browser, or manual-smoke validation cannot be performed for a feature-bearing task; record incomplete validation and keep the task blocked unless the task is explicitly non-runtime/docs-only or the affected runtime feature is outside the named scope
- the only passing evidence for a user-visible runtime feature is unit/service/contract/typecheck/build output without a real API/workstream/browser/manual smoke path
- a task would be marked `done` while its notes still say the required runtime smoke, provider, seed/bootstrap, or protected route validation is blocked, deferred, or not run
- completing the named feature would require widening into another queue item

## Queue update discipline

When editing `specs/pending-tasks.md`:
- preserve task IDs and order
- change only the selected task's status/notes unless discovering dependency state inconsistencies
- do not renumber tasks
- do not delete completed tasks
- append new tasks only if the current task explicitly discovers required follow-on work outside its scope

## Relationship to planning skills

This skill consumes queues created by planning skills such as:
- `akka-prd-to-specs-backlog`
- `akka-slice-spec-to-backlog`
- `akka-backlog-item-to-task-brief`
- `akka-solution-decomposition` when its output has been materialized into `specs/pending-tasks.md`

It does not replace those planning skills.
It is the manual next-task execution entry point.

## Final review checklist

Before finishing, verify:
- exactly one task was selected
- the queue status was updated
- only the selected task's scope was implemented
- required reads and skills were loaded narrowly
- lifecycle/readiness target and compile contract were honored, or the task was blocked before product/runtime assumptions were invented
- current-intent provenance was preserved or the task had a valid docs-only/internal/foundation/cross-cutting exemption
- any AI-first constraints in the task were preserved or explicitly blocked rather than guessed
- checks were run or explicitly reported as not run
- generated-SaaS implementation tasks carried a workstream-attention-dashboard/surface-graph-governed-tool-capability/substrate contract, or were explicitly internal-only/foundation/cross-cutting
- feature-bearing `done` tasks include a `runtime evidence:` note naming readiness level, real path tested, role/AuthContext/tenant setup, denial/provider/fail-closed coverage, trace/audit evidence, and commands or manual-smoke results
- `python3 skills-pack/tools/validate-runtime-completion-evidence.py specs/pending-tasks.md` or installed equivalent was run when available after marking runtime tasks done, or the reason was recorded
- LLM-backed functional-agent tasks carried workstream-expertise/reference-governance context, including model binding, manifests, `readReferenceDoc`, loader authorization, tool boundary, load traces, expertise surfaces, default-content governance, and tests when applicable
- autonomous task work carried AutonomousAgent lifecycle, notification, result/progress surface, failure/cancellation attention, and test requirements when applicable
- if the task was marked `done`, changes were committed or the reason not to commit was reported
- the next runnable pending task was identified, or the absence of one was reported
