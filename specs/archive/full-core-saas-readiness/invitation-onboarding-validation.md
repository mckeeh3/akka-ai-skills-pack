# Invitation Onboarding and Email Outbox Validation

## Scope

Validation artifact for `TASK-FCSR-03-001`, covering invitation onboarding, Resend production boundary, local/test captured outbox, idempotency, audit/lifecycle history, tenant scope, and browser-safe acceptance behavior at the selected local full-core scope.

## Evidence

- Backend invitation lifecycle is implemented through `InvitationService` and the durable `AkkaInvitationRepository` adapter backed by `DurableInvitationRepositoryEntity` plus append-only `InvitationLifecycleHistoryEntity` facts.
- Workstream User Admin actions call the backend service path for create, resend, and revoke through `WorkstreamService` and `WorkstreamEndpoint`; browser acceptance uses `/api/workstream/invitations/accept` and `acceptForBrowser`.
- Local/dev/test delivery uses captured outbox behavior from `ResendEmailService.DeliveryMode.LOCAL_OR_TEST`; automated tests do not send external email.
- Production delivery is Resend-only and fails closed with `resend-config-missing` when backend-only `RESEND_API_KEY` and `INVITE_EMAIL_FROM`/`RESEND_FROM_EMAIL` are absent.
- Raw invitation tokens are confined to outbox delivery messages and acceptance input; invitation state, lifecycle facts, admin rows, and acceptance results expose token hashes or raw tokens nowhere.
- Delivery-failed invitations now require an authorized admin resend before browser or direct acceptance can activate membership.
- Resend requests are idempotent by invitation id plus client idempotency key and do not queue duplicate outbox messages on replay.

## Focused tests

- `InvitationAndUserAdminServiceTest#inviteLifecycleQueuesCapturedOutboxRecordsDeliveryAndAcceptsIdempotently`
- `InvitationAndUserAdminServiceTest#browserAcceptanceReturnsSafeRecoveryStatesAndAcceptsRawToken`
- `InvitationAndUserAdminServiceTest#duplicateCreateResendRevokeAndExpiryAreIdempotentAndAudited`
- `InvitationAndUserAdminServiceTest#deliveryFailedInvitationRequiresAdminResendBeforeAcceptance`
- `InvitationAndUserAdminServiceTest#crossTenantAndRoleEscalationInviteAttemptsAreDenied`
- `InvitationAndUserAdminServiceTest#resendProductionReadinessFailsWithoutResendConfigurationAndLocalCaptureIsSafe`
- `InvitationAndUserAdminServiceTest#resendProductionAdapterBuildsAuthorizedRequestAndRecordsSentStatus`
- `InvitationAndUserAdminServiceTest#resendProductionAdapterMapsFailuresToSafeDeliveryStatus`
- `DurableInvitationRepositoryEntityTest`
- `InvitationLifecycleHistoryEntityTest`

## Remaining blockers / deferred scope

- Live Resend delivery smoke remains blocked until real backend-only Resend configuration and sender/domain setup are provided.
- Timer-backed reminder scheduling was not added in this task; expiry behavior is implemented as an idempotent backend command and remains safe for a later timer integration if selected.
