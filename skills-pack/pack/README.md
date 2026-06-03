# Akka AI Skills Pack Layout

This directory defines the installable packaging model for the Akka AI skills and reference examples in this repository.

## Scope

This pack intentionally includes:
- `skills/**`, including AI-first SaaS routing, description-first, planning, and implementation skills
- repository docs under `docs/**`, including AI-first doctrine, requirements-to-workstream process doctrine, description-first doctrine/architecture references, planning references, and example app-description artifacts
- reference examples exported from `src/**`
- starter app template resources exported from `templates/ai-first-saas-starter/**`
- scaffold tooling exported from `tools/scaffold-ai-first-saas-starter.sh`
- pack manifests
- installer scripts
- a pack-facing `AGENTS.md` that is installed as `<agents-root>/AGENTS.md`

The installable pack always includes the full currently packaged skill library, shared references, and exported examples.
There is no bundle selection during install.

This pack intentionally does **not** include:
- `akka-context/**`
- the repository-internal maintainer guidance files from the repo root

`akka-context` is kept in this repository only as a maintainer/reference input. Installed packs must not depend on local `akka-context` files being present.

The installed pack uses `pack/AGENTS.md` as the source for `<agents-root>/AGENTS.md`.
It also uses `pack/EXAMPLES-README.md` as the source for `<agents-root>/resources/examples/java/README.md`.
Those installed files are for pack users and are distinct from the repository-internal maintainer guidance files.

Installed-pack users should treat `docs/requirements-to-workstream-development-process.md` plus `docs/examples/requirements-to-workstream-mini-example.md` as the compact target architecture path for broad PRD/input processing. That path must preserve affected workstreams, role-specific dashboard attention, human surface graph nodes/edges, internal workstream agent graph candidates, workstream expertise, governed-tools in capability files and surface/action maps, and qualified browser-tool/agent-tool/internal-tool exposures. Legacy purchase-request examples remain mechanics reference material only.

Important distinction for real development projects:
- the installed pack under `<agents-root>/` provides skills, guidance, and examples
- a project's maintained `app-description/` tree belongs in the **target project workspace**, not inside the pack itself, unless that project explicitly chooses another internal location

## Install target layout

The installer places files into one of two cross-harness locations:

- project mode: `<project-root>/.agents`
- global mode: `~/.agents`

Installed layout:

```text
<agents-root>/
  AGENTS.md
  docs/
    ai-first-saas-application-architecture.md
    minimum-ai-first-saas-app.md
    agent-workstream-application-architecture.md
    agent-workstream-design-review-checklist.md
    structured-surface-contracts.md
    capability-first-backend-architecture.md
    workstream-expertise-model.md
    skills-pack-user-guide.md
    description-first-application-doctrine.md
    internal-app-description-architecture.md
    app-description-maintenance-flow.md
    app-description-end-to-end-workflow-example.md
    agent-coverage-matrix.md
    pending-question-queue.md
    pending-task-queue.md
    requirements-to-workstream-development-process.md
    prd-to-akka-flow.md
    module-sprint-planning.md
    security-pattern-selection.md
    security-review-checklist.md
    security-workos-auth-and-admin.md
    frontend-with-akka-backend.md
    web-ui-frontend-decomposition.md
    web-ui-frontend-project-integration.md
    web-ui-pattern-selection.md
    web-ui-quality-checklist.md
    web-ui-style-guide.md
    web-ui-ux-patterns.md
    workstream-ui-reference-architecture.md
    timer-pattern-selection.md
    workflow-endpoint-pattern.md
    examples/
      README.md
      requirements-to-workstream-mini-example.md
      core-ai-first-saas-input/
        README.md
        00-document-development-process-context.md
        01-core-seed-progression-plan.md
        02-persistent-discussion-capture.md
        03-module-auth-app-access-prd.md
        03a-module-agent-workstream-runtime-bootstrap-prd.md
        04-module-user-admin-prd.md
        05-module-agent-definition-prd.md
        06-module-prompt-governance-prd.md
        07-module-skill-governance-prd.md
        08-module-audit-work-trace-prd.md
        09-module-evaluation-closed-loop-improvement-prd.md
        10-canonical-core-app-prd.md
      purchase-request-app-description/        # mechanics-only legacy app-description cross-linking reference
      purchase-request-prd.md                 # conventional mechanics reference, not target architecture
      purchase-request-solution-plan.md       # conventional mechanics reference, not target architecture
      purchase-request-module-sprint-plan.md  # conventional mechanics reference, not target architecture
      purchase-request-pending-tasks.md       # conventional queue mechanics reference
    ...
  manifests/
    akka-ai-skills-pack.yaml
  bin/
    scaffold-ai-first-saas-starter.sh
  resources/
    templates/
      ai-first-saas-starter/
        README.md
        TEMPLATE-MANIFEST.md
        scaffold-rules.md
        app-description/
        specs/
        backend/
    examples/
      java/
        pom.xml
        README.md
        src/
          main/
            java/...
            resources/...
          test/
            java/...
      frontend/
        README.md
        package.json
        src/
          api/
          workstream/
          main.tsx
          *contract.test.mjs
  skills/
    README.md
    references/
      akka-entity-comparison.md
    ai-first-saas/
      SKILL.md
    agent-workstream-apps/
      SKILL.md
    capability-first-backend/
      SKILL.md
    ai-first-saas-object-model/
      SKILL.md
    ai-first-saas-agent-team-design/
      SKILL.md
    app-descriptions/
      SKILL.md
    app-description-bootstrap/
      SKILL.md
    app-description-input-normalization/
      SKILL.md
    app-generate-app/
      SKILL.md
    akka-solution-decomposition/
      SKILL.md
    akka-backlog-to-pending-tasks/
      SKILL.md
    akka-change-request-to-spec-update/
      SKILL.md
    akka-revised-prd-reconciliation/
      SKILL.md
    akka-pending-task-queue-maintenance/
      SKILL.md
    akka-do-next-pending-task/
      SKILL.md
    akka-workflows/
      SKILL.md
    akka-views/
      SKILL.md
    akka-http-endpoints/
      SKILL.md
    akka-agents/
      SKILL.md
    akka-event-sourced-entities/
      SKILL.md
    ...
```

