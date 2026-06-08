# Task Brief: Classify and Repair Active Spec Template References

## Objective

Classify remaining active `specs/**` references to the dissolved full-app starter template and update runnable/current queues so they use the core-app-first paths or are explicitly superseded/deferred as historical planning.

## Required reads

- `AGENTS.md`
- `specs/core-app-first-repo-refactor/README.md`
- `specs/core-app-first-repo-refactor/target-layout-and-path-map.md`
- `specs/core-app-first-repo-refactor/asset-migration-inventory.md`
- `specs/core-app-first-repo-refactor/verification-notes-2026-06-03.md`
- `specs/core-app-first-repo-refactor/tasks/05-validation/03-classify-active-spec-template-references.md`
- search output from:
  - `rg -l --hidden --glob '!target/**' --glob '!frontend/node_modules/**' --glob '!node_modules/**' --glob '!skills-pack/dist/**' --glob '!specs/core-app-first-repo-refactor/**' --glob '!specs/archive/**' --glob '!.git/**' 'templates/ai-first-saas-starter|scaffold-ai-first-saas-starter|resources/templates/ai-first-saas-starter|starter template|full-app template|scaffold-first' specs docs README.md AGENTS.md skills-pack`

## In scope

- Create or update a stale-reference classification note under `specs/core-app-first-repo-refactor/` that groups matches as:
  - active runnable core-app queue/task that must be updated now;
  - skills-pack/spec provenance that can stay historical with a note;
  - obsolete completed queue/spec that should be marked archive/superseded if practical;
  - unrelated phrase use that does not imply scaffold-first guidance.
- Update bounded, high-risk active runnable queue/task references that still instruct fresh contexts to read or edit `templates/ai-first-saas-starter/**` or run scaffold scripts when the root app path should now be used.
- Prefer targeted path updates in pending tasks/task briefs over broad prose rewrites.
- If remaining stale spec references are too broad for one session, append additional bounded follow-up tasks before the terminal verification task and document the split.
- Update `specs/core-app-first-repo-refactor/pending-tasks.md`.

## Out of scope

- Root app source/test cleanup; `TASK-LAYOUT-05-001` owns that.
- Implementing any stale spec's product/runtime tasks.
- Whole-repository style cleanup unrelated to full-app template/scaffold path assumptions.

## Expected outputs

- `specs/core-app-first-repo-refactor/stale-template-reference-classification.md` or updated equivalent.
- Updated active spec queue/task references where safely bounded.
- Updated `specs/core-app-first-repo-refactor/pending-tasks.md`.

## Required checks

- `git diff --check`
- search proof for remaining stale template/scaffold references, with remaining matches classified in the note

## Done criteria

- Active runnable stale references are either updated, superseded, or split into newly appended bounded tasks.
- Historical/provenance references are explicitly classified so terminal verification can distinguish them from current guidance.
- Changes and queue update are committed with message `layout: classify active template references`.
