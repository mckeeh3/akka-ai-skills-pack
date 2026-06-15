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

- Type: `dashboard`; role: workstream starting surface.
- User goal: see what needs governance attention and open the next safe surface.
- Default-visible payload: readiness summary, provider/model status, approval queue counts, manifest drift, seed import state, prompt-risk review state, safe redaction summary.
- Diagnostics drawer: capability ids, governed tool ids, trace/correlation ids, raw contract names, and idempotency mechanics.
- Actions: display catalog, open behavior proposals, start/read prompt-risk review, list seed material, open trace. Every counter/card has an action or is omitted.
- States: loading, empty, ready, forbidden, stale/reconnect, partial-data, provider-fail-closed, failure.
- Tests: default surface is dashboard; attention section precedes authorized actions; all counters are clickable or explicitly omitted; forbidden targets do not leak hidden counts.
- Sufficiency review: sufficient for implementation; default view must avoid raw implementation ids except in diagnostics.

### `surface-agent-admin-catalog` — Managed agent catalog

- Type: `list-search`; role: collection discovery.
- User goal: find a governed managed agent and open its readiness/behavior inspection.
- Default-visible payload: display name, lifecycle/readiness state, authority tier, provider readiness, seed/customization state, attention summary.
- Diagnostics drawer: AgentDefinition id, model ref id, trace policy, trace ids, capability ids.
- Row behavior: every visible row/card is keyboard-operable and opens `surface-agent-admin-detail` through `action-open-agent-detail`; the browser does not infer row authority.
- Mutations: none inline. Lifecycle and behavior changes open separate task/decision/confirmation surfaces.
- Tests: row selection opens inspection; no direct activate/deactivate from ordinary row rendering.
- Sufficiency review: sufficient if backend payload includes `openActionId`, `targetSurfaceId`, and safe row context for each visible row.

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
- Default-visible payload: task status, risk summary, findings, recommendations, required human review reasons, provider failures.
- Diagnostics drawer: model/tool/data/policy usage and trace ids.
- Actions: read, accept advisory result, reject result, cancel, open trace. Accepting the advisory result creates/updates a proposal decision; it does not activate artifacts.
- Tests: model-backed review is fail-closed if provider/runtime is missing; completed review still requires human Agent Admin decision.
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
