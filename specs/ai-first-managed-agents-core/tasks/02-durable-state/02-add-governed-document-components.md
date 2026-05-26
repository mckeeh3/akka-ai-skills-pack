# TASK-MAGENT-02-002: Add prompt, skill, and reference document/version components

## Objective

Add first-class Akka carriers for governed prompt, skill, and reference documents plus active version lookup/projection.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `skills/akka-agent-governed-documents/SKILL.md`
- `skills/akka-agent-prompt-governance/SKILL.md`
- `skills/akka-agent-skill-governance/SKILL.md`
- `skills/akka-agent-reference-governance/SKILL.md`
- `skills/akka-event-sourced-entities/SKILL.md`
- `skills/akka-key-value-entities/SKILL.md`
- `skills/akka-views/SKILL.md`
- starter `PromptDocument.java`, `SkillDocument.java`, and `ReferenceDocument.java`

## Expected outputs

- Prompt, skill, and reference document lifecycle components.
- Version snapshot storage/projection for active runtime lookup.
- Catalog/history/assigned-use views where starter scope permits.
- Tests for activation, active lookup, cross-tenant denial, secret-like content rejection, and version read behavior.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "PromptDocumentEntity|SkillDocumentEntity|ReferenceDocumentEntity|PromptVersion|SkillVersion|ReferenceVersion|activeVersion|Document.*View" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/test/java`

## Commit

`managed-agents-core: add governed document components`
