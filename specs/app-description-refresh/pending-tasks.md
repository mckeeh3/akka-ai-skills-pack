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

- status: done
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
  - commit message: `app-desc-refresh: audit shared foundation contracts`
  - audit complete: `specs/app-description-refresh/shared-foundation-audit.md` now records concrete high/medium/low findings for shared worker, adapter, governed-tool, capability, AuthContext, trace, source-alignment, and runtime-validation refresh gaps, plus coverage proof and follow-up questions for TASK-ADR-01-002.
  - vertical contract: docs-only shared foundation audit; identifies worker/adapter/tool/capability/AuthContext/trace/runtime-validation gaps without runtime implementation; validation path `git diff --check` plus coverage proof

### TASK-ADR-01-002: Refresh shared foundation app-description artifacts

- status: done
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
  - commit message: `app-desc-refresh: refresh shared foundation`
  - shared refresh complete: app/domain/global/capability/data-state artifacts now define the worker -> execution harness -> actor adapter -> governed tool -> capability -> realization/test/runtime-validation -> trace chain; shared worker contracts and adapter vocabulary; canonical governed-tool id/alias, current/deferred tool scope, confirmation/approval, idempotency, transaction, result, denial, and runtime-validation semantics; Organization/Tenant/Customer `AuthContext` conventions; capability/data-state ownership links; source-alignment default `stale-description-changed`; and Audit/Trace 90-day activity-log retention with deferred export/note/summary ids held non-current.
  - vertical contract: description-only shared foundation refresh; non-attention reason cross-cutting shared definition work; role-specific dashboard / surface none except shared surface patterns; surface graph node/action edge none implemented; governed-tool id/type/exposure shared definitions only; actor adapter/source none implemented but must define `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API/internal conventions where applicable; confirmation/approval behavior and idempotency/transaction/result behavior defined as shared semantics only; capability or foundation scope core-starter foundation; AuthContext / roles / tenant scope shared Organization/Tenant conventions; API / frontend / realtime path definition-only; audit/work trace expectation shared trace patterns; validation path `git diff --check` plus graph vocabulary proof

### TASK-ADR-02-001: Refresh My Account workstream app-description

- status: done
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
  - commit message: `app-desc-refresh: refresh my account workstream`
  - graph coverage proof: My Account workstream files now link signed-in member / functional-agent / system workers through `surface_action`, `api_call`, bounded `human_chat_tool_plan`, and described read/advisory `agent_tool_call` adapters to shared account/profile/context governed tools, capability `account-context-and-profile`, dashboard/profile/settings/context/open-denied surfaces, realization API/frontend mappings, runtime-validation references, and account/context read-update/denial/agent-assistance traces; lifecycle/source-alignment is `stale-description-changed` because this docs-only refresh postdates prior automated evidence.
  - vertical contract: My Account functional-agent workstream; attention category account/profile context and non-attention member self-service status; role-specific dashboard / surface My Account dashboard/profile/context surfaces; surface graph node/action edge account-context/profile read-update actions and result/system-message surfaces; governed-tool id/type/exposure governed account/profile/context tools; actor adapter/source `surface_action`, API call, `human_chat_tool_plan` or `agent_tool_call` only where described; confirmation/approval behavior none unless profile update requires confirmation; idempotency/transaction/result behavior profile/context update result surfaces and no-op semantics described; capability or foundation scope account context/profile and membership context; AuthContext / roles / tenant scope signed-in member tenant/Organization scope; API / frontend / realtime path My Account route/API mappings in realization; audit/work trace expectation context read/update/denial/agent assistance traces; source-alignment and runtime-validation references updated; validation path `git diff --check`

### TASK-ADR-02-002: Refresh User Admin workstream app-description

- status: done
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
  - commit message: `app-desc-refresh: refresh user admin workstream`
  - graph coverage proof: User Admin workstream files now link SaaS Owner/Admin, Organization Admin, Customer Admin, User Admin functional-agent, access-review, invitation-onboarding, and admin-audit/projection workers through `surface_action`, `human_chat_tool_plan`, read-only `agent_tool_call`, protected API, workflow/internal, consumer, and timer adapters to shared invitation, membership/status, role/capability, support-access, access-review, and `admin.audit.read` governed tools; capability `user-and-access-administration`; refreshed User Admin dashboard/user/invite/access-review/admin-audit surfaces and action/result/system-message semantics; frontend/API/source-alignment realization maps; runtime-validation expectations; and requestedBy/confirmedBy audit/work trace obligations. Lifecycle/source-alignment is `stale-description-changed` because this docs-only refresh changed current intent without source/runtime validation.
  - vertical contract: User Admin functional-agent workstream; attention category invitation/access-review/risky-admin-action; role-specific dashboard / surface User Admin dashboard, user list, user account, invite, access-review, admin-audit surfaces; surface graph node/action edge invite/create, membership/role/access/support actions and result/partial-failure/system-message surfaces; governed-tool id/type/exposure invitation/membership/role/access/admin-audit governed tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, workflow/timer/consumer/API/internal where described; confirmation/approval behavior explicit confirmation for chat plans and risky/last-admin approvals; idempotency/transaction/result behavior invite idempotency, role transaction boundary, partial-failure result surfaces; capability or foundation scope user-and-access-administration; AuthContext / roles / tenant scope admin tenant/Organization scope, last-admin and denials; API / frontend / realtime path User Admin route/API/projection mappings; audit/work trace expectation admin action, invitation, denial, requestedBy/confirmedBy traces; validation path `git diff --check`

### TASK-ADR-02-003: Refresh Agent Admin workstream app-description

- status: done
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
  - commit message: `app-desc-refresh: refresh agent admin workstream`
  - graph coverage proof: Agent Admin workstream files now link SaaS admin human, Agent Admin functional agent, behavior-editor internal agent, and runtime system worker through `surface_action`, confirmed `human_chat_tool_plan`, bounded `agent_tool_call`, API, and internal runtime loader adapters to canonical AgentDefinition/PromptDocument/SkillDocument/ReferenceDocument/manifest/model-policy/tool-boundary/test-console/trace governed tools; managed-agent governance capability scope; refreshed dashboard/catalog/detail/governance/test-console/proposal/result/system-message surfaces; realization API/frontend/Akka/source-alignment maps; runtime-validation scenarios; and PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, provider fail-closed, loader-denial, tool-boundary-denial, idempotency, and partial-failure trace obligations. Lifecycle/source-alignment is `stale-description-changed` because this docs-only refresh changed current intent without runtime/API/UI validation.
  - vertical contract: Agent Admin functional-agent workstream; attention category behavior-change proposal, approval, provider/config blocker, denied loader/tool-boundary event; role-specific dashboard / surface Agent Admin catalog/detail/governance/test-console/proposal surfaces; surface graph node/action edge behavior profile, prompt/skill/reference/manifest/tool-boundary/test-console actions and result/partial-failure/system-message surfaces; governed-tool id/type/exposure AgentDefinition/PromptDocument/SkillDocument/ReferenceDocument/manifest/tool-boundary/model policy/test-console governed tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, internal runtime loader/API where described; confirmation/approval behavior approval-required authority expansion and explicit chat confirmation; idempotency/transaction/result behavior draft/proposal/version activation boundaries and partial-failure surfaces; capability or foundation scope managed-agent governance; AuthContext / roles / tenant scope SaaS admin tenant scope, provider secret boundary and denials; API / frontend / realtime path Agent Admin route/API/runtime loader mappings; audit/work trace expectation PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, provider fail-closed and loader denial traces; validation path `git diff --check`

### TASK-ADR-02-004: Refresh Governance/Policy workstream app-description

- status: done
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
  - commit message: `app-desc-refresh: refresh governance policy workstream`
  - graph coverage proof: Governance/Policy files now link human operators, functional agent, and system worker through `surface_action`, confirmed `human_chat_tool_plan`, bounded `agent_tool_call`, `api_call`, `workflow_step`, and `internal_call` adapters to policy catalog/read/draft/simulate/submit-for-approval/approve/activate/rollback/exception/history governed tools, capability `governance-policy-lifecycle`, dashboard/catalog/detail/draft/simulation/decision/exception/history/result/partial-failure/system-message surfaces, realization API/frontend/Akka/source-alignment maps, runtime-validation expectations, and policy draft/simulation/decision/activation/rollback/exception/denial/runtime-decision traces. Lifecycle/source-alignment is `stale-description-changed` because this docs-only refresh changed current intent without runtime/API/UI validation.
  - vertical contract: Governance/Policy functional-agent workstream; attention category policy approval, exception, simulation finding, rollback decision; role-specific dashboard / surface Governance/Policy dashboard, policy catalog/detail/draft/simulation/decision surfaces; surface graph node/action edge policy draft/simulate/approve/activate/rollback/exception actions and result/partial-failure/system-message surfaces; governed-tool id/type/exposure policy draft/simulate/approve/activate/rollback/exception governed tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, workflow/internal/API where described; confirmation/approval behavior human approval and decision-card requirements; idempotency/transaction/result behavior policy version activation/rollback transaction boundaries and result surfaces; capability or foundation scope governance-policy lifecycle; AuthContext / roles / tenant scope admin/policy operator tenant scope and denials; API / frontend / realtime path Governance/Policy route/API mappings; audit/work trace expectation policy change, decision, simulation, exception, denial, rollback traces; validation path `git diff --check`

### TASK-ADR-02-005: Refresh Audit/Trace workstream app-description

- status: done
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
  - commit message: `app-desc-refresh: refresh audit trace workstream`
  - graph coverage proof: Audit/Trace files now link tenant admin, SaaS support, Audit Trace functional agent, and system workers through `surface_action`, confirmed `human_chat_tool_plan`, bounded `agent_tool_call`, `api_call`, projection/consumer/internal/timer/runtime-validation adapters to audit/work trace search/read/detail/correlation/denial/support-access/summary/export/runtime-validation governed tools, capability `audit-and-trace-investigation`, dashboard/search/detail/timeline/denial/support-access/summary/export/system-message surfaces, realization API/frontend/Akka/source-alignment maps, runtime-validation expectations, and trace read/denied-read/investigation-summary/support-access/export/cross-workstream-correlation/trace-gap evidence. Lifecycle/source-alignment is `stale-description-changed` because this docs-only refresh changed current intent without runtime/API/UI validation.
  - vertical contract: Audit/Trace functional-agent workstream; attention category investigation, denial, trace gap, support-access review; role-specific dashboard / surface Audit/Trace search/detail/timeline/correlation/investigation surfaces; surface graph node/action edge trace search/read/correlation/summary/export-if-allowed actions and result/redacted/denied surfaces; governed-tool id/type/exposure audit/work trace search/read/correlation/denial investigation tools; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, projection/consumer/internal/API where described; confirmation/approval behavior read-only chat confirmation when needed and export/support-access approval where allowed; idempotency/transaction/result behavior read-only idempotency, redacted result surfaces, denied result surfaces; capability or foundation scope audit-and-trace investigation; AuthContext / roles / tenant scope tenant admin/SaaS support/support-access scope and denials; API / frontend / realtime path Audit/Trace route/API/projection mappings; audit/work trace expectation trace reads, denied trace access, investigation summaries, cross-workstream correlations; validation path `git diff --check`

### TASK-ADR-03-001: Cross-workstream consistency and readiness pass

- status: done
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
  - commit message: `app-desc-refresh: verify cross-workstream readiness`
  - readiness/graph proof: `specs/app-description-refresh/consistency-readiness-review.md` assessed all five refreshed foundation workstreams plus shared/global/core-starter artifacts for worker -> execution harness -> actor adapter -> governed tool -> capability -> realization/source-alignment -> tests/runtime-validation -> traces. My Account and User Admin local graphs were coherent for description/build planning; Agent Admin local graph was coherent with documented legacy alias posture; Governance/Policy and Audit/Trace local graphs were coherent but shared capability/global-tool/surface-catalog/current-status artifacts still carried pre-refresh semantics. All five lifecycle/source-alignment states remain `stale-description-changed`; no runtime-ready claim was made.
  - material gaps queued: appended `TASK-ADR-03-002` to reconcile shared capability/global-tool/surface-catalog drift before terminal verification. Next runnable task is `TASK-ADR-03-002`, not `TASK-ADR-99-001`.
  - vertical contract: all five foundation workstreams; description/readiness review only; non-attention reason cross-workstream verification; role-specific dashboard / surface all refreshed dashboard/surface bindings; surface graph node/action edge reviewed but not implemented; governed-tool id/type/exposure all refreshed governed tools reviewed; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, workflow/timer/consumer/API/MCP/internal consistency reviewed; confirmation/approval behavior and idempotency/transaction/result behavior consistency reviewed; capability or foundation scope all core-starter capabilities; AuthContext / roles / tenant scope reviewed; API / frontend / realtime path realization mappings reviewed; audit/work trace expectation trace/source-alignment/runtime-validation consistency reviewed; validation path `git diff --check` plus graph proof

### TASK-ADR-03-002: Reconcile shared app-description drift after readiness pass

- status: done
- source: specs/app-description-refresh/consistency-readiness-review.md
- task brief: specs/app-description-refresh/consistency-readiness-review.md#impact-and-queued-follow-up
- depends on:
  - TASK-ADR-03-001
- required reads:
  - specs/app-description-refresh/consistency-readiness-review.md
  - specs/app-description-refresh/pending-tasks.md
  - app-description/app.md
  - app-description/global/tools/foundation-governed-tools.md
  - app-description/domains/core-starter/capabilities/agent-doc-administration.md
  - app-description/domains/core-starter/capabilities/audit-and-trace-investigation.md
  - app-description/domains/core-starter/capabilities/governance-policy-lifecycle.md
  - app-description/domains/core-starter/workstreams/surface-catalog.md
  - app-description/domains/core-starter/workstreams/ready-to-build-status.md
  - app-description/domains/core-starter/workstreams/agent-admin/**
  - app-description/domains/core-starter/workstreams/audit-trace/**
  - app-description/domains/core-starter/workstreams/governance-policy/**
- skills:
  - app-description-change-impact
  - app-description-readiness-assessment
  - app-descriptions
- expected outputs:
  - reconciled shared capability/global-tool/surface-catalog/current-status artifacts, or explicit bounded deferrals if a contradiction cannot be safely reconciled
  - queue notes returning terminal verification to runnable status when material shared drift is fixed or consciously deferred
- required checks:
  - `git diff --check`
- done criteria:
  - Audit/Trace shared capability/global tool/catalog/status semantics no longer contradict the refreshed Audit/Trace workstream
  - Governance/Policy shared capability/global tool/catalog/status semantics no longer contradict the refreshed Governance/Policy workstream
  - Agent Admin canonical/legacy governed-tool alias posture is consistently represented in shared artifacts
  - terminal verification dependency is restored only after material shared-current-intent drift is fixed, consciously deferred, or blocked with explicit questions
- notes:
  - commit message: `app-desc-refresh: reconcile shared drift`
  - graph proof: Shared Audit/Trace artifacts now describe the refreshed tenant/support-scoped investigation graph, canonical tools (`search-audit-traces`, `search-work-traces`, detail/correlation/denial/summary/export/support-access/runtime-validation ids), confirmed read-only `human_chat_tool_plan`, bounded `agent_tool_call`, redacted export/support-access gates, trace-gap/runtime-validation evidence, and `stale-description-changed` runtime posture. Shared Governance/Policy artifacts now describe the refreshed policy lifecycle tools (`search`, `read`, `draft`, `simulate`, `submit_for_approval`, `approve`, `activate`, `rollback`, `review_exception`, `read_history`), decision-card approval, idempotency/partial-failure, non-overridable controls, and old simple-setting ids as aliases only. Shared Agent Admin artifacts now record managed-agent governance as the current canonical capability posture with legacy `agent-doc-administration` and `*-agent-doc-*` ids retained only as source-alignment aliases.
  - terminal verification dependency restored: material shared-current-intent drift was reconciled in capability, global tool, surface catalog, and ready-to-build status artifacts without runtime/API/UI code changes or a no-code-impact alignment claim. Next runnable task is `TASK-ADR-99-001`.
  - vertical contract: description-only shared drift reconciliation for Governance/Policy, Audit/Trace, and Agent Admin alias mappings; no runtime/API/UI code changes; preserve `stale-description-changed` lifecycle posture unless a no-code-impact alignment review is explicitly recorded; validation path `git diff --check` plus graph proof.

### TASK-ADR-99-001: Terminal app-description refresh verification

- status: pending
- source: mini-project verification loop
- task brief: specs/app-description-refresh/tasks/99-verification/01-terminal-verification.md
- depends on:
  - TASK-ADR-03-002
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
