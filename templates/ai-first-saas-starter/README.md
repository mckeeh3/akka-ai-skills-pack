# AI-First SaaS Starter Template

This template is the isolated scaffold source for a secure AI-first SaaS application generated with the skills pack.

It is the canonical runnable implementation baseline after placeholders are rendered by the scaffold command. Before rendering, it is template source rather than a directly runnable project.

Reference distinction:
- `templates/ai-first-saas-starter/app-description/` is the canonical description-layer/reference asset for structure, semantics, UI style guide, readiness review, and future generation input.
- this template is the scaffold source for local executable backend/frontend behavior.
- when docs and template both describe the same concept, preserve the starter core app-description as the meaning contract and this template as its runnable starter realization.

## Minimum-first scaffold path

Natural-language requests for a “minimum AI-first app,” “starter app,” “basic app,” “smallest useful app,” or chatbot-like initial SaaS must start from the canonical minimum doctrine in `docs/minimum-ai-first-saas-app.md`: the **five core v0 workstream set**, not a generic chatbot or single-workstream admin slice.

The first runnable starter target is intentionally narrower than full-core readiness. It must provide bootstrap authorization, selected `AuthContext`, bounded AI-first managed functional agents, durable workstream log entries, real model-backed `markdown_response` v1 surfaces produced through a concrete Akka `Agent` component, active runtime configuration resolution, governed loader tools, `ToolPermissionBoundary` enforcement, `effects().tools(runtimeTools)` registration, backend capability boundaries, provider/configuration failure handling, and audit/work trace substrate for these five core v0 workstreams. My Account is launched only by clicking the signed-in user tile/email at the bottom of the left rail; it is not listed with the other top-rail workstreams:

1. My Account
2. User Admin
3. Agent Admin
4. Audit/Trace
5. Governance/Policy

The scaffold must record follow-up work that distinguishes five-core-v0 readiness from full-core readiness: richer structured surfaces, complete invitation onboarding, support access, full governed agent document lifecycle, searchable audit/trace views, policy/governance workflows, security hardening, and only then app-specific domain workstreams.

The five-core starter is also the initial workstream graph baseline:

- each core workstream has a role-specific dashboard surface whose job is to answer what requires this actor's attention and what work can be done next;
- the human surface graph starts at that dashboard, continues through `markdown_response` v1, `system_message`, list/detail/decision/audit/workflow/governance surfaces already present in the starter, and records surface actions as graph edges;
- every protected surface action maps to a governed-tool inside an existing capability boundary and is exposed only through qualified browser-tool, agent-tool, or internal-tool paths as appropriate;
- the internal workstream agent graph is intentionally bounded at v0: request-based functional agents can read authorized evidence, use governed loader tools, emit safe answers or system messages, and create human attention; durable internal/background worker nodes remain follow-up unless explicitly implemented with governed capabilities and tests;
- workstream expertise comes from seeded prompt, skill, reference, manifest, and boundary records that explain each workstream's dashboard purpose, surface graph, governed-tools, denials, and safe user-help semantics.

This template may contain broader full-core scaffold assets, but generated-app guidance must not claim five-core-v0 readiness unless normal workstream message submission uses the governed runtime path, resolves active configuration from managed records, invokes the `WorkstreamRuntimeAgent` Akka Agent component with `effects().tools(runtimeTools)`, and uses a configured backend model provider, and must not claim full-core readiness unless those follow-up gates are satisfied and tested. Direct `ModelProviderClient` or service-only provider calls are support seams, not a substitute for the user-facing Akka Agent runtime path.

## Base package policy

The scaffold flow must ask before materializing Java files:

> What Java base package should I use for generated code? Press Enter to use `ai.first`.

If the user accepts or defers, use `ai.first`.

Rendering rules:

- `{{JAVA_BASE_PACKAGE}}` becomes the selected Java package, for example `ai.first`.
- `{{JAVA_PACKAGE_PATH}}` becomes the matching source path, for example `ai/first`.
- `{{MAVEN_GROUP_ID}}` defaults to the selected Java package.
- `com.example` is not used by this starter unless the user explicitly requests it as the selected package.

## Backend skeleton

The backend skeleton starts at `backend/` and renders into the target project root as:

