# Sprint 05: Review and Hardening

## Objective

Review the pack end to end for stale paths that allow a functional agent to be declared complete without governed workstream expertise. Add only the smallest remaining fixes or a follow-up sprint if gaps remain.

## Scope

Likely source files:

- `skills/README.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/workstream-expertise-model.md` if created
- `docs/agent-coverage-matrix.md`
- `skills/akka-agents/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md`
- `skills/app-description-readiness-assessment/SKILL.md`
- seed/starter docs touched in earlier sprints
- installed-pack parity checks only as read-only dogfooding input when `.agents/` is present

## Deliverables

- Consistency review with remaining gaps, decisions, and suggested next sprint if needed.
- Final cleanup edits for terminology and routing consistency.
- Pending queue updated: remaining tasks added, blocked, deferred, or marked complete according to review findings.

## Checks

- `git diff --check`
- Repository text search for old patterns that imply prompt-only or generic-chatbot functional agents are enough.
