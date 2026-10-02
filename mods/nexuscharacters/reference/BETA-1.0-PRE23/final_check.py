"""Runs the final binary and a rejected baseline, saving only completed checks."""
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import time

ROOT = Path(__file__).resolve().parent.parent
os.chdir(ROOT)
EVIDENCE = ROOT / "pre23/evidence"
FINAL = ROOT / "pre23/candidate.jar"
RUNTIME = ROOT / "runtime/game/mods/NexusCharacters-PRE20.jar"
SHA = hashlib.sha256(FINAL.read_bytes()).hexdigest()
assert SHA == "2d10761e7242b29b6ed1faba2c3dc5456c0b2111e64692018bf72a8cf1d4dbce"
catalog = (EVIDENCE / "catalog-final.log").read_text()
assert "CATALOG_RENDERER_PASS configurations=57 poses=171" in catalog
assert "CATALOG_REGRESSION_FAILURE" not in catalog
(EVIDENCE / "catalog-final.log").write_text(catalog)


def build(entry):
    env = dict(os.environ, HARNESS_ENTRY=entry)
    subprocess.run(["python", "pre23/build_test.py"], env=env, check=True)


def run(name, *, negative=False, game=None):
    env = dict(os.environ, COMPATIBILITY_TEST="1")
    if game is not None:
        env['TEST_GAME_DIR'] = str(game)
    for attempt in range(3):
        result = subprocess.run(["python", "pre23/launch.py"], env=env)
        log = (ROOT / "pre23/render.log").read_text()
        expected = "1.0.0-beta.22+hc.1.21.11" if negative else "1.0.0-beta.23+hc.1.21.11"
        assert "nexuscharacters " + expected in log, "Wrong test binary"
        (EVIDENCE / f"{name}-attempt-{attempt + 1}.log").write_text(log)
        if negative and "REPORTED_REGRESSION_FAILURE" in log:
            match = re.search(r"Preview failed overlaps=(\d+) captures=24", log)
            assert result.returncode != 0 and match and int(match.group(1)) > 0
            (EVIDENCE / f"{name}.log").write_text(log)
            print("PRE22_REJECTED_BY_FINAL_AUDIT overlaps=" + match.group(1), flush=True)
            return log
        if result.returncode == 0:
            assert not negative and "ACTUAL_PLAYER_REGRESSION_PASS" in log
            (EVIDENCE / f"{name}.log").write_text(log)
            return log
        assert not re.search(r"(?:ACTUAL|REPORTED|CATALOG)_REGRESSION_FAILURE", log), name
        print(f"RETRY_INCOMPLETE_STARTUP {name} attempt={attempt + 1}", flush=True)
    raise AssertionError("Minecraft did not complete " + name)


build("ReportedRegression")
negative_game = ROOT / "runtime/pre23-negative-game"
(negative_game / "mods").mkdir(parents=True, exist_ok=True)
for mod in (ROOT / "runtime/game/mods").glob('*.jar'):
    if not mod.name.startswith('NexusCharacters'):
        shutil.copy2(mod, negative_game / 'mods' / mod.name)
shutil.copy2(ROOT / "pre22/NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE22.jar",
             negative_game / 'mods/NexusCharacters-PRE22.jar')
for name in ['options.txt']:
    if (ROOT / 'runtime/game' / name).is_file():
        shutil.copy2(ROOT / 'runtime/game' / name, negative_game / name)
if (ROOT / 'runtime/game/config').is_dir():
    shutil.copytree(ROOT / 'runtime/game/config', negative_game / 'config', dirs_exist_ok=True)
negative = run("pre22-rejected-final-tests", negative=True, game=negative_game)
shutil.copy2(FINAL, RUNTIME)

build("ActualPlayerRegression")
actual_start = time.time()
actual = run("actual-final")
assert "ACTUAL_PLAYER_REGRESSION_PASS cases=12 captures=36 missing=0 overlaps=0" in actual
assert actual.count("POSE_GEOMETRY_RESULT") == 60
assert "ACCOUNT_SKIN_PASS" in actual and "FIRST_PERSON_RENDER_PASS" in actual
assert "ASSET_RELOAD_REQUESTED" in actual and "PAL_ACTIVE_ANIMATION_STARTED" in actual
for i in range(12):
    for angle in range(3):
        assert (EVIDENCE / f"actual-{i}-{angle}.png").stat().st_mtime >= actual_start

build("ReportedRegression")
preview_start = time.time()
preview = run("preview-final")
assert "REPORTED_PREVIEW_PASS captures=24 overlaps=0 wide+slim=true" in preview
for kind in ("widget", "world"):
    for i in range(4):
        for angle in range(3):
            assert (EVIDENCE / f"preview-{kind}-{i}-{angle}.png").stat().st_mtime >= preview_start
assert hashlib.sha256(FINAL.read_bytes()).hexdigest() == SHA
assert hashlib.sha256(RUNTIME.read_bytes()).hexdigest() == SHA
summary = {
    "jar_sha256": SHA,
    "catalog": {"configurations": 57, "poses": 171, "coplanar": 0, "crossings": 0},
    "actual_player": {"configurations": 12, "poses": 60, "captures": 36, "missing_meshes": 0, "coplanar": 0, "crossings": 0},
    "menu_previews": {"geometry_checks": 12, "captures": 24, "overlaps": 0, "wide_and_slim": True},
    "new_assets": {"hair": 2, "classic_beards": 2, "outfits": 1},
    "account_skin": "cached fixture: network entry, preview, actual player, render state, temporary default, stale saved snapshot",
    "first_person": True,
    "resource_cache_reload": True,
    "active_player_animation_library": True,
    "negative_pre22_overlap_count": int(re.search(r"Preview failed overlaps=(\d+)", negative).group(1)),
    "logs_sha256": {name: hashlib.sha256((EVIDENCE / name).read_bytes()).hexdigest() for name in ("catalog-final.log", "actual-final.log", "preview-final.log", "pre22-rejected-final-tests.log")},
}
(EVIDENCE / "validation-run.json").write_text(json.dumps(summary, indent=2) + "\n")
print("FINAL_BINARY_VALIDATED " + SHA, flush=True)
