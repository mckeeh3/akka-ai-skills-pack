---
name: akka-slice-spec-to-backlog
description: Turn one existing specs/slices/*.md or specs/sprints/*.md file into a matching specs/backlog/*-build-backlog.md file with package layout, class list, endpoint list, design notes, tests, implementation order, and harness-sized task breakdown.
---

# Akka Slice Spec to Backlog

Use this skill when the repository already has a slice spec or module-oriented sprint spec and the next task is to convert that delivery increment into a concrete build backlog for implementation.

This is a narrower follow-on planning skill than `akka-prd-to-specs-backlog`.

## Goal

Create or update a single backlog file that is tightly aligned with one existing slice or sprint spec and is detailed enough to drive focused coding sessions.
The backlog should end in bounded harness-sized task items, not just class lists and prose.
For generated secure AI-first SaaS, each backlog item must preserve the vertical workstream graph chain from the source increment: workstream → role-specific dashboard attention → human surface graph node/action or workstream event → internal workstream agent graph delegation/result when applicable → governed-tool id/class inside capability and surface/action maps → selected Akka substrate/exposure channel → request-based workstream Agent or durable AutonomousAgent task candidate → notification/projection → audit/work trace.
Also create or update matching entries in `specs/pending-tasks.md` so follow-on implementation can proceed one task per fresh context.

## Use this skill when

The task sounds like one of these:
- "Turn this slice spec into an implementation backlog"
- "Turn this sprint spec into an implementation backlog"
- "Write the build backlog for slice 03"
- "Write the build backlog for sprint 03"
- "Expand this slice into class-level tasks"
- "Expand this sprint into class-level tasks"
- "Generate a harness-friendly backlog from this spec"

Do **not** use this skill when the user is still at the PRD stage and needs the overall slice structure. Use `akka-prd-to-specs-backlog` for that.

## Relationship to other skills

This skill assumes these already exist or are mostly settled:
- `specs/akka-solution-plan.md`
- one or more `specs/cross-cutting/*.md` files
- a target `specs/slices/*.md` or `specs/sprints/*.md` file
- the related `specs/modules/*.md` file when using module-oriented sprint planning

This skill should be used after:
- `../akka-prd-to-specs-backlog/SKILL.md`
- or after manual slice creation

## Required reading

Read these first if present:
- `../README.md`
- `../akka-prd-to-specs-backlog/SKILL.md`
- `../../../specs/README.md`
- `../../../specs/backlog/README.md`
- `../../../specs/tasks/README.md`
- `../../../specs/pending-tasks.md` if it already exists
- `../docs/pending-task-queue.md`
- `../docs/ai-first-saas-application-architecture.md` when the slice or sprint includes delegated work, agents, approvals, exceptions, governance, audit, supervision UI, or outcomes
- `../docs/requirements-to-workstream-development-process.md` when the source increment includes generated SaaS workstreams, attention, dashboards, surface actions, capabilities, AutonomousAgent candidates, notifications/projections, or task queues
- `../../../app-description/15-operating-model/` or equivalent operating-model specs when present and relevant
- `../../../specs/templates/build-backlog-template.md`
- `../../../specs/templates/implementation-task-template.md`
- the target slice spec file under `../../../specs/slices/` or sprint spec file under `../../../specs/sprints/`
- the related module spec under `../../../specs/modules/` when present
- any cross-cutting spec files referenced or obviously relevant to the slice or sprint
- `../../../specs/cross-cutting/*ui-style-guide*.md`, `../../../app-description/55-ui/style-guide.md`, or equivalent style artifact when the slice or sprint includes browser UI work
- `../../../specs/akka-solution-plan.md`

If a matching backlog file already exists:
- read it first
- update it rather than duplicating it
- preserve numbering and naming consistency

## What this skill must produce

For one target slice file such as:
- `specs/slices/03-domain-specific-process.md`

produce the matching backlog file:
- `specs/backlog/03-domain-specific-process-build-backlog.md`

For one target sprint file such as:
- `specs/sprints/03-domain-specific-process-sprint.md`

produce the matching backlog file:
- `specs/backlog/03-domain-specific-process-build-backlog.md`

Also create or update:
- `specs/pending-tasks.md`

This skill is a one-increment expansion step. Do not reinterpret the whole PRD, create a fresh parallel app, or change unrelated slices/sprints while creating the backlog. The backlog and queue entries must inherit the selected Java base package, core-app-extension assumptions, style-guide status, workstream ids, role-specific dashboards, attention items, surface graph nodes/edges, internal-agent graph context, governed-tool ids, capability ids, AuthContext/scope, role/capability rules, approval gates, audit/trace obligations, and test expectations from the solution plan and source slice/sprint. If the source increment lacks those contracts, block or queue the narrow repair question instead of inventing implementation authority.

## Required backlog content

The backlog file must include:
1. Purpose
2. Delivery goal
3. Workstream/attention/dashboard contract: workstream id, functional agent, role-specific dashboards, attention categories/items, left-rail/My Account contribution, and notification/projection expectations when in scope
4. Human surface graph plus governed-tool/capability map: surface nodes, edges, states/actions, system-message surfaces, governed-tool ids/classes, capability ids/classes, AuthContext, approval/policy, audit/work trace, and exposure channel
5. Internal workstream agent graph and autonomous task candidates: virtual dashboard agent attention, internal worker delegations/results/escalations, durable internal/background work that should use AutonomousAgent, plus task lifecycle/result/notification surfaces when applicable
6. Recommended package layout additions
7. Class-by-class file list
8. Concrete endpoint list
9. Write-model design decisions
10. View/workflow/consumer/timer/integration design as relevant
11. Web UI style-guide dependency and selected AI-first style when the slice includes browser UI work
12. Test plan by file/class
13. Implementation order
14. Suggested harness task breakdown
15. Done criteria, including the working local app/API/UI state for the slice or sprint goal
16. Explicit defer list, including whether each deferral narrows the goal or blocks calling the feature implemented

The suggested harness task breakdown is the default leaf layer for implementation.
Each item should be small enough to become one focused implementation prompt without reopening the full PRD.

When the target slice or sprint is the SaaS foundation, split the breakdown into concrete foundation and five-core workstream starter tasks before app-specific domain features: invitation lifecycle, email delivery/outbox, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, membership/role management, admin audit/search, AI admin agents including AdminRiskAgent and AccessReviewAgent or a skilled UserAdminAgent, decision cards for risky admin actions, My Account/User Admin/Agent Admin/Audit or Trace/Governance or Policy workstream surfaces, admin UI surfaces, and security/admin tests. Do not produce one broad `auth/admin` item or a User-Admin-only starter backlog for this work.

Also split governed runtime agent foundation work into separate backlog and pending-task items by component/UI/test family: `AgentDefinition` lifecycle/profile and agent catalog/detail, `PromptDocument`/`PromptVersion` governance with prompt assembly and `PromptAssemblyTrace`, `SkillDocument`/`SkillVersion` governance, `ReferenceDocument`/`ReferenceVersion` governance, `AgentSkillManifest`/`AgentReferenceManifest`, authorized `readSkill(skillId)`/`readReferenceDoc(referenceId)` and `SkillLoadTrace`/`ReferenceLoadTrace`, `ToolPermissionBoundary`, `AgentWorkTrace`, behavior editing agents and proposed-diff approval, prompt/skill/reference/manifest/tool-boundary UI, trace UI, and security/admin/agent-governance tests. Do not produce one broad managed-agent or `agent governance` item that spans all of these.

For each bounded item in the suggested harness task breakdown, add or update a corresponding task in `specs/pending-tasks.md` using `../docs/pending-task-queue.md`. Each backlog and queue item must carry the relevant workstream id, role-specific dashboard, attention category/item, surface graph node/edge or surface action, source governed-tool ids, source capability ids, internal-agent delegation/result context, actor/caller, `AuthContext`, role/scope or permission checks, approval gates, selected Akka substrate/exposure channel, AutonomousAgent task lifecycle/notification/result semantics when applicable, audit/trace requirements, UI surface, concrete checks, and local/runtime validation path when the work implements app behavior rather than reducing the work to a vague implementation label.
If the slice or sprint goal names a feature such as sign-in, user auth, invitation onboarding, User Admin, Agent Admin, or an app-specific workflow, the backlog must include the backend, endpoint/API, frontend/workstream surface, authorization, audit/trace, tests, and local smoke/manual verification needed for that named feature to work at the stated scope. Deferrals that prevent that working state must narrow/rename the goal or block completion; they must not be counted as done.
If a bounded item implements browser UI and style is unresolved, do not make it runnable; add/update a `specs/pending-questions.md` style-selection question using `../docs/web-ui-style-guide.md` and mark only the affected UI task as blocked or defer it with an explicitly accepted default.
Preserve existing task IDs and statuses when updating an existing queue.

## Mapping rules

### Slice or sprint to backlog name mapping
- `specs/slices/01-foo.md` -> `specs/backlog/01-foo-build-backlog.md`
- `specs/slices/02-bar.md` -> `specs/backlog/02-bar-build-backlog.md`
- `specs/sprints/01-foo-sprint.md` -> `specs/backlog/01-foo-build-backlog.md`
- `specs/sprints/02-bar-sprint.md` -> `specs/backlog/02-bar-build-backlog.md`

### AI-first context preservation

When the source slice or sprint includes AI-first operating-model content, the backlog must carry that context into implementation tasks instead of reducing the work to generic CRUD/component tickets. Workstream attention, role-specific dashboard, human surface graph node/action, governed-tool, capability, internal workstream agent graph delegation/result, AutonomousAgent task, notification/projection, and trace context is required vertical context, not optional prose.

Preserve, when present:
- delegated work and retained human authority
- policies, clauses, approval gates, permissions, thresholds, and escalation rules
- decision-card evidence, risk, confidence, impact, alternatives, and actions
- audit/work/decision traces, tool/data-access records, and outcome links
- supervision, governance, digest, and audit UI surfaces
- evaluation, replay, simulation, or outcome-metric obligations

If any of these are necessary but unresolved, create or update `specs/pending-questions.md` and mark only affected backlog or pending-task items blocked instead of guessing.

### Scope preservation
The backlog must not silently widen the slice or sprint.

Allowed:
- adding implementation detail
- adding class names
- adding tests
- clarifying dependencies

Not allowed:
- pulling in adjacent product areas unless the slice or sprint already implies them
- turning one slice or sprint into a whole-program backlog

### Detail level
The backlog should be detailed enough for several small harness runs, but not so detailed that it becomes source code.
If one task item still spans multiple unrelated component families or too many files, call that out and recommend a further task-brief decomposition before coding.
Do not add an oversized item to `specs/pending-tasks.md` as if it were ready; either split it into smaller pending tasks or mark the queue item `blocked` with a note that a task brief is required. In particular, a task spanning invitation lifecycle plus admin AI plus UI must be split before it becomes runnable. A task spanning `AgentDefinition`, `PromptDocument`, `SkillDocument`, `AgentSkillManifest`, `readSkill`, `SkillLoadTrace`, `PromptAssemblyTrace`, behavior editing agents, tool boundaries, traces, UI, and tests is too broad and must be split or blocked for `akka-backlog-item-to-task-brief`.

## Sizing rules

A good backlog usually supports independent tasks such as:
- one domain package or config model
- one entity or workflow
- one consumer or timed action
- one view family
- one endpoint family
- one test family

## Anti-patterns

Avoid:
- restating the slice spec without implementation detail
- omitting tests, local/runtime validation, or done criteria
- inventing classes unrelated to the slice or sprint's purpose
- widening scope to future slices or sprints
- breaking sprint/backlog or slice/backlog numbering consistency

## Final review checklist

Before finishing, verify:
- the backlog filename matches the slice filename by number and stem, or the sprint filename by number and stem after dropping `-sprint`
- the backlog references the right prerequisite specs
- the class list fits the slice or sprint scope
- the endpoint list fits the slice or sprint scope
- UI tasks include the selected style guide in required reads, or are blocked by the style-selection question
- AI-first delegated-work, authority, policy, decision, trace, UI-surface, evaluation, and outcome constraints from the source slice are preserved in backlog sections and pending-task entries when applicable
- unresolved AI-first authority, approval, policy, risk, trace, UI, evaluation, or outcome decisions block only the affected tasks and are captured as pending questions
- the tests cover entity/workflow/view/endpoint/frontend behavior as applicable
- the local app-run, endpoint smoke, browser/workstream smoke, or manual-test path is present for feature-bearing sprint/slice goals
- the harness task breakdown is composed of bounded operations
- any oversized task item is explicitly marked for further decomposition before coding
- `specs/pending-tasks.md` has matching queue entries for runnable harness tasks
- existing queue task IDs and statuses are preserved
- the defer list is explicit

## Response style

When using this skill:
- name the target slice or sprint first
- summarize the backlog sections you will create or update
- then write the backlog file
- clearly report which backlog file was added or updated
- clearly report whether `specs/pending-tasks.md` was added or updated
- name the first runnable pending task for the slice when one exists
- do not jump into implementation code
