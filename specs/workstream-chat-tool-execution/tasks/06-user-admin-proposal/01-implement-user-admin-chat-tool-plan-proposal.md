# TASK-WCTE-06-001: Implement User Admin chat tool plan proposal

## Purpose

Implement the no-mutation proposal path for the motivating User Admin request.

## Target prompt

```text
create org "Org 1", and invite mckee.hugh@gmail.com as an org admin
```

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- User Admin app-description files updated by prior tasks
- related User Admin backend tests named in the design map

## Skills

- `capability-first-backend`
- `akka-agent-structured-responses`
- `akka-agent-testing`
- `akka-agent-work-trace`

## Expected outputs

- User Admin route from chat prompt to plan proposal/confirmation surface.
- Plan steps for Organization creation and Organization Admin invitation with shared governed tool ids/capabilities.
- No pre-confirmation Organization or invitation mutation.
- Safe denial/plan-unavailable behavior when auth/provider/runtime/tool-boundary checks fail.
- Queue update.

## Required checks

- `git diff --check`
- targeted backend User Admin proposal tests

## Done criteria

- The plan is detailed enough for human confirmation and does not execute.
- The plan is bound to selected AuthContext, workstream, step inputs, idempotency, and trace refs.
- Changes and queue update are committed.
