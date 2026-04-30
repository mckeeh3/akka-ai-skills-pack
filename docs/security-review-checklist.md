# Security review checklist

Use before completing security-sensitive Akka changes.

## Route exposure

- [ ] Public static frontend routes are separated from protected `/api/...` routes.
- [ ] Protected browser APIs have `@JWT`.
- [ ] Internal-only routes use ACL guidance and are not accidentally internet-facing.
- [ ] No broad wildcard route creates unintended exposure or conflicts.

## JWT and identity

- [ ] Frontend sends `Authorization: Bearer <token>` for protected APIs.
- [ ] Endpoint reads claims only after `@JWT` validation.
- [ ] Issuer/audience/static claims are configured when the contract is known.
- [ ] Production does not rely on local unsigned JWT behavior.
- [ ] Integration tests inject representative bearer tokens.

## WorkOS/frontend secrets

- [ ] WorkOS client id and redirect URI are treated as public.
- [ ] `WORKOS_API_KEY` and email/API secrets are backend-only.
- [ ] No backend secrets appear in `frontend/.env*` or built assets.
- [ ] WorkOS dashboard redirect/origin settings match local/deployed modes.

## Authorization

- [ ] Authentication and authorization are separate in code and docs.
- [ ] Backend loads local user/account/role state before protected operations.
- [ ] Frontend navigation is not treated as authorization.
- [ ] Disabled users are rejected despite valid JWTs.
- [ ] Tenant/customer/self scope is checked on reads and writes.
- [ ] Admin APIs prevent privilege escalation.

## Administration

- [ ] `/api/me` returns a browser-facing DTO, not internal entity state.
- [ ] Invite and first-login linking are idempotent.
- [ ] Startup admin bootstrap is idempotent and validates malformed config.
- [ ] Admin actions are audited when audit is in scope.
- [ ] Tests cover allowed and forbidden admin operations.
