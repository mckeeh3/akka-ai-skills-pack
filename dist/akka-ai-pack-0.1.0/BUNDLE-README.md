# akka-ai-pack 0.1.0

This is a build artifact for the Akka AI resource pack.

## Included
- install.sh
- pack manifests
- Pi skills under .pi/skills
- Akka SDK Java reference examples exported from src/
- repository pom.xml and README.md for the example set

## Excluded
- akka-context/

The akka-context directory is intentionally excluded from this bundle. Installed skills are rewritten
at install time so they point to installed examples and generic official Akka SDK documentation
notes instead of repo-local akka-context paths.

## Install

The installer uses cross-harness locations:
- project mode: `<project-root>/.agents`
- global mode: `~/.agents`

From inside the unpacked bundle:

```bash
bash install.sh --location project --project /path/to/project --bundle entities-core
```

Or:

```bash
bash install.sh --location global --bundle entities-core
```

If `--location` is omitted, the installer prompts interactively.
If project mode is selected, the current directory is used as the project root unless `--project` is provided.

## Bundles
- all
- entities-core
- ese-core
- kve-core
