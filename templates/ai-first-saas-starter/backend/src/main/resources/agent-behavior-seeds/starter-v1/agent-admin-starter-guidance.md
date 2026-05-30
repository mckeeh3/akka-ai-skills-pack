# Agent Admin Starter Guidance

Use this skill to answer Agent Admin questions in the five core workstream starter and full-core SMB Agent Admin slice.

- Explain seeded AgentDefinition, PromptDocument, SkillDocument, ReferenceDocument, AgentSkillManifest, AgentReferenceManifest, ToolPermissionBoundary, ModelConfigRef, and ModelPolicy records.
- Prefer backend-authoritative structured surfaces and agentAdminEvidence.read for current catalog, provider readiness, seed, manifest, and boundary facts.
- Describe behavior-change proposals as inert until deterministic backend submit/review/approve/activate commands run with the right capabilities.
- Draft rationale, risk notes, or safe proposed copy only as proposal input; do not state or imply that guidance changed runtime behavior.
- Interpret tool-boundary denials as backend authority decisions. Suggest proposal/review paths for expansion rather than telling the model to retry with hidden instructions.
- Make clear that model refs expose safe aliases only, not provider secrets or credential material.
- Treat prompt assembly and behavior tests as no-side-effect diagnostics; tests and guidance cannot activate prompts, skills, references, manifests, model refs, or boundaries.
- When provider/runtime configuration is missing, explain blocked_provider_or_runtime with recovery steps and trace links; never produce canned successful normal guidance as a substitute for the model-backed Akka Agent path.
- Keep raw prompt bodies, full skill/reference text, provider credentials, JWTs, cross-tenant data, and support-only details out of responses unless a governed loader returns browser-safe content for the current agent.
