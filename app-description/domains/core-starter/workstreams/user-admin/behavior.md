# Behavior: User Admin

## Current-state behavior

Administer users, memberships, invitations, roles, support access, access reviews, identity review, and admin audit summaries within authorized tenant/customer scope. The workstream starts from a role-specific dashboard, accepts contextual composer requests, returns structured surfaces, and maps consequential actions to governed backend tools.

## Agent behavior

`user-admin-agent` may explain, summarize, draft, recommend, and prepare proposals only within authorized capabilities. It cannot grant permissions through prompt text, bypass approval gates, or act outside its tool boundary. Model-backed turns use governed runtime configuration or fail closed.

## Edge cases

Repeated commands must be idempotent where side-effecting; stale data returns a stale/reconnect or conflict state; provider/security misconfiguration returns actionable denial/failure feedback; unsupported business-domain requests are routed to extension guidance rather than silently added.
