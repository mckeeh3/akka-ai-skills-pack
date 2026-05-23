# Align expertise model-binding contract

## Objective

Make model binding a first-class part of every LLM-backed workstream expert bundle.

## Required reads

Use the matching entry in `specs/workstream-expertise-foundation/pending-tasks.md` as the authoritative required-read list. Also read the sprint and backlog file for this task before editing.

## Scope

Update doctrine, app-description functional-agent/readiness guidance, and seed foundation expert bundles so each workstream has explicit `ModelConfigRef`/`ModelPolicy` or explicit inherited default, plus provider secret boundary and model-use trace requirements.

## Non-goals

Do not implement starter runtime model resolver in this task.

## Execution steps

1. Mark this task `in-progress` in `specs/workstream-expertise-foundation/pending-tasks.md`.
2. Read all required files from the queue entry.
3. Make the smallest source/spec edits needed for the task.
4. Run the required checks from the queue entry.
5. Update the queue entry with status `done` only when done criteria are met; otherwise mark `blocked` with a reason.
6. Commit the task changes with the commit message listed in the queue notes.

## Done criteria

Use the matching queue entry's done criteria. The task is not complete until the queue is updated and the git commit is created.
