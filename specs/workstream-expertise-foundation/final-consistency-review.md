# Final Consistency Review: Workstream Expertise Foundation

## Review outcome

Sprint 05 final review found the workstream expertise foundation broadly consistent: canonical docs, routing, readiness, planning, seed example, and testing guidance now require functional agents with LLM behavior to have governed workstream expert bundles or explicit readiness-blocking deferrals.

The plan should not be closed yet. A small final cleanup task remains because several older full-core foundation summaries still name prompts, skills, manifests, boundaries, and traces without the newer reference-document terms. These are not prompt-only or generic-chatbot bypasses, but they can create partial implementation tasks that omit `ReferenceDocument`, `AgentReferenceManifest`, `readReferenceDoc`, and `ReferenceLoadTrace` unless later guidance corrects them.

## Evidence reviewed

Required files reviewed:

- `specs/workstream-expertise-foundation/README.md`
- `docs/workstream-expertise-model.md`
- `skills/README.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/agent-coverage-matrix.md`
- `skills/akka-agents/SKILL.md`
- `skills/agent-workstream-apps/SKILL.md`
- `skills/app-description-readiness-assessment/SKILL.md`
- `specs/workstream-expertise-foundation/sprints/05-review-hardening-sprint.md`
- `specs/workstream-expertise-foundation/backlog/05-review-hardening-build-backlog.md`

Searches run:

```text
rg -n "prompt-only|generic chatbot|chatbot|chat bot|bolt-on|page-first|CRUD-first|functional agent.*ready|workstream.*ready|ready.*functional agent|prompt.*enough|prompt.*only|declare.*ready|declared ready" skills docs specs templates pack --glob '!specs/workstream-expertise-foundation/final-consistency-review.md'

rg -n "ready.*(prompt|chat|tool list)|complete.*(prompt|chat|tool list)|functional agent.*(prompt|chat surface|tools).*ready|prompt-only.*ready|without.*expertise" docs skills templates --glob '!**/archive/**'

rg -n "akka-agent-reference-governance|ReferenceDocument|AgentReferenceManifest|readReferenceDoc|ReferenceLoadTrace" skills/README.md skills/akka-agents/SKILL.md skills/agent-workstream-apps/SKILL.md skills/app-description-readiness-assessment/SKILL.md docs/agent-workstream-application-architecture.md docs/agent-coverage-matrix.md docs/workstream-expertise-model.md
```

## Consistency matrix

| Area | Result | Evidence | Remaining concern |
|---|---|---|---|
| Canonical doctrine | Pass | `docs/workstream-expertise-model.md` defines workstream expert bundle, skill/reference/capability/tool/surface distinctions, compact manifest loading, governance rules, readiness checklist, tests, and routing to `akka-agent-reference-governance`. | None blocking. |
| Agent workstream architecture | Pass | `docs/agent-workstream-application-architecture.md` says each LLM-backed functional agent needs a workstream expert bundle or explicit deferral covering skills, references, manifests, loader authorization, tool boundaries, traces, and tests. | None blocking. |
| Top-level routing | Pass | `skills/README.md` includes `ReferenceDocument`/`ReferenceVersion`, `AgentReferenceManifest`, `readReferenceDoc`, `ReferenceLoadTrace`, reference-governance routing, and compact expertise manifest language. | None blocking. |
| Agent skill routing | Pass with minor downstream cleanup | `skills/akka-agents/SKILL.md` routes broad generated-app work through AI-first/workstream/capability paths and includes agent governance matrices. | Some older companion/foundation summaries outside the required set still mention skill manifests only. |
| Workstream app routing | Pass | `skills/agent-workstream-apps/SKILL.md` rejects generic chatbot/page-first defaults and requires prompt intent, governed documents, skills, tools, tool boundaries, capabilities, traces, and tests for functional agents. | Could mention reference documents more explicitly in a future polish, but readiness and doctrine cover it. |
| Readiness assessment | Pass | `skills/app-description-readiness-assessment/SKILL.md` blocks full-core readiness without per-functional-agent workstream expert bundles with skills, reference documents, manifests, boundaries, traces, and tests. | None blocking. |
| Coverage/testing visibility | Pass with known executable gap | `docs/agent-coverage-matrix.md` routes reference-governance work and lists assigned/unassigned `readReferenceDoc`, `ReferenceLoadTrace`, `read_reference` boundary-denial, redaction, and text-cannot-grant-authority tests. | It still records no executable first-class `ReferenceDocument` example; this is a known coverage backlog item, not a routing bypass. |
| Seed example | Pass | Prior Sprint 03 artifacts made User Admin expertise concrete with seed content and contract tests. | No review blocker. |
| Planning integration | Pass | Sprint 04 review proved generated plans can split expertise work into bundle, seed, loader/boundary, UI/governance, and test tasks. | No review blocker. |
| Generic-chatbot / page-first drift | Pass | Remaining `chatbot`, `page-first`, `CRUD-first`, and `bolt-on` matches are anti-pattern warnings, minimum-starter routing to User Admin workstream v0, migration records, or legacy/mechanics labels. | None blocking. |
| Prompt-only readiness paths | Needs small cleanup | Searches found no positive guidance saying prompt-only/chat/tool-list agents are ready. | Several older summaries list governed runtime agent foundation without reference-document artifacts, which can under-specify expert bundles. |

