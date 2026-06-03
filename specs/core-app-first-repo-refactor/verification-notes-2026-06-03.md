# Verification Notes: Core App First Refactor (2026-06-03)

## Result

Terminal verification found the main root app and skills-pack migration paths working, but found material stale full-app-template references outside the refactor mini-project. The mini-project is not complete until those references are repaired or classified.

## Checks run

- `mvn test` — passed.
- `npm --prefix frontend test -- --run && npm --prefix frontend run typecheck && npm --prefix frontend run build` — passed.
- `./install.sh --location project --project /tmp/akka-install-dry-run --dry-run` — passed.
- `bash skills-pack/tools/build-pack.sh --github-repo example/repo --output-dir /tmp/akka-pack-build-check --clean --no-archive` — passed.
- `bash skills-pack/tools/verify-opinionated-ai-first-saas-pack.sh` — passed.
- `test ! -d templates/ai-first-saas-starter` — passed.
- Stale-reference search outside `specs/core-app-first-repo-refactor/**`, `specs/archive/**`, build output, node modules, and `.git/**` found remaining `templates/ai-first-saas-starter`, scaffold, and starter-template references.

## Material findings

1. Root frontend contract tests still include fallback reads or source path constants for `templates/ai-first-saas-starter/**`:
   - `frontend/src/workstream-attention-backbone.contract.test.mjs`
   - `frontend/src/workstream-my-account-vertical.contract.test.mjs`
   - `frontend/src/workstream-user-admin-expertise.contract.test.mjs`
   - `frontend/src/workstream-attention-update-delivery.contract.test.mjs`
2. Root Java comments still describe local/demo seams as for the “starter template”:
   - `src/main/java/ai/first/application/security/InvitationService.java`
   - `src/test/java/ai/first/application/agentfoundation/LocalDemoAgentBehaviorRepository.java`
   - `src/test/java/ai/first/application/security/LocalDemoIdentityRepository.java`
3. Many active root `specs/**` queues and task briefs still use `templates/ai-first-saas-starter/**`, old scaffold commands, or starter-template framing. These may be historical/provenance in some specs, but active runnable queues need either updated root/skills-pack paths or explicit supersession/classification.

## Follow-up queue updates

Added follow-up tasks before a new terminal verification task:

- `TASK-LAYOUT-05-001`: Repair root app stale starter-template references.
- `TASK-LAYOUT-05-002`: Classify and repair active spec stale scaffold/template references.
- `TASK-LAYOUT-99-002`: Verify core app first refactor completion after follow-ups.
