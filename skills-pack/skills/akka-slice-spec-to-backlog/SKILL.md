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
- target project path: specs/README.md
- target project path: specs/backlog/README.md
- target project path: specs/tasks/README.md
- target project path: specs/pending-tasks.md if it already exists
- `../docs/pending-task-queue.md`
- `../docs/ai-first-saas-application-architecture.md` when the slice or sprint includes delegated work, agents, approvals, exceptions, governance, audit, supervision UI, or outcomes
- `../docs/requirements-to-workstream-development-process.md` when the source increment includes generated SaaS workstreams, attention, dashboards, surface actions, capabilities, AutonomousAgent candidates, notifications/projections, or task queues
- target project path: app-description/15-operating-model/ or equivalent operating-model specs when present and relevant
- target project path: specs/templates/build-backlog-template.md if the target project provides it; otherwise use the backlog structure described in this skill
- target project path: specs/templates/implementation-task-template.md if the target project provides it; otherwise use the task-brief structure described in this skill
- the target slice spec file under target project path: specs/slices/ or sprint spec file under target project path: specs/sprints/
- the related module spec under target project path: specs/modules/ when present
- any cross-cutting spec files referenced or obviously relevant to the slice or sprint
- target project path: specs/cross-cutting/*ui-style-guide*.md, target project path: app-description/55-ui/style-guide.md, or equivalent style artifact when the slice or sprint includes browser UI work
- target project path: specs/akka-solution-plan.md

If a matching backlog file already exists:
- read it first
- update it rather than duplicating it
- preserve numbering and naming consistency

## What this skill must produce

Use `../docs/planning-skill-output-contracts.md` for the detailed output contract. Preserve generated-SaaS/full-core context when in scope, including invitation lifecycle, email delivery, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin/AdminRiskAgent/AccessReviewAgent, decision cards for risky admin, AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, readSkill, PromptAssemblyTrace, SkillLoadTrace, behavior editing, agent catalog, and agent detail coverage across the generated specs/backlog/task sequence.

## Mapping rules

### Slice or sprint to backlog name mapping
- `specs/slices/01-domain-specific.md` -> `specs/backlog/01-domain-specific-build-backlog.md`
- `specs/slices/02-domain-specific-process.md` -> `specs/backlog/02-domain-specific-process-build-backlog.md`
- `specs/sprints/01-domain-specific-sprint.md` -> `specs/backlog/01-domain-specific-build-backlog.md`
- `specs/sprints/02-domain-specific-process-sprint.md` -> `specs/backlog/02-domain-specific-process-build-backlog.md`

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
