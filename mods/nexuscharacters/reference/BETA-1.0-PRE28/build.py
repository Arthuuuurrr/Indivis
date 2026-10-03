from pathlib import Path
import hashlib, json, os, subprocess, zipfile
import fitz

ROOT = Path(__file__).resolve().parent.parent
WORK = ROOT / 'pre28'
BASE = ROOT / 'pre27-delivery/NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE27.jar'
assert hashlib.sha256(BASE.read_bytes()).hexdigest() == 'cb6169824f8f23b6afe3faa045b57ded89bb34eee12210e86c9401ad7568c15c'
classes = WORK / 'classes'
classes.mkdir(exist_ok=True)
cp = ':'.join(str(p) for p in [BASE, ROOT / 'runtime/client-intermediary.jar', *sorted((ROOT / 'runtime/libs').rglob('*.jar'))])
subprocess.run([str(WORK / 'jdk/bin/javac'), '-proc:none', '-cp', cp, '-d', str(classes), *map(str, sorted((WORK / 'src').rglob('*.java')))], check=True)
doc = fitz.open(ROOT / 'attachments/2fddc5d0-9803-4c78-85f2-c2c90cda4039/Haute_Capitale_Fiefs_Maisons_Heraldique_v0_1.pdf')
assets = WORK / 'assets/nexuscharacters/textures/gui/indivis/capitals'
assets.mkdir(parents=True, exist_ok=True)
for name, xref in [('nordic', 35), ('dwarf', 37), ('high_elf', 39), ('wood_elf', 41)]:
    mask = next(i[1] for i in doc[10].get_images() if i[0] == xref)
    fitz.Pixmap(fitz.Pixmap(doc, xref), fitz.Pixmap(doc, mask)).save(assets / (name + '.png'))
with zipfile.ZipFile(BASE) as z:
    entries = {i.filename: z.read(i) for i in z.infolist()}
for p in classes.rglob('*.class'):
    entries[p.relative_to(classes).as_posix()] = p.read_bytes()
for p in (WORK / 'assets').rglob('*.png'):
    entries[p.relative_to(WORK).as_posix()] = p.read_bytes()
meta = json.loads(entries['fabric.mod.json'])
meta['version'] = '1.0.0-beta.28+indivis.1.21.11'
entries['fabric.mod.json'] = json.dumps(meta, ensure_ascii=False, indent=2).encode()
mixins = json.loads(entries['nexuscharacters.client.mixins.json'])
mixins['client'].extend(['IndivisBannerMixin', 'IndivisWorldlessViewportMixin', 'IndivisTextContrastMixin', 'IndivisControlsReadabilityMixin', 'IndivisNameFieldContrastMixin'])
entries['nexuscharacters.client.mixins.json'] = json.dumps(mixins, indent=2).encode()
out = WORK / 'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE28.jar'
with zipfile.ZipFile(out, 'w', zipfile.ZIP_DEFLATED) as z:
    for name, data in sorted(entries.items()):
        info = zipfile.ZipInfo(name, (2026, 10, 3, 0, 0, 0))
        info.compress_type = zipfile.ZIP_DEFLATED
        z.writestr(info, data)
with out.open('rb') as f: os.fsync(f.fileno())
with zipfile.ZipFile(out) as z: assert z.testzip() is None
print('BUILT', out, out.stat().st_size, hashlib.sha256(out.read_bytes()).hexdigest(), flush=True)
