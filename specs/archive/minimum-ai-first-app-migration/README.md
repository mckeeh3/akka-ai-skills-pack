# Minimum AI-First App Migration Plan

This migration captures the revised minimum generated-app model for the skills pack:

> The minimum AI-first SaaS app is a bootstrap-authorized **User Admin workstream v0**, not a generic chatbot. Its first renderable surface is a `markdown_response` surface, typically model markdown rendered as sanitized HTML. It must still include a real AuthContext, role/capability boundary, durable workstream log, capability boundary for backend actions/tools, and audit/work trace substrate.

The migration is planning-only until individual pending tasks are executed in fresh harness sessions. Each task must be self-contained, update `pending-tasks.md`, and make a git commit.

## Target distinction

This migration must preserve the difference between:

| State | Meaning |
|---|---|
| Minimum starter ready | Bootstrap-only User Admin workstream v0 works with request/response timeline, markdown response surface, minimal AuthContext, workstream log, and audit/work trace substrate. |
| Full core SaaS ready | Complete User Admin, Agent Admin, Audit/Trace, invitations/onboarding, roles/memberships, governed runtime agent docs, full security and tenant-isolation tests. |
| App-specific ready | Full core foundation plus app/domain workstreams, surfaces, capabilities, components, and tests. |

The minimum starter is a valid first implementation slice. It is not full SaaS readiness and must carry explicit follow-up work for full User Admin, Agent Admin, Audit/Trace, invitations, and security completeness.

## Architectural growth path

```text
User Admin workstream v0
  bootstrap auth + markdown_response surface + workstream log + traces
→ User Admin structured surfaces and admin capabilities
→ Agent Admin workstream
→ Audit/Trace workstream UI
→ app-specific workstreams
```

## Key rules

- Do not describe the minimum app as a generic chatbot.
- Do not defer all security: bootstrap-only access still needs backend AuthContext, roles/capabilities, denials, and traces.
- Audit/work trace recording starts in the first slice; Audit/Trace UI may come later.
- `markdown_response` is a real structured surface type, not an informal chat blob.
- Surface actions, agent tools, browser APIs, workflows, timers, consumers, and internal calls still map to governed capabilities.
- Full generated core readiness remains stricter than minimum starter readiness.

## Execution model

- Execute one task per fresh harness context.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Read this file, the selected sprint, selected backlog, and selected task brief before editing.
- Do not perform broad opportunistic rewrites outside the selected task scope.
- Each task must end with a git commit containing only that task's intended changes and the queue-status update.
- Record the commit message in task notes; record the commit hash only when practical without amending the same commit.
- Preserve task IDs. Supersede obsolete tasks instead of renumbering or deleting them.

## Read order for future sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `docs/ai-first-saas-application-architecture.md`
4. `docs/agent-workstream-application-architecture.md`
5. `docs/capability-first-backend-architecture.md`
6. this file
7. selected sprint under `sprints/`
8. selected backlog under `backlog/`
9. selected task entry in `pending-tasks.md`
10. task brief under `tasks/`
11. smallest relevant skills/docs named by the task

## Sprint sequence

1. `sprints/01-minimum-app-doctrine-sprint.md` — create and integrate canonical minimum app doctrine, including `markdown_response` as the first surface.
2. `sprints/02-foundation-routing-sprint.md` — update foundation/routing skills to distinguish minimum starter readiness from full-core SaaS readiness.
3. `sprints/03-description-starter-sprint.md` — update app-description guidance, seed examples, starter/scaffold docs, and planning outputs to generate the User Admin v0 first slice.
4. `sprints/04-final-consistency-review-sprint.md` — verify no generic-chatbot or all-or-nothing foundation drift remains.

## Done state

The migration is complete when future harness sessions interpret “minimum AI-first app,” “starter,” “initial chatbot,” or similar input as the bootstrap-only User Admin workstream v0, while preserving capability-first backend semantics, audit/work trace substrate, and an explicit path to full core SaaS readiness.
