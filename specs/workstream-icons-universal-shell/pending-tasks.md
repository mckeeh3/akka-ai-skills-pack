# Pending Tasks: Workstream Icons Universal Shell

## Queue rules

- Execute one task per fresh harness session.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Treat this repository as the skills-pack source: changes are primarily to skills, docs, examples, specs, templates, and reference tests unless a task explicitly targets executable starter/template code.
- Update this file before finishing the harness response: set completed tasks to `done`, add a completion note, and add discovered follow-up tasks rather than expanding the current task.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `workstream-icons: <short task title>`.

## Tasks

### TASK-WSI-00-001: Create workstream icon migration queue and doctrine capture

- status: done
- completion note: Created the migration scaffold and self-contained task briefs; captured universal workstream icon doctrine, governed shell navigation, and My Account dashboard/personal queue/workstream status details in docs.
- source: user request to implement workstream icons as universal shell feature with pending-task workflow
- task brief: specs/workstream-icons-universal-shell/tasks/00-planning/00-create-workstream-icon-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - docs/examples/ai-first-saas-core-app-domain/my-account-workstream/README.md
- expected outputs:
  - specs/workstream-icons-universal-shell/README.md
  - specs/workstream-icons-universal-shell/conversation-capture.md
  - specs/workstream-icons-universal-shell/pending-tasks.md
  - specs/workstream-icons-universal-shell/tasks/**/*.md
  - docs/agent-workstream-application-architecture.md
  - docs/examples/ai-first-saas-core-app-domain/my-account-workstream/README.md
- required checks:
  - `git diff --check`
  - `rg -n "WorkstreamIconDescriptor|workstream icon|personal queue|workstream status|TASK-WSI" docs/agent-workstream-application-architecture.md docs/examples/ai-first-saas-core-app-domain/my-account-workstream/README.md specs/workstream-icons-universal-shell`
- done criteria:
  - The migration queue exists and every follow-up task is self-sufficient for a fresh harness session.
  - Universal shell doctrine captures workstream icon metadata, My Account dashboard status panels, and surface-request navigation.
  - A git commit exists for the changes.
- notes:
  - commit message: `workstream-icons: add migration queue and doctrine capture`

### TASK-WSI-01-001: Align skills and app-description guidance for workstream icons

