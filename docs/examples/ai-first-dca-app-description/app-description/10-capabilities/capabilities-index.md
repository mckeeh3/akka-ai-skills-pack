# Capabilities Index

This inventory is reference material for the skills pack's DCA vertical app-description example. It extends the canonical secure AI-first SaaS seed with DCA lifecycle-operations capabilities. In a target project, the equivalent `app-description/10-capabilities/` tree belongs to that project and should be maintained as the application's source of truth.

Capabilities are governed backend operations or queries. Endpoints, workflows, agents, entities, timers, consumers, and UI actions are selected exposure or realization surfaces; they are not capability roots.

## Capability inventory

| Capability id | File | Class | Primary actors/callers | Protected scope | Selected exposure surfaces |
|---|---|---|---|---|---|
| `secure-tenant-user-foundation` (`CAP-00`) | `01-secure-tenant-user-foundation.md` | command, read/evidence, workflow, scheduled, trace/audit, policy/governance | SaaS Owner Admin, Tenant Admin, Customer Admin, Auditor, tenant member, invited user, support operator, admin-assistant agents, invitation workflow, expiry/reminder timers, Resend email/outbox consumer | authenticated account, selected `AuthContext`, SaaS Owner/Tenant/Customer scope, active membership, role/permission/capability grants, support-access grant, billing-boundary authority | browser foundation/admin UI, JWT HTTP APIs including `/api/me`, admin views, invitation/support-access workflows, expiry/reminder timers, Resend email/outbox consumer, scoped admin-agent tools |
| `lifecycle-orchestration` (`CAP-01`) | `02-lifecycle-orchestration.md` | workflow, command, read/evidence, approval | Tenant operations supervisor, dealer owner, lifecycle coordinator agent, installation/service workflows, customer admin where delegated | selected tenant/customer, customer lifecycle authority, device lifecycle authority, collector lifecycle authority, lifecycle gate permissions | lifecycle operations UI, HTTP APIs, workflow steps, lifecycle views, decision cards, scoped agent recommendations |
| `telemetry-intelligence` (`CAP-02`) | `04-telemetry-intelligence.md` | reactive, read/evidence, scheduled, proposal | DCA telemetry ingest service, tenant operations users, telemetry analyst agent, scheduled refresh/recheck timers, exception workflow | selected tenant/customer, device/collector scope, telemetry ingest permission, data-source trust boundary | service/event ingestion surface, scoped telemetry views, scheduled health/depletion refresh, exception decision cards, read-only evidence tools |
| `supplies-autopilot` (`CAP-03`) | `03-supplies-autopilot.md` | workflow, proposal, approval, command, read/evidence, reactive, scheduled, trace/audit | Tenant operations supervisor, dealer owner, supplies/inventory owner, supplies coordinator agent, contract and policy agent, inventory agent, telemetry consumer/service, approval workflow, recheck timers, fulfillment integration caller | selected tenant/customer, device/contract entitlement scope, supplies policy grants, shipment/approval permission, inventory/shipping integration boundary | supplies autopilot UI, JWT HTTP APIs, workflow steps, scoped evidence views, decision cards, policy-gated agent tools, backend-only integration calls, consumers, timers, work/outcome traces |
| `service-coordination` (`CAP-04`) | `05-service-coordination.md` | workflow, proposal, approval, command, read/evidence, reactive | Tenant service dispatcher, operations supervisor, service coordinator agent, telemetry/fault consumer, technician/service integration caller | selected tenant/customer, device/service-ticket scope, SLA policy grants, dispatch/remote-fix permissions | service coordination UI, HTTP APIs, workflow steps, ticket/fault views, decision cards, scoped agent recommendations, integration calls |
| `meter-billing-review` (`CAP-05`) | `06-meter-billing-review.md` | read/evidence, proposal, approval, workflow, command | Tenant billing analyst, dealer owner, billing-review agent, meter telemetry consumer, billing integration caller | selected tenant/customer, meter-read scope, contract/billing authority, billing-impact approval permission | billing review UI, HTTP APIs, review workflow, anomaly/evidence views, decision cards, scoped agent recommendations, billing integration handoff |
| `onboarding-installation` (`CAP-06`) | `07-onboarding-installation.md` | workflow, command, read/evidence, approval | Tenant onboarding coordinator, customer admin, installation coordinator agent, installer/service integration caller | selected tenant/customer, customer onboarding scope, device/collector installation scope, contract mapping authority | onboarding UI, HTTP APIs, installation workflow, lifecycle views, decision cards, customer-safe status views, scoped agent recommendations |
| `offboarding-retention` (`CAP-07`) | `08-offboarding-retention.md` | workflow, command, approval, scheduled, read/evidence | Tenant operations supervisor, dealer owner, customer admin where delegated, offboarding coordinator agent, retention/expiry timers | selected tenant/customer, customer/device/collector offboarding scope, retention policy, final billing and deauthorization permissions | offboarding UI, HTTP APIs, offboarding workflow, retention timers, decision cards, audit/evidence views, scoped agent recommendations |
| `policy-governance` (`CAP-08`) | `09-policy-governance.md` | policy/governance, proposal, approval, read/evidence, trace/audit | Policy Owner, dealer owner, operations supervisor, governance reviewer, policy proposal agent, evaluator agent | selected tenant/customer, policy family, approval authority, activation/rollback permission, policy simulation data boundary | governance UI, HTTP APIs, policy review/approval workflow, simulation/evidence views, decision cards, scoped proposal tools, audit traces |
| `owner-command-center` (`CAP-09`) | `10-owner-command-center.md` | read/evidence, command, approval | Dealer owner, operations supervisor, auditor, agent coordinator, decision/workflow callers | selected tenant/customer, objective/work/decision visibility, role-scoped operational and audit permissions | command-center UI, HTTP APIs, dashboard/evidence views, decision queue, supervision actions, realtime/progress surfaces, scoped summary tools |
| `audit-outcome-review` (`CAP-10`) | `11-audit-outcome-review.md` | trace/audit, read/evidence, scheduled | Auditor, dealer owner, operations supervisor, SaaS support with support access, audit summary agent, outcome evaluator, retention/export timers | selected tenant/customer, audit visibility grant, support-access grant, outcome metric boundary, retention/export policy | audit/outcome UI, HTTP APIs, audit/outcome views, scheduled digests/retention/export jobs, scoped summary/evaluation tools |

