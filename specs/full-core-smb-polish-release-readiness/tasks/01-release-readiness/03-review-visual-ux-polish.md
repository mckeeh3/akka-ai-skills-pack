# Task: Review visual UX and cross-workstream polish

## Objective

Review the five-core SMB starter against the visual UX quality standard, focusing on shell coherence, dashboard hierarchy, structured surfaces, system messages, provider-blocked states, trace affordances, accessibility, and responsive behavior.

## Required reads

- `AGENTS.md`
- `specs/full-core-smb-polish-release-readiness/README.md`
- `specs/full-core-smb-polish-release-readiness/integrated-release-readiness-map.md`
- `specs/full-core-smb-polish-release-readiness/sprints/01-polish-release-readiness-sprint.md`
- `specs/full-core-smb-polish-release-readiness/backlog/01-polish-release-readiness-backlog.md`
- `specs/full-core-smb-saas-hardening/visual-ux-quality-standard.md`
- `specs/full-core-smb-baseline-and-ux/shared-baseline-contracts.md`

## In scope

- Inspect frontend shell/surface source, fixtures, and contract tests.
- Run focused frontend tests/checks where useful.
- Record findings as blocker, non-blocking polish, or post-release recommendation.
- Append bounded source-fix tasks before terminal verification only for release-blocking visual/UX issues.

## Out of scope

- Do not redesign the product or add new workstream features.
- Do not count fixture-only UI as normal runtime behavior.

## Expected outputs

- `specs/full-core-smb-polish-release-readiness/visual-ux-polish-review.md`
- updated `pending-tasks.md` if bounded visual blocker tasks are needed

## Required checks

- targeted `rg`/`find` commands over frontend shell, surfaces, fixtures, and tests
- `cd templates/ai-first-saas-starter/frontend && npm test -- --run`
- `cd templates/ai-first-saas-starter/frontend && npm run typecheck`
- `git diff --check`

## Done criteria

- Review covers My Account, User Admin, Agent Admin, Audit/Trace, Governance/Policy, shell, system messages, provider-blocked states, and trace links.
- Findings distinguish release blockers from intentional deferrals and recommendations.
- Task changes and queue update are committed.

## Commit message

- `full-core-smb: review visual release polish`
