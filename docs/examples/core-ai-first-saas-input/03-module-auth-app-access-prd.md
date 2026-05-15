# Module 1 PRD: Minimal Auth and App Access MVP

## Status

Detailed PRD for the first MVP module in the progressive core AI-first SaaS seed app.

Read first:

- `00-document-development-process-context.md`
- `01-core-seed-progression-plan.md`
- `02-persistent-discussion-capture.md`

## 1. Module purpose

Module 1 establishes the smallest demonstrable full-stack secure SaaS foundation that every later module can extend safely.

The module proves that a real browser user can authenticate, enter the app, retrieve their current account and authorization context from the backend, see an authenticated application shell, and encounter correct unauthenticated, forbidden, and tenant-isolated behavior.

This is not a complete user administration module. It intentionally creates only the minimal access substrate required for first app entry and safe future feature work.

## 2. User-visible outcome

At completion, a demo operator can:

1. open the browser app;
2. see a signed-out landing/sign-in screen when unauthenticated;
3. authenticate through the configured auth seam;
4. land inside the authenticated app shell;
5. see their profile, account status, active tenant, and membership context;
6. switch between available tenant contexts if their account has more than one active membership;
7. see protected navigation items enabled or hidden according to capabilities;
8. receive clear unauthenticated, forbidden, disabled-account, and no-access states;
9. confirm through tests that provider secrets are not shipped to the frontend and backend authorization is enforced.

## 3. MVP boundaries

### In scope

- Authentication seam for browser users, preferably WorkOS/AuthKit in production-like configuration.
- Local Akka-owned account and authorization state.
- Minimal SaaS organization model required for app access: Tenant and Membership.
- Minimal role/capability model sufficient to protect Module 1 routes and prepare for Module 2.
- `/api/me` endpoint returning browser-safe identity, profile, memberships, selected context, and capabilities.
- Backend authorization helper/pattern used by all protected endpoints in this module.
- Authenticated React/Vite/TypeScript app shell.
- Sign-in/sign-out browser flows.
- Context display and minimal context selection.
- Protected sample page used to prove authorization behavior.
- Audit events for authentication-context access, selected context changes, authorization denials, and disabled/no-access attempts.
- Security and UI tests for core access paths.

### Out of scope for Module 1

- Full user directory and user management.
- Full invitation lifecycle, resend/revoke/expiry/reminders, and email delivery UI.
- Role editing and capability management UI.
- Customer organization model beyond optional placeholder fields required by later modules.
- Support access workflow.
- Billing/subscription enforcement beyond a placeholder boundary.
- Agent definitions, prompts, skills, policy governance, work traces, evaluations, and closed-loop improvement.
- Advanced session-risk scoring, MFA administration, SCIM, SSO domain administration, or enterprise identity configuration UI.

## 4. Actors

| Actor | Description | Module 1 expectations |
|---|---|---|
| Unauthenticated visitor | Browser user without an authenticated session. | Can view public shell/sign-in entry only. Protected API and pages return unauthenticated state. |
| Authenticated member | Signed-in account with at least one active tenant membership. | Can enter the app, call `/api/me`, view profile/context, and access pages allowed by capabilities. |
| Multi-tenant member | Signed-in account with active memberships in more than one tenant. | Can select an active tenant context and see UI/API data scoped to that context. |
| Disabled account | Signed-in identity mapped to a local disabled account. | Cannot enter protected app areas; receives disabled-account state; denial is audited. |
| No-access authenticated user | Signed-in identity with no accepted active membership. | Cannot enter protected app areas; receives no-access state; denial is audited. |
| Seed administrator / operator | Initial configured operator used for demos and tests. | Has initial tenant membership and minimal admin capability required to enter the app and later bootstrap Module 2. |

## 5. Authentication and first access assumptions

Module 1 must define an explicit authentication seam rather than hard-coding assumptions into UI state.

### Production-like behavior

- Browser authentication is delegated to WorkOS/AuthKit or an equivalent provider seam.
- Provider secrets remain backend-only.
- The frontend receives only browser-safe session status and user/account data from app APIs.
- Backend APIs validate the authenticated session/JWT/cookie according to the selected provider seam before resolving local authorization.

### Local development and tests

