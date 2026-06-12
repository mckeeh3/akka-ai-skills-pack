# TASK-UABWS-04-001: Document and integrate User Admin smoke command

## Intent

Make the User Admin browser/workstream smoke test discoverable and repeatable for future maintainers and CI/local validation.

## Required reads

- `AGENTS.md`
- `specs/user-admin-browser-workstream-smoke/README.md`
- `specs/user-admin-browser-workstream-smoke/smoke-tooling-survey.md`
- `specs/user-admin-browser-workstream-smoke/tasks/04-ci-docs/01-document-and-integrate-smoke-command.md`
- smoke implementation from `TASK-UABWS-03-001`
- `frontend/package.json`
- `README.md` or relevant docs if touched by prior tasks

## Skills

- `akka-web-ui-testing`
- `akka-web-ui-frontend-project`

## Expected outputs

- Script/package command or repo tool entry for smoke test.
- Documentation for prerequisites, command, expected artifacts, and troubleshooting.

## Required checks

```bash
git diff --check
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

Also run the smoke command from `TASK-UABWS-03-001`.

## Done criteria

- A future fresh harness session can run the smoke without rediscovering setup.
- Docs explain `ADMIN_USERS` caveat and provider credential non-requirements.
- Smoke command is not conflated with fixture-only tests.
- Generated artifacts, screenshots, or reports are ignored or documented as appropriate.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `agent-user-admin`.
- Attention category or non-attention reason: docs/integration for smoke validation; no product attention item.
- Role-specific dashboard / surface: all smoke-covered User Admin surfaces.
- Surface graph node/action edge: documented smoke traversal.
- Governed-tool id and exposure: documented browser-tool/workstream action path.
- Capability id: User Admin capabilities and denial path documented.
- AuthContext / roles / tenant scope: deterministic smoke auth context documented.
- Akka substrate: frontend/backend smoke command integration.
- API / frontend / realtime path: local UI/API smoke command.
- Audit/work trace requirements: expected trace/redaction assertions documented.
- Local validation path: smoke command, frontend tests/typecheck, diff check.
