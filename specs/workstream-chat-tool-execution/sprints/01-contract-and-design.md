# Sprint 01: Contract and Design

## Goal

Make confirmed chat tool execution implementation-ready before runtime code changes.

## Scope

- Audit current WorkstreamService actions, surface intent routing, managed-agent runtime, frontend composer/surface rendering, and app-description workstream contracts.
- Identify existing governed tools that can safely become first-pass `human_chat_tool_plan` exposures.
- Update app-description/current-intent artifacts for all five foundation workstreams.
- Write the implementation design for shared plan proposal, confirmation, dispatcher, trace, frontend, and validation behavior.

## Output expectations

- A source/design map under `specs/workstream-chat-tool-execution/`.
- App-description workstream/capability updates showing shared governed tool ids and actor adapters.
- A task-ready design note that later implementation tasks can follow without guessing.

## Completion signal

Sprint 01 is complete when the next backend implementation task can name the exact records/DTOs, APIs, surfaces, action ids, trace fields, and first-pass workstream tool paths to implement.
