# Realization: Akka components for User Admin

Capability: `user-and-access-administration`.

This map is docs-only. It points to current implementation evidence and does not change runtime behavior.

## Component and service evidence

| Intent binding | Akka / Java evidence | Notes |
|---|---|---|
| Dashboard, directory, invitation, account-detail, role/support/review surface shaping | `src/main/java/ai/first/application/coreapp/useradmin/UserAdminService.java`, `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java` | Protected admin edge must enforce selected `AuthContext`, tenant/customer scope, account status, roles/capabilities, last-admin rules, support-access visibility, and denial audit. |
| Durable identity and invitation state | `src/main/java/ai/first/application/foundation/identity/**`, `src/main/java/ai/first/application/foundation/invitation/**`, `src/main/java/ai/first/domain/foundation/identity/**`, `src/main/java/ai/first/domain/foundation/invitation/**` | Source-of-authority state for users, memberships, invitation lifecycle, identity linkage, and scoped resource ownership. |
| User/admin read models | `src/main/java/ai/first/application/coreapp/useradmin/UserDirectoryView.java`, foundation `InvitationView` evidence under `application/foundation/invitation/**` | Views are read/evidence capabilities and must remain scoped by backend authorization before filtering/pagination. |
| Email delivery boundary | `src/main/java/ai/first/application/foundation/email/**`, `src/test/java/ai/first/application/coreapp/useradmin/RealResendProviderSmokeTest.java` | Resend/captured-outbox behavior must preserve secret boundaries and provider fail-closed behavior; no raw tokens in browser payloads. |
| User Admin governed guidance | `UserAdminEvidenceTools.java` and any UserAdminAgent runtime evidence | Agent may summarize/draft/recommend through governed runtime; missing provider/model config fails closed. |
| Access review recommendations | `UserAdminAccessReviewService.java`, `UserAdminAccessReviewWorker.java`, `UserAdminAccessReviewAutonomousAgent.java`, `DurableAccessReviewTaskRepositoryEntity.java` | Worker may recommend/draft; risky access changes remain human/backend-policy governed through deterministic capabilities. |
| Evidence tools and traces | `UserAdminEvidenceTools.java`, `src/main/java/ai/first/application/foundation/audit/AdminAuditView.java`, `AuditTraceService.java` | Admin actions, denials, prompt assembly, loader calls, approval gates, and data access require audit/work traces. |

## Surface result mapping

Implementation should map backend outcomes to `surface-user-admin-dashboard`, `surface-user-admin-users`, `surface-user-admin-invitation-detail`, `surface-user-admin-user-detail`, `surface-user-admin-role-change-preview`, `surface-user-admin-access-review-task`, reusable decision/audit/workflow/markdown surfaces, or `surface-user-admin-system-message`.

## Validation evidence

- `src/test/java/ai/first/application/coreapp/useradmin/InvitationAndUserAdminServiceTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewServiceTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewAutonomousAgentTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/DurableAccessReviewTaskRepositoryEntityTest.java`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-user-admin-expertise.contract.test.mjs`

## Gaps / caveats

- Some endpoint tests use seeded-data assumptions; current intent treats the service/workstream/action path as the primary product contract.
- Billing and timer-backed invitation reminders are deferred, not completed User Admin behavior.
- Access-review worker output is advisory and cannot directly mutate access.
