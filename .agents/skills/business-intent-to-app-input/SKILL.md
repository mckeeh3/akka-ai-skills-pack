---
name: business-intent-to-app-input
description: Convert interview notes, transcripts, emails, rough business descriptions, or messy SMB stakeholder discussion into a clean Stage 1 app-description input artifact that preserves explicit input, agent inferences, confirmed intent, rejected ideas, future candidates, and open questions without app implementation decomposition.
---

# Business Intent to App Input

Use this skill when the agent already has business interview material, notes, transcripts, emails, meeting summaries, rough PRD fragments, or stakeholder discussion and needs to author a clean Stage 1 input artifact for later ingestion.

This skill shapes business input. It does not compile accepted input into app-description current intent, specs, backlogs, or code.

## Required Reading

Read:

- target project `AGENTS.md`, when present
- `../docs/business-intent-interview-process.md`
- `../docs/skills-pack-user-guide.md` when path/install boundaries are unclear

Load `business-intent-interview` only if additional live questions are needed. Load Stage 2 skills only after the user asks to ingest the accepted artifact into app-description or implementation planning.

## Goal

Create a durable Markdown input artifact under target project `docs/input/**` that later skills can ingest safely.

Recommended path:

```text
docs/input/business-interviews/<yyyy-mm-dd>-<topic>.md
```

## Transformation Rules

- Preserve stakeholder language and concrete examples.
- Separate explicit input from agent inference.
- Extrapolate likely CRM, ERP/accounting, scheduling, billing, inventory/assets, customer service, compliance, reporting, approval, exception, and operations needs when supported by the source material.
- Mark each extrapolation as confirmed, inferred, candidate future need, rejected/out-of-scope, or open question.
- Normalize repeated or messy statements into clear business intent without erasing uncertainty.
- Avoid workstream, Akka, app-description-node, governed-tool, endpoint, schema, table, route, or component decomposition.
- Do not treat this artifact as accepted app design.

## Output Sections

Use this structure unless the target project has a stronger local convention:

```text
# Business Intent Input: <topic>

## Source Context
## Input Status
## Explicit Input
## Agent-Inferred Business Model
## Confirmed Intent
## Current Process
## Pain Points
## Desired Future State
## Actors and Responsibilities
## Events, Triggers, and Timing
## Decisions, Rules, and Exceptions
## Systems, Documents, and Data
## Candidate CRM / ERP / Operations Needs
## Examples and Scenarios
## Success Measures
## Rejected or Out of Scope
## Open Questions
## Agent Summary for Ingestion
## Confirmation Notes
```

If there was no live confirmation, set `Input Status` to `draft-unconfirmed` and keep inferred content out of `Confirmed Intent`.

## Confirmation Policy

When the user is present and the artifact contains meaningful inferences, summarize the inferred model and ask for confirmation, correction, rejection, or priority changes before marking it accepted.

When writing from supplied notes without live confirmation, save the artifact as draft input and make open questions explicit.

## Handoff

After the artifact is accepted or intentionally saved as draft, route next to:

- `app-description-input-normalization` for Stage 2 current-intent delta normalization;
- `app-description-intake-router` to find affected app-description nodes;
- `ai-first-saas`, `agent-workstream-apps`, and focused app-description skills for requirements-to-feature transformation.

Stage 2 may ingest confirmed intent directly. It should treat unconfirmed inference and candidate future needs as hypotheses or pending questions.

## Guardrails

- Do not ask SMB stakeholders to supply technical architecture.
- Do not silently promote inferred content to confirmed intent.
- Do not remove rejected or out-of-scope ideas; they prevent future confusion.
- Do not store target application input under `.agents/skills` or `skills-pack/**`.
- Do not claim runtime readiness, implementation readiness, or app-description readiness from this Stage 1 artifact alone.
