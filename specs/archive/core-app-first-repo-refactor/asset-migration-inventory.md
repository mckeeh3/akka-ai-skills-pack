# Asset Migration Inventory: Core App First Repository Refactor

## Purpose

This inventory classifies the current repository assets before broad file moves. It applies the target layout contract in `specs/core-app-first-repo-refactor/target-layout-and-path-map.md` and is intentionally an inventory only: it does not move files.

## Classification legend

- `promote`: make this asset part of the canonical root core app.
- `move`: relocate to `skills-pack/` or another target path without treating it as core app source.
- `remove`: delete after canonical ownership is established.
- `archive`: keep as historical/provenance material, not active source.
- `retain`: keep at the current path.
- `update-reference`: edit references after path moves so docs, tasks, scripts, and checks point to the canonical owner.
- `split`: classify subsets differently before moving.

## Repository snapshot

| Area | Current evidence | Classification | Target / action notes |
| --- | --- | --- | --- |
| Root build | `pom.xml`, `effective-pom.xml`, `src/main/java` has 245 files and `src/test/java` has 122 files, currently all under `com/example/**` packages. | split + promote + move | Reconcile the root build as the canonical app build, but split current Java sources: core SaaS/runtime pieces migrate to `ai.first`; focused component examples move to `skills-pack/examples/akka-components/`. `effective-pom.xml` should be treated as generated/build evidence unless later docs/tooling require it. |
| Root resources | `src/main/resources` has 30 files including `application.conf`, logger includes, `agent-behavior-seeds/**`, `mcp/checkout-guidelines.md`, and built/static example resources under `static-resources/**`. | split | App configuration and core agent behavior seeds promote with root app. Focused web UI/static examples (`frontend-reference`, `web-ui`, `web-ui-sse`, `web-ui-websocket`) move to skills-pack examples unless proven to be core app runtime. Generated frontend build output should be regenerated from root `frontend/`, not curated as a separate reference source. |
| Root web-ui source under backend | `src/main/web-ui/**` has 10 TypeScript reference web UI files for focused examples. | move | Move under `skills-pack/examples/akka-components/` with corresponding endpoint/static-resource examples. Do not keep in root runtime source. |
| Root frontend app | `frontend/src` has 121 first-party files plus package/config files. It mirrors the starter frontend file count and includes workstream shell, API clients, design system, screens, fixtures, and contract tests. | promote | Keep as canonical root frontend after reconciliation with the starter copy. `frontend/node_modules/` and `.env.local` are local/generated and should not be treated as source migration targets. |
| Root app description | No root `app-description/` exists yet. | promote from template | Promote/reconcile `templates/ai-first-saas-starter/app-description/**` into root `app-description/**` in the core-root/template-dissolution tasks. |
| Root docs | `docs/` has 112 files, primarily skills-pack doctrine/routing/reference docs plus some web UI and validation guidance. | split | Core app run/extend/domain docs stay or move to root docs. Skills-pack doctrine, skill routing, Akka/reference guidance, planning queue mechanics, and example description docs move to `skills-pack/docs/**`. |
| Root skills | `skills/` has 159 files including `skills/README.md`, skill families, and references. | move | Move intact to `skills-pack/skills/**`; update root references to the moved path or an installed-pack equivalent. |
| Pack metadata | `pack/AGENTS.md`, `pack/README.md`, manifest/schema, examples README. | move | Move to `skills-pack/pack/**` with install/package tooling. |
| Install/release tooling | Root `install.sh`, `tools/build-pack.sh`, `tools/release.sh`, `tools/install-release-template.sh`, `tools/check-version-consistency.sh`, `tools/verify-opinionated-ai-first-saas-pack.sh`, `tools/scaffold-ai-first-saas-starter.sh`, and starter validation/smoke scripts. | move + update-reference | Pack install/release/scaffold tooling moves to `skills-pack/`. `scaffold-ai-first-saas-starter.sh` becomes obsolete or archive-only after the full-app template is dissolved unless retained as historical compatibility wrapper. |
| Runtime/app tools | `tools/prove-workstream-icons-v0.sh`, `tools/scan-ai-first-saas-static-assets.sh`, and any future root validation selected by migrated app docs. | split/retain | Retain only if they validate the root core app. If a script validates starter/template or pack doctrine, move to `skills-pack/tools/**` or archive. |
| Akka reference corpus | `akka-context/` has 236 files of official Akka docs. | move | Move to `skills-pack/akka-context/**`; it supports skills-pack guidance, not the root app runtime. |
| Examples directory | `examples/` is currently empty in the shallow inventory. | move/retain by future content | If later content exists, focused component/reference examples belong under `skills-pack/examples/**`; root examples should be app-facing only. |
| Dist artifacts | `dist/` has generated pack release output. | move/archive/remove | Pack release output belongs under `skills-pack/dist/**` if retained. Prefer generated/ignored release output where possible. Do not treat as core app source. |
| Root node/tooling files | Root `package.json`, `package-lock.json`, `tsconfig.web-ui.json`, top-level `node_modules/`, and standalone list-model scripts. | split | Frontend app dependencies should live under `frontend/`. Pack/docs tooling dependencies move under `skills-pack/` if still needed. `node_modules/` is generated. Model-list helper scripts need purpose classification: retain only if root app operational tooling; otherwise move to `skills-pack/tools/` or archive. |
| Local harness/config | `.agents/skills/project-discussed-idea-to-pending-project/**`, `.akka`, `.m2`, `.tmp`, IDE folders, `target/`, `test/`, `.env`. | retain/generated | Do not package the project-only harness skill. Build/local/generated/private directories are not migration source except `.env.example`, which should reconcile as the root app environment template. |
| Root guidance | `AGENTS.md`, `README.md`, `LICENSE`, `.gitignore`, `.env.example`. | split/update-reference | Root `AGENTS.md` and `README.md` must become core-app-first guidance with pointers to `skills-pack/`. Current source-repo skills-pack maintenance guidance moves to `skills-pack/AGENTS.md`. |

