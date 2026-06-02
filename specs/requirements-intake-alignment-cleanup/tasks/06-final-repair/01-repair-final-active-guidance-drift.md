# TASK-RIAC-06-001: Repair final active-guidance drift findings

## Objective

Repair the bounded active-guidance drift found by terminal verification so the mini-project can close without known misleading intake references.

## Required reads

- AGENTS.md
- skills/README.md
- specs/requirements-intake-alignment-cleanup/README.md
- specs/requirements-intake-alignment-cleanup/final-verification.md
- specs/requirements-intake-alignment-cleanup/prune-and-rewrite-criteria.md
- templates/ai-first-saas-starter/app-description/README.md
- skills/app-description-capability-modeling/SKILL.md

## In scope

- Replace `templates/ai-first-saas-starter/app-description/README.md` wording that implies a `User Admin v0 minimum slice` with five-core workstream v0 starter/minimum scope language.
- Update `skills/app-description-capability-modeling/SKILL.md` so AI-first seed capability references are preferred and purchase-request capability examples are labeled conventional mechanics-only.
- Add an explicit mechanics-only label to the `skills/README.md` pending-task execution reference for `docs/examples/purchase-request-pending-tasks.md` if it remains listed.
- Update `pending-tasks.md` for this task only.

## Out of scope

- Do not run another broad whole-pack rewrite.
- Do not remove purchase-request examples unless a changed reference becomes unsafe and can be updated within this bounded task.

## Checks

- `git diff --check`
- `rg -n "User Admin v0 minimum slice|Prefer these example references when present:|purchase-request-pending-tasks.md" templates/ai-first-saas-starter/app-description/README.md skills/app-description-capability-modeling/SKILL.md skills/README.md`
- Manual inspection that remaining hits are removed or explicitly mechanics-only.

## Done criteria

- The three final-verification drift findings are repaired or explicitly justified as no longer material.
- Queue updated and committed.
