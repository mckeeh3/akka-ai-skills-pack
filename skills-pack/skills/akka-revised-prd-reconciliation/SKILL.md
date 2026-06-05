---
name: akka-revised-prd-reconciliation
description: Reconcile a revised or replacement PRD against existing app-description, specs, backlogs, and pending tasks; produce a controlled delta instead of blindly regenerating the task queue.
---

# Akka Revised PRD Reconciliation

Use this skill when a project already has app-description/spec/backlog/queue artifacts and the user provides a revised PRD, replacement requirements document, or substantial product specification update.

This skill protects long-running work from stale plans. It compares the revised requirements to the current maintained artifacts and updates them deliberately.

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
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/requirements-to-workstream-development-process.md` when reconciling generated SaaS requirements, PRDs, workstreams, dashboards, attention, surface actions, capabilities, or task queues
- `../docs/pending-task-queue.md`
- `../docs/solution-plan-to-implementation-queue.md`
- `../docs/internal-app-description-architecture.md`
- `../docs/app-description-maintenance-flow.md`
- `../docs/workstream-expertise-model.md` when the revised PRD adds or changes functional agents, workstream expertise, prompts, skills, references, manifests, loaders, tool boundaries, governed agent behavior, traces, default content, or expertise tests
- `../akka-prd-to-specs-backlog/SKILL.md`
- `../akka-change-request-to-spec-update/SKILL.md`
- `../app-description-change-impact/SKILL.md`
- `../ai-first-saas/SKILL.md`
- target project `app-description/00-system/app-manifest.md` if present
- target project `app-description/10-capabilities/capabilities-index.md` if present
- target project `app-description/20-behavior/behavior-index.md` if present
- target project `specs/README.md` if present
- target project `specs/akka-solution-plan.md` if present
- target project `specs/pending-tasks.md` if present
- relevant `specs/slices/*.md`, `specs/backlog/*.md`, and `specs/tasks/**/*.md`

Read the revised PRD completely.

If an original PRD is available, read it only when needed for diff confidence. Prefer reconciling against the maintained app-description/specs because they are the current source of truth.

## Reconciliation workflow

### 1. Establish current baseline

Summarize the current baseline from maintained artifacts:
- app identity and goals
- workstream inventory, owner functional agents, role-specific dashboards, attention categories/items, dashboard contracts, and left-rail/My Account attention summaries
- human surface graph nodes/edges, structured surfaces, surface actions, system-message surfaces, and capability/governed-tool mappings
- internal workstream agent graph nodes, delegations, worker results, escalations, and human attention items they create
- capabilities
- major behavior flows
- current AI-first interpretation if present: delegated work, durable goals/plans, agent/team authority, approval/exception model, policies, traces, supervision surfaces, and outcome loops
- current workstream expertise baseline if present: expert bundles, prompt intent, governed skill/reference documents, compact manifests, loader access, `ToolPermissionBoundary`, default-content/upgrade policy, trace obligations, UI/governance surfaces, and expertise tests
- chosen Akka components
- slice/backlog structure
- queue status counts
- completed work that may be affected

### 2. Extract revised PRD facts

From the revised PRD, extract:
- actors and roles
- capabilities and outcomes
- commands/write operations
- queries/views/reporting needs
- workflows and timers
- durable internal/background worker candidates, including AutonomousAgent task lifecycle, notification, dependency, failure/cancellation, and result-surface needs when applicable
- integrations
- AI-first operating-model facts: human objectives, delegated work, retained authority, agent/team responsibilities, policies, approval gates, exception paths, evidence/risk thresholds, audit/trace needs, UI supervision surfaces, learning loops, and outcome metrics
- workstream expertise facts: which functional agents need expert bundles, prompt intent changes, procedural skills, reference documents, `AgentSkillManifest`/`AgentReferenceManifest` entries, `readSkill`/`readReferenceDoc` behavior, denied-load rules, tool-boundary grants, default-content upgrade needs, governance owner, trace surfaces, and expertise tests
- security/auth requirements
- observability/operational requirements
- explicit non-goals/de-scopes
- acceptance and regression expectations

### 3. Produce a delta classification

Classify each meaningful difference as:
- added
- changed
- removed/de-scoped
- clarified
- conflict/open question
- implementation detail only

For each delta, identify likely affected:
- existing workstream graph elements: workstream ownership, role-specific dashboards, attention sources/items, human surface graph nodes/edges, internal workstream agent graph delegations/results, governed-tools, workstream expertise docs, and capability mappings
- app-description layers
- `specs/akka-solution-plan.md`
- cross-cutting specs
- slice specs
- sprint/module vertical contracts
- backlog files
- task briefs
- pending tasks
- generated outputs/components

Do not classify AI-first changes as implementation detail only when they affect authorization, autonomous action, human review, auditability, policy behavior, or outcome accountability.

Do not classify workstream expertise changes as prompt-only or implementation detail only when they affect governed documents, manifests, loaders, tool permissions, reference access, authority profile, capability exposure, UI/governance surfaces, traceability, generation assets, or tests.

### 4. Update app-description first when present

If `app-description/` exists, update authoritative layers before `specs/`:
1. `12-workstreams/` workstream expertise when functional-agent expertise changed: bundle scope, prompt intent, governed skills, references, compact manifests, capability map, `ToolPermissionBoundary`, authority profile, loader denial semantics, surfaces, traces, governance owner, default-content/upgrade policy, and expertise tests
2. capabilities, especially when capability exposure, agent-callable tools, approval semantics, or expertise-to-capability mappings changed
3. behavior
4. tests, including assigned/unassigned skill/reference loads, tool-boundary denial, no-authority-expansion, trace emission, and UI surface coverage where relevant
5. auth/security, especially authority boundaries, tenant/customer scope, data-access, and permission enforcement for loaders/tools
6. observability, especially `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, data-access, decision, and audit events
7. AI-first operating model and governance sections when delegated work, agent/team boundaries, approvals, exceptions, policy, trace, UI supervision, learning, or outcomes changed
8. UI/governance surfaces when manifest display, skill/reference review, denied-load recovery, decision cards, supervision, governance center, digest, or audit views changed
9. traceability, generation maps, and readiness

