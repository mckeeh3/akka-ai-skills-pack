# Readiness Status

- current-state: full-core-local-test-scope-validated-with-live-resend-and-model-smokes
- readiness scope: full-core foundation local/test scope for the five core functional-agent workstreams is smoke-validated; live Resend invite-email and model-provider workstream-agent smokes have passed; live WorkOS/AuthKit provider smoke and billing implementation are not ready

## Ready for

- local scaffold inspection and extension through this `app-description/` tree;
- local/test-scope evaluation of the full-core foundation across My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy;
- implementation planning from the five core workstreams;
- focused follow-up tasks that close the remaining live WorkOS/AuthKit provider gap;
- future product-scope planning for billing implementation or timer-backed invitation reminders if a target product requires them;
- adding domain-specific features only after the affected core/security/capability contracts are preserved.

## Five-core starter readiness target

The starter may be called five-core ready only when normal local runtime proves:

- bootstrap or authenticated identity and selected `AuthContext`;
- role-authorized visibility for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy;
- durable workstream log entries for requests/responses/system messages;
- `markdown_response` v1 and system-message surfaces render safely;
- model-backed workstream turns invoke the governed Akka `Agent` runtime path with active `AgentDefinition`, prompt, manifests, tool boundary, loader tools, `effects().tools(runtimeTools)`, provider fail-closed behavior, and traces;
- protected actions map to backend governed capabilities and governed-tools;
- authorization denials, audit/work traces, tenant isolation, and frontend secret boundaries are tested.

## Full-core SaaS readiness gaps

The local/test-scope full-core foundation smoke is recorded in `specs/full-core-saas-readiness/full-core-runtime-smoke.md`. Before declaring production full-core SaaS completion, close and prove these remaining gaps:

- live WorkOS/AuthKit provider smoke with backend-only issuer/audience/provider configuration and a real AuthKit app;
- billing implementation remains explicitly deferred for the current full-core target; the current scope only preserves the billing-boundary invariant that billing/subscription metadata must not grant Tenant application-data access;
- timer-backed invitation reminder scheduling remains explicitly deferred for the current full-core target; invitation expiry/resend behavior is validated, but scheduled reminders require a future product-scope task;
- optional repair of stale validation tooling such as `tools/prove-workstream-icons-v0.sh` if retained.

## Domain-specific expansion gate

CRM, SMB operations, billing, scheduling, inventory, reporting, or other domain-specific features should be added after this tree records:

1. the owning existing or new functional agent/workstream;
2. surface actions and attention items;
3. governed capability/tool contracts;
4. auth/security, audit/trace, behavior, tests, and UI implications;
5. implementation/regeneration impact.

## Current implementation evidence

Recent alignment work establishes starter-level contracts and runtime behavior for:

- canonical aliases between app-description ids and implementation ids for the five core functional-agent workstreams, dashboards, shell requests, and primary surface actions;
- exact surface-action to governed-tool/capability mappings for representative core workstream actions;
- backend-authoritative default dashboard loading and safe denial/system-message behavior for forbidden shell targets;
- backend-authoritative prompt/surface alias resolution for common workstream requests;
- explicit realtime v1 semantics as bounded SSE replay/refresh behavior, not long-lived true-live streaming;
- Audit/Trace scoped search/detail/timeline/failure-evidence/investigation-note surfaces and Governance/Policy proposal/simulation/decision/activation/rollback/outcome-note surfaces through backend-authorized workstream actions, with model-backed summary/impact workers failing closed until real provider/runtime/tool-boundary configuration is supplied.

This evidence, combined with `specs/full-core-saas-readiness/full-core-runtime-smoke.md`, `specs/full-core-saas-readiness/live-resend-provider-smoke.md`, and `specs/full-core-saas-readiness/live-model-provider-smoke.md`, supports local/test-scope full-core foundation evaluation for the implemented scope plus live Resend/model-provider proof. It does not close live WorkOS/AuthKit provider readiness. Billing implementation and timer-backed invitation reminders are intentionally deferred for the current target and must be added through future product-scope tasks before any production billing-ready or scheduled-reminder-ready claim.

## Last update basis

Updated during TASK-FCSR-08-002 after live Resend invite-email provider smoke passed, preserving TASK-FCSR-07-001 local/test-scope runtime smoke evidence, TASK-FCSR-08-003 live model-provider smoke evidence, and remaining live WorkOS/AuthKit plus deferred billing/timer-reminder scope.