```text
pom.xml
src/main/java/<selected package>/api
src/main/java/<selected package>/application
src/main/java/<selected package>/domain
src/main/resources
src/test/java/<selected package>
```

The package layout follows the skills-pack convention:

- `domain` contains pure records, validation, command/event decisions, and shared foundation types.
- `application` contains Akka components: entities, views, workflows, consumers, timed actions, and agents.
- `api` contains HTTP/gRPC/MCP endpoints and API DTOs.

The scaffolded backend foundation includes:

- canonical Account/Profile/Settings/Tenant/Customer/Membership/Role/AuthContext/AdminAudit domain records;
- local AuthContext resolution from WorkOS JWT identity plus Akka-owned account and membership state;
- JWT-protected `GET /api/me` returning browser-safe account, profile, settings, selected context, memberships, capabilities, functional-agent availability, and audit correlation;
- backend denial paths for disabled accounts, missing memberships, forbidden selected contexts, and tenant/customer mismatch;
- invitation onboarding and user administration services with captured-outbox/Resend boundary, idempotency, and audit behavior;
- a durable Akka invitation repository path: `DurableInvitationRepositoryEntity` stores current invitation/outbox state behind the existing `InvitationRepository` contract through `AkkaInvitationRepository`;
- a durable Akka governed-agent repository path: `DurableAgentBehaviorRepositoryEntity` stores current AgentDefinition, PromptDocument, SkillDocument, ReferenceDocument, AgentSkillManifest, AgentReferenceManifest, and ToolPermissionBoundary records behind `AgentBehaviorRepository` through `AkkaAgentBehaviorRepository`;
- governed runtime agent records, seed import, deterministic prompt assembly with compact skill/reference manifests, authorized `readSkill(skillId)` and `readReferenceDoc(referenceId)`, behavior-change proposal semantics, and trace records;
- workstream API services for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy surface payloads;
- a shared backend-owned attention backbone for actionable `AttentionItem` lifecycle state, workstream dashboard items, My Account aggregate attention, backend-derived left-rail summaries, safe redaction, and audit/work traces;
- bounded attention producer integration for starter state changes: invitation delivery failures/resolutions, governance proposal/approval lifecycle, timed invitation-delivery checks, worker/task blocked/review states, and explicit backend-derived attention refresh after producer-affecting actions;
- a bounded v3 governed workstream event backbone: `WorkstreamEventEnvelope`/`WorkstreamEventSourceRef`, Akka-backed event repository seam, event publication for starter invitation and access-review lifecycle states, an idempotent event-to-attention consumer, and backend-derived projection-refresh hints;
- a governed notification foundation: backend-owned `NotificationItem` projection from authorized attention/event/digest/worker source state, durable notification repository seam, My Account notification center surface data, lifecycle/preference capabilities, AuthContext redaction, Resend/captured-outbox email preference boundary, and provider-neutral fail-closed webhook/SMS/mobile-push/Slack/Teams seams with captured local/test external outboxes and redacted delivery-attempt records;
- service tests that can run after scaffold placeholder rendering.

Current durability and attention coverage:

