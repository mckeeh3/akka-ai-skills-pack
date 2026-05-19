# TASK-CORE-03-003: Harden hybrid Akka agent runtime contract

## Purpose

Document and/or update skills so the code-level handoff between governed tenant records and Java Akka Agent classes is explicit.

## Required reads

- `specs/core-app-full-stack-readiness/agent-admin-runtime-gap-inventory.md`
- `docs/agent-runtime-invocation-pattern.md`
- `skills/akka-agent-component/SKILL.md`
- `skills/akka-agent-behavior-profiles/SKILL.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-tool-boundaries/SKILL.md`
- `skills/akka-agent-testing/SKILL.md`
- `src/main/java/com/example/application/agentfoundation/ReferenceAgentRuntimeResolver.java`

## Expected outputs

- `specs/core-app-full-stack-readiness/hybrid-akka-agent-runtime-contract.md`
- skill/doc updates if gaps are found

## Required checks

- Contract covers resolver inputs/outputs, component clients, prompt injection, compact manifest, `readSkill`, tool-boundary interception, Java Agent invocation, runtime/test/replay/evaluation modes, trace emission, safe denials, and tests.
- `git diff --check`

## Done criteria

- Future code tasks know exactly how static agent code and governed behavior records integrate.
- Queue status and changes are committed.
