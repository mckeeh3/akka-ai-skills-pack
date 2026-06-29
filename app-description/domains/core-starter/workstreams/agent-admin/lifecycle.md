# Agent Admin lifecycle

Workstream id: `agent-admin`
Owning domain: `core-starter`
Current readiness: `description-ready`
Ready-to-build assessment: 2026-06-29 — refreshed current-intent graph covers Agent Admin managed-agent governance, proposal-first behavior changes, governed documents, manifests, tool boundaries, test console, traces, and runtime-validation expectations.
Automated alignment readiness: `partially-aligned-source-evidence` after `TASK-ADIA-02-003`; refreshed source/test/frontend evidence exists for major slices, but runtime-validation run records and several current-intent refinements remain open.
Implementation alignment: `partially-aligned`
Source alignment: `realization/source-alignment.md`
Last description change: 2026-06-29 — current app-description now explicitly models managed-agent governance graph coverage, attention categories, governed tool ids, test-console behavior, provider/config blockers, loader/tool-boundary denials, and runtime trace obligations.
Last alignment review: 2026-06-29 — `TASK-ADIA-02-003` reconciled source/test/frontend/runtime-validation evidence and queued follow-ups; no manual browser run, live provider success, or runtime-ready claim was made.
Last compile: 2026-06-27 — prior Agent Admin behavior-profile realization mini-project closed at `api-smoked/frontend-rendered`; current description changes are only source-evidence aligned.
Last manual runtime test: unknown

## Current alignment posture

This workstream is description-ready and partially aligned at source-evidence level. Current backend source, tests, frontend contracts, behavior seeds, and runtime-validation scaffolding prove meaningful Agent Admin implementation slices for SaaS Owner/Admin access, generated-agent catalog/detail reads, prompt/skill/reference document administration, proposal-first edit/save/review/activation, high-risk and stale activation denials, restore proposals, skill/reference lifecycle and manifest access removal, behavior-profile assignment versions, model-config summary redaction, generated-tool assignment without generated-code mutation, governed loader tools, `readSkill`/`readReferenceDoc` denial traces, provider fail-closed model invocation tests, browser-safe trace rows, and human chat-plan catalog entries.

The refreshed 2026-06-29 current intent is still broader than the proven runtime path. Open gaps remain for canonical governed-tool id adoption versus legacy aliases, trace-backed dashboard attention counts for behavior-change proposals / approval-required / provider blockers / loader denials, the exact `surface-agent-admin-test-console` governance/test-console surface inventory, runtime-validation run records for `RV-AGENT-ADMIN-001`, live provider-backed editing/test-console success when credentials are available, and end-to-end local API/UI/browser proof for chat confirmation, idempotency/no-op/partial-failure, provider-secret boundaries, and complete `PromptAssemblyTrace` / `SkillLoadTrace` / `ReferenceLoadTrace` / `AgentWorkTrace` visibility.

No runtime-ready, manual-ready, configured-provider-success, or frontend-secret-boundary runtime claim is made by this alignment review.

## Slice status map

