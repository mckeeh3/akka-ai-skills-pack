# Sprint 04: Starter Dogfood and Queue Realignment

## Objective

Use the realigned skills model to revise the `ai-first-saas-starter` implementation plan so it is driven by complete vertical workstream/surface/capability delivery rather than vague durable slices.

## Scope

Likely files:

- `specs/ai-first-saas-starter-app-template/pending-tasks.md`
- `specs/ai-first-saas-starter-app-template/sprints/07-fullstack-gap-closure-sprint.md`
- `specs/ai-first-saas-starter-app-template/backlog/07-fullstack-gap-closure-build-backlog.md`
- task briefs under `specs/ai-first-saas-starter-app-template/tasks/07-fullstack-gap-closure/`
- starter template docs only if needed to clarify workstream-first implementation

## Deliverables

- Starter task queue is rewritten or superseded so full-core work is organized by vertical workstreams and surfaces:
  - Access/Profile
  - User Admin
  - Invitation Onboarding
  - Agent Admin
  - Audit/Trace
  - Governance/Policy
  - Workstream shell/realtime/static hosting
- Each starter implementation task includes functional agent, surface/action, capability ids, Akka substrate, frontend/API work, auth/audit, and tests.
- Sprint review confirms whether more skills realignment is required or whether PRD-driven starter implementation can begin.

## Checks

- `git diff --check`
- Consistency check against `docs/agent-workstream-design-review-checklist.md`
