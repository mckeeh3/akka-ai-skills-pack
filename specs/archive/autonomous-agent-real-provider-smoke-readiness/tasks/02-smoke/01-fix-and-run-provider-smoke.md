# TASK-AARPS-02-001: Fix and run provider smoke readiness checks

## Objective

Apply the bounded fix from diagnosis and run provider-skip plus real-provider smoke checks where configuration is available.

## In scope

- Fix brittle assertions or configuration handling if diagnosis identifies local code/test issues.
- Preserve fail-closed absent-provider behavior.
- Do not convert real-provider failure into fake/model-less success.

## Required checks

- `git diff --check`
- provider-skip fullstack validation
- real-provider smoke if configured, or documented skipped/blocked reason if not configured
- focused scans for provider fail-closed and no fake success guardrails

## Commit message

`autonomous-agent-smoke: fix provider smoke readiness`
