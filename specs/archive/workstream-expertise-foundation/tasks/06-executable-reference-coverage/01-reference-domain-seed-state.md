# Add reference domain state and seed import

## Objective

Add first-class governed reference records to the starter backend and import User Admin reference seed resources.

## Required reads

Use the matching entry in `specs/workstream-expertise-foundation/pending-tasks.md` as the authoritative required-read list. Also read the sprint and backlog file for this task before editing.

## Scope

Extend the starter backend domain/application seam with ReferenceDocument and AgentReferenceManifest records or explicitly named interim equivalents. Update AgentDefinition/repository state/seed loader as needed so references are tenant-governed state with provenance, checksums, active versions, and idempotent import behavior.

## Non-goals

Do not implement the runtime readReferenceDoc loader in this task beyond state needed for compilation/tests.

## Execution steps

1. Mark this task `in-progress` in `specs/workstream-expertise-foundation/pending-tasks.md`.
2. Read all required files from the queue entry.
3. Make the smallest source/spec edits needed for the task.
4. Run the required checks from the queue entry.
5. Update the queue entry with status `done` only when done criteria are met; otherwise mark `blocked` with a reason.
6. Commit the task changes with the commit message listed in the queue notes.

## Done criteria

Use the matching queue entry's done criteria. The task is not complete until the queue is updated and the git commit is created.
