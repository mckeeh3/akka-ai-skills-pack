# Sprint 01: Backend doc administration

## Goal

Create the backend foundation for SaaS-admin-only agent document administration: agent list/detail, versioned prompt/skill/reference docs, current-version-only edits, save/cancel, restore, and skill/reference lifecycle.

## Tasks

- `AADE-01-001`: backend contract and service slice for agent doc administration.
- `AADE-01-002`: durable versioned document state and skill/reference lifecycle.

## Acceptance

- Backend model can represent all current app-description document semantics.
- Existing governance-heavy APIs are not used as the user-facing Agent Admin contract.
- Tests prove simple integer versions, adjacent-version diffs, restore-created versions, current-version-only save behavior, permanent skill/reference deletion, and SaaS-admin-only authorization at the service/API boundary appropriate for the task.
