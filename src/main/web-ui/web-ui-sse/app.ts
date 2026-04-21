type CounterEvent = {
  value: number;
  label: string;
};

function appendEvent(target: HTMLOListElement, message: string): void {
  const item = document.createElement("li");
  item.textContent = message;
  target.appendChild(item);
}

function startStream(): void {
  const shell = document.querySelector<HTMLElement>("[data-stream-path]");
  const status = document.getElementById("sse-status");
  const events = document.getElementById("sse-events");

  if (!shell || !status || !(events instanceof HTMLOListElement)) {
    return;
  }

  const streamPath = shell.dataset.streamPath;
  if (!streamPath) {
    status.textContent = "Missing SSE path configuration.";
    return;
  }

  const source = new EventSource(streamPath);

  source.addEventListener("counter", event => {
    const payload = JSON.parse((event as MessageEvent<string>).data) as CounterEvent;
    status.textContent = `Connected to ${streamPath}`;
    appendEvent(events, `${payload.label}: ${payload.value}`);
  });

  source.onerror = () => {
    status.textContent = `Reconnecting to ${streamPath}…`;
  };
}

document.addEventListener("DOMContentLoaded", () => {
  startStream();
});
