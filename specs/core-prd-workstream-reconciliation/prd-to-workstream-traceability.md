# PRD to Five-Core Workstream v0 Traceability

## Purpose

Map the older module-sequenced core PRD input set under `docs/examples/core-ai-first-saas-input/` to the completed five-core v0 workstream planning artifacts.

This report compares PRD intent to these v0 artifacts:

- `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md`
- `specs/five-core-workstreams-v0-plan/workstream-dependency-map.md`
- `specs/my-account-workstream-v0/workstream-contract.md`
- `specs/my-account-workstream-v0/capability-inventory.md`
- `specs/user-admin-workstream-v0/workstream-contract.md`
- `specs/user-admin-workstream-v0/capability-inventory.md`
- `specs/agent-admin-workstream-v0/workstream-contract.md`
- `specs/agent-admin-workstream-v0/capability-inventory.md`
- `specs/audit-trace-workstream-v0/workstream-contract.md`
- `specs/audit-trace-workstream-v0/capability-inventory.md`
- `specs/governance-policy-workstream-v0/workstream-contract.md`
- `specs/governance-policy-workstream-v0/capability-inventory.md`

## Classification vocabulary

- `covered`: represented in five-core v0 contracts/capability inventories at v0 scope.
- `partial`: represented, but intentionally narrower than the older full-module PRD.
- `deferred`: named as future/full-core work or optional when the v0 runtime task implements it.
- `superseded`: replaced by newer workstream-oriented input/path or changed sequencing.
- `gap`: actionable mismatch not represented or not clearly classified.

## Executive mapping

| Older PRD input | Primary mapping | Classification | Notes |
|---|---|---|---|
| `README.md` | Shared v0 contract, dependency map, all five v0 workstreams | `partial` + `superseded` | The older directory still says `10-canonical-core-app-prd.md` is the hard full-core PRD target. Five-core v0 uses a newer workstream-oriented planning path and explicitly narrows scope from full-core readiness. |
| `00-document-development-process-context.md` | Shared v0 contract and dependency map | `covered` for process principles; `partial` for full module sequence | Progressive delivery, one visible full-stack increment at a time, workstream-first implementation, and explicit defers are preserved. The exact module sequence is superseded by the five workstream queues for v0. |
| `01-core-seed-progression-plan.md` | Dependency map plus all five workstream contracts | `partial` + `superseded` | Five-core v0 maps to My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy workstreams, not the old module ordering. Minimum starter/v0 scoping remains explicitly narrower than full-core. |
| `03-module-auth-app-access-prd.md` | My Account v0 contract/inventory + shared security gates | `partial` | `/api/me`, selected `AuthContext`, account/profile/settings/context summary, launcher behavior, safe denials, tenant isolation, secret boundary, and trace refs are covered. WorkOS/AuthKit first-login and full auth-provider implementation details are not expanded in the v0 workstream contracts. |
| `03a-module-agent-workstream-runtime-bootstrap-prd.md` | Shared v0 contract + Agent Admin v0 + all workstream request/response agent contracts | `covered` for runtime doctrine; `partial` for bootstrap catalog specifics | The governed request-based Akka Agent path, AgentDefinition resolution, prompt/skill/reference manifests, ToolPermissionBoundary, provider fail-closed behavior, traces, and no deterministic fallback are covered across v0. Specific Access/Profile bootstrap seed objects are folded into My Account and Agent Admin semantics. |
| `04-module-user-admin-prd.md` | User Admin v0 contract/inventory | `partial` | User/member directory, invitations, membership/role changes, access-review task candidates, audit links, backend authorization, idempotency, outbox/audit expectations, and denials are represented. Full production email/onboarding and rich admin UI scope remain broader than v0. |
| `05-module-agent-definition-prd.md` | Agent Admin v0 contract/inventory | `covered` for v0; `partial` vs full PRD | Agent catalog/detail, definitions, lifecycle, authority, model refs, tool boundaries, seed/default material, proposals/review/activation, and traces are represented. Full standalone CRUD surface detail is collapsed into workstream surfaces/actions. |
| `06-module-prompt-governance-prd.md` | Agent Admin v0 contract/inventory | `partial` | Prompt version reads, behavior proposals, review/activation, runtime prompt assembly, traces, and seed/default visibility are represented. Full prompt editor/review/diff/version-history module detail is not exhaustively captured as separate v0 capabilities. |
| `07-module-skill-governance-prd.md` | Agent Admin v0 contract/inventory | `partial` | Skill reads, governed `readSkill(skillId)`, manifest assignment, loader authorization, SkillLoadTrace, behavior proposals, and activation controls are represented. Full skill catalog/editor/diff lifecycle detail is broader than v0. |
| `08-module-audit-work-trace-prd.md` | Audit/Trace v0 contract/inventory + shared trace gates | `covered` for v0; `partial` vs full PRD | Dashboard, search, detail, timeline, failure evidence, explanation, redaction, tenant isolation, provider/tool traces, and optional future summary task are represented. Enterprise retention/export/SIEM remains deferred. |
| `09-module-evaluation-closed-loop-improvement-prd.md` | Governance/Policy v0 contract/inventory + Agent Admin optional review task + Audit/Trace evidence | `partial` + `deferred` | Proposal, simulation, approval, activation, rollback, policy-impact analysis task candidates, and evidence links are represented. Full evaluator runs, rubrics, outcome metrics, replay/canary, and closed-loop improvement lifecycle are mostly deferred beyond v0. |
| `10-canonical-core-app-prd.md` | All five v0 workstreams + shared contract/dependency map | `partial` + `superseded` | Functional agents, capability-first backend, runtime completion doctrine, auth/security gates, workstream UI, traces, and five-core shape are preserved. The older full-core target is not fully implemented by five-core v0; it is superseded for v0 planning by newer workstream-oriented artifacts. |

