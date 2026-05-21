# Task: Final consistency review and completion summary

## Objective

Verify the migration has produced a coherent minimum app story and record completion.

## Scope

- Search for generic-chatbot or all-or-nothing foundation drift.
- Make small consistency fixes only.
- Queue follow-up tasks for larger work.
- Create `specs/minimum-ai-first-app-migration/migration-completion-summary.md`.

## Required commands

- `git diff --check`
- `rg -n "generic chatbot|simple chatbot|chatbot" docs skills templates specs --glob '!specs/minimum-ai-first-app-migration/**'`
- `rg -n "minimum AI-first|minimum app|markdown_response|User Admin workstream v0" docs skills templates specs`

## Acceptance

- No canonical guidance says the generated SaaS minimum is a generic chatbot.
- Minimum starter still requires AuthContext, capabilities, workstream log, and traces.
- Completion summary records validation and remaining follow-ups.

## Commit

Make one commit for this task and queue update.
