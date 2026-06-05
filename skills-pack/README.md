# Skills Pack Source

This directory contains the source assets for the Akka AI skills pack inside the runnable secure AI-first SMB SaaS core app repository.

The repository has a two-fold purpose: maintain the harness skills library and provide a working core app that users clone or fork. This skills pack supports both by giving AI coding harnesses the guidance, referenced docs, templates, tools, and code examples needed to maintain the core app and add business-specific SaaS domains, workstreams, surfaces, agents, Akka components, frontend extensions, app-description extensions, specs, docs, and tests.

Primary maintenance entry points:

- `AGENTS.md` — source-maintainer guidance for skills-pack work
- `skills/README.md` — skill routing map installed with the skills
- `skills/*/SKILL.md` — focused harness guidance installed into `.agents/skills` or `~/.agents/skills`
- `docs/` — source-attention doctrine, routing, and reference docs
- `examples/` — code examples used by installed skills as generation guidance for real Akka/core-app implementation patterns
- `templates/` — source-attention core app-description assets
- root `akka-context/` — official Akka reference material kept at repository top level and not installed
- `pack/` — source metadata for the skills-only install contract and maintainer-only release assets
- `tools/` — downstream-safe validation/audit tools referenced by installed skills
- `install-skills.sh` — skills-only harness install entrypoint

The repository root is the canonical runnable secure AI-first SMB SaaS core app. Pack users clone or fork this repo, run the root app, and build business-specific domains and workstreams in the root app workspace rather than generating a separate parallel app. There is no separate full-pack installer and no installed duplicate baseline app; the install action makes the skills library and referenced pack assets (`docs/`, `examples/`, `templates/`, and downstream-safe `tools/`) available to an AI coding harness under `.agents/skills/**` or `~/.agents/skills/**`.
