# Pending task queue

Use this contract when PRD/spec planning creates follow-on implementation work that should be executed reliably across separate harness sessions.

Purpose:
- persist follow-on work after decomposition, backlog creation, task-brief creation, and resolution or deferral of blocking questions
- preserve full-stack secure AI-first SaaS operating-model constraints in implementation tasks, including governance, supervision UI, audit, and outcomes
- make the next runnable task obvious
- keep each implementation run bounded to one task
- support fresh-context execution for each task
- record whether tasks are pending, blocked, deferred, done, or superseded

## Canonical location

In a target application project, use:

```text
specs/pending-tasks.md
```

This file belongs in the target project workspace, not inside the installed `.agents/` pack.

If a project already has an equivalent issue tracker or task queue, the harness may map this contract onto that system, but the markdown file is the default portable representation.

## Queue rules

1. Execute one queue task per harness run.
2. Prefer a fresh context session for every task.
3. Do not combine adjacent tasks just because their files are nearby.
4. Select the first `pending` task whose dependencies are `done` or empty.
5. Mark a task `done` only after its required checks pass and its done criteria are satisfied. A check that is not runnable blocks completion unless the task is explicitly non-runtime/docs-only, or the user/project has accepted the limitation and the task's notes say why the named feature still works.
6. Mark a task `blocked` when required decisions, pending questions, inputs, dependencies, build/runtime preconditions, or local validation prerequisites are missing.
7. For AI-first work, unresolved delegated authority, approval, policy/risk threshold, evidence, audit/trace, supervision UI, evaluation, or outcome decisions block only the affected tasks.
8. Mark a task `deferred` only when the user or plan explicitly chooses to postpone it.
9. Mark a task `superseded` when a later app-description/spec/PRD change replaces the task and it should not be executed.
10. Keep the queue stable: append new tasks or update statuses; do not renumber existing task IDs casually.
11. When a task is complete, blocked, or superseded, report the next runnable pending task if one exists.
12. At the end of ordinary harness responses, remind the user when runnable pending tasks remain, without automatically starting them.

## Status values

Use these exact status values:

- `pending` — ready or potentially ready to execute when dependencies are satisfied
- `in-progress` — currently being executed in this harness run
- `blocked` — cannot proceed without a decision, dependency, missing input, or failed prerequisite
- `done` — completed and validated as far as the task requires; for generated app implementation, the implemented behavior works through its intended local runtime/API/UI surface or the task is explicitly non-runtime/internal-only
- `deferred` — intentionally postponed and not eligible for automatic next-task selection
- `superseded` — replaced by a later requirement, spec, backlog, or task and not eligible for execution

## Task ID format

Use stable, sortable task IDs:

```text
TASK-001
TASK-002
TASK-003
```

For large module/sprint or multi-slice plans, a sprint-prefixed or slice-prefixed ID is also acceptable:

```text
TASK-01-001
TASK-01-002
TASK-02-001
```

Choose one format per project and keep it consistent. When using module-oriented sprint planning, group tasks in file order by sprint/module or include the sprint number in the task ID so the next full-stack module increment is obvious.

## Required queue shape

Use this structure. For SaaS app queues, the first runnable tasks must cover the secure user-admin and governed runtime agent foundation before app-specific domain features and must not collapse this work into a vague `auth/admin` or `agent governance` item. Split foundation work into bounded tasks for invitation lifecycle, email delivery/outbox, UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, membership/role management, admin audit/search, `AgentDefinition` lifecycle/profile, `PromptDocument`/`PromptVersion` governance and `PromptAssemblyTrace`, `SkillDocument`/`SkillVersion` governance, `ReferenceDocument`/`ReferenceVersion` governance, `AgentSkillManifest`, `AgentReferenceManifest`, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, `SkillLoadTrace`, `ReferenceLoadTrace`, `ToolPermissionBoundary`, `AgentWorkTrace`, behavior editing agents, AI admin agents such as AdminRiskAgent and AccessReviewAgent or a skilled UserAdminAgent, decision cards for risky admin actions, agent catalog/detail, prompt/skill/reference/manifest/tool-boundary UI surfaces, trace UI, and security/admin/agent-governance tests.

