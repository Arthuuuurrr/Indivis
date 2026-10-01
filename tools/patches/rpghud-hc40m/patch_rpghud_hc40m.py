#!/usr/bin/env python3
from pathlib import Path
import base64, hashlib, json, sys, zipfile

BASE_SHA256 = "ab2dec0daf697af037c294c50ba7a8dc5251048294d5f88229bc14d832a02b00"
OUTPUT_SHA256 = "a255bfe59ddc0bff9081dfc9219da46c5917a3052e8b6279571872fd09552c02"

def sha256(p: Path):
    return hashlib.sha256(p.read_bytes()).hexdigest()

def main(base: Path, out: Path, mixin_b64: Path):
    if sha256(base) != BASE_SHA256:
        raise SystemExit(f"Unexpected RPG-HUD base SHA-256: {sha256(base)}")
    cls = base64.b64decode(mixin_b64.read_text(encoding="ascii").strip())
    with zipfile.ZipFile(base, "r") as zin:
        mix = json.loads(zin.read("rpg-hud.mixins.json").decode("utf-8"))
        if "HauteCapitaleClockMixin" not in mix["client"]:
            mix["client"].append("HauteCapitaleClockMixin")
        fabric = json.loads(zin.read("fabric.mod.json").decode("utf-8"))
        fabric["version"] = "3.13+hc40m.1"
        fabric["name"] = "RPG-Hud — Haute Capitale"
        fabric["description"] = fabric.get("description", "") + " Haute Capitale patch: smooth 0.5x RPG clock for the 40-minute day/night cycle."
        replacements = {
            "rpg-hud.mixins.json": (json.dumps(mix, ensure_ascii=False, indent=2) + "\n").encode(),
            "fabric.mod.json": (json.dumps(fabric, ensure_ascii=False, indent=2) + "\n").encode(),
        }
        note = """RPG-HUD 3.13 — Haute Capitale HC40M compatibility 1.0

Base SHA-256: ab2dec0daf697af037c294c50ba7a8dc5251048294d5f88229bc14d832a02b00

Only RPG-HUD clock reads are smoothed to 0.5x using gameTime as the stable clock.
Small backward server time corrections are ignored; real /time/sleep/world changes
resynchronize immediately. Frozen day cycle and resume are handled.
No gameplay time, gamerule, tick rate, world state or other RPG-HUD element is modified.
""".encode()
        additions = {
            "net/spellcraftgaming/rpghud/mixin/HauteCapitaleClockMixin.class": cls,
            "__haute_capitale_meta/HC40M_CLOCK_PATCH_1.0.txt": note,
        }
        with zipfile.ZipFile(out, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=9) as zout:
            for info in zin.infolist():
                data = replacements.get(info.filename, zin.read(info.filename))
                ni = zipfile.ZipInfo(info.filename, info.date_time)
                ni.compress_type = zipfile.ZIP_DEFLATED
                ni.external_attr = info.external_attr
                ni.internal_attr = info.internal_attr
                ni.create_system = info.create_system
                ni.comment = info.comment
                ni.extra = info.extra
                zout.writestr(ni, data)
            for name, data in additions.items():
                ni = zipfile.ZipInfo(name, (2026, 10, 1, 12, 0, 0))
                ni.compress_type = zipfile.ZIP_DEFLATED
                ni.external_attr = (0o100644 & 0xFFFF) << 16
                zout.writestr(ni, data)
    if sha256(out) != OUTPUT_SHA256:
        raise SystemExit(f"Unexpected patched JAR SHA-256: {sha256(out)}")
    with zipfile.ZipFile(base) as a, zipfile.ZipFile(out) as b:
        assert b.testzip() is None
        an, bn = set(a.namelist()), set(b.namelist())
        assert bn - an == {
            "net/spellcraftgaming/rpghud/mixin/HauteCapitaleClockMixin.class",
            "__haute_capitale_meta/HC40M_CLOCK_PATCH_1.0.txt",
        }
        changed = [n for n in sorted(an) if a.read(n) != b.read(n)]
        assert changed == ["fabric.mod.json", "rpg-hud.mixins.json"], changed
    print(sha256(out))

if __name__ == "__main__":
    if len(sys.argv) != 4:
        raise SystemExit("usage: patch_rpghud_hc40m.py <base.jar> <output.jar> <mixin.class.b64>")
    main(Path(sys.argv[1]), Path(sys.argv[2]), Path(sys.argv[3]))
