# Pending Tasks: My Account Full Automated Alignment

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Mark the selected task `in-progress` before implementation edits.
- Mark a task `done` only after done criteria and required checks pass.
- Each completed task must make one focused git commit containing implementation/planning changes and the queue-status update.
- Record the commit message or hash in task notes.
- If blocked, mark `blocked`, record the blocker and recommended unblock path, and commit the queue update if useful.
- Do not run implementation tasks in parallel.

## Tasks

### MAFA-00-001: Create My Account full automated alignment mini-project

- status: done
- source: user request to create a mini-project for all non-manual My Account full-alignment work
- task brief: `specs/my-account-full-alignment/tasks/00-planning/00-create-mini-project.md`
- depends on: []
- required reads:
  - `AGENTS.md`
  - `specs/AGENTS.md`
  - `app-description/domains/core-starter/workstreams/my-account/**`
- skills:
  - `project-discussed-idea-to-pending-project`
- expected outputs:
  - planning scaffold under `specs/my-account-full-alignment/**`
- required checks:
  - `git diff --check -- specs/my-account-full-alignment`
- done criteria:
  - mini-project captures current intent, backlog, task briefs, pending queue, and terminal verification loop
- notes:
  - lifecycle/readiness: planning-only non-runtime scaffold
  - vertical contract: docs-only planning; My Account / `my-account-agent`; maps all relevant governed tools and capability ids but executes none; no runtime evidence claimed
  - completed by current scaffold creation
  - commit message: `Add My Account full automated alignment plan`

### MAFA-01-001: Split My Account source-alignment entries

- status: done
- source: `backlog/01-my-account-automated-alignment-build-backlog.md` B01
- task brief: `specs/my-account-full-alignment/tasks/01-alignment/01-source-alignment-split.md`
- depends on: [MAFA-00-001]
- required reads:
  - `specs/my-account-full-alignment/README.md`
  - `specs/my-account-full-alignment/conversation-capture.md`
  - `specs/my-account-full-alignment/backlog/01-my-account-automated-alignment-build-backlog.md`
  - `specs/my-account-full-alignment/tasks/01-alignment/01-source-alignment-split.md`
  - `app-description/domains/core-starter/workstreams/my-account/lifecycle.md`
  - `app-description/domains/core-starter/workstreams/my-account/realization/source-alignment.md`
  - `app-description/domains/core-starter/workstreams/my-account/**`
- skills:
  - `app-description-change-impact`
  - `app-description-readiness-summary`
- expected outputs:
  - split My Account source-alignment entries and lifecycle status refinements
- required checks:
  - `git diff --check -- specs/my-account-full-alignment app-description/domains/core-starter/workstreams/my-account`
- done criteria:
  - split entries exist for dashboard, profile/settings, context, notification, digest, chat-plan, trace/audit, and no-access recovery
  - no runtime readiness is overstated
- notes:
  - lifecycle/readiness: build-compile planning/alignment; non-runtime docs task
  - source capability ids: `account-context-and-profile`, `my_account.*`, `notification.*`
  - vertical contract: My Account / `my-account-agent`; maps `surface_action`, `human_chat_tool_plan`, API/internal paths; no tool execution; selected AuthContext and tenant/customer safe-denial requirements documented
  - completed 2026-06-27: split source-alignment entries and lifecycle slice status map; no runtime readiness claimed
  - commit message: `Split My Account source alignment entries`

### MAFA-02-001: Backend protected API and action path tests

