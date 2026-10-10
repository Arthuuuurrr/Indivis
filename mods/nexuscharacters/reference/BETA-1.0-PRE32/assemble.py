from pathlib import Path
import base64,hashlib,json,zipfile,io,sys
R=Path(__file__).resolve().parent;manifest=json.loads((R/'build-manifest.json').read_text());base=Path(sys.argv[1]);out=Path(sys.argv[2])
assert hashlib.sha256(base.read_bytes()).hexdigest()==manifest['base_sha256'],'Wrong PRE31 base'
patch=base64.b64decode((R/'compiled-patches.zip.b64').read_text());assert hashlib.sha256(patch).hexdigest()==manifest['patch_sha256']
with zipfile.ZipFile(base) as z:entries={n:z.read(n) for n in z.namelist()}
with zipfile.ZipFile(io.BytesIO(patch)) as z:
 for n in z.namelist():
  b=z.read(n);assert hashlib.sha256(b).hexdigest()==manifest['patch_entries'][n];entries[n]=b
meta=json.loads(entries['fabric.mod.json']);meta['version']=manifest['version'];meta['description']=manifest['description'];entries['fabric.mod.json']=json.dumps(meta,ensure_ascii=False,indent=2).encode()
out.parent.mkdir(parents=True,exist_ok=True);temp=out.with_suffix('.building')
with zipfile.ZipFile(temp,'w',zipfile.ZIP_DEFLATED) as z:
 for n,b in sorted(entries.items()):
  info=zipfile.ZipInfo(n,(2026,10,10,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;z.writestr(info,b)
with zipfile.ZipFile(temp) as z:assert z.testzip() is None
assert hashlib.sha256(temp.read_bytes()).hexdigest()==manifest['candidate_sha256'],'Candidate SHA differs'
temp.replace(out);print('ASSEMBLY_PASS',manifest['candidate_sha256'])
