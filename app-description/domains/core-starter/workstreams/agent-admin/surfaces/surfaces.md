# Surfaces: Agent Admin

## Workstream placement

Agent Admin is a role-authorized functional-agent workstream owned by `agent-admin-agent`. Routes and deep links reopen structured surfaces; they do not define application meaning. The default workstream entry is `surface-agent-admin-dashboard`, an attention-first command center backed by capability `managed-agent-governance`.

Reusable placements:

- Governance/Policy may reuse behavior proposal, prompt/skill/reference diff, tool-boundary simulation, model-policy, prompt-risk review, activation, and rollback decision surfaces.
- Audit/Trace may reuse all Agent Admin trace drill-ins and diagnostic drawers.
- My Account may link to Agent Admin dashboard attention through backend-owned workstream attention counters.

## Collection-object progression

Durable collection objects in this workstream use the canonical progression: list/search discovery → lifecycle-aware show/inspection → separate create/proposal/edit/destructive-lifecycle/task surfaces.

| Collection object | List/search | Show/inspection | Create/proposal/edit task surfaces | Destructive/lifecycle surfaces |
|---|---|---|---|---|
| Managed `AgentDefinition` | `surface-agent-admin-catalog` | `surface-agent-admin-detail` | `surface-agent-prompt-governance`, `surface-agent-skill-manifest-diff`, `surface-agent-tool-boundary-diff`, `surface-agent-model-refs`, `surface-agent-test-console`, `surface-agent-admin-prompt-risk-review` | `surface-agent-activation-confirmation`, `surface-agent-deactivation-confirmation`, `surface-agent-rollback-confirmation` |
| Prompt/skill/reference/version artifacts | catalog/detail artifact cards on `surface-agent-admin-detail` | prompt/manifest/tool/model inspection cards | `surface-agent-prompt-governance`, `surface-agent-skill-manifest-diff`, `surface-agent-tool-boundary-diff`, `surface-agent-model-refs` | activation/rollback decision surfaces returned from behavior proposal actions |
| Seed material | `surface-agent-seed-material` | seed provenance row/card inspection | seed import workflow/status surface | customization-preserving import confirmation; no raw tenant override deletion |
| Runtime traces | trace links from every surface | `surface-agent-admin-trace` | trace investigation requests | none; trace export/escalation is owned by Audit/Trace |

## Surface contracts

### `surface-agent-admin-dashboard` — Agent Admin command center

- Identity and ownership: `surface-agent-admin-dashboard`; type `dashboard`; contract `agent_admin.dashboard.v1`; role: workstream starting surface; owning workstream `Agent Admin`; owning functional agent `agent-admin-agent`; primary governed capability `managed-agent-governance`.
- Placement: default Agent Admin entry surface from the workstream shell, reopenable from backend-owned My Account/Governance/Audit attention links when the selected `AuthContext` is authorized. Customer-scoped Agent Admin contexts return a safe forbidden/context state rather than implying customer-admin authority.
- Purpose and user goal: show what needs managed-agent governance attention and open the next safe surface for catalog inspection, behavior proposals, prompt/model/tool readiness, seed-material review, and trace investigation.
- Default-visible payload schema:
  - `surfaceSummary`: id, title, type, contract, owning workstream/agent labels, selected scope label, readiness state, and last refreshed time.
  - `scopeSummary`: selected `AuthContext` id, scope type (`saas-owner`, `tenant`, or `organization`), tenant/organization display name, actor role summary, and whether the context is governance-authorized; customer ids/names are omitted unless the actor is already authorized to see that customer through another surface.
  - `attentionSections[]`: ordered top-to-bottom as things needing attention before things the actor can do; each item has a user label, count/status, severity, reason summary, `actionId`, `targetSurfaceId`, governed capability, and disabled/omitted reason when the target cannot be shown.
  - `readinessSummary`: managed-agent count by lifecycle/readiness, provider/model readiness category, prompt-risk review readiness/deferred state, and no-fake-success copy when model/provider readiness is blocked.
  - `approvalQueueCounts`: proposal, high-risk prompt/tool/model change, activation, rollback, and deferred-review counts; every visible count opens a governed review or empty queue surface.
  - `manifestDrift`: skill/reference/tool-boundary drift summaries with impacted agent display names, drift severity, and review targets.
  - `loaderDenialState`: recent governed loader-tool denial summaries by safe category, affected agent display names, and recovery target; raw tool inputs, prompt bodies, and evidence documents are excluded.
  - `authorityExpansionAttempts`: risky authority/tool/data-boundary expansion attempts with approval state, policy-readable reason, and proposal target.
  - `seedImportReadiness`: seed material import/customization preservation state, blocked reasons, and import task target.
  - `providerModelStatus`: provider/runtime/model readiness summary, configured/not-configured/degraded categories, fail-closed reason, and link to model-reference review; provider secrets, raw API errors, and credentials are never included.
  - `safeRedactionSummary`: browser-safe omissions for prompts, skills, references, provider data, hidden tenants/customers, and privileged trace details.
  - `authorizedActions[]`: dashboard-level actions the backend has authorized for the selected context.
