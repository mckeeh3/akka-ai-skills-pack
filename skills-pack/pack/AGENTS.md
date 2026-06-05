# Pack Metadata Guidance

This file is source-repository metadata for maintainers. It is **not** installed into `.agents/` and must not be treated as installed-pack guidance.

## Current user model

Pack users clone or fork this repository. The repository root is the runnable Akka full-stack core app and the place where product/domain-specific workstreams are built.

The only install step is a skills-only harness install:

```bash
./install-skills.sh --target <project>/.agents/skills --prune
```

That command installs only:

```text
<skills-dir>/
  .akka-ai-skills-pack-install-manifest
  README.md
  references/
  <skill-name>/SKILL.md
```

It does not install `AGENTS.md`, `docs/`, `templates/`, `resources/examples/`, manifests, tools, app-description/spec trees, frontend source, Java source, or a duplicate application baseline.

## Maintainer rules

- Keep installed skill files self-contained enough to run from `.agents/skills`.
- When a skill needs source-checkout assets, label those paths explicitly as source-checkout references under this repository, not installed `.agents` paths.
- Do not mention `.agents/docs` or `.agents/resources/examples` unless a future task deliberately changes the install contract and updates `install-skills.sh`.
- Root application guidance belongs in the repository root `AGENTS.md`, `README.md`, `docs/**`, `app-description/**`, `specs/**`, backend source, and `frontend/**`.
- Skills-pack maintenance guidance belongs in `skills-pack/AGENTS.md` and `skills-pack/**`.
