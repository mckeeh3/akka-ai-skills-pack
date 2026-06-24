# TASK-WCTE-10-001: Expand representative chat tool plans to all five foundation workstreams

## Purpose

Add at least one representative confirmed chat tool-plan path for each foundation workstream using the shared substrate.

## Required reads

- `AGENTS.md`
- `specs/workstream-chat-tool-execution/README.md`
- `specs/workstream-chat-tool-execution/source-and-design-map.md`
- completed shared/User Admin implementation files
- `app-description/domains/core-starter/workstreams/**`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- related backend/frontend tests

## Skills

- `agent-workstream-apps`
- `capability-first-backend`
- `ai-first-saas-ui-surfaces`
- `akka-agent-tool-boundaries`

## Expected outputs

- Representative confirmed chat tool-plan path for My Account.
- Representative confirmed chat tool-plan path for Agent Admin.
- Representative confirmed chat tool-plan path for Audit/Trace.
- Representative confirmed chat tool-plan path for Governance/Policy.
- User Admin path remains covered.
- Tests for all five workstreams.
- Queue update.

## Required checks

- `git diff --check`
- targeted backend all-workstream tests
- `npm --prefix frontend test -- --run`
- `npm --prefix frontend run typecheck`

## Done criteria

- Each foundation workstream has a bounded plan path with governed tool id, capability id, confirmation behavior, and trace semantics.
- High-impact tools remain approval-gated or blocked when safe execution is not fully modeled.
- Changes and queue update are committed.
