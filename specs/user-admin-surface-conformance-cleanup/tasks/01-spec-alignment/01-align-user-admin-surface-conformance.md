# TASK-UASCC-01-001: Align User Admin surface conformance specs

## Intent

Update app-description and mini-project traceability so implementation tasks have unambiguous conformance targets for User Admin structured surfaces.

## Required reads

- `AGENTS.md`
- `specs/user-admin-surface-conformance-cleanup/README.md`
- `specs/user-admin-surface-conformance-cleanup/conversation-capture.md`
- `specs/user-admin-surface-conformance-cleanup/backlog/01-user-admin-surface-conformance-build-backlog.md`
- `app-description/domains/core-starter/workstreams/user-admin/workstream.md`
- `app-description/domains/core-starter/workstreams/user-admin/surfaces/surfaces.md`
- `app-description/domains/core-starter/realization/traceability.md`
- `specs/user-admin-surface-navigation-tree/navigation-tree-verification.md`

## Skills

- `app-description-surface-modeling`
- `app-description-change-impact`
- `app-description-ui`

## Expected outputs

- Updated User Admin app-description surface/workstream docs and traceability if needed.
- Optional `specs/user-admin-surface-conformance-cleanup/conformance-specification.md` summarizing implementation decisions.

## Required checks

```bash
git diff --check
rg "show-inspection|surface-user-admin-system-message|backend-authored|canonical" app-description/domains/core-starter/workstreams/user-admin specs/user-admin-surface-conformance-cleanup
```

## Done criteria

- Canonical surface-type expectations are explicit for User Admin descendants.
- Dashboard trunk/role-specific variant policy is explicit.
- Functional-agent id normalization or aliasing is explicit.
- Detail-as-inspection/task-router and no-inline-mutation requirements are explicit.
- Backend-authored dashboard/list routing, metadata visibility split, typed system-message results, backend-shaped options, access-review, and identity-exception starter semantics are explicit.

## Vertical workstream contract

- Workstream / functional agent: User Admin / `user-admin-agent` concept, `agent-user-admin` runtime alias pending normalization.
- Attention category or non-attention reason: spec alignment; no runtime attention item.
- Role-specific dashboard / surface: User Admin dashboard variants, `surface-user-admin-users`, `surface-user-admin-user-detail`, invitation/support/role/access-review/identity surfaces, `surface-user-admin-system-message`.
- Surface graph node/action edge: model dashboard/list/detail/task/decision/workflow/system-message edges.
- Governed-tool id and exposure: browser-tool ids for User Admin surfaces documented, no runtime code.
- Capability id: `user_admin.*`, `saas_owner.organization.*`, `admin.audit.read` as applicable.
- AuthContext / roles / tenant scope: App Admin, Tenant Admin, Customer Admin, Auditor scope semantics.
- Akka substrate: app-description/docs only.
- API / frontend / realtime path: non-runtime spec update.
- Audit/work trace requirements: traceability and redaction expectations documented.
- Local validation path: focused `rg` plus `git diff --check`.
