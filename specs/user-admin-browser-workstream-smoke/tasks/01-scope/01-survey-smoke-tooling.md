# TASK-UABWS-01-001: Survey smoke tooling and choose implementation approach

## Intent

Survey the current project tooling and runtime paths, then record the smallest reliable browser/workstream smoke approach for User Admin.

## Required reads

- `AGENTS.md`
- `specs/user-admin-browser-workstream-smoke/README.md`
- `specs/user-admin-browser-workstream-smoke/conversation-capture.md`
- `specs/user-admin-browser-workstream-smoke/backlog/01-user-admin-browser-smoke-build-backlog.md`
- `specs/user-admin-browser-workstream-smoke/tasks/01-scope/01-survey-smoke-tooling.md`
- `frontend/package.json`
- `frontend/vite.config.ts`
- `pom.xml`
- `src/main/java/ai/first/api/coreapp/workstream/StarterFrontendEndpoint.java`
- `src/main/java/ai/first/api/coreapp/workstream/WorkstreamEndpoint.java`
- `frontend/src/main.tsx`
- `frontend/src/api/**`

## Skills

- `akka-web-ui-testing`
- `akka-http-endpoint-testing`
- `akka-web-ui-frontend-project`

## Expected outputs

- `specs/user-admin-browser-workstream-smoke/smoke-tooling-survey.md`
- Updated pending queue only if task order or blockers change.

## Required checks

```bash
git diff --check
```

## Done criteria

- Survey identifies existing scripts/dependencies and local app-run options.
- Survey chooses automated browser, DOM, HTTP+static, or staged smoke approach with rationale.
- Next task can implement deterministic local smoke setup without guessing.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin`.
- Attention category or non-attention reason: tooling survey; no runtime attention item.
- Role-specific dashboard / surface: User Admin dashboard and branch surfaces as smoke targets.
- Surface graph node/action edge: planned smoke traversal across dashboard/list/detail/task/system-message.
- Governed-tool id and exposure: survey of browser-tool/workstream API exposure.
- Capability id: User Admin capabilities as smoke authorization targets.
- AuthContext / roles / tenant scope: survey must identify how smoke obtains/sets selected AuthContext safely.
- Akka substrate: frontend hosting endpoint, workstream HTTP endpoint, frontend test tooling.
- API / frontend / realtime path: local hosted UI/API path.
- Audit/work trace requirements: survey trace/redaction assertions to include later.
- Local validation path: docs-only survey plus diff check.
