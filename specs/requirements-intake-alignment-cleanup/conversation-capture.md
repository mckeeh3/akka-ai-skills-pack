# Conversation Capture: Requirements Intake Alignment Cleanup

## User goals

- The skills pack has accumulated tech debt after many significant revisions.
- The highest-priority area is content that processes user input for adding new app features and functionality: PRDs, specs, feature requests, fixes, adjustments, app-description updates, backlog changes, and planning queues.
- Earlier content was biased toward conventional CRUD SaaS: traditional screens, navigation bars, search, help, dashboards, pages, and resource APIs.
- The current pack goal is a structured secure AI-first SaaS architecture based on the workstream shell, core workstreams, app-specific workstreams, typed workstream surfaces, and chat-like workstream UX backed by context-specific functional agents.
- The entire pack should be aligned with this way of consuming requirements and producing apps that strictly follow the defined architecture.
- Heavily unaligned legacy content should be removed or rewritten rather than merely archived, because the pack has become too large.
- Content that does not directly contribute to current pack goals should be revised, rewritten, or removed.
- The remediation should make as many passes through the content as needed until alignment is achieved.

## Review findings accepted as source input

The initial review found these high-priority upgrade areas:

- `skills/app-description-bootstrap/SKILL.md` still describes minimum starter as User Admin workstream v0 in several places; it must be five core workstream v0.
- `skills/akka-prd-to-specs-backlog/SKILL.md` and `skills/akka-solution-decomposition/SKILL.md` have the same minimum-starter drift.
- `skills/app-description-input-normalization/SKILL.md` and `skills/app-description-change-impact/SKILL.md` prefer purchase-request examples where AI-first seed examples should be preferred.
- `skills/app-description-intake-router/SKILL.md` should explicitly route minimum/basic/starter/chatbot-like prompts to the five core workstream v0 starter, not generic chat or page shells.
- `docs/intent-driven-usage-flow.md` still says AI-first interpretation is “when applicable” and jumps to Akka component set too early.
- `docs/app-description-skills-plan-backlog.md` is structurally stale and may need heavy rewrite or removal from active guidance.
- `docs/app-description-end-to-end-workflow-example.md` is purchase-request centered and should be replaced or supplemented with current workstream/surface/capability examples.
- `docs/domain-workstream-prd-structure.md` is mostly aligned but overuses `user-list`, `user-edit`, search/detail, and form examples.
- `docs/web-ui-api-contract-patterns.md` starts with `/api/<resource>` conventions rather than workstream/surface API envelopes.
- `docs/web-ui-style-guide.md`, `docs/web-ui-ux-patterns.md`, and `docs/web-ui-frontend-decomposition.md` still contain traditional navigation/dashboard/list/detail language that needs rebalancing.
- Purchase-request and shopping-cart examples should be mechanics-only references, not the canonical target architecture.

## Accepted constraints

- This is a repository-maintenance mini-project for the skills pack source repo.
- Do not treat repo-local specs as an end-user generated app plan.
- Preserve the canonical architecture doctrine in `docs/ai-first-saas-application-architecture.md`, `docs/agent-workstream-application-architecture.md`, `docs/requirements-to-workstream-development-process.md`, and `docs/capability-first-backend-architecture.md`.
- Prefer source reduction and clarity over preserving obsolete content.
- Do not remove content blindly; first classify active guidance as keep, rewrite, remove, or demote-to-mechanics-only.
- Each future task must be bounded enough for one fresh harness session.
- Verification must repeat until material stale active content is gone.

## Non-goals and rejected alternatives

- Do not create another broad abstract migration with no executable queue.
- Do not solve the issue by adding more front-door docs while leaving stale active docs in place.
- Do not archive heavily unaligned active content as a way to keep it in the installed guidance path.
- Do not make users learn internal skill taxonomy; natural language input should route correctly.

## Risks

- Removing docs may break references from skills, README, manifests, templates, or examples.
- Some legacy examples may still be useful as mechanics references and should be demoted rather than deleted if active skills need them for narrow mechanics.
- The repository already has related realignment specs; this project must avoid duplicating completed work and instead close current gaps.
- Whole-pack cleanup can grow too large; each task must stay focused and append follow-up tasks when needed.

## Unresolved questions

No user decision currently blocks the initial cleanup queue. Individual tasks may add targeted questions if they discover an active file whose ownership or removal status is ambiguous.
