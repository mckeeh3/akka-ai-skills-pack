# Starter Queue Gap Matrix: Workstream / Surface / Capability Alignment

## Scope

Audit for `TASK-AWSR-04-001`. Reviewed the current starter app queue and Sprint 07 fullstack gap-closure artifacts:

- `specs/ai-first-saas-starter-app-template/pending-tasks.md`
- `specs/ai-first-saas-starter-app-template/sprints/07-fullstack-gap-closure-sprint.md`
- `specs/ai-first-saas-starter-app-template/backlog/07-fullstack-gap-closure-build-backlog.md`
- `specs/ai-first-saas-starter-app-template/tasks/07-fullstack-gap-closure/**`
- `docs/agent-workstream-design-review-checklist.md`

Canonical audit chain:

```text
secure SaaS foundation
→ functional/context-area agents
→ durable workstreams
→ typed structured surfaces/actions
→ governed backend capabilities
→ horizontal Akka/frontend/test implementation
```

## Summary

The starter queue successfully delivered a scaffoldable fullstack baseline, but many executable queue items were written before the realignment made the workstream/surface/capability contract mandatory. The strongest aligned tasks are the early workstream API/UI tasks and final validation tasks. The weakest tasks are broad component/mechanics slices such as durable identity, durable agent governance, concrete admin APIs, and adapter/frontend polish tasks that do not name the owning functional agents, surface ids/actions, capability ids/classes, AuthContext rules, trace obligations, or rendering/API parity tests.

Important queue-history note: most target tasks are already `done`. Sprint 04 should not rewrite history destructively. `TASK-AWSR-04-002` should either:

1. add a new starter follow-up queue/sprint with workstream-first replacement tasks, or
2. mark only non-done future/vague entries as `superseded` if they exist when the rewrite occurs.

For completed tasks, record them as **supersession targets for future task shape**: their delivered code/evidence can remain, but any renewed or follow-up work should be expressed as vertical functional-agent + surface/action + capability increments rather than broad component slices.

## Matrix

