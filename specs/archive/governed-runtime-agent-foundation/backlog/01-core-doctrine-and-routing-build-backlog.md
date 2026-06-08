# Backlog 01: Core doctrine and routing

## Purpose

Elevate governed runtime agent behavior management into the mandatory core AI-first SaaS foundation.

## Delivery goal

After this backlog, a harness reviewing the getting-started prompt should plan a managed-agent foundation with agent definitions, governed prompts, governed skills, skill manifests, tool boundaries, dynamic skill loading, editing agents, traces, and UI surfaces.

## Capability contracts

- `core.agent-definition.manage`: tenant-scoped agent lifecycle, owner/steward, authority, prompt/skill/model/tool refs, audit.
- `core.agent-prompt.manage`: governed prompt draft/review/approval/activation through editing-agent proposals.
- `core.agent-skill.manage`: governed skill draft/review/approval/activation through editing-agent proposals.
- `core.agent-manifest.manage`: assign active approved skills to agents through manifests.
- `core.agent-tool-boundary.manage`: maintain per-agent tool permissions with approval for authority expansion.
- `core.agent-runtime.assemble-context`: assemble active prompt plus compact skill manifest and trace it.
- `core.agent-runtime.read-skill`: authorized `readSkill(skillId)` with SkillLoadTrace.

## Suggested harness task breakdown

1. Update doctrine and foundation skill guidance.
2. Update agent/admin-agent routing guidance.
3. Update README/getting-started description so expected output includes the governed runtime agent foundation.

## Done criteria

- Doctrine and routing make the managed-agent substrate mandatory for generated AI-first SaaS foundations.
- Admin-agent responsibilities may be realized by fewer governed skilled agents.
- No direct-human-edit-only model is presented as the normal prompt/skill maintenance path.
