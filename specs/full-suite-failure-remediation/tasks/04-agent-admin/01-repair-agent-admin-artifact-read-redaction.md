# TASK-FSFR-04-001: Repair Agent Admin artifact read/redaction mismatch

## Purpose

Fix the Agent Admin artifact read surface/redaction mismatch documented by the full-suite failure inventory.

## Required reads

- `AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- Agent Admin source/test files named by the inventory
- `app-description/domains/core-starter/workstreams/agent-admin/**`

## Skills

- `akka-agent-behavior-profiles`
- `akka-agent-prompt-governance`
- `akka-agent-skill-governance`
- `akka-agent-reference-governance`
- `akka-agent-work-trace`

## Expected outputs

- implementation/test/current-intent repair for backend-authoritative Agent Admin artifact reads
- queue update

## Required checks

- `git diff --check`
- targeted Agent Admin artifact read test
- related seed/import tests if seed or manifest behavior changes

## Done criteria

- Artifact reads remain backend-authoritative and redacted.
- Surface ids/result surfaces match accepted current behavior.
- Changes and queue update are committed.
