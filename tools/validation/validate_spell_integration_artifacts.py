#!/usr/bin/env python3
"""Validate the final Haute Capitale TEST3 spell-integration artifacts.

Usage:
    python tools/validation/validate_spell_integration_artifacts.py /path/to/final_bundle_or_directory

The validator does not start Minecraft. It verifies hashes, ZIP/JAR integrity,
duplicate entries and several critical file-level invariants that previously
caused regressions.
"""
from __future__ import annotations

import hashlib
import json
import sys
import zipfile
from pathlib import Path

EXPECTED = {
    "haute-capitale-rpg-0.3.0+1.21.11.b2.HC.TEST3.RC2.jar":
        "65824c4c54c96825a3ffdc2e8c441c690d9f4a466622efd62b33ff8c6d7548e4",
    "spell_engine-fabric-1.10.5.001+1.21.11-HC-TEST3-RC7-HAZENN-FULLCOMPAT.jar":
        "ecd2065e849a32d6626f9a4f760aae6edda15972ad300305c427b12527eb283c",
    "spell_power-fabric-1.6.1.001+1.21.11-HC-HAZENN-PRECISE-TEST3-RC7-DIRECT-RESIST.jar":
        "a9646fd981b9ebff92efe675603fbe9e29515ada4575683bad6d4d7c318835d9",
    "hazennstuff-fabric-1.21.11-1.0.0-b4-HC-SPELLCOMPAT1.jar":
        "88beb1a43a5d3f4a2ee5f4391937cf2dc0a67e6d8a587f2cbc6203e85846a748",
    "azurelibarmor-fabric-1.21.11-3.1.4-HC-TEST3-RC1.jar":
        "12a93c7fba4a58cff6f9608c537cfb0acde996123a079ceceb23a23fa03ef1b8",
    "witcher-class-mod-fabric-3.1.0-1.21.11-mmo-HC-FOOTWORK-TEST3-RC1.jar":
        "29ab8c9ba7835abcfdd6c9e384965c275a4f23f1e8c8059840e0d27b3b90e9de",
    "arsenal-fabric-1.5.1.002-mmo+1.21.11-HC-FR-PRIMARY-ORDER1.jar":
        "d335876b27273cc952a149a3b0271523f9126101953925a7c35d2fdde29a53d6",
    "capitale_skills_items_fabric_1_7_18_EVENT_ACCESS_FIX_1_21_11.jar":
        "0dfbfe3f324d40851de01346b2bced6ecb6ab2ab38a36c1bc977f5623f94b30b",
    "capitale_skills_BETA_0_10_19_RC2F_TREE_HARD_RESTORE.zip":
        "109ec0428909c009d6599637cc0b23a90c78bd84c9d5772c6447ecf01a3d34f4",
}

REQUIRED_ENTRIES = {
    "haute-capitale-rpg-0.3.0+1.21.11.b2.HC.TEST3.RC2.jar": {
        "net/hautecapitale/rpg/ability/HcAvatarStaffGate.class",
        "net/hautecapitale/rpg/ability/AbilityResolver.class",
    },
    "spell_engine-fabric-1.10.5.001+1.21.11-HC-TEST3-RC7-HAZENN-FULLCOMPAT.jar": {
        "net/spell_engine/config/HcHudMigration.class",
        "net/spell_engine/hc/HcHazennCastingMovementBridge.class",
        "net/spell_engine/hc/HcHazennSummonDamageBridge.class",
        "net/spell_engine/internals/HcArsenalGate.class",
        "net/spell_engine/internals/HcIntegration.class",
        "net/spell_engine/mixin/entity/HcHazennSummonDamageMixin.class",
    },
    "spell_power-fabric-1.6.1.001+1.21.11-HC-HAZENN-PRECISE-TEST3-RC7-DIRECT-RESIST.jar": {
        "net/spell_power/hc/HcHazennResistanceBridge.class",
        "net/spell_power/hc/HcHazennSpellPowerBridge.class",
        "net/spell_power/api/SpellResistance.class",
        "net/spell_power/api/SpellSchool.class",
    },
    "hazennstuff-fabric-1.21.11-1.0.0-b4-HC-SPELLCOMPAT1.jar": {
        "net/hazen/hazennstuff/registry/HnSAttributes.class",
    },
    "witcher-class-mod-fabric-3.1.0-1.21.11-mmo-HC-FOOTWORK-TEST3-RC1.jar": {
        "assets/witcher_rpg/textures/mob_effect/footwork.png",
    },
    "azurelibarmor-fabric-1.21.11-3.1.4-HC-TEST3-RC1.jar": {
        "mod/azure/azurelibarmor/common/render/armor/AzArmorModel.class",
    },
}

