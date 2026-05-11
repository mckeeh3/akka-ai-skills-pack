# Agent-First DCA App Description Example

## Purpose

This directory is a worked AI-first app-description reference example for the `akka-ai-skills-pack`.
It describes an agent-first DCA and office-device lifecycle platform for small office-device dealers.

This is a **reference asset for the skills pack**, not the business app of this repository.
In a downstream project, an installed skills pack would maintain that project's real `app-description/` tree in the target project workspace.

## Source material mapping

This example intentionally promotes selected ideas from temporary inbox source material into the canonical app-description shape.
The inbox files remain provenance until the migration cleanup task disposes of them.

| Source | How this example uses it |
|---|---|
| `skills/inbox/docs/oai-agent-first-dca-office-device-lifecycle.md` | Primary product/lifecycle source: customer, device, DCA collector states; agent team; workflows; UI surfaces; policy examples; decision-card fields. |
| `skills/inbox/docs/cai-dca-agentic-reconstruction.md` | Architectural framing source: DCA-first slice, event-sourced substrate, policy network, agent ensemble, supervision surfaces, supplies-fulfillment first slice. |
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

The initial tree is a scaffold. Later Sprint 6 tasks fill the authoritative content.

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

## Planned fill order

1. `TASK-06-002`: product vision and operating model foundation.
2. `TASK-06-003`: agent team, policies, decision cards, approvals, and workflows.
3. `TASK-06-004`: UI surfaces, audit/trace/outcomes, and implementation-slice examples.
4. `TASK-06-005`: inbox disposition after promoted material is represented in canonical docs/examples.
