# Capability: Supplies Autopilot

This capability is the first detailed DCA vertical slice. It models governed supply replenishment as a capability contract before choosing endpoints, workflows, agents, views, integrations, or UI actions.

## Capability definition

- capability-id: `supplies-autopilot`
- capability number: `CAP-03`
- class:
  - workflow
  - proposal
  - approval
  - command
  - read/evidence
  - reactive
  - scheduled
  - trace/audit
- purpose:
  - use fresh DCA telemetry, lifecycle state, contract entitlement, inventory evidence, and tenant policy to prevent supply depletion while preserving human authority for abnormal, expensive, ambiguous, offboarding, or policy-sensitive shipments
- business outcome:
  - authorized tenant operators can safely delegate high-volume consumable replenishment to bounded automation, with auto-ship limited to policy-approved cases and all consequential recommendations, approvals, suppressions, integrations, and outcomes traceable to evidence and active policy clauses

## In-scope outcomes

- Receive or refresh device consumable telemetry and interpret supply levels for tenant/customer-scoped devices.
- Forecast depletion window, urgency, confidence, abnormal-consumption risk, and candidate supply item.
- Verify device assignment, customer lifecycle, device lifecycle, DCA collector health where relevant, contract entitlement, and customer-specific supply preferences.
- Check inventory, supplier availability, substitution risk, cost, and delivery estimate through approved integration boundaries.
- Auto-prepare or auto-submit fulfillment only when all active policy gates allow bounded automation.
- Create decision cards for review, approval, modification, deferral, escalation, or more-evidence requests when evidence, risk, impact, confidence, lifecycle, entitlement, cost, inventory, or customer preference requires human authority.
- Suppress shipments when policy clearly denies fulfillment and record a durable reason.
- Maintain idempotent supply recommendations and shipment windows so retries, duplicate telemetry, and scheduled rechecks do not create duplicate shipments or duplicate pending cards.
- Record policy invocation, evidence, tool/data access, fulfillment command, decision, suppression, and outcome traces.
- Link later delivery, stock, cost, customer feedback, and depletion-avoidance outcomes back to the recommendation or decision.

## Out-of-scope outcomes

- Vendor-specific DCA telemetry payload contracts, supplier APIs, ERP schemas, shipping-label semantics, or inventory pricing rules not already accepted by future integration specs.
- Tenant-to-customer billing calculations, meter billing, or customer invoicing; those belong to `meter-billing-review` and billing integration contracts.
- Self-service customer supply ordering outside tenant policy and entitlement controls.
- Automatic shipments for offboarding, unknown, unassigned, contract-missing, high-cost, abnormal, stale-telemetry, or ambiguous cases unless a human approves the specific decision card or a future accepted policy grants a narrow safe boundary.
- Prompt-only authorization, frontend-only action hiding, hidden-form authority, or agent discretion without backend policy enforcement.
- Raw contract documents, supplier credentials, integration secrets, unrelated tenant/customer data, or unredacted PII in browser, view, audit-summary, or agent-tool responses.

## Actors and callers

- Tenant operations supervisor reviewing queues, supervising automation, resolving exceptions, and approving operational decisions.
- Dealer owner approving high-cost, high-impact, or policy-sensitive exceptions when required by active policy.
- Supplies/inventory owner managing inventory evidence, substitution decisions, cost-threshold reviews, and fulfillment approval.
- Customer admin or customer contact where a future delegated customer-safe status or preference review surface is explicitly enabled.
- Supplies coordinator agent drafting recommendations, explaining evidence, citing policies, and preparing decision cards within granted tool permissions.
- Contract and Policy Agent checking entitlement and active policy clauses.
- Inventory Agent checking stock, supplier alternatives, cost, delivery estimate, and substitution risk through scoped integration tools.
- DCA telemetry ingest service or telemetry consumer reacting to new or refreshed supply telemetry.
- Supplies workflow, approval workflow, scheduled recheck timers, inventory/service integration callers, fulfillment outbox/consumer, scoped evidence views, and outcome-trace consumers.
- SaaS support operator only through a tenant-created support-access membership and only for scoped support/evidence actions.

## Authority and contract

- AuthContext / scope:
  - all protected operations require an authenticated account or trusted service identity, selected `AuthContext`, active Tenant membership or accepted service ACL, tenant/customer scope, and the required named supplies capability grant.
  - customer, site, device, assignment, telemetry, contract, inventory, recommendation, decision-card, and fulfillment records must be scoped by `tenantId` and, where applicable, `customerId`.
  - human browser actions require local Akka-owned roles/capabilities; WorkOS authenticates humans but does not authorize supplies actions.
  - service, consumer, timer, workflow, and agent callers must carry or resolve an authority basis, correlation id, and tenant/customer scope before reading evidence or creating side effects.
  - support access never bypasses tenant/customer scope and must be reasoned, time-limited, visible, revocable, and audited through `CAP-00`.
