# Intent Compiler Working Note

Status: superseded working document
Scope: historical seed note for consuming, digesting, capturing, reacting to, and realizing incremental user intent.

This note is superseded by the active canonical doc set:

- [Intent Compiler](intent-compiler.md)
- [Current Intent Model](current-intent-model.md)
- [Incremental Intent Processing](incremental-intent-processing.md)
- [Intent to Realization Flow](intent-to-realization-flow.md)
- [Intent Compiler Skill Contracts](intent-compiler-skill-contracts.md)

Keep this file only as temporary historical context until the legacy-doc archive task decides whether to replace it with a deprecation stub or move it into the archive.

## Core thesis

The skills pack is an **intent compiler**.

Its source language is incremental human intent: product ideas, requirements, corrections, refinements, bug reports, design feedback, implementation discoveries, and code-level change requests. Its outputs are:

1. **Current non-code intent artifacts**: app-description, requirements, specs, acceptance criteria, task briefs, pending questions, and other planning documents.
2. **Generated functional app code**: Akka backend components, frontend surfaces, tests, configuration, and runnable application behavior.

The skills pack should preserve traceability from human intent to generated runtime behavior while keeping the canonical intent documentation focused on the **current intended system**, not historical clutter.

Git history, commits, task completion records, and release notes carry historical change context. App-description/spec artifacts should optimize for clean current intent.

## Capturing Incremental Intent

**Capturing Incremental Intent (CII)** means accepting that intent is realized organically over time. The system starts with partial high-level objectives, then grows in extent and clarity as users refine requirements, discover constraints, inspect generated behavior, report defects, and request changes.

Incremental intent can arrive at any level:

- high-level app purpose and outcomes
- domain and capability refinements
- workstream behavior
- role/access/security constraints
- surface and interaction design
- agent authority and tool boundaries
- Akka component choices
- tests and acceptance criteria
- code fixes and implementation revisions

The compiler must normalize each increment into the current canonical intent model, then either update non-code intent artifacts, generate/revise code, or both.

## Current intent, not historical intent

The canonical app-description/spec model should describe what the system is intended to be **now**.

Example of incremental input:

```text
Add invitations.
Actually, invitations expire after 7 days.
Admins can resend unaccepted invitations.
Support can view invitation status but cannot create invitations.
```

Canonical current intent should consolidate this as:

```text
Tenant admins may create email invitations.
Invitations expire after 7 days.
Tenant admins may resend unaccepted invitations.
Support users may view invitation status but may not create invitations.
```

The incremental arrival path is important to git history and review, but it should not pollute the primary current-state intent model.

## Traceable intent flow

The desired trace chain is:

```text
App objective
  -> Domain
    -> Workstream
      -> Surface
      -> Agent
      -> Tool
      -> Akka component
        -> Test
          -> Runtime trace / audit / outcome
```

Reverse traceability should also be possible:

```text
Code/component/state/event/test
  <- Akka component or endpoint contract
  <- Tool/capability invocation
  <- Agent or surface usage
  <- Workstream purpose and access model
  <- Domain capability
  <- App objective
```

## Intent documentation structure

The intent documentation should be an **intent graph rendered as files**. Directory names identify artifact types. Files identify concrete artifact instances.

The primary ownership hierarchy is:

```text
App
  -> Domains
    -> Workstreams
      -> Surfaces
      -> Agents
      -> Tools
      -> Policies
      -> Traces
      -> Realization mappings
      -> Tests
```

Containment expresses primary intent ownership, not exclusive reuse. Reusable global artifacts should have one canonical definition and be explicitly bound by workstreams that use them.

## Proposed app-description layout

```text
app-description/
  app.md
  global/
    actors/
      <actor>.md
    roles/
      <role>.md
    policies/
      <policy>.md
    surfaces/
      <surface-pattern>.md
    agents/
      <agent>.md
    tools/
      <tool>.md
    traces/
      <trace-pattern>.md
  domains/
    <domain>/
      domain.md
      capabilities/
        <capability>.md
      data-state/
        <state-object>.md
      workstreams/
        <workstream>/
          workstream.md
          access.md
          behavior.md
          surfaces/
            <surface-instance-or-binding>.md
          agents/
            <agent-binding-or-local-agent>.md
          tools/
            <tool-binding-or-local-tool>.md
          policies/
            <policy-binding-or-local-policy>.md
          traces/
            <trace-binding-or-local-trace>.md
          tests/
            <test-expectation>.md
          realization/
            akka-components.md
            frontend-routes.md
            api-contracts.md
```

## Global definition plus workstream binding

Reusable artifacts should be defined once globally, then bound concretely inside each workstream.

### Global reusable definition

```text
app-description/global/tools/create-invitation.md
```

Defines the stable tool contract:

```text
Tool: createInvitation
Purpose: Create a tenant-scoped user invitation.
Inputs: tenantId, email, role, invitedByUserId
Authorization: tenant_admin
Effects: creates invitation, starts delivery workflow, emits admin audit
```

### Workstream-specific binding

```text
app-description/domains/admin/workstreams/user-onboarding/tools/create-invitation.md
```

Defines why and how the tool is used in that workstream:

```text
Uses: app-description/global/tools/create-invitation.md
Used by: Admin Assistant Agent, Create Invitation Surface
Workstream-specific rules:
- Agent may draft invitation payloads.
- Human approval is required before sending.
- High-privilege roles require a risk explanation.
Required traces:
- draft generated
- approval decision
- invitation command result
```

The global artifact answers: **what is this thing?**  
The workstream binding answers: **why and how is this thing used here?**

## Workstream as central operational unit

The workstream is the main place where intent becomes operationally meaningful. It binds together:

- purpose and desired outcomes
- authorized users/roles
- surfaces
- agents
- tools
- policies
- behavior rules
- traces and audit requirements
- Akka realization
- tests and acceptance expectations

Access should be modeled primarily as workstream access, then compiled into surface visibility, capability permission, tool permission, endpoint/component authorization, and audit requirements.

## Skills-pack realignment implication

Skills and supporting docs related to user intent input should be revised to use this structure. They should treat user input as incremental intent and guide the harness to:

1. classify the intent increment
2. normalize it into a structured delta
3. identify affected app/domain/workstream/global artifacts
4. update current canonical intent files without historical clutter
5. preserve traceability through explicit references and bindings
6. decide whether to update specs/tasks/code/tests
7. validate generated runtime behavior against current intent

Every skill should clearly state whether it captures intent, refines intent, realizes intent in code, validates intent, or repairs drift between intent and implementation.
