# Sprint 1: Agent Workstream Doctrine and Routing

## Goal

Create the canonical doctrine for agent workstream applications and update top-level routing so this architecture is the default generated-app model.

## Scope

- Add `docs/agent-workstream-application-architecture.md` as canonical doctrine.
- Integrate the doctrine into `docs/ai-first-saas-application-architecture.md`.
- Update `skills/README.md` to route high-level app/UI/generation requests through agent workstream interpretation.
- Create a top-level `agent-workstream-apps` skill.
- Keep capability-first backend doctrine intact: workstream surfaces and tools map to governed capabilities.

## Out of scope

- Broad rewrites of every component skill.
- Implementing frontend source code.
- Removing legacy content beyond explicit references touched by this sprint.

## Done criteria

- The pack names agent workstream architecture as the mandatory default UI/application model for generated AI-first SaaS apps.
- Routing does not present conventional page-first architecture or chatbot-bolt-on as equivalent defaults.
- The new skill routes to app-description, capability-first, web UI, agent, and decomposition skills without duplicating them.
