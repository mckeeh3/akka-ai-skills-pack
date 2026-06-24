# TASK-013: Migrate endpoint and web UI skill families

## Scope

Migrate HTTP, gRPC, MCP, and web UI skills to the lifecycle and worker/tool/capability model.

## Required reads

- `skills-pack/docs/app-development-lifecycle.md`
- `skills-pack/docs/app-worker-tool-model.md`
- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `skills-pack/docs/structured-surface-contracts.md`
- `skills-pack/docs/web-ui-docs-index.md`
- `skills-pack/skills/akka-http-endpoints/SKILL.md`
- `skills-pack/skills/akka-http-endpoint-*/SKILL.md`
- `skills-pack/skills/akka-grpc-endpoints/SKILL.md`
- `skills-pack/skills/akka-grpc-*/SKILL.md`
- `skills-pack/skills/akka-mcp-endpoints/SKILL.md`
- `skills-pack/skills/akka-mcp-*/SKILL.md`
- `skills-pack/skills/akka-web-ui-*/SKILL.md`

## Expected outputs

- Updated endpoint and web UI skills.
- Clear separation among surfaces, endpoints, actor adapters, governed tools, and Akka implementation.

## Done criteria

- Endpoint skills treat transport as exposure/adapter mechanics, not the canonical business operation.
- Web UI skills treat surfaces as human-worker harnesses and surface actions as governed-tool adapters.
- Auth/request-context/JWT guidance preserves backend authority and tenant scope.
- Testing guidance includes worker/adapter/tool/capability path where relevant.

## Required checks

- `git diff --check`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --dry-run`
- `./install-skills.sh --target /tmp/akka-skills-install-check/.agents/skills --check`
