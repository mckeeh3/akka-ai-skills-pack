# Shared Foundation Audit Checklist

This file is the starting checklist for `TASK-ADR-01-001`. The task should replace checklist placeholders with concrete findings.

## Shared artifacts to audit

- `app-description/app.md`
- `app-description/global/actors/**`
- `app-description/global/roles/**`
- `app-description/global/workers/**` if present or needed
- `app-description/global/policies/**`
- `app-description/global/surfaces/**`
- `app-description/global/agents/**`
- `app-description/global/tools/**`
- `app-description/global/traces/**`
- `app-description/domains/core-starter/domain.md`
- `app-description/domains/core-starter/capabilities/**`
- `app-description/domains/core-starter/data-state/**`

## Current skills-pack contract areas

- current-intent graph shape
- worker artifact contracts
- execution harness and actor-adapter separation
- governed tool vs capability separation
- global definition vs workstream binding separation
- AuthContext, role/capability, tenant/organization language
- source-alignment and lifecycle state
- runtime-validation scenario references
- audit/work trace obligations
- UI shell and structured surface conventions

## Finding categories

Use these categories in the audit task:

- `keep`: current artifact already matches the current contract
- `revise`: accepted intent exists but structure/vocabulary/links need refresh
- `split`: artifact mixes global definition with workstream binding
- `add`: required graph node is missing
- `defer`: valid but outside this mini-project; queue follow-up if needed
- `question`: unsafe to revise without a decision

## Expected audit output

`TASK-ADR-01-001` should add a concrete table with:

| Area | Current files | Finding | Required refresh | Affected workstreams | Follow-up task/question |
| --- | --- | --- | --- | --- | --- |
