# Curated Akka component reference examples

This directory contains a curated source-attention subset of Java examples from the runnable core app. The files exist so installed skills can inspect concrete Akka Java SDK and core SaaS foundation patterns for implementation guidance.

These files are **reference snippets**, not a duplicate app baseline, independent build module, generated output, or target application source tree. Do not copy this tree wholesale into a target app. Use individual files to study patterns, then implement in the target project's canonical source layout.

Included patterns:

- HTTP endpoints for `/api/me`, admin, and workstream surfaces
- Key Akka entities/views/consumers for identity, invitation, attention, audit, email/Resend delivery, workstream events, notifications, and governed agent runtime state
- request-based and autonomous agent examples for workstream/core-app capabilities
- fail-closed provider/runtime wrappers and governed loader/tool-boundary examples
- selected tests that show entity, service, agent, workstream, and durability-boundary checks
- `src/main/resources/application.conf` as a compact backend configuration reference

Maintenance rules:

1. Keep this tree small and intentional; do not mirror all root `src/**` or `src/test/**` files.
2. Add a file only when a skill/doc uses it as a concrete repository example or when `REFERENCE-INDEX.md` records it as compact support context for a referenced pattern.
3. Remove examples that no longer teach or support an installed skill pattern.
4. Keep referenced files synchronized with the root core app implementation when the pattern changes.
5. Prefer adding a focused doc snippet over adding a large dependency closure just to make this tree compile.
