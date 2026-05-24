# Production-ready five core v0 release handoff

Date: 2026-05-24

## Validation summary

The production-ready five-core v0 starter is ready for a real local trial from this repository state.

Passed checks:

- `tools/validate-ai-first-saas-starter-fullstack.sh`
  - scaffolds a disposable starter project
  - runs rendered backend `mvn test`
  - runs rendered frontend install, tests, typecheck, and build
  - scans built static assets for backend-secret markers
  - runs the model-provider smoke path when `OPENAI_API_KEY` is present
- `bash tools/check-version-consistency.sh`
- `bash tools/build-pack.sh --clean --no-archive`
- source install smoke into a temp project with `bash install.sh --location project --project <tmp>`
- installed scaffold dry-run from the temp project's `.agents/bin/scaffold-ai-first-saas-starter.sh`
- provider smoke skip mode with `OPENAI_API_KEY` unset
- real provider smoke mode was exercised because `OPENAI_API_KEY` was present in the validation environment
- `git diff --check`

Validation discovery fixed in this task:

- The initial scaffolded backend test treated the literal metadata key `providerSecret` as leaked secret evidence. The starter now uses the non-secret metadata label `providerCredentialValue`, preserving browser-safe redaction semantics while allowing the secret-boundary test to assert no provider-secret marker is emitted.

## Recommended local trial process

Use a disposable project outside this skills-pack repository.

1. Install from the source checkout or a built release into the target project:

   ```bash
   bash /path/to/akka-ai-skills-pack/install.sh --location project --project /tmp/prod-v0-trial
   ```

2. Dry-run the starter scaffold first:

   ```bash
   /tmp/prod-v0-trial/.agents/bin/scaffold-ai-first-saas-starter.sh \
     --target /tmp/prod-v0-trial \
     --app-name "Production v0 Trial" \
     --base-package ai.first \
     --dry-run
   ```

3. Scaffold for real only into an empty or bootstrap-only target, then commit the scaffolded baseline.

4. Copy `.env.example` to `.env`; keep backend-only secrets in the backend environment only. Do not place `OPENAI_API_KEY`, WorkOS API keys, Resend keys, JWT secret material, or admin bootstrap values in `frontend/.env*`.

5. Run starter checks:

   ```bash
   mvn test
   cd frontend
   npm install
   npm test -- --run
   npm run typecheck
   npm run build
   cd ..
   ```

6. Validate provider failure behavior with provider variables absent. Workstream message submission should be blocked with safe actionable recovery copy, not deterministic placeholder text.

7. Validate real model behavior with backend-only provider variables present:

   ```bash
   export OPENAI_API_KEY=...
   # optional: export OPENAI_MODEL_ID, OPENAI_API_BASE_URL, OPENAI_REQUEST_TIMEOUT_SECONDS
   .agents/bin/scaffold-ai-first-saas-starter.sh --target /tmp/prod-v0-real --app-name "Production v0 Real" --base-package ai.first
   cd /tmp/prod-v0-real
   mvn -DrealModelProviderSmoke=true -Dtest=RealModelProviderSmokeTest test
   ```

8. For manual UI validation, start local Akka with the required backend environment, sign in through AuthKit as an admin bootstrap user, select each core workstream, submit one prompt, confirm provider-backed `markdown_response` output, inspect prompt/model/work trace metadata, and verify no provider secret appears in `/api/me`, workstream payloads, trace displays, frontend env, or built assets.

## Release recommendation

Cut a new release when maintainers are ready to publish the production-ready five-core v0 starter behavior. The current manifest version is `0.2.9`; a release cut should use the repository release flow and update versioned references if the release version changes.

Suggested release commands after a clean working tree:

```bash
bash tools/check-version-consistency.sh
bash tools/build-pack.sh --clean
# or use the guided release helper:
bash tools/release.sh
```

## Outside production-ready v0 scope

The following remain outside this v0 completion and should be handled by later full-core/domain-specific tasks:

- richer full-core My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy surfaces beyond the five initial markdown v0 shell
- complete invitation onboarding UX beyond the starter foundation seams
- production deployment hardening beyond local Akka validation
- app-specific domain workstreams and capabilities
- broader tenant/customer billing, support access, and full security review expansion beyond the starter checks