Where `<agents-root>` is either `<project-root>/.agents` or `~/.agents`.

## Install model

The pack is versioned as one release artifact.
Each install copies the full packaged skill library, shared references, exported examples, selected AI-first and description-first reference docs, starter template resources, and the explicit starter scaffold command. Default skills-only/project/global installs do not materialize starter application code into the project root.

## Path rewrite rules

Installed skills must be rewritten so they do not point back to maintainer-repo-only paths.

### Example path rewrite

Rewrite references like:

```text
../../../src/main/java/com/example/application/ShoppingCartEntity.java
```

to:

```text
../../resources/examples/java/src/main/java/com/example/application/ShoppingCartEntity.java
```

### Repo-internal guidance rewrite

Installed skill files must not reference repository-internal maintainer guidance files from the source repository.
When needed, repo-internal `AGENTS.md` references should be rewritten to the installed `<agents-root>/AGENTS.md` guidance file.

### Akka docs reference rewrite

Rewrite references like:

```text
akka-context/sdk/event-sourced-entities.html.md
```

to a non-local note such as:

```text
Official Akka SDK docs for this topic (not bundled with this pack)
```

That keeps installed skills free of broken local file references while still reminding agents to consult official Akka docs.

## Maintainer flow

Recommended release flow:
1. validate all skill references in CI
2. generate a release bundle from repo content
3. rewrite install-time paths in copied skill files
4. publish both `akka-ai-skills-pack-<version>.tar.gz` and `install-akka-ai-skills-pack-<version>.sh` as GitHub release assets
5. install from the versioned release installer with `curl ... | bash` or unpack the archive and run `install.sh`

## Installer UX

- the versioned GitHub release installer installs into `<target-dir>/.agents`; `--target-dir` defaults to the current directory
- if `--location project` is provided, `install.sh` installs into `<project-root>/.agents` without prompting
- if `--location global` is provided, `install.sh` installs into `~/.agents` without prompting
- if `--location` is omitted, `install.sh` prompts the user to choose between those two modes
- `--project <dir>` can be used to set the project root for project mode; otherwise the current directory is used
- after project install, users may explicitly scaffold the starter with `<project-root>/.agents/bin/scaffold-ai-first-saas-starter.sh --target <project-root> --app-name "My App" --base-package ai.first`; `ai.first` is the accepted/deferred default example, and generated apps should use the selected Java base package rather than `com.example` from bundled reference examples
- scaffold mode is fail-closed by default: it reports conflicts with `--dry-run`, refuses existing app files unless `--force-overwrite` is deliberately selected, and writes `specs/scaffold-report.md`
