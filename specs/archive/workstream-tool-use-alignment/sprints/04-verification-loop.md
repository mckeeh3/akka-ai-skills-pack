# Sprint 04: Verification Loop

## Goal

Verify that the initial alignment pass fully integrates the workstream tool-use architecture across `skills-pack/`, and append follow-up tasks if the skills-pack is not yet aligned.

## Required verification behavior

The terminal verification task must:

1. compare completed work against this mini-project's README done state and conversation decisions;
2. search for terminology and doctrine drift across `skills-pack/docs/**`, `skills-pack/skills/**`, templates, examples, and validators;
3. check that installed-skill paths and references still validate;
4. run the relevant pack checks;
5. write verification notes;
6. mark the terminal task `done` only if no material alignment gaps remain;
7. if material gaps remain, append bounded follow-up tasks and a new terminal verification task, then leave the current task done or blocked according to the evidence.

## Completion signal

The mini-project is complete only when the terminal verification notes explicitly say the README done state has been achieved and no new bounded alignment tasks are needed.
