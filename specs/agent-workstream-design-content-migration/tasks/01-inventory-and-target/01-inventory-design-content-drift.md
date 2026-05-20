# Task Brief: Inventory Agent Workstream Design-Content Drift

## Task

Inventory docs, skills, examples, readiness/generation guidance, and web UI routing content that may still conflict with or partially obscure the functional/context-area agent workstream design model.

## Expected outputs

- `specs/agent-workstream-design-content-migration/design-content-drift-inventory.md`
- queue status update and git commit

## Inventory focus

Classify each finding as:

- align now;
- label as legacy/mechanics-only;
- leave unchanged;
- candidate for a later task.

Look especially for:

- inconsistent `55-ui/` file sets;
- unclear `12-workstreams/` vs `55-ui/` ownership;
- terminology drift;
- page/screen/CRUD/chatbot framing;
- surface-first guidance outside functional-agent workstreams;
- weak links to `frontend/src/workstream/**`.

## Completion

Mark `TASK-AWDD-01-001` done after commit.