| Starter task / area | Functional agent mapping | Surfaces / actions mapping | Capability mapping | Horizontal implementation / tests | Alignment | Supersession target |
|---|---|---|---|---|---|---|
| `TASK-STARTER-00-001` planning scaffold | Repository planning only; no generated-app functional agent required. | None. | None. | Specs/queue scaffold only. | Preserve as historical planning. | No. |
| `TASK-STARTER-01-001` starter scope and acceptance | Names full-core foundation areas; should imply Access/Profile, User Admin, Agent Admin, Audit/Trace, Governance/Policy. | Acceptance-level only; no per-surface contract required yet. | Acceptance-level foundation capabilities. | Planning docs. | Mostly aligned for scope. | No, but future scope docs should list functional agents and required default surfaces explicitly. |
| `TASK-STARTER-01-002` legacy/reusable asset inventory | Repository inventory only. | Identifies workstream frontend assets but does not define app surfaces. | None. | Asset classification. | Preserve. | No. |
| `TASK-STARTER-01-003` template layout workflow | Scaffold/packaging workflow only. | None. | None. | Install/scaffold mechanics. | Preserve. | No. |
| `TASK-STARTER-02-001` backend skeleton | Internal foundation scope, not a user-facing vertical. | None. | Package/layout policy only. | Backend skeleton. | Preserve. | No. |
| `TASK-STARTER-02-002` `/api/me`, AuthContext, membership, role, audit foundation | Access/Profile Agent plus internal security foundation. | `/api/me` context/authority indicator payload; not modeled as a structured surface. | Read current account/context; select context; enforce membership/role/capability; record admin audit. | HTTP/JWT/request-context, repository/service tests. | Partially aligned; strong foundation semantics but surface/action names are implicit. | Future replacement should split Access/Profile default context surface and auth capability contracts if this area is revisited. |
| `TASK-STARTER-02-003` invitation onboarding and user admin backend | User Admin Agent. | Invitation list/detail/status and user/membership admin surfaces are implied but not named. | Invite create/resend/revoke/accept/expire; user directory; membership/role/status commands; audit. | Invitation workflow/timers/consumers/views/endpoints/tests. | Partially aligned; backend capability family is clear, surface contracts implicit. | Yes for future shape: replace broad backend slice with User Admin surface/action capability tasks. |
| `TASK-STARTER-03-001` real workstream browser API contracts | Access/Profile, User Admin, Agent Admin, Audit/Trace, Governance/Policy are implied. | Stronger than earlier tasks: workstream bootstrap, surfaces, actions, events. | Workstream API DTO contracts and action dispatch capabilities. | API/DTO contract docs. | Mostly aligned. | No; preserve and use as source when rewriting. |
| `TASK-STARTER-03-002` User Admin workstream UI real endpoints | User Admin Agent. | User Admin dashboard/list/detail/edit surfaces; actions around invite/user/member updates. | User Admin read/action capabilities behind real APIs. | Frontend API client, workstream UI, endpoint/frontend tests. | Strongest existing vertical task. | No; use as model. |
| `TASK-STARTER-03-003` remaining core surfaces and realtime/stale behavior | Access/Profile, Audit/Trace, Governance/Policy, placeholder Agent Admin. | Core workstream surfaces and stale/reconnect behavior named, but placeholder Agent Admin weakens ownership. | Core read/update/realtime capabilities implied. | SSE/realtime/frontend tests. | Partially aligned; still broad across several agents. | Yes for follow-up shape: split by functional agent and surface/action. |
| `TASK-STARTER-04-001` governed agent records and seed import | Agent Admin Agent plus internal seed/bootstrap scope. | Agent catalog/detail/governed-document surfaces implied, not explicit. | AgentDefinition, PromptDocument, SkillDocument, AgentSkillManifest, ToolPermissionBoundary seed/import capabilities. | Components/views/seed tests. | Partially aligned; object families listed but surface/action and capability ids not explicit. | Yes for future Agent Admin vertical tasks. |
| `TASK-STARTER-04-002` prompt assembly, readSkill, behavior editing | Agent Admin Agent for review; internal runtime invocation for prompt assembly/readSkill. | Behavior edit proposal/review surfaces implied. | Assemble prompt, authorize readSkill, create/review/activate behavior proposal, trace allowed/denied loads. | Runtime services/tools/tests. | Partially aligned; strong capability concepts but missing surface contracts and approval surface names. | Yes for future behavior-editing surface/action tasks. |
| `TASK-STARTER-04-003` Agent Admin and Governance/Policy UI real capabilities | Agent Admin Agent and Governance/Policy Agent. | Governed edit, approval, denial, trace surfaces; still broad. | Agent/policy governance actions and tests. | Frontend clients/forms/tests. | Mostly aligned but spans multiple functional agents in one task. | Split in future if more work remains. |
| `TASK-STARTER-05-001` scaffold packaging mode | Repository packaging only. | None. | None. | Pack/install/scaffold tests. | Preserve. | No. |
| `TASK-STARTER-05-002` extension workflow docs/routing | Repository usage/routing; generated app remains starter extension. | None directly. | None directly. | Docs/routing. | Preserve. | No. |
| `TASK-STARTER-06-001` legacy quarantine/routing cleanup | Repository cleanup only. | Quarantines non-canonical page/screen/static references. | None. | Docs/skills/spec cleanup. | Aligned with legacy quarantine. | No. |
| `TASK-STARTER-06-002` final starter acceptance | Acceptance/release validation. | Validates current baseline but does not create surfaces. | Validates foundation behavior at high level. | Full frontend/backend/package checks. | Preserve. | No. |
| `TASK-STARTER-07-001` acceptance gap baseline | Acceptance/review only. | Gap list names missing fullstack concerns, not surfaces. | Gap list names component/API/auth/email/durability areas. | Scaffold verification. | Preserve as Sprint 07 baseline. | No. |
| `TASK-STARTER-07-002` fullstack smoke validation | Validation-only internal task. | None. | None. | Scaffold backend/frontend/static/no-secret script. | Preserve. | No. |
| `TASK-STARTER-07-003` production-first frontend copy | Cross-cutting UI realization. | General production copy; not tied to specific surface ids/actions. | Backend authorization noted but not capability-specific. | Frontend copy/tests/build. | Mechanics-first; acceptable as cleanup but not a model for generated SaaS feature tasks. | Yes if future frontend polish appears: require affected functional agents/surfaces/states. |
| `TASK-STARTER-07-004` local AuthKit / first-admin bootstrap | Access/Profile Agent and internal security bootstrap. | Sign-in/context/authority indicator surfaces implied; no surface contract. | Authenticate/link account, first-admin bootstrap, resolve AuthContext. | Env/docs, AuthContext tests, frontend build. | Partially aligned; safe foundation task but should name Access/Profile and security capabilities. | Future replacement should be Access/Profile + auth/bootstrap capability task. |
| `TASK-STARTER-07-005` invitation acceptance E2E | User Admin Agent plus invited user Access/Profile entry path. | Invitation acceptance result/recovery surface implied; browser route/API named instead. | Accept invitation, duplicate accept no-op, expired/revoked/wrong-account denial, audit. | Backend API, frontend route/state, tests. | Partially aligned; capability semantics good, surface/action ownership implicit. | Yes: rewrite future invite work as User Admin / invitation-acceptance surface/action capability increment. |
| `TASK-STARTER-07-006` Resend adapter/outbox | Internal foundation email service; User Admin Agent consumes invitation email status. | Delivery status/outbox visibility surface implied but not named. | Send invitation email, capture local outbox, map delivery failure/status, secret-boundary audit. | Email adapter, tests, no-secret scan. | Component/integration mechanics-first. | Yes if email/outbox work continues: split delivery capability and User Admin delivery-status surface. |
| `TASK-STARTER-07-007` durable identity/invitation/audit Akka slices | Access/Profile, User Admin, Audit/Trace Agents; internal foundation. | Directory, invitation, audit timeline surfaces implied. | Current account/profile/settings/membership, invitation lifecycle, AdminAudit record/search. | KVE/ESE/views/workflows, service/component tests. | Weakest workstream alignment: broad component slice, not vertical surface/action. | **High-priority supersession target** for future task shape. |
| `TASK-STARTER-07-008` durable governed-agent Akka slices | Agent Admin Agent; internal runtime agents. | Agent catalog/detail, prompt/skill/manifest/tool-boundary, proposal/review, trace surfaces implied. | AgentDefinition, prompt/skill governance, manifest/readSkill, tool boundary, proposal/trace capabilities. | ESE/runtime/seed tests. | Weak workstream alignment: broad governed-agent component/object slice. | **High-priority supersession target** for future task shape. |
| `TASK-STARTER-07-009` admin/governance/audit APIs with integration tests | User Admin, Governance/Policy, Audit/Trace, Agent Admin depending on chosen APIs. | Surfaces explicitly preserved but not selected; task leaves agent/surface choice to implementation. | User directory, membership, support access, access review, audit/trace, proposal review candidates. | HTTP/JWT/request-context/integration tests. | Too broad and choice-heavy for one fresh-context task. | **High-priority supersession target**: split by major functional agent + surface/action + capability family. |
| `TASK-STARTER-07-010` final fullstack acceptance | Acceptance/release validation. | Validates realized surfaces indirectly. | Validates baseline capabilities indirectly. | Fullstack validation, install/scaffold/build-pack. | Preserve as validation task. | No. |

