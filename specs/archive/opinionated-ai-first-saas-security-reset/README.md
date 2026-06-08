# Opinionated AI-first SaaS security reset

This planning package turns the skills pack into an aggressively opinionated AI-first SaaS pack.

Non-negotiable direction:
- Security is not optional for any project using this pack.
- The pack's target output is AI-first SaaS applications.
- Residual generic Akka-app guidance must be removed, reframed as AI-first SaaS support material, or explicitly quarantined as low-level examples that cannot drive routing.
- Generated applications must start from a secure SaaS foundation: identity, local authorization state, tenant/customer isolation, user administration, backend authorization, audit, and tests.
- AI-first applications should model agents doing as much bounded work as possible, with humans acting primarily as supervisors, high-level decision makers, approvers, auditors, policy owners, and outcome owners.

Execution rule:
- Execute exactly one task per fresh harness session.
- Each task must commit its work to git before completion.
- Do not combine tasks.

Queue:
- `pending-tasks.md`
