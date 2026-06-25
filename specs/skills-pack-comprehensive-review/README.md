# Skills-Pack Comprehensive Review

## Purpose

Create a durable, file-by-file review program for `skills-pack/` after significant revisions to the app development lifecycle and app modeling structure.

The review uses one shared doctrine spine so every source file can be accepted, tightened, revised, archived, removed, or blocked for a specific reason instead of being judged by stale or conflicting assumptions.

## Current intent

The skills pack should have one obvious spine:

```text
current-intent graph
  -> workstream vertical
    -> worker / execution harness / actor adapter / governed tool / capability
      -> Akka + frontend realization
        -> real runtime evidence
          -> reconciliation back into current intent
```

The pack should guide harness agents to maintain and extend the runnable root SaaS Foundation App. It should not present `skills-pack/`, installed `.agents/skills/**`, examples, or templates as a separate application baseline.

## Done state

This mini-project is complete when:

- every tracked `skills-pack/**` file is represented in `file-review-inventory.md`;
- each inventory entry has a terminal status: `accepted`, `revised`, `archived`, `removed`, `installer-output-verified`, `superseded`, or `blocked-with-follow-up`;
- source-authoritative docs, skills, references, examples, templates, tools, pack metadata, and install scripts align with `review-guide.md`;
- direct edits are not made to installed mirror files under `skills-pack/.agents/skills/**` except through an explicit installer-output reconciliation decision;
- stale legacy numbered app-description guidance is either revised, clearly labeled compatibility/legacy, or archived;
- broad skills are routing contracts and do not duplicate canonical doctrine unnecessarily;
- runtime completion doctrine remains fail-closed and does not count mock/demo/model-less behavior as real user-visible completion;
- required validation checks pass for the changed assets;
- terminal verification records whether the comprehensive review is complete or appends a new bounded follow-up queue.

## Non-goals

- Do not edit root SaaS Foundation App runtime code as part of this skills-pack review.
- Do not casually remove public skill names; prefer routing/deprecation unless the pruning rules are satisfied.
- Do not hand-edit installed mirror doctrine under `skills-pack/.agents/skills/**` as if it were source truth.
- Do not use examples or templates to reintroduce a competing app-description structure.
- Do not let batching hide per-file decisions; every inventory row still needs its own terminal status and notes.

## Execution model

Use `file-review-inventory.md` as the task list. A parent orchestrator should run exactly one fresh-context subagent for a small batch of pending inventory entries, wait for that subagent to finish and commit or block, then continue launching one batch subagent at a time until no `pending` or `in-progress` entries remain.

Default batch size is **10 consecutive pending source-authoritative entries**. The parent may reduce the batch size to 1 for high-risk top-level guidance, installer, manifest, archive/removal, or broad doctrine files. Installed-output mirror entries may be batched more aggressively only after source-authoritative review is complete and the task is just verifying installed output behavior.

Each batch-review subagent must:

1. select the first pending batch unless the parent names specific inventory ids/paths;
2. mark only the selected rows `in-progress` before reviewing;
3. read `review-guide.md`, `subagent-file-review-brief.md`, and each selected target file;
4. make an independent decision for each row: accept, revise, archive, remove, verify installer output, supersede, or block;
5. keep edits scoped to the selected batch and required reference/manifest/link repairs;
6. update `file-review-inventory.md` with terminal status and review notes for every selected row;
7. run required checks proportional to the batch actions;
8. commit the batch changes and inventory updates together when the batch completes;
9. report the next pending batch and whether any blocked follow-up needs parent attention.

Do not run batch-review subagents in parallel.

## Supporting documents

- `conversation-capture.md` — accepted discussion and scope.
- `review-guide.md` — canonical doctrine spine and review rubric.
- `subagent-file-review-brief.md` — one-file subagent execution contract.
- `file-review-inventory.md` — one row per tracked `skills-pack/**` file; this is the per-file task list.
- `pending-tasks.md` — mini-project orchestration and terminal verification tasks.
