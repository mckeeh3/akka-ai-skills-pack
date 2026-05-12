# State Model: Tenant User Access

- customer/tenant:
  - states: active, suspended, archived
  - owns tenant-scoped data and configuration
- user profile:
  - states: active, disabled
  - may belong to multiple tenants through memberships
- membership:
  - states: invited, active, suspended, removed
  - binds user to tenant with roles and permissions
- invitation:
  - states: pending, accepted, expired, revoked
  - expires through timed action
- tenant settings:
  - current-state configuration; changes require tenant admin permission
