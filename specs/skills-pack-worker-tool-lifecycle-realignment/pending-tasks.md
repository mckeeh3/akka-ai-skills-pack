# Pending Tasks: Skills-Pack Worker/Tool/Lifecycle Realignment

## Queue rules

- Execute one task per fresh harness context.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Mark the selected task `in-progress` before editing and `done` only after required checks pass.
- Commit completed task changes and this queue update together.
- Do not run queued implementation tasks in parallel.

## Tasks

### TASK-001: Add canonical three-phase lifecycle doctrine

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/README.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/001-lifecycle-doctrine.md
- depends on: []
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/001-lifecycle-doctrine.md
- skills:
  - project-discussed-idea-to-pending-project
- expected outputs:
  - `skills-pack/docs/app-development-lifecycle.md`
  - focused links from related docs if needed
- required checks:
  - `git diff --check`
- done criteria:
  - Three-phase lifecycle doctrine exists and is linked from at least one relevant routing/current-intent doc.
- notes:
  - scope: skills-pack maintenance, docs-only
  - vertical contract: cross-cutting docs-only lifecycle doctrine; no runtime feature; validates by markdown review and diff checks
  - runtime evidence: not applicable; docs-only pack-maintenance task

### TASK-002: Add canonical app worker and governed-tool model

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/target-architecture.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/002-worker-tool-model.md
- depends on: [TASK-001]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/002-worker-tool-model.md
- skills:
  - ai-first-saas-worker-decomposition
  - capability-first-backend
- expected outputs:
  - `skills-pack/docs/app-worker-tool-model.md`
  - focused links from worker/surface/capability docs if needed
- required checks:
  - `git diff --check`
- done criteria:
  - Worker, harness, actor-adapter, governed-tool, capability, and Akka implementation separation are canonically defined.
- notes:
  - scope: skills-pack maintenance, docs-only
  - vertical contract: cross-cutting docs-only app modeling doctrine; no runtime feature
  - runtime evidence: not applicable; docs-only pack-maintenance task

### TASK-003: Define app-description graph and compile contract

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/target-architecture.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/003-app-description-graph-and-compile-contract.md
- depends on: [TASK-001, TASK-002]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/003-app-description-graph-and-compile-contract.md
- skills:
  - app-descriptions
  - app-description-change-impact
  - akka-solution-decomposition
- expected outputs:
  - `skills-pack/docs/app-description-component-graph.md`
  - `skills-pack/docs/app-description-to-code-compile-contract.md`
  - focused links from intent/realization docs
- required checks:
  - `git diff --check`
- done criteria:
  - App-description graph node families and compile chain are defined with workers/tools first-class.
- notes:
  - scope: skills-pack maintenance, docs-only
  - vertical contract: cross-cutting docs-only compile contract; no runtime feature
  - runtime evidence: not applicable; docs-only pack-maintenance task

### TASK-004: Define manual runtime test reconciliation doctrine

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/target-architecture.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/004-manual-test-reconciliation.md
- depends on: [TASK-001, TASK-002]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/004-manual-test-reconciliation.md
- skills:
  - akka-runtime-feature-verification
  - akka-manual-failure-reconciliation
- expected outputs:
  - `skills-pack/docs/manual-test-reconciliation.md`
  - focused links from manual/runtime verification skills if needed
- required checks:
  - `git diff --check`
- done criteria:
  - Manual runtime testing is defined as a first-class lifecycle phase with reconciliation outputs.
- notes:
  - scope: skills-pack maintenance, docs-only
  - vertical contract: cross-cutting docs-only manual verification doctrine; no runtime feature
  - runtime evidence: not applicable; docs-only pack-maintenance task

### TASK-005: Update routing map and skill metadata contract

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/005-routing-and-skill-metadata-contract.md
- depends on: [TASK-001, TASK-002, TASK-003, TASK-004]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/005-routing-and-skill-metadata-contract.md
- skills:
  - app-descriptions
  - ai-first-saas
- expected outputs:
  - updated `skills-pack/skills/README.md`
  - updated skill contract docs
  - conservative manifest metadata update or documented deferral
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- done criteria:
  - Routing map and skill contracts describe phases, skill classification, and worker/tool/capability routing.
- notes:
  - scope: skills-pack maintenance, docs/metadata
  - vertical contract: cross-cutting docs/pack metadata; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task

### TASK-006: Pilot migrate architecture and workforce skills

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/006-pilot-architecture-skills.md
- depends on: [TASK-005]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/006-pilot-architecture-skills.md
- skills:
  - ai-first-saas
  - agent-workstream-apps
  - ai-first-saas-worker-decomposition
  - capability-first-backend
  - core-saas-foundation
- expected outputs:
  - migrated pilot architecture skills
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - Pilot architecture skills consistently use lifecycle and worker/tool/capability terminology.
- notes:
  - scope: skills-pack maintenance, skill text
  - vertical contract: cross-cutting skill guidance; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task

### TASK-007: Pilot migrate app-description and surface/capability skills

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/007-pilot-app-description-skills.md
- depends on: [TASK-006]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/007-pilot-app-description-skills.md
- skills:
  - app-descriptions
  - app-description-input-normalization
  - app-description-surface-modeling
  - app-description-capability-modeling
  - app-description-functional-agent-modeling
- expected outputs:
  - migrated representative app-description skills
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - Representative app-description skills model workers/tools as first-class graph implications.
- notes:
  - scope: skills-pack maintenance, skill text
  - vertical contract: cross-cutting skill guidance; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task

