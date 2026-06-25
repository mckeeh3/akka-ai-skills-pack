---
name: app-description-auth-security
description: Update authoritative auth/security nodes and workstream access bindings in the app-description current-intent graph by capturing authentication, authorization, trust boundaries, sensitive-data rules, and forbidden access behavior without generating code.
---

# App Description Auth/Security

Use this skill when the user introduces or revises security requirements in the application description.

This skill maintains reusable auth/security definitions under `app-description/global/**` and workstream-specific `access.md`, policy/tool bindings, capability authorization, and denial semantics under `domains/<domain>/workstreams/<workstream>/**`.
It does not generate code.
It defines the access-control and trust semantics that downstream generation must honor.

## Lifecycle classification

- Phase: interview.
- Kind: focused current-intent capture/editor.
- Family: app-description.
- Living-graph contract: auth/security nodes are part of the app-description current-intent graph and must name affected workers, execution harnesses, actor adapters, governed tools, capabilities, traces, tests, and realization implications when security changes touch them.
- Build/compile handoff: when security intent becomes implementation, planning, code, tests, or validation work, hand off through `../docs/app-description-to-code-compile-contract.md` instead of inventing runtime structure here.

## Goal

Create or update auth/security-oriented app-description artifacts that:
- define who may call what
- define how identity is established
- define authorization boundaries and protected operations
- define sensitive-data handling expectations
- define allowed and forbidden access patterns
- define AI-first authority boundaries for agents, tools, data access, approvals, and governed policy changes for generated AI-first SaaS
- define failure behavior for unauthorized and forbidden actions
- identify linked impacts on behavior, tests, observability, and generation readiness

## Required reading

Read these first if present:
- target project path: AGENTS.md
- `../README.md`
- `../docs/intent-compiler.md`
- `../docs/current-intent-model.md`
- `../docs/incremental-intent-processing.md`
- `../docs/intent-compiler-skill-contracts.md`
- `../docs/app-description-skill-output-contracts.md`
- `../docs/ai-first-saas-application-architecture.md`
- `../docs/capability-first-backend-architecture.md` for protected capability AuthContext, permission, scope, exposure, and audit semantics
- `../core-saas-foundation/SKILL.md` for mandatory secure SaaS foundation semantics
- `../app-description-intake-router/SKILL.md`
- `../app-description-behavior-specification/SKILL.md`
- `../app-description-test-specification/SKILL.md`
- `../docs/security-workos-auth-and-admin.md` when browser user authentication, WorkOS, JWT-secured APIs, or basic administration are in scope

## Use this skill when

The input sounds like:
- "require login for..."
- "use WorkOS for sign-in..."
- "secure frontend/backend calls with JWT..."
- "only admins may..."
- "tenants must not see each other's data"
- "this endpoint should be internal-only"
- "redact sensitive fields in responses or logs"
- "unauthorized access should return..."
- "store or expose this data only under these conditions"
- "the agent may use this tool/data but may not commit changes without approval"
- "policy changes must be simulated and human-approved before activation"

Use it for:
- authentication model changes for WorkOS/AuthKit
- frontend-to-backend JWT bearer-token requirements
- authorization rule changes
- trust-boundary clarification
- tenancy isolation requirements
- basic user administration rules, including initial admin bootstrap, invitations, role assignment, and disabling users
- secret or credential handling expectations
- agent, workflow, tool-use, data-access, and policy-commit permission boundaries
- sensitive-data visibility rules
- allowed and forbidden access paths
- unauthorized and forbidden failure behavior

## Core operating rule

Treat security as part of the app's authoritative meaning, not as a later implementation hardening step.

For every generated SaaS app, start from the `core-saas-foundation` baseline: Account/Profile/Settings, customer-facing Organization backed by Tenant isolation, Customer, Membership/Role/Permission, Invitation, AuthContext, `/api/me`, backend authorization, AdminAuditEvent, support-access, billing boundary, organization/customer product scoping, and tenant/customer internal isolation. WorkOS/AuthKit is the supported browser authentication provider and Resend (resend.com) is the supported production email service for invite/account emails and future app email features. WorkOS/Resend runtime settings may remain open questions during description maintenance, but generated SaaS apps must not choose different auth or production email services unless the skills pack is extended with provider-specific guidance. Local authorization, tenancy, audit, and denial semantics are mandatory.

This skill should define:
- who the actors are
- how trust is established
- what each actor may do
- what each actor must never do
- what agents, workflows, and automated tools may do autonomously versus only recommend
- what evidence or behavior is required when access is denied

## What this skill must capture

