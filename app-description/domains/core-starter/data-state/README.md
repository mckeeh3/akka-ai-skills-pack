# Core Starter Data and State

Data-state nodes describe durable state responsibilities, lifecycle rules, invariants, retention, projections/views, capability ownership, governed-tool ownership, and trace obligations for the built-in core starter domain.

## Shared data-state link convention

Each state node should record:

- owning capability ids and governed tool families;
- allowed actor adapters and worker/system paths that read or mutate the state;
- tenant/customer/account scope, retention, redaction, and secret/PII boundaries;
- projections/views or dashboard/result surfaces that expose browser-safe summaries;
- audit/work trace obligations and denial evidence;
- tests and runtime-validation scenario ids or explicit gaps.

Data/state files do not implement runtime storage. Workstream and realization files bind these responsibilities to Akka components, APIs, frontend surfaces, source-alignment evidence, and checks.

Current nodes:

- `auth-context-and-membership-state.md` — account, profile, settings, Organization/Tenant, Customer, membership, invitation, support-access, identity-linking, first-admin bootstrap, and selected `AuthContext` state.
- `managed-agent-behavior-state.md` — managed-agent behavior documents, proposals, activation/rollback, manifests, tool boundaries, and model policy state.
- `notification-and-attention-state.md` — personal notifications, attention projections, source-opening metadata, and notification preferences.
- `workstream-audit-governance-state.md` — workstream logs, audit/admin events, traces, current Governance/Policy defaults, tenant overrides, effective-policy decisions, history, and governance outcomes. Deferred export, investigation-note, and summary lifecycle state remains non-current unless later accepted by current intent.
