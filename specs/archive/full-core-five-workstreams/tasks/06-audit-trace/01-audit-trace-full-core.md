# TASK-FC5-06-001: Implement Audit/Trace full-core vertical

## Objective

Implement Audit/Trace full-core search, timeline, detail, and export/request surfaces over durable audit/work trace state.

## Required reads

- `specs/full-core-five-workstreams/full-core-contract-matrix.md`
- `docs/ai-first-saas-audit-trace.md` if present or `skills/ai-first-saas-audit-trace/SKILL.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`
- relevant starter audit/work trace backend/frontend files

## Expected outputs

- Audit/Trace dashboard, filters/search, timeline, trace detail, prompt/model/tool/data-access trace views, denial and redaction behavior.
- Governed capabilities for scoped search, detail read, trace explain, safe export/request where in scope.
- Backend tenant/customer scope, auditor/support redaction profiles, audit of trace access, and forbidden tests.
- AuditTraceAgent expertise/tool-boundary updates for explaining traces without leaking secrets.
- Frontend timeline/detail rendering tests.

## Checks

- `mvn test`
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- local smoke path for audit search/detail
- `git diff --check`

## Done criteria

Audit/Trace is a real investigation workstream with searchable scoped trace surfaces and access audit.