- Tests may use a fake auth adapter or signed test token mechanism.
- The fake adapter must be explicit, test-only, and unable to appear as the production provider configuration by accident.
- Local seed data may create one tenant and one seed operator membership to make the first demo possible.

### First-login account linking

For Module 1, first access is intentionally minimal:

- an external authenticated subject can be linked to a local `Account` only if it matches explicit seed/test bootstrap data or an accepted local access rule configured for the demo environment;
- no privileged self-registration is allowed;
- if no valid local account or membership exists, `/api/me` returns a no-access state rather than silently creating broad access;
- full invitation-based onboarding is deferred to Module 2.

## 6. Durable objects and state ownership

Module 1 should introduce the minimal durable objects below. Names may be adjusted during Akka decomposition, but the semantics should remain.

### Account

Represents a human identity known to the application.

Required fields:

- `accountId`
- external identity/provider subject reference
- primary email, display name, optional avatar URL
- status: `ACTIVE`, `DISABLED`, `NO_ACCESS_PENDING`, or equivalent
- timestamps: created, updated, last authenticated/seen

State owner expectation: Key Value Entity is sufficient for current account state unless later audit-grade account lifecycle history is pulled forward from Module 2.

### UserProfile

Browser-safe profile information owned by the app.

Required fields:

- `accountId`
- display name
- email display preference if needed
- avatar URL
- locale/time zone placeholders if easy to include

State owner expectation: Key Value Entity or embedded Account state is acceptable for Module 1. Keep it separable enough for later settings/profile pages.

### UserSettings

Minimal user preferences needed by the app shell.

Required fields:

- `accountId`
- default tenant/context selection if applicable
- theme preference placeholder if used by the shell

State owner expectation: Key Value Entity or embedded Account-adjacent state.

### Tenant

Top-level organization boundary that owns application data.

Required fields:

- `tenantId`
- tenant display name
- status: `ACTIVE` or `DISABLED`
- timestamps

State owner expectation: Key Value Entity is sufficient for Module 1.

### Membership

Connects an Account to a Tenant with a role/capability set.

Required fields:

- `membershipId`
- `accountId`
- `tenantId`
- status: `ACTIVE`, `DISABLED`, `REVOKED`, or equivalent
- role ids or capability ids
- timestamps

State owner expectation: Key Value Entity is sufficient for Module 1, with views for lookup by account and tenant.

### Role / Capability

Minimal permission model used for backend checks and UI affordances.

Required initial capabilities:

- `app.access` — may enter authenticated app shell.
- `profile.read` — may read own profile through `/api/me`.
- `tenant.context.select` — may select among own active memberships.
- `admin.bootstrap.access` or similar placeholder — reserved for initial operator and later Module 2 navigation.

State owner expectation: static configuration or seed KV state is acceptable in Module 1, but the shape must allow Module 2 to replace/extend it with editable roles.

### AuthContext

The resolved authorization context for a request or selected browser session.

Required fields in API responses or internal command context:

- `accountId`
- selected `tenantId`
- selected `membershipId`
- effective roles/capabilities
- account and membership statuses
- correlation/request id where applicable

State owner expectation: not necessarily durable as a standalone object. Selected default context may be stored in UserSettings.

### AdminAuditEvent

Minimal audit record for security-sensitive access behavior.

Required event types:

- `ME_ACCESSED`
- `AUTH_CONTEXT_SELECTED`
- `AUTH_DENIED_UNAUTHENTICATED`
- `AUTH_DENIED_FORBIDDEN`
- `AUTH_DENIED_DISABLED_ACCOUNT`
- `AUTH_DENIED_NO_ACTIVE_MEMBERSHIP`
- `AUTH_DENIED_CROSS_TENANT`

State owner expectation: append-only event/audit storage using an Event Sourced Entity or an audit event ingestion pattern suitable for later Module 6 trace expansion. Module 1 needs queryable enough audit evidence for tests and a basic diagnostics view or API.

## 7. Capabilities

### 7.1 Public landing and sign-in

The web app must provide a public entry state with:

- product/app name placeholder;
- short explanation that sign-in is required;
- sign-in action wired to the auth seam;
- loading/error state for auth configuration problems;
- no provider secrets embedded in rendered assets.

