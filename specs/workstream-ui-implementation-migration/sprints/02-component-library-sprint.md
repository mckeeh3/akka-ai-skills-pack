# Sprint 2: Reusable Workstream Component Library

## Goal

Create a reusable reference component set for agent workstream UIs.

## Scope

- Define shared TypeScript types for functional agents, AuthContext, workstream items, surface envelopes, surface actions, action results, and surface events.
- Create reusable shell components: workstream shell, collapsible functional-agent rail, context/authority bar, main workstream panel, persistent composer.
- Create reusable stream components: user intent, agent response, capability result, workflow progress, safe denial, trace link, and structured surface item.
- Create reusable surface components: dashboard, list/search, detail/edit, decision card, audit timeline, workflow status, governance diff, outcome panel.
- Create reusable capability-action components with idempotency, confirmation, disabled/denied reasons, trace-required indicators, and result-surface hints.
- Add fixture data and contract tests.

## Out of scope

- Production design-system completeness.
- Backend implementation beyond fixture/API contracts.

## Done criteria

- Future frontend examples can compose a workstream UI from reusable components rather than bespoke page screens.
