#!/usr/bin/env bash
set -euo pipefail

QUEUE_PATH="${1:-specs/pending-tasks.md}"

fail() {
  printf '[validate-pending-task-workstream-contract][error] %s\n' "$*" >&2
  exit 1
}

[[ -f "$QUEUE_PATH" ]] || fail "Queue file not found: $QUEUE_PATH"

awk '
function add_missing(name) {
  if (missing == "") missing = name; else missing = missing ", " name
}
function validate_task(title, body, status,    text, exempt) {
  if (title == "") return
  if (status == "") status = "pending"
  if (status !~ /^(pending|in-progress)$/) return

  text = tolower(body)
  exempt = (text ~ /(internal-only|foundation-only|cross-cutting|docs-only|non-runtime|skills-pack|no root app runtime feature)/)
  missing = ""

  if (text !~ /(vertical workstream contract|vertical contract:)/) add_missing("vertical contract block/line")

  if (exempt) {
    if (text !~ /(internal-only|foundation-only|cross-cutting|docs-only|non-runtime|skills-pack|no root app runtime feature)/) add_missing("explicit exemption scope")
    if (text !~ /(non-attention|no attention|non-ui|no ui|non-runtime|docs-only|internal-only|foundation-only|cross-cutting|no root app runtime feature|no generated-app runtime feature)/) add_missing("non-attention/non-UI reason")
    if (text !~ /(capability|foundation scope|scope|skills-pack|docs\/templates\/tools)/) add_missing("capability or foundation scope")
    if (text !~ /(audit|trace|work trace|no trace|none|no root app runtime feature|no generated-app runtime feature|skills-pack)/) add_missing("audit/work trace expectation")
    if (text !~ /(local validation|validation|manual smoke|required checks|mvn|npm|non-runtime|docs-only)/) add_missing("local validation path")
  } else {
    if (text !~ /(workstream|functional agent)/) add_missing("workstream / functional agent")
    if (text !~ /(attention|non-attention)/) add_missing("attention category or non-attention reason")
    if (text !~ /(role-specific dashboard|dashboard|surface)/) add_missing("role-specific dashboard / surface")
    if (text !~ /(surface graph|surface action|action edge|node\/action|node\/edge|non-ui trigger)/) add_missing("surface graph node/action edge")
    if (text !~ /(governed-tool|governedtool|browser-tool|agent-tool|internal-tool|workflow-tool|timer-tool|consumer-tool|mcp-tool|api exposure|human_chat_tool_plan)/) add_missing("governed-tool id and exposure")
    if (text !~ /(actor adapter|actor-adapter|trace source|surface_action|human_chat_tool_plan|agent_tool_call|api|workflow|timer|consumer|mcp|internal)/) add_missing("actor adapter/source")
    if (text !~ /(confirmation|confirmedby|approval|decision-card|no approval|none)/) add_missing("confirmation/approval behavior")
    if (text !~ /(idempotency|idempotent|transaction boundary|transaction|partial-failure|result surface)/) add_missing("idempotency/transaction/result behavior")
    if (text !~ /capability/) add_missing("capability id")
    if (text !~ /(authcontext|auth context|tenant scope|tenant\/customer|roles|role\/capability)/) add_missing("AuthContext / roles / tenant scope")
    if (text !~ /(akka substrate|entity|workflow|view|consumer|timed action|agent|autonomousagent|endpoint|frontend|docs-only)/) add_missing("Akka substrate")
    if (text !~ /(api|frontend|realtime|sse|websocket|route|endpoint|surface path|non-ui)/) add_missing("API / frontend / realtime path")
    if (text !~ /(audit|work trace|trace|correlation)/) add_missing("audit/work trace requirements")
    if (text !~ /(local validation|validation|manual smoke|required checks|mvn|npm|browser smoke|api smoke)/) add_missing("local validation path")
  }

  if (missing != "") {
    failures++
    printf "[fail] %s\n  status: %s\n  missing: %s\n", title, status, missing > "/dev/stderr"
  }
}
/^### TASK/ {
  validate_task(title, body, status)
  title = $0
  body = $0 "\n"
  status = ""
  next
}
{
  if (title != "") {
    body = body $0 "\n"
    if ($0 ~ /^- status:[[:space:]]*/) {
      status = tolower($0)
      sub(/^- status:[[:space:]]*/, "", status)
      sub(/[[:space:]]*$/, "", status)
    }
  }
}
END {
  validate_task(title, body, status)
  if (failures > 0) {
    printf "\n%s runnable task(s) are missing required vertical workstream contract fields. Repair the task brief/queue before coding.\n", failures > "/dev/stderr"
    exit 1
  }
}
' "$QUEUE_PATH"

printf '[validate-pending-task-workstream-contract] pass: %s\n' "$QUEUE_PATH"
