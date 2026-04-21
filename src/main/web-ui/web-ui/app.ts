type WebUiSummaryResponse = {
  title: string;
  message: string;
  capabilities: string[];
  apiPath: string;
};

async function loadSummary(): Promise<void> {
  const shell = document.querySelector<HTMLElement>("[data-api-path]");
  const title = document.getElementById("status-title");
  const message = document.getElementById("status-message");
  const capabilities = document.getElementById("capabilities");

  if (!shell || !title || !message || !capabilities) {
    return;
  }

  const apiPath = shell.dataset.apiPath;
  if (!apiPath) {
    message.textContent = "Missing API path configuration.";
    return;
  }

  try {
    const response = await fetch(apiPath, {
      headers: {
        Accept: "application/json"
      }
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    const payload = (await response.json()) as WebUiSummaryResponse;
    title.textContent = payload.title;
    message.textContent = payload.message;
    capabilities.replaceChildren(
      ...payload.capabilities.map(capability => {
        const item = document.createElement("li");
        item.textContent = capability;
        return item;
      })
    );
  } catch (error) {
    const detail = error instanceof Error ? error.message : "Unknown error";
    title.textContent = "Web UI unavailable";
    message.textContent = `Could not load ${apiPath}: ${detail}`;
    capabilities.replaceChildren();
  }
}

document.addEventListener("DOMContentLoaded", () => {
  void loadSummary();
});
