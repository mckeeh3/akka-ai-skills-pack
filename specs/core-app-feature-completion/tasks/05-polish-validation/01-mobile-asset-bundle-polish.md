# Task Brief: Mobile, Asset Scan, and Bundle Polish

## Objective

Resolve non-blocking polish recommendations after feature implementation.

## Required reads

- `specs/core-app-feature-completion/README.md`
- `specs/core-app-feature-completion/sprints/05-polish-validation-sprint.md`
- `specs/full-core-smb-polish-release-readiness/release-handoff.md`
- `templates/ai-first-saas-starter/README.md`
- `templates/ai-first-saas-starter/frontend/package.json`
- `skills/akka-web-ui-accessibility-responsive/SKILL.md`
- `skills/akka-web-ui-testing/SKILL.md`

## In scope

- Mobile/off-canvas rail QA checklist and bounded fixes.
- Rendered production static asset secret scan automation after source changes.
- Bundle-size analysis and targeted optimization or documented accepted residual.
- README/readiness updates for validation commands.

## Checks

- `git diff --check`
- `tools/validate-ai-first-saas-starter-fullstack.sh`
- frontend `npm test -- --run`, `npm run typecheck`, `npm run build`
- rendered static asset secret scan

## Done criteria

- Polish findings are resolved or explicitly documented as non-blocking with validation evidence.
