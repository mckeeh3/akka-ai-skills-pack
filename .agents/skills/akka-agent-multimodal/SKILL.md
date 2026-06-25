---
name: akka-agent-multimodal
description: Implement Akka Java SDK multimodal agents using UserMessage.from(...), image/PDF message content, and contentLoader(...). Use when image or PDF inputs are the main concern.
---

# Akka Agent Multimodal

Use this skill when an agent should send text together with images or PDFs.


## Generated SaaS input contract

Use `../references/generated-saas-input-contract.md` as the shared gate. Do not implement generated SaaS runtime code until the required capability, AuthContext/scope, DTO, side-effect, trace, and test inputs are present or explicitly deferred; otherwise repair the brief or route back to `agent-workstream-apps` + `capability-first-backend`.

## Worker/tool/capability alignment

For generated AI-first SaaS app work, treat the agent runtime, autonomous task loop, or governed artifact in scope as a software-worker harness concern, not as the product operation or authorization boundary. Keep the chain explicit:

```text
software worker
→ Akka Agent/AutonomousAgent harness or focused governance artifact
→ actor adapter (`agent_tool_call`, `human_chat_tool_plan`, workflow/timer/consumer/API/MCP/internal adapter as applicable)
→ governed tool
→ backend capability
→ Akka/frontend implementation
```

Human surface availability, prompt/skill/reference text, model output, task instructions, and Akka tool registration do not grant tool authority. A model-facing tool, loader, or autonomous task action may be exposed only when the active workstream tool catalog, governed tool contract, backend `AuthContext`, and `ToolPermissionBoundary` explicitly allow that actor adapter; denials and approval-required paths must fail closed and be traced.


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
3. Add media with `ImageMessageContent.fromUrl(...)` and `PdfMessageContent.fromUrl(...)` only after backend authorization has resolved the caller, tenant/customer scope, and allowed document/media capability.
4. Use `contentLoader(...)` when content is not publicly fetchable.
5. Keep the loader deterministic and explicit about MIME types, byte limits, and safe denials for unsupported media.
6. Prefer a per-request loader when credentials vary by call; keep request credentials in backend runtime state, never in prompts, model-visible content, browser payloads, traces, or URLs returned to the model.
7. Reject model- or caller-supplied arbitrary URLs, file paths, or resource ids that bypass governed media access.

## Pattern reference

- a domain-specific multimodal/document analysis agent
  - multimodal request with text, image, and PDF
  - per-request `ExampleContentLoader`
  - deterministic loader implementation for agent-oriented reference use

## Review checklist

Before finishing, verify:
- the configured model supports the media types being sent and provider-unconfigured behavior fails closed before media disclosure
- the user message is actually multimodal, not plain text only
- the content loader returns the correct MIME type when needed
- the loader stays stateless or request-scoped when credentials differ
- media fetches enforce tenant/customer scope, redaction/classification, size limits, and denial traces without leaking cross-tenant existence