- Diagnostics drawer payload: role-gated and visually subordinate capability ids, governed browser/tool ids, compact contract names, redacted trace/correlation ids, idempotency keys, and action routing metadata. Diagnostics never include raw provider secrets, raw prompt/skill/reference bodies, hidden tenant/customer identifiers, JWT/session material, or internal stack traces.
- Governed action mapping:
  - `action-agent-admin-open-catalog` -> `surface-agent-admin-catalog`; capability `managed-agent-governance`; result opens the catalog list/search surface scoped to the selected context.
  - `action-agent-admin-open-behavior-proposals` -> `surface-agent-behavior-proposal`; capability `managed-agent-governance`; result opens the next authorized behavior proposal decision or an empty/no-pending system message.
  - `action-agent-admin-open-prompt-risk-review` -> `surface-agent-admin-prompt-risk-review`; capability `managed-agent-governance`; starts or reads the prompt-risk autonomous review readiness/progress/result and must fail closed when provider/runtime readiness is unavailable.
  - `action-agent-admin-open-seed-material` -> `surface-agent-seed-material`; capability `managed-agent-governance`; result lists authorized seed material without raw tenant overrides.
  - `action-agent-admin-open-manifest-drift` -> `surface-agent-skill-manifest-diff`; capability `managed-agent-governance`; result opens the selected manifest/reference diff review.
  - `action-agent-admin-open-tool-boundary` -> `surface-agent-tool-boundary-diff`; capability `managed-agent-governance`; result opens the selected governed tool-boundary simulation/review.
  - `action-agent-admin-open-model-refs` -> `surface-agent-model-refs`; capability `managed-agent-governance`; result opens model/provider reference inspection with fail-closed provider readiness.
  - `action-agent-admin-open-trace` -> `surface-agent-admin-trace`; capability `audit.trace.read` plus Agent Admin trace visibility; result opens redacted trace timeline or a no-enumeration denial.
  - `action-agent-admin-refresh-dashboard` -> `surface-agent-admin-dashboard`; capability `managed-agent-governance`; idempotent read-only refresh with a new correlation id.
