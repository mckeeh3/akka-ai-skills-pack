export function readAndValidate(elements) {
    const title = elements.title.value.trim();
    const requester = elements.requester.value.trim();
    const amount = Number(elements.amount.value);
    const errors = [];
    if (!title) {
        errors.push({ field: "title", message: "Title is required." });
    }
    if (!requester) {
        errors.push({ field: "requester", message: "Requester is required." });
    }
    if (!Number.isFinite(amount) || amount <= 0) {
        errors.push({ field: "amount", message: "Amount must be greater than zero." });
    }
    return errors.length > 0 ? { ok: false, errors } : { ok: true, input: { title, requester, amount } };
}
export function renderFieldErrors(elements, errors) {
    elements.titleError.textContent = "";
    elements.requesterError.textContent = "";
    elements.amountError.textContent = "";
    elements.title.removeAttribute("aria-invalid");
    elements.requester.removeAttribute("aria-invalid");
    elements.amount.removeAttribute("aria-invalid");
    for (const error of errors) {
        const input = elements[error.field];
        const target = elements[`${error.field}Error`];
        input.setAttribute("aria-invalid", "true");
        target.textContent = error.message;
    }
    const first = errors[0];
    if (first) {
        elements[first.field].focus();
    }
}
