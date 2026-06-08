# Shared Baseline Contracts: Full-Core SMB Baseline and UX

## Purpose

This file is the executable shared contract for full-core SMB child mini-projects. Workstream tasks should inherit these contracts instead of restating the umbrella baseline, then add only their domain-specific capabilities, surfaces, traces, and checks.

Target implementation areas:

- executable starter: `templates/ai-first-saas-starter/`
- synchronized root UI, when starter frontend behavior changes: `frontend/`
- validation scripts and focused checks: `tools/`, starter backend/frontend tests, and child-project task checks

## Non-negotiable completion rule

A user-facing runtime feature is not complete when its normal path is deterministic/demo/model-less, fixture-only, or a direct provider/service call that bypasses the governed Akka runtime.

Model-backed workstream behavior must:

1. invoke a concrete Akka `Agent` component through the governed runtime assembly path;
2. use configured provider/model boundaries;
3. emit durable prompt, skill/reference load, tool, model-call, work-result, denial, and provider-failure traces;
4. fail closed with actionable typed `system_message` surfaces when provider/security configuration is absent.

Fixtures, mocks, deterministic fakes, and canned responses are allowed only in explicitly named test/fixture adapters.

## Shared workstream shell contract

Every child workstream must preserve the functional-agent shell model:

- top rail contains only authorized functional workstreams such as User Admin, Agent Admin, Audit/Trace, and Governance/Policy;
- My Account opens from the lower-left signed-in user tile/email and is not duplicated as a top-rail workstream;
- each workstream has descriptor-backed identity: `workstreamId`, display name, semantic icon id, visual hint, accent token, tooltip, accessible label, and optional approved asset reference;
- rail selection, prompt-entered navigation, deep links, My Account launches, and surface actions all enter one shell request pipeline;
- backend capability checks, selected `AuthContext`, tenant/customer scope, active membership, role/capability, account status, and disabled-user handling decide the result;
- unauthorized or unsupported requests return typed `system_message` surfaces, not hidden frontend-only affordances;
- attention badges and current status come from backend projections or protected APIs, not frontend-only state.

## Structured surface contract

All consequential output must be a typed structured surface. `markdown_response` is acceptable only for bounded explanations, summaries, and guidance that do not conceal a table, form, dashboard, audit timeline, decision card, diff, policy, task-progress panel, or error state.

Every structured surface must define:

- stable `surfaceId`, `surfaceType`, `version`, owner workstream/functional agent, placement, and payload schema;
- state coverage for loading, empty, ready, submitting, success, warning, error, forbidden, stale/reconnect, approval-needed, provider-blocked, and no-op where relevant;
- action descriptors with outcome-oriented labels and one of these action kinds: read/surface-request, command, proposal, approval, workflow, governance, trace, or open-workstream;
- mapped governed capability id for each action, query, API call, agent tool, workflow step, timer, consumer reaction, and internal worker operation;
- trace/correlation ids for protected reads, denials, side effects, decisions, prompt assembly, skill/reference loads, tool calls, model calls, and worker lifecycle events;
- redaction/permission metadata when fields, actions, traces, or workstreams are unavailable;
- accessible headings, labels, focus behavior, live status text, keyboard paths, and non-color-only status semantics.

## `system_message` contract

Use typed `system_message` surfaces for safe, actionable non-happy paths. They are first-class surfaces, not throwaway text.

Required `system_message` cases:

- authorization denial, disabled user, missing membership, unsupported context, and forbidden workstream access;
- validation errors and idempotent no-op mutations;
- provider missing/misconfigured, model unavailable, tool denied, prompt/skill/reference unavailable, and secret-boundary failures;
- deferred work, stale/reconnect, cancelled/retryable worker task, failed workflow/timer/consumer reaction, and approval-required states;
- backend-denied manually submitted actions even when the frontend hid the action.

