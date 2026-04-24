---
name: app-description-auth-security
description: Update the authoritative auth/security layer of the app description by capturing authentication, authorization, trust boundaries, sensitive-data rules, and forbidden access behavior without generating code.
---

# App Description Auth/Security

Use this skill when the user introduces or revises security requirements in the application description.

This skill maintains the **auth/security layer** of the internal app description.
It does not generate code.
It defines the access-control and trust semantics that downstream generation must honor.

## Goal

Create or update auth/security-oriented app-description artifacts that:
- define who may call what
- define how identity is established
- define authorization boundaries and protected operations
- define sensitive-data handling expectations
- define allowed and forbidden access patterns
- define failure behavior for unauthorized and forbidden actions
- identify linked impacts on behavior, tests, observability, and generation readiness

## Required reading

Read these first if present:
- `../../AGENTS.md`
- `../README.md`
- `../../docs/description-first-application-doctrine.md`
- `../../docs/app-description-skills-plan-backlog.md`
- `../../docs/internal-app-description-architecture.md`
- `../../docs/app-description-maintenance-flow.md`
- `../app-description-intake-router/SKILL.md`
- `../app-description-behavior-specification/SKILL.md`
- `../app-description-test-specification/SKILL.md`

## Use this skill when

The input sounds like:
- "require login for..."
- "only admins may..."
- "tenants must not see each other's data"
- "this endpoint should be internal-only"
- "redact sensitive fields in responses or logs"
- "unauthorized access should return..."
- "store or expose this data only under these conditions"

Use it for:
- authentication model changes
- authorization rule changes
- trust-boundary clarification
- tenancy isolation requirements
- secret or credential handling expectations
- sensitive-data visibility rules
- allowed and forbidden access paths
- unauthorized and forbidden failure behavior

## Core operating rule

Treat security as part of the app's authoritative meaning, not as a later implementation hardening step.

This skill should define:
- who the actors are
- how trust is established
- what each actor may do
- what each actor must never do
- what evidence or behavior is required when access is denied

## What this skill must capture

For each requested change, identify and describe as applicable:
- caller identities or principal categories
- authentication mechanism or trust source
- authorization rules by capability or operation
- tenancy or ownership boundaries
- internal-only versus external access
- sensitive-data categories
- visibility, masking, redaction, or retention expectations
- secret-handling constraints
- forbidden access patterns
- unauthorized and forbidden response behavior
- dependencies on behavior, test, and observability layers

## Standard auth/security output shape

Use this response shape when updating or summarizing auth/security changes:

```md
# Auth/Security Specification Update

## Requested change
- ...

## Identity and trust model
- ...

## Authorization rules
- ...

## Trust boundaries and access surfaces
- ...

## Sensitive-data rules
- ...

## Forbidden access behavior
- ...

## Unauthorized / forbidden outcomes
- ...

## Open questions and assumptions
- ...

## Affected linked layers
- behavior:
- tests:
- observability:
```

## Security modeling rules

### 1. Define principals explicitly
Do not say "authorized users" if the actual roles, identities, or caller classes matter.
Be explicit about who is acting.

### 2. Separate authentication from authorization
Clarify both:
- how identity is established
- what that identity is allowed to do

### 3. Make boundaries explicit
If the system has tenant, account, team, environment, or internal-service boundaries, define them directly.

### 4. Define sensitive-data behavior, not just access
Capture not only who can access protected information, but also:
- what must be hidden
- what must be redacted
- what must never be logged or exposed

### 5. Make forbidden access concrete
Specify what must not be allowed, including cross-tenant reads, unauthorized mutation, internal endpoint exposure, or excessive visibility.

### 6. Define denial behavior
State what happens on authentication failure or authorization failure.
Do not leave it as an implicit framework detail if the app meaning depends on it.

### 7. Flag observability implications
Security requirements often imply audit, alerts, or diagnostic traces.
Call that out explicitly.

## Clarification policy

Ask only the smallest questions needed to make the security model materially safe and usable.

Good questions include:
- "Who are the distinct caller types for this action?"
- "Is this restriction based on role, tenant ownership, or both?"
- "Should unauthorized callers learn that the resource exists, or should the system hide that fact?"
- "Which fields are sensitive and must never appear in logs or responses?"
- "Is this surface internal-service-only, or also callable by end users?"

## Handoff to other skills

When the auth/security update is established, route onward as needed:
- to `app-description-behavior-specification` if the security change alters core user-visible behavior
- to `app-description-test-specification` to define authorization, denial, redaction, and isolation verification cases
- to `app-description-observability` if audit events, alerts, traces, or diagnosability expectations need explicit definition
- to `app-description-readiness-assessment` when the user is asking whether the description is mature enough to realize

## Anti-patterns

Avoid:
- reducing security to a vague "must be secure" statement
- merging authentication and authorization into one blurry rule
- forgetting tenant or ownership isolation semantics
- defining sensitive-data rules without mentioning logs, responses, or audit visibility
- assuming denial behavior is irrelevant because a framework will handle it somehow
- treating auth/security as optional polish after behavior is already fixed

## Final review checklist

Before finishing, verify:
- the caller or principal model is explicit
- authentication expectations are explicit where relevant
- authorization rules are explicit by action or surface
- trust boundaries are called out
- sensitive-data rules are included where relevant
- forbidden access behavior is explicit
- denial outcomes are explicit where relevant
- linked impacts on behavior, tests, and observability are called out
- no code-generation step was assumed

## Response style

When answering:
- state the security change first
- separate identity, authorization, and data-protection rules
- keep the rules declarative and precise
- separate confirmed rules from assumptions
- explicitly note what tests and observability implications follow from the change
