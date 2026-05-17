# Operational Tests: Audit, Trace, Observability, and Outcomes

## Behavior or capabilities under test

- linked capability ids/classes: `CAP-00` secure tenant/user foundation, `CAP-03` supplies autopilot, and downstream trace/outcome review through `CAP-10`
- exposure surfaces: audit/admin views, work traces, decision traces, policy traces, tool/data-access traces, outcome links, UI trace drawers, logs/metrics/alerts, timers, consumers, and integration outbox

## Foundation audit and trace cases

- Given WorkOS sign-in/link/unlink, local account creation/disable/reactivate/removal, profile/settings update where auditable, context switch for privileged users, or `/api/me` linking, when the event occurs, then an AdminAuditEvent/work trace records actor, selected AuthContext, target id, action, correlation id, outcome, and redaction marker.
- Given invitation create, resend, delivery attempt, delivery failure, revoke, expire, or accept, when the lifecycle advances, then InvitationView and AdminAuditView show status, delivery status/attempt count where safe, actor/caller, idempotency key, and trace link without raw invite tokens.
- Given membership, role, support-access, last-admin protection, tenant/customer setting, billing-boundary, or admin-agent recommendation activity, when it succeeds, fails, or is denied, then AdminAuditView and AccessReviewQueueView contain searchable scoped facts.
- Given a protected admin read, denied request, support-access use, billing-boundary action, or consequential admin-agent tool call, when it occurs, then audit evidence includes the permission/capability checked and avoids leaking unrelated tenant/customer data.
- Given frontend build output and browser API payloads are inspected, when secrets exist on the server side, then operational checks prove WorkOS private data, JWTs, invite raw tokens, supplier credentials, and backend provider secrets are absent.

## Supplies work-trace cases

- Given a supplies evidence read, telemetry refresh, recommendation creation/update, policy invocation, decision-card creation, approval/rejection/modification/deferral/escalation, suppression, fulfillment preparation/submission, integration result, retry/no-op, or more-evidence request, when the action occurs, then a work trace records tenant/customer/site/device/assignment ids, actor or service identity, AuthContext, policy document/version/clause ids, evidence snapshot ids, correlation id, idempotency key, and redaction marker.
- Given an agent or tool participates in supplies work, when it reads evidence, drafts a recommendation, cites policy, prepares a decision card, or requests more evidence, then traces include agent id, model/tool reference when applicable, tool input/output redaction status, data-access references, confidence/risk/impact, and authority boundary.
- Given a backend integration caller invokes inventory or fulfillment, when the request succeeds, fails, times out, or is retried, then traces expose safe integration target/reference, status, retry/no-op result, and operator-visible error summary without credentials or raw supplier payloads.
- Given a UI trace drawer opens for recommendation, card, order, suppression, support-access use, or audit record, when authorized, then it shows safe evidence summaries, policy citations, decision outcome, actor/caller, correlation id, and outcome link; when unauthorized, it denies without leaking resource existence.

## Policy, decision, and approval observability cases

- Given a policy gate permits auto-ship, when the workflow proceeds, then the trace cites the exact active policy version and clauses that allowed the action.
- Given a policy gate blocks, suppresses, escalates, or requires approval, when the workflow pauses or routes a decision, then the decision trace includes evidence gaps, policy trigger, reviewer authority required, deadline, allowed actions, and escalation target.
- Given a reviewer decides a card, when the workflow resumes, then the decision trace links reviewer id, reason, selected action, changed fulfillment scope if any, previous recommendation, and downstream side effect or suppression.
- Given a human correction should influence future behavior, when `update_policy_from_decision` is selected, then the system creates a policy proposal/reference example trace and does not activate the policy automatically.

## Outcome and metric cases

- Given a supplies recommendation is fulfilled, suppressed, rejected, deferred, or escalated, when later delivery, stock, cost variance, depletion avoidance, missed-depletion, abnormal-consumption confirmation, or customer feedback evidence arrives, then an outcome link is created to the original recommendation/card/order and policy invocation.
- Given outcome evidence contradicts an agent recommendation or policy threshold, when outcome review runs, then the result is visible as a learning-loop candidate or policy proposal, not as an automatic governance commit.
- Given Owner Brief, Mission Control, Supplies Autopilot, Audit & Outcomes, or Policy Center surfaces summarize operational work, when they display counts or risk states, then those summaries derive from scoped trace/outcome records and link back to auditable detail.

## Reliability and alerting cases

- Given telemetry, inventory, entitlement, fulfillment, or outcome consumer processing fails, when retries occur, then retry attempts are observable and idempotent, stale evidence is visible to reviewers, and high-impact failures create command-center or decision-queue attention items.
- Given decision-card deadlines, support-access expiry, invitation expiry, stale telemetry rechecks, cooldown checks, or post-delivery outcome checks are scheduled, when timers fire or are retried, then execution emits success/no-op/failure traces and does not duplicate side effects.
- Given trace, audit, or outcome storage is unavailable, when a consequential action would otherwise proceed without required evidence, then readiness/operation is blocked or degraded according to policy rather than silently losing accountability facts.

## Open operational fixture needs

- Concrete retention periods, redaction classes, trace event schemas, metric names, alert thresholds, and provider-specific integration error fixtures remain future realization inputs.
- Future executable tests must add deterministic fake telemetry, inventory, fulfillment, WorkOS/JWT, email outbox, and agent-tool fixtures that preserve these operational expectations.
