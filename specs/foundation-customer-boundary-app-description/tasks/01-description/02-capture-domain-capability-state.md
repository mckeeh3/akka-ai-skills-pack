# TASK-FCBAD-01-002: Capture customer boundary domain, capability, and state intent

## Objective

Update active `app-description/` current-intent nodes so the foundation customer boundary is unambiguous at the domain, capability, and durable state levels.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`
- `app-description/AGENTS.md`
- `specs/foundation-customer-boundary-app-description/README.md`
- `specs/foundation-customer-boundary-app-description/conversation-capture.md`
- `specs/foundation-customer-boundary-app-description/customer-boundary-evidence-and-gap-map.md`
- this task brief
- app-description nodes identified by the gap map for domain/capability/state edits

## Expected outputs

Edit the smallest complete set of active app-description files, likely including:

- `app-description/domains/core-starter/domain.md`
- one or more files under `app-description/domains/core-starter/capabilities/`
- one or more files under `app-description/domains/core-starter/data-state/`
- global role/policy references only if needed for clarity.

## Description requirements

The updated graph must state:

- foundation customer boundary ownership and non-goals;
- organization/tenant-level customer boundary semantics;
- Customer Admin role and selected customer context semantics;
- capability contracts for list/read/create/rename/suspend/reactivate customer boundary and Customer Admin bootstrap/maintenance;
- durable state responsibilities for Customer, Tenant, Membership, AuthContext, invitations, audit/work traces, and redaction boundaries;
- idempotency and forbidden behavior expectations;
- explicit separation from business-specific CRM, customer success, sales, support/service, billing, and customer intelligence domains;
- note that support/service may span organization and customer layers in downstream business domains, but must still bind each capability to explicit organization/customer/affected-customer/assigned-case scope.

## Required checks

- `git diff --check`
- Targeted proof that active app-description domain/capability/state files mention the foundation customer boundary, business-domain non-goals, `tenant.customer.*` capabilities, and tenant/customer scoping.

## Done criteria

- Domain/capability/state intent is sufficiently specific for later workstream binding edits without guessing.
- No runtime code is changed.
- Queue status and notes are updated and committed with the app-description changes.

## Vertical workstream contract

- Scope: docs-only current-intent capture for foundation customer boundary domain/capability/state.
- Attention/non-UI reason: no runtime attention item; app-description-only source-of-truth update.
- Capability/foundation scope: `tenant.customer.*`, Customer Admin branch, identity/customer boundary state.
- AuthContext/scope: selected tenant context for customer lifecycle; selected customer context for Customer Admin operations; denial expectations documented.
- Akka substrate: docs-only mapping prerequisites; no implementation.
- Audit/work trace: document required audit/work trace and redaction obligations.
- Local validation path: `git diff --check` plus targeted `rg` proof.
