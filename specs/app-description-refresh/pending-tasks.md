# Pending Tasks: App-description Refresh

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected backlog/task entry, selected task brief, and any referenced workstream migration plan before editing.
- Update this file before finishing the harness response.
- Each completed task must make one focused git commit containing the task changes and queue update.
- If the queue status update is included in the same commit, record the commit message in task notes.
- Commit message format: `app-desc-refresh: <short task title>`.
- Do not run queued implementation tasks in parallel; parent orchestration must run one fresh-context subagent at a time.

## Tasks

### TASK-ADR-00-001: Create app-description refresh mini-project

- status: done
- source: user discussion about refreshing older-skills-pack foundation app-description with one umbrella plan plus independent workstream plans
- task brief: specs/app-description-refresh/tasks/00-planning/00-create-refresh-mini-project.md
- depends on: []
- required reads:
  - AGENTS.md
  - app-description/AGENTS.md
  - specs/AGENTS.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - .agents/skills/docs/current-intent-model.md
  - .agents/skills/docs/app-description-component-graph.md
  - current conversation context
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/app-description-refresh/README.md
  - specs/app-description-refresh/conversation-capture.md
  - specs/app-description-refresh/migration-sequence.md
  - specs/app-description-refresh/shared-foundation-audit.md
  - specs/app-description-refresh/workstreams/*.md
  - specs/app-description-refresh/backlog/*.md
  - specs/app-description-refresh/tasks/**/*.md
  - specs/app-description-refresh/pending-tasks.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, done state, backlog, task briefs, per-workstream migration plans, sequential queue, and terminal verification task
  - planning scaffold is committed
- notes:
  - commit message: `app-desc-refresh: add mini-project`
  - vertical contract: planning-only cross-cutting app-description refresh; no runtime/API/UI behavior change; no governed-tool exposure; validation path `git diff --check`

### TASK-ADR-01-001: Audit shared foundation contracts

- status: pending
- source: specs/app-description-refresh/backlog/01-app-description-refresh-build-backlog.md#adr-01-audit-shared-foundation-contracts
- task brief: specs/app-description-refresh/tasks/01-shared/01-audit-shared-foundation.md
- depends on:
  - TASK-ADR-00-001
- required reads:
  - AGENTS.md
  - app-description/AGENTS.md
  - specs/app-description-refresh/README.md
  - specs/app-description-refresh/shared-foundation-audit.md
  - specs/app-description-refresh/tasks/01-shared/01-audit-shared-foundation.md
  - .agents/skills/docs/current-intent-model.md
  - .agents/skills/docs/app-description-component-graph.md
  - .agents/skills/docs/app-description-source-alignment.md
- skills:
  - app-descriptions
  - app-description-change-impact
  - core-saas-foundation
- expected outputs:
  - concrete shared foundation audit findings
  - updated queue notes
- required checks:
  - `git diff --check`
- done criteria:
  - shared refresh gaps are explicit enough for the shared refresh task
  - task changes and queue update are committed
- notes:
  - vertical contract: docs-only shared foundation audit; identifies worker/adapter/tool/capability/AuthContext/trace/runtime-validation gaps without runtime implementation; validation path `git diff --check` plus coverage proof

### TASK-ADR-01-002: Refresh shared foundation app-description artifacts

- status: pending
- source: specs/app-description-refresh/backlog/01-app-description-refresh-build-backlog.md#adr-02-refresh-shared-foundation-app-description-artifacts
- task brief: specs/app-description-refresh/tasks/01-shared/02-refresh-shared-foundation.md
- depends on:
  - TASK-ADR-01-001
- required reads:
  - AGENTS.md
  - app-description/AGENTS.md
  - specs/app-description-refresh/README.md
  - specs/app-description-refresh/shared-foundation-audit.md
  - specs/app-description-refresh/tasks/01-shared/02-refresh-shared-foundation.md
  - .agents/skills/docs/current-intent-model.md
  - .agents/skills/docs/app-description-component-graph.md
  - .agents/skills/docs/app-worker-tool-model.md
- skills:
  - app-descriptions
  - app-description-auth-security
  - app-description-observability
  - app-description-capability-modeling
  - core-saas-foundation
- expected outputs:
  - refreshed shared app/global/domain artifacts
  - shared graph conventions for workstream tasks
- required checks:
  - `git diff --check`
- done criteria:
  - shared artifacts are ready for per-workstream refresh tasks
  - task changes and queue update are committed
