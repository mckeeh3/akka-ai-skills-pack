# Global Current-Intent Definitions

Reusable graph definitions live here. Workstream files bind these reusable definitions to specific access, behavior, worker, execution-harness, actor-adapter, surface, agent, tool, policy, trace, test, and realization contexts.

Shared current graph nodes:

- `actors/` — identity/caller categories only; actor labels do not grant authority.
- `roles/` — reusable role and selected `AuthContext` semantics.
- `workers/` — reusable worker contracts, harnesses, actor adapter vocabulary, authority boundaries, trace obligations, and source-alignment convention.
- `agents/` — reusable functional-agent identities tied to governed managed-agent behavior profiles.
- `policies/` — reusable backend authorization, isolation, provider, approval, retention, redaction, and billing-boundary policies.
- `surfaces/` — workstream shell and surface/action envelope patterns.
- `tools/` — governed tool ids, canonical id/alias rules, exposure adapters, result/transaction semantics, and deferred tool scope.
- `traces/` — shared audit/work trace sources, required facts, redaction, retention scope, and runtime-validation evidence expectations.