## Capability contract expectations

Each capability contract should define or link to:

- purpose, in-scope outcomes, and out-of-scope outcomes;
- actors/callers and `AuthContext` with tenant/customer scope, role, permission, and named capability grants;
- input/output schemas, validation, safe denial/error shape, redaction, idempotency, and correlation expectations;
- data access boundaries, side effects, policy/approval/escalation rules, and autonomy level;
- audit/work-trace obligations and retention/redaction expectations;
- selected exposure surfaces or explicit non-exposure;
- links to operating-model, behavior, tests, auth/security, observability, UI, generation, and traceability artifacts.

## Mandatory secure SaaS foundation

`01-secure-tenant-user-foundation.md` is the first capability contract for the DCA vertical reference. It defines the required secure SaaS substrate: `Account`, `UserProfile`, `UserSettings`, `Membership`, `Role`, `Permission/Capability`, `Invitation`, `AuthContext`, `/api/me`, `AdminAuditEvent`, support access, SaaS Owner to Tenant billing boundary, admin read models, and tenant/customer isolation. DCA-specific capabilities must consume this foundation instead of redefining authentication or authorization.

## Current lifecycle vocabulary

The app centers DCA work around three lifecycle families. These are domain state vocabularies used by multiple capabilities, not capability roots by themselves.

### Customer lifecycle

```text
Lead / Prospect
→ Acquired / Contract Pending
→ Onboarding Planned
→ Installation In Progress
→ DCA Validation
→ Operational / Active Service
→ At Risk / Exception State
→ Renewal / Expansion
→ Reduction / Device Change
→ Offboarding Planned
→ Removal In Progress
→ Decommissioned
→ Archived
```

### Device lifecycle

```text
Planned
→ Ordered / Allocated
→ Staged
→ Install Scheduled
→ Installed
→ DCA Discovered
→ Operational
→ Monitored
→ Needs Service
→ Supply Risk
→ Under Review
→ Replacement Candidate
→ Removal Scheduled
→ Removed
→ Decommissioned
→ Returned / Disposed / Reassigned
→ Archived
```

### DCA collector lifecycle

```text
Required
→ Install Scheduled
→ Installed
→ Connected
→ Discovering Devices
→ Healthy
→ Offline
→ Needs Update
→ Misconfigured
→ Removal Scheduled
→ Removed
→ Deauthorized
→ Archived
```

## First detailed vertical slice: supplies autopilot

The first detailed DCA capability contract, `03-supplies-autopilot.md`, proves the operating model with bounded automation:

1. receive or refresh device consumable telemetry;
2. forecast depletion and urgency;
3. verify contract entitlement and lifecycle state;
4. check inventory and shipment constraints;
5. auto-ship only when policy allows;
6. escalate abnormal, expensive, offboarding, or ambiguous cases with a decision card;
7. record trace and outcome data.

## Scope boundaries

The example intentionally preserves CRM, ERP, and DCA concepts as source/integration domains, but the product boundary is lifecycle operations. It may read from or write to existing systems, yet its own authoritative description focuses on goals, delegated work, policies, decisions, traces, and outcomes.

Lightweight capability entries intentionally avoid inventing vendor-specific thresholds, API contracts, pricing rules, or shipment/billing semantics before future detailed contract tasks define accepted details.