## Full-app starter template inventory

| Current area | Evidence | Classification | Target / action notes |
| --- | --- | --- | --- |
| `templates/ai-first-saas-starter/backend/pom.xml` | Template build with placeholder package/group values. | promote then remove | Reconcile into root `pom.xml` using fixed `ai.first`; remove the template copy after root build ownership is clear. |
| `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/**` | Only package-info files are present in the current shallow backend source summary. | remove after reconciliation | No substantive Java implementation remains in the template path; keep no duplicate source copy. |
| `templates/ai-first-saas-starter/backend/src/main/resources/**` | 28 files including richer `agent-behavior-seeds/starter-v1/**` and `application.conf`. | promote/reconcile | Compare with root `src/main/resources/agent-behavior-seeds/**`; promote useful core seeds/config to root resources, then remove template copy. |
| `templates/ai-first-saas-starter/backend/src/test/java/{{JAVA_PACKAGE_PATH}}/**` | Package-info only in shallow summary. | remove after reconciliation | No separate template test source should remain. |
| `templates/ai-first-saas-starter/frontend/**` | 121 first-party files under `frontend/src`, plus package/config files, matching root frontend scope. | promote/reconcile then remove | Root `frontend/**` is the canonical app copy. Reconcile differences, then remove duplicate template frontend. |
| `templates/ai-first-saas-starter/app-description/**` | 87 files, no root `app-description/` currently exists. | promote | Promote to root `app-description/**` as canonical core app description, then remove template source copy. |
| `templates/ai-first-saas-starter/specs/**` | Starter specs README/seed. | promote/archive | Promote only if still useful as core-app planning/provenance; otherwise archive under `specs/core-app-first-repo-refactor/archive/` or remove. |
| `templates/ai-first-saas-starter/.env.example` | Starter env template. | promote/reconcile | Reconcile into root `.env.example`. |
| `templates/ai-first-saas-starter/README.md`, `TEMPLATE-MANIFEST.md`, `scaffold-rules.md` | Scaffold-era docs/manifests. | archive/remove/update-reference | Convert enduring fork-and-extend guidance into root/skills-pack docs. Archive or remove scaffold-era provenance after dissolution. |
| `templates/ai-first-saas-starter/frontend/node_modules/**` | Local generated dependency tree present under the template. | remove | Generated dependency directory; do not migrate as source. |