## Cleanup candidates for TASK-WEF-05-002

Apply the smallest edits to align older full-core/foundation summaries with the completed model:

1. `skills/akka-solution-decomposition/SKILL.md`
   - Lines around the scope gate and implementation order still list `AgentDefinition`, prompts, skills, `AgentSkillManifest`, `ToolPermissionBoundary`, prompt/skill/work traces, and `readSkill`, but omit `ReferenceDocument`/`ReferenceVersion`, `AgentReferenceManifest`, `readReferenceDoc`, and `ReferenceLoadTrace` in several places.
2. `skills/core-saas-foundation/SKILL.md`
   - Several foundation bullets still describe `AgentSkillManifest`, `readSkill`, `SkillLoadTrace`, and `AgentWorkTrace` without the parallel reference-document path. Add reference terms where the bullet is describing the full governed runtime foundation, while preserving minimum-starter deferral language.
3. `skills/app-description-bootstrap/SKILL.md`
   - The `full core` definition mentions prompts, skills, manifests, tool boundaries, traces, and `readSkill`; update it to include reference documents, reference manifests, `readReferenceDoc`, and `ReferenceLoadTrace`.
4. `skills/app-generate-app/SKILL.md`
   - Generation guardrails and output-order bullets still omit reference documents/loaders/traces in the managed-agent foundation summary. Add the reference artifacts so generation does not proceed from skill-only managed-agent semantics.
5. Optional minor polish: `skills/agent-workstream-apps/SKILL.md`
   - In the functional-agent inventory bullet, change "governed documents, skills, tools" to explicitly include reference documents/manifests when LLM expertise is involved. This is a clarity improvement, not a blocker.

## Recommendation

Proceed to `TASK-WEF-05-002` for a small final cleanup, not a new sprint. The cleanup should update the stale skill-only summaries above and then mark the workstream expertise foundation plan complete if no new material gaps appear.

Do not add a broad new sprint unless the cleanup uncovers executable coverage work that the project wants to prioritize immediately. The only known executable gap is already tracked in the agent coverage matrix: first-class runtime `ReferenceDocument` / `AgentReferenceManifest` executable examples and tests.

## Check results

- `git diff --check`: pending in this task until queue update.
- Repository text search: completed; no prompt-only, generic-chatbot, page-first, or CRUD-first readiness path found that bypasses workstream expertise. Remaining matches are anti-patterns, minimum-starter routing, legacy/migration notes, or the small skill-only summary omissions listed above.