```md
# Pending Tasks

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.

## Tasks

### TASK-001: <short task title>

- status: pending
- source: specs/backlog/01-<slice>-build-backlog.md
- task brief: specs/tasks/01-<slice>/01-<task>.md
- depends on: []
- required reads:
  - specs/akka-solution-plan.md
  - specs/backlog/01-<slice>-build-backlog.md
  - specs/tasks/01-<slice>/01-<task>.md
  - docs/ai-first-saas-application-architecture.md when the task implements AI-first objects, authority, policies, decisions, traces, UI surfaces, or outcomes
  - docs/workstream-expertise-model.md when the task implements or changes an LLM-backed functional agent's workstream expertise, model binding, skills, references, manifests, loaders, tool boundaries, traces, or expertise surfaces
  - skills/akka-agent-behavior-profiles/SKILL.md when the task implements AgentDefinition, lifecycle, authority, agent catalog, or agent detail
  - skills/akka-agent-prompt-governance/SKILL.md when the task implements PromptDocument, PromptVersion, prompt assembly, PromptAssemblyTrace, or prompt governance UI
  - skills/akka-agent-skill-governance/SKILL.md when the task implements SkillDocument, SkillVersion, AgentSkillManifest, readSkill, SkillLoadTrace, or skill/manifest/tool-boundary UI
  - skills/akka-agent-work-trace/SKILL.md when the task implements AgentWorkTrace or agent trace search/detail UI
- skills:
  - <skill-name>
  - ai-first-saas when the task must preserve AI-first operating-model semantics
- expected outputs:
  - <file, package, component, or test family>
- required checks:
  - <test command or verification step>
- done criteria:
  - <observable completion criterion>
- notes:
  - <optional assumptions, constraints, provenance, or links>
  - source requirement ids: <optional stable requirement IDs when available>
  - source capability ids: <optional app-description capability IDs when available>
  - vertical contract: <workstreamId/functional agent or internal/foundation scope; attention category or non-attention reason; role-specific dashboard id/purpose; human surface graph node/state/action edge or non-UI trigger; governed-tool id/type/exposure (browser-tool, agent-tool, internal-tool, workflow/timer/consumer/MCP-tool); capability id/class; API/exposure channel; selected Akka substrate; internal workstream agent graph delegation/result surface when relevant; events/notifications/projections; audit/work trace; tests/local validation>
  - autonomous task contract: <optional AutonomousAgent task definition, start/query/result/lifecycle capability ids, notification mapping, result/progress surface ids, failure/cancellation attention behavior, and lifecycle tests>
  - foundation scope: <optional AgentDefinition/PromptDocument/SkillDocument/AgentSkillManifest/readSkill/PromptAssemblyTrace/SkillLoadTrace/AgentWorkTrace scope for managed-agent foundation tasks>
  - supersedes: <optional task IDs this task replaces>
  - superseded by: <optional replacement task ID when this task is superseded>
```

If no separate task brief exists, omit `task brief:` or set it to `none` and make the `source` backlog item specific enough to execute.

## Required vertical workstream contract block

Generated full-stack AI-first SaaS task briefs and runnable queue entries must make their vertical context explicit in a dedicated section or note block. This is the pre-implementation gate that prevents component-only, page-only, CRUD-only, or dashboard-only tasks from bypassing the workstream model.

Use this section in task briefs:

```md
## Vertical workstream contract

- Workstream / functional agent: <workstream id and backing functional/context-area agent, or explicit internal-only/foundation/cross-cutting scope>
- Attention category or non-attention reason: <approval/decision/exception/blocked/etc. or why no attention item applies>
- Role-specific dashboard / surface: <dashboard or surface id/purpose and target role/AuthContext>
- Surface graph node/action edge: <source surface, action, target/result/system-message surface, or non-UI trigger>
- Governed-tool id and exposure: <governedToolId plus browser-tool/agent-tool/internal-tool/workflow-tool/timer-tool/consumer-tool/MCP-tool/API exposure, or explicit none for non-runtime/internal-only work>
- Capability id: <capability id/class or explicit foundation scope>
- AuthContext / roles / tenant scope: <actor/caller, roles/capabilities, tenant/customer/account scope, denial expectations>
- Akka substrate: <entity/workflow/view/consumer/timed action/agent/autonomous agent/endpoint/frontend/test/docs-only>
- API / frontend / realtime path: <route/API/SSE/WebSocket/workstream surface path or non-UI reason>
- Audit/work trace requirements: <audit, trace, correlation, prompt/tool/data access, denial, or explicit none for docs-only>
- Local validation path: <mvn/npm/API/browser/manual smoke command or explicit non-runtime/internal-only reason>
```

