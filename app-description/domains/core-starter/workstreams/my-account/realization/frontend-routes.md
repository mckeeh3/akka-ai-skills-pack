# Realization: Frontend routes and surfaces for My Account

Capability: `account-context-and-profile`.

## Frontend evidence

| Surface / route concern | Frontend evidence | Notes |
|---|---|---|
| Auth-gated workstream shell and selected context display | `frontend/src/main.tsx`, `frontend/src/workstream/shell/WorkstreamShell.tsx`, `ContextAuthorityBar.tsx` | Backend account/context data drives display; frontend state is not authorization. |
| Functional-agent rail tile | `frontend/src/workstream/rail/FunctionalAgentRail.tsx`, `FunctionalAgentRailItem.tsx`, `WorkstreamIcon.tsx` | My Account appears as a signed-in user workstream tile with attention state. |
| Profile/settings/detail panels and personal notifications | `frontend/src/workstream/surfaces/DetailEditSurface.tsx`, `DashboardSurface.tsx`, `NotificationCenterSurface.tsx` | Renders typed surface payloads from backend APIs. |
| Personal digest/export result and decision/denial feedback | `MarkdownResponseSurface.tsx`, `SystemMessageSurface.tsx`, `OutcomeSurface.tsx`, `ActionFeedbackItem.tsx`, `TraceLinkList.tsx` | Shows trace links, safe denials, and recoverable errors. |
| Browser API client | `frontend/src/api/HttpApiClient.ts`, `HttpWorkstreamApiClient.ts`, `WorkstreamApiClient.ts`, `types.ts` | Typed DTO/client boundary; handles unauthorized/forbidden/server errors. |

## Validation evidence

- `frontend/src/workstream-my-account-vertical.contract.test.mjs`
- `frontend/src/workstream-shell.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`
- `frontend/src/workstream.contract.test.mjs`

## Gaps / caveats

- `frontend/src/screens/**` page-style files are legacy/reference evidence and are not primary runtime architecture for this workstream.
