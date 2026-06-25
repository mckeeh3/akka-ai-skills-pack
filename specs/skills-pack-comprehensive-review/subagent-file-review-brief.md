# Subagent File Review Brief

Use this brief to review exactly one entry from `file-review-inventory.md`.

## Selection

- If the parent named an inventory id/path, review only that entry.
- Otherwise select the first entry with status `pending`.
- Before reading beyond the planning docs, update that row to `in-progress` and commit nothing yet.

## Required reads

Always read:

- `specs/skills-pack-comprehensive-review/README.md`
- `specs/skills-pack-comprehensive-review/review-guide.md`
- `specs/skills-pack-comprehensive-review/file-review-inventory.md`
- the target file

Then read only directly relevant canonical docs, linked docs, references, manifests, or examples needed to judge the target file.

## Scope limits

Review one file entry only. If the target file requires broad cross-file work, do the smallest safe supporting edits needed for consistency, then mark the entry `blocked-with-follow-up` or append a follow-up task rather than expanding into a multi-file migration.

Do not directly edit `skills-pack/.agents/skills/**` as source doctrine. For installed mirror entries, either verify derivation/source drift, record the source file to repair, or block with an installer-output reconciliation note.

## Decision options

Choose one terminal status:

- `accepted`
- `revised`
- `archived`
- `removed`
- `installer-output-verified`
- `superseded`
- `blocked-with-follow-up`

Update the review notes with concise evidence, for example:

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

After status is terminal and checks are complete, create one focused commit containing:

- the target file changes, if any;
- reference/manifest/link repairs required by the target file decision, if any;
- `file-review-inventory.md` status/notes update.

Report the commit hash/message and the next pending inventory entry.
