# Sprint 02: Seed and Starter Reference Runtime Alignment

## Objective

Apply the replacement style and named-theme model to the seed app-description and starter/reference frontend assets.

## Scope

- Seed app-description `55-ui/style-guide.md` and related UI/My Account references.
- Starter/reference frontend CSS tokens and theme application behavior.
- Simple My Account theme selection guidance or implementation, depending on existing starter/reference structure.
- Focused validation that generated/reference UI assets no longer point at the old default style.

## Source context

- `docs/examples/ai-first-saas-seed-app-description/app-description/55-ui/style-guide.md`
- seed app-description My Account/settings files identified by search
- `frontend/src/styles/*.css`
- `templates/ai-first-saas-starter/frontend/src/styles/*.css`
- `frontend/src/main.tsx`
- `templates/ai-first-saas-starter/frontend/src/main.tsx`
- My Account workstream frontend components identified by search

## Ordered work areas

1. Update seed app-description UI/theme contract.
2. Refresh CSS tokens and component styling around the replacement style and named themes.
3. Wire or document simple My Account named theme selection through the reference/starter UI path.
4. Run frontend/doc checks required by the bounded tasks.

## Acceptance criteria

- Seed app-description records the replacement style and four named themes.
- Reference/starter CSS exposes named theme token bundles rather than only mode bundles.
- User-facing My Account theme selection is modeled as selecting one available theme.
- Theme selection does not bypass backend/user-settings requirements if durable runtime persistence is in scope.
- Required frontend checks pass or a bounded follow-up task is appended.

## Handoff notes

If the real My Account settings persistence path is larger than one task, split it. Do not mark durable theme preference complete with only a mock/demo/local-only path unless the task is explicitly narrowed to visual-token reference behavior.