- Authority and tenant rules: backend resolves JWT identity plus selected `AuthContext`; SaaS Owner/App Admin and tenant/organization admin contexts require `managed-agent-governance`; customer-scoped or hidden/cross-tenant contexts return forbidden/not-found-or-redacted system-message states without hidden counts, missing capability names, or cross-scope identifiers. Browser controls are advisory; backend capability, policy, approval, provider readiness, and tenant/customer scope remain authoritative.
- Audit/work trace: every dashboard read and action emits a work trace with actor, selected context, capability decision, surface id, action id, result surface id/status, redaction profile, trace refs, correlation id, and idempotency key where applicable. User-visible trace links summarize who/what/why; raw trace detail is available only through role-gated Agent Admin or Audit/Trace drill-ins.
- States: loading, empty, ready, submitting, forbidden, not-found-or-redacted, stale/reconnect, partial-data, provider-fail-closed, approval-required, validation-error, no-op refresh, and failure. Provider/model/tool/runtime unavailable states must render fail-closed copy and recovery actions rather than fabricated success.
- Accessibility/responsive/style binding: use the selected web UI style guide, named-theme tokens, dashboard/card/counter/action-bar/diagnostics-drawer component catalog anatomy, keyboard-operable counters/cards, focus order matching the attention-first layout, high-contrast severity labels, and responsive single-column to multi-card layouts without hiding actions behind pointer-only controls.
- Tests: app-description and realization tests must cover default Agent Admin entry as `surface-agent-admin-dashboard`, backend-authorized selected AuthContext and tenant/organization scoping, customer-scoped/hidden-context denial with no leaked counts, attention before actions, every visible counter/card/action returning a typed target surface, provider/model fail-closed prompt-risk readiness, manifest/tool-boundary/model/seed/proposal trace action routing, redaction of prompts/skills/references/provider secrets/JWTs/hidden ids, audit/work trace and correlation evidence, stale/reconnect/partial/failure states, and responsive keyboard behavior.
- Sufficiency review: sufficient for implementation; a developer or generator can implement and review the dashboard without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics, and the default view avoids raw implementation ids except in role-gated diagnostics.

### `surface-agent-admin-catalog` — Managed agent catalog

- Identity and ownership: `surface-agent-admin-catalog`; type `list-search`; contract `agent_admin.catalog.v1`; role: collection discovery for managed `AgentDefinition` objects; owning workstream `Agent Admin`; owning functional agent `agent-admin-agent`; primary governed capability `managed-agent-governance`.
- Placement: opened from `surface-agent-admin-dashboard` action `action-agent-admin-open-catalog`, from authorized workstream deep links, and from empty/readiness states that need a catalog return path. The catalog is not a lifecycle or edit surface; it delegates every row selection to `surface-agent-admin-detail` and delegates behavior/lifecycle work to separate task or confirmation surfaces.
- Purpose and user goal: help an authorized SaaS Owner/App Admin, tenant admin, or organization admin find a governed managed agent, understand its readiness at a glance, and open the backend-authorized inspection surface without exposing raw prompt, skill, reference, provider, or hidden tenant/customer data.
- Default-visible payload schema:
  - `catalogSummary`: surface id/title/type/contract, selected scope label, result count, filtered count, readiness counts, lifecycle counts, provider readiness counts, seed/customization summary, last refreshed time, and empty-state reason when no agents are visible.
  - `scopeSummary`: selected `AuthContext` id, scope type (`saas-owner`, `tenant`, or `organization`), tenant/organization display name, actor role summary, and governance authorization state. Customer-scoped or hidden/cross-tenant contexts return a safe forbidden/not-found-or-redacted system-message state instead of this catalog.
  - `filters`: browser-safe search text, lifecycle filters, readiness filters, authority-tier filters, provider-readiness filters, seed/customization filters, sort key/direction, page cursor, and page size. Filters are advisory request inputs; the backend remains authoritative for visibility and ordering.
  - `agents[]`: one row/card per visible managed agent with `displayName`, short purpose, lifecycle state, readiness state, authority tier, provider/model readiness category, prompt-risk status, seed/customization state, attention summary, last changed/reviewed timestamps, redaction note, `openActionId`, `targetSurfaceId`, and safe row context needed to open inspection. Rows never include raw prompt/skill/reference bodies, provider secrets, hidden tenant/customer identifiers, loader-tool input, JWT/session material, or full trace documents.
  - `emptyState`: user-readable reason and recovery actions for no visible agents, no filter matches, forbidden scope, stale data, provider/runtime fail-closed readiness, or partial data.
  - `authorizedActions[]`: catalog-level refresh/search/reset-filters/open-trace actions that the backend authorizes for the selected context.
  - `safeRedactionSummary`: categories omitted from the browser payload, including prompt and skill bodies, reference content, provider credentials, model internals, hidden scopes, raw trace evidence, and privileged policy diagnostics.
