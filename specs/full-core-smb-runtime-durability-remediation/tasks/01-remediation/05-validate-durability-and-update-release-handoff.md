# Task: Validate durability remediation and update release handoff

## Objective

Run broad validation after backend/frontend remediation, then update starter and release-readiness documentation to state the actual durability status and release recommendation.

## Required reads

- AGENTS.md
- specs/full-core-smb-runtime-durability-remediation/README.md
- specs/full-core-smb-runtime-durability-remediation/conversation-capture.md
- specs/full-core-smb-runtime-durability-remediation/runtime-durability-remediation-map.md
- specs/full-core-smb-runtime-durability-remediation/pending-tasks.md
- templates/ai-first-saas-starter/README.md
- specs/full-core-smb-polish-release-readiness/release-handoff.md
- specs/full-core-smb-polish-release-readiness/release-readiness-verification.md

## In scope

- Run fullstack/rendered validation after source remediation.
- Re-run inventory scans and classify any remaining hits.
- Update release handoff/verification and starter README with corrected durability status.
- Append follow-up tasks if validation reveals remaining blockers.

## Out of scope

- Implementing newly discovered source remediation in the same task unless it is a trivial docs correction. Append bounded tasks instead.

## Expected outputs

- Updated validation/release docs.
- Updated pending queue if remaining blockers exist.
- Validation evidence captured in docs or task notes.

## Required checks

- `git diff --check`
- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `rg -n "Substitute|Akka component-backed|mock|Mock|fake|Fake|fixture|Fixture|demo|Demo|canned|model-less|fallback|stub|Stub" templates/ai-first-saas-starter frontend specs/full-core-smb-polish-release-readiness --glob '!**/node_modules/**' --glob '!**/target/**' --glob '!**/dist/**'`
- `rg -n "fixtureWorkstream|FixtureWorkstream|fixture|demo|Substitute|fake|model-less|OPENAI_API_KEY|WORKOS_API_KEY" templates/ai-first-saas-starter/src/main/resources/static-resources --glob '!**/*.map'`

## Done criteria

- Broad starter validation passes or remaining blockers are appended as bounded tasks before verification.
- Release docs no longer recommend shipping under the stronger durability bar unless the bar is met.
- Remaining test-only/local-demo findings are explicitly classified.
- Checks pass and changes are committed.

## Commit message

- `full-core-smb: validate runtime durability remediation`
