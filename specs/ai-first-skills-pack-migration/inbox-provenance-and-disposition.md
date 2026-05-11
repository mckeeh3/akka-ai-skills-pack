# Inbox Provenance and Final Disposition

## Purpose

This file inventories the temporary AI-first concept-development material that originally lived under `skills/inbox/` and records its final disposition from `TASK-06-005`.

Authoritative guidance now lives in:
- `docs/ai-first-saas-application-architecture.md` for doctrine
- `skills/ai-first-saas*/SKILL.md` for AI-first routing and companion procedures
- `docs/examples/agent-first-dca-app-description/` for the worked DCA reference example
- existing Akka component and web UI skill families for implementation details

The former inbox material has been archived under `specs/ai-first-skills-pack-migration/archive/inbox/` for provenance only. It is not operative routing, doctrine, or implementation guidance.

## Final disposition summary

- `skills/inbox/` was removed from active skill-pack source.
- Markdown concept docs, UI reference images, and draft inbox skills were moved to `specs/ai-first-skills-pack-migration/archive/inbox/`.
- `.DS_Store` was deleted as non-source noise.
- Repository guidance now points to canonical docs/skills and treats archived inbox files as provenance only.

## Archived docs inventory

### `specs/ai-first-skills-pack-migration/archive/inbox/docs/ai-first-saas-coding-agent-framework.md`

- source role: broad AI-first SaaS conceptual contract; vocabulary for operating loop, human roles, substrate objects, governance, surfaces, architecture contract, conversation rule, skill index, checklist, and glossary.
- final disposition: archived for provenance.
- promoted/mined into: `docs/ai-first-saas-application-architecture.md`, `skills/ai-first-saas/SKILL.md`, and focused AI-first companion skills.

### `specs/ai-first-skills-pack-migration/archive/inbox/docs/ai-first-saas-ui-patterns.md`

- source role: UI pattern reference for AI-first supervision, decision, governance, digest, audit, fleet, and objective surfaces, including image mappings and acceptance criteria.
- final disposition: archived for provenance with its `images/` assets preserved.
- promoted/mined into: doctrine surface list, `skills/ai-first-saas-ui-surfaces/SKILL.md`, web UI routing guidance, and DCA worked-example UI files.

### `specs/ai-first-skills-pack-migration/archive/inbox/docs/cai-agent-first-saas-design-framework.md`

- source role: earlier design-framework source describing role inversion, temporal modes, command center, deviation review, policy/intent editor, async digest, and substrate requirements.
- final disposition: archived for provenance.
- promoted/mined into: canonical doctrine and companion skills for UI surfaces, policy governance, and decision cards.

### `specs/ai-first-skills-pack-migration/archive/inbox/docs/cai-dca-agentic-reconstruction.md`

- source role: domain-specific DCA reconstruction source for office-device DCA/CRM/ERP consolidation, with supplies fulfillment as recommended initial slice.
- final disposition: archived for provenance.
- promoted/mined into: `docs/examples/agent-first-dca-app-description/`.

### `specs/ai-first-skills-pack-migration/archive/inbox/docs/oai-agent-first-dca-office-device-lifecycle.md`

- source role: detailed DCA/office-device lifecycle source covering customer, device, collector, onboarding, monitoring, supplies, service, billing, offboarding, agent team, UI, decision-card, domain model, retention, and policy examples.
- final disposition: archived for provenance.
- promoted/mined into: DCA product vision, operating model, lifecycle, policy, decision-card, UI, audit, outcome, and implementation-slice example files.

### `specs/ai-first-skills-pack-migration/archive/inbox/docs/oai-agent-first-operating-systems.md`

- source role: early AI-first operating-system thesis covering objective-centered UX, agent teams, decision cards, governance, audit, learning loops, and reference architecture.
- final disposition: archived for provenance.
- promoted/mined into: canonical doctrine and AI-first routing/companion skills.

### `specs/ai-first-skills-pack-migration/archive/inbox/docs/skills-pack-tech-stack.md`

- source role: target full-stack substrate statement: Akka backend components plus React/Vite/TypeScript frontend, with event-driven/CQRS design and agent-native implementation guidance.
- final disposition: archived for provenance.
- promoted/mined into: canonical doctrine, `skills/README.md`, substrate skill reframing, and existing component/web UI skill families.

## Archived draft skills

Draft skill files originally under `skills/inbox/skills/` are archived under `specs/ai-first-skills-pack-migration/archive/inbox/skills/` for provenance only. They are not part of the active skill routing surface. Active AI-first guidance is under `skills/ai-first-saas*/`.

## Verification checklist

- [x] Every former `skills/inbox/docs/*.md` markdown file is archived and listed.
- [x] Former UI images are preserved with the archived UI source doc.
- [x] Former draft inbox skills are archived outside active `skills/` routing.
- [x] `skills/inbox/` no longer exists as active temporary concept material.
- [x] `docs/ai-first-saas-application-architecture.md` is the only canonical doctrine destination.
- [x] DCA-specific files are assigned to the worked example, not general doctrine.
- [x] UI-specific material is represented by active AI-first UI-surface and web UI guidance, not a competing architecture doc.
- [x] Tech-stack material is represented by existing substrate/component skills, not a separate competing stack doctrine.
