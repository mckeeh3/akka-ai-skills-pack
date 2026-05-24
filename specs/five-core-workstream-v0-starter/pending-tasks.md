# Pending Tasks: Five Core Workstream v0 Starter

## Queue rules

- Execute one task per fresh harness session.
- Select the first `pending` task whose dependencies are satisfied.
- Preserve task IDs; supersede obsolete tasks rather than deleting them.
- Do not combine adjacent tasks unless this file is first updated to merge them.
- Treat this repository as the skills-pack source: changes are primarily to skills, docs, examples, specs, templates, and reference tests unless a task explicitly targets executable starter/template code.
- Update this file before finishing the harness response: set completed tasks to `done`, add a completion note, and add discovered follow-up tasks rather than expanding the current task.
- Each task must make one focused git commit before being marked `done`; the commit should include only that task's intended changes and the queue-status update.
- If the queue status update is included in the same commit, record the commit message in task notes instead of attempting to amend the commit hash.
- Commit message format: `five-core-v0: <short task title>`.

## Tasks

### TASK-5WS-00-001: Create five-core workstream v0 migration queue

- status: done
- completion note: Created the planning scaffold, backlog, sprint summaries, conversation capture, pending task queue, and self-contained task briefs for the five core v0 starter migration.
- source: user request to break the five-core workstream starter changes into one-session committed tasks
- task brief: specs/five-core-workstream-v0-starter/tasks/00-planning/00-create-five-core-workstream-v0-queue.md
- depends on: []
- required reads:
  - AGENTS.md
  - skills/README.md
  - docs/ai-first-saas-application-architecture.md
  - docs/minimum-ai-first-saas-app.md
  - docs/agent-workstream-application-architecture.md
  - templates/ai-first-saas-starter/README.md
- expected outputs:
  - specs/five-core-workstream-v0-starter/README.md
  - specs/five-core-workstream-v0-starter/conversation-capture.md
  - specs/five-core-workstream-v0-starter/pending-tasks.md
  - specs/five-core-workstream-v0-starter/backlog/01-five-core-v0-build-backlog.md
  - specs/five-core-workstream-v0-starter/sprints/*.md
  - specs/five-core-workstream-v0-starter/tasks/**/*.md
- required checks:
  - `git diff --check`
  - `rg -n "TASK-5WS|five core|markdown_response|git commit" specs/five-core-workstream-v0-starter`
- done criteria:
  - The migration queue exists and every follow-up task is self-sufficient for a fresh harness session.
  - A git commit exists for the planning changes.
- notes:
  - commit message: `five-core-v0: add migration task queue`

### TASK-5WS-01-001: Update minimum starter doctrine to five core v0 workstreams

- status: done
- completion note: Updated minimum starter doctrine and architecture summaries so the first runnable starter is the five core v0 workstream set with `markdown_response`, while preserving stricter full-core readiness.
- source: specs/five-core-workstream-v0-starter/backlog/01-five-core-v0-build-backlog.md
- task brief: specs/five-core-workstream-v0-starter/tasks/01-doctrine/01-update-minimum-starter-doctrine.md
- depends on: [TASK-5WS-00-001]
- required reads:
  - AGENTS.md
  - skills/README.md
  - specs/five-core-workstream-v0-starter/README.md
  - specs/five-core-workstream-v0-starter/conversation-capture.md
  - docs/ai-first-saas-application-architecture.md
  - docs/minimum-ai-first-saas-app.md
  - docs/agent-workstream-application-architecture.md
  - docs/structured-surface-contracts.md
- expected outputs:
  - Update `docs/minimum-ai-first-saas-app.md` so the minimum starter is five core v0 workstreams with `markdown_response`, not only User Admin v0.
  - Update `docs/agent-workstream-application-architecture.md` so the minimum initial workstream section becomes a minimum initial core workstream set.
  - Update `docs/ai-first-saas-application-architecture.md` links/summary as needed.
  - Preserve the rule that the starter is not a generic chatbot and not full-core SaaS readiness.
- required checks:
  - `git diff --check`
  - `rg -n "five core|My Account|User Admin|Agent Admin|Audit/Trace|Governance/Policy|markdown_response|Full core" docs/minimum-ai-first-saas-app.md docs/agent-workstream-application-architecture.md docs/ai-first-saas-application-architecture.md`
- done criteria:
  - Doctrine clearly defines the first runnable starter as five core text-first v0 workstreams.
  - Full-core readiness remains stricter and follow-up work remains explicit.
  - A git commit exists for the changes.
- notes:
  - commit message: `five-core-v0: update minimum starter doctrine`

### TASK-5WS-01-002: Update routing and getting-started prompts for five core v0

