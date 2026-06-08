# Workstream Full-Core Outline

## Purpose

This outline translates the SMB full-core baseline into workstream-by-workstream implementation scope for child mini-project queues. It extends the five-core v0 contracts into a fuller SMB operating model while staying out of enterprise IAM, SIEM, marketplace, and compliance-suite scope.

Every capability below is a governed backend capability first, then exposed through structured surfaces, request/response agent tools, APIs, workflows, timers, or AutonomousAgent/internal worker task operations only when authorized and traced.

## Shared full-core contract

All five core workstreams must preserve these full-core requirements:

- role-authorized workstream launch and backend capability checks for every browser action, API, agent tool, workflow step, timer, consumer reaction, and internal worker operation;
- selected `AuthContext`, tenant/customer filtering, active membership, non-disabled account status, and role/capability checks;
- typed structured surfaces for dashboards, forms, tables, decisions, diffs, timelines, task progress, denials, provider failures, validation errors, and no-op results;
- request/response Akka `Agent` turns for user-facing composer guidance, summaries, explanations, and guided next actions;
- Akka `AutonomousAgent` only for durable internal/background work whose lifecycle needs task identity, progress, cancellation, retry/failure, notifications, dependencies, or human result review;
- deterministic services for authorization, validation, idempotency, tenant filtering, lifecycle transitions, projection, redaction, outbox/email, trace normalization, policy evaluation, and provider/secret-boundary checks;
- durable audit/work traces for protected reads, denials, side effects, prompt assembly, skill/reference loads, model calls, tool calls, provider failures, worker task lifecycle, approvals, and decisions;
- runtime validation through the real local Akka API/UI path at the stated scope, with missing model/provider configuration failing closed instead of producing deterministic normal responses.

## My Account

### Full-core SMB outcome

My Account becomes the signed-in user's trusted control center: profile, settings, selected context, authority basis, personal attention, and safe cross-workstream navigation. It remains launched from the lower-left user tile/email rather than the top workstream rail.

### Capability outline

- `my_account.view_summary`: account, profile, settings, selected context, membership, and browser-safe authority summary.
- `my_account.view_context`: authorized tenant/customer context list, selected context detail, unsupported-context and missing-context denials.
- `my_account.switch_context`: safe context switch with membership/status validation and trace emission.
- `my_account.update_profile_settings`: self-service profile/settings update, validation, no-op detection, idempotency, and audit.
- `my_account.list_personal_attention`: aggregate authorized attention items from sibling workstreams without leaking hidden workstreams.
- `my_account.open_authorized_workstream`: open a visible workstream or attention item through the shell request pipeline.
- `my_account.view_own_trace_refs`: scoped trace references for the user's own account/context activity.
- `my_account.ask_agent`: request/response My Account Agent guidance using the governed Akka Agent runtime path.

### Surface outline

- personal attention dashboard with next actions, blocked items, failed provider/configuration items, and trace links;
- account summary and authority/context indicator;
- context switcher with first-run, forbidden, disabled, unsupported, and no-membership states;
- profile/settings form surfaces with validation/no-op/success system messages;
- authorized workstream status cards and open-workstream/open-attention-item actions;
- My Account Agent response surfaces with sanitized markdown or richer typed guidance.

### Agent and service outline

- Request/response agent: answer “what can I do?”, explain current authority, summarize attention, guide profile/settings changes, and route users to authorized workstreams.
- Internal worker opportunities: optional personal digest or stale-settings review only if a child task introduces durable task lifecycle and human-visible result review.
- Deterministic services: `/api/me` shaping, context resolution, profile/settings validation, authority filtering, attention aggregation, shell request authorization, trace redaction.

### Runtime validation focus

Validate user tile launch, no duplicate top-rail My Account launcher, context selection, denial states, personal attention queries, profile/settings mutation, trace links, provider fail-closed My Account Agent behavior, and frontend secret-boundary checks.

## User Admin

### Full-core SMB outcome

User Admin lets an owner/operator manage access without spreadsheets or support backdoors: invitations, membership status, role/capability assignments, access review, and risk/attention visibility.

### Capability outline

