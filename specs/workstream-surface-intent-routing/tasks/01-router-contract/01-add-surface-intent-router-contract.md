# Task: Add surface intent router contract

## Objective

Introduce a backend deterministic surface intent router contract and integrate it before model-backed workstream message invocation, without adding direct command submission.

## Required reads

- `AGENTS.md`
- `specs/workstream-surface-intent-routing/README.md`
- `specs/workstream-surface-intent-routing/conversation-capture.md`
- `specs/workstream-surface-intent-routing/sprints/01-router-user-admin-proof.md`
- `specs/workstream-surface-intent-routing/backlog/01-surface-intent-routing-build-backlog.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `src/main/java/ai/first/application/foundation/agent/AgentBehaviorSeedLoader.java`
- relevant workstream service tests

## Skills

- capability-first-backend
- akka-agent-tool-boundaries
- akka-agent-work-trace

## Expected outputs

- Backend router/result model for deterministic surface intent matches.
- Workstream message path attempts router before model invocation.
- Matched route returns a surface response/workstream item and marks the result as no-mutation.
- Safe fallback to current governed model-backed chat for unmatched prompts.
- Focused tests or test seams for matched vs fallback behavior.
- Queue update.

## Required checks

- `git diff --check`
- targeted backend tests for the new router path
- broader `mvn test` if shared workstream runtime behavior changes materially

## Done criteria

- Router contract is explicit and side-effect free.
- Matched routes do not invoke the model-backed runtime.
- Unmatched prompts preserve existing governed runtime behavior.
- Backend authorization/selected-context handling remains authoritative.
- Changes and queue update are committed.
