# Sprint 3 Build Backlog: Component Skill Reframing

## Purpose

Update focused Akka implementation skills so components implement or expose governed capabilities.

## Suggested harness task breakdown

### 1. Agent tool and component-tool skills

- task ID: `TASK-03-001`
- outputs: `akka-agent-tools`, `akka-agent-component-tools`, and related agent guidance updated around capability-first design.

### 2. Entity skills

- task ID: `TASK-03-002`
- outputs: Event Sourced Entity and Key Value Entity skills updated for capability semantics, command/query boundaries, idempotency, audit, and tool exposure decisions.

### 3. Workflow skills

- task ID: `TASK-03-003`
- outputs: workflow skills updated for consequential capability execution, approval gates, supervision, compensation, and traces.

### 4. View skills

- task ID: `TASK-03-004`
- outputs: view skills updated for curated read capabilities and evidence retrieval.

### 5. Endpoint and MCP skills

- task ID: `TASK-03-005`
- outputs: HTTP/gRPC/MCP guidance updated for selective capability exposure.

### 6. Consumer, timer, and testing skills

- task ID: `TASK-03-006`
- outputs: async/scheduled execution and testing guidance updated for capability-first behavior.

## Done criteria

- Stage 3 skills no longer imply component-first design for broad product work.
- Tool exposure is consistently treated as one governed surface of a capability.
