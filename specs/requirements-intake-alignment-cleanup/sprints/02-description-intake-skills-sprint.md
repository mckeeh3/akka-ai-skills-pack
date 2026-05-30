# Sprint 02: Description Intake Skills Alignment

## Objective

Rewrite app-description intake, bootstrap, change-impact, readiness, and generation-facing skills so they enforce the current AI-first workstream/surface/capability architecture from natural user input.

## Scope

Primary files include:

- `skills/app-description-bootstrap/SKILL.md`
- `skills/app-description-input-normalization/SKILL.md`
- `skills/app-description-intake-router/SKILL.md`
- `skills/app-description-change-impact/SKILL.md`
- `skills/app-description-readiness-assessment/SKILL.md`
- `skills/app-generate-app/SKILL.md`
- `skills/app-descriptions/SKILL.md`
- focused app-description companion skills when referenced stale language is found

## Ordered work areas

1. Fix minimum-starter language to five core workstream v0.
2. Replace preferred purchase-request references with AI-first seed/workstream references.
3. Add starter/basic/chatbot-like routing to five core v0.
4. Ensure impact/readiness/generation paths include workstreams, surfaces, managed-agent runtime, `55-ui`, and runtime validation.
5. Remove or rewrite stale page/CRUD/chatbot framing discovered in these skills.

## Acceptance criteria

- App-description skills cannot plausibly route broad generated-SaaS input directly to CRUD/page/component thinking.
- Minimum-starter scope is consistent with five core workstream v0.
- Purchase-request examples are mechanics-only where still referenced.
- `git diff --check` passes.
