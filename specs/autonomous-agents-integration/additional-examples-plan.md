# Additional Autonomous Agent Examples Plan

## Purpose

Define the next executable reference slices for Akka `AutonomousAgent` coverage after the first-pass single-task and delegation examples.

## Selection criteria

Prioritize examples that teach distinct Akka mechanics and generated-app governance concerns without building a full product runtime:

1. small, focused, executable reference slice;
2. proves the real Akka Autonomous Agent task path through endpoint or `ComponentClient`;
3. uses `TestModelProvider.AutonomousAgentTools` only in tests;
4. preserves request-based `Agent` as the default for user-facing workstream turns;
5. records governance requirements where the example touches approval, notification exposure, tenant scope, or tools.

## Planned slices

1. **TaskRule retry** — demonstrate result rejection and retry before typed completion.
2. **Task dependencies and external approval** — demonstrate task dependencies with an unassigned human/external input task gating a downstream autonomous task.
3. **Notification stream endpoint** — demonstrate task/agent notification exposure for progress UI while keeping snapshots/results as source of truth.
4. **Handoff triage** — demonstrate ownership transfer from triage to specialist for the same task type.
5. **Team/moderation coordination** — demonstrate `TeamLeadership` or `Moderation` with a small collaborative review scenario.
6. **Governed tool-boundary reference** — demonstrate a generated-app-style planning/test slice for `ToolPermissionBoundary`, tenant isolation, approval gates, and traces around Autonomous Agent tools.

## Verification

After the implementation tasks land, run a final review task to update `docs/agent-coverage-matrix.md`, verify the examples remain focused, and append further follow-up tasks only if new gaps appear.
