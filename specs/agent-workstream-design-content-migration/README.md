# Agent Workstream Design Content Migration Plan

This migration tightens repository design guidance now that generated full-stack AI-first SaaS apps have a consistent **functional/context-area agent workstream UI** model.

The migration is planning-only until individual pending tasks are executed in fresh harness sessions. Each implementation task must be self-contained, update `pending-tasks.md`, and end with a git commit.

## Why this migration exists

The repository now consistently frames generated apps as:

```text
secure SaaS foundation
→ role-authorized functional/context-area agents
→ durable workstreams
→ typed structured surfaces
→ governed backend capabilities
→ horizontal Akka implementation
```

Some design-related docs, skills, and examples still contain partial drift:

- inconsistent `55-ui/` file sets;
- blurred ownership between `12-workstreams/` and `55-ui/`;
- mixed terminology such as functional agents, context-area agents, left-rail agents, and work areas;
- surface-selection guidance that can sound surface-first instead of workstream-placement-first;
- DCA and legacy UI references that need migration or clearer quarantine labels;
- repeated page/screen/CRUD warnings that should be consolidated into a reusable review checklist.

## Target content model

After this migration, future agents should see one clear design path:

1. `12-workstreams/` owns the application model: functional agents, internal agents, workstreams, surface index, and surface contracts.
2. `55-ui/` owns browser realization semantics: shell, rail, panel/composer, rendering, routes/deep links, API contracts, state/realtime, accessibility/responsive behavior, and style.
3. Surfaces are always placed inside owning/reusable functional-agent workstreams before UI realization or routes are discussed.
4. Conventional pages/routes exist only for public/static content, implementation, and deep links.
5. Generated SaaS UI realization starts from `frontend/src/workstream/**` and the User Admin reference vertical, not legacy `frontend/src/screens/**` or static resource examples.
6. Design review uses a compact checklist that catches page-first, CRUD-first, chatbot-bolt-on, missing capability, missing auth, and missing style-guide drift.

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
6. `docs/capability-first-backend-architecture.md`
7. `docs/internal-app-description-architecture.md`
8. `skills/agent-workstream-apps/SKILL.md`
9. `skills/app-description-ui/SKILL.md`
10. this file
11. selected sprint spec under `sprints/`
12. selected backlog under `backlog/`
13. selected task entry in `pending-tasks.md`
14. selected task brief under `tasks/`

## Sprint sequence

1. `sprints/01-inventory-and-target-sprint.md` — inventory design-content drift and define the canonical terminology, file-set, and review-checklist targets.
2. `sprints/02-doctrine-and-description-sprint.md` — align core doctrine and app-description guidance around `12-workstreams/` vs `55-ui/` ownership and canonical terminology.
3. `sprints/03-skill-routing-sprint.md` — tighten routing and implementation skills so surfaces are workstream-placed and legacy page/screen paths are quarantined.
4. `sprints/04-examples-sprint.md` — refresh or label examples, especially DCA, so examples reinforce the current design.
5. `sprints/05-final-review-sprint.md` — run a final consistency review and record completion.

## Done state

The migration is complete when design-related docs, skills, examples, readiness/generation gates, and review aids consistently teach functional/context-area agent workstreams as the generated-app application model, with structured surfaces and browser UI realization subordinate to that model.
