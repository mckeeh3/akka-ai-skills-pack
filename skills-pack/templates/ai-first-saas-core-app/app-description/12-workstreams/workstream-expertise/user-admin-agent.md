# User Admin Workstream Expert Bundle

- bundle-id: `user-admin-agent.expertise`
- owning functional agent: `user-admin-agent`
- workstream id: `user-admin`
- scope: SaaS Owner Organization/Tenant administration, Organization Admin bootstrap, invitations, users, memberships, roles/capabilities, access review, support-access visibility, and admin audit in the selected `AuthContext`
- primary surfaces: `user-admin-dashboard`, `saas-owner-organization-admin`, `user-admin-user-list`, `user-admin-user-account`, `decision-card`, `audit-trace-explorer`
- model binding: inherited governed default or explicit `ModelConfigRef`/`ModelPolicy`; no provider secrets in prompt, skill, reference, trace, or browser payloads

## Prompt intent

Help administrators understand scoped User Admin and SaaS Owner Organization Administration state, allowed actions, denials, risks, and evidence. Ask clarifying questions for ambiguous context, target Organization/Tenant, target user, role, or approval path. Refuse raw tokens, secrets, cross-tenant data, organization application-data access from SaaS Owner authority, unsupported bulk side effects, role escalation, last-admin loss, and disabled-user actions.

## Skill/reference families

- skills: Organization onboarding triage, access-review triage, admin risk scoring, invitation drafting, role recommendation, support-access review, admin audit summary
- references: Organization/Tenant lifecycle policy, tenant role catalog, invitation/onboarding policy, access-review policy, support-access procedure, last-admin protection, audit redaction guide

Full content loads only through authorized `readSkill(skillId)` / `readReferenceDoc(referenceId)` calls assigned in compact manifests.

## Capability/tool boundary

Read scoped dashboard/list/detail/audit evidence where authorized. SaaS Owner Organization/Tenant reads are limited to platform-safe metadata, billing-safe indicators, admin bootstrap state, and admin audit facts. Side-effecting Organization create/profile/status, admin bootstrap invitation, membership, role, support-access, disable/reactivate, and access-review operations default to human confirmation or approval/decision-card flows. For human chat requests such as “create org and invite an admin”, propose a detailed `human_chat_tool_plan` using only the selected User Admin workstream tool catalog, bind confirmation to that exact plan, then execute each governed-tool invocation as its own authorized, idempotent transaction with `requestedBy`/`confirmedBy`, result/partial-failure surfaces, and audit/work traces. Re-plan and re-confirm when the target organization, role, scope, or tool sequence changes. `ToolPermissionBoundary` denies unassigned loaders, cross-scope reads, organization application-data access from SaaS Owner authority, autonomous side effects, unconfirmed chat tool execution, and authority expansion from text.

## Tests

Cover assigned/unassigned skill and reference loads, tool-boundary denial, capability authorization, SaaS Owner Organization/Tenant create/list/profile/status/admin-bootstrap boundaries, tenant/customer isolation, last-admin protection, support-access rules, no raw invitation token exposure, decision-card routing, PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, and surface rendering.
