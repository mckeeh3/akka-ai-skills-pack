"use strict";
function appendLog(target, message) {
    const item = document.createElement("li");
    item.textContent = message;
    target.appendChild(item);
}
function toSocketUrl(socketPath) {
    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    return `${protocol}//${window.location.host}${socketPath}`;
}
function startSocketDemo() {
    const shell = document.querySelector("[data-socket-path]");
    const status = document.getElementById("websocket-status");
    const log = document.getElementById("websocket-log");
    const sendButton = document.getElementById("send-ping");
    if (!shell || !status || !(log instanceof HTMLUListElement) || !(sendButton instanceof HTMLButtonElement)) {
        return;
    }
    const socketPath = shell.dataset.socketPath;
    if (!socketPath) {
        status.textContent = "Missing WebSocket path configuration.";
        return;
    }
    const socket = new WebSocket(toSocketUrl(socketPath));
    socket.addEventListener("open", () => {
        status.textContent = `Connected to ${socketPath}`;
        socket.send("ping");
    });
    socket.addEventListener("message", event => {
        appendLog(log, `Server replied: ${String(event.data)}`);
    });
    socket.addEventListener("close", () => {
        status.textContent = `Closed: ${socketPath}`;
    });
    sendButton.addEventListener("click", () => {
        if (socket.readyState !== WebSocket.OPEN) {
            appendLog(log, "Socket is not open yet.");
            return;
        }
        appendLog(log, "Client sent: ping");
        socket.send("ping");
    });
}
document.addEventListener("DOMContentLoaded", () => {
    startSocketDemo();
});
