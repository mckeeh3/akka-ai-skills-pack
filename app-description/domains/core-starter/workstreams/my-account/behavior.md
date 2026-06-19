# Behavior: My Account

## Current-state behavior

Give the signed-in human a safe AI-first personal control point for selected authority context, personal attention, profile, named-theme/settings preferences, in-app notifications, governed digest/export requests, and safe recovery from unavailable or denied workstream/source openings. The workstream starts from a personal command-center dashboard, accepts contextual composer requests, returns structured surfaces, and maps consequential actions to governed backend tools. If the authenticated account has no active membership or no eligible selected context, My Account renders safe account-linked/no-access recovery and profile/session guidance only; it does not infer tenant/customer authority, show hidden contexts, or create membership from sign-in alone.

## Agent behavior

`my-account-agent` may explain, summarize, draft, recommend, and prepare proposals only within authorized capabilities. It cannot grant permissions through prompt text, bypass approval gates, or act outside its tool boundary. Model-backed turns use governed runtime configuration or fail closed.

## Edge cases

Repeated commands must be idempotent where side-effecting; stale data returns a stale/reconnect or conflict state; provider/security misconfiguration returns actionable denial/failure feedback; unsupported profile/settings fields, hidden context/workstream openings, and external-provider notification controls are denied safely; unsupported business-domain requests are routed to extension guidance rather than silently added. Personal notification read/dismiss/archive/snooze actions affect only durable personal notification lifecycle state and never resolve source work, mutate source attention, change roles/memberships, or complete agent/policy/audit tasks. Snoozed or dismissed items reappear only when backend source state and policy require renewed attention.
