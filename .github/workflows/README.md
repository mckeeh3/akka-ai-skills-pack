# Release workflows

This directory contains the GitHub Actions workflows that validate, tag, build, and publish release assets for `akka-ai-skills-pack`.

The release process is intentionally version-driven:

- `pack/manifest.yaml` is the source of truth for the pack name and version.
- Release tags must be named `v<metadata.version>`.
- Release assets are built from the tagged source, not from a developer workstation.
- GitHub Releases are created as drafts first so maintainers can review the generated files before publishing.

## Workflows in this directory

### `build-test.yml` — Validate and build pack

Runs on:

- pull requests
- pushes to `main`
- manual `workflow_dispatch`

What it does:

1. Checks out the repository.
2. Runs `tools/check-version-consistency.sh` to verify that documented pack artifact references in `README.md` and `pack/README.md` match `pack/manifest.yaml`.
3. Sets up JDK 21.
4. Runs `mvn verify --no-transfer-progress`.
5. Runs `tools/build-pack.sh --clean --github-repo "${{ github.repository }}"`.
6. Uploads the generated pack archive and release installer as a workflow artifact named `akka-ai-pack-dist`.

Use this workflow as the pre-release confidence check. A release should not be cut unless this workflow is green on the commit you intend to tag.

### `cut-tag.yml` — Cut release tag

Runs only by manual `workflow_dispatch`.

Inputs:

- `target_ref` — branch or commit SHA to tag. Defaults to `main`.
- `release_version` — optional expected version. If supplied, it must match `metadata.version` in `pack/manifest.yaml`.

What it does:

1. Checks out the requested target ref with full history.
2. Sets up JDK 21.
3. Reads `metadata.version` from `pack/manifest.yaml`.
4. Computes the release tag as `v<metadata.version>`.
5. Fails if the optional `release_version` input does not match the manifest version.
6. Runs `mvn verify --no-transfer-progress`.
7. Fails if the tag already exists locally or on `origin`.
8. Creates and pushes an annotated tag named `v<metadata.version>`.

Pushing this tag automatically triggers `release.yml`.

### `release.yml` — Create draft release assets

Runs on:

- pushes to tags matching `v*`
- manual `workflow_dispatch` with a `release_tag` input

What it does:

1. Checks out the repository with full history.
2. Runs `tools/check-version-consistency.sh`.
3. Sets up JDK 21.
4. Reads `metadata.name` and `metadata.version` from `pack/manifest.yaml`.
5. Verifies that the release tag is exactly `v<metadata.version>`.
6. For manual dispatch, verifies that the requested tag exists.
7. Fails if a published GitHub Release already exists for the tag.
8. Allows an existing draft release for the tag and updates its assets.
9. Runs `mvn verify --no-transfer-progress`.
10. Builds versioned release assets with `tools/build-pack.sh --clean --github-repo "${{ github.repository }}"`.
11. Creates or updates a draft GitHub Release with generated release notes and attaches:
    - `<pack-name>-<version>.tar.gz`
    - `install-<pack-name>-<version>.sh`

The draft-release step uses `softprops/action-gh-release@v2`.

## Release assets

For a manifest like this:

```yaml
metadata:
  name: akka-ai-skills-pack
  version: X.Y.Z
```

`release.yml` publishes these assets to GitHub Release tag `vX.Y.Z`:

- `akka-ai-skills-pack-X.Y.Z.tar.gz`
- `install-akka-ai-skills-pack-X.Y.Z.sh`

The archive contains the installable pack, including:

- `install.sh`
- `pack/manifest.yaml`
- `pack/AGENTS.md`
- selected pack-facing docs under `docs/`
- all packaged skills under `skills/`
- exported Java reference examples under `src/`
- `pom.xml`, `README.md`, and `LICENSE`

The archive intentionally excludes `akka-context/` and repository-internal maintainer-only guidance.

The generated `install-akka-ai-skills-pack-X.Y.Z.sh` script is a small release installer. It downloads the matching archive from the same GitHub Release, unpacks it into a temporary directory, and runs the archive's bundled `install.sh` in project mode.

## Maintainer release procedure

