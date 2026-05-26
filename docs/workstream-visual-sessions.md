# Workstream Visual Sessions

## Status and scope

This document captures the target UX and phased implementation plan for workstream visual sessions in generated AI-first SaaS applications.

Canonical related docs:
- `docs/agent-workstream-application-architecture.md`
- `docs/workstream-ui-reference-architecture.md`
- `docs/structured-surface-contracts.md`

A workstream visual session is browser/UI state for how a user is currently viewing a durable workstream. It does not replace durable workstream history, audit/work traces, authorization, or backend capability semantics.

## User experience goal

The workstream should feel like the familiar modern chat experience from ChatGPT, Claude, Gemini, and similar tools, while preserving the workstream model: typed surfaces, governed capabilities, trace links, role-authorized functional agents, and persistent context.

The timeline order is traditional chat order:

```text
oldest turn group
older turn group
current/latest turn group
```

Older content is above. Newer content is appended below previous turn groups.

When a user submits a new request, the new request is appended at the end of the workstream, then the viewport scrolls so that the new request surface is at the top of the visible workstream panel. Prior turn groups remain available by scrolling up. Surfaces produced in response to the request render below that request surface. While responses are streaming or additional surfaces are appended for the same request, the request surface remains anchored at the top of the visible panel unless the user manually scrolls.

The objective is that the user sees:

```text
[top of visible panel]
latest user request surface
response surface 1
response surface 2
system message / progress / decision / table / trace surface ...
```

This lets users keep their own request in view while reviewing the generated response surfaces below it.

## Core terms

### Durable workstream history

Backend-owned durable history of user requests, agent responses, surfaces, capability results, workflow status, decisions, denials, and trace links. This history may be subject to retention, archival, summarization, and authorization rules.

### Visual session

UI-owned state describing how a user is viewing a workstream. It includes selected workstream, loaded turn groups, active anchor, scroll restoration hints, collapsed/expanded state, selected surface, and whether auto-scroll is currently paused because the user manually scrolled.

Visual session state is convenience and continuity state. It must not be used as an authorization boundary or source of truth for business behavior.

### Turn group

A user request plus the response surfaces created because of that request.

A turn group may include:
- the user request surface;
- model-authored markdown response surfaces;
- structured result surfaces;
- system-message surfaces;
- progress/workflow-status surfaces;
- capability result surfaces;
- decision, approval, exception, audit, or trace surfaces.

Turn groups preserve the user's mental model: "the thing I asked" and "what happened because of it."

### Active turn group

The latest submitted request and all surfaces currently being generated or appended in response to it.

## Required scroll behavior

On new request submission:

1. Append the new turn group after existing turn groups.
2. Render the user request surface immediately.
3. Scroll the workstream panel so the request surface is aligned to the top of the visible panel.
4. Append response surfaces below the request as they arrive.
5. Preserve the request-surface anchor while response surfaces stream or append.
6. Stop preserving the anchor if the user manually scrolls up or down.
7. Do not reorder prior turn groups. Prior turn groups remain above the active turn group.

On workstream switch:

1. Save the current workstream's visual session state.
2. Restore the selected workstream's previous visual session state.
3. Preserve the user's last viewed position where feasible.
4. Do not jump to latest unless the user submits a new request or explicitly chooses a "jump to latest" behavior.

On reload/resume, behavior depends on implementation phase.

## Session length guidance

Do not limit visual sessions primarily by raw surface count. Users reason in request/response turns, and one request may produce many surfaces.

Preferred initial policy:

```text
Limit by recent turn groups, with a secondary surface cap.
```

Suggested defaults:

```text
20 recent turn groups per workstream visual session
150-250 rendered surfaces maximum
```

When limits are reached:
- keep durable history available through backend history/search capabilities;
- retain the most recent complete turn groups in the visual session;
- collapse or unload older turn groups as complete groups where possible;
- provide a "load older history" affordance rather than silently discarding meaning.

## Phased implementation plan

### Phase 1: basic in-memory visual sessions

Goal: get the basic UX correct without overbuilding persistence.

Scope:
- maintain per-workstream visual session state in frontend memory;
- use traditional chat ordering: older above, newer below;
- group request and response surfaces into turn groups;
- append new turn groups at the end of the stream;
- scroll the new request surface to the top of the visible panel;
- keep the active request anchored while response surfaces append;
- pause automatic anchoring once the user manually scrolls;
- restore visual state when switching between workstreams during the same browser session;
- add focused UI/state tests for the above behavior.

Out of scope for phase 1:
- cross-tab synchronization;
- cross-device persistence;
- backend-persisted visual sessions;
- exact pixel-perfect restoration after reload;
- history compaction/summarization beyond simple turn/surface caps.

### Phase 2: browser-local persistence

Goal: preserve visual continuity across refreshes and day-to-day use on the same browser/device.

Possible storage:
- localStorage for small semantic session metadata;
- IndexedDB if loaded turn/session state becomes larger.

Persist semantic anchors rather than fragile pixel offsets where possible:

```ts
type WorkstreamVisualSessionSnapshot = {
  accountId: string;
  authContextId: string;
  workstreamId: string;
  activeTurnGroupId?: string;
  anchorSurfaceId?: string;
  selectedSurfaceId?: string;
  loadedTurnGroupIds: string[];
  collapsedSurfaceIds: string[];
  userHasManualScroll: boolean;
  lastViewedAt: string;
};
```

Phase 2 should handle authorization/context changes safely. If the saved workstream, turn group, or surface is no longer authorized or available, restore to a safe current workstream state and show a typed system-message surface if useful.

### Phase 3: backend-persisted visual sessions

Goal: support cross-device continuity and observability-friendly resume behavior for SaaS users.

Store semantic resume state, not raw browser scroll pixels:

```text
resume workstream X at turn group Y / surface Z with approximate position
```

Potential benefits:
- pick up where the user left off across devices;
- support auditability and product observability around workstream usage;
- enable better "continue from yesterday" experiences;
- support managed SaaS user expectations beyond one local browser.

Concerns to design before implementation:
- visual state may reveal sensitive work context;
- authorization, tenant/customer context, membership, and role changes may invalidate a saved session;
- multi-device edits can conflict;
- retention or compaction may remove saved anchors;
- exact scroll positions are viewport-dependent and should not be treated as authoritative;
- backend session state must not become a substitute for audit/work trace history.

## Observability notes

Because the target user is a SaaS app user, visual session behavior is product-relevant observability. However, phase 1 should stay lightweight.

Potential future observability events:
- workstream selected;
- visual session restored;
- new turn group submitted;
- request surface anchored;
- user manually scrolled and auto-anchor paused;
- jump-to-latest used;
- older history loaded;
- saved visual session unavailable due to authorization or retention.

These events should be privacy-conscious and should reference stable workstream/turn/surface ids rather than copying sensitive prompt or payload content.

## Acceptance checklist for phase 1

- [ ] New requests append after existing turn groups; older content remains above newer content.
- [ ] The request surface for the new turn group scrolls to the top of the visible workstream panel.
- [ ] Response surfaces append below the request surface.
- [ ] The request surface remains anchored while response surfaces append, unless the user manually scrolls.
- [ ] Switching workstreams preserves and restores in-memory visual state per workstream.
- [ ] Visual session limits are based primarily on turn groups, with a secondary surface cap.
- [ ] Tests cover append order, scroll target, anchor pause on manual scroll, and per-workstream state restoration.