## Detailed requirement mapping

### Process and source-input requirements

| Requirement from old input | Five-core v0 artifact mapping | Classification |
|---|---|---|
| Input documents are source assets for generated apps, not this repo's business source of truth | `AGENTS.md`; mini-project README; all v0 specs as source planning artifacts | `covered` |
| Feed PRDs through workstreams → surfaces/actions → governed capabilities → Akka substrate | `shared-five-core-v0-contract.md` capability contract; workstream-specific surface/action tables and capability inventories | `covered` |
| Visible, demonstrable full-stack increments, not backend-only foundation | Shared v0 UI/API gates and runtime validation standard | `covered` |
| Module PRDs provide progressive implementation sequence | `workstream-dependency-map.md` reorders work into five workstream mini-projects | `superseded` for v0 sequencing |
| Full core requires Access/Profile, User Admin, Agent Admin, Audit/Trace, Governance/Policy | dependency map and five workstream contracts | `covered` as v0 planning shape; `partial` for full-core maturity |

### Auth, access, and My Account

| Older PRD requirement | Five-core v0 artifact mapping | Classification |
|---|---|---|
| WorkOS/AuthKit-supported browser authentication | Shared security gates preserve WorkOS/AuthKit distinction; My Account requires authenticated account and selected context | `partial` |
| Local Account, UserProfile, UserSettings, Tenant, Membership, Role/Capability, AuthContext | Shared security gates; My Account inventory `view_summary`, `view_context`, `update_profile_settings` | `covered` at v0 contract level |
| `/api/me` browser-safe identity/context/capability state | My Account inventory `my_account.view_summary`; shared UI/API gates | `covered` |
| Context selection and tenant isolation | My Account inventory `view_context`; shared capability contract | `covered` |
| Protected app shell and signed-in user tile | My Account contract launch semantics; shared UI gates | `covered` |
| WorkOS callback, first-login account linking, public sign-in/sign-out routes | Not detailed in five-core v0 workstream contracts | `partial` / potential follow-up only if v0 docs claim auth-provider implementation completeness |
| Minimal AdminAuditEvent for auth denials | Shared trace/audit gates; My Account trace obligations | `covered` |

