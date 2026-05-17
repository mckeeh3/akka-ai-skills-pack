# Sprint 4: Tests, UI, Observability, and Readiness

## Sprint goal

Replace placeholder verification and stale UI/observability notes with concrete app-description requirements tied to the refreshed foundation and capability contracts.

## Scope

- Replace placeholder test READMEs with concrete acceptance, negative, regression, and operational test specs.
- Include foundation security tests and Supplies Autopilot capability tests.
- Strengthen observability requirements for foundation security events and DCA work/decision/policy/tool/data-access/outcome traces.
- Reconcile `55-ui/ui-surfaces.md` with the selected `55-ui/style-guide.md` and current mandatory foundation UI surfaces.
- Update readiness status and readiness summary.

## Expected outputs

- Concrete test files and test index.
- Updated observability layer.
- Consistent UI surface/style guidance.
- Accurate readiness status and review summary.

## Acceptance behavior

A future agent should see tests as authoritative description artifacts, not placeholders, and should understand that UI/observability/readiness are first-class generation gates.

## Defer list

- Do not implement tests in Java unless a later executable-slice task asks for it.
- Do not invent external DCA/ERP/billing/fulfillment contracts.
- Do not claim full generation readiness while integration/evaluation gaps remain.
