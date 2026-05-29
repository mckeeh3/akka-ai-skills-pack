# Conversation Capture

## Source discussion

After completing the Full-Core SMB Baseline/UX child and the first User Admin child, the user asked what should happen next. The assistant observed that the completed User Admin work delivered the dashboard and invitation foundation, while `user-admin-vertical-contracts.md` still lists these next slices:

1. member status and role/capability changes;
2. UserAdminAgent request/response guidance;
3. access-review AutonomousAgent/internal worker.

The assistant recommended starting with User Admin access management before Agent Admin because it is the next most important SMB User Admin loop and creates deterministic evidence needed by later AI guidance and workers. The user agreed and asked to proceed.

## Accepted decisions

- Create a new mini-project for User Admin access management.
- Implement member disable/reactivate and role/capability preview/change before UserAdminAgent guidance or access-review worker work.
- Preserve AI-first workstream/surface architecture; no page-first CRUD admin console.
- Keep deterministic services authoritative for access changes.
- Use real local runtime/API/UI validation for implemented behavior.

## Risks

- Role/capability changes can easily become an enterprise role-builder. Keep this SMB-scoped with manageable roles/capabilities.
- Last-admin and self-disable guardrails are critical; missing them should block completion.
- Frontend action controls are advisory only; backend denial must remain authoritative.
- If implementation reveals missing role/membership primitives in the starter, append bounded follow-up tasks rather than stretching one task.

## Unresolved questions

No blocking question is required to start. If the existing starter lacks enough role/capability state to implement commit behavior safely, the source-boundary task should either define a bounded state extension task or block with a concrete question.
