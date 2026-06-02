# Conversation Capture: Workstream Attention Backbone v1

## Current status

This capture records the pre-v1 discussion. The accepted gaps below are historical: v1 has since implemented the shared backend-owned starter attention backbone, and v2 has added bounded producer/update behavior. Use the current contracts and completed queue notes for implementation status.

## User goals

The user identified a core workstream architecture feature: an internal bus/queue containing “things that need my attention.” They asked whether it had been implemented and then asked whether the bus/queue should be per-workstream or shared.

## Findings accepted in discussion

At the time of the original discussion, the repository/starter had partial attention support but no first-class shared runtime backbone:

- My Account has `my_account.list_personal_attention`, but current starter implementation derives hard-coded items from capabilities.
- Workstream dashboard surfaces have `attentionItems`, but they are local surface payloads rather than shared attention records/projections.
- The frontend rail has component-backed unseen-response state, not backend-owned attention counts.
- Search found no `AttentionItem`, attention repository/entity, attention view/projection, or internal attention bus implementation. This finding is historical and no longer describes the completed starter v1 scope.

## Decisions made

- Implement **one shared attention backbone/store** for the application rather than one separate physical queue per workstream.
- Each attention item must carry an owning workstream and scope metadata.
- Workstream dashboards use workstream-scoped projections/details.
- My Account and the left rail use authorized cross-workstream aggregate projections.
- Frontend-only badges are not sufficient; counts and items must derive from governed backend state/projections.
- v1 is enough to start now if scoped as a foundational slice rather than the full event/autonomous-agent ecosystem.

## Initial v1 shape

The discussed v1 should model:

- `AttentionItem` with tenant/customer scope, owning workstream, category, severity, status, source refs, surface/action/capability refs, and timestamps.
- lifecycle states such as `open`, `acknowledged`, `resolved`, `dismissed`, and `expired`.
- scoped summary reads for left rail and My Account.
- workstream-local item reads for dashboards.
- initial producers/derivations for User Admin invitation failures, Agent Admin provider-blocked/readiness, Governance approvals, and Audit/Trace failure evidence.
- authorization, tenant isolation, redaction, audit/trace, and tests.

## Rejected alternatives / non-goals

- Do not create totally independent attention queues per workstream as the primary model.
- Do not treat notifications, unread messages, or frontend badge state as the same thing as actionable attention.
- Do not implement all future events, notifications, timers, AutonomousAgent task notifications, or digest workers in v1.

## Risks

- Over-broad implementation could turn v1 into a full workflow/event platform. Keep tasks bounded.
- A purely Akka component-backed or fixture-only implementation would violate the repository runtime completion doctrine if claimed as normal runtime behavior.
- Authorization/redaction is central: My Account aggregation must not leak hidden workstreams or item counts.

## Unresolved questions

No blocking product questions remain for a starter/reference v1. Implementation tasks may choose the smallest safe Akka substrate consistent with runtime completion doctrine and the starter's existing architecture.
