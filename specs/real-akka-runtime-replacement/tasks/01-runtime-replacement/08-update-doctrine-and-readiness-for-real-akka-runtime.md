# Task: Update doctrine and readiness for real Akka runtime replacement

## Objective

Update repository guidance so downstream implementation sessions understand that real Akka component-backed runtime is mandatory for workstream/foundation features, not optional hardening after local-demo defaults.

## Required reads

- AGENTS.md
- skills/README.md
- docs/ai-first-saas-application-architecture.md
- docs/agent-workstream-application-architecture.md
- docs/minimum-ai-first-saas-app.md
- skills/app-description-readiness-assessment/SKILL.md
- skills/app-generate-app/SKILL.md
- skills/akka-prd-to-specs-backlog/SKILL.md
- skills/akka-do-next-pending-task/SKILL.md
- templates/ai-first-saas-starter/README.md
- specs/full-core-smb-runtime-durability-remediation/README.md
- specs/real-akka-runtime-replacement/README.md

## Skills

- none; repository doctrine/readiness cleanup task

## In scope

- Replace wording that permits local/demo/non-Akka substitute/default runtime adapters with language requiring real Akka components.
- Clarify that fail-closed applies to missing external provider/security configuration, not missing internal Akka persistence for claimed features.
- Update starter README and related specs to supersede prior local/demo gating compromise.
- Ensure fixture guidance says test-only.

## Out of scope

- Do not rewrite unrelated architecture doctrine.

## Expected outputs

- updated docs/skills/template README/specs as needed
- queue update

## Required checks

- `git diff --check`
- `rg -n "local/demo|non-Akka substitute adapter|default local|fixtureWorkstream|fixture client|mock runtime|optional hardening|replace .* in production" AGENTS.md pack/AGENTS.md skills docs templates/ai-first-saas-starter/README.md specs/full-core-smb-runtime-durability-remediation specs/real-akka-runtime-replacement --glob '!**/node_modules/**' --glob '!**/target/**'`

## Done criteria

- Guidance consistently says normal generated runtime uses real Akka components.
- Test-only substitutes remain allowed only in tests.
- No docs imply local/demo runtime is acceptable for completed workstream features.
- Changes and queue update are committed.

## Commit message

`runtime: update real Akka runtime doctrine`
