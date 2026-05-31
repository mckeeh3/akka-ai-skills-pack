# Content Inventory: Requirements Intake Alignment Cleanup

## Purpose

Inventory active intake, planning, app-description, web UI, and example guidance that shapes how the skills pack consumes broad user input. Classifications guide later rewrite/removal tasks; this file does not itself change active guidance.

## Classification key

- **keep** — already aligned enough; preserve as canonical or supporting doctrine.
- **focused rewrite** — mostly aligned, but contains scoped stale wording or references to fix.
- **heavy rewrite** — useful topic, but structure or examples still steer agents toward stale intake models.
- **remove** — stale active guidance with no clear current purpose; delete after reference checks.
- **demote-to-mechanics-only** — keep only as a narrow mechanics/reference example; active routing must not treat it as canonical target architecture.

## Canonical doctrine and current-aligned sources

| File | Classification | Rationale | Follow-up sprint |
|---|---:|---|---|
| `docs/ai-first-saas-application-architecture.md` | keep | Canonical top-level doctrine: mandatory secure AI-first SaaS, five-core minimum starter, agent workstream shell, managed-agent runtime, capability-first backend, and runtime completion semantics. Use as the alignment source for rewrites. | Reference only |
| `docs/requirements-to-workstream-development-process.md` | keep | Canonical broad-input process: requirements → secure foundation → workstreams → attention → dashboards → surfaces/actions → capabilities/APIs → Akka substrate → agents/AutonomousAgents → events/projections/traces → validation. | Reference only |
| `docs/agent-workstream-application-architecture.md` | keep | Canonical generated-app UI/application model and five-core workstream v0 starter. Use to correct page/chatbot/User-Admin-only drift. | Reference only |
| `docs/capability-first-backend-architecture.md` | keep | Canonical backend substrate doctrine; capabilities are authority contracts before endpoints, tools, components, timers, consumers, or browser actions. | Reference only |
| `docs/minimum-ai-first-saas-app.md` | keep | Canonical minimum/basic/starter/chatbot-like interpretation. Verify downstream skills cite it as five-core v0 rather than User Admin-only. | Sprint 02/03 reference |
| `docs/structured-surface-contracts.md` | keep | Supports workstream/surface/action semantics. Include as canonical UI/surface reference for later web UI cleanup. | Sprint 04 reference |
| `docs/workstream-ui-reference-architecture.md` | keep | Current frontend reference architecture for workstream shell and structured surfaces. Use to rebalance older UI docs. | Sprint 04 reference |
| `docs/agent-workstream-design-review-checklist.md` | keep | Review checklist for stale page/component drift. Use for criteria and final stale-content pass. | Sprint 01/05 reference |
| `docs/workstream-expertise-model.md` | keep | Current governed expert-bundle model for functional agents. Use when pending/task flows need skill/reference/runtime context. | Sprint 03 reference |

## App-description intake and generation skills

