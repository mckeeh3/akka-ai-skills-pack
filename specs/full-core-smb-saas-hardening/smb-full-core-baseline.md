# SMB Full-Core Baseline

## Purpose

This baseline defines what “full-core” means for the five-core AI-first SaaS starter when the target customer is a real small/medium business (SMB). It is an implementation contract for child mini-project queues, not a broad enterprise roadmap.

The starter becomes SMB full-core when a small business can run its secure SaaS administration, agent behavior management, governance, and audit work through the agent workstream shell without relying on page-first CRUD screens, fixture-only behavior, deterministic model-less normal responses, or unaudited manual backdoors.

## Product stance

Full-core SMB is:

- secure SaaS foundation that is usable by an owner/operator and a small admin team;
- role-authorized functional/context-area agents as the primary navigation and work model;
- continuous workstreams with durable request/result history;
- structured surfaces and dashboards that answer “what needs attention?” before offering forms or tables;
- governed capabilities behind every action, query, agent tool, background task, workflow, timer, and API;
- request/response Akka `Agent` turns for user-facing workstream help, explanations, summaries, and guided actions;
- Akka `AutonomousAgent` or equivalent internal worker agents for durable tedious/background work whose lifecycle justifies task semantics;
- deterministic services for mechanical validation, authorization, lifecycle, trace normalization, projection, email/outbox, idempotency, and policy enforcement;
- real local runtime validation at the stated scope.

Full-core SMB is not:

- enterprise IAM, SIEM, legal hold, multi-region administration, complex procurement/compliance suites, or full SSO administration;
- a generic CRUD admin console with chat added later;
- a page tree as the product architecture;
- deterministic/demo/mock/simulated/model-less user-facing agent behavior;
- direct provider/service calls that bypass the governed Akka Agent runtime path.

## Baseline completion gates

A child mini-project may call a workstream or cross-cutting feature SMB full-core only when these gates are satisfied for its scope.

| Gate | Required standard |
|---|---|
| Workstream model | The feature is placed in an authorized functional-agent workstream, or explicitly marked as internal-only. |
| Surface model | User-visible output uses typed structured surfaces, including `system_message` for denials, validation, deferred work, provider failures, and recovery. |
| Capability model | Every surface action, dashboard query, API, agent tool, workflow step, timer, consumer reaction, and internal worker task maps to a governed capability contract. |
| Authorization | Backend checks selected `AuthContext`, account status, membership, role/capability, tenant/customer scope, and disabled-user behavior. |
| Audit/trace | Protected reads, denials, side effects, prompt assembly, skill/reference loads, tool calls, model calls, worker task lifecycle, and decisions emit durable audit/work traces. |
| Runtime validation | Local Akka runtime/API/UI path works at the stated scope. Static-only checks are insufficient for runtime features. |
| Model/provider behavior | Model-backed workstream behavior invokes a concrete Akka `Agent` through governed runtime assembly and configured provider boundary; missing provider config fails closed with actionable surfaces/traces. |
| Internal worker behavior | Durable tedious/background AI work uses `AutonomousAgent` task semantics when lifecycle, dependency, progress, cancellation, notification, or result-review needs justify it. |
| Deterministic services | Mechanical behavior remains deterministic and testable; AI does not own authorization, idempotency, tenant filtering, or policy enforcement. |
| Visual quality | Dashboards and surfaces meet the visual UX quality standard in `visual-ux-quality-standard.md`. |

## Core workstream baseline

### My Account

SMB full-core My Account is the signed-in user’s trusted control and attention inbox. It is launched from the lower-left user tile/email, not duplicated in the top workstream rail.

Required scope:

- current account, profile, preferences, notification settings, and selected context;
- safe context switching across authorized tenant/customer memberships;
- personal attention dashboard aggregating visible workstream attention items;
- self-service profile/settings surfaces with validation and audit where relevant;
- traceable sign-out/session guidance and provider failure recovery messages;
- links to authorized workstreams or attention items through governed `open_workstream` and `open_attention_item` surface requests;
- denials for disabled users, missing memberships, unsupported context selection, and forbidden workstream access.

Out of SMB scope by default:

- enterprise self-service identity provider administration;
- complex device trust or endpoint-management controls.

### User Admin

SMB full-core User Admin lets a small business owner/operator manage user access without external spreadsheets or ad hoc support steps.

Required scope:

- user directory dashboard with status, role/capability summaries, invitation state, recent admin changes, and attention indicators;
- invite, resend, revoke/cancel, accept/expiry visibility, captured outbox/Resend delivery status, and actionable failures;
- membership and role/capability assignment within SMB-safe roles;
- disable/reactivate users with clear authority, impact preview, idempotency, and audit;
- lightweight access-review queue and review history;
- support-access visibility and explicit approval/denial surfaces where support access is in scope;
- request/response `UserAdminAgent` guidance and summaries backed by governed prompt/skill/reference/runtime records;
- internal worker opportunities such as access-review investigation, stale-invite cleanup suggestions, duplicate-account detection, or admin-risk summarization.

Out of SMB scope by default:

- enterprise SCIM/SSO lifecycle consoles;
- complex custom role-builder suites beyond a manageable SMB role/capability set.

### Agent Admin

SMB full-core Agent Admin lets authorized operators understand and safely manage AI behavior without editing static code or hidden prompts.

Required scope:

