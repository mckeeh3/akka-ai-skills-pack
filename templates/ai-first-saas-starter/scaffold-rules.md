# Scaffold Rules: AI-First SaaS Starter

## Required package prompt

Before writing Java source files, ask:

> What Java base package should I use for generated code? Press Enter to use `ai.first`.

Use `ai.first` only when the user accepts or defers the choice.

## Rendering rules

1. Replace placeholders in file paths before copying.
2. Replace placeholders in file contents before writing.
3. Derive `{{JAVA_PACKAGE_PATH}}` by replacing `.` with `/` in `{{JAVA_BASE_PACKAGE}}`.
4. Render backend files into the target root, not into a nested `backend/` directory, unless the scaffold command explicitly supports a monorepo layout.
5. Refuse to overwrite existing application files unless the user selects an explicit force mode.

## Backend package layout after rendering

```text
src/main/java/<base package>/domain
src/main/java/<base package>/application
src/main/java/<base package>/api
src/test/java/<base package>
```

The starter must not render `com.example` unless explicitly selected by the user.
