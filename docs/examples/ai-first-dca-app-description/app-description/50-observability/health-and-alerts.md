# Health and Alerts

## Purpose

Define health signals, alert-worthy conditions, and operational diagnosis expectations for the DCA vertical reference. Alerts should be selective and tied to conditions that require human, support, or automated remediation.

## Health signals

- Backend/API readiness and protected-route authorization health.
- `/api/me`, context selection, admin views, and invitation workflow availability.
- Email/outbox delivery health for invitations and reminders.
- Critical view/projection lag for admin, command-center, decision-card, audit, and outcome read models.
- Workflow health for invitation, support access, lifecycle, supplies, service, billing review, onboarding, offboarding, policy review, export/retention, and outcome follow-up flows.
- Consumer health for telemetry, inventory, fulfillment, email/outbox, trace enrichment, and outcome measurement events.
- Timer health for invitation expiry, support-access expiry, access review, supplies rechecks, decision deadlines, offboarding retention, scheduled digests, and outcome windows.
- Agent/tool health for recommendation, summary, evidence, policy, evaluator, and admin-assistant tools.
- Integration health for WorkOS seam, Resend email service/outbox, telemetry source, inventory/fulfillment systems, service/ticketing systems, billing systems, and export sinks when configured.

## Alert-worthy conditions

Alert thresholds should be configured by future realization policy, but these condition classes are mandatory planning inputs:

- Repeated authorization failures, cross-tenant/customer attempts, disabled-user attempts, or unusual support-access use.
- Invitation delivery failure spikes, expired high-priority invites, or stuck Tenant/Admin bootstrap.
- Admin audit write failures, missing trace records for consequential actions, or audit/outcome projection lag affecting investigation surfaces.
- Stuck high-priority lifecycle, onboarding, offboarding, service, or supplies workflows.
- Decision queues or approval cards stale beyond configured SLA, especially high-risk supplies/service/billing/policy decisions.
- Agent/tool failure spikes, low-confidence recommendation clusters, evaluator drift, or repeated human overrides of the same policy/agent path.
- Telemetry ingestion gaps, stale collector/device data, forecast quality degradation, or large rejected telemetry batches.
- Fulfillment, inventory, service, billing, email, or export integration failures with retry exhaustion or duplicate-risk warnings.
- Policy activation/rollback failures, simulation/replay failures, or missing human approval for material governance changes.
- Outcome measurement jobs missing data, delayed beyond window, or showing negative safety/business outcomes that exceed configured thresholds.

## Diagnosability expectations

For every alert or health degradation, authorized operators should be able to determine:

- affected tenant/customer/resource/workflow/capability;
- latest successful step and current state (`retrying`, `waiting_for_approval`, `blocked`, `suppressed`, `failed`, `terminal`, or `no-op`);
- responsible actor, service, timer, consumer, integration, or agent/tool;
- policy clause, permission/capability, evidence snapshot, and idempotency key involved;
- visible next action, owner role, and command-center/decision/audit surface link;
- safe correlation id for support without exposing secrets.

## Recovery and escalation

- Authorization, audit-write, support-access, and trace-completeness failures default to restricted operation and human/security review.
- Integration failures remain visible, idempotent, and auditable; retries must not duplicate invitations, shipments, billing handoffs, exports, or policy commits.
- Timer/consumer replay must preserve original authority/correlation context or record an explicit trusted scheduled authority basis.
- Agent failures fall back to human review or deterministic workflow state, not silent autonomous action.
