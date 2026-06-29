# Tools: Governance/Policy

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Canonical capability id for every tool in this file: `governance-policy-lifecycle`.

Exact actor adapters replace ambiguous exposure labels. Browser realization normally uses `surface_action` plus protected `api_call`; model-backed reads/draft/simulation assistance use `agent_tool_call` only when the `governance-policy-agent` tool boundary grants them; side-effecting chat execution uses `human_chat_tool_plan` only after exact human confirmation and backend reauthorization; approval/exception/activation/rollback lifecycles may also run through `workflow_step`; runtime enforcement uses `internal_call`.

## Governed-tool contracts

### `governance.policy.search`

- toolType: `search_or_list`
- purpose: read the visible policy catalog, active/draft/rolled-back versions, pending approvals, simulation findings, exceptions, and history summary.
- allowedWorkerTypes: human, functional-agent, system
- allowedActorAdapters: `surface_action`, `agent_tool_call`, `api_call`, `internal_call`
- authorityLevel: observe
- authContextScope: backend-selected SaaS/defaults or tenant/customer/account context with read capability.
- inputSchema: search/filter/page/sort request with policy name text, category, lifecycle state, workstream, agent, governed tool/action, role, exception state, and visible scope hints.
- outputSchema: scoped/redacted catalog rows with category, active version, draft/approval state, simulation status, exception count, rollback availability, action availability, freshness, and trace refs.
- confirmationPolicy: none.
- approvalPolicy: none.
- idempotencyAndTransactionBoundary: read-only scoped query.
- sideEffects: optional read/workstream trace only.
- resultSurfacesAndEvents: `surface-governance-policy-dashboard`, `surface-governance-policy-catalog`, or `system_message`.
- denialBehavior: safe forbidden/not-found-or-redacted system state with trace ref when visible.
- requiredTests: scoped catalog, filters, tenant isolation, support/auditor boundaries, redaction, trace links, agent read-tool boundary.

### `governance.policy.read`

- toolType: `read_evidence`
- purpose: read one policy's active version, draft versions, clauses/values, scope precedence, exception state, runtime decision semantics, rollback targets, and decision evidence.
- allowedWorkerTypes: human, functional-agent, system
- allowedActorAdapters: `surface_action`, `agent_tool_call`, `api_call`, `internal_call`
- authorityLevel: observe/evaluate
- authContextScope: selected context plus policy/scope read capability; runtime `internal_call` uses service/caller provenance.
- inputSchema: policy id, requested scope, optional runtime decision context, version id, freshness token where applicable.
- outputSchema: policy detail/effective result with active version, draft status, clause/value summary, exceptions, winning scope, decision explanation, available actions, and trace refs.
- confirmationPolicy: none.
- approvalPolicy: none.
- idempotencyAndTransactionBoundary: read-only query or runtime check; repeated runtime checks may create deduped policy-decision traces where practical.
- sideEffects: policy-decision trace for runtime checks; optional workstream read trace for UI/agent reads.
- resultSurfacesAndEvents: `surface-governance-policy-detail` or `system_message`.
- denialBehavior: safe denial without confirming hidden policy/scope existence.
- requiredTests: precedence, active-version explanation, hidden target redaction, runtime decision trace, agent read-tool boundary.

### `governance.policy.draft`

- toolType: `proposal`
- purpose: create or update a versioned policy draft/proposal without affecting runtime.
- allowedWorkerTypes: human, functional-agent, system
- allowedActorAdapters: `surface_action`, `agent_tool_call`, `human_chat_tool_plan`, `api_call`, `workflow_step`
- authorityLevel: draft/propose
- authContextScope: actor must have draft capability for selected scope and policy category.
- inputSchema: policy id/category, target scope, proposed clauses or simple values, rationale, risk/impact/confidence notes, affected workstreams/tools/roles, required reason, idempotency key, correlation id, optional base version.
- outputSchema: draft/proposal summary, validation blockers, required simulation/approval gates, next actions, history ref, trace ref, and result surface id.
- validationAndSafeDefaults: validate catalog membership, value/clauses, supported scope/category, reason, base version freshness, and hard-platform boundary.
- confirmationPolicy: surface submit or exact chat-plan snapshot confirmation for human-backed draft commands; agent tool call may draft only when boundary permits and cannot activate.
- approvalPolicy: draft itself does not require final approval; activation later does.
- idempotencyAndTransactionBoundary: one draft create/update per policy/scope/base-version/idempotency key.
- sideEffects: draft state/history/trace; no runtime policy change.
- resultSurfacesAndEvents: `surface-governance-policy-draft`, `surface-governance-policy-result`, or `system_message`.
- partialFailureBehavior: validation/evidence gaps return result blockers and no active version change.
- denialBehavior: missing capability, hidden target, unsupported category/scope, hard-platform override denied safely.
- requiredTests: draft creation, reason required, unsupported category denial, idempotency, no runtime mutation, agent boundary.

