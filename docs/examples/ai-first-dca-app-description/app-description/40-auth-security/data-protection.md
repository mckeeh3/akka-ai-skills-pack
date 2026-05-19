# Data Protection

## Purpose

Define data-protection, redaction, secret-boundary, retention, and deny-by-default expectations for the DCA vertical reference. These rules apply to the secure foundation capability and to every DCA-specific capability, API, view, agent tool, workflow, timer, consumer, and UI surface.

## Sensitive data categories

Treat these categories as sensitive:

- WorkOS subject identifiers, identity claims, JWTs, and provider metadata;
- local `Account` status, invitation state, membership records, role assignments, permission/capability grants, support-access grants, and selected `AuthContext` details;
- Tenant and Customer identifiers, customer contact data, and organization relationships;
- device telemetry that can reveal customer operations, usage volume, uptime, location, supply levels, or service needs;
- contract, billing, supplier, shipping, service, and meter data;
- policy decisions, approval rationale, exception notes, risk/confidence/impact assessments, and decision-card evidence;
- agent tool inputs/outputs when they include customer, device, contract, billing, service, policy, or trace data;
- backend secrets such as `WORKOS_API_KEY`, email provider keys, bootstrap admin configuration, service credentials, signing keys, and integration credentials;
- raw invitation tokens, support-access tokens, reset/relink artifacts, or other bearer secrets.

## Frontend/backend secret rules

- Only public frontend configuration intended for browser use may use `VITE_` variables.
- Backend secrets, WorkOS private keys, email API keys, bootstrap admin configuration, service credentials, signing secrets, and integration credentials are backend-only.
- Do not copy `frontend/.env.local`, provider secrets, captured outbox payloads containing tokens, or local service credentials into static resources or repository examples.
- Built frontend assets must not contain backend-secret names, sample secret values, JWTs, raw invitation tokens, or private provider identifiers.
- Frontend examples may include public WorkOS client id and redirect URI placeholders only.
- Security acceptance tests should scan frontend env examples and generated assets for forbidden backend-secret names and representative secret values.

## Response and log minimization

- `/api/me` returns browser-safe account, profile, settings, membership summaries, selected context, role/capability hints, and no secrets.
- Admin list/search APIs return only fields needed by the administration surface, filtered and paginated server-side by tenant/customer scope.
- Agent tools receive the least evidence required for the task, with redacted or summarized sensitive fields where possible.
- Authorization denials must not leak unnecessary cross-tenant/customer data or confirm unrelated resource existence.
- Logs should include correlation ids, operation ids, outcome categories, and safe identifiers; they must not include raw tokens, provider secrets, full customer/device payloads, or unredacted sensitive tool I/O.
- Audit/work traces may include actor id, effective principal, target ids, operation, selected scope, permission checked, policy citation, reason, and redaction marker, but should avoid full payload capture unless a specific audit requirement justifies it.

## Retention and trace visibility

- Admin audit entries, decision traces, policy invocations, support-access use, invitation lifecycle, billing-boundary changes, high-risk data access, and consequential AI/tool activity require retention rules before production realization.
- `AUDITOR` access is read-only and scope-limited.
- SaaS Owner audit visibility excludes Tenant application data unless support-access or an accepted billing/platform policy explicitly permits a redacted view.
- Routine activity may be summarized in dashboards or digests, but source audit/work/decision facts must remain inspectable by allowed roles.
- Redaction policy must be applied before data reaches browser responses, view rows exposed to users, agent tool outputs, audit summaries, exports, or logs.

## Deny-by-default cases

Deny by default when any of these are missing or invalid:

- authenticated account;
- active local account status;
- valid invitation/acceptance context for first login;
- selected `AuthContext`;
- active membership;
- tenant/customer scope;
- named permission/capability;
- support-access grant for support operators;
- human approval for policy/permission/authority changes;
- data-access authorization for agent tools;
- idempotency key for retry-sensitive commands where required.

Also deny disabled local accounts, inactive memberships, expired support-access grants, unknown tenant/customer scope, cross-tenant/customer reads or writes, uninvited identities when self-registration is disabled, agent/tool access outside active workflow scope, and policy or permission mutation without explicit human authority.

## Linked test expectations

The tests layer must cover `/api/me` redaction, frontend secret-boundary scans, scoped admin list/search, cross-scope denial without leakage, disabled-user denial, denied agent tool access, audit emission for sensitive reads and denials, support-access expiry/revocation, and retention/redaction expectations for trace search/export surfaces.
