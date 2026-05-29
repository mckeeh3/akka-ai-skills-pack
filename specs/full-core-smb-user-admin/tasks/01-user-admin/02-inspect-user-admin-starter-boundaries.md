# Task: Inspect User Admin starter boundaries and queue first source-edit slice

## Objective

Inspect the existing AI-first SaaS starter source boundaries for User Admin, then append one or more bounded source-edit implementation tasks for the directory and invitation dashboard foundation.

## Required reads

- `AGENTS.md`
- `specs/full-core-smb-user-admin/README.md`
- `specs/full-core-smb-user-admin/conversation-capture.md`
- `specs/full-core-smb-user-admin/sprints/01-user-admin-vertical-contract-sprint.md`
- `specs/full-core-smb-user-admin/backlog/01-user-admin-vertical-contract-backlog.md`
- `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`
- targeted source files discovered under `templates/ai-first-saas-starter/` for workstream shell, User Admin-like surfaces, auth/capability checks, invitation/member models, audit/trace, API endpoints, and frontend surface rendering

## In scope

- Discover current starter source paths and tests relevant to the first User Admin directory/invitation dashboard foundation.
- Record source-boundary findings in a concise planning note or directly in appended task briefs.
- Append bounded implementation task(s) to `specs/full-core-smb-user-admin/pending-tasks.md` for the first source-edit slice.
- Create task brief(s) for the appended implementation task(s) with exact required reads, source areas, checks, and done criteria.

## Out of scope

- Implementing starter source changes in this task.
- Expanding scope to member disable/reactivate, role changes, UserAdminAgent normal runtime, or access-review worker implementation unless only referenced as later tasks.

## Expected outputs

- updated `specs/full-core-smb-user-admin/pending-tasks.md`
- one or more new task briefs under `specs/full-core-smb-user-admin/tasks/01-user-admin/`
- optional concise source-boundary note under `specs/full-core-smb-user-admin/`

## Checks

- `git diff --check`
- targeted `rg`/`find` commands proving the discovered starter source/test paths for User Admin, workstream shell, surfaces, auth/capabilities, invitations/members, audit/trace, API, and frontend rendering

## Done criteria

- The next source-edit implementation task can run in a fresh harness session without guessing source paths or validation commands.
- Appended tasks preserve deterministic-service, surface, capability, trace, provider fail-closed, and runtime validation boundaries from `user-admin-vertical-contracts.md`.
- Changes and queue update are committed.
