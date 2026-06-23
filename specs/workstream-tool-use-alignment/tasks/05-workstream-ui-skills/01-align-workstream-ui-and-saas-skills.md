# TASK-WTUA-05-001: Align workstream, SaaS, and UI skills

## Purpose

Update workstream and UI-oriented skills so surfaces are consistently treated as human tool-use adapters and workstream chat is treated as a natural-language tool-plan adapter.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-tool-use-alignment/README.md`
- `specs/workstream-tool-use-alignment/conversation-capture.md`
- `specs/workstream-tool-use-alignment/tool-use-source-map.md`
- `specs/workstream-tool-use-alignment/sprints/02-skill-family-alignment.md`
- canonical docs updated by `TASK-WTUA-02-001`
- `skills-pack/skills/ai-first-saas/SKILL.md`
- `skills-pack/skills/agent-workstream-apps/SKILL.md`
- `skills-pack/skills/ai-first-saas-worker-decomposition/SKILL.md`
- `skills-pack/skills/ai-first-saas-ui-surfaces/SKILL.md`
- `skills-pack/skills/ai-first-saas-audit-trace/SKILL.md`
- `skills-pack/skills/core-saas-foundation/SKILL.md`
- `skills-pack/skills/capability-first-backend/SKILL.md`
- relevant `akka-web-ui-*` skills named by the source map

## Expected outputs

- Workstream/SaaS/UI skills updated to preserve:
  - selected workstream agent purpose and bounded tool catalog;
  - structured surface actions as human tool adapters;
  - chat request → proposed tool plan → human confirmation → transactional tool execution → result surface flow;
  - help/how-to behavior that stays within the workstream and does not require confirmation unless executing tools;
  - UI requirements for detailed confirmation, editable review states, denials, partial-failure reporting, accessibility, and traces.
- Queue update.

## Required checks

- `git diff --check`
- targeted search proving edited skills distinguish surface routing/no-mutation from confirmed chat tool execution

## Done criteria

- Existing deterministic surface routing remains a recommended safe path.
- Skills no longer imply that all direct chat tool execution is categorically forbidden when a complete governed tool boundary exists.
- Changes and queue update are committed.
