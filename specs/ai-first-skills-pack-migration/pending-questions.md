# Pending Questions

## Queue rules

- Ask one question at a time unless the user requests a batch.
- Resolve `answered` questions by reconciling them into the relevant artifacts.
- Do not create or execute implementation tasks blocked by unresolved `blocking` questions.
- Preserve question IDs; supersede obsolete questions rather than deleting them.

## Questions

### Q-001: Select web UI style guide theme

- status: pending
- priority: blocking
- category: ui
- depends on: []
- blocks:
  - TASK-08-006 web UI implementation and generation only
- source:
  - docs/examples/agent-first-dca-app-description/app-description/55-ui/style-guide.md is missing
  - specs/ai-first-skills-pack-migration/tasks/08-executable-ai-first-reference-slice/06-supplies-command-center-ui.md says to block rather than invent a visual theme
  - docs/web-ui-style-guide.md requires a selected style before browser UI implementation
- question: >
    Which default visual theme should generated web UI content use for the supplies command-center and decision-card reference UI?
- why it matters: >
    The selected theme drives generated HTML structure, CSS variables, spacing, typography, chart colors, component states, responsive behavior, and accessibility review. Without it the harness would have to invent visual style during implementation.
- options:
  - A: theme-1-northpeak-analytics — clean blue SaaS analytics dashboard
  - B: theme-2-promanage-violet — violet project-management workspace
  - C: theme-3-nordic-crm-teal — teal CRM/data pipeline console
  - D: theme-4-finflow-emerald — emerald finance/invoicing operations UI
  - E: theme-5-acme-admin-blue — general blue admin/ops dashboard
  - F: custom — user will provide a custom style brief or reference
- default if deferred: none for production generation; theme-1-northpeak-analytics may be accepted explicitly for early evaluation only
- answer: none
- decision: pending
- decision impact: pending
- reconciled into:
  - none
- notes:
  - When answered, reconcile into `docs/examples/agent-first-dca-app-description/app-description/55-ui/style-guide.md` or a migration UI style spec before unblocking TASK-08-006.
