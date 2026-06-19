# Behavior: Agent Admin

## Current-state behavior

Govern managed-agent definitions, prompts, skills, references, manifests, tool boundaries, model refs, seed imports, behavior proposals, activation, rollback, and runtime traces. The workstream starts from a role-specific dashboard, accepts contextual composer requests, returns structured surfaces, and maps consequential actions to governed backend tools.

## Agent behavior

`agent-admin-agent` may explain, summarize, draft, recommend, and prepare proposals only within authorized capabilities. It cannot grant permissions through prompt text, bypass approval gates, or act outside its tool boundary. Model-backed turns use governed runtime configuration or fail closed.

## Lifecycle and command behavior

Managed-agent behavior changes follow the canonical state machine in `../../capabilities/managed-agent-governance.md`. Draft prompt, skill, reference, manifest, tool-boundary, model-ref, seed-import, and behavior-profile changes are inert until submitted, reviewed, approved, and explicitly activated through backend capability checks. Approval records evidence and reviewer intent only; activation, rollback, and deactivation are separate commands that revalidate active scope, current version, provider/runtime readiness, tool-boundary compatibility, model policy, approval freshness, idempotency, and trace obligations.

Prompt-risk review and seed-import work are durable advisory task paths. Starting, reading, cancelling, accepting, or rejecting their results updates task/result disposition and evidence links only. These tasks cannot directly activate behavior, expand tools, weaken policy, load unassigned skills/references, or bypass approval. Model/provider/security/tool-boundary misconfiguration produces blocked task or system-message states rather than model-less acceptable results.

Seed imports preserve tenant customizations by default: imported defaults carry provenance/checksum metadata, conflict summaries, proposed version diffs, rollback references, and human confirmation before activation. Loader tools (`readSkill`, `readReferenceDoc`) may return full content only after active-agent assignment, manifest version, scope, redaction, token, and ToolPermissionBoundary checks pass; denied loads are traced and surfaced as recoverable denials, not silent omissions or prompt-granted authority.

## Edge cases

Repeated commands must be idempotent where side-effecting; stale data returns a stale/reconnect or conflict state; provider/security misconfiguration returns actionable denial/failure feedback; unsupported business-domain requests are routed to extension guidance rather than silently added.
