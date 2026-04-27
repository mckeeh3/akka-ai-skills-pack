async function parseJson(response) {
    try {
        const value = (await response.json());
        return { ok: true, value };
    }
    catch {
        return { ok: false, error: { kind: "malformedResponse", message: "The server response was not valid JSON." } };
    }
}
async function normalizeError(response) {
    const fallback = response.status >= 500 ? "The server failed while processing the request." : "The request was rejected.";
    const message = await response.text().catch(() => fallback);
    return {
        ok: false,
        error: {
            kind: response.status === 400 ? "validation" : "server",
            message: message || fallback,
        },
    };
}
export async function getDashboard(apiPath) {
    try {
        const response = await fetch(apiPath, { headers: { Accept: "application/json" } });
        if (!response.ok) {
            return normalizeError(response);
        }
        return parseJson(response);
    }
    catch {
        return { ok: false, error: { kind: "network", message: "Could not reach the dashboard API." } };
    }
}
export async function submitRequest(apiPath, input) {
    try {
        const response = await fetch(apiPath, {
            method: "POST",
            headers: { Accept: "application/json", "Content-Type": "application/json" },
            body: JSON.stringify(input),
        });
        if (!response.ok) {
            return normalizeError(response);
        }
        return parseJson(response);
    }
    catch {
        return { ok: false, error: { kind: "network", message: "Could not submit the request." } };
    }
}
