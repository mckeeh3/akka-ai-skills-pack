# Implementation Follow-up Queue

This file was consolidated by `TASK-ADIA-03-001` into an executable follow-up queue. It does not implement the follow-up work; it preserves the bounded remediation and runtime-validation tasks that later fresh contexts can execute one at a time.

Readiness claim: queue-authoring only. No runtime-validation scenario has passed and no workstream is `runtime-ready` from this consolidation.

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Mark exactly one task `in-progress` before implementation or runtime-validation edits.
- Mark `done` only after the task's required checks and runtime evidence requirements pass, or `blocked` with an exact blocker classification.
- Do not count fixture-only, mock-only, provider-less success, frontend-only rendering, or description-only evidence as runtime-ready.
- For feature-bearing remediation tasks, update the affected workstream lifecycle/source-alignment evidence before marking the task `done`.

## Consolidated order

1. Execute the authored first runtime-validation scenario for each foundation workstream.
2. Validate auth/setup and provider/fail-closed paths that block runtime-readiness claims.
3. Reconcile canonical governed-tool/surface/action identifiers before broadening runtime evidence.
4. Implement or verify deeper compile/runtime gaps one bounded vertical slice at a time.
5. Record run evidence under `specs/runtime-validation/runs/` and keep source-alignment files honest about readiness levels.

## Tasks

### TASK-ADIA-FU-001: Execute My Account login/account-context runtime-validation scenario

- status: blocked (`auth-setup-blocker`; `runtime-validation-gap`; `seed-data-blocker`)
- prior blocker classification: `runtime-validation-gap`, `auth-setup-blocker`, `seed-data-blocker`
- prior blocker evidence: `specs/runtime-validation/runs/2026-06-29-RV-MY-ACCOUNT-001-blocked-auth-seed.md` recorded missing start/seed contracts and missing seeded member/disabled fixtures. Remediation added `tools/runtime-validation/start-local.sh`, `tools/runtime-validation/seed.sh`, and the local-only Akka seed endpoint; rerun this scenario through the local Akka/API/UI path before marking done.
- source: `TASK-ADIA-02-001`; `specs/runtime-validation/scenarios/my-account/RV-MY-ACCOUNT-001-login-and-account-context.md`
- depends on: []
- required reads:
  - `specs/app-description-implementation-alignment/source-evidence-inventory.md`
  - `specs/app-description-implementation-alignment/runtime-validation-corpus-plan.md`
  - `app-description/domains/core-starter/workstreams/my-account/realization/source-alignment.md`
  - `specs/runtime-validation/scenarios/my-account/RV-MY-ACCOUNT-001-login-and-account-context.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - a run record under `specs/runtime-validation/runs/`
  - updated My Account source-alignment/lifecycle notes if evidence changes
- required checks:
  - `git diff --check`
  - execute or precisely block `RV-MY-ACCOUNT-001` through the local Akka/API/UI path
- done criteria:
  - login, `/api/me`, account context, open-disabled/denial behavior, browser-safe payloads, and trace evidence are recorded or explicitly blocked
- block criteria:
  - `auth-setup-blocker`, `seed-data-blocker`, or `runtime-validation-gap` if the scenario cannot run
- notes:
  - vertical contract: My Account functional agent workstream; attention category account/profile context or open-disabled blocker; role-specific dashboard / surface My Account dashboard/profile/account-context surfaces; surface graph node/action edge login/open account context/read profile/denied-open result surface; governed-tool id/type/exposure account context and profile read tools via `surface_action` and `api_call`; actor adapter/source `surface_action`, `api_call`, and bounded `human_chat_tool_plan` where surfaced; confirmation/approval behavior none for reads and explicit confirmation only if profile/chat actions are executed; idempotency/transaction/result behavior read/no-op profile result surfaces; capability `account-context-and-profile`; AuthContext / roles / tenant scope signed-in member tenant/Organization scope; Akka substrate endpoint/service/view/frontend; API / frontend / realtime path `/api/me` and My Account workstream surfaces; audit/work trace requirements account-context read, denial, and correlation traces; local validation path `RV-MY-ACCOUNT-001` with run record under `specs/runtime-validation/runs/`.

### TASK-ADIA-FU-002: Execute User Admin invitation runtime-validation scenario

- status: pending
- source: `ADIA-FU-UA-001`; `TASK-ADIA-02-002`; `specs/runtime-validation/scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md`
- depends on:
  - `TASK-ADIA-FU-001`
- required reads:
  - `specs/app-description-implementation-alignment/source-evidence-inventory.md`
  - `app-description/domains/core-starter/workstreams/user-admin/realization/source-alignment.md`
  - `specs/runtime-validation/scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - run record for `RV-USER-ADMIN-001`
  - User Admin source-alignment update or blocker notes
- required checks:
  - `git diff --check`
  - execute or precisely block `RV-USER-ADMIN-001` through the protected local path
- done criteria:
  - invitation create, duplicate invite/idempotency, denied member attempt, result surface, and audit/work trace ids are recorded
- block criteria:
  - `auth-setup-blocker` for missing WorkOS/AuthKit/local users; `provider-config-blocker` for required live email delivery
- notes:
  - vertical contract: User Admin functional agent workstream; attention category invitation/onboarding; role-specific dashboard / surface User Admin dashboard/invite/result/admin-audit surfaces; surface graph node/action edge invitation create, duplicate invite, denied member invite, and result/system-message surfaces; governed-tool id/type/exposure invitation tools via `surface_action` and API; actor adapter/source `surface_action`, `api_call`, and confirmed `human_chat_tool_plan` only if in scenario; confirmation/approval behavior invite confirmation where surfaced; idempotency/transaction/result behavior duplicate invite idempotency and invitation result surfaces; capability `user-and-access-administration`; AuthContext / roles / tenant scope organization-admin versus member tenant scope; Akka substrate endpoint/service/entity/view/frontend; API / frontend / realtime path User Admin workstream and admin endpoints; audit/work trace requirements invitation, denial, requestedBy/confirmedBy traces; local validation path `RV-USER-ADMIN-001` with run record.