### `governance.policy.simulate`

- toolType: `simulation`
- purpose: evaluate draft/rollback/exception candidates against selected policy state, representative traces, and affected runtime actions before decision.
- allowedWorkerTypes: human, functional-agent, system
- allowedActorAdapters: `surface_action`, `agent_tool_call`, `human_chat_tool_plan`, `api_call`, `workflow_step`, `internal_call`
- authorityLevel: evaluate
- authContextScope: actor or service must have simulation capability for selected scope and evidence set.
- inputSchema: draft id or rollback target, selected scope, evidence selector, affected action/tool/workstream filters, requested simulation depth, correlation id, optional idempotency key.
- outputSchema: simulation result with expected allow/deny/governed outcomes, changed decisions, risk/impact/confidence, evidence gaps, partial-failure markers, required approval gates, and trace refs.
- confirmationPolicy: none for read/evidence simulation; exact chat-plan confirmation only when a user asks chat to start a simulation job.
- approvalPolicy: simulation never approves or activates.
- idempotencyAndTransactionBoundary: one result per draft/version/scope/evidence selector/correlation id where practical.
- sideEffects: simulation result/history/trace; no active policy change.
- resultSurfacesAndEvents: `surface-governance-policy-simulation`, `surface-governance-policy-partial-failure`, or `system_message`.
- partialFailureBehavior: unavailable evidence or replay gaps produce partial-failure findings and review blockers, not success.
- denialBehavior: hidden evidence, unsupported simulation scope, missing capability, or hard-platform override denied safely.
- requiredTests: simulation evidence, no mutation, partial failure, hidden evidence redaction, required approval detection, agent boundary.

### `governance.policy.submit_for_approval`

- toolType: `workflow`
- purpose: move a validated draft and simulation evidence into human review with a decision-card surface.
- allowedWorkerTypes: human, system
- allowedActorAdapters: `surface_action`, `human_chat_tool_plan`, `api_call`, `workflow_step`
- authorityLevel: request_review
- authContextScope: actor must have approval-request capability for the selected policy scope.
- inputSchema: draft id, simulation result refs, evidence bundle refs, requested reviewers or reviewer group, required reason, idempotency key, correlation id, deadline/SLA if applicable.
- outputSchema: decision-card request, review state, reviewer eligibility summary, trace ref, and next action surface.
- confirmationPolicy: surface submit or exact chat-plan snapshot confirmation.
- approvalPolicy: creates review; does not approve.
- idempotencyAndTransactionBoundary: one approval request per draft/idempotency key.
- sideEffects: workflow review item, attention item, work/audit trace.
- resultSurfacesAndEvents: `surface-governance-policy-decision-card`, `surface-governance-policy-result`, or `system_message`.
- denialBehavior: invalid draft, missing simulation, missing capability, hidden scope, stale base version denied safely.
- requiredTests: approval request, decision-card payload, idempotency, attention item, denial and trace.

### `governance.policy.approve`

- toolType: `approval_decision`
- purpose: record approve, reject, request-evidence, modify, defer, escalate, or exception-required decisions on a policy decision card.
- allowedWorkerTypes: human, system
- allowedActorAdapters: `surface_action`, `api_call`, `workflow_step`
- authorityLevel: decide
- authContextScope: reviewer must have decision authority for policy scope and meet separation-of-duty rules.
- inputSchema: decision card id, action, rationale, evidence refs reviewed, risk/impact/confidence acknowledgement, optional requested changes, idempotency key, correlation id.
- outputSchema: immutable decision record, updated workflow state, required next action, trace ref, and result surface.
- confirmationPolicy: reviewer submit confirmation.
- approvalPolicy: this is the approval/denial action; activation remains separate.
- idempotencyAndTransactionBoundary: one immutable decision per card/action/idempotency key.
- sideEffects: decision record, workflow transition, attention update, audit/work trace.
- resultSurfacesAndEvents: `surface-governance-policy-decision-card`, `surface-governance-policy-result`, or `system_message`.
- denialBehavior: missing reviewer authority, stale card, self-approval violation, hidden scope, or missing rationale denied safely.
- requiredTests: approve/reject/request-evidence/modify/escalate, separation of duty, stale card, idempotency, decision trace.

