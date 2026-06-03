# Akka AI Skills Pack Java Examples

This directory contains the Java example project exported with the installed Akka AI skills pack.

## What is here

The Java examples are installed under:
- `resources/examples/java/pom.xml`
- `resources/examples/java/src/main/...`
- `resources/examples/java/src/test/...`

The frontend workstream UI reference is installed separately under:
- `resources/examples/frontend/README.md`
- `resources/examples/frontend/src/workstream/**`
- `resources/examples/frontend/src/*contract.test.mjs`

Use these files as:
- local implementation references
- pattern examples for component structure
- test examples for calling patterns and expected behavior

The examples may use `com.example` package names. Treat those names as reference-only. For generated application code, ask for the target Java base package first and default to `ai.first` only when the user accepts or defers the choice.

## How to use the examples

When an installed skill points to a local example file, read the example here rather than expecting source-repo paths to exist.

Typical usage:
1. use `AGENTS.md` for installed-pack guidance
2. use `skills/README.md` for routing
3. open the example files named by the selected skill
4. adapt the matching pattern into the target application

## Scope

These examples are Akka substrate references, not a complete application template and not product-architecture guidance. Use them only after the target app's secure AI-first SaaS foundation and operating-model decisions are clear.
Use the smallest relevant example files for the current component task.

For full web app work, prefer the frontend project guidance in the installed skills and treat static resources as packaged build output unless a narrow endpoint example explicitly says otherwise.

## Official Akka docs

Official Akka SDK documentation is not bundled with the installed pack.
If a skill tells you to consult official Akka docs, use that as an external reference rather than looking for a local `akka-context/` directory.