- permissions / named capability grants:
  - `supplies.evidence.read` for scoped telemetry, entitlement, inventory, lifecycle, recommendation, and trace evidence.
  - `supplies.recommendation.create` for creating or refreshing recommendations without fulfillment side effects.
  - `supplies.shipment.auto_prepare` for policy-gated preparation of low-risk fulfillment commands.
  - `supplies.shipment.approve` for approving or modifying shipment decision cards.
  - `supplies.shipment.suppress` for recording explicit suppression reasons where policy denies fulfillment.
  - `supplies.exception.escalate` for routing abnormal, high-cost, offboarding, ambiguous, or missing-evidence cases.
  - `supplies.policy.apply` for workflows/agents that invoke active supply policy clauses; policy activation remains governed by `policy-governance`.
  - `supplies.integration.fulfillment.invoke` for backend integration callers only, not arbitrary browser or agent callers.
- inputs / validation / idempotency:
  - `EvaluateSupplyNeed` input: `tenantId`, `customerId`, `siteId`, `deviceId`, `deviceAssignmentId`, consumable type, telemetry reading id or snapshot reference, telemetry observed-at timestamp, usage baseline reference, contract reference, policy document/version, correlation id, and idempotency key.
  - `RefreshSupplyRecommendation` input: existing recommendation id or natural key, changed evidence reference, reason, correlation id, and idempotency key.
  - `PrepareFulfillmentOrder` input: approved recommendation or auto-ship recommendation id, supply item, quantity, fulfillment option, cost estimate, delivery estimate, policy citations, correlation id, and idempotency key.
  - `DecideSupplyShipment` input: decision-card id, action (`approve`, `reject`, `modify`, `defer`, `escalate`, `request_more_evidence`), reviewer reason, optional modified item/quantity/provider/deadline, evidence snapshot id, policy citations, correlation id, and idempotency key.
  - validation rejects missing tenant/customer/device scope, stale or conflicting telemetry beyond active policy, unknown or unassigned devices, inactive/offboarding lifecycles without approval, missing contract entitlement, invalid item mapping, invalid policy version, unsupported customer preference, forbidden actor/caller, and unsafe idempotency keys.
  - natural dedupe key: `tenantId + customerId + deviceAssignmentId + consumableType + depletionWindow + policyVersion`; repeated commands update evidence or return the existing recommendation/card/order instead of duplicating consequential work.
- outputs / redaction / denial shape:
  - `SupplyRecommendation`: id, status, scope ids, item summary, urgency, forecast window, confidence, risk, policy citations, evidence summaries, allowed next actions, decision-card link when applicable, fulfillment status, trace links, and redaction markers.
  - `SupplyDecisionCard`: decision subject, recommended action, authority, required role, evidence summary, policy triggers, confidence, risk/impact, alternatives, known gaps, allowed reviewer actions, deadline, trace links, and outcome follow-up.
  - `FulfillmentPreparationResult`: order/preparation id, status, safe supplier/inventory summary, cost tier, delivery estimate, policy basis, external reference when safe, and trace link.
  - denials use stable validation, `401`, `403`, not-found, conflict, stale-evidence, policy-denied, pending-approval, or integration-unavailable shapes without leaking unrelated tenant/customer resource existence.
  - browser, agent, and audit-summary outputs redact supplier credentials, raw integration payloads, contract documents, unrelated customer data, raw telemetry payloads when not needed, and PII not required for the decision.
- data access:
  - reads: customer/site/device/assignment lifecycle records, DCA collector health summaries, scoped consumable telemetry, usage baselines, active contract and entitlement summaries, customer preferences, service/offboarding/billing-hold context, active supply policy document/version/clauses/thresholds, inventory snapshot, supplier option summary, existing recommendation/card/order state, work traces, and outcome links.
  - writes: supply recommendation, policy invocation record, decision card, approval/rejection/modification/deferral/escalation facts, suppression record, fulfillment preparation/command record, work trace, tool/data-access trace, outcome follow-up link, and scheduled recheck state.
  - all reads and writes must include tenant/customer filters and redaction before browser, view, agent, support, or audit-summary exposure.
- side effects:
  - start or advance the supplies workflow; create/update recommendations; create/update decision cards; pause/resume approval workflow; prepare or submit fulfillment commands through the approved integration boundary; publish integration/outbox events; schedule rechecks/cool-down checks; notify reviewer queues or command-center views; suppress shipments with reason; and emit trace/outcome events.
  - consequential side effects default to policy-gated automation or decision-card approval; no agent or UI surface may bypass backend policy and approval checks.
