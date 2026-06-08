#!/usr/bin/env python3
"""Validate the machine-readable workstream manifest in an app-description tree."""
from __future__ import annotations

import json
import re
import sys
from pathlib import Path

ALLOWED_READINESS = {
    "identified",
    "described",
    "surface-ready",
    "capability-ready",
    "expertise-ready",
    "runtime-ready",
    "production-ready",
}
ALLOWED_CLASSIFICATION = {"foundation", "domain-specific"}
MAPPING_REQUIRED_READINESS = {"capability-ready", "expertise-ready", "runtime-ready", "production-ready"}
EVIDENCE_REQUIRED_READINESS = {"runtime-ready", "production-ready"}
ALLOWED_EXPOSURE_CHANNELS = {
    "browser-tool",
    "agent-tool",
    "workflow-tool",
    "timer-tool",
    "consumer-tool",
    "MCP-tool",
    "internal-tool",
    "api",
    "surface-request",
}


def fail(errors: list[str], message: str) -> None:
    errors.append(message)


def load_json(path: Path, errors: list[str]) -> dict:
    try:
        return json.loads(path.read_text())
    except Exception as exc:  # noqa: BLE001
        fail(errors, f"invalid JSON: {path}: {exc}")
        return {}


def mentions(path: Path, token: str) -> bool:
    return path.exists() and token in path.read_text(errors="ignore")


