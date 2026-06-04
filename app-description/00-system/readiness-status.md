# Readiness Status

- current-state: five-core-workstream-starter-partially-implemented
- readiness scope: five-core starter advancing toward full-core SaaS readiness; full-core SaaS is not ready

## Ready for

- local scaffold inspection and extension through this `app-description/` tree;
- bounded runtime/UI alignment work against the five core workstream shell;
- implementation planning from the five core workstreams;
- focused follow-up tasks that close gaps in My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy;
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

Before declaring the core app complete, close and prove these gaps:

- production WorkOS/AuthKit configuration and fail-closed local validation;
- complete invitation onboarding with Resend and captured local/dev/test outbox;
- complete User Admin structured surfaces for users, invitations, roles/memberships, access review, support access, and admin audit;
- complete Agent Admin lifecycle for governed agent definitions, prompts, skills, references, manifests, tool boundaries, proposals, approvals, and traces;
- searchable Audit/Trace investigation surfaces with scoped redaction/export rules (local backend/workstream coverage now recorded in `specs/full-core-saas-readiness/audit-governance-validation.md`; full runtime smoke still pending);
- Governance/Policy workflows, approval gates, impact analysis, review surfaces, and policy-change controls (local backend/workstream coverage now recorded in `specs/full-core-saas-readiness/audit-governance-validation.md`; full runtime smoke still pending);
- support-access and billing-boundary semantics where the target product requires them;
- full tenant isolation, forbidden access, disabled-user, role/scope denial, audit, UI, accessibility, and runtime smoke coverage.

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

This evidence supports continued five-core starter realization work. It does not close full-core SaaS gaps unless the real local Akka/API/UI path is proven for each named feature.

## Last update basis

Updated during workstream design/implementation alignment to reflect current five-core starter runtime evidence, bounded realtime semantics, and remaining full-core gaps.
