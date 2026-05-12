# Rules: Tenant Authorization

- every command and query that touches tenant data requires tenant context
- user must have an active membership in the tenant
- requested action must be allowed by role/permission grants
- platform admin access must be explicit and audited
- users cannot infer existence of objects in tenants they cannot access
- tenant switch changes active scope but not identity
- authorization failures return safe errors and emit security/audit signals where appropriate