## Current root Java source classification

The root Java tree is currently a mixed reference/example corpus under `com.example`, so it must be split before package migration.

| Source subset | Evidence | Classification | Notes |
| --- | --- | --- | --- |
| Core SaaS/security APIs | `src/main/java/com/example/api/security/**`, `src/main/java/com/example/application/security/**`, `src/main/java/com/example/domain/security/**`, `src/main/java/com/example/security/**`. | promote | Migrate to `ai.first` only after reconciling with core app feature-completion/readiness specs. Preserve auth, tenancy, `/api/me`, admin, invite, audit, and fail-closed runtime semantics. |
| Governed runtime agent foundation | `src/main/java/com/example/domain/agentfoundation/**`, `src/main/java/com/example/application/agentfoundation/**`, plus related endpoints/tests. | promote | Migrate to root `ai.first` as core app substrate if it is part of the actual five-core workstream runtime. |
| Core workstream/runtime services | Files implementing current core workstream behavior, autonomous workers, attention events, audit/governance/user-admin runtime slices. | promote after review | Promote only when they support the canonical root app, not as detached examples. Keep runtime completion doctrine in force. |
| Focused Akka component examples | Shopping cart, draft cart, purchase order, generic greeting, standalone workflow/timer/consumer/view/MCP/gRPC/SSE/WebSocket examples and matching tests. | move | Move to `skills-pack/examples/akka-components/**` and update skills/docs references. These may keep `com.example` as reference examples. |
| Frontend reference endpoints/static examples | `FrontendReference*`, `WebUi*`, SSE/WebSocket page endpoints, related resources. | move | Treat as focused web UI delivery examples unless incorporated into the root core app frontend. |
| Tests | `src/test/java/com/example/**` mirrors both core/runtime and examples. | split | Core tests migrate to `src/test/java/ai/first/**`; example tests move with example sources. |

## Documentation and guidance classification

| Current area | Classification | Target / action notes |
| --- | --- | --- |
| AI-first SaaS doctrine, workstream, capability-first, skill routing, app-description, PRD/backlog/queue mechanics docs under `docs/**`. | move | Move to `skills-pack/docs/**` because they are installable-pack guidance for generated apps. Root docs may link to the pack docs for maintainers. |
| Core app docs/run/fork/extension guidance. | retain/create | Root `README.md` and root `docs/**` should explain running, testing, extension zones, upstream merge practices, local provider/security configuration, and domain-specific extension boundaries. |
| `docs/examples/**` and purchase-request mechanics references. | move | Move to `skills-pack/examples/app-descriptions/**` or `skills-pack/docs/examples/**` as skills-pack reference material. |
| Existing standalone root docs `frontend-with-akka-backend.md` and `web-ui-high-level-style-guide.md`. | split | Retain only if rewritten as root app guidance; otherwise move to skills-pack docs/examples with stale path updates. |

## Specs and active-queue reference classification

`specs/` contains 1,465 files and 70 pending-task queues. These are active planning/provenance assets, so the refactor should avoid rewriting all of them opportunistically. Instead, later tasks should update only references required by their scope and final verification should report stale references that remain historical or deferred.

