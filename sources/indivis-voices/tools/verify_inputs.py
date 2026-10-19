#!/usr/bin/env python3
"""Read-only check of the voice bank, actor mappings and supplied core 1.5.7."""
import argparse
import hashlib
import json
from pathlib import Path
import re
import zipfile

ROOT = Path(__file__).resolve().parents[1]
CORE_SHA = "9b9c20bbc0a28d85384d7bb5e7d00199fee87af795fc081a24e3f0971c61e9df"


def verify_human_mappings(doc, provenance):
    assert doc['revision'] == provenance['revision']
    references = {r['productionId']: r for r in provenance['lines']}
    assert len(references) == len(provenance['lines']) == 18
    human = [r for r in doc['lines'] if r.get('productionId')]
    assert len(human) == 18 and len({r['productionId'] for r in human}) == 18
    for line in human:
        ref = references[line['productionId']]
        assert (line['id'], line['asset'], line['sound'], line['sha256'], line['masterSha256']) == (ref['lineId'], ref['asset'], ref['sound'], ref['oggSha256'], ref['masterSha256']), line['productionId']
        assert (line['text'], line['wireText'], line['sourceFunction'], line['nextSequence']) == (ref['text'], ref['wireText'], ref['sourceFunction'], ref['nextSequence']), line['productionId']
        assert line['takeVersion'] == 'arthur-20261019-lot1-v1'
    assert references['P01-GER-005']['selectedTake'] == 1


def verify(core=None):
    doc = json.loads((ROOT / "src/main/resources/indivis-voices/catalog.json").read_text())
    manifest = json.loads((ROOT / "demo-resourcepack/voice-manifest.json").read_text())
    assert doc == manifest, "Pack/catalog revision mismatch"
    assert len({l["id"] for l in doc["lines"]}) == len(doc["lines"])
    assert len({l["wireText"] for l in doc["lines"]}) == len(doc["lines"])
    sounds = json.loads((ROOT / "demo-resourcepack/assets/indivis_voices/sounds.json").read_text())
    verify_human_mappings(doc, json.loads((ROOT / 'human-takes.json').read_text()))
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
                messages = [json.JSONDecoder().raw_decode(l.split('tellraw @s ', 1)[1])[0] for l in text.splitlines() if 'tellraw @s [{' in l]
                assert any("".join(c.get("text", "") for c in m) == line["wireText"] for m in messages), source
                if line['nextSequence'] > 0:
                    assert f"scoreboard players set @s CAP_QSEQ {line['nextSequence']}\n" in text, source
                    assert "scoreboard players set @s CAP_QSEQ_TIMER 20" in text, source
                elif line['productionId'] == 'P01-GER-005':
                    assert 'CAP_QSEQ' not in text
                    assert 'if score @s QUEST_SPAWN matches 10..39 run tellraw @s' in text
                else:
                    assert line['productionId'] in ('P01-ALT-001', 'P01-ALT-002', 'P01-ALT-003')
                    assert 'scoreboard players set @s CAP_QSEQ 0' in text
                    assert 'scoreboard players set @s CAP_QSEQ_TIMER 0' in text
            tick = z.read("data/capitale/function/core/tick.mcfunction").decode()
            assert "scoreboard players remove @a[scores={CAP_QSEQ_TIMER=1..}] CAP_QSEQ_TIMER 1" in tick
            assert "execute as @a[scores={CAP_QSEQ=1..,CAP_QSEQ_TIMER=0}] run function capitale:quest/dialogue/sequence/dispatch_self" in tick
            dispatch = z.read("data/capitale/function/quest/dialogue/sequence/dispatch_self.mcfunction").decode()
            fixture = (ROOT / 'src/test/resources/core-1.5.7-dispatch.mcfunction').read_text()
            assert dispatch.splitlines() == fixture.splitlines(), 'Core dispatcher differs from the regression fixture'
            assert hashlib.sha256('\n'.join(dispatch.splitlines()).encode()).hexdigest() == '7c2fa10c295379b9bab0ac4e5157ebe916a42a30d070408d7f57a3859acc6b4a'
            assert len(re.findall(r'^execute if score @s CAP_FLAG matches 1 if score @s CAP_QSEQ matches \d+ run function capitale:', dispatch, re.M)) == 38
    print(f"OK : {len(doc['lines'])} textes, IDs, assets, SHA-256, durées et liens" + (" ; core 1.5.7 exact, timer natif et fixture des 38 branches vérifiés (exécution testée séparément)" if core else ""))


if __name__ == "__main__":
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--core", type=Path)
    verify(p.parse_args().core)