### 7.2 Sign-out

Authenticated users can sign out from the app shell.

Expected behavior:

- sign-out clears browser session through the provider seam;
- local selected context cache is cleared or invalidated as appropriate;
- user returns to the signed-out state;
- protected API calls after sign-out fail as unauthenticated.

### 7.3 `/api/me`

The backend exposes a browser-safe current-user endpoint.

`GET /api/me` returns one of these states:

- unauthenticated;
- authenticated but no local access;
- disabled account;
- active with memberships and selected context.

For active users, response includes:

- account id, email, display name, avatar URL;
- account status;
- profile and app settings;
- memberships visible to the account;
- selected tenant/membership context;
- effective browser-safe capabilities;
- flags for app shell navigation and module availability;
- no provider secrets, raw tokens, internal role secrets, or backend-only policy details.

### 7.4 Context selection

If the user has multiple active memberships, they can choose the active tenant context.

Required behavior:

- UI shows current tenant/context in the app header or account menu;
- context selector appears only when more than one active context exists;
- selecting a context calls a backend endpoint that verifies the membership belongs to the current account and is active;
- selected context is persisted as a user setting or signed/session-bound app preference;
- all protected API responses use the selected authorized context;
- attempts to select another user's tenant or a disabled membership are denied and audited.

### 7.5 Protected sample page

Module 1 includes at least one simple protected page to prove app access.

The page may be named Dashboard, Home, or App Access.

It displays:

- current user's display name/email;
- selected tenant name/id;
- membership role/capability summary;
- a test-only or diagnostic card showing which access checks passed;
- links or disabled navigation placeholders for future modules.

### 7.6 Forbidden and no-access states

The UI must distinguish:

- unauthenticated: sign in required;
- authenticated with no active membership: contact administrator / access not configured;
- disabled account: account disabled;
- forbidden route: signed in but lacks capability;
- selected context invalid: prompt to choose another context or refresh `/api/me`.

These states must not leak tenant data, internal role configuration, raw tokens, or sensitive diagnostics.

## 8. UI requirements

### 8.1 Page and route inventory

Minimum routes:

- `/` public landing or redirect decision route;
- `/sign-in` or provider-hosted sign-in entry integration;
- `/sign-out` or app-shell sign-out action;
- `/app` authenticated landing/dashboard;
- `/app/profile` minimal profile/context page or panel;
- `/app/forbidden` forbidden state page;
- `/app/no-access` no active access state page;
- optional `/app/dev/security-check` diagnostic page guarded to seed operator/test environment only.

### 8.2 App shell

The authenticated app shell must include:

- top-level header;
- product name;
- current account menu;
- current tenant/context display;
- context selector when applicable;
- sign-out action;
- navigation area with current Module 1 pages and disabled/hidden future module placeholders;
- loading skeleton while `/api/me` is resolving;
- stable error boundary for failed `/api/me` or API errors.

### 8.3 Client state expectations

The frontend should treat `/api/me` as the source of truth for browser-safe auth state.

Required client states:

- initial unknown/loading;
- unauthenticated;
- active authenticated;
- no-access;
- disabled;
- forbidden;
- network/API error;
- context-switch pending.

Do not infer authorization only from route metadata or local storage.

### 8.4 Accessibility and responsive behavior

- Sign-in, sign-out, context selection, and forbidden/no-access messages must be keyboard accessible.
- Main shell landmarks should support screen readers.
- Context selector and account menu must expose accessible labels.
- The app should remain usable on narrow desktop/tablet widths; mobile polish can be basic in Module 1.

## 9. API requirements

Exact endpoint names may be adjusted during implementation planning, but the module must cover these contracts.

### `GET /api/me`

Returns current browser-safe identity and context as described above.

Authorization behavior:

- unauthenticated request returns 401 or a typed unauthenticated response according to frontend contract;
- authenticated request without local access returns a typed no-access response and no tenant data;
- disabled account returns a typed disabled response and no tenant data beyond safe self context;
- active account returns memberships and selected context scoped to that account.

### `POST /api/me/context`

Request:

- selected `tenantId` or `membershipId`.

Behavior:

