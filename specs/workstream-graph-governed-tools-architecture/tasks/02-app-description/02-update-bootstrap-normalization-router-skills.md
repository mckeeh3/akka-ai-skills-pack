# TASK-WGGT-02-002: Update bootstrap, normalization, and router skills

## Objective

Update initial and incremental app-description intake skills to extract workstream graphs and governed-tools from user input.

## Required reads

- AGENTS.md
- skills/README.md
- specs/workstream-graph-governed-tools-architecture/README.md
- skills/app-description-bootstrap/SKILL.md
- skills/app-description-input-normalization/SKILL.md
- skills/app-description-intake-router/SKILL.md

## In scope

- Add workstream count/boundary extraction.
- Add role-specific dashboard and attention extraction.
- Add surface graph extraction.
- Add internal workstream agent graph and internal worker delegation extraction.
- Add governed-tool/browser-tool/agent-tool extraction.
- Add incremental existing-app reconciliation cues.

## Checks

- `git diff --check`
- Focused term search over edited skills.

## Done criteria

- Intake skills normalize and route through the new model.
- Queue updated and committed.