### TASK-ADIA-FU-003: Verify User Admin invitee acceptance and selected-context refresh path

- status: pending
- source: `ADIA-FU-UA-002`; `TASK-ADIA-02-002`
- depends on:
  - `TASK-ADIA-FU-002`
- required reads:
  - `app-description/domains/core-starter/workstreams/user-admin/realization/source-alignment.md`
  - `specs/app-description-implementation-alignment/source-evidence-inventory.md`
  - `specs/runtime-validation/scenarios/user-admin/RV-USER-ADMIN-001-invite-user.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - runtime-validation run record or explicit auth/setup blocker for acceptance and selected-context refresh
- required checks:
  - `git diff --check`
  - protected acceptance-path runtime smoke or blocker record
- done criteria:
  - valid acceptance, replay, mismatched/expired/revoked denial, `/api/me` selected-context refresh, and browser secret-boundary evidence are recorded
- block criteria:
  - `auth-setup-blocker` if invitee identity or token setup is unavailable
- notes:
  - vertical contract: User Admin functional agent workstream; attention category invitation acceptance/account activation; role-specific dashboard / surface invitation acceptance result and selected-context account surfaces; surface graph node/action edge accept invite, replay, mismatch/expired/revoked denial, selected-context refresh; governed-tool id/type/exposure invitation acceptance and context refresh via `api_call` plus surface result; actor adapter/source `api_call` and frontend route; confirmation/approval behavior token validation with no approval; idempotency/transaction/result behavior replay idempotency and activation transaction result surfaces; capability `user-and-access-administration`; AuthContext / roles / tenant scope invitee account and tenant membership scope; Akka substrate endpoint/service/entity/view/frontend; API / frontend / realtime path `InvitationAcceptanceEndpoint`, `/api/me`, and acceptance surface; audit/work trace requirements invitation acceptance, replay, denial, and selected-context traces; local validation path runtime smoke plus run record.

### TASK-ADIA-FU-004: Execute Agent Admin provider fail-closed test-console runtime-validation scenario

- status: pending
- source: `ADIA-FU-AA-001`; `TASK-ADIA-02-003`; `specs/runtime-validation/scenarios/agent-admin/RV-AGENT-ADMIN-001-provider-fail-closed-test-console.md`
- depends on:
  - `TASK-ADIA-FU-003`
- required reads:
  - `app-description/domains/core-starter/workstreams/agent-admin/realization/source-alignment.md`
  - `specs/app-description-implementation-alignment/source-evidence-inventory.md`
  - `specs/runtime-validation/scenarios/agent-admin/RV-AGENT-ADMIN-001-provider-fail-closed-test-console.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - run record for `RV-AGENT-ADMIN-001`
  - Agent Admin source-alignment update or blocker notes
- required checks:
  - `git diff --check`
  - execute or precisely block `RV-AGENT-ADMIN-001`
- done criteria:
  - SaaS-admin test-console provider-missing fail-closed surface, loader/tool-boundary denial, retry behavior, sanitized network payloads, and trace ids are recorded
- block criteria:
  - `auth-setup-blocker`; `provider-config-blocker` only if the app cannot be placed in intentionally missing-provider state
- notes:
  - vertical contract: Agent Admin functional agent workstream; attention category provider-config and loader/tool-boundary denial; role-specific dashboard / surface Agent Admin catalog/detail/test-console/runtime-trace surfaces; surface graph node/action edge open test console, submit harmless prompt, provider fail-closed result, loader denial trace; governed-tool id/type/exposure agent test-console and runtime-loader tools via `surface_action` and internal `agent_tool_call`; actor adapter/source `surface_action`, `api_call`, `agent_tool_call`; confirmation/approval behavior no activation approval and no behavior change; idempotency/transaction/result behavior retry/no-side-effect provider-fail result surface; capability managed-agent governance; AuthContext / roles / tenant scope SaaS-admin tenant/platform scope with provider secret boundary; Akka substrate endpoint/service/agent/view/frontend; API / frontend / realtime path Agent Admin workstream API and surfaces; audit/work trace requirements provider fail-closed, loader denial, PromptAssemblyTrace/SkillLoadTrace/ReferenceLoadTrace/AgentWorkTrace correlation; local validation path `RV-AGENT-ADMIN-001` with run record.

### TASK-ADIA-FU-005: Execute Governance/Policy decision-card runtime-validation scenario

- status: pending
- source: `ADIA-FU-GP-001`; `TASK-ADIA-02-004`; `specs/runtime-validation/scenarios/governance-policy/RV-GOVPOL-001-policy-decision-card.md`
- depends on:
  - `TASK-ADIA-FU-004`
- required reads:
  - `app-description/domains/core-starter/workstreams/governance-policy/realization/source-alignment.md`
  - `specs/app-description-implementation-alignment/source-evidence-inventory.md`
  - `specs/runtime-validation/scenarios/governance-policy/RV-GOVPOL-001-policy-decision-card.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - run record for `RV-GOVPOL-001`
  - Governance/Policy source-alignment update or blocker notes
- required checks:
  - `git diff --check`
  - execute or precisely block `RV-GOVPOL-001`
- done criteria:
  - protected decision-card path, repeat decision/idempotency, denied unprivileged decision, and audit/work/policy trace ids are recorded
- block criteria:
  - `auth-setup-blocker`; `provider-config-blocker` only for optional model-backed impact analysis
- notes:
  - vertical contract: Governance/Policy functional agent workstream; attention category policy approval/decision card; role-specific dashboard / surface Governance/Policy proposal/detail/simulation/decision surfaces; surface graph node/action edge create/select proposal, inspect simulation evidence, approve/reject, repeat decision, member denial result; governed-tool id/type/exposure policy decision tools via `surface_action` and API; actor adapter/source `surface_action`, `api_call`, optional confirmed `human_chat_tool_plan`; confirmation/approval behavior human approval decision-card; idempotency/transaction/result behavior repeat decision and policy decision transaction/result surfaces; capability `governance-policy-lifecycle`; AuthContext / roles / tenant scope policy admin/operator versus member tenant scope; Akka substrate endpoint/service/entity/view/frontend; API / frontend / realtime path Governance/Policy workstream API and surfaces; audit/work trace requirements policy decision, denial, simulation, and correlation traces; local validation path `RV-GOVPOL-001` with run record.

### TASK-ADIA-FU-006: Execute Audit/Trace search-denial-redaction runtime-validation scenario

- status: pending
- source: `ADIA-FU-AT-001`; `TASK-ADIA-02-005`; `specs/runtime-validation/scenarios/audit-trace/RV-AUDIT-001-trace-search-denial-redaction.md`
- depends on:
  - `TASK-ADIA-FU-005`
- required reads:
  - `app-description/domains/core-starter/workstreams/audit-trace/realization/source-alignment.md`
  - `specs/app-description-implementation-alignment/source-evidence-inventory.md`
  - `specs/runtime-validation/scenarios/audit-trace/RV-AUDIT-001-trace-search-denial-redaction.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - run record for `RV-AUDIT-001`
  - Audit/Trace source-alignment update or blocker notes
