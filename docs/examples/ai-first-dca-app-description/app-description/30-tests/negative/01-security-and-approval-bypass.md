# Negative Tests: Security, Tenant Isolation, and Approval Bypass

## Behavior or capabilities under test

- linked capability ids/classes: `CAP-00` secure tenant/user foundation and `CAP-03` supplies autopilot
- exposure surfaces: browser UI/API, scoped views, workflows, timers, consumers, integration callers, and agent tools

## Foundation security negative cases

- Given an unauthenticated browser or API request, when it calls any protected route, view query, stream, workflow action, timer-triggered action requiring authority, or agent tool, then the system rejects the request with a safe unauthenticated response and emits required denial evidence where applicable.
- Given an authenticated account with no active Membership in the selected Tenant/Customer, when it attempts any DCA protected operation, then the system denies access without leaking whether the target resource exists.
- Given a disabled account, inactive membership, expired support-access grant, or suspended tenant, when the caller invokes protected UI/API/tool/workflow actions, then access is denied and no domain side effect occurs.
- Given a user in tenant A, when they try to list, read, mutate, stream, export, or tool-access tenant B or customer B data, then no cross-tenant/customer data appears in responses, views, traces, logs, agent context, or UI state.
- Given a caller lacks required foundation role/scope/capability, when they invite users, resend/revoke invites, assign roles, manage memberships, grant support access, read admin audit, mutate billing-boundary metadata, or change tenant/customer settings, then the system denies the action.
- Given a SaaS Owner Admin without tenant-created support access, when they attempt to inspect Tenant application data or DCA operational records, then access is denied even though they can manage platform-safe Tenant subscription metadata.
- Given a Tenant Admin attempts to remove, suspend, or demote the last active Tenant Admin, when last-admin protection applies, then the action is blocked or routed to an explicit decision-card flow and cannot silently complete.
- Given an invitation is expired, revoked, cross-scope, already accepted by another subject, or delivery-failed without an accepted override, when acceptance is attempted, then no Account/Membership is activated and the denial is auditable.
- Given frontend assets or browser-visible API payloads are inspected, when WorkOS, integration, supplier, JWT, invite-token, or provider secret values exist on the backend, then none of those secrets are emitted to the browser.

## Supplies authorization and tenant-isolation negative cases

- Given a user or support operator lacks `supplies.evidence.read`, when they request supplies queues, recommendation detail, inventory evidence, entitlement summaries, trace drawers, or outcome links, then the system denies or resource-hides safely.
- Given a caller lacks `supplies.recommendation.create`, when they request `EvaluateSupplyNeed` or `RefreshSupplyRecommendation`, then no recommendation, card, order, trace side effect beyond denial evidence, or integration call is created.
- Given a caller lacks `supplies.shipment.approve`, when they approve, reject, modify, defer, escalate, or request evidence on a decision card, then the decision remains pending and an audit/work trace records the denial.
- Given a browser request, agent tool, workflow step, timer, consumer, or service identity carries mismatched tenant/customer/device ids, when it attempts a supplies action, then tenant/customer isolation rejects the call without revealing unrelated records.
- Given an unauthorized service identity or consumer, when it reacts to telemetry, lifecycle, entitlement, inventory, fulfillment, or outcome events, then it cannot create recommendations, cards, orders, suppressions, or traces outside its accepted service ACL.

## Approval-bypass negative cases

- Given abnormal consumption above `SUP-3.0`, high cost under `SUP-4.0`, substitution/preference conflict under `SUP-5.0`, offboarding context under `SUP-2.0`/`OFF-3.0`, stale/conflicting telemetry, missing entitlement, low confidence, or ambiguous lifecycle, when an agent, UI action, API caller, workflow retry, timer, or consumer attempts auto-ship, then the system creates or keeps a decision card/suppression instead of committing shipment.
- Given an agent drafts a recommendation, when it tries to submit a high-impact shipment, activate a policy change, widen its own tool authority, or bypass approval through prompt instructions, then backend authorization and policy checks deny the side effect.
- Given a reviewer modifies a decision outside the card's allowed item, quantity, provider, timing, or scope, when the workflow resumes, then the modification is rejected and no fulfillment command is created.
- Given a policy-denied case, when a human or agent retries with the same evidence but without the required approval/policy change, then the denial or suppression remains in force.

## Validation negative cases

- Given malformed email, invalid role, unsupported scope, invalid support-access expiry, missing tenant/customer id, unsafe idempotency key, or unsupported context switch, when a foundation command is submitted, then validation fails safely.
- Given missing device assignment, stale telemetry beyond active policy, invalid policy version, unsupported consumable mapping, missing contract entitlement, unsupported customer preference, unknown supply item, or invalid reviewer action, when supplies commands are submitted, then validation fails safely and does not create fulfillment side effects.
