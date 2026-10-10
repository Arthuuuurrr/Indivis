"""Reproduce the exact tested PRE30 from the exact PRE29, without recompiling."""
from pathlib import Path
import base64,hashlib,json,io,zipfile,sys
R=Path(__file__).resolve().parent
BASE=Path(sys.argv[1]) if len(sys.argv)>1 else R.parents[2]/'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE29.jar'
OUT=Path(sys.argv[2]) if len(sys.argv)>2 else R/'NexusCharacters-Indivis-1.21.11-BETA-1.0-PRE30.jar'
m=json.loads((R/'build-manifest.json').read_text());assert hashlib.sha256(BASE.read_bytes()).hexdigest()==m['base_sha256']
patch=base64.b64decode((R/'compiled-patches.zip.b64').read_text());assert hashlib.sha256(patch).hexdigest()==m['compiled_archive_sha256']
with zipfile.ZipFile(BASE) as z:entries={n:z.read(n) for n in z.namelist()}
with zipfile.ZipFile(io.BytesIO(patch)) as z:
 for n in z.namelist():
  d=z.read(n);assert n in m['class_sha256'] and hashlib.sha256(d).hexdigest()==m['class_sha256'][n];entries[n]=d
meta=json.loads(entries['fabric.mod.json']);meta['version']='1.0.0-beta.30+indivis.1.21.11';meta['description']='Indivis PRE30: cached render reflection and reusable pose scratch buffers.';entries['fabric.mod.json']=json.dumps(meta,ensure_ascii=False,indent=2).encode()
OUT.parent.mkdir(parents=True,exist_ok=True);temporary=OUT.with_suffix('.building')
with zipfile.ZipFile(temporary,'w',zipfile.ZIP_DEFLATED) as z:
 for n,d in sorted(entries.items()):
  info=zipfile.ZipInfo(n,(2026,10,9,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,d)
assert hashlib.sha256(temporary.read_bytes()).hexdigest()==m['candidate_sha256'];temporary.replace(OUT)
with zipfile.ZipFile(OUT) as z:assert z.testzip() is None
print('PRE30_REPRODUCTION_PASS',m['candidate_sha256'])
