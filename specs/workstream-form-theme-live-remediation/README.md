# Workstream Form Styling and Live Theme Remediation

## Purpose

Fix the unacceptable user-settings surface UI shown in `user-settings-surface.png` and harden the web UI style guidance so generated workstream forms never render with default/native-looking controls again.

This is a runtime/reference UI remediation project, not just documentation polish.

## Trigger

The reviewed screenshot shows a My Account settings `detail-edit` surface with:

- native/default-looking `<select>` and `<input>` controls;
- cramped labels and fields with no layout rhythm;
- fields not visually integrated into the `ai-first-workstream-enterprise` style system;
- no polished named-theme selection affordance;
- theme selection not applying immediately when the user changes the theme field.

The user explicitly judged this UI unacceptable.

## Scope

In scope:

- Style `detail-edit` / user-settings surface form fields in the reference frontend and starter template.
- Ensure select/input/textarea controls inside structured workstream surfaces use the same enterprise form styling as design-system forms.
- Make theme selection apply immediately when the `preferredThemeId` field changes, before Save/Confirm.
- Preserve the backend save action for durable persistence; immediate visual theme application is an optimistic local preview of the selected theme, not an authorization or persistence shortcut.
- Add frontend tests/checks that catch unstyled structured-surface controls and live theme application.
- Update `docs/web-ui-style-guide.md` and relevant docs/skills if needed so the canonical guide explicitly requires styled structured-surface forms and live named-theme preview.

## Non-goals

- Do not redesign backend settings persistence beyond what is needed to keep theme save truthful.
- Do not create a generic style gallery.
- Do not change capability authorization or audit semantics.
- Do not mark durable theme persistence complete unless the actual backend action path is used.

## Affected areas

Likely affected:

- `frontend/src/workstream/surfaces/DetailEditSurface.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/surfaces/DetailEditSurface.tsx`
- `frontend/src/styles/components.css`
- `templates/ai-first-saas-starter/frontend/src/styles/components.css`
- frontend tests/fixtures under `frontend/src/__tests__/**`
- starter template tests/fixtures under `templates/ai-first-saas-starter/frontend/src/__tests__/**`
- `docs/web-ui-style-guide.md`
- focused web UI style/UX/testing skills if guidance is missing

## Execution model

- Execute one task per fresh harness session.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Read this README, conversation capture, selected sprint, selected backlog, selected queue entry, and selected task brief before editing.
- Each task must update `pending-tasks.md`, run required checks, and make one focused commit before being marked done.

## Sprint sequence

1. `sprints/01-runtime-remediation-sprint.md` — fix structured-surface form styling and immediate named-theme application in reference/starter frontend.
2. `sprints/02-style-guide-hardening-sprint.md` — harden docs/skills so future generated UIs cannot regress to default native controls or delayed theme application.
3. `sprints/99-verification-sprint.md` — verify runtime/docs completion and append follow-up tasks if gaps remain.

## Done state

Complete when:

- My Account settings/detail-edit surface controls are visibly styled by the enterprise token system in both reference frontend and starter template.
- The `preferredThemeId` select applies the selected theme immediately on change.
- Save/confirm still uses the backend action path for durable preference persistence.
- Tests or source checks prove the structured-surface form uses styled classes/tokens and live theme application behavior.
- Style guide/guidance explicitly requires structured-surface form controls to be designed components, not browser-default controls.
- Verification finds no material gap that would allow a generated workstream settings surface like `user-settings-surface.png` to be considered acceptable.
