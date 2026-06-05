export function initialState() {
    return {
        dashboard: { status: "idle" },
        selectedStatus: "all",
        submit: { status: "idle" },
    };
}
export function visibleRequests(state) {
    if (state.dashboard.status !== "ready") {
        return [];
    }
    if (state.selectedStatus === "all") {
        return state.dashboard.value.requests;
    }
    return state.dashboard.value.requests.filter((request) => request.status === state.selectedStatus);
}
export function withSubmittedRequest(state, request) {
    if (state.dashboard.status !== "ready") {
        return state;
    }
    const dashboard = {
        ...state.dashboard.value,
        requests: [request, ...state.dashboard.value.requests],
    };
    return { ...state, dashboard: { status: "ready", value: dashboard } };
}
