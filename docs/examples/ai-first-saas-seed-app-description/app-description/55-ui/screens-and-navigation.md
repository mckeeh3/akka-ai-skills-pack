# Screens and Navigation

## App shell

- left sidebar navigation with grouped sections:
  - Work: Briefing / Mission Control, Goal Workbench, Goals
  - Decisions: Decision Queue, Exceptions
  - Governance: Policies, Approval Gates, Learning / Proposals, Agent Catalog, Prompt Governance, Skill Governance, Skill Manifests, Tool Permissions, Edit-Agent Proposals
  - Audit: Trace Explorer, Outcome Review, Prompt Assembly Traces, Skill Load Traces, Agent Work Traces
  - Admin: Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings
- top or page header region:
  - page title and short operational subtitle
  - optional page-level action such as export, create goal, or manage policy
- bottom sidebar region:
  - notifications
  - current user/profile
  - collapse control
- navigation rules:
  - hide or disable inaccessible areas based on permissions
  - backend remains authoritative for access decisions
  - active route uses selected style-guide primary treatment

## Screens

### Briefing / Mission Control

- route: `/ui/briefing`
- user goal: understand current autonomous work, risks, exceptions, and required human action
- AI-first surface type: command center and async digest
- primary action: review the highest-priority exception or pending decision
- required regions:
  - AI command strip for summaries, risk explanation, and action intake
  - KPI summary band for active goals, pending decisions, exceptions, and outcome deltas
  - agent execution timeline
  - needs-your-attention queue
  - agent/team autonomy and trust summary
  - trust controls / policy guardrails summary
  - upcoming autonomous actions

### Goal Workbench

- route: `/ui/goals/new`
- user goal: turn intent into a durable goal, plan, agent assignment, constraints, and approval gates
- AI-first surface type: goal-to-execution workbench
- primary action: review and launch the proposed plan
- required regions:
  - goal/objective form
  - success criteria and constraints
  - proposed execution plan
  - agent/team assignment
  - tool and data permission summary
  - approval gates
  - simulation/review results before launch

### Goal Detail / Execution Trace Summary

- route: `/ui/goals/:goalId`
- user goal: monitor progress and inspect why work is proceeding or blocked
- primary action: resolve next blocker or inspect trace
- required regions:
  - goal status and success criteria
  - current plan step
  - agent activity and tool calls
  - decisions and approvals linked to the goal
  - policy invocations
  - trace and outcome links

### Decision Queue

- route: `/ui/decisions`
- user goal: process recommendations, approvals, deviations, and exceptions by priority
- AI-first surface type: decision/deviation review queue
- primary action: open or act on the highest-priority decision card
- required regions:
  - filters by priority, risk, policy trigger, agent, due time, and status
  - decision card list with recommendation, evidence summary, risk, confidence, and impact
  - queue counts and stale/realtime state

### Decision Card Detail

- route: `/ui/decisions/:decisionId`
- user goal: make an informed approval/rejection/counterproposal with evidence and traceability
- AI-first surface type: decision card / deviation review
- primary action: approve, reject, counter, defer, or escalate based on permission and policy
- required regions:
  - recommendation
  - evidence and reasoning summary
  - risk, confidence, impact, and alternatives
  - policy trigger and authority boundary
  - action controls
  - trace links and outcome feedback

### Governance Center / Policies

- route: `/ui/governance/policies`
- user goal: review and govern policy, approval gates, examples, and learned behavior changes
- AI-first surface type: policy/governance/learning center
- primary action: review a policy proposal or edit a policy guardrail
- required regions:
  - policy list and versions
  - approval thresholds and authority boundaries
  - proposed changes and simulations
  - replay results
  - commit/rollback history
  - audit links

### Agent Catalog / Agent Detail

- routes: `/ui/agents`, `/ui/agents/:agentId`
- user goal: inspect and govern active, disabled, draft, and archived `AgentDefinition` records
- required regions:
  - agent list/search with owner/steward, tenant/customer scope, authority level, active prompt, skill manifest, tool permission boundary, status, and recent trace summary
  - agent detail with lifecycle controls, disabled-agent denial state, effective capability grants, prompt/skill/manifest/tool-boundary references, and recent `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace` links

### Prompt and Skill Governance

- routes: `/ui/governance/prompts`, `/ui/governance/skills`
- user goal: review, propose, approve, activate, rollback, and audit governed `PromptDocument`/`PromptVersion` and `SkillDocument`/`SkillVersion` records
- required regions:
  - document/version lists, proposed diffs, rationale, risk/impact notes, review/approval status, activation/rollback controls, prompt assembly preview, and `readSkill(skillId)` test console
  - explicit warning that prompt/skill content is behavior guidance only and cannot grant tool/data/authorization authority

### Skill Manifests and Tool Permissions

