# Sprint 01: Style Doctrine and Named Theme Guidance

## Objective

Replace the canonical generated web UI style direction with the high-level workstream surface style and align repository guidance around named themes.

## Scope

- Canonical web UI style guide update.
- Named theme model with four initial themes: two light and two dark.
- Web UI skill and planning guidance updates where they mention style selection, mode policy, or old default assumptions.
- Quality/checklist updates that help future agents catch stale style behavior.

## Source context

- `web-ui-high-level-style-guide.md`
- `docs/web-ui-style-guide.md`
- `docs/web-ui-ux-patterns.md`
- `docs/web-ui-quality-checklist.md`
- `skills/app-description-ui/SKILL.md`
- `skills/akka-web-ui-apps/SKILL.md`
- other focused web UI skills only as needed by search results

## Ordered work areas

1. Replace the canonical visual direction and token/theme template.
2. Update guidance to say named theme selection is the user-facing model.
3. Align style-selection questions/templates and app-description fields to record selected style plus selected/available themes.
4. Update web UI implementation/review guidance to consume named theme tokens and reject old default assumptions.

## Acceptance criteria

- Canonical docs clearly describe the replacement style from the high-level guide.
- Named themes are first-class and future-extensible.
- Four initial themes are specified or required by the canonical docs.
- Style/theme variation is tokenized and does not change workstream architecture.
- Web UI skills route implementation through the selected style and theme model.
- Search/checks show no obvious stale default guidance requiring `system/light/dark` as the primary user-facing selection model.

## Handoff notes

Sprint 02 should not start until canonical docs and skills define the named theme contract clearly enough for seed/starter assets to implement without guessing.
