# Surface Contract: Access Profile Dashboard

- surface-id: `access-profile-dashboard`
- type/version: dashboard/v1
- functional agents: Access/Profile
- payload schema:
  - current account summary, profile, settings, selected/default AuthContext, memberships, browser-safe capabilities, safe denial reasons
- allowed actions:
  - select tenant/customer context → `secure-tenant-user-foundation`
  - update own profile/settings → `secure-tenant-user-foundation`
- states:
  - loading `/api/me`, no memberships, forbidden/disabled account, stale settings save, successful context switch
- auth/security:
  - backend scopes every context; frontend never trusts stored context without `/api/me` refresh
- rendering tests:
  - active member sees only authorized contexts; disabled user gets safe denial; narrow layout preserves context switcher.
