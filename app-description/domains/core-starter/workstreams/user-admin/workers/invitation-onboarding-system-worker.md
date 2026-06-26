# Worker: Invitation Onboarding System Worker

workerId: `user-admin.invitation-onboarding-system-worker`
workerType: `system`
reasoningEngine: `deterministic`
scope: `local-workstream`
owningDomain: `core-starter`
owningWorkstream: `user-admin`
runtimeReadiness: `description-ready`

## Purpose

Deterministic worker for invitation creation side effects, outbox/Resend delivery attempts, invitee acceptance/onboarding, expiry/revoke/no-op handling, and selected-context refresh guidance.

## Responsibility

Owns/does:

- Enqueue and track invitation email delivery through the supported outbox/Resend path when configured.
- Validate signed invitation tokens, WorkOS/AuthKit-authenticated invitee identity, email match, target scope, requested role, invitation lifecycle state, expiry, revocation, duplicate membership, and account eligibility.
- Create/link account/profile/membership state for accepted invitations, mark accepted evidence immutably, and expose safe onboarding result/system-message states.
- Handle idempotent replay, provider/outbox fail-closed states, delivery failure/bounce status, expiry, resend, and revoke relationships.

Does not own/do:

- Public self-registration, prompt/browser bootstrap, raw token exposure, fake sent success, role changes outside the invitation's backend-validated role, or authority beyond the invitation target scope.

## Behavior profile

- Instructions: deterministic invitation lifecycle and onboarding policy in `../../../capabilities/user-and-access-administration.md` and User Admin behavior/tests.
- Tools: `accept-invitation`, `create-or-resend-invitation`, email outbox delivery, account/membership component calls, admin audit emission.
- Evidence profile: redacted invitation status, delivery status, accepted evidence, and recovery reason; raw invitation tokens/token hashes, JWT/session values, provider secrets, and full email bodies forbidden.

## Authority and scope

- authorityLevel: `execute` for deterministic onboarding commands using stored invitation authority plus authenticated invitee identity.
- AuthContext scope: invitation target SaaS Owner/Tenant/Customer scope; not caller-supplied browser scope.
- Requires backend lifecycle validation for every transition; expired/revoked/accepted/stale/mismatched/hidden tokens return safe recovery/no-op/denial.

## Harnesses and actor adapters

| Harness | Actor adapter | Exposure channel | Trace source | Notes |
|---|---|---|---|---|
| Invitation admin surfaces | `surface_action` | browser | `surface_action` | Create/resend/revoke requests produce deterministic side effects through backend. |
| Invitee onboarding endpoint | `api_call` | browser API | `api_call` | Signed token + WorkOS/AuthKit identity validation. |
| Email outbox/Resend consumer | `consumer_reaction` | backend/provider | `consumer_reaction` | Delivery attempts/status callbacks are provider-safe and idempotent. |
| Expiry/reminder timers when enabled | `timer_invocation` | backend | `timer_invocation` | No-op for stale/revoked/accepted invitations. |

## Governed tools and capabilities

| Governed tool id | Capability id | Allowed adapter(s) | Authority | Approval/confirmation |
|---|---|---|---|---|
| `create-or-resend-invitation` | `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation` | `surface_action`, `api_call`, `consumer_reaction` | execute | Human confirmation for admin create/resend/revoke; provider fail-closed. |
| `accept-invitation` | `user_admin.accept_invitation` | `api_call`, `internal_call` | execute | Invitee token and authenticated identity required; no agent exposure. |

## Audit and work traces

Record invitation command, delivery enqueue/attempt/status, accept/reject/revoke/expire/no-op, account/profile/membership link/create results, selected-context refresh outcome, denial/recovery reason, worker id, adapter, governed tool/capability, correlation/idempotency, and redaction state.

## Tests and manual runtime scenarios

- Automated tests: valid acceptance for all invitation scopes, repeated acceptance idempotency, email mismatch, expired/revoked/stale/malformed token denial, duplicate/open invite handling, provider/outbox fail-closed, raw token/secret redaction, and trace evidence.
- Manual runtime scenario: admin sends invitation -> outbox records provider-safe delivery -> invitee signs in via WorkOS/AuthKit -> acceptance links membership -> `/api/me` shows new eligible selected context.

## Realization links

- Surfaces: `../surfaces/surfaces.md`
- Tools: `../tools/governed-tools.md`
- Capability: `../../../capabilities/user-and-access-administration.md`
- Tests: `../tests/coverage.md`
- Traces: `../traces/work-traces.md`
