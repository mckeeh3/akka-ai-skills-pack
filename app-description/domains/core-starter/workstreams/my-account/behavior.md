# Behavior: My Account

## Current-state behavior

Give the signed-in human a safe AI-first personal control point for selected authority context, personal attention, profile, named-theme/settings preferences, in-app notifications, governed digest/export requests, and safe recovery from unavailable or denied workstream/source openings. The workstream starts from a personal command-center dashboard, accepts contextual composer requests, returns structured surfaces, and maps consequential actions to governed backend tools.

## Agent behavior

`my-account-agent` may explain, summarize, draft, recommend, and prepare proposals only within authorized capabilities. It cannot grant permissions through prompt text, bypass approval gates, or act outside its tool boundary. Model-backed turns use governed runtime configuration or fail closed.

## Edge cases

Repeated commands must be idempotent where side-effecting; stale data returns a stale/reconnect or conflict state; provider/security misconfiguration returns actionable denial/failure feedback; unsupported profile/settings fields, hidden context/workstream openings, and external-provider notification controls are denied safely; unsupported business-domain requests are routed to extension guidance rather than silently added.
