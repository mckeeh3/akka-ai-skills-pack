# Pending Questions

## Queue rules

- Ask one question at a time unless the user requests a batch.
- Resolve `answered` questions by reconciling them into the relevant artifacts.
- Do not create or execute implementation tasks blocked by unresolved `blocking` questions.
- Preserve question IDs; supersede obsolete questions rather than deleting them.

## Questions

### Q-001: Select web UI style guide

- status: resolved
- priority: blocking
- category: ui
- depends on: []
- blocks:
  - TASK-08-006 web UI implementation and generation only
- source:
  - docs/examples/ai-first-dca-app-description/app-description/55-ui/style-guide.md is missing
  - specs/ai-first-skills-pack-migration/tasks/08-executable-ai-first-reference-slice/06-supplies-command-center-ui.md says to block rather than invent a visual style
  - docs/web-ui-style-guide.md requires a selected style before browser UI implementation
- question: >
    Which AI-first style guide should generated web UI content use for the supplies command-center and decision-card reference UI?
- why it matters: >
    The selected style guide drives generated HTML structure, CSS variables, spacing, typography, chart colors, component states, responsive behavior, and accessibility review. Without it the harness would have to invent visual style during implementation.
- options:
  - A: atlas-ops-supervisory-console — recommended canonical AI-first SaaS supervision UI
  - B: custom — user will provide a custom style brief or reference that preserves AI-first supervision, decision, governance, audit, and outcome surfaces
- default if deferred: none for production generation; atlas-ops-supervisory-console may be accepted explicitly for early evaluation only
- answer: atlas-ops-supervisory-console
- decision: selected `atlas-ops-supervisory-console` — canonical AI-first SaaS supervision UI
- decision impact: generated browser UI for the supplies command-center and decision-card reference UI must follow the Atlas Ops supervisory console style guide, using centralized CSS tokens, system light/dark mode, AI-first command/supervision anatomy, decision-card evidence/policy/trace visibility, accessible status semantics, and responsive supervision-oriented layouts.
- reconciled into:
  - `docs/examples/ai-first-dca-app-description/app-description/55-ui/style-guide.md`
- notes:
  - Generic dashboard style-gallery choices were removed from the skills pack and are not available planning options.
  - TASK-08-006 is no longer blocked by style selection.
