# Task Brief: Restore In-Memory Sessions on Workstream Switch

## Task

Maintain independent Akka component-backed visual session state for each selected workstream and restore it when users switch between functional agents/workstreams.

## Expected outputs

- per-workstream visual session state in the frontend reference implementation
- restoration of selected/anchor/loaded/collapsed state where supported by existing UI contracts
- no jump-to-latest on workstream switch unless a new request is submitted or an explicit jump action is used
- focused contract tests for switching away from and back to a workstream
- queue status update and git commit

## Constraints

- Key state by selected auth context and functional agent/workstream id where available.
- Do not introduce browser-local or backend persistence.
- Do not use visual session state as an authorization source.

## Completion

Mark `TASK-WVS-01-003` done after commit.
