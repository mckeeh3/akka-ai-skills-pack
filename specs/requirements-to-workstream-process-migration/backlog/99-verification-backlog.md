# Backlog 99: Verification

## Goal

Verify completion, identify residual drift, and append follow-up tasks if needed.

## Suggested task breakdown

1. Run final cross-repository review for process drift.
2. Update pending queue with completion notes or follow-up tasks plus a new verification task.

## Required checks

- `git diff --check`
- `rg -n "CRUD-first|page-first|component-first|chatbot-bolt-on|generic Akka|requirements-to-workstream|what needs my attention|AutonomousAgent" AGENTS.md pack/AGENTS.md skills docs pack/manifest.yaml`

## Acceptance criteria

- Either the initiative is complete or gaps are captured as bounded follow-up tasks.
