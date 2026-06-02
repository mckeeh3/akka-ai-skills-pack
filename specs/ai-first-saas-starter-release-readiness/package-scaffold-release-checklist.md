# Package and Scaffold Release Checklist

Task: `TASK-AFSSR-01-001`
Date: 2026-06-02

## Reviewed inputs

- `pack/manifest.yaml`
- `pack/README.md`
- `pack/AGENTS.md`
- `pack/EXAMPLES-README.md`
- `install.sh`
- `tools/build-pack.sh`
- `tools/install-release-template.sh`
- `tools/scaffold-ai-first-saas-starter.sh`
- `templates/ai-first-saas-starter/README.md`
- `templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md`
- `templates/ai-first-saas-starter/scaffold-rules.md`
- `templates/ai-first-saas-starter/app-description/README.md`
- `templates/ai-first-saas-starter/specs/README.md`
- starter backend/frontend metadata files (`backend/pom.xml`, `frontend/package.json`)

## Package/scaffold readiness checklist

| Area | Status | Evidence |
| --- | --- | --- |
| Pack manifest advertises starter template as a scaffold resource | Pass | `pack/manifest.yaml` has `templates[ai-first-saas-starter]`, `installAs`, `scaffoldCommand`, and `defaultInstallMaterializesAppCode: false`. |
| Default install does not materialize app code | Pass | `pack/README.md`, manifest, `install.sh`, and `tools/build-pack.sh` describe/install the starter as `.agents/resources/templates/...` plus `.agents/bin/scaffold-ai-first-saas-starter.sh`. |
| Installer includes pack-facing guidance, docs, examples, template, and scaffold command | Pass | `install.sh` validates/copies `pack/AGENTS.md`, `pack/EXAMPLES-README.md`, `docs`, `skills`, Java/frontend examples, starter template, and scaffold command. |
| Release bundle includes starter resources and excludes `akka-context` | Pass | `tools/build-pack.sh` copies `templates`, scaffold tooling, docs, skills, examples, and removes `akka-context`; bundle README states exclusion. |
| Installed-pack guidance boundary is explicit | Pass | `pack/AGENTS.md` is pack-user guidance; `pack/README.md` distinguishes installed-pack guidance from repository-internal maintainer guidance. |
| Java base package policy is explicit | Pass | `pack/AGENTS.md`, `templates/.../README.md`, `scaffold-rules.md`, and scaffold CLI require/user-prompt for base package with accepted/deferred `ai.first` default and forbid `com.example` unless explicitly selected. |
| Scaffold fail-closed overwrite behavior is explicit | Pass | `tools/scaffold-ai-first-saas-starter.sh` rejects existing app/conflicting rendered paths unless `--force-overwrite`; `--force-overwrite` in non-interactive mode requires `--yes`. |
| Placeholder contract is documented | Pass | `TEMPLATE-MANIFEST.md` lists `{{APP_NAME}}`, `{{APP_SLUG}}`, `{{JAVA_BASE_PACKAGE}}`, `{{JAVA_PACKAGE_PATH}}`, and `{{MAVEN_GROUP_ID}}`; scaffold script renders matching placeholders. |
| Starter scope boundaries are documented | Pass | Template docs distinguish five-core-v0 readiness from full-core follow-up and preserve governed runtime/fail-closed requirements. |
| Project-only mini-project leakage | Pass | Focused scan found no release-readiness/spec mini-project paths, local `/home/hxmc`, `.pi`, root maintainer doctrine, or project-only guidance in packaged roots. |
| `akka-context` handling | Pass | References are exclusion/rewrite notes in pack/build/install docs or schema; no installed content depends on bundled `akka-context`. |
| `com.example` handling | Pass | `com.example` appears only as explicit reference-only/forbidden-by-default guidance in pack/template docs; starter source paths use placeholders. |

## Focused scan evidence

```bash
rg -n "specs/(ai-first-saas-starter-release-readiness|notification-delivery-release-readiness|attention-release-readiness|autonomous-agent|requirements-intake|capability-first-backend-migration)|/home/hxmc|\.pi/|AGENTS\.md for detailed constraints|Project Scope: This Repository" \
  pack install.sh tools/scaffold-ai-first-saas-starter.sh tools/build-pack.sh tools/install-release-template.sh templates/ai-first-saas-starter \
  -g '!**/node_modules/**'
# result: no matches
```

```bash
rg -n "templates:|ai-first-saas-starter|scaffoldCommand|defaultInstallMaterializesAppCode" pack/manifest.yaml
# confirms manifest template entry at lines 548-553
```

```bash
rg -n "scaffold-ai-first-saas-starter|copy_starter_template|templates/ai-first-saas-starter|STARTER_TEMPLATE_DIR|TEMPLATE_RESOURCES_DIR" \
  install.sh tools/build-pack.sh tools/install-release-template.sh
# confirms install/build paths copy the starter template and scaffold command
```

```bash
rg -n "fail-closed|force-overwrite|base-package|ai\.first|com\.example|Rendered target paths already exist|Target contains existing application" \
  tools/scaffold-ai-first-saas-starter.sh templates/ai-first-saas-starter/README.md \
  templates/ai-first-saas-starter/scaffold-rules.md templates/ai-first-saas-starter/TEMPLATE-MANIFEST.md
# confirms base-package, fail-closed, and com.example guardrail language
```

## Blockers found

None for package/scaffold metadata readiness. Full rendered scaffold execution, backend/frontend tests, provider skip/fail-closed checks, and implemented-capability scans remain in `TASK-AFSSR-02-001`.