### `governance.policy.activate`

- toolType: `commit`
- purpose: activate an approved policy version and publish it for runtime enforcement.
- allowedWorkerTypes: human, system
- allowedActorAdapters: `surface_action`, `human_chat_tool_plan`, `api_call`, `workflow_step`, `internal_call`
- authorityLevel: commit_approved_version
- authContextScope: actor/service must have activation capability for selected scope; approved decision card is required.
- inputSchema: approved proposal/version id, decision ref, target scope, freshness token, required reason, idempotency key, correlation id, publication target.
- outputSchema: active version summary, superseded version ref, publication status, effective-policy summary, history ref, trace ref, validation blockers, and result/partial-failure markers.
- confirmationPolicy: surface submit or exact chat-plan snapshot confirmation.
- approvalPolicy: approved decision card required; unapproved activation denied.
- idempotencyAndTransactionBoundary: single policy-version activation transaction per policy/scope/version/idempotency key; partial publication is reported separately.
- sideEffects: active policy version commit, supersede prior version, runtime publication/projection, attention resolution, history/audit/work traces.
- resultSurfacesAndEvents: `surface-governance-policy-result`, `surface-governance-policy-detail`, `surface-governance-policy-history`, `surface-governance-policy-partial-failure`, or `system_message`.
- partialFailureBehavior: committed vs not-committed vs partial-publication states are explicit; no silent partial success.
- denialBehavior: unapproved/stale/cross-context/hidden-target/hard-platform attempts denied safely.
- requiredTests: approved activation, unapproved denial, transaction boundary, partial publication, idempotency, trace evidence.

### `governance.policy.rollback`

- toolType: `rollback`
- purpose: restore a prior approved policy version or revoke a problematic exception through a rollback decision.
- allowedWorkerTypes: human, system
- allowedActorAdapters: `surface_action`, `human_chat_tool_plan`, `api_call`, `workflow_step`, `internal_call`
- authorityLevel: restore_prior_version
- authContextScope: actor/service must have rollback capability for selected scope; rollback decision card is required.
- inputSchema: active version id, target prior version or exception id, rollback decision ref, reason, impact summary, idempotency key, correlation id, freshness token.
- outputSchema: rollback commit summary, restored version/exception state, publication status, history ref, trace ref, partial-failure markers, and recovery actions.
- confirmationPolicy: surface submit or exact chat-plan snapshot confirmation.
- approvalPolicy: rollback decision card required.
- idempotencyAndTransactionBoundary: single policy-version rollback transaction per policy/scope/target/idempotency key.
- sideEffects: rollback commit, runtime publication/projection, attention update, history/audit/work trace.
- resultSurfacesAndEvents: `surface-governance-policy-result`, `surface-governance-policy-history`, `surface-governance-policy-partial-failure`, or `system_message`.
- partialFailureBehavior: publication or downstream projection failure is surfaced and trace-linked.
- denialBehavior: missing authority, missing decision, hidden/cross-scope target, stale version, hard-platform override, or missing reason denied safely.
- requiredTests: approved rollback, missing-decision denial, prior-version restoration, idempotency, partial failure, rollback trace.

### `governance.policy.review_exception`

