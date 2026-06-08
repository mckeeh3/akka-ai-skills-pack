# Legacy Content Inventory

Task: `TASK-AW-05-001`
Date: 2026-05-19

## Scope

Searched current repository guidance that can be exposed by the skills pack for page-first, CRUD-first, admin-console-first, static-dashboard, and chatbot-bolt-on drift.

Included:
- `docs/**`
- `skills/**`
- `pack/**`
- top-level `README.md`

Excluded:
- `specs/**` migration/planning artifacts, except this inventory
- `akka-context/**` official upstream reference material

## Required search

Primary command run:

```bash
rg -n -i "page-first|CRUD-first|crud first|chatbot|chat bot|bolt-on|admin console|admin-console|screen hierarchy|screen-first|page hierarchy|page tree|default screen|primary screen|primary screens|traditional app|conventional page|conventional route|static dashboard|dashboard with chat|chat panel|optional AI|optional.*chat|screens-and-navigation" docs skills pack README.md --glob '!specs/**' --glob '!akka-context/**'
```

Result: 65 matches. Most are now anti-drift warnings or explicit workstream-default guidance.

## Actionable cleanup inventory

These entries are the remaining places most likely to teach or preserve legacy UI structure if read without the newer workstream context. They should be handled by `TASK-AW-05-002` unless split into more focused cleanup work later.

| Priority | Location | Drift signal | Why it matters | Recommended cleanup |
|---|---|---|---|---|
| High | `skills/app-description-ui/SKILL.md:3` | Skill description still leads with "user journeys, screens, navigation". | Skill descriptions are routing-facing; this can make page/screen structure appear primary for description-first UI work. | Rewrite frontmatter description around workstream shell, functional-agent surfaces, typed surface contracts, deep links, and frontend realization details. |
| High | `skills/app-description-ui/SKILL.md:30` | Use-case list starts with "screens, pages, dashboards, portals, admin consoles". | Accepting those user terms is valid, but the skill should immediately normalize them into functional-agent workstreams and structured surfaces for generated SaaS. | Rephrase as input vocabulary only; add explicit first step to route generated SaaS UI changes through workstream/surface modeling. |
| High | `skills/app-description-ui/SKILL.md:43-59` | Preferred `55-ui` structure includes `screens-and-navigation.md` as a normal peer and says a very small app may include it. | Even with new surface skills, this retained file name can preserve page-first artifacts in generated app descriptions. | Rename/reframe as `routes-and-deep-links.md` for generated SaaS or mark `screens-and-navigation.md` as legacy compatibility only. Update examples in the skill text. |
| High | `skills/app-description-ui/SKILL.md:85-90` | Section heading "Screens and navigation" captures screens/pages, paths, actions, states, and navigation entry/exit points. | These items overlap with surface contracts and capability-backed actions; left as-is they can become the primary UI contract. | Replace with "Routes and deep links" plus guidance that actions/states belong primarily to structured surface contracts. |
| High | `skills/app-description-bootstrap/SKILL.md:149` | Bootstrap skeleton still creates `55-ui/screens-and-navigation.md`. | Bootstrap templates shape new app-description trees; new generated SaaS examples should not start with page/screen files as core artifacts. | Replace with `workstream-shell.md`, `surfaces-index.md`, or `routes-and-deep-links.md` as appropriate; if compatibility is needed, annotate legacy-only. |
| Medium | `docs/examples/ai-first-dca-app-description/app-description/55-ui/README.md:12` | Future split list includes `screens-and-navigation.md`. | DCA is a vertical reference example; future agents may copy the split list into new examples. | Change the split list to workstream/surface files and `routes-and-deep-links.md`; clarify routes are deep-link realization details. |
| Medium | `docs/examples/ai-first-dca-app-description/app-description/00-system/app-manifest.md:31` | Non-goal says not "a chatbot attached to static records". | This is already an anti-drift warning, but the example lacks the stronger workstream terminology in the same nearby non-goal. | Optional wording refresh: "not a chatbot or conventional record app; use functional agents, workstreams, surfaces, capabilities." |
| Medium | `templates/ai-first-saas-starter/app-description/app-description/55-ui/screens-and-navigation.md` | File exists with legacy name, though content is marked "Legacy Mechanics Note". | The starter core app is the canonical reference; even a warning file can invite copying the legacy filename. | Consider renaming to `routes-and-deep-links.md` in a cleanup task, and update all references. If preserving the old file for compatibility, keep the warning. |
| Medium | `templates/ai-first-saas-starter/app-description/app-description/55-ui/ui-index.md:30` | References `screens-and-navigation.md` as subordinate. | Safe as written, but would need update if the file is renamed. | Update alongside any seed rename. |
| Medium | `templates/ai-first-saas-starter/app-description/app-description/10-capabilities/04-frontend-shell-and-integration-patterns.md:15` | Mentions page-first/CRUD-first navigation in exclusions. | This is compliant anti-drift language. | No change required unless terminology is normalized during seed rename. |

## Compliant anti-drift references

The following match groups are intentional and should generally remain because they prevent legacy drift rather than teaching it:

- `docs/agent-workstream-application-architecture.md` — canonical disallowed alternatives and readiness checks.
- `docs/ai-first-saas-application-architecture.md` — workstream shell as the generated UI/application default.
- `skills/agent-workstream-apps/SKILL.md` — cleanup warnings and default routing.
- `skills/akka-web-ui-apps/SKILL.md`, `skills/akka-web-ui-frontend-project/SKILL.md`, `skills/akka-http-endpoint-web-ui/SKILL.md`, `skills/akka-web-ui-ux-design/SKILL.md` — web UI skills now frame routes/pages as implementation/deep-link details.
- `skills/app-description-functional-agent-modeling/SKILL.md`, `skills/app-description-surface-modeling/SKILL.md`, `skills/app-description-readiness-assessment/SKILL.md`, `skills/app-descriptions/SKILL.md` — app-description path now blocks page/screen hierarchy from becoming primary.
- Capability/decomposition/entity skills that say not to start from CRUD are compliant backend-routing guardrails.
- `docs/examples/purchase-request-app-description/README.md` is already marked mechanics-only and warns not to present page/screen hierarchy as primary generated SaaS architecture.

## Pack exposure review

No direct legacy-default matches were found in `pack/**` from the required search. The installed-pack guidance currently points back to capability-first and workstream-aware routing rather than conflicting page-first defaults.

## Follow-up queue decision

No additional cleanup task IDs are needed from this inventory. Existing `TASK-AW-05-002` is broad enough to perform the identified edits:

- revise `app-description-ui` routing and structure language;
- revise app-description bootstrap UI skeleton;
- update/rename seed and DCA example `screens-and-navigation` references where appropriate;
- verify installed-pack guidance still has no conflicting default UX architecture.
