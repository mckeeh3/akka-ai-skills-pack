# Stale Content Pass 01

## Scope

Task: `TASK-RIAC-05-001`.

Reviewed active guidance under `skills`, `docs`, `pack`, and `templates` for stale requirements-intake language that could route generated SaaS work toward User-Admin-only minimum starters, generic chatbots, CRUD/page/resource-first planning, stale examples, or component-only mechanics.

## Commands run

```bash
rg -n "User Admin workstream v0|User Admin-only|generic chatbot|chatbot shell|page tree|screen tree|page-first|screen-first|CRUD-first|CRUD screen|CRUD module|resource-first|/api/<resource>|nav bar|navigation bar|search page|help page|list/detail|list/detail/edit|user-list|user-edit|frontend/src/screens|shopping-cart|purchase-request" skills docs pack templates --glob '!docs/examples/purchase-request-app-description/**'
```

Initial result: 322 hits. Major classes were:

- canonical/guardrail anti-pattern wording in active doctrine and skills;
- AI-first starter core app surface ids such as `user-admin-user-list`, which are current structured-surface identifiers;
- explicit mechanics-only purchase-request references;
- ShoppingCart component mechanics examples;
- stale minimum-starter wording that still said `User Admin workstream v0` or User-Admin-only.

```bash
rg -n "User Admin workstream v0|User Admin-only" skills docs pack templates --glob '!docs/examples/purchase-request-app-description/**'
```

Initial result: 10 hits in active docs/skills/examples/templates. Final result after this task: no matches.

```bash
rg -n "User Admin workstream v0|User Admin-only|generic chatbot|chatbot shell|page tree|screen tree|page-first|screen-first|CRUD-first|CRUD screen|CRUD module|resource-first|/api/<resource>|nav bar|navigation bar|search page|help page|list/detail|list/detail/edit|user-list|user-edit|frontend/src/screens|shopping-cart|purchase-request" \
  skills/core-saas-foundation/SKILL.md \
  docs/core-ai-first-saas-foundation.md \
  docs/structured-surface-contracts.md \
  docs/app-description-maintenance-flow.md \
  docs/internal-app-description-architecture.md \
  skills/app-description-functional-agent-modeling/SKILL.md \
  skills/app-description-surface-modeling/SKILL.md \
  docs/examples/core-ai-first-saas-input/01-core-seed-progression-plan.md \
  docs/examples/ai-first-saas-core-app-domain/user-admin-workstream/README.md \
  templates/ai-first-saas-starter/README.md \
  skills/akka-web-ui-api-client/SKILL.md \
  skills/akka-consumer-from-topic/SKILL.md \
  skills/akka-consumer-producing/SKILL.md \
  skills/akka-consumer-from-event-sourced-entity/SKILL.md
```

Final edited-file result: remaining hits were reviewed as intentional anti-pattern wording, mechanics-only example names, current structured-surface ids, or capability-derived API route guardrails.

## Bounded batch resolved

### Minimum starter wording

Replaced the remaining active `User Admin workstream v0` / User-Admin-only minimum-starter wording with the current five core workstream v0 starter language in:

- `skills/core-saas-foundation/SKILL.md`
- `docs/core-ai-first-saas-foundation.md`
- `docs/structured-surface-contracts.md`
- `docs/app-description-maintenance-flow.md`
- `docs/internal-app-description-architecture.md`
- `skills/app-description-functional-agent-modeling/SKILL.md`
- `skills/app-description-surface-modeling/SKILL.md`
- `docs/examples/core-ai-first-saas-input/01-core-seed-progression-plan.md`
- `docs/examples/ai-first-saas-core-app-domain/user-admin-workstream/README.md`
- `templates/ai-first-saas-starter/README.md`

The aligned phrasing now treats minimum/starter/basic/chatbot-like generated SaaS requests as the five core workstream v0 starter: My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy, with `markdown_response` surfaces and explicit full-core follow-up gaps.

### Resource/API and component mechanics

Updated `skills/akka-web-ui-api-client/SKILL.md` so browser API route guidance derives from capability and structured-surface contracts instead of suggesting generic `/api/<resource>/...` routes as a primary decomposition.

Labeled ShoppingCart consumer examples as Akka substrate mechanics, not generated-product architecture templates, in:

- `skills/akka-consumer-from-topic/SKILL.md`
- `skills/akka-consumer-producing/SKILL.md`
- `skills/akka-consumer-from-event-sourced-entity/SKILL.md`

## Remaining hit disposition

No follow-up task was appended from this pass because the resolved batch removed the concrete stale minimum-starter contradiction and quarantined the small component/API mechanics batch found during inspection.

Remaining broad stale-term hits appear to fall into these categories for the terminal verification/pass-02 review:

- current starter-core-app structured surface ids and User Admin vertical references, especially `user-admin-user-list`;
- explicit anti-pattern/guardrail wording such as `page-first`, `CRUD-first`, or `generic chatbot`;
- mechanics-only purchase-request planning/example links;
- Akka substrate examples and starter-template test fixture names;
- docs or specs that already describe legacy wording as non-canonical.

Terminal verification should still re-run broad stale-term and reference checks before closing the mini-project.
