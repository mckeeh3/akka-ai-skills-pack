# TASK-UASCC-99-001: Verify User Admin surface conformance cleanup

## Intent

Verify the current task group and overall mini-project done state. If material gaps remain, append bounded follow-up tasks and a new terminal verification task.

## Required reads

- `AGENTS.md`
- `specs/user-admin-surface-conformance-cleanup/README.md`
- `specs/user-admin-surface-conformance-cleanup/conversation-capture.md`
- `specs/user-admin-surface-conformance-cleanup/pending-tasks.md`
- `specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `specs/user-admin-surface-navigation-tree/navigation-tree-verification.md`
- test outputs and notes from prior tasks

## Skills

- `app-description-readiness-assessment`
- `app-description-change-impact`
- `akka-http-endpoint-testing`
- `akka-web-ui-testing`

## Expected outputs

- `specs/user-admin-surface-conformance-cleanup/conformance-verification.md`
- Updated `pending-tasks.md` status and follow-up tasks if needed.

## Required checks

```bash
git diff --check
mvn -q -Dtest=WorkstreamServiceTest test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
```

Verification may require broader `mvn test` or `npm --prefix frontend run build` if touched scope justifies it.

## Done criteria

- Verification compares completed work against README done state, backlog, app-description, previous navigation-tree verification, and task done criteria.
- Verification records command evidence or a clear blocker for any unavailable check.
- If no material gaps remain, mark this task done and record completion.
- If material gaps remain, append bounded follow-up tasks and a new terminal verification task; do not mark mini-project complete.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent` concept and `agent-user-admin` runtime alias/normalization result.
- Attention category or non-attention reason: verification of User Admin attention, task, denial, and system-message surfaces.
- Role-specific dashboard / surface: dashboard, directory, detail, task, decision, workflow, and system-message surfaces in mini-project scope.
- Surface graph node/action edge: full conformance graph verification.
- Governed-tool id and exposure: browser-tool/workstream action mappings verified.
- Capability id: `user_admin.*`, `saas_owner.organization.*`, `admin.audit.read` and related User Admin capabilities.
- AuthContext / roles / tenant scope: allow/deny and hidden target behavior verified.
- Akka substrate: review of backend workstream service/API path and frontend renderer path.
- API / frontend / realtime path: local workstream/API/frontend checks.
- Audit/work trace requirements: audit/trace/redaction assertions reviewed.
- Local validation path: focused Maven/npm checks plus diff check.