- durable/component seam present and normally bound when `ComponentClient` is available: invitation current state and captured email outbox through `DurableInvitationRepositoryEntity` plus event-sourced audit-grade lifecycle facts through `InvitationLifecycleHistoryEntity`; tests cover lookup, tenant-scoped duplicate detection, idempotent outbox enqueue, create/queue/delivery/accept/revoke/expire/denial/no-op history, and raw-token redaction;
- durable/component seam present and normally bound when `ComponentClient` is available: governed-agent records through `AkkaAgentBehaviorRepository`, `AgentDefinitionEntity`, document/manifest/tool-boundary entities, `DurableAgentBehaviorRepositoryEntity`, and `AgentBehaviorRepositoryState`, covering seeded AgentDefinition, active prompt, active skill, active reference, compact skill/reference manifests, model policy/config, and tool boundary records used by prompt assembly, `readSkill(skillId)`, and `readReferenceDoc(referenceId)`; prompt, skill, reference, manifest, and tool-boundary entities also store browser-safe lifecycle facts for activation, rollback, denial, no-op, and authority-expansion audit history without exposing raw content or secrets;
- durable/component seam present and normally bound when `ComponentClient` is available: `AkkaAgentRuntimeTraceSink` stores prompt assembly, skill/reference load, model invocation, and AgentWorkTrace facts in `AgentRuntimeTraceEntity` for projection by `AgentRuntimeTraceView`;
- test-only adapters live under backend test source; normal runtime paths use Akka component-backed repositories/sinks once the Akka `ComponentClient` binding is available; missing internal Akka persistence is not an acceptable completed-runtime substitute;
- durable normal path present for workstream messages where `ComponentClient` is available: `WorkstreamEndpoint` binds `AkkaWorkstreamLogRepository`, Akka invitation and governed-agent repositories, and durable agent runtime trace storage; service construction without `ComponentClient` is an unbound pre-runtime state for tests or pre-binding setup only, not a completed feature path;
- shared attention path present for the starter/reference scope: `AttentionService` and the normal repository binding own actionable attention state; `AttentionProducerService` maps real starter service/timer/task states to idempotent upsert/resolve behavior; workstream dashboards, My Account, and rail summaries read backend attention state; frontend `railAttentionState` remains only transient unseen-response/background-activity presentation state;
- governed event path present for the bounded v3 starter/reference scope: `WorkstreamEventPublisher` stores typed browser-safe `WorkstreamEventEnvelope` records through the Akka-backed repository seam, `WorkstreamEventAttentionConsumer` maps allow-listed invitation/access-review/prompt-risk/audit-summary/governance-impact lifecycle events plus provider-neutral membership/role, support-access, governed-artifact, policy-simulation, export, and notification lifecycle events into attention/projection state with source/idempotency refs, and `WorkstreamService.events(...)` emits `projection.refresh.available` hints that require backend projection reloads;
- governed notification path present for the starter/reference scope: `NotificationService` projects authorized attention, event/projection-refresh, personal digest, and worker task evidence into backend-owned in-app `NotificationItem` records, `DurableNotificationRepositoryEntity` provides the Akka-backed repository seam for notification items, preferences, redacted delivery attempts, and captured local/test external outbox rows, `notification.list_my_account_center` and lifecycle/preference tools own My Account notification center state, `notification.delivery.list_platform` exposes browser-safe channel registry state, `notification.delivery.evaluate_external` fails closed for webhook/SMS/mobile-push/Slack/Teams while capturing local/test intent, and hidden/cross-tenant source state is omitted or returned as `not_found_or_redacted` without names or counts;
- first durable internal/background `AutonomousAgent` vertical present for the starter/reference scope: User Admin access-review start/query/lifecycle/result review uses `UserAdminAccessReviewAutonomousAgent`, typed `UserAdminAccessReviewTasks`, `ComponentClientAccessReviewAutonomousAgentRuntime`, `autonomousAgentTaskId` projection, fail-closed runtime behavior when provider/runtime config is absent, `workflow.access_review.*` and `worker.task.*` v3 events with `autonomous_task` source refs, worker-task attention, and `surface-user-admin-access-review` result-review surfaces;
- second durable internal/background `AutonomousAgent` vertical present for the starter/reference scope: Agent Admin prompt-risk review uses `AgentAdminPromptRiskAutonomousAgent`, typed `AgentAdminPromptRiskTasks`, `ComponentClientPromptRiskAutonomousAgentRuntime`, durable `PromptRiskReviewTask` projection with `autonomousAgentTaskId`, provider/runtime fail-closed behavior, `workflow.agent_admin.prompt_risk_review.*` and `worker.task.*` v3 events with `autonomous_task` source refs, worker-task attention, and `surface-agent-admin-prompt-risk-review` advisory result-review surfaces for prompt, skill, reference, model, and tool-boundary change risk;
- third durable internal/background `AutonomousAgent` vertical present for the starter/reference scope: My Account personal attention digest uses `MyAccountPersonalAttentionDigestAutonomousAgent`, typed `MyAccountPersonalAttentionDigestTasks`, `ComponentClientMyAccountPersonalAttentionDigestAutonomousAgentRuntime`, durable personal-digest task projection with `autonomousAgentTaskId`, provider/runtime fail-closed behavior, `workflow.my_account.personal_attention_digest.*` and `worker.task.*` v3 events with `autonomous_task` source refs, worker-task attention, and My Account digest progress/result/blocked surfaces for authorized personal attention evidence;
- fourth durable internal/background `AutonomousAgent` vertical present for the starter/reference scope: Audit/Trace summary uses `AuditTraceSummaryAutonomousAgent`, typed `AuditTraceSummaryTasks`, `ComponentClientAuditTraceSummaryAutonomousAgentRuntime`, durable summary-task projection with `autonomousAgentTaskId`, provider/runtime fail-closed behavior, `workflow.audit_trace.summary_*` and `worker.task.*` v3 events with `autonomous_task` source refs, worker-task attention, and audit-summary progress/review surfaces for scoped, redacted audit/work-trace evidence;
- fifth durable internal/background `AutonomousAgent` vertical present for the starter/reference scope: Governance/Policy impact analysis uses `GovernancePolicyImpactAutonomousAgent`, typed `GovernancePolicyImpactTasks`, `ComponentClientGovernancePolicyImpactAutonomousAgentRuntime`, durable `GovernancePolicyImpactTask` projection with `autonomousAgentTaskId`, provider/runtime fail-closed behavior, `workflow.governance_policy.impact_analysis.*` and `worker.task.*` v3 events with `autonomous_task` source refs, worker-task attention, and `surface-governance-policy-impact-analysis-task` / `surface-governance-policy-impact-analysis-result` advisory surfaces for scoped policy-change impact evidence, approval-gate findings, redaction findings, and required human decisions;
- remaining slices: broader task/AutonomousAgent notification coverage beyond the implemented access-review, prompt-risk, personal-digest, audit-summary, and governance-impact queued/running/blocked/failed/completed-review/cancelled/accepted/rejected starter states, provider-specific SMS/mobile-push/webhook/Slack/Teams production adapters after Q-001 selects providers, broad notification analytics beyond the current channel registry/delivery-attempt summaries, future digest-platform work beyond the bounded My Account personal attention digest and Audit/Trace summary workers, and a future policy simulation platform beyond the bounded Governance/Policy impact-analysis worker.

