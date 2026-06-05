# Pack Metadata Guidance

This file is source-repository metadata for maintainers. It is **not** installed into `.agents/` and must not be treated as installed-pack guidance.

## Current user model

Pack users clone or fork this repository. The repository root is the runnable Akka full-stack core app and the place where product/domain-specific workstreams are built.

The install step is a harness skills-library install:

```bash
./install-skills.sh --target <project>/.agents/skills --prune
```

That command installs:

```text
<skills-dir>/
  .akka-ai-skills-pack-install-manifest
  README.md
  references/
  docs/
  examples/
  templates/
  tools/
  <skill-name>/SKILL.md
```

It does not install `AGENTS.md`, manifests, app-description/spec trees, frontend source, Java source, `akka-context/**`, or a duplicate application baseline. `akka-context/**` is expected as an independently maintained top-level project/repository directory.

## Maintainer rules

- Keep installed skill files self-contained enough to run from `.agents/skills`, using installed `docs/`, `examples/`, `templates/`, `tools/`, and `references/` paths where needed.
- When a skill needs target-project source files, label those paths explicitly as target-project paths, not pack source-checkout paths.
- Do not mention `.agents/docs` or `.agents/resources/examples`; pack assets install under `.agents/skills/**`. Akka SDK reference docs are the exception and should point to the top-level `akka-context/**` directory.
- Root application guidance belongs in the repository root `AGENTS.md`, `README.md`, `docs/**`, `app-description/**`, `specs/**`, backend source, and `frontend/**`.
- Skills-pack maintenance guidance belongs in `skills-pack/AGENTS.md` and `skills-pack/**`.