### TASK-008: Pilot migrate implementation and verification skills

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/008-pilot-implementation-and-verification-skills.md
- depends on: [TASK-007]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/008-pilot-implementation-and-verification-skills.md
- skills:
  - akka-solution-decomposition
  - akka-agent-tools
  - akka-http-endpoint-component-client
  - akka-web-ui-apps
  - akka-runtime-feature-verification
  - akka-manual-failure-reconciliation
- expected outputs:
  - migrated representative implementation and verification skills
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - Representative implementation and verification skills preserve the compile/manual-test contracts.
- notes:
  - scope: skills-pack maintenance, skill text
  - vertical contract: cross-cutting skill guidance; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task

### TASK-009: Migrate app-description skill family

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/009-migrate-app-description-family.md
- depends on: [TASK-008]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/009-migrate-app-description-family.md
- skills:
  - app-descriptions
- expected outputs:
  - migrated app-description family skills
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - App-description skill family consistently treats app-description as a living graph with workers/tools first-class.
- notes:
  - scope: skills-pack maintenance, skill family migration
  - vertical contract: cross-cutting skill guidance; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task

### TASK-010: Migrate AI-first SaaS/workstream/worker family

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/010-migrate-ai-first-workstream-family.md
- depends on: [TASK-009]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/010-migrate-ai-first-workstream-family.md
- skills:
  - ai-first-saas
  - ai-first-saas-worker-decomposition
  - agent-workstream-apps
  - capability-first-backend
- expected outputs:
  - migrated AI-first/workstream/worker family skills and related docs
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - AI-first/workstream family consistently uses worker, harness, actor-adapter, governed-tool, and capability language.
- notes:
  - scope: skills-pack maintenance, skill family migration
  - vertical contract: cross-cutting skill guidance; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task

### TASK-011: Migrate agent and governed-agent family

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/011-migrate-agent-governance-family.md
- depends on: [TASK-010]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/011-migrate-agent-governance-family.md
- skills:
  - akka-agents
  - akka-autonomous-agents
- expected outputs:
  - migrated agent/governance skill family
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - Agent/governance skills separate software workers, agent runtimes, agent adapters, governed tools, and backend capabilities.
- notes:
  - scope: skills-pack maintenance, skill family migration
  - vertical contract: cross-cutting skill guidance; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task

### TASK-012: Migrate Akka component family

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/012-migrate-akka-component-family.md
- depends on: [TASK-011]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/012-migrate-akka-component-family.md
- skills:
  - akka-key-value-entities
  - akka-event-sourced-entities
  - akka-workflows
  - akka-views
  - akka-consumers
  - akka-timed-actions
- expected outputs:
  - migrated Akka component skill family
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - Component skills fit the compile contract while preserving API-specific mechanics.
- notes:
  - scope: skills-pack maintenance, skill family migration
  - vertical contract: cross-cutting skill guidance; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task

### TASK-013: Migrate endpoint and web UI families

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/013-migrate-endpoint-web-ui-family.md
- depends on: [TASK-012]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/013-migrate-endpoint-web-ui-family.md
- skills:
  - akka-http-endpoints
  - akka-grpc-endpoints
  - akka-mcp-endpoints
  - akka-web-ui-apps
- expected outputs:
  - migrated endpoint and web UI skill families
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - Transport/UI skills separate exposure adapters from canonical governed operations.
- notes:
  - scope: skills-pack maintenance, skill family migration
  - vertical contract: cross-cutting skill guidance; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task

### TASK-014: Migrate planning, testing, and verification families

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/014-migrate-planning-testing-verification-family.md
- depends on: [TASK-013]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/014-migrate-planning-testing-verification-family.md
- skills:
  - akka-prd-to-specs-backlog
  - akka-solution-decomposition
  - akka-backlog-to-pending-tasks
  - akka-runtime-feature-verification
  - akka-manual-failure-reconciliation
- expected outputs:
  - migrated planning, queue, testing, and verification skills
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - Planning and verification skills preserve compile and manual-test reconciliation contracts.
- notes:
  - scope: skills-pack maintenance, skill family migration
  - vertical contract: cross-cutting skill guidance; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task

### TASK-015: Compression, cleanup, manifest, and install validation

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/015-compression-cleanup-and-install-validation.md
- depends on: [TASK-014]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/015-compression-cleanup-and-install-validation.md
- skills:
  - akka-pending-task-queue-maintenance
- expected outputs:
  - duplicate doctrine cleanup
  - routing/manifest consistency updates
  - installed-layout validation evidence
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
- done criteria:
  - Pack routing, manifest, broad skill compression, and installed references are consistent with the new model.
- notes:
  - scope: skills-pack maintenance, cleanup/validation
  - vertical contract: cross-cutting pack maintenance; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task

### TASK-016: Terminal verification and follow-up queue decision

- status: pending
- source: specs/skills-pack-worker-tool-lifecycle-realignment/README.md
- task brief: specs/skills-pack-worker-tool-lifecycle-realignment/tasks/016-terminal-verification.md
- depends on: [TASK-015]
- required reads:
  - specs/skills-pack-worker-tool-lifecycle-realignment/tasks/016-terminal-verification.md
- skills:
  - akka-pending-task-queue-maintenance
- expected outputs:
  - `specs/skills-pack-worker-tool-lifecycle-realignment/verification-notes.md`
  - queue closure or appended follow-up tasks plus replacement terminal verification
- required checks:
  - `git diff --check`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
  - `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
  - `bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh` when practical
- done criteria:
  - Mini-project done state is verified or follow-up tasks are appended.
- notes:
  - scope: skills-pack maintenance, terminal verification
  - vertical contract: cross-cutting pack verification; no runtime feature
  - runtime evidence: not applicable; pack-maintenance task