For `specs/pending-tasks.md`, put the same content under the task's `notes` as a `vertical contract:` line or as equivalent bullet notes. A task may omit workstream/surface/action fields only when it explicitly says `internal-only`, `foundation-only`, `cross-cutting`, `docs-only`, or `non-runtime` and explains the non-attention/non-UI reason, capability/foundation scope, trace expectations, and validation path.

Before implementation, use the repository validator when available:

```bash
bash skills-pack/tools/validate-pending-task-workstream-contract.sh specs/pending-tasks.md
```

In a downstream fork, run the script from the repository source checkout when available, passing the target queue path. The harness install also makes pack tools available under `.agents/skills/tools/**` for installed-skill use. The validator is intentionally conservative: it checks for the presence of the required contract vocabulary, not semantic correctness. Passing the script does not replace reading the task brief; failing it means repair the backlog/task brief/queue before coding.

## Managed-agent and workstream expertise splitting guardrails

Generated SaaS queues must never collapse governed runtime agent foundation work into one vague `agent governance`, `managed agents`, or `AI foundation` task. If a backlog item spans `AgentDefinition`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, `AgentSkillManifest`, `AgentReferenceManifest`, authorized `readSkill(skillId)`, authorized `readReferenceDoc(referenceId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, behavior editing agents, `ToolPermissionBoundary`, `AgentWorkTrace`, UI, and tests, the item is too broad and must be split or blocked for a task brief.

Use bounded foundation task families such as:
- `AgentDefinition` lifecycle/profile, authority, disabled-agent denial, agent catalog/detail UI, and direct tests
- `PromptDocument`/`PromptVersion` governance, prompt assembly, `PromptAssemblyTrace`, prompt governance UI, and tests
- `SkillDocument`/`SkillVersion` governance, `AgentSkillManifest`, authorized `readSkill(skillId)`, `SkillLoadTrace`, skill/manifest UI, and tests
- `ReferenceDocument`/`ReferenceVersion` governance, `AgentReferenceManifest`, authorized `readReferenceDoc(referenceId)`, `ReferenceLoadTrace`, reference/manifest UI, and tests
- `ToolPermissionBoundary` management, approval-required authority expansion denial, tool-boundary UI, and tests
- behavior editing agents, proposed diff creation, review/approval routing, activation/denial audit, and tests
- `AgentWorkTrace` recording/search/detail UI and trace-retention/security tests
- focused security/admin/agent-governance regression tests across tenant isolation, AuthContext/scope, approval, audit/trace, disabled agents, unassigned skills/references, and unauthorized prompt/skill/reference/tool changes

For every new or materially changed domain-specific functional agent with LLM behavior, queue self-contained fresh-session workstream expertise tasks. Split as needed into:
- app-description expert bundle contract with functional agent, approved model binding (`ModelConfigRef`/`ModelPolicy` or explicit inherited governed default), surfaces, capabilities, authority, traces, and tests
- seeded default model refs/policies, prompt, skill documents, reference documents, compact skill/reference manifests, and provenance/checksum expectations
- runtime model-binding and loader/boundary work for model policy validation, fallback/no-fallback behavior, provider secret boundaries, `readSkill`, `readReferenceDoc`, assigned loads, unassigned denials, missing-boundary denials, redaction/token limits, and trace emission
- expertise manifest and governance UI surfaces that show model aliases/policy summaries, compact manifests, evidence, denials, decisions, trace links, and review state without exposing full bodies or provider secrets by default
- contract/runtime tests for model binding success/denial/fallback, provider-secret non-exposure, assigned skill/reference loads, denied loads, tool-boundary denial, no authority expansion from prompt/skill/reference text, tenant isolation, audit/work traces, and surface rendering

Every generated queue entry should preserve source capability ids when available, actor/caller, `AuthContext`, required role/scope or permission, approval gate, audit/trace obligation, UI surface, required checks, and the exact managed-agent foundation or workstream expert bundle scope it covers. For generated full-stack AI-first SaaS, this is a vertical contract, not optional notes: name or inherit the workstream, attention category, role-specific dashboard purpose, human surface graph node/edge or surface action, governed-tool id and qualified exposure (`browser-tool`, `agent-tool`, `internal-tool`, workflow/timer/consumer/MCP-tool), capability id, API/exposure channel, Akka substrate, internal workstream agent graph delegation/result surface when relevant, events/notifications/projections, audit/work trace, and local validation path. A component-only, CRUD-only, page-only, or dashboard-only task is not runnable unless it explicitly states an internal-only, foundation, or cross-cutting scope that does not need a workstream/surface/action. For LLM-backed workstream-agent tasks, the queue entry must name or inherit the approved model binding (`ModelConfigRef`/`ModelPolicy` or explicit inherited governed default), fallback policy, provider secret boundary, and model-use trace requirement. For generated full-stack AI-first SaaS, each runnable implementation task must also carry its vertical workstream contract: functional agent or internal-only/foundation scope, structured surface/action or workstream event/non-UI trigger, capability id/class, selected Akka substrate, frontend/API/realtime work, expertise artifacts when relevant, and required tests. If the task is part of a sprint's named visible capability, include the local app-run, API call, browser/workstream action, or manual smoke path that will prove the feature works; otherwise state that the task is non-runtime/internal-only.

## Implementation-ready vertical task rule

A pending task for generated full-stack AI-first SaaS is runnable only when it is a bounded vertical increment or an explicitly cross-cutting/foundation/internal task. Do not mark a task runnable if it names only a module, page, dashboard, CRUD screen, component family, or generic UI feature.

Each runnable task should state or inherit from its task brief/backlog:

- functional agent(s), or explicit internal-only/foundation/cross-cutting scope;
- structured surface/action or workstream event, or explicit non-UI trigger;
- capability id(s)/class(es), or explicit foundation scope;
- `AuthContext`, tenant/customer scope, and role/capability rules;
- selected Akka substrate and exposure surface such as HTTP/API, agent tool, workflow, timer, consumer, view, MCP, or internal method;
- frontend/API/realtime work when user-facing;
- success, validation, forbidden, tenant-isolation, idempotency, audit/trace, rendering/API/realtime, local-run/manual-smoke checks as applicable, or an explicit non-runtime/internal-only reason.

If this contract is missing, block the task for backlog/task-brief repair instead of guessing the missing workstream, attention semantics, dashboard/surface graph/action edge, governed-tool exposure, capability id, authority, notification/projection behavior, internal workstream agent graph result handling, autonomous task lifecycle, or component scope. If an LLM-backed functional agent is in scope, also block when the task does not name or inherit the workstream expert bundle, approved model binding, skill/reference manifests, authorized `readSkill`/`readReferenceDoc` loader behavior, `ToolPermissionBoundary`, prompt/skill/reference/work traces, expertise UI surfaces, and tests. If an AutonomousAgent is in scope, the task must state or inherit start/query/result/lifecycle capabilities, progress/result surfaces, task notifications, failure/cancellation attention behavior, and tests before execution.

## Selection algorithm

To choose the next task:

1. Read `specs/pending-tasks.md`.
2. Ignore tasks with status `done`, `blocked`, `deferred`, or `superseded`.
3. For each remaining `pending` task in file order, inspect `depends on`.
4. Select the first task whose dependencies are empty or all refer to tasks with status `done`.
5. If no task is runnable, report the earliest blocked dependency chain instead of coding.

## End-of-response reminder

When `specs/pending-tasks.md` exists and runnable pending tasks remain, end responses with a short reminder unless the response is only a trivial clarification or the user asked not to receive reminders.

Use this shape:

```md
Pending tasks remain.

