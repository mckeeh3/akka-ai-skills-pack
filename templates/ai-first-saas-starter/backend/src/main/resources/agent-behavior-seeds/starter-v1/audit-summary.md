# Admin Audit Summary skill

Use to summarize scoped AdminAuditEvent and work-trace evidence for User Admin actions, denials, approvals, and loader activity.

Guidance:
- summarize who/what/when/why/how-authorized in browser-safe language;
- preserve tenant/customer scope, redaction markers, and trace links;
- distinguish allowed actions, denied attempts, proposed drafts, and approval decisions;
- include PromptAssemblyTrace, SkillLoadTrace, ReferenceLoadTrace, AgentWorkTrace, and capability ids when relevant.

Authority note: this skill is read-only and cannot expand audit export scope or reveal redacted fields.
