from pathlib import Path
import json, subprocess, hashlib, zipfile
WORK = Path(__file__).resolve().parent
ROOT = WORK.parent
BASE = WORK / 'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE28.jar'
assert hashlib.sha256(BASE.read_bytes()).hexdigest() == '32247a901654df15cdfad011c85e3cf16ccb02848f893c342029581a81c43574'
classes = WORK / 'classes'; classes.mkdir(exist_ok=True)
cp = ':'.join(map(str, [BASE, ROOT/'runtime/client-intermediary.jar', *sorted((ROOT/'runtime/libs').rglob('*.jar'))]))
subprocess.run([str(ROOT/'jdk21/bin/javac'), '-proc:none', '-cp', cp, '-d', str(classes), *map(str, (WORK/'src').rglob('*.java'))], check=True)
with zipfile.ZipFile(BASE) as z: entries = {i.filename:z.read(i) for i in z.infolist()}
for p in classes.rglob('*.class'): entries[p.relative_to(classes).as_posix()] = p.read_bytes()
meta = json.loads(entries['fabric.mod.json']); meta['version'] = '1.0.0-beta.29+indivis.1.21.11'
meta['description'] = 'Indivis PRE29: uniform menu backgrounds and synchronized initial character preview.'
entries['fabric.mod.json'] = json.dumps(meta, ensure_ascii=False, indent=2).encode()
out = WORK/'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE29.jar'
with zipfile.ZipFile(out, 'w', zipfile.ZIP_DEFLATED) as z:
    for name,data in sorted(entries.items()):
        info=zipfile.ZipInfo(name,(2026,10,3,0,0,0)); info.compress_type=zipfile.ZIP_DEFLATED; z.writestr(info,data)
with zipfile.ZipFile(out) as z: assert z.testzip() is None
print('PRE29_BUILT', hashlib.sha256(out.read_bytes()).hexdigest(), flush=True)
