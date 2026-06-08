# Web UI Style and Theme Refresh Mini-Project

## Purpose

Replace the current generated web UI visual default with the style direction captured in `web-ui-high-level-style-guide.md`, and make named themes a first-class part of generated workstream UI guidance and starter/reference implementation.

The refresh is focused on **workstream surface style and appearance**: functional-agent rail presentation, workstream panels, KPI strips, attention queues, decision/recommendation cards, agent timelines, governance/trust controls, badges, cards, typography, color tokens, and simple theme selection through the My Account workstream.

## Background and trigger

The current default style in `docs/web-ui-style-guide.md`, the starter core app-description style guide, and the starter/frontend tokens still leans toward a warm orange/coral operational cockpit. The reviewed high-level guide describes a calmer enterprise data-product UI with neutral layered surfaces, sparse functional color, blue/indigo accent, semantic status colors, strong numerical hierarchy, and reusable workstream-surface patterns.

The user confirmed:

- this should be handled as a durable mini-project rather than a one-off edit;
- the new style is a **replacement**, not an alternate gallery choice;
- the theme model should start with **named themes**, not `system/light/dark` mode as the primary user concept;
- initial scope should include **four themes**: two light and two dark;
- the main theme variations are background colors plus accent and semantic color variations;
- My Account theme selection should be simple: users choose one available named theme and the UI changes to that theme.

## Scope

In scope:

- Replace the canonical generated AI-first SaaS web UI visual direction with the high-level style guide.
- Define a named-theme model suitable for future theme additions.
- Start with four named themes: two light and two dark.
- Update docs and skills so generated UI work reads style/theme guidance before implementation.
- Update starter core app-description UI artifacts to reference named themes and the replacement style.
- Update reference/starter frontend theme tokens and theme application behavior where appropriate.
- Add or update checks/review guidance to prevent regressions to generic dashboard, orange/coral old-default, or page-first UI styling.

## Non-goals

- Do not create a generic style gallery of dashboard/CRM/admin skins.
- Do not weaken AI-first workstream architecture, capability-backed actions, authorization, audit, accessibility, or runtime completion doctrine.
- Do not make frontend theme state an authorization control.
- Do not require provider/auth/runtime behavior changes except where the starter/reference My Account theme selection path already owns browser preference behavior.
- Do not add this project-only mini-project or its planning skill to installable pack manifests.

## Affected repository areas

Likely affected:

- `web-ui-high-level-style-guide.md`
- `docs/web-ui-style-guide.md`
- `docs/web-ui-ux-patterns.md`
- `docs/web-ui-quality-checklist.md`
- `skills/app-description-ui/SKILL.md`
- `skills/akka-web-ui-apps/SKILL.md`
- focused web UI skills that mention mode/style selection
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/style-guide.md`
- starter core app-description My Account/settings references where theme selection is described
- `frontend/src/styles/*.css`
- `templates/ai-first-saas-starter/frontend/src/styles/*.css`
- reference/starter frontend theme preference logic if present

Future task sessions should inspect the exact affected files before editing.

## Execution model

- Execute one task per fresh harness session.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Read this README, `conversation-capture.md`, the selected sprint, matching backlog, selected queue entry, and task brief before editing.
- Each task must update `pending-tasks.md` before completion.
- Each task must run required checks or document why a check could not run.
- Each task must make one focused git commit before being marked `done`.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `specs/web-ui-style-theme-refresh/README.md`
4. `specs/web-ui-style-theme-refresh/conversation-capture.md`
5. `specs/web-ui-style-theme-refresh/pending-tasks.md`
6. selected sprint file
7. selected backlog file
8. selected task brief
9. only the repository docs/skills/source files named by the selected task

## Sprint sequence

1. `sprints/01-style-doctrine-sprint.md` — replace canonical style/theme doctrine and align web UI skills around named themes.
2. `sprints/02-reference-runtime-sprint.md` — update starter core app-description and starter/reference frontend assets to use the replacement style and named-theme selection.
3. `sprints/99-verification-sprint.md` — verify consistency, checks, and mini-project done state; append follow-up tasks if gaps remain.
4. `sprints/03-remaining-guidance-sprint.md` — align remaining foundation and pending-question guidance discovered by verification.
5. `sprints/99-verification-sprint.md` — re-verify completion after the follow-up task group.

## Done state

This mini-project is complete when:

- `docs/web-ui-style-guide.md` reflects the high-level style guide as the replacement canonical AI-first workstream UI style.
- The canonical style uses a named-theme model with four initial themes, two light and two dark.
- Theme guidance makes user selection simple: choose one available named theme in My Account and apply it to the UI.
- Theme variation guidance is token-based and future-extensible without requiring a new style system.
- Starter core app-description UI artifacts use the replacement style and named-theme model.
- Relevant web UI skills and quality checks prevent implicit style invention, generic dashboard/gallery fallback, and stale old-default orange/coral assumptions.
- Starter/reference frontend tokens and theme application behavior are aligned with the replacement style at the scoped runtime/reference level.
- Verification finds no material stale guidance that would cause generated workstream surfaces to revert to the old default or to mode-first theme selection.

## Open concerns and recommendations

- Future task sessions should decide theme names while editing the canonical style guide. Candidate names should be product-neutral and should not copy demo product names.
- If My Account theme persistence requires backend user settings beyond the selected task's scope, the task should document the runtime limitation and queue a bounded follow-up instead of faking durable completion.
- The existing `web-ui-high-level-style-guide.md` is a source reference for this mini-project. If the canonical docs fully absorb it later, verification may recommend either keeping it as provenance or moving its content under `docs/`.
