# Conversation Capture: Core Workstream Surface Style Alignment

## User goals

- Follow up after the web UI style/theme refresh to ensure existing core app workstream surfaces are updated.
- Create a mini-project rather than doing ad hoc edits.
- Focus on existing core app workstream surface reference docs that were not fully covered by the prior mini-project.

## Decisions already made

Inherited from `specs/web-ui-style-theme-refresh/`:

- The replacement canonical style is `ai-first-workstream-enterprise`.
- The user-facing theme model is named theme selection, not `system/light/dark` as the primary choice.
- Initial named themes are:
  - `aurora-light`
  - `cobalt-light`
  - `obsidian-dark`
  - `midnight-dark`
- My Account theme selection should be simple: select an available named theme and the UI applies that theme.
- Theme preference is not authorization.

Newly established for this follow-up:

- The likely missed area is `docs/examples/ai-first-saas-core-app-domain/**`, which describes required core workstream surfaces.
- The follow-up should align documentation/reference surface contracts, not start with frontend implementation.

## Accepted constraints

- Preserve AI-first workstream architecture and capability-backed surface semantics.
- Do not change backend capabilities, route/API contracts, security, audit, or authorization while doing visual/style alignment.
- Do not duplicate full style-guide content into every core workstream README.
- Do not introduce generic dashboard/CRM/admin style choices.

## Rejected alternatives / non-goals

- Do not reopen the completed `web-ui-style-theme-refresh` queue unless verification finds a direct reason.
- Do not treat this as a whole starter runtime implementation project.
- Do not use old Atlas/orange/coral style language as active guidance.

## Risks

- Core domain docs may use generic surface type names such as `dashboard`, `data_table`, and `detail_card`; these are acceptable if paired with AI-first workstream surface style and capability semantics, but risky if left as generic CRUD/dashboard guidance.
- Over-editing could duplicate canonical style docs and make future maintenance harder.
- Starter core app-description surface contracts may also have small gaps beyond My Account; verification should check but keep follow-up bounded.

## Unresolved questions

No blocking questions are needed for planning. Exact wording can be chosen by future task sessions from the canonical style guide and existing workstream semantics.
