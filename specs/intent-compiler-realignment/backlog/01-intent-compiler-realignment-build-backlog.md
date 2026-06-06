# Intent Compiler Realignment Build Backlog

## Goal

Move the skills pack's user-intent processing layer from accumulated description-first guidance to a clean intent compiler architecture.

## Implementation notes

- Treat `skills-pack/docs/intent-compiler-working-note.md` as the seed artifact.
- Prefer clean canonical docs over broad edits to legacy docs.
- Archive selectively; do not archive focused Akka implementation skills.
- Avoid moving active skill directories until an inventory proves the safe path.
- Preserve installed-layout relative references in `skills/*/SKILL.md`.

## Suggested task breakdown

1. Inventory active intent-processing skills/docs and define archive strategy.
2. Create canonical intent compiler docs.
3. Archive or mark legacy docs and update doc indexes/references.
4. Replace/realign intake and normalization skills.
5. Replace/realign app-description capture/review/generation skills.
6. Replace/realign planning and queue skills.
7. Realign high-level router skills and examples/templates.
8. Run final install/reference validation and append follow-up tasks if gaps remain.

## Required checks

Use the smallest proving checks per task. Common checks:

```bash
git diff --check
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --prune
./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check
bash pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh
```

## Acceptance criteria

- Active docs and skills use the intent compiler terminology and structure.
- Workstream-specific bindings are explicit where reusable global artifacts are referenced.
- Current intent remains the canonical model; historical context is relegated to git/archive notes.
- Terminal verification confirms completion or appends bounded follow-up tasks.
