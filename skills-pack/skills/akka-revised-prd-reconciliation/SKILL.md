---
name: akka-revised-prd-reconciliation
description: Reconcile a revised or replacement PRD against existing app-description, specs, backlogs, and pending tasks; produce a controlled delta instead of blindly regenerating the task queue.
---

# Akka Revised PRD Reconciliation

Use this skill when a project already has app-description/spec/backlog/queue artifacts and the user provides a revised PRD, replacement requirements document, or substantial product specification update.

This skill protects long-running work from stale plans. It treats the revised PRD as incremental source intent, compares it to the current intent graph and maintained planning artifacts, and updates only the accepted current-state model and downstream queues deliberately.

## Goal

Reconcile a revised PRD with the current project plan by identifying:
- added requirements
- changed requirements
- removed or de-scoped requirements
- clarified requirements
- conflicting requirements
- added, changed, or removed AI-first operating-model semantics: delegated work, retained human authority, policies, approvals, exceptions, audit traces, UI supervision, and outcomes
- added, changed, or removed workstream expertise semantics: functional-agent expert bundles, governed prompts/skills/references, compact manifests, loader authorization, tool boundaries, traces, UI/governance surfaces, default-content upgrade rules, and expertise tests
- completed implementation work that now needs follow-up
- pending tasks that remain valid
- pending tasks that must be updated, blocked, deferred, or superseded
- new queue tasks to append
- lifecycle/readiness and compile/manual-test contract changes needed by affected task briefs and queue entries

The output should preserve useful planning history and produce a trustworthy next implementation queue. For generated secure AI-first SaaS, reconciliation must preserve the existing workstream graph instead of starting a fresh parallel app plan: workstreams → role-specific dashboard attention contracts → human surface graph nodes and actions → internal workstream agent graph delegations/results → governed-tools inside capabilities and surface/action maps → Akka substrate/exposure channels → request-based workstream Agent turns or durable AutonomousAgent task candidates → notifications/projections → audit/work traces.

## Use this skill when

The task sounds like:
- "here is the revised PRD; update the plan"
- "reconcile this new PRD with the existing backlog"
- "the requirements changed; update specs and pending tasks"
- "compare this PRD version against our current specs"
- "do not restart from scratch; update the existing plan"

Use `akka-change-request-to-spec-update` for small feature requests, bug reports, or implementation discoveries.

Use `akka-prd-to-specs-backlog` when no existing planning artifacts exist or the user explicitly wants a clean initial planning package.

Use this skill as a reconciliation layer, not as a fresh planning generator: preserve existing app-description/spec/backlog/task-brief/queue IDs and statuses, supersede obsolete non-done tasks instead of deleting them, append follow-up tasks for completed work that must change, and carry forward workstream ids, role-specific dashboards, attention items, surface graph nodes/edges, governed-tool ids, capability ids, internal-agent delegations, AuthContext/scope, approval gates, audit/trace obligations, workstream expertise decisions, tests, Java base package, existing-app extension semantics, and style-guide decisions unless the revised PRD explicitly replaces them. If the app already has implementation artifacts or a legacy `specs/scaffold-report.md`, never generate a fresh parallel app, app-description tree, or queue; extend and reconcile the existing app instead.

## Required reading

Read these first if present:
- `../README.md`
- `../docs/app-development-lifecycle.md`
- `../docs/app-worker-tool-model.md`
- `../docs/app-description-to-code-compile-contract.md`
- `../docs/manual-test-reconciliation.md` when reconciling tester/runtime findings in the revised requirements
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-to-realization-flow.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/requirements-to-workstream-development-process.md` when reconciling generated SaaS requirements, PRDs, workstreams, dashboards, attention, surface actions, capabilities, or task queues
- `../docs/pending-task-queue.md`
- `../docs/solution-plan-to-implementation-queue.md`
- `../docs/workstream-expertise-model.md` when the revised PRD adds or changes functional agents, workstream expertise, prompts, skills, references, manifests, loaders, tool boundaries, governed agent behavior, traces, default content, or expertise tests
- `../akka-prd-to-specs-backlog/SKILL.md`
- `../akka-change-request-to-spec-update/SKILL.md`
- `../app-description-change-impact/SKILL.md`
- `../ai-first-saas/SKILL.md`
- target project path: app-description/app.md if present
- target project path: relevant app-description/global/** and app-description/domains/** current-intent graph nodes
- target project path: legacy numbered app-description files such as app-description/00-system/app-manifest.md, app-description/10-capabilities/capabilities-index.md, or app-description/20-behavior/behavior-index.md only when the target project still maintains them
- target project path: specs/README.md if present
- target project path: specs/akka-solution-plan.md if present
- target project path: specs/pending-tasks.md if present
- relevant `specs/slices/*.md`, `specs/backlog/*.md`, and `specs/tasks/**/*.md`

Read the revised PRD completely.

If an original PRD is available, read it only when needed for diff confidence. Prefer reconciling against the maintained app-description/specs because they are the current source of truth.

## Reconciliation workflow

Use `../docs/intent-compiler-skill-contracts.md` and `../docs/intent-to-realization-flow.md` for the shared current-intent, queue, task, and reconciliation contract. Preserve existing ids, statuses, dependencies, implementation history, graph-node provenance, capability/workstream/surface/agent context, AuthContext/scope, authorization, traces, idempotency, tests, acceptance checks, and explicit out-of-scope items.

For SaaS Foundation App planning, keep required coverage for invitation lifecycle, email delivery, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, AI admin/AdminRiskAgent/AccessReviewAgent, decision cards for risky admin, AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, readSkill, PromptAssemblyTrace, SkillLoadTrace, behavior editing, agent catalog, and agent detail in the relevant task sequence.

## Anti-patterns

Avoid:
- blindly regenerating all specs and losing task history
- deleting completed queue entries
- renumbering existing tasks
- letting old pending tasks survive when their source requirement changed
- treating the revised PRD as additive only
- skipping removal/de-scope analysis
- losing prior AI-first governance, audit, policy, approval, workstream expertise, trace, UI/governance, generation, test, or outcome semantics during rewrite
- changing implementation code during reconciliation
- failing to surface conflicts between the revised PRD and current app-description

## Final review checklist

Before finishing, verify:
- the revised PRD was read completely
- current maintained artifacts and current-intent graph nodes were used as baseline
- deltas are categorized as added/changed/removed/clarified/conflict and reconciled without retaining superseded intent
- AI-first delegation, authority, governance, audit, UI supervision, and outcome implications were compared against the current baseline
- workstream expertise implications were compared against the current baseline, including expert bundles, governed prompts/skills/references, compact manifests, loader authorization, `ToolPermissionBoundary`, traces, UI/governance surfaces, default-content generation assets, and tests
- affected app-description/spec/backlog/task files were updated
- affected task briefs and queue entries preserve or repair lifecycle/readiness, compile-contract, and manual/runtime validation fields
- queue history was preserved
- obsolete non-done tasks were superseded, not deleted
- completed affected work has follow-up tasks
- queue status counts and next runnable task are reported
