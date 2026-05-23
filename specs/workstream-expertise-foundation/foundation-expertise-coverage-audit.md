# Foundation Expertise Coverage Audit

## Scope

This audit inventories the foundation functional agents in the secure AI-first SaaS seed app-description and records whether each has an authoritative workstream expert bundle under:

```text
docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/
```

The audit is intentionally limited to coverage status. It does not author new bundles.

## Source evidence

Reviewed sources:

- `docs/workstream-expertise-model.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/functional-agents.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/README.md`
- `docs/examples/ai-first-saas-seed-app-description/app-description/12-workstreams/workstream-expertise/user-admin-agent.md`
- `specs/workstream-expertise-foundation/post-completion-expertise-review.md`
- `specs/workstream-expertise-foundation/sprints/07-foundation-expertise-expansion-sprint.md`

Current expertise artifacts found:

- `workstream-expertise/README.md`
- `workstream-expertise/user-admin-agent.md`

## Coverage summary

| Functional agent | Foundation role | Current expertise status | Readiness impact | Recommended next action |
|---|---|---|---|---|
| `my-account-agent` | Current account, selected context, profile, settings, sign out, and safe self-service. | No bundle. No explicit non-LLM status or readiness deferral found. | Not expertise-ready if LLM/composer behavior is in scope; could be declared surface-only only if that narrower scope is recorded explicitly. | In TASK-WEF-07-004, add a minimal bundle or an explicit surface-only/non-LLM deferral with readiness impact. |
| `user-admin-agent` | Invitations, users, memberships, roles/capabilities, access review, support-access visibility, and admin audit. | Detailed bundle exists at `workstream-expertise/user-admin-agent.md`. | Expertise-ready at description level; downstream realization still depends on seed/runtime/test implementation staying aligned. | Keep as canonical detailed example; use as structure reference for other bundles. |
| `agent-admin-agent` | Govern AgentDefinition records, prompts, skills, manifests, tool boundaries, lifecycle, behavior proposals, tests, and traces. | No bundle. No explicit deferral found. | Blocking gap for full-core readiness because `functional-agents.md` states full core SaaS scope requires `user-admin-agent` and `agent-admin-agent`. | In TASK-WEF-07-002, add a concrete `workstream-expertise/agent-admin-agent.md` bundle. |
| `mission-control-agent` | Supervise active goals, plans, delegated work, exceptions, approvals, and outcome signals. | No bundle. No explicit deferral found. | Not expertise-ready if included in full-core or app-specific realization; scope may be deferred only with explicit readiness impact. | In TASK-WEF-07-004, add an initial bundle or explicit deferral that prevents silent readiness claims. |
| `governance-policy-agent` | Manage policies, approval gates, proposals, simulations, replay evidence, and activation/rollback. | No bundle. No explicit deferral found. | Blocking or high-risk gap when governance/policy management is in scope; prompt-only governance must not be treated as ready. | In TASK-WEF-07-004, add an initial bundle or explicit readiness-impacting deferral. |
| `audit-trace-agent` | Search and explain identity, authorization, data access, tool use, decisions, workflows, and outcomes. | No bundle. No explicit deferral found. | Blocking or high-risk gap for trace/audit readiness because this agent explains evidence, redaction, tenant/customer filtering, support access, and export denials. | In TASK-WEF-07-003, add a concrete `workstream-expertise/audit-trace-agent.md` bundle. |

## Detailed findings

### `my-account-agent`

- Cataloged in `functional-agents.md` as a foundation functional agent for own-account, selected-context, profile/settings, sign-out, and safe self-service work.
- Callable capability families are `secure-tenant-user-foundation` and `frontend-shell-integration-patterns`.
- Current trace/test obligations cover `/api/me`, context selection, disabled-user denial, profile/settings audit where consequential, and request/response surface rendering.
- No `workstream-expertise/my-account-agent.md` exists.
- No explicit non-LLM/simple surface-only status is recorded.

Audit classification: **needs bundle or explicit non-LLM/surface-only deferral**.

If this remains LLM-enabled, its bundle should cover own-account safe self-service, context selection explanation, profile/settings changes, sign-out limitations, privacy boundaries, `/api/me` evidence, and denial behavior. If it is intentionally not LLM-backed, record that status in `functional-agents.md` and the expertise directory so readiness does not expect skill/reference manifests for this agent.

### `user-admin-agent`

