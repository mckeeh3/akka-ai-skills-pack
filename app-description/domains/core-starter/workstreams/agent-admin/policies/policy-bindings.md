# Policies: Agent Admin

## Scope

Agent Admin policy is intentionally simple and SaaS-admin-only.

## Authorization policy

- Only SaaS Owner/Admin users may use Agent Admin.
- Tenant/organization/customer-scoped Agent Admin access is out of scope.
- Authorized SaaS admins may view unredacted prompt, skill, and governed reference docs for all agents.
- Authorized SaaS admins may inspect safe behavior-profile summaries including placement, lifecycle status, steward, authority level, model alias, manifests, tool-boundary categories, and trace links; provider secrets and hidden backend policy internals are never exposed.

## Editing policy

- Edits are AI-assisted, not direct text editing.
- The editing agent preserves Markdown and existing structure unless the user asks to reorganize.
- Unsafe, authority-expanding, or out-of-scope requests produce an explanation and safer alternative or review/decision-card route.
- The editing agent returns structured proposals with proposed content, diff, rationale, risk classification, authority-expansion flags, and suggested tests/replay evidence.
- Save creates a non-active draft/proposal version; activation is a separate protected backend action.
- Low-risk copy/clarity drafts may be reviewed and activated immediately by the same authorized SaaS admin as the documented foundation simplification.
- Medium/high-risk, authority-expanding, model-policy, tool-boundary, approval-boundary, tenant-scope, or secret-like proposals must be denied or routed to decision-card/review instead of direct activation.
- Prompt/skill/reference text cannot grant backend authority, tools, model/provider access, tenant/customer scope, role capability, approval authority, or autonomous side effects.

## Version policy

- Versions are immutable and retained.
- Historical versions are read-only.
- Edit input is enabled only on the current/latest editable draft or active document.
- Diffs compare selected version `N` only to `N-1`.
- Restore creates a restore proposal; activation creates a new current active version and is recorded in history.

## Deletion policy

- Skills and references can be deprecated or permanently deleted by SaaS admins according to lifecycle policy and confirmation requirements.
- Deleting a skill must remove, deprecate, or reassign affected references and manifest entries without leaving hidden loader access.
- Permanently deleted skills/references cannot be restored.
- Whole agents cannot be created or deleted in Agent Admin.
