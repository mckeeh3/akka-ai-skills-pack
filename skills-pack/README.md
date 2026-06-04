# Skills Pack Source

This directory contains the source for the installable Akka AI skills pack after the core-app-first repository refactor.

Primary maintenance entry points:

- `AGENTS.md` — source-maintainer guidance for skills-pack work
- `skills/README.md` — skill routing map
- `pack/` — installed-pack guidance and manifest metadata
- `docs/` — pack doctrine, routing, and reference docs
- `akka-context/` — vendored official Akka reference material for maintainers; not installed
- `tools/` — release, validation, and audit tools
- `install-skills.sh` — skills-only installer entrypoint

The repository root remains the canonical runnable core app. This directory owns installable skills and supporting guidance assets. The repo/tag is now the unit of installation; a distribution bundle is no longer required for normal use.
