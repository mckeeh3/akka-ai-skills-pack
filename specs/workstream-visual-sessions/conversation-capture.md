# Conversation Capture: Workstream Visual Sessions

## Original concern

The existing control of workstream surfaces is confusing. The desired behavior is that when a new workstream request is submitted, the user should see their last request and the response surfaces below it. Prior surfaces must remain viewable.

## Clarification

The target is the familiar modern chat UX used by tools such as ChatGPT, Claude, and Gemini:

- older content is above;
- newer content is appended below;
- successive request/response groups are appended to the end of prior groups;
- when a new request is submitted, that new request is visually positioned at the top of the screen/visible workstream panel;
- prior turn groups are above it and can be reached by scrolling up;
- response surfaces generated for the request appear below the request surface;
- the request should remain visible at the top while responses append unless the user manually scrolls.

## Terminology accepted

Use **turn group** for a user request plus the response surfaces created because of it.

A turn group may contain:

- user request surface;
- markdown response surface;
- structured response surfaces;
- system-message surfaces;
- workflow/progress surfaces;
- capability result surfaces;
- decision, approval, audit, trace, or exception surfaces.

## Session model

Each workstream should have a current visual session. As users move between workstreams, the visual session state is maintained so the user sees where they left off.

Visual session state is initially focused on UI continuity, not business truth. It should not replace durable workstream history, audit/work traces, backend authorization, or capability semantics.

## Session limits

Initial recommendation:

- limit primarily by number of recent turn groups;
- use a secondary rendered-surface cap;
- suggested default: 20 recent turn groups and 150-250 rendered surfaces;
- older content remains accessible through durable history or a load-older affordance.

## Persistence view

Persistent visual sessions are desirable for SaaS users. Device-only persistence is a technical limitation rather than ideal usability. However, the first implementation should stay simple.

Accepted phased approach:

1. Phase 1: component-backed per-workstream visual sessions.
2. Phase 2: browser-local persistence.
3. Phase 3: backend-persisted visual sessions for cross-device continuity and observability-friendly resume behavior.

## Implementation guidance

Create a self-contained task series under `specs/workstream-visual-sessions/pending-tasks.md`. Tasks should be executed in fresh harness sessions. Each completed task should make a git commit containing that task's intended changes and queue-status update.
