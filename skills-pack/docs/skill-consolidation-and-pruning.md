# Skill consolidation and pruning notes

Use this note during skills-pack maintenance to keep the installed pack focused. It records the current cleanup policy after the June 2026 content-health review.

## Current judgement

The pack is healthy but broad. Do not delete installed skills casually: skill names are a public routing surface for harnesses and downstream prompts. Prefer consolidation by routing, shared references, and shorter skills before removing a skill from the manifest.

## Consolidation watchlist

These focused pairs intentionally remain separate because Akka KVE and ESE APIs differ, but they should share examples and avoid copied doctrine:

| Family | Keep separate for | Tightening rule |
|---|---|---|
| `akka-ese-*` / `akka-kve-*` domain, entity, edge-flow, TTL, notification, replication, unit, integration, doc-snippet skills | different Akka component APIs and test kits | Keep only API-specific mechanics in the focused skill; put shared validation/idempotency/testing guidance in suite skills or references such as `../skills/references/akka-entity-integration-testing-patterns.md`. |
| HTTP / gRPC / MCP endpoint component-client, JWT, request-context, streaming, testing skills | different endpoint APIs and request contexts | Keep transport-specific mapping only; route cross-cutting auth/audit/tenant rules to shared security and capability docs. |
| Agent prompt/skill/reference/model/tool/work-trace governance skills | separate governed artifacts and runtime boundaries | Keep artifact-specific implementation contracts; avoid restating the full governed-agent substrate in every skill. |
| Web UI forms/state/realtime/accessibility/API-client/testing skills | separate frontend implementation concerns | Keep implementation checklists compact and route architecture/style doctrine to canonical UI docs. |

## Pruning rules

Remove or merge a skill only when all are true:

1. Another active skill covers the same trigger with no meaningful implementation difference.
2. The replacement skill has an explicit routing note for the retired trigger.
3. `pack/manifest.yaml`, `skills/README.md`, and any maintainer routing references are updated together.
4. `install-skills.sh --prune --check` behavior remains safe for existing installs.
5. Pack checks pass.

## Broad-skill trimming rules

Broad orchestrator skills should be short routing contracts. They may contain:

- when to use the skill;
- required canonical docs to read;
- focused downstream skills to load;
- hard fail-closed/security/runtime-completion rules;
- output/validation expectations.

They should not copy long sections from canonical docs. If a broad skill grows beyond roughly 200 lines, first move repeated doctrine into `docs/**` or `skills/references/**` and leave a precise pointer.

## Example pruning rules

`examples/akka-components/**` is a curated reference snapshot, not a second app. Keep a file only when it is one of:

- a primary pattern reference named by a skill/doc;
- compact support context required by a primary pattern;
- test/reference coverage for a primary pattern.

When adding examples, prefer compact semantic examples outside the Java snapshot unless a current Akka API pattern must be demonstrated in Java. Update `examples/akka-components/REFERENCE-INDEX.md` for every Java snapshot file and keep `validate-curated-example-index.py` passing.