- required checks:
  - `git diff --check`
  - execute or precisely block `RV-AUDIT-001`
- done criteria:
  - in-scope trace search/detail/timeline, out-of-scope/member denied reads, redaction/support-scope behavior, sanitized payloads, and trace-read/denied-read ids are recorded
- block criteria:
  - `auth-setup-blocker`, `seed-data-blocker`, or `runtime-validation-gap`
- notes:
  - vertical contract: Audit/Trace functional agent workstream; attention category investigation/denial/redaction/support-access; role-specific dashboard / surface Audit/Trace search/detail/timeline/correlation surfaces; surface graph node/action edge trace search, read detail, read timeline, denied out-of-scope/member read, redacted result surface; governed-tool id/type/exposure audit trace read tools via `surface_action`, API, and read-only `human_chat_tool_plan` where confirmed; actor adapter/source `surface_action`, `api_call`, projection/internal trace source; confirmation/approval behavior read-only confirmation only for chat plans and no export approval in this scenario; idempotency/transaction/result behavior repeat read idempotency and redacted/denied result surfaces; capability `audit-and-trace-investigation`; AuthContext / roles / tenant scope tenant admin/SaaS support/support-access versus member scope; Akka substrate endpoint/service/view/projection/frontend; API / frontend / realtime path Audit/Trace workstream API and surfaces; audit/work trace requirements trace-read and denied-trace-access traces with correlation; local validation path `RV-AUDIT-001` with run record.

### TASK-ADIA-FU-007: Verify My Account provider-backed digest and fail-closed runtime paths

- status: pending
- source: `TASK-ADIA-02-001`; My Account provider/runtime-validation gap in source evidence inventory
- depends on:
  - `TASK-ADIA-FU-006`
- required reads:
  - `app-description/domains/core-starter/workstreams/my-account/realization/source-alignment.md`
  - `specs/app-description-implementation-alignment/source-evidence-inventory.md`
  - `specs/app-description-implementation-alignment/runtime-validation-corpus-plan.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - runtime-validation run record or exact provider/config blocker for digest success/fail-closed behavior
  - source-alignment update for readiness level only if evidence supports it
- required checks:
  - `mvn test`
  - `git diff --check`
  - provider-missing fail-closed or configured-provider runtime smoke
- done criteria:
  - personal attention digest start/read/provider-missing fail-closed and, when credentials are available, configured-provider advisory success are proven without browser secret exposure
- block criteria:
  - `provider-config-blocker`, `auth-setup-blocker`, or `runtime-validation-gap`
- notes:
  - vertical contract: My Account functional agent workstream; attention category personal attention digest/provider-config blocker; role-specific dashboard / surface My Account dashboard/digest/task/result surfaces; surface graph node/action edge start digest, read digest status/result, provider fail-closed result; governed-tool id/type/exposure digest/evidence tools via `surface_action`, internal `agent_tool_call`, and API; actor adapter/source `surface_action`, `api_call`, `agent_tool_call`; confirmation/approval behavior human-visible advisory only and no delegated side effect without confirmation; idempotency/transaction/result behavior digest task idempotency and result/blocked surfaces; capability `account-context-and-profile`; AuthContext / roles / tenant scope signed-in member tenant scope; Akka substrate endpoint/service/entity/autonomous agent/view/frontend; API / frontend / realtime path My Account workstream API and task surfaces; audit/work trace requirements digest task, provider fail-closed, evidence-read, and denial traces; local validation path `mvn test`, provider smoke or fail-closed runtime smoke, and run record.

### TASK-ADIA-FU-008: Verify User Admin provider fail-closed and configured-provider paths

- status: pending
- source: `ADIA-FU-UA-003`; `TASK-ADIA-02-002`
- depends on:
  - `TASK-ADIA-FU-007`
- required reads:
  - `app-description/domains/core-starter/workstreams/user-admin/realization/source-alignment.md`
  - `specs/app-description-implementation-alignment/source-evidence-inventory.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - provider runtime smoke/run record or precise `provider-config-blocker`
- required checks:
  - `mvn test`
  - `git diff --check`
  - provider-missing fail-closed or configured-provider runtime smoke
- done criteria:
  - Resend invitation delivery and access-review/model-backed guidance either pass with configured providers or fail closed with actionable surfaces/traces and no fake success
- block criteria:
  - `provider-config-blocker` or `auth-setup-blocker`
