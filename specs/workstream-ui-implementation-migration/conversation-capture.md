# Conversation Capture

## User request

The user asked for a code migration because stale frontend code, including the starter core app reference, should be retired or significantly revised. The migration should create a reusable reference component set and be organized like `specs/agent-workstream-architecture-migration/`: a durable task queue where each task finishes with a git commit.

## Prior assessment

The pack has sufficient doctrine and skill detail to create agent workstream UIs, but the executable `frontend/` reference is not yet strong enough as the canonical implementation pattern. Current code risks looking like a route/page supervisory console instead of a functional-agent workstream shell.

## User's surface navigation clarification

The user clarified that the workstream UI should not be too chat-oriented. Structured surfaces can handle traditional UI behavior while the workstream records navigational/action feedback.

Example:

1. User selects **User Admin** in the collapsible left rail.
2. The app shows a **User Admin Dashboard** surface.
3. The dashboard has a link/button to the **User List** surface.
4. User clicks it; the workstream records text such as `Display the user list view`.
5. The **User List** surface provides list/search behavior.
6. User selects a specific user; the workstream records text such as `Display user account Pat Lee`.
7. A **User Detail** surface appears with edit actions if allowed.
8. User may scroll back to the previous list surface and select another user, or type `show users` in the composer.

This reinforces that clicks, links, buttons, icons, forms, and other non-chat controls remain first-class UI affordances. The workstream provides durable context, traceability, and learnable command language.

## Architectural decision captured

Treat traditional UI pages/screens as structured surfaces and deep links, not as the primary app decomposition:

```text
classic page/form/table/dashboard/editor
→ typed structured surface
→ placed in a functional-agent workstream
→ actions recorded as workstream intent/action feedback
→ backed by governed capabilities
```

## Migration implication

The executable reference should include reusable components for:

- shell and functional-agent rail;
- workstream stream and composer;
- surface envelope/rendering;
- capability action controls;
- classic SaaS surfaces such as dashboard, list/search, detail/edit, decision card, audit timeline, workflow status, and governance diff;
- deep-link routing into functional agents/surfaces;
- fixture API/realtime clients and tests.
