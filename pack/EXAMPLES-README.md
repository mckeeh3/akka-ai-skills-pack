# Akka AI Skills Pack Examples

This directory contains the Java example project exported with the installed Akka AI skills pack.

## What is here

The examples are installed under:
- `resources/examples/java/pom.xml`
- `resources/examples/java/package.json`
- `resources/examples/java/tsconfig.web-ui.json`
- `resources/examples/java/src/main/...`
- `resources/examples/java/src/test/...`

Use these files as:
- local implementation references
- pattern examples for component structure
- test examples for calling patterns and expected behavior

## How to use the examples

When an installed skill points to a local example file, read the example here rather than expecting source-repo paths to exist.

Typical usage:
1. use `AGENTS.md` for installed-pack guidance
2. use `skills/README.md` for routing
3. open the example files named by the selected skill
4. adapt the matching pattern into the target application

## Scope

These examples are a reference set, not a complete application template.
Use the smallest relevant example files for the current task.

For Akka-hosted web UI examples, TypeScript source lives under `src/main/web-ui/...` and compiled browser assets live under `src/main/resources/static-resources/...`. Run `npm run build:web-ui` after editing TypeScript examples.

## Official Akka docs

Official Akka SDK documentation is not bundled with the installed pack.
If a skill tells you to consult official Akka docs, use that as an external reference rather than looking for a local `akka-context/` directory.
