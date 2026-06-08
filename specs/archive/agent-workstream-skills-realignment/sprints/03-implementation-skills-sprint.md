# Sprint 03: Implementation Skill Alignment

## Objective

Align focused implementation skills so they consume a standard workstream/surface/capability input contract before generating code or tests.

## Scope

Likely source skill families:

- Web UI: `akka-web-ui-*`
- Agents: `akka-agents`, `akka-agent-*`
- Endpoints: `akka-http-*`, `akka-grpc-*`, `akka-mcp-*`
- Components: entities, workflows, views, consumers, timed actions
- Testing skills across those families

## Standard input contract

Implementation skills should expect or help derive this contract before coding generated SaaS work:

```text
functional agent:
internal agent, if any:
workstream:
surface id/type/version:
surface action or workstream event:
capability id/class:
AuthContext and roles/capabilities:
input/output DTOs and redaction:
side effects and idempotency:
policy/approval/escalation:
audit/work traces:
selected Akka substrate:
frontend/API/realtime exposure:
required tests:
```

## Deliverables

- Web UI companion skills consistently refer to structured surfaces, selected AuthContext, capability-backed actions, trace links, and stale/reconnect behavior.
- Agent skills consistently distinguish user-facing functional agents from internal backend agents.
- Endpoint/component/testing skills ask for or preserve the standard input contract before implementation.
- Sprint review identifies remaining implementation drift and creates follow-up tasks.

## Checks

- `git diff --check`
- Targeted text audit over touched skills for workstream/surface/capability terminology.
