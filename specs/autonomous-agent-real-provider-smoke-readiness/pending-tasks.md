# Pending Tasks: AutonomousAgent Real-Provider Smoke Readiness

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Read this mini-project's README, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `autonomous-agent-smoke: <short task title>`.

## Tasks

### TASK-AARPS-00-001: Create real-provider smoke readiness queue

- status: done
- source: current conversation after My Account Personal Attention Digest completion; handoff records a local real-provider smoke blocker
- task brief: specs/autonomous-agent-real-provider-smoke-readiness/tasks/00-planning/00-create-real-provider-smoke-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - .agents/skills/project-discussed-idea-to-pending-project/SKILL.md
  - specs/my-account-personal-attention-digest-autonomous-agent/my-account-personal-attention-digest-handoff.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/autonomous-agent-real-provider-smoke-readiness/README.md
  - specs/autonomous-agent-real-provider-smoke-readiness/conversation-capture.md
  - specs/autonomous-agent-real-provider-smoke-readiness/pending-tasks.md
  - specs/autonomous-agent-real-provider-smoke-readiness/sprints/01-real-provider-smoke-sprint.md
  - specs/autonomous-agent-real-provider-smoke-readiness/backlog/01-real-provider-smoke-backlog.md
  - specs/autonomous-agent-real-provider-smoke-readiness/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project scaffold and queue exist
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-smoke: add readiness queue`

### TASK-AARPS-01-001: Diagnose My Account digest real-provider smoke failure

- status: done
- source: specs/autonomous-agent-real-provider-smoke-readiness/backlog/01-real-provider-smoke-backlog.md
- task brief: specs/autonomous-agent-real-provider-smoke-readiness/tasks/01-diagnosis/01-diagnose-my-account-digest-provider-smoke.md
- depends on:
  - TASK-AARPS-00-001
- required reads:
  - specs/autonomous-agent-real-provider-smoke-readiness/README.md
  - specs/autonomous-agent-real-provider-smoke-readiness/tasks/01-diagnosis/01-diagnose-my-account-digest-provider-smoke.md
  - specs/my-account-personal-attention-digest-autonomous-agent/my-account-personal-attention-digest-handoff.md
  - specs/my-account-personal-attention-digest-autonomous-agent/validation/01-personal-attention-digest-validation.md
- skills:
  - none; diagnosis task
- expected outputs:
  - diagnosis artifact under specs/autonomous-agent-real-provider-smoke-readiness/
  - updated pending-tasks.md
- output artifact:
  - specs/autonomous-agent-real-provider-smoke-readiness/01-diagnose-my-account-digest-provider-smoke.md
- required checks:
  - `git diff --check`
  - provider-skip validation or targeted tests sufficient to reproduce context
  - real-provider smoke only if configured; otherwise record missing-config blocked state
- done criteria:
  - failure cause is classified or missing configuration is recorded explicitly
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-smoke: diagnose digest provider smoke`

### TASK-AARPS-02-001: Fix and run provider smoke readiness checks

- status: done
- source: specs/autonomous-agent-real-provider-smoke-readiness/backlog/01-real-provider-smoke-backlog.md
- task brief: specs/autonomous-agent-real-provider-smoke-readiness/tasks/02-smoke/01-fix-and-run-provider-smoke.md
- depends on:
  - TASK-AARPS-01-001
- required reads:
  - diagnosis artifact from TASK-AARPS-01-001
  - specs/autonomous-agent-real-provider-smoke-readiness/tasks/02-smoke/01-fix-and-run-provider-smoke.md
- skills:
  - none; smoke/readiness task
- expected outputs:
  - bounded fix or documented external provider/config blocker
  - validation artifact
  - updated pending-tasks.md
- output artifact:
  - specs/autonomous-agent-real-provider-smoke-readiness/02-fix-and-run-provider-smoke.md
- required checks:
  - `git diff --check`
  - provider-skip fullstack validation
  - real-provider smoke if configured, or documented skipped/blocked reason if not configured
  - focused scans for provider fail-closed and no fake success guardrails
- done criteria:
  - provider smoke readiness state is accurate and no fake success path is introduced
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-smoke: fix provider smoke readiness`

### TASK-AARPS-03-001: Update real-provider smoke docs

- status: done
- source: specs/autonomous-agent-real-provider-smoke-readiness/backlog/01-real-provider-smoke-backlog.md
- task brief: specs/autonomous-agent-real-provider-smoke-readiness/tasks/03-docs/01-update-real-provider-smoke_docs.md
- depends on:
  - TASK-AARPS-02-001
- required reads:
  - diagnosis/fix artifacts from prior tasks
- skills:
  - none; docs task
- expected outputs:
  - smoke docs/handoff updates
  - updated pending-tasks.md
- required checks:
  - `git diff --check`
  - focused `rg` proving docs cover provider configuration, provider-skip validation, real-provider smoke, fail-closed behavior, and no fake success
- done criteria:
  - future agents can run or interpret real-provider smoke safely
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-smoke: update smoke docs`
  - completion note: Updated mini-project guidance, starter README provider-smoke instructions, My Account digest handoff, and release handoff with provider configuration, provider-skip validation, configured real-provider smoke, fail-closed behavior, troubleshooting, and no fake/model-less success guardrails.
  - checks: `git diff --check`; focused `rg -n "OPENAI_API_KEY|provider-skip|real-provider|real provider|fail-closed|fake|model-less|smoke-ai-first-saas-starter-real-model|RealModelProviderSmokeTest|provider configuration" specs/autonomous-agent-real-provider-smoke-readiness/README.md templates/ai-first-saas-starter/README.md specs/my-account-personal-attention-digest-autonomous-agent/my-account-personal-attention-digest-handoff.md specs/full-core-smb-polish-release-readiness/release-handoff.md`

### TASK-AARPS-99-001: Verify real-provider smoke readiness

- status: done
- source: mini-project verification loop
- task brief: specs/autonomous-agent-real-provider-smoke-readiness/tasks/99-verification/01-verify-real-provider-smoke-readiness.md
- depends on:
  - TASK-AARPS-01-001
  - TASK-AARPS-02-001
  - TASK-AARPS-03-001
- required reads:
  - all mini-project artifacts
- skills:
  - none; verification task
- expected outputs:
  - updated pending-tasks.md
  - optional follow-up tasks if gaps remain
- output artifact:
  - specs/autonomous-agent-real-provider-smoke-readiness/99-verify-real-provider-smoke-readiness.md
- required checks:
  - `git diff --check`
  - review diagnosis/fix/docs evidence
  - provider-skip validation evidence
  - real-provider smoke evidence or documented external blocker
  - focused scans for fail-closed/no fake success guardrails
- done criteria:
  - mini-project done state is assessed
  - if incomplete, bounded follow-up tasks and a new terminal verification task are appended
  - task changes and queue update are committed
- notes:
  - commit message: `autonomous-agent-smoke: verify readiness`
  - completion note: Verified provider-skip validation, configured real-provider smoke, docs/handoff evidence, and fail-closed/no-fake-success guardrail scans. No follow-up tasks are required.
  - checks: `git diff --check`; `env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh --keep --base-package ai.first --maven-group-id ai.first`; `tools/smoke-ai-first-saas-starter-real-model.sh --keep --base-package ai.first --maven-group-id ai.first`; focused guardrail/doc `rg` scans.