If the revised PRD conflicts with current app-description meaning, do not silently choose. Ask the smallest clarifying question or mark affected tasks blocked.

### 5. Update specs and backlogs

Update existing files rather than replacing the whole tree:
- modify existing slice specs when a slice remains valid
- add new numbered slice specs for new capability areas
- preserve or update requirements-to-workstream sections in solution plans, cross-cutting specs, slices, sprints, and backlogs when workstream identity, role-specific dashboards, attention categories/items, human surface graph nodes/edges, governed-tool mappings, internal workstream agent graph delegations/results, capability mappings, notifications/projections, AutonomousAgent task candidates, authority, policy, approval, audit, UI supervision, or outcome semantics changed
- preserve or update minimum-starter/full-core readiness sections when the revised PRD changes foundation scope; minimum/basic/starter/chatbot-like generated SaaS still starts from the five-core workstream starter shell, and any narrower User-Admin-only or page/chat-only task shape must be corrected, blocked, or explicitly marked non-SaaS/mechanics-only
- preserve or update workstream expertise sections/tasks when functional-agent competence changes, keeping prompt/skill/reference documents, manifests, `readSkill`/`readReferenceDoc` loaders, `SkillLoadTrace`/`ReferenceLoadTrace`, tool boundaries, UI/governance surfaces, generation/default-content assets, and tests as separate bounded work rather than one vague `agent governance` task
- mark de-scoped behavior in specs explicitly rather than deleting context when useful
- update matching backlog files
- create new backlog files for new slices
- update or create task briefs for oversized changed work

Preserve numbering. Add new numbers at the end unless a local insertion is clearly safer and does not force renumbering.

### 6. Reconcile `specs/pending-tasks.md`

