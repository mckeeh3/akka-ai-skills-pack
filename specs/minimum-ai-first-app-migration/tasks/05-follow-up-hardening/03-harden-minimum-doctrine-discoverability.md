# Task: Harden minimum doctrine and markdown_response discoverability

## Objective

Make `markdown_response` and the minimum app doctrine discoverable from the primary workstream/surface/foundation routing paths.

## Scope

- Add `markdown_response` to the canonical surface type list in `docs/agent-workstream-application-architecture.md`.
- Add `../../docs/minimum-ai-first-saas-app.md` to required-reading lists in `skills/agent-workstream-apps/SKILL.md` and `skills/core-saas-foundation/SKILL.md`, with wording that it is required when the task is minimum/starter/basic/chatbot-like generated SaaS.
- Consider adding the minimum doctrine to any other top-level required-read list that routes such prompts and currently only references it later in the body.
- Do not duplicate the full doctrine text in every skill.

## Required reads

- `specs/minimum-ai-first-app-migration/post-completion-objectives-review.md`
- `docs/minimum-ai-first-saas-app.md`
- `docs/agent-workstream-application-architecture.md`
- `skills/agent-workstream-apps/SKILL.md`
- `skills/core-saas-foundation/SKILL.md`
- `skills/ai-first-saas/SKILL.md`

## Required checks

- `git diff --check`
- `rg -n "markdown_response|minimum-ai-first-saas-app" docs/agent-workstream-application-architecture.md skills/agent-workstream-apps/SKILL.md skills/core-saas-foundation/SKILL.md skills/ai-first-saas/SKILL.md`

## Acceptance

- A future agent scanning canonical surface types sees `markdown_response`.
- Skills that route minimum/starter prompts load the minimum doctrine before applying it.
- Task changes and queue update are committed.
