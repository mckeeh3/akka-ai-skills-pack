# Description-First Application Doctrine

## Core thesis

The application is the combination of its authoritative app description and its maintained runnable implementation.

The app description defines intended behavior, constraints, interfaces, tests, and operational requirements. In this repository and downstream forks, the root source tree is the canonical runnable implementation baseline that realizes and validates that description.

Source code, tests, deployment assets, and other implementation artifacts must stay consistent with the description, but they are not disposable by default.

## Source of truth

The authoritative semantic source of truth is the internal application description maintained by the harness.

This description is:
- multi-layered
- internally interconnected
- optimized for harness/model use
- maintained through focused edits when requirements change

The runnable implementation is the operational source of truth for what currently works locally. When code and description diverge, reconcile them deliberately: update the description when semantics changed, and update implementation/tests when realization is stale or incomplete.

## Repository goal

The goal of this repository is to create sufficient skills for a harness to create, maintain, refine, validate, and realize application descriptions.

These skills are not primarily for ad hoc coding detached from the app description.
They are for helping the harness:
- interpret flexible user input
- maintain the internal app description
- determine what internal artifacts need to change
- decide when the description is sufficiently complete
- extend or repair the canonical runnable app when requested or appropriate

## Primary principles

### 1. Description defines intended semantics
The internal description defines intended app semantics.
If the description is complete, the maintained runnable app can be extended, repaired, or realized from it without invention.
If implementation work requires inventing semantics not present in the description, the description is incomplete for that scope.

For AI-first SaaS apps, the description must define the operating model as well as domain behavior: durable goals, delegated work, retained human authority, agent/team responsibilities, role-specific dashboards, human surface graphs, internal workstream agent graphs, workstream expertise, governed-tools, policies, approvals, exceptions, evidence, traces, learning loops, and outcomes.

### 2. Code is maintained realization, not disposable output
Source code is the maintained realization of the described system.
Do not replace the canonical root app, foundation files, selected Java package, queue history, or user-owned implementation work unless the user explicitly requests a destructive reset.
Localized extension and repair are the default for existing repositories; broad regeneration is an exceptional strategy that must be explicitly scoped and justified.

### 3. Tests are part of the description
Well-articulated tests are a core part of app definition.
They specify intended behavior, edge cases, invariants, forbidden behavior, and regression expectations.
Tests are not merely verification after implementation; they are part of the authoritative behavioral description.

### 4. Harness-optimized internal representation
Internal documents, schemas, syntax, structure, and linkages should be optimized strictly for harness/model reliability and efficiency.
Human readability is secondary.
There are no required human-oriented formats for internal documentation.

### 5. Input-driven focused editing
Humans and harnesses may edit source, tests, specs, and app-description artifacts as part of normal repository maintenance.
Those edits must be driven by explicit user input, selected tasks, or discovered implementation gaps, and they must preserve description/implementation consistency.
All changes to the app should be traceable to external inputs to the harness:
- PRDs
- specs
- feature requests
- issues
- bug reports
- revision requests
- natural-language instructions

The harness decides which internal description artifacts must change.

### 6. Multi-layer description system
The app description is not a single document.
It is a layered, interconnected collection of internal artifacts that together define:
- intended behavior
- constraints
- interfaces
- role-specific dashboard surfaces, human surface graph nodes/actions, and structured workstream surfaces
- internal workstream agent graph nodes, delegations, escalations, and result/proposal return paths
- governed capabilities and governed-tools, with qualified exposure as browser-tools, agent-tools, internal-tools, workflow-tools, timer-tools, consumer-tools, or MCP-tools
- tests
- operational requirements
- AI-first operating-model requirements when delegated work or agents are in scope
- generation rules
- change relationships

### 7. Behavioral determinism over implementation determinism
The goal is not necessarily identical code on every generation.
The goal is behaviorally equivalent output that satisfies the same description, tests, constraints, and operational policies.

### 8. Realization locality
A change in the description does not inherently require broad regeneration.
Affected implementation, test, spec, or UI areas should be updated selectively when possible.
This preserves mergeability, reviewability, queue history, and the canonical root app baseline.

