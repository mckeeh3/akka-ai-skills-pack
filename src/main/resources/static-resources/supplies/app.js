const apiBase = document.currentScript?.dataset.apiBase || "/api/supplies";

/** @typedef {{ok: true, value: any} | {ok: false, error: {kind: string, message: string}}} ApiResult */

const state = {
  selectedDecisionId: "",
  selectedTraceId: "",
  actionPending: false
};

const elements = {
  status: document.querySelector("#status-region"),
  refresh: document.querySelector("#refresh-button"),
  pending: document.querySelector("#pending-decisions"),
  risks: document.querySelector("#risk-rows"),
  history: document.querySelector("#history-rows"),
  decisionStatus: document.querySelector("#decision-status"),
  decisionDetail: document.querySelector("#decision-detail"),
  actionForm: document.querySelector("#decision-action-form"),
  actor: document.querySelector("#actor-input"),
  actorError: document.querySelector("#actor-error"),
  rationale: document.querySelector("#rationale-input"),
  rationaleError: document.querySelector("#rationale-error"),
  actionMessage: document.querySelector("#action-message"),
  traceLink: document.querySelector("#trace-link"),
  traceEvents: document.querySelector("#trace-events")
};

async function requestJson(path, options = {}) {
  try {
    const response = await fetch(`${apiBase}${path}`, {
      headers: { "Content-Type": "application/json", ...(options.headers || {}) },
      ...options
    });
    const text = await response.text();
    const body = text ? parseJsonOrText(text) : null;
    if (!response.ok) {
      return { ok: false, error: { kind: mapStatus(response.status), message: typeof body === "string" ? body : `Request failed with HTTP ${response.status}` } };
    }
    return { ok: true, value: body };
  } catch (error) {
    return { ok: false, error: { kind: "network", message: "Network error while contacting supplies APIs. Retry when the backend is available." } };
  }
}

function parseJsonOrText(text) {
  try { return JSON.parse(text); } catch (_) { return text; }
}

function mapStatus(status) {
  if (status === 400) return "validation";
  if (status === 401) return "unauthorized";
  if (status === 403) return "forbidden";
  if (status === 404) return "notFound";
  return "server";
}

async function loadCommandCenter() {
  setStatus("loading", "Loading risk rows, pending decisions, and recent shipment states…");
  renderLoading(elements.pending, "Loading pending decision cards…");
  renderLoading(elements.risks, "Loading supply risk rows…");
  renderLoading(elements.history, "Loading recent auto and suppressed shipments…");

  const [pending, risks, shipped, suppressed] = await Promise.all([
    requestJson("/decisions/pending"),
    requestJson("/risks?status=WAITING_FOR_APPROVAL"),
    requestJson("/risks?status=SHIPMENT_PREPARED"),
    requestJson("/risks?status=SUPPRESSED")
  ]);

  if (!pending.ok || !risks.ok) {
    const failure = !pending.ok ? pending.error : risks.error;
    setStatus("error", `${failure.message} Decision and risk queues may be stale.`);
  } else {
    setStatus("ready", "Queues loaded. Review evidence and policy context before acting on approval-required cards.");
  }

  renderPending(pending);
  renderRisks(risks);
  renderHistory(shipped, suppressed);
}

function renderLoading(container, message) {
  container.replaceChildren(node("p", { className: "empty-state" }, message));
}

function renderPending(result) {
  if (!result.ok) return renderError(elements.pending, result.error.message);
  const decisions = result.value?.decisions || [];
  if (decisions.length === 0) return renderEmpty(elements.pending);
  elements.pending.replaceChildren(...decisions.map(renderPendingRow));
}

function renderPendingRow(row) {
  const button = node("button", { className: "button secondary", type: "button" }, "Review decision card");
  button.addEventListener("click", () => loadDecision(row.decisionId));
  return node("article", { className: "queue-row" },
    node("div", {}, node("strong", {}, row.decisionId), " ", statusBadge(row.status)),
    node("p", {}, row.riskSummary),
    metaGrid([
      ["Proposed action", row.proposedAction],
      ["Confidence", `${percent(row.confidence)} confidence`],
      ["Estimated cost", money(row.estimatedCostCents)],
      ["Evidence", `${row.evidenceCount} evidence items`],
      ["Policy clauses", `${row.policyClauseCount} triggered`],
      ["Trace", row.traceId],
      ["Outcome", row.outcomeId]
    ]),
    button
  );
}

