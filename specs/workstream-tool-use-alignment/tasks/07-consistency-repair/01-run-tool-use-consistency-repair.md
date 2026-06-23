# TASK-WTUA-07-001: Run tool-use consistency repair pass

## Purpose

Search the skills-pack for remaining contradictions or stale wording after the main alignment tasks, then repair only the smallest safe set of files.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-tool-use-alignment/README.md`
- `specs/workstream-tool-use-alignment/conversation-capture.md`
- `specs/workstream-tool-use-alignment/tool-use-source-map.md`
- `specs/workstream-tool-use-alignment/sprints/03-planning-validation-and-consistency.md`
- completed task notes from `pending-tasks.md`

## Suggested searches

Search for stale or conflicting phrasing such as:

- tools described only as agent function tools;
- surfaces described only as pages/screens without tool adapter semantics;
- direct chat command use globally forbidden rather than conditioned on governed tool boundary and confirmation;
- side-effecting tool guidance that lacks confirmation, approval, idempotency, transaction, denial, or trace semantics;
- duplicated surface action and agent tool semantics without shared governed tool ids.

## Expected outputs

- Focused repairs to docs/skills/templates/examples as needed.
- `specs/workstream-tool-use-alignment/consistency-repair-notes.md` describing searches run, files repaired, and residual findings.
- Queue update.

## Required checks

- `git diff --check`
- relevant install checks if skill references change:
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`

## Done criteria

- No known high-confidence contradictions remain in `skills-pack/docs/**` or touched skill families.
- Residual lower-confidence or out-of-scope findings are recorded for terminal verification to assess.
- Changes and queue update are committed.
