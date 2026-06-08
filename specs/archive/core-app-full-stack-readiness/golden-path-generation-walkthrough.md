# Golden-Path Generation Walkthrough

## Purpose

This walkthrough documents the expected harness path from the canonical full-core PRD to implementation-ready specs, backlogs, pending tasks, one-task-per-session execution, and final verification. It is a process contract for fresh harness sessions; it is not an alternate architecture source of truth.

Primary inputs:

- `docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md`
- `specs/core-app-full-stack-readiness/full-core-realization-map.md`
- `docs/module-sprint-planning.md`
- `docs/pending-task-queue.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `skills/akka-backlog-to-pending-tasks/SKILL.md`

## Scope gate before generation

Every generation run must classify the target scope before writing specs, backlogs, or pending tasks.

- `full core`: includes all seven core modules in this walkthrough and must not omit User Admin, Agent Admin, invitation onboarding, governed runtime agent records, workstream UI, audit/trace, governance/policy, or full acceptance/security tests.
- `Module 1-only / not full core`: allowed only when explicitly requested or selected; it includes minimal auth, `/api/me`, selected `AuthContext`, profile/context display, authenticated shell, and Access/Profile surfaces, and it must list the deferred full-core modules.
- Any other narrower scope: must be named, must list deferred full-core areas, and must not be described as full-core-ready.

If a user asks for full core and any full-core module cannot be planned safely, create or update `specs/pending-questions.md` or mark only the affected tasks as blocked. Do not silently narrow the plan.

## Seven core modules that must be represented

A full-core plan must preserve these modules in specs, backlogs, pending tasks, implementation, and tests:

1. **Minimal Auth and App Access MVP** — WorkOS/AuthKit seam, local account/profile/tenant/membership basics, selected `AuthContext`, `/api/me`, authenticated workstream shell, and Access/Profile surfaces.
2. **User Administration** — invitation onboarding, Resend/outbox boundary, user/member directory, memberships, roles/capabilities, disabled access, access review, and admin audit.
3. **Agent Definition Foundation** — `AgentDefinition` lifecycle, safe model references, tool-boundary placeholders, Agent Admin list/detail, readiness checks, tenant isolation, and audit.
4. **Prompt Governance** — `PromptDocument`, `PromptVersion`, review/diff/history/activation, deterministic prompt assembly, `PromptAssemblyTrace`, and safe prompt test console.
5. **Skill Governance** — `SkillDocument`, `SkillVersion`, `AgentSkillManifest`, authorized `readSkill(skillId)`, `SkillLoadTrace`, manifest assignment, version pinning, and denial tests.
6. **Audit and Work Trace** — unified audit/trace explorer, trace normalization, scoped search, correlated timeline, redaction, optional realtime stream/export, and audited trace access denial.
7. **Evaluation and Closed-Loop Improvement** — evaluator findings, improvement proposals, approval/rejection, activation, rollback, governance/policy surfaces, evidence preservation, and unauthorized authority-expansion denial.

## Golden-path from PRD to planning package

Use `akka-prd-to-specs-backlog` for the first materialization from the canonical PRD.

1. **Read inputs narrowly but completely.** Read the canonical PRD and the required doctrine/skill files named by `akka-prd-to-specs-backlog`. Apply secure AI-first SaaS interpretation first, then the agent workstream model, then capability-first backend modeling.
2. **Record generation conventions.** Ensure the planning package records the selected Java base package and UI style. If either is missing and the run would create Java or browser UI work, queue the relevant pending question instead of guessing.
3. **Write the master solution plan.** Create or update `specs/akka-solution-plan.md` with scope label, AI-first operating model, core SaaS foundation, capability inventory, capability-to-component mapping, skill routing, open questions, implementation order, and required tests.
4. **Write cross-cutting specs.** At minimum, write `specs/cross-cutting/00-common-domain-and-conventions.md`, `specs/cross-cutting/01-auth-tenancy-audit.md`, and a UI style/quality spec when browser UI is in scope.
5. **Use module-oriented sprint planning.** For this PRD, prefer `specs/modules/` plus `specs/sprints/` over a flat slice list. Preserve the seven core modules above as ordered full-stack increments or as explicit module/sprint scopes.
6. **Write sprint specs.** Each sprint spec must describe backend capabilities first, then Akka substrates, HTTP/API exposure, frontend workstream/surface scope, AI-first authority/governance/audit constraints, acceptance behavior, tests, done criteria, and explicit defers.
7. **Write matching build backlogs.** Each `specs/backlog/NN-...-build-backlog.md` must align with a sprint/module and include capability contracts, expected files/components, endpoint/API list, UI surface work, tests, implementation order, and a suggested harness task breakdown.
8. **Create task briefs only when needed.** If a backlog item still spans multiple component families or is too broad for one fresh harness run, create `specs/tasks/**` leaf briefs before it becomes runnable.
9. **Create or update `specs/pending-questions.md`.** Queue only design-impact questions that would otherwise force guesses: authority, approval gates, risk/evidence thresholds, trace redaction/retention, style selection, Java package, or external configuration specifics.
10. **Create or update `specs/pending-tasks.md`.** Materialize one focused task per bounded backlog item, preserving dependencies, required reads, skills, expected outputs, required checks, done criteria, capability ids, authority/scope, audit/trace obligations, UI surface, and notes.

## Golden-path from backlog to pending tasks

Use `akka-backlog-to-pending-tasks` when the specs/backlogs already exist but `specs/pending-tasks.md` is missing, stale, or incomplete.

1. Read existing solution, cross-cutting, module, sprint, backlog, task-brief, pending-question, and pending-task files.
2. Derive tasks from each backlog's `Suggested harness task breakdown`; do not invent a second decomposition when the backlog is already clear.
3. Preserve existing task IDs and statuses. Mark obsolete non-done tasks as `superseded`; do not delete completed tasks.
4. Split broad foundation work before making it runnable. In particular, do not collapse invitation lifecycle, user administration, governed runtime agent foundation, prompt/skill governance, tool boundaries, audit/trace, UI surfaces, and tests into one vague task.
5. If unresolved blocking questions affect only some work, keep unblocked tasks runnable and mark or omit only the blocked affected tasks.
6. Ensure first runnable tasks build or verify the secure SaaS foundation before app-specific feature work.
7. End with a queue whose first `pending` task with satisfied dependencies can be executed by `akka-do-next-pending-task` in a fresh context.

## Golden-path implementation loop

Implementation consumes `specs/pending-tasks.md`; it does not restart PRD decomposition unless a task explicitly asks for planning repair.

For every task:

1. Start a fresh harness context.
2. Ask the harness to execute exactly the next runnable pending task from `specs/pending-tasks.md`.
3. The harness selects the first `pending` task whose dependencies are satisfied.
4. Before changing task outputs, update that task status to `in-progress`.
5. Load only the task brief, required reads, and listed skills.
6. Implement exactly the expected outputs and required tests/checks.
7. Run the required checks or record why a check could not run.
8. Update the task status to `done` only when done criteria are satisfied; otherwise mark it `blocked` with a precise blocker.
9. Make one git commit for that task only, including the queue status update. If the queue update is in the same commit, record the commit message in task notes when the queue requires it.
10. Report the next runnable pending task and stop. Do not start a second queue item in the same session.

Fresh-context prompt:

```text
Use the Akka skills pack to do the next pending task from specs/pending-tasks.md.
Execute only that one task, load only its required reads and listed skills, update its status when finished, make one task-scoped commit, and report the next runnable pending task.
```

## Validation expectations by stage

### Planning package validation

- Every plan artifact states `full core`, `Module 1-only / not full core`, or another explicit narrower scope.
- Full-core artifacts include all seven core modules.
- Capability contracts precede Akka component choices.
- Functional agents and structured surfaces are represented before page-first UI decomposition.
- Backlogs include concrete test obligations for authorization, tenant isolation, idempotency, audit/trace, governance, frontend secret boundaries, and structured-surface rendering.
- Pending tasks are small enough for one fresh harness run.

### Per-task validation

- Required checks from the queue entry pass or are explicitly recorded as not runnable.
- Code/spec changes are limited to the selected task's expected outputs plus its queue update.
- No task guesses unresolved authority, policy, approval, evidence, trace, style, Java package, or external-credential decisions.
- One commit contains only that task's intended changes.

### Full-core completion validation

- The acceptance matrix covers all seven core modules.
- User Admin and Agent Admin are present and tested, not deferred.
- Prompt/skill/manifest/tool-boundary governance is tenant-scoped and durable.
- `readSkill(skillId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`, admin audit, and audit/trace search are exercised.
- Workstream shell and structured surfaces are backed by browser-safe backend capabilities.
- Security tests cover cross-tenant denial, disabled access, role/capability denial, frontend secret boundaries, and redaction.

## Failure handling

- If planning artifacts omit full-core areas without a narrower-scope label, stop and repair the planning/readiness gates before code generation.
- If a pending task is too broad, create or request a leaf task brief instead of implementing it.
- If a required decision is missing, create/update `specs/pending-questions.md` and mark only affected tasks as blocked.
- If implementation discovers a mismatch between code and specs, update the authoritative planning artifact through a bounded change-request/spec-update task before widening code scope.
- If checks fail for reasons outside the selected task, record the failure and blocker; do not opportunistically start adjacent work.
