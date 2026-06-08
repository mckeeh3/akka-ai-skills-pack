# Conversation Capture

## Source discussion

After verifying the six five-core workstream mini-projects were complete, the user asked whether `docs/examples/core-ai-first-saas-input/` PRDs were used as input to the core workstreams.

A repository search found no direct references from the six mini-projects or starter/template implementation artifacts to `docs/examples/core-ai-first-saas-input/`, `10-canonical-core-app-prd.md`, or the module PRD names. The answer concluded that those PRDs were not directly used; they were at most indirect background/provenance.

The recommendation was to create a focused reconciliation mini-project to compare the completed five workstream contracts/capability inventories against the older module PRDs and either confirm coverage, append follow-up tasks for gaps, or document that the older PRDs are superseded by the newer workstream-oriented core plan.

The user approved: "go ahead with your recommendation".

## Accepted decisions

- Create a repository mini-project under `specs/`.
- Do planning and queue materialization first; do not implement runtime fixes in the same session.
- Treat `core-ai-first-saas-input/` as source PRD input that must be reconciled, not automatically assumed covered.
- Preserve the current five-core workstream completion unless concrete gaps are found.

## Known facts

- `docs/examples/core-ai-first-saas-input/README.md` says the directory contains canonical example input documents and identifies `10-canonical-core-app-prd.md` as the hard PRD target for full core generation.
- `docs/skills-pack-user-guide.md` currently names the newer `docs/examples/ai-first-saas-core-app-domain/` set as the preferred full-core rollout input, while keeping `core-ai-first-saas-input/` as an older module-sequenced end-to-end sample.
- The completed five-core workstream specs contain `workstream-contract.md` and `capability-inventory.md` files for My Account, User Admin, Agent Admin, Audit/Trace, and Governance/Policy.

## Risks

- The older module PRDs may contain full-core requirements intentionally beyond v0 scope. These should not be treated as v0 regressions unless the completed workstream claims the same scope.
- If the older PRDs are obsolete, docs should say so clearly rather than leaving two competing canonical input paths.
- If requirements are partially covered, follow-up tasks should be bounded by workstream and capability rather than creating a broad "finish full core" task.

## Unresolved questions

No blocking question is needed to start reconciliation. The reconciliation task may create pending questions if it finds conflicting source-of-truth claims between old and new core input sets.
