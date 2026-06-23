# TASK-WCTE-99-002: Re-verify Workstream Chat Tool Execution completion

## Purpose

Re-run terminal verification after follow-up repairs from `TASK-WCTE-99-001` and close the mini-project only if the README done state is achieved.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/conversation-capture.md`
- `specs/workstream-chat-tool-execution/pending-tasks.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- `specs/workstream-chat-tool-execution/verification-notes.md`
- completed follow-up task notes and changed files
- related backend/frontend/app-description files

## Expected outputs

- updated `specs/workstream-chat-tool-execution/verification-notes.md`
- queue update marking verification done only when README done state is achieved
- additional bounded follow-up tasks plus another terminal verification task if material gaps remain
- commit for verification notes and queue updates

## Required checks

- `git diff --check`
- targeted backend chat tool execution tests
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- runtime-evidence and workstream-contract validators
- local API/UI/manual smoke for the User Admin example when provider/auth/runtime configuration allows it

## Done criteria

- Verification notes compare completed work against every README done-state bullet.
- User Admin example is proven no-mutation before confirmation and executed after confirmation when authorized.
- All five foundation workstreams have representative confirmed chat tool-plan coverage.
- Provider missing config fails closed and is not counted as successful model-backed planning.
- Audit/work trace evidence is recorded.
- All required checks pass, or new bounded follow-up tasks are appended and the mini-project remains open.
- Changes and queue update are committed.
