#!/usr/bin/env python3
from __future__ import annotations
import argparse, hashlib, json, subprocess, tempfile, zipfile
from pathlib import Path

EXPECTED = {
    "hud": "c29d2c029d31152f5f697e6ba347619f58377ff8f9c6d7b1cb8af456bfebb367",
    "nexus": "8d918571c3f7ff518c94c0793fb1080a0f08cc2d9e697d6a0584d09a551efa1e",
}
EXPECTED_OUTPUTS = {
    "hud": "f12179dd3f10d6870d28ad864e01f0e16bd5c9ef5156a346c77f3e10809de145",
    "nexus": "ab6cbe03ce2ce29378c6b43759546790c6d0128fecc650113b94c283098a5ad1",
}

def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()

def json_bytes(value) -> bytes:
    return (json.dumps(value, ensure_ascii=False, indent=2) + "\n").encode("utf-8")

def helper_info(name: str) -> zipfile.ZipInfo:
    info = zipfile.ZipInfo(name, (2026, 9, 23, 17, 45, 0))
    info.compress_type = zipfile.ZIP_DEFLATED
    info.create_system = 3
    info.external_attr = 0o644 << 16
    return info

def compile_sources(helper_source: Path, patch_source: Path, work: Path) -> tuple[Path, Path]:
    helper_classes = work / "helper-classes"
    patch_classes = work / "patch-classes"
    helper_classes.mkdir()
    patch_classes.mkdir()
    subprocess.run([
        "javac", "--release", "21", "-d", str(helper_classes), str(helper_source)
    ], check=True)
    subprocess.run([
        "javac",
        "--add-exports", "java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED",
        "--add-exports", "java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED",
        "-d", str(patch_classes), str(patch_source)
    ], check=True)
    return (
        helper_classes / "fr/arthur/capitale/rphud/RaceHealthModifierCompat.class",
        patch_classes,
    )

def patch_classes(hud: Path, nexus: Path, patch_classes: Path, work: Path) -> dict[str, Path]:
    hud_entries = {
        "strict": "fr/arthur/capitale/rphud/CapitaleStrictRaceHealthFinalizer.class",
        "rebuild": "fr/arthur/capitale/rphud/CapitaleRebuildMorphServer.class",
        "autosync": "fr/arthur/capitale/rphud/CapitaleExactPersonnagesAutoSync.class",
    }
    with zipfile.ZipFile(hud) as zin:
        for key, name in hud_entries.items():
            (work / f"{key}.class").write_bytes(zin.read(name))
    nexus_name = "net/tompsen/nexuscharacters/HauteCapitaleIdentity.class"
    with zipfile.ZipFile(nexus) as zin:
        (work / "nexus.class").write_bytes(zin.read(nexus_name))

    outputs = {
        "strict": work / "strict.patched.class",
        "rebuild": work / "rebuild.patched.class",
        "autosync": work / "autosync.patched.class",
        "nexus": work / "nexus.patched.class",
    }
    subprocess.run([
        "java",
        "--add-exports", "java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED",
        "--add-exports", "java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED",
        "-cp", str(patch_classes), "PatchHealthCompat",
        str(work / "strict.class"), str(outputs["strict"]),
        str(work / "rebuild.class"), str(outputs["rebuild"]),
        str(work / "autosync.class"), str(outputs["autosync"]),
        str(work / "nexus.class"), str(outputs["nexus"]),
    ], check=True)
    return outputs

def build_hud(base: Path, output: Path, helper: Path, patched: dict[str, Path]) -> None:
    replacements = {
        "fr/arthur/capitale/rphud/CapitaleStrictRaceHealthFinalizer.class": patched["strict"],
        "fr/arthur/capitale/rphud/CapitaleRebuildMorphServer.class": patched["rebuild"],
        "fr/arthur/capitale/rphud/CapitaleExactPersonnagesAutoSync.class": patched["autosync"],
    }
    with zipfile.ZipFile(base) as zin, zipfile.ZipFile(output, "w") as zout:
        for info in zin.infolist():
            data = zin.read(info.filename)
            if info.filename == "fabric.mod.json":
                manifest = json.loads(data)
                manifest["version"] = "1.3.3-beta.1+race-health-modifier-compat"
                manifest["description"] = (
                    "BETA 1.3.3: race health owns MAX_HEALTH base only; equipment/effect "
                    "modifiers and health above the racial base are preserved. Keeps 1.3.2 "
                    "Nexus authority and armor40 HUD fixes."
                )
                data = json_bytes(manifest)
            elif info.filename in replacements:
                data = replacements[info.filename].read_bytes()
            zout.writestr(info, data)
        zout.writestr(
            helper_info("fr/arthur/capitale/rphud/RaceHealthModifierCompat.class"),
            helper.read_bytes(),
        )

def build_nexus(base: Path, output: Path, patched_nexus: Path) -> None:
    target = "net/tompsen/nexuscharacters/HauteCapitaleIdentity.class"
    with zipfile.ZipFile(base) as zin, zipfile.ZipFile(output, "w") as zout:
        for info in zin.infolist():
            data = zin.read(info.filename)
            if info.filename == "fabric.mod.json":
                manifest = json.loads(data)
                manifest["version"] = "1.5.1-capitale-port-v0.8.0-alpha1.2-health-modifier-compat+1.21.11"
                manifest["description"] = (
                    "HC 0.8.0-alpha1.2 preserves all alpha1.1 authority/skin fixes and "
                    "clamps current health against actual total MAX_HEALTH instead of the "
                    "racial base, preserving equipment health modifiers."
                )
                data = json_bytes(manifest)
            elif info.filename == target:
                data = patched_nexus.read_bytes()
            zout.writestr(info, data)

def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--hud-1-3-2", type=Path, required=True)
    ap.add_argument("--nexus-alpha1-1", type=Path, required=True)
    ap.add_argument("--out-dir", type=Path, required=True)
    args = ap.parse_args()

    if sha256(args.hud_1_3_2) != EXPECTED["hud"]:
        raise SystemExit("unexpected HUD 1.3.2 SHA-256")
    if sha256(args.nexus_alpha1_1) != EXPECTED["nexus"]:
        raise SystemExit("unexpected Nexus alpha1.1 SHA-256")

    here = Path(__file__).parent
    args.out_dir.mkdir(parents=True, exist_ok=True)
    hud_out = args.out_dir / "capitale_rp_hud_BETA_1_3_3_RACE_HEALTH_MODIFIER_COMPAT.jar"
    nexus_out = args.out_dir / "NexusCharacters-HauteCapitale-1.21.11-v0.8.0-alpha1.2-HEALTH-MODIFIER-COMPAT.jar"

    with tempfile.TemporaryDirectory() as td:
        work = Path(td)
        helper, patch_classes = compile_sources(
            here / "RaceHealthModifierCompat.java",
            here / "PatchHealthCompat.java",
            work,
        )
        patched = patch_classes(args.hud_1_3_2, args.nexus_alpha1_1, patch_classes, work)
        build_hud(args.hud_1_3_2, hud_out, helper, patched)
        build_nexus(args.nexus_alpha1_1, nexus_out, patched["nexus"])

    actual = {"hud": sha256(hud_out), "nexus": sha256(nexus_out)}
    if actual != EXPECTED_OUTPUTS:
        raise SystemExit(f"non-reproducible output: {actual}")
    print(actual)
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