- Diagnostics drawer payload: role-gated and visually subordinate `AgentDefinition` id, model ref id, prompt/manifest/tool-boundary version labels, trace policy label, redacted trace/correlation ids, capability ids, page/filter request metadata, and action routing metadata. Diagnostics never include provider credentials, raw prompt/skill/reference bodies, hidden scope identifiers, bearer tokens, full evidence documents, raw tool inputs, or internal stack traces.
- Governed action mapping:
  - `action-agent-admin-refresh-catalog` -> `surface-agent-admin-catalog`; capability `managed-agent-governance`; read-only refresh preserving safe filters and returning a new correlation id.
  - `action-agent-admin-search-catalog` -> `surface-agent-admin-catalog`; capability `managed-agent-governance`; applies backend-validated search/filter/sort/page inputs and returns an updated list or typed empty state.
  - `action-agent-admin-reset-catalog-filters` -> `surface-agent-admin-catalog`; capability `managed-agent-governance`; idempotently clears advisory filters and returns the default authorized catalog view.
  - `action-open-agent-detail` -> `surface-agent-admin-detail`; capability `managed-agent-governance`; opens the selected visible managed-agent inspection using backend row context and denies hidden/stale/cross-scope rows without enumeration.
  - `action-agent-admin-catalog-open-trace` -> `surface-agent-admin-trace`; capability `audit.trace.read` plus Agent Admin trace visibility; opens a redacted catalog-read/action trace or returns a safe no-enumeration denial.
- Authority and tenant rules: backend resolves JWT identity plus selected `AuthContext`; SaaS Owner/App Admin and tenant/organization admin contexts require `managed-agent-governance`; visibility is limited to managed agents in the selected governance scope. Customer-scoped, hidden, inactive, stale, or cross-tenant contexts produce forbidden/not-found-or-redacted outcomes without leaking hidden counts, missing capability names, managed-agent ids, tenant/customer ids, or provider/model internals. Browser filters, row ids, and disabled controls never grant authority.
- Audit/work trace: every catalog read, search/filter/page request, row-open attempt, denial, refresh, and trace drill-in emits a work trace with actor, selected context, capability decision, surface id, action id, target surface id, safe filter/page summary, row visibility decision, result status, redaction profile, trace refs, correlation id, and idempotency key where applicable. User-visible trace links summarize who/what/why; raw trace detail is role-gated to Agent Admin or Audit/Trace drill-ins.
- States: loading, ready, empty-no-agents, empty-no-filter-matches, submitting/searching, forbidden, not-found-or-redacted, stale/reconnect, partial-data, provider-fail-closed, validation-error for invalid filters/page cursors, no-op refresh/reset, and failure. Provider/model/tool/runtime unavailable states must explain that readiness is blocked or partial without fabricating provider success.
- Accessibility/responsive/style binding: use the selected web UI style guide, named-theme tokens, list/search/table-card/filter-chip/action-bar/diagnostics-drawer component catalog anatomy, keyboard-operable filters, pagination, rows/cards, row-open controls, and trace links, visible focus order from filters to result cards to actions, high-contrast readiness/lifecycle labels, and responsive single-column cards through multi-column/table layouts without hiding row-open actions behind pointer-only controls.
- Tests: app-description and realization tests must cover catalog opening from the dashboard action, backend-authorized selected AuthContext and tenant/organization scoping, customer-scoped/hidden/cross-tenant denial with no leaked counts or ids, search/filter/sort/page validation, empty and partial states, every visible row/card opening `surface-agent-admin-detail` through `action-open-agent-detail`, stale or hidden row denial as a safe typed system message, absence of inline activation/deactivation/rollback mutations, provider/model fail-closed readiness copy, redaction of prompts/skills/references/provider secrets/JWTs/hidden ids/raw traces, audit/work trace and correlation evidence, no-op refresh/reset behavior, and responsive keyboard behavior.
- Sufficiency review: sufficient for implementation; a developer or generator can implement and review the catalog without inventing payload fields, actions, states, auth/tenant behavior, trace links, tests, or visual/component semantics. The normal default view uses product-language readiness and attention summaries, while implementation ids remain limited to role-gated diagnostics and backend-owned action envelopes.

