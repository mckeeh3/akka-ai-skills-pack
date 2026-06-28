# Agent Admin behavior-profile implementation map

Task: `AABP-01-001`  
Source slice: current Agent Admin app-description under `app-description/domains/core-starter/workstreams/agent-admin/**` plus capability `agent-doc-administration`.  
Readiness target for this mini-project: move from stale direct document/governance-console implementation toward SaaS Owner/Admin-only behavior-profile governance with proposal-first edits, review/activation separation, runtime-profile loading, and trace visibility.

## Current intent boundaries to preserve

- Agent Admin is SaaS Owner/Admin-only; tenant/org/customer admins are not Agent Admin operators.
- Generated agents, generated agent identity, generated tool implementations, and static placement are read-only app-description/code-generated defaults.
- Agent Admin manages runtime behavior profiles: active prompt, independently managed skills/references, model config reference, skill assignments, allowed generated tools, and safe trace visibility.
- AI-assisted editing returns a structured behavior-change proposal. `Save draft/proposal` is non-active; `Activate/Commit` is separate and backend-authorized.
- Restore creates a proposal copied from a historical version; it must not directly replace the active version.
- Skill/reference create/update/remove is proposal-first. Skill removal defaults to deprecation unless lifecycle policy permits hard delete.
- Runtime loading uses active profile/documents only and traces profile resolution, prompt assembly, loader-tool reads, generated-tool assignment, model policy, and tool-boundary decisions.

## Source/test drift classification

### Backend service and domain areas

| Area | Current evidence | Classification | Next boundary |
| --- | --- | --- | --- |
| `AgentAdminDocAdministrationService` | Provides SaaS Owner/Admin-gated list/detail, doc reads, version history, adjacent diff, edit sessions, runtime doc-read trace rows. | Keep as the immediate service seam, but change consequential commands. | `AABP-01-002` should add proposal records/statuses and make Save Draft non-active before touching restore/create/delete/profile assignment. |
| `AgentAdminDocAdministrationService.saveEditSession` | Saves proposed content as the new current document version immediately and reports `savedVersion`. | Stale direct-save active mutation. | Replace with immutable non-active proposal/draft creation; add separate activate command with low-risk policy checks and active behavior unchanged until activation. |
| `AgentAdminDocAdministrationService.restoreVersion` | Restores a historical version into a new current active version immediately. | Stale direct-restore active mutation. | Defer to `AABP-01-003`; restore should create a restore proposal with edit request `Restored from version N`. |
| `createSkill`, `createReferenceDoc` | Create active skill/reference records and manifest entries directly. | Stale direct-create mutation. | Defer to `AABP-01-003`; create should use proposal-first activation into tenant-scoped skill/reference library. |
| `deleteSkill`, `deleteReferenceDoc` | Permanently deletes documents and cascades references by default. | Stale direct-delete/permanent-delete assumption. | Defer to `AABP-01-003`; default should deprecate and remove manifest access unless policy permits hard delete. |
| `updateAgentProfile` | Mutates generated agent name/purpose on `AgentDefinition`. | Stale generated-agent identity editing. | Remove product exposure in `AABP-03-001`/`AABP-04-001`; behavior-profile changes must version model/prompt/skill/tool assignments, not generated identity. |
| Edit-session storage | Uses process-local static `SESSIONS`. | Partial test/demo substrate only. | `AABP-01-002` should introduce durable or repository-backed proposal/session records sufficient for service tests; avoid claiming runtime durability until Akka-backed path is wired. |
| `AgentAdminDocEditingAgent` and `ComponentClientAgentAdminDocEditingRuntime` | Concrete Akka Agent invocation seam exists and fails closed through `FailClosedAgentAdminDocEditingRuntime` when unbound. | Keep, but output/schema is narrower than current `BehaviorChangeProposal`. | Later proposal lifecycle should capture risk, authority expansion, full proposed content/diff, rationale, tests, transcript, and trace ids. |
| `AgentAdminService` | Older read facade exposes catalog/detail with governance-console artifact cards, seed material, prompt governance, model refs, tool-boundary diff, test console, tenant-admin allowance. | Stale product surface source; keep only reusable safe-read ideas. | `AABP-03-001` should stop routing stale product actions/surfaces unless remapped to current Agent Admin surfaces. |
| `AgentAdminPromptRiskReviewService` and prompt-risk AutonomousAgent files | Advisory prompt-risk task lifecycle exists, fail-closed provider behavior, no direct mutation. | Potential future review/decision-card substrate, but stale as a user-visible prompt-risk surface for current slice. | Do not make it the proposal lifecycle root in `AABP-01-002`; remap later only if needed for medium/high-risk review. |
| `AgentMarketplaceGovernanceService` | Proposal-first marketplace/tool-binding patterns, but tenant-org-admin scoped and marketplace-oriented. | Reference pattern only; not current Agent Admin product path. | Keep out of next implementation unless extracting validation ideas. |
| `AgentBehaviorRepository` / foundation agent records | Stores AgentDefinition, Prompt/Skill/Reference docs, manifests, model refs, tool boundaries, and version snapshots. | Useful substrate; missing behavior-profile proposal/version aggregate. | `AABP-01-002` can add proposal repository seam; `AABP-01-004` owns profile version/assignment contracts. |
| `AgentRuntimeService`, `AgentRuntimeToolResolver`, runtime traces | Active prompt/skill/reference loading and trace tests exist. | Partially aligned runtime substrate. | Defer runtime profile/trace expansion to `AABP-02-001`. |
| `AdminEndpoint` | No current Agent Admin HTTP route found in the mapped admin endpoint; Agent Admin is currently surfaced primarily through `WorkstreamService` actions. | Not an immediate implementation target for `AABP-01-002`. | API/workstream endpoint wiring belongs to `AABP-03-001`. |
| `WorkstreamService` Agent Admin action branch | Routes current-looking doc surfaces but executes direct save/profile/restore/create/delete; still contains legacy prompt-governance/test-console/activation/rollback branches. | Mixed: keep routing shell, change/remove stale action semantics. | `AABP-01-002` may update save/activate service semantics; full action inventory cleanup is `AABP-03-001`. |

