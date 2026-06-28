# Skills-Pack Comprehensive Review Guide

Use this guide as the common reference for every file review in `file-review-inventory.md`.

## Canonical spine

Every active source file should support, or at least not contradict, this spine:

```text
current-intent graph
  -> workstream vertical
    -> worker
      -> execution harness
        -> actor adapter
          -> governed tool
            -> capability
              -> Akka/frontend implementation
                -> tests/checks/manual runtime evidence
                  -> reconciliation back into current intent
```

## Canonical process

The app development process is an iterative three-phase loop:

1. **Interview / intent reconciliation** — normalize requests, findings, and decisions into current-intent graph changes, pending questions, or bounded task scope.
2. **Build / compile / implement** — compile description-ready/current-intent slices into docs, specs, code, tests, configuration, and manual-ready validation paths.
3. **Manual runtime test / reconciliation** — exercise the real local runtime/API/UI/agent path, classify mismatches, and reconcile them into current intent, specs, tasks, code, tests, or blockers.

The loop is continuous. Bug reports, review findings, manual test observations, and implementation discoveries are request-stream inputs, not side notes.

## Canonical structure

For new current-intent app-description work, prefer:

```text
app-description/
  app.md
  global/
    actors/
    roles/
    workers/
    policies/
    surfaces/
    agents/
    tools/
    traces/
  domains/<domain>/
    domain.md
    capabilities/
    data-state/
    workstreams/<workstream>/
      workstream.md
      access.md
      behavior.md
      workers/
      surfaces/
      agents/
      tools/
      policies/
      traces/
      tests/
      realization/
```

Numbered folders such as `10-capabilities/`, `12-workstreams/`, `55-ui/`, and `70-traceability/` may remain as legacy/template compatibility only when clearly labeled and mapped into the current-intent graph. They should not be presented as the default canonical structure for new work.

## Review rubric

For each file, answer:

1. **Role:** Is this file source-authoritative guidance, an example, template, tool, pack metadata, install script, or installed-output mirror?
2. **Alignment:** Does it support the lifecycle and worker/tool/capability spine?
3. **Conflict:** Does it teach a competing app-description structure, page-first design, endpoint-first design, prompt-first design, component-first design, or mock-complete behavior?
4. **Duplication:** Does it repeat large canonical doctrine that should live in shared docs/references?
5. **Routing:** If it is a skill or routing doc, does it load the smallest relevant skill family and route by lifecycle phase?
6. **Authority:** Does it avoid treating frontend visibility, prompt text, route availability, model output, or tool descriptions as authorization?
7. **Runtime completion:** Does it preserve fail-closed provider/security behavior and real local runtime evidence for user-visible completion?
8. **Install/source boundary:** If it is under `skills-pack/.agents/skills/**`, is it treated as installer output rather than source truth?
9. **References:** If the file is revised, removed, or archived, are inbound links, manifest entries, installer checks, and referenced paths handled?
10. **Outcome:** Should the file be accepted, revised, archived, removed, superseded, or blocked for a follow-up task?

## Preferred outcomes

- `accepted` — file is aligned and needs no content change beyond inventory status.
- `revised` — file was edited to align with the canonical spine.
- `archived` — file remains for history/compatibility but is moved or labeled as retired/legacy according to repository policy.
- `removed` — file is deleted and references/manifest/install behavior are updated.
- `installer-output-verified` — installed mirror file is verified as generated/derived output or intentionally tracked output; no source doctrine edit made there.
- `superseded` — file remains only because another active file replaces it, or the inventory entry points to the replacement.
- `blocked-with-follow-up` — review found an issue too broad or ambiguous for a one-file task; create or reference a bounded follow-up task.

## Required canonical reads for reviewers

Read only what is needed for the file type, but use these as the authoritative baseline:

- `skills-pack/AGENTS.md`
- `skills-pack/README.md`
- `skills-pack/skills/README.md`
- `skills-pack/docs/app-development-lifecycle.md`
- `skills-pack/docs/current-intent-model.md`
- `skills-pack/docs/app-description-component-graph.md`
- `skills-pack/docs/app-worker-tool-model.md`
- `skills-pack/docs/intent-compiler.md`
- `skills-pack/docs/intent-to-realization-flow.md`
- `skills-pack/docs/app-description-to-code-compile-contract.md`
- `skills-pack/docs/skill-consolidation-and-pruning.md`
- `skills-pack/references/generated-saas-runtime-completion.md`

## Known high-risk review themes

Prioritize careful review when a file mentions:

- numbered app-description directories as canonical for new work;
- generated apps as separate baselines instead of root-app fork/extend;
- pages/routes/components/endpoints/prompts as product authority;
- AI tools inheriting human surface authority;
- mock/demo/model-less behavior as normal runtime completion;
- `.agents/skills/**` as writable source;
- broad orchestrator skills that duplicate long doctrine instead of routing;
- old placeholder domain names used as generic labels.
