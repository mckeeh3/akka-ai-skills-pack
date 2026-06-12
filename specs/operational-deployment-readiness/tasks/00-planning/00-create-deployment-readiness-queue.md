# TASK-ODR-00-001: Create operational deployment readiness scaffold

## Intent

Create the mini-project planning scaffold and queue for operational deployment readiness.

## Required reads

- `AGENTS.md`
- `specs/operational-deployment-readiness/README.md`
- `specs/operational-deployment-readiness/conversation-capture.md`
- `specs/operational-deployment-readiness/backlog/01-operational-deployment-readiness-build-backlog.md`

## Expected outputs

- Mini-project docs, backlog, task briefs, and pending queue.

## Required checks

```bash
git diff --check
```

## Done criteria

- Queue has runnable first non-done task and terminal verification task.
- Scaffold committed without unrelated work.

## Vertical workstream contract

- Scope: cross-cutting operational readiness.
- Non-attention reason: docs/planning only.
- Surfaces: deployment/runbook/smoke docs; no runtime surface change.
- Capabilities/AuthContext: preserve existing auth/provider fail-closed behavior.
- Substrate: specs/docs only.
- Validation: diff check.
