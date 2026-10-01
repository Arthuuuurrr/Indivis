#!/usr/bin/env python3
"""Assemble Haute Capitale Dialogue b9 from Clément's untouched b8 + the validated b9 patch jar."""

from __future__ import annotations
import hashlib
import json
import sys
import zipfile
from pathlib import Path

if len(sys.argv) != 4:
    raise SystemExit("usage: assemble_b9.py <b8.jar> <b9-patch.jar> <out-b9.jar>")

b8 = Path(sys.argv[1])
patch = Path(sys.argv[2])
out = Path(sys.argv[3])

EXPECTED_B8_SHA256 = "428c771ef958e56245628bb145a198f04715e5e7a21d5a855ede673b5c7b25c0"
PATCH_ENTRIES = [
    "haute_capitale_dialogue.b9.mixins.json",
    "net/hautecapitale/dialogue/patch/B9ClientPatch.class",
    "net/hautecapitale/dialogue/patch/B9ServerPatch.class",
    "net/hautecapitale/dialogue/patch/mixin/MessageCaptureMixin.class",
]

def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()

if sha256(b8) != EXPECTED_B8_SHA256:
    raise SystemExit(f"Unexpected b8 SHA-256: {sha256(b8)}")

with zipfile.ZipFile(b8, "r") as original, zipfile.ZipFile(patch, "r") as additions, zipfile.ZipFile(
    out, "w", zipfile.ZIP_DEFLATED
) as target:
    for info in original.infolist():
        data = original.read(info.filename)
        if info.filename == "fabric.mod.json":
            metadata = json.loads(data.decode("utf-8"))
            metadata["version"] = "0.1.0+1.21.11.b9"
            metadata.pop("license", None)
            metadata.setdefault("entrypoints", {}).setdefault("main", []).append(
                "net.hautecapitale.dialogue.patch.B9ServerPatch"
            )
            metadata.setdefault("entrypoints", {}).setdefault("client", []).append(
                "net.hautecapitale.dialogue.patch.B9ClientPatch"
            )
            metadata["mixins"] = ["haute_capitale_dialogue.b9.mixins.json"]
            data = (json.dumps(metadata, ensure_ascii=False, indent=2) + "\n").encode("utf-8")
        target.writestr(info, data)

    for name in PATCH_ENTRIES:
        target.writestr(name, additions.read(name))

# Strong preservation check: every original b8 entry except fabric.mod.json must be byte-identical.
with zipfile.ZipFile(b8, "r") as original, zipfile.ZipFile(out, "r") as target:
    original_names = set(original.namelist())
    target_names = set(target.namelist())
    if original_names - target_names:
        raise SystemExit(f"Missing original entries: {sorted(original_names - target_names)}")
    for name in original_names:
        if name.endswith("/") or name == "fabric.mod.json":
            continue
        if hashlib.sha256(original.read(name)).digest() != hashlib.sha256(target.read(name)).digest():
            raise SystemExit(f"Unexpected modification of original b8 entry: {name}")

print(f"{sha256(out)}  {out.name}")
