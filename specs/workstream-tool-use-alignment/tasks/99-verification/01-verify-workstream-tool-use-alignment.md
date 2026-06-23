# TASK-WTUA-99-001: Verify Workstream Tool Use Alignment completion

## Purpose

Verify that the skills-pack is aligned with the workstream tool-use architecture, or append bounded follow-up tasks and a new terminal verification task when gaps remain.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-tool-use-alignment/README.md`
- `specs/workstream-tool-use-alignment/conversation-capture.md`
- `specs/workstream-tool-use-alignment/pending-tasks.md`
- `specs/workstream-tool-use-alignment/sprints/*.md`
- `specs/workstream-tool-use-alignment/backlog/01-workstream-tool-use-alignment-build-backlog.md`
- `specs/workstream-tool-use-alignment/tool-use-source-map.md`
- `specs/workstream-tool-use-alignment/consistency-repair-notes.md` if present
- completed task notes and changed files from prior tasks

## Expected outputs

- `specs/workstream-tool-use-alignment/verification-notes.md`
- queue update marking verification `done` only if the README done state is achieved
- new bounded follow-up tasks plus a new terminal verification task if material gaps remain
- commit for verification notes and queue updates

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- `bash pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh`
- targeted searches proving:
  - governed tools are treated as shared capability-backed operations;
  - surfaces are human tool adapters;
  - human chat tool plans require explicit confirmation;
  - agent tools are model-facing adapters, not the root boundary;
  - stale global prohibitions on direct chat tool use have been reconciled.

## Done criteria

- Verification notes compare completed work against every README done-state bullet.
- Relevant pack checks pass, or any failure is unrelated and documented with a precise follow-up/blocker.
- If material alignment gaps remain, this task appends bounded tasks and a new terminal verification task instead of closing the mini-project.
- If no material gaps remain, this task records that the mini-project is complete.
- Changes and queue update are committed.
