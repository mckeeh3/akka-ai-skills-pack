# TASK-STARTER-03-003: Wire remaining core workstream surfaces and realtime/stale behavior

## Purpose

Wire the rest of the starter's core workstream surfaces to backend endpoints and realtime/stale behavior.

## Required reads

- `specs/ai-first-saas-starter-app-template/starter-workstream-api-contracts.md`
- `docs/workstream-ui-reference-architecture.md`
- `skills/akka-web-ui-realtime/SKILL.md`
- `skills/akka-http-endpoint-sse/SKILL.md`
- `frontend/src/workstream/**`

## Expected outputs

- Access/Profile, Audit/Trace, Governance/Policy, and Agent Admin API/frontend wiring available to the extent backend capabilities exist.
- SSE or stale/reconnect behavior tests.

## Done criteria

- Workstream shell renders core surfaces from backend responses.
- Forbidden, stale, reconnect, malformed, and cross-context behavior is safe and visible.
- Required frontend/backend checks pass, queue status is updated, and changes are committed.
