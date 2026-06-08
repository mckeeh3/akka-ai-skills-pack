# Task: Resolve core foundation minimum-vs-full readiness wording

## Objective

Remove residual all-or-nothing wording from `skills/core-saas-foundation/SKILL.md` that conflicts with the accepted minimum starter readiness state, while preserving strict full-core readiness gates.

## Scope

- Update route-specific app-description guidance so missing full-core semantics block `full-core` or `app-specific` readiness, but minimum starter readiness is judged against Slice 0.
- Update the output checklist so full foundation objects may be explicitly deferred for `minimum starter / not full core` generated SaaS scope, not only for non-SaaS reference work.
- Do not weaken full-core requirements.
- Optionally update `docs/core-ai-first-saas-foundation.md` if similar wording is found there.

## Required reads

- `specs/minimum-ai-first-app-migration/post-completion-objectives-review.md`
- `docs/minimum-ai-first-saas-app.md`
- `docs/core-ai-first-saas-foundation.md`
- `skills/core-saas-foundation/SKILL.md`
- `skills/app-description-readiness-assessment/SKILL.md`

## Required checks

- `git diff --check`
- `rg -n "minimum starter|full-core|explicitly deferred|not full core|blocks full-core|blocks generation" skills/core-saas-foundation/SKILL.md docs/core-ai-first-saas-foundation.md`

## Acceptance

- Minimum starter, full-core, and app-specific readiness are consistently scoped.
- Full-core omissions remain blocking for full-core/app-specific generation.
- Minimum starter omissions become explicit follow-up gates rather than contradictions.
- Task changes and queue update are committed.
