# Conversation Capture: Foundation Surface Completion Tracker

The user observed that many foundation app features were not working during manual User Admin testing and asked whether the app-description and skills-pack process were contributing to incomplete implementation.

Decisions captured for this mini-project:

- Track every existing foundation dashboard/surface across the five core workstreams.
- For every surface, track three separate objectives: fully-specified, fully-implemented, and fully-tested.
- Use a mini-project under `specs/foundation-surface-completion-tracker/` rather than a single standalone file.
- Allow a large pending-task queue of one sub-task per surface/objective so the harness can execute the next pending sub-task in a fresh context.
- Preserve the runtime completion doctrine: user-visible feature work is not complete until the intended local Akka/API/UI runtime path is proven.
- Use `done` as the completed status to align with existing pending-task queue conventions.
