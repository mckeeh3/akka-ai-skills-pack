# TASK-STARTER-07-009: Expand admin, governance, and audit APIs with integration tests

## Goal

Strengthen the starter's concrete backend capability surface beyond the generic workstream action seam, and prove protected behavior through endpoint/integration tests.

## Required reads

- `specs/ai-first-saas-starter-app-template/starter-workstream-api-contracts.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/**`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/UserAdminService.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java`
- `skills/akka-http-endpoints/SKILL.md`
- `skills/akka-http-endpoint-jwt/SKILL.md`
- `skills/akka-http-endpoint-request-context/SKILL.md`
- `skills/akka-integration-testing/SKILL.md`

## Work

1. Identify the highest-value concrete APIs to add or harden in one session, such as:
   - user directory/search;
   - membership role/status command;
   - support access grant/revoke;
   - access review commit;
   - audit/trace search/detail;
   - governance proposal review/approve/reject.
2. Preserve workstream surface/action contracts while adding explicit API contracts where useful.
3. Add HTTP/integration tests for authorized success, validation, missing/disabled auth, forbidden capability, cross-tenant denial, idempotency, and audit/trace facts.
4. Update frontend API clients only if needed for production path.
5. Update the pending queue entry.

## Required checks

- `git diff --check`
- rendered-template Maven tests for endpoint/integration slice
- frontend tests/typecheck/build if frontend API clients changed

## Done criteria

- Concrete protected APIs cover at least one major admin/governance/audit capability family beyond generic action dispatch.
- Integration tests prove security and tenant-scope behavior.
- Task status is marked `done` in `pending-tasks.md`.
- One git commit is created.

## Suggested commit message

`Add starter admin governance API tests`
