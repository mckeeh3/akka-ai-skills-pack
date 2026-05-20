# Backlog 03: Implementation Skill Alignment

## Outcome

Focused implementation skills no longer invite component-first coding. They require or preserve functional-agent, surface/action, governed capability, authority, trace, and test context before generating Akka/backend/frontend code.

## Backlog items

1. **Audit web UI implementation skills**
   - Review `akka-web-ui-*` skills.
   - Align API client, realtime, state rendering, forms, accessibility, and testing skills with structured surfaces and capability-backed actions.

2. **Audit agent implementation skills**
   - Review `akka-agents` and focused `akka-agent-*` skills.
   - Ensure each skill distinguishes functional agents from internal agents and preserves governed behavior artifacts, tool boundaries, workstream traces, and surface outputs.

3. **Audit endpoint implementation skills**
   - Review HTTP/gRPC/MCP endpoint skills.
   - Ensure endpoints are described as exposure surfaces for capabilities, with AuthContext, tenant/customer scope, idempotency, trace, and surface/API DTO alignment.

4. **Audit Akka component implementation skills**
   - Review entity, workflow, view, consumer, timed-action, and testing skills.
   - Add the standard input contract where needed.

5. **Sprint review and next-task generation**
   - Re-run implementation skill audit.
   - Add Sprint 04 tasks for starter dogfooding and queue replacement.
