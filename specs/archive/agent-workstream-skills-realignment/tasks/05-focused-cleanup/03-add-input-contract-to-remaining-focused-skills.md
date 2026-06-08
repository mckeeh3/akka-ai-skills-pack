# TASK-AWSR-05-003: Add input-contract gates to remaining focused skills

## Goal

Add the standard generated SaaS input-contract gate to remaining high-use focused skills that can still be used mechanics-first.

## Required reads

- `specs/agent-workstream-skills-realignment/implementation-skill-gap-matrix.md`
- `specs/agent-workstream-skills-realignment/sprint-03-review.md`
- representative existing gated skill: `skills/akka-web-ui-api-client/SKILL.md`
- candidate skills below

## Candidate skills

Prioritize compact updates to these source skills:

- `skills/akka-agent-behavior-profiles/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- `skills/akka-agent-prompt-governance/SKILL.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-work-trace/SKILL.md`
- `skills/akka-http-endpoint-jwt/SKILL.md`
- `skills/akka-http-endpoint-sse/SKILL.md`
- `skills/akka-http-endpoint-web-ui/SKILL.md`
- `skills/akka-workflow-component/SKILL.md`
- `skills/akka-workflow-testing/SKILL.md`
- `skills/akka-view-testing/SKILL.md`
- `skills/akka-web-ui-frontend-project/SKILL.md`
- `skills/akka-web-ui-state-rendering/SKILL.md`
- `skills/akka-web-ui-ux-design/SKILL.md`

## Work

1. Add a `## Generated SaaS input contract` or equivalent compact gate near the top of each selected skill.
2. Tailor wording to each skill family; avoid bloated repetition where the skill already has similar language.
3. If a candidate skill already has an equivalent hard gate, document it in the task notes rather than editing unnecessarily.
4. Update this task entry in `pending-tasks.md` before committing.

## Required checks

- `git diff --check`
- `rg -n "Generated SaaS input contract"` over touched candidate skills

## Done criteria

- Remaining high-use focused skills no longer invite mechanics-first generated SaaS implementation.
- Queue status is updated.
- One git commit is created.

## Suggested commit message

`Add remaining generated SaaS input gates`
