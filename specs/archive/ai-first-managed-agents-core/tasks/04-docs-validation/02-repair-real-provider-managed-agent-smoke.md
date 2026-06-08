# TASK-MAGENT-04-002: Repair optional real-provider smoke for managed runtime tools

## Objective

Make the optional real-provider starter smoke use the same managed-agent repository and runtime tool resolver context as `WorkstreamRuntimeAgent` when invoked through `ComponentClient.forAgent()`, so provider-enabled validation proves the configuration-driven managed-agent path instead of failing before model invocation with `agent-not-found`.

## Required reads

- `AGENTS.md`
- `skills/README.md`
- `docs/agent-runtime-invocation-pattern.md`
- `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/application/security/RealModelProviderSmokeTest.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/WorkstreamRuntimeAgent.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/agentfoundation/AgentBehaviorSeedLoader.java`

## Scope

- Fix the provider-enabled smoke path so the agent component sees the seeded five-core managed-agent definitions, manifests, tool boundaries, and loader tools.
- Preserve the provider-skip path when `OPENAI_API_KEY` is absent.
- Preserve fail-closed behavior for missing provider configuration, tenant mismatch, disabled agents, and missing tool grants.
- Keep the `effects().tools(runtimeTools)` validation gate intact.

## Non-goals

- Do not broaden this into full runtime dependency-injection redesign.
- Do not weaken managed runtime authorization or make static tool registration the normal generated-app path.
- Do not put provider secrets into frontend files, static assets, traces, or logs.

## Required checks

- `OPENAI_API_KEY=<real key> tools/validate-ai-first-saas-starter-fullstack.sh` or an equivalent documented real-provider smoke run with secrets redacted from logs
- `env -u OPENAI_API_KEY tools/validate-ai-first-saas-starter-fullstack.sh`
- `git diff --check`
- `rg -n "RealModelProviderSmokeTest|StarterSecurityComponents|AgentBehaviorSeedLoader|WorkstreamRuntimeAgent|runtimeTools|agent-not-found|ToolPermissionBoundary" templates/ai-first-saas-starter/backend/src/test/java templates/ai-first-saas-starter/backend/src/main/java`

## Done criteria

- The optional real-provider smoke no longer fails with `agent-not-found` when provider configuration is present and real provider access is available.
- Provider-skip validation still passes without provider secrets.
- A focused git commit exists with message `managed-agents-core: repair real-provider managed-agent smoke`.
