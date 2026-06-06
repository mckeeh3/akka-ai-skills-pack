# Build Backlog 01: Workstream Contract Cleanup

## Goal

Make the workstream contract implementation-ready by aligning docs, schema, validator, templates, and install-layout checks.

## Implementation notes

- Keep `skills-pack/docs/workstream-contract.md` as the semantic contract.
- Keep `skills-pack/docs/workstream-manifest.schema.json` as the machine-readable manifest contract.
- Keep `skills-pack/docs/structured-surface-contracts.md` as the detailed surface/action source; manifest mappings should be lightweight referential integrity, not a duplicate full contract.
- Validate against installed `.agents/skills` layout for skill references.
- Prefer focused edits and tests that prove the contract changes.

## Suggested harness task breakdown

1. **TASK-WCC-01-001** — Align manifest required fields, attention semantics, severity vocabulary, and existing validator/template behavior.
2. **TASK-WCC-01-002** — Add `surfaceActionMappings`/runtime evidence/structured internal worker schema and validator rules.
3. **TASK-WCC-01-003** — Add installed-layout reference convention docs and validation/check integration.
4. **TASK-WCC-01-004** — Run a focused consistency sweep across docs/templates/examples and repair drift.
5. **TASK-WCC-99-001** — Verify completion and append follow-up tasks if gaps remain.

## Dependencies

Tasks should run in order because later tasks depend on the contract vocabulary and schema shape established by earlier tasks.

## Required checks

Use the smallest checks that prove each task, typically:

```bash
git diff --check
./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run
./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune
./skills-pack/install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check
bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh
```

For manifest-specific changes, also validate the relevant template app-description with:

```bash
python3 skills-pack/tools/validate-workstream-manifest.py skills-pack/templates/ai-first-saas-core-app/app-description
bash skills-pack/tools/validate-workstream-contracts.sh skills-pack/templates/ai-first-saas-core-app/app-description
```

## Acceptance criteria

- Accepted decisions in `conversation-capture.md` are implemented or explicitly deferred with a queue note.
- Existing templates and examples are not left invalid under the updated schema/validator.
- Installed-layout reference convention is preserved and documented.
- Verification task can determine completion without redoing the whole review.
