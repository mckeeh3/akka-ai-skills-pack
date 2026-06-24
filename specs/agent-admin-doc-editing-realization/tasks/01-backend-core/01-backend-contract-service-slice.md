# Task AADE-01-001: Backend contract and service slice

## Scope

Implement or revise the backend service contract for Agent Admin doc administration without completing durable entity migration or frontend work in this task.

## Required reads

- `AGENTS.md`
- `specs/AGENTS.md`
- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/sprints/01-backend-doc-admin-sprint.md`
- `specs/agent-admin-doc-editing-realization/backlog/01-backend-doc-admin-build-backlog.md`
- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`
- `app-description/domains/core-starter/workstreams/agent-admin/realization/api-contracts.md`
- current `src/main/java/ai/first/application/coreapp/agentadmin/**`
- current `src/main/java/ai/first/application/foundation/agent/**`

## Skills

- `capability-first-backend`
- `akka-solution-decomposition`

## Implementation guidance

Define the backend boundary for:

- listing/filtering agents;
- reading/updating agent name and purpose;
- reading agent detail with prompt, skills, reference docs, last edit time, and trace links;
- reading current/historical prompt, skill, and reference doc versions;
- edit-session contract records for draft/revise/cancel/save;
- version history and adjacent diff contract records;
- restore contract records;
- SaaS-admin-only access decision points.

Prefer adapting `AgentAdminService` or introducing a focused service class under `application/coreapp/agentadmin`. Avoid broad opportunistic deletion of old prompt-risk/governance code unless it directly blocks this service boundary.

## Required checks

Run the smallest proving checks, normally:

```bash
mvn -Dtest='*AgentAdmin*Service*Test,*Agent*Doc*Test' test
git diff --check
```

If targeted tests are not yet named, create/update focused tests and run them.

## Done criteria

- Backend service/API contract for Agent Admin doc administration is explicit in code.
- Unit tests cover SaaS-admin-only access decisions at the service boundary and basic list/detail/read contracts.
- No frontend implementation is attempted.
- Queue is updated and changes are committed.