### Agent runtime bootstrap

| Older PRD requirement | Five-core v0 artifact mapping | Classification |
|---|---|---|
| Seeded AgentDefinition for bootstrap workstreams | Agent Admin seed/default capabilities; shared request/response runtime contract | `covered` generally; `partial` for exact seed list |
| Prompt, skill, reference manifests in compact prompt context | Shared runtime contract; Agent Admin request/response contract and inventory | `covered` |
| ToolPermissionBoundary deny-by-default | Shared runtime contract; Agent Admin `get_tool_boundary` and `simulate_tool_boundary`; all request/response turn contracts | `covered` |
| Concrete Akka Agent invocation through governed runtime path | Shared contract and every workstream request/response capability | `covered` |
| Missing provider config fails closed | Shared model-provider rules; every model-backed workstream contract | `covered` |
| Diagnostics for PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace | Shared trace gates; Agent Admin/Audit/Trace inventories | `covered` |
| Access/Profile bootstrap as separately named functional agent | My Account v0 replaces `Access/Profile` naming with `My Account Agent` launched from user tile | `superseded` naming; semantics `covered` |

### User administration

| Older PRD requirement | Five-core v0 artifact mapping | Classification |
|---|---|---|
| User Admin functional-agent workstream | User Admin contract | `covered` |
| User/member directory | `USERADMIN_LIST_MEMBERS` | `covered` |
| Invitation list/create/resend/revoke | `USERADMIN_LIST_INVITATIONS`, `SEND`, `RESEND`, `REVOKE` | `covered` |
| Invitation acceptance, expiry, reminders, production Resend delivery | User Admin send/resend/outbox mentions; shared security gates | `partial`; detailed acceptance/expiry/reminder lifecycle is broader than v0 inventory |
| Membership status changes | `USERADMIN_UPDATE_MEMBER_STATUS` | `covered` |
| Role/capability listing, preview, change | `USERADMIN_LIST_ROLES_CAPABILITIES`, `PREVIEW_ROLE_CHANGE`, `CHANGE_MEMBER_ROLES` | `covered` |
| Last-admin/self-demotion protection | User Admin inventory policy notes | `covered` at contract level |
| Access review | User Admin optional AutonomousAgent task capabilities | `deferred` unless implemented in runtime task |
| Admin audit list/detail | User Admin trace references; Audit/Trace workstream | `partial` in User Admin; richer investigation covered by Audit/Trace |

### Agent definition and behavior governance

| Older PRD requirement | Five-core v0 artifact mapping | Classification |
|---|---|---|
| Agent Admin functional-agent workstream | Agent Admin contract | `covered` |
| AgentDefinition catalog/detail/lifecycle | Agent Admin `list_definitions`, `get_definition`, proposal/activation capabilities | `covered` |
| Authority/model/tool-boundary metadata | Agent Admin `get_model_ref`, `get_tool_boundary`, `simulate_tool_boundary` | `covered` |
| Create/edit/activate/disable/archive forms | Agent Admin behavior-change proposal/review/activation capabilities | `partial`; v0 abstracts form mechanics into governed proposal/action model |
| Seed/default managed-agent material | `list_seed_material`, `reseed_missing_defaults` | `covered` |
| Prompt document CRUD/diff/history/review/activation | Agent Admin prompt read and behavior proposal/review/activation | `partial` |
| Skill catalog/versioning/manifest/readSkill | Agent Admin skill/version/manifest/loader capabilities | `partial` to `covered` for runtime loader semantics; full editor lifecycle broader |
| Reference documents/manifests/readReferenceDoc | Added in Agent Admin v0 though not prominent in older skill PRD | `covered` and newer-scope addition |
| Behavior change proposals and review controls | Agent Admin proposal/review/activation/cancel | `covered` |
| Optional background behavior review | Agent Admin optional AutonomousAgent task capabilities | `deferred` unless implemented |

### Audit and trace

