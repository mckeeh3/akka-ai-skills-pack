# Core PRD Workstream Reconciliation Findings

## Summary

The completed five-core v0 workstream artifacts did not directly ingest the older `docs/examples/core-ai-first-saas-input/` PRDs, but they cover a substantial subset of the same intent through a newer workstream-oriented contract model.

The main finding is not a runtime defect. It is a source-of-truth and scope-clarity issue:

- old module PRDs describe a progressive path toward full core;
- five-core v0 contracts describe a narrower, workstream-first starter/reference runtime contract;
- current docs should make that relationship explicit so future harness sessions do not treat the older module PRDs as already completed by v0 or as the preferred current rollout path.

## Findings

### FINDING-CPR-001: Older core PRD directory still reads as a canonical full-core input path

- classification: `gap`
- affected files:
  - `docs/examples/core-ai-first-saas-input/README.md`
  - `docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md`
  - `docs/skills-pack-user-guide.md`
- evidence:
  - `docs/examples/core-ai-first-saas-input/README.md` says the directory contains canonical example input documents and says to use `10-canonical-core-app-prd.md` as the hard PRD target for full core generation.
  - The mini-project background records that `docs/skills-pack-user-guide.md` now names `docs/examples/ai-first-saas-core-app-domain/` as the preferred full-core rollout input while keeping this older directory as a module-sequenced sample.
  - `prd-to-workstream-traceability.md` classifies the older PRD set as mostly `partial`/`superseded` for v0, not completed full-core scope.
- impact:
  - Future agents may choose the older module PRD set as the authoritative current full-core route and re-plan against stale module sequencing.
  - Future agents may incorrectly infer that five-core v0 completed all details in `10-canonical-core-app-prd.md`.
- recommended follow-up:
  - Update docs to state that `core-ai-first-saas-input/` is an older module-sequenced full-core input/provenance sample, while the current v0/starter path is the workstream-oriented core-app domain and five-core v0 contracts.
  - Keep the old PRDs available for full-core/detail provenance, but prevent them from competing with the newer workstream source path.

### FINDING-CPR-002: Five-core v0 covers old auth/app-access intent only at workstream-contract level

- classification: `partial`
- affected PRD:
  - `docs/examples/core-ai-first-saas-input/03-module-auth-app-access-prd.md`
- mapped workstream artifacts:
  - `specs/my-account-workstream-v0/workstream-contract.md`
  - `specs/my-account-workstream-v0/capability-inventory.md`
  - `specs/five-core-workstreams-v0-plan/shared-five-core-v0-contract.md`
- covered at v0:
  - `/api/me`-style browser-safe summary through `my_account.view_summary`;
  - selected `AuthContext` and authority/context explanation through `my_account.view_context`;
  - profile/settings self-service through `my_account.update_profile_settings`;
  - backend authorization, tenant isolation, safe denials, audit/work traces, secret boundaries, and runtime validation gates.
- not fully represented in v0 contracts:
  - detailed WorkOS/AuthKit callback/session route mechanics;
  - first-login account linking rules;
  - explicit sign-in/sign-out route inventory;
  - complete local/dev/test auth adapter semantics.
- recommendation:
  - No immediate runtime follow-up from this reconciliation alone.
  - If docs imply five-core v0 implements the full old Module 1 PRD, clarify that v0 workstream contracts cover the My Account/account-context slice and inherit auth/security gates, while provider-specific auth route mechanics belong to starter/auth implementation docs or a separate auth hardening mini-project.

### FINDING-CPR-003: User Admin v0 narrows invitation/onboarding scope compared with old PRD

- classification: `partial` / `deferred`
- affected PRD:
  - `docs/examples/core-ai-first-saas-input/04-module-user-admin-prd.md`
- mapped workstream artifacts:
  - `specs/user-admin-workstream-v0/workstream-contract.md`
  - `specs/user-admin-workstream-v0/capability-inventory.md`
- covered at v0:
  - User Admin functional workstream;
  - invitation list/send/resend/revoke;
  - member list/status;
  - role/capability list, preview, and change;
  - access-review task candidate;
  - trace/audit references;
  - idempotency, tenant isolation, authorization, and safe denial requirements.
- narrower than old PRD:
  - invitation acceptance is not a first-class detailed capability in the v0 inventory;
  - expiry/reminder timer behavior is not deeply specified;
  - complete production Resend onboarding and captured outbox behavior are referenced but not expanded to the same detail;
  - admin audit list/detail is largely delegated to Audit/Trace rather than User Admin.
- recommendation:
  - Treat this as accepted v0 narrowing unless a current starter/runtime document claims production-complete invitation onboarding.
  - If full invitation onboarding is required next, create a bounded follow-up under User Admin or an onboarding mini-project rather than reopening the whole old PRD.

### FINDING-CPR-004: Agent Admin v0 combines old agent definition, prompt, skill, reference, manifest, and behavior-governance modules

