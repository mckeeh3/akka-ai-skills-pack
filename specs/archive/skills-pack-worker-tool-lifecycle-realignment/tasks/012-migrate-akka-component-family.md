# TASK-012: Migrate Akka component skill family

## Scope

Migrate entity, workflow, view, consumer, timer, and related Akka component skills so they fit the compile contract without losing API-specific implementation guidance.

## Required reads

- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `skills-pack/docs/app-worker-tool-model.md`
- `skills-pack/docs/capability-first-backend-architecture.md`
- `skills-pack/skills/akka-key-value-entities/SKILL.md`
- `skills-pack/skills/akka-kve-*/SKILL.md`
- `skills-pack/skills/akka-event-sourced-entities/SKILL.md`
- `skills-pack/skills/akka-ese-*/SKILL.md`
- `skills-pack/skills/akka-workflows/SKILL.md`
- `skills-pack/skills/akka-workflow-*/SKILL.md`
- `skills-pack/skills/akka-views/SKILL.md`
- `skills-pack/skills/akka-view-*/SKILL.md`
- `skills-pack/skills/akka-consumers/SKILL.md`
- `skills-pack/skills/akka-consumer-*/SKILL.md`
- `skills-pack/skills/akka-timed-actions/SKILL.md`
- `skills-pack/skills/akka-timed-action-*/SKILL.md`
- `skills-pack/skills/akka-timers-scheduling/SKILL.md`

## Expected outputs

- Updated Akka component family skills.
- Component skills preserve API-specific mechanics while referencing the compile contract for why/when the component exists.

## Done criteria

- Component skills require accepted capability/tool/component context before implementation, unless doing isolated doc/example maintenance.
- Shared validation/idempotency/tenant/audit guidance is routed to canonical docs rather than repeated excessively.
- KVE/ESE/API-specific differences remain clear.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
