# Workstream Contract Cleanup

## Purpose

Capture and execute the agreed cleanup for the skills-pack workstream doctrine, manifest schema, validators, templates, and installed-layout reference checks.

The review found that workstreams are conceptually strong but the machine-readable contract and validation are behind the doctrine. This mini-project makes the contract enforceable without changing the installed `.agents/skills` reference convention.

## Source discussion

The initiative comes from a review of `skills-pack/` workstream definitions and follow-up decisions in the harness conversation. Key findings and accepted decisions are recorded in `conversation-capture.md`.

## Scope

In scope:

- `skills-pack/docs/workstream-contract.md`
- `skills-pack/docs/workstream-manifest-schema.md`
- `skills-pack/docs/workstream-manifest.schema.json`
- `skills-pack/docs/workstream-attention-contracts.md`
- `skills-pack/docs/workstream-ui-reference-architecture.md`
- `skills-pack/tools/validate-workstream-manifest.py`
- `skills-pack/tools/validate-workstream-contracts.sh` or pack verification tooling when appropriate
- `skills-pack/templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-manifest.json`
- focused examples/docs that must align with the changed contract
- installed-layout reference validation for skill docs

Out of scope:

- Implementing root SaaS Foundation App runtime features.
- Rewriting the entire workstream doctrine.
- Changing installed skill references from `../docs/...` to source-layout paths.
- Replacing the app-description markdown contract with a large schema-only model.

## Execution model

Execute one task per fresh harness context. Future sessions should read this README, `conversation-capture.md`, `pending-tasks.md`, the selected sprint/backlog, and the selected task brief before editing.

## Sprint sequence

1. **Contract/schema alignment** — align required fields, icon tooltip, attention semantics, severity vocabulary, and manifest validator behavior.
2. **Implementation-readiness mappings** — add lightweight machine-readable surface action mappings, runtime evidence requirements, and structured internal worker support.
3. **Installed-layout reference validation** — preserve the `.agents/skills` install layout convention and add validation so references are checked in the right layout.
4. **Verification** — compare completed changes against decisions and append follow-up tasks if gaps remain.

## Done state

This mini-project is complete when:

- docs, schema, validator, and template agree that `managedAgentDefinitionId` and icon `tooltip` are required;
- manifest `attentionCategories` are documented as workstream-local ids mapped to canonical attention categories in markdown/producer contracts;
- attention severity vocabulary is consistent across attention and UI docs;
- `surfaceActionMappings` or equivalent governed-tool mappings exist and are required at `capability-ready` and above;
- runtime/production readiness requires explicit evidence fields;
- `internalWorkers` supports structured entries when internal/background worker behavior is claimed;
- empty attention arrays require an explicit markdown explanation;
- installed-layout references such as `../docs/...` remain normative and are validated after install;
- relevant pack checks pass.

## Open concerns

No blocking decisions remain from the source discussion. Verification may discover follow-up consistency work and should append bounded tasks before a new terminal verification task if needed.