| Spec/reference pattern | Evidence | Classification | Action notes |
| --- | --- | --- | --- |
| Current refactor spec | `specs/core-app-first-repo-refactor/**`. | retain | Keep at root until the refactor completes. It is the active coordination queue. |
| Core app/runtime specs | `specs/core-app-feature-completion/**`, `specs/core-app-full-stack-readiness/**`, `specs/full-core-*`, `specs/*workstream*`, notification/runtime readiness specs. | retain/update-reference | Keep root if they govern core app runtime or domain-extension planning. Update stale paths when touched by migration tasks. |
| Skills-pack migration/review specs | `specs/ai-first-skills-pack-migration/**`, `specs/capability-first-backend-migration/**`, `specs/agent-workstream-*migration/**`, `specs/skills-review-cleanup/**`, release/pack specs. | move or archive later | Move under `skills-pack/specs/**` only after the root app and pack split is stable, or archive if historical. This task defers physical movement. |
| Specs with starter-template path dependencies | Search found many queues/docs referencing `templates/ai-first-saas-starter`, `scaffold-ai-first-saas-starter`, raw `com.example`, `skills/README.md`, and `pack/`. Examples include `specs/notification-platform-foundation/**`, `specs/workstream-visual-sessions/**`, `specs/web-ui-style-theme-refresh/**`, `specs/agent-admin-workstream-v0/**`, and `specs/core-app-full-stack-readiness/**`. | update-reference/defer | Do not bulk-edit in this inventory task. Later migration tasks must interpret old paths through `target-layout-and-path-map.md`; final verification should distinguish stale active references from historical/provenance references. |
| Archived specs | `specs/archive/**` and completed migration summaries. | archive | Leave as provenance unless directly blocking tooling or docs. |

## Packaging, scripts, and generated-output classification

| Asset | Classification | Target / action notes |
| --- | --- | --- |
| `install.sh` | move/wrap | Move install implementation to `skills-pack/install.sh`. Keep a small root wrapper only if it helps maintainers install the pack from a core-app checkout. |
| `tools/build-pack.sh`, `tools/release.sh`, `tools/install-release-template.sh`, `tools/check-version-consistency.sh`, `tools/verify-opinionated-ai-first-saas-pack.sh` | move | Relocate to `skills-pack/tools/**` and update path resolution. |
| `tools/scaffold-ai-first-saas-starter.sh`, `tools/validate-ai-first-saas-starter-fullstack.sh`, `tools/smoke-ai-first-saas-starter-real-model.sh` | remove/archive/update-reference | Scaffold/full-app-template validation becomes obsolete after root app promotion. Archive or replace with root app validation commands and compatibility notes. |
| `tools/prove-workstream-icons-v0.sh`, `tools/scan-ai-first-saas-static-assets.sh` | split | Retain at root if validating root frontend/static output; otherwise move to skills-pack tools. |
| `dist/**` | move/archive/remove | Generated pack release artifacts belong under `skills-pack/dist/**` or outside source control. |
| `target/**`, root `node_modules/**`, `frontend/node_modules/**`, template `node_modules/**`, `.m2`, `.tmp`, `.akka` | remove/ignore | Generated/local runtime artifacts, not migration source. |

## Ambiguities to resolve in later bounded tasks

1. Which current `com.example` classes are truly core app runtime versus focused examples. The promotion task must inspect by feature area, not blindly rename all Java files.
2. Whether root `src/main/resources/agent-behavior-seeds/**` or starter `agent-behavior-seeds/starter-v1/**` is the more complete canonical seed set; reconcile rather than overwrite.
3. Whether root `frontend/**` and template frontend are identical or diverged; reconcile before deleting the template copy.
4. Which active `specs/**` should ultimately move under `skills-pack/specs/**`; this inventory defers broad spec relocation to avoid breaking active queues during the refactor.
5. Whether root package/tooling files (`package.json`, `tsconfig.web-ui.json`, model-list scripts) serve core app, skills-pack docs/tools, or historical local utilities.

## Done-state check for this inventory

The required major areas are classified: app source, template source, frontend copies, static resources, skills, docs, examples, pack/install assets, validation tools, and active specs. Physical moves and broad reference edits are intentionally deferred to the later queue tasks that own those areas.
