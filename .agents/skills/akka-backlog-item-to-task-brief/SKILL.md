---
name: akka-backlog-item-to-task-brief
description: Turn one specific item from a specs/backlog/*-build-backlog.md file into a small physical specs/tasks/... task brief that is ready for one focused harness implementation run.
---

# Akka Backlog Item to Task Brief

Use this skill when the repository already has a slice spec and build backlog, but one backlog item is still too large, too cross-cutting, or too ambiguous to hand directly to code generation.

This is the **leaf planning skill** below `akka-prd-to-specs-backlog` and `akka-slice-spec-to-backlog`. It creates the implementation contract for exactly one focused harness run; it does not implement the task and does not broaden the source backlog item.

## Goal

Create or update one small task brief under `specs/tasks/` that:
- maps to exactly one slice, one backlog item, and the current-intent graph node(s) being realized
- narrows scope to one focused implementation run
- names the smallest required reads
- makes non-goals explicit
- lists the exact Akka components and skills to load
- lists expected code/test outputs
- gives done criteria clear enough for a harness to stop at the right boundary
- creates or updates the corresponding queue entry in `specs/pending-tasks.md`

## Use this skill when

The task sounds like one of these:
- "Turn backlog item 3 into a task brief"
- "This backlog item is still too big; split it again"
- "Write a focused implementation brief for slice 02 notifications"
- "Create a task file under specs/tasks for this backlog item"
- "Narrow this backlog into one harness-sized coding task"

Do **not** use this skill when:
- the user is still at PRD level and needs slices or backlogs
- the backlog item is already small and unambiguous enough to code directly
- the user wants implementation code now rather than one more planning cut

## Relationship to other skills

This skill assumes the broader planning layers already exist:
- `specs/akka-solution-plan.md`
- one or more `specs/cross-cutting/*.md` files
- one target `specs/slices/*.md` file
- one matching `specs/backlog/*-build-backlog.md` file

This skill should normally be used after:
- `../akka-prd-to-specs-backlog/SKILL.md`
- `../akka-slice-spec-to-backlog/SKILL.md`

## Required reading

Read these first if present:
- `../README.md`
- `../akka-prd-to-specs-backlog/SKILL.md`
- `../akka-slice-spec-to-backlog/SKILL.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/intent-compiler-skill-contracts.md`
- target project path: specs/README.md
- target project path: specs/backlog/README.md
- target project path: specs/tasks/README.md
- target project path: specs/pending-tasks.md if it already exists
- `../docs/pending-task-queue.md`
- `../docs/ai-first-saas-application-architecture.md` when the backlog item includes delegated work, agents, approvals, exceptions, governance, audit, supervision UI, or outcomes
- `../docs/requirements-to-workstream-development-process.md` when the backlog item touches generated SaaS workstreams, attention, dashboards, surface actions, capabilities, AutonomousAgent tasks, notifications/projections, or trace-aware queue execution
- target project path: app-description/15-operating-model/ or equivalent operating-model specs when present and relevant
- target project path: specs/templates/implementation-task-template.md if the target project provides it; otherwise use the task-brief structure described in this skill
- target project path: specs/akka-solution-plan.md
- the target slice spec under target project path: specs/slices/
- the target backlog file under target project path: specs/backlog/
- any cross-cutting spec files referenced by that slice or backlog item

If a matching task brief already exists:
- read it first
- refine it instead of duplicating it
- preserve numbering and naming consistency within the slice task directory

## What this skill must produce

Use `../docs/intent-compiler-skill-contracts.md` and `../docs/intent-to-realization-flow.md` for the detailed task-brief and queue output contract. Preserve generated-SaaS/SaaS Foundation App context when in scope, including invitation lifecycle, email delivery, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin/AdminRiskAgent/AccessReviewAgent, decision cards for risky admin, AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, readSkill, PromptAssemblyTrace, SkillLoadTrace, behavior editing, agent catalog, and agent detail coverage across the generated specs/backlog/task sequence.

## Sizing rules

A good task brief is usually one of these:
- one shared domain/common package task
- one entity or workflow plus its immediate domain records
- one consumer or timed action plus its tests
- one view family plus its tests
- one endpoint family plus its tests
- one tightly bounded integration adapter task

A task brief is too large if it still spans:
- multiple unrelated component families
- too many files for one focused run
- both broad domain modeling and multiple downstream delivery layers at once
- unresolved architecture questions

If that happens, split again before coding.
Only add queue entries that are small enough for one focused harness run; if the work remains too broad, create multiple task briefs or mark the corresponding queue item `blocked` with the exact split needed. For SaaS foundation backlog items, never keep one task brief that spans invitation lifecycle, email delivery/outbox, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin agents such as AdminRiskAgent and AccessReviewAgent or a skilled UserAdminAgent, decision cards for risky admin actions, admin UI surfaces, and security/admin tests; split those into separate harness tasks before app-specific domain features.

Managed-agent foundation task briefs must also be split before coding when they span multiple agent-governance families. Do not make one runnable brief cover `AgentDefinition`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, `AgentSkillManifest`, `AgentReferenceManifest`, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, `SkillLoadTrace`, `ReferenceLoadTrace`, `PromptAssemblyTrace`, behavior editing agents, tool boundaries, traces, UI, and tests. Split into focused briefs such as AgentDefinition lifecycle/profile, prompt governance and prompt assembly traces, skill governance and skill-load traces, reference governance and reference-load traces, manifest/readSkill/readReferenceDoc enforcement, tool permission boundaries, AgentWorkTrace, behavior editing proposal flow, UI surfaces, and security/admin/agent-governance tests.

## Naming rules

Keep numbering aligned inside each slice task directory:
- `specs/tasks/01-domain-specific/01-...md`
- `specs/tasks/01-domain-specific/02-...md`

Use names that describe the implementation boundary, not just the business area.

Prefer names like:
- `shared-foundations-and-tenant-settings`
- `alert-entity-and-lifecycle-tests`
- `order-workflow-core`
- `report-endpoints-and-export-tests`

Avoid names like:
- `misc`
- `part-2`
- `backend-work`

## Anti-patterns

Avoid:
- rewriting the whole backlog item without narrowing it
- omitting non-goals
- omitting tests or local/runtime validation for feature-bearing tasks
- listing broad outputs like "implement service management"
- leaving the skill list too vague for the next run
- creating task briefs for every backlog item even when the backlog item is already small enough

## Final review checklist

Before finishing, verify:
- the task brief points to the correct slice, backlog, and current-intent graph node(s)
- the scope is smaller than the backlog item, not larger
- the reads are the minimum needed
- non-goals are explicit
- outputs are specific files or a tightly bounded file family
- managed-agent task briefs name their exact foundation scope, such as AgentDefinition, PromptDocument, SkillDocument, ReferenceDocument, AgentSkillManifest, AgentReferenceManifest, readSkill, readReferenceDoc, SkillLoadTrace, ReferenceLoadTrace, PromptAssemblyTrace, behavior editing, ToolPermissionBoundary, AgentWorkTrace, UI, or tests, instead of using one vague agent-governance label
- AI-first authority, policy, decision, trace, UI-surface, evaluation, and outcome context from the backlog item is either preserved in the brief or explicitly out of scope
- unresolved AI-first blockers are captured as pending questions and block only affected queue entries
- required tests are named clearly
- local app-run, endpoint smoke, browser/workstream smoke, or manual-test validation is named for tasks that implement runtime behavior, or the task explicitly says it is non-runtime/internal-only
- feature-bearing task briefs name the target readiness level and canonical runtime path: browser/surface/action or confirmed chat tool-plan/non-UI trigger -> API/endpoint/client -> Akka component/service/substrate -> trace/audit/view
- feature-bearing task briefs require runtime evidence in queue notes before `done`: role/AuthContext/tenant setup, governed-tool id and actor adapter exercised, confirmation/approval and idempotency/transaction evidence when consequential tool use is in scope, denial case, provider configured or fail-closed status, commands/manual-smoke result, and trace/audit evidence
- the listed skills match the task's component type
- done criteria define a clear stopping point and do not call a named feature implemented when required backend/API/UI/auth/audit/test pieces are deferred
- `specs/pending-tasks.md` has a matching entry or updated existing entry for this task brief
- the task brief and queue entry both carry the required vertical workstream contract, including workstream tool catalog context, governed-tool id, actor adapter/exposure channel (`surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/workflow/timer/consumer/MCP/internal), confirmation/approval behavior, idempotency/transaction boundary, result/partial-failure surface, trace source/evidence, or an explicit internal/foundation/cross-cutting/docs-only/non-runtime exemption with non-attention/non-UI reason, trace expectations, and validation path
- the pending-task validator passes when available: use `bash skills-pack/tools/validate-pending-task-workstream-contract.sh specs/pending-tasks.md` from a source attention, or `bash .agents/skills/tools/validate-pending-task-workstream-contract.sh specs/pending-tasks.md` from an installed skills library
- existing queue task IDs and statuses are preserved

## Example invocation patterns

Use prompts like:
- "Read `specs/backlog/01-domain-specific-build-backlog.md` and turn task item 1 into a task brief under `specs/tasks/01-domain-specific/`."
- "The `Ingestion endpoint` item in `specs/backlog/01-domain-specific-build-backlog.md` is still too large. Split it into one focused task brief."
- "Create a `specs/tasks/...` brief for the `workflow core` work in `specs/backlog/03-domain-specific-process-build-backlog.md`."
- "Refine the existing task brief for `shared-foundations-and-tenant-settings` so it is implementable in one harness run."

A good response should:
- identify the source backlog item
- show the target `specs/tasks/...` path
- preserve slice scope
- tighten the task until one focused implementation run is realistic

## Response style

When using this skill:
- name the source backlog file and target backlog item first
- summarize how you are narrowing it
- then write the task brief file
- update `specs/pending-tasks.md` for the narrowed task
- clearly report which files were added or updated
- do not jump into implementation code