Apply queue reconciliation rules:
- preserve IDs, order, and status history
- do not delete completed tasks
- do not reset `done` tasks to `pending`
- append new tasks for new work
- update pending tasks whose scope/read list/skills changed but still represent the same work, preserving workstream id, role-specific dashboard, attention category/item, surface graph node/edge or surface action, governed-tool id/class, capability id/class, internal-agent delegation/result context, AuthContext/scope, selected substrate/exposure channel, notification/projection, and audit/work trace context; add `ai-first-saas` and relevant companion skills when tasks implement agentic operating-model behavior; add `akka-autonomous-agents` or governance/testing companions when durable AutonomousAgent task lifecycle is in scope; add `docs/workstream-expertise-model.md` and focused agent governance/testing skills when expert bundles, skills, references, manifests, loaders, boundaries, traces, default content, or expertise UI are in scope
- block or decompose stale task shapes such as CRUD/page/component-only work, `make the agent expert`, or `agent governance` unless they have a self-contained fresh-session scope for the relevant workstream/surface/capability contract and exactly which expert bundle, governed documents, manifests, `readSkill`/`readReferenceDoc` loaders, `SkillLoadTrace`/`ReferenceLoadTrace`, boundaries, surfaces, traces, and tests are included
- mark obsolete non-done tasks as `superseded`
- add follow-up tasks for completed work that must change
- mark tasks `blocked` when the revised PRD creates unresolved decisions about delegation, authority, approvals, policy, evidence/risk, audit, supervision UI, workstream expertise, reference access, loader permission, or outcomes
- keep dependencies valid and not over-serialized

Status counts should be reported after reconciliation.

### 7. Decide whether broader replanning is required

Recommend one of:
- `localized reconciliation` — changes are bounded to known slices/tasks
- `broad reconciliation` — multiple slices/backlogs changed but architecture is intact
- `full replanning recommended` — foundational assumptions changed enough that patching is unsafe

Do not claim a localized update is safe without a clear dependency chain.

## Queue supersession rules

Use `superseded` when a pending/blocked/deferred task is no longer valid because of the revised PRD.

Example:

```md
- status: superseded
- notes:
  - superseded: revised PRD replaced this task with TASK-047 and changed approval behavior from single-step to escalation workflow
```

For completed tasks, keep `done`; append a new follow-up task instead:

```md
### TASK-047: Update approval workflow for revised escalation policy

- status: pending
- source: specs/backlog/03-approval-build-backlog.md
- depends on: [TASK-012]
...
```

## Required reconciliation report

Use this response shape:

```md
# Revised PRD Reconciliation

## Baseline read
- ...

## Revised PRD delta
### Added
- ...
### Changed
- ...
### Removed/de-scoped
- ...
### Clarifications
- ...
### Conflicts/open questions
- ...

## Artifact updates
- app-description:
- specs:
- backlogs:
- task briefs:
- pending tasks:

## Queue reconciliation
- preserved:
- updated:
- added:
- blocked:
- superseded:
- follow-up tasks for completed work:

## AI-first reconciliation
- delegated work / authority changes:
- workstream expertise changes:
- governance / policy / approval changes:
- audit / trace / outcome changes:
- unresolved AI-first or expertise blockers:

## Replanning recommendation
- localized | broad | full replanning recommended
- rationale:

## Next runnable task
- ...
```

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
- current maintained artifacts were used as baseline
- deltas are categorized as added/changed/removed/clarified/conflict
- AI-first delegation, authority, governance, audit, UI supervision, and outcome implications were compared against the current baseline
- workstream expertise implications were compared against the current baseline, including expert bundles, governed prompts/skills/references, compact manifests, loader authorization, `ToolPermissionBoundary`, traces, UI/governance surfaces, default-content generation assets, and tests
- affected app-description/spec/backlog/task files were updated
- queue history was preserved
- obsolete non-done tasks were superseded, not deleted
- completed affected work has follow-up tasks
- queue status counts and next runnable task are reported
