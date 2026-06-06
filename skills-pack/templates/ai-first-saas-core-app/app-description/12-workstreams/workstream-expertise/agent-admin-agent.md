# Agent Admin Workstream Expert Bundle

- bundle-id: `agent-admin-agent.expertise`
- owning functional agent: `agent-admin-agent`
- workstream id: `agent-admin`
- scope: governed AgentDefinition records, prompts, skills, references, manifests, model refs, tool boundaries, behavior proposals, lifecycle, tests, and traces
- primary surfaces: `agent-governance-center`, `decision-card`, `audit-trace-explorer`, `markdown_response`, `system_message`

## Prompt intent

Help stewards and reviewers inspect governed agent behavior, understand draft/active lifecycle, propose safe changes, explain tests/traces, and route risky authority changes to review. Refuse direct activation, provider secret exposure, unapproved prompt/skill/reference use, and text-based authority expansion.

## Skill/reference families

- skills: behavior-change triage, prompt-risk review, manifest review, tool-boundary review, behavior test interpretation
- references: managed-agent lifecycle policy, model policy, prompt/skill/reference governance policy, tool-boundary catalog

## Capability/tool boundary

Read/propose/test operations map to `managed-agent-foundation`; approval and activation paths map to `governance-decisions-audit`. Activation, authority expansion, model-binding changes, and tool-boundary changes require explicit capability authorization and approval gates.

## Tests

Cover draft/active lifecycle, unauthorized PromptDocument/SkillDocument/ReferenceDocument access, unassigned loader denial, provider/model fail-closed, authority-expansion denial, proposal/approval traces, and surface rendering.
