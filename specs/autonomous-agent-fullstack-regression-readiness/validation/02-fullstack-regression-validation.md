# TASK-AAFR-02-001 Fullstack Regression Validation

Date: 2026-06-01
Fresh scaffold: `/tmp/aafr-fullstack-FKxiVA`
Scaffold command:

```bash
tools/scaffold-ai-first-saas-starter.sh \
  --target /tmp/aafr-fullstack-FKxiVA \
  --template-dir templates/ai-first-saas-starter \
  --app-name "AAFR Validation Starter" \
  --app-slug aafr-validation-starter \
  --base-package ai.first \
  --force-empty
```

## Backend validation

Command:

```bash
cd /tmp/aafr-fullstack-FKxiVA/backend
mvn test
```

Result: PASS.

Evidence: Maven reported `Tests run: 173, Failures: 0, Errors: 0, Skipped: 0` and `BUILD SUCCESS`.

## Frontend validation

Commands:

```bash
cd /tmp/aafr-fullstack-FKxiVA/frontend
npm ci
npm test
npm run typecheck
npm run build
```

Results:

- `npm ci`: PASS; added 1 package and reported 0 vulnerabilities.
- `npm test`: FAIL; `Missing script: "test"`.
- `npm run typecheck`: FAIL; `Missing script: "typecheck"`.
- `npm run build`: FAIL; `Missing script: "build"`.

Observed available scripts from `npm run`:

- `build:web-ui` -> `tsc --project tsconfig.web-ui.json`
- `check:web-ui` -> `tsc --project tsconfig.web-ui.json --noEmit`
- `verify:opinionated-ai-first-saas` -> `bash tools/verify-opinionated-ai-first-saas-pack.sh`

Blocker recorded as `TASK-AAFR-02-002`.

## Focused AutonomousAgent vertical scans

Scan scope: `/tmp/aafr-fullstack-FKxiVA/src`, `/tmp/aafr-fullstack-FKxiVA/frontend/src`, `/tmp/aafr-fullstack-FKxiVA/app-description`, `/tmp/aafr-fullstack-FKxiVA/specs`.

Results:

- User Admin Access Review: 38 matching files for `accessReview|AccessReview|user_admin\.access_review`.
- Agent Admin Prompt-Risk: 27 matching files for `promptRisk|PromptRisk|agent_admin\.prompt_risk`.
- Audit/Trace Summary: 26 matching files for `auditTraceSummary|AuditTraceSummary|audit\.trace\.summary|summaryProgress`.
- Governance/Policy Impact: 26 matching files for `policyImpact|PolicyImpact|governance\.policy\.impact_analysis|governance_policy\.impact_analysis`.
- Stale Audit/Trace summary contract scan: 0 matches for `audit\.trace\.summaryTask\.v1`.

## Conclusion

The stale Audit/Trace summary contract regression is clear in a fresh scaffold: backend `mvn test` passes, and no stale `audit.trace.summaryTask.v1` source/spec/frontend matches remain.

Integrated fullstack readiness is not complete because the scaffold frontend package does not expose the required generic `test`, `typecheck`, or `build` npm scripts. The available frontend scripts appear to be pack-oriented (`check:web-ui`, `build:web-ui`) rather than scaffold-readiness-oriented. Fix is bounded in `TASK-AAFR-02-002` before the integrated handoff task should run.
