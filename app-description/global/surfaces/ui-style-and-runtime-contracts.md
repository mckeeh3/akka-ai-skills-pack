# Global UI Style and Runtime Contracts

The canonical browser UI is the workstream shell under `frontend/src/workstream/**`, backed by protected Akka/API calls. Removed screen modules are not active app-description targets, runtime fallbacks, fixture sources, or mechanics references.

## Style source of truth

Generated and maintained UI surfaces use:

- `frontend/src/styles/tokens.css` for named themes and semantic state tokens.
- `frontend/src/styles/base.css`, `layout.css`, and `components.css` for shell, card, action, focus, responsive, and status anatomy.
- `frontend/src/workstream/shell/**`, `frontend/src/workstream/surfaces/**`, `frontend/src/workstream/actions/**`, and `frontend/src/workstream/realtime/**` as the active component system.

Surface descriptions may refer to the selected web UI style guide, named-theme tokens, component catalog anatomy, accessibility, and responsive behavior by this node. They must not reintroduce a page-first or screen-first UI model.

## Runtime source of truth

- Workstream bootstrap, actions, messages, and realtime events come from protected backend endpoints.
- Test fixtures may live only under test assets and must not be importable or selectable by the normal runtime path.
- Browser route state, deep links, local storage, disabled controls, and fixture data never grant authority.
- Provider/model/outbox unavailable states render fail-closed typed surfaces; no fake success or canned model/provider result is normal runtime behavior.
