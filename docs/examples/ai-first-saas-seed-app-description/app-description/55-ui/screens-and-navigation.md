# Screens and Navigation

## App shell

- left sidebar navigation with grouped sections:
  - Work: Briefing / Mission Control, Goal Workbench, Goals
  - Decisions: Decision Queue, Exceptions
  - Governance: Policies, Approval Gates, Learning / Proposals
  - Audit: Trace Explorer, Outcome Review
  - Admin: Users, Invitations, Roles, Tenant Settings
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

### Audit Trace Explorer

- route: `/ui/audit/traces`
- user goal: explain who/what/when/why/how-authorized for actions, decisions, tools, data access, and outcomes
- AI-first surface type: audit/work trace
- primary action: search and inspect a trace
- required regions:
  - filters by goal, agent, decision, policy, tool, actor, tenant, and time
  - chronological trace entries
  - evidence/tool/data-access detail
  - authorization and policy invocation detail
  - outcome links

### Admin Users and Invitations

- route: `/ui/admin/users`
- user goal: manage tenant users, invitations, roles, and access state
- primary action: invite a user or update role assignment
- required regions:
  - user list
  - invitation status
  - role and permission summary
  - disable/reactivate controls when permitted

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
