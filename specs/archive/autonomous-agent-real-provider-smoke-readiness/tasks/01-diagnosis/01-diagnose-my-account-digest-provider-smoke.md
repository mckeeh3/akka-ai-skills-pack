# TASK-AARPS-01-001: Diagnose My Account digest real-provider smoke failure

## Objective

Diagnose the recorded My Account Personal Attention Digest real-provider smoke failure and identify whether it is a brittle assertion, configuration issue, provider-output shape issue, or runtime bug.

## Required reads

- mini-project README/conversation/queue entry and this task brief
- `specs/my-account-personal-attention-digest-autonomous-agent/my-account-personal-attention-digest-handoff.md`
- `specs/my-account-personal-attention-digest-autonomous-agent/validation/01-personal-attention-digest-validation.md`
- starter real-provider/fullstack validation scripts and My Account digest tests

## Expected outputs

- diagnosis artifact under this mini-project
- updated pending queue

## Required checks

- `git diff --check`
- provider-skip validation or targeted tests sufficient to reproduce context
- real-provider smoke only if configured; otherwise record missing-config blocked state

## Commit message

`autonomous-agent-smoke: diagnose digest provider smoke`
