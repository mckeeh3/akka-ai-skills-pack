# Global Roles: Foundation roles

Role definitions are aligned to the reusable foundation doctrine and current implementation role/capability model.

- `authenticated-member`: signed-in human with at least one active membership and selected `AuthContext`.
- `saas-owner-admin`: app-owner/platform operator who may administer other SaaS Owner Admin users, customer-facing Organizations backed by internal Tenant boundaries, and Organization Admin bootstrap/maintenance for those Organizations when the selected `AuthContext` is SaaS Owner scoped and backend capabilities such as `saas_owner.user.manage`, `saas_owner.organization.*`, or internal `saas_owner.tenant.read/manage` mappings are present. This role does not gain tenant/customer application-data access, support access, or billing-derived authority by implication.
- `tenant-admin`: Organization Admin for a customer-facing Organization/Tenant; manages tenant employees, customer records under that Organization, Customer Admin bootstrap/maintenance, invitations, role assignments, and support access within authorized tenant/customer scope. This role cannot manage sibling Organizations, SaaS Owner Admins, or grant SaaS Owner authority.
- `customer-admin`: manages customer users within the selected tenant/customer boundary. This role cannot create sibling customers, manage Organization Admins, or grant tenant/app-owner authority.
- `auditor`: reads authorized audit and trace evidence without mutation authority.
- `policy-owner-approver`: reviews, approves, activates, or rolls back governance and behavior changes within authorized scope.
- `agent-steward`: manages governed managed-agent behavior records and proposals within authorized scope.
- `support-access-operator`: time-bound/support-scoped actor whose access is explicit, expiring, approved, and audited.

Backend roles/capabilities and selected context are authoritative; frontend visibility is never an authorization grant.
