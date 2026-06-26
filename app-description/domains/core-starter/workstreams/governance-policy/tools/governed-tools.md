# Tools: Governance/Policy

## Uses

Global tool inventory: `../../../../../global/tools/foundation-governed-tools.md`.

## Workstream exposure

Canonical capability id for every tool in this file: `governance-policy-lifecycle`.

Exact actor adapters replace older ambiguous exposure labels. Browser realization normally uses `surface_action` plus protected `api_call`; model-backed reads use `agent_tool_call` only when the `governance-policy-agent` tool boundary grants them; side-effecting chat execution uses `human_chat_tool_plan` only after exact human confirmation and backend reauthorization.

## Governed-tool contracts

### `governance.policy.list`

- toolType: `search_or_list`
- purpose: read the visible policy catalog, supported scopes, effective-value summaries, and overridden indicators.
- allowedWorkerTypes: human, functional-agent, system
- allowedActorAdapters: `surface_action`, `agent_tool_call`, `api_call`, `internal_call`
- authorityLevel: observe
- authContextScope: backend-selected SaaS-owner/defaults or tenant/customer/account context with read capability.
- inputSchema: search/filter/page/sort request with policy name text, workstream, agent, tool/action, role, and visible scope hints.
- outputSchema: scoped/redacted inventory surface rows with value type, default, visible override, effective value, supported scopes, winning scope, action availability, freshness, and trace refs.
- validationAndSafeDefaults: unsupported filters are rejected or ignored with validation state; hidden scopes are omitted.
- redactionAndEvidenceRules: no hidden tenant/customer/account facts, raw secrets, raw prompts/model payloads, raw tool payloads, raw correlation/idempotency internals.
- confirmationPolicy: none.
- approvalPolicy: none.
- idempotencyAndTransactionBoundary: read-only scoped query.
- sideEffects: optional read/workstream trace only.
- resultSurfacesAndEvents: `surface-governance-policy-dashboard`, `surface-governance-policy-inventory`, or `system_message`.
- denialBehavior: safe forbidden/not-found-or-redacted system state with trace ref when visible.
- implementationMapping: governance policy service/repository, workstream/admin endpoint, frontend list/search surfaces, agent evidence read tool.
- requiredTests: scoped inventory, filters, tenant isolation, support/auditor boundaries, redaction, trace links, agent read-tool boundary.

### `governance.policy.read_effective`

- toolType: `read_evidence`
- purpose: read one policy's default, visible override, effective value, winning scope, precedence explanation, and runtime decision semantics.
- allowedWorkerTypes: human, functional-agent, system
- allowedActorAdapters: `surface_action`, `agent_tool_call`, `api_call`, `internal_call`
- authorityLevel: observe/evaluate
- authContextScope: selected context plus policy/scope read capability; runtime `internal_call` uses service/caller provenance.
- inputSchema: policy id, requested scope, optional runtime decision context, freshness token where applicable.
- outputSchema: effective-detail surface/result with default, override, effective value, source, winning scope, last-change summary, reset availability, decision explanation, and trace refs.
- validationAndSafeDefaults: unsupported policy ids/scopes/value types return validation/not-found-or-redacted; browser-supplied scope is an untrusted hint.
- redactionAndEvidenceRules: scoped/redacted values and summaries only.
- confirmationPolicy: none.
- approvalPolicy: none.
- idempotencyAndTransactionBoundary: read-only query or runtime check; repeated checks may create deduped policy-decision traces where practical.
- sideEffects: policy-decision trace for runtime checks; optional workstream read trace for UI/agent reads.
- resultSurfacesAndEvents: `surface-governance-policy-effective-detail` or `system_message`.
- denialBehavior: safe denial without confirming hidden policy/scope existence.
- implementationMapping: effective-policy evaluator, governance service/repository, trace services, detail surface, agent read tool.
- requiredTests: precedence, source explanation, hidden target redaction, runtime decision trace, agent read-tool boundary.

### `governance.policy.set_default`

