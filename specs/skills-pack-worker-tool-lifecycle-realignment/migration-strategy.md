# Migration Strategy

## Strategy summary

Use a doctrine-first, pilot-second, family-migration-third approach.

```text
1. Canonical doctrine
2. Routing and skill metadata contract
3. Representative pilot migration
4. Family-by-family migration
5. Compression and cleanup
6. Terminal verification and follow-up planning
```

## Why one umbrella mini-project first

This change affects roughly 150 skills plus shared docs, references, examples, install metadata, and routing. Starting with several independent mini-projects would risk divergent definitions of lifecycle, workers, tools, surfaces, capabilities, and compile contracts.

The umbrella project establishes one conceptual spine first. Follow-on mini-projects may be created later for large remaining migrations or example cleanup.

## Migration principles

- Preserve skill names unless a clear replacement and routing note exists.
- Treat `skills-pack/skills/**` as canonical source.
- Treat `skills-pack/.agents/skills/**` as installed-layout output/reference behavior, not primary source.
- Move repeated doctrine into `skills-pack/docs/**` or `skills-pack/references/**`.
- Keep broad/orchestrator skills short and route to canonical docs and focused skills.
- Keep focused skills focused on implementation mechanics and specific outputs.
- Prefer family-sized tasks over all-pack rewrites.
- Run pack validation after each meaningful migration slice.

## Phase 1: Canonical doctrine

Create or update canonical docs for:

- app development lifecycle;
- app worker/tool model;
- app-description component graph;
- app-description-to-code compile contract;
- manual test reconciliation.

Candidate docs:

```text
skills-pack/docs/app-development-lifecycle.md
skills-pack/docs/app-worker-tool-model.md
skills-pack/docs/app-description-component-graph.md
skills-pack/docs/app-description-to-code-compile-contract.md
skills-pack/docs/manual-test-reconciliation.md
```

## Phase 2: Routing and metadata

Update the routing map and skill contracts:

```text
skills-pack/skills/README.md
skills-pack/docs/intent-compiler-skill-contracts.md
skills-pack/docs/intent-to-realization-flow.md
skills-pack/pack/manifest.yaml
```

Define standard skill metadata in prose and, where practical, manifest fields:

```text
Phase: interview | build-compile | manual-test | cross-phase
Kind: orchestrator | focused | planning | testing | verification | docs
Family: app-description | worker | tool | capability | agent | web-ui | akka-component | endpoint | queue | verification
Consumes:
Produces:
Routes to:
```

## Phase 3: Pilot migration

Pilot the new doctrine on a small representative set before family-wide edits.

Suggested pilot skills:

- `ai-first-saas`
- `agent-workstream-apps`
- `ai-first-saas-worker-decomposition`
- `capability-first-backend`
- `app-descriptions`
- `app-description-surface-modeling`
- `app-description-capability-modeling`
- `akka-solution-decomposition`
- `akka-agent-tools`
- `akka-http-endpoint-component-client`
- `akka-web-ui-apps`
- `akka-runtime-feature-verification`

Pilot goals:

- prove the lifecycle framing works;
- prove the worker/tool/capability terminology works;
- prove broad skills can be compressed into routing contracts;
- identify metadata/header shape that is easy to apply to other skills;
- uncover terminology conflicts before bulk migration.

## Phase 4: Skill family migrations

Migrate families in separate tasks:

1. app-description skills;
2. AI-first SaaS/workstream/worker/surface/object/policy/outcome skills;
3. agent and governed-agent skills;
4. Akka component skills: entities, workflows, views, consumers, timers;
5. endpoint skills: HTTP, gRPC, MCP;
6. web UI skills;
7. planning/backlog/pending-task/change-reconciliation skills;
8. testing/runtime/manual-verification skills.

Each family migration should:

- apply lifecycle and worker/tool/capability terminology;
- add or normalize phase/kind/family metadata where practical;
- replace duplicated doctrine with links to canonical docs;
- preserve focused API-specific mechanics;
- run targeted reference validation/search checks.

## Phase 5: Compression and cleanup

After migrations:

- audit broad skills over roughly 200 lines and compress where safe;
- update `skills-pack/docs/skill-consolidation-and-pruning.md` if cleanup policy changes;
- update examples/reference indexes if examples are renamed or narrowed;
- validate installed-layout references;
- decide whether `.agents/skills` mirror should be regenerated, ignored, or removed from maintenance expectations.

## Phase 6: Terminal verification

The terminal verification task should:

- compare completed work with `README.md` done state;
- check terminology consistency;
- check skill routing consistency;
- run pack install/reference validation;
- search for stale contradictory doctrine;
- append follow-up tasks and a new terminal verification task if material gaps remain;
- only close the mini-project when no material gaps remain.
