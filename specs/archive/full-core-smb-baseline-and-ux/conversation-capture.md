# Conversation Capture: Full-Core SMB Baseline and UX

## Accepted goals

- Use the five-core v0 starter as the executable proving ground for SMB full-core hardening.
- Keep workstreams, functional agents, structured surfaces, governed capabilities, traces, and visual quality as the product architecture.
- Start Wave 1 with a shared baseline/UX child project and User Admin as the first highest-leverage workstream child.

## Constraints

- This repository develops the skills pack; changes are source assets and executable reference material for downstream generated apps.
- SMB scope avoids enterprise IAM/SIEM/legal hold/compliance-suite features unless later selected.
- User-facing model-backed behavior must invoke concrete Akka Agent runtime paths and fail closed when provider configuration is missing.
- AutonomousAgent/internal workers are only for durable background/task lifecycle work; deterministic services retain authorization, validation, idempotency, policy, redaction, and trace normalization.

## Risks

- Shared UX contracts could become too abstract unless tied to starter validation.
- Visual polish could imply runtime readiness that does not exist.
- Workstream children could drift into page-first CRUD without explicit surface/capability contracts.

## Unresolved questions

None blocking this child queue. Implementation tasks may append pending questions if local starter constraints expose missing decisions.
