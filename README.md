# Secure AI-first SMB SaaS Core App

This repository has a two-fold purpose:

1. Develop and maintain the secure AI-first SMB SaaS harness skills, referenced docs, templates, tools, and code examples used by AI coding harnesses.
2. Provide the canonical runnable **secure AI-first SMB SaaS core app** that users clone or fork, run locally, validate, and extend with business-specific domains, workstreams, surfaces, agents, Akka components, and UI.

The Akka AI skills library lives under [`skills-pack/`](skills-pack/). Use the root for core app runtime work and downstream product extensions; use `skills-pack/` for skills maintenance, Akka/code-generation reference examples, metadata, and validation tooling. The only install action is copying/symlinking skills into a harness-accessible directory such as `.agents/skills`.

## What is in the root app

The root app is a merge-friendly baseline with a fixed Java package and app-owned description/spec assets:

```text
pom.xml                         # canonical Akka Java SDK backend build
src/main/java/ai/first/**       # core app runtime source
src/test/java/ai/first/**       # core app tests
src/main/resources/**           # backend resources and built frontend assets
frontend/**                     # React/Vite workstream UI
app-description/**              # authoritative core app description
specs/**                        # core app planning, validation, and extension queues
docs/**                         # root app extension and maintenance guidance
skills-pack/**                  # installable skills-pack source and tooling
```

The supported default Java base package is `ai.first`. Downstream forks may rename it as a deliberate product refactor, but the merge-friendly path is to keep `ai.first` and add domain-specific code under extension zones.

## Core app scope

The baseline centers on the secure AI-first SMB SaaS foundation and five core workstreams:

- **My Account**
- **User Admin**
- **Agent Admin**
- **Audit/Trace**
- **Governance/Policy**

Generated-app features are complete only when they work through the real local Akka/API/UI runtime path at the stated scope. Fixture, mock, deterministic, or model-less behavior is acceptable for tests and explicitly named local fixture modes only; it is not a normal runtime substitute.

## Run and validate

Backend checks from the repository root:

```bash
mvn test
```

Frontend checks from the repository root:

```bash
npm --prefix frontend install
npm --prefix frontend test -- --run
npm --prefix frontend run typecheck
npm --prefix frontend run build
```

User Admin hosted UI/workstream smoke from the repository root:

```bash
npm --prefix frontend run smoke:user-admin-workstream
```

This smoke uses deterministic test-only User Admin identity data, unsets `ADMIN_USERS`, and does not require WorkOS, Resend, or model-provider credentials. See [`specs/user-admin-browser-workstream-smoke/smoke-command.md`](specs/user-admin-browser-workstream-smoke/smoke-command.md) for coverage and caveats.

The frontend build writes Akka static assets to `src/main/resources/static-resources/`. Do not hand-edit generated static output; rebuild from `frontend/` source.

For production-like local runtime smoke testing, configure the backend-only secrets and browser-public `VITE_` values described in `.env.example`, `frontend/.env.example`, and [`docs/deployment-env-secrets.md`](docs/deployment-env-secrets.md), then validate the authenticated shell, `/api/me`, five workstreams, authorization denials, audit/work traces, notifications/email behavior, and provider fail-closed paths through normal runtime APIs and UI.

## Fork-and-extend workflow

Use this repository as the upstream core baseline for a product fork:

1. Fork or clone the repository.
2. Run and validate the secure AI-first SMB SaaS core app locally.
3. Keep the core app baseline on a branch that can receive upstream changes.
4. Add product behavior as domain-specific extensions instead of rewriting core foundation code or generating a separate parallel app.
5. Add business-specific domains, workstreams, surfaces, agents, Akka components, frontend extensions, app-description extensions, specs, docs, and tests in the root app workspace.
6. Update `app-description/` and `specs/` before or alongside implementation changes.
7. Validate backend, frontend, auth, authorization, audit/trace, and UI behavior through the root app runtime path.
8. Merge upstream core changes regularly and resolve conflicts by keeping core hooks small and domain code isolated.

Recommended extension zones are documented in [`docs/domain-extension-guide.md`](docs/domain-extension-guide.md). Java package ownership and dependency rules are documented in [`docs/java-package-boundaries.md`](docs/java-package-boundaries.md). Upstream merge practices are documented in [`docs/upstream-merge-guide.md`](docs/upstream-merge-guide.md).

## Where to add domain-specific work

Prefer additive extension paths:

```text
src/main/java/ai/first/domain/business/<domain>/
src/main/java/ai/first/application/business/<domain>/
src/main/java/ai/first/api/business/<domain>/
src/test/java/ai/first/business/<domain>/
frontend/src/extensions/<domain>/
app-description/extensions/<domain>/
specs/extensions/<domain>/
docs/extensions/<domain>/
```

When domain behavior needs a core integration point, add the smallest stable registry or hook in core code and put Java domain logic under the `business.<domain>` package path. Domain-specific extensions must preserve backend authorization, tenant/customer scoping, audit/work traces, governed capability boundaries, frontend secret boundaries, and provider fail-closed behavior.

## Skills-pack maintenance

The skills library source is isolated under [`skills-pack/`](skills-pack/). It exists to help harness agents maintain this core app and generate real downstream business-specific SaaS extensions in product forks:

- `skills-pack/skills/` — skill routing and focused harness guidance
- `skills-pack/docs/` — source-checkout doctrine and references
- `skills-pack/examples/` — code examples used as generation guidance for real Akka/core-app implementation patterns
- `skills-pack/templates/` — reusable app-description and validation templates
- `skills-pack/pack/` — skills-only install metadata
- `skills-pack/tools/` — release, validation, and audit tooling

Use root [`install-skills.sh`](install-skills.sh) to make the skills library available to a harness in a directory such as `.agents/skills/`. The installer copies or symlinks skill directories plus referenced pack assets (`docs/`, `examples/`, `templates/`, and `tools/`) under `.agents/skills/**`; it does not install pack manifests, `akka-context/**`, frontend/backend source, or another app baseline. Do not add pack-only docs, skills, examples, or metadata assets to root app source paths.

## App description and task queues

The root [`app-description/`](app-description/) tree is the authoritative description for the core app baseline and its domain-specific extensions. The root [`specs/`](specs/) tree contains active planning and pending-task queues for core app work. Execute pending-task queues one task per fresh harness context and commit each completed task with its queue-status update.

## License

See [LICENSE](LICENSE).