| Slice | Current status | Evidence level | Next validation owner |
| --- | --- | --- | --- |
| SaaS-admin access and denial | `partially-aligned` | `AgentAdminDocAdministrationService`, `WorkstreamService`, `AgentAdminBrowserWorkstreamSmokeTest`, and `AgentAdminDocAdministrationServiceTest` exercise SaaS Owner/Admin access and non-SaaS-admin denial at source/test/API-smoke level; real WorkOS/AuthKit local browser mapping is not run. | Runtime-validation / auth setup follow-up. |
| Catalog/detail/profile inspection | `partially-aligned` | `AgentAdminService`, `AgentAdminDocAdministrationService`, `WorkstreamService`, `AgentAdminBrowserWorkstreamSmokeTest`, and `workstream-agent-admin-vertical.contract.test.mjs` cover generated-agent list/detail, profile history, model config summary redaction, generated-tool assignment summary, and trace links; canonical catalog/detail governed-tool ids are still not the primary implementation ids. | Canonical id and frontend/API contract follow-up. |
| Proposal-first prompt/doc/profile editing | `partially-aligned` | `AgentAdminDocAdministrationService`, `AgentAdminDocEditingAgent`, `ComponentClientAgentAdminDocEditingRuntime`, `FailClosedAgentAdminDocEditingRuntime`, `AgentAdminDocAdministrationServiceTest`, and browser smoke cover edit sessions, save draft, approve/reject/cancel, low-risk activation, stale/high-risk denial, restore proposals, and no active mutation on draft save. | Runtime-validation proposal lifecycle follow-up. |
| Attention and dashboard | `partial-gap` | Dashboard source and fixture contracts exist, but current frontend contract still records empty attention queues and does not prove trace-backed behavior-change / approval-required / provider-config / loader-denial counts. | Dashboard attention read-model follow-up. |
| Governance/test-console surfaces | `partial-gap` | Workstream actions and tests include advisory no-side-effect test console and provider fail-closed prompt assembly paths, but the implemented surface ids/contracts still include legacy `surface-agent-test-console` and related governance surfaces rather than the refreshed `surface-agent-admin-test-console` inventory. | Test-console/canonical surface reconciliation follow-up. |
| Skill/reference lifecycle and manifests | `partially-aligned` | `SkillDocument`/`ReferenceDocument` entities, manifests, `AgentRuntimeLoaderTools`, `AgentRuntimeService`, `AgentAdminDocAdministrationServiceTest`, and foundation agent tests prove create/deprecate, manifest removal, allowed/denied `readSkill` and `readReferenceDoc`, and safe trace rows without full content. | Runtime trace visibility and scenario execution follow-up. |
| Behavior-profile versions/assignments | `partially-aligned` | `AgentBehaviorProfileVersion`, `AgentAdminDocAdministrationService.updateBehaviorProfileAssignments`, repository state, browser smoke, and service tests cover tenant-scoped profile versions, model-config reference no-op/update, skill/generated-tool assignment, and no generated-tool code mutation. | Canonical model-policy/tool-boundary proposal semantics follow-up. |
| Runtime loader and traces | `partially-aligned` | Foundation `AgentRuntimeService`, `AgentRuntimeLoaderTools`, `AgentRuntimeTraceEntity/View/Sink`, `AgentRuntimeServiceTest`, `WorkstreamRuntimeAgentTest`, and `AgentAdminDocAdministrationServiceTest` cover prompt assembly, skill/reference loads, boundary denials, provider fail-closed, and Agent Admin-visible trace metadata at source/test level. | Execute runtime-validation and verify Agent Admin UI trace drill-ins. |
| Editing-agent model path | `partially-aligned-provider-success-config-blocked` | `AgentAdminDocEditingAgent` is an Akka Agent component; runtime wrappers include component-client and fail-closed implementations; tests use controlled provider paths and fail-closed behavior. No live external provider success is claimed. | Provider-configured smoke when credentials are available. |
| Frontend current surfaces | `partially-aligned` | `AgentAdminDocEditingSurface.tsx`, `AgentAdminTaskSurface.tsx`, shared workstream surfaces, API/fixture contracts, and `workstream-agent-admin-vertical.contract.test.mjs` cover doc editing, proposals, traces, redaction, and secret-boundary assertions at contract/source level. | Frontend build/typecheck and browser runtime-validation follow-up. |

## Blockers and assumptions

- This task is docs-only and does not update runtime/API/UI code.
- Runtime-ready/manual-ready is not claimed because manual browser/API acceptance and real external provider-backed editing-agent success were not exercised.
- Runtime-validation scenario `specs/runtime-validation/scenarios/agent-admin/RV-AGENT-ADMIN-001-provider-fail-closed-test-console.md` is authored but has no run record.
- Current implementation still carries legacy aliases such as `list-agent-doc-agents`, `read-agent-doc-agent`, `draft-agent-doc-edit`, `activate-agent-doc-version`, and legacy surfaces such as `surface-agent-test-console`; current app-description canonical ids and surface names require a focused reconciliation before alignment can be broadened.
- The shared capability artifact may still use the legacy `agent-doc-administration` name; current workstream intent describes the foundation capability scope as managed-agent governance.

## Next recommended action

Run the next queued workstream alignment task, then consolidate follow-ups so Agent Admin remediation can execute as bounded runtime-validation, canonical-id/surface, attention-read-model, provider, and trace-visibility tasks.
