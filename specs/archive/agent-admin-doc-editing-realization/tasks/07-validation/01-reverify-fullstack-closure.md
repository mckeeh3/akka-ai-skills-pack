# Task AADE-07-001: Re-run full-stack closure verification

## Scope

Re-run terminal verification after the `AADE-06-*` follow-up repairs and close the Agent Admin doc-editing realization mini-project only if full-stack evidence is sufficient.

## Required reads

- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/verification-notes.md`
- `specs/agent-admin-doc-editing-realization/pending-tasks.md`
- all task briefs completed since `AADE-05-001`
- `app-description/domains/core-starter/workstreams/agent-admin/**`
- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`

## Skills

- `akka-runtime-feature-verification`
- `akka-web-ui-testing`
- `akka-agent-testing`

## Verification requirements

Assess the same done state as `AADE-05-001`, with special attention to the repaired gaps:

- Agent Admin workstream/API smoke is non-empty and SaaS-admin-only.
- Stale governance-console tests or surfaces are not counted as current Agent Admin readiness.
- Full `mvn test` no longer fails on Agent Admin or collateral workstream authorization smokes.
- Frontend test/typecheck/build remain green.

## Required checks

```bash
mvn test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

## Follow-up rule

If material gaps remain, append bounded follow-up tasks and a new terminal verification task. If no material gaps remain, update `verification-notes.md`, mark this task done, and note mini-project closure.

## Done criteria

- Full-stack validation is recorded with concrete evidence.
- Queue is updated to closure or a further bounded follow-up loop.
- Changes are committed.
