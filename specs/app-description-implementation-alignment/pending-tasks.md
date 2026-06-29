# Pending Tasks: App-description Implementation Alignment

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, selected backlog/task entry, selected task brief, and any referenced workstream alignment plan before editing.
- Update this file before finishing the harness response.
- Each completed task must make one focused git commit containing the task changes and queue update.
- If the queue status update is included in the same commit, record the commit message in task notes.
- Commit message format: `app-desc-align: <short task title>`.
- Do not run queued implementation/alignment tasks in parallel; parent orchestration must run one fresh-context subagent at a time.

## Tasks

### TASK-ADIA-00-001: Create implementation alignment mini-project

- status: done
- source: user request after completed app-description refresh terminal verification
- task brief: specs/app-description-implementation-alignment/tasks/00-planning/00-create-mini-project.md
- depends on: []
- required reads:
  - AGENTS.md
  - specs/app-description-refresh/terminal-verification.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/app-description-implementation-alignment/README.md
  - specs/app-description-implementation-alignment/conversation-capture.md
  - specs/app-description-implementation-alignment/alignment-sequence.md
  - specs/app-description-implementation-alignment/source-evidence-inventory.md
  - specs/app-description-implementation-alignment/runtime-validation-corpus-plan.md
  - specs/app-description-implementation-alignment/implementation-follow-up-queue.md
  - specs/app-description-implementation-alignment/workstreams/*.md
  - specs/app-description-implementation-alignment/backlog/*.md
  - specs/app-description-implementation-alignment/tasks/**/*.md
  - specs/app-description-implementation-alignment/pending-tasks.md
- required checks:
  - `git diff --check`
  - `bash .agents/skills/tools/validate-pending-task-workstream-contract.sh specs/app-description-implementation-alignment/pending-tasks.md`
- done criteria:
  - mini-project has captured rationale, done state, workstream alignment plans, backlog, task briefs, sequential queue, and terminal verification task
  - planning scaffold is committed
- notes:
  - commit message: `app-desc-align: add mini-project`
  - vertical contract: planning-only cross-cutting app-description implementation alignment; non-attention reason mini-project scaffold; role-specific dashboard / surface none; surface graph node/action edge none; governed-tool id/type/exposure none; actor adapter/source none; confirmation/approval behavior none; idempotency/transaction/result behavior none; capability or foundation scope app-description implementation alignment; AuthContext / roles / tenant scope preserved as planning constraints; API / frontend / realtime path none implemented; audit/work trace expectation none at runtime; validation path `git diff --check` and pending-task validator

### TASK-ADIA-01-001: Inventory current source/runtime evidence

- status: done
- source: specs/app-description-implementation-alignment/backlog/01-implementation-alignment-build-backlog.md#adia-01-inventory-sourceruntime-evidence
- task brief: specs/app-description-implementation-alignment/tasks/01-evidence/01-inventory-source-evidence.md
- depends on:
  - TASK-ADIA-00-001
- required reads:
  - AGENTS.md
  - app-description/AGENTS.md
  - specs/app-description-refresh/terminal-verification.md
  - specs/app-description-implementation-alignment/README.md
  - specs/app-description-implementation-alignment/source-evidence-inventory.md
  - specs/app-description-implementation-alignment/tasks/01-evidence/01-inventory-source-evidence.md
  - .agents/skills/docs/app-description-source-alignment.md
  - .agents/skills/docs/intent-to-realization-flow.md
- skills:
  - app-description-change-impact
  - akka-solution-decomposition
- expected outputs:
  - concrete source evidence inventory and gap classification by workstream
  - updated queue notes
- required checks:
  - `git diff --check`
- done criteria:
  - future workstream alignment tasks can proceed without guessing source/test/frontend evidence
  - task changes and queue update are committed
- notes:
  - commit message: `app-desc-align: inventory source evidence`
  - validation: `git diff --check` passed; evidence proof commands inventoried backend, tests, frontend, app-description source-alignment files, resources/config, and absent `specs/runtime-validation` state.
  - vertical contract: cross-workstream evidence inventory; non-attention reason source-alignment audit; role-specific dashboard / surface all foundation surfaces inspected as evidence only; surface graph node/action edge inspected not implemented; governed-tool id/type/exposure inspected from app-description/source evidence; actor adapter/source inspected; confirmation/approval behavior and idempotency/transaction/result behavior inspected; capability or foundation scope all core-starter capabilities; AuthContext / roles / tenant scope inspected; API / frontend / realtime path inventoried; audit/work trace expectation inventoried; validation path `git diff --check` plus evidence proof

