# Task Brief: Review Top-Level Skill Routing Consistency

## Task

Review top-level routing and app-description skills for consistency after doctrine and UI routing edits.

## Expected outputs

- targeted edits to top-level routing/app-description skills, or a no-op note in queue notes if already consistent
- queue status update and git commit

## Files to consider

- `skills/README.md`
- `skills/agent-workstream-apps/SKILL.md`
- `skills/app-descriptions/SKILL.md`
- `skills/app-description-functional-agent-modeling/SKILL.md`
- `skills/app-description-surface-modeling/SKILL.md`

## Checks

- Natural-language UI, dashboard, admin console, portal, or workflow requests route to functional-agent/surface/capability modeling.
- Users are not required to know the internal skill taxonomy.
- No page-first or chatbot-bolt-on path is presented as an equal default for generated SaaS apps.

## Completion

Mark `TASK-AWDD-03-003` done after commit.
