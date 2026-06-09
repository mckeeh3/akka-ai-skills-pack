---
name: akka-change-request-to-spec-update
description: Convert an iterative feature request, bug report, issue, or implementation discovery into focused updates to app-description/specs artifacts and queue follow-up tasks without redoing the whole PRD decomposition.
---

# Akka Change Request to Spec Update

Use this skill when an Akka project already has app-description and/or `specs/` planning artifacts and the user introduces an incremental change.

This is an **evolution skill** for Capturing Incremental Intent. It compiles a local change request into the current intent graph and keeps the app meaning and implementation plan current before new code is written. For generated secure AI-first SaaS changes, reconcile against the existing workstream graph before adding work: workstream → role-specific dashboard attention → human surface graph node/action → internal workstream agent graph delegation/result → governed-tool inside a capability or surface/action map → selected Akka substrate/exposure channel → request-based workstream Agent or durable AutonomousAgent task candidate → notification/projection → audit/work trace.

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

Keep this skill local-delta oriented. It may update affected app-description/spec/backlog/task-brief/queue artifacts, but it must not rederive the whole PRD plan, regenerate the queue from scratch, or create a fresh parallel app. Preserve existing queue IDs/statuses and existing Java base package, app-description location, style-guide, workstream id, dashboard/surface graph context, governed-tool id, capability id, AuthContext/scope, approval, audit/trace, and test decisions unless the change request explicitly modifies them. If implementation artifacts or a legacy `specs/scaffold-report.md` exist, treat the request as SaaS Foundation App extension/repair, not replacement.

## Required reading

Read these first if present:
- `../README.md`
- `../docs/ai-first-saas-application-architecture.md` when the change involves delegated work, agents, governance, approvals, exceptions, audit, or outcomes
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/requirements-to-workstream-development-process.md` when the change touches workstreams, attention, dashboards, surface actions, capabilities, AutonomousAgent tasks, notifications/projections, or planning queues
- `../docs/workstream-expertise-model.md` when the change adds or revises a functional agent, expert bundle, governed skills, references, manifests, loader authorization, tool boundaries, traces, default content, or expertise tests
- `../docs/pending-task-queue.md`
- `../docs/solution-plan-to-implementation-queue.md`
- `../docs/web-ui-style-guide.md` when the change affects web UI style guide
- `../app-descriptions/SKILL.md`
- `../app-description-input-normalization/SKILL.md`
- `../app-description-intake-router/SKILL.md`
- `../app-description-change-impact/SKILL.md`
- `../ai-first-saas/SKILL.md` for generated AI-first SaaS semantics
- `../akka-backlog-to-pending-tasks/SKILL.md`
- target project path: app-description/ indexes if present
- target project path: specs/README.md if present
- target project path: specs/akka-solution-plan.md if present
- target project path: specs/pending-tasks.md if present

Then read only the affected slice, backlog, task brief, and description files needed to apply the change.

## Change classification

Classify the input as one or more of:
- additive feature
- behavior revision
- bug/regression fix
- security/auth change
- observability/operations change
- AI-first operating-model change: delegated work, retained human authority, agent/team responsibility, approval gates, exception handling, policy/permission rules, audit traces, evidence/risk thresholds, UI supervision surfaces, or outcome metrics
- workstream expertise change: functional-agent prompt intent, `SkillDocument`/`ReferenceDocument` content, `AgentSkillManifest`/`AgentReferenceManifest` entries, `readSkill`/`readReferenceDoc` loader access, `ToolPermissionBoundary`, authority profile, default-content/upgrade policy, governance owner, `PromptAssemblyTrace`/`SkillLoadTrace`/`ReferenceLoadTrace`/`AgentWorkTrace`, or expertise tests
- integration contract change
- UI/API surface change, including web UI style-guide selection or token changes
- workstream graph change: workstream responsibility, role-specific dashboard summary, attention category/item, human surface graph node/edge, system-message surface, internal workstream agent graph delegation/result, governed-tool exposure, capability exposure, autonomous task candidate, notification/projection, or audit/work trace linkage
- implementation discovery
- de-scope/removal
- unclear change requiring clarification

Ask the smallest clarifying question only when necessary to avoid changing the wrong semantic layer or queue scope.

## Operating flow

Use `../docs/intent-compiler-skill-contracts.md` and `../docs/intent-to-realization-flow.md` for the detailed current-intent and planning output contract. Return only the actionable summary, affected graph nodes/artifacts, required edits or queue changes, assumptions/questions, and next step. Preserve secure SaaS foundation, generated-SaaS runtime completion, tenant/customer scoping, backend authorization, governed agents/tools, traces, and tests when in scope.

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
- the change was classified as an incremental intent delta
- authoritative current-intent graph nodes and specs were updated before queue edits
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
