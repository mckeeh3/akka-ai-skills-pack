# AI-First DCA App Description Example

## Purpose

This directory is a worked AI-first app-description reference example for the `akka-ai-skills-pack`.
It describes an AI-first DCA and office-device lifecycle platform for small office-device dealers as a **domain-rich vertical extension** of the canonical secure AI-first SaaS seed example.

Use `../ai-first-saas-seed-app-description/` as the canonical baseline for secure SaaS foundation shape, app-description structure, functional-agent/workstream split, `55-ui/` file set, and reusable generated-app starting points. Use this DCA example when an agent needs a richer vertical reference for lifecycle automation, telemetry-driven delegated work, policies, approvals, decision cards, traces, and outcome loops.

This is a **reference asset for the skills pack**, not the business app of this repository.
In a downstream project, an installed skills pack would maintain that project's real `app-description/` tree in the target project workspace.

## Source material mapping

This example intentionally promotes selected ideas from temporary migration source material into the current app-description shape.
Installed-pack users do not need those source-only provenance files; use the seed example and canonical docs below as the baseline guidance, then use this DCA example as a vertical/domain extension reference.

| Source | How this example uses it |
|---|---|
| Archived office-device lifecycle concept note | Primary product/lifecycle source: customer, device, DCA collector states; agent team; workflows; UI surfaces; policy examples; decision-card fields. |
| Archived DCA architectural reconstruction concept note | Architectural framing source: DCA-first slice, event-sourced substrate, policy network, agent ensemble, supervision surfaces, supplies-fulfillment first slice. |
| `docs/ai-first-saas-application-architecture.md` | Canonical doctrine: goals, bounded agents, policies, approvals, traces, outcomes, and Akka substrate mapping. |
| `docs/internal-app-description-architecture.md` | Canonical layer structure and ownership rules for this tree. |

## Example boundary

The example models an AI-first operations platform that starts with device telemetry and DCA workflows, then expands toward unified customer-device-contract-service-billing lifecycle management.

Initial emphasis:
- lifecycle orchestration for customers, devices, and DCA collectors;
- delegated agent work for telemetry, supplies, service, billing, contracts, customer success, inventory, onboarding, and offboarding;
- retained human authority for high-risk, high-cost, policy-bound, ambiguous, or customer-sensitive decisions;
- governed policies, approval gates, decision cards, audit traces, outcome loops, and supervision UI surfaces;
- supplies fulfillment as the first implementation slice.

Non-goals for this reference example:
- no runnable Akka application code;
- no claim that this repository itself is an office-device DCA product;
- no exhaustive replacement for the source PRD that may exist outside this repository;
- no implicit autonomous authority where policy, risk, or human approval is unspecified.

## Current structure

The current DCA reference includes product vision, operating model, agent-team, governance, decision-card, workflow, compact consolidated UI, audit/trace/outcome, traceability, and implementation-slice reference files. It remains a non-runnable vertical reference app-description asset, not the canonical seed template, not the canonical generated-SaaS file structure, and not application code.

Important UI structure note: this DCA example intentionally keeps functional-agent rail, structured-surface catalog, capability/API links, route/deep-link notes, state/realtime behavior, accessibility, and style routing together in `app-description/55-ui/ui-surfaces.md` as a compact DCA-specific contract. That consolidated file is not the preferred structure for new generated SaaS app descriptions. New or regenerated app-description trees should start from the seed example's `12-workstreams/` application-model layer plus split `55-ui/` browser-realization files; use the DCA file only as domain-rich content to migrate into that structure.

```text
app-description/
  00-system/
  10-capabilities/
  15-operating-model/
  20-behavior/
    state-models/
    flows/
    rules/
  30-tests/
    acceptance/
    regression/
    negative/
    operational/
  40-auth-security/
  50-observability/
  55-ui/
  60-generation/
  70-traceability/
  80-review/
```

## Current coverage

The reference now covers:
- durable goals, constraints, success criteria, delegated work, retained authority, and outcome loops;
- bounded coordinator/specialist/evaluator agents and approval/escalation boundaries;
- policy clauses, thresholds, governed policy changes, decision-card evidence, and exception handling;
- supervision-oriented UI surfaces, audit/work/decision traces, outcome metrics, and traceability maps;
- DCA-specific adaptation points for the secure SaaS foundation described by the seed example;
- non-runnable realization slices that show how future Akka/React implementation work can be decomposed.

Refresh work is aligning this vertical example with the current seed/foundation doctrine. Future executable reference slices and tests should start from the seed baseline plus this DCA vertical description rather than treating DCA as the structural template.
