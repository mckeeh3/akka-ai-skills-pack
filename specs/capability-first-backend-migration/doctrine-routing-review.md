# Doctrine and Routing Review

## Scope

Task: `TASK-05-001`

Reviewed doctrine and top-level routing for stale or conflicting guidance after the capability-first backend migration:

- `AGENTS.md`
- `skills/README.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/capability-first-backend-architecture.md`
- `skills/capability-first-backend/SKILL.md`

## Findings

### Resolved in this task

1. `AGENTS.md` still described the high-level path as AI-first SaaS interpretation directly to description/decomposition/focused implementation, without naming capability-first backend modeling as an intervening substrate.
   - Fix: updated the high-level repository guidance to route broad product input through governed backend capability modeling before description-first, decomposition, or focused implementation paths.

2. `AGENTS.md` Mode B and Stage 1 guidance still said to decompose into the Akka substrate without explicitly preserving capability contracts.
   - Fix: updated Mode B and Stage 1 guidance so decomposition starts from capability contracts and component implementation follows only after capability semantics are clear.

3. `AGENTS.md` session checklist did not include `docs/capability-first-backend-architecture.md` as mandatory reading for high-level product/routing/spec work.
   - Fix: added the capability-first doctrine to the session-start checklist and qualified `app-descriptions`/`akka-solution-decomposition` routing with capability inventory/contract preservation.

### No cleanup required in this task

- `skills/README.md` already routes broad product input through secure AI-first SaaS, `core-saas-foundation`, and capability-first backend modeling before app-description, decomposition, PRD/backlog, or Stage 3 implementation.
- `docs/ai-first-saas-application-architecture.md` already positions capability-first backend modeling below the AI-first operating model and before Akka component or exposure-surface selection.
- `docs/capability-first-backend-architecture.md` explicitly rejects CRUD-first, endpoint-first, entity-first, and agent-tool-root design for broad product input.
- `skills/capability-first-backend/SKILL.md` correctly frames capabilities as the root backend design object and tools/endpoints/timers/consumers/workflows as exposure or realization choices.

## Checks performed

- Searched doctrine/routing files for broad-input routing language, `CRUD`, direct component routing, Stage 3 entry points, and agent-tool guidance.
- Verified no reviewed broad input path bypasses capability-first modeling without a qualifying statement that the capability contract is already clear enough.
- Verified reviewed doctrine does not say all capabilities should be agent-tool exposed.

## Residual work

No new residual doctrine/routing tasks are required from this review. Remaining Sprint 5 tasks should review narrower app-description/decomposition paths, component skills, examples/tests, and duplicate content as already queued.