## Internal/background agent guidance

The starter's five core v0 workstreams remain user-facing request/response workstreams backed by a concrete request-based Akka `Agent` component. Do not migrate `WorkstreamRuntimeAgent`, the persistent composer path, or normal `markdown_response` generation to `AutonomousAgent` by default; those turns must keep the governed runtime path with active managed configuration resolution, governed loader tools, `ToolPermissionBoundary` enforcement, `effects().tools(runtimeTools)`, provider fail-closed behavior, and trace emission.

When extending the starter with durable internal/background agent work, default to Akka `AutonomousAgent` when the work has typed task lifecycle, model-driven iteration, dependencies, cancellation/failure, snapshots, notifications, delegation, handoff, team coordination, moderation, or independent long-running execution. Good future-worker examples include invitation-drafting queues, support-access review, audit-summary batch/platform extensions beyond the implemented bounded summary worker, governance replay/evaluation loops beyond the implemented impact-analysis slice, digest-platform extensions beyond the implemented personal attention digest, monitoring/remediation processors, and specialist agent work that continues after a user-facing turn. Each future worker remains future work until it has its own governed capabilities, fail-closed provider/runtime path, v3 events, attention mapping, structured surfaces, tests, and rendered scaffold validation.

Current implemented verticals:

- User Admin Access Review is a durable internal/background `AutonomousAgent` path, not a replacement for request-based workstream chat. It starts from governed User Admin access-review capabilities, stores both the starter task id and Akka `autonomousAgentTaskId`, maps task lifecycle to backend projections, emits `workflow.access_review.*` and `worker.task.*` events, drives `attention:worker-task:<taskId>:task-state`, and renders backend-derived progress/result/review state on `surface-user-admin-access-review`. Successful recommendations require the real Akka `AutonomousAgent` task path; missing provider/runtime setup fails closed with actionable blocked/provider status, traces, events, and attention instead of deterministic, fake, canned, or model-less success.
- Agent Admin Prompt-Risk Review is the second durable internal/background `AutonomousAgent` path. It starts from governed Agent Admin prompt-risk capabilities (`agent_admin.prompt_risk_review.start/read/cancel/accept_result/reject_result`), reviews prompt/skill/reference/model/tool-boundary behavior proposals, stores `PromptRiskReviewTask` plus Akka `autonomousAgentTaskId`, emits `workflow.agent_admin.prompt_risk_review.*` and `worker.task.*` events, drives `attention:worker-task:<taskId>:task-state`, and renders backend-derived advisory review state on `surface-agent-admin-prompt-risk-review`. Accepting or rejecting a prompt-risk result records a human review decision only; it must not activate prompts, skills, references, models, tool boundaries, or managed-agent definitions. Successful prompt-risk findings require the real Akka `AutonomousAgent` task path; missing provider/runtime setup fails closed with actionable blocked/provider status, traces, events, and attention instead of deterministic, fake, canned, or model-less success.
- My Account Personal Attention Digest is the third durable internal/background `AutonomousAgent` path. It starts from governed My Account digest capabilities, summarizes only authorized personal attention evidence for the selected `AuthContext`, stores a personal digest task plus Akka `autonomousAgentTaskId`, emits `workflow.my_account.personal_attention_digest.*` and `worker.task.*` events, drives `attention:worker-task:<taskId>:task-state`, and renders backend-derived digest progress/result/blocked state on My Account surfaces. Accepting or rejecting a digest result records an advisory task disposition only; it must not acknowledge, dismiss, resolve, expire, or mutate source attention items or perform protected workstream actions. Successful digest findings require the real Akka `AutonomousAgent` task path with scoped/redacted evidence; missing provider/runtime/evidence/tool setup fails closed with actionable blocked/provider status, traces, events, attention, and no deterministic, simulated, fake, canned, or model-less success. A broader scheduled digest/notification platform remains future work and must not be inferred from this bounded implemented vertical.
- Audit/Trace Summary is the fourth durable internal/background `AutonomousAgent` path. It starts from governed Audit/Trace summary capabilities (`audit.trace.summary_task.start/read/cancel/accept_result/reject_result`), summarizes scoped audit/work-trace evidence, stores a summary task plus Akka `autonomousAgentTaskId`, emits `workflow.audit_trace.summary_*` and `worker.task.*` events, drives `attention:worker-task:<taskId>:task-state`, and renders backend-derived progress/result/review state on audit-summary surfaces. Accepting or rejecting a summary result records a human review disposition only; it must not mutate audit records, traces, users, memberships, roles, policies, provider configuration, or attention except through backend-derived task-state projections/events. Successful summary findings require the real Akka `AutonomousAgent` task path with scoped/redacted evidence; missing provider/runtime/evidence/tool setup fails closed with actionable blocked/provider status, traces, events, attention, and no deterministic, simulated, fake, canned, or model-less success. A broader scheduled digest/export platform remains future work and must not be inferred from this bounded implemented vertical.
- Governance/Policy Impact Analysis is the fifth durable internal/background `AutonomousAgent` path. It starts from governed Governance/Policy capabilities (`governance.policy.impact_analysis.start/read/cancel/accept_result/reject_result/request_changes`), reviews proposed policy, approval-rule, threshold, permission/capability-boundary, provider/model policy, or `ToolPermissionBoundary` changes, stores `GovernancePolicyImpactTask` plus Akka `autonomousAgentTaskId`, emits `workflow.governance_policy.impact_analysis.*` and `worker.task.*` events, drives `attention:worker-task:<taskId>:task-state`, and renders backend-derived advisory progress/result state on `surface-governance-policy-impact-analysis-task` and `surface-governance-policy-impact-analysis-result`. Accepting, rejecting, or requesting changes on the result records a human review disposition only; it must not approve, reject, activate, roll back, mutate policy/governance state, or expand capability/tool authority. Successful policy impact findings require the real Akka `AutonomousAgent` task path with scoped/redacted evidence; missing provider/runtime/evidence/tool setup fails closed with actionable blocked/provider status, traces, events, attention, and no deterministic, simulated, fake, canned, or model-less success. A broader policy simulation platform remains future work and must not be inferred from this bounded implemented vertical.

