#!/usr/bin/env python3
"""Generate first-generation OGGs from WAV masters, or explicitly synthetic test takes.

Requires ffmpeg/ffprobe. Human takes: --masters /path/to/wavs (names = stable id.wav).
Technical demo only: --demo --espeak /path/to/espeak-ng (version 1.51 used initially).
No lossy input or lossy-to-lossy re-encoding. Run from any directory.
"""
import argparse
import hashlib
import json
from pathlib import Path
import subprocess
import tempfile
import zipfile

ROOT = Path(__file__).resolve().parents[1]
CATALOG = ROOT / "src/main/resources/indivis-voices/catalog.json"
PACK = ROOT / "demo-resourcepack"


def run(*args):
    subprocess.run(args, check=True, stdout=subprocess.DEVNULL)


def build(args):
    doc = json.loads(CATALOG.read_text())
    doc["revision"] = args.revision
    events = {}
    encoded = 0
    masters = ROOT / "build/voice-masters"
    masters.mkdir(parents=True, exist_ok=True)
    for line in doc["lines"]:
        master = (args.masters / (line["id"] + ".wav")) if args.masters else masters / (line["id"] + ".wav")
        if args.masters and not master.is_file():
            master = args.masters / (line["id"] + ".flac")
        if args.demo:
            # Synthesized test speech; no imitation, sample of a person's voice or casting decision.
            run(args.espeak, "-v", "fr-fr", "-s", "155", "-w", str(master), line["text"])
        target = PACK / "assets/indivis_voices/sounds" / line["asset"]
        target.parent.mkdir(parents=True, exist_ok=True)
        valid_target = target.is_file() and hashlib.sha256(target.read_bytes()).hexdigest() == line["sha256"]
        if not master.is_file() and not valid_target:
            raise SystemExit(f"Missing lossless master and no existing verified take: {master}")
        # A partial set of new human takes can update the bank incrementally.
        master_hash = hashlib.sha256(master.read_bytes()).hexdigest() if master.is_file() else line.get("masterSha256")
        unchanged = valid_target and (not master.is_file() or line.get("masterSha256") == master_hash)
        if not unchanged:
            encoded += 1
            run("ffmpeg", "-nostdin", "-hide_banner", "-loglevel", "error", "-y", "-i", str(master),
                "-map_metadata", "-1", "-ac", "1", "-c:a", "libvorbis", "-b:a", "64k", str(target))
        probe = json.loads(subprocess.check_output(["ffprobe", "-v", "error", "-show_streams", "-of", "json", str(target)]))
        stream = probe["streams"][0]
        if stream["codec_name"] != "vorbis" or stream["channels"] != 1:
            raise SystemExit(f"Incorrect format: {target}")
        line["durationMs"] = round(float(stream["duration"]) * 1000)
        line["sha256"] = hashlib.sha256(target.read_bytes()).hexdigest()
        if not unchanged:
            line["takeVersion"] = args.take_version
        line["masterSha256"] = master_hash
        events[line["sound"]] = {"sounds": [{"name": "indivis_voices:" + line["asset"].removesuffix(".ogg"), "stream": True, "attenuation_distance": 20}]}
    CATALOG.write_text(json.dumps(doc, ensure_ascii=False, indent=2) + "\n")
    (PACK / "assets/indivis_voices/sounds.json").write_text(json.dumps(events, ensure_ascii=False, indent=2) + "\n")
    (PACK / "voice-manifest.json").write_text(json.dumps(doc, ensure_ascii=False, indent=2) + "\n")
    # 1.21.11 client resource format, independently from data pack format 94.1.
    human_count = sum(bool(line.get('productionId')) for line in doc['lines'])
    description = f"Indivis Voices FR — {human_count} prises humaines, {len(doc['lines']) - human_count} démonstrations"
    (PACK / "pack.mcmeta").write_text(json.dumps({"pack": {"description": description, "min_format": 75, "max_format": 75}}, ensure_ascii=False, indent=2) + "\n")
    out = ROOT / "build/distributions"
    out.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(out / f"indivis-voices-{args.revision}.zip", "w", zipfile.ZIP_DEFLATED) as z:
        for p in sorted(PACK.rglob("*")):
            if p.is_file():
                info = zipfile.ZipInfo(p.relative_to(PACK).as_posix(), (2026, 1, 1, 0, 0, 0))
                info.compress_type = zipfile.ZIP_DEFLATED
                z.writestr(info, p.read_bytes())
    print(f"{len(doc['lines'])} prises vérifiées, {encoded} encodées ; catalogue et pack mis à jour")


if __name__ == "__main__":
    p = argparse.ArgumentParser(description=__doc__)
    source = p.add_mutually_exclusive_group(required=True)
    source.add_argument("--demo", action="store_true")
    source.add_argument("--masters", type=Path)
    p.add_argument("--espeak", default="espeak-ng")
    p.add_argument("--revision", required=True)
    p.add_argument("--take-version", required=True)
    build(p.parse_args())
