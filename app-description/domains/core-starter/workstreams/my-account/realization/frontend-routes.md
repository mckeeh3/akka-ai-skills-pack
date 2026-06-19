# Realization: Frontend routes and surfaces for My Account

Capability: `account-context-and-profile`.

## Route/deep-link rule

My Account frontend realization uses the canonical AI-first workstream shell. Conventional routes and query params only reopen a selected functional agent, stream item, or structured surface. They do not define the application model, grant authority, or bypass `/api/me`, selected `AuthContext`, capability checks, or backend denial behavior.

My Account canonical entry is the signed-in user/account control in the rail. It is not duplicated as a primary top rail workstream list item. Authorized deep links may reopen My Account surfaces through the shell using `/workstreams/my-account?surface=<surfaceId>&ref=<opaqueSafeTargetRef>&mode=<surfaceMode>`; the rail user tile remains the visible navigation owner, focus return target, and accessibility label source.

## Rebuilt surface realization targets

| Surface | Frontend realization target | Style/component requirements | Notes |
|---|---|---|---|
| `surface-my-account-dashboard` | Personal command-center dashboard surface under `frontend/src/workstream/surfaces/**` | Authority/context panel, attention counter cards, `Needs me` queue rows/cards, compact control panels, trace/evidence blocks, governed action bar | Replaces generic card-grid navigation. Attention appears before profile/settings/details. |
| `surface-my-profile` | Self-service detail/edit surface | Tokenized structured form controls, immutable/provider-backed identity facts, validation/no-op/save-result system messages, trace links | Must not look or behave like an admin user record editor. |
| `surface-my-settings` | Personal preferences detail/edit surface | Named theme selector from current skills-pack style guide/component catalog, immediate local preview, governed Save/Confirm, save failure recovery | Must not expose light/dark/system modes, external delivery/provider controls, or tenant-wide branding. |
| `surface-my-context` | Authority/context panel surface | Selected context header, role/capability summary, available context cards/rows, stale-impact warning, safe context-switch action, trace/evidence block | Context changes call backend `/api/me`/shell APIs and refresh affected surfaces. |
| `surface-my-account-notification-center` | `NotificationCenterSurface.tsx` | Preserve triage lanes, lifecycle action boundary, responsive card grid, trace links, and backend-authored action availability | Acceptance target: the component renders `my_account.notification_center.v1` lanes, read/dismiss/archive/snooze/preference actions, denied/no-op/stale states, source-opening reauthorization, trace refs, keyboard focus, and responsive card layout without source-work mutation or hidden-workstream enumeration. |
| `surface-my-account-personal-attention-digest-progress` | Workflow-status/progress surface | Autonomous task progress, provider/runtime fail-closed state, no fake/model-less success, trace links | Source attention is not mutated by digest progress. |
| `surface-my-account-personal-attention-digest-result` | Outcome-panel/advisory briefing surface | Evidence summary, redaction/omission notes, source links with reauthorization, accept/reject review actions, trace links | Should not be rendered as a generic dashboard. |
| `surface-my-account-personal-attention-digest-blocked` | System-message card | Blocker code, recovery steps, required readiness hints, trace/correlation id, return/retry actions | Provider/runtime blocked state must fail closed. |
| `surface-my-account-open-denied` | System-message card | `not_found_or_redacted`/forbidden recovery, no hidden workstream leakage, context refresh/request-access guidance, trace link | Used for denied workstream/source/context/surface openings. |

## Frontend evidence

| Concern | Frontend evidence | Notes |
|---|---|---|
| Auth-gated workstream shell and selected context display | `frontend/src/main.tsx`, `frontend/src/workstream/shell/WorkstreamShell.tsx`, `ContextAuthorityBar.tsx` | Backend account/context data drives display; frontend state is not authorization. |
| Functional-agent rail user tile | `frontend/src/workstream/rail/FunctionalAgentRail.tsx`, `FunctionalAgentRailItem.tsx`, `WorkstreamIcon.tsx` | Signed-in user/account tile opens My Account with attention state. |
| Structured surface rendering | `frontend/src/workstream/surfaces/SurfaceRenderer.tsx`, `DashboardSurface.tsx`, `DetailEditSurface.tsx`, `NotificationCenterSurface.tsx`, `WorkflowStatusSurface.tsx`, `OutcomeSurface.tsx`, `SystemMessageSurface.tsx` | Components must render typed surface envelopes, states, actions, trace links, and redaction. |
| Governed actions and result feedback | `frontend/src/workstream/actions/**`, `SurfaceActionBar.tsx`, `ActionFeedbackItem.tsx`, `TraceLinkList.tsx` | Shows trace links, safe denials, no-op/conflict/provider-fail-closed results, and recoverable errors. |
| Browser API client | `frontend/src/api/HttpApiClient.ts`, `HttpWorkstreamApiClient.ts`, `WorkstreamApiClient.ts`, `types.ts` | Typed DTO/client boundary; handles unauthorized/forbidden/server errors. |

## Validation evidence

- `frontend/src/workstream-my-account-vertical.contract.test.mjs`
- `frontend/src/workstream-shell.contract.test.mjs`
- `frontend/src/workstream-surfaces.contract.test.mjs`
- `frontend/src/workstream.contract.test.mjs`

## Gaps / caveats

- Implementation cleanup must rebuild or specialize all My Account owning components named above so they render the exact contracts in `../surfaces/surfaces.md`, use named theme `core-saas-foundation`, and satisfy the web UI style/component contracts referenced by the app-level foundation docs. Generic dashboard/detail-edit rendering is acceptable only as shared primitives beneath the named My Account surfaces, not as the final runtime shape.
- Removed screen modules are not runtime architecture, fallback, or reference targets for this workstream.
