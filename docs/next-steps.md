# Next steps after the web UI/static content family

Purpose: define what this repository should cover next, after the web UI/static content work is complete.

## Working decision

This repository should expand beyond the ten core Akka SDK component families, but only where the result is still:
- Akka-specific
- agent-oriented
- low-ambiguity
- test-backed
- cheaper for future coding agents to load than the official human-oriented docs

That means we should broaden SDLC coverage selectively.

We should cover the parts of the SDLC where Akka semantics, application structure, runtime behavior, and deployment choices materially affect implementation correctness.

We should not prioritize generic SDLC assets that are mostly tool- or process-management concerns.

## Scope boundary

### In scope for this repository

1. Architecture selection guidance for Akka applications
2. Bootstrap, service setup, and dependency injection patterns
3. Configuration and environment setup patterns
4. Security patterns at service and endpoint boundaries
5. Testing patterns beyond component-local examples
6. Runtime inspection and operational verification patterns
7. Deployment-readiness and platform-readiness guidance
8. Upgrade and evolution patterns for long-lived Akka applications

### Lower priority / generally out of scope

These should only be covered when there is a distinctly Akka-specific angle:
- generic feature-spec writing workflows
- generic branch and issue workflows
- generic task decomposition
- generic project governance templates
- generic release-management process docs

## Guiding principles for SDLC expansion

Every new SDLC topic should satisfy most of these:
- improves agent correctness on real Akka projects
- cannot be handled well enough by generic coding patterns alone
- benefits from focused local examples and tests
- has stable routing value by file name alone
- can be represented with small, canonical examples instead of broad tutorials

## Recommended expansion order

## 1. Service setup, bootstrap, and configuration

### Why next

After web UI/static content, the most valuable gap is the path from isolated components to a correctly wired Akka service.

Agents often generate valid entities, workflows, and endpoints, but make mistakes in:
- `ServiceSetup`
- dependency injection boundaries
- environment-specific config
- secrets and provider wiring
- HTTP client/provider setup
- agent model provider setup

### Deliverables

- A top-level skill family for bootstrap and service setup
- Focused examples for:
  - `ServiceSetup` and bootstrap composition
  - dependency registration and constructor injection
  - `application.conf` structure and overrides
  - environment-specific configuration
  - secret-backed configuration boundaries
  - outbound HTTP client setup with `HttpClientProvider`
  - model provider wiring for agent use cases
- Tests or verification examples where practical
- One routing/reference doc summarizing which setup example to read first

### Suggested artifacts

- `skills/akka-service-setup/SKILL.md`
- `skills/akka-configuration/SKILL.md`
- `docs/service-setup-pattern-selection.md`
- focused example and test files under `src/main/java/com/example/...`

### Exit criteria

- agents can route to a single setup skill for service bootstrap questions
- repo contains at least one canonical example for each common setup concern
- guidance clearly separates domain/application/api wiring responsibilities

## 2. Security and boundary patterns

### Why next

Security mistakes are high-cost and recurrent. Akka-specific guidance is especially useful around ACLs, JWT handling, request context, internal endpoints, and mixed public/private surfaces.

### Deliverables

- Consolidated security routing docs across HTTP, gRPC, MCP, and web UI
- Focused examples for:
  - public vs internal endpoint partitioning
  - method-level ACL overrides
  - JWT claim extraction and validation
  - mixed UI/API security boundaries
  - service-to-service patterns
  - safe exposure of static resources and streamed endpoints
- Review checklist for common Akka security mistakes

### Suggested artifacts

- `docs/security-pattern-selection.md`
- `docs/security-review-checklist.md`
- companion skills only where existing endpoint skills are not sufficient

### Exit criteria

- agents can choose the right access-control pattern quickly
- repo has examples for both public and restricted edge surfaces
- common mistakes are captured in a lightweight review checklist

## 3. Observability and runtime inspection

### Why next

