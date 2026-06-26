# Global UI Style and Runtime Contracts

The canonical browser UI is the workstream shell defined by `workstream-shell.md` and realized under `frontend/src/workstream/**`, backed by protected Akka/API calls. Removed screen modules are not active app-description targets, runtime fallbacks, fixture sources, mechanics references, or generation templates.

## Style source of truth

Generated and maintained UI surfaces use:

- `frontend/src/styles/tokens.css` for named themes and semantic state tokens.
- `frontend/src/styles/base.css`, `layout.css`, and `components.css` for shell, card, action, focus, responsive, and status anatomy.
- `frontend/src/workstream/shell/**`, `frontend/src/workstream/surfaces/**`, `frontend/src/workstream/actions/**`, and `frontend/src/workstream/realtime/**` as the active component system.

Surface descriptions may refer to the selected web UI style guide, named-theme tokens, component catalog anatomy, accessibility, responsive behavior, and `workstream-shell.md` by this node. They must not reintroduce a page-first or screen-first UI model.

## Runtime source of truth

- Workstream bootstrap, actions, messages, shell requests, and realtime events come from protected backend endpoints described in `workstream-shell.md` and `../../55-ui/frontend-api-contracts.md`.
- Test fixtures may live only under test assets and must not be importable or selectable by the normal runtime path.
- Browser route state, deep links, local storage, disabled controls, compatibility endpoints, and fixture data never grant authority or imply a missing workstream/surface contract.
- Provider/model/outbox unavailable states render fail-closed typed surfaces; no fake success or canned model/provider result is normal runtime behavior.
- Retired screen modules, page-first route tests, stale compatibility aliases, and archived specs must be treated as historical or test evidence only. If they conflict with current workstream/surface contracts, the current app-description graph wins.
- Frontend code must omit or safely disable unsupported/deferred actions; it must not render placeholder panels for billing, CRM/customer-success/sales/support case management, timer reminders, or other deferred business domains.
