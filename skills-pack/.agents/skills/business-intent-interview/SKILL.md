---
name: business-intent-interview
description: Guide an interactive Stage 1 interview with SMB owners or representatives to capture business goals, processes, pain points, inferred CRM/ERP/operations needs, confirmations, rejected ideas, and open questions as app-description input without premature app design.
---

# Business Intent Interview

Use this skill for live or iterative Stage 1 business intake with SMB owners, operators, department leads, staff representatives, or other business stakeholders.

This is an interview skill. It helps the interviewee express business processes, goals, and intent in their own language, while the agent extrapolates likely business-specific process detail and asks for confirmation.

## Required Reading

Read:

- target project `AGENTS.md`, when present
- `../docs/business-intent-interview-process.md`
- `../docs/skills-pack-user-guide.md` when path/install boundaries are unclear

Do not load Stage 2 app-description or implementation skills unless the user asks to transform an accepted interview artifact into current intent or implementation planning.

## Goal

Create a mutually acceptable business-intent capture that can later be ingested by skills-pack Stage 2 agents.

The output is accepted app-description input, not accepted app design.

## Interview Stance

Act as an extrapolating business analyst:

- ask practical business questions in non-technical language;
- preserve the interviewee's words and examples;
- infer likely business workflows and adjacent needs from sparse input;
- label all extrapolations as hypotheses until confirmed;
- summarize frequently and invite correction;
- avoid asking the interviewee to design workstreams, Akka components, governed-tools, APIs, tables, or screens.

## Interview Loop

1. Capture the business topic, interviewee role, and what they want to improve.
2. Ask focused questions about products/services, customers, sales, delivery, support, money flow, people, tools, decisions, timing, risks, pain points, and success measures.
3. Extrapolate likely adjacent needs such as CRM, ERP/accounting, scheduling, fulfillment, inventory/assets, customer service, compliance, reporting, approvals, and exceptions.
4. Present inferred process detail as candidates for confirmation, correction, rejection, or prioritization.
5. Keep explicit input, inferred hypotheses, confirmed intent, rejected/out-of-scope items, candidate future needs, and open questions separate.
6. Repeat until the interviewee accepts the capture as accurate enough for ingestion.
7. Save the artifact under `docs/input/business-interviews/<yyyy-mm-dd>-<topic>.md` when the user asks to write it or when the session reaches an accepted capture.

## Question Guidance

Ask only a few questions at a time. Prefer concrete prompts:

- "Walk me through what happens from first customer interest to money collected."
- "Where does work get stuck or require owner judgment?"
- "Which steps happen in spreadsheets, email, text messages, or memory?"
- "Who approves exceptions, discounts, refunds, purchases, schedule changes, or risky work?"
- "What do you wish you could see every morning about this part of the business?"
- "Based on what you said, I suspect these processes may matter. Which are real, missing, or irrelevant?"

## Artifact Requirements

Use the section model from `../docs/business-intent-interview-process.md`. At minimum include:

- source context and interview status;
- explicit input;
- agent-inferred business model;
- confirmed intent;
- current process;
- pain points;
- desired future state;
- actors and responsibilities;
- events, triggers, and timing;
- decisions, rules, and exceptions;
- systems, documents, and data;
- candidate CRM / ERP / operations needs;
- examples and scenarios;
- success measures;
- rejected or out of scope;
- open questions;
- agent summary for ingestion;
- interviewee confirmation.

## Handoff

When the interview is accepted, route next to:

- `business-intent-to-app-input` for cleanup if the capture is rough;
- `app-description-input-normalization` when the user wants Stage 2 current-intent processing;
- `app-description-intake-router` and focused app-description skills when the accepted input should update app-description.

## Guardrails

- Do not turn unconfirmed inferences into confirmed requirements.
- Do not skip obvious business process areas just because the initial input is sparse.
- Do not make app architecture decisions in Stage 1.
- Do not promise implementation readiness from interview acceptance.
- Do not write product input under `.agents/skills` or `skills-pack/**`; use target project `docs/input/**`.
