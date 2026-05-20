# AI-First App-Description Example Coverage

## Purpose

This note records AI-first app-description example coverage after Sprint 6 and distinguishes completed reference-description coverage from remaining executable implementation/test gaps.

It prevents existing examples from being silently reinterpreted as AI-first when their intent is simpler.

## Current examples

### `purchase-request-app-description/`

Status: **low-agentic / conventional approval-workflow reference**.

Keep this example focused on the existing purchase request workflow unless a later task intentionally evolves it. It demonstrates the baseline app-description layer structure for capabilities, behavior, tests, auth/security, observability, generation, traceability, and review.

Do **not** force-fit this example into a full AI-first SaaS operating model. It lacks durable delegated goals, bounded agent/team execution, governed policy learning, decision-card evidence, work traces, and outcome loops by design.

### `ai-first-saas-seed-app-description/`

Status: **preferred current generated-SaaS app-description reference**.

This example defines the generic AI-first SaaS seed/reference app before implementation. It is the canonical app-description starting point for secure multi-tenant SaaS foundations, functional/context-area agents, `12-workstreams/` application model, split `55-ui/` browser realization, frontend/backend integration patterns, and coherent Akka component coverage.

Use it when the task is about:
- bootstrapping a generated AI-first SaaS app;
- defining shared human-user access, tenant, membership, role, and permission foundations;
- mapping a reusable AI-first operating model to implementation phases;
- planning the runnable seed app that future skills and examples can reference.

### `ai-first-dca-app-description/`

Status: **completed non-runnable AI-first app-description reference / domain-rich vertical extension**.

Sprint 6 populated this worked example with product, operating-model, agent-team, policy, decision, workflow, UI, audit, outcome, traceability, and implementation-slice examples.

Source-repository provenance inputs:
- archived DCA architectural reconstruction concept note
- archived office-device lifecycle concept note
- archived AI-first SaaS UI patterns concept note

Installed-pack users do not need the source-only archive files; use the completed DCA reference tree and canonical docs as operative guidance.

Current tree shape, intentionally compact and not the preferred generated-SaaS seed structure:

```text
app-description/
  00-system/
  10-capabilities/
  15-operating-model/
    goals-and-objectives.md
    agent-roles-and-authority.md
    agent-team-design.md
    policies-and-approval-gates.md
    decisions-exceptions-and-evidence.md
  20-behavior/
  30-tests/
  40-auth-security/
  50-observability/
    audit-trace-and-outcomes.md
  55-ui/
    ui-surfaces.md
  60-generation/
    implementation-slices.md
  70-traceability/
    ai-first-coverage-map.md
  80-review/
```

Placement rule: `15-operating-model/` owns delegated work, retained authority, policies, decision semantics, trace obligations, and outcome-loop intent. `50-observability/audit-trace-and-outcomes.md` owns concrete audit/work/decision trace events, outcome metrics, privacy/access rules, and tests implied by observability. For new generated SaaS app descriptions, use the seed example's `12-workstreams/` plus split `55-ui/` structure; DCA's consolidated `55-ui/ui-surfaces.md` remains domain-rich content to migrate, not a structural template.

## Completed AI-first app-description coverage

The DCA reference now demonstrates:

- durable operational goals, constraints, success criteria, and outcome links;
- delegated agent/team responsibilities and explicit retained human authority;
- policy clauses, permissions, thresholds, approval gates, and governed policy changes;
- decision or exception cards with evidence, risk, confidence, impact, alternatives, and action controls;
- audit/work/decision traces for tools, data access, policies, approvals, and outcomes;
- supervision-oriented UI surfaces: goal-to-execution, command center, decision review, governance/learning, digest, and audit/trace;
- traceability links from operating-model artifacts to behavior, tests, security, observability, UI, and realization slices.

## Remaining gaps

Remaining gaps are executable reference implementation and test gaps, not app-description scaffold gaps. The new `ai-first-saas-seed-app-description/` tree is the planning/reference description for closing the runnable seed-app gap. Track broader executable gaps in `../ai-first-examples-and-tests-gap-list.md`.

## Non-goals for this note

- Do not move, archive, or delete inbox provenance files here.
- Do not retrofit the purchase-request example into a complete AI-first example without explicit product intent.
- Do not treat the DCA reference as runnable application code.
