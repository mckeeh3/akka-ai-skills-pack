# TASK-WCTE-12-002: Normalize runtime completion evidence in queue notes

## Purpose

Repair the terminal verification blocker where runtime-evidence validators report that completed feature-bearing task notes lack formal readiness labels and required selected-scope/auth/denial/provider evidence fields.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/pending-tasks.md`
- `specs/workstream-chat-tool-execution/verification-notes.md`
- completed task notes and changed files

## Scope

Update queue evidence only. Do not change runtime backend/frontend behavior unless a validator failure exposes a concrete implementation gap that must be handled through a separate bounded task.

## Required checks

- `git diff --check`
- `python3 skills-pack/tools/validate-runtime-completion-evidence.py specs/workstream-chat-tool-execution/pending-tasks.md`
- `bash skills-pack/tools/validate-pending-task-workstream-contract.sh specs/workstream-chat-tool-execution/pending-tasks.md`

## Done criteria

- Completed task notes use exact runtime readiness labels such as `described`, `surface-ready`, `backend-ready`, `frontend-rendered`, `api-smoked`, or `runtime-ready` without over-claiming.
- Provider fail-closed evidence is recorded and not counted as successful model-backed planning.
- Validators pass, or any remaining failures are converted into bounded follow-up tasks before terminal verification.
- Changes and queue update are committed.
