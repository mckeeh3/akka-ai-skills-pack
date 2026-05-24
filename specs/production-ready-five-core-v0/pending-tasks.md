# Pending Tasks: Production-Ready Five Core v0

## Queue rules

- Execute one task per fresh harness session.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Treat local Akka execution as production-like validation. Do not satisfy runtime tasks with fixture-only, deterministic, mocked, simulated, or frontend-only behavior unless the task explicitly says to add a test double.
- Normal runtime behavior must use real backend implementation paths. Test doubles must be isolated to tests and named as such.
- If a required provider/configuration is missing, implement fail-closed actionable errors rather than silent fallback.
- Historical tasks in this queue that mention real model/provider behavior are supplemented by `specs/workstream-akka-agent-runtime/`: normal user-facing workstream replies must invoke the governed Akka Agent component path, not a service-only provider bypass.
- Update this file before finishing the harness response: set completed tasks to `done`, add a completion note, and add discovered follow-up tasks rather than expanding the current task.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- Commit message format: `prod-v0: <short task title>`.

## Tasks

### TASK-PRODV0-00-001: Create production-ready v0 task queue

- status: done
- completion note: Created the production-ready five-core v0 planning scaffold, backlog, sprint sequence, conversation capture, and self-contained pending task queue.
- source: user request to make the five-core v0 app real/model-backed and production-ready instead of deterministic/demo-like
- task brief: specs/production-ready-five-core-v0/tasks/00-planning/00-create-production-ready-v0-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/minimum-ai-first-saas-app.md
  - docs/agent-workstream-application-architecture.md
  - specs/five-core-workstream-v0-starter/pending-tasks.md
- expected outputs:
  - specs/production-ready-five-core-v0/README.md
  - specs/production-ready-five-core-v0/conversation-capture.md
  - specs/production-ready-five-core-v0/pending-tasks.md
  - specs/production-ready-five-core-v0/backlog/01-production-ready-v0-build-backlog.md
  - specs/production-ready-five-core-v0/sprints/*.md
  - specs/production-ready-five-core-v0/tasks/**/*.md
- required checks:
  - `git diff --check`
  - `rg -n "TASK-PRODV0|production-ready|real model|deterministic|mock|fixture" specs/production-ready-five-core-v0`
- done criteria:
  - The production-ready v0 queue exists and every follow-up task is self-sufficient for a fresh harness session.
  - A git commit exists for the planning changes.
- notes:
  - commit message: `prod-v0: add production ready task queue`

### TASK-PRODV0-01-001: Harden implementation completion doctrine against mock/demo runtime paths

