# Sprint 06 Verification: Examples, Seed, and Packaging Alignment

## Task

- `TASK-REQWS-06-099: Verify examples, seed, and packaging sprint`

## Verification summary

Sprint 06 passes for examples, seed, and packaging alignment.

Installed-pack-facing examples and package guidance now make the requirements-to-workstream path discoverable and normal for broad generated-SaaS input:

```text
input / PRD
→ workstreams
→ what-needs-my-attention categories
→ dashboards
→ structured surfaces/actions
→ governed capabilities/APIs
→ Akka substrate selection
→ request-based Agent turns and AutonomousAgent candidates
→ events/notifications/projections
→ audit/work traces
→ implementation task shape
```

## Files reviewed

- `docs/examples/README.md`
- `docs/examples/requirements-to-workstream-mini-example.md`
- `templates/ai-first-saas-starter/app-description/README.md`
- `docs/examples/purchase-request-app-description/README.md`
- `docs/prd-to-akka-flow.md`
- `templates/ai-first-saas-starter/README.md`
- `pack/AGENTS.md`
- `pack/README.md`
- `pack/manifest.yaml`

## Findings

### Installed-pack users can discover the target process

Pass.

- `pack/AGENTS.md` names the requirements-to-workstream process as the normal path for broad product input or PRDs and links both the canonical process doc and compact mini-example.
- `pack/README.md` includes `docs/requirements-to-workstream-development-process.md` and `docs/examples/requirements-to-workstream-mini-example.md` in the installed layout and calls them the compact target architecture path.
- `pack/manifest.yaml` packages the canonical process doc, mini-example, examples index, and starter core app-description references.

### Examples reinforce workstream-attention-dashboard-surface-capability-autonomous-task planning

Pass.

- `docs/examples/requirements-to-workstream-mini-example.md` demonstrates a PRD fragment flowing through workstream inventory, attention/dashboard breakdown, surfaces/actions, governed capabilities, substrate selection, request-based Agent semantics, AutonomousAgent task candidates, events/notifications/projections, traces, and implementation-task shape.
- `templates/ai-first-saas-starter/app-description/README.md` anchors the starter core app-description as the target architecture reference and directs agents to the mini-example before conventional mechanics examples.
- `docs/examples/README.md` labels the mini-example and starter core app-description as preferred generated-SaaS references.
- `templates/ai-first-saas-starter/README.md` preserves the distinction between description-layer meaning and runnable scaffold realization, and keeps request-based workstream Agents separate from durable internal/background AutonomousAgent extensions.

### Legacy examples are demoted to mechanics references

Pass.

- `docs/examples/README.md` labels purchase-request examples as mechanics-only / conventional references.
- `docs/prd-to-akka-flow.md` says the purchase-request files are not the generated SaaS target architecture and requires secure SaaS, workstream, attention/dashboard, surface/action, capability, AutonomousAgent-candidate, notification/projection, and trace context before coding handoff.
- `docs/examples/purchase-request-app-description/README.md` explicitly says the purchase-request app-description is an app-description mechanics reference only and not target architecture doctrine.

### Progressive follow-up need

No bounded Sprint 06 follow-up tasks are required. Remaining validation is the final mini-project verification task.

## Checks run

- `git diff --check`
- `rg -n "requirements-to-workstream|attention|dashboard|AutonomousAgent|mechanics reference|target architecture|prescriptive" docs/examples docs/prd-to-akka-flow.md pack/manifest.yaml pack/README.md pack/AGENTS.md`
- `rg -n "requirements-to-workstream-development-process|requirements-to-workstream-mini-example|ai-first-saas-starter-app-description|purchase-request" pack/manifest.yaml pack/README.md docs/examples/README.md templates/ai-first-saas-starter -g '*.md' -g '*.yaml'`

## Result

Sprint 06 is complete for its objective. The next runnable task is `TASK-REQWS-99-001: Verify requirements-to-workstream process migration`.