- status: pending
- source: specs/workstream-icons-universal-shell/README.md
- task brief: specs/workstream-icons-universal-shell/tasks/01-doctrine-skills/01-align-skills-app-description-guidance.md
- depends on: [TASK-WSI-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - docs/app-description-maintenance-flow.md
  - skills/agent-workstream-apps/SKILL.md
  - skills/app-description-functional-agent-modeling/SKILL.md
  - skills/app-description-surface-modeling/SKILL.md
  - skills/app-description-ui/SKILL.md
  - skills/akka-web-ui-apps/SKILL.md
- expected outputs:
  - Update related skills/docs so new workstreams include icon metadata at definition time.
  - Require app-description `12-workstreams/` ownership of workstream icon semantics and `55-ui/` ownership of browser rendering only.
  - Clarify that buttons/links/icons opening surfaces or workstreams are governed surface-request actions.
  - Preserve My Account lower-left launcher rule and do not add My Account to the top rail.
- required checks:
  - `git diff --check`
  - `rg -n "workstream icon|WorkstreamIconDescriptor|surface-request|open_workstream|My Account" skills/README.md skills/agent-workstream-apps/SKILL.md skills/app-description-functional-agent-modeling/SKILL.md skills/app-description-surface-modeling/SKILL.md skills/app-description-ui/SKILL.md skills/akka-web-ui-apps/SKILL.md docs/app-description-maintenance-flow.md`
- done criteria:
  - Skills route future generated SaaS work through icon metadata and governed shell navigation.
  - A git commit exists for the changes.
- notes: []

### TASK-WSI-02-001: Add icon metadata contracts and fixtures to starter/reference frontend

- status: pending
- source: specs/workstream-icons-universal-shell/README.md
- task brief: specs/workstream-icons-universal-shell/tasks/02-contracts-fixtures/01-add-icon-contracts-fixtures.md
- depends on: [TASK-WSI-01-001]
- required reads:
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
  - frontend/src/workstream/types/agents.ts
  - frontend/src/workstream/types/auth.ts
  - frontend/src/workstream/fixtures/agents.ts
  - frontend/src/workstream/fixtures/me.ts
  - frontend/src/workstream.contract.test.mjs
  - templates/ai-first-saas-starter/frontend/src/workstream/types/agents.ts
  - templates/ai-first-saas-starter/frontend/src/workstream/types/auth.ts
  - templates/ai-first-saas-starter/frontend/src/workstream/fixtures/agents.ts
  - templates/ai-first-saas-starter/frontend/src/workstream/fixtures/me.ts
  - templates/ai-first-saas-starter/frontend/src/workstream.contract.test.mjs
- expected outputs:
  - Add `WorkstreamIconDescriptor` to shared frontend workstream types.
  - Replace or supplement loose `icon?: string` with typed icon descriptor metadata while preserving compatibility where necessary.
  - Seed descriptors for User Admin, Agent Admin, Audit/Trace, Governance/Policy, and fallback/hidden examples in both `frontend/` and starter template fixtures.
  - Ensure `/api/me` fixture/bootstrap data carries icon metadata for visible functional agents.
- required checks:
  - `cd frontend && npm test -- --run src/workstream.contract.test.mjs src/workstream-shell.contract.test.mjs`
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --run src/workstream.contract.test.mjs src/workstream-shell.contract.test.mjs`
  - `git diff --check`
  - `rg -n "WorkstreamIconDescriptor|workstreamIcon|iconId|accentColorToken|ariaLabel" frontend/src templates/ai-first-saas-starter/frontend/src`
- done criteria:
  - Type and fixture contracts expose icon metadata for the four top-rail core v0 workstreams.
  - A git commit exists for the changes.
- notes: []

### TASK-WSI-03-001: Render workstream icons in left rail

- status: pending
- source: specs/workstream-icons-universal-shell/README.md
- task brief: specs/workstream-icons-universal-shell/tasks/03-rail-rendering/01-render-left-rail-icons.md
- depends on: [TASK-WSI-02-001]
- required reads:
  - frontend/src/workstream/rail/FunctionalAgentRail.tsx
  - frontend/src/workstream/rail/FunctionalAgentRailItem.tsx
  - frontend/src/workstream-shell.contract.test.mjs
  - frontend/src/styles/components.css
  - templates/ai-first-saas-starter/frontend/src/workstream/rail/FunctionalAgentRail.tsx
  - templates/ai-first-saas-starter/frontend/src/workstream/rail/FunctionalAgentRailItem.tsx
  - templates/ai-first-saas-starter/frontend/src/workstream-shell.contract.test.mjs
  - templates/ai-first-saas-starter/frontend/src/styles/components.css
- expected outputs:
  - Render icon descriptors in top left rail entries for User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
  - Add tooltip/accessibility behavior from `tooltip` and `ariaLabel`; avoid relying on `title` only.
  - Keep My Account as lower-left signed-in user tile, not a normal top-rail icon entry.
  - Preserve hidden/disabled/denied workstream behavior.
  - Add/adjust contract tests that assert the four core v0 workstreams have rendered icon affordances.
- required checks:
  - `cd frontend && npm test -- --run src/workstream-shell.contract.test.mjs`
  - `cd frontend && npm run typecheck`
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --run src/workstream-shell.contract.test.mjs`
  - `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
  - `git diff --check`
- done criteria:
  - Reference and starter frontend rail code renders accessible icons for the four top-rail core v0 workstreams.
  - A git commit exists for the changes.
- notes: []

### TASK-WSI-04-001: Add scaffold proof for v0 left rail icons

- status: pending
- source: user-specified proof test
- task brief: specs/workstream-icons-universal-shell/tasks/04-proof/01-add-scaffold-proof.md
- depends on: [TASK-WSI-03-001]
- required reads:
  - specs/workstream-icons-universal-shell/README.md
  - templates/ai-first-saas-starter/README.md
  - templates/ai-first-saas-starter/frontend/package.json
  - bin/scaffold-ai-first-saas-starter.sh
  - docs/minimum-ai-first-saas-app.md
- expected outputs:
  - Add a reproducible proof script or documented test that scaffolds/uses the v0 starter and verifies the left rail exposes icons for User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
  - The proof should also assert My Account remains accessible through the lower-left user tile and is not duplicated in the top rail.
  - Record the proof command and expected result in the spec README or a proof report.
- required checks:
  - `git diff --check`
  - Run the new proof command, or document an environment blocker with exact remediation if local scaffold execution is unavailable.
  - `rg -n "User Admin|Agent Admin|Audit/Trace|Governance/Policy|My Account|workstream icon|proof" specs/workstream-icons-universal-shell templates/ai-first-saas-starter frontend/src`
- done criteria:
  - A fresh harness can prove the generated/starter v0 app includes left rail icons for the four required core workstreams.
  - A git commit exists for the changes.
- notes: []