- Cataloged in `functional-agents.md` as the canonical User Admin vertical.
- Authoritative bundle exists at `workstream-expertise/user-admin-agent.md`.
- Bundle covers prompt intent, governed procedural skills, governed reference documents, compact manifest behavior, `readSkill(skillId)`, `readReferenceDoc(referenceId)`, `ToolPermissionBoundary`, capability/tool boundaries, denials, surfaces, traces, seed/upgrade policy, and tests.

Audit classification: **detailed bundle exists**.

This is the only currently materialized detailed foundation expert bundle and should be used as the pattern for Agent Admin and Audit/Trace.

### `agent-admin-agent`

- Cataloged in `functional-agents.md` for governed runtime agent administration: `AgentDefinition`, prompts, skills, manifests, tool boundaries, lifecycle, behavior proposals, tests, and traces.
- Callable capability families are `managed-agent-foundation` and `governance-decisions-audit`.
- Current trace/test obligations include active/draft lifecycle, `readSkill(skillId)` authorization, and prompt/skill/tool-boundary trace coverage.
- No `workstream-expertise/agent-admin-agent.md` exists.
- No explicit deferral is recorded.
- `functional-agents.md` states full core SaaS scope requires `user-admin-agent` and `agent-admin-agent`.

Audit classification: **needs detailed bundle; blocking for full-core readiness**.

The bundle should cover governed behavior artifacts, prompt/skill/reference/manifest/boundary proposals, seed upgrades, approval and rollback paths, authority-expansion denials, trace visibility, and tests.

### `mission-control-agent`

- Cataloged in `functional-agents.md` for supervision of active goals, plans, delegated work, exceptions, approvals, and outcome signals.
- Callable capability families are `ai-first-work-management` and `governance-decisions-audit`.
- Current trace/test obligations include workstream timeline rendering, approval queue, policy-triggered exception, and outcome link tests.
- No `workstream-expertise/mission-control-agent.md` exists.
- No explicit deferral is recorded.

Audit classification: **needs initial bundle or explicit deferral**.

If in scope, this bundle should cover supervision expertise, exception triage, approval queue explanation, outcome evidence, escalation rules, and no autonomous override of retained human authority. If deferred, the app-description must state that mission-control readiness is out of full-core scope for the selected realization.

### `governance-policy-agent`

- Cataloged in `functional-agents.md` for policies, approval gates, proposals, simulations, replay evidence, and activation/rollback.
- Callable capability families are `governance-decisions-audit` and `managed-agent-foundation`.
- Current trace/test obligations include proposal approval, unauthorized authority expansion denial, and simulation/replay trace tests.
- No `workstream-expertise/governance-policy-agent.md` exists.
- No explicit deferral is recorded.

Audit classification: **needs initial bundle or explicit deferral**.

If in scope, this bundle should cover policy-clause interpretation, approval-gate proposal behavior, replay/simulation evidence, activation/rollback boundaries, denials for authority expansion, and tests proving policy text cannot grant backend authority.

### `audit-trace-agent`

- Cataloged in `functional-agents.md` for searching and explaining identity, authorization, data access, tool use, decisions, workflows, and outcomes.
- Callable capability families are `governance-decisions-audit`, `secure-tenant-user-foundation`, and `managed-agent-foundation`.
- Current trace/test obligations include redaction, tenant/customer filtering, trace correlation, and export-denial tests.
- No `workstream-expertise/audit-trace-agent.md` exists.
- No explicit deferral is recorded.

Audit classification: **needs detailed bundle; high-priority readiness gap**.

The bundle should cover trace search/explanation, evidence citation, redaction-preserving summaries, export limits, support-access audit constraints, denied skill/reference/tool-load traces, and tests.

## Readiness conclusion

Foundation workstream expertise coverage is currently **partial**:

- detailed bundle exists: `user-admin-agent`
- detailed bundle needed next: `agent-admin-agent`, `audit-trace-agent`
- bundle or explicit deferral/non-LLM status needed: `my-account-agent`, `mission-control-agent`, `governance-policy-agent`

Full-core readiness must not be claimed from User Admin expertise alone. Every LLM-enabled foundation functional agent must either gain a workstream expert bundle or be explicitly deferred with scope/readiness impact.

## Follow-up task mapping

- TASK-WEF-07-002 should add `workstream-expertise/agent-admin-agent.md`.
- TASK-WEF-07-003 should add `workstream-expertise/audit-trace-agent.md`.
- TASK-WEF-07-004 should add initial bundles or explicit deferrals/non-LLM status for `my-account-agent`, `mission-control-agent`, and `governance-policy-agent`.
- TASK-WEF-07-005 should update traceability, readiness, and tests, then verify every foundation functional agent has a bundle or explicit deferral/non-LLM status.