- status: done
- source: `backlog/01-my-account-automated-alignment-build-backlog.md` B02
- task brief: `specs/my-account-full-alignment/tasks/02-backend/01-protected-api-action-tests.md`
- depends on: [MAFA-01-001]
- required reads:
  - `specs/my-account-full-alignment/README.md`
  - `specs/my-account-full-alignment/backlog/01-my-account-automated-alignment-build-backlog.md`
  - `specs/my-account-full-alignment/tasks/02-backend/01-protected-api-action-tests.md`
  - `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/my-account/tools/governed-tools.md`
  - `app-description/domains/core-starter/workstreams/my-account/tests/coverage.md`
  - `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`
  - `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
  - `src/main/java/ai/first/application/coreapp/myaccount/**`
- skills:
  - `akka-http-endpoints`
  - `akka-http-endpoint-testing`
  - `capability-first-backend`
  - `akka-runtime-feature-verification`
- expected outputs:
  - backend/API tests and small runtime repairs for dashboard, profile/settings, context, no-access recovery, selected AuthContext, and denials
- required checks:
  - `mvn -Dtest='WorkstreamServiceTest,MyAccountBrowserWorkstreamSmokeTest' test`
  - `git diff --check`
- done criteria:
  - protected backend paths pass automated tests or precise blockers are recorded
  - source-alignment backend/API entries are updated
- notes:
  - lifecycle/readiness: backend-ready/api-smoked by automated tests
  - vertical contract: My Account / `my-account-agent`; surfaces `surface-my-account-dashboard`, `surface-my-profile`, `surface-my-settings`, `surface-my-context`, `surface-my-account-open-denied`; governed tools `read-current-account-context`, `my_account.update_profile_settings`, `my_account.open_authorized_workstream`, `core.access.context.select`; adapter `surface_action` plus API/internal; selected AuthContext and tenant/customer denials required; idempotency for profile/settings; traces checked or handed to MAFA-03-001
  - completed: 2026-06-27; commit message `MAFA-02-001 protected My Account API tests`

### MAFA-03-001: Durable trace and audit verification

- status: done
- source: `backlog/01-my-account-automated-alignment-build-backlog.md` B03
- task brief: `specs/my-account-full-alignment/tasks/03-traces/01-durable-trace-audit-verification.md`
- depends on: [MAFA-02-001]
- required reads:
  - `specs/my-account-full-alignment/README.md`
  - `specs/my-account-full-alignment/tasks/03-traces/01-durable-trace-audit-verification.md`
  - `app-description/domains/core-starter/workstreams/my-account/traces/work-traces.md`
  - `app-description/global/traces/foundation-trace-patterns.md`
  - `src/main/java/ai/first/application/foundation/audit/**`
  - `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
  - `src/main/java/ai/first/application/coreapp/myaccount/**`
- skills:
  - `akka-agent-work-trace`
  - `ai-first-saas-audit-trace`
  - `akka-runtime-feature-verification`
- expected outputs:
  - trace/audit tests and repairs or bounded blockers
- required checks:
  - `mvn -Dtest='*Trace*Test,WorkstreamServiceTest,MyAccountPersonalAttentionDigestServiceTest' test`
  - `git diff --check`
- done criteria:
  - durable trace/audit evidence is proven for automated slices or gaps are queued
  - source-alignment trace/audit entry is updated
- notes:
  - lifecycle/readiness: backend-ready for automated durable trace/audit checks
  - vertical contract: My Account / `my-account-agent`; all consequential My Account tools across `surface_action`, `human_chat_tool_plan`, API/internal; selected AuthContext, actor, capability/tool/action id, result, redaction, correlation/idempotency evidence required
  - implementation evidence: added durable My Account surface-action audit traces and `MyAccountTraceAuditTest`; targeted `mvn -Dtest='MyAccountTraceAuditTest' test` passes and source-alignment trace/audit entry is updated
  - blocker repair: aligned `AuditTraceBrowserWorkstreamSmokeTest` disabled Audit/Trace account assertions with the existing no-access recovery path, proving disabled actors receive only `account-disabled` / `surface-my-account-open-denied` recovery without an Audit/Trace AuthContext, capability, or surface.
  - validation: `mvn -Dtest='AuditTraceBrowserWorkstreamSmokeTest' test` passes; required aggregate `mvn -Dtest='*Trace*Test,WorkstreamServiceTest,MyAccountPersonalAttentionDigestServiceTest' test` passes; `git diff --check` passes.
  - commit message: `MAFA-03-001 repair AuditTrace disabled recovery smoke`

### MAFA-04-001: Notification center lifecycle alignment

- status: done
- source: `backlog/01-my-account-automated-alignment-build-backlog.md` B04
- task brief: `specs/my-account-full-alignment/tasks/04-notifications/01-notification-center-lifecycle-alignment.md`
- depends on: [MAFA-03-001]
- required reads:
  - `specs/my-account-full-alignment/README.md`
  - `specs/my-account-full-alignment/tasks/04-notifications/01-notification-center-lifecycle-alignment.md`
  - `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/my-account/tools/governed-tools.md`
  - `src/main/java/ai/first/application/foundation/notification/**`
  - `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
  - `frontend/src/workstream/surfaces/NotificationCenterSurface.tsx`
- skills:
  - `akka-web-ui-testing`
  - `akka-http-endpoint-testing`
  - `capability-first-backend`
- expected outputs:
  - backend/frontend tests and repairs for notification center lifecycle and rendering
- required checks:
  - `mvn -Dtest='WorkstreamServiceTest,*Notification*Test' test`
  - `npm --prefix frontend test -- --run frontend/src/workstream-my-account-vertical.contract.test.mjs`
  - `npm --prefix frontend run typecheck`
  - `git diff --check`
- done criteria:
  - notification center automated behavior matches current app-description or residual gaps are queued
  - source-alignment notification entry is updated
- notes:
  - lifecycle/readiness: backend-ready/frontend-rendered by automated tests
  - vertical contract: My Account / `my-account-agent`; surface `surface-my-account-notification-center`; governed tools `notification.list_my_account_center`, `notification.mark_read`, `notification.dismiss`, `notification.archive`, `notification.snooze`, `notification.update_preferences`, `attention.open_attention_item`; adapter `surface_action`; lifecycle actions mutate notification state only; source-open reauthorizes or returns `surface-my-account-open-denied`; in-app only, no external/provider controls
  - completed 2026-06-27: added notification service/workstream automated coverage for empty refresh, lifecycle/no-op/bounded snooze, visible in-app preferences, external/hidden preference denial, source-open success/safe denial, and frontend notification-center rendering/secret-boundary contract; source-alignment notification entry updated.
  - validation: `mvn -Dtest='WorkstreamServiceTest,*Notification*Test' test`, `npm --prefix frontend test -- --run frontend/src/workstream-my-account-vertical.contract.test.mjs`, `npm --prefix frontend run typecheck`, and `git diff --check` pass.
  - commit message: `MAFA-04-001 align notification center lifecycle`

### MAFA-05-001: Human chat tool-plan runtime proof

- status: done
- source: `backlog/01-my-account-automated-alignment-build-backlog.md` B05
- task brief: `specs/my-account-full-alignment/tasks/05-chat-plan/01-human-chat-tool-plan-runtime-proof.md`
- depends on: [MAFA-04-001]
- required reads:
  - `specs/my-account-full-alignment/README.md`
  - `specs/my-account-full-alignment/tasks/05-chat-plan/01-human-chat-tool-plan-runtime-proof.md`
  - `app-description/domains/core-starter/workstreams/my-account/tools/governed-tools.md`
  - `app-description/domains/core-starter/workstreams/my-account/tests/coverage.md`
  - `app-description/domains/core-starter/workstreams/my-account/traces/work-traces.md`
  - `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
  - `src/main/java/ai/first/application/foundation/agent/AgentBehaviorSeedLoader.java`
- skills:
  - `akka-agent-tool-boundaries`
  - `akka-agent-work-trace`
  - `akka-agent-testing`
  - `capability-first-backend`
- expected outputs:
  - chat-plan proposal/confirmation/denial/idempotency tests and repairs
- required checks:
  - `mvn -Dtest='WorkstreamServiceTest,AgentBehaviorSeedLoaderTest,*ToolBoundary*Test,*Trace*Test' test`
  - `npm --prefix frontend test -- --run frontend/src/workstream-chat-tool-plan.contract.test.mjs frontend/src/workstream-my-account-vertical.contract.test.mjs`
  - `git diff --check`
- done criteria:
  - chat-plan path is automated-tested for proposal, no pre-confirm mutation, exact confirmation, denials, idempotency, partial failure, and tool-boundary enforcement
  - source-alignment chat-plan entry is updated
- notes:
  - lifecycle/readiness: backend-ready/frontend-contract for chat-plan path; provider/model unavailable may be fail-closed unless configured
  - vertical contract: My Account / `my-account-agent`; governed tools `my_account.update_profile_settings`, notification lifecycle/preference tools; actor adapter `human_chat_tool_plan`; exact snapshot confirmation, per-step idempotency/transaction, selected AuthContext, requestedBy/confirmedBy, safe system-message denial, durable trace events required
  - completed 2026-06-27: added safe confirmation-denial system-message handling plus WorkstreamService tests for My Account cross-context/tool-boundary confirmation denials, refreshed exact-confirmation denial assertions, and updated frontend/source-alignment chat-plan evidence.
  - validation: `mvn -Dtest='WorkstreamServiceTest,AgentBehaviorSeedLoaderTest,*ToolBoundary*Test,*Trace*Test' test`, `npm --prefix frontend test -- --run frontend/src/workstream-chat-tool-plan.contract.test.mjs frontend/src/workstream-my-account-vertical.contract.test.mjs`, and `git diff --check` pass.
  - commit message: `MAFA-05-001 prove My Account chat tool plan runtime`

### MAFA-06-001: Digest/export provider-runtime closure

- status: pending
- source: `backlog/01-my-account-automated-alignment-build-backlog.md` B06
- task brief: `specs/my-account-full-alignment/tasks/06-digest/01-digest-provider-runtime-closure.md`
- depends on: [MAFA-05-001]
- required reads:
  - `specs/my-account-full-alignment/README.md`
  - `specs/my-account-full-alignment/tasks/06-digest/01-digest-provider-runtime-closure.md`
  - `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/my-account/tools/governed-tools.md`
  - `src/main/java/ai/first/application/coreapp/myaccount/MyAccountPersonalAttentionDigestService.java`
  - `src/main/java/ai/first/application/coreapp/myaccount/**Digest**`
- skills:
  - `akka-autonomous-agents`
  - `akka-autonomous-agent-testing`
  - `akka-runtime-feature-verification`
  - `ai-first-saas-audit-trace`
- expected outputs:
  - digest lifecycle/fail-closed/provider-runtime tests and source-alignment classification
- required checks:
  - `mvn -Dtest='MyAccountPersonalAttentionDigestServiceTest,MyAccountPersonalAttentionDigestAutonomousAgentTest,DigestExportServiceTest,WorkstreamServiceTest' test`
  - `git diff --check`
- done criteria:
  - digest automated behavior is aligned or provider-backed success is explicitly config-blocked
  - fail-closed/no-fake-success semantics are tested
  - source-alignment digest entry is updated
- notes:
  - lifecycle/readiness: backend-ready for fail-closed/task lifecycle; provider-backed runtime-ready only if configured and tested
  - vertical contract: My Account / `my-account-agent`; system/autonomous digest worker; governed tool `request-personal-digest-export`; digest progress/result/blocked surfaces; start/cancel/review idempotency; advisory-only; no source attention mutation; tenant/customer/account ownership and trace evidence required

### MAFA-07-001: Frontend automated surface coverage

- status: pending
- source: `backlog/01-my-account-automated-alignment-build-backlog.md` B07
- task brief: `specs/my-account-full-alignment/tasks/07-frontend/01-frontend-automated-surface-coverage.md`
- depends on: [MAFA-06-001]
- required reads:
  - `specs/my-account-full-alignment/README.md`
  - `specs/my-account-full-alignment/tasks/07-frontend/01-frontend-automated-surface-coverage.md`
  - `app-description/domains/core-starter/workstreams/my-account/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/my-account/realization/frontend-routes.md`
  - `frontend/src/workstream/surfaces/**`
  - `frontend/src/workstream-my-account-vertical.contract.test.mjs`
- skills:
  - `akka-web-ui-testing`
  - `akka-web-ui-accessibility-responsive`
  - `akka-web-ui-state-rendering`
  - `akka-web-ui-api-client`
- expected outputs:
  - frontend automated tests/types/repairs for My Account surfaces and secret boundaries
- required checks:
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
  - `git diff --check`
- done criteria:
  - automated frontend coverage proves non-manual My Account UI contract alignment
  - source-alignment frontend entries are updated
- notes:
  - lifecycle/readiness: frontend-rendered by automated tests/typecheck/build
  - vertical contract: My Account / `my-account-agent`; all My Account browser surfaces and `surface_action` controls; frontend renders backend-authored actions but grants no authority; no auto-confirm; selected AuthContext from backend; trace links safe; no raw JWT/session/provider/hidden workstream/category/outbox/model/config/arbitrary CSS rendering

### MAFA-08-001: Terminal automated verification

- status: pending
- source: `backlog/01-my-account-automated-alignment-build-backlog.md` B08
- task brief: `specs/my-account-full-alignment/tasks/08-verification/01-terminal-automated-verification.md`
- depends on: [MAFA-07-001]
- required reads:
  - `specs/my-account-full-alignment/README.md`
  - `specs/my-account-full-alignment/conversation-capture.md`
  - `specs/my-account-full-alignment/pending-tasks.md`
  - `specs/my-account-full-alignment/backlog/01-my-account-automated-alignment-build-backlog.md`
  - `specs/my-account-full-alignment/tasks/**`
  - `app-description/domains/core-starter/workstreams/my-account/lifecycle.md`
  - `app-description/domains/core-starter/workstreams/my-account/realization/source-alignment.md`
- skills:
  - `akka-runtime-feature-verification`
  - `app-description-readiness-summary`
  - `akka-pending-task-queue-maintenance`
- expected outputs:
  - terminal verification notes, readiness/source-alignment updates, and follow-up tasks if gaps remain
- required checks:
  - `mvn -Dtest='WorkstreamServiceTest,MyAccountBrowserWorkstreamSmokeTest,MyAccountPersonalAttentionDigestServiceTest,MyAccountPersonalAttentionDigestAutonomousAgentTest,AgentBehaviorSeedLoaderTest' test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `npm --prefix frontend run build`
  - `git diff --check`
- done criteria:
  - terminal verification confirms all non-manual automated alignment items are complete, or appends bounded follow-up tasks plus a new terminal verification task
  - remaining manual/provider-config items are documented separately and not treated as automated gaps
- notes:
  - lifecycle/readiness: verification; automated-aligned/manual-ready only if evidence supports it
  - vertical contract: My Account / `my-account-agent`; verification covers all My Account governed tools/adapters and capability ids in source-alignment entries; selected AuthContext and denial evidence required; trace evidence verified or follow-up queued