- requires authenticated active account;
- validates membership belongs to account and is active;
- updates selected context/default setting;
- returns refreshed `/api/me` payload;
- denies cross-tenant or disabled-membership selection and emits audit event.

### Protected sample endpoint

Example: `GET /api/app-access/summary`.

Behavior:

- requires active account, active membership, selected tenant context, and `app.access` capability;
- returns only selected-tenant data;
- used by UI and tests to prove central authorization helper behavior.

### Optional audit diagnostics endpoint

Example: `GET /api/dev/audit-events` or test-only view/query.

Behavior:

- only enabled in test/dev or guarded by seed operator capability;
- supports verifying audit emission without creating a full Module 6 audit UI.

## 10. Authorization rules

Module 1 must establish a repeatable backend authorization pattern.

Required rules:

- every protected endpoint resolves authenticated provider identity first;
- local `Account` status must be active;
- selected `Membership` must belong to the authenticated account;
- selected Tenant must be active;
- required capability must be present in effective role/capability set;
- tenant/customer-scoped commands and queries must include tenant id from resolved auth context, not from trusted frontend state alone;
- cross-tenant ids supplied by the client must be rejected unless explicitly authorized;
- denials must be typed enough for UI behavior and audited enough for security review.

## 11. Audit and observability requirements

Module 1 audit is intentionally small but must prove the pattern for later expansion.

Required audit fields:

- audit event id;
- event type;
- timestamp;
- actor account id when known;
- provider subject or anonymized subject when account is not linked;
- tenant id/membership id when applicable;
- request/correlation id;
- route/action name;
- authorization decision: allowed/denied;
- denial reason when applicable;
- safe metadata only, with no raw tokens or provider secrets.

Required observability:

- structured logs for auth resolution failures, authorization denials, and context switches;
- request/correlation id propagated through backend handlers and audit events;
- frontend error states that can be matched to backend error categories during tests.

## 12. Security and privacy requirements

- Provider client ids and public config may be exposed only if intended by the provider; secrets must never be included in frontend bundles.
- Raw provider tokens, session cookies, refresh tokens, and internal role evaluation details must not appear in `/api/me` or frontend logs.
- `/api/me` must not return other users or other tenants.
- Context selection must not allow arbitrary tenant id probing.
- Disabled accounts and revoked/disabled memberships must be denied even if provider authentication is valid.
- Backend authorization checks must be tested independently of frontend route guards.
- Error messages must be user-understandable without exposing sensitive internals.

## 13. Acceptance scenarios

### Scenario 1: Unauthenticated visitor sees sign-in

Given no authenticated browser session exists, when the visitor opens the app, then the UI shows the public signed-out state and a sign-in action, and protected API calls do not return user or tenant data.

### Scenario 2: Seed member signs in and enters app

Given a seeded active account, tenant, membership, and app access capability exist, when the user signs in, then `/api/me` returns active account and selected context, and the user lands on the authenticated app page with profile and tenant context visible.

### Scenario 3: `/api/me` is browser-safe

Given an active authenticated user, when the frontend calls `/api/me`, then the response includes account/profile/context/capability data and excludes provider secrets, raw tokens, backend secrets, and unrelated tenants.

### Scenario 4: Multi-tenant member switches context

Given an account has two active tenant memberships, when the user selects the second tenant, then the backend verifies the membership, stores the selected context, returns refreshed `/api/me`, and subsequent protected calls are scoped to the selected tenant.

### Scenario 5: Cross-tenant context selection is denied

Given an authenticated user attempts to select a tenant or membership that does not belong to them, when they submit the context change, then the backend denies the request, no selected context changes, no foreign tenant data is returned, and an audit event is emitted.

### Scenario 6: Disabled account cannot enter app

Given the provider session is valid but the local account is disabled, when `/api/me` resolves, then the UI shows a disabled-account state, protected app data is not returned, and an audit event is emitted.

### Scenario 7: Authenticated user with no membership cannot enter app

Given a provider-authenticated subject has no active local membership, when `/api/me` resolves, then the UI shows no-access state, tenant data is not returned, and an audit event is emitted.

### Scenario 8: Missing capability returns forbidden

Given an active account and membership lack a required capability for the protected sample endpoint, when the endpoint is called, then the response is forbidden, the UI shows a forbidden state, and an audit event is emitted.

