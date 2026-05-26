# TASK-MAGENT-03-001: Seed configuration-driven profiles for all five core agents

## Objective

Ensure My Account, User Admin, Audit/Trace, Governance/Policy, and Agent Admin are all seeded and invoked as configuration-driven managed agents.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/minimum-ai-first-saas-app.md`
- `docs/examples/core-ai-first-saas-input/03a-module-agent-workstream-runtime-bootstrap-prd.md`
- `docs/examples/core-ai-first-saas-input/04-module-user-admin-prd.md`
- `docs/examples/core-ai-first-saas-input/08-module-audit-work-trace-prd.md`
- `docs/examples/core-ai-first-saas-input/09-module-evaluation-closed-loop-improvement-prd.md`
- `docs/examples/core-ai-first-saas-input/10-canonical-core-app-prd.md`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java`
- starter agent behavior seed resources

## Expected outputs

- Five distinct active `AgentDefinition` seed profiles.
- Distinct prompts, manifests, model refs, and tool boundaries for each core functional agent.
- Tests proving all five resolve through the same managed runtime path.
- Regression preventing accidental shared generic manifests unless intentionally documented.

## Checks

- `mvn test`
- `git diff --check`
- `rg -n "my.account|user.admin|audit|governance|policy|agent.admin|AgentDefinition|AgentSkillManifest|ToolPermissionBoundary" templates/ai-first-saas-starter/backend/src/main/java templates/ai-first-saas-starter/backend/src/main/resources templates/ai-first-saas-starter/backend/src/test/java`

## Commit

`managed-agents-core: seed five core agents`