Generated-app extensions must expose those background tasks through governed capabilities before wiring APIs or UI: authorize task start/query/result/cancel/notification operations, scope task and agent instance ids by tenant/customer/AuthContext, enforce model policy and provider-secret boundaries, enforce `ToolPermissionBoundary` before registering tools, record `PromptAssemblyTrace`/`SkillLoadTrace`/`ReferenceLoadTrace`/`AgentWorkTrace` plus task lifecycle traces, and route side-effecting or authority-expanding actions to approval/decision-card surfaces. Akka autonomous `AgentDefinition` means the SDK definition returned by `AutonomousAgent.definition()` or supplied through `AgentSetup`; qualify it separately from this starter's governed managed-agent `AgentDefinition` records.

The scaffold includes the validated React/Vite workstream frontend under `frontend/`. Its production build writes Akka static resources to `src/main/resources/static-resources/`, and `StarterFrontendEndpoint` serves `/`, `/ui`, `/workstream`, `/favicon.ico`, and `/assets/**` while protected APIs remain under `/api/...`.

The frontend includes a My Account notification center surface for backend-derived notification data. Unread/visible counts, item lifecycle, preference summaries, channel registry rows, provider-neutral fail-closed delivery attempts, captured local/test external outbox rows, and source-open actions are backed by notification/source capabilities such as `notification.list_my_account_center`, `notification.mark_read`, `notification.dismiss`, `notification.archive`, `notification.snooze`, `notification.update_preferences`, `notification.delivery.list_platform`, and `notification.delivery.evaluate_external`; frontend-only badges, toasts, or fixture state are presentation/test hints only. Webhook/SMS/mobile-push/Slack/Teams production delivery remains unimplemented until Q-001 selects real providers; unavailable channels must not report delivery success.

