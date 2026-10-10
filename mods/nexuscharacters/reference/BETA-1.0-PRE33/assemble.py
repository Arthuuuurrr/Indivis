from pathlib import Path
import base64, hashlib, io, json, sys, zipfile
R = Path(__file__).resolve().parent
kind, base, output = sys.argv[1], Path(sys.argv[2]), Path(sys.argv[3])
assert kind in ('nexus', 'hud')
manifest = json.loads((R / (kind + '-build-manifest.json')).read_text())
assert hashlib.sha256(base.read_bytes()).hexdigest() == manifest['base_sha256'], 'Wrong original JAR'
patch = base64.b64decode((R / (kind + '-compiled-patches.zip.b64')).read_text())
assert hashlib.sha256(patch).hexdigest() == manifest['patch_sha256'], 'Corrupt class patch'
with zipfile.ZipFile(base) as z: entries = {name: z.read(name) for name in z.namelist()}
with zipfile.ZipFile(io.BytesIO(patch)) as z:
    for name in z.namelist():
        data = z.read(name)
        assert hashlib.sha256(data).hexdigest() == manifest['patch_entries'][name]
        entries[name] = data
meta = json.loads(entries['fabric.mod.json'])
meta['version'], meta['description'] = manifest['version'], manifest['description']
entries['fabric.mod.json'] = json.dumps(meta, ensure_ascii=False, indent=2).encode()
output.parent.mkdir(parents=True, exist_ok=True)
temporary = output.with_suffix('.building')
with zipfile.ZipFile(temporary, 'w', zipfile.ZIP_DEFLATED) as z:
    for name, data in sorted(entries.items()):
        info = zipfile.ZipInfo(name, (2026, 10, 10, 0, 0, 0)); info.compress_type = zipfile.ZIP_DEFLATED
        z.writestr(info, data)
with zipfile.ZipFile(temporary) as z: assert z.testzip() is None
assert hashlib.sha256(temporary.read_bytes()).hexdigest() == manifest['candidate_sha256']
temporary.replace(output)
print('ASSEMBLY33_PASS', kind, manifest['candidate_sha256'])
