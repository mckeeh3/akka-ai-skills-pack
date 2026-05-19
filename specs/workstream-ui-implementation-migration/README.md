# Workstream UI Implementation Migration Plan

This migration updates the repository's executable/frontend reference layer so it matches the skills pack's canonical agent workstream UI architecture.

The migration is planning-only until individual pending tasks are executed in fresh harness sessions. Each implementation task must be self-contained, update `pending-tasks.md`, and end with a git commit.

## Why this migration exists

The doctrine and skills now define generated full-stack AI-first SaaS apps as **agent workstream applications**:

```text
role-authorized functional agents
→ continuous workstreams
→ structured renderable surfaces
→ governed backend capabilities
→ horizontal Akka implementation
```

The current `frontend/` reference still contains stale route/page-oriented seed app code and supervisory-console patterns. Those examples risk teaching future agents to generate page-first UIs even though the pack now requires workstream-first implementation.

## Target implementation model

The canonical frontend reference should demonstrate:

- collapsible left rail of role-authorized functional agents;
- selected `AuthContext`, tenant/customer, role/capability basis, and trace/approval indicators;
- continuous workstream panel for the selected functional agent;
- persistent composer for natural-language requests and command shortcuts;
- structured surfaces embedded in the workstream;
- non-chat navigation actions that append natural-language workstream feedback, e.g. `Display the user list view`;
- reusable surface components for dashboards, list/search, detail/edit, decision cards, audit timelines, workflow status, governance diffs, and outcome panels;
- capability-backed surface actions with idempotency, denial, audit/trace, and result-surface behavior;
- fixture-backed `/api/me`, surface payloads, workstream items, and realtime/stale states;
- implementation routes only as deep links into selected functional agents, workstream items, or surfaces.

## Surface navigation doctrine

This migration assumes structured surfaces can express classic SaaS UI patterns without making the app page-first.

Example User Admin flow:

```text
select User Admin in collapsible rail
→ append/open User Admin Dashboard surface
→ click Users/List action
→ append text item: "Display the user list view"
→ append User List surface with search/filter/table actions
→ click a user
→ append text item: "Display user account Pat Lee"
→ append User Detail surface with editable fields/actions if allowed
```

The user can later scroll to the previous User List surface and choose another user, or type `show users` in the composer. Clicks, buttons, links, icons, and form submissions remain first-class UI actions; the workstream records them as teachable intent/action feedback.

## Execution model

- Execute one task per fresh harness context.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Read this README, the selected sprint, backlog, queue entry, and task brief before editing.
- Do not perform broad opportunistic rewrites outside the selected task scope.
- Each task must end with one git commit containing only that task's intended changes and queue-status update.
- Record the commit message in task notes; record the commit hash only when practical without amending the same commit.
- Preserve task IDs. Supersede obsolete tasks instead of renumbering or deleting them.

## Read order for future sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `docs/ai-first-saas-application-architecture.md`
4. `docs/agent-workstream-application-architecture.md`
5. `docs/structured-surface-contracts.md`
6. `docs/web-ui-frontend-decomposition.md`
7. `skills/agent-workstream-apps/SKILL.md`
8. `skills/akka-web-ui-apps/SKILL.md`
9. this file
10. selected sprint spec under `sprints/`
11. selected backlog under `backlog/`
12. selected task entry in `pending-tasks.md`
13. selected task brief under `tasks/`

## Sprint sequence

1. `sprints/01-inventory-and-target-sprint.md` — inventory stale frontend/seed code and define the target reusable workstream component architecture.
2. `sprints/02-component-library-sprint.md` — create reusable workstream, surface, capability-action, state, and fixture component contracts.
3. `sprints/03-frontend-migration-sprint.md` — migrate `frontend/` from page-first seed shell to canonical workstream shell.
4. `sprints/04-reference-vertical-sprint.md` — implement a User Admin workstream reference vertical using dashboard, list/search, detail/edit, and audit feedback surfaces.
5. `sprints/05-tests-and-docs-sprint.md` — add contract tests, frontend quality checks, and skills/docs links to the executable reference.
6. `sprints/06-final-review-sprint.md` — remove or quarantine remaining stale code and verify consistency.

## Done state

The migration is complete when `frontend/` is a canonical workstream UI reference that future agents can reuse directly: functional-agent rail, workstream panel, composer, typed surfaces, capability actions, deep links, fixture clients, tests, and docs all reinforce the workstream architecture rather than page-first or chatbot-bolt-on patterns.
