# Frontend API Contracts

- API style:
  - typed TypeScript client generated or maintained from backend contracts where feasible
  - all API calls include auth/session context and selected tenant context
- endpoint families:
  - `/api/me` profile and memberships
  - `/api/tenants` tenant switch/settings where permitted
  - `/api/admin/users` users, invitations, memberships, roles
  - `/api/goals` goals, plans, execution controls
  - `/api/decisions` decision queue and decision actions
  - `/api/governance` policies and proposals
  - `/api/audit` trace search and detail
  - `/api/realtime` SSE/WebSocket subscriptions
- error model:
  - safe message
  - machine code
  - correlation id
  - field errors where applicable
