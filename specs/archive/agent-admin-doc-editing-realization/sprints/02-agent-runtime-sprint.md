# Sprint 02: Editing agent and runtime document loading

## Goal

Integrate the AI-assisted editing agent flow and runtime document loading/read tracing.

## Tasks

- `AADE-02-001`: editing-agent draft/revise/save/cancel flow.
- `AADE-02-002`: runtime prompt + skill descriptor loading, `readSkill`, `readReferenceDoc`, and read traces.

## Acceptance

- Editing agent uses a concrete runtime path with fail-closed behavior when model/provider configuration is unavailable.
- Test provider paths can deterministically validate proposal behavior without becoming normal-runtime fake success.
- Runtime agent requests use current prompt plus skill names/descriptions.
- Runtime reads are constrained to self-listed skill/reference docs and traced with Agent Admin-visible metadata.