- exposure surfaces:
  - supplies autopilot browser UI: queue, evidence detail, recommendation review, decision card, fulfillment status, suppression reason, and outcome follow-up.
  - JWT-protected HTTP APIs for scoped recommendation/evidence queries, evaluation requests, decision actions, and fulfillment status.
  - workflow steps for evaluation, policy gating, approval pause/resume, fulfillment preparation, suppression, and outcome follow-up.
  - view/query surfaces for supply queue, risk-ranked recommendations, pending decisions, fulfillment status, inventory/evidence summaries, and outcome review.
  - agent tools limited to scoped evidence reading, recommendation drafting, explanation, policy citation, decision-card drafting, and more-evidence gathering unless future policy grants a narrow autonomous operation.
  - integration calls through backend-only fulfillment/inventory client surfaces, outbox consumers, or internal service callers; not direct browser calls.
  - consumer reactions for telemetry, inventory, lifecycle, entitlement, fulfillment, and outcome events.
  - timed actions for deferred recommendations, stale evidence refresh, decision deadlines, cooldowns, and post-delivery outcome checks.

## Policy, approval, and autonomy

- Default autonomy:
  - bounded automation may auto-prepare or auto-submit a low-risk fulfillment command only when active policy clauses, evidence freshness, entitlement, lifecycle, inventory, cost, customer preference, confidence, and idempotency checks all pass.
  - agents may recommend, summarize, classify, cite policies, draft decision cards, request more evidence, and propose policy improvements; they do not unilaterally approve high-impact or ambiguous shipments.
  - humans retain authority for abnormal consumption, high cost, constrained inventory, substitution conflict, customer preference conflict, lifecycle ambiguity, offboarding/replacement/removal context, low confidence, missing evidence, contract mismatch, and policy changes.
- Required policy gates:
  - `SUP-1.0`: auto-ship only for active, monitored, contract-covered devices.
  - `SUP-2.0`: do not ship when customer is `Offboarding Planned` or later unless explicitly approved.
  - `SUP-3.0`: abnormal consumption above `2x` baseline creates a decision card before shipment.
  - `SUP-4.0`: shipment cost above configured threshold requires approval.
  - `SUP-5.0`: customer-specific supply preferences override default supplier choice when active and contract-compatible.
  - related gates: `OFF-3.0` for pending shipments during offboarding, plus foundation permissions and support-access rules from `CAP-00`.
- Decision-card triggers:
  - abnormal consumption, high cost, constrained stock, substitution proposal, preference conflict, stale/conflicting telemetry, unknown assignment, lifecycle or contract gaps, offboarding/removal/replacement review, billing hold, low confidence, integration ambiguity, or reviewer-requested more evidence.
- Allowed reviewer actions:
  - `approve`: resume workflow and perform the approved fulfillment action within the approved scope.
  - `reject`: suppress recommendation and record rationale.
  - `modify`: execute only the modified item/quantity/provider/timing approved by the reviewer.
  - `defer`: pause until deadline, evidence refresh, or timed recheck.
  - `escalate`: route to dealer owner, operations supervisor, supplies owner, or policy owner based on active policy.
  - `request_more_evidence`: request targeted telemetry, contract, inventory, customer, or lifecycle evidence and pause consequential side effects.
  - `update_policy_from_decision`: create a policy proposal or reference example; never activate policy automatically.
- Escalation:
  - missing authority, cross-tenant attempts, stale evidence, repeated abnormal consumption, conflicting supplier data, failed fulfillment integration, reviewer deadline expiry, or low-confidence agent output route to exception workflows and decision queues.

## Audit and trace requirements

- Audit/work-trace records must be emitted for:
  - protected evidence reads, denials, policy checks, recommendation creation/update, auto-ship decision, decision-card creation, approval/rejection/modification/deferral/escalation, suppression, fulfillment command preparation/submission, integration result, retry/no-op, more-evidence request, support-access use, and agent/tool activity.
- Required trace fields:
  - actor account id or service identity, agent id/model/tool when applicable, selected `AuthContext`, tenant/customer/site/device/assignment ids, consumable type, recommendation/card/order ids, policy document id/version and clause ids, evidence snapshot ids, telemetry observed-at time and freshness status, confidence/risk/impact, decision authority, reviewer and reason where applicable, integration target/reference when safe, correlation id, idempotency key, outcome link id, and redaction marker.
