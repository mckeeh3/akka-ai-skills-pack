# Policies: Agent Admin

## Scope

Agent Admin policy is intentionally simple and SaaS-admin-only.

## Authorization policy

- Only SaaS Owner/Admin users may use Agent Admin.
- SaaS app owners operate in the reserved `saas-app-owner` tenant scope; broader tenant/organization/customer operator access remains out of scope unless explicitly added later.
- Authorized SaaS admins may view unredacted prompt, skill, and governed reference docs for all agents in their authorized Agent Admin scope.
- Authorized SaaS admins may inspect safe behavior-profile summaries including placement, lifecycle status, steward, authority level, model alias, manifests, allowed generated tools, tool-boundary categories, resolved scope, and trace links; provider secrets, generated tool implementation internals, and hidden backend policy internals are never exposed.

## Editing policy

- Edits are AI-assisted, not direct text editing.
- The editing agent preserves Markdown and existing structure unless the user asks to reorganize.
- Unsafe, authority-expanding, or out-of-scope requests produce an explanation and safer alternative or review/decision-card route.
- The editing agent returns structured proposals with proposed content, diff, rationale, risk classification, authority-expansion flags, and suggested tests/replay evidence.
- Save creates a non-active draft/proposal version; activation is a separate protected backend action.
- Low-risk copy/clarity drafts may be reviewed and activated immediately by the same authorized SaaS admin as the documented foundation simplification.
- Medium/high-risk, authority-expanding, backend tool-boundary implementation, approval-boundary, tenant-scope, or secret-like proposals must be denied or routed to decision-card/review instead of direct activation.
- Changing an agent's allowed generated tool list is a protected, auditable behavior-profile change but is not automatically authority-expanding solely because a generated tool is added.
- Prompt/skill/reference text cannot grant backend authority, generated tool implementation access beyond the resolved allowed tool list, model/provider access, tenant/customer scope, role capability, approval authority, or autonomous side effects.

## Version policy

- Prompt, skill, reference, and agent behavior-profile versions are immutable and retained.
- Historical versions are read-only.
- Edit input is enabled only on the current/latest editable draft or active document.
- Diffs compare selected version `N` only to `N-1`.
- Restore creates a restore proposal; activation creates a new current active version and is recorded in history.

## Deletion policy

- Skills are independently managed tenant-scoped library artifacts and can be deprecated by default or permanently deleted by SaaS admins only when lifecycle policy permits hard deletion and confirmation requirements are met.
- Deleting/deprecating a skill must remove, deprecate, or reassign affected references, agent assignments, and manifest entries without leaving hidden loader access.
- Permanently deleted skills/references cannot be restored.
- Whole agents cannot be created or deleted in Agent Admin.
