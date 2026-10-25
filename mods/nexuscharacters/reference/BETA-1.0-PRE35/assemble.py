"""Rebuild the validated PRE35 from the exact user-supplied SI1 base, without Java."""
from pathlib import Path
import base64,hashlib,io,json,sys,zipfile
R=Path(__file__).resolve().parent
manifest=json.loads((R/'build-manifest.json').read_text())
base=Path(sys.argv[1]);out=Path(sys.argv[2])
assert base.resolve()!=out.resolve(),'Output must not overwrite the base'
assert hashlib.sha256(base.read_bytes()).hexdigest()==manifest['base_sha256'],'Wrong base JAR'
raw=base64.b64decode((R/'compiled-patches.zip.b64').read_text().strip(),validate=True)
assert hashlib.sha256(raw).hexdigest()==manifest['patch_zip_sha256']
with zipfile.ZipFile(io.BytesIO(raw)) as z:patches={n:z.read(n) for n in z.namelist()}
assert set(patches)==set(manifest['entries'])
for n,data in patches.items():assert hashlib.sha256(data).hexdigest()==manifest['entries'][n],n
tmp=out.with_suffix('.building');out.parent.mkdir(parents=True,exist_ok=True)
with zipfile.ZipFile(base) as src,zipfile.ZipFile(tmp,'w') as dst:
 for info in src.infolist():dst.writestr(info,patches.pop(info.filename,src.read(info)))
 for n,data in sorted(patches.items()):
  info=zipfile.ZipInfo(n,(2026,10,25,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;dst.writestr(info,data)
with zipfile.ZipFile(tmp) as z:assert z.testzip() is None
assert hashlib.sha256(tmp.read_bytes()).hexdigest()==manifest['output_sha256'],'Reconstruction mismatch'
tmp.replace(out)
print('ASSEMBLE35_PASS',out.name,manifest['output_sha256'])
