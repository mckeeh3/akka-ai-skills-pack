# Sprint 06: Examples, Seed, and Packaging Alignment

## Objective

Update examples, seed app-description references, starter guidance, and package export lists so downstream installed-pack users see the new process as the normal path.

## Scope

Likely affected areas:
- `docs/examples/ai-first-saas-seed-app-description/**`
- `docs/examples/core-ai-first-saas-input/**`
- `docs/examples/purchase-request-*` references where they are still used as primary mechanics examples
- `templates/ai-first-saas-starter/**` docs if process wording is stale
- `docs/prd-to-akka-flow.md` examples
- `pack/AGENTS.md`
- `pack/manifest.yaml`
- `pack/README.md`
- build/export scripts if a new canonical doc is added

## Work areas

1. Update or add an example showing requirements/PRD input decomposed through workstreams, attention, dashboards, surfaces, capabilities, autonomous tasks, events/notifications, and traces.
2. Ensure the AI-first SaaS seed app-description includes or references dashboard/attention/autonomous-task process guidance.
3. Demote legacy purchase-request mechanics examples where they risk implying conventional approval workflow/CRUD-first architecture.
4. Update package manifest/export if new canonical docs or skills are added.
5. Add compact review references for installed-pack users.

## Acceptance criteria

- Installed-pack users receive the new process guidance and examples.
- Examples reinforce the workstream-attention-dashboard-surface-capability-autonomous-task chain.
- Legacy examples are clearly marked as mechanics references, not target architecture.
