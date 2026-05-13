# Interactions and Forms

## Common interaction requirements

- show server validation errors at field and form level
- preserve unsaved draft state where reasonable
- confirm high-impact actions
- support optimistic updates only where command semantics are safe and idempotent
- use drawers/modals for focused review without losing dashboard context
- all submit actions include tenant context and authenticated user context
- duplicate submits are prevented through disabled submit controls and backend idempotency where commands create durable objects
- after validation failure, focus moves to the first invalid field or form error summary
- after success, focus moves to the confirmation or next actionable item

## Create goal form

- surface: Goal Workbench
- route: `/ui/goals/new`
- submit API: `POST /api/goals`
- primary button: `Create goal`
- secondary actions: `Save draft`, `Cancel`
- fields:
  - objective: required text, 10-500 chars, label `Objective`, helper `Describe the outcome the agent team should pursue.`
  - priority: required enum `low | normal | high | urgent`, label `Priority`
  - target date: optional date/time, label `Target date`
  - success criteria: required list of 1-10 text criteria, label `Success criteria`
  - constraints: optional list, label `Constraints`, helper `Limits the plan must respect.`
  - allowed data/tools: optional checklist, label `Tool and data permissions`
  - approval gates: displayed from policy, editable only when permission allows
- client validation:
  - objective length
  - at least one success criterion
  - target date must not be in the past when provided
- backend validation:
  - tenant membership active
  - permission to create goal
  - selected tools/data allowed for requester and tenant
  - approval gates match active policy
- idempotency:
  - client sends idempotency key for create command
- success behavior:
  - show `Goal created. Review the draft plan before launch.`
  - navigate to goal detail or keep user in workbench with next action `Request draft plan`
- failure recovery:
  - forbidden: `You do not have permission to create goals in this tenant.`
  - validation: keep form values and highlight invalid fields

## Request draft plan action

- surface: Goal Workbench / Goal Detail
- submit API: `POST /api/goals/{goalId}/draft-plan`
- primary button: `Draft plan`
- fields:
  - optional instructions: textarea, label `Planning guidance`
- backend validation:
  - goal exists in tenant
  - user can manage goal
  - goal is draft or planning state
- idempotency:
  - safe to retry; backend should avoid creating duplicate active draft-plan jobs for same goal/version
- success behavior:
  - show planning progress state
  - stream or poll plan-generation status
- failure recovery:
  - stale goal version asks user to refresh before retry

## Approve launch action

- surface: Goal Workbench / Goal Detail
- submit API: `POST /api/goals/{goalId}/launch`
- primary button: `Approve launch`
- confirmation copy: `Launch this goal and allow the selected agent team to execute within the listed policy gates?`
- backend validation:
  - user has launch permission
  - required plan exists
  - required approval gates satisfied
  - no unresolved blocking policy conflicts
- idempotency:
  - launch command is idempotent for an already-launched goal version
- success behavior:
  - show `Goal launched. Agent activity will appear as work progresses.`
  - navigate to goal detail or mission control

## Invite user form

- surface: Admin Users and Invitations
- route: `/ui/admin/users`
- submit API: `POST /api/admin/users/invitations`
- primary button: `Send invitation`
- fields:
  - email: required email, label `Email address`
  - display name: optional text, label `Display name`
  - role: required role id, label `Role`
  - tenant scope: required tenant id from current tenant context, normally read-only
  - message: optional text, label `Invitation note`
- client validation:
  - valid email shape
  - role selected
- backend validation:
  - inviter has tenant admin permission
  - role assignment is allowed by inviter
  - tenant is active
  - invitee is not already an active member with same role
- idempotency:
  - retry may resend or return existing pending invitation according to backend command semantics
- success behavior:
  - show `Invitation sent to <email>.`
  - add pending invitation row
- failure recovery:
  - duplicate pending invite shows existing invitation and allowed resend action

## Edit role assignment form

- surface: Admin Users and Invitations
- submit API: `PUT /api/admin/users/{userId}/roles`
- primary button: `Save role changes`
- confirmation copy for elevated roles: `Granting this role may allow the user to approve decisions or administer tenant settings.`
- fields:
  - roles: required multi-select or checklist, label `Roles`
  - reason: required for elevated role changes, label `Reason for change`
- client validation:
  - at least one active role when membership remains active
  - reason required for admin, reviewer, or policy-owner grants
- backend validation:
  - acting user can grant/revoke selected roles
  - target user is in tenant
  - cannot remove last tenant admin unless replacement exists
- idempotency:
  - role update is idempotent for same role set and membership version
- success behavior:
  - show `Role assignment updated.`
  - update user row and audit trace link

## Review decision card form

- surface: Decision Card Detail or review drawer
- route: `/ui/decisions/:decisionId`
- submit APIs:
  - `POST /api/decisions/{decisionId}/approve`
  - `POST /api/decisions/{decisionId}/reject`
  - `POST /api/decisions/{decisionId}/request-changes`
  - `POST /api/decisions/{decisionId}/escalate`
  - `POST /api/decisions/{decisionId}/counter`
- primary buttons vary by permission and state: `Approve`, `Reject`, `Request changes`, `Escalate`, `Submit counterproposal`
- fields:
  - decision action: implicit from button or explicit radio when multiple actions shown
  - comment: optional for approve, required for reject/request changes/escalate/counter
  - counterproposal: required when action is counter
  - acknowledgement: required checkbox for high-impact approvals, label `I reviewed the evidence, risk, policy trigger, and impact.`
- backend validation:
  - reviewer has permission for selected action
  - decision card is open and current
  - required policy gate permits selected action
  - stale version conflict is rejected
- idempotency:
  - action command includes decision version and idempotency key
- success behavior:
  - show `Decision <approved/rejected/escalated>.`
  - return to queue and focus next actionable decision
- failure recovery:
  - conflict: `This decision changed while you were reviewing it. Refresh to see the latest evidence and actions.`
  - forbidden: `You do not have permission to decide this item.`

## Propose policy change form

- surface: Governance Center / Policies
- submit API: `POST /api/governance/policy-proposals`
- primary button: `Submit policy proposal`
- secondary actions: `Run simulation`, `Save draft`, `Cancel`
- fields:
  - policy id: required, label `Policy`
  - title: required text, label `Proposal title`
  - proposed change: required structured diff or markdown, label `Proposed change`
  - rationale: required text, label `Rationale`
  - expected impact: required text or structured estimate, label `Expected impact`
  - simulation scope: optional, label `Replay or simulation scope`
  - examples: optional examples affected by proposal
- client validation:
  - title and rationale present
  - proposed change present
- backend validation:
  - user can propose policy changes
  - target policy exists and is active
  - change does not directly activate expanded authority
- idempotency:
  - proposal create uses idempotency key
- success behavior:
  - show `Policy proposal submitted for review.`
  - open proposal detail or governance queue
- confirmation for commit action:
  - `Activate this policy version? This may change what agents are allowed to do.`

## Export trace/report action

- surface: Audit Trace Explorer and selected dashboard reports
- submit API: `POST /api/audit/exports` or report-specific endpoint
- primary button: `Export permitted results`
- backend validation:
  - export permission
  - tenant scope
  - filter scope does not exceed user authority
- success behavior:
  - show export job status or download link when ready
- failure recovery:
  - explain if export is too broad or forbidden