For each requested change, identify and describe as applicable:
- secure SaaS foundation impact: Account, UserProfile, UserSettings, customer-facing Organization backed by Tenant isolation, Customer, Membership, Role, Permission/Capability, Invitation, AuthContext, AdminAuditEvent, support-access, billing boundary, `/api/me`, backend authorization, organization/customer product scoping, and tenant/customer internal isolation
- caller identities or principal categories
- authentication mechanism or trust source, using WorkOS/AuthKit for generated browser users
- frontend-to-backend token propagation expectations
- local user/account linking rules
- authorization rules by capability id/governed-tool id/operation/query and selected exposure surface or actor adapter, including surface action/browser-tool, confirmed human chat tool plan, AI agent-tool, API, workflow, timer, consumer, MCP, and internal callers
- tenancy or ownership boundaries
- admin roles and scopes when user administration is in scope
- bootstrap/invite/first-login behavior when relevant, including mandatory email delivery for production, explicit local/dev/test outbox capture, invitation status, expiry, resend, revoke/cancel, acceptance, delivery status, delivery attempts, idempotency, and audit trail
- internal-only versus external access
- agent/tool/data/policy permission grants, workstream tool-catalog membership, autonomy thresholds, explicit human chat confirmation requirements, and approval gates
- policy, prompt, skill, guardrail, and evaluator update authority
- sensitive-data categories
- visibility, masking, redaction, or retention expectations
- secret-handling constraints
- forbidden access patterns
- unauthorized and forbidden response behavior
- explicit default-deny rule for every route, surface action/browser-tool, human chat tool-plan execution, agent tool, data access path, workflow action, view query, stream, consumer side effect, timer action, MCP/API exposure, internal-tool call, and generated UI action unless deliberately public static assets
- dependencies on behavior, test, and observability graph nodes

## Standard auth/security output shape

Use the delta modeling contract in `../docs/app-description-skill-output-contracts.md`. For this auth/security skill, report the requested change, affected graph nodes/file targets, reusable global definitions versus workstream access/policy/tool bindings, in-scope and out-of-scope behavior, authority/scope, DTOs or payloads where relevant, side effects/idempotency/denials/traces/tests, linked graph nodes, assumptions, and next handoff. Avoid repeating the full app-description graph model.

## Security modeling rules

Apply the concise rules in `../docs/app-description-skill-output-contracts.md` plus the focused skill's goal. Preserve mandatory secure SaaS foundation, generated-SaaS runtime completion, tenant/customer scoping, backend authorization, governed agent/tool boundaries, traces, and tests when those concerns are in scope. Ask only blocking questions; otherwise record assumptions and hand off to the next focused skill.

## Clarification policy

Ask only the smallest questions needed to make the security model materially safe and usable.

Good questions include:
- "Who are the distinct caller types for this action?"
- "Is this restriction based on role, tenant ownership, or both?"
- "Should unauthorized callers learn that the resource exists, or should the system hide that fact?"
- "Which fields are sensitive and must never appear in logs or responses?"
- "Is this surface internal-service-only, or also callable by end users?"
- "Which actions may the agent commit autonomously, and which must pause for human approval?"
- "Who may approve, simulate, activate, or roll back policy, prompt, guardrail, evaluator, or threshold changes?"

## Handoff to other skills

When the auth/security update is established, route onward as needed:
- to `app-description-capability-modeling` if the security change creates or changes protected capability ids, callers, AuthContext, permissions, named capability grants, exposure surfaces, side effects, approvals, or data-access boundaries
- to `app-description-behavior-specification` if the security change alters core user-visible behavior
- to `app-description-test-specification` to define authorization, denial, redaction, and isolation verification cases
- to `app-description-ui` if route/action visibility, capability-gated navigation, decision cards, supervision queues, or trace access needs UI description updates
- to `app-description-observability` if audit events, alerts, work traces, decision traces, policy invocations, tool/data access traces, or diagnosability expectations need explicit definition
- to AI-first companion skills when authority, governance, decision, audit, or outcome semantics need focused modeling
- to `app-description-readiness-assessment` when the user is asking whether the description is mature enough to realize
- to `akka-workos-user-auth` when realization should implement WorkOS/AuthKit, JWT-secured APIs, or `/api/me`
- to `akka-basic-user-admin` when realization should implement roles, invites, admin bootstrap, or user-management APIs

## Anti-patterns

Avoid:
- reducing security to a vague "must be secure" statement
- merging authentication and authorization into one blurry rule
- forgetting tenant or ownership isolation semantics
- leaving Account/Profile/Settings/Membership/Tenant/Customer/admin/audit foundation behavior undefined
- treating a route, agent tool, data access path, workflow action, view query, stream, or generated UI action as unauthenticated or unauthorized by default
- defining sensitive-data rules without mentioning logs, responses, or audit visibility
- assuming denial behavior is irrelevant because a framework will handle it somehow
- using prompt instructions, model plans, or UI/chat labels as the only control for surface actions, human chat tool-plan execution, agent tool use, data access, policy commits, or high-impact actions
- delaying auth/security until after behavior is already fixed

## Final review checklist

Before finishing, verify:
- the caller or principal model is explicit
- authentication expectations are explicit where relevant
- secure SaaS foundation objects and `/api/me` semantics are explicit for generated SaaS apps
- authorization rules are explicit by capability/governed-tool and by adapter/action/surface, including confirmation and approval semantics for chat-mediated or agent-mediated execution
- trust boundaries are called out
- sensitive-data rules are included where relevant
- agent/tool/data/policy authority boundaries are explicit for generated AI-first SaaS semantics
- forbidden access behavior is explicit
- denial outcomes are explicit where relevant
- linked impacts on capability contracts, behavior, tests, UI, observability, and readiness are called out
- no code-generation step was assumed

## Response style

When answering:
- state the security change first
- separate identity, authorization, and data-protection rules
- keep the rules declarative and precise
- separate confirmed rules from assumptions
- explicitly note what tests and observability implications follow from the change