def main(argv: list[str]) -> int:
    root = Path(argv[1] if len(argv) > 1 else "app-description")
    manifest = root / "12-workstreams" / "workstream-manifest.json"
    errors: list[str] = []

    if not manifest.exists():
        fail(errors, f"missing manifest: {manifest}")
    data = load_json(manifest, errors) if manifest.exists() else {}

    workstreams = data.get("workstreams")
    if not isinstance(workstreams, list) or not workstreams:
        fail(errors, "manifest must contain non-empty workstreams[]")
        workstreams = []

    seen_workstreams: set[str] = set()
    seen_agents: set[str] = set()
    surface_ids: set[str] = set()
    functional_agents = root / "12-workstreams" / "functional-agents.md"
    surfaces_index = root / "12-workstreams" / "surfaces-index.md"
    attention_dashboards = root / "12-workstreams" / "attention-and-dashboards.md"
    internal_agents = root / "12-workstreams" / "internal-agents.md"
    surface_trace = root / "70-traceability" / "surface-to-capability-map.md"
    agent_trace = root / "70-traceability" / "functional-agent-to-capability-map.md"

    for i, ws in enumerate(workstreams):
        prefix = f"workstreams[{i}]"
        if not isinstance(ws, dict):
            fail(errors, f"{prefix} must be an object")
            continue

        required = [
            "workstreamId",
            "displayName",
            "classification",
            "functionalAgentId",
            "managedAgentDefinitionId",
            "defaultSurfaceId",
            "readiness",
            "instanceScope",
            "authorizedActors",
            "icon",
            "attentionCategories",
            "surfaces",
            "capabilities",
            "traceability",
            "localValidation",
        ]
        for key in required:
            if key not in ws:
                fail(errors, f"{prefix} missing required field: {key}")

        wid = str(ws.get("workstreamId", ""))
        agent = str(ws.get("functionalAgentId", ""))
        managed_agent = str(ws.get("managedAgentDefinitionId", ""))
        default_surface = str(ws.get("defaultSurfaceId", ""))
        readiness = str(ws.get("readiness", ""))
        classification = str(ws.get("classification", ""))

        if not re.fullmatch(r"[-a-z0-9]+", wid):
            fail(errors, f"{prefix}.workstreamId must be kebab-case lowercase: {wid!r}")
        if wid in seen_workstreams:
            fail(errors, f"duplicate workstreamId: {wid}")
        seen_workstreams.add(wid)

        if not re.fullmatch(r"[-a-z0-9]+-agent", agent):
            fail(errors, f"{prefix}.functionalAgentId should be kebab-case and end with -agent: {agent!r}")
        if not managed_agent:
            fail(errors, f"{prefix}.managedAgentDefinitionId must be explicit; it may match functionalAgentId until separately named")
        if agent in seen_agents:
            fail(errors, f"functionalAgentId must own exactly one workstream in this manifest; duplicate: {agent}")
        seen_agents.add(agent)

        if readiness not in ALLOWED_READINESS:
            fail(errors, f"{prefix}.readiness invalid: {readiness!r}")
        if classification not in ALLOWED_CLASSIFICATION:
            fail(errors, f"{prefix}.classification invalid: {classification!r}")

        icon = ws.get("icon")
        if not isinstance(icon, dict) or not all(icon.get(k) for k in ["iconId", "visualHint", "accentColorToken", "tooltip", "ariaLabel"]):
            fail(errors, f"{prefix}.icon must include iconId, visualHint, accentColorToken, tooltip, ariaLabel")

        actors = ws.get("authorizedActors")
        if not isinstance(actors, list) or not actors:
            fail(errors, f"{prefix}.authorizedActors must be non-empty")

        attention = ws.get("attentionCategories")
        if attention != [] and (not isinstance(attention, list) or not all(isinstance(x, str) and x for x in attention)):
            fail(errors, f"{prefix}.attentionCategories must be [] or a list of strings")
        if attention == []:
            no_attention_evidence = (
                mentions(attention_dashboards, f"`{wid}`")
                and (
                    mentions(attention_dashboards, "no actionable attention model")
                    or mentions(attention_dashboards, "does not produce attention")
                    or mentions(attention_dashboards, "does not produce actionable attention")
                    or mentions(attention_dashboards, "intentionally does not produce attention")
                )
            )
            if not no_attention_evidence:
                fail(errors, f"{prefix}.attentionCategories is [] but attention-and-dashboards.md does not explain the non-attention model for {wid}")

        surfaces = ws.get("surfaces")
        if not isinstance(surfaces, list) or default_surface not in surfaces:
            fail(errors, f"{prefix}.surfaces must include defaultSurfaceId {default_surface!r}")
        elif not all(isinstance(s, str) and s for s in surfaces):
            fail(errors, f"{prefix}.surfaces must be a list of non-empty strings")
        else:
            surface_ids.update(surfaces)

        capabilities = ws.get("capabilities")
        if not isinstance(capabilities, list) or not capabilities or not all(isinstance(c, str) and c for c in capabilities):
            fail(errors, f"{prefix}.capabilities must be a non-empty list of strings")
        surface_refs = surfaces if isinstance(surfaces, list) else []
        capability_refs = capabilities if isinstance(capabilities, list) else []

        mappings = ws.get("surfaceActionMappings")
        if readiness in MAPPING_REQUIRED_READINESS and (not isinstance(mappings, list) or not mappings):
            fail(errors, f"{prefix}.surfaceActionMappings must be non-empty at readiness {readiness}")
        if mappings is not None:
            if not isinstance(mappings, list):
                fail(errors, f"{prefix}.surfaceActionMappings must be a list when present")
            else:
                for j, mapping in enumerate(mappings):
                    mapping_prefix = f"{prefix}.surfaceActionMappings[{j}]"
                    if not isinstance(mapping, dict):
                        fail(errors, f"{mapping_prefix} must be an object")
                        continue
                    required_mapping = [
                        "surfaceId",
                        "actionId",
                        "capabilityId",
                        "governedToolId",
                        "exposureChannel",
                        "authBasis",
                        "idempotency",
                        "resultSurfaceId",
                        "traceRequired",
                    ]
                    for key in required_mapping:
                        if key not in mapping:
                            fail(errors, f"{mapping_prefix} missing required field: {key}")
                    surface_id = mapping.get("surfaceId")
                    result_surface_id = mapping.get("resultSurfaceId")
                    capability_id = mapping.get("capabilityId")
                    channel = mapping.get("exposureChannel")
                    if surface_id not in surface_refs:
                        fail(errors, f"{mapping_prefix}.surfaceId must reference this workstream's surfaces: {surface_id!r}")
                    if result_surface_id not in surface_refs:
                        fail(errors, f"{mapping_prefix}.resultSurfaceId must reference this workstream's surfaces: {result_surface_id!r}")
                    if capability_id not in capability_refs:
                        fail(errors, f"{mapping_prefix}.capabilityId must reference this workstream's capabilities: {capability_id!r}")
                    if channel not in ALLOWED_EXPOSURE_CHANNELS:
                        fail(errors, f"{mapping_prefix}.exposureChannel invalid: {channel!r}")
                    for key in ["actionId", "governedToolId", "authBasis", "idempotency"]:
                        if not isinstance(mapping.get(key), str) or not mapping.get(key):
                            fail(errors, f"{mapping_prefix}.{key} must be a non-empty string")
                    if not isinstance(mapping.get("traceRequired"), bool):
                        fail(errors, f"{mapping_prefix}.traceRequired must be true or false")

        evidence = ws.get("readinessEvidence")
        if readiness in EVIDENCE_REQUIRED_READINESS and not isinstance(evidence, dict):
            fail(errors, f"{prefix}.readinessEvidence must be present at readiness {readiness}")
        if evidence is not None:
            if not isinstance(evidence, dict):
                fail(errors, f"{prefix}.readinessEvidence must be an object when present")
            else:
                commands = evidence.get("localCommands")
                if not isinstance(commands, list) or not commands or not all(isinstance(c, str) and c for c in commands):
                    fail(errors, f"{prefix}.readinessEvidence.localCommands must be a non-empty list of strings")
                for key in ["apiUiSmokePath", "providerSecurityFailClosedCheck", "traceEvidence"]:
                    if not isinstance(evidence.get(key), str) or not evidence.get(key):
                        fail(errors, f"{prefix}.readinessEvidence.{key} must be a non-empty string")

        internal_workers = ws.get("internalWorkers")
        if internal_workers is not None:
            if not isinstance(internal_workers, list):
                fail(errors, f"{prefix}.internalWorkers must be a list when present")
            else:
                for j, worker in enumerate(internal_workers):
                    worker_prefix = f"{prefix}.internalWorkers[{j}]"
                    if not isinstance(worker, dict):
                        fail(errors, f"{worker_prefix} must be a structured object, not a string id")
                        continue
                    required_worker = [
                        "workerId",
                        "substrate",
                        "trigger",
                        "authorityBasis",
                        "capabilityId",
                        "governedToolId",
                        "progressSurfaceId",
                        "resultSurfaceId",
                        "failureSurfaceId",
                    ]
                    for key in required_worker:
                        if key not in worker:
                            fail(errors, f"{worker_prefix} missing required field: {key}")
                    if worker.get("capabilityId") not in capability_refs:
                        fail(errors, f"{worker_prefix}.capabilityId must reference this workstream's capabilities: {worker.get('capabilityId')!r}")
                    for key in ["progressSurfaceId", "resultSurfaceId", "failureSurfaceId"]:
                        if worker.get(key) not in surface_refs:
                            fail(errors, f"{worker_prefix}.{key} must reference this workstream's surfaces: {worker.get(key)!r}")
                    for key in ["workerId", "substrate", "trigger", "authorityBasis", "governedToolId"]:
                        if not isinstance(worker.get(key), str) or not worker.get(key):
                            fail(errors, f"{worker_prefix}.{key} must be a non-empty string")

        expertise = ws.get("expertiseBundle")
        if expertise:
            path = root / "12-workstreams" / "workstream-expertise" / str(expertise)
            if not path.exists():
                fail(errors, f"{prefix}.expertiseBundle file missing: {path}")

        if not mentions(functional_agents, f"`{agent}`"):
            fail(errors, f"{prefix}.functionalAgentId not listed in functional-agents.md: {agent}")
        if not mentions(functional_agents, f"`{wid}`"):
            fail(errors, f"{prefix}.workstreamId not listed in functional-agents.md: {wid}")
        if not mentions(agent_trace, f"`{agent}`"):
            fail(errors, f"{prefix}.functionalAgentId not mapped in functional-agent-to-capability-map.md: {agent}")
        if not mentions(agent_trace, f"`{wid}`"):
            fail(errors, f"{prefix}.workstreamId not mapped in functional-agent-to-capability-map.md: {wid}")
        if not mentions(attention_dashboards, wid):
            fail(errors, f"{prefix}.workstreamId not mentioned in attention-and-dashboards.md: {wid}")
        if attention and not mentions(attention_dashboards, "producerId"):
            fail(errors, "attention-and-dashboards.md must include producerId rows when manifest defines attentionCategories")
        if not mentions(internal_agents, wid):
            fail(errors, f"{prefix}.workstreamId not mentioned in internal-agents.md: {wid}")

    for sid in sorted(surface_ids):
        if sid in {"markdown_response", "system_message"}:
            continue
        if not mentions(surfaces_index, f"`{sid}`"):
            fail(errors, f"surface not listed in surfaces-index.md: {sid}")
        if not mentions(surface_trace, f"`{sid}`"):
            fail(errors, f"surface not mapped in surface-to-capability-map.md: {sid}")

    if errors:
        for error in errors:
            print(f"[validate-workstream-manifest][error] {error}", file=sys.stderr)
        print(f"[validate-workstream-manifest][error] {len(errors)} failure(s)", file=sys.stderr)
        return 1

    print(f"[validate-workstream-manifest] validation passed: {root}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
