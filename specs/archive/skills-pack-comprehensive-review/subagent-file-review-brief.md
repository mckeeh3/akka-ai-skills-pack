# Subagent File Review Brief

Use this brief to review a small batch of entries from `file-review-inventory.md`.

## Selection

- If the parent named inventory ids/paths, review only those entries.
- Otherwise select the first 10 consecutive entries with status `pending`.
- The parent may intentionally choose a smaller batch, including one file, for high-risk guidance, installer, manifest, archive/removal, or broad doctrine files.
- Before reading beyond the planning docs, update all selected rows to `in-progress` and commit nothing yet.

## Required reads

Always read:

- `specs/skills-pack-comprehensive-review/README.md`
- `specs/skills-pack-comprehensive-review/review-guide.md`
- `specs/skills-pack-comprehensive-review/file-review-inventory.md`
- every selected target file

Then read only directly relevant canonical docs, linked docs, references, manifests, or examples needed to judge the selected files.

## Scope limits

Review only the selected batch. If a selected file requires broad cross-file work, do the smallest safe supporting edits needed for consistency, then mark that entry `blocked-with-follow-up` or append a follow-up task rather than expanding into a multi-file migration. Continue with the remaining selected files only when doing so is safe and does not depend on the blocked decision.

Do not directly edit `skills-pack/.agents/skills/**` as source doctrine. For installed mirror entries, either verify derivation/source drift, record the source file to repair, or block with an installer-output reconciliation note.

## Decision options

Choose one terminal status per selected row:

- `accepted`
- `revised`
- `archived`
- `removed`
- `installer-output-verified`
- `superseded`
- `blocked-with-follow-up`

Update each row's review notes with concise evidence, for example:

- `accepted: aligns with lifecycle and worker/tool spine; no stale numbered layout guidance found.`
- `revised: replaced numbered app-description structure with current-intent graph references; git diff --check passed.`
- `blocked-with-follow-up: removing this file requires manifest and installer policy decision; follow-up needed.`

## Checks

Minimum check for every completed file review:

```bash
git diff --check
```

Also run relevant checks when applicable:

```bash
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check
bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh
```

For removals/archives, search for inbound references and update or block:

```bash
rg -n "<file-name-or-skill-id>" skills-pack specs
```

## Commit rule

After every selected row has a terminal status and checks are complete, create one focused commit containing:

- selected target file changes, if any;
- reference/manifest/link repairs required by selected file decisions, if any;
- `file-review-inventory.md` status/notes updates for the selected batch.

Report the commit hash/message, selected row ids, terminal statuses, and the next pending batch.
