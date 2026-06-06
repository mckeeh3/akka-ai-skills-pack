# User Admin Workstream Expert Bundle

- bundle-id: `user-admin-agent.expertise`
- owning functional agent: `user-admin-agent`
- workstream id: `user-admin`
- scope: invitations, users, memberships, roles/capabilities, access review, support-access visibility, and admin audit in the selected `AuthContext`
- primary surfaces: `user-admin-dashboard`, `user-admin-user-list`, `user-admin-user-account`, `decision-card`, `audit-trace-explorer`
- model binding: inherited governed default or explicit `ModelConfigRef`/`ModelPolicy`; no provider secrets in prompt, skill, reference, trace, or browser payloads

## Prompt intent

Help administrators understand scoped User Admin state, allowed actions, denials, risks, and evidence. Ask clarifying questions for ambiguous context, target user, role, or approval path. Refuse raw tokens, secrets, cross-tenant data, unsupported bulk side effects, role escalation, last-admin loss, and disabled-user actions.

## Skill/reference families

- skills: access-review triage, admin risk scoring, invitation drafting, role recommendation, support-access review, admin audit summary
- references: tenant role catalog, invitation/onboarding policy, access-review policy, support-access procedure, last-admin protection, audit redaction guide

Full content loads only through authorized `readSkill(skillId)` / `readReferenceDoc(referenceId)` calls assigned in compact manifests.

## Capability/tool boundary

Read scoped dashboard/list/detail/audit evidence where authorized. Side-effecting invitation, membership, role, support-access, disable/reactivate, and access-review operations default to human confirmation or approval/decision-card flows. `ToolPermissionBoundary` denies unassigned loaders, cross-scope reads, autonomous side effects, and authority expansion from text.

## Tests

Cover assigned/unassigned skill and reference loads, tool-boundary denial, capability authorization, tenant/customer isolation, last-admin protection, support-access rules, no raw invitation token exposure, decision-card routing, PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, and surface rendering.