- toolType: `admin`
- purpose: SaaS-owner/defaults-context update of a simple boolean or counter/limit default.
- allowedWorkerTypes: human, system
- allowedActorAdapters: `surface_action`, `human_chat_tool_plan`, `api_call`
- authorityLevel: administer
- authContextScope: authenticated SaaS owner admin in SaaS-owner/defaults selected context with default-management capability.
- inputSchema: policy id, value type, requested default value, required reason, idempotency key, correlation id, optional freshness token.
- outputSchema: refreshed effective/default summary, history ref, trace ref, validation blockers, and action availability.
- validationAndSafeDefaults: validate catalog membership, value type, supported default scope, reason, stale version, and hard-platform boundary.
- redactionAndEvidenceRules: never expose hidden tenant overrides except safe aggregate/impact copy when authorized; no raw internals.
- confirmationPolicy: surface submit or exact chat-plan snapshot confirmation; no mutation during proposal.
- approvalPolicy: no additional approval in current SMB scope unless future policy adds one.
- idempotencyAndTransactionBoundary: one default update per policy/scope/idempotency key; replay returns existing result without duplicate history/traces.
- sideEffects: default value update, history entry, effective-value recomputation for tenants without overrides, audit/work trace; no default notification.
- resultSurfacesAndEvents: `surface-governance-policy-edit`, `surface-governance-policy-effective-detail`, `surface-governance-policy-history`, or `system_message`.
- partialFailureBehavior: no partial cross-tenant overwrite; failed recomputation/trace produces safe failure and does not masquerade as success.
- denialBehavior: tenant admins/support/auditors/cross-context callers denied; hard-platform controls denied.
- implementationMapping: governance service/repository, workstream/admin endpoint, edit surface, confirmed chat-plan executor.
- requiredTests: SaaS-owner write, tenant override non-overwrite, reason required, idempotency, stale conflict, hard-platform denial, chat-plan confirmation.

### `governance.policy.set_override`

- toolType: `command`
- purpose: tenant-admin update of a tenant-owned business-governance override for an authorized scope.
- allowedWorkerTypes: human, system
- allowedActorAdapters: `surface_action`, `human_chat_tool_plan`, `api_call`
- authorityLevel: execute
- authContextScope: active tenant admin membership with override capability for the selected tenant/customer/account scope.
- inputSchema: policy id, target scope, value type, requested override value, required reason, idempotency key, correlation id, optional freshness token.
- outputSchema: refreshed effective-policy summary, override indicator, history ref, trace ref, validation blockers, and action availability.
- validationAndSafeDefaults: validate catalog membership, supported tenant scope, value type, reason, stale version, hidden target, and hard-platform boundary.
- redactionAndEvidenceRules: browser-safe scoped values only; no hidden customer/account enumeration.
- confirmationPolicy: surface submit or exact chat-plan snapshot confirmation; no mutation during proposal.
- approvalPolicy: no additional approval in current SMB scope unless future policy adds one.
- idempotencyAndTransactionBoundary: one override update per policy/scope/idempotency key; replay returns existing result without duplicate history/traces.
- sideEffects: tenant override active immediately, history entry, audit/work trace, effective-policy recomputation; no default notification.
- resultSurfacesAndEvents: `surface-governance-policy-edit`, `surface-governance-policy-effective-detail`, `surface-governance-policy-history`, or `system_message`.
- partialFailureBehavior: single override transaction succeeds or fails; no silent partial write.
- denialBehavior: missing capability, SaaS-owner/defaults context, cross-tenant/customer, hidden target, unsupported scope/type, missing reason, and hard-platform override attempts denied safely.
- implementationMapping: governance service/repository, workstream/admin endpoint, edit surface, confirmed chat-plan executor.
- requiredTests: tenant override, precedence, reason/idempotency, hidden target denial, hard-platform denial, chat-plan confirmation.

### `governance.policy.reset_override`