Use this procedure for a normal release.

### 1. Choose the next version

Pick the next semantic version for the pack, for example `X.Y.Z`.

The release tag will be:

```text
vX.Y.Z
```

The two public release assets will be:

```text
akka-ai-skills-pack-X.Y.Z.tar.gz
install-akka-ai-skills-pack-X.Y.Z.sh
```

### 2. Update the manifest and versioned docs

Update `pack/manifest.yaml`:

```yaml
metadata:
  version: X.Y.Z
```

Then update versioned references in repository documentation. At minimum, keep these files aligned with the manifest version:

- `README.md`
- `pack/README.md`

The validation script checks those files for stale release asset names and stale `/download/v.../` links.

Run locally before opening or merging the release PR:

```bash
bash tools/check-version-consistency.sh
```

### 3. Validate the repository locally

Run the Maven build:

```bash
mvn verify --no-transfer-progress
```

Optionally build the release assets locally for inspection:

```bash
bash tools/build-pack.sh --clean --github-repo mckeeh3/akka-ai-skills-pack
```

Inspect the generated files:

```bash
ls -lh dist/akka-ai-skills-pack-X.Y.Z.tar.gz dist/install-akka-ai-skills-pack-X.Y.Z.sh
```

You can also inspect the archive contents:

```bash
tar -tzf dist/akka-ai-skills-pack-X.Y.Z.tar.gz | sort | less
```

Do not commit `dist/` output.

### 4. Merge the release-prep change to `main`

Open and merge a PR containing the manifest/doc version updates.

Before cutting the tag, verify that `build-test.yml` is green for the target commit on `main`.

### 5. Cut the tag

Preferred method: run the **Cut release tag** workflow in GitHub Actions.

Use inputs like:

- `target_ref`: `main`, or the exact commit SHA you want to release
- `release_version`: `X.Y.Z` (optional but recommended)

The workflow creates and pushes annotated tag `vX.Y.Z`.

Alternative manual method:

```bash
git fetch origin main --tags
git checkout main
git pull --ff-only origin main
git tag -a vX.Y.Z -m "Release vX.Y.Z"
git push origin refs/tags/vX.Y.Z
```

Only use the manual method if you have already run the same validation checks locally.

### 6. Let the draft release workflow run

After the tag is pushed, `release.yml` should start automatically.

It will:

1. validate the tag against `pack/manifest.yaml`
2. rebuild and test from the tagged source
3. create a draft GitHub Release
4. upload the archive and versioned installer assets

If the workflow fails, fix the underlying source issue and decide whether to move the tag or cut a new patch version. Do not publish a draft release whose assets were produced by a failed run.

### 7. Review the draft GitHub Release

Before publishing, review:

- release title and generated release notes
- tag name is exactly `vX.Y.Z`
- attached archive is exactly `akka-ai-skills-pack-X.Y.Z.tar.gz`
- attached installer is exactly `install-akka-ai-skills-pack-X.Y.Z.sh`
- there are no stale assets from an earlier draft attempt

Optionally download and test the draft assets before publishing. For private draft assets, browser download or `gh release download` may be easier than `curl`.

Example with GitHub CLI:

```bash
gh release download vX.Y.Z \
  --pattern 'akka-ai-skills-pack-X.Y.Z.tar.gz' \
  --pattern 'install-akka-ai-skills-pack-X.Y.Z.sh' \
  --dir /tmp/akka-ai-skills-pack-release-test
```

Test the archive installer:

```bash
mkdir -p /tmp/akka-pack-target
tar -xzf /tmp/akka-ai-skills-pack-release-test/akka-ai-skills-pack-X.Y.Z.tar.gz -C /tmp
bash /tmp/akka-ai-skills-pack-X.Y.Z/install.sh \
  --location project \
  --project /tmp/akka-pack-target \
  --dry-run
```

### 8. Publish the release

When the draft looks correct, publish it from the GitHub Releases UI.

Once published, the public install URLs are available and users can install the release with `curl`.

## User installation from a published release

Users normally install a release with the versioned release installer.

