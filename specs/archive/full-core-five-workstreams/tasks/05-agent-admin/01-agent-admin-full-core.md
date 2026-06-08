# TASK-FC5-05-001: Implement Agent Admin full-core vertical

## Objective

Implement Agent Admin full-core surfaces and capabilities for managed-agent definitions, prompts, skills, references, manifests, tool boundaries, model policy, behavior tests, proposals, and traces.

## Required reads

- `specs/full-core-five-workstreams/full-core-contract-matrix.md`
- `docs/workstream-expertise-model.md`
- `docs/agent-runtime-invocation-pattern.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- agent governance skills named by the contract matrix
- relevant starter agentfoundation backend/frontend files

## Expected outputs

- Agent catalog/detail, prompt/skill/reference/manifest/tool-boundary surfaces, proposed diff/review surfaces, model policy summary, and behavior test console surface.
- Governed capabilities for read/list/detail, draft proposal, activate/deprecate where allowed, test behavior, and open traces.
- Backend enforcement that prompt/skill/reference content cannot grant authority; tool-boundary and approval-required expansion denials.
- AgentAdminAgent expertise/tool-boundary and trace updates.
- Tests for tenant isolation, disabled agent, unassigned loader denial, unauthorized authority expansion, secret boundary, and rendering.

## Checks

- `mvn test`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- local smoke path for agent catalog/detail and a safe behavior test
- `git diff --check`

## Done criteria

Agent Admin exposes managed-agent runtime governance through real protected surfaces/actions, not static seed markdown.
