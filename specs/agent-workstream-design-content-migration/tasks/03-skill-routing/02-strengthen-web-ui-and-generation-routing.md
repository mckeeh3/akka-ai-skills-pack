# Task Brief: Strengthen Web UI and Generation Routing

## Task

Update web UI docs/skills and generation/readiness guidance so generated SaaS UI realization clearly starts from the canonical workstream reference and does not promote legacy page/screen/static examples.

## Expected outputs

- targeted edits to files such as:
  - `docs/web-ui-pattern-selection.md`
  - `docs/web-ui-frontend-decomposition.md`
  - `docs/web-ui-quality-checklist.md`
  - `skills/akka-web-ui-apps/SKILL.md`
  - `skills/app-generate-app/SKILL.md`
  - `skills/app-description-readiness-assessment/SKILL.md`
- queue status update and git commit

## Rules

- Generated SaaS UI realization starts from `frontend/src/workstream/**` and the User Admin reference vertical.
- `frontend/src/screens/**` and static resource examples are legacy/mechanics references only.
- Route/page tests are insufficient without shell/rail/composer/surface/action/deep-link/realtime coverage.

## Completion

Mark `TASK-AWDD-03-002` done after commit.
