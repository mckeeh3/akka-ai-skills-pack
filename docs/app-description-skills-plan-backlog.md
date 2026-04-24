# App Description Skills Plan and Backlog

## Purpose

This document defines the initial skill plan for turning this repository into a harness-oriented system for creating and maintaining **application descriptions as the source of truth**.

It translates `docs/description-first-application-doctrine.md` into a concrete skill roadmap.

Supporting architecture docs:
- `docs/internal-app-description-architecture.md`
- `docs/app-description-maintenance-flow.md`
- `docs/app-description-end-to-end-workflow-example.md`

The goal is not to help a user hand-author code.
The goal is to help the harness:
- interpret flexible user input
- maintain an internal multi-layer application description
- decide whether the user wants description-only change or app generation
- assess whether the description is sufficiently complete
- generate code, tests, and runnable outputs only when requested or justified

## Initial operating model

The harness should initially support two primary user intents:

1. **Change only the app description**
2. **Generate the app**

Most user interaction should be treated as description-oriented by default.
Generation should occur only when the user asks for it explicitly or when the harness recommends it as the next step.

## Design constraints

The skills in this plan should follow these rules:
- optimize strictly for harness/model use
- assume internal app-description artifacts are harness-maintained only
- avoid requiring humans to know skill names or internal workflow stages
- preserve description primacy over code primacy
- treat selective regeneration as an optimization
- represent tests as part of the app description
- make auth/security and observability first-class early concerns

## Target skill architecture

The first usable version should be organized into five skill groups.

### Group A. Intake and routing
Purpose: bootstrap the internal app-description system, classify user input, and select the next skill path.

Planned skills:
- `app-description-bootstrap`
- `app-description-input-normalization`
- `app-description-intake-router`

Responsibilities:
- create a stable initial app-description tree when none exists yet
- normalize flexible prompts into a stable internal change envelope
- identify whether the user is changing the description or requesting generation
- extract candidate capability, constraint, test, policy, and revision signals from flexible prompts
- route into focused description-maintenance skills
- keep the user interaction natural and skill-agnostic

### Group B. Description maintenance core
Purpose: update the internal app description without generating code.

Planned skills:
- `app-description-capability-modeling`
- `app-description-behavior-specification`
- `app-description-test-specification`
- `app-description-change-impact`

Responsibilities:
- represent business capabilities and scope
- define behavior, invariants, edge cases, and forbidden states
- express tests as source-of-truth artifacts
- update linked internal description layers based on user input
- identify which internal description artifacts are impacted by a change

### Group C. Production-readiness layers
Purpose: make initial production concerns explicit inside the app description.

Planned skills:
- `app-description-auth-security`
- `app-description-observability`

Responsibilities:
- capture authentication and authorization requirements
- capture trust boundaries and sensitive-data constraints
- capture logs, metrics, traces, audit events, and diagnosability requirements
- ensure these concerns become part of the description rather than post-hoc code additions

### Group D. Readiness and realization
Purpose: decide whether the description is complete enough to generate outputs, then realize them.

Planned skills:
- `app-description-readiness-assessment`
- `app-generate-app`
- `app-run-and-evaluate`

Responsibilities:
- assess whether the description is sufficient for reliable generation
- identify remaining ambiguities before generation
- generate code and tests from the current app description
- run the app, run tests, and surface results for human evaluation

### Group E. Review and response
Purpose: answer human review questions without exposing internal editing.

Planned skills:
- `app-description-change-summary`
- `app-description-readiness-summary`

Responsibilities:
- explain what changed after a user revision request
- summarize changed behavior, policies, tests, and generation impact
- explain why the harness considers the description ready or not ready for generation

## Recommended initial skill flow

The harness should prefer this default flow:

1. bootstrap the internal app-description tree if no usable root exists yet
2. normalize the user input into candidate description deltas
3. classify and route the normalized input
4. update the internal description layers
5. run change-impact analysis across linked layers and realization surfaces
6. assess readiness
7. either:
   - stop after description update, or
   - generate the app if explicitly requested or recommended and accepted
8. answer human review questions in prompt/response form

## MVP skill set

The smallest useful first milestone should include these skills:
- `app-descriptions`
- `app-description-bootstrap`
- `app-description-input-normalization`
- `app-description-intake-router`
- `app-description-capability-modeling`
- `app-description-behavior-specification`
- `app-description-test-specification`
- `app-description-change-impact`
- `app-description-auth-security`
- `app-description-observability`
- `app-description-readiness-assessment`
- `app-generate-app`
- `app-description-change-summary`

This set is enough to support:
- description-first dialog
- two initial input modes
- test-centered description evolution
- first-class auth/security and observability
- explicit generation handoff
- review by harness-generated summaries

## Skill definitions

## 0. `app-description-bootstrap`

### Use when
A new app needs its first internal app-description tree or the current description root is too incomplete to maintain safely.

### Must do
- create the minimum authoritative app-description layers
- establish the initial capability, behavior, test, auth/security, and observability seed artifacts
- create a readiness baseline and generation policy
- create enough cross-linking for later maintenance

