# Intent-driven usage flow

Use the repository in this sequence:

1. **Read the input artifact first**
   - PDR
   - requirements doc
   - user story
   - process description
   - API sketch
   - UI brief
   - other spec file

2. **Decompose before coding**
   - start with `../skills/akka-solution-decomposition/SKILL.md`
   - identify the Akka component set, boundaries, and delivery model

3. **Resolve focused structural decisions**
   - use `../skills/akka-entity-type-selection/SKILL.md` when the remaining question is Event Sourced Entity vs Key Value Entity
   - use other focused routing in `../skills/README.md` when the solution shape is partly known but one design choice is still open

4. **Load only the focused implementation skills**
   - use `../skills/README.md`
   - read only the Stage 3 skills that match the chosen components

5. **Generate code and tests last**
   - implement component by component
   - use `../src/` examples and focused `../docs/` references as pattern support

## Rule of thumb

Code generation is a downstream phase.
Do not start writing Akka components until decomposition is complete and any key structural decisions are resolved.
