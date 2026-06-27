# Sprint 02: Runtime loading and trace alignment

## Goal

Ensure active runtime agent behavior resolves from activated behavior-profile/docs only and exposes browser-safe trace evidence required by the Agent Admin current intent.

## Scope

- Runtime profile resolution fallback: tenant-specific active profile, then global active profile.
- Prompt assembly from active prompt and compact assigned skill/reference hints.
- Authorized `readSkill` / `readReferenceDoc` loading only for assigned active documents.
- Generated-tool assignment and tool-boundary decision checks.
- Trace metadata for profile resolution, prompt assembly, model policy, skill/reference reads, generated tool decisions, and tool-boundary denials.

## Runtime proof target

`backend-ready` for loader/tool-boundary behavior and trace records; model/provider success remains fail-closed unless real provider config exists.