- notes:
  - vertical contract: User Admin functional agent workstream; attention category invitation delivery/provider-config/access-review; role-specific dashboard / surface invite result/access-review/task/admin-audit surfaces; surface graph node/action edge send invite, provider fail-closed, access-review start/read/result; governed-tool id/type/exposure invitation delivery and access-review evidence tools via `surface_action`, API, internal `agent_tool_call`; actor adapter/source `surface_action`, `api_call`, `agent_tool_call`; confirmation/approval behavior risky admin/access-review advisory confirmation where surfaced; idempotency/transaction/result behavior invitation outbox idempotency and access-review task result/blocked surfaces; capability `user-and-access-administration`; AuthContext / roles / tenant scope organization-admin tenant scope; Akka substrate endpoint/service/entity/autonomous agent/view/frontend; API / frontend / realtime path User Admin admin/workstream API and task surfaces; audit/work trace requirements provider fail-closed, invite delivery, access-review, denial traces; local validation path `mvn test`, provider smoke or fail-closed runtime smoke.

### TASK-ADIA-FU-009: Reconcile Agent Admin canonical governed-tool ids and surface names

- status: pending
- source: `ADIA-FU-AA-002`; `TASK-ADIA-02-003`
- depends on:
  - `TASK-ADIA-FU-008`
- required reads:
  - `app-description/domains/core-starter/workstreams/agent-admin/tools/governed-tools.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/realization/source-alignment.md`
  - `specs/app-description-implementation-alignment/source-evidence-inventory.md`
- skills:
  - `akka-backlog-item-to-task-brief`
  - `akka-runtime-feature-verification`
- expected outputs:
  - implementation/app-description/frontend/API mapping update or a narrower task brief if the reconciliation is too broad
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `git diff --check`
- done criteria:
  - each canonical Agent Admin governed-tool id maps to one backend capability/action and one frontend result surface, with legacy aliases documented only as compatibility wrappers
- block criteria:
  - `source-alignment-gap` if compatibility posture cannot be chosen from current intent; `test-gap` if contracts cannot prove mapping
- notes:
  - vertical contract: Agent Admin functional agent workstream; attention category behavior governance/canonical-id reconciliation; role-specific dashboard / surface catalog/detail/proposal/test-console/runtime-trace surfaces; surface graph node/action edge canonical tool actions to result surfaces; governed-tool id/type/exposure `agent-definition.catalog.read`, `prompt-document.proposal.create`, `agent-test-console.run`, `agent-runtime-trace.read`, and aliases via `surface_action`, `human_chat_tool_plan`, and API; actor adapter/source `surface_action`, `human_chat_tool_plan`, `api_call`, internal loader; confirmation/approval behavior authority-expansion approval and chat confirmation preserved; idempotency/transaction/result behavior proposal/version/test-console result and partial-failure surfaces; capability managed-agent governance; AuthContext / roles / tenant scope SaaS-admin scope; Akka substrate endpoint/service/entity/view/frontend; API / frontend / realtime path Agent Admin workstream/API/frontend contracts; audit/work trace requirements PromptAssemblyTrace/SkillLoadTrace/ReferenceLoadTrace/AgentWorkTrace correlation preserved; local validation path mvn/npm tests plus `git diff --check`.

### TASK-ADIA-FU-010: Implement or verify trace-backed Agent Admin dashboard attention queues

- status: pending
- source: `ADIA-FU-AA-003`; `TASK-ADIA-02-003`
- depends on:
  - `TASK-ADIA-FU-009`
- required reads:
  - `app-description/domains/core-starter/workstreams/agent-admin/workstream.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/behavior.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/realization/source-alignment.md`
- skills:
  - `akka-backlog-item-to-task-brief`
  - `akka-runtime-feature-verification`
- expected outputs:
  - backend/frontend/tests or a narrower task brief for trace-backed dashboard attention
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `git diff --check`
- done criteria:
  - dashboard attention counts/cards are trace-backed for behavior proposals, approval-required proposals, provider/config blockers, and denied loader/tool-boundary events
- block criteria:
  - `implementation-gap` or `test-gap`
- notes:
  - vertical contract: Agent Admin functional agent workstream; attention category behavior-change proposal/approval-required/provider-config/loader-denial; role-specific dashboard / surface Agent Admin dashboard attention cards opening proposal/review/test-console/runtime-trace surfaces; surface graph node/action edge attention card to filtered surface and result surface; governed-tool id/type/exposure dashboard reads and runtime trace reads via `surface_action` and API; actor adapter/source `surface_action`, `api_call`, projection/internal trace source; confirmation/approval behavior no auto-activation and approval-required cards route to decision/review; idempotency/transaction/result behavior read-only counts and filtered result surfaces; capability managed-agent governance; AuthContext / roles / tenant scope SaaS-admin authorization/redaction; Akka substrate view/service/endpoint/frontend; API / frontend / realtime path Agent Admin dashboard API/surfaces; audit/work trace requirements trace ids backing counts without secret exposure; local validation path mvn/npm tests plus `git diff --check`.

### TASK-ADIA-FU-011: Verify Agent Admin proposal, chat-plan, idempotency, and partial-failure runtime path

- status: pending
- source: `ADIA-FU-AA-004`; `TASK-ADIA-02-003`
- depends on:
  - `TASK-ADIA-FU-010`
