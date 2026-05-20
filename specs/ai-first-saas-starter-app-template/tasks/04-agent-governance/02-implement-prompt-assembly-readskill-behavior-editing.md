# TASK-STARTER-04-002: Implement prompt assembly, readSkill, and behavior-editing flow

## Purpose

Make governed agent behavior executable: prompt assembly, skill loading authorization, traces, and behavior editing proposals.

## Required reads

- `docs/agent-runtime-invocation-pattern.md`
- `specs/core-app-full-stack-readiness/hybrid-akka-agent-runtime-contract.md`
- `skills/akka-agent-runtime-state/SKILL.md`
- `skills/akka-agent-tools/SKILL.md`
- `skills/akka-agent-testing/SKILL.md`

## Expected outputs

- Deterministic prompt assembly service/capability.
- Authorized `readSkill(skillId)` capability.
- Behavior editing proposal/review/activation flow.
- Denial and trace tests.

## Done criteria

- Prompt/skill content cannot grant authority.
- Skill loads, prompt assembly, and consequential agent work create traces.
- Unauthorized skill reads, disabled agents, and authority expansion attempts are denied and tested.
- Required checks pass, queue status is updated, and changes are committed.
