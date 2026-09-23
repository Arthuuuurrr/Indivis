#!/usr/bin/env python3
from __future__ import annotations
import argparse, hashlib, json, zipfile
from pathlib import Path

ICON_PATH = "assets/more_rpg_classes/textures/spell/decapitate.png"
EXPECTED_ICON_SHA256 = "7f972f91fe7de5960550aea89c4cd87c01636c57374f7905f464c1cd8ce1dbf7"

def main() -> int:
    ap=argparse.ArgumentParser()
    ap.add_argument("--rc12", type=Path, required=True)
    ap.add_argument("--icon", type=Path, required=True)
    ap.add_argument("--out", type=Path, required=True)
    args=ap.parse_args()

    icon=args.icon.read_bytes()
    got=hashlib.sha256(icon).hexdigest()
    if got != EXPECTED_ICON_SHA256:
        raise SystemExit(f"unexpected decapitate icon SHA-256: {got}")

    with zipfile.ZipFile(args.rc12) as zin:
        if ICON_PATH in zin.namelist():
            raise SystemExit("RC12 unexpectedly already contains Decapitate fallback")
        args.out.parent.mkdir(parents=True,exist_ok=True)
        with zipfile.ZipFile(args.out,"w",zipfile.ZIP_DEFLATED) as zout:
            for info in zin.infolist():
                data=zin.read(info.filename)
                if info.filename=="fabric.mod.json":
                    meta=json.loads(data)
                    meta["version"]="1.10.5.1+1.21.11-HC-TEST3-RC13-AXE-ICON-FALLBACK"
                    meta["description"]="HC TEST3 RC13: RC12 runtime fixes plus exact Decapitate icon fallback for Berserker axe right-click weapon skill."
                    data=(json.dumps(meta,ensure_ascii=False,indent=2)+"\n").encode()
                zout.writestr(info,data)
            zout.writestr(ICON_PATH,icon)
    return 0

if __name__=="__main__":
    raise SystemExit(main())
