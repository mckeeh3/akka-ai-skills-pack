# Verification Notes: Core App First Refactor Follow-ups (2026-06-03)

## Result

Terminal verification after the stale-reference follow-ups passed. The core-app-first mini-project done state is satisfied with no new bounded follow-up tasks required.

## Done-state assessment

- Repository root is the single canonical runnable core app source: verified by root Maven/frontend checks and the absence of the removed `templates/ai-first-saas-starter/` directory.
- Large full-app template is removed as a maintained duplicate source: `test ! -d templates/ai-first-saas-starter` passed.
- Skills-pack source and maintenance assets are isolated under `skills-pack/`: install, build-pack, and pack verification checks passed from the new paths.
- Focused Akka examples are retained under `skills-pack/examples/akka-components/`: pack build/install dry-run still stages Java examples from that location.
- Root docs/guidance and extension boundaries are current: no stale full-app-template/scaffold-first matches were found in current root/skills-pack guidance or scripts outside specs/provenance paths.
- Installed-pack guidance no longer directs users to scaffold a duplicate starter app: skills-pack verification passed and stale-guidance search returned no current guidance/script matches.
- Root backend/frontend validation commands passed.
- Historical stale references remain only in completed specs/provenance records as classified by `stale-template-reference-classification.md`.

## Checks run

- `mvn test` — passed.
- `npm --prefix frontend test -- --run` — passed.
- `npm --prefix frontend run typecheck` — passed.
- `npm --prefix frontend run build` — passed.
- `./install.sh --location project --project /tmp/akka-install-dry-run --dry-run` — passed.
- `bash skills-pack/tools/build-pack.sh --github-repo example/repo --output-dir /tmp/akka-pack-build-check --clean --no-archive` — passed.
- `bash skills-pack/tools/verify-opinionated-ai-first-saas-pack.sh` — passed.
- `test ! -d templates/ai-first-saas-starter` — passed.
- Current guidance/script stale-reference search outside `specs/**`, `target/**`, node modules, `skills-pack/dist/**`, and `.git/**` — returned no matches.
- Pending/in-progress stale-reference block scan initially found only this terminal verification task while it was in progress; after marking it done there are no remaining pending tasks in this mini-project and no material active stale-reference gap remains.
- Broad stale-reference count excluding `specs/core-app-first-repo-refactor/**`, `specs/archive/**`, build outputs, node modules, and `.git/**`: 389 files, classified as completed historical/provenance records by `stale-template-reference-classification.md` unless future work reopens those specs.

## Follow-up decision

No new tasks were appended. The remaining broad matches are not current runnable guidance, scripts, or pending/in-progress queue instructions requiring removed template paths.
