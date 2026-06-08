# Backlog 05: Trim and Repeat Review

## Goal

Perform repeated cross-repository review passes until the current goals are reflected consistently and unnecessary active content has been removed or rewritten.

## Suggested harness task breakdown

1. Run whole-pack stale-term and reference search pass, then remove/rewrite a bounded batch of remaining stale active content.
2. Check package/manifest/resource references after removals or rewrites.
3. Produce final alignment review and, if incomplete, append the next bounded task group before a new terminal verification task.

## Required checks

- `git diff --check`
- Relevant `rg` searches recorded in the final review.
- Manifest/reference checks for any removed files.

## Acceptance criteria

- No known active intake/planning guidance remains materially aligned to old CRUD/page/chatbot/component-first defaults.
- Final verification can either mark complete or append follow-up tasks with a new terminal verification task.
