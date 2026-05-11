# Inbox Provenance and Disposition Plan

## Purpose

This file inventories the temporary AI-first concept-development markdown files under `skills/inbox/docs/` and records how each should be treated during the AI-first skills-pack migration.

Authoritative doctrine now lives in `docs/ai-first-saas-application-architecture.md`. Inbox files are provenance/source material only until a later cleanup task explicitly promotes, merges, archives, or removes them.

## Canonical destination rule

- Use `docs/ai-first-saas-application-architecture.md` as the canonical architecture doctrine.
- Use future Sprint 2 AI-first skills as concise routing/procedure surfaces, not as competing doctrine.
- Use future Sprint 6 DCA example files as reference examples, not canonical doctrine.
- Do not delete or move inbox files until `TASK-06-005` or another explicit cleanup task applies this plan.

## Inventory

### `skills/inbox/docs/ai-first-saas-coding-agent-framework.md`

- purpose/source role: broad AI-first SaaS conceptual contract; vocabulary for operating loop, human roles, substrate objects, governance, surfaces, architecture contract, conversation rule, skill index, checklist, and glossary.
- disposition: partially promoted.
- promoted/mined into: `docs/ai-first-saas-application-architecture.md`.
- future canonical destinations:
  - concise top-level routing: `skills/ai-first-saas/SKILL.md` in Sprint 2.
  - focused routing/procedure slices: planned object-model, agent-team, governance, decision-card, audit, UI, and outcomes AI-first skills.
- cleanup notes: archive or remove after Sprint 2 skills and any needed doctrine references have been created. Do not preserve broken links in this file to planned skill names unless they are either created or explicitly labeled historical/provenance.

### `skills/inbox/docs/ai-first-saas-ui-patterns.md`

- purpose/source role: UI pattern reference for AI-first supervision, decision, governance, digest, audit, fleet, and objective surfaces, including mappings from reference images to durable backing objects and acceptance criteria.
- disposition: partially mined and deferred.
- promoted/mined into: `docs/ai-first-saas-application-architecture.md` for required product surfaces at doctrine level.
- future canonical destinations:
  - `skills/ai-first-saas-ui-surfaces/SKILL.md` for AI-first UI routing and surface selection.
  - existing web UI guidance and skills for React/Vite/TypeScript implementation details.
  - future DCA example UI files for domain-specific surfaces.
- cleanup notes: keep until Sprint 2 UI-surface guidance and Sprint 6 worked example extract the reusable patterns. Archive remaining image-specific commentary if it is not needed by generated skills.

### `skills/inbox/docs/cai-agent-first-saas-design-framework.md`

- purpose/source role: earlier design-framework source describing role inversion, temporal modes, command center, deviation review, policy/intent editor, async digest, and substrate requirements.
- disposition: partially mined and mostly superseded by canonical doctrine plus the newer coding-agent framework.
- promoted/mined into: `docs/ai-first-saas-application-architecture.md`, mainly through the broader framework files.
- future canonical destinations:
  - `skills/ai-first-saas-ui-surfaces/SKILL.md` for temporal modes and surface archetypes.
  - `skills/ai-first-saas-policy-governance/SKILL.md` for versioned policy and replay concepts.
  - `skills/ai-first-saas-decision-cards/SKILL.md` for deviation review concepts.
- cleanup notes: after focused skills exist, archive or delete as historical provenance. Retain no separate canonical doc named from this file unless there is an explicit reason to preserve a human-narrative design history.

### `skills/inbox/docs/cai-dca-agentic-reconstruction.md`

- purpose/source role: domain-specific DCA reconstruction source applying the agentic SaaS framework to office-device DCA/CRM/ERP consolidation, with supplies fulfillment as recommended initial slice.
- disposition: deferred for worked example.
- promoted/mined into: not yet promoted into a canonical destination beyond high-level doctrine influence.
- future canonical destinations:
  - `docs/examples/agent-first-dca-app-description/` in Sprint 6.
  - possibly Sprint 6 implementation-slice example notes for supplies fulfillment, service dispatch, billing, and customer surfaces.
- cleanup notes: do not treat this as general doctrine. Use it as source material for the DCA worked example, then archive or remove after example files cite or absorb the useful domain-specific material.

### `skills/inbox/docs/oai-agent-first-dca-office-device-lifecycle.md`

- purpose/source role: detailed DCA/office-device lifecycle source covering customer, device, DCA collector, onboarding, monitoring, supplies, service, billing, offboarding, agent-team, UI, decision-card, domain-model, retention, and policy examples.
- disposition: deferred for worked example.
- promoted/mined into: not yet promoted into a canonical destination beyond high-level doctrine influence.
- future canonical destinations:
  - `docs/examples/agent-first-dca-app-description/` in Sprint 6.
  - DCA product vision, operating model, lifecycle, policy, decision-card, UI, audit, outcomes, and implementation-slice example files.
- cleanup notes: this is the main DCA worked-example source. Preserve until Sprint 6 example files intentionally map or quote its lifecycle details, then archive or remove.

### `skills/inbox/docs/oai-agent-first-operating-systems.md`

- purpose/source role: early broad AI-first operating-system thesis covering objective-centered UX, agent teams, decision cards, governance, audit, learning loops, and reference architecture.
- disposition: partially mined and superseded.
- promoted/mined into: `docs/ai-first-saas-application-architecture.md`, mainly as broad doctrine input.
- future canonical destinations:
  - `skills/ai-first-saas/SKILL.md` for intake/routing framing.
  - companion skills for agent teams, governance, decision cards, audit traces, UI surfaces, and outcomes where specific procedure is still useful.
- cleanup notes: likely archive/delete after Sprint 2 skills are created; avoid maintaining it as an additional top-level manifesto.

### `skills/inbox/docs/skills-pack-tech-stack.md`

- purpose/source role: target full-stack substrate statement: Akka backend components plus React/Vite/TypeScript frontend, with event-driven/CQRS design and agent-native implementation guidance.
- disposition: partially promoted.
- promoted/mined into: `docs/ai-first-saas-application-architecture.md` and existing `skills/README.md` routing language.
- future canonical destinations:
  - existing component skill families remain canonical for Akka implementation details.
  - existing web UI skills remain canonical for React/Vite/TypeScript frontend implementation details.
  - top-level AI-first routing should reference the substrate without duplicating component guidance.
- cleanup notes: after Sprint 5 substrate-skill reframing verifies component families cover the same stack message, archive or remove this inbox source.

## Cleanup sequencing guidance

1. Sprint 2 should create the AI-first skill family using this inventory to avoid broken links and duplicated doctrine.
2. Sprint 3 and Sprint 4 should update app-description and planning skills to reference canonical doctrine plus AI-first skills, not inbox files.
3. Sprint 5 should reframe component skills as substrate roles and should not copy long narrative sections from inbox files.
4. Sprint 6 should create the DCA worked example from the two DCA-specific inbox files.
5. `TASK-06-005` should then move, archive, or delete each remaining inbox file according to this disposition plan and update any references.

## Verification checklist

- [x] Every current `skills/inbox/docs/*.md` markdown file is listed.
- [x] `docs/ai-first-saas-application-architecture.md` is the only canonical doctrine destination.
- [x] DCA-specific files are assigned to the worked example, not general doctrine.
- [x] UI-specific material is assigned to future UI-surface guidance and examples, not a competing architecture doc.
- [x] Tech-stack material is assigned to existing substrate/component skills, not a separate competing stack doctrine.