Each `system_message` must carry browser-safe reason, recovery action(s), capability id where applicable, trace/correlation id, severity, and redaction status. It must not expose provider secrets, hidden prompt text, hidden tenant/customer data, or support-only details.

## Capability and authority contract

Child workstream tasks must name capability ids before implementing UI or agent behavior.

For each capability record:

- capability id and owning workstream;
- request origin(s): rail, composer, deep link, My Account, surface action, API, workflow, timer, consumer, request/response Agent tool, or AutonomousAgent/internal worker;
- required `AuthContext`, tenant/customer scope, membership status, account status, role/capability basis, and denial reasons;
- deterministic service responsibilities for authorization, validation, idempotency, lifecycle, projection, redaction, policy checks, provider readiness, and secret-boundary checks;
- side effects, outbox/email behavior, workflow/timer/consumer interaction, or no-side-effect guarantee;
- trace fields and surface trace links;
- local runtime validation command(s) and expected provider-missing behavior.

AI must not own authorization, idempotency, tenant filtering, lifecycle transitions, trace normalization, redaction, or policy enforcement.

## Agent and internal-worker contract

Use request/response Akka `Agent` for user-facing workstream turns from the persistent composer and for bounded help, explanation, summarization, recommendation, classification, or guided action planning.

Use Akka `AutonomousAgent` or internal worker task semantics only when the work needs durable task identity, progress, dependencies, cancellation, failure/retry, notifications, iteration beyond one response, or human result review.

User-facing Agent paths must show:

- producing functional agent;
- selected context and capability basis;
- provider/loading/generation/provider-blocked states;
- prompt assembly, governed skill/reference loads, tool permission decisions, model invocation, and work-result traces;
- safe `system_message` surfaces for denied tools/capabilities and provider failures.

Internal-worker paths must show task purpose, owner, status, progress, blockers, dependencies, result/recommendation, review/approval/retry/cancel actions where authorized, and trace links.

## Audit/work trace contract

Every child workstream must emit or preserve durable traces for:

- protected reads and scoped queries;
- authorization denials, validation failures, disabled-user denials, no-op decisions, and policy decisions;
- side effects, outbox/email attempts, workflows, timers, consumers, and lifecycle transitions;
- prompt assembly, AgentDefinition/version selection, skill/reference loads, tool permission checks, model calls, provider failures, and work results;
- AutonomousAgent/internal worker task start/progress/failure/cancel/result/review/accept/reject;
- human approvals, exceptions, governance decisions, behavior changes, and trace exports/copies.

Browser DTOs must include only redacted, scoped, browser-safe trace references. Audit/Trace child work may deepen timelines, but every originating workstream must provide correlation ids near consequential surfaces and actions.

## Attention and dashboard contract

Each full-core workstream dashboard must answer:

1. what is happening;
2. what needs attention now;
3. what is blocked, risky, overdue, failed, stale, or waiting for approval;
4. which humans, request/response Agents, internal workers, workflows, timers, or deterministic services are participating;
5. what the current user is authorized to do next;
6. where to inspect traces and evidence.

Attention items must be backend-derived, scoped by authority, and safe to aggregate in My Account without leaking hidden workstreams, tenant/customer data, secrets, or support-only details.

## Visual contract

Use `docs/web-ui-style-guide.md` style id `atlas-ops-supervisory-console` unless a later child records an accepted style brief that preserves the same AI-first anatomy.

Required visual qualities:

- workstream-first, not page-first CRUD;
- calm operational supervision interface for delegated work, decisions, exceptions, policy boundaries, auditability, and outcomes;
- attention/decisions/risk above FYI metrics;
- professional descriptor-backed icons, never letter-only or arbitrary emoji as normal fallback;
- compact cards, clear spacing, attractive density, readable typography, responsive stacking, and no color-only meaning;
- trace links near consequential results and actions;
- request/response Agent, AutonomousAgent/internal worker, workflow/timer, and deterministic service states visually distinct when present;
- no fake/demo metric claims unless explicitly labeled fixture/test data.

