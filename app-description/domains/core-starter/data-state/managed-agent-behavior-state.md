# Data State: Managed agent behavior state

## Responsibility

Tenant-scoped `AgentDefinition`, `PromptDocument`, `SkillDocument`, `ReferenceDocument`, manifests, tool permission boundaries, model config references, seed import provenance, runtime assembly records, and agent runtime traces.

## Lifecycle and invariants

- Default behavior records for the five foundation workstream agents are created as governed records with provenance, checksums, active versions, idempotency, and upgrade behavior that preserves tenant customizations; the core starter begins with these default managed agents active rather than awaiting initial manual activation.
- Draft/proposed changes require review and approval before activation when they affect behavior, policy, model, or tool authority.
- Prompt/skill/reference content cannot grant backend authority.
- Loader tools return full text only after manifest, scope, version, redaction, and tool-boundary authorization.
- Model-backed workstream behavior uses the configured provider boundary or fails closed. Missing model provider configuration is a serious runtime issue: agent turns, advisory tasks, and model-backed readiness surfaces must show blocked/fail-closed recovery and trace evidence rather than hiding the problem or returning model-less success.

## Retention and traces

Prompt assembly, skill/reference loads, tool invocations, model calls, denials, provider failures, proposals, approvals, activations, and rollbacks are traceable through durable work/audit traces.