| File | Classification | Rationale | Follow-up sprint |
|---|---:|---|---|
| `skills/app-descriptions/SKILL.md` | focused rewrite | Top-level orchestration is generally aligned, but must be checked for generation/readiness routing that still prefers old examples or omits current five-core workstream/runtime UI obligations. | Sprint 02, task 02 |
| `skills/app-description-bootstrap/SKILL.md` | focused rewrite | High-priority drift: several lines describe the minimum starter as **User Admin workstream v0**. Rewrite to five core workstream v0 and keep purchase-request references mechanics-only. | Sprint 02, task 01 |
| `skills/app-description-input-normalization/SKILL.md` | focused rewrite | Workstream/attention/surface/capability extraction is strong, but high-priority review found purchase-request examples are preferred where the AI-first seed should be preferred. | Sprint 02, task 01 |
| `skills/app-description-intake-router/SKILL.md` | focused rewrite | Mostly aligned, but must explicitly route minimum/basic/starter/chatbot-like prompts to the five-core workstream v0 starter, not generic chat/page shells. | Sprint 02, task 01 |
| `skills/app-description-change-impact/SKILL.md` | focused rewrite | High-priority review found purchase-request example preference; adjust to prefer AI-first seed and preserve workstream/surface/capability impacts. | Sprint 02, task 01 |
| `skills/app-description-readiness-assessment/SKILL.md` | focused rewrite | Must ensure readiness fails when workstream UI, managed-agent runtime, five-core/full-core scope, security, or local runtime validation are missing. | Sprint 02, task 02 |
| `skills/app-generate-app/SKILL.md` | focused rewrite | Generation must require current workstream UI/runtime doctrine and not produce page/component-only scaffolds. | Sprint 02, task 02 |
| `skills/app-description-functional-agent-modeling/SKILL.md` | keep | Directly supports current `12-workstreams/functional-agents.md` model; review only if later searches find stale examples. | Sprint 05 review |
| `skills/app-description-surface-modeling/SKILL.md` | keep | Directly supports structured surfaces/actions/system-message surfaces; review only if later searches find stale examples. | Sprint 05 review |
| `skills/app-description-capability-modeling/SKILL.md` | keep | Directly supports capability-first description layer; review only if later searches find stale examples. | Sprint 05 review |
| `skills/app-description-ui/SKILL.md` | focused rewrite | Likely aligned, but UI cleanup should verify `55-ui/` stays browser-realization-only and does not reintroduce page-first ownership. | Sprint 04/05 review |
| `skills/app-description-auth-security/SKILL.md` | keep | Core auth/security layer should remain mandatory foundation-aligned. | Sprint 05 review |
| `skills/app-description-behavior-specification/SKILL.md` | keep | Behavior layer is supporting guidance; verify only if stale-term pass flags it. | Sprint 05 review |
| `skills/app-description-test-specification/SKILL.md` | keep | Test layer should preserve acceptance/regression semantics; verify only if stale-term pass flags it. | Sprint 05 review |
| `skills/app-description-observability/SKILL.md` | keep | Supports audit/trace/diagnosability; verify only if stale-term pass flags it. | Sprint 05 review |
| `skills/app-description-change-summary/SKILL.md` | keep | Summary-only skill; low intake risk. | Sprint 05 review |
| `skills/app-description-readiness-summary/SKILL.md` | keep | Summary-only skill; low intake risk after readiness assessment is aligned. | Sprint 05 review |

## PRD, decomposition, backlog, and queue skills

| File | Classification | Rationale | Follow-up sprint |
|---|---:|---|---|
| `skills/akka-solution-decomposition/SKILL.md` | focused rewrite | Strongly workstream/capability-first, but high-priority drift says minimum-starter plans are User Admin v0. Rewrite to five-core v0 and verify examples no longer prefer purchase-request as canonical. | Sprint 03, task 01 |
| `skills/akka-prd-to-specs-backlog/SKILL.md` | focused rewrite | Strong planning shape, but high-priority drift says minimum starter is User Admin v0. Rewrite to five-core v0 and ensure generated specs/backlogs/tasks preserve current vertical contracts. | Sprint 03, task 01 |
| `skills/akka-revised-prd-reconciliation/SKILL.md` | focused rewrite | Must preserve current app-description/spec state and block stale task shapes during revised PRD reconciliation. | Sprint 03, task 02 |
| `skills/akka-change-request-to-spec-update/SKILL.md` | focused rewrite | Must update authoritative meaning first and retain workstream/surface/capability context for small feature/fix/adjustment input. | Sprint 03, task 02 |
| `skills/akka-slice-spec-to-backlog/SKILL.md` | focused rewrite | Backlog generation should reject page/component-only slices and preserve vertical workstream contracts. | Sprint 03, task 02 |
| `skills/akka-backlog-to-pending-tasks/SKILL.md` | focused rewrite | Pending tasks must carry functional agent, attention, surface/action, capability, AuthContext, audit/trace, UI, and runtime validation context. | Sprint 03, task 02 |
| `skills/akka-backlog-item-to-task-brief/SKILL.md` | focused rewrite | Leaf task briefs must block vague module/page/component prompts and preserve vertical implementation contracts. | Sprint 03, task 02 |
| `skills/akka-pending-question-generation/SKILL.md` | focused rewrite | Questions should capture unresolved workstream/surface/capability/security/runtime decisions instead of allowing guesses. | Sprint 03, task 03 |
| `skills/akka-pending-question-queue-maintenance/SKILL.md` | focused rewrite | Queue repair should preserve blockers tied to current architecture semantics. | Sprint 03, task 03 |
| `skills/akka-pending-task-queue-maintenance/SKILL.md` | focused rewrite | Queue repair must detect stale CRUD/page/component-only tasks and require current vertical context. | Sprint 03, task 03 |
| `skills/akka-do-next-pending-question/SKILL.md` | focused rewrite | Execution flow should retain architecture context while resolving one question. | Sprint 03, task 03 |
| `skills/akka-do-next-pending-task/SKILL.md` | focused rewrite | Execution flow should load only listed reads/skills while preserving task-level workstream/surface/capability/runtime context. | Sprint 03, task 03 |

