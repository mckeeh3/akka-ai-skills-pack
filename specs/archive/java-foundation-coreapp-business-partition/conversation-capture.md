# Conversation Capture: Java Foundation/Coreapp/Business Package Partition

## User goals and decisions

- The project should keep the standard Akka Java package structure: `something.api`, `something.application`, and `something.domain`.
- The specific top-level `something` is not the important partitioning point; the important partition is inside `api`, `application`, and `domain`.
- The root app needs a clear separation between:
  - common/base-layer code used by all domains;
  - the first built-in app area with the five core app workstreams;
  - future user/business-specific domains.
- The word `domain` is overloaded: it means the Akka Java domain layer and also a business/product domain. Avoid adding more ambiguity in package names.
- `ai.first.application.workstreams.*` is too ambiguous because both the built-in core app and future business domains can have workstreams.
- The package model should be outside-in: understandable to users who fork this project to build CRM, ERP, and other business-specific SaaS domains using the provided skills.
- The accepted naming set is `foundation`, `coreapp`, and `business`.

## Accepted target convention

Preserve:

```text
ai.first.api.*
ai.first.application.*
ai.first.domain.*
```

Partition within those layers:

```text
ai.first.api.foundation.*
ai.first.application.foundation.*
ai.first.domain.foundation.*

ai.first.api.coreapp.*
ai.first.application.coreapp.*
ai.first.domain.coreapp.*

ai.first.api.business.<business-area>.*
ai.first.application.business.<business-area>.*
ai.first.domain.business.<business-area>.*
```

## Accepted constraints

- The refactor should be planned as a multi-session queue.
- Preserve existing runtime behavior and validation.
- Do not generate a real business domain as part of this package move.
- Avoid inside-out terminology that makes sense only to this repository's maintainers.
- Keep downstream business extensions additive and merge-friendly.

## Risks

- Mechanical package moves can break Akka component discovery, imports, tests, config, and docs.
- Some existing classes combine foundation and core app responsibilities and may need careful classification.
- A too-broad move in one session could be hard to review and debug.
- If docs/skills are not updated, generated business extensions may continue using ambiguous package paths.

## Unresolved questions

No blocking question remains for planning. The first implementation task should inventory current classes and may add a pending question if a class responsibility cannot be safely classified from code and docs.