- notes:
  - vertical contract: description-only shared foundation refresh; non-attention reason cross-cutting shared definition work; role-specific dashboard / surface none except shared surface patterns; surface graph node/action edge none implemented; governed-tool id/type/exposure shared definitions only; actor adapter/source none implemented but must define `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/internal conventions where applicable; confirmation/approval behavior and idempotency/transaction/result behavior defined as shared semantics only; capability or foundation scope core-starter foundation; AuthContext / roles / tenant scope shared Organization/Tenant conventions; API / frontend / realtime path definition-only; audit/work trace expectation shared trace patterns; validation path `git diff --check` plus graph vocabulary proof

### TASK-ADR-02-001: Refresh My Account workstream app-description

- status: pending
- source: specs/app-description-refresh/workstreams/my-account-migration-plan.md
- task brief: specs/app-description-refresh/tasks/02-workstreams/01-refresh-my-account.md
- depends on:
  - TASK-ADR-01-002
- required reads:
  - specs/app-description-refresh/workstreams/my-account-migration-plan.md
  - specs/app-description-refresh/tasks/02-workstreams/01-refresh-my-account.md
  - app-description/domains/core-starter/workstreams/my-account/**
- skills:
  - app-description-functional-agent-modeling
  - app-description-surface-modeling
  - app-description-capability-modeling
  - app-description-auth-security
  - app-description-test-specification
  - app-description-observability
  - app-description-ui
- expected outputs:
  - refreshed My Account workstream current-intent graph
  - lifecycle/source-alignment update
- required checks:
  - `git diff --check`
- done criteria:
  - My Account graph links worker/adapter/governed-tool/capability/realization/tests/runtime-validation/traces coherently
  - task changes and queue update are committed
- notes:
  - vertical contract: My Account functional-agent workstream; attention category account/profile context and non-attention member self-service status; role-specific dashboard / surface My Account dashboard/profile/context surfaces; surface graph node/action edge account-context/profile read-update actions and result/system-message surfaces; governed-tool id/type/exposure governed account/profile/context tools; actor adapter/source `surface_action`, API call, `human_chat_tool_plan` or `agent_tool_call` only where described; confirmation/approval behavior none unless profile update requires confirmation; idempotency/transaction/result behavior profile/context update result surfaces and no-op semantics described; capability or foundation scope account context/profile and membership context; AuthContext / roles / tenant scope signed-in member tenant/Organization scope; API / frontend / realtime path My Account route/API mappings in realization; audit/work trace expectation context read/update/denial/agent assistance traces; source-alignment and runtime-validation references updated; validation path `git diff --check`

### TASK-ADR-02-002: Refresh User Admin workstream app-description

- status: pending
- source: specs/app-description-refresh/workstreams/user-admin-migration-plan.md
- task brief: specs/app-description-refresh/tasks/02-workstreams/02-refresh-user-admin.md
- depends on:
  - TASK-ADR-02-001
- required reads:
  - specs/app-description-refresh/workstreams/user-admin-migration-plan.md
  - specs/app-description-refresh/tasks/02-workstreams/02-refresh-user-admin.md
  - app-description/domains/core-starter/workstreams/user-admin/**
- skills:
  - app-description-functional-agent-modeling
  - app-description-surface-modeling
  - app-description-capability-modeling
  - app-description-auth-security
  - app-description-test-specification
  - app-description-observability
  - app-description-ui
  - core-saas-foundation
- expected outputs:
  - refreshed User Admin workstream current-intent graph
  - lifecycle/source-alignment update
- required checks:
  - `git diff --check`
- done criteria:
  - User Admin graph links worker/adapter/governed-tool/capability/realization/tests/runtime-validation/traces coherently
  - task changes and queue update are committed
- notes:
  - vertical contract: User Admin functional-agent workstream; attention category invitation/access-review/risky-admin-action; role-specific dashboard / surface User Admin dashboard, user list, user account, invite, access-review, admin-audit surfaces; surface graph node/action edge invite/create, membership/role/access/support actions and result/partial-failure/system-message surfaces; governed-tool id/type/exposure invitation/membership/role/access/admin-audit governed tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, workflow/timer/consumer/API/internal where described; confirmation/approval behavior explicit confirmation for chat plans and risky/last-admin approvals; idempotency/transaction/result behavior invite idempotency, role transaction boundary, partial-failure result surfaces; capability or foundation scope user-and-access-administration; AuthContext / roles / tenant scope admin tenant/Organization scope, last-admin and denials; API / frontend / realtime path User Admin route/API/projection mappings; audit/work trace expectation admin action, invitation, denial, requestedBy/confirmedBy traces; validation path `git diff --check`

### TASK-ADR-02-003: Refresh Agent Admin workstream app-description

- status: pending
- source: specs/app-description-refresh/workstreams/agent-admin-migration-plan.md
- task brief: specs/app-description-refresh/tasks/02-workstreams/03-refresh-agent-admin.md
- depends on:
  - TASK-ADR-02-002
- required reads:
  - specs/app-description-refresh/workstreams/agent-admin-migration-plan.md
  - specs/app-description-refresh/tasks/02-workstreams/03-refresh-agent-admin.md
  - app-description/domains/core-starter/workstreams/agent-admin/**
- skills:
  - app-description-functional-agent-modeling
  - app-description-surface-modeling
  - app-description-capability-modeling
  - app-description-auth-security
  - app-description-test-specification
  - app-description-observability
  - app-description-ui
  - akka-agent-behavior-profiles
  - akka-agent-prompt-governance
  - akka-agent-skill-governance
  - akka-agent-reference-governance
  - akka-agent-tool-boundaries
- expected outputs:
  - refreshed Agent Admin workstream current-intent graph
  - lifecycle/source-alignment update
- required checks:
  - `git diff --check`
- done criteria:
  - Agent Admin graph links worker/adapter/governed-tool/capability/realization/tests/runtime-validation/traces coherently
  - task changes and queue update are committed
- notes:
  - vertical contract: Agent Admin functional-agent workstream; attention category behavior-change proposal, approval, provider/config blocker, denied loader/tool-boundary event; role-specific dashboard / surface Agent Admin catalog/detail/governance/test-console/proposal surfaces; surface graph node/action edge behavior profile, prompt/skill/reference/manifest/tool-boundary/test-console actions and result/partial-failure/system-message surfaces; governed-tool id/type/exposure AgentDefinition/PromptDocument/SkillDocument/ReferenceDocument/manifest/tool-boundary/model policy/test-console governed tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, internal runtime loader/API where described; confirmation/approval behavior approval-required authority expansion and explicit chat confirmation; idempotency/transaction/result behavior draft/proposal/version activation boundaries and partial-failure surfaces; capability or foundation scope managed-agent governance; AuthContext / roles / tenant scope SaaS admin tenant scope, provider secret boundary and denials; API / frontend / realtime path Agent Admin route/API/runtime loader mappings; audit/work trace expectation PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, provider fail-closed and loader denial traces; validation path `git diff --check`

### TASK-ADR-02-004: Refresh Governance/Policy workstream app-description

- status: pending
- source: specs/app-description-refresh/workstreams/governance-policy-migration-plan.md
- task brief: specs/app-description-refresh/tasks/02-workstreams/04-refresh-governance-policy.md
- depends on:
  - TASK-ADR-02-003
- required reads:
  - specs/app-description-refresh/workstreams/governance-policy-migration-plan.md
  - specs/app-description-refresh/tasks/02-workstreams/04-refresh-governance-policy.md
  - app-description/domains/core-starter/workstreams/governance-policy/**
- skills:
  - app-description-functional-agent-modeling
  - app-description-surface-modeling
  - app-description-capability-modeling
  - app-description-auth-security
  - app-description-test-specification
  - app-description-observability
  - app-description-ui
  - ai-first-saas-policy-governance
  - ai-first-saas-decision-cards
- expected outputs:
  - refreshed Governance/Policy workstream current-intent graph
  - lifecycle/source-alignment update
- required checks:
  - `git diff --check`
- done criteria:
  - Governance/Policy graph links worker/adapter/governed-tool/capability/realization/tests/runtime-validation/traces coherently
  - task changes and queue update are committed
- notes:
  - vertical contract: Governance/Policy functional-agent workstream; attention category policy approval, exception, simulation finding, rollback decision; role-specific dashboard / surface Governance/Policy dashboard, policy catalog/detail/draft/simulation/decision surfaces; surface graph node/action edge policy draft/simulate/approve/activate/rollback/exception actions and result/partial-failure/system-message surfaces; governed-tool id/type/exposure policy draft/simulate/approve/activate/rollback/exception governed tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, workflow/internal/API where described; confirmation/approval behavior human approval and decision-card requirements; idempotency/transaction/result behavior policy version activation/rollback transaction boundaries and result surfaces; capability or foundation scope governance-policy lifecycle; AuthContext / roles / tenant scope admin/policy operator tenant scope and denials; API / frontend / realtime path Governance/Policy route/API mappings; audit/work trace expectation policy change, decision, simulation, exception, denial, rollback traces; validation path `git diff --check`

### TASK-ADR-02-005: Refresh Audit/Trace workstream app-description

- status: pending
- source: specs/app-description-refresh/workstreams/audit-trace-migration-plan.md
- task brief: specs/app-description-refresh/tasks/02-workstreams/05-refresh-audit-trace.md
- depends on:
  - TASK-ADR-02-004
- required reads:
  - specs/app-description-refresh/workstreams/audit-trace-migration-plan.md
  - specs/app-description-refresh/tasks/02-workstreams/05-refresh-audit-trace.md
  - app-description/domains/core-starter/workstreams/audit-trace/**
- skills:
  - app-description-functional-agent-modeling
  - app-description-surface-modeling
  - app-description-capability-modeling
  - app-description-auth-security
  - app-description-test-specification
  - app-description-observability
  - app-description-ui
  - ai-first-saas-audit-trace
  - akka-agent-work-trace
- expected outputs:
  - refreshed Audit/Trace workstream current-intent graph
  - lifecycle/source-alignment update
- required checks:
  - `git diff --check`
- done criteria:
  - Audit/Trace graph links worker/adapter/governed-tool/capability/realization/tests/runtime-validation/traces coherently
  - task changes and queue update are committed
- notes:
  - vertical contract: Audit/Trace functional-agent workstream; attention category investigation, denial, trace gap, support-access review; role-specific dashboard / surface Audit/Trace search/detail/timeline/correlation/investigation surfaces; surface graph node/action edge trace search/read/correlation/summary/export-if-allowed actions and result/redacted/denied surfaces; governed-tool id/type/exposure audit/work trace search/read/correlation/denial investigation tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, projection/consumer/internal/API where described; confirmation/approval behavior read-only chat confirmation when needed and export/support-access approval where allowed; idempotency/transaction/result behavior read-only idempotency, redacted result surfaces, denied result surfaces; capability or foundation scope audit-and-trace investigation; AuthContext / roles / tenant scope tenant admin/SaaS support/support-access scope and denials; API / frontend / realtime path Audit/Trace route/API/projection mappings; audit/work trace expectation trace reads, denied trace access, investigation summaries, cross-workstream correlations; validation path `git diff --check`

### TASK-ADR-03-001: Cross-workstream consistency and readiness pass

- status: pending
- source: specs/app-description-refresh/backlog/01-app-description-refresh-build-backlog.md#adr-08-cross-workstream-consistency-and-readiness-pass
- task brief: specs/app-description-refresh/tasks/03-consistency/01-cross-workstream-consistency-readiness.md
- depends on:
  - TASK-ADR-02-005
- required reads:
  - specs/app-description-refresh/tasks/03-consistency/01-cross-workstream-consistency-readiness.md
  - specs/app-description-refresh/workstreams/*.md
  - refreshed app-description/**
- skills:
  - app-description-change-impact
  - app-description-readiness-assessment
  - app-description-readiness-summary
  - app-descriptions
- expected outputs:
  - specs/app-description-refresh/consistency-readiness-review.md
  - small consistency fixes or appended follow-up tasks/questions
- required checks:
  - `git diff --check`
- done criteria:
  - cross-workstream consistency and readiness are assessed
  - material gaps are fixed, queued, or blocked
  - task changes and queue update are committed
- notes:
  - vertical contract: all five foundation workstreams; description/readiness review only; non-attention reason cross-workstream verification; role-specific dashboard / surface all refreshed dashboard/surface bindings; surface graph node/action edge reviewed but not implemented; governed-tool id/type/exposure all refreshed governed tools reviewed; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, workflow/timer/consumer/API/MCP/internal consistency reviewed; confirmation/approval behavior and idempotency/transaction/result behavior consistency reviewed; capability or foundation scope all core-starter capabilities; AuthContext / roles / tenant scope reviewed; API / frontend / realtime path realization mappings reviewed; audit/work trace expectation trace/source-alignment/runtime-validation consistency reviewed; validation path `git diff --check` plus graph proof

### TASK-ADR-99-001: Terminal app-description refresh verification

- status: pending
- source: mini-project verification loop
- task brief: specs/app-description-refresh/tasks/99-verification/01-terminal-verification.md
- depends on:
  - TASK-ADR-03-001
- required reads:
  - specs/app-description-refresh/README.md
  - specs/app-description-refresh/conversation-capture.md
  - specs/app-description-refresh/pending-tasks.md
  - specs/app-description-refresh/tasks/99-verification/01-terminal-verification.md
  - refreshed app-description/**
- skills:
  - app-description-readiness-assessment
  - app-description-readiness-summary
  - akka-pending-task-queue-maintenance
- expected outputs:
  - specs/app-description-refresh/terminal-verification.md
  - queue completion notes or appended follow-up tasks plus new terminal verification task
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project done state is verified, or follow-up bounded tasks are appended before declaring incomplete
  - task changes and queue update are committed
- notes:
  - vertical contract: terminal verification across all five foundation workstreams; non-runtime; non-attention reason terminal verification; role-specific dashboard / surface all refreshed surfaces verified; surface graph node/action edge verified; governed-tool id/type/exposure verified; actor adapter/source verified; confirmation/approval behavior and idempotency/transaction/result behavior verified; capability or foundation scope all core-starter capabilities; AuthContext / roles / tenant scope verified; API / frontend / realtime path realization references verified; audit/work trace expectation verifies app-description graph, source-alignment, lifecycle, runtime-validation references, trace obligations, and queue state; validation path `git diff --check` plus graph/queue proof