## Production-readiness doctrine

Production-readiness must be described explicitly, not inferred implicitly from code quality.

Initial required production-readiness dimensions:
- auth/security
- observability

These are part of the app description itself and must be represented as first-class internal requirements, not post-hoc implementation concerns.

### Auth/security
The description must explicitly define relevant security behavior and constraints, including as applicable:
- authentication model
- authorization rules
- trust boundaries
- secret handling expectations
- sensitive data rules
- allowed and forbidden access paths
- failure behavior for unauthorized access

### Observability
The description must explicitly define operational visibility requirements, including as applicable:
- logs
- metrics
- traces
- audit events
- health signals
- alert-worthy conditions
- diagnosability expectations

## Interaction doctrine

User interaction is flexible.
The user is not required to speak in terms of internal skill names, artifact names, or internal workflow stages.
The harness and its skills are responsible for determining how to interpret and respond to the user input.

For most dialog, the user interaction is focused on the app description rather than on source code.

## Initial input modes

The system should initially support two primary forms of user intent.

### 1. Change only the app description
The user provides new information, corrections, revisions, requirements, constraints, or feedback, and the harness updates the internal application description without generating the app.

Typical examples:
- add a new capability
- revise a workflow
- tighten auth rules
- refine observability expectations
- correct an edge case
- clarify a testable behavior

### 2. Realize or run the app
The user explicitly asks the harness to realize, extend, repair, run, or validate the current app description in the runnable implementation.

Typical examples:
- generate the code
- run the app
- execute tests
- prepare artifacts for manual evaluation

Example user intent:
- "ok, now generate the code and run the app"

## Harness guidance behavior

The harness may also proactively guide the user.
If the harness determines that the app description has reached sufficient completeness, it may tell the user that the description appears ready for:
- focused implementation or generation
- test execution
- manual testing
- human evaluation

This guidance does not change source-of-truth rules.
It is simply a recommendation that the current description is mature enough to realize and evaluate.

## Change model

All app evolution occurs through input to the harness.

The harness is responsible for:
1. interpreting the requested change
2. reconciling it against the existing workstream graph instead of creating parallel functional-agent, dashboard, surface, capability, or governed-tool structures
3. updating the internal description system when semantics change
4. determining impact
5. updating affected implementation, tests, specs, or generated/derived outputs when requested or appropriate
6. validating consistency against the updated description and runnable path

A bug fix may require code, test, spec, or app-description edits. If the bug exposes incorrect intended semantics, correct the authoritative description; if it exposes stale realization, repair the maintained runnable implementation.

## Review model

Human review may be prompt/response, diff review, or focused file review.

Humans ask questions such as:
- what changed?
- what behavior changed?
- what internal requirements were updated?
- what outputs were regenerated?
- what risks or ambiguities remain?

The harness answers from its analysis of description changes, implementation diffs, executed validation, and any generated or derived outputs.

## Non-goals

This doctrine rejects the following assumptions:
- source code alone is the ultimate definition of the app
- generated implementation artifacts are disposable by default in existing core-app repositories
- app-description artifacts should drift behind implementation changes
- internal description artifacts should be optimized primarily for human reading
- full regeneration is required for conceptual correctness
- production-readiness can be deferred until after implementation
- agentic products can be fully described as CRUD screens plus a chatbot without durable goals, authority boundaries, policies, decisions, traces, and outcomes

## Standard of completeness

An application description is sufficiently complete when it is precise enough for the harness to reliably:
- update and maintain the description
- preserve role-specific dashboards, human surface graphs, internal workstream agent graphs, workstream expertise, and governed-tool mappings without generation-time invention
- realize, extend, or repair the app
- generate or update and run validating tests
- explain the app’s behavior
- apply requested changes
- update affected implementation and derived outputs
- preserve intended semantics across revisions

## Operating rule

When there is conflict between:
- preserving human habits built around source-code primacy, and
- preserving description primacy,

the system must prefer description primacy.