- `user_admin.view_overview`: user administration readiness, invitation health, role/capability summary, recent changes, and attention counts.
- `user_admin.list_members`: scoped directory with account, membership, status, role/capability basis, and last activity/change evidence.
- `user_admin.invite_user`, `user_admin.resend_invitation`, `user_admin.revoke_invitation`: invitation lifecycle with outbox/Resend status, expiry visibility, idempotency, and audit.
- `user_admin.acceptance_status.read`: invitation acceptance, expiry, failed delivery, and recovery evidence.
- `user_admin.update_member_status`: disable/reactivate with impact preview, last-admin/self-disable guardrails, no-op handling, and trace.
- `user_admin.preview_role_change` and `user_admin.change_member_roles`: role/capability assignment preview, policy checks, approval where required, and audit.
- `user_admin.list_access_reviews`, `user_admin.start_access_review_task`, `user_admin.read_access_review_task`, `user_admin.cancel_access_review_task`, `user_admin.accept_access_review_result`: durable access-review/admin-risk task lifecycle when implemented.
- `user_admin.ask_agent`: governed User Admin Agent turn over scoped user-admin evidence.

### Surface outline

- user directory dashboard with status, invitation, role/capability, recent admin-change, and attention cards;
- invitation panel with send/resend/revoke, outbox delivery status, expiry, validation errors, and actionable failures;
- member directory and detail surfaces with disable/reactivate and role-change previews;
- role/capability matrix focused on SMB-safe roles, not enterprise role-builder complexity;
- access review queue and result surfaces with worker task progress, recommendations, human decisions, and trace links;
- system-message surfaces for forbidden access, missing context, disabled users, last-admin protection, provider/config blocks, and no-op mutations.

### Agent and service outline

- Request/response agent: explain blocked invitations, summarize access risk, help interpret member status, draft safe role-change recommendations, and navigate to trace evidence.
- Internal worker candidates: access-review investigation, stale-invite cleanup suggestions, duplicate-account detection, suspicious admin-change summary, role/capability drift summary.
- Deterministic services: authorization, invitation lifecycle/outbox/idempotency, membership status transitions, role/capability validation, last-admin guardrails, projection/query shaping, audit event emission, trace redaction.

### Runtime validation focus

Validate invitation lifecycle, member directory, role preview/change, disable/reactivate guardrails, tenant isolation, disabled-user denials, outbox failure surfaces, access-review task unavailable/progress states, User Admin Agent provider fail-closed behavior, and audit/trace links.

## Agent Admin

### Full-core SMB outcome

Agent Admin lets authorized operators inspect and safely change AI behavior without hidden prompts or code edits. It governs AgentDefinitions, prompts, skills, references, manifests, model refs, tool boundaries, seeds, provider readiness, and behavior-change lifecycle.

### Capability outline

- `agent_admin.list_definitions` and `agent_admin.get_definition`: catalog/detail for functional agents and internal workers.
- `agent_admin.get_prompt_version`, `agent_admin.get_skill_version`, `agent_admin.get_reference_version`, `agent_admin.get_manifest`, `agent_admin.get_model_ref`, `agent_admin.get_tool_boundary`: governed reads with redaction and trace links.
- `agent_admin.draft_behavior_change`: draft prompt/skill/reference/manifest/model/tool-boundary change without activation.
- `agent_admin.submit_behavior_change_for_review`, `agent_admin.approve_behavior_change`, `agent_admin.reject_behavior_change`, `agent_admin.activate_behavior_change`, `agent_admin.cancel_behavior_change`, `agent_admin.rollback_behavior_change`: audited proposal lifecycle.
- `agent_admin.compare_versions` and `agent_admin.simulate_tool_boundary`: diff, risk, and tool-boundary simulation evidence.
- `agent_admin.list_seed_material` and `agent_admin.reseed_missing_defaults`: default seed visibility and idempotent reseeding.
- `agent_admin.start_behavior_review_task`, `agent_admin.read_behavior_review_task`, `agent_admin.cancel_behavior_review_task`, `agent_admin.accept_behavior_review_result`: durable evaluation/review worker lifecycle when implemented.
- `agent_admin.ask_agent`: governed Agent Admin Agent guidance.

