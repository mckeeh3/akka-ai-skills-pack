# TASK-MAGENT-04-001: Update docs, skills, and validation gates for AI-first managed agents

## Objective

Elevate AI-first managed agents as a primary core generated-app feature alongside workstreams and surfaces, and add validation gates for the mandatory runtime path.

## Required reads

- `AGENTS.md`
- `README.md`
- `pack/AGENTS.md`
- `skills/README.md`
- `docs/ai-first-saas-application-architecture.md`
- `docs/agent-workstream-application-architecture.md`
- `docs/agent-runtime-invocation-pattern.md`
- `docs/minimum-ai-first-saas-app.md`
- `docs/agent-coverage-matrix.md`
- `templates/ai-first-saas-starter/README.md`
- `tools/validate-ai-first-saas-starter-fullstack.sh`

## Expected outputs

- Doctrine states AI-first managed agents are a first-class core app architecture pillar.
- Starter README/gates require runtime config resolution, `.tools(runtimeTools)`, governed loader tools, and traces.
- Skills/routing say generated SaaS agents must use the managed runtime path unless explicitly non-SaaS/reference-only.
- Validation detects missing runtime tool registration in starter workstream runtime agent.

## Checks

- `tools/validate-ai-first-saas-starter-fullstack.sh`
- `git diff --check`
- `rg -n "AI-first managed agents|configuration-driven|effects\(\)\.tools|runtimeTools|readSkill|readReferenceDoc|ToolPermissionBoundary|workstreams|surfaces" AGENTS.md pack/AGENTS.md README.md skills docs templates/ai-first-saas-starter tools`

## Commit

`managed-agents-core: update docs and gates`
