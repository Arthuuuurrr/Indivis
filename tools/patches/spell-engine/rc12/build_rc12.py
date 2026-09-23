#!/usr/bin/env python3
from __future__ import annotations
import argparse, json, shutil, subprocess, tempfile, zipfile
from pathlib import Path

RESTORE = [
    "net/spell_engine/config/HudConfig.class",
    "net/spell_engine/config/HcHudMigration.class",
    "net/spell_engine/internals/impact/SpellImpacts.class",
]

def extract(jar: Path, dst: Path) -> None:
    with zipfile.ZipFile(jar) as z:
        z.extractall(dst)

def copy_entry(jar: Path, entry: str, dst_root: Path) -> None:
    with zipfile.ZipFile(jar) as z:
        data = z.read(entry)
    out = dst_root / entry
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_bytes(data)

def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--rc9", type=Path, required=True)
    ap.add_argument("--rc11", type=Path, required=True)
    ap.add_argument("--out", type=Path, required=True)
    args = ap.parse_args()

    src_java = Path(__file__).with_name("PatchRc12Migration.java")
    with tempfile.TemporaryDirectory() as td:
        root = Path(td) / "root"
        classes = Path(td) / "classes"
        root.mkdir(); classes.mkdir()
        extract(args.rc11, root)

        for entry in RESTORE:
            copy_entry(args.rc9, entry, root)

        subprocess.run([
            "javac",
            "--add-exports", "java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED",
            "-d", str(classes), str(src_java)
        ], check=True)
        subprocess.run([
            "java",
            "--add-exports", "java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED",
            "-cp", str(classes),
            "PatchRc12Migration",
            str(root / "net/spell_engine/config/HcHudMigration.class")
        ], check=True)

        meta = root / "fabric.mod.json"
        data = json.loads(meta.read_text(encoding="utf-8"))
        data["version"] = "1.10.5.001+1.21.11-HC-TEST3-RC12-HUD-IMPACT-RESTORE"
        data["description"] = (
            "HC TEST3 RC12: restores the validated hotbar-aligned spellbar "
            "position and RC9 SpellImpacts bytecode while preserving RC11 "
            "contextual cast/cooldown timing split."
        )
        meta.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

        args.out.parent.mkdir(parents=True, exist_ok=True)
        if args.out.exists():
            args.out.unlink()
        with zipfile.ZipFile(args.out, "w", zipfile.ZIP_DEFLATED) as z:
            for p in sorted(root.rglob("*")):
                if p.is_file():
                    z.write(p, p.relative_to(root).as_posix())
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
