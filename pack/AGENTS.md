# AGENTS

Use this file as the **installed-pack guidance** for AI coding agents working with this Akka AI skills pack after it has been installed under `.agents/`.

This file is intended for pack users.
It is distinct from the repository-internal maintainer guidance used to develop the source repository.

## What this installed pack is for

This pack now supports two complementary ways of working in a real development project:

1. **description-first application maintenance**
   - maintain an authoritative internal app-description as the source of truth
   - review readiness, change impact, security, observability, and test intent before realization
   - generate app outputs only when requested or accepted
2. **intent-driven Akka decomposition and implementation**
   - derive the Akka solution shape from high-level intent
   - load focused implementation skills
   - generate component code and corresponding tests

Users should be able to speak in natural language.
They do not need to know the pack's internal skill taxonomy.

## Core usage model

Treat the installed skills as an **internal routing layer for the harness**.

### Mode A: description-first application maintenance

Use this mode when the user is primarily describing or revising the app, asking what changed, asking whether it is ready, or asking to generate only after the description is sufficiently mature.

Recommended flow:
1. read the user's input completely
2. if the user is working description-first, start with `skills/app-descriptions/SKILL.md`
3. bootstrap with `skills/app-description-bootstrap/SKILL.md` when no usable app-description tree exists yet
4. normalize broad or mixed input with `skills/app-description-input-normalization/SKILL.md`
5. route and maintain the smallest relevant app-description layer
6. use readiness and review-summary skills before or around generation as needed
7. realize outputs with `skills/app-generate-app/SKILL.md` only when generation is requested or accepted

Important installed-pack rule:
- the maintained app-description tree belongs to the **target project workspace**, not to `.agents/` itself, unless the user explicitly wants some other project-equivalent internal location
- the pack provides the skills and reference docs; it is not the application's source-of-truth storage location

### Mode B: intent-driven Akka decomposition and implementation

Use this mode when the user wants to derive the Akka solution shape and then implement components.

Recommended flow:
1. read the user's input completely
2. if the Akka solution shape is still unclear, start with `skills/akka-solution-decomposition/SKILL.md`
3. if one structural choice is still unresolved, use the focused decision skill such as `skills/akka-entity-type-selection/SKILL.md`
4. once the architecture is clear, use `skills/README.md` to load only the smallest relevant implementation skill set
5. generate code and tests component by component

Do not jump straight into a component family when the broader architecture is still unclear.

## Installed pack layout

After installation, the main entry points are:
- `AGENTS.md` — this installed-pack guidance file
- `skills/README.md` — routing map across the installed skill library, including description-first and implementation paths
- `skills/<skill-name>/SKILL.md` — focused implementation or routing guidance
- `docs/` — selected pack-facing reference docs used by installed skills, including description-first doctrine/architecture examples
- `resources/examples/java/` — exported Akka Java SDK examples and tests for concrete Akka implementation patterns

Use the docs under `docs/` as routing/reference support.
Use the examples under `resources/examples/java/` as canonical local implementation references when they match the task.

## Official Akka docs

Official Akka SDK documentation is **not bundled** with the installed pack.

Some installed skills may refer to official Akka docs generically.
Use those references when you need:
- API confirmation
- edge-case semantics
- feature behavior not fully covered by the installed examples

## Working style

Prefer this pattern:
- decompose before coding when requirements are still high-level
- load only the smallest relevant skill set
- use examples and tests as implementation references
- state assumptions clearly when requirements are ambiguous
- ask clarifying questions when ambiguity materially affects architecture or component choice

## Short routing rule

When unsure:
- start with `skills/app-descriptions/SKILL.md` if the user is describing, revising, reviewing, or readiness-checking the app itself
- start with `skills/akka-solution-decomposition/SKILL.md` if the user wants direct Akka architecture derivation from high-level intent
- use `skills/README.md` to route to the smallest next skill set
- generate tests alongside component code, not afterward
