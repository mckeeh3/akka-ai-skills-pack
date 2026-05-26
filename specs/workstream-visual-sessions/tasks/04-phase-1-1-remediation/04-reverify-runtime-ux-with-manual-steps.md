# Task Brief: Reverify Runtime UX with Manual Steps

## Task

Reverify the runtime workstream visual-session UX after remediation using explicit manual/smoke steps, not only static contract tests.

## Required reads

- `AGENTS.md`
- `docs/workstream-visual-sessions.md`
- `specs/workstream-visual-sessions/pending-tasks.md`
- `frontend/README.md`
- `templates/ai-first-saas-starter/frontend/README.md`
- changed source and starter frontend files from this remediation

## Expected outputs

- Documented manual or local smoke verification steps and results in `docs/workstream-visual-sessions.md` or task notes.
- Verification covers:
  1. select Workstream A;
  2. submit a request;
  3. switch to Workstream B before A's response arrives;
  4. confirm the visible workstream remains B when A's response arrives;
  5. confirm A has a visible unseen-response rail indicator;
  6. select A and confirm the indicator clears;
  7. confirm request anchoring remains correct.
- Phase 1.1 readiness notes updated only if verified.

## Required checks

- `cd frontend && npm run typecheck`
- `cd frontend && node --test src/workstream-visual-session.contract.test.mjs`
- `cd frontend && node --test src/workstream-shell.contract.test.mjs`
- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- `cd templates/ai-first-saas-starter/frontend && node --test src/workstream-visual-session.contract.test.mjs`
- `cd templates/ai-first-saas-starter/frontend && node --test src/workstream-shell.contract.test.mjs`

## Constraints

- Do not mark readiness based solely on pending-task status or regex tests.
- Do not claim browser-local or backend persistence.

## Completion

Mark `TASK-WVS-04-004` done after commit.
