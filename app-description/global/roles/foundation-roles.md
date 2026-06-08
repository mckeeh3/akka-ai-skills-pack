# Global Roles: Foundation roles

Role definitions are aligned to the reusable foundation doctrine and current implementation role/capability model.

- `authenticated-member`: signed-in human with at least one active membership and selected `AuthContext`.
- `tenant-admin`: manages tenant employees, tenant-owned customer administration, invitations, role assignments, and support access within authorized scope.
- `customer-admin`: manages customer users within the selected tenant/customer boundary.
- `auditor`: reads authorized audit and trace evidence without mutation authority.
- `policy-owner-approver`: reviews, approves, activates, or rolls back governance and behavior changes within authorized scope.
- `agent-steward`: manages governed managed-agent behavior records and proposals within authorized scope.
- `support-access-operator`: time-bound/support-scoped actor whose access is explicit, expiring, approved, and audited.

Backend roles/capabilities and selected context are authoritative; frontend visibility is never an authorization grant.