- routes: `/ui/governance/skill-manifests`, `/ui/governance/tool-permissions`
- user goal: govern agent skill discovery, full skill loading, and allowed tools/data/side effects
- required regions:
  - `AgentSkillManifest` assignment UI, unassigned skill denial visibility, compact manifest metadata, `ToolPermissionBoundary` editor/review, scoped tool/data grants, approval-required authority expansion warnings, policy citations, and decision-card links

### Edit-Agent Proposals

- route: `/ui/governance/agent-edit-proposals`
- user goal: review behavior changes proposed by `AgentBehaviorEditorAgent`
- required regions:
  - proposed diff, rationale, risk/impact notes, affected AgentDefinition/PromptDocument/SkillDocument/AgentSkillManifest/ToolPermissionBoundary records, suggested tests/replays, review actions, denial reasons, activation status, rollback result, and trace/audit links

### Audit Trace Explorer

- route: `/ui/audit/traces`
- user goal: explain who/what/when/why/how-authorized for actions, decisions, tools, data access, and outcomes
- AI-first surface type: audit/work trace
- primary action: search and inspect a trace
- required regions:
  - filters by goal, agent, decision, policy, tool, actor, tenant, prompt version, skill version, manifest, tool boundary, and time
  - chronological trace entries including PromptAssemblyTrace, SkillLoadTrace, and AgentWorkTrace
  - evidence/tool/data-access detail
  - authorization and policy invocation detail
  - outcome links

### Admin Users

- route: `/ui/admin/users`
- user goal: discover, search, and manage tenant/customer users within authority boundary
- primary action: invite a user or open a user detail/access action
- required regions:
  - user list/search backed by UserDirectoryView, with filters for tenant/customer, email/name, account status, role, membership status, identity link state, and last activity
  - user detail drawer/page with account/profile, memberships, roles, identity link state, support-access grants, and audit links
  - allowed profile-field edit controls
  - disable/reactivate account controls when permitted
  - last-admin protection warnings

### Admin Invitations

- route: `/ui/admin/invitations`
- user goal: manage invite delivery and activation issues
- primary action: resend invite or revoke invite
- required regions:
  - InvitationView list/search with invitation status, delivery status, delivery attempts, expiry/due time, target email, inviter, tenant/customer scope, and resend/revoke eligibility
  - create invite form with role/scope validation
  - resend/revoke controls with confirmation and audit result
  - failed-delivery, stale-invite, and expiring-invite queues
  - InvitationDraftAgent suggestions for invite copy and role rationale without exposing raw tokens

### Admin Roles / Memberships

- route: `/ui/admin/memberships`
- user goal: manage roles, scopes, memberships, and access state safely
- primary action: assign/replace/remove roles or suspend/reactivate/remove membership
- required regions:
  - MembershipView filters by tenant/customer scope, account, role, membership status, support-access expiry, lifecycle review status, and last-admin risk
  - role and permission summary
  - membership lifecycle controls
  - last-admin protection and privilege-escalation feedback
  - RoleRecommendationAgent suggestions with least-privilege evidence and decision-card links for risky role changes

### Access Review

- route: `/ui/admin/access-review`
- user goal: review stale, risky, or expiring access
- primary action: resolve the highest-risk access review item
- required regions:
  - AccessReviewQueueView filters by tenant/customer scope, target user, role, membership status, invitation status, delivery status, risk, due/expiry time, review status, item type, and agent recommendation source
  - stale invites, dormant admins, risky role combinations, support-access nearing expiry, last-admin risk, and orphaned customer admin gaps
  - AccessReviewAgent and AdminRiskAgent recommendation/risk summary
  - decision-card links for high-risk actions

### Support Access

- route: `/ui/admin/support-access`
- user goal: grant, revoke, and review time-limited SaaS Owner support memberships
- primary action: approve or revoke a support-access grant
- required regions:
  - active and expiring support-access grants
  - reason, expiry, scope, actor, and audit summary
  - grant/revoke controls with policy and last-admin protections

### Admin Audit

- route: `/ui/admin/audit`
- user goal: answer who changed access, in what scope, when, why, and under which policy
- primary action: search admin audit events
- required regions:
  - AdminAuditView filters for actor, target user, action type, tenant/customer, role, membership status, invitation status, delivery status, support-access grant, risk/policy metadata, and time range
  - redacted event details according to caller scope
  - links to affected user, invitation, membership, and decision cards
  - AdminAuditSummaryAgent summaries for selected result sets with audit trace links

### Profile / Preferences

- route: `/ui/profile`
- user goal: manage local preferences such as display name, notification preferences, and light/dark/system mode
- primary action: save preferences
- required regions:
  - user details
  - notification preferences
  - mode selection: light, dark, or system

## Narrow-screen navigation behavior

- sidebar collapses to menu/drawer
- decision queues and pending human actions appear before lower-priority charts
- KPI bands stack as cards
- detail pages preserve primary action visibility near the top
- tables use card-list fallback where practical