- status: done
- completion note: Updated installed-pack routing and getting-started guidance so minimum/starter/basic/chatbot-like SaaS prompts and README/user-guide flows target the five core v0 workstreams with `markdown_response` before full-core expansion.
- source: specs/five-core-workstream-v0-starter/backlog/01-five-core-v0-build-backlog.md
- task brief: specs/five-core-workstream-v0-starter/tasks/01-doctrine/02-update-routing-and-getting-started.md
- depends on: [TASK-5WS-01-001]
- required reads:
  - specs/five-core-workstream-v0-starter/README.md
  - docs/minimum-ai-first-saas-app.md
  - skills/README.md
  - README.md
  - docs/skills-pack-user-guide.md
  - skills/agent-workstream-apps/SKILL.md
  - skills/ai-first-saas/SKILL.md
- expected outputs:
  - Update `skills/README.md` so starter/basic/chatbot-like generated SaaS prompts route to five core v0 workstreams.
  - Update `README.md` getting-started prompts, especially Step 2, Step 3, Step 6, and Step 7, to target five core v0 workstreams before full-core expansion.
  - Update `docs/skills-pack-user-guide.md` if it repeats the old one-workstream starter language.
  - Update `skills/agent-workstream-apps/SKILL.md` and `skills/ai-first-saas/SKILL.md` only if they contain stale User Admin-only minimum language.
- required checks:
  - `git diff --check`
  - `rg -n "five core|core v0|My Account|User Admin|Agent Admin|Audit/Trace|Governance/Policy|markdown_response" README.md docs/skills-pack-user-guide.md skills/README.md skills/agent-workstream-apps/SKILL.md skills/ai-first-saas/SKILL.md`
- done criteria:
  - Installed-pack users following README prompts are guided toward making all five core v0 workstreams functional.
  - Routing still requires secure SaaS, workstream-first, capability-first, audit/trace semantics.
  - A git commit exists for the changes.
- notes:
  - commit message: `five-core-v0: update starter routing prompts`

### TASK-5WS-01-003: Align starter template docs and app-description placeholders

- status: pending
- source: specs/five-core-workstream-v0-starter/backlog/01-five-core-v0-build-backlog.md
- task brief: specs/five-core-workstream-v0-starter/tasks/02-template-docs/01-align-template-docs-app-description.md
- depends on: [TASK-5WS-01-002]
- required reads:
  - specs/five-core-workstream-v0-starter/README.md
  - templates/ai-first-saas-starter/README.md
  - templates/ai-first-saas-starter/app-description/README.md
  - templates/ai-first-saas-starter/specs/README.md
  - templates/ai-first-saas-starter/scaffold-rules.md
  - templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md
- expected outputs:
  - Update starter template docs to identify the generated starter target as five core v0 workstreams.
  - Update scaffold/app-description/spec placeholder guidance so pending tasks after scaffold distinguish five-core-v0 readiness from full-core readiness.
  - Preserve current base package, secret-boundary, WorkOS/AuthKit, Resend/outbox, and model-provider environment guidance.
- required checks:
  - `git diff --check`
  - `rg -n "five core|core v0|markdown_response|full-core|My Account|User Admin|Agent Admin|Audit/Trace|Governance/Policy" templates/ai-first-saas-starter/README.md templates/ai-first-saas-starter/app-description/README.md templates/ai-first-saas-starter/specs/README.md templates/ai-first-saas-starter/scaffold-rules.md templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md`
- done criteria:
  - Scaffolded-project docs tell downstream harnesses what the v0 starter is and what full-core follow-up remains.
  - A git commit exists for the changes.

### TASK-5WS-02-001: Implement frontend markdown_response surface renderer

- status: pending
- source: specs/five-core-workstream-v0-starter/backlog/01-five-core-v0-build-backlog.md
- task brief: specs/five-core-workstream-v0-starter/tasks/04-frontend/01-implement-markdown-response-renderer.md
- depends on: [TASK-5WS-01-003]
- required reads:
  - docs/structured-surface-contracts.md
  - docs/minimum-ai-first-saas-app.md
  - templates/ai-first-saas-starter/frontend/src/workstream/types/surfaces.ts
  - templates/ai-first-saas-starter/frontend/src/workstream/surfaces/SurfaceRenderer.tsx
  - templates/ai-first-saas-starter/frontend/src/workstream/surfaces/SurfaceStateFrame.tsx
  - templates/ai-first-saas-starter/frontend/src/workstream-surfaces.contract.test.mjs