### Output contract
Create a stable `app-description/` root or project-equivalent internal description root with a minimum viable authoritative structure.

## 0.5 `app-description-input-normalization`

### Use when
The user input is broad, mixed, ambiguous, or likely to affect more than one description layer and the harness needs a stable normalized delta envelope before routing.

### Must do
- normalize flexible user input into a structured change envelope
- separate primary and secondary intents
- separate confirmed vs inferred deltas
- separate behavior, test, security, observability, review, and realization signals

### Output contract
Produce a normalized app-description input envelope that downstream routing and maintenance skills can consume consistently.

## 1. `app-description-intake-router`

### Use when
The user provides any new prompt, revision request, issue, PRD fragment, feature request, or direct generation request.

### Must determine
- is this description-only or generate-app intent?
- does the request introduce new capability, changed behavior, constraint, test, security concern, or observability concern?
- which focused maintenance skill should run next?

### Output contract
Produce a structured routing result with:
- normalized user intent
- candidate description deltas
- required focused skills
- whether generation is requested now
- whether clarification is required before continuing

## 2. `app-description-capability-modeling`

### Use when
The harness needs to define or revise business capabilities, scope boundaries, actors, or intended outcomes in `10-capabilities/`.

### Must capture
- capability names and goals
- actors
- in-scope outcomes
- out-of-scope outcomes
- links to behavior, tests, security, and observability

### Output contract
Update the authoritative capability layer so downstream behavior and verification work have a clear business boundary.

## 3. `app-description-behavior-specification`

### Use when
The user is defining or revising functional behavior.

### Must capture
- capabilities
- actors
- commands/actions
- state transitions
- invariants
- forbidden behavior
- edge-case expectations

### Output contract
Update or create internal behavior-oriented description artifacts that a downstream generator can treat as authoritative.

## 4. `app-description-test-specification`

### Use when
Behavior needs to be made unambiguous through tests or examples.

### Must capture
- acceptance behaviors
- regression cases
- invariants as tests
- negative cases
- no-op and idempotency behavior
- failure-path expectations

### Output contract
Update the internal test description layer as part of the authoritative app definition.

## 5. `app-description-change-impact`

### Use when
A change has been requested or applied and the harness needs to know which other layers, traceability artifacts, readiness state, and generated outputs are affected.

### Must capture
- impacted authoritative layers
- impacted derived layers
- readiness impact
- likely affected output surfaces
- localized vs broad regeneration recommendation

### Output contract
Produce a change-impact result that makes the next required updates and regeneration implications explicit.

## 6. `app-description-auth-security`

### Use when
The request introduces or changes security concerns.

### Must capture
- authentication model
- authorization rules
- trust boundaries
- sensitive-data handling
- allowed and forbidden access patterns
- unauthorized and forbidden failure behavior

### Output contract
Update internal security-related description artifacts and mark impacted generation surfaces.

## 7. `app-description-observability`

### Use when
The request introduces or changes operational visibility expectations.

### Must capture
- logs
- metrics
- traces
- audit events
- health indicators
- alert-worthy conditions
- diagnosability expectations

### Output contract
Update internal observability-related description artifacts and mark impacted generation surfaces.

## 8. `app-description-readiness-assessment`

### Use when
The harness needs to decide whether the app description is complete enough for realization.

### Must assess
- ambiguity remaining in functional behavior
- ambiguity remaining in tests
- missing auth/security details
- missing observability details
- whether generation would be stable enough to be useful

### Output contract
Return one of:
- `not-ready`
- `ready-with-assumptions`
- `ready`

with explicit reasons and next steps.

## 9. `app-generate-app`

### Use when
The user explicitly asks to generate the app, or accepts a harness recommendation to generate.

### Must do
- consume the current internal app description
- generate code and tests as projections
- preserve description primacy
- identify whether full or localized regeneration is appropriate

### Output contract
Summarize:
- what was generated
- what was regenerated
- what was executed
- what passed or failed
- what remains uncertain or needs human evaluation

## 10. `app-description-change-summary`

### Use when
The human asks what changed after a revision request.

### Must summarize
- changed capabilities
- changed behavior
- changed tests
- changed auth/security rules
- changed observability requirements
- generation impact

### Output contract
Produce a prompt/response review summary without requiring file editing.

## Backlog

## Slice 01 — Intake routing and interaction model

### Goal
Give the harness a consistent front door for natural user input and the two initial intent modes, including bootstrapping a new internal app-description tree.

### Skills
- `app-description-bootstrap`
- `app-description-input-normalization`
- `app-description-intake-router`

### Deliverables
- bootstrap skill spec
- routing skill spec
- normalized intent shape
- example prompt patterns for description-only vs generation intent
- clarification rules for ambiguous prompts

### Done criteria
- a new app-description tree can be created from an early app idea or PRD fragment
- flexible user prompts can be classified without requiring internal skill names
- the router can distinguish default description work from explicit generation requests
- the router can identify when to ask a targeted clarification question

### Suggested harness task breakdown
1. define bootstrap output shape and minimum seed artifacts
2. write the bootstrap skill
3. define normalized input envelope and intent categories
4. write the input-normalization skill
5. define routing outputs
6. write the intake-router skill
7. add example prompt patterns and anti-patterns
8. add tests/examples for ambiguous user inputs

