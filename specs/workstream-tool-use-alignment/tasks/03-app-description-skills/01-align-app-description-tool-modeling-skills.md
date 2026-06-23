# TASK-WTUA-03-001: Align app-description and intent skills with workstream tool catalogs

## Purpose

Update intent/app-description skills so generated current-intent graph artifacts can represent governed workstream tools, actor adapters, and confirmed human chat tool plans consistently.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-tool-use-alignment/README.md`
- `specs/workstream-tool-use-alignment/conversation-capture.md`
- `specs/workstream-tool-use-alignment/tool-use-source-map.md`
- `specs/workstream-tool-use-alignment/sprints/02-skill-family-alignment.md`
- canonical docs updated by `TASK-WTUA-02-001`
- focused `skills-pack/skills/app-description-*/SKILL.md` files named by the source map
- `skills-pack/skills/app-descriptions/SKILL.md`
- `skills-pack/skills/app-generate-app/SKILL.md`

## Expected outputs

- App-description skills updated to model:
  - workstream agent bounded tool catalog;
  - shared governed tool ids inside capability/workstream bindings;
  - surface action/browser-tool adapters;
  - human chat tool-plan adapters with confirmation semantics;
  - AI agent-tool adapters and tool-boundary membership;
  - policy/approval/denial behavior, transaction/idempotency, traces, and tests.
- Readiness skills updated so missing tool catalog, confirmation, adapter mapping, or trace semantics block readiness where consequential work is in scope.
- Queue update.

## Required checks

- `git diff --check`
- targeted search over edited app-description skills proving `governed tool`, `surface`, `human chat`, `confirmation`, `agent-tool`, and `trace` concepts are represented where applicable

## Done criteria

- Skills avoid duplicating business semantics between surface actions and agent/chat tools.
- Skills preserve global definition plus workstream binding separation from the current-intent model.
- Changes and queue update are committed.
