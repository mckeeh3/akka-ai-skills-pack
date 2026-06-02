# Core Workstream Surface Style Alignment Mini-Project

## Purpose

Align the existing core app workstream surface reference docs with the replacement `ai-first-workstream-enterprise` web UI style and named-theme model completed by `specs/web-ui-style-theme-refresh/`.

This mini-project focuses on the **core app workstream surface documentation layer**, especially required surfaces for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy. It ensures future generated apps and planning tasks do not treat these core surfaces as generic tables/dashboards, stale old-style UI, or theme-agnostic placeholders.

## Background and trigger

After completing `specs/web-ui-style-theme-refresh/`, the user asked whether all existing core app workstream surfaces had been updated. A quick review found the canonical style/theme docs, seed style guide, starter/reference tokens, and My Account seed surface contract were updated, but the broader core app workstream domain docs under `docs/examples/ai-first-saas-core-app-domain/` were not fully aligned with the new style/theme contract or surface appearance guidance.

## Scope

In scope:

- Update the core app domain overview and five core workstream README files:
  - `docs/examples/ai-first-saas-core-app-domain/README.md`
  - `docs/examples/ai-first-saas-core-app-domain/my-account-workstream/README.md`
  - `docs/examples/ai-first-saas-core-app-domain/user-admin-workstream/README.md`
  - `docs/examples/ai-first-saas-core-app-domain/agent-admin-workstream/README.md`
  - `docs/examples/ai-first-saas-core-app-domain/audit-trace-workstream/README.md`
  - `docs/examples/ai-first-saas-core-app-domain/governance-policy-workstream/README.md`
- Align required surface descriptions with `docs/web-ui-style-guide.md` and named themes.
- Make My Account settings explicitly use `preferredThemeId` / available named theme selection instead of generic theme/mode wording.
- Add appearance/style expectations for core surface families: dashboard/KPI strips, attention queues, decision cards, recommendation/evidence panels, timelines, diff reviews, governance/trust controls, lists/tables, system-message surfaces, and audit trace surfaces.
- Preserve surface/capability/workstream semantics and keep visual style in the UI realization layer.

## Non-goals

- Do not edit React/Vite frontend code or CSS unless a later verification task explicitly appends a bounded follow-up.
- Do not change backend capabilities, authorization rules, APIs, routes, or Akka component plans.
- Do not introduce generic style-gallery choices.
- Do not duplicate the entire canonical style guide in each workstream file; link to it and add only surface-specific style guidance.
- Do not treat style/theme selection as authorization.

## Affected repository areas

Primary:

- `docs/examples/ai-first-saas-core-app-domain/**`
- `docs/web-ui-style-guide.md` only as a reference, not expected to change unless verification finds a small clarification gap.

Secondary verification targets:

- `templates/ai-first-saas-starter/app-description/app-description/12-workstreams/surface-contracts/**`
- `templates/ai-first-saas-starter/app-description/app-description/55-ui/style-guide.md`
- `specs/web-ui-style-theme-refresh/verification-notes.md`

## Execution model

- Execute one task per fresh harness session.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Read this README, `conversation-capture.md`, the selected sprint, selected backlog, selected queue entry, and task brief before editing.
- Each task must update `pending-tasks.md` before completion.
- Each task must run required checks or document why a check could not run.
- Each task must make one focused git commit before being marked `done`.

## Read order for future task sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `specs/core-workstream-surface-style-alignment/README.md`
4. `specs/core-workstream-surface-style-alignment/conversation-capture.md`
5. `specs/core-workstream-surface-style-alignment/pending-tasks.md`
6. selected sprint file
7. selected backlog file
8. selected task brief
9. `docs/web-ui-style-guide.md`
10. only the core workstream files named by the selected task

## Sprint sequence

1. `sprints/01-core-domain-surfaces-sprint.md` — update core app domain overview and five workstream README surface/style contracts.
2. `sprints/02-seed-surface-style-sprint.md` — review starter core app-description surface contracts against the same surface-style expectations and patch any gaps found.
3. `sprints/99-verification-sprint.md` — verify completion and append follow-up tasks if material gaps remain.

## Done state

This mini-project is complete when:

- the core app domain overview names `ai-first-workstream-enterprise` and the four named themes as the style/theme contract for core workstream surfaces;
- My Account settings surface docs specify named theme selection and `preferredThemeId` semantics;
- User Admin, Agent Admin, Audit/Trace, and Governance/Policy surface docs include concise appearance/style expectations tied to the canonical style guide;
- dashboard, list/search, detail/edit, decision, audit timeline, governance diff, outcome, notification, and system-message surface families are described as enterprise workstream surfaces, not generic dashboards/tables/mockups;
- no active core app domain doc still implies mode-first `light/dark/system`, `uiMode`, `preferredColorMode`, obsolete Atlas style ids, or old orange/coral style defaults;
- verification records no material mismatch between core domain surface docs, starter core app-description surface contracts, and `docs/web-ui-style-guide.md`.

## Open concerns and recommendations

- The first sprint should keep edits concise. The canonical style guide owns full token/component rules; core workstream docs should add surface-specific style expectations and references.
- If verification finds substantial starter core app-description gaps, append bounded follow-up tasks rather than widening an active task.
