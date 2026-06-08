# TASK-AWSR-01-002: Align AI-first and agent-workstream routing

## Goal

Update the top-level AI-first SaaS and agent-workstream routing guidance so broad generated-SaaS intent always hands downstream work a functional-agent/workstream/surface model before capability/component selection.

## Required reads

- `specs/agent-workstream-skills-realignment/routing-gap-matrix.md`
- `skills/README.md`
- `skills/ai-first-saas/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/agent-workstream-design-review-checklist.md`

## Work

1. Apply the highest-priority routing changes from the gap matrix for `ai-first-saas`, `agent-workstream-apps`, and `skills/README.md`.
2. Ensure the output/handoff sections require:
   - functional agents;
   - internal agents when applicable;
   - initial workstreams;
   - structured surfaces;
   - surface action-to-capability mappings;
   - downstream skills to load.
3. Avoid broad rewrites unrelated to routing.
4. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`
- text search confirming touched routing files mention functional agents, surfaces, capabilities, and Akka components in the intended order

## Done criteria

- Top-level routing makes the workstream model the normal handoff for generated SaaS apps.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Align AI-first workstream routing`