- expected outputs:
  - Add a `MarkdownResponseSurface` component for `markdown_response` v1.
  - Add/adjust TypeScript surface types for `markdown_response` payloads: markdown, optional title/summary, safety/redaction metadata, trace/correlation metadata.
  - Render markdown as sanitized HTML. Do not allow scripts, event handlers, `javascript:` links, or unsafe raw HTML.
  - Wire `SurfaceRenderer.tsx` to render `markdown_response` with the new component.
  - Add frontend contract/unit tests for rendering, sanitization, trace links, empty/error/forbidden state behavior, and no raw JSON fallback for `markdown_response`.
- required checks:
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
  - `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
  - `git diff --check`
  - `rg -n "MarkdownResponseSurface|markdown_response|sanitize|javascript:" templates/ai-first-saas-starter/frontend/src`
- done criteria:
  - `markdown_response` is a first-class rendered structured surface in the starter frontend.
  - Tests fail if unsafe markdown rendering is reintroduced.
  - A git commit exists for the changes.

### TASK-5WS-02-002: Add backend workstream message endpoint and markdown_response contract

- status: pending
- source: specs/five-core-workstream-v0-starter/backlog/01-five-core-v0-build-backlog.md
- task brief: specs/five-core-workstream-v0-starter/tasks/03-backend/01-add-workstream-message-endpoint.md
- depends on: [TASK-5WS-02-001]
- required reads:
  - docs/capability-first-backend-architecture.md
  - docs/minimum-ai-first-saas-app.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/api/workstream/WorkstreamEndpoint.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamService.java
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/MeResponse.java
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java
- expected outputs:
  - Add `POST /api/workstream/messages` to the starter template backend.
  - Define request/response records for selected context, functional agent id, prompt, correlation id, idempotency key, user item, agent item, and returned `markdown_response` surface.
  - Enforce backend authorization: selected context must match, account/membership must be active, requested functional agent must be visible/allowed, and denied/hidden agents must not produce model responses.
  - Return safe denial/system-message behavior or HTTP denial consistently with existing endpoint patterns.
  - Initially allow a deterministic local/demo model-response seam if no real provider is configured, but preserve model-provider secret boundaries and mark provider-backed invocation as the extension point.
  - Add backend tests for authorized message, denied functional agent, selected-context mismatch, idempotency/correlation propagation, and `markdown_response` envelope shape.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "messages|WorkstreamMessage|markdown_response|functionalAgentId|selectedContextId" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Backend exposes a capability-backed message submission contract for the selected core v0 workstream.
  - Tests prove the endpoint does not rely on frontend/prompt-only authorization.
  - A git commit exists for the changes.

### TASK-5WS-02-003: Seed or configure five core v0 functional-agent prompts, skills, and model refs

- status: pending
- source: specs/five-core-workstream-v0-starter/backlog/01-five-core-v0-build-backlog.md
- task brief: specs/five-core-workstream-v0-starter/tasks/03-backend/02-seed-five-core-v0-agent-behavior.md
- depends on: [TASK-5WS-02-002]
- required reads:
  - docs/agent-workstream-application-architecture.md
  - skills/akka-agent-seed-documents/SKILL.md
  - templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java
  - templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/user-admin-system.md
  - templates/ai-first-saas-starter/backend/src/main/resources/agent-behavior-seeds/starter-v1/user-admin-agent-expertise.yaml
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation
- expected outputs:
  - Add/align seed material for five core v0 functional agents: My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy.
  - Each v0 agent must have a bounded prompt, one simple skill/reference or manifest entry, model config ref, tool-boundary default, and trace requirements.
  - The v0 prompts should explain starter scope, available/deferred capabilities, and safe next steps without pretending full-core features are complete.
  - Ensure `/api/me` returns the five core workstreams visible for the bootstrap tenant admin context when required capabilities exist.
  - Add/update tests for seed idempotency, prompt assembly, skill/reference manifest availability, and no provider secret exposure.
- required checks:
  - `mvn test`
  - `git diff --check`
  - `rg -n "agent-my-account|agent-user-admin|agent-agent-admin|agent-audit-trace|agent-governance-policy|PromptAssemblyTrace|SkillLoadTrace|model" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/main/resources templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - The five left-rail workstreams have explicit governed v0 behavior configuration rather than sharing an unnamed chatbot prompt.
  - A git commit exists for the changes.

### TASK-5WS-02-004: Wire frontend composer to backend message submission

- status: pending
- source: specs/five-core-workstream-v0-starter/backlog/01-five-core-v0-build-backlog.md
- task brief: specs/five-core-workstream-v0-starter/tasks/04-frontend/02-wire-composer-to-message-api.md
- depends on: [TASK-5WS-02-003]
- required reads:
  - templates/ai-first-saas-starter/frontend/src/main.tsx
  - templates/ai-first-saas-starter/frontend/src/api/WorkstreamApiClient.ts
  - templates/ai-first-saas-starter/frontend/src/api/HttpWorkstreamApiClient.ts
  - templates/ai-first-saas-starter/frontend/src/api/FixtureWorkstreamApiClient.ts
  - templates/ai-first-saas-starter/frontend/src/workstream/composer/WorkstreamComposer.tsx
  - templates/ai-first-saas-starter/frontend/src/workstream/stream
