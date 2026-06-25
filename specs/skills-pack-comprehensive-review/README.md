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

- Do not review multiple inventory entries in one subagent task.
- Do not edit root SaaS Foundation App runtime code as part of this skills-pack review.
- Do not casually remove public skill names; prefer routing/deprecation unless the pruning rules are satisfied.
- Do not hand-edit installed mirror doctrine under `skills-pack/.agents/skills/**` as if it were source truth.
- Do not use examples or templates to reintroduce a competing app-description structure.

## Execution model

Use `file-review-inventory.md` as the task list. A parent orchestrator should run exactly one fresh-context subagent for exactly one pending file entry, wait for that subagent to finish and commit or block, then select the next pending file.

Each file-review subagent must:

1. select the first `pending` inventory entry unless the parent names a specific entry;
2. mark only that entry `in-progress` before reviewing;
3. read `review-guide.md`, `subagent-file-review-brief.md`, and the target file;
4. accept, revise, archive, remove, or block only that file's review scope;
5. update `file-review-inventory.md` with status and review notes;
6. run required checks proportional to the action;
7. commit the file changes and inventory update together when the review completes;
8. report the next pending inventory entry.

Do not run file-review subagents in parallel.

## Supporting documents

- `conversation-capture.md` — accepted discussion and scope.
- `review-guide.md` — canonical doctrine spine and review rubric.
- `subagent-file-review-brief.md` — one-file subagent execution contract.
- `file-review-inventory.md` — one row per tracked `skills-pack/**` file; this is the per-file task list.
- `pending-tasks.md` — mini-project orchestration and terminal verification tasks.
