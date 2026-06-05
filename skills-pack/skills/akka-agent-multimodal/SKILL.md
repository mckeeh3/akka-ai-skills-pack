---
name: akka-agent-multimodal
description: Implement Akka Java SDK multimodal agents using UserMessage.from(...), image/PDF message content, and contentLoader(...). Use when image or PDF inputs are the main concern.
---

# Akka Agent Multimodal

Use this skill when an agent should send text together with images or PDFs.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. For this skill, require the task/app-description/spec/backlog to name or explicitly defer the relevant functional agent/internal trigger, capability, AuthContext/scope, DTOs, side effects, audit/work traces, and tests before implementing generated SaaS runtime code. If those inputs are absent, route back to `agent-workstream-apps` + `capability-first-backend` or repair the task brief instead of guessing.

## Required reading

Read these first if present:
- `akka-context/sdk/agents/prompt.html.md`

## Use this pattern when

- the model should inspect an image, PDF, or both together with text instructions
- media is fetched from authenticated or custom-backed URLs
- request-specific credentials are needed to load message content

## Core pattern

1. Build the user prompt with `UserMessage.from(...)`.
2. Add text with `TextMessageContent.from(...)`.
3. Add media with `ImageMessageContent.fromUrl(...)` and `PdfMessageContent.fromUrl(...)`.
4. Use `contentLoader(...)` when content is not publicly fetchable.
5. Keep the loader deterministic and explicit about MIME types.
6. Prefer a per-request loader when credentials vary by call.

## Pattern reference

- a domain-specific multimodal/document analysis agent
  - multimodal request with text, image, and PDF
  - per-request `ExampleContentLoader`
  - deterministic loader implementation for agent-oriented reference use

## Review checklist

Before finishing, verify:
- the configured model supports the media types being sent
- the user message is actually multimodal, not plain text only
- the content loader returns the correct MIME type when needed
- the loader stays stateless or request-scoped when credentials differ
