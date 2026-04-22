# AGENTS

Use this file as the **installed-pack guidance** for AI coding agents working with this Akka AI skills pack after it has been installed under `.agents/`.

This file is intended for pack users.
It is distinct from the repository-internal maintainer guidance used to develop the source repository.

## What this installed pack is for

This pack helps the harness turn high-level user intent into:
1. Akka solution decomposition
2. focused skill selection
3. component-by-component code generation
4. corresponding tests

Users should be able to speak in natural language.
They do not need to know the pack's internal skill taxonomy.

## Core usage model

Treat the installed skills as an **internal routing layer for the harness**.

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
- `skills/README.md` — routing map across the installed skill library
- `skills/<skill-name>/SKILL.md` — focused implementation or routing guidance
- `docs/` — selected pack-facing reference docs used by some installed skills
- `resources/examples/java/` — exported Akka Java SDK examples and tests

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
- start with `skills/akka-solution-decomposition/SKILL.md` for high-level intent
- use `skills/README.md` once the architecture is clear
- generate tests alongside component code, not afterward