## Active usage, app-description, and process docs

| File | Classification | Rationale | Follow-up sprint |
|---|---:|---|---|
| `docs/intent-driven-usage-flow.md` | heavy rewrite | High-priority drift: says AI-first interpretation is "when applicable" and jumps to Akka component set too early. Rewrite around mandatory secure AI-first SaaS → workstream/surface/capability flow. | Sprint 04, task 01 |
| `docs/prd-to-akka-flow.md` | focused rewrite | Usage companion should mirror current requirements-to-workstream process and avoid direct component-first PRD flow. | Sprint 04, task 01 |
| `docs/app-description-end-to-end-workflow-example.md` | heavy rewrite | High-priority drift: purchase-request centered. Replace/supplement with current workstream/surface/capability example or mark purchase-request mechanics only. | Sprint 04, task 01 |
| `docs/app-description-skills-plan-backlog.md` | remove | High-priority drift: structurally stale plan/backlog doc. Prefer deletion after reference checks unless a future task identifies a small still-current mechanics section worth moving. | Sprint 04, task 02 |
| `docs/internal-app-description-architecture.md` | focused rewrite | Must verify `12-workstreams/` and `55-ui/` are first-class and current seed/starter references are preferred. | Sprint 04, task 02 or 05 |
| `docs/app-description-maintenance-flow.md` | focused rewrite | Must verify maintenance flow starts from workstream/surface/capability impacts for broad generated-SaaS input. | Sprint 04, task 02 or 05 |
| `docs/description-first-application-doctrine.md` | keep | Current doctrine likely remains useful; verify only if stale-term pass flags it. | Sprint 05 review |
| `docs/app-description-readiness-review-summary.md` | keep | Summary/reporting doc; low direct intake risk. | Sprint 05 review |
| `docs/app-description-realization-readiness.md` | focused rewrite | If active, it must require five-core/full-core scope clarity, workstream UI, managed agents, security, and runtime validation. | Sprint 05 review |

## Domain workstream and web UI docs

| File | Classification | Rationale | Follow-up sprint |
|---|---:|---|---|
| `docs/domain-workstream-prd-structure.md` | focused rewrite | Mostly aligned, but high-priority review found overuse of `user-list`, `user-edit`, search/detail, and form examples. Rebalance around workstream attention/surface/action contracts. | Sprint 04, task 03 |
| `docs/web-ui-api-contract-patterns.md` | heavy rewrite | High-priority drift: starts with `/api/<resource>` conventions. Rewrite to workstream/surface API envelopes and capability-backed shell requests first. | Sprint 04, task 03 |
| `docs/web-ui-style-guide.md` | focused rewrite | Contains current AI-first style doctrine but still has traditional navigation/dashboard/list/detail language; rebalance around workstream rail/shell/surface anatomy. | Sprint 04, task 03 |
| `docs/web-ui-ux-patterns.md` | focused rewrite | Generally aligned, but should emphasize agent workstream shell and surface requests before route/page navigation patterns. | Sprint 04, task 03 |
| `docs/web-ui-frontend-decomposition.md` | focused rewrite | Starts with current workstream reference architecture, but still mentions list/search/detail/edit and route/page details; keep those as implementation/deep-link mechanics only. | Sprint 04, task 03 |
| `docs/web-ui-pattern-selection.md` | focused rewrite | Verify generated SaaS pattern selection defaults to agent workstream shell, not generic page/UI pattern selection. | Sprint 05 review |
| `docs/web-ui-frontend-project-integration.md` | keep | Implementation integration reference; review only for stale active routing references. | Sprint 05 review |
| `docs/web-ui-quality-checklist.md` | keep | Quality checklist should remain, but final pass should verify workstream/surface/runtime items dominate. | Sprint 05 review |

## Examples and reference positioning