## Accessibility and responsive contract

UI work must preserve:

- WCAG-aware contrast for text, controls, borders, badges, and focus rings;
- keyboard access for rail selection, composer, surface actions, forms, tables, dialogs, and deep links;
- focus management when surfaces append, refresh, error, or open from shell requests;
- screen-reader labels for icons, status badges, charts, traces, and action results;
- reduced-motion behavior for updates, working states, denials, reconnects, and approval results;
- narrow-screen layouts that keep context/authority indicators and top attention/actions visible before lower-priority reports;
- textual alternatives for charts/tables where needed.

## Provider and secret-boundary contract

Provider readiness is a protected runtime state, not a frontend assumption.

- normal model-backed behavior must call the governed Agent/provider path;
- missing provider/model/security configuration must fail closed with a typed `system_message`, trace, and recovery guidance;
- backend secrets, provider credentials, hidden prompts, hidden skill/reference text, tool credentials, support-only details, and cross-tenant data must never appear in browser assets, API DTOs, frontend logs, traces visible to unauthorized users, or denial text;
- secret-boundary scans are required for UI/runtime changes that touch provider config, prompt/skill/reference content, generated assets, or trace payloads.

## Runtime validation map

Child tasks should choose the smallest set that validates their scope, but must not replace runtime validation with static checks when behavior is runtime/API/UI-visible.

| Area | Minimum validation expectation |
|---|---|
| Queue/doc contract | `git diff --check` and targeted `rg` proof for workstream shell, structured surface, `system_message`, provider, trace, visual, runtime validation, and secret-boundary terms. |
| Backend capability | Focused unit/integration tests for authorization, tenant/customer filtering, disabled-user denial, validation, idempotency/no-op, lifecycle/policy rules, trace emission, and redaction. |
| Agent runtime | Local Akka path invokes concrete `Agent`; provider-configured smoke when environment exists; provider-missing path fails closed with `system_message` and traces. No deterministic/model-less normal completion. |
| Internal worker | Task lifecycle tests for start/read/progress/failure/cancel/result/review where implemented; traces for lifecycle and model/tool calls. |
| API and surface action | Protected API tests for allowed/denied requests, forged/manual action denial, capability id mapping, correlation ids, and browser-safe DTOs. |
| UI rendering | Tests or stories for ready/loading/empty/error/forbidden/provider-blocked/no-op/stale/approval-needed states, action labels, trace links, and redaction indicators. |
| Visual quality | Review against `visual-ux-quality-standard.md`: attention-first hierarchy, professional workstream identity, responsive density, accessible state semantics, no generic page-first CRUD regression. |
| Accessibility/responsive | Keyboard/focus checks, label checks, reduced-motion behavior, narrow layout with context/authority/attention preserved. |
| Secret-boundary | Scan built frontend/assets/API payloads for secrets, provider credentials, hidden prompts, hidden tenant/customer data, and support-only details. |
| Broad starter changes | Run or explicitly justify deferring `tools/validate-ai-first-saas-starter-fullstack.sh`; keep `templates/ai-first-saas-starter/` and root `frontend/` synchronized when applicable. |

## Child mini-project handoff checklist

Before a workstream child marks a slice complete, it must record:

- [ ] capability ids and authority/tenant/customer rules;
- [ ] structured dashboard/surface contracts and `system_message` states;
- [ ] request/response Agent behavior and provider fail-closed path;
- [ ] AutonomousAgent/internal worker tasks only where durable lifecycle is justified;
- [ ] deterministic services and policy/idempotency/redaction boundaries;
- [ ] audit/work trace fields and trace-link surfaces;
- [ ] visual, accessibility, responsive, and icon acceptance criteria;
- [ ] runtime validation commands and provider-missing expected results;
- [ ] secret-boundary validation for browser/API/generated assets;
- [ ] explicit note that deterministic/demo/model-less normal runtime behavior was not used to claim model-backed completion.
