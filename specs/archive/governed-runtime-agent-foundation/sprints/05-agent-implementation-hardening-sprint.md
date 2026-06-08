# Sprint 05: Agent implementation hardening

## Goal

Turn the now-mandatory governed runtime agent foundation from doctrine into clearer, implementation-ready guidance with focused skills, reference patterns, tests, and review guardrails.

## Why this sprint exists

The current pack now consistently requires managed AI-first SaaS agents: `AgentDefinition`, governed prompts, governed skills, manifests, tool boundaries, behavior-editing agents, traces, and UI surfaces. The remaining weakness is practical implementation depth. Several agent coverage matrix rows are still marked as pattern-only or missing executable/reference-backed guidance.

## Scope

This sprint adds or strengthens content for:

- an executable or docs-backed governed runtime agent reference slice;
- the runtime invocation resolver pattern from request/AuthContext to active agent invocation;
- a dedicated behavior-editing agent skill;
- tool permission boundary implementation guidance;
- governed agent testing guidance;
- model configuration governance;
- one-agent vs agent-team responsibility shaping;
- final verification and coverage-matrix reconciliation.

## Non-goals

- Do not build a full product app.
- Do not replace the already-completed mandatory foundation doctrine.
- Do not implement unrelated agent SDK features unless needed to support governed runtime agent foundations.
- Do not collapse all hardening work into one large task; each task must be fresh-session sized and git committed separately.

## Acceptance

- Future agents have concrete implementation paths for managed runtime agent invocation, behavior editing, tool boundaries, and governed-agent tests.
- The coverage matrix accurately distinguishes executable coverage from pattern-only coverage.
- Planning and implementation guidance remains consistent with secure AI-first SaaS, capability-first backend, and mandatory web UI doctrine.

## Related backlog

- `../backlog/05-agent-implementation-hardening-build-backlog.md`