| File | Classification | Rationale | Follow-up sprint |
|---|---:|---|---|
| `docs/examples/README.md` | focused rewrite | Already labels purchase-request as mechanics-only and AI-first seed as preferred. Verify all example pointers match later removals/renames. | Sprint 04, task 04 |
| `docs/examples/ai-first-saas-seed-app-description/README.md` | keep | Preferred secure AI-first SaaS seed app-description reference. Keep as canonical description-layer example. | Reference only |
| `docs/examples/requirements-to-workstream-mini-example.md` | keep | Compact current process example; use as quick PRD/intake mechanics pattern. | Reference only |
| `docs/examples/ai-first-saas-core-app-domain/README.md` | keep | Current core domain reference; useful for generated SaaS core/domain semantics. | Reference only |
| `docs/examples/core-ai-first-saas-input/README.md` and `docs/examples/core-ai-first-saas-input/*.md` | keep | Current canonical source input set for core AI-first SaaS seed progression and modules. | Reference only |
| `docs/examples/ai-first-app-description-gaps.md` | keep | Useful gap catalog if still linked from readiness/review; verify in final pass. | Sprint 05 review |
| `docs/examples/purchase-request-app-description/README.md` | demote-to-mechanics-only | High-priority legacy example. Keep only if all active references clearly say mechanics/cross-linking reference, not target architecture. | Sprint 04, task 04 |
| `docs/examples/purchase-request-app-description/normalized-input-example.md` | demote-to-mechanics-only | Useful normalization mechanics, but not canonical target architecture. Prefer AI-first seed examples in active skills. | Sprint 04, task 04 |
| `docs/examples/purchase-request-prd.md` | demote-to-mechanics-only | Conventional PRD mechanics only; must not be first/primary example for generated SaaS planning. | Sprint 04, task 04 |
| `docs/examples/purchase-request-solution-plan.md` | demote-to-mechanics-only | Conventional solution-plan mechanics only; ensure decomposition skill does not prefer it as canonical. | Sprint 04, task 04 |
| `docs/examples/purchase-request-module-sprint-plan.md` | demote-to-mechanics-only | Conventional module/sprint mechanics only. | Sprint 04, task 04 |
| `docs/examples/purchase-request-pending-tasks.md` | demote-to-mechanics-only | Queue mechanics only; not current generated-app architecture. | Sprint 04, task 04 |
| `docs/examples/shopping-cart*` | demote-to-mechanics-only | No active files found under `docs/examples/` in this checkout, but any future shopping-cart references should be mechanics-only or removed from active intake guidance. | Sprint 04/05 review |

## Related package/reference surfaces to check after rewrites

| File or area | Classification | Rationale | Follow-up sprint |
|---|---:|---|---|
| `skills/README.md` | focused rewrite | Already contains much current routing, but final consistency pass should remove any stale links to deleted docs and verify minimum-starter language is five-core. | Sprint 05, task 02 |
| `pack/manifest.yaml` | keep | Check only when docs/skills/examples are removed or renamed. | Sprint 05, task 02 |
| `pack/AGENTS.md` | focused rewrite | Installed-pack guidance may need reference consistency after active guidance changes. | Sprint 05, task 02 |
| `templates/ai-first-saas-starter/**` | keep | Template is canonical implementation baseline; do not edit for this inventory task. Check docs links only if rewrites change references. | Sprint 05, task 02 |

## High-priority finding coverage proof

Every high-priority path from `conversation-capture.md` is represented above:

- `skills/app-description-bootstrap/SKILL.md`
- `skills/akka-prd-to-specs-backlog/SKILL.md`
- `skills/akka-solution-decomposition/SKILL.md`
- `skills/app-description-input-normalization/SKILL.md`
- `skills/app-description-change-impact/SKILL.md`
- `skills/app-description-intake-router/SKILL.md`
- `docs/intent-driven-usage-flow.md`
- `docs/app-description-skills-plan-backlog.md`
- `docs/app-description-end-to-end-workflow-example.md`
- `docs/domain-workstream-prd-structure.md`
- `docs/web-ui-api-contract-patterns.md`
- `docs/web-ui-style-guide.md`
- `docs/web-ui-ux-patterns.md`
- `docs/web-ui-frontend-decomposition.md`
- `docs/examples/README.md`
- `docs/examples/purchase-request-prd.md`
- `docs/examples/purchase-request-app-description/README.md`

## Notes for follow-up tasks

- Prefer rewriting/removal over archiving within active docs/skills.
- Keep legacy examples only when they are explicitly mechanics-only and not first-choice routing references.
- Later edits should preserve the canonical chain: secure SaaS foundation → functional agents/workstreams → attention/dashboard → structured surfaces/actions → governed capabilities → Akka substrate/exposure channels → runtime/API/UI validation.
- Do not include unrelated working-tree changes in RIAC commits.
