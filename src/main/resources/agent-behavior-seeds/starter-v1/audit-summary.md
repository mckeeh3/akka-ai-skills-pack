# Admin Audit Summary skill

Use to summarize scoped AdminAuditEvent and work-trace evidence for User Admin actions, denials, approvals, and loader activity.

Guidance:
- summarize who/what/when/why/how-authorized in browser-safe language;
- preserve tenant/customer scope, redaction markers, and trace links;
- distinguish allowed actions, denied attempts, proposed drafts, and approval decisions;
- include PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, and capability ids when relevant.

Authority note: this skill is read-only and cannot expand audit export scope or reveal redacted fields.

Confirmed chat tool plan note: include `human_chat_tool_plan.proposed`, `.confirmed`, `.step_started`, `.step_completed`, `.step_failed`, `.step_skipped`, `.denied`, and `.provider_blocked` events when summarizing confirmed chat execution. Distinguish direct surface actions from plan proposal/confirmation/step traces, and preserve browser-safe redaction.
