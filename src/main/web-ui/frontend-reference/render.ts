import type { Elements } from "./dom.js";
import { replaceChildren } from "./dom.js";
import { visibleRequests } from "./state.js";
import type { AppState, RequestRow } from "./types.js";

function card(label: string, value: string): HTMLElement {
  const element = document.createElement("article");
  element.className = "summary-card";
  const heading = document.createElement("h3");
  heading.textContent = label;
  const body = document.createElement("p");
  body.textContent = value;
  element.append(heading, body);
  return element;
}

function requestCard(request: RequestRow): HTMLElement {
  const element = document.createElement("article");
  element.className = "request-card";

  const main = document.createElement("div");
  const title = document.createElement("strong");
  title.textContent = request.title;
  const meta = document.createElement("p");
  meta.textContent = `${request.id} · ${request.requester}`;
  main.append(title, meta);

  const amount = document.createElement("p");
  amount.textContent = `$${request.amount.toLocaleString()}`;

  const status = document.createElement("span");
  status.className = "badge";
  status.textContent = request.status;

  element.append(main, amount, status);
  return element;
}

function renderStatusOptions(elements: Elements, state: AppState): void {
  if (state.dashboard.status !== "ready") {
    return;
  }
  const current = elements.statusFilter.value || state.selectedStatus;
  const options = ["all", ...state.dashboard.value.allowedStatuses].map((status) => {
    const option = document.createElement("option");
    option.value = status;
    option.textContent = status === "all" ? "All statuses" : status;
    return option;
  });
  elements.statusFilter.replaceChildren(...options);
  elements.statusFilter.value = current;
}

export function render(elements: Elements, state: AppState): void {
  elements.formStatus.classList.toggle("error", state.submit.status === "error");
  elements.formStatus.textContent = state.submit.message ?? "";
  elements.submitButton.disabled = state.submit.status === "submitting";

  if (state.dashboard.status === "loading") {
    elements.status.textContent = "Loading dashboard data…";
    replaceChildren(elements.summary, []);
    elements.requests.textContent = "";
    return;
  }

  if (state.dashboard.status === "error") {
    elements.status.textContent = state.dashboard.message;
    elements.status.classList.add("error");
    elements.requests.textContent = "The request list could not be loaded.";
    return;
  }

  elements.status.classList.remove("error");

  if (state.dashboard.status === "empty") {
    elements.status.textContent = "Dashboard loaded with no requests yet.";
    replaceChildren(elements.summary, [card("Total", "0"), card("Visible", "0"), card("Next step", "Create a request")]);
    elements.requests.textContent = "No requests exist yet. Use the form to create the first one.";
    return;
  }

  if (state.dashboard.status === "ready") {
    const requests = visibleRequests(state);
    elements.status.textContent = `Loaded ${state.dashboard.value.requests.length} requests from ${elements.root.dataset.apiPath}.`;
    replaceChildren(elements.summary, [
      card("Total", String(state.dashboard.value.requests.length)),
      card("Visible", String(requests.length)),
      card("Live updates", state.dashboard.value.streamPath),
    ]);
    renderStatusOptions(elements, state);

    if (requests.length === 0) {
      elements.requests.textContent = "No requests match the selected filter.";
    } else {
      const list = document.createElement("div");
      list.className = "request-list";
      list.append(...requests.map(requestCard));
      elements.requests.replaceChildren(list);
    }
  }
}
