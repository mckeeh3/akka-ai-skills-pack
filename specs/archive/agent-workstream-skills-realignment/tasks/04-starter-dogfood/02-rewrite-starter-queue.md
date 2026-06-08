# TASK-AWSR-04-002: Rewrite starter queue as workstream/surface/capability tasks

## Goal

Supersede vague starter Sprint 07 tasks and create vertical full-core starter implementation tasks driven by functional agents, surfaces, capabilities, Akka components, frontend/API work, and tests.

## Required reads

- `specs/agent-workstream-skills-realignment/starter-queue-gap-matrix.md`
- `specs/ai-first-saas-starter-app-template/pending-tasks.md`
- `specs/ai-first-saas-starter-app-template/starter-app-scope-and-acceptance.md`
- `templates/ai-first-saas-starter/README.md`
- `templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/structured-surface-contracts.md`

## Work

1. Supersede vague tasks rather than deleting history.
2. Add new starter implementation tasks for at least:
   - Access/Profile workstream;
   - User Admin workstream;
   - Invitation Onboarding surfaces/actions;
   - Agent Admin workstream;
   - Audit/Trace workstream;
   - Governance/Policy workstream;
   - shell/realtime/static hosting validation.
3. Each new task must include functional agent, surface/action, capability ids, Akka substrate, frontend/API work, auth/audit, and tests.
4. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`

## Done criteria

- Starter queue is workstream-first and implementation-ready.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Rewrite starter queue workstream first`
