# TASK-019: Repair optional-security wording guardrail violation

## Scope

Repair the maintainer verification blocker revealed by TASK-018: forbidden optional-security wording in `docs/ai-first-saas-application-architecture.md`.

Keep the repair focused on preserving mandatory-security doctrine. Do not edit root runtime code and do not make broader architecture, app-description, or skill-family changes.

## Required reads

- `specs/skills-pack-worker-tool-lifecycle-realignment/verification-notes.md`
- `docs/ai-first-saas-application-architecture.md`

## Expected outputs

- Focused wording repair in `docs/ai-first-saas-application-architecture.md` or the exact file reported by the maintainer script if the line moved.
- Updated verification notes summarizing the repaired blocker and any newly revealed unrelated blocker.

## Done criteria

- Removes the forbidden optional-security phrasing without weakening the doctrine that security is mandatory for AI-first SaaS architecture.
- Does not alter runtime code.
- The maintainer verification script advances past the optional-security wording guardrail or passes; any newly revealed unrelated blocker is recorded for terminal verification.

## Required checks

- `git diff --check`
- `bash skills-pack/pack/maintainer/tools/verify-opinionated-ai-first-saas-pack.sh`
