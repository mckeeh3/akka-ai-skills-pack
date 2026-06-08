# Conversation Capture: Notification Platform Foundation

## User request

After AutonomousAgent real-provider smoke readiness completed, the assistant recommended a notification platform foundation as the next user-facing layer. The user said:

> go ahead

## Decision

Create a mini-project for a small governed in-app notification foundation.

## Constraints

- In-app first; no email/push in first slice.
- Backend notification projection is authoritative.
- Attention remains source-of-truth for attention semantics.
- Hidden workstreams/items must not leak.