- required reads:
  - `app-description/domains/core-starter/workstreams/agent-admin/tests/coverage.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/tools/governed-tools.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/traces/work-traces.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/realization/source-alignment.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - runtime-validation scenario/run record or focused tests for proposal/chat-plan/idempotency behavior
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `git diff --check`
  - runtime smoke or authored scenario for the proposal/chat-plan path
- done criteria:
  - proposal save/review/activation denial/safe activation, repeated activation no-op/stale recovery, confirmed chat-plan proposal-only execution, and partial-failure trace/result surfaces are proven
- block criteria:
  - `auth-setup-blocker`, `provider-config-blocker`, `runtime-validation-gap`, or `test-gap`
- notes:
  - vertical contract: Agent Admin functional agent workstream; attention category proposal review/chat-plan/partial-failure; role-specific dashboard / surface proposal editor/review/detail/chat-plan/result surfaces; surface graph node/action edge save proposal, review, deny authority expansion, activate low-risk, repeat/stale retry, confirmed chat-plan result; governed-tool id/type/exposure prompt/skill/reference/profile proposal tools via `surface_action` and `human_chat_tool_plan`; actor adapter/source `surface_action`, `human_chat_tool_plan`, `api_call`, internal agent tooling; confirmation/approval behavior exact human confirmation and authority-expansion approval; idempotency/transaction/result behavior proposal/version idempotency, stale recovery, and partial-failure result surfaces; capability managed-agent governance; AuthContext / roles / tenant scope SaaS-admin scope; Akka substrate endpoint/service/entity/view/frontend; API / frontend / realtime path Agent Admin workstream API and chat-plan surfaces; audit/work trace requirements proposal, activation, denial, partial-failure, and work trace correlation; local validation path mvn/npm tests plus runtime smoke or scenario record.

### TASK-ADIA-FU-012: Verify Agent Admin runtime trace visibility across loaders, provider, and work traces

- status: pending
- source: `ADIA-FU-AA-005`; `TASK-ADIA-02-003`
- depends on:
  - `TASK-ADIA-FU-011`
- required reads:
  - `app-description/domains/core-starter/workstreams/agent-admin/traces/work-traces.md`
  - `app-description/domains/core-starter/workstreams/agent-admin/realization/source-alignment.md`
  - `specs/app-description-implementation-alignment/source-evidence-inventory.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - runtime trace visibility run record and/or trace tests
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `git diff --check`
  - runtime trace smoke or precise blocker record
- done criteria:
  - allowed/denied prompt assembly, skill/reference reads, tool-boundary denial, provider-missing, and workstream-agent invocations are visible through browser-safe trace surfaces
- block criteria:
  - `runtime-validation-gap`, `provider-config-blocker`, or `test-gap`
- notes:
  - vertical contract: Agent Admin functional agent workstream; attention category runtime trace/provider/loader denial; role-specific dashboard / surface runtime trace list/detail/test-console surfaces; surface graph node/action edge invoke loader/provider path, open trace rows/details, denied-load category result; governed-tool id/type/exposure runtime trace and loader tools via `agent_tool_call`, `surface_action`, and API; actor adapter/source `agent_tool_call`, `surface_action`, `api_call`, internal loader; confirmation/approval behavior no authority expansion and no secret body display; idempotency/transaction/result behavior read-only trace views and provider fail-closed result surfaces; capability managed-agent governance; AuthContext / roles / tenant scope SaaS-admin authorization/redaction; Akka substrate agent/service/entity/view/endpoint/frontend; API / frontend / realtime path Agent Admin runtime trace API/surfaces; audit/work trace requirements PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, ToolPermissionBoundary denial, provider fail-closed, AgentWorkTrace; local validation path mvn/npm tests plus runtime trace smoke.

### TASK-ADIA-FU-013: Reconcile Governance/Policy canonical governed-tool/action ids and aliases

- status: pending
- source: `ADIA-FU-GP-002`; `TASK-ADIA-02-004`
- depends on:
  - `TASK-ADIA-FU-012`
- required reads:
  - `app-description/domains/core-starter/workstreams/governance-policy/tools/governed-tools.md`
  - `app-description/domains/core-starter/workstreams/governance-policy/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/governance-policy/realization/source-alignment.md`
- skills:
  - `akka-backlog-item-to-task-brief`
  - `akka-runtime-feature-verification`
- expected outputs:
  - mapping update or narrower task brief for canonical Governance/Policy ids
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `git diff --check`
- done criteria:
  - each canonical Governance/Policy governed-tool/action id maps to exactly one backend capability/action path and result surface, with legacy aliases documented and tested only as compatibility wrappers
- block criteria:
  - `source-alignment-gap` or `test-gap`
- notes:
  - vertical contract: Governance/Policy functional agent workstream; attention category canonical-id/policy lifecycle reconciliation; role-specific dashboard / surface catalog/detail/draft/simulation/decision/rollback surfaces; surface graph node/action edge policy search/read/draft/simulate/submit/approve/activate/rollback/review-exception/read-history to result surfaces; governed-tool id/type/exposure `governance.policy.*` via `surface_action`, `human_chat_tool_plan`, and API; actor adapter/source `surface_action`, `human_chat_tool_plan`, `api_call`, internal service; confirmation/approval behavior decision-card approval and chat confirmation preserved; idempotency/transaction/result behavior policy proposal/version transactions and result/partial-failure surfaces; capability `governance-policy-lifecycle`; AuthContext / roles / tenant scope policy admin/operator tenant scope; Akka substrate endpoint/service/entity/view/frontend; API / frontend / realtime path Governance/Policy workstream API/frontend contracts; audit/work trace requirements policy action, denial, and correlation traces; local validation path mvn/npm tests plus `git diff --check`.

### TASK-ADIA-FU-014: Implement or verify Governance/Policy exception lifecycle

- status: pending
- source: `ADIA-FU-GP-003`; `TASK-ADIA-02-004`
- depends on:
  - `TASK-ADIA-FU-013`
- required reads:
  - `app-description/domains/core-starter/workstreams/governance-policy/behavior.md`
  - `app-description/domains/core-starter/workstreams/governance-policy/tools/governed-tools.md`
  - `app-description/domains/core-starter/workstreams/governance-policy/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/governance-policy/policies/policy-bindings.md`
  - `app-description/domains/core-starter/workstreams/governance-policy/traces/work-traces.md`
- skills:
  - `akka-backlog-item-to-task-brief`
  - `akka-runtime-feature-verification`
- expected outputs:
  - bounded exception lifecycle implementation/tests or narrower task brief if split is needed
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `git diff --check`
- done criteria:
  - exception request/review grant/deny/revoke/expire, expiry behavior, scoped runtime effect, hidden-target denial, idempotency, result surfaces, and traces are verified or implemented
- block criteria:
  - `implementation-gap`, `test-gap`, or `runtime-validation-gap`