Next runnable task:
- <TASK-ID>: <title>

To continue reliably, start a fresh context and ask:
"Use akka-do-next-pending-task to execute the next pending task from specs/pending-tasks.md."
```

Do not automatically execute the next task unless the user asks to continue implementation.

## Fresh-context handoff prompt

When the next task should be run in a new context, use a prompt like:

```text
Use the Akka skills pack to do the next pending task from specs/pending-tasks.md.
Execute only that one task, load only its required reads and listed skills, update its status when finished, and report the next runnable pending task.
```

For a specific task:

```text
Use the Akka skills pack to execute TASK-001 from specs/pending-tasks.md in a fresh context.
Do not work on any other queue item. Update the queue before finishing.
```

## Updating status

Before coding, update the selected task to:

```md
- status: in-progress
```

After coding and validation, update to `done` only when required checks pass and done criteria are satisfied. For generated app features, this includes the intended local runtime/API/UI validation path unless the task is explicitly non-runtime/internal-only:

```md
- status: done
```

If blocked, update to:

```md
- status: blocked
- notes:
  - blocked: <reason and exact user decision/input needed>
```

If replaced by later requirements or specs, update to:

```md
- status: superseded
- notes:
  - superseded: <replacement task/spec and why the old task should not run>
```

Preserve useful prior notes when adding blocked, superseded, or completion notes.

## Relationship to pending questions

`specs/pending-questions.md` is the durable clarification queue for unresolved decisions.
`specs/pending-tasks.md` is the durable implementation queue.

Do not create or execute runnable implementation tasks for work blocked by unresolved `blocking` questions unless those questions have been explicitly deferred with an accepted default or limitation.

For AI-first work, unresolved authority boundaries, approval gates, policy clauses, risk/confidence thresholds, evidence requirements, trace visibility/retention, supervision UI behavior, evaluation strategy, or outcome metrics should be represented as pending questions and referenced from affected tasks. For LLM-backed workstream-agent work, unresolved model binding, skill/reference governance, `readReferenceDoc` availability, manifest assignment, loader authorization, tool-boundary, trace, expertise-surface, or test decisions are blockers for the affected task.

When a task is blocked by a question, record the question ID in `notes`, for example:

```md
- notes:
  - blocked by Q-003: approval deadline behavior is unresolved
