# Notification Platform Foundation

## Purpose

Create the next user-facing layer after attention, events, digests, AutonomousAgent workers, and real-provider smoke readiness: a small governed in-app notification foundation.

The foundation should turn backend-owned attention, digest, and worker event states into user-visible notification items without making frontend state authoritative or building a broad email/push platform in the first slice.

## Source context

Builds on:

- `specs/workstream-attention-backbone-v1/`
- `specs/workstream-attention-event-producers-v2/`
- `specs/workstream-event-backbone-v3/`
- `specs/my-account-personal-attention-digest-autonomous-agent/`
- `specs/autonomous-agent-real-provider-smoke-readiness/`
- starter My Account, attention, event backbone, digest, and frontend shell files

## Scope

- Define notification item and preference contracts.
- Start with **in-app notifications only**.
- Project notifications from backend events/attention/digest states.
- Add My Account notification center/list surface.
- Add governed capabilities for list, mark-read, dismiss/archive, snooze/mute where scoped, and preference update.
- Preserve tenant/customer/AuthContext visibility and redaction.
- Ensure frontend state is not authoritative.
- Add tests and scaffold validation.

## Non-goals

- Do not implement email/push delivery in the first slice.
- Do not build an enterprise notification analytics platform.
- Do not duplicate attention source-of-truth semantics; notifications are a user-facing projection/channel layer.
- Do not leak hidden workstream/item existence through notifications.

## Execution model

Execute one task per fresh harness context. Each task must update `pending-tasks.md`, run checks or record blockers, and make one focused commit.

## Done state

Complete when the starter/reference assets have:

- documented notification item/preference/capability contract;
- backend-owned notification projection from scoped event/attention/digest inputs;
- governed notification read/lifecycle/preference operations;
- My Account notification center surface;
- tests for auth, tenant isolation/redaction, lifecycle/idempotency, projection behavior, and frontend rendering;
- docs that clearly mark email/push and broad notification platform work as future.
