# Organization Admin UI Runtime Smoke Path

Date: 2026-06-11

Purpose: prove the browser Organization Admin surface uses the protected Admin API path rather than fixture-only workstream action results.

## Automated evidence in this task

- Frontend contract tests assert `Organization Admin` runtime actions are intercepted in `frontend/src/main.tsx` and routed to typed `apiClient.admin.*Organization` methods.
- `HttpApiClient` sends WorkOS bearer tokens plus `X-Selected-Context-Id` and correlation headers to protected Admin API routes.
- The production Organization Admin action handler maps list/read/create/rename/suspend/reactivate API success, no-op, denial, validation, hidden/not-found, conflict/stale, and failure results back into the structured surface.

## Manual local Akka-hosted smoke checklist

1. Run the backend and frontend build normally so Akka serves the packaged browser app.
2. Sign in as a SaaS Owner Admin with a selected SaaS Owner context.
3. Open User Admin → Organization Admin.
4. In browser dev tools, perform:
   - refresh/search Organizations;
   - open an Organization detail;
   - create an Organization with an audit reason;
   - rename it;
   - suspend it with a reason;
   - reactivate it with a reason.
5. Confirm each request goes to `/api/admin/organizations...` with `Authorization`, `X-Selected-Context-Id`, and `X-Correlation-Id` headers.
6. Confirm the surface shows the returned trace/correlation refs and boundary copy, and does not expose tenant app data, provider secrets, support-access internals, or billing-derived authority.
7. Repeat from a Tenant Admin or Customer Admin selected context and confirm a safe forbidden/unavailable state is rendered without hidden Organization counts.