### Surface outline

- agent catalog dashboard with readiness, active versions, provider status, authority tier, internal worker status, and attention counts;
- AgentDefinition detail surface with prompt/skill/reference/manifest/model/tool-boundary cards;
- prompt/skill/reference diff and provenance surfaces;
- tool-boundary detail and simulation surfaces showing read-only vs side-effecting tools, denials, and approval needs;
- behavior change proposal/review/activation/rollback surfaces with risk, impact, authority change, evidence, and trace links;
- seed/default material surface;
- behavior review task progress/result surface for AutonomousAgent-backed review work;
- blocked provider/configuration system messages that do not expose secrets.

### Agent and service outline

- Request/response agent: explain current behavior, summarize config readiness, draft change proposals, interpret tool-boundary denials, and guide reviews.
- Internal worker candidates: behavior-change drafting, prompt-risk review, replay/evaluation analysis, stale-document review, tool-boundary drift review.
- Deterministic services: active config resolution, seed idempotency, version validation, lifecycle rules, manifest/schema checks, ToolPermissionBoundary enforcement, provider/secret readiness checks, redaction, projection, audit/trace emission.

### Runtime validation focus

Validate catalog/detail reads, version/proposal lifecycle, approval/activation/rollback idempotency, tool-boundary simulation and denial, seed material, provider fail-closed diagnostics, governed loader tools, concrete Agent invocation traces, behavior-review task states when implemented, and frontend secret-boundary scans.

## Audit/Trace

### Full-core SMB outcome

Audit/Trace is the practical investigation workspace for who/what/when/why/how-authorized across people, agents, workflows, tools, policies, and provider failures.

### Capability outline

- `audit.trace.dashboard.read`: trace health, important denials/failures, recent filters, and readiness states.
- `audit.trace.search`: scoped search over audit events, workstream requests, capability calls, model/tool traces, worker tasks, and policy decisions.
- `audit.trace.timeline.read`: correlation/request/work-item timeline.
- `audit.trace.detail.read`: redacted event detail/evidence card.
- `audit.trace.failureEvidence.read`: denial, provider, model, tool, capability, workflow, or worker failure evidence.
- `audit.trace.export.copy`: safe copy/export of selected records where SMB scope permits and authority is present.
- `audit.trace.investigationGuide.read`: deterministic next-step guidance and related authorized workstream links.
- `audit.trace.explain`: governed Audit/Trace Agent explanation over already-authorized evidence.
- `audit.trace.summaryTask.start`, `audit.trace.summaryTask.read`, `audit.trace.summaryTask.cancel`, `audit.trace.summaryTask.acceptResult`: durable audit-summary/anomaly-review lifecycle when implemented.

### Surface outline

- audit trace dashboard with search shortcuts, recent denials, provider/tool failures, policy decisions, worker task exceptions, and trace-health cards;
- search/filter command surface for time, actor, workstream, capability, tenant/customer context, event type, risk/severity, outcome, and trace id;
- trace search results table with redaction status and safe links;
- correlation timeline with actor, AuthContext, capability, model/tool, workflow, worker, and decision events;
- detail/evidence cards with denial reason, authorization basis, redacted payload summary, and related traces;
- investigation guidance card and bounded explanation response;
- audit-summary/anomaly-review worker task progress/result surface when implemented.

### Agent and service outline

- Request/response agent: explain trace evidence, summarize what happened, suggest safe next investigative steps, and clarify denials/provider failures.
- Internal worker candidates: scheduled audit summaries, anomalous admin activity review, support-access investigation, policy-drift summaries, repeated provider/tool failure clustering.
- Deterministic services: trace ingestion/query/projection, redaction, tenant filtering, correlation timeline assembly, export/copy authorization, retention-safe browser DTOs, idempotent read tracking where required.

### Runtime validation focus

Validate scoped search/detail/timeline, redaction, cross-tenant denial, failure evidence, provider fail-closed Audit/Trace Agent behavior, trace links from sibling workstreams, worker task states if introduced, and no secret or hidden prompt leakage in frontend payloads.