### Install into the current directory

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/vX.Y.Z/install-akka-ai-skills-pack-X.Y.Z.sh | bash -s --
```

This installs into:

```text
./.agents
```

### Install into a specific project

```bash
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/vX.Y.Z/install-akka-ai-skills-pack-X.Y.Z.sh | bash -s -- --target-dir /path/to/project
```

This installs into:

```text
/path/to/project/.agents
```

### Installer options for users

The versioned release installer supports:

```text
--target-dir <dir>   Project directory that will receive .agents/. Default: current directory
--force              Forward --force to the bundled installer
--dry-run            Forward --dry-run to the bundled installer
--archive-url <url>  Override the release archive URL, mainly for testing
--keep-temp          Keep the temporary download/extract directory
--help               Show help
```

Examples:

```bash
# Preview actions without writing files
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/vX.Y.Z/install-akka-ai-skills-pack-X.Y.Z.sh | bash -s -- --target-dir /path/to/project --dry-run

# Replace existing pack-owned installed files
curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/vX.Y.Z/install-akka-ai-skills-pack-X.Y.Z.sh | bash -s -- --target-dir /path/to/project --force
```

## User installation from the archive

Users can also download and unpack the archive manually.

```bash
curl -fsSL -O https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/vX.Y.Z/akka-ai-skills-pack-X.Y.Z.tar.gz
tar -xzf akka-ai-skills-pack-X.Y.Z.tar.gz
cd akka-ai-skills-pack-X.Y.Z
```

Install into a project:

```bash
bash install.sh --location project --project /path/to/project
```

Install globally:

```bash
bash install.sh --location global
```

If `--location` is omitted, `install.sh` prompts interactively when stdin is a terminal.

The bundled `install.sh` supports:

```text
--location <mode>  Install location: project or global
--project <dir>    Project root used for project mode. Default: current directory
--force            Replace existing pack-owned files
--dry-run          Show planned actions without writing files
--help             Show help
```

## Installed layout

Project-mode installs create this structure under `<project-root>/.agents`.

Global installs create the same structure under `~/.agents`.

```text
.agents/
├── AGENTS.md
├── docs/
├── manifests/
│   └── akka-ai-skills-pack.yaml
├── resources/
│   └── examples/
│       └── java/
│           ├── pom.xml
│           ├── README.md
│           └── src/
│               ├── main/
│               └── test/
└── skills/
    ├── README.md
    ├── references/
    ├── app-descriptions/
    ├── akka-solution-decomposition/
    ├── akka-workflows/
    ├── akka-http-endpoints/
    └── ...
```

## Re-running and failure behavior

- `cut-tag.yml` refuses to create a tag that already exists locally or on `origin`.
- `release.yml` refuses to overwrite an already published GitHub Release.
- `release.yml` may update an existing draft release for the same tag.
- `tools/build-pack.sh` refuses to overwrite existing local build outputs unless `--clean` is used.
- The release installer downloads the archive matching its embedded version and tag.
- The bundled `install.sh` installs the full pack; there is no partial bundle selection during install.

## Permissions

The workflows use least-privilege permissions for their jobs:

- `build-test.yml`: `contents: read`
- `cut-tag.yml`: `contents: write` so it can push the release tag
- `release.yml`: `contents: write` so it can create or update the draft GitHub Release and upload assets

## Quick checklist

For maintainers:

1. Update `pack/manifest.yaml` to `X.Y.Z`.
2. Update versioned references in `README.md` and `pack/README.md`.
3. Run `bash tools/check-version-consistency.sh`.
4. Run `mvn verify --no-transfer-progress`.
5. Merge the release-prep PR to `main`.
6. Confirm `build-test.yml` is green on `main`.
7. Run **Cut release tag** with `target_ref=main` and `release_version=X.Y.Z`.
8. Wait for **Create draft release assets**.
9. Review assets on the draft release.
10. Publish the GitHub Release.
11. Share the versioned install command:

    ```bash
    curl -fsSL https://github.com/mckeeh3/akka-ai-skills-pack/releases/download/vX.Y.Z/install-akka-ai-skills-pack-X.Y.Z.sh | bash -s -- --target-dir /path/to/project
    ```
