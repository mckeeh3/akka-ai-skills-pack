# DCA App Description Refresh Migration Plan

Status: **completed**. See `migration-completion-summary.md` for closeout details.

This planning package coordinated a multi-sprint refresh of `docs/examples/ai-first-dca-app-description/` so it remains useful under the current secure AI-first SaaS and capability-first backend direction.

The DCA example should remain a **vertical domain reference** for office-device/DCA lifecycle automation. It should not replace the canonical secure SaaS seed reference in `docs/examples/ai-first-saas-seed-app-description/`.

## Target positioning

Future agents should now interpret the DCA example as:

```text
canonical secure AI-first SaaS seed baseline
→ DCA/domain-specific vertical extension
→ governed capabilities for lifecycle, telemetry, supplies, service, billing, onboarding, offboarding, policy, audit, and outcomes
→ non-runnable app-description reference unless a later task explicitly realizes slices
```

The migration preserved the example's strengths: domain-rich lifecycle modeling, supplies autopilot as the first vertical slice, bounded agents, policy gates, decision cards, supervision UI, traces, outcomes, and Akka realization slicing.

## Execution model

This migration is closed. Historical execution rules were:

- Execute one task per fresh harness context.
- Use `pending-tasks.md` as the durable queue.
- Select the first `pending` task whose dependencies are satisfied.
- Do not edit files outside the selected task scope.
- Each task should end with a git commit containing only that task's intended changes and its `pending-tasks.md` status update.
- Record the commit hash in task notes when practical.
- Preserve task IDs; supersede obsolete tasks instead of renumbering or deleting them.

## Read order for future sessions

1. `AGENTS.md`
2. `skills/README.md`
3. `docs/ai-first-saas-application-architecture.md`
4. `docs/capability-first-backend-architecture.md`
5. `docs/internal-app-description-architecture.md`
6. `docs/app-description-maintenance-flow.md`
7. `docs/examples/ai-first-saas-seed-app-description/README.md`
8. `docs/examples/ai-first-dca-app-description/README.md`
9. This file
10. The relevant sprint spec under `sprints/`
11. The matching backlog under `backlog/`
12. The selected task entry in `pending-tasks.md`
13. The task brief under `tasks/` when present

## Sprint sequence

1. `sprints/01-positioning-and-structure-sprint.md` — reframe DCA as a vertical extension, remove stale wording, and add current control files.
2. `sprints/02-secure-saas-foundation-alignment-sprint.md` — add first-class secure SaaS foundation capability and align auth/security with current baseline.
3. `sprints/03-capability-first-contracts-sprint.md` — refactor DCA capabilities into governed backend capability contracts and update traceability.
4. `sprints/04-tests-ui-observability-readiness-sprint.md` — replace test placeholders, reconcile UI/style guidance, strengthen observability, and update readiness.
5. `sprints/05-final-consistency-and-realization-prep-sprint.md` — review for coherence, stale content, links, and future executable-slice readiness.

## Backlog alignment

Each sprint has a matching backlog:

- `backlog/01-positioning-and-structure-build-backlog.md`
- `backlog/02-secure-saas-foundation-alignment-build-backlog.md`
- `backlog/03-capability-first-contracts-build-backlog.md`
- `backlog/04-tests-ui-observability-readiness-build-backlog.md`
- `backlog/05-final-consistency-and-realization-prep-build-backlog.md`

## Migration principles

- Keep `ai-first-saas-seed-app-description` as the canonical secure SaaS baseline.
- Keep DCA as a vertical domain example, not a new canonical template.
- Do not make the DCA example runnable unless a later realization sprint explicitly asks for that.
- Preserve domain-specific AI-first operating-model richness.
- Make secure SaaS foundation semantics explicit before DCA-specific automation.
- Model backend behavior as governed capabilities before Akka component choices.
- Tests are part of the app description, not placeholders.
- UI must remain supervision/decision/governance/audit/outcome-oriented, not CRUD-first.

## Done state

Complete. The DCA example is current enough that future agents can use it as a high-quality vertical reference without copying outdated structure, skipping secure SaaS foundation requirements, or treating component choices/agent tools as the root backend design.

No additional refresh work is planned for this migration package.
