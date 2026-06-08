# Routes and Deep Links

Routes support implementation, browser history, bookmarks, and direct links to functional agents or surfaces. They are not the primary application architecture.

Implementation reference: `../../../../../frontend/src/workstream/shell/WorkstreamDeepLinks.ts` parses and serializes selected functional-agent, stream-item, and surface links for the reusable shell and turns direct entry into the same typed shell request pipeline used by prompts and surface actions. Do not rebuild the starter UI as a primary page-route tree. Legacy `frontend/src/screens/**` page components are fixture/reference artifacts for earlier slices and contract tests; they are not active route targets unless a future task explicitly migrates them into the shell request pipeline.

| Route family | Deep-links to | Notes |
|---|---|---|
| signed-in user tile / `/ui/my-account` | My Account workstream and `my-account-dashboard` | Current account, context selection, profile/settings, notifications. |
| `/ui/admin/*` | User Admin workstream and admin surfaces | Users, invitations, roles/memberships, access review, support access, admin audit. |
| `/ui/agents`, `/ui/governance/*` | Agent Admin and Governance/Policy workstreams | Agent definitions, prompts, skills, manifests, tool boundaries, proposals, policies. |
| `/ui/governance-policy`, `/ui/goals/*` | Governance/Policy workstream and goal/plan surfaces | Briefing, goal workbench, progress, outcome links. |
| `/ui/decisions/*` | Decision card surface in the owning functional-agent workstream | Approval, rejection, counterproposal, escalation. |
| `/ui/audit/traces` | Audit/Trace workstream and trace explorer | Scoped audit/work trace search and detail. |

## Legacy page-style artifact classification

- Active route model: workstream shell routes and deep links that resolve to a functional-agent workstream or structured surface through the backend-authoritative shell request path.
- Compatibility/reference artifacts: `frontend/src/screens/admin/AdminUsersPage.tsx`, `frontend/src/screens/audit/AuditTraceExplorerPage.tsx`, `frontend/src/screens/briefing/BriefingPage.tsx`, `frontend/src/screens/decisions/DecisionQueuePage.tsx`, `frontend/src/screens/goals/GoalWorkbenchPage.tsx`, `frontend/src/screens/governance/GovernancePoliciesPage.tsx`, and `frontend/src/screens/profile/ProfilePreferencesPage.tsx`.
- These screen files may remain as contract-fixture references while tests depend on them, but they must not be presented as the primary generated SaaS UI architecture or as proof that the corresponding protected feature is complete.
- Future cleanup should be a separate bounded task that either removes the fixtures after replacing their contract-test value or migrates useful behavior into canonical `frontend/src/workstream/**` surfaces with backend capability/action tests.

## Route rules

- Direct route entry must call `/api/me` before rendering protected data.
- Backend APIs reject unauthorized route-backed queries and actions even if the route exists.
- A route may open a specific surface in a workstream, but the surface contract and capability mapping remain authoritative.
- Deep-link entry appends a prompt-like request item in the target workstream only, with `origin: deep_link` and canonical feedback such as `show surface user-admin-user-list` or `show workstream user-admin`.
- Unresolved or unauthorized deep-link targets render a typed `system_message` surface rather than leaking hidden workstream or surface existence.
