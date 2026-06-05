# Akka component code examples for generation guidance

This directory contains curated Java code examples used by installed skills when generating or reviewing real Akka Java SDK and secure SaaS implementation patterns.

These files are **generation guidance examples**, not a duplicate app baseline, independent build module, generated output, or target application source tree. Do not copy this tree wholesale into a target app. Use individual files to study patterns, then implement the needed behavior in the target project's canonical source layout.

Included patterns:

- HTTP endpoints for `/api/me`, admin, and workstream surfaces
- Key Akka entities/views/consumers for identity, invitation, attention, audit, email/Resend delivery, workstream events, notifications, and governed agent runtime state
- request-based and autonomous agent examples for workstream/core-app capabilities
- fail-closed provider/runtime wrappers and governed loader/tool-boundary examples
- selected tests that show entity, service, agent, workstream, and durability-boundary checks
- `src/main/resources/application.conf` as a compact backend configuration reference

Maintenance rules:

1. Keep this tree small and intentional; include code only when it improves harness generation or review quality.
2. Add a file only when a skill/doc uses it as a concrete code example or when `REFERENCE-INDEX.md` records it as compact support context for a referenced pattern.
3. Remove examples that no longer teach or support an installed skill pattern.
4. Keep examples current with the implementation patterns they are meant to teach.
5. Prefer adding a focused doc snippet over adding a large dependency closure just to make this tree compile.
