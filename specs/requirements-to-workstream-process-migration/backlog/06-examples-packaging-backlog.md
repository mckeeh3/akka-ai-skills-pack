# Backlog 06: Examples, Seed, and Packaging Alignment

## Goal

Make reference examples and installed-pack assets reinforce the new process.

## Suggested task breakdown

1. Add or update a requirements-to-workstream example.
2. Update seed/starter references and demote legacy mechanics examples where needed.
3. Update pack manifest/readme/export if new canonical docs are added.

## Implementation notes

- Examples should show large or realistic input becoming workstreams, attention breakdowns, dashboards, surfaces, capabilities, autonomous tasks, events/notifications, traces, and tasks.
- Purchase-request examples should remain mechanics references only if kept.

## Required checks

- `git diff --check`
- `rg -n "requirements-to-workstream|attention|dashboard|AutonomousAgent|mechanics reference|target architecture" docs/examples docs/prd-to-akka-flow.md pack/manifest.yaml pack/README.md pack/AGENTS.md`

## Acceptance criteria

- Installed-pack users can discover and follow the new process.