## Full-core SMB release-readiness status

As of the real Akka runtime replacement work on 2026-05-30, the starter is **release-ready for the stronger Akka-component-backed normal-runtime bar at the documented SMB starter scope**. Fullstack rendered validation passed after backend, frontend, and static-asset remediation. Normal completed runtime paths no longer silently depend on non-Akka substitute/default fixture stores: workstream-log, identity, invitation, access-review, audit, governance, governed-agent behavior, and agent runtime trace paths bind Akka durable components when `ComponentClient` is available; non-ComponentClient foundation ports do not expose substitute normal runtime state; frontend fixtures are test-only and production-like static resources scan clean for fixture/demo/provider-secret markers.

The earlier release-readiness evidence remains valid for provider fail-closed, governed-agent runtime, visual UX, and secret-boundary checks, and the durability remediation adds the stronger normal-runtime bar. This status remains bounded by the recorded release handoff and does not expand scope to enterprise IAM, SIEM/legal hold/e-discovery, compliance suites, marketplace prompts, arbitrary tenant-managed tool binding, policy-as-code authoring, full policy simulation, digest/export platforms, or optional durable background workers beyond the implemented User Admin access-review, Agent Admin prompt-risk, My Account personal attention digest, Audit/Trace summary, and Governance/Policy impact-analysis AutonomousAgent verticals.

This release status depends on the same runtime completion doctrine used by the starter: normal model-backed workstream message submission must use the governed Akka Agent runtime path, including active managed configuration resolution, `WorkstreamRuntimeAgent`, governed loader tools, `ToolPermissionBoundary`, `effects().tools(runtimeTools)`, trace emission, and a configured backend model provider. Missing or blank provider configuration must fail closed with actionable `system_message`/provider-blocked behavior; it must not return deterministic, mock, canned, model-less, or fixture-backed normal success responses.

Recommended post-release/manual QA items are mobile viewport/off-canvas rail inspection, a final rendered production static asset secret scan after any future source changes, and frontend bundle-size optimization if the Vite chunk-size warning becomes operationally relevant.

## Fullstack scaffold validation

From the skills-pack source repository, validate the rendered starter with one command:

```bash
tools/validate-ai-first-saas-starter-fullstack.sh
```

