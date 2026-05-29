# Conversation Capture: Full-Core SMB User Admin

## Accepted goals

- User Admin is the first highest-leverage workstream child in Wave 1.
- It should support real SMB access operations without spreadsheets, support backdoors, or page-first CRUD.
- It must exercise deterministic access mechanics, request/response Akka Agent guidance, audit/trace visibility, and a durable access-review worker candidate.

## Accepted constraints

- Backend authorization, tenant/customer filtering, disabled-user behavior, last-admin/self-disable guardrails, idempotency, policy checks, and audit emission are deterministic service responsibilities.
- The User Admin Agent may explain, summarize, guide, and recommend, but must use the governed Akka Agent runtime path and must not own mechanical authorization or state transitions.
- Access-review/internal-worker work is justified only when durable task lifecycle, progress, evidence, and human result review exist.

## Risks

- User Admin can easily drift into CRUD tables without attention-first dashboard and structured surfaces.
- Invitation and outbox delivery status may need starter source discovery before implementation tasks can be precise.
- Worker scope must not bypass governed role/membership mutation capabilities.

## Unresolved questions

None blocking this queue. Implementation discovery may append questions for exact starter code boundaries or provider smoke prerequisites.