### `surface-agent-admin-detail` — Agent readiness/behavior inspection

- Type: `show-inspection` realized by the detail renderer; role: selected AgentDefinition inspection.
- User goal: understand readiness, active behavior artifacts, provider state, and safe next tasks.
- Default-visible payload: human-readable readiness, active prompt/manifest/tool/model summaries, blocked/approval state, redaction note, task entry points.
- Diagnostics drawer: prompt/skill/reference/model/tool ids, policy/capability ids, trace ids, raw version metadata.
- Actions: open dedicated prompt diff, manifest review, tool-boundary simulation, model-ref proposal, no-side-effect test, prompt-risk review, activation/deactivation confirmation, rollback/trace surfaces as backend-authorized task entry points.
- Mutations: no inline mutation; editable fields are read-only summaries. Consequential changes require separate surfaces and backend authorization.
- Tests: detail inspection is read-only; task entry actions return typed surfaces; provider secrets and raw prompt/skill/reference bodies are absent.
- Sufficiency review: sufficient; default view must translate artifact ids into product-language cards.

### `surface-agent-prompt-governance`, `surface-agent-skill-manifest-diff`, `surface-agent-tool-boundary-diff`, `surface-agent-model-refs`

- Type: `governance-diff` or `show-inspection` depending on artifact.
- User goal: review proposed behavior/configuration changes with risk, simulation, redacted previews, and approval requirements.
- Default-visible payload: before/after summaries, impact, risk class, activation status, provider/tool/data boundary summary.
- Diagnostics drawer: compact manifests, loader tool names, raw capability ids, trace ids, model/provider aliases; secrets and hidden body text remain omitted.
- Actions: propose, simulate, submit for review, approve, reject, or open trace as authorized. Approval/activation are separate decisions.
- Tests: authority expansion and tool grants produce approval-required or denied result surfaces; accepting advisory output never activates behavior directly.
- Sufficiency review: sufficient; no broad edit page may combine diff review with activation.

### `surface-agent-test-console` — No-side-effect runtime test surface

- Type: `workflow-status`; role: no-side-effect managed-agent runtime test and evidence surface.
- User goal: verify prompt assembly, compact manifests, governed loader tools, scoped evidence access, provider/model readiness, and ToolPermissionBoundary behavior without committing behavior changes.
- Default-visible payload: task status, safe result summary, no-direct-mutation notice, provider fail-closed status, allowed/denied loader/evidence checks, and next human review route.
- Diagnostics drawer: PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, model/tool/data/policy usage, correlation ids, and redaction metadata.
- Actions: run/read test and open trace as authorized. Test output can draft or update a proposal decision only through a separate behavior proposal; it never activates artifacts.
- Tests: provider/runtime missing fails closed; side-effecting tools remain disabled; raw prompt/skill/reference bodies and provider secrets remain absent.
- Sufficiency review: sufficient; this is an advisory workflow surface, not a chat console or mutation page.

### `surface-agent-activation-confirmation`, `surface-agent-deactivation-confirmation`, `surface-agent-rollback-confirmation` — Lifecycle confirmation surfaces

