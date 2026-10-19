#!/usr/bin/env python3
"""Read-only check of supplied core 1.5.7 + demo pack. No patching or server access."""
import argparse
import hashlib
import json
from pathlib import Path
import re
import zipfile

ROOT = Path(__file__).resolve().parents[1]
CORE_SHA = "9b9c20bbc0a28d85384d7bb5e7d00199fee87af795fc081a24e3f0971c61e9df"


def verify(core=None):
    doc = json.loads((ROOT / "src/main/resources/indivis-voices/catalog.json").read_text())
    manifest = json.loads((ROOT / "demo-resourcepack/voice-manifest.json").read_text())
    assert doc == manifest, "Pack/catalog revision mismatch"
    assert len({l["id"] for l in doc["lines"]}) == len(doc["lines"])
    assert len({l["wireText"] for l in doc["lines"]}) == len(doc["lines"])
    sounds = json.loads((ROOT / "demo-resourcepack/assets/indivis_voices/sounds.json").read_text())
    for line in doc["lines"]:
        asset = ROOT / "demo-resourcepack/assets/indivis_voices/sounds" / line["asset"]
        assert hashlib.sha256(asset.read_bytes()).hexdigest() == line["sha256"], asset
        assert 100 <= line["durationMs"] <= 120000
        assert sounds[line["sound"]]["sounds"][0]["stream"] is True
        master = ROOT / "demo-masters" / (line["id"] + ".flac")
        if master.is_file() and line["takeVersion"] == "demo-espeak-ng-1.51-v1":
            assert hashlib.sha256(master.read_bytes()).hexdigest() == line["masterSha256"], master
    if core:
        assert hashlib.sha256(core.read_bytes()).hexdigest() == CORE_SHA, "Different core: audit it before enabling voices"
        with zipfile.ZipFile(core) as z:
            for line in doc["lines"]:
                source = line["sourceFunction"].removeprefix("capitale:")
                text = z.read(f"data/capitale/function/{source}.mcfunction").decode()
                messages = [json.loads(l.removeprefix("tellraw @s ")) for l in text.splitlines() if l.startswith("tellraw @s [{")]
                assert any("".join(c.get("text", "") for c in m) == line["wireText"] for m in messages), source
                assert f"scoreboard players set @s CAP_QSEQ {line['nextSequence']}\n" in text, source
                assert "scoreboard players set @s CAP_QSEQ_TIMER 20" in text, source
            tick = z.read("data/capitale/function/core/tick.mcfunction").decode()
            assert "scoreboard players remove @a[scores={CAP_QSEQ_TIMER=1..}] CAP_QSEQ_TIMER 1" in tick
            assert "execute as @a[scores={CAP_QSEQ=1..,CAP_QSEQ_TIMER=0}] run function capitale:quest/dialogue/sequence/dispatch_self" in tick
            dispatch = z.read("data/capitale/function/quest/dialogue/sequence/dispatch_self.mcfunction").decode()
            assert "scoreboard players set @s CAP_FLAG 0" in dispatch
    print(f"OK : {len(doc['lines'])} textes, IDs, assets, SHA-256, durées et liens" + (" ; core 1.5.7 exact et timer/dispatch vérifiés" if core else ""))


if __name__ == "__main__":
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--core", type=Path)
    verify(p.parse_args().core)