- notes:
  - vertical contract: Governance/Policy functional agent workstream; attention category exception review/approval/expiry; role-specific dashboard / surface exception request/review/detail/result surfaces; surface graph node/action edge request exception, review grant/deny, revoke, expire, hidden-target denial; governed-tool id/type/exposure `governance.policy.review_exception` via `surface_action`, API, and optional `human_chat_tool_plan`; actor adapter/source `surface_action`, `api_call`, internal policy service; confirmation/approval behavior human exception approval and self/hidden-target denial; idempotency/transaction/result behavior exception lifecycle transaction, expiry, replay, and result/partial-failure surfaces; capability `governance-policy-lifecycle`; AuthContext / roles / tenant scope policy admin/operator/requester tenant scope; Akka substrate endpoint/service/entity/view/frontend; API / frontend / realtime path Governance/Policy exception API/surfaces; audit/work trace requirements exception request/review/revoke/expire/denial traces; local validation path mvn/npm tests plus runtime-validation scenario when authored.

### TASK-ADIA-FU-015: Verify runtime effective-policy decisions and policy-decision trace drill-in

- status: pending
- source: `ADIA-FU-GP-004`; `TASK-ADIA-02-004`
- depends on:
  - `TASK-ADIA-FU-014`
- required reads:
  - `app-description/domains/core-starter/workstreams/governance-policy/traces/work-traces.md`
  - `app-description/domains/core-starter/workstreams/governance-policy/tests/coverage.md`
  - `app-description/domains/core-starter/workstreams/governance-policy/realization/source-alignment.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - runtime trace drill-in run record and/or focused tests
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `git diff --check`
  - runtime smoke or precise blocker record
- done criteria:
  - allowed, denied, approval-required, and exception-authorized downstream actions show active policy version, matching clause/value, winning scope, exception status, approval status, and browser-safe drill-in
- block criteria:
  - `runtime-validation-gap`, `implementation-gap`, `auth-setup-blocker`, or `test-gap`
- notes:
  - vertical contract: Governance/Policy functional agent workstream; attention category runtime policy decision/denial/approval-required; role-specific dashboard / surface policy trace drill-in and affected workstream result surfaces; surface graph node/action edge downstream policy-affected action to policy-decision trace detail; governed-tool id/type/exposure policy enforcement/decision trace tools via `surface_action`, API, and internal policy service; actor adapter/source `surface_action`, `api_call`, internal_call; confirmation/approval behavior approval-gate status and exception authorization preserved; idempotency/transaction/result behavior downstream action result/denial/idempotent trace display; capability `governance-policy-lifecycle`; AuthContext / roles / tenant scope selected AuthContext tenant/scope; Akka substrate endpoint/service/view/frontend; API / frontend / realtime path protected workstream APIs and Audit/Trace drill-in; audit/work trace requirements policy-decision traces with redacted browser summaries; local validation path mvn/npm tests plus runtime smoke.

### TASK-ADIA-FU-016: Verify Governance/Policy activation, rollback, partial failures, and separation of duty

- status: pending
- source: `ADIA-FU-GP-005`; `TASK-ADIA-02-004`
- depends on:
  - `TASK-ADIA-FU-015`
- required reads:
  - `app-description/domains/core-starter/workstreams/governance-policy/policies/policy-bindings.md`
  - `app-description/domains/core-starter/workstreams/governance-policy/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/governance-policy/traces/work-traces.md`
  - `app-description/domains/core-starter/workstreams/governance-policy/realization/source-alignment.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - focused implementation/tests or runtime evidence for activation/rollback boundaries
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `git diff --check`
- done criteria:
  - self-approval denial, approved activation with/without required metadata, repeated activation/rollback behavior, partial-publication/not-committed result surfaces, append-only history, and trace links are proven
- block criteria:
  - `implementation-gap`, `test-gap`, `auth-setup-blocker`, or `runtime-validation-gap`
- notes:
  - vertical contract: Governance/Policy functional agent workstream; attention category activation/rollback/separation-of-duty/partial-failure; role-specific dashboard / surface policy decision/activation/rollback/history result surfaces; surface graph node/action edge approve, activate, rollback, repeat/conflict, partial-publication result; governed-tool id/type/exposure policy approve/activate/rollback tools via `surface_action`, API, optional `human_chat_tool_plan`; actor adapter/source `surface_action`, `api_call`, internal policy service; confirmation/approval behavior decision-card approval, self-approval denial, rollback confirmation; idempotency/transaction/result behavior activation/rollback transaction boundary, repeat idempotency/conflict, partial-failure result surfaces; capability `governance-policy-lifecycle`; AuthContext / roles / tenant scope policy admin/operator with separation-of-duty; Akka substrate endpoint/service/entity/view/frontend; API / frontend / realtime path Governance/Policy workstream API/surfaces; audit/work trace requirements activation, rollback, denial, partial-failure, append-only history traces; local validation path mvn/npm tests plus runtime smoke when available.

### TASK-ADIA-FU-017: Verify Governance/Policy model-backed impact-analysis provider paths and fail-closed behavior

- status: pending
- source: `ADIA-FU-GP-006`; `TASK-ADIA-02-004`
- depends on:
  - `TASK-ADIA-FU-016`
