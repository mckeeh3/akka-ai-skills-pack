# Backlog 08: Workstream Model Binding Readiness

## Outcome

Every generated workstream agent has a specific governed model binding: either an explicit per-agent `ModelConfigRef`/`ModelPolicy` or a deliberate inherited default recorded in the expert bundle, runtime resolver, traces, and tests.

## Backlog items

1. **Audit model-binding readiness**
   - Review workstream expertise docs, model governance skill, app-description/readiness/planning guidance, seed bundles, and starter runtime.
   - Produce a concise gap matrix for model binding.

2. **Align expertise and app-description contracts**
   - Add model binding fields to the workstream expert bundle contract.
   - Update seed foundation expert bundles to name explicit or inherited model bindings.
   - Ensure readiness blocks missing model bindings for LLM-backed workstreams.

3. **Add executable starter model-governance state and runtime checks**
   - Add `ModelConfigRef` and `ModelPolicy` records or starter-appropriate equivalents.
   - Seed approved default model refs.
   - Resolve and validate model refs before runtime invocation.
   - Emit model-use trace facts without provider secrets.

4. **Add model-governance tests**
   - Cover active model success, disabled/unknown/cross-scope denial, provider-policy denial, no secret exposure, fallback/no-fallback behavior where in scope, and model refs in traces.

5. **Align planning/generation and coverage docs**
   - Ensure PRD/spec/backlog/app-generation guidance creates model-binding tasks for every new workstream agent.
   - Update `docs/agent-coverage-matrix.md` after executable model-governance coverage exists.

6. **Sprint review**
   - Confirm whether workstream expertise is now fully ready for generated apps with per-workstream governed model binding.
