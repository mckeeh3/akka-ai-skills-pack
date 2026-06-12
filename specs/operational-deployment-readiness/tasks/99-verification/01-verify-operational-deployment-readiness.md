# TASK-ODR-99-001: Verify operational deployment readiness

## Intent

Verify the operational deployment readiness mini-project against its done state and command evidence. Append follow-up tasks plus a new terminal verification task if gaps remain.

## Required reads

- `AGENTS.md`
- `specs/operational-deployment-readiness/README.md`
- `specs/operational-deployment-readiness/conversation-capture.md`
- `specs/operational-deployment-readiness/pending-tasks.md`
- outputs from prior tasks
- deployment/env/readiness docs created by prior tasks

## Skills

- `app-description-readiness-assessment`
- `akka-http-endpoint-testing`
- `akka-web-ui-testing`

## Expected outputs

- `specs/operational-deployment-readiness/deployment-readiness-verification.md`
- Updated queue with done status or follow-up tasks and new terminal verification task.

## Required checks

```bash
git diff --check
env -u ADMIN_USERS mvn test
npm --prefix frontend run smoke:user-admin-workstream
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
```

## Done criteria

- Verification compares completed work against README done state, backlog, and task criteria.
- Required checks pass or blockers are recorded.
- No secrets are introduced.
- Follow-up tasks are appended if material gaps remain.

## Vertical workstream contract

- Scope: cross-cutting operational readiness verification.
- Non-attention reason: verification/docs only.
- Capabilities/AuthContext: checks preserve auth/provider boundaries.
- Substrate: docs/scripts/tests/build.
- Validation: listed checks.
