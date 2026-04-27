function requireElement(selector, expected) {
    const element = document.querySelector(selector);
    if (!(element instanceof expected)) {
        throw new Error(`Missing required frontend reference element: ${selector}`);
    }
    return element;
}
export function getElements() {
    return {
        root: requireElement("[data-api-path]", HTMLElement),
        status: requireElement("#connection-status", HTMLElement),
        summary: requireElement("#summary-region", HTMLElement),
        requests: requireElement("#requests-region", HTMLElement),
        statusFilter: requireElement("#status-filter", HTMLSelectElement),
        form: requireElement("#request-form", HTMLFormElement),
        formStatus: requireElement("#form-status", HTMLElement),
        submitButton: requireElement("#submit-button", HTMLButtonElement),
        title: requireElement("#title", HTMLInputElement),
        requester: requireElement("#requester", HTMLInputElement),
        amount: requireElement("#amount", HTMLInputElement),
        titleError: requireElement("#title-error", HTMLElement),
        requesterError: requireElement("#requester-error", HTMLElement),
        amountError: requireElement("#amount-error", HTMLElement),
    };
}
export function replaceChildren(parent, children) {
    parent.replaceChildren(...children);
}
