# Realization: API contracts for User Admin

Capability: `user-and-access-administration`.

## Browser/API evidence

| Tool / action | Exposure | API evidence | Contract obligations |
|---|---|---|---|
| `search-user-directory` | `browser-tool`, `agent-tool` | `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java`, `UserDirectoryView.java`; frontend `HttpWorkstreamApiClient.ts` | Scoped search/list results with tenant/customer filters and redaction. |
| `create-or-resend-invitation` | `browser-tool` | `AdminEndpoint.java`, foundation invitation/email services | Idempotent invitation lifecycle; Resend/captured outbox boundary; no email-only authorization. |
| `change-membership-role-or-status` | `browser-tool` with approval when risky | `AdminEndpoint.java`, `UserAdminService.java`, identity repositories | Backend authorization, risky-change policy/decision-card path, audit events, cross-scope denials. |
| `grant-or-revoke-support-access` | `browser-tool` with expiry/approval | `AdminEndpoint.java`, identity/support-access service evidence | Expiring scoped support access; traced grant/revoke and forbidden access behavior. |
| `run-access-review` | `agent-tool`, `internal-tool` | access review worker/service/autonomous agent classes | Produces recommendations and decision evidence; cannot autonomously expand authority. |
| Workstream messages/actions/events | `browser-tool` | `WorkstreamEndpoint.java`, `frontend/src/api/WorkstreamApiClient.ts` | Typed action and surface payloads with correlation ids and trace links. |

## Validation evidence

- `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/InvitationAndUserAdminServiceTest.java`
- `frontend/src/workstream-actions.contract.test.mjs`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`

## Gaps / caveats

- External Resend smoke proves provider integration only when configured; normal runtime must fail closed without secrets.