### Frontend and contract-test areas

| Area | Current evidence | Classification | Next boundary |
| --- | --- | --- | --- |
| `AgentAdminDocEditingSurface.tsx` | Purpose-built renderer for blank/dashboard/list/detail/doc/edit-session/version/diff/create/delete/runtime-trace surfaces. | Keep as rendering substrate, but direct mutation copy/actions are stale. | `AABP-04-001` should align after backend/workstream contracts stabilize. |
| Agent detail renderer | Allows editing agent name/purpose and Save Agent Profile. | Stale generated-agent identity editing. | Remove or convert to read-only generated identity; expose profile history/model/skill/tool assignment entry points later. |
| Edit session renderer | Shows proposal content and warnings, but says Save creates a new current version immediately. | Stale Save Draft semantics. | After `AABP-01-002`, Save Draft should show non-active proposal and route activation/review separately. |
| Delete skill/reference renderers | Emphasize permanent deletion and no restore. | Stale lifecycle default. | After `AABP-01-003`, default to deprecate/remove access with policy-gated hard delete copy. |
| Surface inventory fixtures/contracts | Current fixture covers many doc-editing surfaces but omits profile history, skill library, skill assignment, generated-tool assignment, proposal review, and behavior-profile version surfaces from app-description. | Partially aligned fixture with stale assertions. | Expand in `AABP-04-001`; do not use current fixtures as product authority. |
| `frontend/src/api/types.ts` and workstream surface types | Types cover agent list/detail, doc version, edit session, diff, runtime doc-read traces. | Partial DTO set. | Add proposal, review/activation, behavior-profile, assignment, model-summary, and generated-tool summary DTOs later. |
| `FixtureWorkstreamApiClient` | Routes current doc actions plus legacy prompt-governance/model/tool-boundary/test-console fallbacks. | Mixed/stale fixture routing. | Clean after backend action contract is current. |
| `workstream-agent-admin-vertical.contract.test.mjs` | Correctly rejects some stale governance-console surface ids in current inventory, but asserts direct save/current-version and permanent-delete markers. | Test drift. | Update in the task that changes the matching behavior; do not preserve stale assertions. |

