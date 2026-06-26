# Workers: User Admin

User Admin follows the current skills-pack worker model: every action is traced through `worker -> execution harness -> actor adapter -> governed tool -> capability -> Akka implementation`.

Worker bindings in this directory:

- `saas-owner-admin-human.md` — human SaaS Owner/App Admin worker for app-owner administrators, Organizations, and Organization Admin bootstrap/maintenance.
- `organization-admin-human.md` — human Organization/Tenant Admin worker for tenant employees, Customers, Customer Admin bootstrap/maintenance, and support access.
- `customer-admin-human.md` — human Customer Admin worker for Customer Users inside one selected Customer scope.
- `user-admin-functional-agent-worker.md` — user-facing `user-admin-agent` workstream assistant / functional-agent worker.
- `access-review-agent-worker.md` — bounded model-backed access-review specialist worker; advisory only.
- `invitation-onboarding-system-worker.md` — deterministic invitation delivery, acceptance, expiry, and onboarding system worker.
- `admin-audit-projection-system-worker.md` — deterministic audit/projection/read-model worker for User Admin evidence and attention.

These worker artifacts do not grant authority. Backend capability authorization, selected `AuthContext`, tenant/customer ownership, policy, approval gates, idempotency, and audit/work traces remain authoritative for every adapter.