- toolType: `exception_review`
- purpose: grant, deny, revoke, expire, or request evidence for scoped policy exceptions.
- allowedWorkerTypes: human, system
- allowedActorAdapters: `surface_action`, `human_chat_tool_plan`, `api_call`, `workflow_step`
- authorityLevel: exception_decision
- authContextScope: reviewer must have exception authority for policy scope and exception type.
- inputSchema: exception request id or policy/version/scope, requested action, allowed deviation, reason, evidence refs, expiry, owner, idempotency key, correlation id.
- outputSchema: exception state summary, decision record, expiry/review state, runtime enforcement effect, trace ref, and result surface.
- confirmationPolicy: reviewer submit confirmation or exact chat-plan snapshot confirmation.
- approvalPolicy: exception decision card required for grants/revocations where policy marks consequential.
- idempotencyAndTransactionBoundary: one exception state transaction per exception/action/idempotency key.
- sideEffects: exception grant/deny/revoke/expire state, runtime enforcement projection when applicable, attention update, audit/work trace.
- resultSurfacesAndEvents: `surface-governance-policy-exception`, `surface-governance-policy-result`, or `system_message`.
- denialBehavior: unsupported exception, expired/hidden target, missing authority, missing evidence/reason, hard-platform-control exception denied safely.
- requiredTests: grant/deny/revoke/expire, expiry not authorizing runtime behavior, idempotency, hidden target denial, trace evidence.

### `governance.policy.read_history`

- toolType: `read_evidence`
- purpose: read authorized policy changes, decisions, simulations, exceptions, rollback records, and runtime outcome links.
- allowedWorkerTypes: human, functional-agent, system
- allowedActorAdapters: `surface_action`, `agent_tool_call`, `api_call`, `internal_call`
- authorityLevel: observe
- authContextScope: selected context with policy history/audit read capability; support requires active scoped support access.
- inputSchema: policy/scope/actor/time/state/filter/page request with browser-provided ids treated as hints.
- outputSchema: scoped/redacted history timeline, decision/simulation/exception/rollback events, runtime outcome links, actor display summaries, reasons, values, trace refs, and redaction metadata.
- confirmationPolicy: none.
- approvalPolicy: none.
- idempotencyAndTransactionBoundary: read-only scoped query.
- sideEffects: optional read/workstream trace.
- resultSurfacesAndEvents: `surface-governance-policy-history` or `system_message`.
- denialBehavior: safe forbidden/not-found-or-redacted response without hidden event enumeration.
- requiredTests: auditor/support scoping, redaction, runtime outcome links, filters, tenant isolation, agent read-tool boundary.

## Surface-intent and `human_chat_tool_plan` catalog

Deterministic no-mutation surface routing runs first for read/open/prefill requests. Prompts like `show policy approval queue`, `show effective policy for this tool`, `open exceptions expiring this week`, and `show simulation results for draft GP-42` should open or prefill catalog/detail/simulation/decision/exception/history surfaces through surface-intent routes, not execute a chat plan.

Confirmed `human_chat_tool_plan` is reserved for side-effecting command plans or starting simulation/draft workflows that the human explicitly confirms:

| Classification | Representative prompts | Surface action ids | Shared governed tool ids | Capability id | Result surface(s) |
|---|---|---|---|---|---|
| `chat-proposal-only` | `draft a policy requiring approval before this agent sends email`; `simulate this policy change`; `send this draft for approval`; `activate the approved policy`; `roll back to the prior version`; `grant a 7 day exception for this customer` | `action-governance-policy-draft`; `action-governance-policy-simulate`; `action-governance-policy-submit-approval`; `action-governance-policy-activate`; `action-governance-policy-rollback`; `action-governance-policy-review-exception` | `governance.policy.draft`; `governance.policy.simulate`; `governance.policy.submit_for_approval`; `governance.policy.activate`; `governance.policy.rollback`; `governance.policy.review_exception` | `governance-policy-lifecycle` | `surface-governance-policy-draft`; `surface-governance-policy-simulation`; `surface-governance-policy-decision-card`; `surface-governance-policy-exception`; `surface-governance-policy-result`; `surface-governance-policy-partial-failure`; `system_message` |

Blocked or out-of-scope entries:

- complex policy scripts or arbitrary rule expressions;
- legal compliance workflow suites;
- unbounded autonomous policy commits;
- enterprise delegation models not represented in app-description;
- any request to override hard platform security controls.

Execution requirements for every accepted confirmed plan:

- validate catalog membership, supported policy category/type/scope, selected context, actor capability, exact plan snapshot, required reason, approval state where needed, idempotency, freshness, and trace emission;
- reject hidden scope targets, unsupported categories/value types, cross-tenant/customer scope, missing reasons, stale versions, unapproved activation/rollback/exception grants, and hard platform-security overrides;
- idempotent replay returns prior results without duplicate decisions, commits, history, or traces;
- no workstream agent, prompt, frontend route, visible control, or tool description grants authority beyond backend authorization.