FORBIDDEN_ENTRIES = {
    "spell_power-fabric-1.6.1.001+1.21.11-HC-HAZENN-PRECISE-TEST3-RC7-DIRECT-RESIST.jar": {
        "net/spell_power/mixin/HcHazennSpellResistanceMixin.class",
    },
}


def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def find_artifacts(root: Path) -> dict[str, Path]:
    found: dict[str, Path] = {}
    for path in root.rglob("*"):
        if path.is_file() and path.name in EXPECTED:
            found[path.name] = path
    return found


def validate_zip(path: Path) -> list[str]:
    errors: list[str] = []
    try:
        with zipfile.ZipFile(path) as zf:
            names = zf.namelist()
            if len(names) != len(set(names)):
                errors.append(f"{path.name}: duplicate ZIP entries")
            bad = zf.testzip()
            if bad:
                errors.append(f"{path.name}: corrupt ZIP member {bad}")

            required = REQUIRED_ENTRIES.get(path.name, set())
            missing = sorted(required - set(names))
            for item in missing:
                errors.append(f"{path.name}: missing required entry {item}")

            forbidden = FORBIDDEN_ENTRIES.get(path.name, set())
            present_forbidden = sorted(forbidden & set(names))
            for item in present_forbidden:
                errors.append(f"{path.name}: forbidden legacy entry still present: {item}")

            if path.name.startswith("spell_power-") and "spell_power.mixins.json" in names:
                mixins = zf.read("spell_power.mixins.json").decode("utf-8")
                if "HcHazennSpellResistanceMixin" in mixins:
                    errors.append(
                        f"{path.name}: RC6 resistance mixin is still referenced in spell_power.mixins.json"
                    )

            if "fabric.mod.json" in names:
                try:
                    json.loads(zf.read("fabric.mod.json"))
                except Exception as exc:
                    errors.append(f"{path.name}: invalid fabric.mod.json: {exc}")
    except zipfile.BadZipFile as exc:
        errors.append(f"{path.name}: invalid archive: {exc}")
    return errors


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: validate_spell_integration_artifacts.py <bundle-or-directory>", file=sys.stderr)
        return 2

    root = Path(sys.argv[1]).resolve()
    if root.is_file() and root.suffix.lower() == ".zip":
        print("Extract the bundle first, then pass the extracted directory.")
        return 2
    if not root.is_dir():
        print(f"not a directory: {root}", file=sys.stderr)
        return 2

    found = find_artifacts(root)
    errors: list[str] = []

    for name, expected_hash in EXPECTED.items():
        path = found.get(name)
        if path is None:
            errors.append(f"missing artifact: {name}")
            continue
        actual = sha256(path)
        if actual != expected_hash:
            errors.append(f"{name}: SHA-256 mismatch\n  expected {expected_hash}\n  actual   {actual}")
        errors.extend(validate_zip(path))

    print(f"Artifacts found: {len(found)}/{len(EXPECTED)}")
    if errors:
        print(f"FAIL: {len(errors)} problem(s)")
        for err in errors:
            print(f"- {err}")
        return 1

    print("PASS: hashes, archive integrity and critical file invariants match TEST3 final state")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
