# Internal App Description Architecture

## Purpose

This document defines the default internal artifact architecture for a **description-first application system**.

Its purpose is to give the harness and app-description skills a concrete target structure to maintain.

The structure is optimized for harness/model use, not for direct human authorship.
Humans interact through prompt/response.
The harness maintains these internal artifacts.

## Core rule

The application description is the source of truth.

Everything in this architecture exists to maintain that truth in a layered, interconnected, regenerable form.
Generated code, tests, and runnable assets are downstream projections.

## Default root

Use a dedicated root such as:

```text
app-description/
```

The exact root name may vary by project, but the harness should prefer one stable root rather than scattering description artifacts arbitrarily.

Reference example:
- `docs/examples/purchase-request-app-description/app-description/`

## Architecture goals

The internal artifact system should be:
- layered
- explicit
- cross-linked
- small enough for focused updates
- stable under repeated harness maintenance
- suitable for localized regeneration
- suitable for readiness assessment
- suitable for prompt/response review summaries

## Default directory layout

Prefer this baseline structure:

```text
app-description/
  00-system/
    app-manifest.md
    readiness-status.md
    generation-policy.md

  10-capabilities/
    capabilities-index.md
    01-<capability>.md
    02-<capability>.md

  20-behavior/
    behavior-index.md
    state-models/
      01-<stateful-area>.md
    flows/
      01-<flow>.md
    rules/
      01-<rule-family>.md

  30-tests/
    test-index.md
    acceptance/
      01-<capability>-acceptance.md
    regression/
      01-<regression-area>.md
    negative/
      01-<negative-area>.md
    operational/
      01-<operational-verification>.md

  40-auth-security/
    identity-and-trust.md
    authorization-rules.md
    data-protection.md
    boundary-and-surface-rules.md

  50-observability/
    logs-and-audit.md
    metrics.md
    traces-and-correlation.md
    health-and-alerts.md

  60-generation/
    realization-scope.md
    regeneration-map.md
    output-surfaces.md

  70-traceability/
    capability-to-behavior-map.md
    behavior-to-tests-map.md
    change-impact-map.md

  80-review/
    latest-change-summary.md
    latest-readiness-summary.md
```

This is the default starting architecture, not a rigid final law.
The harness may refine names or split files further as the app grows, but should preserve the same layer responsibilities.

## Layer responsibilities

## `00-system/`
System-level control artifacts.

### `app-manifest.md`
Defines the high-level identity and scope of the described app.
Should include:
- app name or working identity
- current status or maturity
- top-level goals
- non-goals
- major architectural assumptions
- primary generation targets

### `readiness-status.md`
Stores the current readiness posture.
Should include:
- current state: `not-ready`, `ready-with-assumptions`, or `ready`
- decisive reasons
- blocking gaps
- accepted assumptions
- last readiness update basis

### `generation-policy.md`
Defines realization policy.
Should include:
- when generation is allowed
- default full vs localized regeneration preference
- acceptable assumption policy
- required validation after generation

## `10-capabilities/`
Business capability inventory.

This layer answers:
- what business or user-visible capabilities exist?
- what is in scope?
- what is explicitly out of scope?

Each capability file should be narrowly focused and should link to the corresponding behavior, tests, security, and observability artifacts.

## `20-behavior/`
Behavioral semantics.

This layer answers:
- what must happen?
- what must never happen?
- what state exists?
- what transitions are valid or invalid?
- what no-op or idempotent behavior is required?

Subareas:
- `state-models/` for durable state concepts and lifecycle semantics
- `flows/` for multi-step or temporal behavior
- `rules/` for invariant-heavy or policy-like behavior groupings

## `30-tests/`
Authoritative verification layer.

This layer is part of the app description, not a downstream afterthought.
It answers:
- what must be provable?
- what acceptance cases define success?
- what regressions must remain fixed?
- what negative or idempotency cases constrain behavior?
- what operational verification is required?

Subareas:
- `acceptance/`
- `regression/`
- `negative/`
- `operational/`

## `40-auth-security/`
Auth/security semantics.

This layer answers:
- how identity is established
- what authorization rules exist
- what trust boundaries exist
- what access is forbidden
- what data is sensitive
- what masking, redaction, or restricted visibility applies

## `50-observability/`
Operational evidence requirements.

This layer answers:
- what must be logged
- what must be audited
- what metrics matter
- what trace continuity is required
- what health signals exist
- what should alert operators
- what evidence is needed to diagnose failures

## `60-generation/`
Realization policy and output mapping.

This layer answers:
- what outputs may be generated from the description
- what output surfaces exist
- how regeneration locality is tracked
- what parts of the realized app are affected by which description layers

### `realization-scope.md`
Defines the current intended realization boundary.
Examples:
- code only
- code + tests
- code + tests + run configuration
- code + tests + deploy assets

### `regeneration-map.md`
Maps description areas to generated output areas for localized regeneration decisions.

