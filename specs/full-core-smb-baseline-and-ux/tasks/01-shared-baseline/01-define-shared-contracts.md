# Task: Define executable shared baseline contracts and validation map

## Objective

Create a focused shared contract document that translates the umbrella SMB baseline and visual UX standard into implementation-ready contracts for the starter template and synchronized root frontend.

## Required reads

- `AGENTS.md`
- `specs/full-core-smb-baseline-and-ux/README.md`
- `specs/full-core-smb-baseline-and-ux/conversation-capture.md`
- `specs/full-core-smb-baseline-and-ux/sprints/01-shared-baseline-ux-sprint.md`
- `specs/full-core-smb-baseline-and-ux/backlog/01-shared-baseline-ux-backlog.md`
- `specs/full-core-smb-saas-hardening/smb-full-core-baseline.md`
- `specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md`
- `specs/full-core-smb-saas-hardening/workstream-full-core-outline.md`

## In scope

- Shared shell, structured-surface, `system_message`, action/capability, trace, attention, provider-blocked, visual, responsive/accessibility, and runtime-validation contracts.
- A validation map naming the checks future implementation tasks should run or refine.

## Out of scope

- Implementing starter source changes.
- Expanding enterprise scope.

## Expected outputs

- `specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md`
- updated `specs/full-core-smb-baseline-and-ux/pending-tasks.md`

## Checks

- `git diff --check`
- `rg -n "workstream shell|structured surface|system_message|provider|trace|visual|runtime validation|secret-boundary" specs/full-core-smb-baseline-and-ux`

## Done criteria

- Contracts are specific enough for workstream children to inherit.
- Validation map refuses deterministic/demo/model-less normal runtime completion for model-backed paths.
- Changes and queue update are committed.
