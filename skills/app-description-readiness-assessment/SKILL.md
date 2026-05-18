---
name: app-description-readiness-assessment
description: Assess whether the current app description is sufficiently complete and unambiguous for reliable generation, testing, and manual evaluation, and recommend whether to continue description work or proceed to app realization.
---

# App Description Readiness Assessment

Use this skill when the harness needs to decide whether the current app description is complete enough to realize the app.

This skill does not primarily change the app description.
It evaluates the current description state and determines whether generation would be responsible, premature, or acceptable with assumptions.

## Goal

Assess whether the current application description is sufficiently complete to support reliable:
- code generation
- test generation
- app execution
- manual testing and evaluation

The result should tell the harness and the user whether to:
- continue description work
- proceed with assumptions called out explicitly
- proceed to app generation now

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/ai-first-saas-application-architecture.md`
- `../../docs/capability-first-backend-architecture.md` for capability contract completeness criteria
- `../core-saas-foundation/SKILL.md` for mandatory secure SaaS foundation readiness criteria
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../app-description-intake-router/SKILL.md`
- `../app-description-behavior-specification/SKILL.md`
- `../app-description-test-specification/SKILL.md`
- `../app-description-auth-security/SKILL.md`
- `../app-description-observability/SKILL.md`
- `../app-description-ui/SKILL.md`
- `../ai-first-saas/SKILL.md` when delegated work, agents, decisions, governance, supervision, audit, or outcomes are in scope
- `../../docs/web-ui-style-guide.md`

## Use this skill when

The input sounds like:
- "is the description ready?"
- "can we generate the app now?"
- "what is still missing before generation?"
- "assess whether this is sufficient for implementation"
- "ok, now generate the code and run the app"

Use it before generation when the harness must decide whether the current description is:
- incomplete
- sufficient with assumptions
- sufficiently complete

## Core operating rule

Readiness is about **semantic completeness**, not about whether code could be guessed.

A description is ready only when it is clear enough that generation is likely to preserve intended behavior without hiding major unresolved decisions.

This skill should resist premature generation when important capability contracts, behavior, test, security, observability, in-scope frontend/UI, mandatory secure SaaS foundation, or AI-first operating-model semantics are still undefined.

## Readiness dimensions

Assess the current description across these dimensions:

### 1. Core secure SaaS foundation completeness
For every generated SaaS app, this is blocking readiness. Check that the description explicitly defines:
- SaaS Owner, Tenant, Customer, Account, UserProfile, UserSettings, Membership, Role, Permission/Capability, Invitation, AuthContext, AdminAuditEvent, support-access, and subscription/billing boundary
- WorkOS/JWT or selected authentication seam plus local Akka authorization state
- `/api/me`, selected context, context switching, and browser-safe capability payloads
- backend authorization for every protected route, component command, view query, stream, agent tool, workflow action, consumer side effect, timer action, and generated UI action
- tenant/customer-scoped commands and queries with forbidden access, disabled-user, inactive-membership, role/scope denial, and tenant-isolation behavior
- foundation behavior artifacts for sign-in, account/profile/settings, tenant/customer admin, Membership/Role/Permission, complete invitation lifecycle, support-access, audit, and billing-boundary behavior
- full onboarding behavior: invite send, resend, revoke/cancel, expire, accept, delivery status, delivery attempts, failed-delivery visibility, email delivery/outbox or explicit local/dev/test captured adapter, idempotent repeated invite/acceptance, and production readiness blocking missing Resend configuration unless an explicitly selected alternate provider is configured
- operational admin management beyond invite/disable/activate: user list/search without caller-supplied user IDs, view user detail, edit allowed profile fields, assign/replace/remove roles, add/suspend/reactivate/remove memberships, disable/reactivate account, reset/relink identity subject under policy, support-access grant/revoke/expiry, and last-admin protection
- required admin read models: UserDirectoryView, MembershipView, InvitationView, AdminAuditView, and AccessReviewQueueView where applicable
- scoped capabilities for SaaS Owner Admin, Tenant Admin, Customer Admin, Auditor, and app-specific admins
- mandatory AI-assisted admin offload surfaces and behavior: AccessReviewAgent, AdminRiskAgent, InvitationDraftAgent, RoleRecommendationAgent, SupportAccessReviewAgent, AdminAuditSummaryAgent or one governed `UserAdminAgent` with equivalent skills, scoped tools/redaction, audit/work traces, and decision cards for risky admin actions such as admin role grants, support-access expansion, identity relink/reset, bulk operations, and last-admin risk
- governed runtime agent foundation artifacts: `AgentDefinition`, `PromptDocument`/`PromptVersion`, `SkillDocument`/`SkillVersion`, `AgentSkillManifest`, `ToolPermissionBoundary`, first-install/tenant-bootstrap default behavior seed import, deterministic prompt assembly, authorized `readSkill(skillId)`, `PromptAssemblyTrace`, `SkillLoadTrace`, and `AgentWorkTrace`
- behavior-editing agent semantics: an `AgentBehaviorEditorAgent` or equivalent editing agent creates proposed diffs, draft prompt/skill/manifest/tool-boundary versions, rationale, risk notes, review/approval routing, activation/rollback flow, audit, and denial of unauthorized authority expansion
- foundation test artifacts for tenant isolation, forbidden access, disabled users, role/scope denial, `/api/me`, audit, invite send/resend/revoke/expire/accept, delivery failure, user and membership list/search, membership lifecycle, last-admin protection, support-access, AI access review recommendations, admin-agent approval boundaries, governed prompt/skill/manifest/tool-boundary changes, disabled-agent denial, unassigned skill denial, `readSkill(skillId)` authorization, decision cards for risky admin actions, audit/search views, billing boundary, idempotency, and frontend secret-boundary checks
- observability/audit artifacts for identity, Membership/role, support-access, billing-boundary, policy, approval, data-access, access-review, `PromptAssemblyTrace`, `SkillLoadTrace`, `AgentWorkTrace`, editing agent proposal traces, and consequential AI/tool activity
- mandatory browser UI foundation surfaces: sign-in state, context selection, profile/settings, Users, Invitations, Roles/Memberships, Access Review, Support Access, Admin Audit, Tenant/Customer Settings, admin-agent recommendation queues, decision-card review for risky admin actions, agent catalog, agent detail, prompt governance, skill governance, skill manifests, tool permission management, edit-agent proposal review, trace search/detail, and capability-gated navigation

