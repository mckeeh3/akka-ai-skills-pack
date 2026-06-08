# Global Actors: Foundation actors

- `authenticated-human`: WorkOS/AuthKit-authenticated browser user linked to Akka-owned authorization state.
- `functional-agent`: user-facing workstream agent acting within governed managed-agent configuration and backend tool boundaries.
- `internal-worker`: service, workflow, consumer, timed action, or background/agent worker executing only authorized internal capabilities.
- `auditor-investigator`: human with trace/audit responsibilities and redacted read access.
- `policy-reviewer`: human who approves or rejects high-impact governance, behavior, and authority changes.
- `provider-boundary`: external WorkOS/AuthKit, Resend, or model provider integration that must keep secrets server-side and fail closed when unavailable.