### Source-alignment entries

- Keep the broad `agent-admin.workstream-slice` mapping as candidate evidence only.
- Split future realization evidence by slice:
  1. proposal lifecycle service/domain/tests;
  2. prompt/skill/reference restore and lifecycle;
  3. behavior-profile version/model/skill/generated-tool assignment;
  4. runtime loader/trace expansion;
  5. workstream/API action routing;
  6. frontend surfaces/contracts.
- Do not mark `realization/source-alignment.md` aligned until a later validation task has runtime/API/UI evidence.

## Keep/change/remove/defer buckets

### Keep now

- SaaS Owner/Admin checks in `AgentAdminDocAdministrationService.requireSaasAdmin`.
- Read-only document/version/diff primitives and runtime doc-read trace query shape, while extending metadata later.
- Fail-closed editing-agent runtime seam and concrete Akka Agent bridge.
- Runtime loader/tool resolver tests as future evidence for active-only `readSkill` / `readReferenceDoc` enforcement.
- Frontend renderer structure for current doc/read/history/diff/edit-session surfaces.

### Change next

- Replace direct Save with proposal/draft creation and separate activation.
- Add proposal status/risk/authority/base-version/transcript/trace fields and stale-version denial tests.
- Ensure active document/runtime reads remain unchanged after Save Draft and after rejected/blocked activation.
- Change tests that currently assert immediate current-version update.

### Remove or de-expose from current Agent Admin product path

- Whole/generated-agent name-purpose editing action (`action-agent-admin-save-agent-profile`).
- Legacy prompt governance, skill-manifest diff, tool-boundary diff, model refs, test console, activation/deactivation/rollback, seed import/material surfaces as primary Agent Admin UX.
- Tenant-admin Agent Admin access paths in older `AgentAdminService`/prompt-risk services unless a future app-description change grants them.

### Defer explicitly

- Restore-as-proposal, skill/reference create/deprecate/delete semantics: `AABP-01-003`.
- Behavior-profile version records for model config ref, prompt version, skill assignment, generated-tool assignment, and tenant/global clone semantics: `AABP-01-004`.
- Runtime profile resolution and expanded traces: `AABP-02-001`.
- Workstream/API action inventory cleanup and protected browser/API smokes: `AABP-03-001`.
- Frontend inventory, proposal review/profile/assignment surfaces, fixtures, and contract tests: `AABP-04-001`.

## Next implementation boundary: AABP-01-002

`AABP-01-002` should implement only the proposal lifecycle foundation:

1. Add a minimal proposal/draft model and repository seam for prompt/skill/reference document edits.
2. Convert `saveEditSession` from direct active mutation into non-active draft/proposal creation.
3. Add explicit activation/review command(s) only for low-risk authorized activation, with stale base-version and medium/high-risk/authority-expansion denial paths.
4. Record proposal, save-draft, activation, rejection/denial, and stale-version trace/audit facts available to service tests.
5. Update backend service tests so Save Draft leaves active document/runtime reads unchanged and activation is separate.
6. Do not solve restore, skill/reference lifecycle, profile assignments, runtime loader expansion, or frontend rendering in this task.

## Immediate validation scope

For this planning/map task only, validation is docs consistency:

```bash
git diff --check -- specs/agent-admin-behavior-profile-realization
```

No code tests are added or changed by `AABP-01-001`.
