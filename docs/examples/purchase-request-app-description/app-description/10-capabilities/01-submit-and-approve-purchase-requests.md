# Capability: Submit and Approve Purchase Requests

This file is a compact capability-first contract for the conventional purchase-request app-description mechanics example. It is not the generated SaaS foundation doctrine.

## Capability ids/classes

- `purchase-request.submit` (`command`)
- `purchase-request.approve` (`approval`)
- `purchase-request.reject` (`approval`)
- `purchase-request.view-status` (`read/evidence`)

## Purpose

Support employee purchase-request submission and manager approval or rejection while preserving request lifecycle rules, scoped access, idempotent repeat handling, and auditable operational evidence.

## Actors/callers and AuthContext

- employee submitter:
  - may create and submit their own draft request in the selected organization/tenant context
  - may view their own request status
- manager approver:
  - may view, approve, or reject requests assigned to them in the selected organization/tenant context
- operations reviewer:
  - may view scoped queue/status evidence when granted the appropriate review permission
- required context:
  - authenticated user, selected organization/tenant context, active membership, and role/permission grant matching the requested capability

## Inputs and validation

- submit request:
  - request id or client idempotency key, title/description, amount, currency, department/cost center, justification, requester identity, correlation id
  - validate required fields, positive amount, allowed currency, requester membership, and editable lifecycle state
- approve/reject request:
  - request id, approver identity, decision, optional comment, idempotency key, correlation id
  - validate assigned approver authority, submitted lifecycle state, and terminal-state no-op behavior
- view status/query:
  - request id or queue filters such as status, department, approver, and age
  - validate scope filters and redact fields the caller is not allowed to see

## Outputs and denial shape

- submit returns the request id, lifecycle status, submitted timestamp, and next approval step if applicable
- approve/reject returns the request id, terminal or next status, decision timestamp, and safe decision summary
- view-status returns scoped request or queue evidence, not raw internal state
- forbidden, missing-scope, invalid-state, and validation failures use safe errors that do not disclose cross-scope request existence

## Data access and side effects

- reads/writes purchase-request lifecycle state and decision history
- approval may advance the durable approval workflow or complete the request
- final approval may produce an asynchronous procurement handoff event
- reminder/expiry behavior may be scheduled for stalled approval steps
- no payment processing, vendor selection, or procurement fulfillment is performed here

## Idempotency and no-op rules

- duplicate submit with the same idempotency key returns the existing request result when inputs match
- duplicate approve/reject from the same authorized approver returns the already-recorded decision result when it matches
- repeat terminal-state mutations normalize to safe no-op or invalid-state results according to linked behavior rules

## Policy, approval, and autonomy

- approval/rejection is retained human authority for assigned managers in this example
- finance or higher-level approval can be added as a separate approval capability if amount thresholds require it
- no agent or automation has autonomous approval authority in this conventional mechanics example

## Audit/trace obligations

- audit request submission, approval, rejection, forbidden attempts, validation failures that affect lifecycle state, reminder/expiry callbacks, and procurement handoff publication
- preserve correlation id, idempotency key reference, actor, selected context, request id, previous/new status, decision reason/comment metadata, and denial reason where applicable

## Selected exposure surfaces

- browser UI action for submit/approve/reject/status review
- HTTP API for command and query access
- workflow step for approval progression
- timed action for reminder/expiry callbacks
- consumer for procurement handoff after final approval
- view/query for scoped queue and status evidence
- no agent tool or MCP exposure in this example unless explicitly added later

## Required tests

- authorized submit, approve, reject, and status-view success cases
- validation failures for missing fields, invalid amount, and invalid lifecycle state
- forbidden access for wrong requester/approver/scope
- idempotent duplicate submit and duplicate decision behavior
- audit event creation for submission, decision, denial, reminder/expiry, and handoff
- surface-specific HTTP, workflow, timed-action, consumer, and view behavior as realized

## Linked artifacts

- linked-behavior:
  - `../20-behavior/state-models/01-purchase-request-lifecycle.md`
  - `../20-behavior/flows/01-submission-and-approval-flow.md`
  - `../20-behavior/rules/01-edit-and-approval-rules.md`
- linked-tests:
  - `../30-tests/acceptance/01-purchase-request-acceptance.md`
  - `../30-tests/regression/01-repeat-actions.md`
  - `../30-tests/negative/01-forbidden-actions.md`
- linked-security:
  - `../40-auth-security/identity-and-authorization.md`
- linked-observability:
  - `../50-observability/logs-metrics-traces-and-alerts.md`