## Supersession target set

High-priority targets for `TASK-AWSR-04-002` when it rewrites the starter queue/task shape:

1. `TASK-STARTER-07-007` — replace broad durable identity/invitation/audit component slice with vertical increments:
   - Access/Profile Agent context surface + current-account/profile/settings capabilities;
   - User Admin Agent invitation directory/detail/acceptance surfaces + invitation lifecycle capabilities;
   - Audit/Trace Agent admin-audit timeline/search surfaces + audit record/search capabilities.
2. `TASK-STARTER-07-008` — replace broad governed-agent durable slice with Agent Admin vertical increments:
   - agent catalog/detail surface + AgentDefinition lifecycle capabilities;
   - prompt/skill/version surfaces + PromptDocument/SkillDocument governance capabilities;
   - manifest/readSkill trace surface + AgentSkillManifest/readSkill/SkillLoadTrace capabilities;
   - tool-boundary surface + ToolPermissionBoundary capabilities.
3. `TASK-STARTER-07-009` — split candidate API families into one concrete vertical task each, with the selected functional agent, surface/action, capability id/class, AuthContext, tenant/customer scope, audit/trace, frontend/API parity, and tests.
4. `TASK-STARTER-03-003`, `TASK-STARTER-04-001`, `TASK-STARTER-04-002`, `TASK-STARTER-04-003`, `TASK-STARTER-07-003`, `TASK-STARTER-07-004`, `TASK-STARTER-07-005`, and `TASK-STARTER-07-006` — do not need historical mutation, but any follow-up should use replacement task wording that names the workstream/surface/capability contract before implementation mechanics.