function renderRisks(result) {
  if (!result.ok) return renderError(elements.risks, result.error.message);
  const risks = result.value?.risks || [];
  if (risks.length === 0) return renderEmpty(elements.risks);
  elements.risks.replaceChildren(...risks.map(row => node("article", { className: "queue-row" },
    node("div", {}, node("strong", {}, `${row.customerId} · ${row.deviceId}`), " ", statusBadge(row.status)),
    metaGrid([
      ["Toner", `${row.tonerPercent}%`],
      ["Risk", percent(row.depletionRisk)],
      ["Action", row.proposedAction],
      ["Timer", row.staleDecisionTimerRequested ? "stale-decision timer requested" : "no stale timer"],
      ["Trace", row.traceId],
      ["Outcome", row.outcomeId]
    ])
  )));
}

function renderHistory(shipped, suppressed) {
  const rows = [];
  if (shipped.ok) rows.push(...(shipped.value?.risks || []));
  if (suppressed.ok) rows.push(...(suppressed.value?.risks || []));
  if (rows.length === 0) return renderEmpty(elements.history);
  elements.history.replaceChildren(...rows.map(row => node("article", { className: "meta-item" },
    node("span", { className: "meta-label" }, row.status),
    node("span", { className: "meta-value" }, `${row.customerId} / ${row.deviceId}`),
    node("p", {}, `${row.proposedAction} · trace ${row.traceId} · outcome ${row.outcomeId}`)
  )));
}

async function loadDecision(decisionId) {
  state.selectedDecisionId = decisionId;
  elements.decisionStatus.replaceWith(statusBadge("LOADING", "decision-status"));
  elements.decisionStatus = document.querySelector("#decision-status");
  elements.decisionDetail.replaceChildren(node("p", { className: "empty-state" }, "Loading decision card with evidence, policy triggers, trace events, and outcome context…"));
  disableActionButtons(true);

  const result = await requestJson(`/decisions/${encodeURIComponent(decisionId)}`);
  if (!result.ok) {
    elements.decisionDetail.replaceChildren(node("p", { className: "empty-state" }, result.error.message));
    setActionMessage("", false);
    return;
  }
  renderDecisionDetail(result.value);
}

function renderDecisionDetail(detail) {
  state.selectedTraceId = detail.traceId;
  elements.decisionStatus.replaceWith(statusBadge(detail.status, "decision-status"));
  elements.decisionStatus = document.querySelector("#decision-status");
  elements.traceLink.textContent = `Trace ${detail.traceId}`;
  elements.traceLink.href = `${apiBase}/traces/${encodeURIComponent(detail.traceId)}`;
  elements.traceLink.removeAttribute("aria-disabled");

  elements.decisionDetail.replaceChildren(
    metaGrid([
      ["Objective", detail.objectiveId],
      ["Recommended SKU", detail.recommendedSku],
      ["Action", detail.proposedAction],
      ["Cost", money(detail.estimatedCostCents)],
      ["Risk", percent(detail.depletionRisk)],
      ["Confidence", percent(detail.confidence)],
      ["Trace", detail.traceId],
      ["Outcome", detail.outcomeId]
    ]),
    section("Risk and impact", [detail.riskSummary, detail.impactSummary]),
    listSection("Evidence", detail.evidence.map(e => `${e.type} from ${e.source}: ${e.summary} (${percent(e.confidence)} confidence, trace ${e.traceId})`), "evidence-list"),
    listSection("Policy triggers", detail.policyClauses.map(p => `${p.clauseId}: ${p.summary}`), "policy-list"),
    listSection("Alternatives", detail.alternatives, "alternatives-list")
  );
  renderTraceEvents(detail.traceEvents || []);
  disableActionButtons(detail.status !== "APPROVAL_REQUIRED");
  setActionMessage(detail.status === "APPROVAL_REQUIRED" ? "Evidence, risk/confidence, policy, alternatives, trace, and outcome context are visible. Choose an action with rationale." : "Decision is no longer waiting for approval.", detail.status !== "APPROVAL_REQUIRED");
  elements.rationale.focus();
}

