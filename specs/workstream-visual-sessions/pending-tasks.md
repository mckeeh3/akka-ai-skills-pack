# Pending Tasks

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Update task status before finishing the harness response.
- Each task must make one git commit before being marked `done`; the commit should include only that task's intended changes and its queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- This queue is for the workstream visual sessions effort, rooted at `specs/workstream-visual-sessions/`.

## Tasks

### TASK-WVS-00-001: Create workstream visual sessions planning scaffold

- status: done
- source: user request to capture workstream visual-session UX and create self-contained implementation tasks
- task brief: specs/workstream-visual-sessions/tasks/00-planning-scaffold/00-create-workstream-visual-sessions-plan.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/agent-workstream-application-architecture.md
  - docs/workstream-ui-reference-architecture.md
  - docs/workstream-visual-sessions.md
- skills:
  - none; repository planning task
- expected outputs:
  - docs/workstream-visual-sessions.md
  - docs/workstream-ui-reference-architecture.md
  - specs/workstream-visual-sessions/README.md
  - specs/workstream-visual-sessions/conversation-capture.md
  - specs/workstream-visual-sessions/pending-tasks.md
  - specs/workstream-visual-sessions/tasks/**
- required checks:
  - verify the queue defines self-contained fresh-session tasks
  - verify planning docs capture traditional chat ordering and phased persistence
- done criteria:
  - planning scaffold exists
  - task changes and queue update are committed
- notes:
  - commit message: `Add workstream visual sessions plan`
  - completed: captured the visual-session UX doctrine, linked it from the workstream UI reference, and created a fresh-session implementation queue with task briefs.

### TASK-WVS-01-001: Add visual session and turn-group state helpers

- status: done
- source: docs/workstream-visual-sessions.md phase 1
- task brief: specs/workstream-visual-sessions/tasks/01-phase-1-frontend/01-add-visual-session-state-helpers.md
- depends on: [TASK-WVS-00-001]
- required reads:
  - AGENTS.md
  - specs/workstream-visual-sessions/README.md
  - specs/workstream-visual-sessions/conversation-capture.md
  - docs/workstream-visual-sessions.md
  - docs/workstream-ui-reference-architecture.md
  - frontend/src/workstream/types/**
  - frontend/src/workstream/stream/**
  - frontend/src/workstream/shell/**
  - frontend/src/workstream/fixtures/**
- skills:
  - akka-web-ui-state-rendering
  - akka-web-ui-testing
- expected outputs:
  - reusable frontend visual-session/turn-group types or helpers under `frontend/src/workstream/**`
  - focused contract tests for turn grouping, ordering, caps, and session snapshot semantics
- required checks:
  - traditional ordering is preserved: older turn groups above newer turn groups
  - session limits are based primarily on turn groups with a secondary surface cap
  - no browser-local or backend persistence is introduced in this task
- done criteria:
  - state helpers are reusable by stream/shell components
  - frontend tests/checks relevant to changed files pass
  - task changes and queue update are committed
- notes:
  - commit message: `Add workstream visual session state helpers`
  - completed: added reusable in-memory visual-session and turn-group helpers, contract tests for grouping/order/limits/snapshots, and no browser-local/backend persistence.

### TASK-WVS-01-002: Implement request-surface anchoring and manual-scroll pause

- status: done
- source: docs/workstream-visual-sessions.md phase 1
- task brief: specs/workstream-visual-sessions/tasks/01-phase-1-frontend/02-implement-request-anchor-and-scroll-pause.md
- depends on: [TASK-WVS-01-001]
- required reads:
  - AGENTS.md
  - specs/workstream-visual-sessions/README.md
  - docs/workstream-visual-sessions.md
  - docs/workstream-ui-reference-architecture.md
  - frontend/src/workstream/stream/**
  - frontend/src/workstream/shell/**
  - frontend/src/workstream/*.contract.test.mjs
- skills:
  - akka-web-ui-state-rendering
  - akka-web-ui-accessibility-responsive
  - akka-web-ui-testing
- expected outputs:
  - stream anchoring behavior that scrolls a new request surface to the top of the visible workstream panel
  - auto-anchor preservation while response surfaces append
  - manual-scroll detection that pauses automatic anchoring
  - focused contract tests
- required checks:
  - new requests append after prior turn groups rather than reordering history
  - response surfaces remain below the request surface
  - reduced-motion preferences are respected
  - auto-anchor pauses after user scroll input
- done criteria:
  - request-surface anchoring works for appended responses
  - frontend tests/checks relevant to changed files pass
  - task changes and queue update are committed
- notes:
  - commit message: `Implement workstream request anchoring`
  - completed: added reduced-motion-safe request-surface top anchoring, preserved the active request anchor while responses append, and paused automatic anchoring after wheel, touch, or keyboard scroll input.

### TASK-WVS-01-003: Restore in-memory visual sessions when switching workstreams

- status: pending
- source: docs/workstream-visual-sessions.md phase 1
- task brief: specs/workstream-visual-sessions/tasks/01-phase-1-frontend/03-restore-in-memory-sessions-on-workstream-switch.md
- depends on: [TASK-WVS-01-002]
- required reads:
  - AGENTS.md
  - specs/workstream-visual-sessions/README.md
  - docs/workstream-visual-sessions.md
  - docs/workstream-ui-reference-architecture.md
  - frontend/src/workstream/shell/**
  - frontend/src/workstream/rail/**
  - frontend/src/workstream/stream/**
  - frontend/src/main.tsx
- skills:
  - akka-web-ui-state-rendering
  - akka-web-ui-testing
- expected outputs:
  - per-workstream in-memory visual session state
  - restoration of anchor/selected-surface/collapsed-or-loaded state where supported by existing UI contracts
  - focused contract tests for switching workstreams and returning where the user left off
- required checks:
  - session state is keyed by selected auth context and functional agent/workstream id where available
  - switching workstreams does not jump to latest unless a new request is submitted or explicit jump behavior exists
  - no cross-device/browser-local/backend persistence is introduced in this task
- done criteria:
  - users can switch workstreams and return to the previous in-memory visual position/state
  - frontend tests/checks relevant to changed files pass
  - task changes and queue update are committed
- notes: []

### TASK-WVS-01-004: Sync phase 1 visual sessions into the starter template

- status: pending
- source: starter template is the canonical generated-app implementation baseline
- task brief: specs/workstream-visual-sessions/tasks/01-phase-1-frontend/04-sync-visual-sessions-to-starter-template.md
- depends on: [TASK-WVS-01-003]
- required reads:
  - AGENTS.md
  - specs/workstream-visual-sessions/README.md
  - docs/workstream-visual-sessions.md
  - docs/workstream-ui-reference-architecture.md
  - templates/ai-first-saas-starter/frontend/src/workstream/**
  - templates/ai-first-saas-starter/frontend/src/*.contract.test.mjs
  - templates/ai-first-saas-starter/README.md
- skills:
  - akka-web-ui-state-rendering
  - akka-web-ui-testing
- expected outputs:
  - starter template frontend updated to match phase 1 reference visual-session behavior
  - starter template contract tests updated or added
- required checks:
  - template behavior matches reference frontend behavior for turn order, anchoring, manual-scroll pause, and per-workstream in-memory restore
  - generated template placeholders remain intact
  - run relevant starter frontend checks/tests if available
- done criteria:
  - starter template remains canonical for generated apps with phase 1 visual sessions
  - task changes and queue update are committed
- notes: []

### TASK-WVS-01-005: Close phase 1 docs and readiness notes

- status: pending
- source: docs/workstream-visual-sessions.md phase 1 acceptance checklist
- task brief: specs/workstream-visual-sessions/tasks/01-phase-1-frontend/05-close-phase-1-docs-and-readiness.md
- depends on: [TASK-WVS-01-004]
- required reads:
  - AGENTS.md
  - specs/workstream-visual-sessions/README.md
  - specs/workstream-visual-sessions/pending-tasks.md
  - docs/workstream-visual-sessions.md
  - docs/workstream-ui-reference-architecture.md
  - frontend/src/workstream/**
  - templates/ai-first-saas-starter/frontend/src/workstream/**
- skills:
  - none; repository docs/readiness task
- expected outputs:
  - updated `docs/workstream-visual-sessions.md` phase 1 status/readiness notes
  - follow-up pending task notes or backlog stubs for phase 2 and phase 3 if warranted
- required checks:
  - phase 1 acceptance checklist reflects actual implemented behavior
  - phase 2/3 remain future work and are not claimed implemented
- done criteria:
  - documentation accurately reflects implemented phase 1 scope and remaining persistence work
  - task changes and queue update are committed
- notes: []
