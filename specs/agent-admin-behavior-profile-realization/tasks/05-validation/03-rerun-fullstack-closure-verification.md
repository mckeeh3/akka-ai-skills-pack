# Task AABP-05-003: Rerun Agent Admin behavior-profile realization terminal verification

## Goal

Rerun terminal verification after `AABP-05-002` repairs the full-suite blocker and determine whether the Agent Admin behavior-profile realization mini-project can close.

## Required reads

- `specs/agent-admin-behavior-profile-realization/README.md`
- `specs/agent-admin-behavior-profile-realization/verification-notes.md`
- `specs/agent-admin-behavior-profile-realization/pending-tasks.md`
- `app-description/domains/core-starter/workstreams/agent-admin/**`
- Relevant backend/frontend/source-alignment evidence directly needed to update closure.

## Skills

- `akka-runtime-feature-verification`
- `akka-web-ui-testing`
- `akka-agent-testing`
- `akka-agent-work-trace`

## Verification obligations

- Re-check the implemented Agent Admin runtime path against current app-description and prior verification notes.
- Record readiness level achieved using the required runtime-readiness labels.
- Close the mini-project if required checks pass and no material gaps remain for the stated `api-smoked/frontend-rendered` target.
- If material gaps remain, append a further bounded follow-up loop and do not claim `runtime-ready` without local real-runtime/manual/provider evidence.

## Expected outputs

- Updated `specs/agent-admin-behavior-profile-realization/verification-notes.md`.
- Updated `pending-tasks.md` status and notes.
- Updated app-description source-alignment if closure or residuals are established.

## Required checks

```bash
mvn test
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
git diff --check
```

## Done criteria

- All required checks run and results are recorded.
- Mini-project is closed with evidence, or remaining material gaps are converted into a further bounded follow-up loop.
- Changes are committed.

## Commit message

`Rerun Agent Admin behavior profile verification`
