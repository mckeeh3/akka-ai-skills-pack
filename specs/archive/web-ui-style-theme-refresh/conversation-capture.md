# Conversation Capture: Web UI Style and Theme Refresh

## User goals

- Improve the generated web UI style significantly, especially for workstream surface appearance.
- Use `web-ui-high-level-style-guide.md` as the reference for general style, look, and appearance.
- Treat the style refresh as a durable mini-project with pending tasks.
- Replace the current default style rather than adding another optional style choice.
- Start theme support with named themes rather than a user-facing `system/light/dark` mode selector.
- Provide four initial named themes: two light themes and two dark themes.
- Keep theme selection simple in the My Account workstream: select one available theme and have the UI change to that theme.

## Decisions already made

- The high-level guide's visual direction is the desired replacement default for generated workstream UI surfaces.
- The old warm orange/coral Atlas Ops style should not remain the default generated AI-first SaaS style.
- The repository should model themes as named token bundles that can grow over time.
- Initial theme count is four: two light and two dark.
- Theme variation should mainly cover background colors plus variations on accent and semantic colors.
- My Account should expose a simple available-theme selection interaction.

## Accepted constraints

- Preserve the secure AI-first SaaS and workstream application architecture.
- Style remains a visual realization layer; it must not redefine functional agents, surfaces, capability mappings, authorization, audit, routes, API contracts, or readiness semantics.
- Theme selection is user preference behavior, not authorization.
- Color must remain functional and accessible; status cannot rely on color alone.
- Generated apps should continue to avoid generic dashboard/CRM/project-management style galleries.
- Future work should be split into one-task-per-fresh-context queue items.

## Rejected alternatives / non-goals

- Do not start with `system`, `light`, and `dark` as the primary user-facing theme model.
- Do not keep the old orange/coral default as a peer default style.
- Do not create many generic visual style options.
- Do not treat theme support as page-first UI work outside the workstream shell and My Account preference flow.

## Risks

- Stale references to the old style may remain across docs, skills, examples, and starter CSS.
- A docs-only update could leave starter/reference frontend behavior inconsistent with the new guidance.
- A frontend-only update could leave generated-app planning still saying `system/light/dark` or old default style.
- If theme persistence is not checked carefully, future tasks might claim My Account selection works when it only changes a local frontend class temporarily.
- Brand/theme customization guidance could accidentally reopen generic style-gallery behavior unless tightly scoped.

## Unresolved questions

No blocking questions are currently required to create this mini-project. Future implementation tasks may choose exact names for the four initial themes when updating canonical docs and frontend tokens.

If backend-persisted user settings are missing or outside scope when implementing My Account theme selection, that task should either:

- implement the real scoped persistence path required by the starter/reference app, or
- append a bounded follow-up task and avoid claiming durable runtime completion.