function renderTraceEvents(events) {
  if (!events.length) {
    elements.traceEvents.replaceChildren(node("li", {}, "No trace events returned for this decision card."));
    return;
  }
  elements.traceEvents.replaceChildren(...events.map(event => node("li", {},
    node("strong", {}, `${event.type} · ${event.actor || "system"}`),
    node("span", {}, `${event.summary} · policies ${event.policyClauseCount} · outcome ${event.outcomeId || "pending"}`)
  )));
}

async function submitAction(event) {
  event.preventDefault();
  const submitter = event.submitter;
  if (!submitter || !state.selectedDecisionId || state.actionPending) return;
  clearValidation();
  const actor = elements.actor.value.trim();
  const rationale = elements.rationale.value.trim();
  let valid = true;
  if (!actor) { elements.actorError.textContent = "Reviewer is required."; valid = false; }
  if (rationale.length < 8) { elements.rationaleError.textContent = "Rationale must explain the evidence, policy, risk, or trace context."; valid = false; }
  if (!valid) {
    (actor ? elements.rationale : elements.actor).focus();
    return;
  }

  state.actionPending = true;
  disableActionButtons(true);
  setActionMessage(`Submitting ${submitter.value} with reviewer rationale…`, false);
  const result = await requestJson(`/decisions/${encodeURIComponent(state.selectedDecisionId)}/${submitter.value}`, {
    method: "POST",
    body: JSON.stringify({ idempotencyKey: `${submitter.value}-${state.selectedDecisionId}-${Date.now()}`, actor, rationale })
  });
  state.actionPending = false;
  if (!result.ok) {
    setActionMessage(result.error.message, false);
    disableActionButtons(false);
    return;
  }
  setActionMessage(`${submitter.textContent} recorded. Workflow status is ${result.value.status}; trace ${result.value.traceId}; outcome ${result.value.outcomeId}.`, true);
  elements.rationale.value = "";
  await Promise.all([loadCommandCenter(), loadDecision(state.selectedDecisionId)]);
}

function clearValidation() {
  elements.actorError.textContent = "";
  elements.rationaleError.textContent = "";
}

function disableActionButtons(disabled) {
  elements.actionForm.querySelectorAll("button[name='action']").forEach(button => { button.disabled = disabled; });
}

function setActionMessage(message, success) {
  elements.actionMessage.textContent = message;
  elements.actionMessage.classList.toggle("success", success);
}

function setStatus(kind, message) {
  elements.status.textContent = message;
  elements.status.dataset.state = kind;
}

function renderEmpty(container) {
  container.replaceChildren(node("p", { className: "empty-state" }, container.dataset.empty || "No data available."));
}

function renderError(container, message) {
  container.replaceChildren(node("p", { className: "empty-state" }, message));
}

function section(title, paragraphs) {
  return node("section", {}, node("h3", {}, title), ...paragraphs.map(text => node("p", {}, text)));
}

function listSection(title, items, className) {
  return node("section", {}, node("h3", {}, title), node("ul", { className }, ...items.map(item => node("li", {}, item))));
}

function metaGrid(entries) {
  return node("dl", { className: "meta-grid" }, ...entries.flatMap(([label, value]) => [
    node("div", { className: "meta-item" }, node("dt", { className: "meta-label" }, label), node("dd", { className: "meta-value" }, value || "—"))
  ]));
}

function statusBadge(status, id) {
  const className = status.includes("APPROVAL") || status.includes("WAITING") || status === "LOADING" ? "badge warning" : status.includes("REJECT") || status.includes("SUPPRESS") ? "badge danger" : status.includes("SHIPMENT") ? "badge success" : "badge neutral";
  return node("span", { className, id }, status.replaceAll("_", " "));
}

function percent(value) {
  return `${Math.round(Number(value) * 100)}%`;
}

function money(cents) {
  return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(Number(cents) / 100);
}

function node(tag, props = {}, ...children) {
  const element = document.createElement(tag);
  Object.entries(props).forEach(([key, value]) => {
    if (key === "className") element.className = value;
    else if (key === "dataset") Object.assign(element.dataset, value);
    else if (value !== undefined && value !== null) element.setAttribute(key, value);
  });
  children.flat().forEach(child => element.append(child instanceof Node ? child : document.createTextNode(String(child))));
  return element;
}

elements.refresh.addEventListener("click", loadCommandCenter);
elements.actionForm.addEventListener("submit", submitAction);
window.addEventListener("DOMContentLoaded", loadCommandCenter);
