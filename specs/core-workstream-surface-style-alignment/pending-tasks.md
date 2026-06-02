# Pending Tasks: Core Workstream Surface Style Alignment

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Read this mini-project's README, conversation capture, selected sprint, selected backlog, selected task entry, and task brief before editing.
- Update this file before finishing the harness response.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `ui-theme: <short task title>`.

## Tasks

### TASK-CWSS-00-001: Create core workstream surface style alignment planning scaffold

- status: done
- source: user request to create a follow-up mini-project after finding core app workstream surface docs were not fully updated by the web UI style/theme refresh
- task brief: specs/core-workstream-surface-style-alignment/tasks/00-planning/00-create-core-workstream-surface-style-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/pending-task-queue.md
  - docs/pending-question-queue.md
  - specs/web-ui-style-theme-refresh/verification-notes.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - specs/core-workstream-surface-style-alignment/README.md
  - specs/core-workstream-surface-style-alignment/conversation-capture.md
  - specs/core-workstream-surface-style-alignment/pending-tasks.md
  - specs/core-workstream-surface-style-alignment/sprints/*.md
  - specs/core-workstream-surface-style-alignment/backlog/*.md
  - specs/core-workstream-surface-style-alignment/tasks/**/*.md
- required checks:
  - `git diff --check`
- done criteria:
  - mini-project has captured rationale, sprint sequence, backlogs, task briefs, and pending queue
  - task changes and queue update are committed
- notes:
  - commit message: `ui-theme: add core workstream surface style queue`

### TASK-CWSS-01-001: Align core overview and My Account surface style

- status: done
- source: specs/core-workstream-surface-style-alignment/backlog/01-core-domain-surfaces-build-backlog.md
- task brief: specs/core-workstream-surface-style-alignment/tasks/01-core-domain-surfaces/01-align-overview-my-account-surfaces.md
- depends on:
  - TASK-CWSS-00-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-workstream-surface-style-alignment/README.md
  - specs/core-workstream-surface-style-alignment/conversation-capture.md
  - specs/core-workstream-surface-style-alignment/pending-tasks.md
  - specs/core-workstream-surface-style-alignment/sprints/01-core-domain-surfaces-sprint.md
  - specs/core-workstream-surface-style-alignment/backlog/01-core-domain-surfaces-build-backlog.md
  - specs/core-workstream-surface-style-alignment/tasks/01-core-domain-surfaces/01-align-overview-my-account-surfaces.md
  - docs/web-ui-style-guide.md
  - docs/examples/ai-first-saas-core-app-domain/README.md
  - docs/examples/ai-first-saas-core-app-domain/my-account-workstream/README.md
- skills:
  - app-description-ui
- expected outputs:
  - updated docs/examples/ai-first-saas-core-app-domain/README.md
  - updated docs/examples/ai-first-saas-core-app-domain/my-account-workstream/README.md
  - updated specs/core-workstream-surface-style-alignment/pending-tasks.md
- required checks:
  - `git diff --check`
  - focused stale-language search from task brief
- done criteria:
  - overview names or links the replacement style/theme contract
  - My Account settings surface uses named-theme selection with available theme ids and `preferredThemeId` semantics
  - no stale mode-first or old default style contradiction remains in touched files
  - task changes and queue update are committed
- notes:
  - commit message: `ui-theme: align core my account surface style`
  - checks: `git diff --check`; focused stale-language search over overview and My Account docs passed

### TASK-CWSS-01-002: Align User Admin and Agent Admin surface style

- status: done
- source: specs/core-workstream-surface-style-alignment/backlog/01-core-domain-surfaces-build-backlog.md
- task brief: specs/core-workstream-surface-style-alignment/tasks/01-core-domain-surfaces/02-align-admin-workstream-surfaces.md
- depends on:
  - TASK-CWSS-01-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-workstream-surface-style-alignment/README.md
  - specs/core-workstream-surface-style-alignment/conversation-capture.md
  - specs/core-workstream-surface-style-alignment/pending-tasks.md
  - specs/core-workstream-surface-style-alignment/sprints/01-core-domain-surfaces-sprint.md
  - specs/core-workstream-surface-style-alignment/backlog/01-core-domain-surfaces-build-backlog.md
  - specs/core-workstream-surface-style-alignment/tasks/01-core-domain-surfaces/02-align-admin-workstream-surfaces.md
  - docs/web-ui-style-guide.md
  - docs/examples/ai-first-saas-core-app-domain/user-admin-workstream/README.md
  - docs/examples/ai-first-saas-core-app-domain/agent-admin-workstream/README.md
- skills:
  - app-description-ui
  - ai-first-saas-ui-surfaces
- expected outputs:
  - updated docs/examples/ai-first-saas-core-app-domain/user-admin-workstream/README.md
  - updated docs/examples/ai-first-saas-core-app-domain/agent-admin-workstream/README.md
  - updated specs/core-workstream-surface-style-alignment/pending-tasks.md
- required checks:
  - `git diff --check`
  - focused stale style/theme search over touched files
- done criteria:
  - User Admin and Agent Admin surfaces include enterprise workstream style expectations
  - touched docs contain no stale mode-first or old default style contradictions
  - task changes and queue update are committed
- notes:
  - commit message: `ui-theme: align admin workstream surface style`
  - checks: `git diff --check`; focused stale style/theme search over touched files passed

### TASK-CWSS-01-003: Align Audit/Trace and Governance/Policy surface style

