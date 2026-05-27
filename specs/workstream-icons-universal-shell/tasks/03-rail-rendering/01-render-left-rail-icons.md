# TASK-WSI-03-001: Render workstream icons in left rail

## Goal

Render typed workstream icon descriptors in the left rail for the initial core v0 workstreams.

## Required reads

- `frontend/src/workstream/rail/FunctionalAgentRail.tsx`
- `frontend/src/workstream/rail/FunctionalAgentRailItem.tsx`
- `frontend/src/workstream-shell.contract.test.mjs`
- `frontend/src/styles/components.css`
- `templates/ai-first-saas-starter/frontend/src/workstream/rail/FunctionalAgentRail.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream/rail/FunctionalAgentRailItem.tsx`
- `templates/ai-first-saas-starter/frontend/src/workstream-shell.contract.test.mjs`
- `templates/ai-first-saas-starter/frontend/src/styles/components.css`

## Expected edits

- Replace string-only rail glyph lookup with descriptor-aware rendering.
- Render an icon affordance for User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
- Use descriptor `ariaLabel` and tooltip text through accessible markup; do not rely only on `title`.
- Preserve current selected, hidden, denied, disabled, attention, and unseen-response behavior.
- Keep My Account as the lower-left signed-in user tile, not a normal top rail workstream.
- Add or update contract tests that assert the four required core workstream labels are associated with icon descriptors/rendering affordances.

## Required checks

```bash
cd frontend && npm test -- --run src/workstream-shell.contract.test.mjs
cd frontend && npm run typecheck
cd templates/ai-first-saas-starter/frontend && npm test -- --run src/workstream-shell.contract.test.mjs
cd templates/ai-first-saas-starter/frontend && npm run typecheck
git diff --check
```

## Done criteria

- Reference and starter frontend rail code renders accessible icons for the four top-rail core v0 workstreams.
- Pending task is marked done with a completion note.
- Commit message: `workstream-icons: render left rail icons`.
