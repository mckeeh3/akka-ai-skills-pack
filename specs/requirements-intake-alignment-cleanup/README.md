# Requirements Intake Alignment Cleanup

## Purpose

Bring all skills-pack content that consumes user input for new app features, PRDs, specs, fixes, adjustments, backlog updates, and app-description maintenance into strict alignment with the current secure AI-first SaaS architecture.

The target interpretation path is:

```text
natural user requirement
→ secure AI-first SaaS foundation
→ functional/context-area agents and workstreams
→ attention model and default/briefing surfaces
→ structured surfaces, system-message surfaces, and actions
→ governed backend capabilities
→ Akka substrate and exposure channels
→ full-stack local runtime/API/UI validation
```

The cleanup must remove, rewrite, or clearly demote legacy content that encourages CRUD-first SaaS, page/screen-first UI, generic chatbot shells, component-first planning, endpoint-first planning, or purchase-request mechanics as the canonical generated-app target.

## Background and trigger

The skills pack has gone through several major migrations. The current canonical goal is a highly structured, opinionated AI-first SaaS workstream architecture, but older intake and planning assets still leak previous assumptions: traditional screens, navigation bars, search/help pages, CRUD dashboards, generic pages, and User-Admin-only minimum starter language.

This mini-project captures the review findings from the current discussion and turns them into a durable, multi-pass remediation queue.

## Scope

In scope:

- Skills that ingest broad user input: PRDs, specs, feature requests, fixes, adjustments, revised PRDs, backlog changes, app-description changes, generation requests, readiness requests, and pending task/question flows.
- Docs that teach intent-driven usage, PRD-to-Akka flow, app-description maintenance, domain workstream PRD structure, web UI/API/UX patterns, and examples used by those skills.
- Example and reference positioning where legacy purchase-request or shopping-cart mechanics are treated as canonical target architecture.
- Trimming or deleting content that no longer contributes directly to the pack's current goals.
- Repeated search/review passes until stale CRUD/page/chatbot/component-first paths are either rewritten, removed, or explicitly labeled mechanics-only.

## Non-goals

- Do not implement an end-user Akka application.
- Do not weaken the current AI-first SaaS, workstream shell, secure foundation, managed-agent, capability-first, or runtime completion doctrines.
- Do not add the project-only planning skill to installable pack manifests or docs.
- Do not preserve heavily unaligned content merely by archiving it inside the active docs/skills tree. Prefer rewrite or removal; use provenance only when it is clearly outside installed-pack guidance.
- Do not touch unrelated uncommitted changes outside this mini-project unless a later task explicitly scopes them.

## Affected repository areas

Primary:

- `skills/app-description-*`
- `skills/app-descriptions/SKILL.md`
- `skills/app-generate-app/SKILL.md`
- `skills/akka-solution-decomposition/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `skills/akka-revised-prd-reconciliation/SKILL.md`
- `skills/akka-change-request-to-spec-update/SKILL.md`
- `skills/akka-slice-spec-to-backlog/SKILL.md`
- `skills/akka-backlog-*`
- `skills/akka-pending-*`
- `skills/akka-do-next-pending-*`
- `docs/intent-driven-usage-flow.md`
- `docs/prd-to-akka-flow.md`
- `docs/app-description-*.md`
- `docs/domain-workstream-prd-structure.md`
- `docs/web-ui-*.md`
- `docs/examples/**`
- `skills/README.md` only if follow-up review finds stale routing remains there

Secondary:

- `pack/` and manifest/resource packaging if active installed-pack content needs removal or path updates.
- `templates/` only if docs point to obsolete template behavior.

## Execution model

- Execute one queue task per fresh harness session.
- Read this README, `conversation-capture.md`, the selected sprint, matching backlog, selected queue entry, and task brief before editing.
- Each implementation/review task must update `pending-tasks.md` and make one focused commit when complete.
- Each pass may append follow-up tasks if it finds material stale content.
- The terminal verification task must compare the completed work against the done state and append more bounded tasks before a new terminal verification task when gaps remain.

## Sprint sequence

1. `sprints/01-inventory-and-prune-sprint.md` — create a repo inventory of intake/planning docs and skills, classify each as keep/rewrite/remove/demote, and define the deletion/rewrite criteria.
2. `sprints/02-description-intake-skills-sprint.md` — rewrite app-description intake/bootstrap/change-impact/readiness/generation skills so natural input always routes through five-core starter/full-core scope, workstreams, surfaces, capabilities, and UI/runtime validation.
3. `sprints/03-prd-spec-backlog-sprint.md` — rewrite PRD/spec/backlog/revised-change/pending-task flows so planning queues preserve workstream/surface/capability context and cannot create CRUD/page/component-only tasks.
4. `sprints/04-docs-examples-ui-sprint.md` — rewrite or remove stale docs/examples/UI guidance that still presents traditional screens, resource APIs, or purchase-request mechanics as canonical.
5. `sprints/05-trim-and-repeat-review-sprint.md` — run whole-pack search/review passes, remove/rewrite remaining stale active content, and verify package/installable guidance consistency.

## Done state

This mini-project is complete only when:

- every broad input path for PRDs, specs, feature requests, fixes, adjustments, revised PRDs, app-description changes, generation, readiness, pending questions, and pending tasks begins from secure AI-first SaaS + workstream/surface/capability doctrine;
- minimum/starter/basic/chatbot-like generated SaaS requests consistently mean the five core workstream v0 starter, not User Admin-only or a generic chatbot;
- legacy purchase-request/shopping-cart/page/static examples are either rewritten, removed from active guidance, or explicitly labeled mechanics-only and no longer preferred by intake skills;
- active docs no longer teach page trees, CRUD screens, route/resource APIs, nav bars, or component lists as the primary generated-app decomposition;
- app-description guidance treats `12-workstreams/` and `55-ui/` as first-class for generated full-stack SaaS;
- planning and queue skills preserve functional-agent, attention, surface/action, capability, AuthContext, audit/trace, UI, and runtime validation context in tasks;
- stale or duplicate active docs that do not contribute directly to the current pack goals are removed or rewritten, not merely archived;
- final verification searches for stale terms and inspects all files touched or classified as active intake/planning guidance;
- no installable-pack manifest or README points to removed files;
- `git diff --check` and relevant docs/package consistency checks pass.

## Open concerns

- Some existing specs may overlap this work, especially `specs/agent-workstream-skills-realignment/` and `specs/five-core-workstream-v0-starter/`. This mini-project should reuse their conclusions but focus specifically on user-input intake, content pruning, and repeated whole-content cleanup.
- Existing unrelated working-tree changes must not be included in this mini-project's commits.