- status: done
- completion note: Hardened source and installed-pack guidance to require real local Akka runtime validation for named generated-app features, reject normal-runtime deterministic/demo/mock/simulated substitutes, fail closed on missing provider/security configuration, and keep fixtures/test doubles isolated to tests.
- source: specs/production-ready-five-core-v0/backlog/01-production-ready-v0-build-backlog.md
- task brief: specs/production-ready-five-core-v0/tasks/01-doctrine/01-harden-completion-doctrine.md
- depends on: [TASK-PRODV0-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/minimum-ai-first-saas-app.md
  - docs/agent-workstream-application-architecture.md
  - docs/skills-pack-user-guide.md
  - README.md
  - pack/AGENTS.md
- expected outputs:
  - Update pack doctrine/routing/user guidance so named generated-app features are done only when the real local runtime path works.
  - Explicitly reject normal-runtime deterministic/demo/mock/simulated/model-less implementation for workstream agents, auth, durability, provider calls, and protected capabilities.
  - Clarify that Akka local execution is production-like validation and should be used aggressively rather than avoided.
  - Keep test fixtures/test doubles allowed only as tests, not as user-facing runtime substitutes.
- required checks:
  - `git diff --check`
  - `rg -n "production-ready|real runtime|mock|deterministic|fixture|Akka local|production-like|test double" AGENTS.md pack/AGENTS.md skills/README.md docs/ai-first-saas-application-architecture.md docs/minimum-ai-first-saas-app.md docs/agent-workstream-application-architecture.md docs/skills-pack-user-guide.md README.md`
- done criteria:
  - Guidance makes it hard for downstream harnesses to mark mock/demo/kinda-sorta behavior as implemented.
  - A git commit exists for the changes.

### TASK-PRODV0-01-002: Update getting-started prompts for real model-backed workstream agents

- status: done
- completion note: Updated getting-started and starter-template guidance to require real model-backed five-core workstream behavior, document backend-only provider config and fail-closed missing-provider handling, correct rendered backend paths to `pom.xml`/`src/`, and add manual smoke checks for sign-in, workstream prompts, traces, and secret boundaries.
- source: specs/production-ready-five-core-v0/backlog/01-production-ready-v0-build-backlog.md
- task brief: specs/production-ready-five-core-v0/tasks/01-doctrine/02-update-real-agent-getting-started.md
- depends on: [TASK-PRODV0-01-001]
- required reads:
  - README.md
  - docs/skills-pack-user-guide.md
  - templates/ai-first-saas-starter/README.md
  - templates/ai-first-saas-starter/.env.example
  - templates/ai-first-saas-starter/frontend/.env.example
- expected outputs:
  - Update getting-started prompts to require real model-backed workstream agent behavior before the five-core v0 app is called functional.
  - Add explicit local config guidance for backend-only model provider variables and clarify that provider missing means model-backed message submission is blocked, not silently deterministic.
  - Correct stale scaffold-review paths such as `backend/` if backend renders into project root `pom.xml`/`src/`.
  - Add a manual smoke prompt/checklist for signing in, selecting each core workstream, submitting a prompt, seeing a real model response, and checking traces/secret boundaries.
- required checks:
  - `git diff --check`
  - `rg -n "real model|model-backed|OPENAI_API_KEY|provider|blocked|pom.xml|src/|five core" README.md docs/skills-pack-user-guide.md templates/ai-first-saas-starter/README.md templates/ai-first-saas-starter/.env.example`
- done criteria:
  - A downstream user following docs will configure and test real workstream agents, not deterministic placeholders.
  - A git commit exists for the changes.

### TASK-PRODV0-02-001: Implement backend model provider client boundary

- status: done
- completion note: Added a backend-only ModelProviderClient boundary, real OpenAI chat-completions adapter using OPENAI_API_KEY/model/endpoint/timeout environment configuration, fail-closed missing-provider validation, safe redacted summaries, and focused tests with a clearly named unit-test fake for request shaping, error mapping, redaction, and missing config.
- source: specs/production-ready-five-core-v0/backlog/01-production-ready-v0-build-backlog.md
- task brief: specs/production-ready-five-core-v0/tasks/02-agent-runtime/01-implement-model-provider-client.md
- depends on: [TASK-PRODV0-01-002]
- required reads:
  - skills/akka-agents/SKILL.md
  - skills/akka-agent-configuration/SKILL.md
  - templates/ai-first-saas-starter/backend/pom.xml
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/domain/agentfoundation/ModelConfigRef.java
  - templates/ai-first-saas-starter/.env.example
  - templates/ai-first-saas-starter/backend/src/main/resources/application.conf
- expected outputs:
  - Add a backend-only model provider client abstraction for the starter runtime.
  - Implement a real provider adapter using `OPENAI_API_KEY` and a configured model id/endpoint/timeout suitable for local production-like execution.
  - Add fail-closed config validation for missing/blank provider vars when runtime model invocation is requested.
  - Ensure provider secrets are never returned through `/api/me`, workstream surfaces, traces, frontend env, or static assets.
  - Keep deterministic behavior only as a clearly named unit-test fake, not as normal runtime fallback.
  - Add focused backend tests with fake provider adapter for request shaping, timeout/error mapping, secret redaction, and missing-config behavior.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "ModelProvider|OpenAI|OPENAI_API_KEY|model id|missing.*provider|secret" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java templates/ai-first-saas-starter/.env.example templates/ai-first-saas-starter/backend/src/main/resources/application.conf`
- done criteria:
  - Starter backend has a real model provider boundary that can be used by workstream agents and cannot silently fall back to deterministic runtime text.
  - A git commit exists for the changes.

### TASK-PRODV0-02-002: Wire five core workstream agents through governed prompt assembly and real model invocation

- status: done
- completion note: Replaced normal-runtime deterministic workstream message generation with governed runtime invocation that assembles active prompts/manifests/tool/model policy, invokes the configured model provider, emits PromptAssemblyTrace/MODEL_INVOCATION/AgentWorkTrace metadata, returns provider markdown, and uses injected fake providers only in tests.
- source: specs/production-ready-five-core-v0/backlog/01-production-ready-v0-build-backlog.md
- task brief: specs/production-ready-five-core-v0/tasks/02-agent-runtime/02-wire-real-workstream-agents.md
- depends on: [TASK-PRODV0-02-001]
- required reads:
  - docs/agent-workstream-application-architecture.md
  - docs/capability-first-backend-architecture.md
  - skills/akka-agent-seed-documents/SKILL.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentRuntimeService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java
  - templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/**
- expected outputs:
  - Replace normal-runtime deterministic `submitMessage` response generation with real governed agent invocation.
  - Resolve the selected `AgentDefinition`, active prompt, active skill/reference manifests, tool boundary, model config, and model policy for the selected functional agent.
  - Assemble a model request with compact manifests and selected AuthContext; do not include secrets or unauthorized data.
  - Invoke the real model provider and return its markdown as `markdown_response`.
  - Emit `PromptAssemblyTrace`, model invocation trace metadata, `AgentWorkTrace`, and denial/error traces.
  - Preserve testability through injected/named fake provider in tests only.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "deterministicMarkdownResponse|invoke.*model|PromptAssemblyTrace|AgentWorkTrace|markdown_response|ModelProvider" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - All five core workstreams use the real governed workstream-agent path for normal message submission.
  - No normal runtime deterministic text generator remains for workstream messages.
  - A git commit exists for the changes.

### TASK-PRODV0-02-003: Add real model provider integration smoke test and manual validation guard

- status: done
- completion note: Added an optional real model smoke script plus scaffolded JUnit smoke that submits a User Admin workstream message through backend WorkstreamService, verifies provider-backed markdown and prompt/model/work trace shape without secret leakage, wires fullstack validation to report/run the smoke based on provider env, and documents skip/real enablement.
- source: specs/production-ready-five-core-v0/backlog/01-production-ready-v0-build-backlog.md
- task brief: specs/production-ready-five-core-v0/tasks/02-agent-runtime/03-add-real-model-smoke-validation.md
- depends on: [TASK-PRODV0-02-002]
- required reads:
  - templates/ai-first-saas-starter/README.md
  - templates/ai-first-saas-starter/.env.example
  - tools/validate-ai-first-saas-starter-fullstack.sh
  - tools/scaffold-ai-first-saas-starter.sh
- expected outputs:
  - Add an optional explicit real-provider smoke command/script or documented command that runs only when provider env vars are present.
  - The smoke must submit at least one workstream message through backend code and verify a provider-backed response/trace shape without exposing secrets.
  - Ensure default CI/fullstack validation can pass without real secrets but clearly reports that provider smoke was skipped unless env is present.
  - Document exactly how a human enables the real model smoke locally.
- required checks:
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - provider smoke command in skip mode with provider env absent
  - `git diff --check`
  - `rg -n "provider smoke|OPENAI_API_KEY|skip|model-backed|real model" tools templates/ai-first-saas-starter/README.md templates/ai-first-saas-starter/.env.example`
- done criteria:
  - Maintainers and downstream users have an explicit real-model validation path instead of assuming tests prove provider integration.
  - A git commit exists for the changes.

### TASK-PRODV0-03-001: Persist workstream messages, surfaces, and traces durably

- status: done
- completion note: Added an Akka Key Value Entity-backed WorkstreamLogRepository with Akka and test/local adapters, wired workstream message submission/items/surface lookup through the log with idempotent duplicate handling and persisted denial entries, and added service/entity tests for append, read, surface lookup, duplicate idempotency, tenant/context isolation, and denial persistence.
- source: specs/production-ready-five-core-v0/backlog/01-production-ready-v0-build-backlog.md
- task brief: specs/production-ready-five-core-v0/tasks/03-workstream-runtime/01-persist-workstream-log.md
- depends on: [TASK-PRODV0-02-002]
- required reads:
  - skills/akka-key-value-entities/SKILL.md
  - skills/akka-event-sourced-entities/SKILL.md
  - docs/capability-first-backend-architecture.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java
- expected outputs:
  - Add an Akka durable component for tenant/context/functional-agent scoped workstream logs, including user message, agent response item, markdown surface envelope, correlation/idempotency, model/provider trace references, and error/denial entries.
  - Make `/api/workstream/items`, `/api/workstream/surfaces/{surfaceId}`, and message submission read/write the durable log rather than only rebuilding static in-memory starter items.
  - Preserve idempotency for duplicate message submission.
  - Add tests for append, read by functional agent, surface lookup, duplicate idempotency, tenant/context isolation, and denial persistence.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "WorkstreamLog|KeyValueEntity|EventSourcedEntity|idempotency|surfaceId|tenant" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Workstream messages and response surfaces survive normal backend service boundaries and are not merely frontend/in-memory append illusions.
  - A git commit exists for the changes.

### TASK-PRODV0-03-002: Normalize backend bootstrap to five markdown v0 surfaces only

- status: done
- completion note: Normalized backend bootstrap and fixture bootstrap to exactly five initial core `markdown_response` items/surfaces, moved richer dashboard/list/detail/audit/governance surfaces out of the initial canonical bootstrap set, and updated backend/frontend tests for the five-core v0 shell.
- source: specs/production-ready-five-core-v0/backlog/01-production-ready-v0-build-backlog.md
- task brief: specs/production-ready-five-core-v0/tasks/03-workstream-runtime/02-normalize-backend-bootstrap.md
- depends on: [TASK-PRODV0-03-001]
- required reads:
  - docs/minimum-ai-first-saas-app.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
  - templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java
- expected outputs:
  - Make real API bootstrap match the intended five-core-v0 initial shell: one initial/default `markdown_response` surface per core workstream.
  - Move richer dashboard/list/detail/audit/governance surfaces behind explicit full-core/demo actions or follow-up APIs, not the initial v0 bootstrap acceptance path.
  - Update backend tests so they assert five initial markdown surfaces, not richer initial dashboard surfaces.
  - Ensure fixture mode and real API mode agree.
- required checks:
  - `mvn test`
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
  - `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
  - `git diff --check`
  - `rg -n "surface-v0|markdown_response|surface-user-admin-dashboard|full-core/demo" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java templates/ai-first-saas-starter/frontend/src`
- done criteria:
  - The first real screen after sign-in is the five-core markdown v0 shell, not a mixed richer-demo bootstrap.
  - A git commit exists for the changes.

### TASK-PRODV0-03-003: Update frontend runtime UX for real agent readiness, errors, and traces

- status: done
- completion note: Added frontend in-flight workstream submission state, safe provider/configuration and forbidden system-notification errors with preserved retry context, trace-link enrichment for successful model responses, and contract coverage for in-flight, provider-missing, forbidden, retry, and trace rendering.
- source: specs/production-ready-five-core-v0/backlog/01-production-ready-v0-build-backlog.md
- task brief: specs/production-ready-five-core-v0/tasks/04-frontend/01-real-agent-runtime-ux.md
- depends on: [TASK-PRODV0-03-002]
- required reads:
  - templates/ai-first-saas-starter/frontend/src/main.tsx
  - templates/ai-first-saas-starter/frontend/src/workstream/composer/WorkstreamComposer.tsx
  - templates/ai-first-saas-starter/frontend/src/workstream/surfaces/MarkdownResponseSurface.tsx
  - templates/ai-first-saas-starter/frontend/src/api/HttpWorkstreamApiClient.ts
  - templates/ai-first-saas-starter/frontend/src/workstream-shell.contract.test.mjs
  - templates/ai-first-saas-starter/frontend/src/workstream-composer-message-api.contract.test.mjs
- expected outputs:
  - Add visible in-flight state for model-backed workstream message submission.
  - Render provider/configuration failures as safe `system_message`/system-notification style entries with actionable recovery copy.
  - Preserve selected workstream context during failures and retries.
  - Surface prompt/model/work trace links from successful model responses.
  - Add frontend tests for in-flight, provider-missing, forbidden, retry, and trace-link rendering.
- required checks:
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
  - `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
  - `git diff --check`
  - `rg -n "submitting|provider|retry|trace|Message not submitted|model" templates/ai-first-saas-starter/frontend/src`
- done criteria:
  - Users can tell whether the real workstream agent is running, blocked by configuration, forbidden, or complete.
  - A git commit exists for the changes.

### TASK-PRODV0-04-001: Sync frontend reference and restore pack buildability

- status: done
- completion note: Synced root `frontend/src` from the starter template frontend source, confirmed no frontend source differences remain, and restored pack build validation with `bash tools/build-pack.sh --clean --no-archive`.
- source: specs/production-ready-five-core-v0/backlog/01-production-ready-v0-build-backlog.md
- task brief: specs/production-ready-five-core-v0/tasks/05-validation/01-sync-frontend-reference-build-pack.md
- depends on: [TASK-PRODV0-03-003]
- required reads:
  - tools/build-pack.sh
  - frontend/src/**
  - templates/ai-first-saas-starter/frontend/src/**
  - docs/skills-pack-developer-guide.md
- expected outputs:
  - Sync root `frontend/src` with `templates/ai-first-saas-starter/frontend/src` or deliberately update build-pack validation if a new source-of-truth policy is chosen.
  - Ensure no template-only frontend source differences block pack build.
  - Run pack build validation.
- required checks:
  - `diff -qr --exclude node_modules --exclude .env.local frontend/src templates/ai-first-saas-starter/frontend/src`
  - `bash tools/build-pack.sh --clean --no-archive`
  - `git diff --check`
- done criteria:
  - Pack build no longer fails because the frontend reference and starter template are out of sync.
  - A git commit exists for the changes.

### TASK-PRODV0-04-002: Final production-ready v0 validation and release handoff

- status: done
- completion note: Ran full starter validation, version consistency, pack build, source install smoke, installed scaffold dry-run, provider smoke skip mode, and real provider smoke mode; fixed a validation-discovered non-secret metadata marker in the starter surface redaction payload; added release handoff notes with the recommended local trial process and out-of-scope follow-up areas.
- source: specs/production-ready-five-core-v0/backlog/01-production-ready-v0-build-backlog.md
- task brief: specs/production-ready-five-core-v0/tasks/05-validation/02-final-validation-release-handoff.md
- depends on: [TASK-PRODV0-04-001]
- required reads:
  - specs/production-ready-five-core-v0/README.md
  - specs/production-ready-five-core-v0/pending-tasks.md
  - README.md
  - docs/skills-pack-user-guide.md
  - docs/skills-pack-developer-guide.md
  - tools/validate-ai-first-saas-starter-fullstack.sh
  - tools/check-version-consistency.sh
  - tools/build-pack.sh
- expected outputs:
  - Run full starter validation, version consistency check, pack build, source install smoke, scaffold dry-run, and documented provider-smoke skip/real modes as available.
  - Update docs with the final recommended trial process.
  - Add release handoff notes: whether to cut a new version, which commands passed, and what remains outside production-ready v0.
  - Add follow-up tasks only for discovered blockers that cannot fit this task.
- required checks:
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - `bash tools/check-version-consistency.sh`
  - `bash tools/build-pack.sh --clean --no-archive`
  - source install smoke into a temp project and scaffold dry-run
  - provider smoke skip mode when env is absent; real provider smoke if env is present
  - `git diff --check`
- done criteria:
  - The repository is ready for a real local production-ready v0 trial or has explicit blocker tasks explaining why not.
  - A git commit exists for the changes.
