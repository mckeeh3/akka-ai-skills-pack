# Conversation Capture: Foundation Customer Boundary App-description

## Discussion summary

The user asked what customer-related backend and frontend functionality has been implemented so far. The implementation inventory found that the current repository already includes foundation customer-boundary administration rather than a full CRM/customer domain:

- `Customer` foundation record under identity state.
- `TenantCustomerAdminService` for list/read/create/rename/suspend/reactivate customer boundaries.
- `/api/admin/customers...` HTTP routes and customer-admin invitation/membership routes.
- User Admin workstream action routing and structured customer/customer-admin surfaces.
- Frontend API methods and scoped admin surfaces for customer directory, detail, lifecycle forms, and Customer Admin flows.

The user then asked how customer, customer management, CRM, and related functionality should be organized in secure AI-first SaaS apps. The accepted direction was:

- Do not model all customer concerns as one monolithic domain.
- Keep the foundation customer boundary generic, security-focused, and small.
- Model CRM/account management, customer success, sales/revenue, support/service, billing/entitlements, and customer intelligence as separate business domains or bounded contexts when needed.
- Present unified experiences such as Customer 360 at the workstream/surface layer while preserving backend domain ownership and capability boundaries.

The user clarified that most customer domains are organization-level, while support/service spans organization and customer layers. The accepted direction was:

- Most business customer domains are organization/tenant-level domains that own customer-scoped records.
- Support/service may have organization-scoped objects such as queues, SLA policies, escalation rules, and incidents, and customer-scoped objects such as cases, comments, attachments, SLA clocks, and escalations.
- Individual capabilities must explicitly declare organization, customer, assigned-case, or affected-customer scope.

The user also asked whether customer domains need to be tailored for specific business types. The accepted direction was:

- The foundation customer boundary remains generic.
- Business customer domains should be tailored to business type: B2B SaaS account/contact/usage/renewal, healthcare patient/care/claims, professional-services client/engagement/project, marketplace buyer/seller/merchant/order/dispute, field-service site/asset/work-order, etc.

## Current request

The user requested a focused mini-project for the foundation customer boundary:

> let's focus on the foundation customer boundary. we need to capture and describe the foundation customer boundary domains, workstreams, surfaces, agents, and backend akka components in the app-description. verification should ask is the description sufficiently unambiguous, and if needed add more tasks until the sufficiently unambiguous goal has been reached.

## Accepted current intent

The foundation customer boundary must be captured in the authoritative `app-description/` graph as current intent. It should describe:

- domain and non-domain boundaries;
- workstream placement and cross-workstream relationships;
- structured surfaces and action edges;
- functional-agent authority and forbidden effects;
- governed tool/capability contracts;
- backend Akka components and API/frontend realization mapping;
- audit/work traces, redaction, idempotency, authorization, tenant/customer scoping, and denial behavior;
- tests/verification expectations; and
- explicit separation from business-specific customer domains.

## Decisions

- Treat this as root app-realization work, not skills-pack maintenance.
- Use `specs/foundation-customer-boundary-app-description/` as the durable mini-project path.
- Keep tasks app-description/planning focused; runtime source is evidence unless later verification appends implementation tasks.
- The terminal verification task must decide whether the resulting app-description is sufficiently unambiguous. If no, it must append the next bounded description tasks and a new terminal verification task.

## Open concerns

- Existing app-description may already mention customer concepts in multiple places; tasks must update the smallest complete node set without duplicating reusable foundation doctrine.
- Some implemented customer API/workstream details may be more complete than current app-description. Description tasks should capture intended current behavior, not blindly mirror accidental implementation details.
- If verification finds runtime drift, it should record follow-up recommendations or append a separate bounded task only if needed to make the description unambiguous; this mini-project is not a broad runtime remediation effort.
