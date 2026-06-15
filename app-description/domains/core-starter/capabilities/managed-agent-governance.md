# Capability: Managed agent governance

## Purpose

Let authorized agent stewards administer tenant-scoped managed agent behavior records, governed prompts, skills, references, manifests, tool permission boundaries, model policy references, seed imports, proposals, activation, rollback, and runtime trace review.

## Actors and scope

- Tenant/Organization Admin: the only human role that may open Agent Admin and create, review, approve, activate, or roll back managed-agent behavior changes through a selected `TENANT_ADMIN` / `tenant-admin` AuthContext with explicit `agent_admin.*` capabilities.
- Customer Admins and customer-scoped users: not authorized for Agent Admin reads or behavior changes, because managed-agent behavior records are tenant/organization-scoped rather than customer-scoped.
- Auditor: may read audit/trace evidence through Audit/Trace capabilities, but audit access alone does not allow Agent Admin catalog, prompt, skill, reference, manifest, model, tool-boundary, seed, or prompt-risk task reads.
- Agent Admin functional agent: explains, drafts proposals, identifies risk, and guides safe activation only for authorized tenant/organization admins; it cannot grant itself tools, data scope, model access, or approval authority.

## Governed tools and exposure

- `list-agent-catalog` (`browser-tool`, `agent-tool` read): scoped managed agents and status.
- `read-agent-behavior-detail` (`browser-tool`, `agent-tool` read): current versions, manifests, boundaries, model refs, and traces with redaction.
- `draft-agent-behavior-proposal` (`browser-tool`, `agent-tool` proposal): creates draft/proposed changes.
- `approve-activate-or-rollback-agent-behavior` (`browser-tool` approval): human-governed activation/rollback only.
- `readSkill` and `readReferenceDoc` (`agent-tool` loaders): authorized by manifest, scope, and tool boundary and traced.

## Authorization and denials

Backend policy, version state, approval gates, tenant/customer scope, `ToolPermissionBoundary`, and provider fail-closed rules are authoritative. Prompt text, expertise text, or rail visibility cannot expand authority.

## Outcomes

In scope: governed behavior lifecycle, compact expertise manifests, loader denials/traces, seed provenance, customization-preserving upgrades, safe model provider boundary, and behavior change decision cards.

Out of scope: arbitrary Java class registration from tenant content, unapproved tool expansion, preloading all skill/reference text into prompts, and model-less runtime substitutes for model-backed behavior.

## Linked graph nodes

- Workstream: `../workstreams/agent-admin/workstream.md`
- Tests: `../workstreams/agent-admin/tests/coverage.md`
- Traces: `../workstreams/agent-admin/traces/work-traces.md`
