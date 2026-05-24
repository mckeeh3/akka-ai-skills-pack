# TASK-WSAGENT-03-001: Add real provider smoke validation for Akka Agent path

## Objective

Add explicit validation that a local generated starter can submit a workstream prompt through the API/backend path, invoke the Akka Agent component, use a real configured provider, emit traces, and render/return a `markdown_response` without secrets.

## Required reads

- AGENTS.md
- templates/ai-first-saas-starter/README.md
- templates/ai-first-saas-starter/.env.example
- templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/WorkstreamServiceTest.java
- templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/*
- tools/validate-ai-first-saas-starter-fullstack.sh
- tools/scaffold-ai-first-saas-starter.sh

## Expected outputs

- Add or update an optional real-provider smoke test/script that is skipped by default when provider env vars are absent.
- The smoke must exercise the normal message path, not a direct provider call: selected context + functional agent + workstream message endpoint/service + Akka Agent component + provider-backed response + trace shape.
- The smoke must assert that `OPENAI_API_KEY` or any provider secret is not present in `/api/me`, workstream items, surfaces, trace summaries, frontend env, or logs produced by the smoke script.
- Update fullstack validation to report clearly whether the real-provider Akka Agent smoke ran or was skipped due to missing env.
- Document exact local commands to run the smoke with a real provider.

## Required checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- Run the provider smoke in skip mode with provider env absent.
- If provider env is available, run the provider smoke in real mode and capture the command/result in the task completion note.
- `git diff --check`
- `rg -n "Akka Agent smoke|provider smoke|OPENAI_API_KEY|skip|real provider|workstream message" tools templates/ai-first-saas-starter`

## Done criteria

- Maintainers can distinguish CI-safe tests from real local provider validation.
- There is a documented command that proves the actual Akka Agent-backed workstream path with a real model provider.
- Task status is updated in `specs/workstream-akka-agent-runtime/pending-tasks.md`.
- A focused git commit exists with message `workstream-agent: add real provider smoke`.