## Slice 02 — Core description maintenance

### Goal
Enable the harness to update authoritative app-description layers for behavior and tests.

### Skills
- `app-description-capability-modeling`
- `app-description-behavior-specification`
- `app-description-test-specification`
- `app-description-change-impact`

### Deliverables
- capability-modeling skill spec
- behavior-specification skill spec
- test-specification skill spec
- change-impact rules for linked internal artifacts
- examples of capability additions, behavior revisions, and bug-fix updates

### Done criteria
- a user feature request can be translated into behavior-level description changes
- a bug fix can be represented as a description correction plus updated tests
- the harness can identify which internal description layers are impacted by a change

### Suggested harness task breakdown
1. define capability-layer artifact responsibilities
2. define behavior-layer artifact responsibilities
3. define test-layer artifact responsibilities
4. write capability-modeling skill
5. write behavior-specification skill
6. write test-specification skill
7. write change-impact skill
8. add examples for capability change, feature change, bug fix, and constraint refinement

## Slice 03 — Production-readiness foundations

### Goal
Make auth/security and observability first-class description concerns.

### Skills
- `app-description-auth-security`
- `app-description-observability`

### Deliverables
- security skill spec
- observability skill spec
- minimum required question sets for missing production details
- examples showing how security or observability changes alter generation readiness

### Done criteria
- auth/security requirements can be represented without dropping into code-level details
- observability requirements can be represented as description-layer expectations
- readiness checks can identify when these concerns are underspecified

### Suggested harness task breakdown
1. define security description dimensions
2. write auth-security skill
3. define observability description dimensions
4. write observability skill
5. add example revisions for both concern areas

## Slice 04 — Readiness assessment and generation handoff

### Goal
Let the harness determine when description work is sufficient and hand off cleanly to app generation.

### Skills
- `app-description-readiness-assessment`
- `app-generate-app`
- `app-run-and-evaluate`

### Deliverables
- readiness scoring or state model
- generation handoff contract
- generation summary format
- run/test/evaluation summary format

### Done criteria
- the harness can explain why a description is or is not ready
- the user can explicitly request generation after description work
- the harness can recommend generation when the description appears sufficiently complete
- generated outputs are summarized as projections from the current description

### Suggested harness task breakdown
1. define readiness states and required evidence
2. write readiness-assessment skill
3. define generation handoff contract
4. write generate-app skill
5. define run-and-evaluate response format

## Slice 05 — Human review and change summaries

### Goal
Support prompt/response review instead of file editing.

### Skills
- `app-description-change-summary`
- `app-description-readiness-summary`

### Deliverables
- summary templates for behavior changes
- summary templates for production-readiness changes
- summary templates for generation impact and readiness

### Done criteria
- the harness can answer “what changed?” reliably
- the harness can summarize changes across multiple internal description layers
- the harness can explain generation impact in plain response form

### Suggested harness task breakdown
1. define summary sections for behavior and policy changes
2. write change-summary skill
3. write readiness-summary skill
4. add example review prompts and expected responses

## Recommended implementation order

1. Slice 01 — Intake routing and interaction model
2. Slice 02 — Core description maintenance
3. Slice 03 — Production-readiness foundations
4. Slice 04 — Readiness assessment and generation handoff
5. Slice 05 — Human review and change summaries

## Suggested first repository additions

When implementation begins, prefer creating these first:
- `skills/app-descriptions/SKILL.md`
- `skills/app-description-bootstrap/SKILL.md`
- `skills/app-description-input-normalization/SKILL.md`
- `skills/app-description-intake-router/SKILL.md`
- `skills/app-description-capability-modeling/SKILL.md`
- `skills/app-description-behavior-specification/SKILL.md`
- `skills/app-description-test-specification/SKILL.md`
- `skills/app-description-change-impact/SKILL.md`
- `skills/app-description-auth-security/SKILL.md`
- `skills/app-description-observability/SKILL.md`
- `skills/app-description-readiness-assessment/SKILL.md`
- `skills/app-generate-app/SKILL.md`
- `skills/app-description-change-summary/SKILL.md`
- `docs/internal-app-description-architecture.md`
- `docs/app-description-maintenance-flow.md`
- `docs/examples/purchase-request-app-description/README.md`
- `docs/app-description-skills-plan-backlog.md`

## Anti-patterns

Avoid:
- treating code generation as the default response to every prompt
- collapsing behavior, tests, security, and observability into one vague skill
- forcing users to speak in internal taxonomy
- letting manual code edits or manual internal-doc edits become part of the operating model
- treating production-readiness as a later implementation detail
- making readiness assessment implicit instead of explicit

## Success criteria for this skill initiative

This initiative succeeds when the repository can support a harness that:
- treats the app description as the source of truth
- defaults to maintaining the description unless generation is requested
- can evolve the description from flexible user dialog
- can explain what changed after revisions
- can determine when the description is ready for realization
- can generate and run the app as a downstream projection of the maintained description