| Older PRD requirement | Five-core v0 artifact mapping | Classification |
|---|---|---|
| Audit/Trace functional-agent workstream | Audit/Trace contract | `covered` |
| Trace dashboard/search/detail/timeline | Audit/Trace dashboard/search/detail/timeline capabilities | `covered` |
| Failure/denial/provider/tool evidence | `audit.trace.failureEvidence.read` | `covered` |
| Bounded trace explanation | `audit.trace.explain` request-based Akka Agent | `covered` |
| Redaction policy and tenant isolation | Audit/Trace contract and inventory | `covered` |
| Export, retention administration, SIEM/legal hold | Audit/Trace contract out-of-scope | `deferred` |
| Durable audit summary/anomaly task | Optional Audit/Trace AutonomousAgent summaryTask capabilities | `deferred` unless implemented |

### Evaluation, governance, and policy

| Older PRD requirement | Five-core v0 artifact mapping | Classification |
|---|---|---|
| Governance/Policy functional-agent workstream | Governance/Policy contract | `covered` |
| Policy inventory and dashboard | `GOVPOL-READ-DASHBOARD`, `GOVPOL-LIST-POLICIES`, `GOVPOL-READ-POLICY` | `covered` |
| Request/response explanation and draft | `GOVPOL-EXPLAIN-OR-DRAFT` | `covered` |
| Proposal draft/submit/simulate/decide/activate/rollback | `GOVPOL-DRAFT-PROPOSAL`, `SUBMIT`, `SIMULATE`, `DECIDE`, `ACTIVATE`, `ROLLBACK` | `covered` for governance proposal lifecycle |
| EvaluationRubric/EvaluationRun/EvaluationFinding objects | Not explicitly modeled as standalone v0 capabilities | `deferred` |
| Evaluator agent scoring/classification | Optional policy-impact analysis; not equivalent to full evaluator module | `partial` / `deferred` |
| Replay/simulation evidence | Governance/Policy simulation capability | `partial` |
| Outcome monitoring and closed-loop improvement | Not in five-core v0 capability inventories except proposal rollback/trace evidence | `deferred` |
| Fully autonomous self-improvement | Explicitly out of scope / human-governed by default | `deferred` / intentionally not covered |

## Source-of-truth relationship

The older `core-ai-first-saas-input/` set remains useful as a full-core PRD input sample and provenance for detailed module requirements. The completed five-core v0 workstreams should be treated as the current v0 implementation contract for the starter/reference runtime because they:

- use the workstream as the primary application abstraction;
- decompose by functional agents and governed capabilities instead of module/page sequence;
- explicitly inherit runtime completion, provider fail-closed, ToolPermissionBoundary, trace, and local validation rules;
- label v0 as narrower than full-core readiness.

Therefore, old module PRDs are not fully implemented by five-core v0. They are mostly `partial` against v0 and should be described as older/full-core module-sequenced input rather than the authoritative v0 source.

## Potential actionable gaps for findings review

1. `docs/examples/core-ai-first-saas-input/README.md` still calls `10-canonical-core-app-prd.md` the hard PRD target for full core generation while newer guidance prefers workstream-oriented core-app domain input. This creates source-of-truth ambiguity.
2. Old Module 1 auth-provider details such as WorkOS callback/linking/sign-out route behavior are only partially represented in My Account v0 contracts. This is not necessarily a v0 gap unless current docs claim those details are implemented by five-core v0.
3. Invitation acceptance/expiry/reminder and production Resend details from the old User Admin PRD are richer than User Admin v0 inventory. They should be treated as full-core follow-up unless the starter/runtime claims production invitation readiness.
4. Prompt and skill editor/version-history UI detail from old modules is broader than Agent Admin v0's proposal-oriented capability surface. This is likely a v0 scope decision, not a defect, but should be explicit in source docs.
5. The old Evaluation/Closed-Loop Improvement module is mostly beyond five-core v0; Governance/Policy v0 covers proposal/simulation/approval/rollback but not standalone evaluator runs, rubrics, outcome monitoring, or closed-loop improvement objects.
