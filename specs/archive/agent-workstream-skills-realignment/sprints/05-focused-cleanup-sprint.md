# Sprint 05: Focused Realignment Cleanup

## Objective

Close the remaining practical alignment gaps found after the broad agent-workstream realignment:

1. installed `.agents/` output is stale relative to source skills;
2. `core-saas-foundation` is still too foundation-object-first and should route through foundation functional agents and surfaces;
3. several focused implementation skills still lack the standard generated SaaS input-contract gate;
4. terminology conflates structured workstream surfaces with backend/API/tool exposure surfaces;
5. AI-first companion skills do not consistently hand off workstream surfaces/actions;
6. source skill relative paths are fragile or inconsistent;
7. starter acceptance docs do not acknowledge the later Sprint 08 workstream-first follow-up queue.

## Scope

This is a targeted cleanup sprint, not a broad rewrite. Each task should make the smallest source changes needed to prevent mechanics-first or stale installed-pack behavior during upcoming starter implementation.

Likely touched areas:

- `install.sh` output only for validation; do not commit `.agents/`.
- `skills/core-saas-foundation/SKILL.md`
- focused skills listed in `tasks/05-focused-cleanup/03-add-input-contract-to-remaining-focused-skills.md`
- AI-first companion skills listed in `tasks/05-focused-cleanup/05-align-ai-first-companion-surface-handoffs.md`
- top-level terminology docs/skills where `surface` ambiguity appears
- source skill path references discovered by validation
- `specs/ai-first-saas-starter-app-template/final-acceptance-review.md` or related summary docs

## Deliverables

- Installed-pack dogfood output is refreshed and spot-checked.
- Core SaaS foundation explicitly names foundation functional agents/surfaces and routes through `agent-workstream-apps`.
- Remaining high-use focused implementation skills include the generated SaaS input-contract gate or an equivalent explicit deferral rule.
- Terminology distinguishes structured workstream surfaces from capability exposure channels/paths.
- AI-first companion outputs are surface/action/capability-ready.
- Source skill path references have a validation report and the highest-impact broken references are fixed or queued.
- Starter acceptance docs are consistent with the Sprint 08 queue.
- Sprint review decides whether realignment is fully closed or creates a targeted Sprint 06.

## Checks

- `git diff --check`
- relevant `rg` checks described in each task
- installed/source spot-check after reinstall where requested