### `output-surfaces.md`
Names the output families that can be realized from the description.

## `70-traceability/`
Relationship mapping.

This layer exists to support change impact analysis and localized regeneration.
It should answer:
- which capabilities depend on which behavior artifacts?
- which behavior artifacts require which tests?
- which security and observability rules attach to which capabilities or flows?
- what outputs are likely affected by a given description change?

## `80-review/`
Derived review summaries.

This layer is optional and non-authoritative.
It can be maintained when the harness benefits from persisting its latest summaries, but the truth still lives in the earlier layers.

Use it for:
- latest change summary
- latest readiness summary
- optional human-review snapshots

Do not treat this layer as the source of app meaning.

## Skill-to-layer ownership

Default ownership should be:

- `app-description-bootstrap`
  - creates the initial `00-system/`, `10-capabilities/`, `20-behavior/`, `30-tests/`, `40-auth-security/`, and `50-observability/` seed artifacts
  - establishes the first stable app-description root

- `app-description-input-normalization`
  - produces a structured non-authoritative change envelope for downstream skills
  - may be persisted only as a temporary working artifact when useful

- `app-description-intake-router`
  - identifies candidate impacted layers
  - does not usually own long-lived authoritative artifacts

- `app-description-capability-modeling`
  - primarily owns `10-capabilities/`
  - maintains capability boundaries and links to downstream layers

- `app-description-behavior-specification`
  - primarily owns `20-behavior/`

- `app-description-test-specification`
  - primarily owns `30-tests/`

- `app-description-change-impact`
  - primarily owns `70-traceability/change-impact-map.md`
  - may recommend updates across all authoritative layers and `60-generation/regeneration-map.md`

- `app-description-auth-security`
  - primarily owns `40-auth-security/`

- `app-description-observability`
  - primarily owns `50-observability/`

- `app-description-readiness-assessment`
  - primarily owns `00-system/readiness-status.md`
  - may update `60-generation/realization-scope.md` references when readiness posture changes

- `app-generate-app`
  - reads all authoritative layers
  - may update `60-generation/` and derived generation status notes

- `app-description-change-summary`
  - derives from all impacted authoritative layers
  - may optionally update `80-review/latest-change-summary.md`

- `app-description-readiness-summary`
  - derives from readiness assessment outputs
  - may optionally update `80-review/latest-readiness-summary.md`

## Cross-layer invariants

The harness should maintain these invariants:

1. Every in-scope capability must link to at least one behavior artifact.
2. Every important behavior change must link to one or more test artifacts.
3. Security-sensitive behavior must link to relevant auth/security artifacts.
4. Operationally important behavior must link to relevant observability artifacts.
5. Readiness must be based on the actual state of behavior, tests, security, and observability layers.
6. Generation policy must never override description correctness.
7. Review summaries must be derivable from authoritative layers.

## File sizing rules

Prefer files that are:
- small enough for one focused harness update
- large enough to preserve a coherent semantic topic
- stable under repeated revisions

Good file boundaries:
- one capability
- one flow
- one stateful area
- one rule family
- one security concern area
- one observability concern area
- one verification family

Avoid files that collapse the entire app into one giant narrative.

## Naming rules

Prefer:
- stable numeric prefixes when ordering matters
- explicit topic names
- one topic per file where possible
- directory names that reveal the layer role immediately

Examples:
- `10-capabilities/02-order-submission.md`
- `20-behavior/flows/03-approval-escalation.md`
- `30-tests/regression/02-duplicate-submission.md`
- `40-auth-security/authorization-rules.md`

## Artifact update rules

When a change request arrives, the harness should:
1. identify impacted capabilities
2. update behavior semantics first
3. update linked test semantics
4. update linked auth/security semantics if needed
5. update linked observability semantics if needed
6. update traceability links
7. reassess readiness
8. generate outputs only if requested or accepted

## What is authoritative vs derived

### Authoritative
These layers define the app:
- `10-capabilities/`
- `20-behavior/`
- `30-tests/`
- `40-auth-security/`
- `50-observability/`
- relevant control state in `00-system/`

### Derived
These layers are useful but not authoritative:
- `60-generation/` generation status notes
- `70-traceability/` relationship maps
- `80-review/` review summaries

Derived artifacts must be reproducible from authoritative artifacts plus harness analysis.

## Minimum viable internal architecture

For a very small project, the harness may start with only:

```text
app-description/
  00-system/
    app-manifest.md
    readiness-status.md
  10-capabilities/
    capabilities-index.md
  20-behavior/
    behavior-index.md
  30-tests/
    test-index.md
  40-auth-security/
    identity-and-trust.md
  50-observability/
    logs-and-audit.md
```

Then expand into the full structure as complexity grows.

## Operating rule

If there is tension between:
- keeping the internal description minimal but explicit, and
- pushing meaning back down into generated code,

prefer maintaining the internal description.