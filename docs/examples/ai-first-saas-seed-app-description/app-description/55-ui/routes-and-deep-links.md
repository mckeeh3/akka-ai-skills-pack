# Routes and Deep Links

Routes support implementation, browser history, bookmarks, and direct links to functional agents or surfaces. They are not the primary application architecture.

Implementation reference: `../../../../../frontend/src/workstream/shell/WorkstreamDeepLinks.ts` parses and serializes selected functional-agent, stream-item, and surface links for the reusable shell. Do not rebuild the seed UI as a primary page-route tree.

| Route family | Deep-links to | Notes |
|---|---|---|
| `/ui/access` | Access/Profile workstream and `access-profile-dashboard` | Current account, context selection, profile/settings. |
| `/ui/admin/*` | User Admin workstream and admin surfaces | Users, invitations, roles/memberships, access review, support access, admin audit. |
| `/ui/agents`, `/ui/governance/*` | Agent Admin and Governance/Policy workstreams | Agent definitions, prompts, skills, manifests, tool boundaries, proposals, policies. |
| `/ui/mission-control`, `/ui/goals/*` | Mission Control workstream and goal/plan surfaces | Briefing, goal workbench, progress, outcome links. |
| `/ui/decisions/*` | Decision card surface in the owning functional-agent workstream | Approval, rejection, counterproposal, escalation. |
| `/ui/audit/traces` | Audit/Trace workstream and trace explorer | Scoped audit/work trace search and detail. |

## Route rules

- Direct route entry must call `/api/me` before rendering protected data.
- Backend APIs reject unauthorized route-backed queries and actions even if the route exists.
- A route may open a specific surface in a workstream, but the surface contract and capability mapping remain authoritative.
