export interface Elements {
  root: HTMLElement;
  status: HTMLElement;
  summary: HTMLElement;
  requests: HTMLElement;
  statusFilter: HTMLSelectElement;
  form: HTMLFormElement;
  formStatus: HTMLElement;
  submitButton: HTMLButtonElement;
  title: HTMLInputElement;
  requester: HTMLInputElement;
  amount: HTMLInputElement;
  titleError: HTMLElement;
  requesterError: HTMLElement;
  amountError: HTMLElement;
}

function requireElement<T extends Element>(selector: string, expected: new () => T): T {
  const element = document.querySelector(selector);
  if (!(element instanceof expected)) {
    throw new Error(`Missing required frontend reference element: ${selector}`);
  }
  return element;
}

export function getElements(): Elements {
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

export function replaceChildren(parent: HTMLElement, children: Node[]): void {
  parent.replaceChildren(...children);
}
