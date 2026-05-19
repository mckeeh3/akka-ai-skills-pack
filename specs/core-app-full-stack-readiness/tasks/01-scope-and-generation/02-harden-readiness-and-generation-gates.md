# TASK-CORE-01-002: Harden readiness and generation gates

## Purpose

Ensure app-description, generation, decomposition, and PRD/backlog paths block or explicitly label incomplete core scope.

## Required reads

- `specs/core-app-full-stack-readiness/full-core-realization-map.md`
- `skills/app-description-readiness-assessment/SKILL.md`
- `skills/app-generate-app/SKILL.md`
- `skills/app-description-bootstrap/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `skills/akka-solution-decomposition/SKILL.md`

## Expected outputs

- updates to readiness/generation/planning skills
- optional doc update if a reusable full-core readiness rubric is needed

## Required checks

- `rg -n "full core|Module 1-only|User Admin|Agent Admin|Invitation|AgentDefinition|workstream" skills/app-description-readiness-assessment/SKILL.md skills/app-generate-app/SKILL.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-solution-decomposition/SKILL.md`
- `git diff --check`

## Done criteria

- Full core cannot silently omit User Admin, Agent Admin, invitation onboarding, workstream UI, governed runtime agents, or tests.
- Queue status and changes are committed.
