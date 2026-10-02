"""Preserve the exact checked binary, original assets, sources and rendered evidence."""
from pathlib import Path
import base64
import hashlib
import json
import shutil
import zipfile

ROOT = Path(__file__).resolve().parent.parent
TASK = ROOT / 'pre23'
REPO = ROOT / 'indivis'
REFERENCE = REPO / 'mods/nexuscharacters/reference/BETA-1.0-PRE23'
EVIDENCE = TASK / 'evidence'
summary = json.loads((EVIDENCE / 'validation-run.json').read_text())
jar = TASK / 'candidate.jar'
sha = hashlib.sha256(jar.read_bytes()).hexdigest()
assert sha == summary['jar_sha256']
assert sha == '2d10761e7242b29b6ed1faba2c3dc5456c0b2111e64692018bf72a8cf1d4dbce'

pre17 = REPO / 'mods/NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE17.jar'
if not zipfile.is_zipfile(pre17):
    oid = next(line.split(':', 1)[1] for line in pre17.read_text().splitlines()
               if line.startswith('oid sha256:'))
    pre17 = REPO / '.git/lfs/objects' / oid[:2] / oid[2:4] / oid
with zipfile.ZipFile(pre17) as original, zipfile.ZipFile(jar) as final:
    names = [name for name in original.namelist() if name.startswith('assets/') and not name.endswith('/')]
    assert len(names) == 4958 and final.testzip() is None
    assert set(names) == {name for name in final.namelist() if name.startswith('assets/') and not name.endswith('/')}
    assert all(original.read(name) == final.read(name) for name in names)
integrity = {'jar_sha256': sha, 'original_pre17_sha256': hashlib.sha256(pre17.read_bytes()).hexdigest(),
             'original_asset_files': len(names), 'changed': [], 'missing': [], 'extra': [],
             'jar_bytes': jar.stat().st_size}
(EVIDENCE / 'asset-integrity.json').write_text(json.dumps(integrity, indent=2) + '\n')

for name in ['sources', 'tests', 'patches']:
    shutil.copytree(TASK / name, REFERENCE / name, dirs_exist_ok=True)
for name in ['assemble.py', 'build_test.py', 'launch.py', 'final_check.py', 'save_verified.py']:
    shutil.copy2(TASK / name, REFERENCE / name)

archive = TASK / 'compiled-patches.zip'
with zipfile.ZipFile(archive, 'w', compression=zipfile.ZIP_DEFLATED, compresslevel=9) as target:
    for path in sorted((TASK / 'classes').rglob('*.class')):
        name = path.relative_to(TASK / 'classes').as_posix()
        entry = zipfile.ZipInfo(name, date_time=(2026, 10, 2, 0, 0, 0))
        entry.compress_type = zipfile.ZIP_DEFLATED
        entry.external_attr = 0o644 << 16
        target.writestr(entry, path.read_bytes())
manifest = {
    'jar_sha256': sha,
    'compiled_archive_sha256': hashlib.sha256(archive.read_bytes()).hexdigest(),
    'source_sha256': {p.name: hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted((TASK / 'sources').glob('*.java'))},
    'class_sha256': {p.relative_to(TASK / 'classes').as_posix(): hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted((TASK / 'classes').rglob('*.class'))},
    'tests_sha256': {p.name: hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted((TASK / 'tests').glob('*.java'))},
}
(REFERENCE / 'compiled-patches.zip.b64').write_bytes(base64.b64encode(archive.read_bytes()) + b'\n')
(REFERENCE / 'build-manifest.json').write_text(json.dumps(manifest, indent=2) + '\n')
(REFERENCE / 'evidence').mkdir(exist_ok=True)
for name in ['actual-final.log', 'preview-final.log', 'catalog-final.log', 'pre22-rejected-final-tests.log']:
    shutil.copy2(EVIDENCE / name, REFERENCE / 'evidence' / (Path(name).stem + '.txt'))
for name in ['validation-run.json', 'asset-integrity.json', 'geometry-final.txt']:
    shutil.copy2(EVIDENCE / name, REFERENCE / 'evidence' / name)

images = sorted([*EVIDENCE.glob('actual-*-*.png'), *EVIDENCE.glob('preview-widget-*.png'),
                 *EVIDENCE.glob('preview-world-*.png'), EVIDENCE / 'first-person.png',
                 EVIDENCE / 'third-person.png', EVIDENCE / 'account-actual.png', EVIDENCE / 'account-preview.png'])
assert len(images) == 64
capture_manifest = {p.name: hashlib.sha256(p.read_bytes()).hexdigest() for p in images}
(REFERENCE / 'evidence/captures-manifest.json').write_text(json.dumps(capture_manifest, indent=2) + '\n')
with zipfile.ZipFile(REFERENCE / 'evidence/captures.zip', 'w', zipfile.ZIP_DEFLATED) as target:
    for path in images:
        target.write(path, path.name)
    target.writestr('jar-sha256.txt', sha + '\n')

with zipfile.ZipFile(REFERENCE / 'test-assets.zip', 'w', zipfile.ZIP_DEFLATED) as target:
    for base in [ROOT / 'pre20/test-assets', TASK / 'test-assets']:
        for path in sorted(base.rglob('*.png')):
            target.write(path, path.relative_to(base))

target_jar = REPO / 'mods/NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE23.jar'
shutil.copy2(jar, target_jar)
workflow = REPO / '.github/workflows/build-nexus-pre23.yml'
workflow.write_text(workflow.read_text().replace('08de58a256525ec46b1362f8ab86eb4d3990a051a18f905b43127d28d498079d', sha))
(REFERENCE / 'WORK-IN-PROGRESS.md').write_text('PRE23 validée sur le JAR identifié dans VALIDATION.md. Voir ce rapport et les captures associées.\n')
print('VERIFIED_BINARY_AND_EVIDENCE_SAVED', target_jar, sha)