- agent catalog dashboard for the five core functional agents and any internal workers;
- active governed `AgentDefinition`, prompt, skill, reference, manifest, model, and `ToolPermissionBoundary` visibility;
- draft/proposal/review/activation lifecycle for prompt, skill, reference, manifest, and tool-boundary changes;
- diff, risk, simulation/replay, and approval surfaces for behavior changes;
- model/provider configuration readiness and fail-closed diagnostics without exposing secrets;
- trace links from behavior versions to prompt assembly, skill/reference loads, model calls, and work results;
- request/response `AgentAdminAgent` guidance through the governed Akka Agent runtime path;
- internal worker opportunities such as behavior-change drafting, prompt-risk review, replay/evaluation analysis, and stale-document review.

Out of SMB scope by default:

- marketplace prompt packs, delegated enterprise model procurement, or arbitrary tenant-managed Java class/tool binding.

### Audit/Trace

SMB full-core Audit/Trace gives operators a practical investigation workspace for who/what/when/why/how-authorized.

Required scope:

- searchable audit/trace dashboard for identity, authorization, admin changes, invitations, role changes, surface actions, model calls, tool calls, worker tasks, and policy decisions;
- timeline/detail surfaces with correlation ids, trace ids, actor, AuthContext, capability id, outcome, denial reason, evidence references, and redaction state;
- safe export/copy/link behavior where authorized;
- filters for time, actor, workstream, capability, tenant/customer context, event type, risk/severity, and trace id;
- request/response `AuditTraceAgent` summaries and investigation guidance grounded in scoped trace data;
- internal worker opportunities such as scheduled audit summaries, anomalous admin activity review, support-access investigation, and policy-drift summaries.

Out of SMB scope by default:

- SIEM integrations, legal hold, complex retention policy consoles, or forensic e-discovery suites.

### Governance/Policy

SMB full-core Governance/Policy lets operators define and approve bounded behavior rules, thresholds, and policy changes that shape people and agents.

Required scope:

- governance dashboard for active policies, thresholds, pending approvals, exceptions, recent decisions, and proposed changes;
- policy/rule/threshold version surfaces with owner, status, provenance, review notes, effective date, and trace links;
- approval/exception/decision-card surfaces with evidence, risk, confidence, impact, alternatives, and allowed actions;
- behavior-governing prompt/skill/tool-boundary changes routed through human review when authority expands;
- replay/simulation or test evidence for impactful changes where feasible;
- request/response `GovernancePolicyAgent` guidance via governed Akka Agent runtime;
- internal worker opportunities such as policy-change analysis, exception clustering, replay/evaluation batches, and approval-summary drafting.

Out of SMB scope by default:

- complex compliance frameworks, policy-as-code authoring suites, or enterprise governance office workflows unless later selected.

## Cross-workstream baseline

### Functional-agent rail and shell

- User Admin, Agent Admin, Audit/Trace, and Governance/Policy appear in the top workstream rail only when authorized.
- My Account is opened from the signed-in user tile/email at the bottom of the rail.
- Each visible workstream has stable `WorkstreamIconDescriptor` metadata: `workstreamId`, display name, semantic icon id, visual hint, accent token, tooltip, accessible label, and optional approved asset reference.
- Left-rail attention badges come from backend projections, not frontend-only state.
- Rail selection, surface actions, My Account panels, deep links, and prompt-entered requests use one shell request pipeline with authorization and safe `system_message` denials.

### Dashboard and surface baseline

Each workstream needs a dashboard or briefing surface that shows:

- current operational state;
- what needs attention;
- blocked, failed, overdue, risky, stale, or approval-needed items;
- human, workflow, request/response agent, and AutonomousAgent/internal worker participation;
- recent consequential activity and trace links;
- authorized next actions;
- empty and first-run states that teach the user what to do next.

Richer typed surfaces should replace `markdown_response` when the interaction is table/form/decision/audit/timeline/workflow/task-progress shaped. `markdown_response` remains valid for bounded explanations, summaries, and guidance.

### Agent and internal-worker selection rules

Use request/response Akka `Agent` for:

- user-facing workstream turns from the persistent composer;
- bounded help, explanation, summarization, recommendation, classification, or guided action planning;
- single-response agent interactions that can return a structured surface or system message.

Use Akka `AutonomousAgent` for internal/background work when the task needs one or more of:

- durable task lifecycle;
- progress snapshots or notifications;
- dependencies or handoff;
- cancellation, failure, retry, or rejection semantics;
- model-driven iteration beyond one request/response turn;
- human result review or approval.

Use deterministic services for:

- authorization and tenant/customer filtering;
- validation and idempotency;
- invitation/email/outbox mechanics;
- lifecycle state transitions and projections;
- trace normalization and redaction;
- policy threshold enforcement and approval gating;
- provider configuration validation and secret-boundary checks.

### Runtime validation standard

For runtime/API/UI features, child tasks must prefer validation through the real scaffolded local path:

1. scaffold or modify `templates/ai-first-saas-starter/` as the executable baseline;
2. keep root `frontend/` synchronized when relevant;
3. run targeted tests for backend, frontend, surface rendering, and secret boundary;
4. run `tools/validate-ai-first-saas-starter-fullstack.sh` when the feature changes generated starter behavior broadly;
5. run provider smoke when model/provider environment exists, and otherwise verify fail-closed provider diagnostics;
6. never mark normal model-backed workstream behavior complete via deterministic, direct-service, or fixture-only substitutes.

## Child-project planning implications

Child mini-projects created from this umbrella baseline should:

- name the workstream, surfaces, capability ids, request/response agent behavior, internal worker candidates, deterministic services, validation checks, and visual acceptance criteria up front;
- split implementation into vertical SMB slices rather than horizontal “all backend” or “all UI” passes when possible;
- keep enterprise-only enhancements explicitly out of scope or backlog-only;
- append skills-pack gaps as explicit tasks or separate mini-projects when implementation reveals missing guidance.