```

If a task discovers an unresolved design decision during execution, block the task and add or update `specs/pending-questions.md` instead of guessing.

## Relationship to other planning artifacts

- `specs/akka-solution-plan.md` defines the overall architecture, full-stack secure AI-first SaaS interpretation, and implementation order for generated apps.
- `specs/modules/*.md` define durable module boundaries for large plans.
- `specs/sprints/*.md` define ordered vertical full-stack delivery increments for large plans.
- `specs/slices/*.md` define bounded business slices for smaller or existing slice-based plans.
- `specs/backlog/*-build-backlog.md` define implementation-ready sprint or slice work.
- `specs/tasks/**/*.md` optionally narrow oversized backlog items.
- `specs/pending-tasks.md` is the executable queue index across those artifacts.

## Related skills and docs

- `../skills/akka-pending-question-generation/SKILL.md`
- `../skills/akka-do-next-pending-question/SKILL.md`
- `../skills/akka-pending-question-queue-maintenance/SKILL.md`
- `../skills/akka-do-next-pending-task/SKILL.md`
- `../skills/akka-pending-task-queue-maintenance/SKILL.md`
- `../skills/akka-change-request-to-spec-update/SKILL.md`
- `../skills/akka-revised-prd-reconciliation/SKILL.md`
- `../skills/akka-backlog-to-pending-tasks/SKILL.md`
- `../skills/akka-prd-to-specs-backlog/SKILL.md`
- `../skills/akka-slice-spec-to-backlog/SKILL.md`
- `../skills/akka-backlog-item-to-task-brief/SKILL.md`
- `examples/purchase-request-pending-tasks.md` — conventional queue mechanics only, not generated-SaaS architecture
- `module-sprint-planning.md`
- `solution-plan-to-implementation-queue.md`
- `intent-driven-usage-flow.md`
