# Backlog 03: Intake and Description-First Realignment

## Goal

Make description-first input processing extract workstreams, attention, dashboards, surfaces, capabilities, and autonomous task candidates as first-class deltas.

## Suggested task breakdown

1. Update top-level `ai-first-saas` and `agent-workstream-apps` handoff language if needed.
2. Update `app-description-input-normalization` and `app-description-intake-router`.
3. Update app-description modeling/readiness/generation companion skills.

## Implementation notes

- Normalized input should distinguish confirmed vs inferred workstreams, attention categories, dashboard/surface candidates, capability candidates, and autonomous task candidates.
- Intake router should route dashboard/attention/surface changes before direct UI/component work.
- Readiness should detect missing dashboard/attention/surface/action/capability semantics for generated SaaS workstreams.

## Required checks

- `git diff --check`
- `rg -n "attention|dashboard|surface|AutonomousAgent|autonomous task|workstream" skills/app-description-* skills/app-descriptions skills/ai-first-saas skills/agent-workstream-apps`

## Acceptance criteria

- Description-first paths cannot silently reduce broad app input to CRUD, page, or component changes.
