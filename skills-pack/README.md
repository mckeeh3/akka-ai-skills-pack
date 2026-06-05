# Skills Pack Source

This directory contains the source assets for the Akka AI skills pack inside the runnable core app repository.

Primary maintenance entry points:

- `AGENTS.md` — source-maintainer guidance for skills-pack work
- `skills/README.md` — skill routing map installed with the skills
- `skills/*/SKILL.md` — focused harness guidance installed into `.agents/skills` or `~/.agents/skills`
- `docs/` — source-attention doctrine, routing, and reference docs
- `examples/` — source-attention Akka core app component examples copied from the current root app implementation
- `templates/` — source-attention core app app-description assets
- root `akka-context/` — official Akka reference material kept at repository top level and not installed
- `pack/` — source metadata for the skills-only install contract
- `tools/` — release, validation, and audit tools
- `install-skills.sh` — skills-only harness install entrypoint

The repository root is the canonical runnable full-stack Akka app. Pack users clone or fork this repo and build domain-specific workstreams in the root app workspace. There is no separate full-pack installer and no installed duplicate baseline app; the install action makes the skills library and referenced pack assets (`docs/`, `examples/`, `templates/`, and `tools/`) available to an AI coding harness under `.agents/skills/**` or `~/.agents/skills/**`.