The validation command scaffolds this template into a temporary target, verifies rendered backend/frontend paths, checks that `WorkstreamRuntimeAgent` still registers runtime tools with `effects().tools(runtimeTools)`, runs `mvn test` including governed agent seed/runtime tests and Akka Agent runtime guards for the five core v0 workstreams, runs `npm install`, `npm test -- --run`, `npm run typecheck`, and `npm run build`, verifies the frontend build wrote Akka static resources under `src/main/resources/static-resources/`, scans the built static assets for obvious backend secret markers, and reports the optional provider smoke state. If `OPENAI_API_KEY` is absent, the provider smoke is skipped loudly and validation still passes; if backend model-provider env is present, it runs a targeted real model smoke through one message in each five-core v0 workstream, backend workstream message submission, the `ComponentClient`-backed `WorkstreamRuntimeAgent`, runtime tool registration, trace ids, and secret-boundary checks. Use `--keep` to retain the generated target for inspection.

Focused workstream icon proof from the skills-pack source repository:

```bash
tools/prove-workstream-icons-v0.sh
```

This scaffolds the starter into a temporary target without network access and verifies that User Admin, Agent Admin, Audit/Trace, and Governance/Policy expose descriptor-backed left rail icon affordances while My Account remains launched only from the lower-left signed-in user tile.

## Local build and manual-test commands

From a scaffolded project:

```bash
mvn test
cd frontend
npm install
npm test -- --run
npm run typecheck
npm run build
cd ..
mvn compile exec:java
```

Optional provider smoke from the skills-pack source repository:

```bash
# Skip mode is safe for CI with no provider secrets. This must report an
# explicit skip, not model-backed success:
env -u OPENAI_API_KEY tools/smoke-ai-first-saas-starter-real-model.sh

# Real mode runs a targeted JUnit smoke in a rendered scaffold and submits one
# prompt through each five-core v0 workstream via backend WorkstreamService:
export OPENAI_API_KEY="sk-..."
export OPENAI_MODEL_ID="gpt-4o-mini" # optional; defaults to gpt-4o-mini
export OPENAI_API_BASE_URL="https://api.openai.com/v1" # optional default
export OPENAI_REQUEST_TIMEOUT_SECONDS="30" # optional default
tools/smoke-ai-first-saas-starter-real-model.sh
```

Real mode must pass through the `ComponentClient`-backed `WorkstreamRuntimeAgent`, assert provider-backed `markdown_response` output, prompt/model/work trace ids, durable/in-process trace shape, and provider-secret redaction in smoke logs, frontend env files, and static assets. If `OPENAI_API_KEY` is missing or blank, the command exits successfully only as an environmental skip with enablement guidance; it must not fake deterministic, canned, simulated, or model-less success. If real mode fails, keep the rendered target with `--keep` and rerun the focused backend test from that scaffold with `mvn -DrealModelProviderSmoke=true -Dtest=RealModelProviderSmokeTest test` after confirming the backend-only provider variables are exported.

The starter's Akka model-provider config intentionally keeps `temperature` and `top-p` at OpenAI default-compatible values because some supported OpenAI models reject non-default sampling overrides. Keep any future sampling overrides model-specific.

Then open the Akka-hosted frontend at `http://localhost:9000/` or `http://localhost:9000/ui` depending on the local Akka port. Normal starter testing should exercise `/api/workstream/...` backend APIs. Frontend-only inspection data is available only through the Vite dev server, or through a build explicitly created with `VITE_ENABLE_FIXTURE_WORKSTREAM=true`; production-like static resources omit that local inspection path.

## Local environment and AuthKit bootstrap

The scaffold renders `.env.example` into the target project. Copy it to `.env` and fill in provider values before local manual testing that needs real WorkOS/AuthKit, Resend, or model-backed agent calls. Provider settings for workstream agents are backend-only; missing or blank model-provider settings must block message submission with an actionable error, not silently fall back to deterministic response text:

```bash
cp .env.example .env
set -a
source .env
set +a
```

For local WorkOS/AuthKit sign-in:

