# User Admin Workstream Migration Plan

## Scope

Refresh `app-description/domains/core-starter/workstreams/user-admin/**` to the current skills-pack app-description graph contract.

## Primary intent

Organization and SaaS admins manage invitations, users, memberships, roles/capabilities, access state, support access, access review, and admin audit evidence through governed User Admin surfaces and agents.

## Required graph coverage

- Workstream purpose and lifecycle/alignment state.
- Human admin workers, User Admin functional agent, access-review agent/system workers, invitation-onboarding worker, and admin-audit projection worker.
- Surfaces for dashboard, user directory/list, user account detail, invite form/result, membership/role actions, access-review decision cards, support access, and admin audit evidence.
- Governed tools for invite, resend/cancel/accept linkage, user search/read, membership changes, role/capability changes, access state changes, support access, risky-action review, and audit reads.
- Actor adapters: direct surface actions, confirmed human chat plans, AI agent tool calls where bounded, workflow/timer/consumer/system calls, API calls.
- Capability links to user/access administration and auth-context/membership state.
- Confirmation, approval, idempotency, last-admin guard, disabled-user, tenant isolation, and denial semantics.
- Audit/work traces including `requestedBy`, `confirmedBy`, invitation lifecycle, role changes, denial, and risk decision evidence.
- Tests and runtime-validation scenarios for invitation lifecycle, user list/detail, role change denial/success, last-admin protection, and audit trace evidence.
- Realization files and source-alignment entries.

## Specific refresh questions for the task

- Which operations require decision-card approval vs immediate admin execution?
- Which User Admin actions may be proposed by chat but require explicit confirmation?
- Which agent tool calls are read-only vs side-effecting?

## Expected task output

The task should update only User Admin workstream files plus narrow shared references if required, then mark lifecycle/source-alignment to reflect description changes and implementation alignment.
