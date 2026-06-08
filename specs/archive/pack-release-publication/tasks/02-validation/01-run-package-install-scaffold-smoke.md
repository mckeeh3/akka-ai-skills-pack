# TASK-PRP-02-001: Run package install scaffold smoke

## Objective

Run package/install/scaffold smoke validation for the release-ready pack resources.

## Required checks

- `git diff --check`
- package/install command checks appropriate for this repo
- scaffold smoke from packaged resources or equivalent installed-pack path
- focused scans confirming starter scaffold files and docs are present where expected

## Expected outputs

- package smoke validation artifact
- updated pending queue

## Commit message

`pack-release: run package smoke`