- Type: `lifecycle-confirmation`; role: separate consequential lifecycle confirmation after review/diff/decision surfaces.
- User goal: understand impact, approval state, rollback/deactivation consequences, idempotency, policy basis, and trace evidence before confirming lifecycle changes.
- Default-visible payload: target managed agent, current/proposed lifecycle state, impact summary, approval blockers, idempotency requirement, safe evidence summary, and disabled action reasons.
- Diagnostics drawer: proposal id, AgentDefinition id, activation/rollback metadata, policy/capability ids, trace/correlation ids, and redaction metadata.
- Actions: activate, deactivate, rollback, cancel/back, or open trace as authorized. Backend approval/version/provider/tool-boundary state remains authoritative.
- Tests: activation remains disabled until backend approved state exists; rollback requires activated proposal metadata; deactivation cannot bypass runtime authorization or audit.
- Sufficiency review: sufficient; lifecycle changes must not be combined into catalog, detail, broad diff, or advisory result surfaces.

### `surface-agent-behavior-proposal` — Behavior proposal decision card

- Type: `decision-card`/`decision`; role: human approval surface.
- User goal: decide whether to submit, approve, reject, defer, activate, cancel, or rollback a behavior change.
- Default-visible payload: recommendation, evidence, risk, confidence, impact, alternatives, allowed and disabled actions, activation blockers.
- Diagnostics drawer: governed action ids, idempotency source, capability ids, policy refs, trace ids.
- Actions: submit/approve/reject/activate/cancel/rollback/open trace; disabled actions explain the missing backend prerequisite.
- Tests: high-risk changes require human approval; disabled activation remains disabled until backend state says approved.
- Sufficiency review: sufficient; this is the mandatory route for high-risk prompt/tool/model/manifest changes.

### `surface-agent-admin-prompt-risk-review` — Prompt-risk autonomous review result

- Type: `workflow-status`; role: autonomous-agent analysis progress/result, reused by Governance/Policy.
- User goal: inspect advisory risk findings and route them to human decision without direct mutation.
- Default-visible payload: task status, risk summary or blocked/deferred readiness summary, findings only when a real model-backed result exists, recommendations, required human review reasons, provider failures.
- Diagnostics drawer: model/tool/data/policy usage and trace ids.
- Actions: read, accept advisory result, reject result, cancel, open trace. Accepting a real advisory result creates/updates a proposal decision; accepting a blocked/deferred state cannot activate artifacts or claim review success.
- Tests: model-backed review is fail-closed if provider/runtime is missing; deferred worker readiness renders `blocked_provider_or_runtime` and does not claim fixture-only success; completed review still requires human Agent Admin decision.
- Sufficiency review: sufficient; may be rendered with a specialized workflow/result panel.

### `surface-agent-admin-trace` — Agent Admin trace timeline

- Type: `audit-timeline`; role: investigation surface owned by Agent Admin and reusable by Audit/Trace.
- User goal: understand who/what/when/why/how-authorized for managed-agent reads, prompt assembly, loader tools, provider calls, proposals, approvals, denials, and no-direct-mutation guarantees.
- Default-visible payload: concise event summaries and safe trace links.
- Diagnostics drawer: raw trace ids, correlation ids, actor/source details, redaction profile, omitted categories.
- Actions: trace drill-down/export/escalation only if authorized by Audit/Trace.
- Tests: routine summaries remain auditable; redacted/cross-scope evidence is not enumerated.
- Sufficiency review: sufficient; raw internals stay role-gated and visually subordinate.

## Action rules

Every consequential browser action has a stable `actionId`, maps to `managed-agent-governance` or `audit.trace.read`, carries a correlation/idempotency key where needed, and returns a typed result, decision card, workflow status, markdown response, or safe system message. Browser controls are advisory; backend capability, version state, approval policy, tenant/customer scope, provider readiness, and `ToolPermissionBoundary` remain authoritative.

## States

Surfaces define loading, empty, ready, submitting, validation-error, forbidden, conflict, stale/reconnect, partial-data, provider-fail-closed, not-found-or-redacted, no-op, approval-required, and failure states where applicable.
