# Akka Solution Plan

## Inputs

- source:
  - `docs/examples/purchase-request-prd.md`
- assumptions:
  - this remains a conventional approval-workflow planning-mechanics example, not generated SaaS foundation doctrine
  - reminder delivery itself can be delegated to downstream infrastructure after the service emits the reminder trigger
  - procurement handoff can be modeled as publishing to a topic rather than making a synchronous external API call
  - protected API calls carry an authenticated internal AuthContext; full WorkOS/user-admin foundation details are intentionally out of scope for this small example

## AI-first interpretation

- operating model: low-agentic conventional human approval workflow
- delegated work: timed reminders/expiry and asynchronous procurement handoff only
- retained human authority: manager/finance approval and rejection decisions
- durable substrate objects: purchase request lifecycle, approval decisions, reminder/expiry state, procurement handoff evidence
- governance / approval / exception needs: approval authority by assigned approver and amount threshold; terminal states are locked
- audit, trace, and outcome needs: lifecycle event history, decision audit, reminder/expiry trace, handoff publication trace
- AI-first UI surfaces: not required for this mechanics example beyond conventional queue/action/status surfaces

## Core secure SaaS foundation

- baseline objects: assumed existing authenticated internal user, organization/tenant context, requester/approver roles, and permissions
- `/api/me` and AuthContext: caller identity and selected scope are required inputs at protected HTTP boundaries
- backend authorization: entity/workflow/API code must enforce requester, assigned-approver, finance-threshold, and queue-view permissions rather than trusting UI filtering
- tenant/customer isolation: every command/query carries organization/tenant scope and rejects cross-scope request ids without disclosure
- audit and security tests: required for forbidden requester/approver access, role denial, cross-scope denial, idempotent duplicates, and audit event emission

## Capability summary

- `purchase-request.submit` (`command`): employee submitter; authenticated organization/tenant AuthContext; request fields plus idempotency/correlation; creates/submits lifecycle state; duplicate submit returns existing matching result; audit submission/denial; exposed by browser action and HTTP API; tests for success, validation, forbidden, idempotency, audit.
- `purchase-request.approve` (`approval`): assigned manager or finance approver; scoped AuthContext with approval permission; decision/comment plus idempotency/correlation; advances workflow and may trigger procurement handoff after final approval; duplicate matching approval is safe; audit decision/denial; exposed by browser action, HTTP API, and workflow step; tests for manager-only, manager-plus-finance, forbidden, idempotency, audit.
- `purchase-request.reject` (`approval`): assigned approver; scoped AuthContext with rejection permission; decision/comment plus idempotency/correlation; moves request to terminal rejected state; duplicate matching rejection is safe; audit decision/denial; exposed by browser action, HTTP API, and workflow step; tests for success, terminal lock, forbidden, idempotency, audit.
- `purchase-request.view-queue` (`read/evidence`): manager/finance/operations reviewer; scoped AuthContext and queue-view permission; status/department/approver/age filters; no side effects; redacted queue rows; data-access trace where required; exposed by browser query, HTTP API, and View; tests for scoped filters, redaction, forbidden, query shape.
- `purchase-request.remind-or-expire` (`scheduled`): system timer with stored authority basis; request/workflow id plus timer metadata; sends reminder or expires stale approval step; idempotent stale timers no-op; audit reminder/expiry; exposed by TimedAction callback; tests for reminder, expiry, stale timer no-op, audit.
- `purchase-request.publish-procurement-handoff` (`reactive`): internal consumer after final approval; scoped event provenance and correlation; publishes exactly one procurement handoff; idempotent duplicate handling; audit publication/failure; exposed by Consumer; tests for publication, dedupe, failure/retry metadata.

## Capability-to-component mapping

- `purchase-request.submit` → `PurchaseRequestEntity`: validates submitter scope, records lifecycle event, enforces duplicate submit semantics.
- `purchase-request.approve` / `purchase-request.reject` → `PurchaseRequestApprovalWorkflow` + `PurchaseRequestEntity`: coordinates approval waits/threshold branch and records auditable decision outcomes.
- `purchase-request.view-queue` → `PurchaseRequestsByStatusView` + `PurchaseRequestEndpoint`: returns scoped/redacted queue evidence through API/UI surfaces.
- `purchase-request.remind-or-expire` → `PurchaseRequestApprovalTimedAction`: schedules and executes reminder/expiry callbacks with stale-timer no-op behavior.
- `purchase-request.publish-procurement-handoff` → `ApprovedPurchaseRequestConsumer`: reacts after final approval and publishes the procurement handoff asynchronously.
- All exposed capabilities → `PurchaseRequestEndpoint`: validates AuthContext/scope at the request boundary and preserves shared denial/audit semantics.

## Chosen components

