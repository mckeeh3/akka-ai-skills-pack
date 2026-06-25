# Pending Tasks: Skills-Pack Comprehensive Review

## Queue rules

- Use `file-review-inventory.md` as the per-file task list.
- Execute one small batch of inventory entries per fresh-context subagent; default batch size is 10 pending source-authoritative rows.
- Do not run file-review subagents in parallel.
- Mark selected inventory entries `in-progress` before review edits.
- Mark inventory entries terminal only after checks pass or a clear blocker/follow-up is recorded.
- Commit each completed batch review and inventory update together.
- The parent orchestrator must loop: after each batch subagent completes, inspect the result, select the next pending batch, and launch the next single subagent until the inventory has no `pending` or `in-progress` rows.

## Tasks

### TASK-001: Review all inventory entries in sequential small batches

- status: pending
- source: specs/skills-pack-comprehensive-review/file-review-inventory.md
- task brief: specs/skills-pack-comprehensive-review/subagent-file-review-brief.md
- depends on: []
- required reads:
  - specs/skills-pack-comprehensive-review/README.md
  - specs/skills-pack-comprehensive-review/review-guide.md
  - specs/skills-pack-comprehensive-review/subagent-file-review-brief.md
  - specs/skills-pack-comprehensive-review/file-review-inventory.md
- skills:
  - project-discussed-idea-to-pending-project
  - akka-pending-task-queue-maintenance
- expected outputs:
  - up to 10 reviewed inventory entries per fresh-context subagent run by default
  - target file edits/archive/removal only when selected entries require it
  - committed inventory status/notes update per reviewed batch
- required checks:
  - `git diff --check`
  - install/manifest/reference checks as required by the selected file decision
- done criteria:
  - every row in `file-review-inventory.md` has a terminal status
- notes:
  - scope: skills-pack comprehensive source review
  - vertical contract: cross-cutting pack maintenance; docs/tools/examples/templates/metadata review; no root runtime feature
  - runtime evidence: not applicable; pack-maintenance review task
  - execution model: do not mark this task `done` until the inventory has no `pending` or `in-progress` rows
  - batching: source-authoritative rows default to 10 per subagent; reduce to 1 for high-risk broad doctrine/installer/archive/removal work; installed-output mirror rows may use larger verification batches only after source review is complete

### TASK-002: Terminal verification after file inventory review

- status: pending
- source: specs/skills-pack-comprehensive-review/README.md
- task brief: none
- depends on: [TASK-001]
- required reads:
  - specs/skills-pack-comprehensive-review/README.md
  - specs/skills-pack-comprehensive-review/review-guide.md
  - specs/skills-pack-comprehensive-review/file-review-inventory.md
  - skills-pack/docs/skill-consolidation-and-pruning.md
- skills:
  - akka-pending-task-queue-maintenance
- expected outputs:
  - final review summary or appended follow-up tasks
  - inventory status consistency check
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
  - `bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh` when practical
- done criteria:
  - mini-project done state is verified or follow-up tasks are appended with a replacement terminal verification task
- notes:
  - scope: terminal pack-maintenance verification
  - vertical contract: cross-cutting pack verification; no root runtime feature
  - runtime evidence: not applicable; pack-maintenance task
