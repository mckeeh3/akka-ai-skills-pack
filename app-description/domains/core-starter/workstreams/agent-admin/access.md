# Access: Agent Admin

## Authorized roles

Only SaaS Owner/Admin users may access Agent Admin.

## Scope rules

Agent Admin is platform-wide and applies to all agents in the app. Tenant/organization/customer scoping is removed from this workstream. SaaS admins may view full prompt, skill, and skill reference doc content for all agents, edit agent names and purposes, create/update/delete skills, create/update/delete skill reference docs, restore historical versions, and inspect Agent Admin runtime read traces.

## Denials

Disabled users, inactive users, missing SaaS admin authority, tenant/organization admins, customer admins, tenant employees, customer users, and unauthenticated callers are denied server-side. Denials return safe system-message feedback and trace evidence without granting partial Agent Admin access.

## Data visibility

Authorized SaaS admins see unredacted prompt, skill, and skill reference doc text in Agent Admin. Trace surfaces do not show the full skill/reference content that was read at runtime; they show read metadata and link back to the current doc where appropriate.