### TASK-ADIA-01-002: Author runtime-validation corpus scaffold

- status: done
- source: specs/app-description-implementation-alignment/backlog/01-implementation-alignment-build-backlog.md#adia-02-author-runtime-validation-corpus-scaffold
- task brief: specs/app-description-implementation-alignment/tasks/01-evidence/02-author-runtime-validation-corpus.md
- depends on:
  - TASK-ADIA-01-001
- required reads:
  - AGENTS.md
  - specs/app-description-implementation-alignment/runtime-validation-corpus-plan.md
  - specs/app-description-implementation-alignment/source-evidence-inventory.md
  - specs/app-description-implementation-alignment/tasks/01-evidence/02-author-runtime-validation-corpus.md
  - .agents/skills/docs/runtime-validation.md
  - .agents/skills/docs/runtime-validation-task-authoring.md
- skills:
  - akka-runtime-feature-verification
  - app-description-test-specification
- expected outputs:
  - specs/runtime-validation/** scenario corpus scaffold
  - updated runtime-validation-corpus-plan.md
- required checks:
  - `git diff --check`
- done criteria:
  - runtime-validation scenario skeletons exist for the five foundation workstreams without claiming pass results
  - task changes and queue update are committed
- notes:
  - commit message: `app-desc-align: author runtime validation corpus`
  - validation: `git diff --check` passed; `find specs/runtime-validation -type f | sort` listed the authored corpus files.
  - vertical contract: all five foundation workstreams; attention category scenario-dependent; role-specific dashboard / surface scenario files name target surfaces; surface graph node/action edge documented per scenario; governed-tool id/type/exposure documented per scenario; actor adapter/source documented; confirmation/approval behavior and idempotency/transaction/result behavior documented as expected results; capability or foundation scope all core-starter capabilities; AuthContext / roles / tenant scope documented per persona; API / frontend / realtime path documented; audit/work trace expectation documented; validation path `git diff --check` and scenario file proof

### TASK-ADIA-02-001: Align My Account implementation evidence

- status: done
- source: specs/app-description-implementation-alignment/workstreams/my-account-alignment-plan.md
- task brief: specs/app-description-implementation-alignment/tasks/02-workstreams/01-align-my-account.md
- depends on:
  - TASK-ADIA-01-002
- required reads:
  - specs/app-description-implementation-alignment/workstreams/my-account-alignment-plan.md
  - specs/app-description-implementation-alignment/source-evidence-inventory.md
  - specs/app-description-implementation-alignment/tasks/02-workstreams/01-align-my-account.md
  - app-description/domains/core-starter/workstreams/my-account/**
- skills:
  - app-description-change-impact
  - akka-http-endpoints
  - akka-web-ui-apps
  - akka-runtime-feature-verification
- expected outputs:
  - updated My Account lifecycle/source-alignment evidence or exact follow-up tasks
- required checks:
  - `git diff --check`
- done criteria:
  - My Account alignment posture is recorded without overclaiming runtime readiness
  - task changes and queue update are committed
- notes:
  - commit message: `app-desc-align: align my account evidence`
  - validation: `git diff --check` passed; mapped evidence proof verified 19 My Account implementation/test/frontend paths and `specs/runtime-validation/scenarios/my-account/RV-MY-ACCOUNT-001-login-and-account-context.md`.
  - result: lifecycle/source-alignment updated to `partially-aligned` at source-evidence level without runtime-ready, manual-ready, or provider-success claims.
  - vertical contract: My Account functional-agent workstream; attention category account/profile context; role-specific dashboard / surface My Account dashboard/profile/context; surface graph node/action edge account/profile/context read-update and result surfaces; governed-tool id/type/exposure account/profile/context tools; actor adapter/source `surface_action`, `api_call`, bounded `human_chat_tool_plan`, read/advisory `agent_tool_call`; confirmation/approval behavior profile/chat confirmation where applicable; idempotency/transaction/result behavior profile/context no-op/result surfaces; capability or foundation scope account-context-and-profile; AuthContext / roles / tenant scope signed-in member tenant/Organization scope; API / frontend / realtime path My Account route/API mappings; audit/work trace expectation account/context read-update/denial/agent-assistance traces; validation path `git diff --check` plus mapped evidence proof

### TASK-ADIA-02-002: Align User Admin implementation evidence

- status: done
- source: specs/app-description-implementation-alignment/workstreams/user-admin-alignment-plan.md
- task brief: specs/app-description-implementation-alignment/tasks/02-workstreams/02-align-user-admin.md
- depends on:
  - TASK-ADIA-02-001
- required reads:
  - specs/app-description-implementation-alignment/workstreams/user-admin-alignment-plan.md
  - specs/app-description-implementation-alignment/source-evidence-inventory.md
  - specs/app-description-implementation-alignment/tasks/02-workstreams/02-align-user-admin.md
  - app-description/domains/core-starter/workstreams/user-admin/**
- skills:
  - app-description-change-impact
  - akka-basic-user-admin
  - akka-saas-invitation-onboarding
  - akka-runtime-feature-verification
- expected outputs:
  - updated User Admin lifecycle/source-alignment evidence or exact follow-up tasks
- required checks:
  - `git diff --check`
- done criteria:
  - User Admin alignment posture is recorded without overclaiming runtime readiness
  - task changes and queue update are committed
- notes:
  - commit message: `app-desc-align: align user admin evidence`
  - validation: `git diff --check` passed; mapped evidence proof verified 30 User Admin implementation/test/frontend paths and `specs/runtime-validation/scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md`.
  - result: lifecycle/source-alignment updated to `partially-aligned` at source-evidence level; implementation follow-up queue now records exact runtime-validation, provider, auth, and scenario-coverage follow-ups without runtime-ready, manual-ready, live Resend, model-provider, or WorkOS/AuthKit success claims.
  - vertical contract: User Admin functional-agent workstream; attention category invitation/access-review/risky-admin-action; role-specific dashboard / surface User Admin dashboard/user list/user detail/invite/access-review/admin-audit; surface graph node/action edge invite, membership, role, support-access, access-review result surfaces; governed-tool id/type/exposure invitation/membership/role/access/admin-audit tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/workflow/timer/consumer/internal; confirmation/approval behavior chat confirmation, last-admin/risky approvals; idempotency/transaction/result behavior invitation idempotency and role/support transaction/result/partial-failure surfaces; capability or foundation scope user-and-access-administration; AuthContext / roles / tenant scope admin tenant/Organization scope; API / frontend / realtime path User Admin route/API/projection mappings; audit/work trace expectation admin action, invitation, denial, requestedBy/confirmedBy traces; validation path `git diff --check` plus mapped evidence proof

### TASK-ADIA-02-003: Align Agent Admin implementation evidence

- status: done
- source: specs/app-description-implementation-alignment/workstreams/agent-admin-alignment-plan.md
- task brief: specs/app-description-implementation-alignment/tasks/02-workstreams/03-align-agent-admin.md
- depends on:
  - TASK-ADIA-02-002
- required reads:
  - specs/app-description-implementation-alignment/workstreams/agent-admin-alignment-plan.md
  - specs/app-description-implementation-alignment/source-evidence-inventory.md
  - specs/app-description-implementation-alignment/tasks/02-workstreams/03-align-agent-admin.md
  - app-description/domains/core-starter/workstreams/agent-admin/**
- skills:
  - app-description-change-impact
  - akka-agents
  - akka-agent-behavior-profiles
  - akka-agent-prompt-governance
  - akka-agent-skill-governance
  - akka-agent-reference-governance
  - akka-agent-tool-boundaries
  - akka-runtime-feature-verification
- expected outputs:
  - updated Agent Admin lifecycle/source-alignment evidence or exact follow-up tasks
- required checks:
  - `git diff --check`
- done criteria:
  - Agent Admin alignment posture is recorded without overclaiming runtime readiness
  - task changes and queue update are committed
- notes:
  - commit message: `app-desc-align: align agent admin evidence`
  - validation: `git diff --check` passed; mapped evidence proof verified 20 Agent Admin implementation/domain source files, 8 Agent Admin backend tests, 34 foundation agent source files, 18 foundation agent tests, `frontend/src/workstream-agent-admin-vertical.contract.test.mjs`, and `specs/runtime-validation/scenarios/agent-admin/RV-AGENT-ADMIN-001-provider-fail-closed-test-console.md`.
  - result: lifecycle/source-alignment updated to `partially-aligned` at source-evidence level; implementation follow-up queue now records exact runtime-validation, canonical-id/surface, dashboard-attention, chat-plan/idempotency/partial-failure, and trace-visibility follow-ups without runtime-ready, manual-ready, live provider, or provider-secret-boundary runtime claims.
  - vertical contract: Agent Admin functional-agent workstream; attention category behavior-change proposal/provider-config/loader-denial; role-specific dashboard / surface Agent Admin catalog/detail/governance/test-console/proposal; surface graph node/action edge AgentDefinition/PromptDocument/SkillDocument/ReferenceDocument/manifest/tool-boundary/model/test-console actions and results; governed-tool id/type/exposure managed-agent governance tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/internal runtime loader; confirmation/approval behavior authority-expansion approval and chat confirmation; idempotency/transaction/result behavior draft/proposal/version activation/result/partial-failure surfaces; capability or foundation scope managed-agent governance; AuthContext / roles / tenant scope SaaS admin tenant scope and provider secret boundary; API / frontend / realtime path Agent Admin route/API/runtime-loader mappings; audit/work trace expectation PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, provider fail-closed and loader denial traces; validation path `git diff --check` plus mapped evidence proof

### TASK-ADIA-02-004: Align Governance/Policy implementation evidence

- status: pending
- source: specs/app-description-implementation-alignment/workstreams/governance-policy-alignment-plan.md
- task brief: specs/app-description-implementation-alignment/tasks/02-workstreams/04-align-governance-policy.md
- depends on:
  - TASK-ADIA-02-003
- required reads:
  - specs/app-description-implementation-alignment/workstreams/governance-policy-alignment-plan.md
  - specs/app-description-implementation-alignment/source-evidence-inventory.md
  - specs/app-description-implementation-alignment/tasks/02-workstreams/04-align-governance-policy.md
  - app-description/domains/core-starter/workstreams/governance-policy/**
- skills:
  - app-description-change-impact
  - ai-first-saas-policy-governance
  - ai-first-saas-decision-cards
  - akka-runtime-feature-verification
- expected outputs:
  - updated Governance/Policy lifecycle/source-alignment evidence or exact follow-up tasks
- required checks:
  - `git diff --check`
- done criteria:
  - Governance/Policy alignment posture is recorded without overclaiming runtime readiness
  - task changes and queue update are committed
- notes:
  - vertical contract: Governance/Policy functional-agent workstream; attention category policy approval/exception/simulation/rollback; role-specific dashboard / surface Governance/Policy dashboard/catalog/detail/draft/simulation/decision; surface graph node/action edge policy draft/simulate/approve/activate/rollback/exception results; governed-tool id/type/exposure policy lifecycle tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/workflow/internal; confirmation/approval behavior human approval/decision cards; idempotency/transaction/result behavior policy version activation/rollback transaction and partial-failure surfaces; capability or foundation scope governance-policy-lifecycle; AuthContext / roles / tenant scope admin/policy operator tenant scope; API / frontend / realtime path Governance/Policy route/API mappings; audit/work trace expectation policy change/decision/simulation/exception/denial/rollback traces; validation path `git diff --check` plus mapped evidence proof

### TASK-ADIA-02-005: Align Audit/Trace implementation evidence

- status: pending
- source: specs/app-description-implementation-alignment/workstreams/audit-trace-alignment-plan.md
- task brief: specs/app-description-implementation-alignment/tasks/02-workstreams/05-align-audit-trace.md
- depends on:
  - TASK-ADIA-02-004
- required reads:
  - specs/app-description-implementation-alignment/workstreams/audit-trace-alignment-plan.md
  - specs/app-description-implementation-alignment/source-evidence-inventory.md
  - specs/app-description-implementation-alignment/tasks/02-workstreams/05-align-audit-trace.md
  - app-description/domains/core-starter/workstreams/audit-trace/**
- skills:
  - app-description-change-impact
  - ai-first-saas-audit-trace
  - akka-agent-work-trace
  - akka-runtime-feature-verification
- expected outputs:
  - updated Audit/Trace lifecycle/source-alignment evidence or exact follow-up tasks
- required checks:
  - `git diff --check`
- done criteria:
  - Audit/Trace alignment posture is recorded without overclaiming runtime readiness
  - task changes and queue update are committed
- notes:
  - vertical contract: Audit/Trace functional-agent workstream; attention category investigation/denial/trace-gap/support-access; role-specific dashboard / surface Audit/Trace search/detail/timeline/correlation/investigation; surface graph node/action edge trace search/read/correlation/summary/export-if-allowed results; governed-tool id/type/exposure audit/work trace investigation tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/projection/consumer/internal; confirmation/approval behavior read-only chat confirmation and export/support-access approval where allowed; idempotency/transaction/result behavior read-only idempotency, redacted and denied result surfaces; capability or foundation scope audit-and-trace-investigation; AuthContext / roles / tenant scope tenant admin/SaaS support/support-access; API / frontend / realtime path Audit/Trace route/API/projection mappings; audit/work trace expectation trace reads, denied trace access, investigation summaries and correlations; validation path `git diff --check` plus mapped evidence proof

### TASK-ADIA-03-001: Consolidate build/compile/runtime-validation follow-up queue

- status: pending
- source: specs/app-description-implementation-alignment/backlog/01-implementation-alignment-build-backlog.md#adia-08-consolidate-buildcompileruntime-validation-follow-up-queue
- task brief: specs/app-description-implementation-alignment/tasks/03-consolidation/01-consolidate-follow-up-queue.md
- depends on:
  - TASK-ADIA-02-005
- required reads:
  - specs/app-description-implementation-alignment/README.md
  - specs/app-description-implementation-alignment/source-evidence-inventory.md
  - specs/app-description-implementation-alignment/runtime-validation-corpus-plan.md
  - specs/app-description-implementation-alignment/implementation-follow-up-queue.md
  - specs/app-description-implementation-alignment/tasks/03-consolidation/01-consolidate-follow-up-queue.md
  - .agents/skills/docs/pending-task-queue.md
- skills:
  - akka-backlog-to-pending-tasks
  - akka-backlog-item-to-task-brief
  - akka-runtime-feature-verification
- expected outputs:
  - consolidated executable follow-up queue or task briefs for build/compile/runtime-validation remediation
- required checks:
  - `git diff --check`
- done criteria:
  - follow-up work is ordered, bounded, and executable one task per fresh context
  - task changes and queue update are committed
- notes:
  - vertical contract: cross-workstream follow-up queue consolidation; non-attention reason planning/queue authoring; role-specific dashboard / surface inherited per generated task; surface graph node/action edge inherited per generated task; governed-tool id/type/exposure inherited per generated task; actor adapter/source inherited per generated task; confirmation/approval behavior and idempotency/transaction/result behavior inherited per generated task; capability or foundation scope all core-starter capabilities; AuthContext / roles / tenant scope inherited per generated task; API / frontend / realtime path inherited per generated task; audit/work trace expectation inherited per generated task; validation path `git diff --check` plus queue validator when applicable

### TASK-ADIA-99-001: Terminal implementation alignment verification

- status: pending
- source: mini-project verification loop
- task brief: specs/app-description-implementation-alignment/tasks/99-verification/01-terminal-verification.md
- depends on:
  - TASK-ADIA-03-001
- required reads:
  - specs/app-description-implementation-alignment/README.md
  - specs/app-description-implementation-alignment/conversation-capture.md
  - specs/app-description-implementation-alignment/alignment-sequence.md
  - specs/app-description-implementation-alignment/pending-tasks.md
  - specs/app-description-implementation-alignment/tasks/99-verification/01-terminal-verification.md
  - specs/app-description-implementation-alignment/source-evidence-inventory.md
  - specs/app-description-implementation-alignment/runtime-validation-corpus-plan.md
  - specs/app-description-implementation-alignment/implementation-follow-up-queue.md
- skills:
  - app-description-readiness-assessment
  - akka-pending-task-queue-maintenance
  - akka-runtime-feature-verification
- expected outputs:
  - specs/app-description-implementation-alignment/terminal-verification.md
  - queue completion notes or appended follow-up tasks plus new terminal verification task
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project done state is verified, or follow-up bounded tasks are appended before declaring incomplete
  - task changes and queue update are committed
- notes:
  - vertical contract: terminal verification across all five foundation workstreams; non-attention reason verification; role-specific dashboard / surface all foundation surfaces verified for evidence posture; surface graph node/action edge verified; governed-tool id/type/exposure verified; actor adapter/source verified; confirmation/approval behavior and idempotency/transaction/result behavior verified; capability or foundation scope all core-starter capabilities; AuthContext / roles / tenant scope verified; API / frontend / realtime path verified; audit/work trace expectation verifies source-alignment, runtime-validation, trace, and queue evidence; validation path `git diff --check` plus proof commands
