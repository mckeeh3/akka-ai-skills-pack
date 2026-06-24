# Policies: Agent Admin

## Scope

Agent Admin policy is intentionally simple and SaaS-admin-only.

## Authorization policy

- Only SaaS Owner/Admin users may use Agent Admin.
- Tenant/organization/customer-scoped Agent Admin access is out of scope.
- Authorized SaaS admins may view unredacted prompt, skill, and skill reference docs for all agents.

## Editing policy

- Edits are AI-assisted, not direct text editing.
- The editing agent preserves Markdown and existing structure unless the user asks to reorganize.
- Unsafe or out-of-scope requests produce an explanation and safer alternative.
- Warnings/risks are advisory only and do not block Save for authorized SaaS admins.
- Save immediately creates a new current version and updates runtime behavior.
- There is no separate publish, activation, approval, or rollback policy in Agent Admin beyond version restore.

## Version policy

- Versions are immutable and retained.
- Historical versions are read-only.
- Edit input is enabled only on the current/latest version.
- Diffs compare selected version `N` only to `N-1`.
- Restore creates a new current version and is recorded in history.

## Deletion policy

- Skills and reference docs can be permanently deleted by SaaS admins.
- Deleting a skill permanently deletes its reference docs.
- Deleted skills/reference docs cannot be restored.
- Whole agents cannot be created or deleted in Agent Admin.
