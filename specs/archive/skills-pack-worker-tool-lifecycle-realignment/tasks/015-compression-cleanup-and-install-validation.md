# TASK-015: Compression, cleanup, manifest, and install validation

## Scope

After family migrations, clean up duplicated doctrine, update pack metadata/routing summaries, and validate installed-layout references.

## Required reads

- `skills-pack/docs/skill-consolidation-and-pruning.md`
- `skills-pack/skills/README.md`
- `skills-pack/pack/manifest.yaml`
- `skills-pack/install-skills.sh`
- `skills-pack/tools/validate-installed-skill-references.py`
- `specs/skills-pack-worker-tool-lifecycle-realignment/migration-strategy.md`

## Expected outputs

- Updated consolidation/pruning notes if policy changes.
- Updated manifest/routing metadata as needed.
- Cleanup of obvious duplicate doctrine introduced or discovered during migration.
- Decision note about `skills-pack/.agents/skills/**` source-vs-generated expectations.

## Done criteria

- Broad/orchestrator skills over roughly 200 lines are reviewed for compression opportunities.
- Public skill names are preserved unless a fully routed retirement is justified.
- Installed-layout references are valid.
- Manifest and routing docs do not contradict the new lifecycle/model.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
