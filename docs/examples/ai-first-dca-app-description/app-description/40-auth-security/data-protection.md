# Data Protection

## Sensitive data categories

The DCA seed app should treat these categories as sensitive:

- WorkOS subject identifiers and identity claims;
- local account status, role assignments, and scopes;
- tenant/customer identifiers and customer contact data;
- device telemetry that can reveal customer operations;
- policy decisions, approval rationale, and exception notes;
- agent tool inputs/outputs when they include customer, device, contract, billing, or service data;
- backend secrets such as WorkOS API keys, email provider keys, and bootstrap configuration.

## Frontend/backend secret rules

- Only `VITE_` variables may be exposed to frontend code.
- `WORKOS_API_KEY`, email API keys, bootstrap admin configuration, service credentials, and signing secrets are backend-only.
- Do not copy `frontend/.env.local` into static resources or repository examples.
- Built frontend assets must not contain backend secrets.

## Response and log minimization

- `/api/me` returns browser-safe profile, status, roles, scopes, and UI capability hints only.
- Admin list APIs should return only fields needed for the administration screen.
- Authorization denials should not leak unnecessary cross-tenant/customer data.
- Logs should include correlation ids and outcome categories, not raw tokens, secrets, or full sensitive payloads.
- Audit traces may include actor, effective user, target ids, operation, scope, and reason, but should avoid storing full tokens or secret values.

## Retention and trace visibility

- Admin audit entries, decision traces, policy invocations, and high-risk data-access events should have retention rules before production use.
- `AUDITOR` access is read-only and scope-limited.
- Routine activity may be summarized in UI, but underlying audit/work/decision facts must remain inspectable by allowed roles.

## Deny-by-default cases

- Unknown tenant/customer scope.
- Disabled local account.
- Uninvited identity when self-registration is disabled.
- Agent/tool access outside active workflow scope.
- Policy or permission mutation without explicit human authority.
