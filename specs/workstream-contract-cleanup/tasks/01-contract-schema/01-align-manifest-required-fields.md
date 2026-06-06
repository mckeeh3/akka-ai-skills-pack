# TASK-WCC-01-001: Align manifest required fields and attention vocabulary

## Objective

Align the existing workstream manifest contract across docs, JSON schema, validator, and template for required governed-agent id, icon tooltip, attention category semantics, severity vocabulary, and non-empty capability validation.

## Required reads

- `AGENTS.md`
- `skills-pack/AGENTS.md`
- `specs/workstream-contract-cleanup/README.md`
- `specs/workstream-contract-cleanup/conversation-capture.md`
- `specs/workstream-contract-cleanup/sprints/01-workstream-contract-cleanup-sprint.md`
- `specs/workstream-contract-cleanup/backlog/01-workstream-contract-cleanup-build-backlog.md`
- `specs/workstream-contract-cleanup/tasks/01-contract-schema/01-align-manifest-required-fields.md`
- `skills-pack/docs/workstream-contract.md`
- `skills-pack/docs/workstream-manifest-schema.md`
- `skills-pack/docs/workstream-manifest.schema.json`
- `skills-pack/docs/workstream-attention-contracts.md`
- `skills-pack/docs/workstream-ui-reference-architecture.md`
- `skills-pack/tools/validate-workstream-manifest.py`
- `skills-pack/templates/ai-first-saas-core-app/app-description/12-workstreams/workstream-manifest.json`

## Skills

- none; skills-pack docs/tooling maintenance task

## In scope

- Require `managedAgentDefinitionId` in manifest docs, JSON schema, validator, and template.
- Require icon `tooltip` in manifest docs, JSON schema, validator, and template.
- Document `attentionCategories` as workstream-local ids mapped in markdown/producer contracts to canonical `AttentionItem.category` values.
- Replace UI severity drift from `critical` to the canonical `info | warning | urgent | blocked` vocabulary.
- Fix validator handling so `capabilities: []` fails.
- Require `attentionCategories: []` to have explicit markdown explanation in `attention-and-dashboards.md` or equivalent validator-supported evidence.

## Out of scope

- Adding full `surfaceActionMappings` schema.
- Adding runtime-ready evidence fields.
- Adding structured `internalWorkers` objects.
- Changing installed-layout skill references.

## Expected outputs

- Updated workstream docs/schema/validator/template files in the in-scope list.
- Queue status update in `specs/workstream-contract-cleanup/pending-tasks.md`.

## Required checks

```bash
git diff --check
python3 skills-pack/tools/validate-workstream-manifest.py skills-pack/templates/ai-first-saas-core-app/app-description
bash skills-pack/tools/validate-workstream-contracts.sh skills-pack/templates/ai-first-saas-core-app/app-description
```

If broader pack verification is cheap and relevant, also run:

```bash
./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run
```

## Done criteria

- The accepted decisions 1, 2, 3, 4, and 13 are reflected in docs/schema/validator/template.
- The foundation template validates under the updated validator.
- The queue is updated and the task changes are committed.

## Commit message

```text
skills-pack: align workstream manifest required fields
```
