# Backlog 02: Doctrine Consolidation

## Goal

Make the requirements-to-workstream process canonical enough for installable skills to depend on.

## Suggested task breakdown

1. Create or promote a canonical process doc from the WIP.
2. Update core AI-first/workstream/surface/capability/autonomous-agent docs with concise cross-links and normative rules.

## Implementation notes

- Prefer one compact canonical process doc plus targeted references in existing doctrine.
- Do not duplicate the entire WIP everywhere.
- Preserve terminology guardrails for request-based Akka `Agent`, Akka `AutonomousAgent`, Akka autonomous `AgentDefinition`, and governed managed-agent `AgentDefinition`.

## Required checks

- `git diff --check`
- `rg -n "requirements-to-workstream|what needs my attention|WorkstreamAttentionSummary|AutonomousAgent|dashboard" docs skills/README.md`

## Acceptance criteria

- Canonical docs explain the process and link to component selection where needed.
