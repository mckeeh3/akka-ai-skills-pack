# Akka AI Skills Pack Examples

Pack examples are installed into `.agents/skills/examples/**` because installed skills cite them as required reference material.

Use examples from the installed skills library or from a clone/fork of this repository:

- `.agents/skills/examples/akka-components/**` / `skills-pack/examples/akka-components/**` — focused Akka component examples
- `.agents/skills/templates/ai-first-saas-core-app/app-description/**` / `skills-pack/templates/ai-first-saas-core-app/app-description/**` — core app app-description surface contracts
- root `frontend/**` — runnable React/Vite workstream UI reference from the core app, not installed into `.agents`
- root `src/main/java/ai/first/**` and `src/test/java/ai/first/**` — runnable core app backend and tests, not installed into `.agents`

The installed skills directory contains skill guidance, shared skill references, docs, examples, templates, and tools:

```text
.agents/skills/
  README.md
  references/
  docs/
  examples/
  templates/
  tools/
  <skill-name>/SKILL.md
```

If a skill or doc still points to `.agents/resources/examples/**` or `resources/examples/**`, treat that as stale content and update it to `.agents/skills/examples/**` or an installed-relative `../examples/**` path from a skill.

Examples use `ai.first` package names. Treat those names as both reference examples and the fixed package convention for generated or domain-specific application code in this core-app-first repository and downstream forks.