- required reads:
  - `app-description/domains/core-starter/workstreams/governance-policy/realization/source-alignment.md`
  - `specs/app-description-implementation-alignment/source-evidence-inventory.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - provider configured/fail-closed run record and trace evidence
- required checks:
  - `mvn test`
  - `git diff --check`
  - provider-missing fail-closed or configured-provider runtime smoke
- done criteria:
  - impact-analysis task creation/read, model-backed advisory result when configured, provider-missing fail-closed surfaces/traces, evidence refs, and browser secret-boundary evidence are recorded
- block criteria:
  - `provider-config-blocker`, `auth-setup-blocker`, or `runtime-validation-gap`
- notes:
  - vertical contract: Governance/Policy functional agent workstream; attention category impact-analysis/provider-config; role-specific dashboard / surface impact-analysis task/result/provider-blocked surfaces; surface graph node/action edge start impact analysis, read status/result, provider fail-closed, advisory result; governed-tool id/type/exposure impact-analysis/evidence tools via `surface_action`, API, and internal `agent_tool_call`; actor adapter/source `surface_action`, `api_call`, `agent_tool_call`; confirmation/approval behavior advisory only and no direct approval/activation/rollback; idempotency/transaction/result behavior task idempotency and blocked/result surfaces; capability `governance-policy-lifecycle`; AuthContext / roles / tenant scope policy admin/operator tenant scope; Akka substrate endpoint/service/entity/autonomous agent/view/frontend; API / frontend / realtime path Governance/Policy task API/surfaces; audit/work trace requirements impact task, evidence reads, provider fail-closed, denial traces; local validation path `mvn test`, provider smoke or fail-closed runtime smoke.

### TASK-ADIA-FU-018: Reconcile Audit/Trace canonical governed-tool ids and v2 surface contracts

- status: pending
- source: `ADIA-FU-AT-002`; `TASK-ADIA-02-005`
- depends on:
  - `TASK-ADIA-FU-017`
- required reads:
  - `app-description/domains/core-starter/workstreams/audit-trace/tools/governed-tools.md`
  - `app-description/domains/core-starter/workstreams/audit-trace/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/audit-trace/realization/source-alignment.md`
- skills:
  - `akka-backlog-item-to-task-brief`
  - `akka-runtime-feature-verification`
- expected outputs:
  - mapping update or narrower task brief for canonical Audit/Trace ids and v2 surfaces
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `git diff --check`
- done criteria:
  - each canonical Audit/Trace governed-tool id maps to one backend capability/action path and one frontend result surface, with legacy aliases documented and tested as compatibility wrappers only
- block criteria:
  - `source-alignment-gap` or `test-gap`
- notes:
  - vertical contract: Audit/Trace functional agent workstream; attention category canonical-id/v2-surface reconciliation; role-specific dashboard / surface search/detail/correlation/denial/summary v2 surfaces; surface graph node/action edge search/read detail/lookup correlation/investigate denied access/summarize to v2 result surfaces; governed-tool id/type/exposure `audit.trace.*` canonical tools via `surface_action`, read-only `human_chat_tool_plan`, API; actor adapter/source `surface_action`, `human_chat_tool_plan`, `api_call`, projection/internal trace source; confirmation/approval behavior read-only chat confirmation and no export approval change; idempotency/transaction/result behavior read-only idempotency and redacted/denied/partial-failure result surfaces; capability `audit-and-trace-investigation`; AuthContext / roles / tenant scope tenant admin/SaaS support/support-access; Akka substrate endpoint/service/view/projection/frontend; API / frontend / realtime path Audit/Trace workstream API/frontend contracts; audit/work trace requirements trace-read, denied-read, summary, and correlation traces; local validation path mvn/npm tests plus `git diff --check`.

### TASK-ADIA-FU-019: Implement or verify Audit/Trace support-access review and redacted export workflow depth

- status: pending
- source: `ADIA-FU-AT-003`; `TASK-ADIA-02-005`
- depends on:
  - `TASK-ADIA-FU-018`
- required reads:
  - `app-description/domains/core-starter/workstreams/audit-trace/access.md`
  - `app-description/domains/core-starter/workstreams/audit-trace/behavior.md`
  - `app-description/domains/core-starter/workstreams/audit-trace/surfaces/surfaces.md`
  - `app-description/domains/core-starter/workstreams/audit-trace/tools/governed-tools.md`
  - `app-description/domains/core-starter/workstreams/audit-trace/policies/policy-bindings.md`
- skills:
  - `akka-backlog-item-to-task-brief`
  - `akka-runtime-feature-verification`
- expected outputs:
  - bounded support-access/export implementation/tests or narrower task brief if split is needed
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `npm --prefix frontend run typecheck`
  - `git diff --check`
- done criteria:
  - support-access grant/use/expiry/revoke review, self-approval denial, redacted export approval-required/denied/ready/expired states, idempotent replay, no raw browser download URL, and trace refs are verified or implemented
- block criteria:
  - `implementation-gap`, `test-gap`, `auth-setup-blocker`, or `runtime-validation-gap`
- notes:
  - vertical contract: Audit/Trace functional agent workstream; attention category support-access review/export approval; role-specific dashboard / surface support-access review/redacted export/result/timeline surfaces; surface graph node/action edge grant/use/expiry/revoke support access, request export, approve/deny/ready/expired export, self-approval denial; governed-tool id/type/exposure support-access and redacted export tools via `surface_action`, API, optional read-only `human_chat_tool_plan`; actor adapter/source `surface_action`, `api_call`, internal audit/export service; confirmation/approval behavior export/support-access approval and self-approval denial; idempotency/transaction/result behavior export request idempotency, approval transaction, ready/expired/denied result surfaces; capability `audit-and-trace-investigation`; AuthContext / roles / tenant scope tenant admin/SaaS support/support-access; Akka substrate endpoint/service/entity/view/frontend; API / frontend / realtime path Audit/Trace export/support APIs and surfaces; audit/work trace requirements support-access and export approval traces without raw URLs; local validation path mvn/npm tests plus runtime-validation scenario when authored.

### TASK-ADIA-FU-020: Verify Audit/Trace trace-gap and runtime-validation evidence linking

- status: pending
- source: `ADIA-FU-AT-004`; `TASK-ADIA-02-005`
- depends on:
  - `TASK-ADIA-FU-019`
- required reads:
  - `app-description/domains/core-starter/workstreams/audit-trace/traces/work-traces.md`
  - `app-description/domains/core-starter/workstreams/audit-trace/realization/akka-components.md`
  - `app-description/domains/core-starter/workstreams/audit-trace/tests/coverage.md`
  - `specs/runtime-validation/README.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - trace-gap/evidence-link implementation/tests or runtime-validation scenario/run record
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `git diff --check`
- done criteria:
  - runtime-validation evidence links, trace-gap diagnostics, dashboard attention counts, timeline gap display, source-alignment refs, redaction, and tenant/support authorization are proven
- block criteria:
  - `implementation-gap`, `test-gap`, `runtime-validation-gap`, or `seed-data-blocker`
