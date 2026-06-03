# Readiness Status

- current-state: scaffolded-core-app-in-progress
- readiness scope: five-core starter advancing toward full-core SaaS readiness

## Ready for

- local scaffold inspection and extension through this `app-description/` tree;
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
- searchable Audit/Trace investigation surfaces with scoped redaction/export rules;
- Governance/Policy workflows, approval gates, impact analysis, review surfaces, and policy-change controls;
- support-access and billing-boundary semantics where the target product requires them;
- full tenant isolation, forbidden access, disabled-user, role/scope denial, audit, UI, accessibility, and runtime smoke coverage.

## Domain-specific expansion gate

CRM, SMB operations, billing, scheduling, inventory, reporting, or other domain-specific features should be added after this tree records:

1. the owning existing or new functional agent/workstream;
2. surface actions and attention items;
3. governed capability/tool contracts;
4. auth/security, audit/trace, behavior, tests, and UI implications;
5. implementation/regeneration impact.

## Last update basis

Created by the AI-first SaaS starter scaffold as the initial core app document structure.
