# Sprint 05 Review: Focused Realignment Cleanup

## Task

`TASK-AWSR-05-008`: review Sprint 05 focused cleanup and decide whether to close the agent-workstream skills realignment or create a targeted Sprint 06.

## Verdict

Sprint 05 closes the practical realignment gaps identified after the broad agent-workstream migration.

No blocking Sprint 06 is required. The skills pack is ready to dogfood starter implementation through `TASK-STARTER-08-001` in `specs/ai-first-saas-starter-app-template/pending-tasks.md`.

## Gap closure review

| Sprint 05 gap | Status | Evidence |
|---|---|---|
| Installed `.agents/` output was stale relative to source skills | Closed | `installed-pack-parity-check.md` records a forced project install, maintainer `AGENTS.md` restore, representative installed skill spot checks, and no tracked `.agents/` changes. |
| `core-saas-foundation` was too object/foundation-first | Closed | `skills/core-saas-foundation/SKILL.md` now routes through `agent-workstream-apps`, defines foundation functional agents and structured surfaces, and requires action-to-capability mapping before components. |
| Remaining focused skills lacked generated SaaS input-contract gates | Closed | High-use focused web UI, agent, endpoint, workflow, and view skills now include `Generated SaaS input contract` gates that block mechanics-first implementation when functional-agent/surface/capability/AuthContext/test contracts are absent. |
| Structured-surface and exposure-channel terminology was ambiguous | Closed | Top-level routing and doctrine distinguish `structured surface` for workstream renderables from `exposure channel/path` for HTTP/gRPC/MCP/tools/workflows/timers/consumers/views/internal calls. |
| AI-first companion skills lacked consistent surface/action handoffs | Closed | Policy, decision-card, audit/trace, admin-agent, and outcomes companion skills now require implementation-ready functional-agent, structured-surface, surface-action, capability, AuthContext, audit/trace, and downstream-skill handoffs. |
| Source skill relative paths were fragile | Closed | `source-skill-path-reference-audit.md` documents the path normalization and `tools/audit-source-skill-paths.py` currently reports `skill_files=150 checked_refs=1203 broken_refs=0`. |
| Starter acceptance docs conflicted with later Sprint 08 queue | Closed | Starter final acceptance and migration summary now acknowledge the later Sprint 08 workstream-first follow-up queue rather than implying no remaining starter implementation work. |

## Design checklist result

Reviewed against `docs/agent-workstream-design-review-checklist.md`:

- Functional/context-area agents are the generated SaaS default before pages, CRUD modules, or component families.
- `12-workstreams/` and `55-ui/` ownership remains separated in planning and description guidance.
- Structured surfaces are treated as typed workstream artifacts with payloads, actions, states, AuthContext, trace links, and rendering/action tests.
- Governed backend capabilities remain the authoritative contract behind surface actions, tools, workflows, APIs, timers, consumers, and internal calls.
- Routes, endpoints, agent tools, and Akka components are framed as exposure channels or horizontal implementation details, not product decomposition roots.
- Legacy mechanics references remain quarantined as implementation examples rather than generated SaaS architecture doctrine.

## Checks run for this review

```bash
git status --short
rg -n "agent-workstream-apps|functional agent|structured surface|Access/Profile|User Admin|Agent Admin|Audit/Trace|Governance/Policy" skills/core-saas-foundation/SKILL.md
rg -l "Generated SaaS input contract" <Sprint 05 focused-skill set> | wc -l
rg -n "structured surface|exposure channel|exposure path" docs/capability-first-backend-architecture.md docs/agent-workstream-application-architecture.md docs/structured-surface-contracts.md skills/akka-prd-to-specs-backlog/SKILL.md skills/akka-solution-decomposition/SKILL.md skills/capability-first-backend/SKILL.md
rg -n "workstream|structured surface|surface action|capability" skills/ai-first-saas-admin-agents/SKILL.md skills/ai-first-saas-audit-trace/SKILL.md skills/ai-first-saas-decision-cards/SKILL.md skills/ai-first-saas-outcomes-metrics/SKILL.md skills/ai-first-saas-policy-governance/SKILL.md
python3 tools/audit-source-skill-paths.py
git diff --check
```

Results:

- Core foundation workstream terms present.
- `Generated SaaS input contract` present in all 14 focused candidate skills checked.
- Terminology checks show structured-surface/exposure-channel distinction in top-level touched docs and skills.
- AI-first companion checks show workstream/surface/action/capability handoff terms in touched companion skills.
- Source path audit reports zero broken references.
- `git diff --check` passes for review changes.

## Follow-up decision

Do not create Sprint 06 now. Any future refinement should come from concrete starter dogfooding discoveries during Sprint 08 implementation, not from continuing the realignment project spec in the abstract.

## Next execution path

Proceed with starter implementation from:

- `specs/ai-first-saas-starter-app-template/pending-tasks.md`
- next runnable task: `TASK-STARTER-08-001`