## Replacement task contract for starter follow-up queue

Every new runnable starter implementation task should include these fields in the queue entry or task brief:

```text
functional agent or explicit internal/foundation scope:
workstream / surface id / surface type / version:
surface action or workstream event:
capability id and class:
actors/callers and AuthContext:
tenant/customer scope and role/capability rule:
input/output DTOs, redaction, and frontend-safe denial shape:
side effects and idempotency:
policy/approval/escalation:
audit/work trace obligations:
selected Akka substrate and exposure surfaces:
frontend/API/realtime work:
required tests:
```

A task may be explicitly internal/foundation-only, packaging-only, validation-only, or repository-maintenance-only. Otherwise, if it changes authenticated user-facing behavior, it must name the owning functional agent and structured surface/action before choosing Akka components or frontend files.

## Suggested starter queue rewrite direction

For a follow-up Sprint 08 or equivalent, prefer these vertical starter tasks over broad component slices:

1. **Access/Profile Agent context baseline** — context/authority indicator surface, `/api/me` payload, AuthContext selection/linking capabilities, KVE-backed current state, authorization/tenant-isolation/audit tests.
2. **User Admin Agent invitation lifecycle** — invitation dashboard/table/detail/acceptance result surfaces, create/resend/revoke/accept/expire capabilities, ESE/workflow/timer/email/outbox realization, UI/API/realtime tests.
3. **User Admin Agent membership/access review** — user directory and membership detail surfaces, role/status/support-access/access-review capabilities, backend auth/audit/idempotency tests.
4. **Agent Admin Agent catalog and definition lifecycle** — agent catalog/detail/version surfaces, AgentDefinition lifecycle capabilities, durable component/view realization, tenant isolation and disabled-agent denial tests.
5. **Agent Admin Agent prompt/skill/manifest governance** — prompt/skill/version/manifest surfaces and proposed-diff review actions, PromptDocument/SkillDocument/AgentSkillManifest/readSkill capabilities, PromptAssemblyTrace/SkillLoadTrace tests.
6. **Governance/Policy Agent proposal review** — proposal/review/approval surfaces, approval/rejection/activation capabilities, workflow/agent/audit realization, retained-human-authority tests.
7. **Audit/Trace Agent investigation surface** — audit timeline/search/detail surfaces, trace/audit read capabilities, redaction and support/auditor boundary tests.
8. **Cross-cutting fullstack validation** — keep one validation-only task after verticals, but make it verify the named surfaces/capabilities instead of only generic scaffold build success.

## Pass/fail assessment

- Functional/context-area agents: **partial pass**. Many tasks imply Access/Profile, User Admin, Agent Admin, Audit/Trace, and Governance/Policy, but only a few name them directly.
- Structured surfaces: **partial fail**. Workstream UI tasks mention surfaces, but most backend/durability/API tasks do not name stable surface ids, types, payloads, actions, or states.
- Governed capabilities and auth: **partial pass**. AuthContext, audit, idempotency, tenant isolation, and denial tests appear frequently; stable capability ids/classes and exposure-surface parity are inconsistent.
- Routes/UI realization: **partial pass**. The starter is production-first and validated, but some task briefs lead with browser route/API/component mechanics rather than surface contracts.
- Legacy/mechanics quarantine: **pass for docs/cleanup**, **partial fail for queue shape**. Legacy assets are quarantined, but broad mechanics tasks remain as historical examples in the starter queue.

## Action for next task

`TASK-AWSR-04-002` should use this matrix to rewrite the starter queue or add a follow-up starter queue so future runnable tasks are workstream-first and implementation-ready. Preserve completed historical evidence; do not delete completed tasks just to improve their wording.