- toolType: `command`
- purpose: tenant-admin removal of a visible tenant override so effective value falls back to inherited/default behavior.
- allowedWorkerTypes: human, system
- allowedActorAdapters: `surface_action`, `human_chat_tool_plan`, `api_call`
- authorityLevel: execute
- authContextScope: active tenant admin membership with override capability for the selected tenant/customer/account scope.
- inputSchema: policy id, target scope, required reason, idempotency key, correlation id, optional freshness token.
- outputSchema: refreshed effective-policy summary, removed override marker, inherited/default value, history ref, trace ref, validation blockers, and action availability.
- validationAndSafeDefaults: validate override exists or return idempotent no-op where safe; reject unsupported/hidden/hard-platform scopes.
- redactionAndEvidenceRules: no hidden override enumeration; values are scoped/redacted.
- confirmationPolicy: surface submit or exact chat-plan snapshot confirmation; no mutation during proposal.
- approvalPolicy: no additional approval in current SMB scope unless future policy adds one.
- idempotencyAndTransactionBoundary: one reset per policy/scope/idempotency key; replay returns existing result without duplicate history/traces.
- sideEffects: override removal, effective-value recomputation, history entry, audit/work trace.
- resultSurfacesAndEvents: `surface-governance-policy-edit`, `surface-governance-policy-effective-detail`, `surface-governance-policy-history`, or `system_message`.
- partialFailureBehavior: single reset transaction succeeds or fails; no silent partial state.
- denialBehavior: missing authority, hidden/cross-scope target, stale version, hard-platform override, or missing reason denied safely.
- implementationMapping: governance service/repository, workstream/admin endpoint, edit surface, confirmed chat-plan executor.
- requiredTests: reset-to-default, inherited value, idempotency/no duplicate history, hidden target denial, chat-plan confirmation.

### `governance.policy.read_history`

- toolType: `read_evidence`
- purpose: read authorized direct policy changes and practical runtime outcome links.
- allowedWorkerTypes: human, functional-agent, system
- allowedActorAdapters: `surface_action`, `agent_tool_call`, `api_call`
- authorityLevel: observe
- authContextScope: selected context with policy history/audit read capability; support requires active scoped support access.
- inputSchema: policy/scope/actor/time/filter/page request with browser-provided ids treated as hints.
- outputSchema: scoped/redacted history timeline, runtime outcome links, actor display summaries, reasons, values, trace refs, and redaction metadata.
- validationAndSafeDefaults: unsupported filters produce validation state; hidden events are omitted or redacted.
- redactionAndEvidenceRules: no raw trace payloads, secrets, prompts/model/tool payloads, hidden facts, or raw idempotency/correlation internals.
- confirmationPolicy: none.
- approvalPolicy: none.
- idempotencyAndTransactionBoundary: read-only scoped query.
- sideEffects: optional read/workstream trace.
- resultSurfacesAndEvents: `surface-governance-policy-history` or `system_message`.
- denialBehavior: safe forbidden/not-found-or-redacted response without hidden event enumeration.
- implementationMapping: governance history repository, audit/workstream trace services, history surface, agent read tool.
- requiredTests: auditor/support scoping, redaction, runtime outcome links, filters, tenant isolation, agent read-tool boundary.

## Surface-intent and `human_chat_tool_plan` catalog

Deterministic no-mutation surface routing runs first for read/open/prefill requests. Prompts like `show policy settings`, `show effective policy for SalesAgent email`, and `show overridden policies` should open or prefill `surface-governance-policy-inventory` or `surface-governance-policy-effective-detail` through surface-intent routes, not execute a chat plan.

Confirmed `human_chat_tool_plan` is reserved for side-effecting command plans:

| Classification | Representative prompts | Surface action ids | Shared governed tool ids | Capability id | Result surface(s) |
|---|---|---|---|---|---|
| `chat-proposal-only` | `allow SalesAgent to send emails immediately`; `reset this policy to default`; `set max retries to 3`; `set the SaaS default for this policy to false` | `action-governance-policy-set-override`; `action-governance-policy-reset-override`; `action-governance-policy-set-default` | `governance.policy.set_override`; `governance.policy.reset_override`; `governance.policy.set_default` | `governance-policy-lifecycle` | `surface-governance-policy-edit`; `surface-governance-policy-effective-detail`; `surface-governance-policy-history`; `system_message` |

Blocked or out-of-scope entries:

- complex policy scripts or arbitrary rule expressions;
- policy simulations or impact-analysis tasks;
- legal compliance workflows;
- policy-edit approval workflows;
- default notifications for policy changes;
- enterprise delegation models;
- any request to override hard platform security controls.

Execution requirements for every accepted confirmed plan:

- validate catalog membership, supported policy type, supported scope, selected context, actor capability, exact plan snapshot, required reason for writes, idempotency, freshness, and trace emission;
- recompute and return effective policy after writes;
- reject hidden scope targets, unsupported value types, cross-tenant/customer scope, missing reasons, stale versions, and hard platform-security overrides;
- idempotent replay returns prior write results without duplicate history or traces;
- no workstream agent, prompt, frontend route, visible control, or tool description grants authority beyond backend authorization.
