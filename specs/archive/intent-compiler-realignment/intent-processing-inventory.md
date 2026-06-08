# Intent-Processing Inventory and Archive Strategy

## Scope and method

This inventory covers the skills-pack artifacts that currently process, route, normalize, capture, plan, queue, or realize user intent. It uses the candidate families named in `conversation-capture.md` plus a repository search of `skills-pack/skills/*/SKILL.md` and `skills-pack/docs/*.md` for intent/app-description/intake/requirements/planning references.

Focused Akka implementation skills are intentionally not listed unless they participate in high-level intent routing or generated-app planning. They should remain active and receive only reference updates if later tasks discover stale links.

## Recommended action labels

- **replace in place**: keep the public skill/doc path stable, but rewrite the artifact around the intent compiler model.
- **archive doc**: move or copy the legacy doc to a clearly inactive archive after replacements exist; update active references first.
- **deprecate marker**: leave a short active-path stub that points to the new canonical doc when path stability matters.
- **keep active / light realign**: preserve the artifact's main function and terminology, updating only references/phrasing needed for intent compiler consistency.
- **untouched**: no direct intent-processing migration expected.

## Primary skill inventory

| Artifact | Role in current pack | Recommendation | Rationale / follow-up task |
| --- | --- | --- | --- |
| `skills-pack/skills/app-description-input-normalization/SKILL.md` | Normalizes flexible input into app-description deltas. | replace in place | This should become the first-stage current-intent delta compiler. Realign in TASK-IC-03-001. |
| `skills-pack/skills/app-description-intake-router/SKILL.md` | Routes flexible input to app-description maintenance/generation skills. | replace in place | Should route increments by intent kind and affected app/domain/workstream/global artifacts. Realign in TASK-IC-03-001. |
| `skills-pack/skills/app-descriptions/SKILL.md` | Orchestrates app-description maintenance. | replace in place | Needs to become the app-description intent graph orchestrator, not a layer-stack router. Realign in TASK-IC-03-002. |
| `skills-pack/skills/app-description-bootstrap/SKILL.md` | Creates a starter app-description tree. | replace in place | Must create the new app/global/domains/workstreams graph. Realign in TASK-IC-03-002. |
| `skills-pack/skills/app-description-capability-modeling/SKILL.md` | Captures capability layer. | replace in place | Capabilities should live under domain/workstream traceability and bind governed tools/actions. Realign in TASK-IC-03-002. |
| `skills-pack/skills/app-description-behavior-specification/SKILL.md` | Captures behavior layer. | replace in place | Behavior should update current workstream/domain intent without historical clutter. Realign in TASK-IC-03-002. |
| `skills-pack/skills/app-description-auth-security/SKILL.md` | Captures auth/security layer. | replace in place | Security rules should bind from workstream access to surfaces/tools/endpoints/traces. Realign in TASK-IC-03-002. |
| `skills-pack/skills/app-description-observability/SKILL.md` | Captures observability layer. | replace in place | Trace/audit obligations should align with workstream trace bindings. Realign in TASK-IC-03-002. |
| `skills-pack/skills/app-description-test-specification/SKILL.md` | Captures acceptance/regression tests. | replace in place | Tests should be attached to workstream/domain artifacts and realization mappings. Realign in TASK-IC-03-002. |
| `skills-pack/skills/app-description-ui/SKILL.md` | Captures UI descriptions. | replace in place | UI should bind surfaces/routes/actions to workstreams and capabilities. Realign in TASK-IC-03-002. |
| `skills-pack/skills/app-description-functional-agent-modeling/SKILL.md` | Models functional/context-area agents. | keep active / light realign | Already workstream-centric; update references to canonical intent graph docs and binding language. TASK-IC-03-002. |
| `skills-pack/skills/app-description-surface-modeling/SKILL.md` | Models structured surfaces. | keep active / light realign | Already focused; update to global surface definition + workstream binding terminology. TASK-IC-03-002. |
| `skills-pack/skills/app-description-change-impact/SKILL.md` | Assesses affected app-description layers/output. | replace in place | Should analyze current-intent graph impact and localized realization scope. TASK-IC-03-002. |
| `skills-pack/skills/app-description-change-summary/SKILL.md` | Summarizes app-description changes. | keep active / light realign | Keep as report-only, but align summaries to intent graph and current-state artifacts. TASK-IC-03-002. |
| `skills-pack/skills/app-description-readiness-assessment/SKILL.md` | Assesses readiness for generation. | replace in place | Readiness gates should check app/domain/workstream/global graph completeness and traceability. TASK-IC-03-002. |
| `skills-pack/skills/app-description-readiness-summary/SKILL.md` | Summarizes readiness. | keep active / light realign | Keep report-only; point at new readiness criteria. TASK-IC-03-002. |
| `skills-pack/skills/app-generate-app/SKILL.md` | Realizes app-description as code/tests/runtime outputs. | replace in place | Must realize current intent graph to code and validate traceability/runtime behavior. TASK-IC-03-002 or TASK-IC-04-001 depending on canonical doc dependencies. |
| `skills-pack/skills/akka-prd-to-specs-backlog/SKILL.md` | Converts high-level PRD to specs/backlog/tasks. | replace in place | PRD should be treated as incremental source intent compiled to current intent + queue. TASK-IC-03-003. |
| `skills-pack/skills/akka-revised-prd-reconciliation/SKILL.md` | Reconciles revised PRDs with existing specs/tasks. | replace in place | Should reconcile revised input into current-intent deltas instead of parallel regenerated plans. TASK-IC-03-003. |
| `skills-pack/skills/akka-change-request-to-spec-update/SKILL.md` | Converts iterative change requests into specs/task updates. | replace in place | This is a core CII path; should update current intent and downstream tasks. TASK-IC-03-003. |
| `skills-pack/skills/akka-slice-spec-to-backlog/SKILL.md` | Turns slice specs into build backlogs. | keep active / light realign | Preserve queue materialization role; require traceability to current-intent graph. TASK-IC-03-003. |
| `skills-pack/skills/akka-backlog-to-pending-tasks/SKILL.md` | Creates/repairs pending-task queues. | keep active / light realign | Preserve queue contract; add current-intent graph provenance. TASK-IC-03-003. |
| `skills-pack/skills/akka-backlog-item-to-task-brief/SKILL.md` | Turns one backlog item into a task brief. | keep active / light realign | Preserve narrow task compiler role; require vertical traceability from current intent. TASK-IC-03-003. |
| `skills-pack/skills/akka-pending-question-generation/SKILL.md` | Generates pending questions from ambiguous intent/specs. | replace in place | Questions should block compilation of unsafe/ambiguous current-intent graph nodes. TASK-IC-03-003. |
| `skills-pack/skills/akka-pending-question-queue-maintenance/SKILL.md` | Maintains pending-question queue. | keep active / light realign | Preserve queue maintenance, align categories with current intent. TASK-IC-03-003. |
| `skills-pack/skills/akka-do-next-pending-question/SKILL.md` | Executes one pending question/reconciliation item. | keep active / light realign | Preserve execution semantics, add current-intent reconciliation language. TASK-IC-03-003. |
| `skills-pack/skills/akka-pending-task-queue-maintenance/SKILL.md` | Maintains pending-task queue. | keep active / light realign | Preserve queue maintenance, validate task provenance against current intent. TASK-IC-03-003. |
| `skills-pack/skills/akka-do-next-pending-task/SKILL.md` | Executes one pending task. | keep active / light realign | Preserve strict one-task execution; update references and current-intent vertical contract wording. TASK-IC-03-003. |
| `skills-pack/skills/project-discussed-idea-to-pending-project/SKILL.md` | Creates mini-project specs/pending tasks from discussed ideas. | keep active / light realign | Keep repository-maintenance utility; capture discussed idea as current mini-project intent. TASK-IC-03-003. |
| `skills-pack/skills/ai-first-saas/SKILL.md` | High-level AI-first SaaS interpretation/router. | keep active / light realign | Update router to hand broad product intent to the compiler model. TASK-IC-04-001. |
| `skills-pack/skills/agent-workstream-apps/SKILL.md` | Interprets apps as agent workstream applications. | keep active / light realign | Keep as domain doctrine router; ensure workstream binding terms match canonical docs. TASK-IC-04-001. |
| `skills-pack/skills/capability-first-backend/SKILL.md` | Models backend behavior as capabilities before Akka choices. | keep active / light realign | Preserve capability-first doctrine, connect capabilities to current-intent graph. TASK-IC-04-001. |
| `skills-pack/skills/core-saas-foundation/SKILL.md` | Applies mandatory SaaS foundation. | keep active / light realign | Preserve foundation guardrails, clarify how they seed global/workstream intent. TASK-IC-04-001. |
| `skills-pack/skills/akka-solution-decomposition/SKILL.md` | Decomposes requirements/specs into Akka components. | keep active / light realign | Keep Akka routing role; require current-intent/readiness inputs before component choice. TASK-IC-04-001. |
| `skills-pack/skills/ai-first-saas-ui-surfaces/SKILL.md` | Designs AI-first SaaS surfaces. | keep active / light realign | Update references to global surface definitions and workstream surface bindings. TASK-IC-04-001. |
| `skills-pack/skills/ai-first-saas-agent-team-design/SKILL.md` | Designs bounded agent teams. | keep active / light realign | Update to bind teams under workstreams/current-intent graph. TASK-IC-04-001. |
| `skills-pack/skills/ai-first-saas-object-model/SKILL.md` | Selects durable substrate objects. | keep active / light realign | Update traceability to current-intent graph and realization mappings. TASK-IC-04-001. |
| `skills-pack/skills/ai-first-saas-audit-trace/SKILL.md` | Designs audit/work traces. | keep active / light realign | Update trace bindings to global/workstream trace artifacts. TASK-IC-04-001. |
| `skills-pack/skills/ai-first-saas-outcomes-metrics/SKILL.md` | Designs outcome loops/metrics. | keep active / light realign | Update outcome links to current-intent tests/traces/outcomes. TASK-IC-04-001. |
| `skills-pack/skills/ai-first-saas-policy-governance/SKILL.md` | Models policies/approval gates. | keep active / light realign | Update policy artifacts to global definition + workstream binding. TASK-IC-04-001. |
| `skills-pack/skills/ai-first-saas-decision-cards/SKILL.md` | Designs decision/review cards. | keep active / light realign | Update as surface pattern/binding guidance. TASK-IC-04-001. |
| `skills-pack/skills/ai-first-saas-admin-agents/SKILL.md` | Applies AI-assisted admin offload. | keep active / light realign | Update to new workstream/current-intent terms if references drift. TASK-IC-04-001. |

