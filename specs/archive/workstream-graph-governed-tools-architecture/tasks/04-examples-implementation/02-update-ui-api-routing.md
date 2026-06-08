# TASK-WGGT-04-002: Update UI/API and browser-tool routing

## Objective

Update UI/API docs and web UI skills so browser interactions are modeled as surface graph actions invoking browser-tools backed by governed-tools.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- docs/web-ui-api-contract-patterns.md
- docs/web-ui-frontend-decomposition.md
- docs/web-ui-ux-patterns.md
- docs/workstream-ui-reference-architecture.md
- skills/akka-web-ui-apps/SKILL.md
- skills/akka-web-ui-api-client/SKILL.md
- skills/akka-web-ui-state-rendering/SKILL.md
- skills/akka-web-ui-ux-design/SKILL.md

## In scope

- Browser-tool term and governed-tool contract mapping.
- Surface graph transitions and result/system-message surface handling.
- Role dashboard attention sources and freshness/evidence display.

## Checks

- `git diff --check`
- Focused term search over edited docs/skills.

## Done criteria

- UI/API guidance implements surface graphs and browser-tools.
- Queue updated and committed.
