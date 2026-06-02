# Conversation Capture: Workstream Form Styling and Live Theme Remediation

## User complaint

The user reviewed `user-settings-surface.png` and said the UI is horrible and unacceptable. Specific concerns:

- input fields look default/unstyled;
- the web UI style guide still needs substantial work;
- selecting a theme should change the UI to that theme immediately;
- immediate theme change is not happening.

## Findings from screenshot and quick source inspection

- The My Account settings surface is rendered through the structured `detail-edit` surface path.
- `DetailEditSurface.tsx` renders native `<select>` and `<input>` controls inside `.surface-detail-field`.
- Existing CSS styles `.form-field input/select/textarea` for design-system forms, but there is no equivalent styling for `.surface-detail-field input/select/textarea` in the structured-surface form path.
- This explains why the screenshot shows native/default-looking controls.
- Current theme code applies `data-theme` from `themeId`, and action handling updates `themeId` after successful action results when `preferredThemeId` is present in the submitted input. That means changing the select alone does not immediately preview the selected theme.

## Decisions

- This must be fixed as runtime/reference UI remediation, not merely documentation.
- Theme selection should apply immediately on field change as a local UI preview.
- Durable preference persistence still belongs to the Save/Confirm backend action path.
- Structured-surface form controls must be styled through the same tokenized enterprise system as other forms.

## Constraints

- Preserve backend authorization and audit semantics.
- Do not fake durable persistence.
- Keep changes mirrored between `frontend/` and `templates/ai-first-saas-starter/frontend/`.
- Add checks/tests so this class of regression is caught.

## Risks

- Immediate preview could be confused with saved persistence unless UI copy/behavior remains clear.
- If only the screenshot path is fixed, other structured-surface form controls may remain default-looking.
- If only CSS is changed, the immediate-theme bug remains.
