# Task: Define User Admin vertical slice contracts and implementation map

## Objective

Create an implementation-ready contract document for User Admin full-core SMB slices, starting with the highest-leverage vertical path.

## Required reads

- `AGENTS.md`
- `specs/full-core-smb-user-admin/README.md`
- `specs/full-core-smb-user-admin/conversation-capture.md`
- `specs/full-core-smb-user-admin/sprints/01-user-admin-vertical-contract-sprint.md`
- `specs/full-core-smb-user-admin/backlog/01-user-admin-vertical-contract-backlog.md`
- `specs/full-core-smb-saas-hardening/smb-full-core-baseline.md`
- `specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md`
- `specs/full-core-smb-saas-hardening/workstream-full-core-outline.md`
- `specs/full-core-smb-saas-hardening/agent-worker-opportunities.md`

## In scope

- User Admin capability ids, authority rules, surfaces/actions, deterministic service responsibilities, User Admin Agent behavior, access-review worker contract, audit/work traces, and validation map.
- Identify the first source-edit implementation task(s) to append if the implementation map reveals enough detail.

## Out of scope

- Editing starter source unless the queue is first updated to split a bounded source-change task.
- Enterprise IAM/SCIM/SSO administration.

## Expected outputs

- `specs/full-core-smb-user-admin/user-admin-vertical-contracts.md`
- updated `specs/full-core-smb-user-admin/pending-tasks.md` if follow-up source-edit tasks are appended

## Checks

- `git diff --check`
- `rg -n "User Admin|invitation|member|role|disable|reactivate|access review|UserAdminAgent|AutonomousAgent|deterministic|audit|trace|runtime validation" specs/full-core-smb-user-admin`

## Done criteria

- The first User Admin implementation slice is bounded enough for a fresh harness task.
- Agent, worker, deterministic-service, surface, capability, trace, and validation boundaries are explicit.
- Changes and queue update are committed.
