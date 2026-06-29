# Agent Admin lifecycle

Workstream id: `agent-admin`
Owning domain: `core-starter`
Current readiness: `description-ready`
Ready-to-build assessment: 2026-06-29 — refreshed current-intent graph covers Agent Admin managed-agent governance, proposal-first behavior changes, governed documents, manifests, tool boundaries, test console, traces, and runtime-validation expectations.
Automated alignment readiness: `stale-description-changed` because this docs-only refresh postdates the prior AABP-05-003 implementation evidence.
Implementation alignment: `stale-description-changed`
Source alignment: `realization/source-alignment.md`
Last description change: 2026-06-29 — current app-description now explicitly models managed-agent governance graph coverage, attention categories, governed tool ids, test-console behavior, provider/config blockers, loader/tool-boundary denials, and runtime trace obligations.
Last alignment review: 2026-06-29 — docs-only refresh; no runtime/API/UI validation beyond `git diff --check` in this task.
Last compile: 2026-06-27 — prior Agent Admin behavior-profile realization mini-project closed at `api-smoked/frontend-rendered`; current description changes are not yet recompiled.
Last manual runtime test: unknown

## Current alignment posture

This workstream is description-ready and stale relative to implementation. Earlier AABP-05-003 evidence still indicates a partially aligned behavior-profile slice for protected WorkstreamEndpoint/API routing, SaaS Owner/Admin authorization and safe denials, proposal-first save/activation, stale/high-risk denial behavior, restore proposals, skill/reference lifecycle, behavior-profile versions and assignments, active-profile runtime loader/tool-boundary traces, model-backed editing-agent tests with fail-closed missing-runtime behavior, and frontend current surface contracts.

The 2026-06-29 refresh adds or sharpens current-intent requirements for dashboard attention categories, governance center/test-console surfaces, canonical governed-tool ids, model-policy/tool-boundary assignment semantics, explicit chat confirmation, idempotent/no-op/partial-failure results, provider/config blockers, `ReferenceLoadTrace`, and graph proof across worker/adapter/tool/capability/realization/test/runtime-validation/trace nodes. Those description changes require future implementation/source-alignment review before claiming partial or runtime alignment for the refreshed scope.

## Slice status map

| Slice | Current status | Evidence level | Next validation owner |
| --- | --- | --- | --- |
| SaaS-admin access and denial | `stale-description-changed` | Prior AABP-05-003 API/frontend evidence; refreshed access adds managed-agent-governance wording and denial categories. | Re-run protected API/UI auth smoke. |
| Catalog/detail/profile inspection | `stale-description-changed` | Prior catalog/detail evidence; refreshed graph adds governance center, attention badges, model-policy/tool-boundary summary expectations. | Reconcile frontend/API contracts and smoke. |
| Proposal-first prompt/doc/profile editing | `stale-description-changed` | Prior proposal-first evidence; refreshed graph expands manifest/model/tool-boundary proposal semantics and partial-failure/idempotency outcomes. | Re-run proposal lifecycle validation. |
| Attention and dashboard | `description-only` | Newly described attention categories for proposals, approvals, provider blockers, and denied loader/tool-boundary events. | Implement/validate dashboard attention read model. |
| Governance/test-console surfaces | `description-only` | Newly explicit governance center and safe test-console current intent. | Implement/validate provider fail-closed and test-mode behavior. |
| Skill/reference lifecycle and manifests | `stale-description-changed` | Prior skill/reference evidence; refreshed graph adds first-class manifest editor and `ReferenceLoadTrace` expectations. | Reconcile runtime loader and trace assertions. |
| Behavior-profile versions/assignments | `stale-description-changed` | Prior API/frontend evidence; refreshed graph adds model-policy/tool-boundary assignment and idempotency/no-op semantics. | Re-run profile assignment/activation validation. |
| Runtime loader and traces | `stale-description-changed` | Prior service/tool resolver/trace evidence; refreshed graph requires `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, `AgentWorkTrace`, provider fail-closed, test-console, and denial trace coverage. | Re-run runtime loader/provider/trace validation. |
| Editing-agent model path | `stale-description-changed-provider-success-config-blocked` | Prior tests used `TestModelProvider` and fail-closed missing-runtime behavior; no fake normal provider success claimed. | Real external provider configuration smoke remains required. |
| Frontend current surfaces | `stale-description-changed` | Prior frontend tests/typecheck/build; refreshed surface inventory requires reconciliation. | Re-run frontend contract/typecheck/build after implementation. |

## Blockers and assumptions

- This task is docs-only and does not update runtime/API/UI code.
- Runtime-ready/manual-ready is not claimed because manual browser/API acceptance and real external provider-backed editing-agent success were not exercised.
- Prior AABP-05-003 evidence remains useful historical context but no longer proves alignment for the refreshed graph.
- The shared capability artifact may still use the legacy `agent-doc-administration` name; current workstream intent describes the foundation capability scope as managed-agent governance.
- Future validation must classify implementation as aligned, stale-description-changed, stale-code-changed, partially-aligned, blocked, or intentionally description-only after reconciling these files.

## Next recommended action

Run a focused Agent Admin implementation/source-alignment follow-up that reconciles the refreshed managed-agent governance graph with backend/API/frontend/runtime-loader/test-console traces, including real provider fail-closed and, when configured, provider-backed smoke evidence.
