# Akka AI Skills Pack Examples

Examples are source-checkout reference assets. They are **not** installed into `.agents/` by the skills-only harness installer.

Use examples from a clone or fork of this repository:

- root `frontend/**` — runnable React/Vite workstream UI reference from the core app
- root `src/main/java/ai/first/**` and `src/test/java/ai/first/**` — runnable core app backend and tests
- `skills-pack/examples/akka-components/**` — focused Akka component examples
- `skills-pack/templates/ai-first-saas-starter/app-description/**` — source starter app-description surface contracts

The installed skills directory contains only skill guidance and shared skill references:

```text
.agents/skills/
  README.md
  references/
  <skill-name>/SKILL.md
```

If a skill or doc still points to `.agents/resources/examples/**`, `resources/examples/**`, or an installed Java/frontend example tree, treat that as stale content and update it to either a source-checkout path or a target-project path.

Examples may use `com.example` package names. Treat those names as reference-only. For generated or domain-specific application code, keep the root app's `ai.first` package unless the product deliberately performs a package rename.
