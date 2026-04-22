# Step 7 - Reduce the perception that the repo is entity-centric

## Objective

Reduce legacy signals that make the repository look primarily centered on Event Sourced Entities and Key Value Entities, and strengthen its identity as a broader **Akka application architecture resource for AI agents**.

## Why this step matters

The repository has expanded well beyond entity modeling, but some docs and packaging signals still make it appear entity-first.

That weakens the new intent-driven story because a future agent may still infer:
- everything should start from entity selection
- other component families are secondary
- the repo is mainly for state-model decisions

## Primary files to consider

Likely targets:
- `README.md`
- `AGENT-README.md`
- `skills/README.md`
- packaging docs such as `pack/README.md`
- packaging metadata such as `pack/manifest.yaml`
- installer notes such as `install.sh` comments or help text if relevant

## Required changes

Implement the following:

1. Audit docs for entity-first framing.
2. Rebalance top-level lists so they present the full solution space more evenly.
3. Where appropriate, explain that entities are one part of application design, not the only core entry point.
4. Evaluate whether pack/install docs should mention that source coverage is broader than current bundles, or update packaging to match the broader story.
5. Make sure workflows, views, consumers, timed actions, endpoints, web UI, and agents are visible as peer building blocks.

## Desired outcome

A future agent should read the repo as a guide to designing and generating Akka applications, not just choosing an entity type.

## Deliverables

At minimum:
- docs no longer over-signal an entity-only or entity-first interpretation

Potential follow-on deliverable:
- packaging docs updated to reflect current source-library breadth or intentionally scoped bundle limits

## Out of scope

Do not fully redesign release packaging unless needed for a small consistency improvement.
If broader packaging changes are large, document them clearly and keep them scoped.

## Completion criteria

This step is done when:
- top-level docs present the repository as broader than entity selection
- component families appear as peer tools in an architecture system
- remaining entity-focused language is either intentional or clearly scoped

## Suggested implementation notes

Watch for legacy signals such as:
- entity-focused bundle descriptions presented as if they describe the full repo
- wording that implies all requirements should be reduced to entity decisions first
- top-level lists that underrepresent workflows, endpoints, agents, or UI
