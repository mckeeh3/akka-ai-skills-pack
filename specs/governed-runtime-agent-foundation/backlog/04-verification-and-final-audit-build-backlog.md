# Backlog 04: Verification and final audit

## Purpose

Prevent regressions and verify consistency after the governed runtime agent foundation migration.

## Delivery goal

Automated checks and a final audit confirm that the getting-started prompt now implies the fully backed managed-agent core.

## Capability contracts

- `verify.agent-foundation.guardrails`: fail when mandatory governed-agent references disappear from foundation/routing/planning guidance.
- `audit.agent-foundation.consistency`: repair contradictions and record final state.

## Suggested harness task breakdown

1. Add verification guardrails for governed runtime agent foundation references.
2. Run final audit and repair contradictions.

## Done criteria

- Verification passes locally.
- No known contradictions remain in README, doctrine, routing, app-description, planning, packaging, or verification guidance.
