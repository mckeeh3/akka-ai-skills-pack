# Task: Add core surface catalog metadata

## Objective

Define surface familiarity metadata for all five core workstreams so deterministic routing and agent guidance share an explicit catalog.

## Required reads

- `AGENTS.md`
- `specs/workstream-surface-intent-routing/README.md`
- `specs/workstream-surface-intent-routing/sprints/03-catalog-and-expansion.md`
- `src/main/java/ai/first/application/coreapp/workstream/WorkstreamService.java`
- `app-description/domains/core-starter/workstreams/**` where present
- `frontend/src/workstream/surfaces/**`

## Skills

- app-description-surface-modeling
- ai-first-saas-ui-surfaces
- akka-agent-behavior-profiles

## Expected outputs

- Surface catalog metadata for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.
- Each catalog entry includes surface id, title, purpose, supported prompt examples, required capabilities, prefill fields, and forbidden direct effects.
- Router can use the catalog or a documented mapping derived from it.
- Queue update.

## Required checks

- `git diff --check`
- focused search/contract check proving all five workstreams have catalog entries
- targeted tests if code-owned catalog behavior changes

## Done criteria

- Surface catalog does not grant authority; it only describes routing/familiarity.
- Catalog entries cover current core surfaces sufficiently for high-confidence open/prefill routing.
- Any deferred or ambiguous surfaces are explicitly noted.
- Changes and queue update are committed.
