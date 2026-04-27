import type { AppState, DashboardResponse, RequestRow } from "./types.js";

export function initialState(): AppState {
  return {
    dashboard: { status: "idle" },
    selectedStatus: "all",
    submit: { status: "idle" },
  };
}

export function visibleRequests(state: AppState): RequestRow[] {
  if (state.dashboard.status !== "ready") {
    return [];
  }
  if (state.selectedStatus === "all") {
    return state.dashboard.value.requests;
  }
  return state.dashboard.value.requests.filter((request) => request.status === state.selectedStatus);
}

export function withSubmittedRequest(state: AppState, request: RequestRow): AppState {
  if (state.dashboard.status !== "ready") {
    return state;
  }
  const dashboard: DashboardResponse = {
    ...state.dashboard.value,
    requests: [request, ...state.dashboard.value.requests],
  };
  return { ...state, dashboard: { status: "ready", value: dashboard } };
}
