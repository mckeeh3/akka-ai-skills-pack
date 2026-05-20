# TASK-STARTER-07-004: Make local AuthKit and first-admin bootstrap turnkey

## Goal

Make a clean scaffolded starter easy to run locally with WorkOS/AuthKit while keeping production auth secure and backend-authoritative.

## Required reads

- `templates/ai-first-saas-starter/.env.example`
- `templates/ai-first-saas-starter/frontend/.env.example`
- `templates/ai-first-saas-starter/README.md`
- `templates/ai-first-saas-starter/backend/src/main/resources/application.conf`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/AuthContextResolver.java`
- `templates/ai-first-saas-starter/backend/src/main/java/{{JAVA_PACKAGE_PATH}}/application/security/StarterSecurityComponents.java`
- `skills/akka-workos-user-auth/SKILL.md`
- `skills/akka-http-endpoint-jwt/SKILL.md`

## Work

1. Define explicit local setup steps for WorkOS/AuthKit callback URI, frontend public env, and backend JWT issuer/audience.
2. Make first-admin bootstrap semantics explicit and safe:
   - no silent privileged self-registration;
   - configured admin or seed account behavior is documented;
   - production use requires valid provider config and local membership state.
3. Add tests around bootstrap/admin linking behavior where possible.
4. Consider a clearly named test/dev identity path only if it cannot be confused with production auth.
5. Update docs and `.env.example` comments.
6. Update the pending queue entry for this task.

## Required checks

- `git diff --check`
- rendered-template `mvn test`
- frontend typecheck/build if frontend env/docs/source changed

## Done criteria

- A user can follow the scaffold README to configure AuthKit and sign in locally.
- First-admin behavior is safe, explicit, and covered by tests or documented constraints.
- Task status is marked `done` in `pending-tasks.md`.
- One git commit is created.

## Suggested commit message

`Document starter local auth bootstrap`