- classification: `partial` with newer-scope additions
- affected PRDs:
  - `docs/examples/core-ai-first-saas-input/05-module-agent-definition-prd.md`
  - `docs/examples/core-ai-first-saas-input/06-module-prompt-governance-prd.md`
  - `docs/examples/core-ai-first-saas-input/07-module-skill-governance-prd.md`
- mapped workstream artifacts:
  - `specs/agent-admin-workstream-v0/workstream-contract.md`
  - `specs/agent-admin-workstream-v0/capability-inventory.md`
- covered at v0:
  - AgentDefinition catalog/detail/lifecycle;
  - model refs and ToolPermissionBoundary reads/simulation;
  - prompt/skill/reference version reads;
  - skill/reference loader authorization through `readSkill(skillId)` and `readReferenceDoc(referenceId)`;
  - manifest reads;
  - seed/default material visibility and idempotent reseeding;
  - behavior proposal, review, approval, activation, cancellation;
  - request/response Akka Agent runtime, provider fail-closed behavior, and traces.
- narrower than old module PRDs:
  - full prompt editor/review/diff/version-history UI is not enumerated as a separate module capability set;
  - full skill catalog/editor/review/diff/version-history UI is not enumerated as a separate module capability set;
  - direct create/edit/disable/archive AgentDefinition CRUD details are abstracted into behavior-change proposal and activation capabilities.
- newer than old module PRDs:
  - reference documents and `readReferenceDoc(referenceId)` are explicitly included in Agent Admin v0.
- recommendation:
  - Clarify in docs that Agent Admin v0 is a workstream-oriented governance slice, not all old prompt/skill editor module detail.
  - Do not mark old Modules 5-7 fully completed by Agent Admin v0 unless the runtime/UI actually includes those editor/version-history paths.

### FINDING-CPR-005: Audit/Trace old module is the best v0 alignment

- classification: `covered` for v0 / `partial` for full module hardening
- affected PRD:
  - `docs/examples/core-ai-first-saas-input/08-module-audit-work-trace-prd.md`
- mapped workstream artifacts:
  - `specs/audit-trace-workstream-v0/workstream-contract.md`
  - `specs/audit-trace-workstream-v0/capability-inventory.md`
- covered at v0:
  - Audit/Trace functional workstream;
  - dashboard/search/detail/timeline;
  - denial/provider/tool/capability failure evidence;
  - bounded trace explanation through request-based Akka Agent;
  - redaction, tenant isolation, authorization, provider fail-closed, trace emissions, and safe UI states.
- deferred beyond v0:
  - retention administration;
  - legal hold;
  - external SIEM integration;
  - broad exports;
  - cross-tenant support/SaaS-owner investigation console;
  - durable audit-summary/anomaly AutonomousAgent tasks unless explicitly implemented.
- recommendation:
  - No immediate gap beyond doc clarity.

### FINDING-CPR-006: Evaluation and closed-loop improvement is mostly deferred from five-core v0

- classification: `partial` / `deferred`
- affected PRD:
  - `docs/examples/core-ai-first-saas-input/09-module-evaluation-closed-loop-improvement-prd.md`
- mapped workstream artifacts:
  - `specs/governance-policy-workstream-v0/workstream-contract.md`
  - `specs/governance-policy-workstream-v0/capability-inventory.md`
  - `specs/audit-trace-workstream-v0/*`
  - `specs/agent-admin-workstream-v0/*`
- covered at v0:
  - Governance/Policy workstream;
  - policy inventory/dashboard;
  - explanation/drafting;
  - proposal draft/submit/simulate/decision/activation/rollback;
  - optional durable policy-impact analysis task;
  - human approval default and audit/work trace requirements.
- deferred beyond v0:
  - standalone EvaluationRubric/EvaluationRun/EvaluationFinding lifecycle;
  - evaluator-agent scoring as a complete module;
  - replay/simulation against datasets beyond deterministic proposal simulation;
  - outcome monitoring objects and dashboards;
  - canary/shadow rollout and rich closed-loop improvement.
- recommendation:
  - Treat closed-loop improvement as full-core/later hardening work, not a five-core v0 completion requirement.
  - If needed, create a separate bounded mini-project for evaluation/outcome-loop expansion after v0 source-of-truth docs are clarified.

## Required follow-up for this mini-project

The next queue task should apply doc/source-of-truth follow-ups, primarily:

1. update `docs/examples/core-ai-first-saas-input/README.md` to label the directory as older module-sequenced full-core/provenance input and point to the newer workstream-oriented core path for current v0/starter planning;
2. update or add a short note in `docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md` that five-core v0 is narrower than the full canonical PRD target;
3. confirm `docs/skills-pack-user-guide.md` already distinguishes the newer core-app domain input path from the older module-sequenced sample, or adjust wording if needed;
4. avoid adding runtime implementation tasks from this reconciliation unless a doc now claims v0/full-core behavior that the traceability report classified as only partial/deferred.

## Non-findings

- No evidence requires reopening completed five-core v0 queues during this task.
- No runtime code change is justified by this reconciliation alone.
- No old PRD requirement should be counted as v0 complete solely because it appears in `10-canonical-core-app-prd.md`; completion must be based on current v0 contracts/runtime validation.
