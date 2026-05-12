# Authorization Rules

- required checks:
  - authenticated principal
  - active tenant membership
  - tenant status permits action
  - role/permission allows action
  - policy gate allows action or requires approval
- role baseline:
  - platform-admin: cross-tenant operational support with strict audit
  - tenant-admin: manage tenant settings, users, roles, and policies within tenant
  - supervisor: create/launch/supervise goals and handle exceptions
  - reviewer: decide assigned approval/decision cards
  - member: use permitted tenant features
  - auditor: read audit/trace surfaces without mutation rights
- enforcement:
  - endpoint checks prevent unauthorized command submission
  - command handlers/workflows re-check sensitive state transitions
