# Structured Surface Rendering

Authoritative surface definitions live in `../12-workstreams/surfaces-index.md` and `../12-workstreams/surface-contracts/`.

Implementation reference: render these contracts with `../../../../../frontend/src/workstream/surfaces/**` and action controls in `../../../../../frontend/src/workstream/actions/**`. The User Admin vertical in `../../../../../frontend/src/workstream/fixtures/**` plus `../../../../../frontend/src/workstream-user-admin-vertical.contract.test.mjs` is the canonical foundation-admin example.

## Rendering rules

- Render by `surfaceType` and `surfaceVersion`, not by route name.
- Validate payload shape in the typed frontend client before rendering consequential controls.
- Surface actions carry capability id, expected input schema, idempotency key need, and correlation id.
- Backend capabilities decide whether an action is allowed; the UI must render server denials safely.
- Every surface has loading, empty, error, forbidden, stale/reconnect, and submitted/success states where relevant.
- Surface components must expose accessible labels, keyboard operation, focus management, and responsive layouts.

## Reusable surface components

- dashboard / attention surface
- form / guided intake
- data table / search results
- chart / metric panel
- entity detail card
- decision / approval / exception card
- diff / proposed change review
- audit / work-trace timeline
- workflow status / progress card
- evidence bundle
- policy, prompt, or skill version card
- outcome review panel

## Tests

- fixture-driven rendering for each canonical surface type using the workstream fixtures.
- User Admin dashboard → list/search → detail/edit flow through structured surfaces, not standalone pages.
- forbidden action and server-denial recovery states.
- stale/reconnect banners and preserved prior payload.
- responsive collapse for data-dense surfaces.
