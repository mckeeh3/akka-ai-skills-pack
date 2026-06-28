# Task AADE-09-001: Re-run full-stack closure verification

## Scope

Re-run terminal verification after `AADE-08-001` and close the Agent Admin doc-editing realization mini-project only if full-stack evidence is sufficient.

## Required reads

- `specs/agent-admin-doc-editing-realization/README.md`
- `specs/agent-admin-doc-editing-realization/conversation-capture.md`
- `specs/agent-admin-doc-editing-realization/verification-notes.md`
- `specs/agent-admin-doc-editing-realization/pending-tasks.md`
- `specs/agent-admin-doc-editing-realization/tasks/08-follow-up/01-repair-agent-admin-full-suite-isolation.md`
- `app-description/domains/core-starter/workstreams/agent-admin/**`
- `app-description/domains/core-starter/capabilities/agent-doc-administration.md`

## Skills

- `akka-runtime-feature-verification`
- `akka-web-ui-testing`
- `akka-agent-testing`

## Verification requirements

Assess the same done state as `AADE-07-001`, with special attention to the repaired full-suite/order-dependent Agent Admin protected workstream smoke gap:

- `mvn test` passes without `AgentAdminBrowserWorkstreamSmokeTest` failures.
- Agent Admin workstream/API smoke remains non-empty and SaaS-admin-only.
- Full frontend test/typecheck/build checks remain green.
- No stale governance-console tests or surfaces are counted as current Agent Admin readiness.

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
