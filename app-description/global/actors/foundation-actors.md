# Global Actors: Foundation actors

Actors name identity and trust categories reused across workstreams. They do not grant authority by themselves. Authority comes from selected `AuthContext`, active membership/support-access state, roles/capability grants, policies, governed-tool contracts, backend authorization, and tool boundaries. Worker responsibilities and adapter exposure live in `../workers/foundation-workers.md` plus workstream-local bindings.

- `authenticated-human`: WorkOS/AuthKit-authenticated browser user linked to Akka-owned account, membership, role/capability, and selected `AuthContext` state.
- `saas-owner-actor`: authenticated human acting in a SaaS Owner selected context with explicit SaaS Owner capability grants.
- `organization-admin-actor`: authenticated human acting as Organization/Tenant Admin inside one Organization-backed Tenant context.
- `customer-admin-actor`: authenticated human acting as Customer Admin inside one tenant-owned Customer boundary.
- `tenant-employee-actor`: authenticated non-admin Organization/Tenant member with My Account and explicitly granted future tenant workstream capability scope.
- `customer-user-actor`: authenticated non-admin Customer-scoped member with My Account and explicitly granted future customer workstream capability scope.
- `support-access-actor`: time-bound support actor with explicit, approved, expiring, audited access to a scoped tenant/customer context.
- `auditor-investigator`: authenticated human with trace/audit responsibility and backend-authorized, redacted evidence access.
- `policy-reviewer`: authenticated human authorized to review, approve, reject, or audit governance, behavior, and authority-impacting changes.
- `functional-agent-actor`: AI-backed functional-agent identity resolved through managed-agent behavior state, active model policy, prompt/skill/reference manifests, and tool boundaries. This actor has no authority outside declared `agent_tool_call` or proposal/plan bindings.
- `system-service-actor`: deterministic service, workflow, timer, consumer, projection, endpoint, onboarding, or integration caller with explicit provenance and capability-owned authority basis.
- `provider-boundary-actor`: external WorkOS/AuthKit, Resend/captured-outbox, or model-provider boundary. Secrets remain server-side and unavailable providers fail closed with traceable blockers.
