# TASK-WTUA-06-001: Align planning, templates, examples, and validation assets

## Purpose

Update planning outputs and reusable assets so future generated-app specs/backlogs/tasks carry the workstream tool-use contract into implementation.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-tool-use-alignment/README.md`
- `specs/workstream-tool-use-alignment/conversation-capture.md`
- `specs/workstream-tool-use-alignment/tool-use-source-map.md`
- `specs/workstream-tool-use-alignment/sprints/03-planning-validation-and-consistency.md`
- canonical docs and skills updated by prior tasks
- `skills-pack/docs/pending-task-queue.md`
- `skills-pack/docs/pending-question-queue.md`
- planning skills named by the source map
- relevant `skills-pack/templates/**`, `skills-pack/examples/**`, and `skills-pack/tools/**` named by the source map

## Expected outputs

- Planning/queue skills and docs updated so task briefs and queue entries require tool-use fields when consequential work is in scope:
  - workstream agent/tool catalog;
  - governed tool id and capability id;
  - actor adapter and exposure channel;
  - human-chat confirmation behavior;
  - policy approval behavior;
  - idempotency/transaction boundary;
  - audit/work trace and validation evidence.
- Templates/examples updated where needed to show shared governed tool ids across surface and agent/chat adapters.
- Existing validators updated only where lightweight structural checks are appropriate.
- Queue update.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- targeted validator/test command if a tool script changes

## Done criteria

- Planning assets make the first implementation task block rather than guess when tool catalog/adapter/confirmation semantics are missing.
- Installed skills validation passes.
- Changes and queue update are committed.