- `EventSourcedEntity`: `PurchaseRequestEntity` — durable request state plus lifecycle and decision history.
- `Workflow`: `PurchaseRequestApprovalWorkflow` — durable orchestration for manager approval, optional finance approval, and fulfillment release.
- `TimedAction`: `PurchaseRequestApprovalTimedAction` — executes reminder and expiry callbacks safely.
- `View`: `PurchaseRequestsByStatusView` — queue/search projection for status, department, approver, and age filters.
- `Consumer`: `ApprovedPurchaseRequestConsumer` — reacts after final approval and publishes the procurement handoff asynchronously.
- `HTTP endpoint`: `PurchaseRequestEndpoint` — internal REST surface for submit, approve, reject, get, and list operations.

## Why each component exists

- `PurchaseRequestEntity`: lifecycle history and auditability make event sourcing the best fit over latest-state-only storage.
- `PurchaseRequestApprovalWorkflow`: approval is a durable multi-step process with branching, waits, retries, and terminal rejection/expiry behavior.
- `PurchaseRequestApprovalTimedAction`: reminder and 72-hour expiry requirements need scheduled callbacks that survive restarts and safely no-op on stale timers.
- `PurchaseRequestsByStatusView`: operations queries are list/search oriented and should be scoped/redacted rather than served directly from the write model.
- `ApprovedPurchaseRequestConsumer`: procurement handoff is an asynchronous side effect isolated from core approval commands.
- `PurchaseRequestEndpoint`: the PRD asks for HTTP command/query access, and the route layer is a selected exposure surface for the capability contracts.

## Skill routing

- `akka-solution-decomposition`
- `capability-first-backend`
- `akka-event-sourced-entities`
- `akka-ese-domain-modeling`
- `akka-ese-application-entity`
- `akka-workflows`
- `akka-workflow-component`
- `akka-workflow-pausing`
- `akka-workflow-testing`
- `akka-timed-actions`
- `akka-timed-action-component`
- `akka-timers-scheduling`
- `akka-timed-action-testing`
- `akka-views`
- `akka-view-from-event-sourced-entity`
- `akka-view-query-patterns`
- `akka-view-testing`
- `akka-consumers`
- `akka-consumer-from-workflow`
- `akka-consumer-producing`
- `akka-consumer-testing`
- `akka-http-endpoints`
- `akka-http-endpoint-component-client`
- `akka-http-endpoint-request-context`
- `akka-http-endpoint-acl-internal`
- `akka-http-endpoint-testing`

## Open questions and assumptions

- question: should reminder delivery be email, chat, or another internal channel?
- question: does the procurement handoff need an outbox-style deduplication key beyond Akka delivery guarantees?
- question: should expired requests be restartable by submitting a new approval command, or are they terminal?
- question: what exact role/scope names should map to requester, manager approver, finance approver, and operations reviewer?
- assumption: approval comments are part of the entity event history.
- assumption: the workflow id can be aligned with the purchase request id for simpler tracing.
- assumption: the operations queue does not require live SSE updates in the first version.

## Recommended implementation order

1. define capability ids, AuthContext/scope model, domain records, validation rules, lifecycle statuses, and API request/response models
2. implement `PurchaseRequestEntity` with auditable lifecycle events, command validation, idempotency, and scoped denial behavior
3. implement `PurchaseRequestApprovalWorkflow` to drive manager approval, optional finance approval, retained-human decisions, and terminal outcomes
4. implement `PurchaseRequestApprovalTimedAction` for reminder and expiry callbacks into the workflow or entity
5. implement `PurchaseRequestsByStatusView` for scoped operational queue queries
6. implement `ApprovedPurchaseRequestConsumer` to publish approved requests to the procurement topic with dedupe/correlation metadata
7. implement `PurchaseRequestEndpoint` for internal portal access with AuthContext extraction and ACL checks
8. add component-family tests and one end-to-end happy-path integration scenario

## Required tests

- capability/security tests — authorized AuthContext success, wrong requester/approver denial, cross-scope denial, role/scope denial, and audit on denials.
- entity unit tests — `PurchaseRequestEntity` validation, event emission, replayed lifecycle state, and duplicate submit/decision behavior.
- workflow integration tests — `PurchaseRequestApprovalWorkflow` manager-only path, manager-plus-finance path, rejection path, retry behavior, and terminal locks.
- timed action tests — `PurchaseRequestApprovalTimedAction` reminder scheduling, expiry execution, stale-timer idempotency, and audit trace.
- view integration tests — `PurchaseRequestsByStatusView` scoped/redacted projections for status, department, approver, and age-based queries.
- consumer integration tests — `ApprovedPurchaseRequestConsumer` publishes exactly one procurement handoff with correct metadata.
- HTTP endpoint integration tests — `PurchaseRequestEndpoint` AuthContext-aware submit, approve, reject, get-by-id, and list/search flows.
- end-to-end scenario test — submit request, approve through required stages, verify final status, audit trace, and procurement publication.
