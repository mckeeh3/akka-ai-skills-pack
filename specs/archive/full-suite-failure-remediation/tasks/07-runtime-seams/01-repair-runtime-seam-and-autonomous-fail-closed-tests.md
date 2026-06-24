# TASK-FSFR-07-001: Repair runtime seam and autonomous fail-closed tests

## Purpose

Resolve runtime seam/autonomous-agent test failures without substituting fake runtime success.

## Required reads

- `AGENTS.md`
- `specs/full-suite-failure-remediation/README.md`
- `specs/full-suite-failure-remediation/failure-inventory.md`
- Workstream runtime, Audit/Trace autonomous runtime, and My Account smoke source/test files named by the inventory

## Skills

- `akka-agent-component`
- `akka-autonomous-agents`
- `akka-autonomous-agent-testing`
- `akka-runtime-feature-verification`

## Expected outputs

- implementation/test/current-intent repair or split follow-up tasks for:
  - concrete WorkstreamRuntimeAgent seam expectation
  - Audit/Trace summary autonomous runtime fail-closed behavior
  - My Account browser smoke runtime errors when owned by runtime harness
- queue update

## Required checks

- `git diff --check`
- targeted runtime seam/autonomous/browser smoke tests named by inventory

## Done criteria

- Runtime tests either pass through real/test Akka runtime boundaries or are split into precise blockers.
- No model-less or fake runtime path is counted as normal success.
- Changes and queue update are committed.
