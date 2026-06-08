# Build Backlog 05: Packaging and Install Modes

## Items

1. Move or copy the starter app into the chosen pack template/resource location.
2. Add or update install/init scripts so users can choose skills-only or starter-app scaffold mode.
3. Ensure scaffold mode refuses unsafe overwrites and supports empty/new project initialization.
4. Add template validation checks in packaging tests or scripts.
5. Update user/developer docs to explain skills-only install, starter scaffold install, Java base package selection, and extension workflow.
6. Update routing skills so generated new-project work prefers extending the starter app when scaffolded.

## Completion signal

The installed pack can provide a working starter app intentionally, without polluting existing projects.
