import hashlib,json,zipfile,sys
from pathlib import Path
root=Path(__file__).resolve().parent
base=Path(sys.argv[1]) if len(sys.argv)>1 else root.parent/'download-pre19/NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE19.jar'
assert hashlib.sha256(base.read_bytes()).hexdigest()=='99a1129b4d38b09d9073a13180cb86332c7d58718a104bfa89a29e034605090e'
out=Path(sys.argv[2]) if len(sys.argv)>2 else root/'NexusCharacters-HauteCapitale-1.21.11-BETA-1.0-PRE20.jar'
patches={p.relative_to(root/'classes').as_posix():p.read_bytes() for p in (root/'classes').rglob('*.class')}
with zipfile.ZipFile(base) as src:
 mod=json.loads(src.read('fabric.mod.json'));mod['version']='1.0.0-beta.20+hc.1.21.11'
 mod['description']='Haute Capitale PRE20: native Skin Layers voxel outer geometry, flat base, classic facial hair support and joint clipping.'
 patches['fabric.mod.json']=(json.dumps(mod,ensure_ascii=False,indent=2)+'\n').encode()
 out.parent.mkdir(parents=True,exist_ok=True)
 with zipfile.ZipFile(out,'w',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as dst:
  for n in sorted(set(src.namelist())|patches.keys()):
   if n.endswith('/'):continue
   entry=zipfile.ZipInfo(n,date_time=(2026,10,2,0,0,0));entry.compress_type=zipfile.ZIP_DEFLATED;entry.external_attr=0o644<<16
   dst.writestr(entry,patches[n] if n in patches else src.read(n))
with zipfile.ZipFile(out) as z,zipfile.ZipFile(base) as src:
 assert z.testzip() is None
 for n in src.namelist():
  if n.startswith('assets/'):assert z.read(n)==src.read(n),n
print(out,hashlib.sha256(out.read_bytes()).hexdigest())
