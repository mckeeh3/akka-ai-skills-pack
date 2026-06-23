# TASK-WCTE-99-001: Verify Workstream Chat Tool Execution completion

## Purpose

Verify that confirmed workstream chat tool execution works through the intended local runtime/API/UI path at the stated scope, or append follow-up tasks and a new terminal verification task.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/conversation-capture.md`
- `specs/workstream-chat-tool-execution/pending-tasks.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- completed task notes and changed files
- related backend/frontend/app-description files

## Skills

- `akka-runtime-feature-verification`
- `akka-agent-testing`
- `akka-web-ui-testing`
- `akka-agent-work-trace`

## Expected outputs

- `specs/workstream-chat-tool-execution/verification-notes.md`
- queue update marking verification done only when the README done state is achieved
- new bounded follow-up tasks plus a new terminal verification task if material gaps remain
- commit for verification notes and queue updates

## Required checks

- `git diff --check`
- targeted backend chat tool execution tests
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`
- `npm --prefix frontend run build` if frontend runtime output changed materially
- `mvn test` if shared backend workstream/agent behavior changed materially
- local API/UI/manual smoke for the User Admin example when provider/auth/runtime configuration allows it

## Done criteria

- Verification notes compare completed work against every README done-state bullet.
- User Admin example is proven no-mutation before confirmation and executed after confirmation when authorized.
- All five foundation workstreams have representative confirmed chat tool-plan coverage.
- Provider missing config fails closed and is not counted as successful model-backed planning.
- Audit/work trace evidence is recorded.
- If gaps remain, new bounded tasks plus a new terminal verification task are appended.
- Changes and queue update are committed.