### 2. AI-first operating-model completeness
Check this for generated AI-first SaaS apps and for any existing `15-operating-model/`:
- durable goals, objectives, constraints, success criteria, and outcome links
- delegated work versus retained human authority
- agent/team responsibilities, tools, data access, thresholds, and escalation rules
- policies, clauses, guardrails, permissions, and approval gates
- decision/exception semantics with evidence, risk, confidence, impact, alternatives, and precedents
- audit/work/decision traces, policy invocations, tool/data-access events, feedback, replay/simulation, and outcome metrics

### 3. Capability contract completeness
Check whether `10-capabilities/` is sufficiently defined for every in-scope capability:
- stable id/name and capability class
- purpose, in-scope outcomes, and out-of-scope boundaries
- actors/callers and AuthContext with tenant/customer scope, role, permission, or named capability grants
- input/output schemas, validation, redaction, safe denial/error shape, idempotency, and correlation expectations
- data access boundaries, side effects, policy/approval/escalation rules, autonomy level, and audit/work-trace obligations
- selected exposure surfaces or explicit non-exposure
- links to operating-model, behavior, tests, auth/security, observability, UI, and traceability artifacts as applicable

### 4. Behavior completeness
Check whether the app meaning is sufficiently defined:
- governed capability contracts
- major flows and state changes
- invariants
- failure behavior
- forbidden behavior
- no-op or idempotency rules where relevant

### 5. Test completeness
Check whether important behavior is backed by explicit verification expectations:
- acceptance cases
- regression cases
- negative cases
- repeated-request behavior
- failure-path expectations

### 6. Auth/security completeness
Check whether required production security semantics are defined:
- identity and trust model
- authorization rules
- trust boundaries
- sensitive-data rules
- denial behavior
- mechanical enforcement of agent/tool permissions and human authority boundaries when AI-first behavior is in scope
- governed runtime agent security for disabled agents, active prompt and skill version selection, default behavior seed import validation/provenance/idempotency, `AgentSkillManifest` assignment, authorized `readSkill(skillId)`, `ToolPermissionBoundary` enforcement, editing agent proposal approval, and denial of unauthorized authority expansion

### 7. Observability completeness
Check whether required operational evidence is defined:
- logs and audit events
- metrics
- traces and correlation
- health signals
- alert-worthy conditions
- diagnosability expectations
- AI-first work traces, decision traces, policy invocations, tool/data-access records, and outcome links when applicable

### 8. Frontend/UI completeness
Check this for generated full-stack AI-first SaaS apps:
- user journeys and screens
- navigation
- forms/actions and validation behavior
- frontend API contracts
- loading, empty, error, submitting, success, and stale states
- realtime behavior when needed
- accessibility and responsive expectations
- selected web UI style guide, mode policy, core CSS tokens, component styling, and brand adaptation rules
- for AI-first apps, supervision, decision-card, governance, digest, goal-to-execution, and audit/trace surfaces instead of only CRUD navigation

### 9. Generation stability
Check whether remaining ambiguity would likely cause incorrect or unstable generated outputs.

## Allowed outcomes

Return exactly one of these states:

### `not-ready`
Use when important semantics are still too incomplete or ambiguous for responsible generation.

