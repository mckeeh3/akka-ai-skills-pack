---
name: akka-change-request-to-spec-update
description: Convert an iterative feature request, bug report, issue, or implementation discovery into focused updates to app-description/specs artifacts and queue follow-up tasks without redoing the whole PRD decomposition.
---

# Akka Change Request to Spec Update

Use this skill when an Akka project already has app-description and/or `specs/` planning artifacts and the user introduces an incremental change.

This is an **evolution skill**. It keeps the app meaning and implementation plan current before new code is written. For generated secure AI-first SaaS changes, reconcile against the existing workstream graph before adding work: workstream → role-specific dashboard attention → human surface graph node/action → internal workstream agent graph delegation/result → governed-tool inside a capability or surface/action map → selected Akka substrate/exposure channel → request-based workstream Agent or durable AutonomousAgent task candidate → notification/projection → audit/work trace.

## Goal

Turn a change request into a controlled planning delta that:
- preserves the current app-description/spec structure
- preserves AI-first operating-model meaning when delegated work, governance, decisions, audit, or outcomes are in scope
- preserves workstream identity, role-specific dashboard contracts, attention items, surface graph nodes/edges, governed-tool ids, capability ids, internal workstream agent graph delegations/results, AutonomousAgent task candidates, notifications/projections, and audit/work traces when the change affects generated SaaS planning
- preserves workstream expertise meaning when a functional agent's prompt intent, governed skills, references, expertise manifest, governed-tool help/denial guidance, loaders, tool boundary, traces, or tests change
- updates only the affected authoritative description/spec artifacts
- adds or revises verification expectations
- identifies auth/security and observability impact
- updates affected slice/backlog/task-brief artifacts
- appends, blocks, defers, or supersedes pending queue tasks as needed
- avoids broad PRD redecomposition unless the change is foundational

## Use this skill when

The task sounds like:
- "add this feature to the existing plan"
- "update the specs for this new requirement"
- "we found a bug; add a regression and fix task"
- "this pending task exposed a missing requirement"
- "adjust the backlog for this behavior change"
- "incorporate this issue into the existing task queue"

Use `akka-revised-prd-reconciliation` instead when the input is a full revised PRD or replacement requirements document.

Use `akka-do-next-pending-task` instead when the user wants to execute an existing queue item without changing the plan.

Keep this skill local-delta oriented. It may update affected app-description/spec/backlog/task-brief/queue artifacts, but it must not rederive the whole PRD plan, regenerate the queue from scratch, or create a fresh parallel app. Preserve existing queue IDs/statuses and existing Java base package, scaffold-report, app-description location, style-guide, workstream id, dashboard/surface graph context, governed-tool id, capability id, AuthContext/scope, approval, audit/trace, and test decisions unless the change request explicitly modifies them. If `specs/scaffold-report.md` exists, treat the request as scaffold extension/repair, not replacement.

## Required reading

