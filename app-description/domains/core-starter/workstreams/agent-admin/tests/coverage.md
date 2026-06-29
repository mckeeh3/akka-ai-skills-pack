# Tests: Agent Admin

## Acceptance

- Given a SaaS Owner/Admin, when they open Agent Admin with no persisted surface, then a blank workstream renders with Show dashboard, Show agents, Clear workstream, and composer affordances.
- Given a SaaS Owner/Admin, when they open the dashboard, then it shows attention cards for behavior-change proposals, approval-required proposals, provider/config blockers, and denied loader/tool-boundary events before action cards for catalog, skill/reference creation, recently changed agents, test console, and traces.
- Given a SaaS Owner/Admin, when they filter the agent catalog by name, workstream/domain, placement, lifecycle, steward, authority, model-policy alias, or scope provenance, then rows show generated agent name, purpose, placement, lifecycle, safe model alias, resolved profile scope, attention badges, and last edit time and open agent detail.
- Given agent detail, then it shows generated identity/provenance, purpose, placement, lifecycle, steward, authority level, safe model alias/model-policy summary, compact skill/reference manifest summaries, generated-tool assignment, safe tool-boundary summary, governance center, behavior-profile history, test-console entry point, proposal review entry point, and trace entry points; it does not expose whole-agent create/delete, generated tool code editing, provider secrets, raw model-setting mutation, or direct backend tool-boundary implementation mutation.
- Given a `PromptDocument`, `SkillDocument`, `ReferenceDocument`, manifest, model-policy selection, or tool-boundary assignment surface, when the artifact is current/latest editable and the caller is authorized, then edit/proposal input is enabled and all consequential changes create drafts/proposals rather than active runtime mutation.
- Given a historical version, then content, metadata, edit request/transcript summary, optional diff, trace links, and restore action are visible, edit input is disabled, and a read-only banner is shown.
- Given version `N`, when Show diff is requested, then the diff compares only `N` to `N-1`; version 1 has no-prior-version behavior.
- Given a free-form edit request, when the editing agent proposes a change, then the surface shows proposed content or profile delta, proposal id, summary/rationale, risk classification, authority-expansion flags, suggested tests/replay evidence, advisory warnings, expected result/partial-failure surface, additional input, Save Draft, Submit for Review when required, Cancel, and Show diff.
- Given Save Draft, then a non-active immutable proposal version is created and runtime reads remain on the active version.
- Given an approved low-risk proposal and an authorized SaaS admin, when Activate is confirmed, then a new active immutable version/profile is created and runtime reads use it.
- Given a medium/high-risk, authority-expanding, model-policy, tool-boundary, provider, approval-boundary, or tenant-scope proposal, when direct activation is attempted, then the system routes to review/decision card or denies activation and leaves active behavior unchanged.
- Given Reject, then active behavior remains unchanged and rejection rationale is audited.
- Given Cancel, then no active version is created and the current active version is shown.
- Given Restore this version, then a restore proposal is created with content copied from the historical version and edit request `Restored from version N`; activation creates the active restore version.
- Given create skill or reference, then name/title, purpose/description, compact manifest hint or when-to-consult hint, optional access/redaction summary, and editing-agent-drafted content create a non-active tenant-scoped proposal; activation creates the first active version independent of any specific agent assignment.
- Given assign/unassign skill/reference manifest entry or generated tool, then activation creates a new target agent behavior-profile version and does not change document versions or generated tool code.
- Given delete/deprecate skill or reference, then confirmation names the artifact, defaults to deprecation unless lifecycle policy permits hard deletion, lists/counts affected assignments/references/manifests, and confirm performs the configured lifecycle action with no hidden loader access.
- Given safe test-console mode, when provider/model configuration and tool-boundary grants are available, then the console assembles the selected prompt/profile/manifests, runs only authorized test/replay/evaluation behavior, emits traces, labels output as test mode, and avoids production side effects.

## Security and negative

