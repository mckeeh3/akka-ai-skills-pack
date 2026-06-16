# Realization: Akka components for User Admin

Capability: `user-and-access-administration`.

This map is docs-only. It points to current implementation evidence and does not change runtime behavior.

## Component and service evidence

| Intent binding | Akka / Java evidence | Notes |
|---|---|---|
| Dashboard, directory, invitation, account-detail, role/support/review surface shaping | `src/main/java/ai/first/application/coreapp/useradmin/UserAdminService.java`, `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java` | Protected admin edge must enforce selected `AuthContext`, tenant/customer scope, account status, roles/capabilities, last-admin rules, support-access visibility, and denial audit. |
| Customer boundary lifecycle and Customer Admin branch | `src/main/java/ai/first/domain/foundation/identity/Customer.java`, `src/main/java/ai/first/application/coreapp/useradmin/TenantCustomerAdminService.java`, identity repository customer methods under `src/main/java/ai/first/application/foundation/identity/**`, `src/main/java/ai/first/api/coreapp/admin/AdminEndpoint.java` | Realizes `tenant.customer.*` and `tenant.customer_admin.*` through backend-authorized Customer directory/detail/create/rename/suspend/reactivate and Customer Admin invitation/membership operations. The implementation boundary is foundation Customer state for authorization/audit, not CRM/customer-success/sales/billing/support business state. |
| Customer workstream action routing | `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java` | Maps `action-customer-list`, `action-customer-read`, `action-customer-create`, `action-customer-rename`, `action-customer-suspend`, `action-customer-reactivate`, `action-customer-admin-list`, `action-customer-admin-invite`, and `action-customer-admin-manage` to Customer/Customer Admin surfaces with selected Tenant/Customer scope, idempotency, safe denials, no-op/conflict handling, redaction, and audit/work traces. |
| Durable identity and invitation state | `src/main/java/ai/first/application/foundation/identity/**`, `src/main/java/ai/first/application/foundation/invitation/**`, `src/main/java/ai/first/domain/foundation/identity/**`, `src/main/java/ai/first/domain/foundation/invitation/**` | Source-of-authority state for users, memberships, Customers, Customer Admin scope, invitation lifecycle, identity linkage, and scoped resource ownership. |
| User/admin read models | `src/main/java/ai/first/application/coreapp/useradmin/UserDirectoryView.java`, foundation `InvitationView` evidence under `application/foundation/invitation/**` | Views are read/evidence capabilities and must remain scoped by backend authorization before filtering/pagination. |
| Email delivery boundary | `src/main/java/ai/first/application/foundation/email/**`, `src/test/java/ai/first/application/coreapp/useradmin/RealResendProviderSmokeTest.java` | Resend/captured-outbox behavior must preserve secret boundaries and provider fail-closed behavior; no raw tokens in browser payloads. |
| User Admin governed guidance | `UserAdminEvidenceTools.java` and any UserAdminAgent runtime evidence | Agent may summarize/draft/recommend through governed runtime; missing provider/model config fails closed. |
| Access review recommendations | `UserAdminAccessReviewService.java`, `UserAdminAccessReviewWorker.java`, `UserAdminAccessReviewAutonomousAgent.java`, `DurableAccessReviewTaskRepositoryEntity.java` | Worker may recommend/draft; risky access changes remain human/backend-policy governed through deterministic capabilities. |
| Evidence tools and traces | `UserAdminEvidenceTools.java`, `src/main/java/ai/first/application/foundation/audit/AdminAuditView.java`, `AuditTraceService.java` | Admin actions, denials, prompt assembly, loader calls, approval gates, and data access require audit/work traces. |

## Surface result mapping

Implementation should map backend outcomes to `surface-user-admin-dashboard`, Customer branch surfaces (`surface-user-admin-customer-directory`, `surface-user-admin-customer-detail`, `surface-user-admin-customer-create`, `surface-user-admin-customer-rename`, `surface-user-admin-customer-suspend-confirmation`, `surface-user-admin-customer-reactivate-confirmation`, `surface-user-admin-customer-admins`, `surface-user-admin-customer-admin-invitation-create`, `surface-user-admin-customer-admin-detail`), `surface-user-admin-users`, `surface-user-admin-invitation-detail`, `surface-user-admin-user-detail`, `surface-user-admin-role-change-preview`, `surface-user-admin-access-review-task`, reusable decision/audit/workflow/markdown surfaces, or `surface-user-admin-system-message`. Customer branch outcomes must preserve Organization/Tenant scope for Customer lifecycle and selected Customer scope for Customer Admin operations.

## Validation evidence

- `src/test/java/ai/first/application/coreapp/useradmin/InvitationAndUserAdminServiceTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/AdminEndpointIntegrationTest.java`
- `src/test/java/ai/first/application/coreapp/workstream/WorkstreamServiceTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewServiceTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/UserAdminAccessReviewAutonomousAgentTest.java`
- `src/test/java/ai/first/application/coreapp/useradmin/DurableAccessReviewTaskRepositoryEntityTest.java`
- `frontend/src/workstream-user-admin-vertical.contract.test.mjs`
- `frontend/src/workstream-user-admin-expertise.contract.test.mjs`

## Gaps / caveats

- Some endpoint tests use seeded-data assumptions; current intent treats the service/workstream/action path as the primary product contract.
- Billing and timer-backed invitation reminders are deferred, not completed User Admin behavior.
- Access-review worker output is advisory and cannot directly mutate access.