## Primary document inventory

| Artifact | Role in current pack | Recommendation | Rationale / follow-up task |
| --- | --- | --- | --- |
| `skills-pack/docs/intent-compiler-working-note.md` | Temporary seed document. | replace with canonical docs, then archive or deprecate marker | It should not remain the active source of truth after TASK-IC-02-001. |
| `skills-pack/docs/description-first-application-doctrine.md` | Legacy description-first doctrine. | archive doc + deprecate marker if references need stability | Superseded by intent compiler/current-intent graph. Heavy active references must be updated before move. TASK-IC-02-002. |
| `skills-pack/docs/internal-app-description-architecture.md` | Legacy internal layer architecture. | archive doc + deprecate marker | Replace with app-description intent graph doc. Many app-description skills reference it, so update first. TASK-IC-02-001/TASK-IC-02-002. |
| `skills-pack/docs/app-description-maintenance-flow.md` | Legacy app-description update flow. | archive doc + deprecate marker | Replace with incremental intent capture/update flow. TASK-IC-02-002. |
| `skills-pack/docs/app-description-skill-output-contracts.md` | Shared output contracts for app-description skills. | replace in place | Active path is referenced deeply; rewrite around current-intent delta/report contracts. TASK-IC-02-001. |
| `skills-pack/docs/app-description-end-to-end-workflow-example.md` | Legacy workflow example. | archive doc or replace with new concise example | Avoid preserving old conceptual clutter. TASK-IC-02-002/TASK-IC-04-001. |
| `skills-pack/docs/intent-driven-usage-flow.md` | High-level usage flow. | replace in place or deprecate to new compiler overview | It is close to the new concept but likely redundant once canonical compiler docs exist. TASK-IC-02-001/TASK-IC-02-002. |
| `skills-pack/docs/requirements-to-workstream-development-process.md` | Canonical requirements-to-workstream process. | keep active / light realign | Already central and referenced broadly; align terminology to intent compiler/current-intent graph. TASK-IC-02-001/TASK-IC-04-001. |
| `skills-pack/docs/domain-workstream-prd-structure.md` | PRD/domain/workstream structure. | replace in place or deprecate to current-intent graph docs | PRD-specific framing should be subordinate to incremental intent. TASK-IC-02-002. |
| `skills-pack/docs/prd-to-akka-flow.md` | PRD-to-Akka process. | replace in place or deprecate to compiler realization flow | Should become intent/spec-to-Akka realization mapping, not PRD-only flow. TASK-IC-02-002. |
| `skills-pack/docs/planning-skill-output-contracts.md` | Shared planning/queue output contracts. | replace in place | Active path is referenced by planning skills; rewrite to preserve traceability from current intent graph. TASK-IC-02-001. |
| `skills-pack/docs/solution-plan-to-implementation-queue.md` | Solution plan to tasks. | keep active / light realign | Preserve queue materialization, make provenance current-intent graph explicit. TASK-IC-02-001. |
| `skills-pack/docs/pending-question-queue.md` | Pending-question queue contract. | keep active / light realign | Preserve queue contract; add question-to-intent-node blocking semantics. TASK-IC-02-001. |
| `skills-pack/docs/pending-task-queue.md` | Pending-task queue contract. | keep active / light realign | Preserve queue contract; add current-intent provenance and vertical traceability. TASK-IC-02-001. |
| `skills-pack/docs/workstream-contract.md` | Compact workstream contract. | keep active / light realign | Important active contract; align with global definition + workstream binding model. TASK-IC-02-001. |
| `skills-pack/docs/structured-surface-contracts.md` | Structured surface contract. | keep active / light realign | Keep active; add global surface pattern/workstream binding language. TASK-IC-02-001. |
| `skills-pack/docs/workstream-manifest-schema.md` | Machine-readable workstream index. | keep active / light realign | Likely becomes part of current-intent graph validation. TASK-IC-02-001. |
| `skills-pack/docs/workstream-attention-contracts.md` | Attention contract. | keep active / light realign | Keep active as workstream operational contract. TASK-IC-02-001. |
| `skills-pack/docs/workstream-expertise-model.md` | Agent expertise/reference-governance contract. | keep active / light realign | Keep active; align expertise artifacts to global/workstream bindings. TASK-IC-02-001. |
| `skills-pack/docs/agent-workstream-application-architecture.md` | Agent workstream app doctrine. | keep active / light realign | Keep as generated-app architecture below compiler docs. TASK-IC-04-001. |
| `skills-pack/docs/ai-first-saas-application-architecture.md` | AI-first SaaS doctrine/router context. | keep active / light realign | Keep as high-level doctrine; update routing paragraph to intent compiler. TASK-IC-04-001. |
| `skills-pack/docs/capability-first-backend-architecture.md` | Backend capability doctrine. | keep active / light realign | Keep active; connect capability ids to graph realization. TASK-IC-04-001. |
| `skills-pack/docs/core-ai-first-saas-foundation.md` | Foundation doctrine. | keep active / light realign | Keep active; clarify foundation as global/current-intent baseline. TASK-IC-04-001. |
| `skills-pack/docs/minimum-implementable-workstream-slice.md` | One-slice implementation scope. | keep active / light realign | Useful for task queues; ensure slice traceability to current intent. TASK-IC-04-001. |
| `skills-pack/docs/module-sprint-planning.md` | Module/sprint planning guidance. | keep active / light realign | Update if it references legacy PRD/description-first flow. TASK-IC-03-003/TASK-IC-04-001. |
| `skills-pack/docs/skills-pack-user-guide.md` | User-facing skills-pack overview. | keep active / light realign | Update high-level usage after canonical docs exist. TASK-IC-04-001. |
| `skills-pack/docs/skill-consolidation-and-pruning.md` | Historical pruning notes. | untouched or archive reference | Not an active intent compiler contract. |
| `skills-pack/docs/retired-content-boundaries.md` | Existing archive/retirement guidance. | keep active / light realign | Use/extend for archive mechanics. TASK-IC-02-002. |

