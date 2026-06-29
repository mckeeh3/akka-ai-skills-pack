---
id: RV-USER-ADMIN-001
title: Organization admin invites a user and non-admin is denied
workstream: user-admin
surface: user-admin-invite-user
persona: organization-admin
environment: local-dev
dataSetup:
  - base-organization
authMode: workos-test-users
executionMode: human-manual
executionStatus: authored-not-run
readinessClaim: not-run
---

# Purpose

Validate the User Admin invitation path through the real organization-admin surface, protected API/action adapter, invitation capability, email/provider boundary, result surface, and audit/work trace evidence. Also validate that a member persona cannot perform the same operation.

# Prerequisites

- Start the app using `environments/local-dev.md`.
- Prepare `data-setups/base-organization.md` without pre-creating the invitation being validated.
- Log in as `personas/organization-admin.md`.
- Know the invitee email for the run and whether live email delivery is configured or expected to fail closed/outbox-only.

# Runtime path

`organization admin -> User Admin invite surface -> invite user surface_action or protected admin API -> invitation/user-and-access-administration governed capability -> invitation/account/membership services and Akka-backed state -> invitation result surface, audit event, work trace, and provider/outbox evidence`

# Surface, adapter, and governed-tool contract

- Surface graph node: User Admin dashboard/invite-user.
- Action edge: create invitation and show invitation result.
- Actor adapter/source: browser `surface_action` or protected API call; any human chat plan must bind the invitee email and require confirmation before sending.
- Governed tool scope: invitation and membership administration for the base organization only.
- Transaction/idempotency: repeated invite for the same email/scope should return an idempotent or already-invited result rather than duplicate side effects.

# Setup

The base setup creates the organization and admin membership only. The scenario action creates the invitation.

# Human UI validation script

1. Open the local frontend URL and log in as `org.admin@example.com`.
2. Navigate to the User Admin surface and open the invite-user action.
3. Enter the run-specific invitee email and role/scope allowed by the base organization.
4. Submit or confirm the invitation.
5. Record the result surface, invitation id/status, and any email/outbox/provider message.
6. Submit the same invitation again and record the idempotency result.
7. Log out or open a separate session as `member@example.com`.
8. Attempt the same invite action and record the forbidden/hidden behavior.

# Expected results

- The organization admin can create or reuse one invitation for the base organization.
- The result surface shows safe invitation status and next steps without exposing provider secrets.
- Live email delivery, if configured, is recorded; if not configured, the provider path fails closed or records an outbox/pending-delivery state with an actionable message.
- Duplicate invitation submission is idempotent and does not create duplicate active invitations.
- The member persona is denied or cannot see the invite action.
- Audit/work traces include requestedBy/confirmedBy, organization scope, invitation id/status, provider state, and denial evidence.

# Evidence to capture

- Persona, organization id, invitee email, invitation id/status.
- Screenshots or DOM observations of invite form and result surface.
- Network/API statuses for create, duplicate, and denied invite attempts.
- Email provider/outbox state or fail-closed message.
- Audit/work trace ids for successful, duplicate, and denied actions.

# Failure classification hints

- `auth/setup gap` for missing organization-admin/member identity mapping.
- `provider/config blocker` for live email delivery when provider credentials are required but absent.
- `implementation gap` for missing backend authorization, duplicate side effects, or no invitation result.
- `UX/state gap` for ambiguous invite status or missing recovery instructions.
- `test gap` for missing audit/work trace visibility.
