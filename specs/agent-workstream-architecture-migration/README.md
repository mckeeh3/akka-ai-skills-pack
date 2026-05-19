# Agent Workstream Architecture Migration Plan

This migration realigns the skills pack around a single opinionated UX/application architecture for generated AI-first SaaS apps: **agent workstream applications**.

The migration is planning-only until individual pending tasks are executed in fresh harness sessions. Each task must be self-contained, update its queue status, and make a git commit.

## Architectural pivot

Generated AI-first SaaS apps should no longer be described primarily as page hierarchies or conventional admin consoles with optional chat. The default generated app architecture is:

```text
role-authorized functional agents
→ continuous workstreams
→ structured renderable surfaces
→ governed backend capabilities
→ horizontal Akka implementation
```

A generated app grows by adding role-based functional areas. Each functional area is backed by a user-facing context-area agent. That agent owns a durable workstream and is progressively given skills, tools, surfaces, and capabilities. Internal agents may support evaluation, classification, routing, summarization, governance review, proposal drafting, replay, and other backend work without appearing directly in the left rail.

## Canonical terms

- **Agent workstream shell**: the primary browser UI. Left rail lists authorized functional agents; main panel shows a continuous vertical request/response/result stream; bottom composer accepts user input and commands.
- **Functional agent / context-area agent**: a role-authorized user-facing agent representing a functional area such as User Admin, Agent Admin, Sales Pipeline, Procurement, Finance, Support, Audit, or Governance.
- **Internal agent**: a non-left-rail agent invoked by workflows, tools, consumers, timers, or functional agents for bounded internal work.
- **Surface**: a typed renderable artifact in a workstream, such as dashboard, form, data table, chart, decision card, diff review, audit timeline, entity detail, approval card, or workflow status.
- **Capability**: the governed backend contract behind screen actions, agent tools, workflow steps, APIs, timers, consumers, and internal component calls.
- **Horizontal implementation**: Akka entities, workflows, views, consumers, timed actions, agents, endpoints, web UI code, auth/security, audit, and tests needed to support the vertical agents and surfaces.

## Opinionated target

This migration intentionally removes or rewrites legacy guidance that presents conventional screen/page-first UI, CRUD-admin-console structure, chatbot-as-bolt-on, or multiple equivalent UX alternatives as acceptable defaults.

Allowed nuance:

- traditional routes may exist as implementation details or deep links into workstream surfaces;
- reusable surfaces may appear in more than one functional agent's workstream;
- not every static/legal/public page is a functional agent, but every consequential authenticated work area is agent-backed;
- internal Akka components remain horizontal implementation details derived from capabilities.

Disallowed as default generated-app guidance:

- page hierarchy as the primary app model;
- chatbot panel beside a traditional app as the AI-first architecture;
- CRUD screens as the default decomposition root;
- tools as the root abstraction instead of governed capabilities;
- optional user/admin/agent governance for core generated SaaS apps.

## Execution model

- Execute one task per fresh harness context.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Read the sprint, backlog, and task brief before editing.
- Do not perform broad opportunistic rewrites outside the selected task scope.
- Each task must end with a git commit containing only that task's intended changes and the queue-status update.
- Record the commit message in task notes; record the commit hash only when practical without amending the same commit.
- Preserve task IDs. Supersede obsolete tasks instead of renumbering or deleting them.

## Read order for future sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `docs/ai-first-saas-application-architecture.md`
4. `docs/capability-first-backend-architecture.md`
5. this file
6. selected sprint spec under `sprints/`
7. selected backlog under `backlog/`
8. selected task entry in `pending-tasks.md`
9. task brief under `tasks/` when present
10. smallest relevant skills/docs named by the task

## Sprint sequence

1. `sprints/01-agent-workstream-doctrine-and-routing-sprint.md` — create canonical doctrine and make routing point to agent workstream architecture as the only default UI/application model.
2. `sprints/02-description-model-realignment-sprint.md` — revise app-description structure around functional agents, internal agents, surfaces, capabilities, and horizontal maps.
3. `sprints/03-ui-and-agent-skill-realignment-sprint.md` — update web UI and agent skills to implement workstream shell, structured surfaces, and context-area agent patterns.
4. `sprints/04-core-app-prd-and-seed-realignment-sprint.md` — replace/get rid of weak getting-started behavior with a canonical core app PRD based on foundation functional agents.
5. `sprints/05-legacy-content-removal-sprint.md` — search, revise, or remove stale page-first/CRUD-first/chatbot-bolt-on alternatives.
6. `sprints/06-final-consistency-review-sprint.md` — verify the pack presents one coherent architecture and add follow-up tasks for any remaining drift.

## Done state

The migration is complete when future harness sessions consistently interpret new generated apps as agent workstream applications, specify vertical functional agents and surfaces first, derive governed capabilities next, and implement Akka horizontals from those capabilities without offering page-first or chatbot-bolt-on alternatives as defaults.
