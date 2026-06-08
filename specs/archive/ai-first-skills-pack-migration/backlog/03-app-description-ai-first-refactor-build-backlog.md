# Sprint 3 Build Backlog: App-Description AI-First Refactor

## Purpose

Make the description-first path preserve AI-first product meaning before implementation planning.

## Delivery goal

App-description trees should be able to act as the source of truth for agentic operating models.

## Suggested harness task breakdown

### 1. Refactor app-description architecture docs

- task ID: `TASK-03-001`
- output: updates to app-description architecture and doctrine docs.
- scope: describe new/updated sections for goals, agents, policies, decisions, traces, outcomes, and AI-first UI surfaces.
- dependencies: `TASK-02-001` done.

### 2. Refactor app-description entry and bootstrap skills

- task ID: `TASK-03-002`
- output: updates to `app-descriptions`, `app-description-bootstrap`, and `app-description-intake-router`.
- scope: route agentic product inputs to AI-first modeling; bootstrap appropriate tree sections.

### 3. Refactor capability and behavior specification skills

- task ID: `TASK-03-003`
- output: updates to capability and behavior skills.
- scope: represent operational delegation, human governance, policies, approvals, exceptions, and learning loops.

### 4. Refactor test/security/observability/UI app-description skills

- task ID: `TASK-03-004`
- output: updates to test, security, observability, and UI description skills.
- scope: add AI-first acceptance, eval, permission, audit, trace, privacy, digest, and supervision surface guidance.

### 5. Update app-description examples or add example placeholders

- task ID: `TASK-03-005`
- output: minimal example updates or explicit TODO references for Sprint 6 worked example.
- scope: avoid forcing old examples into AI-first unless useful.

## Done criteria

- Description-first maintenance can ingest broad product intent and preserve agentic substrate meaning.
- The path still supports non-agentic or low-agentic applications when explicitly scoped that way.
