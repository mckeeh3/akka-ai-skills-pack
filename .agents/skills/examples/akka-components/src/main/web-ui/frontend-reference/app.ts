import { getDashboard, submitRequest } from "./api.js";
import { getElements } from "./dom.js";
import { readAndValidate, renderFieldErrors } from "./forms.js";
import { render } from "./render.js";
import { initialState, withSubmittedRequest } from "./state.js";
import type { AppState } from "./types.js";

window.addEventListener("DOMContentLoaded", () => {
  const elements = getElements();
  let state: AppState = { ...initialState(), dashboard: { status: "loading" } };

  const rerender = (): void => render(elements, state);

  elements.statusFilter.addEventListener("change", () => {
    state = { ...state, selectedStatus: elements.statusFilter.value };
    rerender();
  });

  elements.form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const validation = readAndValidate(elements);
    if (!validation.ok) {
      renderFieldErrors(elements, validation.errors);
      state = { ...state, submit: { status: "error", message: "Fix the highlighted fields." } };
      rerender();
      return;
    }

    renderFieldErrors(elements, []);
    state = { ...state, submit: { status: "submitting", message: "Submitting request…" } };
    rerender();

    const submitPath = elements.root.dataset.submitPath ?? "/api/frontend-reference/requests";
    const result = await submitRequest(submitPath, validation.input);
    if (result.ok) {
      elements.form.reset();
      state = withSubmittedRequest(
        { ...state, submit: { status: "success", message: result.value.message } },
        result.value.request,
      );
    } else {
      state = { ...state, submit: { status: "error", message: result.error.message } };
    }
    rerender();
  });

  rerender();

  void (async () => {
    const apiPath = elements.root.dataset.apiPath ?? "/api/frontend-reference/dashboard";
    const result = await getDashboard(apiPath);
    state = result.ok
      ? {
          ...state,
          dashboard:
            result.value.requests.length === 0
              ? { status: "empty" }
              : { status: "ready", value: result.value },
        }
      : { ...state, dashboard: { status: "error", message: result.error.message } };
    rerender();
  })();
});
