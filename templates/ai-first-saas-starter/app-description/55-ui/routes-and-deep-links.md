# Routes and Deep Links

Routes support implementation, browser history, bookmarks, and direct links to functional agents or surfaces. They are not the primary application architecture.

Implementation reference: `../../../../../frontend/src/workstream/shell/WorkstreamDeepLinks.ts` parses and serializes selected functional-agent, stream-item, and surface links for the reusable shell and turns direct entry into the same typed shell request pipeline used by prompts and surface actions. Do not rebuild the starter UI as a primary page-route tree.

| Route family | Deep-links to | Notes |
|---|---|---|
| signed-in user tile / `/ui/my-account` | My Account workstream and `my-account-dashboard` | Current account, context selection, profile/settings, notifications. |
| `/ui/admin/*` | User Admin workstream and admin surfaces | Users, invitations, roles/memberships, access review, support access, admin audit. |
| `/ui/agents`, `/ui/governance/*` | Agent Admin and Governance/Policy workstreams | Agent definitions, prompts, skills, manifests, tool boundaries, proposals, policies. |
| `/ui/governance-policy`, `/ui/goals/*` | Governance/Policy workstream and goal/plan surfaces | Briefing, goal workbench, progress, outcome links. |
| `/ui/decisions/*` | Decision card surface in the owning functional-agent workstream | Approval, rejection, counterproposal, escalation. |
| `/ui/audit/traces` | Audit/Trace workstream and trace explorer | Scoped audit/work trace search and detail. |

## Route rules

- Direct route entry must call `/api/me` before rendering protected data.
- Backend APIs reject unauthorized route-backed queries and actions even if the route exists.
- A route may open a specific surface in a workstream, but the surface contract and capability mapping remain authoritative.
- Deep-link entry appends a prompt-like request item in the target workstream only, with `origin: deep_link` and canonical feedback such as `show surface user-admin-user-list` or `show workstream user-admin`.
- Unresolved or unauthorized deep-link targets render a typed `system_message` surface rather than leaking hidden workstream or surface existence.
