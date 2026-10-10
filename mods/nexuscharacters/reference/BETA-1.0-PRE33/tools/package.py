from pathlib import Path
import base64, hashlib, io, json, shutil, subprocess, zipfile
R = Path(__file__).resolve().parents[1]
JARS = {
    'nexus': (R / 'base/NexusCharacters-PRE32.jar', R / 'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE33.jar', R / 'classes'),
    'hud': (next((R / 'hud-base').rglob('*.jar')), R / 'capitale_rp_hud_BETA_1_3_3_OPT1_1.21.11.jar', R / 'hud-classes'),
}
delivery = R / 'delivery'; delivery.mkdir(exist_ok=True)
for kind, (base, jar, classes) in JARS.items():
    stream = io.BytesIO(); hashes = {}
    with zipfile.ZipFile(stream, 'w', zipfile.ZIP_DEFLATED) as z:
        for path in sorted(classes.rglob('*.class')):
            name, data = path.relative_to(classes).as_posix(), path.read_bytes()
            hashes[name] = hashlib.sha256(data).hexdigest()
            info = zipfile.ZipInfo(name, (2026, 10, 10, 0, 0, 0)); info.compress_type = zipfile.ZIP_DEFLATED
            z.writestr(info, data)
    patch = stream.getvalue()
    (R / (kind + '-compiled-patches.zip.b64')).write_text(base64.b64encode(patch).decode() + '\n')
    with zipfile.ZipFile(jar) as z: meta = json.loads(z.read('fabric.mod.json'))
    manifest = {'base_sha256': hashlib.sha256(base.read_bytes()).hexdigest(),
                'candidate_sha256': hashlib.sha256(jar.read_bytes()).hexdigest(),
                'patch_sha256': hashlib.sha256(patch).hexdigest(), 'patch_entries': hashes,
                'version': meta['version'], 'description': meta['description']}
    (R / (kind + '-build-manifest.json')).write_text(json.dumps(manifest, indent=2) + '\n')
    reconstructed = R / 'qa' / ('reassembled-' + jar.name)
    subprocess.run(['python', str(R / 'assemble.py'), kind, str(base), str(reconstructed)], check=True)
    assert reconstructed.read_bytes() == jar.read_bytes()
    shutil.copyfile(jar, delivery / jar.name)
for name in ['PRE33-verifications.md', 'LISEZ-MOI-PRE33.txt']: shutil.copyfile(R / name, delivery / name)
output = delivery / 'Indivis-PRE33-et-HUD-OPT1.zip'
with zipfile.ZipFile(output, 'w', zipfile.ZIP_DEFLATED) as z:
    for path in [delivery / jar.name for _, jar, _ in JARS.values()] + [delivery / 'PRE33-verifications.md', delivery / 'LISEZ-MOI-PRE33.txt']:
        info = zipfile.ZipInfo(path.name, (2026, 10, 10, 0, 0, 0)); info.compress_type = zipfile.ZIP_DEFLATED
        z.writestr(info, path.read_bytes())
with zipfile.ZipFile(output) as z:
    assert z.testzip() is None
    for name in z.namelist(): assert z.read(name) == (delivery / name).read_bytes()
print('PACKAGE33_PASS')
for path in sorted(delivery.iterdir()): print(path.name, path.stat().st_size)
