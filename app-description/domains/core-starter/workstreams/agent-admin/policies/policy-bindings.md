# Policies: Agent Admin

## Scope

Agent Admin policy is SaaS-admin-only managed-agent governance. It governs behavior profiles, prompts, skills, references, manifests, model-policy selections, generated-tool assignments, tool-boundary references, safe test-console use, proposals, approvals, activations, denials, and traces.

## Authorization policy

- Only SaaS Owner/Admin users may use Agent Admin.
- SaaS app owners operate in the reserved `saas-app-owner` tenant scope; broader tenant/organization/customer operator access remains out of scope unless explicitly added later.
- Authorized SaaS admins may view unredacted prompt, skill, and governed reference docs for all agents in their authorized scope.
- Authorized SaaS admins may inspect safe behavior-profile summaries including placement, lifecycle status, steward, authority level, model alias, manifests, allowed generated tools, tool-boundary categories, resolved scope, test-console status, and trace links.
- Provider secrets, generated tool implementation internals, hidden platform instructions, and hidden backend policy internals are never exposed.

## Editing and proposal policy

- Edits are AI-assisted and proposal-first, not direct mutation of active runtime behavior.
- The editing agent preserves Markdown and existing structure unless the user asks to reorganize.
- Unsafe, authority-expanding, model-policy-expanding, tool-boundary-expanding, provider-secret-seeking, tenant-scope-expanding, or out-of-scope requests produce denial, safer alternative, or review/decision-card routing.
- The editing agent returns structured proposals with proposed content or profile delta, diff, rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, expected result surface, and trace links.
- Save creates a non-active draft/proposal version; activation is a separate protected backend action.
- Low-risk copy/clarity drafts may be reviewed and activated immediately by the same authorized SaaS admin as the documented foundation simplification.
- Medium/high-risk, authority-expanding, model-policy, tool-boundary, approval-boundary, provider, or tenant-scope proposals must be denied or routed to decision-card/review instead of direct activation.
- Prompt/skill/reference text cannot grant backend authority, generated tool implementation access, model/provider access, tenant/customer scope, role capability, approval authority, autonomous side effects, or loader/tool access outside manifests and `ToolPermissionBoundary`.

## Confirmation and approval policy

- Consequential `human_chat_tool_plan` execution requires explicit confirmation bound to the exact proposed plan.
- Approval-required authority expansion includes increased autonomy, expanded tool-boundary grants, broader model/provider policy, broader tenant/customer scope, approval-boundary changes, and attempts to convert behavior guidance into authority.
- The editing agent and user-facing functional agent cannot approve or activate their own proposals.
- Approval/activation re-checks SaaS-admin authority, proposal status, current-version consistency, artifact lifecycle, model policy, provider/config availability when relevant, and tool-boundary decisions.

## Version, idempotency, and deletion policy

- Prompt, skill, reference, and agent behavior-profile versions are immutable and retained.
- Historical versions are read-only.
- Edit input is enabled only on the current/latest editable draft or active document.
- Diffs compare selected version `N` only to `N-1`.
- Restore creates a restore proposal; activation creates a new current active version and records `Restored from version N`.
- Repeated save/activate/confirm operations with the same idempotency key return the existing result or no-op surface rather than duplicate side effects.
- Skills and references are deprecated by default; hard deletion requires lifecycle policy and explicit confirmation.
- Deleting/deprecating a skill or reference removes/reassigns affected manifest entries and loader access without leaving hidden orphaned access.
- Whole agents cannot be created or deleted in Agent Admin.

## Provider, test-console, and tool-boundary policy

- Test-console provider-backed runs are allowed only for authorized test/replay/evaluation modes with active approved model config and provider runtime configuration.
- Missing provider/runtime config, inactive model config, disabled/archived agent, missing prompt/manifest/docs, unassigned loaders, or denied boundary grants fail closed with provider/config blocker or loader/tool-boundary denial traces.
- Test-console mode does not perform production side effects unless an explicitly modeled safe test tool and approval are present.
- Runtime tools `readSkill` and `readReferenceDoc` require active manifest assignment and separate tool-boundary grants.
- Generated tool assignment is a protected, auditable behavior-profile change; actual runtime use still requires backend capability authorization and `ToolPermissionBoundary` enforcement.