- expected outputs:
  - Add `submitWorkstreamMessage` to the frontend workstream API client contract, HTTP client, and fixture client.
  - Replace local composer heuristics in `main.tsx` with backend message submission for normal prompts.
  - Append returned user item, agent item, and `markdown_response` surface to the current workstream.
  - Preserve explicit surface action handling for richer surfaces as follow-up/full-core behavior.
  - Add tests that composer submission targets the selected functional agent and renders returned markdown for each of the five core workstreams.
- required checks:
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
  - `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
  - `git diff --check`
  - `rg -n "submitWorkstreamMessage|/api/workstream/messages|markdown_response|handleComposerSubmit" templates/ai-first-saas-starter/frontend/src`
- done criteria:
  - The browser composer exercises the backend v0 workstream message contract instead of frontend-only prompt heuristics.
  - A git commit exists for the changes.

### TASK-5WS-03-001: Normalize starter fixture data and contract tests around five core markdown v0 surfaces

- status: pending
- source: specs/five-core-workstream-v0-starter/backlog/01-five-core-v0-build-backlog.md
- task brief: specs/five-core-workstream-v0-starter/tasks/05-validation/01-normalize-fixtures-and-contract-tests.md
- depends on: [TASK-5WS-02-004]
- required reads:
  - templates/ai-first-saas-starter/frontend/src/workstream/fixtures/agents.ts
  - templates/ai-first-saas-starter/frontend/src/workstream/fixtures/surfaces.ts
  - templates/ai-first-saas-starter/frontend/src/workstream/fixtures/workstream.ts
  - templates/ai-first-saas-starter/frontend/src/workstream.contract.test.mjs
  - templates/ai-first-saas-starter/frontend/src/workstream-shell.contract.test.mjs
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/MeServiceTest.java
  - templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java
- expected outputs:
  - Ensure fixture and backend bootstrap data consistently show the five core workstreams as the initial visible bootstrap-admin rail set.
  - Ensure each core workstream has an initial/default v0 `markdown_response` surface or can obtain one through message submission.
  - Keep richer full-core surfaces as follow-up/demo surfaces only when tests and copy make that scope explicit.
  - Remove or quarantine confusing rail entries such as denied-example agents from default starter tests if they interfere with the five-core initial shell acceptance.
  - Update frontend/backend contract tests to assert five-core v0 behavior.
- required checks:
  - `mvn test`
  - `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
  - `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
  - `git diff --check`
  - `rg -n "My Account|User Admin|Agent Admin|Audit/Trace|Governance/Policy|markdown_response|five core" templates/ai-first-saas-starter/frontend/src templates/ai-first-saas-starter/backend/src/test/java`
- done criteria:
  - Fixture mode and real API mode communicate the same initial five-core-v0 acceptance target.
  - A git commit exists for the changes.

### TASK-5WS-03-002: Validate scaffold fullstack and update final handoff guidance

- status: pending
- source: specs/five-core-workstream-v0-starter/backlog/01-five-core-v0-build-backlog.md
- task brief: specs/five-core-workstream-v0-starter/tasks/05-validation/02-validate-scaffold-and-handoff.md
- depends on: [TASK-5WS-03-001]
- required reads:
  - specs/five-core-workstream-v0-starter/README.md
  - README.md
  - templates/ai-first-saas-starter/README.md
  - tools/validate-ai-first-saas-starter-fullstack.sh
  - docs/skills-pack-user-guide.md
  - specs/five-core-workstream-v0-starter/pending-tasks.md
- expected outputs:
  - Run the full starter validation script and fix only issues directly caused by the five-core-v0 migration.
  - Update README/user-guide handoff guidance if validation/manual test commands changed.
  - Add final notes in this queue about remaining full-core follow-up tasks that should stay outside the five-core-v0 starter migration.
  - Mark completed migration tasks done; add follow-up tasks only for real discovered gaps that could not safely fit existing task boundaries.
- required checks:
  - `tools/validate-ai-first-saas-starter-fullstack.sh`
  - `git diff --check`
  - `rg -n "five core|core v0|markdown_response|validate-ai-first-saas-starter-fullstack" README.md docs/skills-pack-user-guide.md templates/ai-first-saas-starter/README.md specs/five-core-workstream-v0-starter/pending-tasks.md`
- done criteria:
  - Freshly scaffolded starter validation passes with the five-core-v0 shell target.
  - Remaining full-core work is explicit and not claimed as done.
  - A git commit exists for the changes.
