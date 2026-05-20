# Task Brief: Refresh or Label DCA UI Example

## Task

Refresh or explicitly label the DCA app-description UI example so it aligns with the current functional-agent workstream design.

## Expected outputs

- targeted edits under `docs/examples/ai-first-dca-app-description/`, especially `app-description/55-ui/**`
- optional split files if the task chooses migration over labeling
- queue status update and git commit

## Acceptable approaches

Choose one based on smallest safe change:

1. **Label compact/consolidated**: keep `55-ui/ui-surfaces.md` but state it is a compact DCA-specific consolidated UI contract, not the canonical seed split structure.
2. **Migrate split structure**: add or revise `12-workstreams/` and `55-ui/` files to mirror the seed-style functional-agent/surface/UI split.

## Checks

- Functional agents, surfaces, capabilities, routes/deep links, state/realtime, and style are clearly placed.
- The seed app remains the preferred canonical structure reference.

## Completion

Mark `TASK-AWDD-04-001` done after commit.
