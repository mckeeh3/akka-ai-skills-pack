# Regression Tests: Idempotency, No-op Behavior, and Policy Safety

## Behavior or capabilities under test

- linked capability ids/classes: `CAP-00` secure tenant/user foundation and `CAP-03` supplies autopilot
- exposure surfaces: commands, queries, workflows, timers, consumers, integration outbox, browser/API actions, and agent tools

## Foundation idempotency regression cases

- Given duplicate invitation create commands for the same target email, scope, role/capability set, and idempotency key, when retried, then the system returns or updates the same active Invitation according to policy and does not create duplicate active invites.
- Given repeated invite resend, when delivery is retried, then delivery attempts/status and audit facts are updated without losing original invitation history or creating duplicate memberships.
- Given repeated revoke, expiry, or acceptance callbacks for an Invitation, when the command is replayed after projection rebuild or service restart, then revoked/expired/accepted state remains correct and acceptance does not create duplicate memberships.
- Given repeated `/api/me` linking or context switching, when the same WorkOS subject and accepted invitation context are presented, then Account/Membership state remains stable and no scopes are widened.
- Given membership add, suspend, reactivate, remove, role replace, support-access grant/revoke/expiry, or billing-boundary updates are retried, then current status, last-admin protection, and audit history remain consistent.
- Given admin-assistant agents replay recommendations or summaries, when tool calls are retried, then they do not create unapproved role, membership, support-access, policy, billing, or account side effects.

## Supplies idempotency and no-op regression cases

- Given duplicate telemetry, repeated scheduled recheck, repeated workflow step, or command retry for the same `tenantId + customerId + deviceAssignmentId + consumableType + depletionWindow + policyVersion`, when `EvaluateSupplyNeed` runs, then the existing recommendation/card/order is returned or refreshed instead of creating duplicate consequential records.
- Given a pending decision card already exists for the natural dedupe key, when new evidence arrives, then the card is updated with the changed evidence and trace link rather than duplicated.
- Given an approved recommendation already prepared fulfillment, when `PrepareFulfillmentOrder` is retried with the same idempotency key, then exactly one external/order-preparation command exists.
- Given approval, rejection, modification, deferral, escalation, or request-more-evidence is repeated on an already decided card, when the command is replayed, then the system rejects or no-ops according to recorded decision semantics and never creates duplicate fulfillment or suppression side effects.
- Given offboarding starts while a supply recommendation or pending shipment exists, when the lifecycle event is replayed or retried, then pending fulfillment is paused, canceled, or routed to review according to `OFF-3.0` exactly once.
- Given fulfillment integration fails and the outbox/consumer retries, when the same integration request is processed, then retry state remains diagnosable and no duplicate external shipment is submitted.
- Given a timer recheck fires after the recommendation is resolved, suppressed, fulfilled, or no longer applicable, when the timer executes, then it records a safe no-op trace and performs no shipment side effect.

## Tenant isolation regression cases

- Given the same external device id, recommendation id, card id, invitation id, or user-visible reference appears in two tenants, when views rebuild or consumers replay events, then query and command behavior remains scoped by tenant/customer and never joins or leaks records across tenants.
- Given UserDirectoryView, MembershipView, InvitationView, AdminAuditView, AccessReviewQueueView, supplies queue, decision-card view, fulfillment view, or outcome view projections rebuild, when queried, then filters and redaction still enforce selected AuthContext.

## Policy and approval regression cases

- Given active supply policy changes version, when an old recommendation is refreshed, then the trace and decision output cite the policy version actually applied and do not silently apply stale authority.
- Given policy clauses `SUP-1.0` through `SUP-5.0` and `OFF-3.0`, when edge cases are replayed, then low-risk auto-ship remains bounded and abnormal, high-cost, substitution, offboarding, missing-evidence, or policy-denied cases do not regress into auto-approval.
- Given a human overrides or rejects an agent recommendation, when similar future evidence appears, then the system preserves the precedent/reference-example link as guidance or proposal evidence but does not activate new policy automatically.
- Given support access expires or is revoked, when a support operator retries a formerly valid supplies or audit action, then the action is denied even if a prior browser route or trace link is reused.

## Frontend regression cases

- Given UI capability hints change after context switch, role change, support-access expiry, tenant suspension, or disabled account status, when the browser refreshes `/api/me`, then hidden/visible actions update without relying on stale frontend authorization.
- Given stale or replayed realtime events arrive for supplies queues, decision cards, admin audit, or access review, when the UI processes them, then rows are updated idempotently and not duplicated.
