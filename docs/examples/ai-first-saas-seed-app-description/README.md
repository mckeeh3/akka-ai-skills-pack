# AI-First SaaS Seed App Description Example

This directory is the reference app-description tree for a runnable **Akka Java + React/Vite/TypeScript AI-first SaaS seed app**.

Purpose:
- define the canonical seed app before implementation
- provide a reusable source asset for the skills pack
- describe secure multi-tenant SaaS foundations shared by generated apps
- describe a functional AI-first operating-model baseline: goals, plans, governed runtime agents, managed prompts/skills/manifests/tool boundaries, decisions, policy gates, traces, and outcomes
- map the seed app to Akka Java components and frontend/backend integration patterns

Example root:

```text
docs/examples/ai-first-saas-seed-app-description/app-description/
```

Repository/use distinction:
- in this repository, this is a reference example and future seed-generation source asset for the skills pack
- in a target project, the equivalent `app-description/` tree would be maintained in that project workspace

Primary implementation target:
- backend: Akka Java SDK
- frontend: React + Vite + TypeScript
- app class: secure multi-tenant AI-first SaaS shell with modular extension points
