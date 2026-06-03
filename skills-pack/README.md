# Skills Pack Source

This directory contains the source for the installable Akka AI skills pack after the core-app-first repository refactor.

Primary maintenance entry points:

- `AGENTS.md` — source-maintainer guidance for skills-pack work
- `skills/README.md` — skill routing map
- `pack/` — installed-pack guidance and manifest metadata
- `docs/` — pack doctrine, routing, and reference docs
- `akka-context/` — vendored official Akka reference material for maintainers; not installed
- `tools/` — pack build, release, validation, and audit tools
- `install.sh` — local installer entrypoint

The repository root remains the canonical runnable core app. This directory owns installable guidance and packaging assets.
