from pathlib import Path
import base64, hashlib, zipfile

WORK = Path(__file__).resolve().parent
BASE = WORK.parent / 'pre27-delivery/NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE27.jar'
CANDIDATE = WORK / 'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE28.jar'
assert hashlib.sha256(CANDIDATE.read_bytes()).hexdigest() == '32247a901654df15cdfad011c85e3cf16ccb02848f893c342029581a81c43574'
with zipfile.ZipFile(BASE) as b, zipfile.ZipFile(CANDIDATE) as c:
    changed = [name for name in b.namelist() if b.read(name) != c.read(name)]
    assert set(changed) == {'fabric.mod.json', 'nexuscharacters.client.mixins.json'}, changed
    added = sorted(set(c.namelist()) - set(b.namelist()))
    with zipfile.ZipFile(WORK / 'compiled-patches.zip', 'w', zipfile.ZIP_DEFLATED) as z:
        for name in sorted(changed + added): z.writestr(name, c.read(name))
    report = '\n'.join([
        'PRE28_ORIGINAL_BYTES_PRESERVED',
        'Original class files unchanged: ' + str(sum(n.endswith('.class') for n in b.namelist())),
        'Original asset files unchanged: ' + str(sum(n.startswith('assets/') for n in b.namelist())),
        'Only modified entries: ' + ', '.join(changed),
        'Added entries: ' + ', '.join(added),
        'SHA256: ' + hashlib.sha256(CANDIDATE.read_bytes()).hexdigest(),
    ])
    (WORK / 'qa/original-bytes-audit.txt').write_text(report + '\n')
(WORK / 'compiled-patches.zip.b64').write_text(base64.b64encode((WORK / 'compiled-patches.zip').read_bytes()).decode())
with zipfile.ZipFile(WORK / 'NexusCharacters-PRE28.zip', 'w', zipfile.ZIP_DEFLATED) as z:
    z.write(CANDIDATE, CANDIDATE.name)
print(report)
print('BUNDLE_SIZE', (WORK / 'compiled-patches.zip.b64').stat().st_size)
