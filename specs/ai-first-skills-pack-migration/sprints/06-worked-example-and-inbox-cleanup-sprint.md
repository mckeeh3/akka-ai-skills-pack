# Sprint 6: Worked Example and Inbox Cleanup

## Sprint goal

Convert the temporary DCA/office-device concept material into a canonical AI-first worked example and retire or archive temporary inbox concept files.

## Dependencies

- Sprint 1 doctrine complete.
- Sprint 2 AI-first routing skill family available.
- Sprint 3 app-description shape updated.
- Sprint 4 planning output expectations updated.

## Scope

- Create a worked example app-description/spec tree for an AI-first DCA and office-device lifecycle application.
- Use the example to demonstrate goals, agent teams, policies, decisions, traces, outcomes, UI surfaces, and Akka substrate mapping.
- Decide final disposition for `skills/inbox/docs/*` files.

## Candidate example location

```text
docs/examples/agent-first-dca-app-description/
```

Suggested files:

- `README.md`
- `00-product-vision.md`
- `10-agentic-operating-model.md`
- `20-agent-team.md`
- `30-policies-approval-gates.md`
- `40-workflows-and-decisions.md`
- `50-ui-surfaces.md`
- `60-audit-trace-outcomes.md`
- `70-implementation-slices.md`

## Primary reference inputs

- `skills/inbox/docs/cai-dca-agentic-reconstruction.md`
- `skills/inbox/docs/oai-agent-first-dca-office-device-lifecycle.md`
- `skills/inbox/docs/oai-agent-first-operating-systems.md`
- `skills/inbox/docs/cai-agent-first-saas-design-framework.md`
- canonical docs and skills created in earlier sprints

## Acceptance behavior

- The example is agent-readable and demonstrates the new default architecture concretely.
- The example does not become the business source of truth for this repository.
- Inbox files are either promoted, merged, archived, or deleted by explicit documented action.

## Done criteria

- Worked example exists and is referenced from relevant docs/skills.
- Temporary inbox material no longer creates competing authoritative concepts.
- Any remaining inbox files have an explicit reason to remain.