### Scenario 9: Sign-out clears access

Given an authenticated member is inside the app, when they sign out, then they return to the signed-out state and subsequent protected API calls are unauthenticated.

### Scenario 10: Frontend secret boundary holds

Given the production-like frontend bundle is built, when static assets are inspected by tests, then provider/backend secrets, raw tokens, and private API keys are absent.

## 14. Test requirements

Minimum test coverage:

- `/api/me` unauthenticated behavior.
- `/api/me` active account payload shape and browser-safe redaction.
- `/api/me` disabled account behavior.
- `/api/me` no active membership behavior.
- Context selection success for an owned active membership.
- Context selection denial for cross-tenant membership.
- Protected endpoint allowed with `app.access`.
- Protected endpoint forbidden without required capability.
- Tenant isolation for all Module 1 view/query paths.
- Audit event emitted for denial and context-switch cases.
- Frontend app shell renders loading, active, forbidden, disabled, no-access, and error states.
- Frontend route guards do not replace backend authorization tests.
- Frontend bundle/static asset test verifies no backend/provider secrets are exposed.

## 15. Akka decomposition notes

This section is input for later `akka-prd-to-specs-backlog` and implementation planning. It is not the final design.

Likely Akka components:

- Key Value Entity for `Account` current state.
- Key Value Entity for `Tenant` current state.
- Key Value Entity for `Membership` current state.
- Key Value Entity or static seed adapter for minimal `Role`/`Capability` definitions.
- Key Value Entity for `UserSettings` if selected context should persist.
- Event Sourced Entity or append-oriented audit component for `AdminAuditEvent` records.
- Views for lookup by provider subject, memberships by account, memberships by tenant, and audit diagnostics.
- HTTP endpoints for auth session integration, `/api/me`, context selection, protected sample summary, and optional audit diagnostics.
- React/Vite/TypeScript UI for sign-in, app shell, profile/context, forbidden/no-access states, and protected sample page.

Implementation guidance:

- Build the auth-resolution and authorization helper early and route all protected endpoints through it.
- Keep provider authentication and local authorization separate.
- Keep frontend auth state derived from `/api/me`; do not duplicate authorization logic in local storage.
- Use tenant ids from resolved `AuthContext` for scoped backend reads.
- Use test fakes for provider identity rather than bypassing authorization helper code paths.

## 16. Demo flow

A successful Module 1 demo should run as follows:

1. Start the backend and frontend with seeded local data.
2. Open the app as unauthenticated visitor and see sign-in prompt.
3. Sign in as seed operator.
4. Land on `/app` with account and tenant context visible.
5. Open profile/context page and inspect `/api/me`-derived data.
6. If demo data includes two tenants, switch context and observe updated selected tenant.
7. Attempt a forbidden protected route with a limited test user and see forbidden state.
8. Attempt a no-access/disabled test user and see correct state.
9. Run security tests showing tenant isolation, denied access, audit event emission, and frontend secret boundary.

## 17. Explicit defers to Module 2 and later

Deferred to Module 2 User Administration:

- invitation creation, resend, revoke/cancel, expiry, acceptance, delivery status, and reminder flows;
- user directory;
- role/capability editing;
- membership lifecycle management;
- disabled user administration UI;
- access review basics;
- support-access administration;
- admin audit UI beyond minimal diagnostics.

Deferred to later modules:

- agent definition records and tool permission boundaries;
- runtime prompt governance;
- skill governance and `readSkill(skillId)`;
- durable work traces and timeline UI;
- evaluator agents and closed-loop improvement.

## 18. Readiness checklist

Module 1 is ready for decomposition when the following are true:

- [ ] Authentication provider seam and local/test behavior are named.
- [ ] First-access bootstrap rule is explicit and does not allow privileged self-registration.
- [ ] Account, Tenant, Membership, minimal Role/Capability, UserSettings, AuthContext, and AdminAuditEvent semantics are accepted.
- [ ] `/api/me` response states and redaction expectations are accepted.
- [ ] Context selection behavior is accepted.
- [ ] UI route inventory and app-shell states are accepted.
- [ ] Security/audit/test requirements are accepted.
- [ ] Deferred features are confirmed as not part of Module 1.
