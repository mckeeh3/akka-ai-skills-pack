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

Confirmed chat tool plan note: distinguish opening Agent Admin review surfaces from confirmed chat execution. The representative `human_chat_tool_plan` path may propose `action-agent-prompt-risk-review-start` with governed tool/capability `agent_admin.start_behavior_review_task` and schema `schema.agent-admin.prompt-risk-review.start.v1`, but dispatcher execution remains approval-gated after exact plan snapshot confirmation; confirmation cannot approve, activate, roll back, reseed, or change governed behavior artifacts.