- status: done
- source: specs/core-workstream-surface-style-alignment/backlog/01-core-domain-surfaces-build-backlog.md
- task brief: specs/core-workstream-surface-style-alignment/tasks/01-core-domain-surfaces/03-align-audit-governance-surfaces.md
- depends on:
  - TASK-CWSS-01-002
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-workstream-surface-style-alignment/README.md
  - specs/core-workstream-surface-style-alignment/conversation-capture.md
  - specs/core-workstream-surface-style-alignment/pending-tasks.md
  - specs/core-workstream-surface-style-alignment/sprints/01-core-domain-surfaces-sprint.md
  - specs/core-workstream-surface-style-alignment/backlog/01-core-domain-surfaces-build-backlog.md
  - specs/core-workstream-surface-style-alignment/tasks/01-core-domain-surfaces/03-align-audit-governance-surfaces.md
  - docs/web-ui-style-guide.md
  - docs/examples/ai-first-saas-core-app-domain/audit-trace-workstream/README.md
  - docs/examples/ai-first-saas-core-app-domain/governance-policy-workstream/README.md
- skills:
  - app-description-ui
  - ai-first-saas-ui-surfaces
  - ai-first-saas-audit-trace
  - ai-first-saas-policy-governance
- expected outputs:
  - updated docs/examples/ai-first-saas-core-app-domain/audit-trace-workstream/README.md
  - updated docs/examples/ai-first-saas-core-app-domain/governance-policy-workstream/README.md
  - updated specs/core-workstream-surface-style-alignment/pending-tasks.md
- required checks:
  - `git diff --check`
  - focused stale style/theme search over touched files
- done criteria:
  - Audit/Trace and Governance/Policy surfaces include enterprise workstream style expectations for evidence, trace, decision, and governance surfaces
  - touched docs contain no stale mode-first or old default style contradictions
  - task changes and queue update are committed
- notes:
  - commit message: `ui-theme: align audit governance surface style`
  - checks: `git diff --check`; focused stale style/theme search over touched files passed

### TASK-CWSS-02-001: Review seed surface style alignment

- status: done
- source: specs/core-workstream-surface-style-alignment/backlog/02-seed-surface-style-build-backlog.md
- task brief: specs/core-workstream-surface-style-alignment/tasks/02-seed-surface-style/01-review-seed-surface-style-alignment.md
- depends on:
  - TASK-CWSS-01-003
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-workstream-surface-style-alignment/README.md
  - specs/core-workstream-surface-style-alignment/conversation-capture.md
  - specs/core-workstream-surface-style-alignment/pending-tasks.md
  - specs/core-workstream-surface-style-alignment/sprints/02-seed-surface-style-sprint.md
  - specs/core-workstream-surface-style-alignment/backlog/02-seed-surface-style-build-backlog.md
  - specs/core-workstream-surface-style-alignment/tasks/02-seed-surface-style/01-review-seed-surface-style-alignment.md
  - docs/web-ui-style-guide.md
  - templates/ai-first-saas-starter/app-description/app-description/55-ui/style-guide.md
  - templates/ai-first-saas-starter/app-description/app-description/12-workstreams/surface-contracts/*.md
- skills:
  - app-description-ui
  - app-description-surface-modeling
- expected outputs:
  - updated seed surface contract docs if gaps are found
  - optional findings note
  - updated specs/core-workstream-surface-style-alignment/pending-tasks.md
- required checks:
  - `git diff --check`
  - stale style/theme search over seed 12-workstreams and 55-ui files
- done criteria:
  - seed surface contracts are consistent with canonical style guide and updated core domain docs
  - any remaining larger gaps are queued as follow-up or explicitly documented as non-material
  - task changes and queue update are committed
- notes:
  - commit message: `ui-theme: review seed surface style alignment`
  - checks: `git diff --check`; focused stale style/theme search over seed 12-workstreams and 55-ui files found only non-stale canonical style-system/font/system-message references and the explicit My Account rejection of mode labels

### TASK-CWSS-99-001: Verify core workstream surface style alignment completion

- status: done
- source: mini-project verification loop
- task brief: specs/core-workstream-surface-style-alignment/tasks/99-verification/01-verify-core-workstream-surface-style-alignment.md
- depends on:
  - TASK-CWSS-01-001
  - TASK-CWSS-01-002
  - TASK-CWSS-01-003
  - TASK-CWSS-02-001
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/core-workstream-surface-style-alignment/README.md
  - specs/core-workstream-surface-style-alignment/conversation-capture.md
  - specs/core-workstream-surface-style-alignment/pending-tasks.md
  - specs/core-workstream-surface-style-alignment/sprints/*.md
  - specs/core-workstream-surface-style-alignment/backlog/*.md
  - specs/core-workstream-surface-style-alignment/tasks/**/*.md
  - docs/web-ui-style-guide.md
  - docs/examples/ai-first-saas-core-app-domain/**/*.md
  - templates/ai-first-saas-starter/app-description/app-description/12-workstreams/surface-contracts/*.md
  - templates/ai-first-saas-starter/app-description/app-description/55-ui/style-guide.md
- skills:
  - none; repository verification task
- expected outputs:
  - updated specs/core-workstream-surface-style-alignment/pending-tasks.md
  - specs/core-workstream-surface-style-alignment/verification-notes.md
  - follow-up task briefs if needed
- required checks:
  - `git diff --check`
  - stale contradiction searches from task brief
- done criteria:
  - sprint goals and mini-project done state have been compared against completed work
  - if complete, completion is recorded with no new required work
  - if incomplete, new bounded tasks are appended before a new terminal verification task
  - verification changes and queue update are committed
- notes:
  - commit message: `ui-theme: verify core workstream surface style alignment`
  - checks: `git diff --check`; stale contradiction search found only explicit reject-list/task wording and the intended My Account rejection of mode-first theme labels; positive style/theme search confirmed coverage; no follow-up tasks required