1. Create/select a WorkOS application for local development.
2. Add `http://localhost:9000` as the AuthKit redirect/callback URI.
3. Put the public client id in `frontend/.env.local` as `VITE_WORKOS_CLIENT_ID`; keep `VITE_WORKOS_REDIRECT_URI=http://localhost:9000`.
4. Put backend-only WorkOS values in `.env`: `WORKOS_API_KEY`, `WORKOS_JWT_ISSUER`, and `WORKOS_JWT_AUDIENCE` from the same WorkOS environment, plus `APP_PUBLIC_BASE_URL=http://localhost:9000`.
5. Set `ADMIN_USERS="your.email@example.com:SAAS_OWNER_ADMIN:OWNER"` before running the backend for real local AuthKit testing. Add tenant admins as `email:TENANT_ADMIN:tenant-starter` when needed.
6. Set `RESEND_API_KEY` and `INVITE_EMAIL_FROM` or `RESEND_FROM_EMAIL` before testing production invitation email delivery; local/dev/test may use the captured outbox adapter.

AuthKit access tokens may contain `sub` without `email`. The backend resolves `/api/me` identity through `WorkosIdentityResolver`: it uses email-like token claims when present and otherwise calls WorkOS user-management server-side with backend-only `WORKOS_API_KEY`. This lookup does not authorize users by itself; local Akka `ADMIN_USERS`, account, membership, and invitation state remains authoritative.

Important variables:

- backend-only: `WORKOS_API_KEY`, `WORKOS_API_BASE_URL`, `WORKOS_JWT_ISSUER`, `WORKOS_JWT_AUDIENCE`, `ADMIN_USERS`, `RESEND_API_KEY`, `RESEND_FROM_EMAIL`, `INVITE_EMAIL_FROM`, `INVITE_EMAIL_SUBJECT`, `RESEND_API_BASE_URL`, `OPENAI_API_KEY`, model id, provider endpoint, and provider timeout;
- browser-public: `VITE_WORKOS_CLIENT_ID`, `VITE_WORKOS_REDIRECT_URI`.

First-admin semantics are intentionally closed:

- there is no open self-registration and no silent privileged account creation from `/api/me`;
- `ADMIN_USERS` is an explicit first-admin allowlist for the clean local scaffold using `email:ROLE:scope` entries, such as `email:SAAS_OWNER_ADMIN:OWNER`, `email:TENANT_ADMIN:tenant-starter`, or `email:CUSTOMER_ADMIN:tenant-starter/customer-123`;
- if `ADMIN_USERS` is unset, no privileged user is silently created for normal runtime; configure explicit first-admin entries before local AuthKit testing;
- generated projects must keep durable local authorization state and audited bootstrap/import flow bound through Akka components; absent that binding, the starter fails closed instead of silently using volatile foundation state.

Never put backend secrets into frontend env files or built static assets.

Generated backend code that loads required environment values must treat missing, empty, and blank values as unset. Startup/readiness or blocked-operation logs must include the exact missing env var name, for example `Required backend environment variable [WORKOS_API_KEY] is not set or is blank`, and must never log secret values.

## Manual real-model smoke checklist

Run this after the workstream-agent runtime is implemented and before calling the five core v0 starter functional. The automated provider smoke is `tools/smoke-ai-first-saas-starter-real-model.sh`; it skips when `OPENAI_API_KEY` is absent and, when enabled, verifies a `ComponentClient` → `WorkstreamRuntimeAgent` Akka Agent invocation, provider-backed `markdown_response`, PromptAssemblyTrace/MODEL_INVOCATION/AgentWorkTrace shape, trace ids, and provider-secret redaction:

1. Load `.env` with backend-only WorkOS/AuthKit, JWT, admin-bootstrap, and model-provider variables such as `OPENAI_API_KEY`; keep provider secrets out of `frontend/.env*`.
2. Run `mvn test`, frontend tests/typecheck/build, and `mvn compile exec:java` from the rendered project root where `pom.xml` and `src/` live.
3. Sign in through AuthKit as a configured `ADMIN_USERS` account.
4. Open the workstream UI and submit one short prompt in each workstream: open My Account from the signed-in user tile/email at the bottom of the left rail, then use the top-rail entries for User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
5. Verify each response is an Akka Agent-backed, provider-backed `markdown_response` and that prompt/model/work trace metadata is present with secrets redacted.
6. Inspect `/api/me`, workstream payloads, trace surfaces, `frontend/.env*`, and built static resources for secret-boundary violations; no `OPENAI_API_KEY` value or backend secret should appear.
7. Restart without model-provider variables and verify message submission is blocked with actionable provider-configuration copy instead of deterministic fallback output.