Once setup and security are covered, the next big gap is how to verify a running Akka service and how to design code so runtime behavior is inspectable.

This is especially important for:
- workflows
- views
- timers
- consumers
- agents
- web UIs backed by Akka endpoints and streams

### Deliverables

- Runtime inspection playbooks for each major component type
- Small docs for logging, tracing, metrics, and correlation context expectations
- Example patterns for:
  - request-context-aware logging
  - workflow/agent traceability
  - endpoint and stream observability
  - backoffice-driven verification flows
- A compact reference that maps acceptance criteria to runtime checks

### Suggested artifacts

- `docs/runtime-inspection-playbook.md`
- `docs/observability-pattern-selection.md`
- focused examples and tests where new code materially improves routing clarity

### Exit criteria

- a future agent can verify running behavior without reverse-engineering the whole app
- repo shows how Akka runtime state can be inspected and correlated with API behavior

## 4. Deployment readiness and platform readiness

### Why next

This repo should help agents distinguish between code that compiles and services that are actually ready to ship.

### Deliverables

- Akka-specific deployment-readiness checklist
- Descriptor and routing examples if they add clear local value
- Guidance for:
  - local-to-platform transition
  - secrets/environment handoff
  - route and hostname concerns
  - production readiness for HTTP, gRPC, workflows, agents, consumers, and timers
- Minimal examples only where local code/config structure is reusable

### Suggested artifacts

- `docs/deployment-readiness.md`
- `docs/platform-transition-checklist.md`

### Exit criteria

- agents can identify missing production-readiness concerns before deployment
- readiness docs stay concise and Akka-specific

## 5. Evolution, migration, and upgrade safety

### Why later, but important

Long-lived Akka systems need more guidance around safe change than greenfield examples do.

### Deliverables

- Event and state evolution guidance
- view evolution and rebuild guidance
- protobuf evolution guidance
- prompt/model/config evolution guidance for agent-enabled apps
- upgrade notes for SDK-version changes when local examples are affected

### Suggested artifacts

- `docs/evolution-patterns.md`
- `docs/sdk-upgrade-notes.md`

### Exit criteria

- repo contains concrete guidance for making safe changes to existing Akka systems
- examples focus on the change patterns most likely to trip up coding agents

## 6. Akka architecture selection support

### Why keep on roadmap

This is a high-value cross-cutting layer: helping agents choose the right Akka building block before implementation starts.

### Deliverables

- decision docs for:
  - Event Sourced Entity vs Key Value Entity
  - Workflow vs Consumer vs Timed Action
  - HTTP vs gRPC vs MCP
  - bundled web UI vs separate frontend
  - view-backed query vs direct component lookup
- lightweight matrices and “read this first” routing docs

### Suggested artifacts

- extend `skills/akka-entity-type-selection/`
- add `docs/interface-selection.md`
- add `docs/web-delivery-selection.md`

### Exit criteria

- agents can make better up-front choices with fewer retries and less rework

## Proposed sequence

1. Finish `web-ui/static-content` family
2. Service setup, bootstrap, and configuration
3. Security and boundary patterns
4. Observability and runtime inspection
5. Deployment/platform readiness
6. Evolution and upgrade safety
7. Broaden architecture-selection references across all major interface choices

## Delivery model for each new topic family

For each future SDLC area, follow the same package shape:
- one broad entry skill
- a small number of narrow companion skills only if needed
- one compact reference doc in `docs/`
- one or more focused executable examples in `src/main/java`
- tests that act as reference material, not just verification
- updates to any coverage matrix or routing doc affected by the new material

## Success criteria for the broader SDLC expansion

We should consider the next phase successful if:
- the repo remains primarily Akka-specific rather than generic SDLC guidance
- each added topic reduces reading cost for future agents
- examples stay small and canonical
- runtime/testing/deployment guidance is grounded in executable local references
- future agents can route to the right pattern with minimal ambiguity