## Governance/Policy

### Full-core SMB outcome

Governance/Policy lets authorized operators understand active policy, thresholds, proposals, exceptions, approvals, simulations, behavior-authority changes, decisions, and outcomes without creating an enterprise governance suite.

### Capability outline

- `governance.policy.dashboard.read`: posture, pending approvals, exceptions, recent decisions, blocked changes, and attention items.
- `governance.policy.list` and `governance.policy.read`: active policies, thresholds, capability gates, owners, versions, effective dates, and traces.
- `governance.policy.proposal.draft`, `governance.policy.proposal.submit`, `governance.policy.proposal.read`: policy or authority-boundary proposal lifecycle.
- `governance.policy.simulate`: deterministic simulation/replay evidence for proposed changes where data exists.
- `governance.policy.approve`, `governance.policy.reject`, `governance.policy.activate`, `governance.policy.rollback`: human-governed decision lifecycle with idempotency and audit.
- `governance.policy.exception.request`, `governance.policy.exception.decide`, `governance.policy.exception.read`: bounded exception handling where introduced.
- `governance.policy.analysis.start`, `governance.policy.analysis.read`, `governance.policy.analysis.cancel`, `governance.policy.analysis.acceptResult`: durable policy-impact/replay/evaluation worker lifecycle when implemented.
- `governance.policy.ask_agent`: governed Governance/Policy Agent guidance.

### Surface outline

- governance dashboard with active policy posture, pending approvals, exceptions, proposed changes, decision history, and trace links;
- policy inventory and threshold/version detail surfaces;
- proposal diff/risk/impact surface with evidence, authority expansion flags, alternatives, and allowed actions;
- simulation/replay surface with deterministic inputs, expected allows/denials, affected capabilities, warnings, confidence, and traces;
- decision card surface for approvals, rejections, activations, rollbacks, and exceptions;
- policy-impact analysis task surface for AutonomousAgent-backed work;
- provider/configuration/authorization blocked system messages.

### Agent and service outline

- Request/response agent: explain policies, draft proposals, summarize impact evidence, clarify approval requirements, and guide authorized decisions.
- Internal worker candidates: policy-change analysis, exception clustering, replay/evaluation batches, approval-summary drafting, policy drift review, stale exception review.
- Deterministic services: policy evaluation, proposal lifecycle, approval/activation/rollback rules, simulation normalization, threshold enforcement, exception validation, idempotency, redaction, audit/trace emission.

### Runtime validation focus

Validate dashboard/inventory reads, proposal/simulation/decision lifecycle, activation/rollback idempotency, exception states if introduced, tenant isolation, approval denials, Governance/Policy Agent provider fail-closed behavior, worker task surfaces when implemented, and trace links to Agent Admin/User Admin/Audit surfaces.

## Cross-workstream dependency notes

- My Account depends on shared identity, context, membership, role/capability, `/api/me`, and attention projection foundations.
- User Admin produces membership, role, invitation, and access-review traces consumed by Audit/Trace and Governance/Policy.
- Agent Admin owns behavior artifact lifecycle that all request/response Agents and AutonomousAgent/internal workers depend on.
- Audit/Trace must ingest or read trace records emitted by all workstreams before rich investigation dashboards can be complete.
- Governance/Policy depends on User Admin authority data, Agent Admin behavior artifacts, and Audit/Trace evidence for policy proposals, simulations, and decisions.
- All workstreams depend on the shared shell request pipeline, typed surface renderer, backend capability facade, trace substrate, provider fail-closed boundary, and visual UX quality standard.

## Child mini-project planning requirements

Each child mini-project should start by selecting a vertical SMB slice and naming:

1. capability ids and exact AuthContext/tenant/customer rules;
2. structured dashboard/surface contracts and visual acceptance criteria;
3. request/response Agent behavior and provider fail-closed path;
4. AutonomousAgent/internal worker tasks only where durable lifecycle is justified;
5. deterministic service responsibilities;
6. audit/work trace fields and trace-link surfaces;
7. local Akka runtime/API/UI validation commands and fallback behavior when provider credentials are absent.