- Retention/redaction:
  - retain accountability facts, evidence summaries, policy citations, decision outcomes, fulfillment references, and outcome links.
  - redact secrets, raw supplier credentials, raw tokens, unrelated customer data, raw telemetry payloads not needed for review, and sensitive contract details not required by an authorized reviewer.
- Outcome links:
  - follow-up should capture delivery success/failure, stock impact, cost variance, depletion avoidance or missed-depletion event, customer feedback, abnormal-consumption confirmation, and policy-improvement candidates.

## Required tests

- success:
  - authorized tenant operations user or workflow evaluates a fresh, active, contract-covered, low-risk supply need and creates an auto-ship recommendation with cited `SUP-1.0` and no decision card.
  - authorized reviewer approves a pending supply decision card and the workflow prepares exactly one fulfillment command.
  - scoped queue/query returns only redacted recommendations and decision cards for the selected tenant/customer scope.
- validation:
  - missing device assignment, stale telemetry, invalid policy version, unsupported consumable mapping, missing contract entitlement, malformed idempotency key, or invalid reviewer action is rejected with safe errors.
- forbidden and tenant isolation:
  - wrong tenant/customer, missing membership, disabled account, denied supplies capability, expired support access, unauthorized service identity, and unauthorized agent tool call are denied without leaking resource existence.
- policy and approval:
  - offboarding customer triggers `SUP-2.0`/`OFF-3.0` decision-card or suppression behavior, not auto-ship.
  - abnormal consumption above threshold triggers review under `SUP-3.0`.
  - high-cost or substitution case requires approval under `SUP-4.0`/`SUP-5.0`.
  - policy-denied cases suppress shipment with durable reason and trace.
- idempotency and no-op:
  - duplicate telemetry or retry for the same natural dedupe key updates evidence or returns the existing recommendation/card/order.
  - repeated approval or fulfillment retry does not create duplicate external orders.
  - existing pending decision card is updated rather than duplicated.
  - pending shipment is paused/canceled according to `OFF-3.0` when offboarding starts.
- audit/trace:
  - protected reads, denials, policy invocations, recommendation, decision-card creation, approval, suppression, fulfillment integration, tool invocation, and outcome follow-up create required trace records.
- exposure-specific:
  - UI hides unavailable actions but backend still denies forbidden actions.
  - agent evidence tools expose only scoped/redacted data and cannot submit high-impact shipments without policy approval.
  - timer rechecks and consumers are retry-safe.
  - integration failures remain visible, auditable, and do not duplicate fulfillment commands.

## Linked layers

- operating model:
  - `../15-operating-model/agent-roles-and-authority.md`
  - `../15-operating-model/policies-and-approval-gates.md`
  - `../15-operating-model/decisions-exceptions-and-evidence.md`
  - `../15-operating-model/audit-trace-and-outcomes.md`
  - `../15-operating-model/outcomes-and-learning-loops.md`
- behavior:
  - `../20-behavior/flows/01-supplies-autopilot-flow.md`
  - `../20-behavior/flows/02-lifecycle-and-exception-flows.md`
  - `../20-behavior/state-models/01-lifecycle-foundation.md`
- tests:
  - `../30-tests/README.md`
  - `../30-tests/test-index.md`
  - `../30-tests/acceptance/01-foundation-and-supplies-acceptance.md`
  - `../30-tests/negative/01-security-and-approval-bypass.md`
  - `../30-tests/regression/01-idempotency-and-policy-regression.md`
  - `../30-tests/operational/01-audit-trace-and-outcomes.md`
- auth/security:
  - `../40-auth-security/identity-and-trust.md`
  - `../40-auth-security/authorization-rules.md`
  - `../40-auth-security/agent-permissions.md`
  - `../40-auth-security/data-protection.md`
  - `../40-auth-security/foundation-onboarding-admin-boundaries.md`
  - `../40-auth-security/boundary-and-surface-rules.md`
- observability:
  - `../50-observability/audit-trace-and-outcomes.md`
- UI:
  - `../55-ui/ui-surfaces.md`
  - `../55-ui/style-guide.md`
- generation:
  - `../60-generation/implementation-slices.md`
- traceability:
  - `../70-traceability/ai-first-coverage-map.md`
- review:
  - `../80-review/structure-gap-summary.md`

## Akka realization notes

Future realization should map this capability to a supplies workflow with approval pause/resume, event-sourced recommendation/decision history, scoped views for queues and evidence, timer-backed rechecks and decision deadlines, consumers for telemetry/inventory/fulfillment/outcome events, backend-only integration callers, bounded agent recommendation tools, JWT-protected HTTP APIs, and React/Vite decision/supervision UI. These are realization surfaces for `CAP-03`, not separate capability roots.
