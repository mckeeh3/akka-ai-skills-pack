# TASK-PRP-01-001: Review package metadata and resources

## Objective

Review package metadata, install resources, starter template inclusion, and source-only leakage boundaries.

## Required reads

- mini-project README and this task brief
- `specs/ai-first-saas-starter-release-readiness/starter-release-notes.md`
- `pack/`
- package/install/scaffold scripts and docs

## Expected outputs

- package resource review artifact
- updated pending queue

## Required checks

- `git diff --check`
- focused scans for starter template/resource references and source-only `specs/` leakage into packaged assets

## Commit message

`pack-release: review package resources`
