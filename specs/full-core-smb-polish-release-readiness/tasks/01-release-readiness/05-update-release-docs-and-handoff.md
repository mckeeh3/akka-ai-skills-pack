# Task: Update release docs and handoff

## Objective

Update starter/release documentation where needed and produce a concise release handoff that summarizes validation evidence, known blockers, intentional deferrals, and the current ship/no-ship recommendation.

## Required reads

- `AGENTS.md`
- `specs/full-core-smb-polish-release-readiness/README.md`
- `specs/full-core-smb-polish-release-readiness/conversation-capture.md`
- `specs/full-core-smb-polish-release-readiness/integrated-release-readiness-map.md`
- `specs/full-core-smb-polish-release-readiness/validation-results.md`
- `specs/full-core-smb-polish-release-readiness/visual-ux-polish-review.md`
- `specs/full-core-smb-polish-release-readiness/provider-trace-secret-audit.md`
- `templates/ai-first-saas-starter/README.md`

## In scope

- Update docs only where they underclaim, overclaim, or omit release-critical behavior.
- Write the release handoff with commands/results, provider-env mode, intentional deferrals, known recommendations, and release recommendation.
- Append bounded blocker tasks before terminal verification if docs cannot honestly recommend release.

## Out of scope

- Do not implement new product behavior.
- Do not hide failed validation behind ambiguous release wording.

## Expected outputs

- `specs/full-core-smb-polish-release-readiness/release-handoff.md`
- updated `templates/ai-first-saas-starter/README.md` only if needed
- updated `pending-tasks.md` if bounded blocker tasks are needed

## Required checks

- `rg -n "full-core|provider|fail-closed|system_message|trace|secret|hidden prompt|worker|release|deferral|OPENAI_API_KEY" templates/ai-first-saas-starter/README.md specs/full-core-smb-polish-release-readiness`
- `git diff --check`

## Done criteria

- Handoff reports validation evidence and release recommendation clearly.
- Docs do not claim deterministic/model-less normal runtime completion for model-backed behavior.
- Intentional deferrals are explicit and not confused with completed features.
- Task changes and queue update are committed.

## Commit message

- `full-core-smb: write release handoff`