## Canonical docs to create before archiving legacy docs

TASK-IC-02-001 should create a concise active doc set before any legacy path is moved. Recommended source paths:

1. `skills-pack/docs/intent-compiler.md` — overview: source language, current intent outputs, generated code outputs, trace chain.
2. `skills-pack/docs/current-intent-model.md` — app/global/domain/workstream graph, global definition plus workstream binding rules.
3. `skills-pack/docs/incremental-intent-processing.md` — classify/normalize/reconcile/dedupe user increments, current-state editing rules, pending question boundaries.
4. `skills-pack/docs/intent-to-realization-flow.md` — how current intent maps to specs/backlogs/tasks, Akka components, frontend, tests, runtime traces.
5. `skills-pack/docs/intent-compiler-skill-contracts.md` — shared output contracts for normalization, app-description capture, planning, readiness, generation, and review skills.

The later task may choose different filenames, but it should keep this split: compiler overview, current intent graph, incremental processing, realization, and skill contracts.

## Archive and replacement mechanics

1. **Do not move skill directories in this mini-project unless a later task explicitly proves install safety.** Skills are installed by directory name and may be referenced by users or harness metadata. Prefer in-place replacement of `SKILL.md` content.
2. **Docs may be archived only after active references are updated.** Use `rg '<doc-name>' skills-pack/skills skills-pack/docs specs` before moving a doc.
3. **Preferred archive path for legacy docs:** `skills-pack/docs/archive/intent-compiler-realignment/<original-file-name>`. The archive directory should include a short `README.md` explaining that archived files are historical reference, not active source of truth.
4. **Use deprecation stubs when a filename is heavily referenced or likely user-facing.** A stub should be short, state the doc is superseded, and link to canonical docs. It must not duplicate legacy doctrine.
5. **Keep installed-layout references valid.** Skill references should continue using `../docs/...` for active docs. If an archived doc remains referenced intentionally, it must still exist in installed payload or the reference checker must be updated; avoid this by removing active references to archived docs.
6. **Treat `skills-pack/docs/examples/**`, `skills-pack/examples/**`, and `skills-pack/templates/**` as follow-up migration surfaces.** Update them in TASK-IC-04-001 after canonical docs and skill rewrites exist.
7. **Validation sequence:** run `git diff --check` for doc-only tasks; run `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run` and then `--check` after reference-affecting doc/skill moves.

## Main risks and mitigations

- **Broken installed references:** mitigate with pre-move `rg`, deprecation stubs, and install check in migration tasks.
- **Moving public skill names:** avoid moving skill directories; rewrite in place.
- **Legacy concept leakage:** canonical docs should be new and concise; salvage details only when they fit current intent, not as historical chronology.
- **Over-broad focused skill migration:** keep Akka implementation skills out of scope except for stale reference wording.
- **Task sequencing drift:** complete canonical docs before archive, then update skills by family, then examples/templates, then terminal verification.

## Recommended task ordering confirmation

The existing queue order is safe:

1. Inventory/archive plan (this task).
2. Canonical docs.
3. Archive/retire legacy docs after references are clear.
4. Realign intake/normalization skills.
5. Realign app-description capture/review/generation skills.
6. Realign planning/queue skills.
7. Realign high-level routers/examples/templates.
8. Terminal install/reference validation.
