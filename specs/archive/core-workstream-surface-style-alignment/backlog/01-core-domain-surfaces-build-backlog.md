# Backlog: Core Domain Workstream Surface Style Alignment

## Goal

Patch the core app domain workstream reference docs so all required surfaces inherit the replacement style and named-theme model.

## Implementation notes

- Keep edits concise and reference `docs/web-ui-style-guide.md` for full rules.
- Use surface-specific language such as KPI strip, attention queue, detail/recommendation panel, audit timeline, diff review, decision card, and system-message card where appropriate.
- Preserve governed capability mappings and backend authorization semantics.

## Suggested harness task breakdown

1. Update core domain overview and My Account theme/settings surfaces.
2. Update User Admin and Agent Admin workstream surface docs.
3. Update Audit/Trace and Governance/Policy workstream surface docs.

## Dependencies

- Planning scaffold.

## Required checks

- `git diff --check`
- Focused search for stale style/theme language in `docs/examples/ai-first-saas-core-app-domain/**`.

## Acceptance criteria

- All core domain workstream README files are aligned with named-theme style guidance.
- My Account settings clearly use available named themes and `preferredThemeId` semantics.
- Surface appearance guidance is specific enough for future generation without duplicating the canonical style guide.