- Non-SaaS-admin callers, tenant/org admins, customer admins, tenant employees, customer users, disabled users, inactive users, unauthenticated callers, and cross-scope SaaS admins are denied server-side.
- Unauthorized `AgentDefinition`/`PromptDocument`/`SkillDocument`/`ReferenceDocument` reads, unassigned skill/reference denial, disabled/archived-agent denial, cross-tenant/customer access, inactive/deleted document access, missing provider/model config, and `ToolPermissionBoundary` denial return safe system-message recovery without hidden enumeration.
- Agent Admin does not create/delete whole agents, create/edit/delete generated tool code, manage provider secrets, mutate raw model settings beyond approved model config reference selection, bypass backend authorization, or activate changes without protected review/activation checks.
- Unsafe or out-of-scope edit requests, including authority expansion attempts through prompt/skill/reference text, produce an explanation and safer alternative or review/decision-card route; authority-expanding proposals cannot be activated directly from the editor.
- Historical versions cannot be directly edited.
- Stale current-version saves or activations are rejected or recovered by backend consistency checks.
- Provider-secret boundary tests prove model/provider keys, WorkOS/Resend secrets, hidden platform instructions, generated tool implementation internals beyond safe summaries, and unapproved tool-boundary internals never appear in browser payloads, prompts, skills, references, test-console output, or trace views.

## Confirmation, idempotency, and partial failure

- Given a `human_chat_tool_plan` contains consequential Agent Admin tools, when the plan is proposed, then no tool executes until the human confirms the exact plan.
- Given a confirmed chat plan contains multiple tool invocations, when one invocation fails, then completed invocations remain consistent according to their transaction contracts and the workstream returns a partial-failure result surface with trace evidence.
- Given the same Save Draft, Activate, Restore, assignment, deprecation, or test-console request is retried with the same idempotency key, then the system returns the prior result/no-op surface rather than duplicate versions or side effects.
- Given an already-active version is activated again, then the system returns an idempotent no-op result and emits trace evidence.

## Runtime and observability

- Each agent request resolves the tenant-specific active behavior profile when present, otherwise the global active behavior profile, then lifecycle status, authority level, current active `PromptDocument`/`PromptVersion`, compact `AgentSkillManifest`, compact `AgentReferenceManifest`, `ModelConfigRef`/model policy, selected `AuthContext`, allowed generated tool list, and `ToolPermissionBoundary`, then loads only the current prompt plus assigned skill/reference names/descriptions/hints.
- Agents can call `readSkill(skillId)` and `readReferenceDoc(referenceId)` only for authorized assigned active documents and can call generated tools only when allowed by the resolved profile, backend capability, and boundary.
- Runtime profile resolution and prompt assembly emit `PromptAssemblyTrace`; skill loads emit `SkillLoadTrace`; reference loads emit `ReferenceLoadTrace`; model-policy/provider decisions emit safe trace facts; generated-tool assignment and `ToolPermissionBoundary` decisions emit allow/deny/approval-required trace facts; model/tool work emits `AgentWorkTrace`.
- Provider-missing or inactive model config fails closed with a provider/config blocker trace rather than fake normal success.
- Loader/tool-boundary denials trace the denied loader/tool id category, agent, profile/manifests, AuthContext, denial reason category, and safe result surface without exposing hidden document bodies or secrets.
- Agent Admin trace surfaces show agent name, resolved profile scope/version, prompt/skill/reference doc read, generated tool decision where applicable, version/checksum where allowed, safe model alias, tool-boundary decision category, mode, timestamp, request/session id, and tenant/customer/user context, filterable by agent, doc/tool, decision, mode, and time range.
- Trace rows do not show full skill/reference content by default.
- Every edit session audit includes user input, editing-agent structured proposal output, saved/proposed/activated version content where allowed, Save Draft/Submit/Review/Approve/Reject/Activate/Cancel outcome, timestamps, actors, risk classification, authority-expansion flags, decision-card links when applicable, and result/partial-failure surface ids.

## Runtime-validation scenarios

The app-description runtime-validation path for future implementation verification must cover: SaaS-admin-only authorization; catalog/detail/governance center; proposal-first prompt/skill/reference/profile edits; approval-required authority expansion; safe test-console provider fail-closed behavior; manifest loader denials; tool-boundary denials; partial-failure chat-plan results; provider secret boundary; `PromptAssemblyTrace`, `SkillLoadTrace`, `ReferenceLoadTrace`, and `AgentWorkTrace` visibility; and frontend/API route mappings.

## UX and formatting

- Prompt, skill, and reference doc content supports Markdown.
- `AgentBehaviorEditorAgent` preserves existing Markdown and structure unless the user requests reorganization.
- Users may Save without opening the diff.
- No live-update behavior is required for concurrent edit sessions; backend consistency is authoritative.
- Browser-rendered surface definitions are sufficient for implementation only when a developer can implement payload fields, actions, states, auth/tenant behavior, trace links, and visual semantics without inventing app meaning.