- notes:
  - vertical contract: Audit/Trace functional agent workstream; attention category trace-gap/runtime-validation evidence link; role-specific dashboard / surface trace-gap attention/timeline/evidence-link/source-alignment surfaces; surface graph node/action edge ingest/display evidence link, detect missing correlation/producer evidence, open gap detail; governed-tool id/type/exposure trace-gap and evidence-link tools via `surface_action`, API, projection/internal consumer; actor adapter/source `surface_action`, `api_call`, consumer/internal trace source; confirmation/approval behavior read-only diagnostics and no approval unless export/support flow is invoked; idempotency/transaction/result behavior evidence-link idempotency, gap result surfaces, partial malformed evidence handling; capability `audit-and-trace-investigation`; AuthContext / roles / tenant scope tenant admin/SaaS support/support-access; Akka substrate endpoint/service/view/consumer/frontend; API / frontend / realtime path Audit/Trace evidence-link and timeline surfaces; audit/work trace requirements gap diagnostics and source-alignment impact refs with redaction; local validation path mvn/npm tests plus runtime-validation scenario/run record if authored.

### TASK-ADIA-FU-021: Verify Audit/Trace read-only chat plans, bounded agent tools, and summary provider paths

- status: pending
- source: `ADIA-FU-AT-005`; `TASK-ADIA-02-005`
- depends on:
  - `TASK-ADIA-FU-020`
- required reads:
  - `app-description/domains/core-starter/workstreams/audit-trace/tools/governed-tools.md`
  - `app-description/domains/core-starter/workstreams/audit-trace/traces/work-traces.md`
  - `app-description/domains/core-starter/workstreams/audit-trace/realization/source-alignment.md`
- skills:
  - `akka-runtime-feature-verification`
- expected outputs:
  - provider configured/fail-closed and read-only chat/agent-tool run record or focused tests
- required checks:
  - `mvn test`
  - `npm --prefix frontend test -- --run`
  - `git diff --check`
  - provider-missing fail-closed or configured-provider runtime smoke
- done criteria:
  - summary start/read/review provider-missing fail-closed, configured-provider summary when available, confirmed read-only chat plans, bounded agent-tool allow/deny, tool-boundary denial traces, partial-failure surfaces, and browser secret-boundary evidence are recorded
- block criteria:
  - `provider-config-blocker`, `auth-setup-blocker`, `implementation-gap`, `test-gap`, or `runtime-validation-gap`
- notes:
  - vertical contract: Audit/Trace functional agent workstream; attention category summary provider/read-only chat/tool-boundary denial; role-specific dashboard / surface summary task/chat-plan/search/detail/correlation/denial result surfaces; surface graph node/action edge start summary, read/review result, confirmed read-only search/detail/correlation/denial chat plan, bounded agent-tool allow/deny, provider fail-closed; governed-tool id/type/exposure audit trace read/summary tools via `human_chat_tool_plan`, `agent_tool_call`, `surface_action`, API; actor adapter/source `human_chat_tool_plan`, `agent_tool_call`, `surface_action`, `api_call`; confirmation/approval behavior exact read-only chat confirmation and no mutation/export approval; idempotency/transaction/result behavior summary task idempotency, read-only result, denial, and partial-failure surfaces; capability `audit-and-trace-investigation`; AuthContext / roles / tenant scope tenant admin/SaaS support/support-access; Akka substrate endpoint/service/autonomous agent/entity/view/frontend; API / frontend / realtime path Audit/Trace workstream API/chat/task surfaces; audit/work trace requirements summary, tool-boundary denial, provider fail-closed, trace-read correlation; local validation path mvn/npm tests plus provider smoke or fail-closed runtime smoke.

### TASK-ADIA-FU-022: Author or execute expanded User Admin runtime-validation coverage

- status: pending
- source: `ADIA-FU-UA-004`; `TASK-ADIA-02-002`
- depends on:
  - `TASK-ADIA-FU-021`
- required reads:
  - `app-description/domains/core-starter/workstreams/user-admin/tests/coverage.md`
  - `app-description/domains/core-starter/workstreams/user-admin/traces/work-traces.md`
  - `app-description/domains/core-starter/workstreams/user-admin/realization/source-alignment.md`
  - `specs/runtime-validation/README.md`
- skills:
  - `akka-runtime-feature-verification`
  - `akka-backlog-item-to-task-brief`
- expected outputs:
  - bounded runtime-validation scenarios/run records or narrower task briefs for role/status/support/identity/access-review/chat-plan coverage
- required checks:
  - `git diff --check`
  - scenario authoring validator if a scenario queue is created, or runtime scenario execution/run record if executing coverage
- done criteria:
  - role/status/last-admin/self-action denial, support-access grant/revoke/expiry, identity exception recovery, access-review advisory, exact chat-plan confirmation/denial/partial-failure, and audit/trace reauthorization coverage is either executed or split into runnable scenario tasks
- block criteria:
  - `auth-setup-blocker`, `provider-config-blocker`, or `runtime-validation-gap`
- notes:
  - vertical contract: User Admin functional agent workstream; attention category role/status/support-access/identity/access-review/chat-plan; role-specific dashboard / surface user list/detail/admin-audit/access-review/chat-plan result surfaces; surface graph node/action edge role change, membership status, support access, identity exception, access-review advisory, confirmed chat-plan, denied/partial-failure result; governed-tool id/type/exposure membership/role/support/access-review/admin-audit tools via `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, API; actor adapter/source `surface_action`, `human_chat_tool_plan`, `agent_tool_call`, `api_call`; confirmation/approval behavior risky admin/chat confirmation and last-admin/self-action denial; idempotency/transaction/result behavior membership/support/access-review transaction, idempotency, and partial-failure result surfaces; capability `user-and-access-administration`; AuthContext / roles / tenant scope organization-admin/member/support scopes; Akka substrate endpoint/service/entity/autonomous agent/view/frontend; API / frontend / realtime path User Admin workstream/admin APIs and surfaces; audit/work trace requirements admin action, access-review, support, identity, denial traces; local validation path scenario authoring/execution with run records plus `git diff --check`.
