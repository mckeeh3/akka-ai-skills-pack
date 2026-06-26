# Access: Governance/Policy

## Authorized roles

- `saas-owner-admin`: manages SaaS default policy values in a SaaS-owner/defaults selected context. Default changes apply only where tenants have not set overrides.
- `tenant-admin`: reads effective policies for their tenant, creates/updates tenant business-governance overrides, resets tenant overrides back to defaults, and views policy history for their authorized tenant/customer/account scope.
- `auditor`: reads authorized policy defaults, overrides, effective values, history, runtime-decision evidence, and trace links. Audit access alone does not allow policy mutation.
- `support`: reads authorized tenant policy state and history for support/troubleshooting only when support access is active and scoped by backend authorization.

## Scope rules

All reads, writes, agent turns, surface actions, and governed-tool invocations use backend-owned selected `AuthContext`, tenant/customer/account ids, membership status, role/capability grants, and hard platform-security checks.

Policy scopes may include tenant, agent, workstream, action/tool, role, and customer/account. Browser-provided scope values are untrusted hints; the backend resolves and authorizes the selected scope before returning or changing policy state.

Tenant admins may override business-governance policies for their authorized tenant scope. They cannot change SaaS defaults and cannot override platform security controls.

SaaS owners may change defaults in defaults context but must not silently overwrite tenant overrides.

## Denials

Disabled users, inactive memberships, missing selected context, missing capability, cross-tenant/customer access, hidden scope targets, unsupported policy types/scopes, missing change reason, and attempts to override hard platform security are denied server-side, produce safe system-message feedback where user-facing, and emit required traces without exposing protected data.
