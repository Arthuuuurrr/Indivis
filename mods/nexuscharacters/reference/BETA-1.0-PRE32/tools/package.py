from pathlib import Path
import zipfile, io, base64, json, hashlib, shutil, subprocess

R=Path(__file__).resolve().parents[1]
jar=R/'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE32.jar'
base=R.parent/'pre31/NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE31.jar'
buffer=io.BytesIO();patch_entries={}
with zipfile.ZipFile(buffer,'w',zipfile.ZIP_DEFLATED) as z:
    for p in sorted((R/'classes').rglob('*.class')):
        n=p.relative_to(R/'classes').as_posix();b=p.read_bytes()
        patch_entries[n]=hashlib.sha256(b).hexdigest()
        info=zipfile.ZipInfo(n,(2026,10,10,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
patch=buffer.getvalue();(R/'compiled-patches.zip.b64').write_text(base64.b64encode(patch).decode()+'\n')
with zipfile.ZipFile(jar) as z:meta=json.loads(z.read('fabric.mod.json'))
manifest={'base_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),
          'candidate_sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),
          'patch_sha256':hashlib.sha256(patch).hexdigest(),'patch_entries':patch_entries,
          'version':meta['version'],'description':meta['description']}
(R/'build-manifest.json').write_text(json.dumps(manifest,indent=2)+'\n')
D=R/'delivery';D.mkdir(exist_ok=True)
shutil.copy2(jar,D/jar.name);shutil.copy2(R/'PRE32-verifications.md',D/'PRE32-verifications.md')
out=D/'NexusCharacters-PRE32.zip'
with zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as z:
    for p in [D/jar.name,D/'PRE32-verifications.md']:
        info=zipfile.ZipInfo(p.name,(2026,10,10,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,p.read_bytes())
with zipfile.ZipFile(out) as z:
    assert z.testzip() is None
    assert z.read(jar.name)==jar.read_bytes()
    assert z.read('PRE32-verifications.md')==(R/'PRE32-verifications.md').read_bytes()
subprocess.run(['python',str(R/'assemble.py'),str(base),str(R/'qa/reassembled-PRE32.jar')],check=True)
assert (R/'qa/reassembled-PRE32.jar').read_bytes()==jar.read_bytes()
print('PACKAGE32_PASS',manifest['candidate_sha256'])
for p in D.iterdir():print(p.name,p.stat().st_size)