Read these first if present:
- `../README.md`
- `../../docs/ai-first-saas-application-architecture.md` when the change involves delegated work, agents, governance, approvals, exceptions, audit, or outcomes
- `../../docs/requirements-to-workstream-development-process.md` when the change touches workstreams, attention, dashboards, surface actions, capabilities, AutonomousAgent tasks, notifications/projections, or planning queues
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../../docs/workstream-expertise-model.md` when the change adds or revises a functional agent, expert bundle, governed skills, references, manifests, loader authorization, tool boundaries, traces, seed content, or expertise tests
- `../../docs/pending-task-queue.md`
- `../../docs/solution-plan-to-implementation-queue.md`
- `../../docs/web-ui-style-guide.md` when the change affects web UI style guide
- `../app-descriptions/SKILL.md`
- `../app-description-input-normalization/SKILL.md`
- `../app-description-intake-router/SKILL.md`
- `../app-description-change-impact/SKILL.md`
- `../ai-first-saas/SKILL.md` for generated AI-first SaaS semantics
- `../akka-backlog-to-pending-tasks/SKILL.md`
- target project `app-description/` indexes if present
- target project `specs/README.md` if present
- target project `specs/akka-solution-plan.md` if present
- target project `specs/pending-tasks.md` if present

Then read only the affected slice, backlog, task brief, and description files needed to apply the change.

## Change classification

Classify the input as one or more of:
- additive feature
- behavior revision
- bug/regression fix
- security/auth change
- observability/operations change
- AI-first operating-model change: delegated work, retained human authority, agent/team responsibility, approval gates, exception handling, policy/permission rules, audit traces, evidence/risk thresholds, UI supervision surfaces, or outcome metrics
- workstream expertise change: functional-agent prompt intent, `SkillDocument`/`ReferenceDocument` content, `AgentSkillManifest`/`AgentReferenceManifest` entries, `readSkill`/`readReferenceDoc` loader access, `ToolPermissionBoundary`, authority profile, seed/upgrade policy, governance owner, `PromptAssemblyTrace`/`SkillLoadTrace`/`ReferenceLoadTrace`/`AgentWorkTrace`, or expertise tests
- integration contract change
- UI/API surface change, including web UI style-guide selection or token changes
- workstream graph change: workstream responsibility, role-specific dashboard summary, attention category/item, human surface graph node/edge, system-message surface, internal workstream agent graph delegation/result, governed-tool exposure, capability exposure, autonomous task candidate, notification/projection, or audit/work trace linkage
- implementation discovery
- de-scope/removal
- unclear change requiring clarification

Ask the smallest clarifying question only when necessary to avoid changing the wrong semantic layer or queue scope.

## Operating flow

### 1. Normalize and route the request

Summarize:
- change basis
- affected workstreams, role-specific dashboards, attention categories/items, human surface graph nodes/edges, structured surfaces, system-message surfaces, surface actions, and governed-tools
- affected capabilities
- affected behavior
- affected tests
- affected AI-first semantics: durable goals/plans, agent authority, policy, approval, exception, trace, UI, or outcome implications
- affected internal workstream agent graph semantics: virtual dashboard agent attention, internal worker delegation, worker results/proposals, escalations, and human attention items created
- affected workstream expertise semantics: bundle scope, prompt/skill/reference docs, compact manifests, governed-tool descriptions/denials, loader denials, tool boundaries, seed/import, governance owner, traces, UI/governance surfaces, and tests
- likely affected Akka components
- whether this is local, cross-slice, or foundational

If the change is broad enough to invalidate the current architecture or authority model, stop and recommend `akka-revised-prd-reconciliation` or `akka-prd-to-specs-backlog` instead of patching locally.

When the input changes delegated work or automation authority, apply `ai-first-saas` before selecting implementation artifacts. Do not treat the change as a simple CRUD field or endpoint update if it changes who/what may act, approve, decide, learn, or be audited. When the input changes durable background investigation, review, evaluation, monitoring, or remediation, evaluate whether it changes an AutonomousAgent task lifecycle and preserve task notifications/results as governed surfaces rather than loose background jobs.

When the input says to make an agent more capable, more expert, more knowledgeable, or better at a workstream, do not collapse the request into prompt text. Route it as a workstream expertise change and identify impacted expert bundle artifacts, governed documents, manifests, loaders, tool boundaries, capabilities, auth/security, observability, UI/governance surfaces, generation assets, and tests.

### 2. Update authoritative meaning first

If `app-description/` exists, update it before implementation specs:
1. `12-workstreams/` workstream expertise when functional-agent competence changes: bundle scope, prompt intent, governed skills, reference documents, compact skill/reference manifests, capability map, `ToolPermissionBoundary`, authority profile, loader denial semantics, traces, governance owner, seed/upgrade policy, surfaces, and expertise tests
2. capabilities when scope/outcomes, capability exposure, agent-callable tools, approval semantics, or capability-to-expertise mappings changed
3. behavior when app semantics changed
4. tests for acceptance, regression, negative, idempotency, security, operational, assigned/unassigned loader, tool-boundary, no-authority-expansion, trace, or surface verification
5. auth/security when identity, authorization, trust, authority boundaries, tenant/customer scope, data access, or sensitive data changed
6. observability when logs, metrics, traces, audit, health, alerts, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, or `AgentWorkTrace` changed
7. AI-first operating model when delegated work, retained authority, agent/team boundaries, policy gates, decision cards, work traces, or outcome loops changed
8. UI style guide or UI/governance surfaces when a browser frontend style system, named-theme contract, My Account theme preference behavior, density, brand treatment, component styling, supervision surface, decision card, governance center, manifest/reference/skill display, denied-load recovery, digest, or audit view changes
9. traceability, generation maps, and readiness as needed

Do not bury new governance, audit, policy, approval, or outcome semantics only in backlog text or generated code.

### 3. Update realization specs

Update the smallest relevant `specs/` artifacts:
- `specs/akka-solution-plan.md` only if architectural choices, AI-first operating model, requirements-to-workstream contract, workstream expertise foundation, authority boundaries, minimum-starter/five-core/full-core readiness, or global implementation order changed
- `specs/cross-cutting/*.md` for shared conventions/policies, including agent authority, expert bundle governance, prompt/skill/reference document governance, manifest/loader/boundary rules, approval/evidence/risk rules, audit/trace contracts, outcome metrics, and `*ui-style-guide*.md` for browser UI style-guide decisions
- `specs/slices/*.md` for business slice meaning, including workstream expertise responsibilities when a slice introduces or materially changes a functional agent
- `specs/backlog/*-build-backlog.md` for implementation breakdown, preserving separate bounded tasks for affected role dashboards, attention sources, surface graph nodes/edges, governed-tools, internal-agent delegations/results, expert bundle description, seed documents, skill/reference manifests, `readSkill`/`readReferenceDoc` loaders, `SkillLoadTrace`/`ReferenceLoadTrace`, loader/boundary behavior, UI/governance surfaces, and expertise tests instead of one vague agent-governance task; repair or block CRUD/page/component-only backlog items that lack workstream/surface/governed-tool/capability context
- `specs/tasks/**/*.md` when one task brief must change or a new leaf task is needed

Preserve numbering and existing file names unless the user asks for a larger reorganization.

### 4. Reconcile the queue

Update `specs/pending-tasks.md` if present.

Rules:
- preserve task IDs and statuses
- do not delete completed tasks
- append new tasks for new work
- mark obsolete pending/deferred/blocked tasks as `superseded` when a later spec change replaces them
- leave completed tasks as `done`; add new follow-up tasks if completed work now needs changes
- update required reads and skills for affected pending tasks, preserving workstream id, role-specific dashboard, attention category/item, surface graph node/edge or surface action, governed-tool id/class, capability id/class, internal-agent delegation/result context, AuthContext/scope, selected substrate/exposure channel, notification/projection, and audit/work trace context; add `ai-first-saas` and relevant companion skills when the task implements agentic operating-model behavior; add `akka-autonomous-agents` or focused governance/testing skills when the task changes durable AutonomousAgent task lifecycle, notifications, results, or governed-tool authority; add `docs/workstream-expertise-model.md` plus focused agent governance/testing skills when the task changes expert bundles, skills, references, manifests, loaders, boundaries, traces, seed content, or expertise UI
- block or decompose stale/vague pending tasks such as CRUD/page/component-only work, `make the agent expert`, or `agent governance` unless they have a self-contained scope for the relevant workstream/surface/capability contract and exactly which expert bundle, governed documents, manifests, `readSkill`/`readReferenceDoc` loaders, `SkillLoadTrace`/`ReferenceLoadTrace`, boundaries, surfaces, traces, and tests are in or out
- block tasks whose delegation, authority, approval, policy, evidence/risk, audit, UI supervision, workstream expertise, or outcome semantics are now ambiguous
- block web UI tasks whose source spec has no selected style guide and add or update the pending style-selection question
- block tasks whose source spec is now ambiguous
- avoid renumbering

If there is no queue yet but follow-on implementation is needed, create it with `akka-backlog-to-pending-tasks` rules.

### 5. Report the plan delta

End with:
- changed authoritative artifacts
- changed planning artifacts
- queue changes
- new/blocked/superseded tasks
- next runnable pending task

## Queue status guidance

Use these statuses:
- `pending`
- `in-progress`
- `blocked`
- `done`
- `deferred`
- `superseded`

Use `superseded` only when a task should not be executed because a later change replaced its source requirement, design, or implementation path.

Recommended note:

```md
- notes:
  - superseded: replaced by <TASK-ID or spec path> due to <change request summary>
```

## Anti-patterns

Avoid:
- implementing code before updating changed app meaning/specs
- regenerating all specs from scratch for a local change
- deleting completed or obsolete queue entries
- renumbering task IDs
- leaving regression tests unspecified for a bug fix
- treating security, authorization, tenant isolation, audit, observability, UI/governance, generation, or test impact as skippable after behavior or workstream expertise changes
- silently widening the current implementation task

## Final review checklist

Before finishing, verify:
- the change was classified
- authoritative description/spec files were updated before queue edits
- test impact was handled
- governance, audit, policy, approval, workstream expertise, generation, and outcome implications were preserved when applicable
- security, observability, UI/governance, and test impact were considered
- affected backlog/task files were updated or intentionally left unchanged
- queue IDs and statuses were preserved
- obsolete tasks were superseded rather than deleted
- the next runnable task is named

## Response style

Use this shape:

```md
# Change Request Spec Update

## Change classification
- ...

## Updated artifacts
- app-description: ...
- specs: ...
- queue: ...

## Impact and rationale
- ...

## Queue result
- added:
- updated:
- blocked:
- superseded:

## Next runnable task
- ...
```
