# AI-First App-Description Example Gaps

## Purpose

This note records the current example coverage gap after the app-description AI-first refactor.
It prevents existing examples from being silently reinterpreted as AI-first when their intent is simpler, and it gives Sprint 6 a concrete placeholder target for a worked AI-first example.

## Current examples

### `purchase-request-app-description/`

Status: **low-agentic / conventional approval-workflow reference**.

Keep this example focused on the existing purchase request workflow unless a later task intentionally evolves it.
It demonstrates the baseline app-description layer structure for capabilities, behavior, tests, auth/security, observability, generation, traceability, and review.

Do **not** force-fit this example into a full AI-first SaaS operating model. It currently lacks durable delegated goals, bounded agent/team execution, governed policy learning, decision-card evidence, work traces, and outcome loops by design.

## AI-first reference example scaffold

Sprint 6 adds a separate worked example rather than overloading the purchase-request example:

```text
docs/examples/agent-first-dca-app-description/
```

Current status: Sprint 6 populated the DCA scaffold with product, operating-model, agent-team, policy, decision, workflow, UI, audit, outcome, and implementation-slice examples.

Archived provenance inputs:
- `specs/ai-first-skills-pack-migration/archive/inbox/docs/cai-dca-agentic-reconstruction.md`
- `specs/ai-first-skills-pack-migration/archive/inbox/docs/oai-agent-first-dca-office-device-lifecycle.md`
- `specs/ai-first-skills-pack-migration/archive/inbox/docs/ai-first-saas-ui-patterns.md`

Expected minimum tree shape:

```text
app-description/
  00-system/
  10-capabilities/
  15-operating-model/
    goals-and-objectives.md
    agent-roles-and-authority.md
    policies-and-approval-gates.md
    decisions-exceptions-and-evidence.md
    audit-trace-and-outcomes.md
  20-behavior/
  30-tests/
  40-auth-security/
  50-observability/
  55-ui/
  60-generation/
  70-traceability/
  80-review/
```

## Required AI-first coverage for the future example

The DCA worked example should demonstrate, at minimum:

- durable operational goals, constraints, success criteria, and outcome links;
- delegated agent/team responsibilities and explicit retained human authority;
- policy clauses, permissions, thresholds, approval gates, and governed policy changes;
- decision or exception cards with evidence, risk, confidence, impact, alternatives, and action controls;
- audit/work/decision traces for tools, data access, policies, approvals, and outcomes;
- supervision-oriented UI surfaces: goal-to-execution, command center, decision review, governance/learning, digest, and audit/trace;
- traceability links from operating-model artifacts to behavior, tests, security, observability, UI, and realization slices.

## Non-goals for this note

- Do not move, archive, or delete inbox provenance files here.
- Do not retrofit the purchase-request example into a complete AI-first example without explicit product intent.
