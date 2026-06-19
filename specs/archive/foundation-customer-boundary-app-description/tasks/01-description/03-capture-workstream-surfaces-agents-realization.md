# TASK-FCBAD-01-003: Capture customer boundary workstream, surfaces, agents, and realization bindings

## Objective

Update active `app-description/` current-intent nodes so the foundation customer boundary is unambiguous at the User Admin workstream, surface/action, functional-agent, governed-tool, policy, trace/test, and Akka/frontend/API realization levels.

## Required reads

- `AGENTS.md`
- `.agents/skills/docs/current-intent-model.md`
- `.agents/skills/docs/intent-to-realization-flow.md`
- `.agents/skills/docs/intent-compiler-skill-contracts.md`
- `app-description/AGENTS.md`
- `specs/foundation-customer-boundary-app-description/README.md`
- `specs/foundation-customer-boundary-app-description/conversation-capture.md`
- `specs/foundation-customer-boundary-app-description/customer-boundary-evidence-and-gap-map.md`
- this task brief
- app-description nodes edited by `TASK-FCBAD-01-002`
- app-description nodes identified by the gap map for workstream/surface/agent/tool/policy/trace/test/realization edits

## Expected outputs

Edit the smallest complete set of active app-description files, likely under:

- `app-description/domains/core-starter/workstreams/user-admin/`
- `app-description/global/agents/foundation-functional-agents.md` if the global `user-admin-agent` definition needs clarification
- `app-description/global/tools/foundation-governed-tools.md` if shared governed tool descriptions need clarification
- `app-description/global/surfaces/foundation-surface-patterns.md` if reusable structured-surface semantics need clarification
- `app-description/domains/core-starter/realization/traceability.md` if cross-node traceability needs updating.

## Description requirements

The updated graph must state:

- User Admin/Tenant Customer Admin branch purpose, access, behavior, and forbidden actions;
- customer directory, detail, create, rename, suspend/reactivate confirmation, Customer Admin list/invitation/detail, system-message/denial, and audit-link surfaces;
- surface action edges such as list/read/create/rename/suspend/reactivate and Customer Admin invitation/membership operations;
- browser actions are human-backed adapters for backend capabilities, not authority grants;
- `user-admin-agent` can summarize/draft/recommend/prepare customer-boundary administration but cannot autonomously mutate customers, grant authority, cross scope, or affect business CRM/customer data;
- governed tools/capability ids, actor adapters, idempotency, confirmation/approval requirements, and denial result shapes;
- trace/audit events for allowed, denied, no-op, idempotent replay, provider/outbox failure, and cross-scope attempts;
- tests/acceptance expectations for customer lifecycle, Customer Admin branch, forbidden Customer Admin tenant actions, cross-customer denial, redaction, and frontend non-authority;
- Akka/API/frontend realization mapping to existing component families without overclaiming new runtime behavior.

## Required checks

- `git diff --check`
- Targeted proof that active User Admin app-description files mention customer surfaces, `action-customer`, `tenant.customer`, Customer Admin limits, `TenantCustomerAdminService` or equivalent realization mapping, and trace/test obligations.

## Done criteria

- Workstream/surface/agent/tool/policy/trace/test/realization bindings are specific enough for future realization or drift repair tasks.
- No runtime code is changed.
- Queue status and notes are updated and committed with the app-description changes.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent`.
- Attention/non-UI reason: docs-only capture; future runtime actions may emit attention/system-message surfaces as described.
- Surface graph: customer directory/detail/lifecycle/Customer Admin branch surfaces and action edges.
- Governed-tool/capability: `tenant.customer.*`, `tenant.customer_admin.*`, User Admin invitation/membership tools as applicable.
- AuthContext/scope: tenant context for Customer lifecycle; customer context for Customer Admin; backend denial for scope/capability mismatch.
- Akka substrate: docs-only realization mapping to endpoint/service/entity/view/workstream/frontend components.
- Audit/work trace: capture audit/work trace obligations and redaction.
- Local validation path: `git diff --check` plus targeted `rg` proof.
