---
name: akka-prd-to-specs-backlog
description: Turn a PRD or other high-level requirements artifact into a repo-ready planning package; master Akka solution plan, cross-cutting specs, module/sprint specs for large inputs or slice specs for smaller inputs, numbered build backlogs, and execution-order docs under specs/.
---

# Akka PRD to Specs Backlog

Context-budget rule: load canonical doctrine by reference. Do not paste the full foundation inventory into every generated spec, backlog, or queue item; summarize selected obligations and link the canonical checklist.

Use this skill when the user wants a PRD, high-level requirements document, or broad feature set materialized into a harness-friendly `specs/` package. Do not use it for direct implementation of an already-settled queue task.

## Goal

Create or update a repo-ready planning package that lets later harness sessions implement one bounded task at a time. The package must:

- interpret product intent through the secure AI-first SaaS operating model when generated-app scope is in play;
- derive workstreams, attention/dashboard contracts, surface graph, governed tools, and backend capabilities before Akka substrates;
- produce a master solution plan with fixed Java base package `ai.first`;
- split broad work into module/sprint specs or smaller vertical slice specs;
- create matching build backlogs and, when needed, leaf task briefs;
- create/update pending-question and pending-task queues without losing IDs/status/history;
- preserve existing app-description/spec/source decisions when extending an existing project;
- make implementation order, dependencies, authority, tests, and runtime validation explicit.

## Use this skill when

Use for requests like:

- “Turn this PRD into specs/backlogs/tasks.”
- “Break this product doc into harness-friendly implementation work.”
- “Create a master Akka plan plus slices/sprints/backlogs.”

Do not skip lifecycle stages by turning a PRD directly into code, turning a local change into a full replan, or regenerating pending tasks without preserving existing status, source capability ids, AuthContext/scope, approval, audit/trace, tests, and existing-app/package decisions.

## Existing-app planning

If the target project already contains app-description/specs/source/frontend artifacts, extend the existing baseline instead of planning a separate fresh application:

- read existing app-description/specs, pending queues, and legacy `specs/scaffold-report.md` when present;
- preserve fixed package `ai.first`, Maven group id, foundation scope, workstream UI baseline, and established spec naming;
- reconcile new PRD/domain input into existing app-description/specs before creating tasks;
- queue questions for conflicts with foundation semantics rather than overwriting silently;
- plan destructive replacement only when explicitly requested.

## Relationship to other planning skills

- `akka-solution-decomposition` owns the compact component plan.
- `akka-prd-to-specs-backlog` owns initial PRD/requirements → solution plan → specs → backlog → pending queues.
- `akka-revised-prd-reconciliation` owns replacement/substantially revised PRDs after planning exists.
- `akka-change-request-to-spec-update` owns local feature/bug/discovery deltas.
- `akka-slice-spec-to-backlog` owns one slice/sprint → backlog and queue entries.
- `akka-backlog-to-pending-tasks` owns queue materialization/repair from existing backlogs.
- `akka-backlog-item-to-task-brief` owns one oversized backlog item → one focused task brief.
- `akka-do-next-pending-question` and `akka-do-next-pending-task` execute one queued item.

## Required reading

Read first when present/relevant:

- `../README.md`
- `../akka-solution-decomposition/SKILL.md`
- `../core-saas-foundation/SKILL.md`
- `../docs/full-core-foundation-readiness.md`
- `../docs/minimum-ai-first-saas-app.md`
- `../ai-first-saas/SKILL.md`
- `../agent-workstream-apps/SKILL.md`
- `../capability-first-backend/SKILL.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/agent-workstream-application-architecture.md`
- `../docs/workstream-expertise-model.md`
- `../docs/structured-surface-contracts.md`
- `../docs/agent-workstream-design-review-checklist.md`
- `../docs/pending-question-queue.md`
- `../docs/pending-task-queue.md`
- `../docs/module-sprint-planning.md`
- `../docs/web-ui-style-guide.md`
- `../references/akka-entity-comparison.md`
- `../references/generated-saas-runtime-completion.md`
- target project path: specs/README.md, `specs/akka-solution-plan.md`, `specs/pending-questions.md`, `specs/pending-tasks.md`, `specs/backlog/README.md`, `specs/tasks/README.md`, and existing slice/sprint/module/backlog/task files as applicable.

If the user provided a PRD/requirements file, read it completely before writing specs.

## Extraction order for PRD input

Use `../docs/planning-skill-output-contracts.md` for the detailed output contract. Preserve generated-SaaS/SaaS Foundation App context when in scope, including invitation lifecycle, email delivery, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin/AdminRiskAgent/AccessReviewAgent, decision cards for risky admin, AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, readSkill, PromptAssemblyTrace, SkillLoadTrace, behavior editing, agent catalog, and agent detail coverage across the generated specs/backlog/task sequence.

## Sizing rules

- One sprint/slice should be understandable in one focused planning/review session.
- One backlog item should map to one bounded implementation run or be split.
- One task brief should produce one coherent code/doc/test change with clear checks.
- Prefer vertical workstream/surface/capability increments over horizontal “all entities,” “all endpoints,” or “all UI.”

## Naming rules

- Use numbered, stable, kebab-case names.
- Preserve existing numbers when updating.
- Use `domain-specific` or the user's actual domain name for product-specific follow-up work; do not use historical placeholder domains.
- Never create package-selection questions; Java base package is `ai.first`.

## Queue safety rules

When updating existing queues:

- preserve completed/done entries;
- do not reset `in-progress` without evidence;
- mark stale entries `superseded` with replacement references;
- keep blocked work tied to pending questions;
- do not duplicate tasks with new ids when updating the same work;
- update indexes and backlinks together.

## Runtime completion and validation

For generated-app implementation tasks, link `../references/generated-saas-runtime-completion.md`. Plans/backlogs must require validation through the intended local Akka/API/UI path for the selected scope. Fixture-only, mock-only, deterministic/model-less normal runtime behavior cannot satisfy named runtime features.

## Anti-patterns

- Writing specs before reading the PRD and existing queue state.
- Component-first or page-first plans that skip workstreams/surfaces/capabilities.
- Collapsing SaaS Foundation App into a vague “foundation” task.
- Creating giant backlog items that cannot be executed in one harness session.
- Regenerating queues and losing IDs/status/history.
- Treating `.agents/skills` assets as writable application source.
- Copying curated examples wholesale as the app baseline.

## Final review checklist

Before finishing, verify:

- requested scope is explicit and honestly named;
- fixed package `ai.first` is recorded;
- existing app-description/specs/source constraints were preserved;
- foundation obligations are included or explicitly out of scope;
- workstreams, surfaces, capabilities, governed tools, and Akka components are linked;
- specs/backlogs/tasks are small enough for focused runs;
- pending questions block only affected work;
- pending tasks have required reads, focused skills, acceptance checks, and validation path;
- no fixture/demo/model-less runtime path is counted as implementation completion.

## Response style

Be concrete and file-oriented. Summarize files created/updated, queue changes, blocked questions, and next runnable task. If input is too broad, create a safe planning skeleton plus questions rather than inventing authority, policy, trace, or UI behavior.
