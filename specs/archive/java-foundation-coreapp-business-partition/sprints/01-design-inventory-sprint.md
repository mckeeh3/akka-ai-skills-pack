# Sprint 01: Package Design and Inventory

## Objective

Define the exact target package map and classify current Java classes before moving source files.

## Scope

- Inventory current `ai.first` Java packages/classes.
- Classify classes as `foundation`, `coreapp`, test-only, or business-extension seam.
- Define old-to-new package map.
- Identify risky mixed-responsibility classes and required checks.

## Acceptance criteria

- Future migration tasks can move packages without guessing.
- Ambiguous classes are flagged with explicit recommendations or bounded follow-up tasks.
- No production Java files are moved in this sprint unless the task brief explicitly permits tiny package-info/docs changes.