### `ready-with-assumptions`
Use when the description is mostly sufficient, but some limited assumptions remain.
These assumptions must be listed explicitly and judged acceptable for a useful realization step.

### `ready`
Use when the description is sufficiently complete for reliable generation and downstream evaluation.

## Standard readiness output shape

Use this response shape:

```md
# App Description Readiness Assessment

## Overall state
- not-ready | ready-with-assumptions | ready

## Core secure SaaS foundation completeness
- status:
- notes:

## AI-first operating-model completeness
- status:
- notes:

## Capability contract completeness
- status:
- notes:

## Behavior completeness
- status:
- notes:

## Test completeness
- status:
- notes:

## Auth/security completeness
- status:
- notes:

## Observability completeness
- status:
- notes:

## Frontend/UI completeness
- status:
- notes:

## Remaining assumptions or gaps
- ...

## Recommendation
- continue description work | generate app

## Suggested next skill or skill sequence
1. ...
2. ...
```

## Assessment rules

### 1. Prefer explicit gaps over silent optimism
If a critical area is underspecified, say so directly.
Do not mark the description ready just because generation is technically possible.

### 2. Weight missing production concerns appropriately
Missing secure SaaS foundation, capability contracts, auth/security, observability, operating-model, or AI-first UI details must block readiness when generation would otherwise invent Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, `/api/me`, backend authorization, audit, tenant isolation, actors/callers, AuthContext/scope, schemas, side effects, idempotency, exposure surfaces, authority, policies, approval gates, decision evidence, trace obligations, outcome metrics, or supervision surfaces.

### 3. Allow limited assumptions only when localized
`ready-with-assumptions` is valid only when the remaining assumptions are:
- few
- explicit
- low-risk
- unlikely to distort the app's core behavior

For browser UI generation, a missing style guide is a blocking UI readiness gap unless the user explicitly defers it with an accepted default recorded in `specs/pending-questions.md` and the affected app-description/spec style-guide artifact.

For AI-first/delegated operations, missing `15-operating-model/` semantics are blocking when generation would otherwise invent authority, policies, approval gates, decision evidence, trace obligations, outcome metrics, or supervision surfaces.

### 4. Consider manual evaluation intent
If the user mainly wants a rough generated app for early evaluation, readiness may tolerate more assumptions than a production-grade generation step, but those assumptions must still be surfaced.

### 5. Recommend generation proactively when justified
If the description is sufficiently mature, this skill may recommend moving on to generation even when the user has not yet explicitly asked.

## Handoff rules

Route onward as follows:
- if `not-ready`, route to the most relevant missing description skills:
  - `core-saas-foundation`, `app-description-bootstrap`, `app-description-auth-security`, and `app-description-test-specification` when Account/Profile/Settings, Tenant/Customer, Membership/Role/Permission, `/api/me`, backend authorization, audit, tenant isolation, disabled-user, forbidden-access, or foundation test semantics are missing
  - `ai-first-saas` or focused AI-first companion skills when operating-model semantics are missing
  - `app-description-behavior-specification`
  - `app-description-test-specification`
  - `app-description-auth-security`
  - `app-description-observability`
  - `app-description-ui`
- if `ready-with-assumptions`, route to:
  - explicit assumption confirmation if needed
  - then `app-generate-app` if generation is requested or accepted
- if `ready`, route to:
  - `app-generate-app` when realization is requested or accepted

## Example readiness questions

Ask only when necessary:
- "Is repeated submission expected to succeed idempotently or fail?"
- "Are there caller roles or tenant boundaries not yet described?"
- "What operational evidence is required when this flow fails or times out?"
- "Are the current acceptance and regression cases enough to define expected behavior?"

## Anti-patterns

Avoid:
- treating vague behavior as ready because the model can improvise
- ignoring production concerns because they are not code yet
- hiding important assumptions inside a `ready` result
- blocking generation over trivial missing details that do not materially affect correctness
- recommending generation without listing the basis for readiness

## Final review checklist

Before finishing, verify:
- the result uses one of the three allowed states
- core secure SaaS foundation completeness was assessed explicitly for generated SaaS apps
- missing foundation/security blocks generation or marks the description `not-ready`
- AI-first operating-model completeness was assessed explicitly for generated AI-first SaaS
- behavior completeness was assessed explicitly
- test completeness was assessed explicitly
- auth/security completeness was assessed explicitly
- observability completeness was assessed explicitly
- frontend/UI completeness was assessed explicitly as mandatory generated SaaS scope
- remaining assumptions or gaps are listed clearly
- the recommendation is explicit
- the next skill or sequence is named clearly

## Response style

When answering:
- state the readiness result first
- be direct about blocking gaps
- distinguish critical gaps from acceptable assumptions
- keep the recommendation actionable
- if generation is appropriate, say so plainly
