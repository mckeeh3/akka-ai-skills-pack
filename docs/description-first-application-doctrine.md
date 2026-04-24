# Description-First Application Doctrine

## Core thesis

The application is not its source code.

The application is the harness-maintained internal description of intended behavior, constraints, interfaces, tests, and operational requirements.

Source code, tests, deployment assets, and other implementation artifacts are generated outputs derived from that description.

## Source of truth

The sole source of truth is the internal application description maintained by the harness.

This description is:
- multi-layered
- internally interconnected
- optimized strictly for harness/model use
- not intended for direct human authorship or maintenance

Generated code is not authoritative.
Generated tests are not authoritative unless incorporated into the internal description layer.
Human understanding is not authoritative unless expressed as new input to the harness.

## Repository goal

The goal of this repository is to create sufficient skills for a harness to create, maintain, refine, validate, and realize application descriptions.

These skills are not primarily for helping users hand-author source code.
They are for helping the harness:
- interpret flexible user input
- maintain the internal app description
- determine what internal artifacts need to change
- decide when the description is sufficiently complete
- generate application outputs when requested or appropriate

## Primary principles

### 1. Description is the app
The internal description fully defines the app.
If the description is complete, the app can be regenerated.
If the app cannot be regenerated from the description, the description is incomplete.

### 2. Code is a disposable projection
Source code is an output artifact, not the definition of the system.
Regenerating all code from the current description is always valid in principle.
Selective regeneration is an optimization, not a conceptual requirement.

### 3. Tests are part of the description
Well-articulated tests are a core part of app definition.
They specify intended behavior, edge cases, invariants, forbidden behavior, and regression expectations.
Tests are not merely verification after implementation; they are part of the authoritative behavioral description.

### 4. Harness-optimized internal representation
Internal documents, schemas, syntax, structure, and linkages should be optimized strictly for harness/model reliability and efficiency.
Human readability is secondary.
There are no required human-oriented formats for internal documentation.

### 5. Input, not manual editing
Humans do not edit generated code.
Humans do not edit internal application description artifacts.
All changes to the app enter the system as external inputs to the harness:
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
- tests
- operational requirements
- generation rules
- change relationships

### 7. Behavioral determinism over implementation determinism
The goal is not necessarily identical code on every generation.
The goal is behaviorally equivalent output that satisfies the same description, tests, constraints, and operational policies.

### 8. Regeneration locality
A change in the description does not inherently require full regeneration.
Affected outputs should be regenerated selectively when possible.
This is an optimization for speed, stability, and cost.
It does not change the underlying rule that the whole app remains regenerable from the description.

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

### 2. Generate the app
The user explicitly asks the harness to realize the current app description as generated outputs.

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
- code generation
- test execution
- manual testing
- human evaluation

This guidance does not change source-of-truth rules.
It is simply a recommendation that the current description is mature enough to realize and evaluate.

## Change model

All app evolution occurs through input to the harness.

The harness is responsible for:
1. interpreting the requested change
2. updating the internal description system
3. determining impact
4. regenerating affected outputs when requested or appropriate
5. validating consistency against the updated description

A bug fix is not fundamentally a code patch.
It is a correction to the authoritative description, plus regeneration of affected outputs.

## Review model

Human review is prompt/response, not file editing.

Humans ask questions such as:
- what changed?
- what behavior changed?
- what internal requirements were updated?
- what outputs were regenerated?
- what risks or ambiguities remain?

The harness answers from its internal analysis of description changes and regenerated outputs.

## Non-goals

This doctrine rejects the following assumptions:
- source code is the ultimate definition of the app
- humans should hand-maintain generated implementation artifacts
- humans should hand-maintain internal app-description artifacts
- internal description artifacts should be optimized primarily for human reading
- partial regeneration is required for conceptual correctness
- production-readiness can be deferred until after implementation

## Standard of completeness

An application description is sufficiently complete when it is precise enough for the harness to reliably:
- update and maintain the description
- generate the app
- generate and run validating tests
- explain the app’s behavior
- apply requested changes
- regenerate affected outputs
- preserve intended semantics across revisions

## Operating rule

When there is conflict between:
- preserving human habits built around source-code primacy, and
- preserving description primacy,

the system must prefer description primacy.
