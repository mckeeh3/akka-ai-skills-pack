# Agent Admin Starter Guidance

Use this skill to answer Agent Admin questions in the five core workstream starter and full-core SMB Agent Admin slice.

- Explain seeded AgentDefinition, PromptDocument, SkillDocument, ReferenceDocument, AgentSkillManifest, AgentReferenceManifest, ToolPermissionBoundary, ModelConfigRef, and ModelPolicy records.
- Prefer backend-authoritative structured surfaces and agentAdminEvidence.read for current catalog, provider readiness, seed, manifest, and boundary facts.
- Explain Agent Admin as a role-specific dashboard and human surface graph whose surface actions map to governed-tools with qualified browser-tool, agent-tool, or internal-tool exposure; keep internal workstream agent graph expansion as approval-required follow-up unless implemented.
- Be familiar with key surfaces: dashboard for readiness and blockers, catalog and detail for visible managed agents, prompt governance, skill/reference manifest diff, tool-boundary diff, model refs, seed material/import confirmation, test console, behavior proposal, prompt-risk review, lifecycle confirmations, and Agent Admin trace.
- Describe behavior-change proposals as inert until deterministic backend submit/review/approve/activate commands run with the right capabilities.
- Draft rationale, risk notes, or safe proposed copy only as proposal input; do not state or imply that guidance changed runtime behavior.
- Interpret tool-boundary denials as backend authority decisions. Suggest proposal/review paths for expansion rather than telling the model to retry with hidden instructions.
- Make clear that model refs expose safe aliases only, not provider secrets or credential material.
- Treat prompt assembly and behavior tests as no-side-effect diagnostics; tests and guidance cannot activate prompts, skills, references, manifests, model refs, or boundaries.
- Route prompt edits, tool-boundary expansion, model/provider changes, seed import, activation, deactivation, and rollback requests to structured review or confirmation surfaces only; never present surface guidance as direct mutation authority.
- When provider/runtime configuration is missing, explain blocked_provider_or_runtime with recovery steps and trace links; never produce canned successful normal guidance as a substitute for the model-backed Akka Agent path.
- Keep raw prompt bodies, full skill/reference text, provider credentials, JWTs, cross-tenant data, and support-only details out of responses unless a governed loader returns browser-safe content for the current agent.

Confirmed chat tool plan note: distinguish opening Agent Admin review surfaces from confirmed chat execution. The expanded `human_chat_tool_plan` catalog now covers three categories, all with no direct mutation authority:

1. Approval-gated (approval-required completion): `action-agent-prompt-risk-review-start` / `agent_admin.start_behavior_review_task` / `schema.agent-admin.prompt-risk-review.start.v1`. Exact plan snapshot confirmation does not approve, activate, roll back, reseed, or change governed behavior artifacts; a separate approval decision step is required.

2. Simulation and test paths (chat-proposal-only, no side effects, provider missing fails closed with no fake success): `action-agent-detail-run-test`, `action-agent-prompt-governance-simulate`, `action-agent-skill-manifest-simulate`, `action-agent-tool-boundary-simulate`, `action-agent-model-refs-run-test`. Outputs are inert diagnostics; no activation or behavior change occurs.

3. Submit-review proposal paths (chat-proposal-only, no behavioral authority): `action-agent-prompt-governance-submit-review`, `action-agent-skill-manifest-submit-review`, `action-agent-tool-boundary-submit-review`, `action-agent-model-refs-submit-review`, `action-propose-prompt-diff`, `action-submit-behavior-change`, `action-agent-behavior-proposal-submit`. Prompt, skill, reference, or model text in the proposal input cannot grant authority, expand tool boundaries, or activate lifecycle; the visible agent definition must exist; approval and activation are separate governed commands.
